package de.persosim.simulator.ui;

import java.util.Iterator;
import java.util.LinkedList;

import org.globaltester.logging.filter.LevelFilter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

import de.persosim.simulator.CommandParser;
import de.persosim.simulator.Simulator;
import de.persosim.simulator.ui.parts.PersoSimGuiMain;
import de.persosim.simulator.ui.utils.LinkedListLogListener;

/**
 * The activator for this bundle. It tracks the {@link Simulator} service and
 * provides accessor methods.
 * 
 * @author mboonk
 *
 */
public class Activator implements BundleActivator {

	private static BundleContext context;
	private static Simulator sim;

	private LinkedList<LogReaderService> readers = new LinkedList<>();
	private static LinkedListLogListener linkedListLogger = new LinkedListLogListener(PersoSimGuiMain.MAXIMUM_CACHED_CONSOLE_LINES);
	private ServiceTracker<LogReaderService, LogReaderService> logReaderTracker;
	private static ServiceTracker<Simulator, Simulator> simulatorServiceTracker;

	public static Simulator getSim() {
		return sim;
	}

	static BundleContext getContext() {
		return context;
	}
	
	public static LinkedListLogListener getListLogListener(){
		return linkedListLogger;
	}
	
	public static void executeUserCommands(String command){
		Simulator sim = (Simulator) simulatorServiceTracker.getService();
		if (sim != null){
			CommandParser.executeUserCommands(sim, command);
		} else {
			throw new ServiceException("The Simulator service could not be found");
		}
	}
	
	// This will be used to keep track of listeners as they are un/registering
	private ServiceListener logServiceListener = new ServiceListener() {
		@Override
		public void serviceChanged(ServiceEvent event) {
			BundleContext bundleContext = event.getServiceReference().getBundle().getBundleContext();
			LogReaderService readerService = (LogReaderService) bundleContext.getService(event.getServiceReference());
			if (readerService != null){
				if (event.getType() == ServiceEvent.REGISTERED){
					readers.add(readerService);
					readerService.addLogListener(linkedListLogger);
				} else if (event.getType() == ServiceEvent.UNREGISTERING){
					readerService.removeLogListener(linkedListLogger);
					readers.remove(readerService);
				}
			}
		}
	};

	private ServiceListener simulatorServiceListener = new ServiceListener() {
		
		@Override
		public void serviceChanged(ServiceEvent event) {
			ServiceReference<?> serviceReference = event.getServiceReference();
			switch (event.getType()) {
			case ServiceEvent.REGISTERED:
				sim = (Simulator) context.getService(serviceReference);
				break;
			default:
				break;
			}
			
		}
	};
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		Activator.context = context;
				

		logReaderTracker = new ServiceTracker<>(context, LogReaderService.class.getName(), null);
		logReaderTracker.open();
		Object[] readers = logReaderTracker.getServices();
		if (readers != null){
			for (int i=0; i<readers.length; i++){
				LogReaderService readerService = (LogReaderService) readers [i];
				this.readers.add(readerService);
				readerService.addLogListener(linkedListLogger);
			}
		}
				
		simulatorServiceTracker = new ServiceTracker<Simulator, Simulator>(context, Simulator.class.getName(), null);
		simulatorServiceTracker.open();
		simulatorServiceTracker.getService().startSimulator();

		String filter = "(objectclass=" + Simulator.class.getName() + ")";
		context.addServiceListener(simulatorServiceListener, filter);
		
        filter = "(objectclass=" + LogReaderService.class.getName() + ")";
        try {
            context.addServiceListener(logServiceListener, filter);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
        Iterator<LogReaderService> iterator = readers.iterator();
        while (iterator.hasNext())
        {
            LogReaderService readerService = iterator.next();
            readerService.removeLogListener(linkedListLogger);
            iterator.remove();
        }
		
		logReaderTracker.close();

		Activator.context = null;
		simulatorServiceTracker.close();
	}

	private static LevelFilter logLevelFilter;
	
	public static LevelFilter getLogLevelFilter() {
		return logLevelFilter;
	}

	public static void setLogLevelFilter(LevelFilter levelFilter) {
		Activator.logLevelFilter = levelFilter;
	}
}
