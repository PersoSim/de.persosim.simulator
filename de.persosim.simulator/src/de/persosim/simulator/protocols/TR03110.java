package de.persosim.simulator.protocols;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.ietf.jgss.GSSException;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.exception.NotParseableException;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.Utils;

/**
 * XXX MBK replace TaOid with OID according to our own OID class hierarchy 
 * 
 * This class contains methods and constants unique to the TR-03110 specification.
 * @author mboonk
 *
 */
public class TR03110 {
	public static final int ACCESS_RIGHTS_AT_CAN_ALLOWED_BIT = 4;
	
	// data objects

	public static final TlvTag TAG_06 = new TlvTag(new byte []{0x06});
	public static final TlvTag TAG_42 = new TlvTag(new byte []{0x42});
	public static final TlvTag TAG_53 = new TlvTag(new byte []{0x53});
	public static final TlvTag TAG_65 = new TlvTag(new byte []{0x65});
	public static final TlvTag TAG_67 = new TlvTag(new byte []{0x67});
	public static final TlvTag TAG_73 = new TlvTag(new byte []{0x73});
	public static final TlvTag TAG_80 = new TlvTag(new byte []{(byte) 0x80});
	public static final TlvTag TAG_81 = new TlvTag(new byte []{(byte) 0x81});
	public static final TlvTag TAG_82 = new TlvTag(new byte []{(byte) 0x82});
	public static final TlvTag TAG_83 = new TlvTag(new byte []{(byte) 0x83});
	public static final TlvTag TAG_84 = new TlvTag(new byte []{(byte) 0x84});
	public static final TlvTag TAG_85 = new TlvTag(new byte []{(byte) 0x85});
	public static final TlvTag TAG_86 = new TlvTag(new byte []{(byte) 0x86});
	public static final TlvTag TAG_87 = new TlvTag(new byte []{(byte) 0x87});
	public static final TlvTag TAG_91 = new TlvTag(new byte []{(byte) 0x91});
	public static final TlvTag TAG_5F20 = new TlvTag(new byte []{0x5F, 0x20});
	public static final TlvTag TAG_5F24 = new TlvTag(new byte []{0x5F, 0x24});
	public static final TlvTag TAG_5F25 = new TlvTag(new byte []{0x5F, 0x25});
	public static final TlvTag TAG_5F29 = new TlvTag(new byte []{0x5F, 0x29});
	public static final TlvTag TAG_5F37 = new TlvTag(new byte []{0x5F, 0x37});
	public static final TlvTag TAG_7F21 = new TlvTag(new byte []{0x7F, 0x21});
	public static final TlvTag TAG_7F49 = new TlvTag(new byte []{0x7F, 0x49});
	public static final TlvTag TAG_7F4C = new TlvTag(new byte []{0x7F, 0x4C});
	public static final TlvTag TAG_7F4E = new TlvTag(new byte []{0x7F, 0x4E});

	/**
	 * This method finds a signature object according to OIDs as defined in
	 * TR-03110 v2.10.
	 * 
	 * @param oid
	 *            to search for
	 * @return an instance of a {@link Signature} object
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public static Signature getSignatureForOid(TaOid oid) throws NoSuchAlgorithmException, NoSuchProviderException{
		if (oid.equals(TaOid.id_TA_RSA_v1_5_SHA_1)){
			return Signature.getInstance("SHA1withRSA", Crypto.getCryptoProvider());
		} else if (oid.equals(TaOid.id_TA_RSA_v1_5_SHA_256)){
			return Signature.getInstance("SHA256withRSA", Crypto.getCryptoProvider());
		} else if (oid.equals(TaOid.id_TA_RSA_v1_5_SHA_512)){
			return Signature.getInstance("SHA512withRSA", Crypto.getCryptoProvider());
		} else if (oid.equals(TaOid.id_TA_RSA_PSS_SHA_1)){
			return Signature.getInstance("SHA1withRSA/PSS", Crypto.getCryptoProvider());
		} else if (oid.equals(TaOid.id_TA_RSA_PSS_SHA_256)){
			return Signature.getInstance("SHA256withRSA/PSS", Crypto.getCryptoProvider());
		} else if (oid.equals(TaOid.id_TA_RSA_PSS_SHA_512)){
			return Signature.getInstance("SHA512withRSA/PSS", Crypto.getCryptoProvider());
		} else if (oid.equals(TaOid.id_TA_ECDSA_SHA_1)){
			return Signature.getInstance("SHA1withECDSA", Crypto.getCryptoProvider());
		} else if (oid.equals(TaOid.id_TA_ECDSA_SHA_224)){
			return Signature.getInstance("SHA224withECDSA", Crypto.getCryptoProvider());
		} else if (oid.equals(TaOid.id_TA_ECDSA_SHA_256)){
			return Signature.getInstance("SHA256withECDSA", Crypto.getCryptoProvider());
		} else if (oid.equals(TaOid.id_TA_ECDSA_SHA_384)){
			return Signature.getInstance("SHA384withECDSA", Crypto.getCryptoProvider());
		} else if (oid.equals(TaOid.id_TA_ECDSA_SHA_512)){
			return Signature.getInstance("SHA512withECDSA", Crypto.getCryptoProvider());
		}
		return null;
	}
	
	/**
	 * Extract the OID from a TLV public key data object.
	 * 
	 * @param publicKeyData
	 *            to parse
	 * @return an {@link Oid} object as read from the publicKeyData
	 */
	public static TaOid getOidFromPublicKey(ConstructedTlvDataObject publicKeyData) {
		return new TaOid(publicKeyData.getTagField(TR03110.TAG_06).getValueField());
	}
	
