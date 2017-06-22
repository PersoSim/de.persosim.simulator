package de.persosim.simulator.crypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.persosim.simulator.Activator;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class provides static access to PACE standardized domain parameters.
 * 
 * @author slutters
 *
 */
public class StandardizedDomainParameters{
	public static final byte[] OID = Utils.appendBytes(Tr03110.id_BSI, (byte) 0x01, (byte) 0x02);
	
	public static final int NO_OF_STANDARDIZED_DOMAIN_PARAMETERS = 32;
	
	static private List<StandardizedDomainParameterProvider> providers = new ArrayList<>();
	
	static private ServiceTracker<StandardizedDomainParameterProvider, StandardizedDomainParameterProvider> serviceTracker;
	
	static {
		if (Activator.getContext() != null){
			ServiceTrackerCustomizer<StandardizedDomainParameterProvider, StandardizedDomainParameterProvider> customizer = new ServiceTrackerCustomizer<StandardizedDomainParameterProvider, StandardizedDomainParameterProvider>() {
				
				@Override
				public void removedService(
						ServiceReference<StandardizedDomainParameterProvider> reference,
						StandardizedDomainParameterProvider service) {
					providers.remove(service);
				}
				
				@Override
				public void modifiedService(
						ServiceReference<StandardizedDomainParameterProvider> reference,
						StandardizedDomainParameterProvider service) {
					//Nothing to be done
				}
				
				@Override
				public StandardizedDomainParameterProvider addingService(
						ServiceReference<StandardizedDomainParameterProvider> reference) {
					StandardizedDomainParameterProvider provider = Activator.getContext().getService(reference);
					providers.add(provider);
					return provider;
				}
			};
			
			serviceTracker = new ServiceTracker<>(Activator.getContext(), StandardizedDomainParameterProvider.class.getName(), customizer);
			serviceTracker.open();
			
			ServiceReference<StandardizedDomainParameterProvider>[] references = serviceTracker.getServiceReferences();
			
			if (references != null){
				for(ServiceReference<StandardizedDomainParameterProvider> providerReference : references){
					providers.add(Activator.getContext().getService(providerReference));
				}
			}
			
		} else {
			BasicLogger.log(StandardizedDomainParameters.class, "No OSGi context is available, no additional domain parameters are supported", LogLevel.INFO);
		}
		providers.add(new StandardizedDomainParameterDefaultProvider());
	}
		
	static private HashMap<Integer, StandardizedDomainParameterProvider> getCurrentlySupportedParameters(){
		HashMap<Integer, StandardizedDomainParameterProvider> supported = new HashMap<>();
		
		for (StandardizedDomainParameterProvider provider : providers) {
			for (Integer i : provider.getSupportedDomainParameters()){
				supported.put(i, provider);
			}
		}
		
		return supported;
	}
	
	static public DomainParameterSet getDomainParameterSetById(int id){

		if (id < 0 || id >= NO_OF_STANDARDIZED_DOMAIN_PARAMETERS){
			throw new IllegalArgumentException("id for standardized domain parameters must be > 0 and < " + NO_OF_STANDARDIZED_DOMAIN_PARAMETERS);
		}
		
		StandardizedDomainParameterProvider provider = getCurrentlySupportedParameters().get(id);
				
		if (provider != null){
			return provider.getDomainParameterSet(id);
		}
		return null;
	}	
	
	/**
	 * Simplify the given AlgorithmIdentifier using standardized domain
	 * parameters if possible
	 * <p/>
	 * Returns a new AlgorithmIdentifier describing the provided input using OID
	 * bsi-de 1 2 and an integer identifying the used domain parameter set.
	 * <p/>
	 * If the provided input does not match any known standardized domain
	 * parameters the input is returned without further checking.
	 * 
	 * @param algIdentifier
	 * @return
	 */
	public static ConstructedTlvDataObject simplifyAlgorithmIdentifier(
			ConstructedTlvDataObject algIdentifier) {
		
		for (StandardizedDomainParameterProvider provider : providers){
			String algIdHexString = HexString.encode(algIdentifier.toByteArray());
			Integer current = provider.getSimplifiedAlgorithm(algIdHexString);
			if (current != null){
				ConstructedTlvDataObject newAlgIdentifier = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE);
				newAlgIdentifier.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_OID, OID));
				newAlgIdentifier.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_INTEGER, new byte[] {(byte) current.intValue()}));
				return newAlgIdentifier;
			}
		}
		return algIdentifier;
	}
	
	/**
	 * This method retrieves the standardized domain parameter ID from an TLV
	 * algorithm identifier.
	 * 
	 * @param algIdentifier
	 *            The identifier to be checked
	 * @return The {@link Integer} id or null if the domain parameters are not
	 *         supported
	 */
	public static Integer getDomainParameterSetId(ConstructedTlvDataObject algIdentifier){
		for (StandardizedDomainParameterProvider provider : providers){
			String algIdHexString = HexString.encode(algIdentifier.toByteArray());
			return provider.getSimplifiedAlgorithm(algIdHexString);
		}
		return null;
	}
	
}
