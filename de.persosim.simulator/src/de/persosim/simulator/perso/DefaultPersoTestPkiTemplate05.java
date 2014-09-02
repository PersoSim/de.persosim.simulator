package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate05 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg3PlainData() {
		return "20161031";
	}
	
	@Override
	public String getEidDg4PlainData() {
		return "AĞÇA";
	}

	@Override
	public String getEidDg5PlainData() {
		return "ÖZM̂EN";
	}

	@Override
	public String getEidDg8PlainData() {
		return "1989    ";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "DESSAU-ROßLAU";
	}

	@Override
	public String getEidDg11PlainData() {
		return "F";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv("GROßENHAINER STR. 133/135", "DRESDEN", null, "D", "01129");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dg17Tlv = null;
		}
		
		return dg17Tlv.toByteArray();
	}

	@Override
	public String getEidDg18PlainData() {
		return "02761406120000";
	}

	@Override
	public String getDocumentNumber() {
		return "000000005";
	}

	@Override
	public String getMrzLine3of3() {
		return "OEZMEN<<AGCA<<<<<<<<<<<<<<<<<<";
	}

}
