package de.persosim.simulator.crypto.certificates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.utils.HexString;

public class CardVerifiableCertificateTest extends PersoSimTestCase {
	
	byte[] cvCertDETESTeID00004FullData, cvCertDETESTeID00004BodyData, signature;
	ConstructedTlvDataObject cvCertDETESTeID00004FullTlv, cvCertDETESTeID00004BodyTlv;
	
	@Before
	public void setUp() {
		cvCertDETESTeID00004BodyData = HexString.toByteArray("7F4E82016E5F290100420E44455445535465494430303030347F4982011D060A04007F000702020202038120A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E537782207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9832026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B68441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F0469978520A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A786410474FF63AB838C73C303AC003DFEE95CF8BF55F91E8FEBCB7395D942036E47CF1845EC786EC95BB453AAC288AD023B6067913CF9B63F908F49304E5CFC8B3050DD8701015F200E44455445535465494430303030347F4C12060904007F0007030102025305FC0F13FFFF5F25060102000501015F2406010500050101");
		cvCertDETESTeID00004FullData = HexString.toByteArray("7F218201B67F4E82016E5F290100420E44455445535465494430303030347F4982011D060A04007F000702020202038120A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E537782207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9832026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B68441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F0469978520A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A786410474FF63AB838C73C303AC003DFEE95CF8BF55F91E8FEBCB7395D942036E47CF1845EC786EC95BB453AAC288AD023B6067913CF9B63F908F49304E5CFC8B3050DD8701015F200E44455445535465494430303030347F4C12060904007F0007030102025305FC0F13FFFF5F25060102000501015F24060105000501015F37408CAC3E842EB053EE10E9D57FB373FF4E9C36D1EDF966D6535978D498309B00D59C51D83965F4B1C75557FA6B6CA03D360A782B9BC172CE391623D6BB48B9B1AA");
		cvCertDETESTeID00004BodyTlv = new ConstructedTlvDataObject(cvCertDETESTeID00004BodyData);
		cvCertDETESTeID00004FullTlv = new ConstructedTlvDataObject(cvCertDETESTeID00004FullData);
		signature = HexString.toByteArray("8CAC3E842EB053EE10E9D57FB373FF4E9C36D1EDF966D6535978D498309B00D59C51D83965F4B1C75557FA6B6CA03D360A782B9BC172CE391623D6BB48B9B1AA");
	}
	
	/**
	 * Positive test case: check constructor for parsing certificate from full TLV encoding.
	 * @throws CertificateNotParseableException 
	 */
	@Test
	public void testConstructor_ConstructedTlvDataObject() throws CertificateNotParseableException {
		CardVerifiableCertificate cvCertDETESTeID00004 = new CardVerifiableCertificate(cvCertDETESTeID00004FullTlv);
		assertEquals(cvCertDETESTeID00004FullTlv, cvCertDETESTeID00004.getEncoded());
	}

	/**
	 * Positive test case: check constructor for instantiating from body and signature.
	 * @throws CertificateNotParseableException 
	 */
	@Test
	public void testConstructor_BodyByteArray() throws CertificateNotParseableException {
		CertificateBody body = new CertificateBody(cvCertDETESTeID00004BodyTlv);
		CardVerifiableCertificate cvCertDETESTeID00004 = new CardVerifiableCertificate(body, signature);
		assertEquals(cvCertDETESTeID00004FullTlv, cvCertDETESTeID00004.getEncoded());
	}
	
	/**
	 * Positive test case: check constructor for instantiating from body and signature.
	 * @throws CertificateNotParseableException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	@Test
	public void testConstructor_ConstructedTlvDataObjectPublicKey() throws CertificateNotParseableException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		CertificateBody body = new CertificateBody(cvCertDETESTeID00004BodyTlv);
		CardVerifiableCertificate cvCertDETESTeID00004 = new CardVerifiableCertificate(body, signature);
		
		ConstructedTlvDataObject bodyTlv = body.encodeBody(false);
		System.out.println("body tlv: " + bodyTlv);
		System.out.println("body    : " + HexString.encode(bodyTlv.toByteArray()));
		
		CvEcPublicKey cvEcPub = ((CvEcPublicKey) body.getPublicKey());
		DomainParameterSetEcdh domparams = new DomainParameterSetEcdh(cvEcPub.getParams());
		
		KeyPair kp = CryptoUtil.generateKeyPair(domparams, new SecureRandom());
		PublicKey newPubKey = kp.getPublic();
		
		CertificateBody newBody = new CertificateBody(bodyTlv);
		CardVerifiableCertificate newCert = new CardVerifiableCertificate(newBody, signature);
		
		ConstructedTlvDataObject finalEnc = newCert.getEncoded();
		
		CardVerifiableCertificate finalCert = new CardVerifiableCertificate(finalEnc, newPubKey);
		
		assertEquals(cvCertDETESTeID00004FullTlv, finalCert.getEncoded());
	}
	
	@Test
	public void fail() {
		assertTrue(false);
	}
	
}
