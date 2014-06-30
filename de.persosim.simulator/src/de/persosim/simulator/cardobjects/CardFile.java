package de.persosim.simulator.cardobjects;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * This interface represents an ISO7816-4 compliant file in the object hierarchy
 * on the card.
 * 
 * @author amay
 * 
 */
public interface CardFile extends CardObject {
	
	/**
	 * @return the file control parameter data object as specified in ISO78164 5.3.3
	 */
	ConstructedTlvDataObject getFileControlParameterDataObject();
	
	/**
	 * @return the file management data object as specified in ISO78164 5.3.3
	 */
	ConstructedTlvDataObject getFileManagementDataObject();
}
