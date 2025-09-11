package de.persosim.simulator.ui;

import static org.globaltester.logging.BasicLogger.log;

import java.io.IOException;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;
import org.globaltester.simulator.Simulator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.persosim.driver.connector.DriverConnectorFactory;
import de.persosim.driver.connector.service.IfdConnector;
import de.persosim.simulator.CommandParser;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

/**
 * The activator for this bundle. It tracks the {@link Simulator} service and
 * provides accessor methods.
 *
 * @author mboonk
 *
 */
public class Activator implements BundleActivator
{
	private static BundleContext context;
	private static ServiceTracker<DriverConnectorFactory, DriverConnectorFactory> serviceTrackerDriverConnectorFactory;
	public static final int DEFAULT_PORT = 5678;
	public static final String DEFAULT_HOST = "localhost";
	public static IfdConnector connector = null;

	static BundleContext getContext()
	{
		return context;
	}

	public static void executeUserCommands(String command, boolean withOverlayProfile)
	{
		String[] commands = CommandParser.parseCommand(command);
		boolean cmdLoadPerso = false;
		if (commands[0].equals(CommandParser.CMD_LOAD_PERSONALIZATION)) {
			cmdLoadPerso = true;
		}

		String isNonInteractive = PersoSimPreferenceManager.getPreference("PREF_NON_INTERACTIVE");
		BasicLogger.log("Mode is " + ((isNonInteractive != null && Boolean.TRUE.toString().equals(isNonInteractive)) ? "non-" : "") + "interactive", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));

		CommandParser.executeUserCommands(withOverlayProfile, commands);
		if (commands.length == 0)
			return; // just do nothing.
		if (cmdLoadPerso) {
			resetNativeDriver();
		}
		if (commands[0].equals(CommandParser.CMD_STOP)) {
			disconnectFromNativeDriver();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception
	{
		BasicLogger.log("START Activator Simulator UI", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));

		Activator.context = context;
		serviceTrackerDriverConnectorFactory = new ServiceTracker<>(context, DriverConnectorFactory.class.getName(), null);
		serviceTrackerDriverConnectorFactory.open();

		BasicLogger.log("END Activator Simulator UI", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		Activator.context = null;
		serviceTrackerDriverConnectorFactory.close();
	}

	public static void resetNativeDriver()
	{
		try {
			connector = serviceTrackerDriverConnectorFactory.getService().getConnector(de.persosim.driver.connector.Activator.PERSOSIM_CONNECTOR_CONTEXT_ID);
			connector.reconnect();
		}
		catch (IOException e) {
			log(Activator.class, "Exception: " + e.getMessage(), LogLevel.ERROR);
		}
	}

	public static void disconnectFromNativeDriver()
	{
		try {
			connector = serviceTrackerDriverConnectorFactory.getService().getConnector(de.persosim.driver.connector.Activator.PERSOSIM_CONNECTOR_CONTEXT_ID);

			if (connector != null) {
				serviceTrackerDriverConnectorFactory.getService().returnConnector(connector);
			}
		}
		catch (IOException e) {
			log(Activator.class, "Exception: " + e.getMessage(), LogLevel.ERROR);
		}
	}

	public static IfdConnector getConnector()
	{
		return connector;
	}
}
