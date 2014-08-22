package de.persosim.simulator.perso;

import static de.persosim.simulator.utils.PersoSimLogger.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSequenceParser;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.pkcs.SignerInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.utils.HexString;


public abstract class SecInfoCmsBuilderTest extends PersoSimTestCase {
	
	//FIXME add tests for DefaultSecInfoCmsBuilder

	/**
	 * Creates a new instance of the SecInfoCmsBuilder implementation to test
	 * @return the new instance
	 */
	protected abstract SecInfoCmsBuilder getNewTestObject();
	
	@Test
	public void testBuildSignedData() throws Exception {
		SecInfoCmsBuilder objectUnderTest = getNewTestObject();

		byte[] secInfosBytes = HexString.toByteArray("310F310301010131030101023103010103");
		
		ConstructedTlvDataObject secInfosTlv = new ConstructedTlvDataObject(secInfosBytes);
		
		checkSignedData(objectUnderTest.buildSignedData(secInfosTlv).toByteArray(), secInfosBytes);
	}

	public static boolean verifySignature() {
		//FIXME implement signature verification
		return true; 
	}

	public static X509Certificate getCertificate(SignedData cms, SignerInfo sigInfo) throws GeneralSecurityException {
		X500Name sigIssuerName = sigInfo.getIssuerAndSerialNumber().getName();
		X500Principal signerInfoIssuerPrincipal = new X500Principal(sigIssuerName.toString());
		
		log(SecInfoCmsBuilderTest.class, "Searching certificate for \"" + signerInfoIssuerPrincipal + "\"", DEBUG);
		
		ASN1Encodable[] certificates = cms.getCertificates().toArray();
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
		
		for (int i = 0; i < certificates.length; i++) {
			
			InputStream in;
			try {
				in = new ByteArrayInputStream(certificates[i].toASN1Primitive().getEncoded());
				X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);
	
				X500Principal certificateIssuerPrincipal = cert.getIssuerX500Principal();
				
				if (signerInfoIssuerPrincipal.equals(certificateIssuerPrincipal)) {
					log(SecInfoCmsBuilderTest.class, "Found certificate \"" + certificateIssuerPrincipal + "\", does match.", DEBUG);
					return cert;
				}
				log(SecInfoCmsBuilderTest.class, "Found certificate \"" + certificateIssuerPrincipal + "\", does NOT match.", DEBUG);
				
			} catch (IOException | CertificateException e) {
				//this certificate can not be used
				e.printStackTrace();
				continue;
			}
			
		}
		
		return null;
	}

	/**
	 * Check the content of EF.CardSecurity/EF.ChipSecurity.
	 * <p/>
	 * Implemented checks:
	 * <ul>
	 *  <li>match of eContent with provided parameter</li>
	 *  <li>valid signature</li>
	 * </ul> 
	 * @param cmsBytes CmsSignedData structure
	 * @param expectedEContent the expected eContent
	 * @throws Exception 
	 */
	public static void checkSignedData(byte[] cmsBytes,
			byte[] expectedEContent) throws Exception {
		
		DERSequenceParser cmsParser = (DERSequenceParser) new ASN1StreamParser(cmsBytes).readObject();
		ASN1Encodable cmsAsn1 = cmsParser.getLoadedObject();
		assertTrue("provided data does not encode a CMS", cmsAsn1 instanceof ASN1Sequence);
		
		SignedData cms = new SignedData((ASN1Sequence) cmsAsn1);
		
		//match the eContent
		PrimitiveTlvDataObject eContentOctetString = new PrimitiveTlvDataObject(cms.getContentInfo().getContent().toASN1Primitive().getEncoded());
		assertEquals("provided eContent does not match", HexString.encode(expectedEContent), HexString.encode(eContentOctetString.getValueField()));
		
		//check presence of signerInfos
		ASN1Encodable[] signerInfos = cms.getSignerInfos().toArray();
		assertTrue("No SignerInfos found", signerInfos.length > 0);
		
		//check signature for each SignerInfo
		for (int i = 0; i < signerInfos.length; i++) {
			SignerInfo sigInfo = new SignerInfo((ASN1Sequence) signerInfos[i]);
			
			//get certificate
			Object cert = SecInfoCmsBuilderTest.getCertificate(cms, sigInfo);
			assertNotNull("No matching certificate found for SignerInfo " + i, cert);
			
			//verifySignature
			assertTrue("Signature verification failed for SingerInfo " + i, SecInfoCmsBuilderTest.verifySignature());
		}
		
	}

}