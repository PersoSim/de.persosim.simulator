package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate06 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg4PlainData() {
		return "Hans-Günther";
	}

	@Override
	public String getEidDg5PlainData() {
		return "von Drebenbusch-Dalgoßen";
	}
	
	@Override
	public String getEidDg6PlainData() {
		return "Freiherr zu Möckern-Windensberg";
	}
	
	@Override
	public String getEidDg7PlainData() {
		return "Dr.eh.Dr.";
	}

	@Override
	public String getEidDg8PlainData() {
		return "19460125";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "BREMERHAVEN";
	}

	@Override
	public String getEidDg11PlainData() {
		return "M";
	}
	
	@Override
	public String getEidDg13PlainData() {
		return "Weiß";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv("WEG NR. 12 8E", "HAMBURG", null, "D", "22043");
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
		return "000000006";
	}

	@Override
	public String getMrzLine3of3() {
		return "VONDREBENBUSCHDALGOSSEN<<HANS<";
	}

}
