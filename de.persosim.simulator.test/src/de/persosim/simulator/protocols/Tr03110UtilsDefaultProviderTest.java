package de.persosim.simulator.protocols;

import static org.junit.Assert.assertArrayEquals;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;

import org.globaltester.cryptoprovider.Crypto;
import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.certificates.CvEcPublicKey;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class Tr03110UtilsDefaultProviderTest extends PersoSimTestCase {


	ConstructedTlvDataObject publicKeyDataEcNoDomainParameters;
	ConstructedTlvDataObject publicKeyDataEc;
	ECPublicKey publicKeyEc;
	
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

		TlvDataObject modulusData = new PrimitiveTlvDataObject(Tr03110Utils.TAG_81, modulusRaw);
		TlvDataObject firstCoefficientData = new PrimitiveTlvDataObject(Tr03110Utils.TAG_82, firstCoefficientRaw);
		TlvDataObject secondCoefficientData = new PrimitiveTlvDataObject(Tr03110Utils.TAG_83, secondCoefficientRaw);
		TlvDataObject basePointData = new PrimitiveTlvDataObject(Tr03110Utils.TAG_84, basePointRaw);
		TlvDataObject orderOfBasePointData = new PrimitiveTlvDataObject(Tr03110Utils.TAG_85, orderOfBasePointRaw);
		TlvDataObject publicPointData = new PrimitiveTlvDataObject(Tr03110Utils.TAG_86, publicPointRaw);
		TlvDataObject cofactorData = new PrimitiveTlvDataObject(Tr03110Utils.TAG_87, cofactorRaw);

		publicKeyDataEc = new ConstructedTlvDataObject(Tr03110Utils.TAG_7F49);
		publicKeyDataEc.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_06, TaOid.id_TA_ECDSA_SHA_1.toByteArray()));
		publicKeyDataEc.addTlvDataObject(modulusData);
		publicKeyDataEc.addTlvDataObject(firstCoefficientData);
		publicKeyDataEc.addTlvDataObject(secondCoefficientData);
		publicKeyDataEc.addTlvDataObject(basePointData);
		publicKeyDataEc.addTlvDataObject(orderOfBasePointData);
		publicKeyDataEc.addTlvDataObject(publicPointData);
		publicKeyDataEc.addTlvDataObject(cofactorData);

		// without domain parameters
		
		publicKeyDataEcNoDomainParameters = new ConstructedTlvDataObject(Tr03110Utils.TAG_7F49);
		publicKeyDataEcNoDomainParameters.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_06, TaOid.id_TA_ECDSA_SHA_1.toByteArray()));
		publicKeyDataEcNoDomainParameters.addTlvDataObject(publicPointData);
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
		CvEcPublicKey parseCvPublicKey = (CvEcPublicKey) new Tr03110UtilsDefaultProvider().parseCvPublicKey(publicKeyDataEcNoDomainParameters);
		parseCvPublicKey.updateKey(publicKeyEc);
		byte[] pubKey = parseCvPublicKey.getEncoded();
		assertArrayEquals(publicKeyEc.getEncoded(), pubKey);
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
		assertArrayEquals(publicKeyEc.getEncoded(), ((ECPublicKey) new Tr03110UtilsDefaultProvider().parseCvPublicKey(publicKeyDataEc)).getEncoded());
	}
}
