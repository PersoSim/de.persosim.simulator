package de.persosim.simulator.crypto;

import java.security.Provider;
import java.security.Security;

import org.globaltester.cryptoprovider.Cryptoprovider;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import de.persosim.simulator.Activator;

/**
 * This class is intended to be used as source for the CryptoProvider in order
 * to allow using the code base as single source solution when porting to
 * Android.
 * 
 * @author amay, mboonk, jkoch
 * 
 */
public class Crypto implements ServiceListener {

	private static Crypto instance;
	private static Provider providerObject = null;
	
	/**
	 * Singleton constructor, ensures that the class can not be instantiated from outside.
	 */
	private Crypto(){
		try {
			@SuppressWarnings("unchecked") // legacy code
			ServiceReference<Cryptoprovider> sRef = (ServiceReference<Cryptoprovider>) Activator.getContext().getServiceReference(Cryptoprovider.class.getName());
			if (sRef != null) {
				cryptoProviderService = (Cryptoprovider) Activator.getContext().getService(sRef);
			}
		} catch (Exception e) {
			//nothing to do
		}
	};
	
	/**
	 * @return singleton instance
	 */
	public static Crypto getInstance() {
		if (instance == null) {
			instance = new Crypto();
		}
		return instance;
	}
	
	
	private Cryptoprovider cryptoProviderService = null;
	
	public static void setCryptoProvider(Provider newProvider) {
		providerObject = newProvider;
	}

	public static Provider getCryptoProvider() {
		if (providerObject != null) {
			return providerObject;
		}
		
		return getInstance().getCryptoProviderFromService();
	}
		
	private Provider getCryptoProviderFromService() {
	    if (cryptoProviderService != null) {
			return cryptoProviderService.getCryptoProviderObject();
		}

		Provider[] providers = Security.getProviders();
		if (providers != null && providers.length > 0)
			return providers[0];

		return null;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		if (event.getType() == ServiceEvent.REGISTERED){
			this.cryptoProviderService = (Cryptoprovider) Activator.getContext().getService(event.getServiceReference());
		} else if (event.getType() == ServiceEvent.UNREGISTERING){
			this.cryptoProviderService = null;
		}
	}

}
