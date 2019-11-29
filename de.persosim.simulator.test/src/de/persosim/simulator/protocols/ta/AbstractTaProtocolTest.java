package de.persosim.simulator.protocols.ta;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.globaltester.junit.ReflectionHelper;
import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.DateTimeCardObject;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.crypto.certificates.CertificateExtension;
import de.persosim.simulator.crypto.certificates.ExtensionOid;
import de.persosim.simulator.crypto.certificates.GenericExtension;
import de.persosim.simulator.crypto.certificates.PublicKeyReference;
import de.persosim.simulator.exception.CarParameterInvalidException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.pace.TestCardStateAccessor;
import de.persosim.simulator.protocols.pace.TestMasterFile;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.BitField;

public class AbstractTaProtocolTest extends PersoSimTestCase {

	TestCardStateAccessor testCardState;
	TestMasterFile testMf;
	DateTimeCardObject currentDate;
	Date future;
	Date past;
	Date current;
	AbstractTaProtocol taProtocol;

	CertificateHolderAuthorizationTemplate isCvcaChat;
	CertificateHolderAuthorizationTemplate isDvDomesticChat;
	CertificateHolderAuthorizationTemplate isDvForeignChat;
	CertificateHolderAuthorizationTemplate isTerminalChat;
	PublicKeyReference chr;
	TrustPointCardObject trustPoint;

	
	TestCardVerifiableCertificate certificate;
	TestCardVerifiableCertificate issuingCertificate;

