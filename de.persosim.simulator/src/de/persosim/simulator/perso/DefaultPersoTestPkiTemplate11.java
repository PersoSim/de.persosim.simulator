package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate11 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg4PlainData() {
		return "HILDEGARD";
	}

	@Override
	public String getEidDg5PlainData() {
		return "MÜLLER";
	}

	@Override
	public String getEidDg8PlainData() {
		return "19390204";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "SAARBRÜCKEN";
	}

	@Override
	public String getEidDg11PlainData() {
		return "F";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv("HARKORTSTR. 58", "DORTMUND", null, "D", "44225");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dg17Tlv = null;
		}
		
		return dg17Tlv.toByteArray();
	}

	@Override
	public String getEidDg18PlainData() {
		return "02760509130000";
	}

	@Override
	public String getDocumentNumber() {
		return "000000011";
	}

	@Override
	public String getMrzLine3of3() {
		return "MUELLER<<HILDEGARD<<<<<<<<<<<<";
	}

}
