package de.persosim.simulator.protocols.ca3;

import static org.globaltester.logging.BasicLogger.log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.logging.InfoSource;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.apdumatching.ApduSpecificationConstants;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.CardObjectUtils;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.TypeIdentifier;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.crypto.certificates.CertificateExtension;
import de.persosim.simulator.crypto.certificates.ExtensionOid;
import de.persosim.simulator.exception.ProcessingException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.PlatformUtil;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.Tr03110Utils;
import de.persosim.simulator.protocols.ca3.PsAuthInfo.PsAuthInfoValue;
import de.persosim.simulator.protocols.puo.TerminalSectorForPseudonymousSignaturesCertificateExtension;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.seccondition.AndSecCondition;
import de.persosim.simulator.seccondition.AuthorizationSecCondition;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.seccondition.TaSecurityCondition;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvDataObjectFactory;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class implements common functionality to be used in the pseudonymous
 * signatures protocols PSA/PSM/PSC as described in TR-03110
 * 
 * @author slutters
 *
 */
public abstract class PsProtocol implements Protocol, Iso7816, ApduSpecificationConstants, InfoSource, Ps, TlvConstants {
	
	protected CardStateAccessor cardState;
	protected PsOid psOid;
	protected ProcessingData processingData;
	protected DomainParameterSetEcdh psDomainParameters;
	
	protected ECPrivateKey privateKeyIcc1;
	protected ECPrivateKey privateKeyIcc2;
	protected ECPublicKey publicKeyGroupManager;
	protected ECPublicKey sectorPublicKey;
	
	protected int ps1AuthInfo;
	protected int ps2AuthInfo;
	
	protected ECPoint sectorIcc1;
	protected ECPoint sectorIcc2;
	
	protected SecCondition secConditonForComputingSectorIcc1;
	protected SecCondition secConditonForComputingSectorIcc2;
	
	/**
	 * This method extracts the PsOid from the command data
	 * @param commandData
	 * @return PsOid
	 */
	protected byte[] extractPsOidFromTag80(TlvDataObjectContainer commandData) {
		//extract PS OID from tag 80 of commandData
		TlvDataObject cryptographicMechanismReferenceData = commandData.getTlvDataObject(TAG_80);
		if (cryptographicMechanismReferenceData != null) {
			try {
				return cryptographicMechanismReferenceData.getValueField();
			} catch (RuntimeException e) {
				throw new ProcessingException (PlatformUtil.SW_4A80_WRONG_DATA, e.getMessage());
			}
		} else {
			throw new ProcessingException (PlatformUtil.SW_4A88_REFERENCE_DATA_NOT_FOUND, "The cryptographic mechanism reference data is missing");
		}
	}
	
	/**
	 * This method extracts the key identifier from the reference data stored in TAG_84 in the command data.
	 * @param commandData the command data to be used
	 * @return the key identifier from the reference data stored in TAG_84 in the command data
	 */
	protected CardObject extractCardObjectMatchingReferenceDataFromTag84(TlvDataObjectContainer commandData) {
		TlvDataObject privateKeyReferenceData = commandData.getTlvDataObject(TAG_84);
		
		KeyIdentifier keyIdentifier;
		if(privateKeyReferenceData == null) {
			keyIdentifier = new KeyIdentifier();
		} else{
			keyIdentifier = new KeyIdentifier(Utils.getIntFromUnsignedByteArray(privateKeyReferenceData.getValueField()));
		}
		
		CardObject cardObject = null;
		try {
			cardObject = CardObjectUtils.getSpecificChild(cardState.getMasterFile(), keyIdentifier, new OidIdentifier(psOid));
		} catch (IllegalArgumentException e) {
			throw new ProcessingException(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, e.getMessage());
		}
		return cardObject;
	}
	
	protected PsAuthInfo findPsAuthInfo(MasterFile file){
		TypeIdentifier typeIdentifier = new TypeIdentifier(PsAuthInfo.class);
		CardObject cardObject = null;
		try {
			cardObject = CardObjectUtils.getSpecificChild(file, typeIdentifier, new OidIdentifier(getGenericPsxOid()));
		} catch (IllegalArgumentException e) {
			throw new ProcessingException(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, e.getMessage());
		}
		return (PsAuthInfo) cardObject; 
	}
	
