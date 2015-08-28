package de.persosim.simulator.ui;

import static de.persosim.simulator.utils.PersoSimLogger.ERROR;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.globaltester.logging.filter.LevelFilter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

import de.persosim.driver.connector.NativeDriverConnector;
import de.persosim.driver.connector.service.NativeDriverConnectorInterface;
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
	private LinkedList<LogReaderService> readers = new LinkedList<>();
	private static LinkedListLogListener linkedListLogger = new LinkedListLogListener(PersoSimGuiMain.MAXIMUM_CACHED_CONSOLE_LINES);
	private ServiceTracker<LogReaderService, LogReaderService> logReaderTracker;
	private static ServiceTracker<NativeDriverConnectorInterface, NativeDriverConnectorInterface> serviceTrackerNativeDriverConnector;
	public static final int DEFAULT_PORT = 5678;
	public static final String DEFAULT_HOST = "localhost";
	public static NativeDriverConnector connector = null;
	private static LogReaderService readerService = null;
	

	public static NativeDriverConnectorInterface getConnector() {
		if (serviceTrackerNativeDriverConnector != null){
			return serviceTrackerNativeDriverConnector.getService();
		}
		return null;
	}

	static BundleContext getContext() {
		return context;
	}
	
	public static LinkedListLogListener getListLogListener(){
		return linkedListLogger;
	}
	
	public static void executeUserCommands(String command){
		
		String[] commands = CommandParser.parseCommand(command);
		CommandParser.executeUserCommands(commands);
		if (commands[0].equals(CommandParser.CMD_LOAD_PERSONALIZATION)) {
			connectToNativeDriver();
		}
		if (commands[0].equals(CommandParser.CMD_STOP)) {
			disconnectFromNativeDriver();
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
				readerService = (LogReaderService) readers [i];
				this.readers.add(readerService);
			}
		}
				
		serviceTrackerNativeDriverConnector = new ServiceTracker<NativeDriverConnectorInterface, NativeDriverConnectorInterface>(context, NativeDriverConnectorInterface.class.getName(), null);
		serviceTrackerNativeDriverConnector.open();
		
        String filter = "(objectclass=" + LogReaderService.class.getName() + ")";
        try {
            context.addServiceListener(logServiceListener, filter);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
	}
	
	public static void removeLogListener() {
		readerService.removeLogListener(linkedListLogger);
	}
	
	public static void addLogListener() {
		readerService.addLogListener(linkedListLogger);
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
		serviceTrackerNativeDriverConnector.close();
	}

	private static LevelFilter logLevelFilter;
	
	public static LevelFilter getLogLevelFilter() {
		return logLevelFilter;
	}

	public static void setLogLevelFilter(LevelFilter levelFilter) {
		Activator.logLevelFilter = levelFilter;
	}
	
	public static void connectToNativeDriver() {
		connector = (NativeDriverConnector) getConnector();
		try {
			if (!connector.isRunning()) {
			connector.connect(DEFAULT_HOST, DEFAULT_PORT);
			}
		} catch (IOException e) {
			log(CommandParser.class, "Exception: " + e.getMessage(), ERROR);
			e.printStackTrace();
		}
	}
	
	public static void disconnectFromNativeDriver() {
			try {
				if (connector == null) {
					connector = (NativeDriverConnector) getConnector();
				}
				if (connector != null) {
					connector.disconnect();
				}
			} catch (IOException | InterruptedException e) {
				log(CommandParser.class, "Exception: " + e.getMessage(), ERROR);
				e.printStackTrace();
			}
		}
}
