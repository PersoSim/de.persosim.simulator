package de.persosim.simulator;

import org.globaltester.cryptoprovider.Cryptoprovider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import de.persosim.simulator.crypto.Crypto;

public class Activator implements BundleActivator {

	public static BundleContext context;
	
	//TODO move this service tracking to the class PersoSimLogger (similar to Crypto)
	private static ServiceTracker<LogService, LogService> logServiceTracker;

	
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
        
        //register service listener for CryptoProvider
        String filter = "(objectclass=" + Cryptoprovider.class.getName() + ")";
		context.addServiceListener(Crypto.getInstance(), filter);
}

	@Override
	public void stop(BundleContext context) throws Exception {
		logServiceTracker.close();
		Activator.context = null;
	}

	public static BundleContext getContext() {
		return context;
	}
	
}
