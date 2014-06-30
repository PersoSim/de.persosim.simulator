package de.persosim.simulator.platform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.tlv.TlvValuePlain;

public class IoManagerTest extends PersoSimTestCase {

	/**
	 * Test instance
	 */
	private IoManager ioManager = new IoManager(0);

	/**
	 * Provide a simple APDU via HardwareCommandApduPropagation in
	 * {@link de.persosim.simulator.processing.ProcessingData ProcessingData}
	 * and ensure that it is converted to a CommandApdu object in
	 * {@link de.persosim.simulator.processing.ProcessingData#commandApdu
	 * ProcessingData.commandApdu} when processed upwards.
	 */
	@Test
	public void testConversionOfCommandApdu() {
		// prepare processingData
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = new byte[] { 0x00, (byte) 0x84, 0x00, 0x00, 0x08 };
		HardwareCommandApduPropagation apduPropagation = new HardwareCommandApduPropagation(
				apduBytes);
		processingData.addUpdatePropagation(this, "testConversionOfCommandApdu",
				apduPropagation);

		// call mut
		ioManager.processAscending(processingData);

		//extract/check converted CommandApdu
		CommandApdu commandApdu = processingData.getCommandApdu();
		assertNotNull("commandApdu was not created", commandApdu);
		assertArrayEquals("converted commandApdu does not match input",
				apduBytes, commandApdu.toByteArray());
	}

	/**
	 * Provide a simple ResponseApdu in
	 * {@link de.persosim.simulator.processing.ProcessingData ProcessingData}
	 * and ensure that it is converted to a HardwareResponseApduPropagation
	 * object in when processed downwards.
	 */
	@Test
	public void testConversionOfOResponseApdu() {
		// prepare processingData
		ProcessingData processingData = new ProcessingData();
		TlvValue respData = new TlvValuePlain(new byte[] { 0x01, 0x02, 0x03,
				0x04, (byte) 0x90, 0x00 });
		ResponseApdu respApdu = new ResponseApdu(respData,
				Iso7816.SW_9000_NO_ERROR);
		processingData.updateResponseAPDU(this, "testConversionOfOResponseApdu", respApdu);

		// call mut
		ioManager.processDescending(processingData);

		// extract/check converted response
		UpdatePropagation lastHardwareResponseUpdate = processingData
				.getUpdatePropagations(HardwareResponseApduPropagation.class).getLast();
		assertNotNull("no HardwareResponseApduUpdate was created", lastHardwareResponseUpdate);
		assertTrue("last HardwareResponseApduUpdate has wrong type", lastHardwareResponseUpdate instanceof HardwareResponseApduPropagation);
		assertArrayEquals("converted response Apdu does not match input",
				respApdu.toByteArray(), ((HardwareResponseApduPropagation) lastHardwareResponseUpdate)
				.getResponseApdu());
	}

}
