package de.persosim.simulator.soap;

import org.globaltester.control.soap.SoapControlEndpointManager;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.persosim.simulator.log.PersoSimLogTags;

public class Activator implements BundleActivator
{
	public static final int PERSOSIM_SOAP_PORT = 8890;

	public static BundleContext context;
	private static Activator plugin;

	private SoapControlEndpointManager endpointManager;

	@Override
	public void start(BundleContext context) throws Exception
	{
		BasicLogger.log("START Activator Simulator SOAP", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));

		Activator.context = context;
		plugin = this;

		enablePersoSimRemoteControlService();

		BasicLogger.log("END Activator Simulator SOAP", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
	}

	private void enablePersoSimRemoteControlService()
	{
		endpointManager = new SoapControlEndpointManager("persosim");
		endpointManager.start(PERSOSIM_SOAP_PORT);
		org.globaltester.control.soap.Activator activatorSoap = org.globaltester.control.soap.Activator.getDefault();
		if (activatorSoap != null) {
			activatorSoap.unregisterServices();
		}
	}

	private void disablePersoSimRemoteControlService()
	{
		if (endpointManager != null && endpointManager.isRunning()) {
			endpointManager.stop();
			endpointManager = null;
		}
		org.globaltester.control.soap.Activator activatorSoap = org.globaltester.control.soap.Activator.getDefault();
		if (activatorSoap != null) {
			activatorSoap.unregisterServices();
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		disablePersoSimRemoteControlService();
		Activator.context = null;
	}

	public static BundleContext getContext()
	{
		return context;
	}

	public static Activator getDefault()
	{
		return plugin;
	}

}
