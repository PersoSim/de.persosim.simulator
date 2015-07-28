package de.persosim.simulator;

import static de.persosim.simulator.utils.PersoSimLogger.INFO;
import static de.persosim.simulator.utils.PersoSimLogger.UI;
import static de.persosim.simulator.utils.PersoSimLogger.WARN;
import static de.persosim.simulator.utils.PersoSimLogger.log;
import static de.persosim.simulator.utils.PersoSimLogger.logException;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.Profile01;
import de.persosim.simulator.platform.PersoSimKernel;
import de.persosim.simulator.utils.PersoSimLogger;

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
public class PersoSim implements Simulator {
	
	/*
	 * This variable holds the currently used personalization.
	 * It may explicitly be null and should not be read directly from here.
	 * As there exist several ways of providing a personalization of which none at all may be used the variable may remain null/unset.
	 * Due to this possibility access to this variable should be performed by calling the getPersonalization() method. 
	 */
	private Personalization currentPersonalization;
	
	public static final String LOG_NO_OPERATION = "nothing to process";
	public static final String LOG_SIM_EXIT     = "simulator exit";
	
	public static final String persoPlugin = "platform:/plugin/de.persosim.rcp/";
	public static final String persoPath = "personalization/profiles/";
	public static final String persoFilePrefix = "Profile";
	public static final String persoFilePostfix = ".xml";
	
	private PersoSimKernel kernel;
	
	/**
	 * This constructor is used by the OSGi-service instantiation
	 */
	public PersoSim(){
		currentPersonalization = new Profile01();
	}
	
	public PersoSim(String... args) {
		this();
		try {
			CommandParser.handleArgs(this, args);
		} catch (IllegalArgumentException e) {
			log(this.getClass(),
					"simulation aborted, reason is: " + e.getMessage());
		}
		
	}
	
	public void startPersoSim(){
		System.out.println("Welcome to PersoSim");

		startSimulator();
		final Simulator sim = this;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				CommandParser.handleUserCommands(sim);
			}
		}).start();
	}
	
	@Override
	public boolean startSimulator() {
		if (kernel != null) {
			log(this.getClass(), "Simulator already running", UI);
			return true;
		}
		
		if (currentPersonalization == null) {
			log(this.getClass(), "No personalization available, please load a valid personalization before starting the simulator", PersoSimLogger.UI);
			return false;
		}
		
		try {
			kernel = new PersoSimKernel(currentPersonalization);
		} catch (AccessDeniedException e) {
			logException(this.getClass(), e, PersoSimLogger.ERROR);
			return false;
		}
		kernel.init();
		return true;
	}
	
	@Override
	public boolean stopSimulator() {
		boolean simStopped = false;
		
		if (kernel != null) {
			kernel = null;
				log(this.getClass(), "The simulator has been stopped and will no longer respond to incoming APDUs until it is (re-) started", UI);
			return true;
		}
		
		return simStopped;
	}
	
	@Override
	public boolean restartSimulator() {
		stopSimulator();
		return startSimulator();
	}

	@Override
	public boolean loadPersonalization(Personalization personalization) {
		currentPersonalization = personalization;
		return restartSimulator();
	}

	@Override
	public byte[] processCommand(byte[] apdu) {
	
		if (kernel == null){
			log(this.getClass(), "The simulator is stopped and the APDU was ignored", INFO);
			return new byte[]{0x6f, 0x78};
		}
		
		return kernel.process(apdu);
	}

	@Override
	public boolean isRunning() {
		return kernel != null;
	}

	@Override
	public byte[] cardPowerUp() {
		if (kernel == null){
			log(this.getClass(), "The simulator is stopped, attempt to power up ignored", WARN);
			return new byte[]{0x6f, 0x79};
		}
		return kernel.powerOn();
	}

	@Override
	public byte[] cardPowerDown() {
		if (kernel == null){
			log(this.getClass(), "The simulator is stopped, attempt to power down ignored", WARN);
			return new byte[]{0x6f, (byte)0x80};
		}
		return kernel.powerOff();

	}

	@Override
	public byte[] cardReset() {
		if (kernel == null){
			log(this.getClass(), "The simulator is stopped, attempt to reset ignored", WARN);
			return new byte[]{0x6f, (byte)0x81};
		}
		return kernel.reset();
	}
}
