package de.persosim.simulator.platform;

import static org.globaltester.logging.BasicLogger.log;
import static org.globaltester.logging.BasicLogger.logException;

import org.globaltester.logging.InfoSource;
import org.globaltester.logging.tags.LogLevel;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.exception.GeneralException;
import de.persosim.simulator.processing.ProcessingData;

/**
 * @author slutters
 *
 */
public abstract class Layer implements Iso7816, InfoSource {

	protected ProcessingData processingData;

	/**
	 * This method finalizes the layer so it can actually be used. It is to be
	 * called when the layer is ready to be used.
	 */
	abstract public void initializeForUse();
	
	/**
	 * Power-management function. This method is called by the
	 * {@link PersoSimKernel} to notify each layer of the simulated power on of
	 * the card.
	 * 
	 * Default implementation does nothing but logging. Subclasses are expected
	 * to override this behavior if needed.
	 */
	public void powerOn() {
		log(this, "powerOn, nothing needs to be done for this layer", LogLevel.TRACE);
	}

	/**
	 * Power-management function. This method is called by the
	 * {@link PersoSimKernel} to notify each layer of the simulated power off of
	 * the card.
	 * 
	 * Default implementation does nothing but logging. Subclasses are expected
	 * to override this behavior if needed.
	 */
	public void powerOff() {
		log(this, "powerOff, nothing needs to be done for this layer", LogLevel.TRACE);
	}
	
	/**
	 * Central processing routine for events handed up from a lower layer.
	 * Actual layer specific processing is done in {@link #processAscending()}
	 * @param pData processingData collected during processing of the APDU
	 */
	public final void processAscending(ProcessingData pData) {
		try{
			this.processingData = pData;
			processAscending();
			log(this, "successfully processed ascending APDU", LogLevel.TRACE);
		} catch(GeneralException e) {
			logException(this, e);

			//create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(e.getStatusWord());
			pData.updateResponseAPDU(this, "Generic error handling", resp);
		}
	}
	
	/**
	 * Layer specific processing of ascending APDUs.
	 * 
	 * Default implementation does nothing but logging. Subclasses are expected
	 * to override this behavior. In order to access the current processingData
	 * subclasses can rely on the value of {@link #processingData} which is
	 * guaranteed to be set during execution of this method.
	 * 
	 */
	public void processAscending() {
		log(this, "skipped processing of ascending APDU", LogLevel.TRACE);
	}
	
	/**
	 * Central processing routine for events handed down from a higher layer.
	 * Actual layer specific processing is done in {@link #processDescending()}
	 * @param pData processingData collected during processing of the APDU
	 */
	public final void processDescending(ProcessingData pData) {
		try{
			this.processingData = pData;
			this.processDescending();
		} catch(GeneralException e) {
			logException(this, e);
			
			//create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(e.getStatusWord());
			pData.updateResponseAPDU(this, "Generic error handling", resp);
		}
	}

	/**
	 * Layer specific processing of descending APDUs.
	 * 
	 * Default implementation does nothing but logging. Subclasses are expected
	 * to override this behavior. In order to access the current processingData
	 * subclasses can rely on the value of {@link #processingData} which is
	 * guaranteed to be set during execution of this method.
	 * 
	 */
	public void processDescending() {
		log(this, "skipped processing of descending APDU", LogLevel.TRACE);
	}
	
	/**
	 * Returns the human readable name of this layer. This identifier should
	 * allow the reader to distinguish the given layer from other layers as well
	 * as other implementations.
	 * 
	 * @return human readable name
	 */
	public abstract String getLayerName();
	
	@Override
	public String getIDString() {
		return "Layer " + getLayerName();
	}

	public ProcessingData getProcessingData() {
		return processingData;
	}
	
}
