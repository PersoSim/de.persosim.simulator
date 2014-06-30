package de.persosim.simulator.protocols.pin;

/**
 * @author slutters
 *
 */
//XXX make PinManagement more generic according to ISO7816 (e.g. remove references/expectations to specific passswords), tihs will make this interface obsolete
public interface Pin {
	
	public static final byte P1_02_UNBLOCK_AND_CHANGE = (byte) 0x02; // for PIN
	public static final byte P1_02_CHANGE             = (byte) 0x02; // for CAN (CAN by definition non-blocking)
	public static final byte P1_03_UNBLOCK            = (byte) 0x03;
	
	public static final byte P2_02_CAN                = (byte) 0x02;
	public static final byte P2_03_PIN                = (byte) 0x03;
	
}
