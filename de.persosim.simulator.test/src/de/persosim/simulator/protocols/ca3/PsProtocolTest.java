package de.persosim.simulator.protocols.ca3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.security.interfaces.ECPublicKey;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.ca3.PsAuthInfo.PsAuthInfoValue;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvTagIdentifier;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class PsProtocolTest extends PersoSimTestCase implements TlvConstants {

	PsAuthInfoValue ps1AuthInfo;
	PsAuthInfoValue ps2AuthInfo;
	DomainParameterSetEcdh domParams13;
	ECPublicKey pubKey;
	ECPublicKey pubKeyGroupManager;
	KeyIdentifier keyIdentifier;
	KeyObjectIcc keyObjectIcc;
	MasterFile mf;
	SecStatus secStatus;
	CardStateAccessor cardState;
	
	@Before
	public void setUp() throws AccessDeniedException {
		secStatus = new SecStatus();

		mf = new MasterFile();
		mf.setSecStatus(secStatus);
		
		cardState = new CardStateAccessor(){
			@Override
			public MasterFile getMasterFile() {
				return mf;
			}

			@Override
			public Collection<SecMechanism> getCurrentMechanisms(SecContext context,
					Collection<Class<? extends SecMechanism>> wantedMechanisms) {
				return secStatus.getCurrentMechanisms(context, wantedMechanisms);
			}
		};
		
		ps1AuthInfo = PsAuthInfoValue.NO_EXPLICIT_AUTHORISATION;
		ps2AuthInfo = PsAuthInfoValue.NO_EXPLICIT_AUTHORISATION;
		domParams13 = (DomainParameterSetEcdh) StandardizedDomainParameters
				.getDomainParameterSetById(13);
		pubKey = domParams13.reconstructPublicKey(HexString.toByteArray(
				"04 A4 4E BE 54 51 DF 7A AD B0 1E 45 9B 8C 92 8A 87 74 6A 57 92 7C 8C 28 A6 77 5C 97 A7 E1 FE 8D 9A 46 FF 4A 1C C7 E4 D1 38 9A EA 19 75 8E 4F 75 C2 8C 59 8F D7 34 AE BE B1 35 33 7C F9 5B E1 2E 94"));
		keyIdentifier = new KeyIdentifier((byte) 123);
		

		pubKeyGroupManager = domParams13.reconstructPublicKey(HexString.toByteArray(
				"04 5F 64 11 CA 64 ED 6F 59 C4 25 24 A4 1A EE 4B B2 1F 15 2A 3A DC 63 43 6C EE 59 F4 46 0E 46 10 8F 61 1D C9 05 40 54 B8 B4 B6 50 D0 36 38 75 A9 BC 39 39 EB 77 70 5E B4 18 26 37 0C F2 2F 71 39 8E"));

		keyObjectIcc = new KeyObjectIcc(null, null, pubKey, pubKeyGroupManager, domParams13, keyIdentifier);
	}
	
	@Test
	public void testGetSecInfosPsa() throws AccessDeniedException{
		keyObjectIcc.addOidIdentifier(Psa.OID_IDENTIFIER_id_PSA_ECDH_ECSchnorr_SHA_256);
		mf.addChild(keyObjectIcc);
		mf.addChild(new PsAuthInfo(new OidIdentifier(Psa.id_PSA), ps1AuthInfo, ps2AuthInfo));
		PsaProtocol protocol = new PsaProtocol();
		protocol.setCardStateAccessor(cardState);
		testGetSecInfos(protocol, Psa.OID_IDENTIFIER_id_PSA_ECDH_ECSchnorr_SHA_256, SecInfoPublicity.PUBLIC);
		testGetSecInfos(protocol, Psa.OID_IDENTIFIER_id_PSA_ECDH_ECSchnorr_SHA_256, SecInfoPublicity.AUTHENTICATED);
		testGetSecInfos(protocol, Psa.OID_IDENTIFIER_id_PSA_ECDH_ECSchnorr_SHA_256, SecInfoPublicity.PRIVILEGED);
	}

	public void testGetSecInfos(PsProtocol protocol, OidIdentifier psOid, SecInfoPublicity publicity) {
		HashSet<TlvDataObject> secInfos = protocol.getSecInfos(publicity, mf);
		Iterator<TlvDataObject> iterator = secInfos.iterator();

		TlvDataObject psInfo = iterator.next();
		if (psInfo instanceof ConstructedTlvDataObject) {
			ConstructedTlvDataObject psInfoConstructed = (ConstructedTlvDataObject) psInfo;

			PsOid oid = getPsOid(protocol, psInfoConstructed.getTlvDataObject(TAG_OID).getValueField());

			assertNotEquals("PS oid is null", null, oid);
			assertEquals("PS oid does not match", psOid.getOid(), oid);

			ConstructedTlvDataObject requiredData = (ConstructedTlvDataObject) psInfoConstructed
					.getTlvDataObject(TAG_SEQUENCE);
			byte[] version = requiredData.getTlvDataObject(new TlvTagIdentifier(TAG_INTEGER, 0)).getValueField();
			assertArrayEquals("version must be 1", new byte[] { 1 }, version);
			byte[] foundPs1AuthInfo = requiredData.getTlvDataObject(new TlvTagIdentifier(TAG_INTEGER, 1))
					.getValueField();
			assertArrayEquals("ps1-authInfo is not legal", new byte[] { ps1AuthInfo.toValue() }, foundPs1AuthInfo);
			byte[] foundPs2AuthInfo = requiredData.getTlvDataObject(new TlvTagIdentifier(TAG_INTEGER, 2))
					.getValueField();
			assertArrayEquals("ps2-authInfo is not legal", new byte[] { ps2AuthInfo.toValue() }, foundPs2AuthInfo);

			TlvDataObject keyId = psInfoConstructed.getTlvDataObject(TAG_INTEGER);
			assertArrayEquals("keyId is not valid", new byte[] { (byte) keyIdentifier.getKeyReference() },
					keyId.getValueField());
		}

		if (iterator.hasNext()) {
			assertTrue("Wrong publicity level",
					publicity.equals(SecInfoPublicity.AUTHENTICATED) || publicity.equals(SecInfoPublicity.PRIVILEGED));
			TlvDataObject psPublicKeyInfo = iterator.next();
			if (psPublicKeyInfo instanceof ConstructedTlvDataObject) {
				ConstructedTlvDataObject psPublicKeyInfoConstructed = (ConstructedTlvDataObject) psPublicKeyInfo;

				// protocol
				byte[] oid = psPublicKeyInfoConstructed.getTlvDataObject(TAG_OID).getValueField();
				assertArrayEquals("protocol must be id-PS-PK-ECDH-ECSchnorr", Ps.id_PS_PK_ECDH_ECSchnorr.toByteArray(), oid);

				// requiredData
				TlvDataObject requiredData = psPublicKeyInfoConstructed.getTlvDataObject(new TlvTagIdentifier(TAG_SEQUENCE, 0));
				TlvDataObject pSPublicKey = ((ConstructedTlvDataObject) requiredData).getTlvDataObject(TAG_SEQUENCE);

				TlvDataObject algorithmIdentifier = ((ConstructedTlvDataObject) pSPublicKey)
						.getTlvDataObject(TAG_SEQUENCE);

				TlvDataObject algorithm = ((ConstructedTlvDataObject) algorithmIdentifier).getTlvDataObject(TAG_OID);
				assertArrayEquals("id must be id_EC_PSPUBLIC_KEY", Ps.id_EC_PSPUBLIC_KEY.toByteArray(), algorithm.getValueField());
				TlvDataObject algorithmParameters = ((ConstructedTlvDataObject) algorithmIdentifier)
						.getTlvDataObject(TAG_SEQUENCE);
				TlvDataObject parametersAlgorithmIdentifier = ((ConstructedTlvDataObject) algorithmParameters)
						.getTlvDataObject(TAG_SEQUENCE);
				assertArrayEquals("parameters are not valid",
						keyObjectIcc.getPsDomainParameters().getAlgorithmIdentifier().getValueField(),
						parametersAlgorithmIdentifier.getValueField());
				TlvDataObject groupManager = ((ConstructedTlvDataObject) algorithmParameters)
						.getTlvDataObject(TAG_OCTET_STRING);
				byte[] ecPoint = CryptoUtil.encode(pubKeyGroupManager.getW(), domParams13.getPublicPointReferenceLengthL(),
						CryptoUtil.ENCODING_UNCOMPRESSED);
				assertArrayEquals("group manager is incorrect", ecPoint, groupManager.getValueField());

				TlvDataObject subjectPublicKey = ((ConstructedTlvDataObject) pSPublicKey)
						.getTlvDataObject(TAG_BIT_STRING);
				ecPoint = CryptoUtil.encode(pubKey.getW(), domParams13.getPublicPointReferenceLengthL(),
						CryptoUtil.ENCODING_UNCOMPRESSED);
				byte[] expectedBitStringValue = Utils.concatByteArrays(new byte[] {0}, ecPoint);
				assertArrayEquals("encoded public key is wrong", expectedBitStringValue, subjectPublicKey.getValueField());

				// optionalData
				TlvDataObject optionalData = psPublicKeyInfoConstructed.getTlvDataObject(new TlvTagIdentifier(TAG_SEQUENCE, 1));
				if (optionalData != null) {
					if (psInfo instanceof ConstructedTlvDataObject) {
						ConstructedTlvDataObject optionalDataConstructed = (ConstructedTlvDataObject) optionalData;

						TlvDataObject psParameterID = optionalDataConstructed.getTlvDataObject(TAG_81);
						Integer algorithmId = StandardizedDomainParameters
								.getDomainParameterSetId(domParams13.getAlgorithmIdentifier());
						assertArrayEquals("pSParameterId is wrong", new byte[] { algorithmId.byteValue() },
								psParameterID.getValueField());

						TlvDataObject keyId = optionalDataConstructed.getTlvDataObject(TAG_82);
						assertArrayEquals("keyId does not match", new byte[] {(byte) keyIdentifier.getKeyReference()}, keyId.getValueField());
					}
				}
			}
		}

	}

	protected PsOid getPsOid(PsProtocol protocol, byte[] valueField) {
		if (protocol instanceof PsaProtocol) {
			return new PsaOid(valueField);
		}
		throw new RuntimeException("No matching PsOid found");
	}
}
