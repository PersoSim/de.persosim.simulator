package de.persosim.simulator.protocols.ri;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.crypto.KeyAgreement;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.apdumatching.ApduSpecificationConstants;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyPairObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.exception.VerificationException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.PlatformUtil;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.InfoSource;
import de.persosim.simulator.utils.Utils;

/**
 * This class implements the restricted identification protocol as described in
 * TR-03110 v2.10 Part 2.
 * 
 * @author mboonk
 * 
 */
public class RiProtocol implements Protocol, Iso7816, ApduSpecificationConstants,
		InfoSource, Ri, TlvConstants {

	private CardStateAccessor cardState;
	private int privateKeyReference;
	
	private KeyPairObject staticKeyObject;

	public RiProtocol() {
		reset();
	}

	@Override
	public String getProtocolName() {
		return "RI";
	}

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState) {
		this.cardState = cardState;
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {

		if ((publicity == SecInfoPublicity.AUTHENTICATED) || (publicity == SecInfoPublicity.PRIVILEGED)) {
			OidIdentifier riOidIdentifier = new OidIdentifier(new Oid(Ri.id_RI));
			
			Collection<CardObject> riKeyCardObjects = mf.findChildren(
					new KeyIdentifier(), riOidIdentifier);
			
			HashSet<TlvDataObject> secInfos = new HashSet<TlvDataObject>();
			
			for (CardObject curCardObject : riKeyCardObjects) {
				if (! (curCardObject instanceof KeyPairObject)) {
					continue;
				}
				
				KeyPairObject curKey = (KeyPairObject) curCardObject;
				
				Collection<CardObjectIdentifier> identifiers = curKey.getAllIdentifiers();
				
				//extract keyId
				int keyId = -1;
				for (CardObjectIdentifier curIdentifier : identifiers) {
					if (curIdentifier instanceof KeyIdentifier) {
						keyId = ((KeyIdentifier) curIdentifier).getKeyReference();
						break;
					}
				}
				
				if (keyId == -1) continue; // skip keys that dont't provide a keyId
				
				//cached values
				byte[] genericRiOidBytes = null;
				
				//construct and add RiInfos
				for (CardObjectIdentifier curIdentifier : identifiers) {
					if (riOidIdentifier.matches(curIdentifier)) {
						byte[] oidBytes = ((OidIdentifier) curIdentifier).getOid().toByteArray();
						genericRiOidBytes = Arrays.copyOfRange(oidBytes, 0, 9);
						

						/*
						 * ProtocolParams ::= SEQUENCE {
						 * 		version         INTEGER, -- MUST be 1
						 * 		keyId           INTEGER,
						 * 		authorizedOnly  BOOLEAN
						 * }
						 */
						ConstructedTlvDataObject params = new ConstructedTlvDataObject(TAG_SEQUENCE);
						params.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{1}));
						params.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{(byte) keyId}));
						
						// add "authorizedOnly"
						if(curKey.isPrivilegedOnly()) {
							params.addTlvDataObject(new PrimitiveTlvDataObject(TAG_BOOLEAN, new byte[]{0x01})); //IMPL RI handle authorizedOnly
						} else {
							params.addTlvDataObject(new PrimitiveTlvDataObject(TAG_BOOLEAN, new byte[]{0x00}));
						}
						
						// define RestrictedIdentificationInfo
						
						/*
						 * RestrictedIdentificationInfo ::= SEQUENCE {
						 * 		protocol  OBJECT IDENTIFIER(
						 *           id-RI-DH-SHA-1  |
						 *           id-RI-DH-SHA-224  |
						 *           id-RI-DH-SHA-256  |
						 *           id-RI-DH-SHA-384 |
						 *           id-RI-DH-SHA-512 |
						 *           id-RI-ECDH-SHA-1  |
						 *           id-RI-ECDH-SHA-224  |
						 *           id-RI-ECDH-SHA-256 |
						 *           id-RI-ECDH-SHA-384 |
						 *           id-RI-ECDH-SHA-512),
						 * 		params    ProtocolParams,
						 * 		maxKeyLen INTEGER OPTIONAL
						 * }
						 */
						ConstructedTlvDataObject riInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
						riInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, oidBytes));
						riInfo.addTlvDataObject(params);
						//IMPL RI handle maxKeyLen
