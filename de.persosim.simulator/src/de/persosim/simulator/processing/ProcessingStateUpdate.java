package de.persosim.simulator.processing;

import de.persosim.simulator.utils.InfoSource;

/**
 * Container class for storing the history of {@link ProcessingData} and its state updates.
 * @author amay
 *
 */
public class ProcessingStateUpdate {

	private InfoSource source;
	private String message;
	private ProcessingStateDelta stateDelta;

	public ProcessingStateUpdate(InfoSource initiator, String msg, ProcessingStateDelta stateDelta) {
		this.source = initiator;
		this.stateDelta = stateDelta;
		this.message = msg;
	}

	public String getInitiatorId() {
			return source.getIDString();
	}

	public ProcessingStateDelta getStateDelta() {
		return stateDelta;
	}
	
	public String getMessage(){
		return message;
	}	

}
