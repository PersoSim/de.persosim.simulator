package de.persosim.simulator.platform;

import de.persosim.simulator.processing.UpdatePropagation;

/**
 * This UpdatePropagation holds the original APDU as received from hardware
 * layer and provides it to the upper layers during processing.
 * 
 * This is the initial content of the ProcessingData and shall be transformed to
 * a new CommandApdu by the first Layer (IoManager).
 * 
 * @author amay
 * 
 */
public class HardwareCommandApduPropagation implements UpdatePropagation {

	@Override
	public Class<? extends UpdatePropagation> getKey() {
		return HardwareCommandApduPropagation.class;
	}

	byte[] commandApdu = null;
	
	public HardwareCommandApduPropagation(byte[] commandApdu) {
		super();
		this.commandApdu = commandApdu;
	}

	public byte[] getCommandApdu() {
		return commandApdu;
	}

	
	
	

}
