package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate10 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg4PlainData() {
		return "GHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGH";
	}

	@Override
	public String getEidDg5PlainData() {
		return "CDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCD";
	}
	
	@Override
	public String getEidDg6PlainData() {
		return "STSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTST";
	}
	
	@Override
	public String getEidDg7PlainData() {
		return "ABABABABABABABABABABABABABABABABABABABAB";
	}
	
	@Override
	public String getEidDg8PlainData() {
		return "18990502";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "IJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJ";
	}

	@Override
	public String getEidDg11PlainData() {
		return "M";
	}
	
	@Override
	public String getEidDg13PlainData() {
		return "EFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFE";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv("OPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOP", "MNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMN", null, "D", "12345");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dg17Tlv = null;
		}
		
		return dg17Tlv.toByteArray();
	}

	@Override
	public String getEidDg18PlainData() {
		return "02761100000000";
	}

	@Override
	public String getDocumentNumber() {
		return "000000010";
	}

	@Override
	public String getMrzLine3of3() {
		return "CDCDCDCDCDCDCD<<GHGHGHGHGHGHGH";
	}

}
