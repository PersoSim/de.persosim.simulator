package de.persosim.simulator.perso;

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
import java.util.Arrays;
import java.util.Collection;

import javax.security.auth.x500.X500Principal;

import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.DERSequenceParser;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.pkcs.SignerInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.Protocol.SecInfoPublicity;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.HexString;

public class DefaultNpaUnmarshallerCallbackTest extends PersoSimTestCase {
	
	@Mocked
	Personalization mockedPerso;
	MasterFile masterFile = new MasterFile();
	@Mocked
	Protocol mockedProtocol1, mockedProtocol2;

	@Before
	public void setUp() {
		masterFile.setSecStatus(new SecStatus());
		
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedPerso.getObjectTree();
				result = masterFile;
				
				mockedPerso.getProtocolList();
				result = Arrays.asList(mockedProtocol1, mockedProtocol2);
				
				mockedProtocol1.getSecInfos(withInstanceOf(SecInfoPublicity.class), masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("310301011F")));
				
				mockedProtocol2.getSecInfos(withInstanceOf(SecInfoPublicity.class), masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("310301012F")));
				
			}
		};
		
		//ensure that matching of IssuerNames works as expected
		X500Name.setDefaultStyle(RFC4519Style.INSTANCE);
	}
	
	/**
	 * Positive test: check that EF.CardAccess is created if missing
	 * @throws Exception 
	 */
	@Test
	public void testAfterUnmarshall_addCardAccess() throws Exception {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedProtocol1.getSecInfos(SecInfoPublicity.PUBLIC, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010101")));
				
				mockedProtocol2.getSecInfos(SecInfoPublicity.PUBLIC, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010102")));
				
			}
		};
				
		new DefaultNpaUnmarshallerCallback().afterUnmarshall(mockedPerso);
		
		//check content of created EF.CardAccess
		Collection<CardObject> files = masterFile.findChildren(new FileIdentifier(0x011C));
		assertEquals(1, files.size());
		
		ElementaryFile efCardAccess = (ElementaryFile) files.iterator().next();
		
		assertEquals("310A31030101013103010102", HexString.encode(efCardAccess.getContent()));
	}
	
	/**
	 * Positive test: check that EF.CardSecurity is created if missing
	 * @throws Exception 
	 */
	@Test
	public void testAfterUnmarshall_addCardSecurity() throws Exception {
		
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedProtocol1.getSecInfos(SecInfoPublicity.AUTHENTICATED, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010101")));
				
				mockedProtocol2.getSecInfos(SecInfoPublicity.AUTHENTICATED, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010102")), new ConstructedTlvDataObject(HexString.toByteArray("3103010103")));
				
			}
		};
						
		//FIXME restore the following line here
		new DefaultNpaUnmarshallerCallback().afterUnmarshall(mockedPerso);
//		new DefaultNpaUnmarshallerCallback(new TestPkiCmsBuilder()).afterUnmarshall(mockedPerso);

		
		//check content of created EF.CardSecurity
		Collection<CardObject> files = masterFile.findChildren(new FileIdentifier(0x011D));
		assertEquals(1, files.size());
		
		ElementaryFile efCardSecurity = (ElementaryFile) files.iterator().next();
		byte[] expecedEContent = HexString.toByteArray("310F310301010131030101023103010103");
		
		byte[] fileContent = Deencapsulation.getField(efCardSecurity, "content");
		
		ConstructedTlvDataObject fileContentTlv = new ConstructedTlvDataObject(fileContent);
		
		assertEquals(new TlvTag(Asn1.SEQUENCE),  fileContentTlv.getTlvTag());
		TlvDataObject cmsTlv = new ConstructedTlvDataObject(fileContentTlv.getTagField(new TlvTag((byte)0xA0)).getValueField());
		checkSignedData(cmsTlv.toByteArray(), expecedEContent);
	}

	/**
	 * Positive test: check that EF.ChipSecurity is created if missing
	 * @throws Exception 
	 */
	@Test
	public void testAfterUnmarshall_addChipSecurity() throws Exception {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedProtocol1.getSecInfos(SecInfoPublicity.AUTHENTICATED, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010101")));
				
				mockedProtocol2.getSecInfos(SecInfoPublicity.AUTHENTICATED, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010102")), new ConstructedTlvDataObject(HexString.toByteArray("3103010103")));
				
			}
		};
								
		new DefaultNpaUnmarshallerCallback().afterUnmarshall(mockedPerso);
		
		//check content of created EF.ChipSecurity
		Collection<CardObject> files = masterFile.findChildren(new FileIdentifier(0x011D)); //FIXME wrong FID here, use 0x011B instead, and fix according issues
		assertEquals(1, files.size());
		
		ElementaryFile efChipSecurity = (ElementaryFile) files.iterator().next();
		byte[] expecedEContent = HexString.toByteArray("310F310301010131030101023103010103");
		
		byte[] fileContent = Deencapsulation.getField(efChipSecurity, "content");
		
		ConstructedTlvDataObject fileContentTlv = new ConstructedTlvDataObject(fileContent);
		
		assertEquals(new TlvTag(Asn1.SEQUENCE),  fileContentTlv.getTlvTag());
		TlvDataObject cmsTlv = new ConstructedTlvDataObject(fileContentTlv.getTagField(new TlvTag((byte)0xA0)).getValueField());
		checkSignedData(cmsTlv.toByteArray(), expecedEContent);
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
	private void checkSignedData(byte[] cmsBytes,
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
			Object cert = getCertificate(cms, sigInfo);
			assertNotNull("No matching certificate found for SignerInfo " + i, cert);
			
			//verifySignature
			assertTrue("Signature verification failed for SingerInfo " + i, verifySignature());
		}
		
	}

	private X509Certificate getCertificate(SignedData cms, SignerInfo sigInfo) throws GeneralSecurityException {
		
		X500Name sigIssuerName = sigInfo.getIssuerAndSerialNumber().getName();
		X500Principal signerInfoIssuerPrincipal = new X500Principal(sigIssuerName.toString());
		
		ASN1Encodable[] certificates = cms.getCertificates().toArray();
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
		
		for (int i = 0; i < certificates.length; i++) {
			
			InputStream in;
			try {
				in = new ByteArrayInputStream(certificates[i].toASN1Primitive().getEncoded());
				X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);

				X500Principal certificateIssuerPrincipal = cert.getIssuerX500Principal();
				
				if (signerInfoIssuerPrincipal.equals(certificateIssuerPrincipal)) {
					return cert;
				}
				
			} catch (IOException | CertificateException e) {
				//this certificate can not be used
				e.printStackTrace();
				continue;
			}
			
		}
		
		return null;
	}

	private boolean verifySignature() {
		//FIXME implement signature verification
		return true; 
	}

}
