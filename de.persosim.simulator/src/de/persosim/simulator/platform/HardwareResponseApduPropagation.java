package de.persosim.simulator.platform;

import de.persosim.simulator.processing.UpdatePropagation;

/**
 * 
 * This UpdatePropagation holds the response APDU as provided by the upper
 * layers and to be transmitted by the hardware layer.
 * 
 * The PersoSimKernel expects to find one of these UpdatePropagations in the
 * ProcessingData when processing was finished. The lowest layer (IoManager) is
 * responsible to convert the ResponseAPDU stored in the processingData to the
 * hardware compatible format.
 * 
 * PersoSimKernel wil use the last provided response or return 6F00 if no
 * response is available.
 * 
 * @author amay
 * 
 */
public class HardwareResponseApduPropagation implements UpdatePropagation {

	@Override
	public Class<? extends UpdatePropagation> getKey() {
		return HardwareResponseApduPropagation.class;
	}

	byte[] responseApdu = null;

	public HardwareResponseApduPropagation(byte[] responseApdu) {
		super();
		this.responseApdu = responseApdu;
	}

	public byte[] getResponseApdu() {
		return responseApdu;
	}

}
