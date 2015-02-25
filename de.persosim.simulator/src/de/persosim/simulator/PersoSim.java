package de.persosim.simulator;

import static de.persosim.simulator.utils.PersoSimLogger.INFO;
import static de.persosim.simulator.utils.PersoSimLogger.UI;
import static de.persosim.simulator.utils.PersoSimLogger.log;
import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.security.Security;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.osgi.framework.Bundle;

import de.persosim.simulator.jaxb.PersoSimJaxbContextProvider;
import de.persosim.simulator.perso.DefaultPersoTestPki;
import de.persosim.simulator.perso.MinimumPersonalization;
import de.persosim.simulator.perso.Personalization;
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
	private Personalization currentPersonalization = new DefaultPersoTestPki();
	
	public static final String LOG_NO_OPERATION = "nothing to process";
	public static final String LOG_SIM_EXIT     = "simulator exit";
	
	public static final String persoPlugin = "platform:/plugin/de.persosim.rcp/";
	public static final String persoPath = "personalization/profiles/";
	public static final String persoFilePrefix = "Profile";
	public static final String persoFilePostfix = ".xml";
	
	private PersoSimKernel kernel;
	
	static {
		//register BouncyCastle provider
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}
	
	/**
	 * This constructor is used by the OSGi-service instantiation
	 */
	public PersoSim(){
		currentPersonalization = new MinimumPersonalization();
	}
	
	public PersoSim(String... args) {
		this();
		try {
			CommandParser.handleArgs(this, args);
		} catch (IllegalArgumentException e) {
			log(this.getClass(), "simulation aborted, reason is: " + e.getMessage());
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
	
	/**
	 * This method parses a {@link Personalization} object from a file identified by its name.
	 * @param persoFileName the name of the file to contain the personalization
	 * @return the parsed personalization
	 * @throws FileNotFoundException 
	 * @throws JAXBException if parsing of personalization not successful
	 */
	public static Personalization parsePersonalization(String persoFileName) throws FileNotFoundException, JAXBException {
		File persoFile = new File(persoFileName);
		
		Unmarshaller um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
		log(PersoSim.class, "Parsing personalization from file " + persoFileName, INFO);
		return (Personalization) um.unmarshal(new FileReader(persoFile));
	}
	
	@Override
	public boolean loadPersonalization(String identifier) {
		currentPersonalization = null;

		//try to parse the given identifier as profile number
		try {
			int personalizationNumber = Integer.parseInt(identifier);
			log(this.getClass(), "trying to load personalization profile no: " + personalizationNumber, INFO);
			Bundle plugin = Activator.getContext().getBundle();
			
			if(plugin == null) {
				// TODO how to handle this case? Add OSGI requirement?
				log(this.getClass(), "unable to resolve bundle \"de.persosim.simulator\" - personalization unchanged", INFO);
				return false;
			} else {
				URL url = plugin.getResource(persoPath + persoFilePrefix + String.format("%02d", personalizationNumber) + persoFilePostfix);
				System.out.println("resolved absolute URL for selected profile is: " + url);
				identifier = url.getPath();
			}
		} catch (Exception e) {
			//seems to be a call to load a personalization by path
		}
		
		//actually load perso from the identified file
		try{
			currentPersonalization = parsePersonalization(identifier);
			return restartSimulator();
		} catch(FileNotFoundException | JAXBException e) {
			logException(this.getClass(), e);
			stopSimulator();
			log(this.getClass(), "simulation is stopped", INFO);
			return false;
		}
	}

	@Override
	public byte[] processCommand(byte[] apdu) {
	
		if (kernel == null){
			log(this.getClass(), "The simulator is stopped and the APDU was ignored", INFO);
			return NACK;
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
