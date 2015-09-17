package de.persosim.simulator.crypto.certificates;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.persosim.simulator.exception.CarParameterInvalidException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.exception.NotParseableException;
import de.persosim.simulator.protocols.Tr03110Utils;
import de.persosim.simulator.protocols.ta.CertificateHolderAuthorizationTemplate;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.Utils;


/**
 * This class implements card verifiable certificates as described in TR-03110
 * v2.10 Part 3 Appendix C.
 * 
 * @see CardVerifiableCertificate
 * @author mboonk
 * 
 */
public class CardVerifiableCertificate {
	int certificateProfileIdentifier;
	PublicKeyReference certificationAuthorityReference;
	
	TaOid publicKeyOid;
	PublicKey publicKey;
	ConstructedTlvDataObject publicKeyData;
	
	PublicKeyReference certificateHolderReference;
	CertificateHolderAuthorizationTemplate certificateHolderAuthorizationTemplate;
	Date certificateEffectiveDate;
	Date certificateExpirationDate;
	List<CertificateExtension> certificateExtensions;
	
	PublicKeyReference certificateHolderReferenceSigner;
	byte[] signature;
	
	

	// TODO remove this field as soon as the certificate encoding can be
	// recreated from the parsed data
	private ConstructedTlvDataObject initialData;
	
	/**
	 * Create a certificate object from the TLV-encoding using the domain
	 * parameters from the certificate.
	 * @param certificateData as described in TR-03110 V2.10 part 3, C
	 * @throws CertificateNotParseableException
	 */
	public CardVerifiableCertificate(ConstructedTlvDataObject certificateData) throws CertificateNotParseableException {
		this(certificateData, null);
	}

	/**
	 * Create a certificate object from the TLV-encoding using the domain
	 * parameters from the given public key if the certificate does not contain
	 * them.
	 * 
	 * @param certificateBodyData as described in TR-03110 V2.10 part 3, C
	 * @param currentPublicKey the public key to be used
	 * @throws CertificateNotParseableException
	 */
	public CardVerifiableCertificate(ConstructedTlvDataObject certificateData, PublicKey currentPublicKey) throws CertificateNotParseableException {
		
		initialData = certificateData;
		ConstructedTlvDataObject certificateBodyData = (ConstructedTlvDataObject) certificateData.getTlvDataObject(TlvConstants.TAG_7F4E);
		
		
		
		//Certificate Profile Identifier (CPI)
		certificateProfileIdentifier = Utils.getIntFromUnsignedByteArray(certificateBodyData.getTlvDataObject(TlvConstants.TAG_5F29).getValueField());
		
		
		
		//Certification Authority Reference (CAR)
		try {
			certificationAuthorityReference = new PublicKeyReference(certificateBodyData.getTlvDataObject(TlvConstants.TAG_42));
		} catch (CarParameterInvalidException e) {
			throw new CertificateNotParseableException("The certificate authority reference could not be parsed");
		}
		
		
		
		//Public Key (PK)
		ConstructedTlvDataObject publicKeyData = (ConstructedTlvDataObject) certificateBodyData.getTlvDataObject(TlvConstants.TAG_7F49);
		publicKeyOid = new TaOid(publicKeyData.getTlvDataObject(TlvConstants.TAG_06).getValueField());
		
		if(!Tr03110Utils.isPartialKeyRepresentation(publicKeyData)) {
			throw new CertificateNotParseableException("The public key could not be parsed");
		}
		
		setPublicKey(publicKeyData, currentPublicKey);
		
		
		
		//Certificate Holder Reference (CHR)
		try {
			certificateHolderReference = new PublicKeyReference(certificateBodyData.getTlvDataObject(TlvConstants.TAG_5F20));
		} catch (CarParameterInvalidException e) {
			throw new CertificateNotParseableException("The certificate holder reference could not be parsed");
		}
		
		
		
		//Certificate Holder Authorization Template (CHAT)
		certificateHolderAuthorizationTemplate = parseChat((ConstructedTlvDataObject) certificateBodyData.getTlvDataObject(TlvConstants.TAG_7F4C));
		
		
		
		//Certificate Expiration Date
		//Certificate Effective Date
		try {
			certificateExpirationDate = Tr03110Utils.parseDate(((PrimitiveTlvDataObject) certificateBodyData.getTlvDataObject(TlvConstants.TAG_5F24)).getValueField());
			certificateEffectiveDate = Tr03110Utils.parseDate(((PrimitiveTlvDataObject) certificateBodyData.getTlvDataObject(TlvConstants.TAG_5F25)).getValueField());
		} catch (NotParseableException e) {
			throw new CertificateNotParseableException("The date could not be parsed");
		}
		
		if (certificateExpirationDate.before(certificateEffectiveDate)){
			throw new CertificateNotParseableException("The certificates expiration date is before the effective date");
		}
		
		
		
		//Certificate Extensions (CE)
		certificateExtensions = parseExtensions((ConstructedTlvDataObject) certificateBodyData.getTlvDataObject(TlvConstants.TAG_65));
	}
	
