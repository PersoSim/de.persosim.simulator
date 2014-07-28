package de.persosim.simulator.protocols.ca;

import static de.persosim.simulator.protocols.TR03110.buildAuthenticationTokenInput;
import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.ERROR;
import static de.persosim.simulator.utils.PersoSimLogger.TRACE;
import static de.persosim.simulator.utils.PersoSimLogger.log;
import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.MasterFileIdentifier;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.crypto.CryptoSupport;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.KeyDerivationFunction;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.protocols.AbstractProtocolStateMachine;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.protocols.TR03110;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusMechanismUpdatePropagation;
import de.persosim.simulator.securemessaging.SmDataProviderTr03110;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvPath;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class is part of the implementation of the Chip Authentication (CA)
 * protocol version 2 and implements basic methods to be used by
 * {@link DefaultCaProtocol}.
 * 
 * @author slutters
 * 
 */
//XXX SLS generalize code overlapping with {@link AbstractPaceProtocol} where possible.
public abstract class AbstractCaProtocol extends AbstractProtocolStateMachine implements Ca, TlvConstants {
	protected SecureRandom secureRandom;
	
	protected CaOid caOid;
	
	protected DomainParameterSet caDomainParameters;
	
	protected CryptoSupport cryptoSupport;
	
	protected int keyReference;
	protected KeyPair staticKeyPairPicc;
	
	protected SecretKeySpec secretKeySpecMAC;
	protected SecretKeySpec secretKeySpecENC;
	
	
	
	public AbstractCaProtocol() {
		super("CA");
		
		secureRandom = new SecureRandom();
	}
	
	@Override
	public void initialize() {
		
	}
	
