package de.persosim.simulator.protocols;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.ietf.jgss.GSSException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.persosim.simulator.Activator;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.exception.NotParseableException;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.PersoSimLogger;

/**
 * XXX MBK replace TaOid with OID according to our own OID class hierarchy 
 * 
 * This class contains methods unique to the TR-03110 specification.
 * @author mboonk
 *
 */
public class Tr03110Utils implements TlvConstants {
	public static final int ACCESS_RIGHTS_AT_CAN_ALLOWED_BIT = 4;
	
static private List<Tr03110UtilsProvider> providers = new ArrayList<>();
	
	static private ServiceTracker<Tr03110UtilsProvider, Tr03110UtilsProvider> serviceTracker;
	
	static {
		if (Activator.getContext() != null){
			ServiceTrackerCustomizer<Tr03110UtilsProvider, Tr03110UtilsProvider> customizer = new ServiceTrackerCustomizer<Tr03110UtilsProvider, Tr03110UtilsProvider>() {
				
				@Override
				public void removedService(
						ServiceReference<Tr03110UtilsProvider> reference,
						Tr03110UtilsProvider service) {
					providers.remove(service);
				}
				
				@Override
				public void modifiedService(
						ServiceReference<Tr03110UtilsProvider> reference,
						Tr03110UtilsProvider service) {
					//Nothing to be done
				}
				
				@Override
				public Tr03110UtilsProvider addingService(
						ServiceReference<Tr03110UtilsProvider> reference) {
					Tr03110UtilsProvider provider = Activator.getContext().getService(reference); 
					providers.add(provider);
					return provider;
				}
			};
			
			serviceTracker = new ServiceTracker<Tr03110UtilsProvider, Tr03110UtilsProvider>(Activator.getContext(), Tr03110UtilsProvider.class.getName(), customizer);
			serviceTracker.open();
			
			ServiceReference<Tr03110UtilsProvider> references [] = serviceTracker.getServiceReferences();
			
			if (references != null){
				for(ServiceReference<Tr03110UtilsProvider> providerReference : references){
					providers.add(Activator.getContext().getService(providerReference));	
				}	
			}
					
		} else {
			PersoSimLogger.log(Tr03110Utils.class, "No OSGi context is available, no additional TR03110 functionalities are supported", PersoSimLogger.INFO);
		}
		providers.add(new Tr03110UtilsDefaultProvider());

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
	 * @throws GeneralSecurityException
	 * @throws GSSException
	 */
	// TODO replace second parameter of type PublicKey with DomainParameterSet
	// PublicKey is only required as source of domain parameter information in
	// case the public key data to be parsed does not contain any.
	// Provide domain parameter information directly if not provided by key data
	// to be parsed.
	public static PublicKey parseCertificatePublicKey(
			ConstructedTlvDataObject publicKeyData,
			PublicKey trustPointPublicKey) {
		
		for (Tr03110UtilsProvider provider : providers) {
			try{
				PublicKey key = provider.parsePublicKey(publicKeyData, trustPointPublicKey);
				if (key != null){
					return key;
				}
			} catch (GeneralSecurityException e){
				PersoSimLogger.logException(Tr03110Utils.class, e, PersoSimLogger.WARN);
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
			log(Tr03110Utils.class, "selected " + matchingCardObject, DEBUG);
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
		for(Tr03110UtilsProvider provider : providers){
			DomainParameterSet domainParameters = provider.getDomainParameterSetFromKey(key);
			if (domainParameters != null){
				return domainParameters;
			}
		}
		throw new IllegalArgumentException("unexpected key format");
	}

	/**
	 * Reads the a date encoded in 6 bytes as described in TR-03110 v2.10 D.2.1.3.
	 * @param dateData as described in TR-03110 V2.10 part 3, D
	 * @return a {@link Date} object containing the encoded date
	 * @throws CertificateNotParseableException
	 */
	public static Date parseDate(byte [] dateData) throws NotParseableException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		if (dateData.length == 6){
			for(byte currentByte : dateData){
				if (currentByte < 0 || currentByte > 9){
					throw new NotParseableException("The date could not be parsed, it contains illegal digit " + currentByte);
				}
			}
			calendar.set(dateData[0] * 10 + dateData[1] + 2000, dateData[2] * 10 + dateData[3] - 1, dateData[4] * 10 + dateData[5], 0, 0, 0);
		} else {
			throw new NotParseableException("The date could not be parsed, its length was incorrect");
		}
		return calendar.getTime();
	}
	
	/**
	 * Encodes a date as described in TR-03110 v2.10 D.2.1.3.
	 * 
	 * @param date
	 *            the date to encode, only the year, month and day components
	 *            are used
	 * @return the 6 byte long BCD encoding
	 */
	public static byte[] encodeDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		DecimalFormat formatter = new DecimalFormat("00");
		String tempDate = ""
				+ formatter.format((calendar.get(Calendar.YEAR) - 2000) / 10)
				+ formatter.format((calendar.get(Calendar.YEAR) - 2000) % 10)
				+ formatter.format((calendar.get(Calendar.MONTH) + 1) / 10)
				+ formatter.format((calendar.get(Calendar.MONTH) + 1) % 10)
				+ formatter.format(calendar.get(Calendar.DAY_OF_MONTH) / 10)
				+ formatter.format(calendar.get(Calendar.DAY_OF_MONTH) % 10);
		return HexString.toByteArray(tempDate);
	}
}
