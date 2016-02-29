package de.persosim.simulator.protocols;

import de.persosim.simulator.apdumatching.ApduSpecification;

/**
 * This interface defines methods that are commonly used from within state
 * machine code.
 * 
 * @author amay
 * 
 */
public interface ProtocolStateMachine extends Protocol {

	/**
	 * Shortcut method to add an existing ApduSpecification to the collection of
	 * APDUs supported by this protocol.
	 * 
	 * @param apduSpecification
	 */
	public abstract void registerApduSpecification(
			ApduSpecification apduSpecification);

	/**
	 * This is a convenience method for logging changes of state within the
	 * state machine. It is only to be called from the state machine and
	 * prepends the String "State changed to " to any provided state name.
	 * 
	 * @param state
	 *            the name of the state to be named
	 */
	public abstract void logs(String state);

	/**
	 * This method is used to stop the processing loop within state machine
	 * code. In order to enable the state machine to change states for an
	 * arbitrary number of times triggered by a single call, processing within
	 * state machine code is performed within a loop. If the loop is to be
	 * exited, this method must be called (from within).
	 */
	public abstract void returnResult();

	/**
	 * This method returns whether the current APDU has already been processed.
	 * @return whether the current APDU has already been processed
	 */
	public abstract boolean apduHasBeenProcessed();

	/**
	 * This method checks whether the received status word matches a certain
	 * expected (provided) status word.
	 * 
	 * @param sw
	 *            the expected status word
	 * @return true iff the received status word matches the expected one, false
	 *         otherwise
	 */
	public abstract boolean isStatusWord(short sw);

	/**
	 * This method checks whether the received status word contains counter
	 * information in the form of 0x63CX with X being the counter
	 * 
	 * @return true iff the received status word is a counter, false otherwise
	 */
	public abstract boolean isStatusWord_63CX_Counter();

	/**
	 * This method returns whether processing data reports any warnings or errors.
	 * @return whether processing data reports any warnings or errors
	 */
	public abstract boolean warningOrErrorOccurredDuringProcessing();

	/**
	 * Compare the APDU contained in current processingData with the
	 * ApduSpecification registered under the given Id.
	 * <p/>
	 * Returns false if either the current APDU or the requested
	 * ApduSpecification could not be found.
	 * 
	 * @param apduId
	 * @return
	 */
	public abstract boolean isAPDU(String apduId);

}