	/**
	 * This method performs the processing of the CA Set AT command.
	 */
	public void processCommandSetAT() {
		//get commandDataContainer
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
				
		/* 
		 * Extract security parameters
		 */
		
		/* CA OID */
		/* Check for the CA OID for itself */
		/* tlvObject will never be null if APDU passed check against APDU specification */
		TlvDataObject tlvObject = commandData.getTagField(TAG_80);
		
		try {
			caOid = new CaOid(tlvObject.getValueField());
		} catch (RuntimeException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			logException(this, e);
			/* there is nothing more to be done here */
			return;
		}
		
		/* key reference */
		/* tlvObject may be null if key material is to be implicitly selected */
		tlvObject = commandData.getTagField(TAG_84);
		
		KeyIdentifier keyIdentifier;
		if(tlvObject == null) {
			keyIdentifier = new KeyIdentifier();
		} else{
			keyIdentifier = new KeyIdentifier(tlvObject.getValueField());
		}
		
		
		CardObject cardObject;
		try {
			cardObject = TR03110.getSpecificChild(cardState.getObject(new MasterFileIdentifier(), Scope.FROM_MF), keyIdentifier, new OidIdentifier(caOid));
		} catch (IllegalArgumentException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		}
		
		KeyObject keyObject;
		if((cardObject instanceof KeyObject)) {
			keyObject = (KeyObject) cardObject;
			staticKeyPairPicc = keyObject.getKeyPair();
			keyReference = keyObject.getPrimaryIdentifier().getInteger();
		} else{
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			this.processingData.updateResponseAPDU(this, "invalid key reference", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		/* CA domain parameters */
		caDomainParameters = TR03110.getDomainParameterSetFromKey(staticKeyPairPicc.getPublic());
		
		this.cryptoSupport = caOid.getCryptoSupport();
		
		log(this, "new OID is " + caOid, DEBUG);
		
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		processingData.updateResponseAPDU(this, "Command Set AT successfully processed", resp);
	}
	
	/**
	 * This method performs the processing of the CA General AUthenticate
	 * command.
	 */
	public void processCommandGeneralAuthenticate() {
		//retrieve command data
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
		
		//retrieve PCD's public key
		TlvDataObject tlvObject = commandData.getTagField(new TlvPath(new TlvTag((byte) 0x7C), new TlvTag((byte) 0x80)));
		byte[] pcdPublicKeyMaterial = tlvObject.getValueField();
		
		String keyAgreementAlgorithmName = caDomainParameters.getKeyAgreementAlgorithm();
		log(this, "PCD's ephemeral public " + keyAgreementAlgorithmName + " key material of " + pcdPublicKeyMaterial.length + " bytes length is: " + HexString.encode(pcdPublicKeyMaterial), TRACE);
		
		PublicKey ephemeralPublicKeyPcd;
		try {
			ephemeralPublicKeyPcd = caDomainParameters.reconstructPublicKey(pcdPublicKeyMaterial);
			log(this, "PCD's  ephemeral public " + keyAgreementAlgorithmName + " key is " + new TlvDataObjectContainer(ephemeralPublicKeyPcd.getEncoded()), TRACE);
		} catch (IllegalArgumentException e) {
			logException(this, e, ERROR);
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
			return;
		} catch (Exception e) {
			logException(this, e, ERROR);
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
			return;
		}
		
		//compare expected PCD's (compressed) public key with the key previously received during TA
		byte[] ephemeralPublicKeyPcdCompressedExpected;
		try {
			ephemeralPublicKeyPcdCompressedExpected = caDomainParameters.comp(ephemeralPublicKeyPcd);
		} catch (NoSuchAlgorithmException e) {
			logException(this, e, ERROR);
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
			return;
		}
		
		byte[] ephemeralPublicKeyPcdCompressedReceived = null;
		
		Collection<Class<? extends SecMechanism>> wantedMechanisms = new HashSet<Class<? extends SecMechanism>>();
		wantedMechanisms.add(TerminalAuthenticationMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, wantedMechanisms);
		
		for(SecMechanism secMechanism : currentMechanisms) {
			if(secMechanism instanceof TerminalAuthenticationMechanism) {
				ephemeralPublicKeyPcdCompressedReceived = ((TerminalAuthenticationMechanism) secMechanism).getCompressedTerminalEphemeralPublicKey();
			}
		}
		
		if(ephemeralPublicKeyPcdCompressedReceived == null) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
			processingData.updateResponseAPDU(this, "PICC's compressed ephemeral public key from TA is missing. Maybe TA was not performed.", resp);
			return;
		}
		
		log(this, "expected compressed PCD's ephemeral public " + keyAgreementAlgorithmName + " key of " + ephemeralPublicKeyPcdCompressedExpected.length + " bytes length is: " + HexString.encode(ephemeralPublicKeyPcdCompressedExpected), DEBUG);
		log(this, "received compressed PCD's ephemeral public " + keyAgreementAlgorithmName + " key of " + ephemeralPublicKeyPcdCompressedReceived.length + " bytes length is: " + HexString.encode(ephemeralPublicKeyPcdCompressedReceived), DEBUG);
		
		if(Arrays.equals(ephemeralPublicKeyPcdCompressedExpected, ephemeralPublicKeyPcdCompressedReceived)) {
			log(this, "compressed representation of PCD's ephemeral public " + caDomainParameters.getKeyAgreementAlgorithm() + " key matches the one received during previous TA", DEBUG);
		} else{
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "compressed representation of PCD's public " + keyAgreementAlgorithmName + " key does NOT match the one received during previous TA", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		//perform key agreement
		PrivateKey staticPrivateKeyPicc = (PrivateKey) staticKeyPairPicc.getPrivate();
		
		KeyAgreement keyAgreement;
		byte[] sharedSecret = null;
		
		try {
			keyAgreement = KeyAgreement.getInstance(caOid.getKeyAgreementName());
			keyAgreement.init(staticPrivateKeyPicc);
			keyAgreement.doPhase(ephemeralPublicKeyPcd, true);
			sharedSecret = keyAgreement.generateSecret();
		} catch (InvalidKeyException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
			processingData.updateResponseAPDU(this, "invalid key", resp);
			logException(this, e);
			/* there is nothing more to be done here */
			return;
		} catch(NoSuchAlgorithmException | IllegalStateException e) {
			e.printStackTrace();
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
			logException(this, e);
			/* there is nothing more to be done here */
			return;
		}
		
		log(this, "shared secret K of " + sharedSecret.length + " bytes length is: " + HexString.encode(sharedSecret), DEBUG);
		
		//get nonce r_PICC
		int nonceSizeInBytes = 8;
		byte[] rPiccNonce = new byte[nonceSizeInBytes];
		this.secureRandom.nextBytes(rPiccNonce);
		log(this, "nonce r_PICC of " + nonceSizeInBytes + " bytes length is: " + HexString.encode(rPiccNonce), DEBUG);
		
		//compute session keys
		KeyDerivationFunction kdf = new KeyDerivationFunction(caOid.getSymmetricCipherKeyLengthInBytes());
		
		byte[] keyMaterialMac = kdf.deriveMAC(sharedSecret, rPiccNonce);
		byte[] keyMaterialEnc = kdf.deriveENC(sharedSecret, rPiccNonce);
		
		log(this, "PICC's session key for MAC of " + keyMaterialMac.length + " bytes length is: " + HexString.encode(keyMaterialMac), DEBUG);
		log(this, "PICC's session key for ENC of " + keyMaterialMac.length + " bytes length is: " + HexString.encode(keyMaterialEnc), DEBUG);
		
		secretKeySpecMAC = cryptoSupport.generateSecretKeySpecMac(keyMaterialMac);
		secretKeySpecENC = cryptoSupport.generateSecretKeySpecCipher(keyMaterialEnc);
		
		//compute authentication token T_PICC
		TlvDataObjectContainer authenticationTokenInput = buildAuthenticationTokenInput(ephemeralPublicKeyPcd, caDomainParameters, caOid);
		log(this, "authentication token raw data " + authenticationTokenInput, DEBUG);
		byte[] authenticationTokenTpicc = Arrays.copyOf(this.cryptoSupport.macAuthenticationToken(authenticationTokenInput.toByteArray(), this.secretKeySpecMAC), 8);
		log(this, "PICC's authentication token T_PICC of " + authenticationTokenTpicc.length + " bytes length is: " + HexString.encode(authenticationTokenTpicc), DEBUG);
		
		//create and propagate new secure messaging data provider
		SmDataProviderTr03110 smDataProvider;
		try {
			smDataProvider = new SmDataProviderTr03110(this.secretKeySpecENC, this.secretKeySpecMAC);
			processingData.addUpdatePropagation(this, "init SM after successful CA", smDataProvider);
		} catch (GeneralSecurityException e) {
			logException(this, e);
			ResponseApdu failureResponse = new ResponseApdu(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
			processingData.updateResponseAPDU(this, "Unable to initialize new secure messaging", failureResponse);
			return;
		}
		
		ChipAuthenticationMechanism mechanism = new ChipAuthenticationMechanism(caOid, keyReference, ephemeralPublicKeyPcd);
		processingData.addUpdatePropagation(this, "Updated security status with chip authentication information", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, mechanism));
		
		//create and prepare response APDU
		PrimitiveTlvDataObject primitive81 = new PrimitiveTlvDataObject(TAG_81, rPiccNonce);
		log(this, "primitive tag 81 is: " + primitive81, TRACE);
		PrimitiveTlvDataObject primitive82 = new PrimitiveTlvDataObject(TAG_82, authenticationTokenTpicc);
		log(this, "primitive tag 82 is: " + primitive82, TRACE);
		ConstructedTlvDataObject constructed7C = new ConstructedTlvDataObject(TAG_7C);
		constructed7C.addTlvDataObject(primitive81);
		constructed7C.addTlvDataObject(primitive82);
		
		log(this, "response data to be sent is: " + constructed7C, DEBUG);
		
		//create and propagate response APDU
		TlvValue responseData = new TlvDataObjectContainer(constructed7C);
		ResponseApdu resp = new ResponseApdu(responseData, Iso7816.SW_9000_NO_ERROR);
		processingData.updateResponseAPDU(this, "Command General Authenticate successfully processed", resp);
		
		/* 
		 * Request removal of this instance from the stack.
		 * Protocol either successfully completed or failed.
		 * In either case protocol is completed.
		 */
		processingData.addUpdatePropagation(this, "Command General Authenticate successfully processed - Protocol CA completed", new ProtocolUpdate(true));
	}
	
	//XXX SLS remove method from state machine
	public void processCommandInitialize() {
		log(this, "processed COMMAND_INITIALIZE", DEBUG);
	}
	
	//XXX SLS remove method from state machine
	public void processCommandFinalize() {
		log(this, "processed COMMAND_FINALIZE", DEBUG);
	}
	
	//XXX SLS remove method from state machine
	public void processCommandReset() {
		log(this, "processed COMMAND_RESET", DEBUG);
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		
		OidIdentifier caOidIdentifier = new OidIdentifier(OID_id_CA);
		
		Collection<CardObject> caKeyCardObjects = mf.findChildren(
				new KeyIdentifier(), caOidIdentifier);
		
		HashSet<TlvDataObject> secInfos = new HashSet<TlvDataObject>();
		
		for (CardObject curKey : caKeyCardObjects) {
			if (! (curKey instanceof KeyObject)) {
				continue;
			}
			Collection<CardObjectIdentifier> identifiers = curKey.getAllIdentifiers();
			
			//extract keyId
			int keyId = -1;
			for (CardObjectIdentifier curIdentifier : identifiers) {
				if (curIdentifier instanceof KeyIdentifier) {
					keyId = ((KeyIdentifier) curIdentifier).getKeyReference();
					break;
				}
			}
			if (keyId == -1) continue;
			
			//cached values
			byte[] genericCaOidBytes = null;
			
			//construct and add CaInfos
			for (CardObjectIdentifier curIdentifier : identifiers) {
				if (caOidIdentifier.matches(curIdentifier)) {
					byte[] oidBytes = ((OidIdentifier) curIdentifier).getOid().toByteArray();
					genericCaOidBytes = Arrays.copyOfRange(oidBytes, 0, 9);
					
					ConstructedTlvDataObject caInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
					caInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, oidBytes));
					caInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{2}));
					caInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{(byte) keyId}));
					
