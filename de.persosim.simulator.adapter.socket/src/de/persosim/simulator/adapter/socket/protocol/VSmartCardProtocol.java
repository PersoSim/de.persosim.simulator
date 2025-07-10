package de.persosim.simulator.adapter.socket.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;
import org.globaltester.simulator.Simulator;

import de.persosim.driver.connector.SimulatorManager;
import de.persosim.simulator.adapter.socket.SimulatorProvider;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.utils.HexString;

public class VSmartCardProtocol implements SocketProtocol
{
	public static final int DEFAULT_PORT = 35963;

	SimulatorProvider simProvider;

	public VSmartCardProtocol(SimulatorProvider simProvider)
	{
		this.simProvider = simProvider;
	}

	@Override
	public boolean handleConnectionExchange(InputStream is, OutputStream os) throws IOException
	{
		Simulator sim = simProvider.getSimulator();
		// if there is a simulator available, get the response
		if (sim != null) {
			var lengthField = new byte[2];
			var readBytes = is.read(lengthField);
			if (readBytes == -1) {
				BasicLogger.log("Stream is EOF while reading length field", LogLevel.WARN, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
				return false;
			}

			BasicLogger.log("Received length field (" + readBytes + "):" + HexString.encode(lengthField), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
			var data = new byte[new BigInteger(lengthField).intValue()];
			readBytes = is.read(data);

			if (readBytes == -1) {
				BasicLogger.log("Stream is EOF while reading payload", LogLevel.WARN, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
				return false;
			}

			BasicLogger.log("Received data (" + readBytes + "):" + HexString.encode(data), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));

			if (data.length > 1) {
				BasicLogger.log("Got APDU", LogLevel.INFO);
				byte[] responseApdu = SimulatorManager.getSim().processCommand(data);
				send(responseApdu, os);
			}
			else if (data.length == 1) {
				switch (data[0]) {
					case 0:
						BasicLogger.log("Got power off", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
						SimulatorManager.getSim().cardPowerDown();
						break;
					case 1:
						BasicLogger.log("Got power on", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
						SimulatorManager.getSim().cardPowerUp();
						break;
					case 2:
						BasicLogger.log("Got reset", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
						SimulatorManager.getSim().cardReset();
						break;
					default:
						BasicLogger.log("Got unexpected command: " + data[0], LogLevel.WARN, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
						return false;
				}
			}
		}
		return true;
	}

	public static void send(byte[] payload, OutputStream os) throws IOException
	{
		sendLength(payload.length, os);
		os.write(payload);
		os.flush();
	}

	public static void sendLength(int length, OutputStream os) throws IOException
	{
		byte[] lengthBytes = new byte[2];
		lengthBytes[0] = (byte) (length >> 8);
		lengthBytes[1] = (byte) (length & 0xff);
		os.write(lengthBytes);
	}

	public static byte[] receive(InputStream is) throws IOException
	{
		int length = getLengthFromStream(is);
		if (length > 0) {
			byte[] data = new byte[length];
			int offset = 0;
			while (length > 0) {
				int readBytes = is.read(data, offset, length);
				if (readBytes == -1)
					throw new IOException("Got less than the expected " + length + " bytes of data");
				offset += readBytes;
				length -= readBytes;
			}
			return data;
		}
		return null;
	}

	private static int getLengthFromStream(InputStream is) throws IOException
	{
		int lengthByte1 = is.read();
		int lengthByte2 = is.read();

		if (lengthByte1 < 0 || lengthByte2 < 0) {
			return -1;
		}

		return (lengthByte1 << 8) + lengthByte2;
	}

}
