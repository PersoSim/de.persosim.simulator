package de.persosim.simulator.protocols.ta;

import static de.persosim.simulator.utils.PersoSimLogger.log;

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

import de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DateTimeCardObject;
import de.persosim.simulator.cardobjects.DateTimeObjectIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.crypto.certificates.CertificateExtension;
import de.persosim.simulator.crypto.certificates.PublicKeyReference;
import de.persosim.simulator.exception.CarParameterInvalidException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.exception.CertificateUpdateException;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.protocols.AbstractProtocolStateMachine;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.Tr03110Utils;
import de.persosim.simulator.secstatus.AuthorizationMechanism;
import de.persosim.simulator.secstatus.AuthorizationStore;
import de.persosim.simulator.secstatus.PaceMechanism;
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
 * FIXME MBK add javadoc
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
	private CardVerifiableCertificate currentCertificate;
	private CardVerifiableCertificate mostRecentTemporaryCertificate;

	private byte [] challenge;
	private List<AuthenticatedAuxiliaryData> auxiliaryData;
	private TaOid crypographicMechanismReference;
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
	 * @return true, iff it is a {@link IsoSecureMessagingCommandApdu} and the
	 *         APDU was encrypted at some point in its history
	 */
	private boolean checkSecureMessagingApdu(){
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
	
	void processCommandGetChallenge() {
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
	
	void processCommandSetDst() {
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
		
		//get necessary information stored in an earlier protocol (e.g. PACE)
		HashSet<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(PaceMechanism.class);
		previousMechanisms.add(AuthorizationMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		
		PaceMechanism paceMechanism = null;
		AuthorizationMechanism authMechanism = null;
		
		if (currentMechanisms.size() >= 2){
			for(SecMechanism secMechanism:currentMechanisms) {
				if(secMechanism instanceof PaceMechanism) {
					paceMechanism = (PaceMechanism) secMechanism;
				} else{
					if(secMechanism instanceof AuthorizationMechanism) {
						authMechanism = (AuthorizationMechanism) secMechanism;
					}
				}
			}
			
			if(paceMechanism == null) {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "Did not detect execution of previous Pace protocol", resp);
				return;
			}
			
			if(authMechanism == null) {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "Did not find any authorization information", resp);
				return;
			}
			
			// extract the currently used terminal type
			TaOid terminalTypeOid = paceMechanism.getTerminalType();
			
			if(terminalTypeOid == null) {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "Previous Pace protocol did not provide information about terminal type", resp);
				return;
			}
			
			terminalType = Tr03110Utils.getTerminalTypeFromTaOid(terminalTypeOid);
			
			if (authorizationStore == null) {
				authorizationStore = authMechanism.getAuthorizationStore();
			}
			
			Authorization auth = authorizationStore.getAuthorization(terminalTypeOid);
			
			if(auth == null) {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "Previous Pace protocol did not provide authorization information from chat", resp);
				return;
			}

			// reset the currently set key
			currentCertificate = null;

			// get the next certificate to verify against
			if (mostRecentTemporaryCertificate != null && mostRecentTemporaryCertificate.getCertificateHolderReference() != null) {
				// the temporary imported key is to be used
				if (Arrays.equals(nameOfPublicKeyEncoded,
						mostRecentTemporaryCertificate.getCertificateHolderReference().getBytes())) {
					currentCertificate = mostRecentTemporaryCertificate;
					
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
					this.processingData.updateResponseAPDU(this,
							"Command SetDST successfully processed, public key found in temporary imported certificate", resp);
					return;
				}
			}
			
			// get the stored trust points
			CardObject trustPointCandidate = cardState.getObject(new TrustPointIdentifier(terminalType), Scope.FROM_MF);
			
			if (trustPointCandidate instanceof TrustPointCardObject) {
				trustPoint = (TrustPointCardObject) trustPointCandidate;
				String anchor = "no";
				
				if (trustPoint.getCurrentCertificate().getCertificateHolderReference() != null
						&& Arrays.equals(trustPoint.getCurrentCertificate().getCertificateHolderReference().getBytes(), nameOfPublicKeyEncoded)) {
					
					currentCertificate = trustPoint.getCurrentCertificate();
					anchor = "first";
				} else{
					if (trustPoint.getPreviousCertificate().getCertificateHolderReference() != null
							&& Arrays.equals(trustPoint.getPreviousCertificate().getCertificateHolderReference().getBytes(), nameOfPublicKeyEncoded)) {
						
						currentCertificate = trustPoint.getPreviousCertificate();
						anchor = "second";
					}
				}
				
				if(currentCertificate != null) {
					updateAuthorizations(currentCertificate);
					
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
					this.processingData.updateResponseAPDU(this, "Command SetDST successfully processed, public key found in " + anchor + " trust anchor", resp);
					return;
				}
			}

			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			this.processingData.updateResponseAPDU(this,
					"The identified public key could not be found in a trust point or temporarily imported certificate", resp);
			return;
		} else {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this,
					"No protocol prepared the execution of terminal authentication, e.g. no PACE/BAC was run before", resp);
			return;
		}
	}
	
	public void updateAuthorizations(CardVerifiableCertificate certificate) {
		authorizationStore.updateAuthorization(getAuthorizationsFromCertificate(certificate));
	}
	
	public HashMap<Oid, Authorization> getAuthorizationsFromCertificate(CardVerifiableCertificate certificate) {
		HashMap<Oid, Authorization> authorizations = new HashMap<>();
		
		CertificateHolderAuthorizationTemplate chat = certificate.getCertificateHolderAuthorizationTemplate();
		RelativeAuthorization authFromChat = chat.getRelativeAuthorization();
		
		authorizations.put(chat.getObjectIdentifier(), authFromChat);
		
		return authorizations;
	}

	void processCommandSetAt() {
		if (!checkSecureMessagingApdu()){
			return;
		}
		
		Collection<Class<? extends SecMechanism>> wantedMechanisms = new HashSet<Class<? extends SecMechanism>>();
		wantedMechanisms.add(TerminalAuthenticationMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, wantedMechanisms);
		if (currentMechanisms.size() > 0){
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this,
					"TA must not be executed more than once in the same session", resp);
			return;
		}
		
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
		TlvDataObject cryptographicMechanismReferenceData = commandData.getTlvDataObject(TlvConstants.TAG_80);
		TlvDataObject publicKeyReferenceData = commandData.getTlvDataObject(TlvConstants.TAG_83);
		TlvDataObject auxiliaryAuthenticatedData = commandData.getTlvDataObject(TlvConstants.TAG_67);
		TlvDataObject ephemeralPublicKeyData = commandData.getTlvDataObject(TlvConstants.TAG_91);
		
		if (publicKeyReferenceData != null){
			try {
				PublicKeyReference keyReference = new PublicKeyReference(publicKeyReferenceData);

				if (!currentCertificate.getCertificateHolderReference().equals(keyReference)){
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
					this.processingData.updateResponseAPDU(this,
							"The referenced public key could not be found", resp);
					return;
				}
			} catch (CarParameterInvalidException e) {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
				this.processingData.updateResponseAPDU(this,
						"The public key reference data is invalid", resp);
				return;
			}
		} else {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
			this.processingData.updateResponseAPDU(this,
					"The public key reference data is missing", resp);
			return;
		}
		
		if (cryptographicMechanismReferenceData != null){
			//add missing Tag and Length
			TlvDataObject cryptographicMechanismReferenceDataReconstructed = new PrimitiveTlvDataObject(TlvConstants.TAG_06, cryptographicMechanismReferenceData.getValueField());
			try {
				crypographicMechanismReference = new TaOid(cryptographicMechanismReferenceDataReconstructed.getValueField());
			} catch (IllegalArgumentException e) {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
				this.processingData.updateResponseAPDU(this,
						"The cryptographic mechanism reference encoding is invalid", resp);
				return;
			}
		} else {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			this.processingData.updateResponseAPDU(this,
					"The public key reference data is missing", resp);
			return;
		}
		
		if (auxiliaryAuthenticatedData != null){
			if (auxiliaryAuthenticatedData instanceof ConstructedTlvDataObject){
				auxiliaryData = new ArrayList<AuthenticatedAuxiliaryData>();
				ConstructedTlvDataObject constructedAuxiliaryAuthenticatedData = (ConstructedTlvDataObject) auxiliaryAuthenticatedData;
				for (TlvDataObject currentObject : constructedAuxiliaryAuthenticatedData.getTlvDataObjectContainer()){
					if(!(currentObject instanceof ConstructedTlvDataObject) || !currentObject.getTlvTag().equals(TlvConstants.TAG_73)){
						// create and propagate response APDU
						ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
						this.processingData.updateResponseAPDU(this,
								"Invalid encoding of the auxiliary data", resp);
						return;	
					}
					ConstructedTlvDataObject ddo = (ConstructedTlvDataObject) currentObject;
					TlvDataObject objectIdentifier = ddo.getTlvDataObject(TlvConstants.TAG_06);
					TlvDataObject discretionaryData = ddo.getTlvDataObject(TlvConstants.TAG_53);
					try {
						auxiliaryData.add(new AuthenticatedAuxiliaryData(new TaOid(objectIdentifier.getValueField()), discretionaryData.getValueField()));
					} catch (IllegalArgumentException e) {
						// create and propagate response APDU
						ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
						this.processingData.updateResponseAPDU(this,
								"Invalid encoding of the auxiliary data, object identifier not parseable", resp);
						return;	
					}
				}
			} else {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
				this.processingData.updateResponseAPDU(this,
						"Invalid encoding of the auxiliary data, authentication object is not constructed TLV", resp);
				return;	
			}
		}
		
		if (ephemeralPublicKeyData != null){
			compressedTerminalEphemeralPublicKey = ephemeralPublicKeyData.getValueField();
		} else {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
			this.processingData.updateResponseAPDU(this,
					"The ephemeral public key reference data is missing", resp);
			return;
		}
		
		// create and propagate response APDU
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this,
				"Command SetAT successfully processed", resp);
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
	 * @return true, iff the signature can be verified against the given data
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
			
			if (publicKey instanceof ECPublicKey){
				signatureData = CryptoUtil.restoreAsn1SignatureStructure(signatureData).toByteArray();
			}
			log(this, "Signature data:\n" + HexString.dump(signatureData));
			
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

	void processCommandPsoVerifyCertificate() {
		if (!checkSecureMessagingApdu()){
			return;
		}
		
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
		ConstructedTlvDataObject certificateBodyData = (ConstructedTlvDataObject) commandData.getTlvDataObject(TlvConstants.TAG_7F4E);
		PrimitiveTlvDataObject certificateSignatureData = (PrimitiveTlvDataObject) commandData.getTlvDataObject(TlvConstants.TAG_5F37);
		
		try {
			ConstructedTlvDataObject certificateData = new ConstructedTlvDataObject(TlvConstants.TAG_7F21);
			certificateData.addTlvDataObject(certificateBodyData, certificateSignatureData);
			CardVerifiableCertificate certificate = new CardVerifiableCertificate(certificateData, currentCertificate.getPublicKey());
			if (certificate.getCertificationAuthorityReference().equals(currentCertificate.getCertificateHolderReference())){
				if (!isCertificateIssuerValid(certificate, currentCertificate)){
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
					this.processingData.updateResponseAPDU(this,
							"The certificate was issued by an not valid instance", resp);
					return;
				}
				if (checkSignature((TaOid) currentCertificate.getBody().getPublicKey().getCvOid(), currentCertificate.getPublicKey(), certificateBodyData.toByteArray(), certificateSignatureData.getValueField())){
					//differentiate between CVCA link certificates and other types for date validation
					if (checkValidity(certificate, currentCertificate, getCurrentDate().getDate())){
						try {
							importCertificate(certificate, currentCertificate);
							updateAuthorizations(currentCertificate);
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
								"The certificate has a non valid date", resp);
						return;
					}
				} else {
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
					this.processingData.updateResponseAPDU(this,
							"Could not verify the certificates signature", resp);
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
	 *            the date to check agains
	 * @return true, iff the certificate is valid as defined in TR-03110 v2.10
	 */
	protected static boolean checkValidity(CardVerifiableCertificate certificate, CardVerifiableCertificate issuingCertificate, Date currentDate) {
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
	 * @return true, iff the conditions are fulfilled
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
		return (DateTimeCardObject) cardState.getObject(new DateTimeObjectIdentifier(), Scope.FROM_MF);
	}

	/**
	 * This method does not check the certificates validity.
	 * @param certificate to check
	 * @return true, iff the certificate is a domestic DV certificate
	 */
	private static boolean isDomesticDvCertificate(CardVerifiableCertificate certificate) {
		return certificate.getCertificateHolderAuthorizationTemplate().getRelativeAuthorization().getRole().equals(CertificateRole.DV_TYPE_1);
	}

	/**
	 * @param certificate
	 * @return true, iff the given certificate uses one of the DV {@link CertificateRole}s
	 */
	private static boolean isDvCertificate(CardVerifiableCertificate certificate) {
		return certificate.getCertificateHolderAuthorizationTemplate()
				.getRelativeAuthorization().getRole()
				.equals(CertificateRole.DV_TYPE_1)
				|| certificate.getCertificateHolderAuthorizationTemplate()
						.getRelativeAuthorization().getRole()
						.equals(CertificateRole.DV_TYPE_2);
	}


	/**
	 * @param certificate
	 * @return true, iff the given certificate uses the {@link CertificateRole#CVCA}
	 */
	private static boolean isCvcaCertificate(CardVerifiableCertificate certificate) {
		return certificate.getCertificateHolderAuthorizationTemplate()
				.getRelativeAuthorization().getRole()
				.equals(CertificateRole.CVCA);
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
	 * @return true, iff the given certificate uses the {@link CertificateRole#TERMINAL}
	 */
	private static boolean isTerminalCertificate(CardVerifiableCertificate certificate) {
		return certificate.getCertificateHolderAuthorizationTemplate()
				.getRelativeAuthorization().getRole()
				.equals(CertificateRole.TERMINAL);
	}

	/**
	 * Perform the temporary import of a certificate as described in TR-03110 v2.10 A.6.2.2.
	 * @param certificate
	 */
	private void temporaryImport(CardVerifiableCertificate certificate) {
		mostRecentTemporaryCertificate = certificate;
		currentCertificate = mostRecentTemporaryCertificate;
	}

	void processCommandExternalAuthenticate() {
		if (processingData.getCommandApdu() instanceof IsoSecureMessagingCommandApdu
				&& !((IsoSecureMessagingCommandApdu) processingData
						.getCommandApdu()).wasSecureMessaging()) {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(
					Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this,
					"TA must be executed in secure messaging", resp);
			return;
		}

		//ensure GetChallenge was called before
		if (challenge == null) {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this,"No challenge was generated, please call GetChellenge first", resp);
			return;
		}
		
		
		byte [] terminalSignatureData = processingData.getCommandApdu().getCommandData().toByteArray();
		
		//get necessary information stored in an earlier protocol (e.g. PACE)
		HashSet<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(PaceMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		if (currentMechanisms.size() > 0){
			PaceMechanism paceMechanism = (PaceMechanism) currentMechanisms.toArray()[0];
			byte [] idPicc = paceMechanism.getCompressedEphemeralPublicKey();
			
			byte [] dataToVerify = Utils.concatByteArrays(idPicc, challenge, compressedTerminalEphemeralPublicKey);
			
			if (auxiliaryData != null && auxiliaryData.size() > 0){
				ConstructedTlvDataObject auxiliaryDataTlv = new ConstructedTlvDataObject(TlvConstants.TAG_67);
				for(AuthenticatedAuxiliaryData current : auxiliaryData){
					auxiliaryDataTlv.addTlvDataObject(current.getEncoded());
				}
				dataToVerify = Utils.concatByteArrays(dataToVerify, auxiliaryDataTlv.toByteArray());
			}
			
			try {
				if (checkSignature(crypographicMechanismReference, currentCertificate.getPublicKey() , dataToVerify, terminalSignatureData)){
					extractTerminalSector(currentCertificate);
					
					TerminalAuthenticationMechanism mechanism = new TerminalAuthenticationMechanism(compressedTerminalEphemeralPublicKey, terminalType, auxiliaryData, firstSectorPublicKeyHash, secondSectorPublicKeyHash, crypographicMechanismReference.getHashAlgorithmName());
					processingData.addUpdatePropagation(this, "Updated security status with terminal authentication information", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, mechanism));
					
					AuthorizationMechanism authMechanism = new AuthorizationMechanism(authorizationStore);
					processingData.addUpdatePropagation(this, "Updated security status with terminal authentication information", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, authMechanism));
					
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
					this.processingData.updateResponseAPDU(this,
							"Command External Authenticate successfully processed", resp);
				} else {
					// create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(Iso7816.SW_6300_AUTHENTICATION_FAILED);
					this.processingData.updateResponseAPDU(this,"The signature could not be verified", resp);
					return;
				}
			} catch (InvalidKeyException | NoSuchAlgorithmException
					| SignatureException | NoSuchProviderException e) {
				// create and propagate response APDU
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
				this.processingData.updateResponseAPDU(this,"The signature could not be verified", resp);
				return;
			}

		} else {
			// create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this,"No protocol providing data for ID_PICC calculation was run", resp);
		}
	}
	
	/**
	 * Extract the terminal sector information from the current certificate.
	 * This method should be called on terminal certificates.
	 * 
	 * @param certificate
	 */
	private void extractTerminalSector(CardVerifiableCertificate certificate) {
		for(CertificateExtension extension : certificate.getCertificateExtensions()){
			if (extension.getObjectIdentifier().equals(TaOid.id_Sector)){
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
	public Collection<? extends TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		// TAInfo
		ConstructedTlvDataObject taInfo = new ConstructedTlvDataObject(
				new TlvTag(Asn1.SEQUENCE));

		PrimitiveTlvDataObject protocol = new PrimitiveTlvDataObject(
				new TlvTag(Asn1.OBJECT_IDENTIFIER),
				new TlvValuePlain(HexString
						.toByteArray("04 00 7F 00 07 02 02 02")));

		PrimitiveTlvDataObject version = new PrimitiveTlvDataObject(new TlvTag(Asn1.INTEGER),
				new TlvValuePlain(new byte[] { 2 }));
		taInfo.addTlvDataObject(protocol);
		taInfo.addTlvDataObject(version);
		
		return Arrays.asList(taInfo);
	}

}
