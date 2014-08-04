package de.persosim.simulator.protocols;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.ietf.jgss.GSSException;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.exception.NotParseableException;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
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
public class TR03110 implements TlvConstants {
	public static final int ACCESS_RIGHTS_AT_CAN_ALLOWED_BIT = 4;
	
	public final static byte[] id_BSI                              = {0x04, 0x00, 0x7F, 0x00, 0x07};
	public final static byte[] id_PK                               = Utils.appendBytes(id_BSI, new byte[]{0x02, 0x02, 0x01});
	
	public final static int ID_PIN = 3;
	public final static int ID_CAN = 2;
	
	
	
	/**
	 * The given public key data will be parsed and if needed filled in with the
	 * trust points public key domain parameters.
	 * 
	 * @param publicKeyData
	 *            object from a {@link CardVerifiableCertificate}
	 * @param trustPointPublicKey
	 *            or null
	 * @return the created {@link PublicKey} object
	 * @throws GeneralSecurityException
	 * @throws GSSException
	 */
	public static PublicKey parseCertificatePublicKey(
			ConstructedTlvDataObject publicKeyData,
			PublicKey trustPointPublicKey) throws GeneralSecurityException {
		TaOid oid = new TaOid(publicKeyData.getTagField(TAG_06)
				.getValueField());

		if (oid.getIdString().contains("ECDSA")) {
			ECParameterSpec paramSpec = null;
			ECPublicKey trustPointEcPublicKey = (ECPublicKey) trustPointPublicKey;
			if (publicKeyData.containsTagField(TAG_81)&&
					publicKeyData.containsTagField(TAG_82)&&
					publicKeyData.containsTagField(TAG_83)&&
					publicKeyData.containsTagField(TAG_84)&&
					publicKeyData.containsTagField(TAG_85)&&
					publicKeyData.containsTagField(TAG_87)) {
				paramSpec = CryptoUtil.parseParameterSpecEc(publicKeyData);
			} else {
				if (trustPointEcPublicKey.getParams().getCurve().getField()
						.getFieldSize() / 8 != ((publicKeyData.getTagField(
						TlvConstants.TAG_86).getLengthValue() - 1) / 2)) {
					throw new InvalidKeySpecException(
							"The trust points field bit length does not match");
				}
				paramSpec = trustPointEcPublicKey.getParams();
			}

			return CryptoUtil.parsePublicKeyEc(publicKeyData, paramSpec);
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
