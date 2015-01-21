package de.persosim.simulator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	private static LogService logservice;
	
	public static LogService getLogservice() {
		return logservice;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		ServiceTracker logServiceTracker = new ServiceTracker(context, LogService.class.getName(), null);
        logServiceTracker.open();
        logservice = (LogService) logServiceTracker.getService();
       
        if (logservice != null)
            logservice.log(LogService.LOG_INFO, "Log initialized");

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
