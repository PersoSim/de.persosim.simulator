package de.persosim.simulator.platform;

import static org.globaltester.logging.BasicLogger.log;
import static org.globaltester.logging.BasicLogger.logException;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.InfoSource;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.exception.GeneralException;
import de.persosim.simulator.log.PersoSimLogTags;
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
	public abstract void initializeForUse();

	/**
	 * Power-management function. This method is called by the
	 * {@link PersoSimKernel} to notify each layer of the simulated power on of
	 * the card.
	 *
	 * Default implementation does nothing but logging. Subclasses are expected
	 * to override this behavior if needed.
	 */
	public void powerOn() {
		log("powerOn, nothing needs to be done for this layer", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
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
		log("powerOff, nothing needs to be done for this layer", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
	}

	/**
	 * Central processing routine for events handed up from a lower layer.
	 * Actual layer specific processing is done in {@link #processAscending()}
	 * @param pData processingData collected during processing of the APDU
	 */
	public final boolean processAscending(ProcessingData pData) {
		try{
			this.processingData = pData;
			boolean processFurther = processAscending();
			log("successfully processed ascending APDU", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			return processFurther;
		} catch(GeneralException e) {
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

			//create and propagate response APDU
			ResponseApdu resp = new ResponseApdu(e.getStatusWord());
			pData.updateResponseAPDU(this, "Generic error handling", resp);
		}
		return false;
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
	public boolean processAscending() {
		log("skipped processing of ascending APDU", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		return true;
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
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

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
		log("skipped processing of descending APDU", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
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
