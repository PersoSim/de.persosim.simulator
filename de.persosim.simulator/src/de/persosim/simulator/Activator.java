package de.persosim.simulator;

import static org.globaltester.logging.BasicLogger.log;

import org.globaltester.logging.tags.LogLevel;
import org.globaltester.simulator.Simulator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

import de.persosim.simulator.preferences.EclipsePreferenceAccessor;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class Activator implements BundleActivator {

	public static BundleContext context;

	private static Activator plugin;
	private static PersoSim sim = null;
	private ServiceRegistration<Simulator> simRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		plugin = this;
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new EclipsePreferenceAccessor());
}

	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.context = null;
	}

	public static BundleContext getContext() {
		return context;
	}

	/**
	 * This enables the {@link Simulator} for the PersoSim simulator.
	 * @throws InvalidSyntaxException
	 */
	public void enableService() {
		if (isSimulatorRunning()) {
			log(this.getClass(), "There is already a simulator running, please stop it before starting another one!", LogLevel.ERROR);
			throw new RuntimeException("There is already a simulator running, please stop it before starting another one!");
		}
		if (sim == null) {
			sim = new PersoSim();
			simRegistration = context.registerService(Simulator.class, sim, null);
		}
	}

	/**
	 * This function checks if other simulators are already running
	 * @return true if other simulator are running.
	 */
	public boolean isSimulatorRunning() {
		int simulatorCnt = 0;
		try {
			simulatorCnt = context.getServiceReferences(Simulator.class, null).size();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		};
		return simulatorCnt > 0;
	}

	/**
	 * This disables the {@link Simulator} for the PersoSim simulator.
	 */
	public void disableService(){

		if (sim != null && sim.isRunning()){
			sim.stopSimulator();
		}
		sim = null;
		if (simRegistration != null) {
			simRegistration.unregister();
			simRegistration = null;
		}
	}

	public static Activator getDefault() {
		return plugin;

	}

	/**
	 * This function returns the current simulator
	 * @return Simulator
	 */
	public PersoSim getSim() {
		return sim;
	}

	public Runnable getCleanupHook() {
		return new Runnable() {

			@Override
			public void run() {
				if (plugin != null) {
					plugin.disableService();
				}
			}
		};
	}
}
