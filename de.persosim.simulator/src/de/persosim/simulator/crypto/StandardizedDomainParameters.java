package de.persosim.simulator.crypto;

import java.math.BigInteger;
import java.security.spec.ECParameterSpec;

import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;

/**
 * This class provides static access to PACE standardized domain parameters.
 * 
 * @author slutters
 *
 */
public class StandardizedDomainParameters {
	public static final int NO_OF_STANDARDIZED_DOMAIN_PARAMETERS = 32;
	
	// START DH
	static final BigInteger MODP_1024_160_PRIME = new BigInteger("B10B8F96A080E01DDE92DE5EAE5D54EC52C99FBCFB06A3C69A6A9DCA52D23B616073E28675A23D189838EF1E2EE652C013ECB4AEA906112324975C3CD49B83BFACCBDD7D90C4BD7098488E9C219A73724EFFD6FAE5644738FAA31A4FF55BCCC0A151AF5F0DC8B4BD45BF37DF365C1A65E68CFDA76D4DA708DF1FB2BC2E4A4371", 16);
	static final BigInteger MODP_1024_160_GENERATOR = new BigInteger("A4D1CBD5C3FD34126765A442EFB99905F8104DD258AC507FD6406CFF14266D31266FEA1E5C41564B777E690F5504F213160217B4B01B886A5E91547F9E2749F4D7FBD7D3B9A92EE1909D0D2263F80A76A6A24C087A091F531DBF0A0169B6A28AD662A4D18E73AFA32D779D5918D08BC8858F4DCEF97C2A24855E6EEB22B3B2E5",16);
	static final BigInteger MODP_1024_160_ORDER = new BigInteger("F518AA8781A8DF278ABA4E7D64B7CB9D49462353",16);

	static final BigInteger MODP_2048_224_PRIME = new BigInteger("AD107E1E9123A9D0D660FAA79559C51FA20D64E5683B9FD1B54B1597B61D0A75E6FA141DF95A56DBAF9A3C407BA1DF15EB3D688A309C180E1DE6B85A1274A0A66D3F8152AD6AC2129037C9EDEFDA4DF8D91E8FEF55B7394B7AD5B7D0B6C12207C9F98D11ED34DBF6C6BA0B2C8BBC27BE6A00E0A0B9C49708B3BF8A317091883681286130BC8985DB1602E714415D9330278273C7DE31EFDC7310F7121FD5A07415987D9ADC0A486DCDF93ACC44328387315D75E198C641A480CD86A1B9E587E8BE60E69CC928B2B9C52172E413042E9B23F10B0E16E79763C9B53DCF4BA80A29E3FB73C16B8E75B97EF363E2FFA31F71CF9DE5384E71B81C0AC4DFFE0C10E64F", 16);
	static final BigInteger MODP_2048_224_GENERATOR = new BigInteger("AC4032EF4F2D9AE39DF30B5C8FFDAC506CDEBE7B89998CAF74866A08CFE4FFE3A6824A4E10B9A6F0DD921F01A70C4AFAAB739D7700C29F52C57DB17C620A8652BE5E9001A8D66AD7C17669101999024AF4D027275AC1348BB8A762D0521BC98AE247150422EA1ED409939D54DA7460CDB5F6C6B250717CBEF180EB34118E98D119529A45D6F834566E3025E316A330EFBB77A86F0C1AB15B051AE3D428C8F8ACB70A8137150B8EEB10E183EDD19963DDD9E263E4770589EF6AA21E7F5F2FF381B539CCE3409D13CD566AFBB48D6C019181E1BCFE94B30269EDFE72FE9B6AA4BD7B5A0F1C71CFFF4C19C418E1F6EC017981BC087F2A7065B384B890D3191F2BFA",16);
	static final BigInteger MODP_2048_224_ORDER = new BigInteger("801C0D34C58D93FE997177101F80535A4738CEBCBF389A99B36371EB", 16);
	
	static final BigInteger MODP_2048_256_PRIME = new BigInteger("87A8E61DB4B6663CFFBBD19C651959998CEEF608660DD0F25D2CEED4435E3B00E00DF8F1D61957D4FAF7DF4561B2AA3016C3D91134096FAA3BF4296D830E9A7C209E0C6497517ABD5A8A9D306BCF67ED91F9E6725B4758C022E0B1EF4275BF7B6C5BFC11D45F9088B941F54EB1E59BB8BC39A0BF12307F5C4FDB70C581B23F76B63ACAE1CAA6B7902D52526735488A0EF13C6D9A51BFA4AB3AD8347796524D8EF6A167B5A41825D967E144E5140564251CCACB83E6B486F6B3CA3F7971506026C0B857F689962856DED4010ABD0BE621C3A3960A54E710C375F26375D7014103A4B54330C198AF126116D2276E11715F693877FAD7EF09CADB094AE91E1A1597", 16);
	static final BigInteger MODP_2048_256_GENERATOR = new BigInteger("3FB32C9B73134D0B2E77506660EDBD484CA7B18F21EF205407F4793A1A0BA12510DBC15077BE463FFF4FED4AAC0BB555BE3A6C1B0C6B47B1BC3773BF7E8C6F62901228F8C28CBB18A55AE31341000A650196F931C77A57F2DDF463E5E9EC144B777DE62AAAB8A8628AC376D282D6ED3864E67982428EBC831D14348F6F2F9193B5045AF2767164E1DFC967C1FB3F2E55A4BD1BFFE83B9C80D052B985D182EA0ADB2A3B7313D3FE14C8484B1E052588B9B7D2BBD2DF016199ECD06E1557CD0915B3353BBB64E0EC377FD028370DF92B52C7891428CDC67EB6184B523D1DB246C32F63078490F00EF8D647D148D47954515E2327CFEF98C582664B4C0F6CC41659",16);
	static final BigInteger MODP_2048_256_ORDER = new BigInteger("8CF83642A709A097B447997640129DA299B1A47D1EB3750BA308B0FE64F5FBD3",16);
	// END DH
	