	/**
	 * This method creates a {@link PsOid} specific to the implementing class
	 * @param oidData the OID data encoded as byte[]
	 * @return a {@link PsOid} specific to the implementing class
	 */
	abstract protected PsOid createPsOid(byte[] oidData);
	
	/**
	 * This method extracts all parameters from the command data field for future use
	 * @param commandData the command data to parse parameters from
	 */
	protected void setPrivateKeyReferencedByTag84(TlvDataObjectContainer commandData) {
		CardObject cardObject = extractCardObjectMatchingReferenceDataFromTag84(commandData);

		if((cardObject instanceof KeyObjectIcc)) {
			
			KeyObjectIcc keyObjectICC = (KeyObjectIcc) cardObject;
			log(this, "key referenced by tag 0x84 is: " + keyObjectICC, LogLevel.DEBUG);
			
			psDomainParameters = (DomainParameterSetEcdh)Tr03110Utils.getDomainParameterSetFromKey(keyObjectICC.getPublicKeyICC());
			
			PsAuthInfo psInfo = findPsAuthInfo(cardState.getMasterFile());
			
			secConditonForComputingSectorIcc1 = getSecConditionForComputingSectorSpecificIdentifier(psInfo.getPs1AuthInfo());
			secConditonForComputingSectorIcc2 = getSecConditionForComputingSectorSpecificIdentifier(psInfo.getPs2AuthInfo());
			
			privateKeyIcc1 = (ECPrivateKey) keyObjectICC.getPrivateKeyICC1();
			privateKeyIcc2 = (ECPrivateKey) keyObjectICC.getPrivateKeyICC2();
			
			publicKeyGroupManager = (ECPublicKey)keyObjectICC.getGroupManagerPublicKey();
		} else {
			throw new ProcessingException(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "invalid key reference");
		}
	}
	
	/**
	 * This method returns the TLV tag below which
	 * 	* the sector public key is stored within the command APDU initiating the PS signature process
	 *  * the signature is to be stored in the response data
	 * 
	 * @return the TLV data tag
	 */
	abstract public TlvTag getDataTag();
	
	/**
	 * This method extracts the public key from the received APDU and
	 * furthermore, checks if its hash value is equal to the provided hash from
	 * terminal authentication
	 * 
	 * @param commandData
	 * @return ECPublicKey
	 */
	protected ECPublicKey extractPublicKeySector(TlvDataObjectContainer commandData) {
		TlvTag dataTag = getDataTag();
		TlvDataObject dynamicAuthenticationData = commandData.getTlvDataObject(dataTag);
		if (dynamicAuthenticationData instanceof ConstructedTlvDataObject) {
			
			TlvDataObject publicKeyReferenceData = ((ConstructedTlvDataObject) dynamicAuthenticationData).getTlvDataObject(TAG_80);
			byte[] pcdPublicKeyMaterial = publicKeyReferenceData.getValueField();
			ECPublicKey publicKey = psDomainParameters.reconstructPublicKey(pcdPublicKeyMaterial);
			
			HashSet<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
			previousMechanisms.add(TerminalAuthenticationMechanism.class);
			Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
			
			if(currentMechanisms.isEmpty()) {
				throw new ProcessingException(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED, "preceding TA required");
			}
			
			TerminalAuthenticationMechanism taMechanism = (TerminalAuthenticationMechanism) currentMechanisms.iterator().next();
			List<CertificateExtension> certificateExtensions = taMechanism.getCertificateExtensions();
			if(certificateExtensions == null) {
				throw new ProcessingException(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED, "missing terminal sector for pseudonymous signatures certificate extension");
			}
			
			MessageDigest hashAlgorithm;
			try {
				hashAlgorithm = MessageDigest.getInstance(taMechanism.getSectorPublicKeyHashAlgorithm(), Crypto.getCryptoProvider());
			} catch (NoSuchAlgorithmException e1) {
				throw new ProcessingException(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR, "The hash algorithm for checking the public key could not be instantiated");
			}
			
			TerminalSectorForPseudonymousSignaturesCertificateExtension terminalSectorForPseudonymousSignaturesCertificateExtension = null;
			
			for(CertificateExtension certificateExtension : certificateExtensions) {
				try {
					terminalSectorForPseudonymousSignaturesCertificateExtension = new TerminalSectorForPseudonymousSignaturesCertificateExtension(certificateExtension.toTlv());
					break;
				} catch (IllegalArgumentException e){
					// expected behavior for all extensions that are not parseable as a terminal sector extension
				}
			}
			if(terminalSectorForPseudonymousSignaturesCertificateExtension == null) {
				throw new ProcessingException(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED, "missing terminal sector for pseudonymous signatures certificate extension");
			}
			
			List<byte[]> sectorPublicKeyHashes = terminalSectorForPseudonymousSignaturesCertificateExtension.getHashesOfSectorPublicKeys();
			
			boolean hashesAreEqual = false;
			for(byte[] currentSectorPublicKeyHash : sectorPublicKeyHashes) {
				if(Arrays.equals(hashAlgorithm.digest(pcdPublicKeyMaterial), currentSectorPublicKeyHash)) {
					hashesAreEqual = true;
					break;
				}
			}
			if(!hashesAreEqual) {
				throw new ProcessingException(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED, "sector public key is not the one declared within TA");
			}
			return publicKey;
			
		} else {
			throw new ProcessingException(PlatformUtil.SW_4A88_REFERENCE_DATA_NOT_FOUND, "invalid reference data");
		}
	}
	
