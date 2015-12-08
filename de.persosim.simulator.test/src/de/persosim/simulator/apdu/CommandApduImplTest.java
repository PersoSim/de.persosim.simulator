package de.persosim.simulator.apdu;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.tlv.TlvValuePlain;
import de.persosim.simulator.utils.HexString;

public class CommandApduImplTest extends PersoSimTestCase {
	
	@Test
	public void testImmutability() {
		byte[] cApduData = HexString.toByteArray("0022000003010203");
		CommandApduImpl cApdu = new CommandApduImpl(cApduData);
		
		byte[] cApduDataExpected = cApdu.toByteArray();
		
		TlvValue cDataTlvValue = cApdu.getCommandData();
		TlvValuePlain cDataTlvValuePlain = (TlvValuePlain) cDataTlvValue;
		
		cDataTlvValuePlain.setValueField(new TlvValuePlain(HexString.toByteArray("AABBCCDD")));
		
		byte[] cApduDataReceived = cApdu.toByteArray();
		
		assertArrayEquals(cApduDataExpected, cApduDataReceived);
	}
	
}
