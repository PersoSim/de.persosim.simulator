package de.persosim.simulator.protocols;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.util.Arrays;
import java.util.Collections;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.DomainParameterSetCardObject;
import de.persosim.simulator.cardobjects.DomainParameterSetIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.protocols.pace.Pace;
import de.persosim.simulator.protocols.pace.PaceOid;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class TR03110Test extends PersoSimTestCase {

	ConstructedTlvDataObject publicKeyDataEcNoDomainParameters;
	ConstructedTlvDataObject publicKeyDataEc;
	ECPublicKey publicKeyEc;
	@Mocked
	CardStateAccessor mockedCardStateAccessor;
	@Mocked
	MasterFile mf;
	Oid oid1, oid2;
	OidIdentifier oidIdentifier1, oidIdentifier2;
	DomainParameterSetCardObject domainParameters12, domainParameters13;
	DomainParameterSetIdentifier domainparameterSetIdentifier12, domainparameterSetIdentifier13;

	@Before
	public void setUp() throws Exception{

		// build EC public key 
		
		byte [] modulusRaw = HexString.toByteArray("A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377");
		byte [] firstCoefficientRaw = HexString.toByteArray("7D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9");
		byte [] secondCoefficientRaw = HexString.toByteArray("26DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B6");
		byte [] basePointRaw = HexString.toByteArray("048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997");
		byte [] orderOfBasePointRaw = HexString.toByteArray("A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7");
		byte [] cofactorRaw = HexString.toByteArray("01");
		byte [] publicPointRaw = HexString.toByteArray("0405AB6A1DDF4C611C1BB363A0BBC0E307EC1C03EA90CF4B7A51DC6798119D75173670D740FABA4E497EBBB01A20EA14D5A423FE7A43FB954A4A0173F238036478");
		
		ECField field = new ECFieldFp(new BigInteger(1, modulusRaw));				
		EllipticCurve curve = new EllipticCurve(field, new BigInteger(1, firstCoefficientRaw), new BigInteger(1, secondCoefficientRaw));
		ECPoint basePoint = DomainParameterSetEcdh.reconstructPoint(basePointRaw);
		ECParameterSpec paramSpec = new ECParameterSpec(curve, basePoint, new BigInteger(1, orderOfBasePointRaw), Utils.getIntFromUnsignedByteArray(cofactorRaw));
		ECPublicKeySpec keySpec = new ECPublicKeySpec(DomainParameterSetEcdh.reconstructPoint(publicPointRaw), paramSpec);
		publicKeyEc = (ECPublicKey) KeyFactory.getInstance("EC", Crypto.getCryptoProvider()).generatePublic(keySpec);
		
		// with domain parameters

		TlvDataObject modulusData = new PrimitiveTlvDataObject(TR03110.TAG_81, modulusRaw);
		TlvDataObject firstCoefficientData = new PrimitiveTlvDataObject(TR03110.TAG_82, firstCoefficientRaw);
		TlvDataObject secondCoefficientData = new PrimitiveTlvDataObject(TR03110.TAG_83, secondCoefficientRaw);
		TlvDataObject basePointData = new PrimitiveTlvDataObject(TR03110.TAG_84, basePointRaw);
		TlvDataObject orderOfBasePointData = new PrimitiveTlvDataObject(TR03110.TAG_85, orderOfBasePointRaw);
		TlvDataObject publicPointData = new PrimitiveTlvDataObject(TR03110.TAG_86, publicPointRaw);
		TlvDataObject cofactorData = new PrimitiveTlvDataObject(TR03110.TAG_87, cofactorRaw);

		publicKeyDataEc = new ConstructedTlvDataObject(TR03110.TAG_7F49);
		publicKeyDataEc.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_06, TaOid.id_TA_ECDSA_SHA_1.toByteArray()));
		publicKeyDataEc.addTlvDataObject(modulusData);
		publicKeyDataEc.addTlvDataObject(firstCoefficientData);
		publicKeyDataEc.addTlvDataObject(secondCoefficientData);
		publicKeyDataEc.addTlvDataObject(basePointData);
		publicKeyDataEc.addTlvDataObject(orderOfBasePointData);
		publicKeyDataEc.addTlvDataObject(publicPointData);
		publicKeyDataEc.addTlvDataObject(cofactorData);

		// without domain parameters
		
		publicKeyDataEcNoDomainParameters = new ConstructedTlvDataObject(TR03110.TAG_7F49);
		publicKeyDataEcNoDomainParameters.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_06, TaOid.id_TA_ECDSA_SHA_1.toByteArray()));
		publicKeyDataEcNoDomainParameters.addTlvDataObject(publicPointData);
		
		// define domain parameters
		oid1 = Pace.OID_id_PACE_ECDH_GM_AES_CBC_CMAC_256;
		oidIdentifier1 = new OidIdentifier(oid1);
		oid2 = Pace.OID_id_PACE_DH_IM_3DES_CBC_CBC;
		oidIdentifier2 = new OidIdentifier(oid2);
		
		domainparameterSetIdentifier12 = new DomainParameterSetIdentifier(12);
		domainParameters12 = new DomainParameterSetCardObject(StandardizedDomainParameters.getDomainParameterSetById(12), domainparameterSetIdentifier12);
		domainParameters12.addOidIdentifier(oidIdentifier1);
		
		domainparameterSetIdentifier13 = new DomainParameterSetIdentifier(13);
		domainParameters13 = new DomainParameterSetCardObject(StandardizedDomainParameters.getDomainParameterSetById(13), domainparameterSetIdentifier13);
		domainParameters13.addOidIdentifier(oidIdentifier1);
	}
	
	/**
	 * Positive test: parses a public key and is expected to use domain
	 * parameters from trust point key
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParsePublicKeyEcUsingTrustPointKey() throws Exception {
		//call mut
		assertArrayEquals(publicKeyEc.getEncoded(), TR03110.parseCertificatePublicKey(publicKeyDataEcNoDomainParameters, publicKeyEc).getEncoded());
	}
	
	/**
	 * Positive test: parses a public key without a referenced trust point key,
	 * thus uses domain parameters from provided public key data.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParsePublicKeyEcNoTrustPointKey() throws Exception {
		//call mut
		assertArrayEquals(publicKeyEc.getEncoded(), TR03110.parseCertificatePublicKey(publicKeyDataEc, null).getEncoded());
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
		TlvDataObjectContainer tokenReceived = TR03110.buildAuthenticationTokenInput(ecdhPublicKeyPicc, domainParametersEcdh, oid);
		byte[] tokenReceivedPlain = tokenReceived.toByteArray();
		
		assertArrayEquals("token mismatch", tokenExpected, tokenReceivedPlain);
	}
	
	/**
	 * Positive test: check that the combination of OID and id-able object returns the correct object.
	 */
	@Test
	public void testGetSpecificChild_MatchingSingleElement() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mf.findChildren(
						withInstanceOf(OidIdentifier.class),
						withInstanceOf(DomainParameterSetIdentifier.class)
						);
				result = Arrays.asList(domainParameters12);
			}
		};
				
		assertEquals(domainParameters12, TR03110.getSpecificChild(mf, oidIdentifier2, domainparameterSetIdentifier12));
	}
	
	/**
	 * Negative test: check that the combination of OID and id-able object is not allowed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetSpecificChild_NonMatching_SecondaryIdentifier() {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mf.findChildren(
						withInstanceOf(OidIdentifier.class),
						withInstanceOf(DomainParameterSetIdentifier.class)
						);
				result = Collections.EMPTY_SET;
			}
		};
				
		TR03110.getSpecificChild(mf, oidIdentifier2, domainparameterSetIdentifier12);
	}
	
	/**
	 * Negative test: check that an {@link IllegalArgumentException} is thrown when the selection is  ambiguous.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetImplicitId_Ambiguous() {
		// prepare the mock
				new NonStrictExpectations() {
					{
						mf.findChildren(
								withInstanceOf(OidIdentifier.class),
								withInstanceOf(DomainParameterSetIdentifier.class)
								);
						result = Arrays.asList(domainParameters12, domainParameters13);
					}
				};
				
		TR03110.getSpecificChild(mf, oidIdentifier2, domainparameterSetIdentifier12);
	}
	
}