	public boolean addPublicKeyDomainParameters(PublicKey currentPublicKey) {
		return setPublicKey(publicKeyData, currentPublicKey);
	}
	
	private boolean setPublicKey(ConstructedTlvDataObject publicKeyData, PublicKey currentPublicKey) {
		if(publicKey != null) {
			return false;
		}
		
		publicKey = Tr03110Utils.parseCertificatePublicKey(publicKeyData, currentPublicKey);
		if (publicKey == null){
			this.publicKeyData = publicKeyData;
			return false;
		} else{
			this.publicKeyData = null;
			return true;
		}
	}

	/**
	 * Create a list of all certificate extensions
	 * @param extensionsData as described in TR03110 v2.10 part 3, C
	 * @return all parsed extensions
	 */
	private List<CertificateExtension> parseExtensions(
			ConstructedTlvDataObject extensionsData) {
		List<CertificateExtension> result = new ArrayList<>();
		if (extensionsData != null){
			for (TlvDataObject ddt : extensionsData.getTlvDataObjectContainer()){
				if (ddt instanceof ConstructedTlvDataObject){
					result.add(new CertificateExtension((ConstructedTlvDataObject) ddt));
				}
			}
		}
		return result;
	}
	
	/**
	 * Create a CHAT object from data as stored in a card verifiable certificate.
	 * @param chatData as described in TR-03110 V2.10 part 3, C
	 * @return the {@link CertificateHolderAuthorizationTemplate} object
	 * @throws CertificateNotParseableException
	 */
	private CertificateHolderAuthorizationTemplate parseChat(
			ConstructedTlvDataObject chatData) throws CertificateNotParseableException {

		TaOid objectIdentifier = new TaOid(chatData.getTlvDataObject(TlvConstants.TAG_06).getValueField());
		PrimitiveTlvDataObject relativeAuthorizationData = (PrimitiveTlvDataObject) chatData.getTlvDataObject(TlvConstants.TAG_53);
		CertificateRole role = CertificateRole.getFromMostSignificantBits(relativeAuthorizationData.getValueField()[0]);
		BitField authorization = BitField.buildFromBigEndian(relativeAuthorizationData.getLengthValue() * 8 - 2, relativeAuthorizationData.getValueField());
		RelativeAuthorization relativeAuthorization = new RelativeAuthorization(role, authorization);
		
		CertificateHolderAuthorizationTemplate result = new CertificateHolderAuthorizationTemplate(objectIdentifier, relativeAuthorization);
		
		//check if oid and relative authorization fit together
		TerminalType type = result.getTerminalType();
		int authBits = result.getRelativeAuthorization().getRepresentation().getNumberOfBits();
		
		if ((type.equals(TerminalType.AT) && authBits != 40) || ((type.equals(TerminalType.IS) || type.equals(TerminalType.ST)) && authBits != 8)){
			throw new CertificateNotParseableException("invalid combination of OID and terminal type");
		}
		
		return result;
	}

	public int getCertificateProfileIdentifier() {
		return certificateProfileIdentifier;
	}
	
	/**
	 * @return the reference to the public key of the certificate authority
	 */
	public PublicKeyReference getCertificateAuthorityReference() {
		return certificationAuthorityReference;
	}
	
	/**
	 * @return the reference to the public key of the certificate holder
	 */
	public PublicKeyReference getCertificateHolderReference() {
		return certificateHolderReference;
	}
	
	/**
	 * @return the public key associated with this certificate
	 */
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	/**
	 * @return the {@link CertificateHolderAuthorizationTemplate} for this
	 *         certificate
	 */
	public CertificateHolderAuthorizationTemplate getCertificateHolderAuthorizationTemplate() {
		return certificateHolderAuthorizationTemplate;
	}

	/**
	 * @return the date from which the certificate is valid
	 */
	public Date getEffectiveDate() {
		return certificateEffectiveDate;
	}
	
	/**
	 * @return the date form which the certificate is no longer valid
	 */
	public Date getExpirationDate() {
		return certificateExpirationDate;
	}
	
	/**
	 * @return the extensions this certificate has included
	 */
	public Collection<CertificateExtension> getCertificateExtensions() {
		return certificateExtensions;
	}

	/**
	 * Get the DER-encoded representation of this certificate.
	 * 
	 * @return
	 * 
	 */
	public byte[] getEncoded() {
		return initialData.toByteArray();
	}

	/**
	 * @return the public keys oid
	 */
	public TaOid getPublicKeyOid() {
		return publicKeyOid;
	}

	@Override
	public String toString() {
		return "CardVerifiableCertificate [certificateAuthorityReference="
				+ certificationAuthorityReference
				+ ", certificateHolderReference=" + certificateHolderReference
				+ "]";
	}
}