					secInfos.add(caInfo);					
				}
			}
			
			//extract required data from curKey
			ConstructedTlvDataObject encKey = new ConstructedTlvDataObject(((KeyObject) curKey).getKeyPair().getPublic().getEncoded());
			ConstructedTlvDataObject algIdentifier = (ConstructedTlvDataObject) encKey.getTagField(TAG_SEQUENCE);
			TlvDataObject subjPubKey = encKey.getTagField(TAG_BIT_STRING);
			
			//XXX AMY simplify algorithmIdentifer (using standardized domain parameters) if applicable
			
			//add CaDomainParameterInfo
			ConstructedTlvDataObject caDomainInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
			caDomainInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, genericCaOidBytes));
			caDomainInfo.addTlvDataObject(algIdentifier);
			secInfos.add(caDomainInfo);
			
			//build SubjectPublicKeyInfo
			ConstructedTlvDataObject subjPubKeyInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
			subjPubKeyInfo.addTlvDataObject(algIdentifier);
			subjPubKeyInfo.addTlvDataObject(subjPubKey);
			
			if ((publicity == SecInfoPublicity.AUTHENTICATED) || (publicity == SecInfoPublicity.PRIVILEGED)) {
				//add CaPublicKeyInfo
				ConstructedTlvDataObject caPublicKeyInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
				caPublicKeyInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, Utils.concatByteArrays(TR03110.id_PK, new byte[] {genericCaOidBytes[8]})));
				caPublicKeyInfo.addTlvDataObject(subjPubKeyInfo);
				caPublicKeyInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{(byte) keyId}));
				secInfos.add(caPublicKeyInfo);
			}
			
			//TODO handle privilegedTerminalInfo
			//TODO handle duplicates?
		}
		
		return secInfos;
	}
	
}
