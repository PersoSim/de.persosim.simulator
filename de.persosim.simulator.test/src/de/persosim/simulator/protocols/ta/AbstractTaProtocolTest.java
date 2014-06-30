package de.persosim.simulator.protocols.ta;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.DateTimeCardObject;
import de.persosim.simulator.cardobjects.DateTimeObjectIdentifier;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.crypto.certificates.CertificateExtension;
import de.persosim.simulator.crypto.certificates.PublicKeyReference;
import de.persosim.simulator.exception.CarParameterInvalidException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.BitField;

public class AbstractTaProtocolTest extends PersoSimTestCase {

	@Mocked
	CardStateAccessor mockedCardStateAccessor;
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

	@Mocked
	CardVerifiableCertificate certificate;
	@Mocked
	CardVerifiableCertificate issuingCertificate;
	@Mocked
	CertificateExtension certificateExtension;

	@Before
	public void setUp() throws CarParameterInvalidException {
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

		currentDate = new DateTimeCardObject(null, current);

		taProtocol = new DefaultTaProtocol();
		taProtocol.setCardStateAccessor(mockedCardStateAccessor);
		taProtocol.init();

		isCvcaChat = new CertificateHolderAuthorizationTemplate(TaOid.id_IS,
				new RelativeAuthorization(CertificateRole.CVCA, new BitField(
						new boolean[] {})));
		isDvDomesticChat = new CertificateHolderAuthorizationTemplate(
				TaOid.id_IS, new RelativeAuthorization(
						CertificateRole.DV_TYPE_1, new BitField(
								new boolean[] {})));
		isDvForeignChat = new CertificateHolderAuthorizationTemplate(
				TaOid.id_IS, new RelativeAuthorization(
						CertificateRole.DV_TYPE_2, new BitField(
								new boolean[] {})));
		isTerminalChat = new CertificateHolderAuthorizationTemplate(
				TaOid.id_IS, new RelativeAuthorization(
						CertificateRole.TERMINAL,
						new BitField(new boolean[] {})));

		chr = new PublicKeyReference("DE", "JUNIT", "00000");

		trustPoint = new TrustPointCardObject(null, chr, issuingCertificate);
	}

