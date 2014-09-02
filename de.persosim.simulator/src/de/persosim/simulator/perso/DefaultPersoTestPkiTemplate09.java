package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate09 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg3PlainData() {
		return "20161031";
	}
	
	@Override
	public String getEidDg4PlainData() {
		return "LILLY";
	}

	@Override
	public String getEidDg5PlainData() {
		return "SCHUSTER";
	}

	@Override
	public String getEidDg8PlainData() {
		return "20020330";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "MÜHLHAUSEN/THÜRINGEN";
	}

	@Override
	public String getEidDg11PlainData() {
		return "F";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv("MARIENSTRAßE 144", "EISENACH", null, "D", "99817");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dg17Tlv = null;
		}
		
		return dg17Tlv.toByteArray();
	}

	@Override
	public String getEidDg18PlainData() {
		return "02761600560000";
	}

	@Override
	public String getDocumentNumber() {
		return "000000009";
	}

	@Override
	public String getMrzLine3of3() {
		return "SCHUSTER<<LILLY<<<<<<<<<<<<<<<";
	}

}
