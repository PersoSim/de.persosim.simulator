package de.persosim.simulator.documents;

/**
 * @author slutters
 *
 * Note: this class is under construction and will only provide basic functionality
 */
public class MrzTD2 extends Mrz {
	/* 2 lines of 36 characters each --> total length: 72 */
	public static final String[] defaultMRZ = new String[]{
		"ITD<<MUSTERMANN<<ERIKA<<<<<<<<<<<<<<C<00000004D<<6408125<1102015<<<<<<<6",
		"I<UTOSTEVENSON<<PETER<<<<<<<<<<<<<<<D231458907UTO3407127M9507122<<<<<<<2"
	};
	
	public MrzTD2(String mrz) {
		super(mrz);
	}
	
	@Override
	protected DocumentFormat getDocumentFormat() {
		return Mrz.td2;
	}
}
