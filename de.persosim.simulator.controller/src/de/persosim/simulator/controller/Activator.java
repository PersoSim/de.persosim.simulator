package de.persosim.simulator.controller;

//import org.globaltester.logging.BasicLogger;
//import org.globaltester.logging.tags.LogLevel;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

	@Override
	public void start(BundleContext context) throws Exception
	{
		// BasicLogger.log("START Activator Simulator Controller", LogLevel.TRACE);
		// BasicLogger.log("END Activator Simulator Controller", LogLevel.TRACE);
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		// nothing to do
	}
}