	/**
	 * The given public key data will be parsed and if needed filled in with the
	 * trust points public key domain parameters.
	 * 
	 * @param publicKeyData
	 *            object from a {@link CardVerifiableCertificate}
	 * @param trustPointPublicKey
	 *            or null
	 * @return the created {@link PublicKey} object
	 * @throws GSSException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public static PublicKey parsePublicKey(
			ConstructedTlvDataObject publicKeyData,
			PublicKey trustPointPublicKey) throws InvalidKeySpecException,
			NoSuchAlgorithmException, NoSuchProviderException {
		TaOid oid = getOidFromPublicKey(publicKeyData);
		
		if (oid.getIdString().contains("RSA")){
			TlvDataObject modulusData = publicKeyData.getTagField(TAG_81);
			TlvDataObject publicExponentData = publicKeyData.getTagField(TAG_82);
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(Utils.getBigIntegerFromUnsignedByteArray(modulusData.getValueField()), Utils.getBigIntegerFromUnsignedByteArray(publicExponentData.getValueField()));
			if (trustPointPublicKey instanceof RSAPublicKey && ((RSAPublicKey)trustPointPublicKey).getModulus().bitLength() != keySpec.getModulus().bitLength()){
				throw new InvalidKeySpecException("The trust points bit length does not match");
			}
			return KeyFactory.getInstance("RSA", Crypto.getCryptoProvider()).generatePublic(keySpec);
		} else if (oid.getIdString().contains("ECDSA")){
			TlvDataObject publicPointData = publicKeyData.getTagField(TAG_86);
			ECPoint publicPoint = DomainParameterSetEcdh.reconstructPoint(publicPointData.getValueField());
			
			TlvDataObject modulusData = publicKeyData.getTagField(TAG_81);
			TlvDataObject firstCoefficientData = publicKeyData.getTagField(TAG_82);
			TlvDataObject secondCoefficientData = publicKeyData.getTagField(TAG_83);
			TlvDataObject basePointData = publicKeyData.getTagField(TAG_84);
			TlvDataObject orderOfBasePointData = publicKeyData.getTagField(TAG_85);
			TlvDataObject cofactorData = publicKeyData.getTagField(TAG_87);

			if (!Utils.isAnyNull(modulusData, firstCoefficientData, secondCoefficientData, basePointData, orderOfBasePointData, cofactorData)){
				//in this case complete domain parameters are given and used
				ECField field = new ECFieldFp(new BigInteger(1, modulusData.getValueField()));				
				EllipticCurve curve = new EllipticCurve(field, new BigInteger(1, firstCoefficientData.getValueField()), new BigInteger(1, secondCoefficientData.getValueField()));
				ECPoint basePoint = DomainParameterSetEcdh.reconstructPoint(basePointData.getValueField());
				ECParameterSpec paramSpec = new ECParameterSpec(curve, basePoint, new BigInteger(1, orderOfBasePointData.getValueField()), Utils.getIntFromUnsignedByteArray(cofactorData.getValueField()));
				ECPublicKeySpec keySpec = new ECPublicKeySpec(publicPoint, paramSpec);
				return KeyFactory.getInstance("EC", Crypto.getCryptoProvider()).generatePublic(keySpec);
			}
			
			//the trust points domain parameters are used
			if (trustPointPublicKey != null && trustPointPublicKey instanceof ECPublicKey){
				ECPublicKey trustPointEcPublicKey = (ECPublicKey) trustPointPublicKey;
				
				//check if the given points x coordinates length fits the trust points field size
				if (trustPointEcPublicKey.getParams().getCurve().getField().getFieldSize() / 8 != ((publicPointData.getLengthValue()-1)/2)){
					throw new InvalidKeySpecException("The trust points field bit length does not match");
				}
				
				ECPublicKeySpec keySpec = new ECPublicKeySpec(publicPoint, trustPointEcPublicKey.getParams());
				return KeyFactory.getInstance("EC", Crypto.getCryptoProvider()).generatePublic(keySpec);
			}
		}
		return null;
	}
	
	/**
	 * This method constructs the input data used to compute the authentication token needed e.g. by Pace's Mutual Authenticate or CA's General Authenticate.
	 * @param publicKey the ephemeral public key to be inserted
	 * @param domParamSet the domain parameters matching the provided public key
	 * @return the authentication token input data
	 */
	public static TlvDataObjectContainer buildAuthenticationTokenInput(PublicKey publicKey, DomainParameterSet domParamSet, Oid oidInput) {
		/* construct authentication token object based on OID and public key */
		byte[] ephemeralPublicKeyByteArray = domParamSet.encodePublicKey(publicKey);
		
		TlvTag pubKeyTag = domParamSet.getAuthenticationTokenPublicKeyTag();
		
		PrimitiveTlvDataObject primitive06 = new PrimitiveTlvDataObject(TAG_06, oidInput.toByteArray());
		PrimitiveTlvDataObject primitive84 = new PrimitiveTlvDataObject(pubKeyTag, ephemeralPublicKeyByteArray);
		ConstructedTlvDataObject constructed7F49 = new ConstructedTlvDataObject(TAG_7F49);
		constructed7F49.addTlvDataObject(primitive06);
		constructed7F49.addTlvDataObject(primitive84);
		TlvDataObjectContainer authenticationTokenInput = new TlvDataObjectContainer();
		authenticationTokenInput.addTlvDataObject(constructed7F49);
		
		return authenticationTokenInput;
	}
	
