package de.persosim.simulator;

import de.persosim.simulator.perso.Personalization;

/**
 * This interface defines the methods used for controlling the simulator. It is
 * used in the OSGi service definitions.
 * 
 * @author mboonk
 *
 */
public interface Simulator {

	/**
	 * This method handles instantiation and start of the SocketSimulator.
	 * Calling this method on an already running simulator does nothing.
	 * 
	 * After successfully starting the Simulator the simulator is able to
	 * process administrative FF apdus.
	 * 
	 * @return whether instantiation and starting was successful
	 */
	public abstract boolean startSimulator();

	/**
	 * Stops the simulator thread and returns when the thread is stopped.
	 * Calling this method on an already stopped simulator does nothing.
	 * @return whether stopping was successful
	 */
	public abstract boolean stopSimulator();

	/**
	 * This method restarts the simulator. It works on independent of running state.
	 * @return whether restarting has been successful
	 */
	public abstract boolean restartSimulator();

	/**
	 * @return whether the simulator is currently active
	 */
	public abstract boolean isRunning();
	
	/**
	 * This method returns the content of {@link #currentPersonalization}, the
	 * currently used personalization. If no personalization is set, i.e. the
	 * variable is null, it will be set to the default personalization which
	 * will be returned thereafter. This mode of accessing personalization
	 * opportunistic assumes that a personalization will always be set and
	 * generating a default personalization is an overhead only to be spent as a
	 * measure of last resort.
	 * 
	 * @return the currently used personalization
	 */
	public abstract Personalization getPersonalization();

	/**
	 * The given identifier is parsed and the corresponding personalization is
	 * loaded. If the identifier is a number, the profile with this number is
	 * loaded. Other inputs are interpreted as file names of personalization
	 * files.
	 * 
	 * @param identifier, the number or file name of the profile to load
	 * @return true, if the profile loading was successful
	 */
	public abstract boolean loadPersonalization(Personalization personalization);
	
	/**
	 * Handles APDUs. Control APDUs are filtered and the respective methods of
	 * the kernel are called. All other APDUs are simply forwarded to the
	 * kernels process() method.
	 * 
	 * @param apdu
	 * @return
	 */
	public abstract byte[] processCommand(byte[] apdu);

	/**
	 * This method powers the simulated card up. It is equivalent to moving a
	 * card to the readers field.
	 * 
	 * @return the ATR of the simulated card
	 */
	public abstract byte [] cardPowerUp();
	
	/**
	 * This method powers the simulated card down. It is equivalent to removing
	 * a a card from the readers field.
	 * 
	 * @return the status word
	 */
	public abstract byte [] cardPowerDown();
	
	/**
	 * This method resets the simulated card.
	 * @return the ATR of the simulated card
	 */
	public abstract byte [] cardReset();

}