package de.persosim.simulator.adapter.socket;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;
import org.globaltester.simulator.Simulator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.persosim.simulator.adapter.socket.protocol.GlobalTesterProtocol;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.preferences.EclipsePreferenceAccessor;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

/**
 * This bundle activator tracks the {@link Simulator} service provided via OSGi
 * and manages the lifecycle of the socket.
 *
 * @author mboonk
 *
 */
public class Activator implements BundleActivator, SimulatorProvider
{
	private static BundleContext context;
	private static SocketAdapter gtSimulatorSocket;
	private static ServiceTracker<Simulator, Simulator> serviceTracker;
	private static final int GT_SIM_PORT = 9876;

	public static BundleContext getContext()
	{
		return context;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext bundleContext) throws Exception
	{
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new EclipsePreferenceAccessor());
		BasicLogger.log("START Activator Adapter Socket", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
		Activator.context = bundleContext;
		gtSimulatorSocket = new SocketAdapter(GT_SIM_PORT, new GlobalTesterProtocol(this));
		serviceTracker = new ServiceTracker<Simulator, Simulator>(bundleContext, Simulator.class.getName(), new ServiceTrackerCustomizer<Simulator, Simulator>() {

			@Override
			public Simulator addingService(ServiceReference<Simulator> reference)
			{
				gtSimulatorSocket.start();
				return bundleContext.getService(reference);
			}

			@Override
			public void modifiedService(ServiceReference<Simulator> reference, Simulator service)
			{
				// nothing to do
			}

			@Override
			public void removedService(ServiceReference<Simulator> reference, Simulator service)
			{
				gtSimulatorSocket.stop();

			}
		});
		serviceTracker.open();
		BasicLogger.log("END Activator Adapter Socket", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception
	{
		Activator.context = null;
		serviceTracker.close();
	}

	@Override
	public Simulator getSimulator()
	{
		return serviceTracker.getService();
	}
}
