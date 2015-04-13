package de.persosim.simulator.ui;

import java.util.Iterator;
import java.util.LinkedList;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

import de.persosim.simulator.ui.utils.TextFieldLogListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	private LinkedList<LogReaderService> readers = new LinkedList<>();
	private static TextFieldLogListener textFieldLogger = new TextFieldLogListener();
	private ServiceTracker<LogReaderService, LogReaderService> logReaderTracker;
	
	public static TextFieldLogListener getTextFieldLogListener(){
		return textFieldLogger;
	}
	
	// This will be used to keep track of listeners as they are un/registering
	private ServiceListener serviceListener = new ServiceListener() {
		@Override
		public void serviceChanged(ServiceEvent event) {
			BundleContext bundleContext = event.getServiceReference().getBundle().getBundleContext();
			LogReaderService readerService = (LogReaderService) bundleContext.getService(event.getServiceReference());
			if (readerService != null){
				if (event.getType() == ServiceEvent.REGISTERED){
					readers.add(readerService);
					readerService.addLogListener(textFieldLogger);
				} else if (event.getType() == ServiceEvent.UNREGISTERING){
					readerService.removeLogListener(textFieldLogger);
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
	public void start(BundleContext context) throws Exception {
		logReaderTracker = new ServiceTracker<>(context, LogReaderService.class.getName(), null);
		logReaderTracker.open();
		Object[] readers = logReaderTracker.getServices();
		if (readers != null){
			for (int i=0; i<readers.length; i++){
				LogReaderService readerService = (LogReaderService) readers [i];
				this.readers.add(readerService);
				readerService.addLogListener(textFieldLogger);
			}
		}
		
        String filter = "(objectclass=" + LogReaderService.class.getName() + ")";
        try {
            context.addServiceListener(serviceListener, filter);
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
            readerService.removeLogListener(textFieldLogger);
            iterator.remove();
        }
		
		logReaderTracker.close();

	}

}