	@Before
	public void setUp() throws CarParameterInvalidException {
		
		testMf = new TestMasterFile();
		testCardState = new TestCardStateAccessor(testMf);
		
		Calendar calendar = Calendar.getInstance();

		calendar.set(2014, 4, 5, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		current = calendar.getTime();

		calendar.set(2014, 4, 4, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		past = calendar.getTime();

		calendar.set(2014, 4, 6, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		future = calendar.getTime();

		currentDate = new DateTimeCardObject(current);

		taProtocol = new DefaultTaProtocol();
		taProtocol.setCardStateAccessor(testCardState);
		taProtocol.init();

		isCvcaChat = new CertificateHolderAuthorizationTemplate(TerminalType.IS,
				new RelativeAuthorization(CertificateRole.CVCA, new BitField(
						new boolean[] {})));
		isDvDomesticChat = new CertificateHolderAuthorizationTemplate(
				TerminalType.IS, new RelativeAuthorization(
						CertificateRole.DV_TYPE_1, new BitField(
								new boolean[] {})));
		isDvForeignChat = new CertificateHolderAuthorizationTemplate(
				TerminalType.IS, new RelativeAuthorization(
						CertificateRole.DV_TYPE_2, new BitField(
								new boolean[] {})));
		isTerminalChat = new CertificateHolderAuthorizationTemplate(
				TerminalType.IS, new RelativeAuthorization(
						CertificateRole.TERMINAL,
						new BitField(new boolean[] {})));

		chr = new PublicKeyReference("DE", "JUNIT", "00000");

		trustPoint = new TrustPointCardObject(null, issuingCertificate);
	}

	/**
	 * Test the permanent certificate import by using a valid certificate
	 * replacing an expired one in the trust point and check if the trust point
	 * contains the newly imported certificate.
	 */
	@Test
	public void testImportCertificatePermanent() throws Exception {
		// prepare the test data
		testMf.addChild(currentDate);
		testMf.addChild(trustPoint);
		
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isCvcaChat;
		certificate.effectiveDate = current;
		certificate.expirationDate = future;
		certificate.chr = chr;
		
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isCvcaChat;
		issuingCertificate.effectiveDate= past;
		issuingCertificate.expirationDate= past;
		issuingCertificate.chr = chr;

		ReflectionHelper.setField(AbstractTaProtocol.class, taProtocol, "trustPoint", trustPoint);

		// call MUT
		ReflectionHelper.invoke(AbstractTaProtocol.class, taProtocol, "importCertificate", 
				new Class<?>[]{CardVerifiableCertificate.class, CardVerifiableCertificate.class}, certificate, issuingCertificate);

		assertEquals(trustPoint.getCurrentCertificate(), certificate);
	}

	/**
	 * Test the temporary certificate import by using a valid domestic DV
	 * certificate and checking if the internal variables representing the last
	 * temporary imported certificate are set.
	 */
	@Test
	public void testImportCertificateTemporary() throws Exception {
		// prepare the test data
		testMf.addChild(currentDate);
		testMf.addChild(trustPoint);
		
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isDvDomesticChat;
		certificate.effectiveDate = current;
		certificate.expirationDate = future;
		certificate.chr = chr;
		
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isCvcaChat;
		issuingCertificate.effectiveDate= past;
		issuingCertificate.expirationDate= future;
		
		// call MUT
		ReflectionHelper.invoke(AbstractTaProtocol.class, taProtocol, "importCertificate", 
				new Class<?>[]{CardVerifiableCertificate.class, CardVerifiableCertificate.class}, certificate, issuingCertificate);

		assertEquals(certificate, ReflectionHelper.getFieldValue(AbstractTaProtocol.class, taProtocol,
				"mostRecentTemporaryCertificate"));
	}

	/**
	 * Positive test for the date validity checks using domestic dv certificate
	 */
	@Test
	public void testCheckValidityDvDomesticPositive() throws Exception {
		// prepare the test data
		testMf.addChild(currentDate);
		
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isDvDomesticChat;
		certificate.effectiveDate= past;
		certificate.expirationDate= future;
		
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isCvcaChat;
		issuingCertificate.effectiveDate= past;
		issuingCertificate.expirationDate= future;
		
		// call MUT
		assertTrue(AbstractTaProtocol.checkValidity(certificate, issuingCertificate, current));
	}

	/**
	 * Positive test for the date validity checks using an accurate terminal certificate (signed by domestic DV)
	 */
	@Test
	public void testCheckValidityAccurateTerminalPositive() throws Exception {
		// prepare the test data
		testMf.addChild(currentDate);
		
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isDvDomesticChat;
		certificate.effectiveDate= past;
		certificate.expirationDate= future;
		
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isDvDomesticChat;
		issuingCertificate.effectiveDate= past;
		issuingCertificate.expirationDate= past;
		
		// call MUT
		assertTrue(AbstractTaProtocol.checkValidity(certificate, issuingCertificate, current));
	}

	/**
	 * Negative test for the date validity checks using a non accurate terminal certificate (signed by foreign DV)
	 */
	@Test
	public void testCheckValidityNonAccurateTerminalNegative() throws Exception {
		// prepare the test data
		testMf.addChild(currentDate);
		
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isTerminalChat;
		certificate.effectiveDate= past;
		certificate.expirationDate= future;
		
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isDvForeignChat;
		issuingCertificate.effectiveDate= past;
		issuingCertificate.expirationDate= past;
		

		// call MUT
		assertTrue(AbstractTaProtocol.checkValidity(certificate, issuingCertificate, current));
	}

	/**
	 * Positive test for the date validity checks using a CVCA link certificate
	 */
	@Test
	public void testCheckValidityCvcaLinkIssuingCvcaExpired() throws Exception {
		// prepare the test data
		testMf.addChild(currentDate);
		
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isCvcaChat;
		certificate.effectiveDate= past;
		certificate.expirationDate= future;
		
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isCvcaChat;
		issuingCertificate.effectiveDate= past;
		issuingCertificate.expirationDate= past;
		
		// call MUT
		assertTrue(AbstractTaProtocol.checkValidity(certificate, issuingCertificate, current));
	}

	/**
	 * Positive test for the date validity checks using a CVCA link certificate
	 */
	@Test
	public void testCheckValidityCvcaLinkIssuingCvcaValid() throws Exception {
		// prepare the test data
		testMf.addChild(currentDate);
		
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isCvcaChat;
		certificate.effectiveDate= past;
		certificate.expirationDate= future;
		
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isCvcaChat;
		issuingCertificate.effectiveDate= past;
		issuingCertificate.expirationDate= past;
		
		// call MUT
		assertTrue(AbstractTaProtocol.checkValidity(certificate, issuingCertificate, current));
	}

	/**
	 * Test for checking the date validity using an expired CVCA certificate 
	 */
	@Test
	public void testCheckValidityDomesticDvIssuingCvcaInvalid() throws Exception {
		// prepare the test data
		testMf.addChild(currentDate);
		
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isDvDomesticChat;
		certificate.effectiveDate= past;
		certificate.expirationDate= future;
		
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isCvcaChat;
		issuingCertificate.effectiveDate= past;
		issuingCertificate.expirationDate= past;

		// call MUT
		assertFalse(AbstractTaProtocol.checkValidity(certificate, issuingCertificate, current));
		
	}
	
	/**
	 * Positive test for the update date mechanism that uses an accurate
	 * terminal certificate (signed by domestic DV)
	 */
	@Test
	public void testUpdateDatePositive() throws Exception {
		// prepare the test data
		testMf.addChild(currentDate);
		
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isCvcaChat;
		certificate.effectiveDate= future;
		certificate.expirationDate= future;
		
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isCvcaChat;
		issuingCertificate.effectiveDate= past;
		issuingCertificate.expirationDate= past;
		
		// call MUT
		AbstractTaProtocol.updateDate(certificate, issuingCertificate, currentDate);
		assertEquals(future, currentDate.getDate());
	}

	/**
	 * Negative test for the update date mechanism that uses a non accurate
	 * terminal certificate (signed by foreign DV)
	 */
	@Test
	public void testUpdateDateNegative() {
		
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isTerminalChat;
		certificate.effectiveDate = future;
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isDvForeignChat;

		// call MUT
		AbstractTaProtocol.updateDate(certificate, issuingCertificate, currentDate);
		assertEquals(current, currentDate.getDate());
	}

	@Test
	public void testIsCertificateIssuerValidPositiveDv() throws CertificateNotParseableException {
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isDvDomesticChat;
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isCvcaChat;
		
		// call MUT
		assertTrue(AbstractTaProtocol.isCertificateIssuerValid(issuingCertificate, issuingCertificate));
	}

	@Test
	public void testIsCertificateIssuerValidPositiveTerminal() throws CertificateNotParseableException {
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isTerminalChat;
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isDvDomesticChat;

		// call MUT
		assertTrue(AbstractTaProtocol.isCertificateIssuerValid(certificate, issuingCertificate));
	}

	@Test
	public void testIsCertificateIssuerValidPositiveCvcaLink() throws CertificateNotParseableException {
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isCvcaChat;
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isCvcaChat;
		
		// call MUT
		assertTrue(AbstractTaProtocol.isCertificateIssuerValid(issuingCertificate, issuingCertificate));
	}

	@Test
	public void testIsCertificateIssuerValidNegativeCvcaTerminal() throws CertificateNotParseableException {
		certificate = new TestCardVerifiableCertificate();
		certificate.chat = isTerminalChat;
		issuingCertificate = new TestCardVerifiableCertificate();
		issuingCertificate.chat = isCvcaChat;

		// call MUT
		assertFalse(AbstractTaProtocol.isCertificateIssuerValid(certificate, issuingCertificate));
	}
	
	@Test
	public void testExtractTerminalSector() throws Exception {
		// prepare the test data
		PrimitiveTlvDataObject firstObject = new PrimitiveTlvDataObject(TlvConstants.TAG_80, new byte [] {1,2,3,4});
		PrimitiveTlvDataObject secondObject = new PrimitiveTlvDataObject(TlvConstants.TAG_81, new byte [] {5,6,7,8});
		final TlvDataObjectContainer dataObjects = new TlvDataObjectContainer(firstObject);
		dataObjects.addTlvDataObject(secondObject);
		
		final ArrayList<CertificateExtension> extensions = new ArrayList<>();
		extensions.add(new GenericExtension(null, null) {
			@Override
			public Oid getObjectIdentifier() {
				return ExtensionOid.id_Sector;
			}
			
			@Override
			public TlvDataObjectContainer getDataObjects() {
				return dataObjects;
			}
			
		});
		

		certificate = new TestCardVerifiableCertificate();
		certificate.extensions = extensions;
		
		ReflectionHelper.invoke(AbstractTaProtocol.class, taProtocol, "extractTerminalSector", new Class<?>[] {CardVerifiableCertificate.class}, certificate);
		assertArrayEquals(firstObject.getValueField(), (byte []) ReflectionHelper.getFieldValue(AbstractTaProtocol.class, taProtocol, "firstSectorPublicKeyHash"));
		assertArrayEquals(secondObject.getValueField(), (byte []) ReflectionHelper.getFieldValue(AbstractTaProtocol.class, taProtocol, "secondSectorPublicKeyHash"));
	}
}
