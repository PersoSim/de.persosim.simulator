package de.persosim.simulator.protocols.pace;

import static de.persosim.simulator.protocols.Tr03110Utils.buildAuthenticationTokenInput;
import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.ERROR;
import static de.persosim.simulator.utils.PersoSimLogger.TRACE;
import static de.persosim.simulator.utils.PersoSimLogger.log;
import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.DomainParameterSetCardObject;
import de.persosim.simulator.cardobjects.DomainParameterSetIdentifier;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.MasterFileIdentifier;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.crypto.CryptoSupport;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.KeyDerivationFunction;
import de.persosim.simulator.crypto.certificates.PublicKeyReference;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.protocols.AbstractProtocolStateMachine;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.protocols.ResponseData;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.Tr03110Utils;
import de.persosim.simulator.protocols.ta.Authorization;
import de.persosim.simulator.protocols.ta.CertificateHolderAuthorizationTemplate;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.AuthorizationMechanism;
import de.persosim.simulator.secstatus.AuthorizationStore;
import de.persosim.simulator.secstatus.PaceMechanism;
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

/**
 * @author slutters
 *
 */
public abstract class AbstractPaceProtocol extends AbstractProtocolStateMachine implements Pace, TlvConstants {
	
	/*--------------------------------------------------------------------------------*/
	
	public static final short P1P2_C1A4_SET_AT                 = (short) 0xC1A4;
	public static final short P1P2_81B6_SET_DST                = (short) 0x81B6;
	public static final short P1P2_00BE_VERIFY_CERTIFICATE     = (short) 0x00BE;
	public static final short P1P2_0000_NO_FURTHER_INFORMATION = (short) 0x0000;
	
	// values 0x00 - 0x3F are reserved for common COMMAND_X variables
	public static final byte COMMAND_GET_NONCE                     = (byte) 0x40;
	public static final byte COMMAND_MAP_NONCE                     = (byte) 0x41;
	public static final byte COMMAND_PERFORM_KEY_AGREEMENT         = (byte) 0x42;
	
	public static final byte APDU_SET_AT                 = 0;
	public static final byte APDU_GET_NONCE              = 1;
	public static final byte APDU_MAP_NONCE              = 2;
	public static final byte APDU_PERFORM_KEY_AGREEMENT  = 3;
	public static final byte APDU_MUTUAL_AUTHENTICATE    = 4;
	
	/*--------------------------------------------------------------------------------*/
	
	protected PaceOid paceOid;
	protected PasswordAuthObject pacePassword;
	
	protected int paceDomainParameterId;
	protected DomainParameterSet paceDomainParametersUnmapped;
	protected DomainParameterSet paceDomainParametersMapped;
	
	protected SecureRandom secureRandom;
	
	protected SecretKeySpec secretKeySpecNonce;
	protected SecretKeySpec secretKeySpecMAC;
	protected SecretKeySpec secretKeySpecENC;
	protected CryptoSupport cryptoSupport;
	
	protected byte[] piccsPlainNonceS;
	
	protected KeyPair ephemeralKeyPairPicc;
	protected PublicKey ephemeralPublicKeyPcd;
	
	protected MappingResult mappingResult;
	
	protected AuthorizationStore authorizationStore;
	
	TrustPointCardObject trustPoint;
	CertificateHolderAuthorizationTemplate usedChat;
	TaOid terminalTypeOid;
	
	/*--------------------------------------------------------------------------------*/
	
	public AbstractPaceProtocol() {
		super("PACE");
		
		secureRandom = new SecureRandom();
	}
	
	/**
	 * @param bytes the OID given in the SET AT command
	 * @return the PaceOid helper class to be used by this protocol
	 */
	protected PaceOid getOid(byte [] bytes){
		return new PaceOid(bytes);
	}
	
