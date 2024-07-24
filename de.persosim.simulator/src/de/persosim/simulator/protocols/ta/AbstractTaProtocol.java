package de.persosim.simulator.protocols.ta;

import static org.globaltester.logging.BasicLogger.log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectUtils;
import de.persosim.simulator.cardobjects.DateTimeCardObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.cardobjects.TypeIdentifier;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.crypto.certificates.CertificateExtension;
import de.persosim.simulator.crypto.certificates.CvEcPublicKey;
import de.persosim.simulator.crypto.certificates.CvPublicKey;
import de.persosim.simulator.crypto.certificates.ExtensionOid;
import de.persosim.simulator.crypto.certificates.PublicKeyReference;
import de.persosim.simulator.exception.CarParameterInvalidException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.exception.CertificateUpdateException;
import de.persosim.simulator.exception.ProcessingException;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.protocols.AbstractProtocolStateMachine;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.secstatus.AuthorizationStore;
import de.persosim.simulator.secstatus.ConfinedAuthorizationMechanism;
import de.persosim.simulator.secstatus.EffectiveAuthorizationMechanism;
import de.persosim.simulator.secstatus.PaceMechanism;
import de.persosim.simulator.secstatus.CAPAMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusMechanismUpdatePropagation;
import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValuePlain;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * @author mboonk
 * 
 */
public abstract class AbstractTaProtocol extends AbstractProtocolStateMachine implements TlvConstants {

	/*--------------------------------------------------------------------------------*/

	public static final short P1P2_C1A4_SET_AT = (short) 0xC1A4;
	public static final short P1P2_81B6_SET_DST = (short) 0x81B6;
	public static final short P1P2_00BE_VERIFY_CERTIFICATE = (short) 0x00BE;
	public static final short P1P2_0000_NO_FURTHER_INFORMATION = (short) 0x0000;

	// values 0x00 - 0x3F are reserved for common COMMAND_X variables
	public static final byte COMMAND_SET_DST = (byte) 0x40;
	public static final byte COMMAND_EXTERNAL_AUTHENTICATE = (byte) 0x41;
	public static final byte COMMAND_GET_CHALLENGE = (byte) 0x42;
	public static final byte COMMAND_PSO = (byte) 0x43;

	public static final byte APDU_SET_AT = 0;
	public static final byte APDU_GET_NONCE = 1;
	public static final byte APDU_MAP_NONCE = 2;
	public static final byte APDU_PERFORM_KEY_AGREEMENT = 3;
	public static final byte APDU_MUTUAL_AUTHENTICATE = 4;

	public static final byte MASK_SFI_BYTE = (byte) 0x80;
	
	private SecureRandom secureRandom = new SecureRandom();
	protected CardVerifiableCertificate currentCertificate;
	private CardVerifiableCertificate mostRecentTemporaryCertificate;

	protected byte [] challenge;
	private List<AuthenticatedAuxiliaryData> auxiliaryData;
	private TaOid cryptographicMechanismReference;
	private byte [] compressedTerminalEphemeralPublicKey;
	private TrustPointCardObject trustPoint;
	private TerminalType terminalType;
	private byte[] firstSectorPublicKeyHash;
	private byte[] secondSectorPublicKeyHash;
	
	protected AuthorizationStore authorizationStore = null;
	
	/*--------------------------------------------------------------------------------*/

	public AbstractTaProtocol() {
		super("TA");
	}

	/*--------------------------------------------------------------------------------*/

