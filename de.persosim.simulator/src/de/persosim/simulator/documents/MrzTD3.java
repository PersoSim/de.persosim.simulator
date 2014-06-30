package de.persosim.simulator.documents;

/**
 * @author slutters
 *
 * Note: this class is under construction and will only provide basic functionality
 */
public class MrzTD3 extends Mrz {
	/* 2 lines of 44 characters each --> total length: 88 */
	public static final String[] defaultMRZ = new String[]{
		"P<UTOERIKSSON<<ANNA<MARIA<<<<<<<<<<<<<<<<<<<L898902C<3UTO6908061F9406236ZE184226B<<<<<14"
	};
	
	public MrzTD3(String mrz) {
		super(mrz);
	}
	
	@Override
	protected DocumentFormat getDocumentFormat() {
		return Mrz.mrp;
	}
}
