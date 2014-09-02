package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate01 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg4PlainData() {
		return "ERIKA";
	}

	@Override
	public String getEidDg5PlainData() {
		return "MUSTERMANN";
	}

	@Override
	public String getEidDg8PlainData() {
		return "19640812";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "BERLIN";
	}

	@Override
	public String getEidDg11PlainData() {
		return "F";
	}
	
	@Override
	public String getEidDg13PlainData() {
		return "GABLER";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv("HEIDESTRASSE 17", "KÖLN", null, "D", "51147");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dg17Tlv = null;
		}
		
		return dg17Tlv.toByteArray();
	}

	@Override
	public String getEidDg18PlainData() {
		return "02760503150000";
	}

	@Override
	public String getDocumentNumber() {
		return "000000001";
	}

	@Override
	public String getMrzLine3of3() {
		return "MUSTERMANN<<ERIKA<<<<<<<<<<<<<";
	}
	
}
