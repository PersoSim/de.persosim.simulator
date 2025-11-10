package de.persosim.simulator.controller;

//import org.globaltester.logging.BasicLogger;
//import org.globaltester.logging.tags.LogLevel;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.persosim.simulator.preferences.EclipsePreferenceAccessor;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class Activator implements BundleActivator
{

	@Override
	public void start(BundleContext context) throws Exception
	{
		// BasicLogger.log("START Activator Simulator Controller", LogLevel.TRACE);
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new EclipsePreferenceAccessor());
		PersoSimPreferenceManager.storePreference("PREF_NON_INTERACTIVE", Boolean.TRUE.toString(), false); // Set to non-gui-based mode
		// BasicLogger.log("END Activator Simulator Controller", LogLevel.TRACE);
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		// nothing to do
	}
}
