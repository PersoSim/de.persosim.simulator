package de.persosim.simulator.documents;

/**
 * @author slutters
 *
 * Note: this class is under construction and will only provide basic functionality
 */
public class MrzTD1 extends Mrz {
	/* 3 lines of 30 characters each --> total length: 90 */
	public static final String[] defaultMRZ = new String[]{
		"P<D<<C11T002JM4<<<<<<<<<<<<<<<9608122F2310314D<<<<<<<<<<<<<4MUSTERMANN<<ERIKA<<<<<<<<<<<<<"
	};
	
	public MrzTD1(String mrz) {
		super(mrz);
	}
	
	@Override
	protected DocumentFormat getDocumentFormat() {
		return Mrz.td1;
	}
}