	/**
	 * Test the permanent certificate import by using a valid certificate
	 * replacing an expired one in the trust point and check if the trust point
	 * contains the newly imported certificate.
	 */
	@Test
	public void testImportCertificatePermanent() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(TrustPointIdentifier.class),
						withInstanceOf(Scope.class));
				result = trustPoint;
				mockedCardStateAccessor.getObject(
						withInstanceOf(DateTimeObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = currentDate;
				issuingCertificate.getEffectiveDate();
				result = past;
				issuingCertificate.getExpirationDate();
				result = past;
				issuingCertificate.getCertificateHolderReference();
				result = chr;
				certificate.getEffectiveDate();
				result = current;
				certificate.getExpirationDate();
				result = future;
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isCvcaChat;
				certificate.getCertificateHolderReference();
				result = chr;
			}
		};

		Deencapsulation.setField(taProtocol, trustPoint);

		// call MUT
		Deencapsulation.invoke(taProtocol, "importCertificate", certificate, issuingCertificate);

		assertEquals(trustPoint.getCurrentCertificate(), certificate);
	}

	/**
	 * Test the temporary certificate import by using a valid domestic DV
	 * certificate and checking if the internal variables representing the last
	 * temporary imported certificate are set.
	 */
	@Test
	public void testImportCertificateTemporary() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(TrustPointIdentifier.class),
						withInstanceOf(Scope.class));
				result = trustPoint;
				mockedCardStateAccessor.getObject(
						withInstanceOf(DateTimeObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = currentDate;
				certificate.getEffectiveDate();
				result = current;
				certificate.getExpirationDate();
				result = future;
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isDvDomesticChat;
				certificate.getCertificateHolderReference();
				result = chr;
			}
		};

		// call MUT
		Deencapsulation.invoke(taProtocol, "importCertificate", certificate, issuingCertificate);

		assertEquals(Deencapsulation.getField(taProtocol,
				"mostRecentTemporaryCertificate"), certificate);
		assertEquals(Deencapsulation.getField(taProtocol,
				"mostRecentTemporaryPublicKeyReference"), chr);
	}

	/**
	 * Positive test for the date validity checks using domestic dv certificate
	 */
	@Test
	public void testCheckValidityDvDomesticPositive() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(DateTimeObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = currentDate;
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isDvDomesticChat;
				certificate.getExpirationDate();
				result = future;
			}
		};
		// call MUT
		assertTrue((boolean) Deencapsulation.invoke(taProtocol, "checkValidity",
				certificate, issuingCertificate));
	}

	/**
	 * Positive test for the date validity checks using an accurate terminal certificate (signed by domestic DV)
	 */
	@Test
	public void testCheckValidityAccurateTerminalPositive() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(DateTimeObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = currentDate;
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isDvDomesticChat;
				certificate.getExpirationDate();
				result = future;
				certificate.getEffectiveDate();
				result = past;
			}
		};

		// call MUT
		assertTrue((boolean) Deencapsulation.invoke(taProtocol, "checkValidity",
				certificate, issuingCertificate));
	}

	/**
	 * Negative test for the date validity checks using a non accurate terminal certificate (signed by foreign DV)
	 */
	@Test
	public void testCheckValidityNonAccurateTerminalNegative() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(DateTimeObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = currentDate;
				issuingCertificate.getCertificateHolderAuthorizationTemplate();
				result = isDvForeignChat;
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isTerminalChat;
				certificate.getExpirationDate();
				result = future;
			}
		};

		// call MUT
		assertTrue((boolean) Deencapsulation.invoke(taProtocol, "checkValidity",
				certificate, issuingCertificate));
	}

	/**
	 * Positive test for the date validity checks using a CVCA link certificate
	 */
	@Test
	public void testCheckValidityCvcaLink() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(DateTimeObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = currentDate;
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isCvcaChat;
				certificate.getExpirationDate();
				result = future;
			}
		};

		// call MUT
		assertTrue((boolean) Deencapsulation.invoke(taProtocol, "checkValidity",
				certificate, issuingCertificate));
	}


	/**
	 * Positive test for the update date mechanism that uses an accurate
	 * terminal certificate (signed by domestic DV)
	 */
	@Test
	public void testUpdateDatePositive() {

		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(DateTimeObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = currentDate;
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isCvcaChat;
				certificate.getEffectiveDate();
				result = future;
			}
		};

		// call MUT
		Deencapsulation.invoke(taProtocol, "updateDate", certificate,
				issuingCertificate);
		assertEquals(future, currentDate.getDate());
	}

	/**
	 * Negative test for the update date mechanism that uses a non accurate
	 * terminal certificate (signed by foreign DV)
	 */
	@Test
	public void testUpdateDateNegative() {

		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(
						withInstanceOf(DateTimeObjectIdentifier.class),
						withInstanceOf(Scope.class));
				result = currentDate;
				issuingCertificate.getCertificateHolderAuthorizationTemplate();
				result = isDvForeignChat;
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isTerminalChat;
				certificate.getEffectiveDate();
				result = future;
			}
		};

		// call MUT
		Deencapsulation.invoke(taProtocol, "updateDate", certificate,
				issuingCertificate);
		assertEquals(current, currentDate.getDate());
	}

	@Test
	public void testIsCertificateIssuerValidPositiveDv() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isDvDomesticChat;
				issuingCertificate.getCertificateHolderAuthorizationTemplate();
				result = isCvcaChat;
			}
		};
		// call MUT
		assertTrue((boolean)Deencapsulation.invoke(taProtocol, "isCertificateIssuerValid", certificate, issuingCertificate));
	}

	@Test
	public void testIsCertificateIssuerValidPositiveTerminal() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isTerminalChat;
				issuingCertificate.getCertificateHolderAuthorizationTemplate();
				result = isDvDomesticChat;
			}
		};
		// call MUT
		assertTrue((boolean)Deencapsulation.invoke(taProtocol, "isCertificateIssuerValid", certificate, issuingCertificate));
	}

	@Test
	public void testIsCertificateIssuerValidPositiveCvcaLink() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isCvcaChat;
				issuingCertificate.getCertificateHolderAuthorizationTemplate();
				result = isCvcaChat;
			}
		};
		// call MUT
		assertTrue((boolean)Deencapsulation.invoke(taProtocol, "isCertificateIssuerValid", certificate, issuingCertificate));
	}

	@Test
	public void testIsCertificateIssuerValidNegativeCvcaTerminal() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				certificate.getCertificateHolderAuthorizationTemplate();
				result = isTerminalChat;
				issuingCertificate.getCertificateHolderAuthorizationTemplate();
				result = isCvcaChat;
			}
		};
		// call MUT
		assertTrue(!(boolean)Deencapsulation.invoke(taProtocol, "isCertificateIssuerValid", certificate, issuingCertificate));
	}
	
	@Test
	public void testExtractTerminalSector(){
		// prepare the mock
		final HashSet<CertificateExtension> extensions = new HashSet<>();
		extensions.add(certificateExtension);

		PrimitiveTlvDataObject firstObject = new PrimitiveTlvDataObject(TlvConstants.TAG_80, new byte [] {1,2,3,4});
		PrimitiveTlvDataObject secondObject = new PrimitiveTlvDataObject(TlvConstants.TAG_81, new byte [] {5,6,7,8});
		final TlvDataObjectContainer dataObjects = new TlvDataObjectContainer(firstObject);
		dataObjects.addTlvDataObject(secondObject);
		
		new NonStrictExpectations() {
			{
				certificate.getCertificateExtensions();
				result = extensions;
				certificateExtension.getObjectIdentifier();
				result = TaOid.id_Sector;
				certificateExtension.getDataObjects();
				result = dataObjects;
			}
		};
		Deencapsulation.invoke(taProtocol, "extractTerminalSector", certificate);
		assertArrayEquals(firstObject.getValueField(), (byte []) Deencapsulation.getField(taProtocol, "firstSectorPublicKeyHash"));
		assertArrayEquals(secondObject.getValueField(), (byte []) Deencapsulation.getField(taProtocol, "secondSectorPublicKeyHash"));
	}
}
