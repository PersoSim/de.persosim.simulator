package de.persosim.simulator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	private static ServiceTracker<LogService, LogService> logServiceTracker;
	private static BundleContext context;
	private static List<SocketSimulator> simulators = new ArrayList<>();
	
	public static LogService getLogservice() {		
		if (logServiceTracker != null){
			return logServiceTracker.getService();
		}
		return null;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class.getName(), null);
        logServiceTracker.open();

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		logServiceTracker.close();
		for (SocketSimulator socketSimulator : simulators) {
			socketSimulator.stop();
		}
		simulators = new ArrayList<>();
		Activator.context = null;
	}

	public static BundleContext getContext() {
		return context;
	}

	public static void addForTermination(SocketSimulator sim){
		simulators.add(sim);
	}
}