	/**
	 * This method checks if the received APDU is a correct
	 * {@link IsoSecureMessagingCommandApdu} and was encrypted.
	 * 
	 * @return true, if it is a {@link IsoSecureMessagingCommandApdu} and the
	 *         APDU was encrypted at some point in its history
	 */
	protected boolean checkSecureMessagingApdu(){
		if (processingData.getCommandApdu() instanceof IsoSecureMessagingCommandApdu){
			if (!((IsoSecureMessagingCommandApdu) processingData
							.getCommandApdu()).wasSecureMessaging()) {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(
						Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this,
						"TA must be executed in secure messaging", resp);
				return false;
			}
		} else {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(
					Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
			this.processingData.updateResponseAPDU(this,
					"This APDU should not have reached this point in processing, check for the correct APDU type processing in the APDU factory", resp);
			return false;
		}
		return true;
	}
	
	protected void processCommandGetChallenge() {
		if (!checkSecureMessagingApdu()){
			return;
		}		
		
		challenge = new byte [8];
		secureRandom.nextBytes(challenge);	
		
		// create and propagate response APDU
		ResponseApdu resp = new ResponseApdu(new TlvValuePlain(challenge), Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this,
				"Command GetChallenge successfully processed", resp);
	}
	
	@Override
	public void reset(){
		super.reset();
		authorizationStore = null;
		currentCertificate = null;
		mostRecentTemporaryCertificate = null;
		auxiliaryData = null;
		challenge = null;
	}
	
	protected TerminalType getTerminalType() {
		// get necessary information stored in an earlier protocol (e.g. PACE or CAPA)
		TerminalType type = getTerminalType(PaceMechanism.class);
		if (type != null)
			return type;
		type = getTerminalType(CAPAMechanism.class);
		if (type != null)
			return type;
		else
			throw new ProcessingException(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED,
					"Missing previous execution of PACE or CAPA protocol.");
	}

	private TerminalType getTerminalType(Class<? extends SecMechanism> secMechanism) {
		Collection<? extends SecMechanism> currentMechanisms = null;
		if (PaceMechanism.class.getCanonicalName().equals(secMechanism.getCanonicalName())) {
			currentMechanisms = getPreviousSecMechanisms(secMechanism);

			if (currentMechanisms.size() == 1) {
				PaceMechanism ceMechanism = (PaceMechanism) currentMechanisms.iterator().next();

				// extract the currently used terminal type
				try {
					return TerminalType.getFromOid(ceMechanism.getOidForTa());
				} catch (IllegalArgumentException e) {
					throw new ProcessingException(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED,
							"Previous PACE protocol did not provide information about terminal type.");
				}
			}
		} else if (CAPAMechanism.class.getCanonicalName().equals(secMechanism.getCanonicalName())) {
			currentMechanisms = getPreviousSecMechanisms(CAPAMechanism.class);

			if (currentMechanisms.size() == 1) {
				CAPAMechanism ceMechanism = (CAPAMechanism) currentMechanisms.iterator().next();

				// extract the currently used terminal type
				try {
					return TerminalType.getFromOid(ceMechanism.getTerminalTypeOid());
				} catch (IllegalArgumentException e) {
					throw new ProcessingException(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED,
							"Previous CAPA protocol did not provide information about terminal type.");
				}
			}
		}
		if (currentMechanisms != null && currentMechanisms.size() > 1)
			throw new ProcessingException(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR,
					"Previous execution of PACE or CAPA protocol is ambiguous.");
		return null;
	}

	private Collection<? extends SecMechanism> getPreviousSecMechanisms(Class<? extends SecMechanism> secMechanism) {
		Set<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(secMechanism);
		return cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
	}
	
	protected void processCommandSetDst() {
		try {
			if (!checkSecureMessagingApdu()){
				return;
			}
			
			TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
			TlvDataObject publicKeyReference = commandData.getTlvDataObject(TlvConstants.TAG_83);
			
			if(publicKeyReference == null) {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
				this.processingData.updateResponseAPDU(this,"no public key reference found", resp);
				return;
			}
			
			byte[] nameOfPublicKeyEncoded = publicKeyReference.getValueField();
			
			terminalType = getTerminalType();
			
			// reset the currently set key
			currentCertificate = null;
			
			// get the next certificate to verify against
			if (mostRecentTemporaryCertificate != null && mostRecentTemporaryCertificate.getCertificateHolderReference() != null) {
				// the temporary imported key is to be used
				if (Arrays.equals(nameOfPublicKeyEncoded,
						mostRecentTemporaryCertificate.getCertificateHolderReference().getBytes())) {
					currentCertificate = mostRecentTemporaryCertificate;
				}
			}
			
			if (currentCertificate != null){
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
				this.processingData.updateResponseAPDU(this,
						"Command SetDST successfully processed, public key found in temporary imported certificate", resp);
				return;
			}
				
			String anchor = "";
			
			// get the stored trust points
			CardObject trustPointCandidate = CardObjectUtils.getSpecificChild(cardState.getMasterFile(), new TrustPointIdentifier(terminalType));
			
			if (trustPointCandidate instanceof TrustPointCardObject) {
				trustPoint = (TrustPointCardObject) trustPointCandidate;
				
				
				if (trustPoint.getCurrentCertificate() != null && trustPoint.getCurrentCertificate().getCertificateHolderReference() != null
						&& Arrays.equals(trustPoint.getCurrentCertificate().getCertificateHolderReference().getBytes(), nameOfPublicKeyEncoded)) {
					
					currentCertificate = trustPoint.getCurrentCertificate();
					anchor = "first";
				} else {
					if (trustPoint.getPreviousCertificate() != null && trustPoint.getPreviousCertificate().getCertificateHolderReference() != null
							&& Arrays.equals(trustPoint.getPreviousCertificate().getCertificateHolderReference().getBytes(), nameOfPublicKeyEncoded)) {
						
						currentCertificate = trustPoint.getPreviousCertificate();
						anchor = "second";
					}
				}
				
//				if (currentCertificate != null){
//					// a new root certificate was selected
//					authorizationStore = getInitialAuthorizations(currentCertificate);
//					Authorization auth = null;
//					if (authorizationStore != null){
//						auth = authorizationStore.getAuthorization(terminalType.getAsOid());
//					}
//						
//					if(auth == null) {
//						// create and propagate response APDU
//						ResponseApdu resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
//						this.processingData.updateResponseAPDU(this, "Previous protocol did not provide authorization information from chat", resp);
//						return;
//					}
//				}
			}
				
			if (currentCertificate != null){
				updateAuthorizations(currentCertificate);
				
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
				this.processingData.updateResponseAPDU(this, "Command SetDST successfully processed, public key found in " + anchor + " trust anchor", resp);
				return;
			}

			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			this.processingData.updateResponseAPDU(this,
					"The identified public key could not be found in a trust point or temporarily imported certificate", resp);
			return;
		} catch (ProcessingException e) {
			ResponseApdu resp = new ResponseApdu(e.getStatusWord());
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
		}
	}

	public void updateAuthorizations(CardVerifiableCertificate certificate) {
		if (isCvcaCertificate(certificate)) {
			authorizationStore = new AuthorizationStore(getAuthorizationsFromCertificate(certificate));
		} else {
			authorizationStore.updateAuthorization(getAuthorizationsFromCertificate(certificate));
		}
	}
	
	public HashMap<Oid, Authorization> getAuthorizationsFromCertificate(CardVerifiableCertificate certificate) {
		HashMap<Oid, Authorization> authorizations = new HashMap<>();
		
		CertificateHolderAuthorizationTemplate chat = certificate.getCertificateHolderAuthorizationTemplate();
		if (chat == null)
			throw new IllegalArgumentException("No CHAT available");
		RelativeAuthorization authFromChat = chat.getRelativeAuthorization();		
		authorizations.put(chat.getTerminalType().getAsOid(), authFromChat);
		
		return authorizations;
	}
	
	/**
	 * Checks if there are any known previous TA runs in the session.
	 * 
	 * @return true if TA generally can be executed
	 */
	protected boolean isTaAllowed(){
		Collection<Class<? extends SecMechanism>> wantedMechanisms = new HashSet<>();
		wantedMechanisms.add(TerminalAuthenticationMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, wantedMechanisms);
		if (!currentMechanisms.isEmpty()){
			return false;
		} else {
			return true;
		}
	}
	
	protected TaOid getCryptographicMechanismReference(TlvDataObjectContainer commandData) {
		TlvDataObject cryptographicMechanismReferenceData = commandData.getTlvDataObject(TlvConstants.TAG_80);
		if (cryptographicMechanismReferenceData != null){
			//add missing Tag and Length
			TlvDataObject cryptographicMechanismReferenceDataReconstructed = new PrimitiveTlvDataObject(TlvConstants.TAG_06, cryptographicMechanismReferenceData.getValueField());
			try {
				return new TaOid(cryptographicMechanismReferenceDataReconstructed.getValueField());
			} catch (IllegalArgumentException e) {
				throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "The cryptographic mechanism reference encoding is invalid");
			}
		} else {
			throw new ProcessingException(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "The public key reference data is missing");
		}
	}
	