	// START ECDH
	// TODO define ECDH parameters here as part of replacement of BC dependency
	// END DH
	
	/*--------------------------------------------------------------------------------*/
	
	static public DomainParameterSet getDomainParameterSetById(int id){
		switch (id){
		case 0:  // fallthrough
		case 1:  // fallthrough
		case 2:  // fallthrough
		case 3:  // fallthrough
		case 4:  // fallthrough
		case 5:  // fallthrough
		case 6:  // fallthrough
		case 7:
			return null; //IMPL add missing standardized domain parameters
		case 8:
			return convertBcnamedCurveToDomainParameterSet(NISTNamedCurves.getByName("P-192"));
		case 9:
			return convertBcnamedCurveToDomainParameterSet(TeleTrusTNamedCurves.getByName("brainpoolp192r1"));
		case 10:
			return convertBcnamedCurveToDomainParameterSet(NISTNamedCurves.getByName("P-224"));
		case 11:
			return convertBcnamedCurveToDomainParameterSet(TeleTrusTNamedCurves.getByName("brainpoolp224r1"));
		case 12:
			return convertBcnamedCurveToDomainParameterSet(NISTNamedCurves.getByName("P-256"));
		case 13:
			return convertBcnamedCurveToDomainParameterSet(TeleTrusTNamedCurves.getByName("brainpoolp256r1"));
		case 14:
			return convertBcnamedCurveToDomainParameterSet(TeleTrusTNamedCurves.getByName("brainpoolp320r1"));
		case 15:
			return convertBcnamedCurveToDomainParameterSet(NISTNamedCurves.getByName("P-384"));
		case 16:
			return convertBcnamedCurveToDomainParameterSet(TeleTrusTNamedCurves.getByName("brainpoolp384r1"));
		case 17:
			return convertBcnamedCurveToDomainParameterSet(TeleTrusTNamedCurves.getByName("brainpoolp512r1"));
		case 18:
			return convertBcnamedCurveToDomainParameterSet(NISTNamedCurves.getByName("P-521"));
		case 19:  // fallthrough
		case 20:  // fallthrough
		case 21:  // fallthrough
		case 22:  // fallthrough
		case 23:  // fallthrough
		case 24:  // fallthrough
		case 25:  // fallthrough
		case 26:  // fallthrough
		case 27:  // fallthrough
		case 28:  // fallthrough
		case 29:  // fallthrough
		case 30:  // fallthrough
		case 31:
			return null;
		default:
			throw new IllegalArgumentException("id for standardized domain parameters must be > 0 and < " + NO_OF_STANDARDIZED_DOMAIN_PARAMETERS);
		}
	}
	
	/**
	 * This method converts Bouncy Castle named EC curves to objects usable with standard Java crypto API.
	 * @param namedCurve a Bouncy Castle named EC curve
	 * @return EC curve parameter specification usable with standard Java crypto API
	 */
	static private ECParameterSpec convertBcNamedCurve(X9ECParameters namedCurve) {
		/* import org.bouncycastle.jce.spec.ECParameterSpec; */
		org.bouncycastle.jce.spec.ECParameterSpec bcECParameterSpec = new org.bouncycastle.jce.spec.ECParameterSpec(namedCurve.getCurve(), namedCurve.getG(), namedCurve.getN(), namedCurve.getH());
		
		/* import java.security.spec.ECParameterSpec; */
		/* 
		 * byte[] seed is set to null for the following reasons
		 * 1) the information is not available for the used named curves
		 * 2) seed data is not necessarily needed and the constructor
		 * EllipticCurve(ECField field, BigInteger a, BigInteger b)
		 * just calls this(field, a, b, null); itself
		 */
		return EC5Util.convertSpec(EC5Util.convertCurve(namedCurve.getCurve(), null), bcECParameterSpec);
	}
	
	/**
	 * This method converts EC parameter specifications to EC domain parameter sets.
	 * @param ecParameterSpec the EC parameter specification
	 * @return the corresponding EC domain parameter set
	 */
	static private DomainParameterSetEcdh convertEcParameterSpec(ECParameterSpec ecParameterSpec){
		return new DomainParameterSetEcdh(ecParameterSpec.getCurve(), ecParameterSpec.getGenerator(), ecParameterSpec.getOrder(), ecParameterSpec.getCofactor());
	}
	
	/**
	 * This method converts Bouncy Castle named EC curves to EC domain parameter sets.
	 * @param namedCurve the Bouncy Castle named EC curve
	 * @return the corresponding EC domain parameter set
	 */
	static private DomainParameterSetEcdh convertBcnamedCurveToDomainParameterSet(X9ECParameters namedCurve) {
		ECParameterSpec ecParameterSpec = convertBcNamedCurve(namedCurve);
		return convertEcParameterSpec(ecParameterSpec);
	}
	
}
