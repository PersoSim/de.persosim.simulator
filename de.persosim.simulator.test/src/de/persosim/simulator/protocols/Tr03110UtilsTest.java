package de.persosim.simulator.protocols;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.DomainParameterSetCardObject;
import de.persosim.simulator.cardobjects.DomainParameterSetIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.crypto.certificates.CvPublicKey;
import de.persosim.simulator.exception.NotParseableException;
import de.persosim.simulator.protocols.pace.Pace;
import de.persosim.simulator.protocols.pace.PaceOid;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.HexString;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

public class Tr03110UtilsTest extends PersoSimTestCase {
	@Mocked
	MasterFile mf;
	Oid oid1, oid2;
	OidIdentifier oidIdentifier1, oidIdentifier2;
	DomainParameterSetCardObject domainParameters12, domainParameters13;
	DomainParameterSetIdentifier domainparameterSetIdentifier12, domainparameterSetIdentifier13;
	
	@Before
	public void setUp() throws Exception{
		// define domain parameters
		oid1 = Pace.OID_id_PACE_ECDH_GM_AES_CBC_CMAC_256;
		oidIdentifier1 = new OidIdentifier(oid1);
		oid2 = Pace.OID_id_PACE_ECDH_GM_AES_CBC_CMAC_128;
		oidIdentifier2 = new OidIdentifier(oid2);
		
		domainparameterSetIdentifier12 = new DomainParameterSetIdentifier(12);
		domainParameters12 = new DomainParameterSetCardObject(StandardizedDomainParameters.getDomainParameterSetById(12), domainparameterSetIdentifier12);
		domainParameters12.addOidIdentifier(oidIdentifier1);
		
		domainparameterSetIdentifier13 = new DomainParameterSetIdentifier(13);
		domainParameters13 = new DomainParameterSetCardObject(StandardizedDomainParameters.getDomainParameterSetById(13), domainparameterSetIdentifier13);
		domainParameters13.addOidIdentifier(oidIdentifier2);
	}
	
	/**
	 * Positive test: build authentication token with EC public key.
	 */
	@Test
	public void testBuildAuthenticationTokenInputEcPublicKey() {
		Oid oid = new PaceOid(Pace.id_PACE_ECDH_GM_AES_CBC_CMAC_256); // arbitrary OID with matching key agreement and key length
		DomainParameterSetEcdh domainParametersEcdh = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(13);
		byte[] ecdhPublicKeyDataPicc = HexString.toByteArray("04A44EBE5451DF7AADB01E459B8C928A87746A57927C8C28A6775C97A7E1FE8D9A46FF4A1CC7E4D1389AEA19758E4F75C28C598FD734AEBEB135337CF95BE12E94");
		ECPublicKey ecdhPublicKeyPicc = domainParametersEcdh.reconstructPublicKey(ecdhPublicKeyDataPicc);
		
		byte[] tokenExpected = HexString.toByteArray("7F494F060A04007F00070202040204864104A44EBE5451DF7AADB01E459B8C928A87746A57927C8C28A6775C97A7E1FE8D9A46FF4A1CC7E4D1389AEA19758E4F75C28C598FD734AEBEB135337CF95BE12E94");
		TlvDataObjectContainer tokenReceived = Tr03110Utils.buildAuthenticationTokenInput(ecdhPublicKeyPicc, domainParametersEcdh, oid);
		byte[] tokenReceivedPlain = tokenReceived.toByteArray();
		
		assertArrayEquals("token mismatch", tokenExpected, tokenReceivedPlain);
	}
	
	
	
	/**
	 * Positive test: check parsing of a valid date encoding for input length 6.
	 * @throws NotParseableException 
	 */
	@Test
	public void testParseDate_inputLength6() throws NotParseableException {
		byte[] date = HexString.toByteArray("010001010202");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(2010, Calendar.NOVEMBER, 22, 0, 0, 0);
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		Date expectedDate = calendar.getTime();
		
		Date receivedDate = Tr03110Utils.parseDate(date);
		
		assertEquals(expectedDate, receivedDate);
	}
	
	/**
	 * Negative test: check parsing of a date encoding of illegal length.
	 * @throws NotParseableException 
	 */
	@Test(expected = NotParseableException.class)
	public void testParseDate_illegalLength() throws NotParseableException {
		byte[] date = HexString.toByteArray("0100010102");
		
		Tr03110Utils.parseDate(date);
	}
	
	/**
	 * Negative test: check parsing of a date encoding encoding non-numeric characters.
	 * @throws NotParseableException 
	 */
	@Test(expected = NotParseableException.class)
	public void testParseDate_nonNumericCharacters() throws NotParseableException {
		byte[] date = HexString.toByteArray("01000101020A");
		
		Tr03110Utils.parseDate(date);
	}
	
	/**
	 * Positive test: Convert the Date 01.01.2070 to its BCD representation
	 */
	@Test
	public void testEncodeDate_singleCharacters() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(2070, Calendar.JANUARY, 1, 0, 0, 0);

		Date date = calendar.getTime();
		byte [] expected = HexString.toByteArray("070000010001");
		assertArrayEquals(expected, Tr03110Utils.encodeDate(date));
	}
	
	/**
	 * Positive test: Convert the Date 23.12.2078 to its BCD representation
	 */
	@Test
	public void testEncodeDate_doubleCharacters() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(2078, Calendar.DECEMBER, 23, 0, 0, 0);

		Date date = calendar.getTime();
		byte [] expected = HexString.toByteArray("070801020203");
		assertArrayEquals(expected, Tr03110Utils.encodeDate(date));
	}

	/**
	 * This tests if the
	 * {@link Tr03110Utils#parseCertificatePublicKey(de.persosim.simulator.tlv.ConstructedTlvDataObject, PublicKey)}
	 * method correctly handles exceptions thrown by providers.
	 * 
	 * @param provider
	 * @throws GeneralSecurityException
	 */
	@Test
	public void testParseCertificatePublicKeyExceptionInProvider(
			@Capturing final Tr03110UtilsProvider provider) throws GeneralSecurityException{
		// prepare the mock
		new NonStrictExpectations() {
			{
				provider.parseCvPublicKey(null);
				result = new GeneralSecurityException();
			}
		};
		
		assertNull(Tr03110Utils.parseCvPublicKey(null));
	}
	
	/**
	 * This test checks if a key object returned by a provider is delivered by
	 * {@link Tr03110Utils#parseCertificatePublicKey(de.persosim.simulator.tlv.ConstructedTlvDataObject, PublicKey)}
	 * 
	 * @param provider
	 * @throws GeneralSecurityException
	 */
	@Test
	public void testParseCertificatePublicKeyResultFound(
			@Capturing final Tr03110UtilsProvider provider) throws GeneralSecurityException{
		// prepare the mock
		final CvPublicKey key = new CvPublicKey(null, null) {

			private static final long serialVersionUID = 1L;

			@Override
			public ConstructedTlvDataObject toTlvDataObject(boolean includeConditionalObjects) {return null;}

			@Override
			public boolean isComplete() {return false;}

			@Override
			public boolean updateKey(PublicKey publicKey) {return false;}

			@Override
			public KeyPairGenerator getKeyPairGenerator(SecureRandom secRandom)
					throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
				return null;
			}

			@Override
			public boolean matchesCoreMaterial(CvPublicKey publicKey) {
				return false;
			}};
			
		new Expectations() {
			{
				provider.parseCvPublicKey(null);
				result =  key;
			}
		};
		
		assertSame(key, Tr03110Utils.parseCvPublicKey(null));
	}
	
}