	protected void assertPublicKeyReferenceDataMatchesCertificate(TlvDataObjectContainer commandData, CardVerifiableCertificate cvCert) {
		TlvDataObject publicKeyReferenceData = commandData.getTlvDataObject(TlvConstants.TAG_83);
		if (publicKeyReferenceData != null){
			try {
				PublicKeyReference keyReference = new PublicKeyReference(publicKeyReferenceData);

				if (!cvCert.getCertificateHolderReference().equals(keyReference)){
					throw new ProcessingException(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, "The referenced public key could not be found");
				}
			} catch (CarParameterInvalidException e) {
				throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "The public key reference data is invalid");
			}
		} else {
			throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "The public key reference data is missing");
		}
	}
	
	protected List<AuthenticatedAuxiliaryData> parseAuxiliaryData(TlvDataObjectContainer commandData) {
		List<AuthenticatedAuxiliaryData> foundAuxData = new ArrayList<>();
		
		TlvDataObject auxiliaryAuthenticatedData = commandData.getTlvDataObject(TlvConstants.TAG_67);
		if (auxiliaryAuthenticatedData != null){
			if (auxiliaryAuthenticatedData instanceof ConstructedTlvDataObject){
				ConstructedTlvDataObject constructedAuxiliaryAuthenticatedData = (ConstructedTlvDataObject) auxiliaryAuthenticatedData;
				for (TlvDataObject currentObject : constructedAuxiliaryAuthenticatedData.getTlvDataObjectContainer()){
					if(!(currentObject instanceof ConstructedTlvDataObject) || !currentObject.getTlvTag().equals(TlvConstants.TAG_73)){
						throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "Invalid encoding of the auxiliary data");
					}
					ConstructedTlvDataObject ddo = (ConstructedTlvDataObject) currentObject;
					TlvDataObject objectIdentifier = ddo.getTlvDataObject(TlvConstants.TAG_06);
					TlvDataObject discretionaryData = ddo.getTlvDataObject(TlvConstants.TAG_53);
					if (objectIdentifier == null)
						throw new IllegalArgumentException("No OID available");
					if (discretionaryData == null)
						throw new IllegalArgumentException("No Discretionary Data available");
					try {
						foundAuxData.add(new AuthenticatedAuxiliaryData(new GenericOid(objectIdentifier.getValueField()), discretionaryData.getValueField()));
					} catch (IllegalArgumentException e) {
						throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "Invalid encoding of the auxiliary data, object identifier not parseable");
					}
				}
			} else {
				throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "Invalid encoding of the auxiliary data, authentication object is not constructed TLV");
			}
		}
		return foundAuxData;
	}
	
	protected byte[] extractCompressedEphemeralPublicKeyTerminal(TlvDataObjectContainer commandData) {
		TlvDataObject ephemeralPublicKeyData = commandData.getTlvDataObject(TlvConstants.TAG_91);
		if (ephemeralPublicKeyData != null){
			return ephemeralPublicKeyData.getValueField();
		} else {
			throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "The ephemeral public key reference data is missing");
		}
	}

	protected void processCommandSetAt() {
		try {
			if (!checkSecureMessagingApdu()){
				return;
			}
			
			TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
			
			assertPublicKeyReferenceDataMatchesCertificate(commandData, currentCertificate);
			cryptographicMechanismReference = getCryptographicMechanismReference(commandData);
			
			auxiliaryData = parseAuxiliaryData(commandData);
			
			compressedTerminalEphemeralPublicKey = extractCompressedEphemeralPublicKeyTerminal(commandData);
			
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
			processingData.updateResponseAPDU(this, "Command SetAT successfully processed", resp);
		} catch (ProcessingException e) {
			ResponseApdu resp = new ResponseApdu(e.getStatusWord());
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
		}
	}
	
	/**
	 * This method checks the given data against the given RSA or ECDSA
	 * signature using the TA cryptographic mechanism reference OIDs.
	 * 
	 * @param taOid
	 *            defining the signature algorithm to be used
	 * @param publicKey
	 *            to use for verification
	 * @param dataToVerify
	 *            the raw data to be verified
	 * @param signatureData
	 *            the signature data, in case of ECDSA the ASN1 signature
	 *            structure is restored
	 * @return true, if the signature can be verified against the given data
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws NoSuchProviderException
	 */
	private boolean checkSignature(TaOid taOid, PublicKey publicKey, byte [] dataToVerify, byte [] signatureData) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException{
		log(this, "Verifying signature:");
		Signature signature = taOid.getSignature();
		if (signature != null){
			signature.initVerify(publicKey);
			signature.update(dataToVerify);

			log(this, "Data to verify:\n" + HexString.dump(dataToVerify));
			
			log(this, "Unprocessed signature data:\n" + HexString.dump(signatureData));
			
			if (publicKey instanceof ECPublicKey){
				signatureData = CryptoUtil.restoreAsn1SignatureStructure(signatureData).toByteArray();
			}
			
			log(this, "Processed signature data  :\n" + HexString.dump(signatureData));
			
			if(signature.verify(signatureData)){
				log(this, "Verification OK");
				return true;
			}
		} else {
			log(this, "No signature found for OID");
		}
		log(this, "Verification failed");
		return false;
	}

	protected void processCommandPsoVerifyCertificate() {
		if (!checkSecureMessagingApdu()){
			return;
		}
		
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
		ConstructedTlvDataObject certificateBodyData = (ConstructedTlvDataObject) commandData.getTlvDataObject(TlvConstants.TAG_7F4E);
		PrimitiveTlvDataObject certificateSignatureData = (PrimitiveTlvDataObject) commandData.getTlvDataObject(TlvConstants.TAG_5F37);
		
		try {
			ConstructedTlvDataObject certificateData = new ConstructedTlvDataObject(TlvConstants.TAG_7F21);
			certificateData.addTlvDataObject(certificateBodyData, certificateSignatureData);
			CardVerifiableCertificate certificate = new CardVerifiableCertificate(certificateData);
			CvPublicKey cvPublicKey = certificate.getPublicKey();
			if (cvPublicKey == null)
				throw new IllegalArgumentException("No CV Public Key available");
			cvPublicKey.addKeyParameters(currentCertificate.getPublicKey());
			if (certificate.getCertificationAuthorityReference().equals(currentCertificate.getCertificateHolderReference())){
				if (!isCertificateIssuerValid(certificate, currentCertificate)){
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
					this.processingData.updateResponseAPDU(this,
							"The certificate was issued by an invalid instance", resp);
					return;
				}
				if (!isCertificatePublicKeyDataObjectMinimal(certificateBodyData.getTlvDataObject(TlvConstants.TAG_7F49), cvPublicKey)){
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
					this.processingData.updateResponseAPDU(this,
							"Public key contains unspecified data objects", resp);
					return;
				}
				if (checkSignature((TaOid) currentCertificate.getBody().getPublicKey().getCvOid(), currentCertificate.getPublicKey(), certificateBodyData.toByteArray(), certificateSignatureData.getValueField())){
					//differentiate between CVCA link certificates and other types for date validation
					if (checkValidity(certificate, currentCertificate, getCurrentDate().getDate())){
						try {
							importCertificate(certificate, currentCertificate);
							handleSuccessfulVerification(currentCertificate);
						} catch (CertificateUpdateException e) {
							// create and propagate response APDU
							ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
							this.processingData.updateResponseAPDU(this,
									"Could not import the certificate", resp);
							return;
						}
					} else {
						// create and propagate response APDU
						ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
						this.processingData.updateResponseAPDU(this,
								"The certificate has an invalid date", resp);
						return;
					}
				} else {
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
					this.processingData.updateResponseAPDU(this,
							"Could not verify the certificate's signature", resp);
					return;
				}
			} else {
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
				this.processingData.updateResponseAPDU(this,
						"Could not find fitting certificate (CAR invalid)", resp);
				return;
			}
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException | CertificateNotParseableException e) {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this,
					"Could not verify the certificate", resp);
			return;
		}
		
		// create and propagate response APDU
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this,
				"Command PSO Verify Certificate successfully processed", resp);
	}
	

	/**
	 * Checks the validity of a certificate against the current date. Expired
	 * CVCA link certificates are accepted, not yet effective certificates are
	 * also accepted. Terminal and DV certificates are checked to be not yet
	 * expired according to the chips date.
	 * 
	 * @param certificate
	 *            the certificate to check
	 * @param issuingCertificate
	 *            the parent certificate in the chain to use for the check
	 * @param currentDate
	 *            the date to check against
	 * @return true, if the certificate is valid as defined in TR-03110 v2.10
	 */
	protected static boolean checkValidity(CardVerifiableCertificate certificate, CardVerifiableCertificate issuingCertificate, Date currentDate) {
		
		BasicLogger.log(AbstractTaProtocol.class, "Checking validity for: " + certificate + 
				"\n\teffective:  " + certificate.getEffectiveDate() + 
				"\n\texpiration: " + certificate.getExpirationDate() +
				"\nagainst: " + issuingCertificate + 
				"\n\teffective:  " + issuingCertificate.getEffectiveDate() + 
				"\n\texpiration: " + issuingCertificate.getExpirationDate() +
				"\nCurrent Date is: " + currentDate, LogLevel.DEBUG);
		
		//verify that issuingCertificate was valid, when certificate was issued
		if (certificate.getEffectiveDate().after(issuingCertificate.getExpirationDate())) return false;
		if (issuingCertificate.getEffectiveDate().after(certificate.getEffectiveDate())) return false; 
		
		
		//check validity on current date
		if (isCvcaCertificate(issuingCertificate)){
			if (isCvcaCertificate(certificate)){
				// the issuing certificate is allowed to be expired to allow import of a link certificate
				return true;
			} else {
				// for terminal and dv certificates the issuing cvca and the certificate itself must be valid (not yet expired)
				if (!currentDate.after(issuingCertificate.getExpirationDate()) && !currentDate.after(certificate.getExpirationDate())){
					return true;
				}
			}
		} else {
			//check only the date of the given certificate, at this point the cvca has already been verified
			if (currentDate.before(certificate.getExpirationDate()) || currentDate.equals(certificate.getExpirationDate())){
				return true;
			}
		}
		return false;
	}

	/**
	 * Check the given certificates for compliance to the definitions in
	 * TR-03110 v2.10 2.6.2
	 * 
	 * @param certificate
	 *            to check
	 * @param certificateToCheckAgainst
	 *            (normally the preceding certificate in the chain)
	 * @return true, if the conditions are fulfilled
	 * @throws CertificateNotParseableException
	 */
	protected static boolean isCertificateIssuerValid(CardVerifiableCertificate certificate,
			CardVerifiableCertificate certificateToCheckAgainst) throws CertificateNotParseableException {
		if ((isCvcaCertificate(certificate) || isDvCertificate(certificate)) && !isCvcaCertificate(certificateToCheckAgainst)){
			return false;
		}
		
		if (isTerminalCertificate(certificate) && !isDvCertificate(certificateToCheckAgainst)){
			return false;
		}
		return true;
	}

	/**
	 * Check the given certificates for compliance to the definitions D.3.
	 * This expects the publicKey having been parsed int cvPublicKey to ensure key data is valid.
	 * @param publicKey 
	 * @param cvPublicKey 
	 * 
	 * @return true, if the conditions are fulfilled
	 */
	protected static boolean isCertificatePublicKeyDataObjectMinimal(TlvDataObject publicKey, CvPublicKey cvPublicKey) throws CertificateNotParseableException {
		if (publicKey != null) {
			ConstructedTlvDataObject key = (ConstructedTlvDataObject) publicKey;
			int numberOfElements = key.getNoOfElements();
			if (cvPublicKey instanceof CvEcPublicKey && (numberOfElements == 2 || numberOfElements == 7 || numberOfElements == 8)) {
				// 2 mandatory elements, 2+5 in case of domain parameters and 2+5+1 in case of domain paramaters with cofactor
				return true;
			} else if (!(cvPublicKey instanceof CvEcPublicKey) && numberOfElements == 3) {
				// 3 mandatory elements for RSA
				return true;
			}
		}
		return false;
	}

	/**
	 * Update a date object using the given certificates as described in
	 * TR-03110 v2.10 2.6.2
	 * 
	 * @param certificate
	 *            to extract the new date from
	 * @param issuingCertificate
	 *            issuer of the certificate given in the first parameter, this
	 *            is not checked
	 * @param currentDate
	 *            the {@link DateTimeCardObject} to store the certificates date
	 *            in
	 */
	protected static void updateDate(CardVerifiableCertificate certificate, CardVerifiableCertificate issuingCertificate, DateTimeCardObject currentDate) {
		if (currentDate.getDate().before((certificate.getEffectiveDate()))){
			if (isCvcaCertificate(certificate) || isDomesticDvCertificate(certificate)
					|| isDomesticDvCertificate(issuingCertificate)){
				currentDate.update(certificate.getEffectiveDate());
			}
		}
	}
	
	/**
	 * @return the currently stored date
	 */
	private DateTimeCardObject getCurrentDate(){
		return (DateTimeCardObject) CardObjectUtils.getSpecificChild(cardState.getMasterFile(), new TypeIdentifier(DateTimeCardObject.class));
	}

	/**
	 * This method does not check the certificates validity.
	 * @param certificate to check
	 * @return true, if the certificate is a domestic DV certificate
	 */
	private static boolean isDomesticDvCertificate(CardVerifiableCertificate certificate) {
		CertificateHolderAuthorizationTemplate chat = certificate.getCertificateHolderAuthorizationTemplate();
		if (chat == null || chat.getRelativeAuthorization() == null)
			throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "CHAT is missing");
		return CertificateRole.DV_TYPE_1.equals(chat.getRelativeAuthorization().getRole());
	}

	/**
	 * @param certificate
	 * @return true, if the given certificate uses one of the DV {@link CertificateRole}s
	 */
	private static boolean isDvCertificate(CardVerifiableCertificate certificate) {
		CertificateHolderAuthorizationTemplate chat = certificate.getCertificateHolderAuthorizationTemplate();
		if (chat == null || chat.getRelativeAuthorization() == null)
			throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "CHAT is missing");
		return CertificateRole.DV_TYPE_1.equals(chat.getRelativeAuthorization().getRole())
				|| CertificateRole.DV_TYPE_2.equals(chat.getRelativeAuthorization().getRole());
	}


	/**
	 * @param certificate
	 * @return true, if the given certificate uses the {@link CertificateRole#CVCA}
	 */
	private static boolean isCvcaCertificate(CardVerifiableCertificate certificate) {
		CertificateHolderAuthorizationTemplate chat = certificate.getCertificateHolderAuthorizationTemplate();
		if (chat == null || chat.getRelativeAuthorization() == null)
			throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "CHAT is missing");
		return CertificateRole.CVCA.equals(chat.getRelativeAuthorization().getRole());
	}

	/**
	 * This method imports the given certificate without further checks.
	 * @param certificate
	 * @param issuingCertificate
	 * @throws CertificateUpdateException 
	 */
	private void importCertificate(CardVerifiableCertificate certificate, CardVerifiableCertificate issuingCertificate) throws CertificateUpdateException {
		updateDate(certificate, issuingCertificate, getCurrentDate());
		if (isCvcaCertificate(certificate)) {
			permanentImport(certificate);
		} else if (isDvCertificate(certificate)
				|| isTerminalCertificate(certificate)) {
			temporaryImport(certificate);
		}
	}
	
	/**
	 * Perform the permanent import of a certificate as described in TR-03110 v2.10 A.6.2.1.
	 * @param certificate
	 * @throws CertificateUpdateException 
	 */
	private void permanentImport(CardVerifiableCertificate certificate) throws CertificateUpdateException {
		if (trustPoint != null){
			//trustPoint.updateTrustpoint(certificate.getCertificateHolderReference(), certificate);
			trustPoint.updateTrustpoint(certificate);
		}
	}

	/**
	 * @param certificate
	 * @return true, if the given certificate uses the {@link CertificateRole#TERMINAL}
	 */
	public static boolean isTerminalCertificate(CardVerifiableCertificate certificate) {
		CertificateHolderAuthorizationTemplate chat = certificate.getCertificateHolderAuthorizationTemplate();
		if (chat == null || chat.getRelativeAuthorization() == null)
			throw new ProcessingException(Iso7816.SW_6A80_WRONG_DATA, "CHAT is missing");
		return CertificateRole.TERMINAL.equals(chat.getRelativeAuthorization().getRole());
	}

	/**
	 * Perform the temporary import of a certificate as described in TR-03110 v2.10 A.6.2.2.
	 * @param certificate
	 */
	private void temporaryImport(CardVerifiableCertificate certificate) {
		mostRecentTemporaryCertificate = certificate;
		currentCertificate = mostRecentTemporaryCertificate;
	}

	protected byte[] getIdIcc() {
		// get necessary information stored in an earlier protocol (e.g. PACE or CAPA)
		Collection<? extends SecMechanism> currentMechanisms = getPreviousSecMechanisms(PaceMechanism.class);
		if (currentMechanisms.isEmpty())
			currentMechanisms = getPreviousSecMechanisms(CAPAMechanism.class);
		if (!currentMechanisms.isEmpty()) {
			Object curMechFirst = currentMechanisms.toArray()[0];
			if (curMechFirst instanceof PaceMechanism)
				return ((PaceMechanism) curMechFirst).getCompressedEphemeralPublicKeyChip();
			else if (curMechFirst instanceof CAPAMechanism)
				return ((CAPAMechanism) curMechFirst).getCompressedEphemeralPublicKeyChip();
			else
				throw new ProcessingException(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED,
						"Unknown protocol providing data for ID_PICC calculation was run");
		} else {
			throw new ProcessingException(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED,
					"No protocol providing data for ID_PICC calculation was run");
		}
	}

	protected void processCommandExternalAuthenticate() {
		ResponseApdu resp;
		
		try {
			if (processingData.getCommandApdu() instanceof IsoSecureMessagingCommandApdu
					&& !((IsoSecureMessagingCommandApdu) processingData
							.getCommandApdu()).wasSecureMessaging()) {
				// create and propagate response APDU
				resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "TA must be executed in secure messaging", resp);
				return;
			}

			//ensure GetChallenge was called before
			if (challenge == null) {
				// create and propagate response APDU
				resp = new ResponseApdu(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this,"No challenge was generated, please call GetChallenge first", resp);
				return;
			}
			
			if (!isTaAllowed()){
				// create and propagate response APDU
				resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "execution of terminal authentication is not allowed", resp);
				return;
			}
			
			byte [] terminalSignatureData = processingData.getCommandApdu().getCommandData().toByteArray();
			
			byte [] idIcc = getIdIcc();
			
			byte [] dataToVerify = Utils.concatByteArrays(idIcc, challenge, compressedTerminalEphemeralPublicKey);
			
			if (auxiliaryData != null && (!auxiliaryData.isEmpty())){
				ConstructedTlvDataObject auxiliaryDataTlv = new ConstructedTlvDataObject(TlvConstants.TAG_67);
				for(AuthenticatedAuxiliaryData current : auxiliaryData){
					auxiliaryDataTlv.addTlvDataObject(current.getEncoded());
				}
				dataToVerify = Utils.concatByteArrays(dataToVerify, auxiliaryDataTlv.toByteArray());
			}
			
			try {
				if (checkSignature(cryptographicMechanismReference, currentCertificate.getPublicKey() , dataToVerify, terminalSignatureData)){
					handleSuccessfulTerminalAuthentication(currentCertificate);
				} else {
					// create and propagate response APDU
					resp = new ResponseApdu(Iso7816.SW_6300_AUTHENTICATION_FAILED);
					this.processingData.updateResponseAPDU(this,"The signature could not be verified", resp);
					return;
				}
			} catch (InvalidKeyException | NoSuchAlgorithmException
					| SignatureException | NoSuchProviderException e) {
				// create and propagate response APDU
				resp = new ResponseApdu(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
				this.processingData.updateResponseAPDU(this,"The signature could not be verified", resp);
				return;
			}
		} catch (ProcessingException e) {
			resp = new ResponseApdu(e.getStatusWord());
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
		} finally {
			/* 
			 * Request removal of this instance from the stack.
			 * Protocol either successfully completed or failed.
			 * In either case protocol is completed.
			 */
			processingData.addUpdatePropagation(this, "Command External Authenticate successfully processed - Protocol TA completed", new ProtocolUpdate(true));
		}
	}
	
	protected List<CertificateExtension> extractExtensions(CardVerifiableCertificate certificate) {
		return certificate.getCertificateExtensions();
	}

	/**
	 * This methods handles a successful terminal authentication and sets the
	 * protocol state accordingly.
	 * 
	 * @param verifiedTerminalCertificate
	 */
	protected void handleSuccessfulTerminalAuthentication(CardVerifiableCertificate verifiedTerminalCertificate) {
		List<CertificateExtension> certificateExtensions = extractExtensions(currentCertificate);
		
		extractTerminalSector(verifiedTerminalCertificate);
					
		TerminalAuthenticationMechanism mechanism = new TerminalAuthenticationMechanism(compressedTerminalEphemeralPublicKey, terminalType, auxiliaryData, firstSectorPublicKeyHash, secondSectorPublicKeyHash, cryptographicMechanismReference.getHashAlgorithmName(), certificateExtensions);
			processingData.addUpdatePropagation(this, "Updated security status with terminal authentication information", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, mechanism));
		
		this.updateAuthorizationStoreWithConfinedAuth();
		EffectiveAuthorizationMechanism authMechanism = new EffectiveAuthorizationMechanism(authorizationStore);
		processingData.addUpdatePropagation(this, "Updated security status with terminal authentication information", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, authMechanism));
		
		// create and propagate response APDU
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this,
				"Command External Authenticate successfully processed", resp);
	}
	
	/**
	 * Update the AuthorizationStore with the confined authorizations from earlier Protocols (e.g. PACE)
	 * 
	 */
	protected void updateAuthorizationStoreWithConfinedAuth() {
		HashSet<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(ConfinedAuthorizationMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		
		for (SecMechanism secMechanism : currentMechanisms) {
			if (!(secMechanism instanceof ConfinedAuthorizationMechanism)) continue;
			authorizationStore.updateAuthorization(((ConfinedAuthorizationMechanism)secMechanism).getAuthorizationStore());
		}
	} 

	/**
	 * This method handles the internal state changes caused by successfully
	 * verified certificates.
	 */
	protected void handleSuccessfulVerification(CardVerifiableCertificate verifiedCertificate) {
		updateAuthorizations(verifiedCertificate);
	}

	/**
	 * Extract the terminal sector information from the current certificate.
	 * This method should be called on terminal certificates.
	 * 
	 * @param certificate
	 */
	private void extractTerminalSector(CardVerifiableCertificate certificate) {
		for(CertificateExtension extension : certificate.getCertificateExtensions()){
			if (extension.getObjectIdentifier().equals(ExtensionOid.id_Sector)){
				if (extension.getDataObjects().containsTlvDataObject(TlvConstants.TAG_80)){
					firstSectorPublicKeyHash = extension.getDataObjects().getTlvDataObject(TlvConstants.TAG_80).getValueField();
				}
				if (extension.getDataObjects().containsTlvDataObject(TlvConstants.TAG_81)){
					secondSectorPublicKeyHash = extension.getDataObjects().getTlvDataObject(TlvConstants.TAG_81).getValueField();
				}
			}
		}
		
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		// TAInfo
		ConstructedTlvDataObject taInfo = new ConstructedTlvDataObject(
				new TlvTag(Asn1.SEQUENCE));

		PrimitiveTlvDataObject protocol = new PrimitiveTlvDataObject(
				new TlvTag(Asn1.OBJECT_IDENTIFIER),
				new TlvValuePlain(TaOid.id_TA.toByteArray()));

		PrimitiveTlvDataObject version = new PrimitiveTlvDataObject(new TlvTag(Asn1.INTEGER),
				new TlvValuePlain(new byte[] {getProtocolVersion()}));
		taInfo.addTlvDataObject(protocol);
		taInfo.addTlvDataObject(version);
		
		Collection<TlvDataObject> result = new HashSet<>();
		result.add(taInfo);
		return result;
	}
	
	protected byte getProtocolVersion() {
		return 2;
	}

}
