package de.persosim.simulator.adapter.socket.ui;

import org.globaltester.simulator.Simulator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.persosim.simulator.adapter.socket.SimulatorProvider;
import de.persosim.simulator.adapter.socket.SocketAdapter;
import de.persosim.simulator.adapter.socket.protocol.VSmartCardProtocol;
import de.persosim.simulator.preferences.EclipsePreferenceAccessor;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class Activator implements BundleActivator , SimulatorProvider{
	public static String PLUGIN_ID = "de.persosim.adapter.socket.ui";

	public static final int VSC_SIM_PORT = 35963;

	private static BundleContext context;
	private static Activator INSTANCE;

	static BundleContext getContext() {
		return context;
	}

	private static SocketAdapter vscSimulatorSocket;
	private ServiceTracker<Simulator, Simulator> serviceTracker;

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		context = bundleContext;
		INSTANCE = this;
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new EclipsePreferenceAccessor());
		serviceTracker = new ServiceTracker<Simulator, Simulator>(bundleContext, Simulator.class.getName(), new ServiceTrackerCustomizer<Simulator, Simulator>() {

			@Override
			public Simulator addingService(ServiceReference<Simulator> reference) {
				return bundleContext.getService(reference);
			}

			@Override
			public void modifiedService(ServiceReference<Simulator> reference, Simulator service) {
				// nothing to do
			}

			@Override
			public void removedService(ServiceReference<Simulator> reference, Simulator service) {
				if (vscSimulatorSocket != null)
					vscSimulatorSocket.stop();
			}
		});
		serviceTracker.open();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		context = null;
	}

	@Override
	public Simulator getSimulator() {
		return serviceTracker.getService();
	}
	
	public static void startVsmartcard() {
		vscSimulatorSocket = new SocketAdapter(VSC_SIM_PORT, new VSmartCardProtocol(Activator.INSTANCE));
		vscSimulatorSocket.start();
	}
	
	public static void stopVsmartcard() {
		vscSimulatorSocket.stop();
	}
}
