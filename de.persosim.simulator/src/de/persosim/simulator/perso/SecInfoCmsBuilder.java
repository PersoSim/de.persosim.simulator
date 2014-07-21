package de.persosim.simulator.perso;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * This interface allows autmatic greation of SignedData files liek
 * EF.CardSecurity and EF.ChipSecurity.
 * 
 * @author amay
 * 
 */
public interface SecInfoCmsBuilder {

	/**
	 * Build the CMS SignedData structure that contains the SecInfos, as
	 * required for EF.CardSecurity and EF.ChipSecurity
	 * 
	 * @param secInfos
	 *            Set of SecurityInfos
	 * @return
	 */
	public abstract ConstructedTlvDataObject buildSignedData(
			ConstructedTlvDataObject secInfos);

}