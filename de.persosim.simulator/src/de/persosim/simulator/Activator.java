package de.persosim.simulator;

import static org.globaltester.logging.BasicLogger.log;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;
import org.globaltester.simulator.Simulator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

import de.persosim.simulator.log.LinkedListLogListener;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.preferences.EclipsePreferenceAccessor;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class Activator implements BundleActivator
{
	public static BundleContext context;

	private static Activator plugin;
	private static PersoSim sim = null;
	private ServiceRegistration<Simulator> simRegistration;

	private static final int MAXIMUM_CACHED_CONSOLE_LINES = 100000;
	private static LinkedListLogListener linkedListLogger = new LinkedListLogListener(MAXIMUM_CACHED_CONSOLE_LINES);

	@Override
	public void start(BundleContext context) throws Exception
	{
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new EclipsePreferenceAccessor());
		BasicLogger.addLogListener(linkedListLogger);
		BasicLogger.log("START Activator Simulator", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
		Activator.context = context;
		plugin = this;
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new EclipsePreferenceAccessor());
		BasicLogger.log("END Activator Simulator", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		Activator.context = null;
		BasicLogger.removeLogListener(linkedListLogger);
	}

	public static BundleContext getContext()
	{
		return context;
	}

	/**
	 * This enables the {@link Simulator} for the PersoSim simulator.
	 *
	 * @throws InvalidSyntaxException
	 */
	public void enableService()
	{
		if (isSimulatorRunning()) {
			log("There is already a simulator running, please stop it before starting another one!", LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
			throw new RuntimeException("There is already a simulator running, please stop it before starting another one!");
		}
		if (sim == null) {
			sim = new PersoSim();
			simRegistration = context.registerService(Simulator.class, sim, null);
		}
	}

	/**
	 * This function checks if other simulators are already running
	 *
	 * @return true if other simulator are running.
	 */
	public boolean isSimulatorRunning()
	{
		int simulatorCnt = 0;
		try {
			simulatorCnt = context.getServiceReferences(Simulator.class, null).size();
		}
		catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		return simulatorCnt > 0;
	}

	/**
	 * This disables the {@link Simulator} for the PersoSim simulator.
	 */
	public void disableService()
	{

		if (sim != null && sim.isRunning()) {
			sim.stopSimulator();
		}
		sim = null;
		if (simRegistration != null) {
			simRegistration.unregister();
			simRegistration = null;
		}
	}

	public static Activator getDefault()
	{
		return plugin;

	}

	/**
	 * This function returns the current simulator
	 *
	 * @return Simulator
	 */
	public PersoSim getSim()
	{
		return sim;
	}

	public Runnable getCleanupHook()
	{
		return new Runnable() {

			@Override
			public void run()
			{
				if (plugin != null) {
					plugin.disableService();
				}
			}
		};
	}

	public static LinkedListLogListener getListLogListener()
	{
		return linkedListLogger;
	}
}