	/**
	 * This method processes the command APDU SET_AT.
	 */
	public void processCommandSetAT() {
		//get commandDataContainer
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
		
		/* 
		 * Extract security parameters
		 */
		
		/* PACE OID */
		/* Check for the PACE OID for itself */
		/* tlvObject will never be null if APDU passed check against APDU specification */
		TlvDataObject tlvObject = commandData.getTlvDataObject(TAG_80);
		
		try {
			paceOid = getOid(tlvObject.getValueField());
		} catch (RuntimeException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			logException(this, e);
			/* there is nothing more to be done here */
			return;
		}
		
		/* PACE password */
		/* Check for the PACE password itself */
		tlvObject = commandData.getTlvDataObject(TAG_83);
		
		CardObject pwdCandidate = cardState.getObject(new AuthObjectIdentifier(tlvObject.getValueField()), Scope.FROM_MF);
		if (pwdCandidate instanceof PasswordAuthObject){
			pacePassword = (PasswordAuthObject) pwdCandidate;
			log(this, "selected password is: " + getPasswordName(), DEBUG);
		} else {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			this.processingData.updateResponseAPDU(this, "no fitting authentication object found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		/* PACE domain parameters */
		/*
		 * {@link #tlvObject} may only be null if the combination of OID and domain parameters
		 * set is not ambiguous. This is the case iff among all sets of domain
		 * parameters registered for the selected OID exactly one matches the
		 * key agreement implicitly indicated by the OID.
		 */
		tlvObject = commandData.getTlvDataObject(TAG_84);
		
		DomainParameterSetIdentifier domainParameterSetIdentifier;
		if(tlvObject == null) {
			domainParameterSetIdentifier = new DomainParameterSetIdentifier();
		} else{
			domainParameterSetIdentifier = new DomainParameterSetIdentifier(tlvObject.getValueField());
		}
		
		CardObject cardObject;
		try {
			cardObject = Tr03110Utils.getSpecificChild(cardState.getObject(new MasterFileIdentifier(), Scope.FROM_MF), domainParameterSetIdentifier, new OidIdentifier(paceOid));
		} catch (IllegalArgumentException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		}
		
		if((cardObject instanceof DomainParameterSetCardObject)) {
			DomainParameterSetCardObject domainParameterObject = (DomainParameterSetCardObject) cardObject;
			paceDomainParametersUnmapped = domainParameterObject.getDomainParameterSet();
			paceDomainParameterId = domainParameterObject.getPrimaryIdentifier().getInteger();
		} else{
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			this.processingData.updateResponseAPDU(this, "invalid key reference", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		/* CHAT */
		/*
		 * {@link #tlvObject} may be null if no terminal authentication follows PACE
		 */
		tlvObject = commandData.getTlvDataObject(TAG_7F4C);
		if (tlvObject != null){
			try {
				usedChat = new CertificateHolderAuthorizationTemplate((ConstructedTlvDataObject) tlvObject);
				
				HashMap<Oid, Authorization> authorizations = getAuthorizationsFromCommandData(commandData);
				
				authorizationStore = new AuthorizationStore(authorizations);
				
				terminalTypeOid = usedChat.getObjectIdentifier();
				TerminalType terminalType = usedChat.getTerminalType();

				trustPoint = (TrustPointCardObject) cardState.getObject(
						new TrustPointIdentifier(terminalType), Scope.FROM_MF);
				if (!checkPasswordAndAccessRights(usedChat, pacePassword)){
					ResponseApdu resp = new ResponseApdu(
							Iso7816.SW_6A80_WRONG_DATA);
					this.processingData.updateResponseAPDU(this, "The given terminal type and password does not match the access rights", resp);

					/* there is nothing more to be done here */
					return;
				}
			} catch (Exception e) {
				//FIXME check PokemonException
				ResponseApdu resp = new ResponseApdu(
						Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
				this.processingData.updateResponseAPDU(this, e.getMessage(),
						resp);
				logException(this, e);
				/* there is nothing more to be done here */
				return;
			}
		}
		
		this.cryptoSupport = paceOid.getCryptoSupport();
		
		String logString = "new OID is " + paceOid + ", new " + pacePassword;
		
		log(this, logString, DEBUG);	

		/* 
		 * Create and set crypto parameters
		 */
		KeyDerivationFunction kdf = new KeyDerivationFunction(paceOid.getSymmetricCipherKeyLengthInBytes());
		byte[] commonSecret = pacePassword.getPassword();
		
		log(this, "common secret is: " + HexString.encode(commonSecret), TRACE);
		
		byte[] keyMaterialForEncryptionOfNonce = kdf.derivePI(commonSecret);
		
		log(this, "computed raw key material of byte length " + keyMaterialForEncryptionOfNonce.length + " is: " + HexString.encode(keyMaterialForEncryptionOfNonce), TRACE);
		
		this.secretKeySpecNonce = this.cryptoSupport.generateSecretKeySpecCipher(keyMaterialForEncryptionOfNonce);
		
		log(this, "computed " + paceOid.getSymmetricCipherAlgorithmName() + " key material: " + HexString.encode(keyMaterialForEncryptionOfNonce), DEBUG);
		
		// If PIN is used, check for retry counter.
		ResponseData isPasswordUsable = isPasswordUsable(pacePassword, cardState);
		if (isPasswordUsable != null){
			//create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(isPasswordUsable.getStatusWord());
			this.processingData.updateResponseAPDU(this, isPasswordUsable.getResponse(), resp);
			return;
		}
		
		//create and propagate response APDU
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "Command SetAt successfully processed", resp);
	}
	
	public HashMap<Oid, Authorization> getAuthorizationsFromCommandData(TlvDataObjectContainer commandData) {
		HashMap<Oid, Authorization> authorizations = new HashMap<Oid, Authorization>();
		
		TlvDataObject tlvObject = commandData.getTlvDataObject(TAG_7F4C);
		CertificateHolderAuthorizationTemplate chatFromCommandData = null;
		if (tlvObject != null){
				try {
					chatFromCommandData  = new CertificateHolderAuthorizationTemplate((ConstructedTlvDataObject) tlvObject);
					authorizations.put(chatFromCommandData.getObjectIdentifier(), chatFromCommandData.getRelativeAuthorization());
				} catch (CertificateNotParseableException e) {
					ResponseApdu resp = new ResponseApdu(
							Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
					this.processingData.updateResponseAPDU(this, e.getMessage(),
							resp);
				}
		}

		return authorizations;
	}
	
	/**
	 * Get the status word for the use of the given password.
	 * 
	 * @param pacePassword
	 *            the password to check
	 * @param cardState
	 *            the card state accessor to be used
	 * @return the status word encoding an error or null if the password is
	 *         usable
	 */
	public static ResponseData isPasswordUsable(PasswordAuthObject pacePassword, CardStateAccessor cardState){
		if (pacePassword instanceof PasswordAuthObjectWithRetryCounter) {
			PasswordAuthObjectWithRetryCounter pacePasswordWithRetryCounter = (PasswordAuthObjectWithRetryCounter) pacePassword;
			
			int retryCounter = pacePasswordWithRetryCounter.getRetryCounterCurrentValue();
			short retryCounterDefault = (short) pacePasswordWithRetryCounter.getRetryCounterDefaultValue();
			
			short sw;
			String note;
			
			if (pacePassword.getLifeCycleState().equals(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED)) {
				if (retryCounter != retryCounterDefault) {
					if (retryCounter == 1) {
						sw = Iso7816.SW_63C1_COUNTER_IS_1;
						if(isPinTemporarilyResumed(cardState)) {
							note = "PIN is temporarily resumed due to preceding CAN";
						} else{
							note = "PIN is suspended, use CAN first for temporary resume or unblock PIN";
						}
						/* there is nothing more to be done here */
						return new ResponseData(sw, note);
					} else {
						// If(retryCounterPin > 1) - 0 is not possible as this
						// would have caused the PIN to be deactivated before.
						// In this case this code would have never been reached
						// due to the previous check for the PIN being activated

						sw = (short) 0x63C0;
						sw |= ((short) (retryCounter & (short) 0x000F));
						
						note = "PACE with PIN has previously failed - current retry counter for PIN is " + retryCounter;
						return new ResponseData(sw, note);
					}
				}
			} else {
				/* there is nothing more to be done here */
				return new ResponseData(Iso7816.SW_6283_SELECTED_FILE_DEACTIVATED, "PIN is deactivated");
			}
		}
		return null;
	}
	
	/**
	 * This method checks for allowed combinations of chat and password as
	 * defined in TR-03110 v2.10 Part 2 2.4.
	 * 
	 * @param chat
	 *            Certificate holder authorization template to check
	 * @param password
	 *            to check
	 * @return true, iff the combination used is allowed
	 */
	public static boolean checkPasswordAndAccessRights(
			CertificateHolderAuthorizationTemplate chat,
			PasswordAuthObject password) {
		switch (chat.getTerminalType()) {
		case AT:
			if (password.getPasswordIdentifier() == ID_PIN
					|| (password.getPasswordIdentifier() == ID_CAN && chat
							.getRelativeAuthorization().getAuthorization()
							.getBit(Tr03110Utils.ACCESS_RIGHTS_AT_CAN_ALLOWED_BIT))) {
				return true;
			}
			break;
		case IS:
			if (password.getPasswordIdentifier() == ID_CAN
					|| password.getPasswordIdentifier() == ID_MRZ) {
				return true;
			}
			break;
		case ST:
			if (password.getPasswordIdentifier() == ID_CAN
					|| password.getPasswordIdentifier() == ID_PUK
					|| password.getPasswordIdentifier() == ID_PIN) {
				return true;
			}
			break;
		}
		return false;
	}

	/**
	 * This method processes the command APDU GET_NONCE
	 */
	public void processCommandGetNonce() {
		byte[] encryptedNonce;
		int blockSizeInBytes, keySizeInBytes, nonceSizeInBytes, multiplicationFactor;
		PrimitiveTlvDataObject primitive80;
		ConstructedTlvDataObject constructed7C;
		
		keySizeInBytes = paceOid.getSymmetricCipherKeyLengthInBytes();
		blockSizeInBytes = this.cryptoSupport.getBlockSize();
		
		multiplicationFactor = (int) Math.ceil(keySizeInBytes/(double) blockSizeInBytes);
		nonceSizeInBytes = multiplicationFactor * blockSizeInBytes;
		
		log(this, "key length k in Bytes is " + keySizeInBytes + ", block size in Bytes is " + blockSizeInBytes + " --> nonce s must be of smallest length l in Bytes, l being a multiple of the block size, such that l<=k", TRACE);
		
		this.piccsPlainNonceS = new byte[nonceSizeInBytes];
		this.secureRandom.nextBytes(this.piccsPlainNonceS);
		
		log(this, "new (plain) nonce s of byte length " + this.piccsPlainNonceS.length + " is " + HexString.encode(this.piccsPlainNonceS), TRACE);
		
		encryptedNonce = this.cryptoSupport.encryptWithIvZero(this.piccsPlainNonceS, this.secretKeySpecNonce);
		
		log(this, "(encryted) nonce z = E_KPi(s) is " + HexString.encode(encryptedNonce), TRACE);
		
		primitive80 = new PrimitiveTlvDataObject(TAG_80, encryptedNonce);
		log(this, "primitive tag 80 is: " + primitive80, TRACE);
		constructed7C = new ConstructedTlvDataObject(TAG_7C);
		constructed7C.addTlvDataObject(primitive80);
		
		//create and propagate response APDU
		TlvValue responseData = new TlvDataObjectContainer(constructed7C);
		ResponseApdu resp = new ResponseApdu(responseData, Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "Command GetNonce successfully processed", resp);
	}
	
	/**
	 * This method processes the command APDU MAP_NONCE
	 */
	public void processCommandMapNonce() {
		// create and initialize mapping
		Mapping mapping = paceOid.getMapping();
		String keyAgreementName = paceDomainParametersUnmapped.getKeyAgreementAlgorithm();
		
		/*
		 * Extract mapping data
		 */
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
		TlvDataObject tlvObject = commandData.getTlvDataObject(new TlvPath(TAG_7C, TAG_81));
		
		/* 
		 * The received mapping data may contain the following
		 * objects depending on the selected mapping:
		 * IM: the PCD's nonce t
		 * GM: the PCD's public DH/ECDH key component to the base of g/G as defined by the used domain parameters
		 * 
		 * Due to this differentiation validity checks of the received mapping data are performed directly by the mapping itself.
		 */
		byte[] mappingDataFromPcd = tlvObject.getValueField();
		
		log(this, "mapping data received from PCD is expected to contain " + mapping.getMeaningOfMappingData(), DEBUG);
		log(this, "unchecked mapping data content of " + mappingDataFromPcd.length + " bytes length is: " + HexString.encode(mappingDataFromPcd), DEBUG);
		log(this, "nonce s generated by PICC during processing of GetNonce command is " + HexString.encode(piccsPlainNonceS), TRACE);
		
		byte[] mappingResponse;
		
		try {
			log(this, "about to perform " + mapping.getMappingName(), DEBUG);
			mappingResult = mapping.performMapping(paceDomainParametersUnmapped, piccsPlainNonceS, mappingDataFromPcd);
			
			ephemeralKeyPairPicc = mappingResult.getKeyPairPiccMapped();
			paceDomainParametersMapped = mappingResult.getMappedDomainParameters();
			mappingResponse = mappingResult.getMappingResponse();
		} catch (InvalidAlgorithmParameterException | InvalidKeySpecException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
			processingData.updateResponseAPDU(this, "Mapping failed due to " + e.getMessage(), resp);
			logException(this, e);
			/* there is nothing more to be done here */
			return;
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
			logException(this, e);
			/* there is nothing more to be done here */
			return;
		}

		
		log(this, "PICC's ephemeral public  mapped " + keyAgreementName + " key is " + new TlvDataObjectContainer(ephemeralKeyPairPicc.getPublic().getEncoded()), TRACE);
		log(this, "PICC's ephemeral private mapped " + keyAgreementName + " key is " + new TlvDataObjectContainer(ephemeralKeyPairPicc.getPrivate().getEncoded()), TRACE);
		
		// Build response data
		TlvValue responseData = buildResponseDataForMapNonce(mappingResponse);
		
		ResponseApdu resp = new ResponseApdu(responseData, Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "Command MapNonce successfully processed", resp);
	}
	
	/**
	 * Build the response data structure to GAP:MapNonce
	 * 
	 * @param mappingResponse
	 * @return the response data
	 */
	public TlvValue buildResponseDataForMapNonce(byte [] mappingResponse){
		// Build response data
		PrimitiveTlvDataObject primitive82 = new PrimitiveTlvDataObject(TAG_82, mappingResponse);
		ConstructedTlvDataObject constructed7C = new ConstructedTlvDataObject(TAG_7C);
		constructed7C.addTlvDataObject(primitive82);
		
		// Create and propagate response APDU
		TlvValue responseData = new TlvDataObjectContainer(constructed7C);
		return responseData;
	}
	
	/**
	 * This method processes the command APDU PERFORM_KEY_AGREEMENT.
	 */
	public void processCommandPerformKeyAgreement() {
		byte[] ephemeralPublicKeyComponentPicc;
		
		//get commandDataContainer
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
						
		TlvDataObject tlvObject = commandData.getTlvDataObject(new TlvPath(new TlvTag((byte) 0x7C), new TlvTag((byte) 0x83)));
		byte[] rawKeyPlain = tlvObject.getValueField();
		
		log(this, "PCD's public raw key of " + rawKeyPlain.length + " bytes length is: " + HexString.encode(rawKeyPlain), TRACE);
		
		try {
			ephemeralPublicKeyPcd = paceDomainParametersMapped.reconstructPublicKey(rawKeyPlain);
			ephemeralPublicKeyComponentPicc = paceDomainParametersMapped.encodePublicKey(ephemeralKeyPairPicc.getPublic());
			log(this, "PCD's  ephemeral public  mapped " + paceDomainParametersMapped.getKeyAgreementAlgorithm() + " key is " + new TlvDataObjectContainer(ephemeralPublicKeyPcd.getEncoded()), TRACE);
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
		
		log(this, "bare response data of byte length " + ephemeralPublicKeyComponentPicc.length + " is " + HexString.encode(ephemeralPublicKeyComponentPicc), DEBUG);
		
		/* create and propagate response APDU */
		TlvValue responseData = buildResponseDataForKeyAgreement(paceDomainParametersMapped, ephemeralPublicKeyComponentPicc);
		
		ResponseApdu resp = new ResponseApdu(responseData, Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "Command PerformKeyAgreement successfully processed", resp);
	}
	
	/**
	 * Build the response data structure to GAP:PerformKeyAgreement
	 * 
	 * @param paceDomainParametersMapped
	 * @param ephemeralPublicKeyComponentPicc
	 * @return the response data
	 */
	public TlvValue buildResponseDataForKeyAgreement(DomainParameterSet paceDomainParametersMapped, byte [] ephemeralPublicKeyComponentPicc){
		PrimitiveTlvDataObject primitive84 = new PrimitiveTlvDataObject(TAG_84, ephemeralPublicKeyComponentPicc);
		ConstructedTlvDataObject constructed7C = new ConstructedTlvDataObject(TAG_7C);
		constructed7C.addTlvDataObject(primitive84);
		TlvValue responseData = new TlvDataObjectContainer(constructed7C);
		return responseData;
	}
	
	/**
	 * This method processes the command APDU MUTUAL_AUTHENTICATE.
	 */
	public void processCommandMutualAuthenticate() {
		TlvDataObject tlvObject;
		byte[] pcdTokenReceivedFromPCD, piccToken, pcdToken;
		TlvPath path;
		
		path = new TlvPath(new TlvTag[]{TAG_7C, TAG_85});
		
		/* get commandDataContainer */
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
						
		tlvObject = commandData.getTlvDataObject(path);
		pcdTokenReceivedFromPCD = tlvObject.getValueField();
		
		/* construct authentication token input based on PACE OID and ephemeral keys */
		TlvDataObjectContainer piccTokenInput = buildAuthenticationTokenInput(ephemeralPublicKeyPcd, paceDomainParametersMapped, paceOid);
		TlvDataObjectContainer pcdTokenInput = buildAuthenticationTokenInput(ephemeralKeyPairPicc.getPublic(), paceDomainParametersMapped, paceOid);
		
		log(this, "picc token raw data " + piccTokenInput, DEBUG);
		log(this, "pcd  token raw data " + pcdTokenInput, DEBUG);
		
		try {
			KeyAgreement keyAgreement = KeyAgreement.getInstance(paceOid.getKeyAgreementName(), Crypto.getCryptoProvider());
			keyAgreement.init(this.ephemeralKeyPairPicc.getPrivate());
			keyAgreement.doPhase(this.ephemeralPublicKeyPcd, true);
			
			byte[] sharedSecret = keyAgreement.generateSecret();
			
			log(this, "shared secret of byte length " + sharedSecret.length + " resulting from " + paceOid.getKeyAgreementName() + " key agreement is " + HexString.encode(sharedSecret), DEBUG);
			
			KeyDerivationFunction kdf = new KeyDerivationFunction(paceOid.getSymmetricCipherKeyLengthInBytes());
			
			byte[] keyMaterialMAC = kdf.deriveMAC(sharedSecret);
			byte[] keyMaterialENC = kdf.deriveENC(sharedSecret);
			
			this.secretKeySpecMAC = this.cryptoSupport.generateSecretKeySpecMac(keyMaterialMAC);
			this.secretKeySpecENC = this.cryptoSupport.generateSecretKeySpecCipher(keyMaterialENC);
			
			log(this, "final " + secretKeySpecENC.getAlgorithm() + " symmetric key material ENC is " + HexString.encode(secretKeySpecENC.getEncoded()), DEBUG);
			log(this, "final " + secretKeySpecMAC.getAlgorithm() + " symmetric key material MAC is " + HexString.encode(secretKeySpecMAC.getEncoded()), DEBUG);
		} catch (InvalidKeyException | IllegalStateException | NoSuchAlgorithmException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
			processingData.updateResponseAPDU(this, "Invalid symmetric key", resp);
			logException(this, e);
			//XXX shouldn't we return here? (remember to mark the protocol as finished)
		}
		
		/* get first 8 bytes of mac */
		piccToken = Arrays.copyOf(this.cryptoSupport.macAuthenticationToken(piccTokenInput.toByteArray(), this.secretKeySpecMAC), 8);
		log(this, "picc token data is: " + HexString.encode(piccToken), DEBUG);
		
		pcdToken = Arrays.copyOf(this.cryptoSupport.macAuthenticationToken(pcdTokenInput.toByteArray(), this.secretKeySpecMAC), 8);
		log(this, "pcd  token data is: " + HexString.encode(pcdToken), DEBUG);
		
		log(this, "expected pcd token data is: " + HexString.encode(pcdToken), DEBUG);
		log(this, "received pcd token data is: " + HexString.encode(pcdTokenReceivedFromPCD), DEBUG);
		
		boolean paceSuccessful;
		short sw;
		String note;
		
		if(Arrays.equals(pcdToken, pcdTokenReceivedFromPCD)) {
			log(this, "Token received from PCD matches expected one", DEBUG);
			
			if(pacePassword instanceof PasswordAuthObjectWithRetryCounter) {
				ResponseData pinResponse = getMutualAuthenticatePinManagementResponsePaceSuccessful(pacePassword, cardState);
				
				sw = pinResponse.getStatusWord();
				note = pinResponse.getResponse();
				
				paceSuccessful = !Iso7816Lib.isReportingError(sw);
			} else{
				sw = Iso7816.SW_9000_NO_ERROR;
				note = "MutualAuthenticate processed successfully";
				paceSuccessful = true;
			}
		} else{
			//PACE failed
			log(this, "Token received from PCD does NOT match expected one", DEBUG);
			paceSuccessful = false;
			
			if(pacePassword.getPasswordIdentifier() == Pace.ID_PIN) {
				ResponseData pinResponse = getMutualAuthenticatePinManagementResponsePaceFailed((PasswordAuthObjectWithRetryCounter) pacePassword);
				sw = pinResponse.getStatusWord();
				note = pinResponse.getResponse();
			} else{
				sw = Iso7816.SW_6A80_WRONG_DATA;
				note = "authentication token received from PCD does NOT match expected one";
			}
		}
		
		ResponseApdu responseApdu;
		
		if(paceSuccessful) {
			ConstructedTlvDataObject responseContent = buildMutualAuthenticateResponse(piccToken);
			if (setSmDataProvider()){
				PaceMechanism paceMechanism = new PaceMechanism(pacePassword, paceDomainParametersMapped.comp(ephemeralKeyPairPicc.getPublic()), terminalTypeOid);
				processingData.addUpdatePropagation(this, "Security status updated with PACE mechanism", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, paceMechanism));
				
				
				HashSet<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
				previousMechanisms.add(AuthorizationMechanism.class);
				Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
				
				AuthorizationMechanism authMechanism = null;
				if (currentMechanisms.size() >= 1){
					authMechanism = (AuthorizationMechanism) currentMechanisms.toArray()[0];
				}
				
				AuthorizationMechanism newAuthMechanism;
				
				if(authorizationStore == null) {
					authorizationStore = new AuthorizationStore();
				}
				
				if(authMechanism == null) {
					newAuthMechanism = new AuthorizationMechanism(authorizationStore);
				} else{
					newAuthMechanism = authMechanism.getUpdatedMechanism(authorizationStore);
				}
				
				processingData.addUpdatePropagation(this, "Security status updated with authorization mechanism", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, newAuthMechanism));
				
				
				responseApdu = new ResponseApdu(new TlvDataObjectContainer(responseContent), sw);
			} else {
				return;
			}
		} else {
			responseApdu = new ResponseApdu(sw);
		}
		this.processingData.updateResponseAPDU(this, note, responseApdu);
		
		/* 
		 * Request removal of this instance from the stack.
		 * Protocol either successfully completed or failed.
		 * In either case protocol is completed.
		 */
		processingData.addUpdatePropagation(this, "Command MutualAuthenticate successfully processed - Protocol PACE completed", new ProtocolUpdate(true));
	}
	
	protected void addCars(ConstructedTlvDataObject constructed7c){
		//add CARs to response data if available
		if (trustPoint != null) {
			if (trustPoint.getCurrentCertificate() != null
					&& trustPoint.getCurrentCertificate()
							.getCertificateHolderReference() instanceof PublicKeyReference) {
				constructed7c
						.addTlvDataObject(new PrimitiveTlvDataObject(
								TAG_87, trustPoint.getCurrentCertificate()
										.getCertificateHolderReference()
										.getBytes()));
				if (trustPoint.getPreviousCertificate() != null
						&& trustPoint.getPreviousCertificate()
								.getCertificateHolderReference() instanceof PublicKeyReference) {
					constructed7c
							.addTlvDataObject(new PrimitiveTlvDataObject(
									TAG_88,
									trustPoint
											.getPreviousCertificate()
											.getCertificateHolderReference()
											.getBytes()));
				}
			}
		}
	}
	
	protected boolean setSmDataProvider(){

		try {
			//create and propagate new secure messaging data provider
			SmDataProviderTr03110 smDataProvider = new SmDataProviderTr03110(this.secretKeySpecENC, this.secretKeySpecMAC);
			processingData.addUpdatePropagation(this, "init SM after successful PACE", smDataProvider);
			
			return true;
		} catch (GeneralSecurityException e) {
			logException(this, e);
			ResponseApdu failureResponse = new ResponseApdu(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
			processingData.updateResponseAPDU(this, "Unable to initialize new secure messaging", failureResponse);
			return false;
		}
	}
	
	protected ConstructedTlvDataObject buildMutualAuthenticateResponse(byte[] piccToken){
		PrimitiveTlvDataObject primitive86;
		ConstructedTlvDataObject constructed7C;
		primitive86 = new PrimitiveTlvDataObject(TAG_86, piccToken);
		constructed7C = new ConstructedTlvDataObject(TAG_7C);
		constructed7C.addTlvDataObject(primitive86);
		
		addCars(constructed7C);
		
		return constructed7C;
	}
	
	/**
	 * This method returns data required to send a response APDU for Mutual Authenticate if PACE was performed using PIN as password and failed.
	 * @return data required to send a response APDU for Mutual Authenticate
	 */
	public static ResponseData getMutualAuthenticatePinManagementResponsePaceFailed(PasswordAuthObjectWithRetryCounter pacePasswordPin) {
		int pinRetryCounter = pacePasswordPin.getRetryCounterCurrentValue();
		log(AbstractPaceProtocol.class, "PACE with PIN has failed - PIN retry counter will be decremented, current value is: " + pinRetryCounter, DEBUG);
		pacePasswordPin.decrementRetryCounter();
		pinRetryCounter = pacePasswordPin.getRetryCounterCurrentValue();
		log(AbstractPaceProtocol.class, "PACE with PIN has failed - PIN retry counter has been decremented, current value is: " + pinRetryCounter, DEBUG);
		
		short sw = (short) 0x63C0;
		sw |= ((short) (pinRetryCounter & (short) 0x000F)); 
		
		String note = "PACE with PIN has failed - PIN retry counter has been decremented, current value is: " + pinRetryCounter;
		
		return new ResponseData(sw, note);
	}
	
	/**
	 * This method returns data required to send a response APDU for Mutual Authenticate if PACE was performed using PIN as password and succeeded.
	 * @responseData the response data to be sent with the response APDU
	 * @return data required to send a response APDU for Mutual Authenticate
	 */
	public static ResponseData getMutualAuthenticatePinManagementResponsePaceSuccessful(PasswordAuthObject password, CardStateAccessor cardState) {
		short sw;
		String note;
		
		if(password.getLifeCycleState() == Iso7816LifeCycleState.OPERATIONAL_ACTIVATED) {
			PasswordAuthObjectWithRetryCounter pacePasswordPin = (PasswordAuthObjectWithRetryCounter) password;
			int pinRetryCounter = pacePasswordPin.getRetryCounterCurrentValue();
			int pinRetryCounterDefault = pacePasswordPin.getRetryCounterDefaultValue();
			
			if(pinRetryCounter != pinRetryCounterDefault) {
				if(pinRetryCounter == 1) {
					if (isPinTemporarilyResumed(cardState)) {
						// everything ok, password is PIN, PIN activated, retry counter is not default value, retry counter == 1, PIN is suspended, PIN is temporarily resumed
						pacePasswordPin.resetRetryCounterToDefault();
						sw = Iso7816.SW_9000_NO_ERROR;
						note = "MutualAuthenticate processed successfully with password PIN after CAN - PIN retry counter has been reset from: " + pinRetryCounter + " to: " + pacePasswordPin.getRetryCounterCurrentValue();
					} else{
						// everything ok, password is PIN, PIN activated, retry counter is not default value, retry counter == 1, PIN is suspended, PIN is not temporarily resumed
						sw = Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED;
						note = "MutualAuthenticate processed successfully but PIN is suspended";
					}
				} else if (pinRetryCounter == 0){
					// everything ok, password is PIN, PIN activated, retry counter is not default value, retry counter == 0, PIN is blocked
					sw = Iso7816.SW_6983_FILE_INVALID;
					note = "MutualAuthenticate processed successfully but PIN is blocked";
				} else{
					// everything ok, password is PIN, PIN activated, retry counter is not default value, retry counter > 1
					pacePasswordPin.resetRetryCounterToDefault();
					
					sw = Iso7816.SW_9000_NO_ERROR;
					note = "MutualAuthenticate processed successfully with password PIN - PIN retry counter has been reset from: " + pinRetryCounter + " to: " + pacePasswordPin.getRetryCounterCurrentValue();
				}
			} else{
				// everything ok, password is PIN, PIN activated, retry counter is default value
				sw = Iso7816.SW_9000_NO_ERROR;
				note = "MutualAuthenticate processed successfully";
			}
		} else{
			// everything ok, password is PIN, PIN deactivated
			sw = Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE;
			note = "MutualAuthenticate processed successfully but PIN is deactivated";
		}
		
		return new ResponseData(sw, note);
	}
	
	/**
	 * This method returns the name of the password associated with the currently used password identifier.
	 * @return the name of the used password
	 */
	public String getPasswordName() {
		return getPasswordName(pacePassword.getPasswordIdentifier());
	}
	
	/**
	 * This method returns the name of the password associated with the provided password identifier.
	 * @pwdIdentifier the password identifier
	 * @return the name of the used password
	 */
	public static String getPasswordName(int pwdIdentifier) {
		switch (pwdIdentifier) {
		case Pace.ID_CAN:
			return "CAN";
		case Pace.ID_MRZ:
			return "MRZ";
		case Pace.ID_PIN:
			return "PIN";
		case Pace.ID_PUK:
			return "PUK";
		default:
			return "unknown password identifier " + pwdIdentifier;
		}
	}
	
	@Override
	public void initialize() {
		
	}

	/**
	 * This method checks whether PACE has previously been run with CAN.
	 * @return true iff PACE has previously been run with CAN, otherwise false
	 */
	public static boolean isPinTemporarilyResumed(CardStateAccessor cardState) {
		HashSet<Class<? extends SecMechanism>> paceMechanisms = new HashSet<>();
		paceMechanisms.add(PaceMechanism.class);
		
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, paceMechanisms);
		if (currentMechanisms.size() > 0){
			PaceMechanism paceMechanism = (PaceMechanism) currentMechanisms.toArray()[0];
			PasswordAuthObject previouslyUsedPwd = paceMechanism.getUsedPassword();
			int previouslyUsedPasswordIdentifier = previouslyUsedPwd.getPasswordIdentifier();
			log(AbstractPaceProtocol.class, "last successfull PACE run used " + getPasswordName(previouslyUsedPasswordIdentifier) + " as password with value " + HexString.encode(previouslyUsedPwd.getPassword()), DEBUG);
			return previouslyUsedPasswordIdentifier == Pace.ID_CAN;
		} else{
			return false;
		}
	}

	/**
	 * This method performs all necessary steps to react to interrupted chaining.
	 */
	public void processChainingInterrupted() {
		ResponseApdu resp = new ResponseApdu(SW_6883_LAST_COMMAND_EXPECTED);
		processingData.updateResponseAPDU(this, "chaining interrupted", resp);
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		OidIdentifier paceOidIdentifier = new OidIdentifier(OID_id_PACE);
		
		Collection<CardObject> domainParameterCardObjects = mf.findChildren(
				new DomainParameterSetIdentifier(), paceOidIdentifier);
		
		HashSet<TlvDataObject> secInfos = new HashSet<TlvDataObject>();
		
		for (CardObject curDomainParam : domainParameterCardObjects) {
			Collection<CardObjectIdentifier> identifiers = curDomainParam.getAllIdentifiers();
			
			//extract domainParameterId
			int parameterId = -1;
			for (CardObjectIdentifier curIdentifier : identifiers) {
				if (curIdentifier instanceof DomainParameterSetIdentifier) {
					parameterId = ((DomainParameterSetIdentifier) curIdentifier).getDomainParameterId();
					break;
				}
			}
			if (parameterId == -1) continue;
			
			//construct and add PaceInfos
			for (CardObjectIdentifier curIdentifier : identifiers) {
				if (paceOidIdentifier.matches(curIdentifier)) {
					byte[] oidBytes = ((OidIdentifier) curIdentifier).getOid().toByteArray();
					
					ConstructedTlvDataObject paceInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
					paceInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, oidBytes));
					paceInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{2}));
					paceInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{(byte) parameterId}));
					
					secInfos.add(paceInfo);					
				}
			}
			
			createDomainParameterInfo(secInfos, parameterId, paceOidIdentifier, identifiers, domainParameterCardObjects);
		}
		
		//TODO handle duplicates?
		
		return secInfos;
	}

	/**
	 * This method creates security infos for proprietary domain parameter
	 * 
	 * @param secInfos HashSet with all security infos
	 * @param parameterId the identifier for domain parameter
	 * @param paceOidIdentifier the pace oid identifier
	 * @param identifiers list of all identifier of the current domain parameter
	 * @param domainParameterCardObjects collection of card objects which matches the paceOidIdentifier
	 */
	public void createDomainParameterInfo(HashSet<TlvDataObject> secInfos, int parameterId,
			OidIdentifier paceOidIdentifier, Collection<CardObjectIdentifier> identifiers,
			Collection<CardObject> domainParameterCardObjects) {
		
		//IMPL add required domainParameterInfo elemtens here
		
	}
	
}
