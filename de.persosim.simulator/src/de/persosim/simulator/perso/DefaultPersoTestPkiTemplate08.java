package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate08 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg4PlainData() {
		return "KARL";
	}

	@Override
	public String getEidDg5PlainData() {
		return "HILLEBRANDT";
	}
	
	@Override
	public String getEidDg6PlainData() {
		return "GRAF V. LÝSKY";
	}
	
	@Override
	public String getEidDg7PlainData() {
		return "DR.HC.";
	}

	@Override
	public String getEidDg8PlainData() {
		return "19520617";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "TRIER";
	}

	@Override
	public String getEidDg11PlainData() {
		return "M";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv(null, null, null, null, null);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dg17Tlv = null;
		}
		
		return dg17Tlv.toByteArray();
	}

	@Override
	public String getEidDg18PlainData() {
		return "";
	}

	@Override
	public String getDocumentNumber() {
		return "000000008";
	}

	@Override
	public String getMrzLine3of3() {
		return "GRAF<VON<LYSKY<<KARL<<<<<<<<<<";
	}

}
