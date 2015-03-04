package de.persosim.simulator;

import static de.persosim.simulator.utils.PersoSimLogger.INFO;
import static de.persosim.simulator.utils.PersoSimLogger.UI;
import static de.persosim.simulator.utils.PersoSimLogger.log;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.Profile01;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.PersoSimKernel;
import de.persosim.simulator.utils.PersoSimLogger;
import de.persosim.simulator.utils.Utils;

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

	private static final byte[] ACK = Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR);
	private static final byte[] NACK = Utils.toUnsignedByteArray(Iso7816.SW_6F00_UNKNOWN);
	
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
		
		if (getPersonalization() == null) {
			log(this.getClass(), "No personalization available, please load a valid personalization before starting the simulator", PersoSimLogger.UI);
			return false;
		}
		
		kernel = new PersoSimKernel(getPersonalization());
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
	public boolean exitSimulator() {
		log(this.getClass(), LOG_SIM_EXIT, UI);
		
		boolean stopped = stopSimulator();
		
		if(stopped) {
			log(this.getClass(), "The simulator has been terminated and will no longer respond to incoming APDUs or commands", UI);
		}
				
		return stopped;
	}

	@Override
	public Personalization getPersonalization() {
		return currentPersonalization;
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
		
		int clains = Utils.maskUnsignedShortToInt(Utils.concatenate(apdu[0], apdu[1]));
		switch (clains) {
		case 0xFF00:
			return kernel.powerOff();
		case 0xFF01:
			return kernel.powerOn();
		case 0xFF6F:
			return NACK;
		case 0xFF90:
			return ACK;
		case 0xFFFF:
			return kernel.reset();
		default:
			// all other (unknown) APDUs are forwarded to the
			// PersoSimKernel
			return kernel.process(apdu);
		}
	}

	@Override
	public boolean isRunning() {
		return kernel != null;
	}
}
