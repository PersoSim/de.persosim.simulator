package de.persosim.simulator.documents;

/**
 * 
 */
public class MrzFactory {
	
	private MrzFactory() {};
	
	public static Mrz parseMrz(String mrzString) {
		
		if (mrzString == null)
			throw new IllegalArgumentException("No MRZ given");
		
		mrzString = mrzString.replaceAll("\\s", "");
		
		int length = mrzString.length();
		
		switch (length) {
			case 90: return new MrzTD1(mrzString);
			case 72: return new MrzTD2(mrzString);
			case 88: return new MrzTD3(mrzString);
			default: throw new IllegalArgumentException("Unsupported length of MRZ");
		}
		
	}
	
}
