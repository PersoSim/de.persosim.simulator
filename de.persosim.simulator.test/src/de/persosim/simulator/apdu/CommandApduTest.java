package de.persosim.simulator.apdu;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class CommandApduTest extends PersoSimTestCase {
	/**
	 * Creates a CommandApdu from a string representation. This string
	 * representation can be either a plain HexString or similar to the output
	 * of {@link CommandApduImpl#toString()}
	 * 
	 * @param apduString
	 * @return
	 */
	private CommandApdu createApduFromString(String apduString) {
		String inputStr = apduString;
		inputStr = inputStr.replaceAll("\\(", "").replaceAll("\\)","");
		inputStr = inputStr.replaceAll("\\[", "").replaceAll("\\]","");
		inputStr = inputStr.replaceAll("\\|", "");

		return new CommandApduImpl(HexString.toByteArray(inputStr));
	}
	
	/**
	 * Common method that implements similar check required for all APDU types.
	 * 
	 * This tests default construction, toString() and toByteArray().
	 * 
	 * @param apduString
	 *            expected String representation, the required input is
	 *            extracted from this by removing all formatting characters.
	 * @param isExtendedLength
	 *            expected return value of
	 *            {@link CommandApdu#isExtendedLength()}
	 * @param isNeZeroEncoded
	 *            expected return value of {@link CommandApdu#isNeZeroEncoded()}
	 * @param ne
	 *            expected return value of {@link CommandApdu#getNe()}
	 */
	private void createAndCheck(String apduString, boolean isExtendedLength, boolean isNeZeroEncoded, int ne) {
		String inputStr = apduString;
		inputStr = inputStr.replaceAll("\\(", "").replaceAll("\\)","");
		inputStr = inputStr.replaceAll("\\[", "").replaceAll("\\]","");
		inputStr = inputStr.replaceAll("\\|", "");
		byte[] input = HexString.toByteArray(inputStr);

		CommandApdu cmdApdu = createApduFromString(inputStr);
		
		assertArrayEquals("toByteArray()", input, cmdApdu.toByteArray());
		assertEquals("toString()", apduString, cmdApdu.toString());

		assertEquals("isExtendedLength()", isExtendedLength, cmdApdu.isExtendedLength());
		assertEquals("isNeZeroEncoded()", isNeZeroEncoded, cmdApdu.isNeZeroEncoded());
	}
	
	@Test
	public void testIsoCase1() {
		String apduString = "00800000";
		createAndCheck(apduString, false, false, 0);
	}
	
	@Test
	public void testIsoCase2() {
		String apduString = "00800000|01";
		createAndCheck(apduString, false, false, 1);
	}
	
	@Test
	public void testIsoCase2_MsbSet() {
		String apduString = "00800000|80";
		createAndCheck(apduString, false, false, 128);
	}
	
	@Test
	public void testIsoCase2_MsbSetMax() {
		String apduString = "00800000|FF";
		createAndCheck(apduString, false, false, 255);
	}
	
	@Test
	public void testIsoCase2_Le00() {
		String apduString = "00800000|00";
		createAndCheck(apduString, false, true, 256);
	}
	
	@Test
	public void testIsoCase2Extended() {
		String apduString = "00800000|000001";
		createAndCheck(apduString, true, false, 1);
	}
	
	@Test
	public void testIsoCase2Extended_MsbSet() {
		String apduString = "00800000|008000";
		createAndCheck(apduString, true, false, 32768);
	}
	
	@Test
	public void testIsoCase2Extended_MsbSetMax() {
		String apduString = "00800000|00FFFF";
		createAndCheck(apduString, true, false, 65535);
	}
	
	@Test
	public void testIsoCase2Extended_Le00() {
		String apduString = "00800000|000000";
		createAndCheck(apduString, true, true, 65536);
	}
	
	@Test
	public void testIsoCase3() {
		String apduString = "00800000|01|FF";
		createAndCheck(apduString, false, false, 0);
	}
	
	@Test
	public void testIsoCase3Extended() {
		String apduString = "00800000|000001|FF";
		createAndCheck(apduString, true, false, 0);
	}
	
	@Test
	public void testIsoCase4() {
		String apduString = "00800000|01|FF|01";
		createAndCheck(apduString, false, false, 1);
	}
	
	@Test
	public void testIsoCase4_MsbSet() {
		String apduString = "00800000|01|FF|80";
		createAndCheck(apduString, false, false, 128);
	}
	
	@Test
	public void testIsoCase4_MsbSetMax() {
		String apduString = "00800000|01|FF|FF";
		createAndCheck(apduString, false, false, 255);
	}
	
	@Test
	public void testIsoCase4_Le00() {
		String apduString = "00800000|01|FF|00";
		createAndCheck(apduString, false, true, 256);
	}
	
	@Test
	public void testIsoCase4Extended() {
		String apduString = "00800000|000001|FF|0001";
		createAndCheck(apduString, true, false, 1);
	}
	
	@Test
	public void testIsoCase4Extended_MsbSet() {
		String apduString = "00800000|000001|FF|8000";
		createAndCheck(apduString, true, false, 32768);
	}
	
	@Test
	public void testIsoCase4Extended_MsbSetMax() {
		String apduString = "00800000|000001|FF|FFFF";
		createAndCheck(apduString, true, false, 65535);
	}
	
	@Test
	public void testIsoCase4Extended_Le00() {
		String apduString = "00800000|000001|FF|0000";
		createAndCheck(apduString, true, true, 65536);
	}

	@Test
	public void testIsNeZeroEncoded_extended000100(){
		CommandApdu commandApdu = createApduFromString("00800000|000100");
		
		//call mut
		assertFalse(commandApdu.isNeZeroEncoded());
	}

}
