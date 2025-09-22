package de.persosim.simulator;

import static org.globaltester.logging.BasicLogger.log;
import static org.globaltester.logging.BasicLogger.logException;

import java.util.Arrays;
import java.util.HashSet;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;
import org.globaltester.simulator.Simulator;
import org.globaltester.simulator.SimulatorEventListener;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.platform.PersoSimKernel;

/**
 * This class provides access to and control of the actual simulator. It can be
 * used to start, stop and configure it. The simulator may be configured by
 * providing either command line arguments during start-up or user initiated
 * commands at runtime. As all parameters vital for the operation of the
 * simulator are implicitly set to default values by fall-through, no explicit
 * configuration is required.
 *
 * @author slutters
 *
 */
public class PersoSim implements Simulator
{

	public static final String LOG_NO_OPERATION = "nothing to process";
	public static final String LOG_SIM_EXIT = "simulator exit";

	private PersoSimKernel kernel = null;

	private HashSet<SimulatorEventListener> simEventListeners = new HashSet<>();

	/**
	 * This constructor is used by the OSGi-service instantiation
	 */
	public PersoSim()
	{
	}

	public PersoSim(String... args)
	{
		this();
		try {
			CommandParser.handleArgs(args);
		}
		catch (IllegalArgumentException e) {
			logException("simulation aborted", e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
		}

	}

	public void startPersoSim()
	{
		log("Welcome to PersoSim", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));

		startSimulator();
	}

	@Override
	public boolean startSimulator()
	{
		if (kernel != null) {
			log("Simulator already running", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
		}
		else {
			logStartOK();
		}
		// actual starting is lazily done when a perso is loaded, thus always return true here
		return true;
	}

	public void logStartOK()
	{
		log("The simulator has been started", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
	}

	@Override
	public boolean stopSimulator()
	{
		if (kernel != null) {
			kernel = null;
			log("The simulator has been stopped and will no longer respond to incoming APDUs until it is (re-) started", LogLevel.INFO,
					new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
			return true;
		}
		log("The simulator is already stopped", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
		return false;
	}

	@Override
	public boolean restartSimulator()
	{
		stopSimulator();
		return startSimulator();
	}

	/**
	 * This methods loads the provided personalization.
	 *
	 * @param personalization
	 *            the personalization to load
	 * @return true, if the profile loading was successful, otherwise false
	 */
	public boolean loadPersonalization(Personalization personalization)
	{
		try {
			kernel = new PersoSimKernel();
		}
		catch (AccessDeniedException e) {
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return false;
		}
		kernel.init(personalization);

		return true;
	}

	@Override
	public byte[] processCommand(byte[] apdu)
	{
		if (kernel == null) {
			log("The simulator is not initialized and the APDU was ignored", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
			return new byte[] { 0x6f, 0x78 };
		}

		return kernel.process(apdu);
	}

	@Override
	public boolean isRunning()
	{
		return kernel != null;
	}

	@Override
	public byte[] cardPowerUp()
	{
		if (kernel == null) {
			log("The simulator is not initialized, attempt to power up ignored", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
			return new byte[] { 0x6f, (byte) 0x82 };
		}
		return kernel.powerOn();
	}

	@Override
	public byte[] cardPowerDown()
	{
		if (kernel == null) {
			log("The simulator is not initialized, attempt to power down ignored", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
			return new byte[] { 0x6f, (byte) 0x83 };
		}
		return kernel.powerOff();
	}

	@Override
	public byte[] cardReset()
	{
		if (kernel == null) {
			log("The simulator is not initialized, reset attempt ignored", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
			return new byte[] { 0x6f, (byte) 0x84 };
		}
		return kernel.reset();
	}

	@Override
	public void addEventListener(SimulatorEventListener... newListeners)
	{
		simEventListeners.addAll(Arrays.asList(newListeners));

		if (kernel != null) {
			kernel.addEventListener(newListeners);
		}
	}

	@Override
	public void removeEventListener(SimulatorEventListener oldListener)
	{
		simEventListeners.remove(oldListener);

		if (kernel != null) {
			kernel.removeEventListener(oldListener);
		}
	}
}
