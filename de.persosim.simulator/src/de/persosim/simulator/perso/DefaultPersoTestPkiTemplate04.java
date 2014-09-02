package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate04 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg3PlainData() {
		return "20161031";
	}
	
	@Override
	public String getEidDg4PlainData() {
		return "---";
	}

	@Override
	public String getEidDg5PlainData() {
		return "ĆOSIĆ";
	}

	@Override
	public String getEidDg8PlainData() {
		return "199409  ";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "PRIŠTINA";
	}

	@Override
	public String getEidDg11PlainData() {
		return "M";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv("F4 14-15", "MANNHEIM", null, "D", "68159");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dg17Tlv = null;
		}
		
		return dg17Tlv.toByteArray();
	}

	@Override
	public String getEidDg18PlainData() {
		return "02760802220000";
	}

	@Override
	public String getDocumentNumber() {
		return "000000004";
	}

	@Override
	public String getMrzLine3of3() {
		return "COSIC<<<<<<<<<<<<<<<<<<<<<<<<<";
	}

}
