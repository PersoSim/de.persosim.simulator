package de.persosim.simulator.adapter.socket;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.persosim.simulator.Simulator;

public class Activator implements BundleActivator, SimulatorProvider {

	private static BundleContext context;
	private static SocketAdapter simulator;
	private static ServiceTracker<Simulator, Simulator> serviceTracker;
	private static final int SIM_PORT = 9876;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		serviceTracker = new ServiceTracker<Simulator, Simulator>(bundleContext, Simulator.class.getName(), null);
		serviceTracker.open();
		simulator = new SocketAdapter(this, SIM_PORT);
		simulator.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		simulator.stop();
		serviceTracker.close();
	}

	@Override
	public Simulator getSimulator() {
		return serviceTracker.getService();
	}
}
