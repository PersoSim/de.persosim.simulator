package de.persosim.simulator.protocols.pin;

/**
 * @author slutters
 *
 */
//XXX make PinManagement more generic according to ISO7816 (e.g. remove references/expectations to specific passswords), tihs will make this interface obsolete
public interface Pin {
	
	public static final byte P2_02_CAN                = (byte) 0x02;
	public static final byte P2_03_PIN                = (byte) 0x03;
	
}
