package de.persosim.simulator.platform;

import static org.globaltester.logging.BasicLogger.logException;

import java.util.Iterator;
import java.util.LinkedList;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.exception.GeneralException;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.utils.HexString;

/**
 * This class implements the lowest Layer that is in charge of converting the
 * APDU received by hardware to a {@link de.persosim.simulator.apdu.CommandApdu}
 * while it is processed upwards and returning a byte[] apdu to the hardware
 * from a {@link de.persosim.simulator.apdu.ResponseApdu} while it is processed
 * downwards.
 * 
 * @author amay
 * 
 */
public class IoManager extends Layer {
	
	public static final String IOMANAGER = "IoManager";
	
	@Override
	public String getLayerName() {
		return IOMANAGER;
	}
	
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public boolean processAscending() {
		LinkedList<UpdatePropagation> hardwareCommandUpdates = processingData.getUpdatePropagations(HardwareCommandApduPropagation.class);
		
		boolean retVal = false;
		
		//update processingData for every HardwareCommandApduUpdate with a newCommandApdu
		for (Iterator<UpdatePropagation> iterator = hardwareCommandUpdates.iterator(); iterator
				.hasNext();) {
			UpdatePropagation updatePropagation = (UpdatePropagation) iterator
					.next();
			if (updatePropagation != null && updatePropagation instanceof HardwareCommandApduPropagation) {
				try {
					CommandApdu commandApdu = CommandApduFactory.createCommandApdu(((HardwareCommandApduPropagation)updatePropagation).getCommandApdu());
					
					processingData.updateCommandApdu(this, "CommandApduFactory.createCommandApdu from hardware : "+ commandApdu , commandApdu);
					retVal = true;
				} catch(GeneralException e) {
					logException(this, e);

					//create and propagate response APDU
					ResponseApdu resp = new ResponseApdu(e.getStatusWord());
					processingData.updateResponseAPDU(this, "Unable to handle this HardwareCommandApdu", resp);
				}
			}
		}
		return retVal;
	}
	
	@Override
	public void processDescending() {
		// convert the ResponseApdu
		HardwareResponseApduPropagation responseApduPropagation = new HardwareResponseApduPropagation(
				processingData.getResponseApdu().toByteArray());
		processingData.addUpdatePropagation(this, "Converted response APDU to hardware representation: "+ HexString.dump(responseApduPropagation.getResponseApdu()),
				responseApduPropagation);
	}

	@Override
	public void initializeForUse() {
		// nothing to do here
	}
	
}
