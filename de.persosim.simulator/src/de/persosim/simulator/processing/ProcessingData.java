package de.persosim.simulator.processing;

import static org.globaltester.logging.PersoSimLogger.TRACE;
import static org.globaltester.logging.PersoSimLogger.WARN;
import static org.globaltester.logging.PersoSimLogger.log;

import java.util.HashMap;
import java.util.LinkedList;

import org.globaltester.logging.InfoSource;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.PlatformUtil;

/**
 * The primary purpose of this class is to store data about an APDU which
 * is accumulated while processing it.
 * 
 * Every entity that is involved during processing this APDU can use this to
 * exchange UpdatePropagation instances with other involved entities.
 * 
 * @author amay
 * @author slutters
 */
public class ProcessingData implements Iso7816, InfoSource {
	
	protected CommandApdu commandApdu;
	protected ResponseApdu responseApdu;
	
	protected HashMap<Class<? extends UpdatePropagation>, LinkedList<UpdatePropagation>> updatePropagations = new HashMap<>();
	
	protected LinkedList<ProcessingStateUpdate> processingHistory = new LinkedList<>();
	
	/*--------------------------------------------------------------------------------*/
	/* Variables concerning APDU processing status */
	/*--------------------------------------------------------------------------------*/

	public boolean isReportingError() {
		if (responseApdu != null) {
			return responseApdu.isReportingError();
		}
		return false;
	}
	
	/*--------------------------------------------------------------------------------*/

	public CommandApdu getCommandApdu() {
		return commandApdu;
	}

	public ResponseApdu getResponseApdu() {
		return responseApdu;
	}
	
	/**
	 * Incorporates the given updates into the current state of this object.
	 * UpdatePropagations are added to their respective lists, command and
	 * response-APDUs are updated with the new value.
	 * 
	 * @param source
	 *            Source that initiated this update
	 * @param message
	 *            User readable message shown in log
	 * @param update
	 *            deltas that contain the respective update specification
	 */
	public void updateProcessingState(InfoSource source, String message, ProcessingStateDelta... update) {
		//log modifications accordingly
		log(source, "Update processing state with " + update.length + " deltas.", TRACE);
		log(source, "Update message\n" + message, TRACE);
		for (ProcessingStateDelta curStateDelta : update) {
			if (curStateDelta != null && curStateDelta.getNrOfModifications() > 0) {
				// add to state history
				processingHistory.add(new ProcessingStateUpdate(source, message, curStateDelta));

				//log modifications accordingly
				log(source, curStateDelta.toString(), TRACE);
				
				// update command APDU if present
				if (curStateDelta.getCommandApdu() != null) {
					
					//check that current commandApdu is part of history of new commandApdu
					CommandApdu curPredecessor = curStateDelta.getCommandApdu();
					while (curPredecessor  != null) {
						if (curPredecessor == commandApdu) {
							break;
						}
						curPredecessor = curPredecessor.getPredecessor();
					}
					if (curPredecessor != commandApdu) {
						throw new IllegalArgumentException("New CommandApdu must have current CommandApdu in its line of predecessors");
					}
					
					
					this.commandApdu = curStateDelta.getCommandApdu();
					log(source, "Command APDU updated\n" + commandApdu, TRACE);
				}

				// update response APDU if present
				if (curStateDelta.getResponseApdu() != null) {
					this.responseApdu = curStateDelta.getResponseApdu();
					log(source, "Response APDU updated\n" + responseApdu + "\nreason is: " + message, TRACE);
				}
				
				// update updatePropagations if present
				if (curStateDelta.getUpdatePropagations() != null) {
					HashMap<Class<? extends UpdatePropagation>, UpdatePropagation> newPropagations = curStateDelta.getUpdatePropagations();
					for (Class<? extends UpdatePropagation> curKey : newPropagations.keySet()) {
						LinkedList<UpdatePropagation> curPropagations = this.updatePropagations.get(curKey);
						
						// create new list and add it to the HashMap if needed
						if (curPropagations == null) {
							curPropagations = new LinkedList<>();
							this.updatePropagations.put(curKey, curPropagations);
						}
						
						//skip this propagation if type does not math the curKey
						UpdatePropagation curNewProp = newPropagations.get(curKey);
						if (curKey.isInstance(curNewProp)) {
							// add current new propagation to the list 
							curPropagations.add(curNewProp);
						} else {
							log(this, "Skipping one UpdatePropagation, as type does not match key", WARN);
						}
					}
				}
				
			} 
		}
	}
	
	public boolean isProcessingFinished() {
		return (responseApdu != null && !PlatformUtil.is4xxxStatusWord(responseApdu.getStatusWord())) ;
	}

	public void updateCommandApdu(InfoSource source, String message, CommandApdu commandApdu) {
		updateProcessingState(source, message, new ProcessingStateDelta(commandApdu));
	}

	public void updateResponseAPDU(InfoSource source, String message, ResponseApdu respApdu) {
		updateProcessingState(source, message, new ProcessingStateDelta(respApdu));
	}

	@Override
	public String getIDString() {
		return "ProcessingData";
	}

	/**
	 * Return the list of UpdatePropagations for the given key.
	 * 
	 * The List as well as all its elements should be regarded as immutable and
	 * thus not be modified. Implementation might change to enforce this
	 * behavior for security reasons. If you need to modify this List you can
	 * provide new UpdatePropagations through
	 * {@link #addUpdatePropagation(InfoSource, String, UpdatePropagation)
	 * addProtocolUpdate()} or {
	 * {@link #updateProcessingState(InfoSource, String, ProcessingStateDelta...)
	 * updateProcessingState} method.
	 * 
	 * 
	 * @param key
	 *            Class for which UpdatePrpopagations are requested
	 * @return possibly empty List, but never null
	 */
	public LinkedList<UpdatePropagation> getUpdatePropagations(
			Class<? extends UpdatePropagation> key) {
		LinkedList<UpdatePropagation> retVal = updatePropagations.get(key);
		
		//ensure reVal is not null
		if (retVal == null) {
			retVal = new LinkedList<>();
		}

		return retVal;
	}

	/**
	 * Convenience method to add a new UpdatePropagation to the ProcessingData.
	 * 
	 * The provided UpatePropagation is added under the key returned by its
	 * getKey()-method. If adding under a different key is desired you need to
	 * build a custom PocessingStateDelta and provide it through 
	 * {@link #updateProcessingState(InfoSource, String, ProcessingStateDelta...) updateProcessingState} method.
	 * 
	 * @param source
	 *            Source that initiated this update
	 * @param message
	 *            User readable message shown in log
	 * @param updatePropagation
	 *            UpdatePropagation that shall be added
	 */
	public void addUpdatePropagation(InfoSource source, String message,
			UpdatePropagation updatePropagation) {
		updateProcessingState(source, message, new ProcessingStateDelta(updatePropagation));
	}

}