	/**
	 * This method returns the only existing child {@link CardObject} of parent
	 * parameter, that match all provided {@link CardObjectIdentifier}.
	 * <p/>
	 * It is expected that exactly one CardObject is returned (meaning that the
	 * given Set of Identifiers is unambiguous). If no or more matching elements
	 * are found an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param parent CardObject whose children should be searched
	 * @param cardObjectIdentifier set of identifiers that are required to match on the returned element
	 * @return the one and only child element of parent that matches all provided identifiers
	 * @throws IllegalArgumentException if none or several matching children are found
	 * 
	 */
	public static CardObject getSpecificChild(CardObject parent, CardObjectIdentifier... cardObjectIdentifier) {

		Collection<CardObject> cardObjects = parent.findChildren(cardObjectIdentifier);
		
		// assume that selection is not ambiguous and can be performed implicitly
		switch (cardObjects.size()) {
		case 0:
			throw new IllegalArgumentException("no matching selection found");
			
		case 1:
			CardObject matchingCardObject = cardObjects.iterator().next();;
			log(TR03110.class, "selected " + matchingCardObject, DEBUG);
			return matchingCardObject;

		default:
			throw new IllegalArgumentException("selection is ambiguous, more identifiers required");
		}
	}
	
	/**
	 * This method extracts the domain parameter information from DH and EC public and private keys.
	 * @param key a DH/EC public/private key
	 * @return the extracted domain parameter information
	 */
	public static DomainParameterSet getDomainParameterSetFromKey(Key key) {
		if((key instanceof ECPublicKey) || (key instanceof ECPrivateKey)) {
			ECParameterSpec ecParameterSpec;
			
			if(key instanceof ECPublicKey) {
				ecParameterSpec = ((ECPublicKey) key).getParams();
			} else{
				ecParameterSpec = ((ECPrivateKey) key).getParams();
			}
			
			return new DomainParameterSetEcdh(ecParameterSpec.getCurve(), ecParameterSpec.getGenerator(), ecParameterSpec.getOrder(), ecParameterSpec.getCofactor());
		}
		
		throw new IllegalArgumentException("unexpected key format");
	}

	/**
	 * Reads the a date encoded in 6 bytes as described in TR-03110 v2.10 D.2.1.3.
	 * @param dateData as described in TR-03110 V2.10 part 3, D
	 * @return a {@link Date} object containing the encoded date
	 * @throws CertificateNotParseableException
	 */
	//XXX JUnit tests missing
	public static Date parseDate(byte [] dateData) throws NotParseableException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		
		if (dateData.length == 6){
			for(byte currentByte : dateData){
				if (currentByte < 0 || currentByte > 9){
					throw new NotParseableException("The date could not be parsed, it contains illegal digit " + currentByte);
				}
			}
			calendar.set(dateData[0] * 10 + dateData[1] + 2000, dateData[2] * 10 + dateData[3] - 1, dateData[4] * 10 + dateData[5], 0, 0, 0);
		} else if (dateData.length == 8){
			String dateString = new String(dateData);
			int year = Integer.parseInt(dateString.substring(0, 4));
			int month = Integer.parseInt(dateString.substring(4, 6)) - 1;
			int day = Integer.parseInt(dateString.substring(6, 8));
			calendar.set(year, month, day, 0, 0, 0);
		} else {
			throw new NotParseableException("The date could not be parsed, its length was incorrect");
		}
		return calendar.getTime();
	}
}
