package de.persosim.simulator.perso;

import java.io.UnsupportedEncodingException;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate03 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public String getEidDg4PlainData() {
		return "JOHANNA EDELTRAUT LISBETH";
	}

	@Override
	public String getEidDg5PlainData() {
		return "MUSTERMANN";
	}
	
	@Override
	public String getEidDg6PlainData() {
		return "ORDENSSCHWESTER JOHANNA";
	}
	
	@Override
	public String getEidDg7PlainData() {
		return "DR.";
	}

	@Override
	public String getEidDg8PlainData() {
		return "19280421";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "MÜNCHEN";
	}

	@Override
	public String getEidDg11PlainData() {
		return "F";
	}
	
	@Override
	public String getEidDg13PlainData() {
		return "VON MÜLLER-SCHWARZENBERG";
	}
	
	@Override
	public byte[] getEidDg17Data() {
		ConstructedTlvDataObject dg17Tlv; 
		
		try {
			dg17Tlv = DefaultPersoTestPkiTemplate.createEidDg17Tlv("BOUCHÉSTR. 68 A", "BERLIN", null, "D", "12059");
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
		return "000000003";
	}

	@Override
	public String getMrzLine3of3() {
		return "MUSTERMANN<<JOHANNA EDELTRAUT<";
	}

}