	/**
	 * This method returns the message to be signed by the pseudonymous signature.
	 * @return the message to be signed by the pseudonymous signature 
	 */
	abstract protected byte[] getMessage();
	
	/**This method checks if the CaV3 protocol has been executed before.
	 * @return the current Ca mechanisms or throws a ProcessingExpetion if no Ca protocol has been executed before.
	 */
	//IMPL implement consistent access control mechanism for protocols
	abstract protected Collection<SecMechanism> getCaExecutionStatus();
	
	/**
	 * This method computes a sector ICC based on the provided parameters
	 * @param pkSector the sector public key
	 * @param skIccNo the sector private key
	 * @return a sector ICC based on the provided parameters
	 */
	protected ECPoint computeSectorIcc(ECPublicKey pkSector, ECPrivateKey skIccNo) {
		ECPoint sectorIcc = null;
		
		log(this, "performing key agreement", LogLevel.DEBUG);
		log(this, "pk sector is: " + HexString.encode(psDomainParameters.encodePublicKey(pkSector)), LogLevel.DEBUG);
		log(this, "sk ICC no is: " + HexString.encode(psDomainParameters.encodePrivateKey(skIccNo)), LogLevel.DEBUG);
		
		if(skIccNo != null) {
			sectorIcc = psDomainParameters.performEcdhKeyAgreement(pkSector, skIccNo);
		} else{
			throw new ProcessingException(SW_6984_REFERENCE_DATA_NOT_USABLE, "Icc private key not found!");
		}
		
		return sectorIcc;
	}
	