//						riInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{xxx}));
						
						secInfos.add(riInfo);					
					}
				}
				
				//extract required data from curKey
				KeyPair curKeyPair = curKey.getKeyPair();
				ConstructedTlvDataObject encKey = new ConstructedTlvDataObject(curKeyPair.getPublic().getEncoded());
				ConstructedTlvDataObject algIdentifier = (ConstructedTlvDataObject) encKey.getTlvDataObject(TAG_SEQUENCE);
				
				//using standardized domain parameters if possible
				algIdentifier = StandardizedDomainParameters.simplifyAlgorithmIdentifier(algIdentifier);
				
				/*
				 * RestrictedIdentificationDomainParameterInfo ::= SEQUENCE {
				 *   protocol        OBJECT IDENTIFIER(id-RI-DH  |  id-RI-ECDH),
				 *   domainParameter AlgorithmIdentifier
				 * }
				 */
				ConstructedTlvDataObject riDomainInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
				riDomainInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, genericRiOidBytes));
				riDomainInfo.addTlvDataObject(algIdentifier);
				
				secInfos.add(riDomainInfo);
			}
			
			return secInfos;
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	public void process(ProcessingData processingData) {
		if (processingData.getCommandApdu().getIns() == INS_22_MANAGE_SECURITY_ENVIRONMENT) {
			processCommandSetAt(processingData);
		} else if (processingData.getCommandApdu().getIns() == INS_86_GENERAL_AUTHENTICATE) {
			processCommandGeneralAuthenticate(processingData);
		}
	}
	
	/**
	 * This method checks the public key against the hash that was transmitted
	 * as a part of a previously executed terminal authentication.
	 * 
	 * @param sectorPublicKey
	 *            the data object containing the public key as transmitted
	 * @param hash
	 *            the {@link MessageDigest} to use for checking
	 * @param sectorPublicKeyHash
	 *            the hash to check against
	 * @return true, iff the hashes are equal
	 */
	private boolean checkSectorPublicKeyHash(ConstructedTlvDataObject sectorPublicKey, MessageDigest hash, byte [] sectorPublicKeyHash){
		ConstructedTlvDataObject temp = new ConstructedTlvDataObject(TlvConstants.TAG_7F49);
		for (TlvDataObject object : sectorPublicKey.getTlvDataObjectContainer()){
			temp.addTlvDataObject(object);
		}
		return Arrays.equals(sectorPublicKeyHash, hash.digest(temp.toByteArray()));
	}

	/**
	 * This method performs the calculation of the sector identifier as
	 * described in TR-03110 v2.10 Part 2 3.5
	 * 
	 * @param sectorPublicKey
	 *            the public key transmitted by the terminal
	 * @param keyAgreement
	 *            mechanism to use
	 * @param hash
	 *            {@link MessageDigest} to use
	 * @return the sector identifier as byte array or null in case of errors
	 */
	private byte[] calculateSectorIdentifier(PrivateKey staticPrivateKey, PublicKey sectorPublicKey,
			KeyAgreement keyAgreement, MessageDigest hash) {
		byte[] result;
		// perform key agreement
		try {
			keyAgreement.init(staticPrivateKey);
			keyAgreement.doPhase(sectorPublicKey, true);
			result = hash.digest(keyAgreement.generateSecret());
		} catch (InvalidKeyException e) {
			result = null;
		}
		return result;
	}

	/**
	 * This method handles the complete sector identifier processing, including
	 * extraction from the tlv data.
	 * 
	 * @param commandTag
	 *            the {@link TlvTag} that was used to transmit the public key
	 * @param staticPrivateKey
	 *            the private key to calculate the sector identifier with
	 * @param dynamicAuthenticationData
	 *            the tlv data containing the public key
	 * @param publicKeyCheckingHash
	 *            the {@link MessageDigest} to use for checking the public key
	 *            hash
	 * @param sectorPublicKeyHash
	 *            the previously transmitted hash of the public key
	 * @param responseTag
	 *            the {@link TlvTag} to use for the response data object
	 * @return the {@link PrimitiveTlvDataObject} that contains the sector
	 *         identifier
	 * @throws GeneralSecurityException
	 * @throws VerificationException
	 */
	private PrimitiveTlvDataObject handleSectorKey(TlvTag commandTag, PrivateKey staticPrivateKey, ConstructedTlvDataObject dynamicAuthenticationData, MessageDigest publicKeyCheckingHash, byte [] sectorPublicKeyHash, TlvTag responseTag) throws GeneralSecurityException, VerificationException{
		TlvDataObject sectorPublicKeyData = dynamicAuthenticationData.getTlvDataObject(commandTag);
		if (sectorPublicKeyData instanceof ConstructedTlvDataObject) {
			if (!checkSectorPublicKeyHash((ConstructedTlvDataObject)sectorPublicKeyData, publicKeyCheckingHash, sectorPublicKeyHash)){
				throw new VerificationException("The public key hash transmitted during a previous protocol does not match the given public key");
			}
			RiOid oid = new RiOid(((ConstructedTlvDataObject)sectorPublicKeyData).getTlvDataObject(TlvConstants.TAG_06).getValueField());
			PublicKey sectorPublicKey = oid.parsePublicKey((ConstructedTlvDataObject) sectorPublicKeyData);
			return new PrimitiveTlvDataObject(responseTag, calculateSectorIdentifier(staticPrivateKey, sectorPublicKey, oid.getKeyAgreement(), oid.getHash()));
		}
		return null;
	}
	
	private void processCommandGeneralAuthenticate(ProcessingData processingData) {
		if (processingData.getCommandApdu().getCommandDataObjectContainer().getTlvDataObject(TlvConstants.TAG_7C) instanceof ConstructedTlvDataObject){
			ConstructedTlvDataObject dynamicAuthenticationData = (ConstructedTlvDataObject)processingData.getCommandApdu()
					.getCommandDataObjectContainer().getTlvDataObject(TlvConstants.TAG_7C);
			
			//get necessary information stored in TA
			HashSet<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
			previousMechanisms.add(TerminalAuthenticationMechanism.class);
			Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
			TerminalAuthenticationMechanism taMechanism = null;
			if (currentMechanisms.size() > 0){
				taMechanism = (TerminalAuthenticationMechanism) currentMechanisms.toArray()[0];
				
				if (!(taMechanism.getTerminalType().equals(TerminalType.AT))) {
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
					processingData.updateResponseAPDU(this, "Restricted Identification only allowed for Authentication Terminals", resp);
					return;
				}

				byte [] firstSectorPublicKeyHash = taMechanism.getFirstSectorPublicKeyHash();
				byte [] secondSectorPublicKeyHash = taMechanism.getSecondSectorPublicKeyHash();
				MessageDigest publicKeyCheckingHash;
				try {
					publicKeyCheckingHash = MessageDigest.getInstance(taMechanism.getSectorPublicKeyHashAlgorithm(), Crypto.getCryptoProvider());
				} catch (GeneralSecurityException e) {
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
					processingData.updateResponseAPDU(this,
							"The hash algorithm for checking the public key could not be instantiated", resp);
					return;
				}

				if (staticKeyObject == null){
					//{@link staticKeyObject} may be null either if setAt has not yet been executed or failed.
					
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(PlatformUtil.SW_4A80_WRONG_DATA);
					processingData.updateResponseAPDU(this,
							"The static key pair was not set correctly, probably due to missing or failed execution of setAT command", resp);
					return;
				}
				
				KeyPair staticKeyPair = staticKeyObject.getKeyPair();
				
				// check for Restricted Identification bit
				if(staticKeyObject.isPrivilegedOnly()) {
					if (!(taMechanism.getEffectiveAuthorization().getAuthorization().getBit(2))) {
						// create and propagate response APDU
						ResponseApdu resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
						processingData.updateResponseAPDU(this, "Restricted Identification only allowed for authorized terminals", resp);
						return;
					}
				}
				
				PrivateKey staticPrivateKey = staticKeyPair.getPrivate();
				
				ConstructedTlvDataObject responseData = new ConstructedTlvDataObject(
						TlvConstants.TAG_7C);
				try {
					if (dynamicAuthenticationData.getTlvDataObject(RI_FIRST_SECTOR_KEY_TAG) != null) {
						responseData.addTlvDataObject(handleSectorKey(
								RI_FIRST_SECTOR_KEY_TAG,
								staticPrivateKey,
								dynamicAuthenticationData,
								publicKeyCheckingHash,
								firstSectorPublicKeyHash,
								TlvConstants.TAG_81));

					}
					if (dynamicAuthenticationData.getTlvDataObject(RI_SECOND_SECTOR_KEY_TAG) != null) {
						responseData.addTlvDataObject(handleSectorKey(
								RI_SECOND_SECTOR_KEY_TAG,
								staticPrivateKey,
								dynamicAuthenticationData,
								publicKeyCheckingHash,
								secondSectorPublicKeyHash,
								TlvConstants.TAG_83));
					}
				} catch (GeneralSecurityException e) {
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
					processingData.updateResponseAPDU(this,
							"no sector identifiers could be calculated because of errors using the public key", resp);
					return;
				} catch (VerificationException e) {
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
					processingData.updateResponseAPDU(this,
							"the given public key is invalid", resp);
				}
				
				if (responseData.getNoOfElements() > 0) {
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(new TlvDataObjectContainer(responseData), Iso7816.SW_9000_NO_ERROR);
					processingData.updateResponseAPDU(this,
							"Restricted identification successfully executed", resp);
					return;
				} else {
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
					processingData.updateResponseAPDU(this,
							"no sector identifiers could be calculated becaus of missing or incorrect input data", resp);
					return;
				}
			} else {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
				processingData.updateResponseAPDU(this, "Restricted Identification requires preceding Terminal Authentication", resp);
			}
		}

	}

	private void processCommandSetAt(ProcessingData processingData) {
		TlvDataObject cryptographicMechanismReferenceData = processingData
				.getCommandApdu().getCommandDataObjectContainer()
				.getTlvDataObject(TlvConstants.TAG_80);
		TlvDataObject privateKeyReferenceData = processingData.getCommandApdu()
				.getCommandDataObjectContainer()
				.getTlvDataObject(TlvConstants.TAG_84);

		if (cryptographicMechanismReferenceData != null) {
			try{
				new RiOid(
					cryptographicMechanismReferenceData.getValueField());
			} catch (IllegalArgumentException e){
				ResponseApdu resp = new ResponseApdu(PlatformUtil.SW_4A80_WRONG_DATA);
				processingData.updateResponseAPDU(this,
						"The cryptographic mechanism reference data is missing",
						resp);
				return;
			}
		} else {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(
					Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			processingData.updateResponseAPDU(this,
					"The cryptographic mechanism reference data is missing",
					resp);
			return;
		}

		if (privateKeyReferenceData != null) {
			privateKeyReference = Utils
					.getIntFromUnsignedByteArray(privateKeyReferenceData
							.getValueField());
			
			KeyIdentifier keyIdentifier = new KeyIdentifier(privateKeyReference);
			CardObject cardObject = cardState.getObject(keyIdentifier,
					Scope.FROM_MF);

			if ((cardObject instanceof KeyPairObject)) {
				staticKeyObject = (KeyPairObject) cardObject;
			} else {
				ResponseApdu resp = new ResponseApdu(
						Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
				processingData.updateResponseAPDU(this,
						"invalid key reference", resp);
				/* there is nothing more to be done here */
				return;
			}
		} else {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(
					Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			processingData.updateResponseAPDU(this,
					"The private key reference was not found", resp);
			return;
		}

		//create and propagate response APDU
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		processingData.updateResponseAPDU(this, "Command SetAt successfully processed", resp);
	}
	
	@Override
	public String getIDString() {
		return "Restricted Identification";
	}

	@Override
	public void reset() {
		privateKeyReference = -1;
	}
	
	@Override
	public boolean isMoveToStackRequested() {
		return false;
	}

}
