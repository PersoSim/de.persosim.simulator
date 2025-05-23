package de.persosim.simulator.protocols.ca3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.globaltester.cryptoprovider.Crypto;
import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.crypto.certificates.CertificateExtension;
import de.persosim.simulator.crypto.certificates.ExtensionOid;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.PlatformUtil;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ca3.PsAuthInfo.PsAuthInfoValue;
import de.persosim.simulator.protocols.puo.TerminalSectorForPseudonymousSignaturesCertificateExtension;
import de.persosim.simulator.protocols.ta.Authorization;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.AuthorizationStore;
import de.persosim.simulator.secstatus.EffectiveAuthorizationMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusMechanismUpdatePropagation;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvPath;
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class PsaProtocolTest extends PersoSimTestCase implements TlvConstants {

	private SecStatus secStatus;
	private MasterFile masterFile;

	private PsaProtocol psaProtocol;
	private ECPoint sectorPublicKey12;
	private DomainParameterSetEcdh domainParameters12;

	private ECPublicKey groupManagerPublicKey12;
	private BigInteger secretKeyIcc1PrivateKey12;
	private BigInteger secretKeyIcc2PrivateKey12;
	private KeyPair ephemeralKeyPair;

	private HashMap<Oid, Authorization> authorizations;
	private Oid psOid = Psa.id_PSA_ECDH_ECSchnorr_SHA_256;
	private SecCondition secConditionForAccessingIccXNoExplicitAuth;
	private SecCondition secConditionForAccessingIccXExplicitAuth;
	private TerminalSectorForPseudonymousSignaturesCertificateExtension tsfpsExtension;
	private TerminalSectorForPseudonymousSignaturesCertificateExtension tsfpsExtensionWrong;

	@Before
	public void setUp() throws InvalidKeySpecException, NoSuchAlgorithmException, AccessDeniedException {
		CardStateAccessor mockedCardStateAccessor;
		KeyObjectIcc keyObjectIccFull;
		Authorization sfAuthorization;
		ECPublicKey iccPublicKey12;
		secStatus = new SecStatus();

		masterFile = new MasterFile();
		masterFile.setSecStatus(secStatus);

		mockedCardStateAccessor = new CardStateAccessor() {
			@Override
			public MasterFile getMasterFile() {
				return masterFile;
			}

			@Override
			public Collection<SecMechanism> getCurrentMechanisms(SecContext context,
					Collection<Class<? extends SecMechanism>> wantedMechanisms) {
				return secStatus.getCurrentMechanisms(context, wantedMechanisms);
			}
		};

		domainParameters12 = (DomainParameterSetEcdh) StandardizedDomainParameters.getDomainParameterSetById(12);

		sectorPublicKey12 = DomainParameterSetEcdh.reconstructPoint(HexString.toByteArray(
				"0406588E6F1421A1C0D6527A54499724E92F2BBA0D72ACFFAA1CB2A483F733C63D242FC2B74DA86E861377228FC09C1A7F914591E1850B27D2CCC75822A60C35C4"));

		BigInteger groupManagerPrivateKey12 = new BigInteger(1,
				HexString.toByteArray("1065B7B861E96E2CA0AC40F49AC6333EACCEBE2DA3D9ECBD0B460D0F4704C504"));
		secretKeyIcc1PrivateKey12 = BigInteger.valueOf(23L);
		secretKeyIcc2PrivateKey12 = BigInteger.valueOf(66L);

		BigInteger iccPrivateKey12 = secretKeyIcc1PrivateKey12
				.add(groupManagerPrivateKey12.multiply(secretKeyIcc2PrivateKey12)).mod(domainParameters12.getOrder());

		iccPublicKey12 = (ECPublicKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePublic(domainParameters12
						.getPublicKeySpec(EcSchnorrSigner.generatePublicKey(domainParameters12, iccPrivateKey12)));
		ECPoint groupManagerPublicPoint12 = DomainParameterSetEcdh.reconstructPoint(HexString.toByteArray(
				"0431526A45EE6DF0A5BA34082E001C0A44CCD76DCC151374433E415B85009A5B9A9A7B9DC49DB2022F93E85CC3862B82408F7047C4A495C0701297C14A772DA54A"));
		groupManagerPublicKey12 = (ECPublicKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePublic(domainParameters12.getPublicKeySpec(groupManagerPublicPoint12));

		keyObjectIccFull = new KeyObjectIcc(
				KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
						.generatePrivate(domainParameters12.getPrivateKeySpec(secretKeyIcc1PrivateKey12)),
				KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
						.generatePrivate(domainParameters12.getPrivateKeySpec(secretKeyIcc2PrivateKey12)),
				iccPublicKey12, groupManagerPublicKey12, domainParameters12, new KeyIdentifier(42));
		keyObjectIccFull.addOidIdentifier(new OidIdentifier(psOid));
		masterFile.addChild(keyObjectIccFull);

		masterFile.addChild(new PsAuthInfo(new OidIdentifier(Psa.id_PSA), PsAuthInfoValue.NO_EXPLICIT_AUTHORISATION,
				PsAuthInfoValue.EXPLICIT_AUTHORISATION));

		ephemeralKeyPair = CryptoUtil.reconstructKeyPair(domainParameters12, HexString.toByteArray(
				"04ea7e44a199b2ed53cd27a1155bbcff7242bbe20f9ba684def28e633d3b8bd51230052756bd038d156aed27b0001b04b7f4c7f5f327f4522f9ed26ed16cd05b4b"),
				HexString.toByteArray("81d56defc80432390a9d62382e70e130ba9bbc5b764ed544a22e7b6548aa6e1e"));

		psaProtocol = new PsaProtocol();
		psaProtocol.setCardStateAccessor(mockedCardStateAccessor);

		TlvDataObjectContainer contextSpecificDataObjects = new TlvDataObjectContainer();
		ConstructedTlvDataObject tlvA0 = new ConstructedTlvDataObject(TlvConstants.TAG_A0);
		PrimitiveTlvDataObject tlv80 = new PrimitiveTlvDataObject(TlvConstants.TAG_80, HexString.toByteArray("0D"));
		PrimitiveTlvDataObject tlv81 = new PrimitiveTlvDataObject(TlvConstants.TAG_81,
				HexString.toByteArray("037394FF436D7B805C42A413959A4972B7AC79C95D573EFBF3111E604F27783D"));
		tlvA0.addTlvDataObject(tlv80);
		tlvA0.addTlvDataObject(tlv81);
		contextSpecificDataObjects.addTlvDataObject(tlvA0);
		tsfpsExtension = new TerminalSectorForPseudonymousSignaturesCertificateExtension(ExtensionOid.id_Ps_Sector,
				contextSpecificDataObjects);

		TlvDataObjectContainer contextSpecificDataObjectsWrong = new TlvDataObjectContainer();
		ConstructedTlvDataObject tlvA0Wrong = new ConstructedTlvDataObject(TlvConstants.TAG_A0);
		PrimitiveTlvDataObject tlv80Wrong = new PrimitiveTlvDataObject(TlvConstants.TAG_80,
				HexString.toByteArray("0D"));
		PrimitiveTlvDataObject tlv81Wrong = new PrimitiveTlvDataObject(TlvConstants.TAG_81,
				HexString.toByteArray("7c727e860045b3b994e6345d429c3500254fa6ce"));
		tlvA0Wrong.addTlvDataObject(tlv80Wrong);
		tlvA0Wrong.addTlvDataObject(tlv81Wrong);
		contextSpecificDataObjectsWrong.addTlvDataObject(tlvA0Wrong);
		tsfpsExtensionWrong = new TerminalSectorForPseudonymousSignaturesCertificateExtension(ExtensionOid.id_Ps_Sector,
				contextSpecificDataObjectsWrong);

		authorizations = new HashMap<>();
		byte[] auth = new byte[5];
		Arrays.fill(auth, (byte) 0xFF);
		sfAuthorization = new Authorization(new BitField(40, auth));

		authorizations.put(ExtensionOid.id_specialFunctions, sfAuthorization);

		secConditionForAccessingIccXNoExplicitAuth = psaProtocol
				.getSecConditionForComputingSectorSpecificIdentifier(PsAuthInfoValue.NO_EXPLICIT_AUTHORISATION);
		secConditionForAccessingIccXExplicitAuth = psaProtocol
				.getSecConditionForComputingSectorSpecificIdentifier(PsAuthInfoValue.EXPLICIT_AUTHORISATION);
	}

	/**
	 * Sets {@link ChipAuthentication3Mechanism} within security status
	 */
	private void updateSecStatusCa3() {
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION,
				new ChipAuthentication3Mechanism(null, 0, null, ephemeralKeyPair)));
	}

	/**
	 * Sets {@link EffectiveAuthorizationMechanism} with predefined authorization
	 * within security status
	 */
	private void updateSecStatusEffectiveAuthorizationPsa() {
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION,
				new EffectiveAuthorizationMechanism(new AuthorizationStore(authorizations))));
	}

	/**
	 * Sets {@link ChipAuthentication3Mechanism} and
	 * {@link EffectiveAuthorizationMechanism} with predefined authorization within
	 * security status
	 */
	private void updateSecStatusCa3AndEffectiveAuthorizationPsa() {
		updateSecStatusCa3();
		updateSecStatusEffectiveAuthorizationPsa();
	}

	/**
	 * Sets {@link TerminalAuthenticationMechanism} within security status with a
	 * TSFPS extension
	 * 
	 * @param useCorrectHash if true, TSFPS extension contains correct hash of
	 *                       sector public key
	 */
	private void updateSecStatusTaWithExtension(boolean useCorrectHash) {
		List<CertificateExtension> certificateExtensions = new ArrayList<>();
		if (useCorrectHash) {
			certificateExtensions.add(tsfpsExtension);
		} else {
			certificateExtensions.add(tsfpsExtensionWrong);
		}
		updateSecStatusTa(certificateExtensions);
	}

	/**
	 * Sets {@link TerminalAuthenticationMechanism} within security status
	 * 
	 * @param certificateExtensions the certificate extensions which shall be found
	 *                              within TA
	 */
	private void updateSecStatusTa(List<CertificateExtension> certificateExtensions) {
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, new TerminalAuthenticationMechanism(
						new byte[] {}, TerminalType.AT, null, null, null, "SHA-256", certificateExtensions)));
	}

	/**
	 * Positive test case: performs Set AT command, explicit key reference (Tag 84
	 * is present).
	 */
	@Test
	public void test_ExplicitKeyReference() {
		updateSecStatusCa3AndEffectiveAuthorizationPsa();

		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 41 A4 10 80 0B 04 00 7F 00 07 02 02 0B 01 02 03 84 01 2A");
		processingData.updateCommandApdu(this, "setAT APDU", CommandApduFactory.createCommandApdu(apduBytes));

		psaProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR,
				processingData.getResponseApdu().getStatusWord());
	}

	/**
	 * Positive test case: perform Set AT command, explicit key reference (Tag 84 is
	 * present).
	 */
	@Test
	public void test_ImplicitKeyReference() {
		updateSecStatusCa3AndEffectiveAuthorizationPsa();

		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 41 A4 0D 80 0B 04 00 7F 00 07 02 02 0B 01 02 03");
		processingData.updateCommandApdu(this, "setAT APDU", CommandApduFactory.createCommandApdu(apduBytes));

		psaProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR,
				processingData.getResponseApdu().getStatusWord());
	}

	/**
	 * Positive test case: perform General Authenticate command for PSA using both
	 * sector identifiers.
	 */
	@Test
	public void test_GeneralAuthenticate() throws NoSuchAlgorithmException, InvalidKeySpecException {
		updateSecStatusCa3AndEffectiveAuthorizationPsa();
		updateSecStatusTaWithExtension(true);

		ProcessingData processingData = new ProcessingData();
		byte[] sectorPublicKey = CryptoUtil.encode(sectorPublicKey12,
				domainParameters12.getPublicPointReferenceLengthL(), CryptoUtil.ENCODING_UNCOMPRESSED);
		ConstructedTlvDataObject data = new ConstructedTlvDataObject(TlvConstants.TAG_7C,
				new PrimitiveTlvDataObject(TlvConstants.TAG_80, sectorPublicKey));
		byte[] apduBytes = Utils.concatByteArrays(HexString.toByteArray("00 86 00 00"),
				new byte[] { (byte) data.getLength() }, data.toByteArray(), new byte[] { 0 });
		processingData.updateCommandApdu(this, "GeneralAuthenticate APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// set needed protocol state
		psaProtocol.psDomainParameters = domainParameters12;
		psaProtocol.publicKeyGroupManager = (ECPublicKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePublic(domainParameters12.getPublicKeySpec(groupManagerPublicKey12.getW()));
		psaProtocol.psOid = Psa.id_PSA_ECDH_ECSchnorr_SHA_256;
		psaProtocol.privateKeyIcc1 = (ECPrivateKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePrivate(domainParameters12.getPrivateKeySpec(secretKeyIcc1PrivateKey12));
		psaProtocol.privateKeyIcc2 = (ECPrivateKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePrivate(domainParameters12.getPrivateKeySpec(secretKeyIcc2PrivateKey12));
		psaProtocol.secConditonForComputingSectorIcc1 = secConditionForAccessingIccXNoExplicitAuth;
		psaProtocol.secConditonForComputingSectorIcc2 = secConditionForAccessingIccXNoExplicitAuth;

		psaProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR,
				processingData.getResponseApdu().getStatusWord());
		assertNotNull(((TlvDataObjectContainer) processingData.getResponseApdu().getData())
				.getTlvDataObject(new TlvPath(TlvConstants.TAG_7C, TlvConstants.TAG_82)));
		assertNotNull(((TlvDataObjectContainer) processingData.getResponseApdu().getData())
				.getTlvDataObject(new TlvPath(TlvConstants.TAG_7C, TlvConstants.TAG_83)));
		assertNotNull(((TlvDataObjectContainer) processingData.getResponseApdu().getData())
				.getTlvDataObject(new TlvPath(TlvConstants.TAG_7C, TlvConstants.TAG_84)));
	}

	/**
	 * Positive test case: perform General Authenticate command for PSA using one
	 * sector identifier.
	 */
	@Test
	public void test_OneSectorIdentifier() throws NoSuchAlgorithmException, InvalidKeySpecException {
		updateSecStatusCa3();
		updateSecStatusTaWithExtension(true);

		ProcessingData processingData = new ProcessingData();
		byte[] sectorPublicKey = CryptoUtil.encode(sectorPublicKey12,
				domainParameters12.getPublicPointReferenceLengthL(), CryptoUtil.ENCODING_UNCOMPRESSED);
		ConstructedTlvDataObject data = new ConstructedTlvDataObject(TlvConstants.TAG_7C,
				new PrimitiveTlvDataObject(TlvConstants.TAG_80, sectorPublicKey));
		byte[] apduBytes = Utils.concatByteArrays(HexString.toByteArray("00 86 00 00"),
				new byte[] { (byte) data.getLength() }, data.toByteArray(), new byte[] { 0 });
		processingData.updateCommandApdu(this, "GeneralAuthenticate APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// set needed protocol state
		psaProtocol.psDomainParameters = domainParameters12;
		psaProtocol.publicKeyGroupManager = (ECPublicKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePublic(domainParameters12.getPublicKeySpec(groupManagerPublicKey12.getW()));
		psaProtocol.psOid = Psa.id_PSA_ECDH_ECSchnorr_SHA_256;
		psaProtocol.ps1AuthInfo = 1;
		psaProtocol.ps2AuthInfo = 0;
		psaProtocol.privateKeyIcc1 = (ECPrivateKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePrivate(domainParameters12.getPrivateKeySpec(secretKeyIcc1PrivateKey12));
		psaProtocol.privateKeyIcc2 = (ECPrivateKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePrivate(domainParameters12.getPrivateKeySpec(secretKeyIcc2PrivateKey12));
		psaProtocol.secConditonForComputingSectorIcc1 = secConditionForAccessingIccXExplicitAuth;
		psaProtocol.secConditonForComputingSectorIcc2 = secConditionForAccessingIccXNoExplicitAuth;

		psaProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR,
				processingData.getResponseApdu().getStatusWord());
		assertNull(((TlvDataObjectContainer) processingData.getResponseApdu().getData())
				.getTlvDataObject(new TlvPath(TlvConstants.TAG_7C, TlvConstants.TAG_82)));
		assertNotNull(((TlvDataObjectContainer) processingData.getResponseApdu().getData())
				.getTlvDataObject(new TlvPath(TlvConstants.TAG_7C, TlvConstants.TAG_83)));
		assertNotNull(((TlvDataObjectContainer) processingData.getResponseApdu().getData())
				.getTlvDataObject(new TlvPath(TlvConstants.TAG_7C, TlvConstants.TAG_84)));
	}

	/**
	 * Negative test case: perform General Authenticate command for PSA with wrong
	 * public sector key hash
	 */
	@Test
	public void test_GeneralAuthenticateWrongHash() {
		updateSecStatusCa3AndEffectiveAuthorizationPsa();
		updateSecStatusTaWithExtension(false);

		ProcessingData processingData = new ProcessingData();
		byte[] sectorPublicKey = CryptoUtil.encode(sectorPublicKey12,
				domainParameters12.getPublicPointReferenceLengthL(), CryptoUtil.ENCODING_UNCOMPRESSED);
		ConstructedTlvDataObject data = new ConstructedTlvDataObject(TlvConstants.TAG_7C,
				new PrimitiveTlvDataObject(TlvConstants.TAG_80, sectorPublicKey));
		byte[] apduBytes = Utils.concatByteArrays(HexString.toByteArray("00 86 00 00"),
				new byte[] { (byte) data.getLength() }, data.toByteArray(), new byte[] { 0 });
		processingData.updateCommandApdu(this, "GeneralAuthenticate APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// set needed protocol state
		psaProtocol.psDomainParameters = domainParameters12;

		psaProtocol.process(processingData);

		// check expected SW
		short sw = processingData.getResponseApdu().getStatusWord();
		assertEquals("Statusword is not 6982", Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED, sw);
	}

	/**
	 * Negative test case: perform General Authenticate command for PSA without
	 * TSFPS-Extension
	 */
	@Test
	public void test_GeneralAuthenticateNoPsExtension() {
		updateSecStatusCa3AndEffectiveAuthorizationPsa();
		updateSecStatusTa(null);

		ProcessingData processingData = new ProcessingData();
		byte[] sectorPublicKey = CryptoUtil.encode(sectorPublicKey12,
				domainParameters12.getPublicPointReferenceLengthL(), CryptoUtil.ENCODING_UNCOMPRESSED);
		ConstructedTlvDataObject data = new ConstructedTlvDataObject(TlvConstants.TAG_7C,
				new PrimitiveTlvDataObject(TlvConstants.TAG_80, sectorPublicKey));
		byte[] apduBytes = Utils.concatByteArrays(HexString.toByteArray("00 86 00 00"),
				new byte[] { (byte) data.getLength() }, data.toByteArray(), new byte[] { 0 });
		processingData.updateCommandApdu(this, "GeneralAuthenticate APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// set needed protocol state
		psaProtocol.psDomainParameters = domainParameters12;

		psaProtocol.process(processingData);

		// check expected SW
		short sw = processingData.getResponseApdu().getStatusWord();
		assertEquals("Statusword is not 6982", Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED, sw);
	}

	/**
	 * Negative test case: perform General Authenticate command for PSA but no
	 * ephemeral public key found.
	 */
	@Test
	public void test_GetEphemeralPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		secStatus.updateMechanisms(new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION,
				new ChipAuthentication3Mechanism(null, 0, null, null)));

		updateSecStatusEffectiveAuthorizationPsa();
		updateSecStatusTaWithExtension(true);

		ProcessingData processingData = new ProcessingData();
		byte[] sectorPublicKey = CryptoUtil.encode(sectorPublicKey12,
				domainParameters12.getPublicPointReferenceLengthL(), CryptoUtil.ENCODING_UNCOMPRESSED);
		ConstructedTlvDataObject data = new ConstructedTlvDataObject(TlvConstants.TAG_7C,
				new PrimitiveTlvDataObject(TlvConstants.TAG_80, sectorPublicKey));
		byte[] apduBytes = Utils.concatByteArrays(HexString.toByteArray("00 86 00 00"),
				new byte[] { (byte) data.getLength() }, data.toByteArray(), new byte[] { 0 });
		processingData.updateCommandApdu(this, "GeneralAuthenticate APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// set needed protocol state
		psaProtocol.psDomainParameters = domainParameters12;
		psaProtocol.publicKeyGroupManager = (ECPublicKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePublic(domainParameters12.getPublicKeySpec(groupManagerPublicKey12.getW()));
		psaProtocol.psOid = Psa.id_PSA_ECDH_ECSchnorr_SHA_256;
		psaProtocol.ps1AuthInfo = 2;
		psaProtocol.privateKeyIcc1 = (ECPrivateKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePrivate(domainParameters12.getPrivateKeySpec(secretKeyIcc1PrivateKey12));
		psaProtocol.privateKeyIcc2 = (ECPrivateKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePrivate(domainParameters12.getPrivateKeySpec(secretKeyIcc2PrivateKey12));
		psaProtocol.secConditonForComputingSectorIcc1 = secConditionForAccessingIccXNoExplicitAuth;
		psaProtocol.secConditonForComputingSectorIcc2 = secConditionForAccessingIccXNoExplicitAuth;

		psaProtocol.process(processingData);

		// check expected SW
		short sw = processingData.getResponseApdu().getStatusWord();
		assertEquals("Wrong SW returned", Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, sw);
	}

	/**
	 * Negative test case: perform Set AT command, cardObject is null.
	 */
	@Test
	public void test_ExtractyReferenceData() {
		updateSecStatusCa3AndEffectiveAuthorizationPsa();

		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 41 A4 10 80 0B 04 00 7F 00 07 02 02 0B 01 02 03 84 01 2B");
		processingData.updateCommandApdu(this, "setAT APDU", CommandApduFactory.createCommandApdu(apduBytes));

		psaProtocol.process(processingData);

		// check expected SW
		short sw = processingData.getResponseApdu().getStatusWord();
		assertEquals("Wrong SW returned", Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, sw);

	}

	/**
	 * Negative test case: Execution status not satisfied, CA3 has not been executed
	 * before.
	 */
	@Test
	public void test_getCa3ExecutionStatus() throws NoSuchAlgorithmException, InvalidKeySpecException {
		updateSecStatusEffectiveAuthorizationPsa();
		updateSecStatusTaWithExtension(true);

		ProcessingData processingData = new ProcessingData();
		byte[] sectorPublicKey = CryptoUtil.encode(sectorPublicKey12,
				domainParameters12.getPublicPointReferenceLengthL(), CryptoUtil.ENCODING_UNCOMPRESSED);
		ConstructedTlvDataObject data = new ConstructedTlvDataObject(TlvConstants.TAG_7C,
				new PrimitiveTlvDataObject(TlvConstants.TAG_80, sectorPublicKey));
		byte[] apduBytes = Utils.concatByteArrays(HexString.toByteArray("00 86 00 00"),
				new byte[] { (byte) data.getLength() }, data.toByteArray(), new byte[] { 0 });
		processingData.updateCommandApdu(this, "GeneralAuthenticate APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// set needed protocol state
		psaProtocol.psDomainParameters = domainParameters12;
		psaProtocol.publicKeyGroupManager = (ECPublicKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePublic(domainParameters12.getPublicKeySpec(groupManagerPublicKey12.getW()));
		psaProtocol.psOid = Psa.id_PSA_ECDH_ECSchnorr_SHA_256;
		psaProtocol.ps1AuthInfo = 2;
		psaProtocol.privateKeyIcc1 = (ECPrivateKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePrivate(domainParameters12.getPrivateKeySpec(secretKeyIcc1PrivateKey12));
		psaProtocol.privateKeyIcc2 = (ECPrivateKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider())
				.generatePrivate(domainParameters12.getPrivateKeySpec(secretKeyIcc2PrivateKey12));
		psaProtocol.secConditonForComputingSectorIcc1 = secConditionForAccessingIccXNoExplicitAuth;
		psaProtocol.secConditonForComputingSectorIcc2 = secConditionForAccessingIccXNoExplicitAuth;

		psaProtocol.process(processingData);
		short sw = processingData.getResponseApdu().getStatusWord();
		assertEquals("Statusword is not 9000", PlatformUtil.SW_4982_SECURITY_STATUS_NOT_SATISFIED, sw);
	}

	/**
	 * Positive test case: perform General Authenticate command for PSA using one
	 * sector identifier.
	 */
	@Test
	public void test_SetAtGeneralAuthenticate() {
		updateSecStatusCa3();
		updateSecStatusTaWithExtension(true);

		ProcessingData processingData = new ProcessingData();
		byte[] firstApduBytes = HexString.toByteArray("00 22 41 A4 10 80 0B 04 00 7F 00 07 02 02 0B 01 02 03 84 01 2A");
		processingData.updateCommandApdu(this, "setAT APDU", CommandApduFactory.createCommandApdu(firstApduBytes));

		psaProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR,
				processingData.getResponseApdu().getStatusWord());

		processingData = new ProcessingData();
		byte[] sectorPublicKey = CryptoUtil.encode(sectorPublicKey12,
				domainParameters12.getPublicPointReferenceLengthL(), CryptoUtil.ENCODING_UNCOMPRESSED);
		ConstructedTlvDataObject data = new ConstructedTlvDataObject(TlvConstants.TAG_7C,
				new PrimitiveTlvDataObject(TlvConstants.TAG_80, sectorPublicKey));
		byte[] secondApduBytes = Utils.concatByteArrays(HexString.toByteArray("00 86 00 00"),
				new byte[] { (byte) data.getLength() }, data.toByteArray(), new byte[] { 0 });
		processingData.updateCommandApdu(this, "GeneralAuthenticate APDU",
				CommandApduFactory.createCommandApdu(secondApduBytes));

		psaProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR,
				processingData.getResponseApdu().getStatusWord());
		assertNotNull(((TlvDataObjectContainer) processingData.getResponseApdu().getData())
				.getTlvDataObject(new TlvPath(TlvConstants.TAG_7C, TlvConstants.TAG_82)));
		assertNull(((TlvDataObjectContainer) processingData.getResponseApdu().getData())
				.getTlvDataObject(new TlvPath(TlvConstants.TAG_7C, TlvConstants.TAG_83)));
		assertNotNull(((TlvDataObjectContainer) processingData.getResponseApdu().getData())
				.getTlvDataObject(new TlvPath(TlvConstants.TAG_7C, TlvConstants.TAG_84)));
	}
}
