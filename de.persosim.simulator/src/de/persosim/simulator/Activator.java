package de.persosim.simulator;

import java.security.Provider;
import java.security.Security;
import java.util.Hashtable;

import org.globaltester.cryptoprovider.Cryptoprovider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	public static BundleContext context;
	
	private static Provider cryptoProvider;
	public static Cryptoprovider objectImplementingInterface;
	
	private static ServiceTracker<LogService, LogService> logServiceTracker;

	
	private ServiceTracker<?, ?> serviceTracker;
	
	
	public static LogService getLogservice() {		
		if (logServiceTracker != null){
			return logServiceTracker.getService();
		}
		return null;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		
		//get LogService
		logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class.getName(), null);
        logServiceTracker.open();
		
		registerCryptoProvider();

		//Registers Simulator service
		context.registerService(Simulator.class.getName(), new PersoSim(), new Hashtable<String, String>());
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		logServiceTracker.close();
		Activator.context = null;
	}

	public static BundleContext getContext() {
		return context;
	}
	
	public void registerCryptoProvider() {
		serviceTracker = new ServiceTracker<>(context, Cryptoprovider.class, null);
		serviceTracker.open();
		
		Object[] allServiceObjects = serviceTracker.getServices();
		System.out.println("service tracker tracking " + allServiceObjects.length + " service objects");
		for(Object currentServiceObject : allServiceObjects) {
			System.out.println("service object: " + currentServiceObject.getClass().getName());
		}
		
		objectImplementingInterface = (Cryptoprovider) serviceTracker.getService();
		
		System.out.println("objectImplementingInterface: " + objectImplementingInterface);
		
		cryptoProvider = objectImplementingInterface.getCryptoProviderObject();
		
		System.out.println("cryptoProvider: " + cryptoProvider);
		
		// register BouncyCastle provider
		Security.addProvider(cryptoProvider);
	}
	
}
