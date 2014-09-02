package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate07 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg4PlainData() {
		return "ANNEKATHRIN";
	}

	@Override
	public String getEidDg5PlainData() {
		return "LERCH";
	}

	@Override
	public String getEidDg8PlainData() {
		return "19760705";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "BAD KÖNIGSHOFEN I. GRABFELD";
	}

	@Override
	public String getEidDg11PlainData() {
		return "F";
	}
	
	@Override
	public String getEidDg13PlainData() {
		return "BJØRNSON";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv(null, "HALLE (SAALE)", null, "D", "06108");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dg17Tlv = null;
		}
		
		return dg17Tlv.toByteArray();
	}

	@Override
	public String getEidDg18PlainData() {
		return "02760200000000";
	}

	@Override
	public String getDocumentNumber() {
		return "000000007";
	}

	@Override
	public String getMrzLine3of3() {
		return "LERCH<<ANNEKATHRIN<<<<<<<<<<<<";
	}
}
