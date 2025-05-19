package de.persosim.simulator.platform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.globaltester.logging.InfoSource;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.tlv.TlvValuePlain;
import de.persosim.simulator.utils.HexString;

public class EnvelopingTest implements InfoSource {
	@Test
	public void testGetResponseWithoutEnvelope() {
		Enveloping enveloping = new Enveloping();
		enveloping.initializeForUse();
		ProcessingData data = new ProcessingData();
		data.updateCommandApdu(this, "First APDU", CommandApduFactory.createCommandApdu(HexString.toByteArray("00B000001BAABBCCDDEEFF1122334455AABBCCDDEEFF1122334455667788990020")));
		
		
		enveloping.processAscending(data);
		
		data.updateResponseAPDU(this, "Upper Layer response", new ResponseApdu(new TlvValuePlain(HexString.toByteArray("AAAABBBBCCCCDDDDEEEEFFFF1111222233334444555566667777888899990000")), (short)0x6789));
		
		enveloping.processDescending(data);

		data = new ProcessingData();
		data.updateCommandApdu(this, "Get Response APDU", CommandApduFactory.createCommandApdu(HexString.toByteArray("00C0000008")));

		enveloping.processAscending(data);
		enveloping.processDescending(data);
		
		assertEquals(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED, data.getResponseApdu().getStatusWord());
		assertNull(data.getResponseApdu().getData());
	}
	
	@Test
	public void testEnvelopeWithLeField() {
		Enveloping enveloping = new Enveloping();
		enveloping.initializeForUse();
		ProcessingData data = new ProcessingData();
		data.updateCommandApdu(this, "First Envelope APDU with LE field", CommandApduFactory.createCommandApdu(HexString.toByteArray("10C200001000B000001BAABBCCDDEEFF112233445500")));
		
		
		enveloping.processAscending(data);
		enveloping.processDescending(data);
		
		assertEquals((short)0x9000, data.getResponseApdu().getStatusWord());
		assertNull(data.getResponseApdu().getData());

		data = new ProcessingData();
		CommandApdu apdu = CommandApduFactory.createCommandApdu(HexString.toByteArray("00C2000011AABBCCDDEEFF112233445566778899002010"));
		data.updateCommandApdu(this, "Last Envelope APDU", apdu);

		enveloping.processAscending(data);
		
		assertSame(apdu, data.getCommandApdu());
		
		enveloping.processDescending(data);
		
		assertEquals((short)0x6883, data.getResponseApdu().getStatusWord());
		assertNull(data.getResponseApdu().getData());
	}
	
	@Test
	public void testProcess() {
		Enveloping enveloping = new Enveloping();
		enveloping.initializeForUse();
		ProcessingData data = new ProcessingData();
		data.updateCommandApdu(this, "First Envelope APDU", CommandApduFactory.createCommandApdu(HexString.toByteArray("10C200001000B000001BAABBCCDDEEFF1122334455")));
		
		
		enveloping.processAscending(data);
		enveloping.processDescending(data);
		
		assertEquals((short)0x9000, data.getResponseApdu().getStatusWord());
		assertNull(data.getResponseApdu().getData());

		data = new ProcessingData();
		data.updateCommandApdu(this, "Last Envelope APDU", CommandApduFactory.createCommandApdu(HexString.toByteArray("00C2000010AABBCCDDEEFF11223344556677889900")));

		enveloping.processAscending(data);
		assertArrayEquals(HexString.toByteArray("00B000001BAABBCCDDEEFF1122334455AABBCCDDEEFF11223344556677889900"), data.getCommandApdu().toByteArray());
		
		data.updateResponseAPDU(this, "Upper layer response", new ResponseApdu(new TlvValuePlain(HexString.toByteArray("AAAABBBBCCCCDDDDEEEEFFFF1111222233334444555566667777888899990000")), (short)0x6789));
		
		enveloping.processDescending(data);
		
		assertEquals((short)0x6122, data.getResponseApdu().getStatusWord());
		assertEquals(null, data.getResponseApdu().getData());

		data = new ProcessingData();
		data.updateCommandApdu(this, "First Get Response APDU", CommandApduFactory.createCommandApdu(HexString.toByteArray("00C0000008")));

		enveloping.processAscending(data);
		enveloping.processDescending(data);
		
		assertEquals((short)0x611A, data.getResponseApdu().getStatusWord());
		assertArrayEquals(HexString.toByteArray("AAAABBBBCCCCDDDD"), data.getResponseApdu().getData().toByteArray());
		
		data = new ProcessingData();
		data.updateCommandApdu(this, "Intermediate Response APDU", CommandApduFactory.createCommandApdu(HexString.toByteArray("00C0000004")));

		enveloping.processAscending(data);
		enveloping.processDescending(data);
		
		assertEquals((short)0x6116, data.getResponseApdu().getStatusWord());
		assertArrayEquals(HexString.toByteArray("EEEEFFFF"), data.getResponseApdu().getData().toByteArray());
		
		data = new ProcessingData();
		data.updateCommandApdu(this, "Last Get Response APDU", CommandApduFactory.createCommandApdu(HexString.toByteArray("00C0000016")));

		enveloping.processAscending(data);
		enveloping.processDescending(data);
		
		assertEquals((short)0x9000, data.getResponseApdu().getStatusWord());
		assertArrayEquals(HexString.toByteArray("11112222333344445555666677778888999900006789"), data.getResponseApdu().getData().toByteArray());
	}

	@Override
	public String getIDString() {
		return "TEST";
	}
}
