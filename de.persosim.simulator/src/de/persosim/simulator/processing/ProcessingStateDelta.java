package de.persosim.simulator.processing;

import java.util.HashMap;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;

public class ProcessingStateDelta extends ProcessingState {

	private CommandApdu commandApdu;
	private ResponseApdu responseApdu;

	private HashMap<Class<? extends UpdatePropagation>, UpdatePropagation> updatePropagations = new HashMap<>();

	/**
	 * Create a new ProcessingStateDelta which only updates the command APDU
	 * @param respApdu
	 */
	public ProcessingStateDelta(CommandApdu cmdApdu) {
		commandApdu = cmdApdu;
	}
	
	/**
	 * Create a new ProcessingStateDelta which only updates the response APDU
	 * @param respApdu
	 */
	public ProcessingStateDelta(ResponseApdu respApdu) {
		responseApdu = respApdu;
	}

	/**
	 * Create a new ProcessingStateDelta which only contains one
	 * updatePropagation.
	 * 
	 * The provided UpatePropagation is added under the key returned by its
	 * getKey()-method. If adding under a different key is desired you need to
	 * provide it through the {@link #putUpdatePropagation(Class<? extends
	 * UpdatePropagation>, UpdatePropagation) putUpdatePropagation} method
	 * instead.
	 * 
	 * @param respApdu
	 */
	public ProcessingStateDelta(UpdatePropagation updatePropagation) {
		if (updatePropagation != null) {
			updatePropagations.put(updatePropagation.getKey(), updatePropagation);
		}
	}

	public CommandApdu getCommandApdu() {
		return commandApdu;
	}

	public ResponseApdu getResponseApdu() {
		return responseApdu;
	}
	
	public HashMap<Class<? extends UpdatePropagation>, UpdatePropagation> getUpdatePropagations() {
		return updatePropagations;
	}

	public void putUpdatePropagation(Class<? extends UpdatePropagation> key, UpdatePropagation newPropagation) {
		if (!key.isInstance(newPropagation)) {
			throw new IllegalArgumentException("UpdatePropagation must be instance of the class used as key");
		}
		this.updatePropagations.put(key, newPropagation);
	}

	/**
	 * Returns the number of parts modified by this ProcessingStateDelta
	 * 
	 * @return number of parts modified by this ProcessingStateDelta
	 */
	public int getNrOfModifications() {
		int nrOfModifications = 0;

		if (commandApdu != null) {
			nrOfModifications++;
		}

		if (responseApdu != null) {
			nrOfModifications++;
		}

		if (updatePropagations != null) {
			nrOfModifications += updatePropagations.size();
		}

		return nrOfModifications;
	}

}
