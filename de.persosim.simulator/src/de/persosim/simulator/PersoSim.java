package de.persosim.simulator;

import static de.persosim.simulator.utils.PersoSimLogger.INFO;
import static de.persosim.simulator.utils.PersoSimLogger.UI;
import static de.persosim.simulator.utils.PersoSimLogger.log;
import static de.persosim.simulator.utils.PersoSimLogger.logException;

import org.globaltester.simulator.Simulator;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Personalization;
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
	
	private PersoSimKernel kernel;
	private boolean running = false;
	
	/**
	 * This constructor is used by the OSGi-service instantiation
	 */
	public PersoSim(){
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
	}
	
	@Override
	public boolean startSimulator() {
		if (running) {
			log(this.getClass(), "Simulator already running", UI);
			return true;
		}
		
		running = true;
		log(this.getClass(), "The simulator has been started", PersoSimLogger.UI);
		
		return true;
	}
	
	@Override
	public boolean stopSimulator() {		
		if (running) {
			running = false;
			log(this.getClass(), "The simulator has been stopped and will no longer respond to incoming APDUs until it is (re-) started", UI);
			return true;
		}
		log(this.getClass(), "The simulator is already stopped", UI);
		return false;
	}
	
	@Override
	public boolean restartSimulator() {
		stopSimulator();
		return startSimulator();
	}
	
	/**
	 * This methods loads the provided personalization.
	 * 
	 * @param personalization the personalization to load
	 * @return true, if the profile loading was successful, otherwise false
	 */
	public boolean loadPersonalization(Personalization personalization) {
		currentPersonalization = personalization;
		
		try {
			kernel = new PersoSimKernel();
		} catch (AccessDeniedException e) {
			logException(this.getClass(), e, PersoSimLogger.ERROR);
			return false;
		}
		kernel.init(currentPersonalization);
		
		return true;
	}

	@Override
	public byte[] processCommand(byte[] apdu) {

		if (!running){
			log(this.getClass(), "The simulator is stopped and the APDU was ignored", INFO);
			return new byte[]{0x6f, (byte)0x85};
		}
		if (kernel == null){
			log(this.getClass(), "The simulator is not initialized and the APDU was ignored", INFO);
			return new byte[]{0x6f, 0x78};
		}
		
		return kernel.process(apdu);
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public byte[] cardPowerUp() {
		if (!running){
			log(this.getClass(), "The simulator is stopped, attempt to power up ignored", INFO);
			return new byte[]{0x6f, 0x79};
		}
		if (kernel == null){
			log(this.getClass(), "The simulator is not initialized, attempt to power up ignored", INFO);
			return new byte[]{0x6f, (byte)0x82};
		}
		return kernel.powerOn();
	}

	@Override
	public byte[] cardPowerDown() {
		if (!running){
			log(this.getClass(), "The simulator is stopped, attempt to power down ignored", INFO);
			return new byte[]{0x6f, (byte)0x80};
		}
		if (kernel == null){
			log(this.getClass(), "The simulator is not initialized, attempt to power up ignored", INFO);
			return new byte[]{0x6f, (byte)0x83};
		}
		return kernel.powerOff();
	}

	@Override
	public byte[] cardReset() {
		if (!running){
			log(this.getClass(), "The simulator is stopped, attempt to reset ignored", INFO);
			return new byte[]{0x6f, (byte)0x81};
		}
		if (kernel == null){
			log(this.getClass(), "The simulator is not initialized, attempt to power up ignored", INFO);
			return new byte[]{0x6f, (byte)0x84};
		}
		return kernel.reset();
	}
}
