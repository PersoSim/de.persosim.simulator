package de.persosim.simulator.crypto.certificates;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import de.persosim.simulator.exception.CarParameterInvalidException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.Tr03110Utils;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.Utils;


/**
 * This class implements the body of a card verifiable certificate as described
 * in TR-03110 v2.10 Part 3 Appendix C.
 * 
 * @author mboonk, cstroh
 * 
 */
public class ReducedCertificateBody implements Body {
	protected int certificateProfileIdentifier;

	protected PublicKeyReference certificationAuthorityReference;
	
	protected CvPublicKey publicKey;
	
	protected PublicKeyReference certificateHolderReference;
	protected List<CertificateExtension> certificateExtensions;
	
	public ReducedCertificateBody(
			int certificateProfileIdentifier,
			PublicKeyReference certificationAuthorityReference,
			CvPublicKey publicKey,
			PublicKeyReference certificateHolderReference,
			List<CertificateExtension> certificateExtensions) {
		if(certificateHolderReference.equals(certificationAuthorityReference)) {
			if(!publicKey.isComplete()) {
				throw new IllegalArgumentException("Certificate seems to be a self-signed CVCA root certificate, but domain parameters are missing.");
			}
		}
		
		this.certificateProfileIdentifier = certificateProfileIdentifier;
		this.certificationAuthorityReference = certificationAuthorityReference;
		this.publicKey = publicKey;
		this.certificateHolderReference = certificateHolderReference;
		this.certificateExtensions = certificateExtensions;
	}
	
	/**
	 * Create a certificate object from the TLV-encoding using the domain
	 * parameters from the certificate.
	 * @param certificateData as described in TR-03110 V2.10 part 3, C
	 * @throws CertificateNotParseableException
	 */
	public ReducedCertificateBody(ConstructedTlvDataObject certificateData) throws CertificateNotParseableException {
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
	public ReducedCertificateBody(ConstructedTlvDataObject certificateBodyData, PublicKey currentPublicKey) throws CertificateNotParseableException {
		
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
		
		publicKey = Tr03110Utils.parseCvPublicKey(publicKeyData);
		if(currentPublicKey != null) {
			publicKey.updateKey(currentPublicKey);
		}
		
		//Certificate Holder Reference (CHR)
		try {
			certificateHolderReference = new PublicKeyReference(certificateBodyData.getTlvDataObject(TlvConstants.TAG_5F20));
		} catch (CarParameterInvalidException e) {
			throw new CertificateNotParseableException("The certificate holder reference could not be parsed");
		}
		
		//Certificate Extensions (CE)
		certificateExtensions = parseExtensions((ConstructedTlvDataObject) certificateBodyData.getTlvDataObject(TlvConstants.TAG_65));
		
		if(certificateHolderReference.equals(certificationAuthorityReference)) {
			if(!publicKey.isComplete()) {
				throw new IllegalArgumentException("Certificate seems to be a self-signed CVCA root certificate, but domain parameters are missing.");
			}
		}
	}
	
	/**
	 * Create a list of all certificate extensions
	 * see {@link #parseCertificateExtensions(ConstructedTlvDataObject)} for details
	 */
	protected List<CertificateExtension> parseExtensions(ConstructedTlvDataObject extensionsData) {
		return parseCertificateExtensions(extensionsData);
	}

	/**
	 * Create a list of all certificate extensions
	 * @param extensionsData as described in TR03110 v2.10 part 3, C
	 * @return all parsed extensions
	 */
	public static List<CertificateExtension> parseCertificateExtensions(ConstructedTlvDataObject extensionsData) {
		
		List<CertificateExtension> result = new ArrayList<>();
		if (extensionsData != null){
			for (TlvDataObject ddt : extensionsData.getTlvDataObjectContainer()){
				if (ddt instanceof ConstructedTlvDataObject){
					result.add(new GenericExtension((ConstructedTlvDataObject) ddt));
				}
			}
		}
		return result;
		
	}
	
	@Override
	public int getCertificateProfileIdentifier() {
		return certificateProfileIdentifier;
	}
	
	@Override
	public PublicKeyReference getCertificationAuthorityReference() {
		return certificationAuthorityReference;
	}
	
	@Override
	public PublicKeyReference getCertificateHolderReference() {
		return certificateHolderReference;
	}
	
	@Override
	public CvPublicKey getPublicKey() {
		return publicKey;
	}
	
	@Override
	public List<CertificateExtension> getCertificateExtensions() {
		return certificateExtensions;
	}

	@Override
	public byte[] getEncoded() {
		return getTlvEncoding(true).toByteArray();
	}

	@Override
	public String toString() {
		return "CardVerifiableCertificate [certificateAuthorityReference="
				+ certificationAuthorityReference
				+ ", certificateHolderReference=" + certificateHolderReference
				+ "]";
	}
	
	@Override
	public ConstructedTlvDataObject getTlvEncoding(boolean withParams) {
		ConstructedTlvDataObject encoding = CertificateUtils.encodeReducedCertificateBody(
				certificateProfileIdentifier,
				certificationAuthorityReference,
				publicKey.toTlvDataObject(withParams),
				certificateHolderReference,
				getExtensionRepresentation());
		
		return encoding;
	}
	
	@Override
	public ConstructedTlvDataObject getExtensionRepresentation() {
		
		if((certificateExtensions != null) && (!certificateExtensions.isEmpty())) {
			ConstructedTlvDataObject extensionRepresentation = new ConstructedTlvDataObject(TlvConstants.TAG_65);
			
			for(CertificateExtension extension : certificateExtensions) {
				extensionRepresentation.addTlvDataObject(extension.toTlv());
			}
			return extensionRepresentation;
		} else{
			return null;
		}
		
	}
	
}
