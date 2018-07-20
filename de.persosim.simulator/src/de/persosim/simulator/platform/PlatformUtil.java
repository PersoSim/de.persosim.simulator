package de.persosim.simulator.platform;

/**
 * This class contains utility methods for the simulator platform.
 * @author mboonk
 *
 */
public class PlatformUtil {
	//These statuswords are NOT ISO7816 compliant and are used internally to signal 
	//that a given APDU could not be processed, but did not create fatal errors
	//If processing of an APDU only produced an 4xxx SW this is converted to its 6xxx equivalent
	public static final short SW_4984_REFERENCE_DATA_NOT_USABLE			 = (short) 0x4984;
	public static final short SW_4A00_WRONG_PARAMETERS_P1P2              = (short) 0x4A00;
	public static final short SW_4A80_WRONG_DATA                         = (short) 0x4A80;
	public static final short SW_4A81_FUNC_NOT_SUPPORTED                 = (short) 0x4A81;
	public static final short SW_4A82_FILE_NOT_FOUND                     = (short) 0x4A82;
	public static final short SW_4A83_RECORD_NOT_FOUND                   = (short) 0x4A83;
	public static final short SW_4A84_FILE_FULL                          = (short) 0x4A84;
	public static final short SW_4A85_NC_INCONSISTENT_WITH_TLV_STRUCTURE = (short) 0x4A85;
	public static final short SW_4A86_INCORRECT_PARAMETERS_P1P2          = (short) 0x4A86;
	public static final short SW_4A88_REFERENCE_DATA_NOT_FOUND           = (short) 0x4A88;

	public static final short SW_4982_SECURITY_STATUS_NOT_SATISFIED = (short) 0x4982;
	public static final short SW_4985_CONDITIONS_OF_USE_NOT_SATISFIED    = (short) 0x4985;
	
	
	public static final short MASK_STATUS_WORD_4XXX = 0b0100000000000000;
	public static final short MASK_STATUS_WORD_6XXX = 0b0110000000000000;

	public static boolean is4xxxStatusWord(short statusWord) {
		return (short) (statusWord & MASK_STATUS_WORD_6XXX) == MASK_STATUS_WORD_4XXX;
	}

	public static boolean is6xxxStatusWord(short statusWord) {
		return (short) (statusWord & MASK_STATUS_WORD_6XXX) == MASK_STATUS_WORD_6XXX;
	}
	
	/**
	 * Convert an internal signaling SW 4xxx to an equivalent 6xxx SW that can
	 * be legally returned through the smart card interface. 
	 *
	 * @param statusWord an 4xxx statusword as defined in constants of this class 
	 * @return the equivalent 6xxx representation
	 * @throws IllegalArgumentException
	 */
	public static short convert4xxxTo6xxxStatusWord(short statusWord){
		if (is4xxxStatusWord(statusWord)){
			return (short) (statusWord | MASK_STATUS_WORD_6XXX);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Convert an 6xxx SW to an equivalent internal signaling SW 4xxx.
	 *
	 * @param statusWord
	 * @return the equivalent 4xxx representation
	 * @throws IllegalArgumentException
	 */
	public static short convertTo4xxxStatusWord(short statusWord){
		return (short) ((statusWord & 0x0FFF) | 0x4000);

	}
}
