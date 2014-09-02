package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate02 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg4PlainData() {
		return "ANDRÉ";
	}

	@Override
	public String getEidDg5PlainData() {
		return "MUSTERMANN";
	}

	@Override
	public String getEidDg8PlainData() {
		return "19810617";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "FRANKFURT (ODER)";
	}

	@Override
	public String getEidDg11PlainData() {
		return "M";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv("EHM-WELK-STRAßE 33", "LÜBBENAU/SPREEWALD", null, "D", "03222");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dg17Tlv = null;
		}
		
		return dg17Tlv.toByteArray();
	}

	@Override
	public String getEidDg18PlainData() {
		return "02761200660196";
	}

	@Override
	public String getDocumentNumber() {
		return "000000002";
	}

	@Override
	public String getMrzLine3of3() {
		return "MUSTERMANN<<ANDRE<<<<<<<<<<<<<";
	}

}
