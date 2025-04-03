package de.persosim.simulator.adapter.socket.ui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.persosim.simulator.preferences.EclipsePreferenceAccessor;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class Activator implements BundleActivator {

	private static BundleContext context;
	public static String PLUGIN_ID = "de.persosim.adapter.socket.ui";

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new EclipsePreferenceAccessor());;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
}
