package de.persosim.simulator.adapter.socket;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.persosim.simulator.Simulator;

/**
 * This bundle activator tracks the {@link Simulator} service provided via OSGi
 * and manages the lifecycle of the socket.
 * 
 * @author mboonk
 *
 */
public class Activator implements BundleActivator, SimulatorProvider {

	private static BundleContext context;
	private static SocketAdapter simulatorSocket;
	private static ServiceTracker<Simulator, Simulator> serviceTracker;
	private static final int SIM_PORT = 9876;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		simulatorSocket = new SocketAdapter(this, SIM_PORT);
		serviceTracker = new ServiceTracker<Simulator, Simulator>(bundleContext, Simulator.class.getName(), new ServiceTrackerCustomizer<Simulator, Simulator>() {

			@Override
			public Simulator addingService(ServiceReference<Simulator> reference) {
				simulatorSocket.start();
				return bundleContext.getService(reference);
			}

			@Override
			public void modifiedService(ServiceReference<Simulator> reference, Simulator service) {
				// nothing to do
			}

			@Override
			public void removedService(ServiceReference<Simulator> reference, Simulator service) {
				simulatorSocket.stop();
				
			}
		});
		serviceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		serviceTracker.close();
	}

	@Override
	public Simulator getSimulator() {
		return serviceTracker.getService();
	}
}