	protected void computeSectorIccs() {
		if(isSecurityConditionSatisfied(secConditonForComputingSectorIcc1)) {
			log(this, "computing sector ICC1", LogLevel.DEBUG);
			sectorIcc1 = computeSectorIcc(sectorPublicKey, privateKeyIcc1);
			log(this, "sector ICC1 is: " + HexString.encode(psDomainParameters.encodePoint(sectorIcc1, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		} else{
			log(this, "skipping computing sector ICC1 due to missing access rights", LogLevel.DEBUG);
			sectorIcc1 = null;
		}
		
		if(isSecurityConditionSatisfied(secConditonForComputingSectorIcc2)) {
			log(this, "computing sector ICC2", LogLevel.DEBUG);
			sectorIcc2 = computeSectorIcc(sectorPublicKey, privateKeyIcc2);
			log(this, "sector ICC2 is: " + HexString.encode(psDomainParameters.encodePoint(sectorIcc2, CryptoUtil.ENCODING_UNCOMPRESSED)), LogLevel.DEBUG);
		} else{
			log(this, "skipping computing sector ICC2 due to missing access rights", LogLevel.DEBUG);
			sectorIcc2 = null;
		}
	}
	
	/**
	 * This method returns whether the provided security conditions have been met for this protocol.
	 * @return whether the provided security conditions have been met for this protocol
	 */
	protected boolean isSecurityConditionSatisfied(SecCondition secCondition) {
		Collection<Class<? extends SecMechanism>> wantedMechanisms = secCondition.getNeededMechanisms();
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, wantedMechanisms);
		return secCondition.check(currentMechanisms);
	}
	
	/**
	 * This methods performs the protocol step of PS signing a message
	 * @param message the message to be signed
	 * @return the signature
	 */
	protected PsSignature sign(byte[] message) {
		PsSignature signature = null;
		try {
			PsSigner signer = new PsSigner(psDomainParameters, ((ECPublicKey)publicKeyGroupManager).getW(), MessageDigest.getInstance(psOid.getMessageDigestName(), Crypto.getCryptoProvider()), new Random());
			//IMPL check of authorization extensions used in TA (TR-03110 v2.2 2.2.3 Table 6)
			signature = signer.sign(sectorIcc1, sectorIcc2, privateKeyIcc1.getS(), privateKeyIcc2.getS(), sectorPublicKey.getW(), psOid, message);
		} catch (NoSuchAlgorithmException e) {
			throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, e.getMessage());
		}
		
		return signature;
	}
	
	/**
	 * This method generates the response data for a PS signature
	 * @param signature the signature to include in the response
	 * @return the response data
	 */
	protected TlvValue generateResponseData(PsSignature signature) {
		ConstructedTlvDataObject constructedData = new ConstructedTlvDataObject(getDataTag());
		
		if (sectorIcc1 != null){
			PrimitiveTlvDataObject primitive82 = new PrimitiveTlvDataObject(TAG_82, psDomainParameters.encodePoint(sectorIcc1, CryptoUtil.ENCODING_UNCOMPRESSED));
			constructedData.addTlvDataObject(primitive82);
		}
		if (sectorIcc2 != null){
			PrimitiveTlvDataObject primitive83 = new PrimitiveTlvDataObject(TAG_83, psDomainParameters.encodePoint(sectorIcc2, CryptoUtil.ENCODING_UNCOMPRESSED));
			constructedData.addTlvDataObject(primitive83);
		}
		
		PrimitiveTlvDataObject primitive84;
		try{
			primitive84 = new PrimitiveTlvDataObject(TAG_84, PsSignature.encode(signature, psOid.getMessageDigestSize(), psDomainParameters.getPublicPointReferenceLengthL()));
		} catch(IllegalArgumentException e) {
			throw new ProcessingException(SW_6FFF_IMPLEMENTATION_ERROR, e.getMessage());
		}
		
		constructedData.addTlvDataObject(primitive84);
		
		//create and propagate response APDU
		return new TlvDataObjectContainer(constructedData);
	}
	
	/**
	 * This method returns the most generic Oid specific to the implementing
	 * protocol, e.g. a protocol specific OID missing concrete protocol
	 * parameters
	 * 
	 * @return the most generic Oid specific to the implementing protocol
	 */
	abstract protected Oid getGenericPsxOid();
	
	/**
	 * This method returns the bit used to encode the permission to use this PSx
	 * function within the authorization extension for special functions if
	 * explicit authorization is required
	 * 
	 * @return the bit to encode
	 */
	abstract protected int getBit();
	
	@Override
	public HashSet<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		OidIdentifier psxOidIdentifier = new OidIdentifier(getGenericPsxOid());
		
		Collection<CardObject> psxCardObjects = mf.findChildren(
				new KeyIdentifier(), psxOidIdentifier);
		
		HashSet<TlvDataObject> secInfos = new HashSet<>();
		
		for (CardObject curObject : psxCardObjects) {
			if (! (curObject instanceof KeyObjectIcc)) {
				continue;
			}
			KeyObjectIcc keyObjectIcc = (KeyObjectIcc) curObject;
			Collection<CardObjectIdentifier> identifiers = keyObjectIcc.getAllIdentifiers();
			
			//extract keyId
			int keyId = -1;
			for (CardObjectIdentifier curIdentifier : identifiers) {
				if (curIdentifier instanceof KeyIdentifier) {
					keyId = ((KeyIdentifier) curIdentifier).getKeyReference();
					break;
				}
			}
			if (keyId == -1) continue; // skip keys that dont't provide a keyId
			
			
			//construct and add PSXInfos
			for (CardObjectIdentifier curIdentifier : identifiers) {
				if (curIdentifier instanceof OidIdentifier) {
					Oid curOid = ((OidIdentifier) curIdentifier).getOid();
					if (curOid.startsWithPrefix(getGenericPsxOid().toByteArray())) {
						byte[] oidBytes = curOid.toByteArray();
						
						/*
						* PSXRequiredData ::= SEQUENCE {
						*	 version 		INTEGER, -- MUST be 1
						*	 ps1-authInfo	INTEGER (0 | 1 | 2),
						*	 ps2-authInfo	INTEGER (0 | 1 | 2)}
						*/
						
						PsAuthInfo authInfo = findPsAuthInfo(mf);
						
						ConstructedTlvDataObject psxRequiredData = new ConstructedTlvDataObject(TAG_SEQUENCE);
						psxRequiredData.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{1}));
						psxRequiredData.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{ authInfo.getPs1AuthInfo().toValue()}));
						psxRequiredData.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{ authInfo.getPs2AuthInfo().toValue()}));
						
						/*
						* PSXInfo ::= SEQUENCE {
						*  protocol OBJECT IDENTIFIER,
						*  requiredData PSXRequiredData,
						*  keyId INTEGER OPTIONAL }
						*/
						
						ConstructedTlvDataObject psxInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
						psxInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, oidBytes));
						psxInfo.addTlvDataObject(psxRequiredData);
						psxInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{(byte) keyId}));
						secInfos.add(psxInfo);
						
						if (publicity.equals(SecInfoPublicity.AUTHENTICATED) || publicity.equals(SecInfoPublicity.PRIVILEGED)){
							/*
							* AlgorithmIdentifier ::= SEQUENCE { 
							*	algorithm OBJECT IDENTIFIER, 
							*	parameters ANY DEFINED BY algorithm OPTIONAL }
							*/
							
							ConstructedTlvDataObject algorithmIdentifier = new ConstructedTlvDataObject(TAG_SEQUENCE);
							algorithmIdentifier.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, id_EC_PSPUBLIC_KEY.toByteArray()));
							
							ConstructedTlvDataObject parameterSequence = new ConstructedTlvDataObject(TAG_SEQUENCE);
							
							parameterSequence.addTlvDataObject(keyObjectIcc.getPsDomainParameters().getAlgorithmIdentifier()); 
							parameterSequence.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_OCTET_STRING, CryptoUtil.encode(((ECPublicKey)keyObjectIcc.getGroupManagerPublicKey()).getW(), ((DomainParameterSetEcdh)keyObjectIcc.getPsDomainParameters()).getPublicPointReferenceLengthL(), CryptoUtil.ENCODING_UNCOMPRESSED)));
							algorithmIdentifier.addTlvDataObject(parameterSequence);
							
							/*
							* SubjectPublicKeyInfo ::= SEQUENCE {
							*	algorithm AlgorithmIdentifier, 
							*	subjectPublicKey BIT STRING }
							*/
							
							ConstructedTlvDataObject subjectPublicKeyInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
							subjectPublicKeyInfo.addTlvDataObject(algorithmIdentifier);
							subjectPublicKeyInfo.addTlvDataObject(TlvDataObjectFactory.createBitString(CryptoUtil.encode(((ECPublicKey)keyObjectIcc.getPublicKeyICC()).getW(), ((DomainParameterSetEcdh)keyObjectIcc.getPsDomainParameters()).getPublicPointReferenceLengthL(), CryptoUtil.ENCODING_UNCOMPRESSED)));	
							
							/*
							* PSPKRequiredData ::= SEQUENCE {
							*	pSPublicKeySubjectPublicKeyInfo, }
							*/
							
							ConstructedTlvDataObject psPKRequiredData = new ConstructedTlvDataObject(TAG_SEQUENCE);	
							psPKRequiredData.addTlvDataObject(subjectPublicKeyInfo);
							
							/*
							* PSPublicKeyInfo ::= SEQUENCE {
							*	protocol OBJECT IDENTIFIER (id-PS-PK-ECDH-ECSchnorr),
							*	requiredDataPSPKRequiredData,
							*	optionalDataPSPKOptionalData OPTIONAL }
							*/
							
							ConstructedTlvDataObject psaPublicKeyInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
							psaPublicKeyInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, id_PS_PK_ECDH_ECSchnorr.toByteArray()));
							psaPublicKeyInfo.addTlvDataObject(psPKRequiredData);
	
							/*
							* PSPKOptionalData ::= SEQUENCE {
							*	pSParameterID[1] IMPLICIT INTEGER OPTIONAL,
							*	keyId[2] IMPLICIT INTEGER OPTIONAL }
							*/
							
							Integer algorithmId = StandardizedDomainParameters.getDomainParameterSetId(keyObjectIcc.getPsDomainParameters().getAlgorithmIdentifier());
							if (algorithmId != null){
								ConstructedTlvDataObject psPKOptionalData = new ConstructedTlvDataObject(TAG_SEQUENCE);
								psPKOptionalData.addTlvDataObject(new PrimitiveTlvDataObject(TAG_81, new byte[]{algorithmId.byteValue()}));
								psPKOptionalData.addTlvDataObject(new PrimitiveTlvDataObject(TAG_82, new byte[]{(byte) keyId}));
								psaPublicKeyInfo.addTlvDataObject(psPKOptionalData);
							}
							
							secInfos.add(psaPublicKeyInfo);
						}
					}
				}
			}
		}
		return secInfos;
	}
	
	/**
	 * This method returns the {@link SecCondition} imposed on computing sector specific identifiers.
	 * @param psxAuthInfo the access rights for the key to be used for computing the sector specific identifier
	 * @return the {@link SecCondition} imposed on computing sector specific identifiers
	 */
	public SecCondition getSecConditionForComputingSectorSpecificIdentifier(PsAuthInfoValue psxAuthInfo) {
		TaSecurityCondition taCondition = new TaSecurityCondition(null, null);
		
		switch (psxAuthInfo) {
		case NO_EXPLICIT_AUTHORISATION:
			return taCondition;
		case EXPLICIT_AUTHORISATION:
			SecCondition explicitAuthCondition = getSecConditionForExplicitAuthorization();
			AndSecCondition andCondition = new AndSecCondition(taCondition, explicitAuthCondition);
			return andCondition;
		case NO_TERMINAL_AUTHORISATION:
			return SecCondition.DENIED;
		default:
			return SecCondition.DENIED;
		}
		
	}
	
	/**
	 * This method returns the {@link SecCondition} that must be matched for
	 * executing this protocol if explicit authorization is required.
	 * 
	 * @return the {@link SecCondition} to be matched if explicit authorization is required.
	 */
	protected SecCondition getSecConditionForExplicitAuthorization() {
		return new AuthorizationSecCondition(ExtensionOid.id_specialFunctions, getBit());
	}
	
	@Override
	public void reset() {
	}

	@Override
	public boolean isMoveToStackRequested() {
		return false;
	}
	
	@Override
	public String getIDString() {
		return getProtocolName();
	}
	
	@Override
	public void setCardStateAccessor(CardStateAccessor cardState) {
		this.cardState = cardState;
	}
	
	@Override
	public String getProtocolName() {
		return "Pseudonymous Signature (PS)";
	}
	
}
