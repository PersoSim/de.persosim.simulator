package de.persosim.simulator.perso;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.log;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.DERSequenceParser;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.utils.HexString;


public abstract class SecInfoCmsBuilderTest extends PersoSimTestCase {
	
	//TODO add tests for DefaultSecInfoCmsBuilder

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

	/**
	 * Verify the signature of the hashed input against the provided certificate
	 * @param sigInput input of the signature to be checked
	 * @param sigBytes the signature
	 * @param cert certificate that provides the public key for signature verification
	 * @return true iff sigBytes represent a valid signature of the input sigInput generated with the private key associated to cert
	 * @throws GeneralSecurityException
	 */
	public static boolean verifySignature(byte[] sigInput, byte[] sigBytes, String digest, X509Certificate cert) throws GeneralSecurityException {
		Signature sig = Signature.getInstance(cert.getSigAlgName(), BouncyCastleProvider.PROVIDER_NAME);
		
		PublicKey pubKey = cert.getPublicKey();
		sig.initVerify(pubKey);
		
		MessageDigest mda = MessageDigest.getInstance(digest, BouncyCastleProvider.PROVIDER_NAME);
		sig.update(mda.digest(sigInput));
		
		sig.verify(sigBytes);
		
		return true; //TODO implement signature verification		
	}

	public static X509Certificate getCertificate(SignedData cms, ASN1Sequence sigInfo) throws GeneralSecurityException, IOException {
		ASN1Sequence sid = (ASN1Sequence) sigInfo.getObjectAt(1);
		X500Principal signerInfoIssuerPrincipal = new X500Principal(sid.getObjectAt(0).toASN1Primitive().getEncoded());
		
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
			ASN1Sequence sigInfo = (ASN1Sequence) signerInfos[i];
			
			//get certificate
			X509Certificate cert = SecInfoCmsBuilderTest.getCertificate(cms, sigInfo);
			assertNotNull("No matching certificate found for SignerInfo " + i, cert);

			
			
			//verifySignature
			ASN1Primitive sigAttribs = sigInfo.getObjectAt(3).toASN1Primitive();
			byte[] sigInput, sigBytes;
			if (sigAttribs instanceof DERTaggedObject) {
				//find message digest in signed attributes
				byte[] actualDigest = null;
				ASN1Sequence sigAttribsSequence = (ASN1Sequence) ((DERTaggedObject) sigAttribs).getObject();
				@SuppressWarnings("rawtypes") // legacy code
				Enumeration e = sigAttribsSequence.getObjects();
				while (e.hasMoreElements()) {
					Object curElem = e.nextElement();
					if (!(curElem instanceof ASN1Sequence)) continue;
					
					ASN1Sequence curAttrib = (ASN1Sequence) curElem;
					if (curAttrib.size() != 2) continue;
					
					ASN1Encodable attrType = curAttrib.getObjectAt(0);
					if (!(attrType instanceof ASN1ObjectIdentifier)) continue;
					if (!((ASN1ObjectIdentifier) attrType).getId().equals("1.2.840.113549.1.9.4")) continue;
					
					ASN1Encodable attrValue = curAttrib.getObjectAt(1);
					assertTrue("attrValues of signed attribute 'Message digest' must be a SET ", attrValue instanceof ASN1Set);
					assertEquals("signed attribute 'Message digest' - nr of elements in SET", 1, ((ASN1Set)attrValue).size());
					ASN1Encodable messageDigest = ((ASN1Set) attrValue).getObjectAt(0);
					assertTrue("message digest must be encoded as OctestString within SignedAttributes", messageDigest instanceof ASN1OctetString);
					
					actualDigest = ((ASN1OctetString) messageDigest).getOctets();
				}
				assertNotNull("message digest missing in signed attributes", actualDigest);
				
				//check message digest of eContent
				MessageDigest md = MessageDigest.getInstance("SHA224"); //XXX extract the used digest alg from cms
				byte[] expectedDigest = md.digest(expectedEContent);
				assertArrayEquals("message digest found in signed attributes does not match eContent", expectedDigest, actualDigest);
				
				
				sigInput = sigAttribs.getEncoded();
				sigInput[0] = Asn1.SEQUENCE;
				sigBytes = sigInfo.getObjectAt(5).toASN1Primitive().getEncoded();
			} else {
				sigInput = eContentOctetString.getValueField();
				sigBytes = sigInfo.getObjectAt(4).toASN1Primitive().getEncoded();
			}
			
			sigBytes = Arrays.copyOfRange(sigBytes, 2, sigBytes.length);
			assertTrue("Signature verification failed for SingerInfo " + i, SecInfoCmsBuilderTest.verifySignature(sigInput, sigBytes, "SHA224", cert));
		}
		
	}

}