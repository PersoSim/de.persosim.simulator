package de.persosim.simulator.adapter.socket.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import org.globaltester.logging.BasicLogger;
import org.globaltester.simulator.Simulator;

import de.persosim.driver.connector.SimulatorManager;
import de.persosim.simulator.adapter.socket.SimulatorProvider;
import de.persosim.simulator.utils.HexString;

public class VSmartCardProtocol implements SocketProtocol {

	public static int DEFAULT_PORT = 35963;

	SimulatorProvider simProvider;
	
	public VSmartCardProtocol(SimulatorProvider simProvider) {
		this.simProvider = simProvider;
	}
	
	@Override
	public boolean handleConnectionExchange(InputStream is, OutputStream os) throws IOException {
		Simulator sim = simProvider.getSimulator();
		// if there is a simulator available, get the response
		if (sim != null){
				var lengthField = new byte[2];
				var readBytes = is.read(lengthField);
				if (readBytes == -1) {
					BasicLogger.log(getClass(), "Stream is EOF while reading length field");
					return false;
				}
				
				BasicLogger.log(getClass(), "Received length field (" + readBytes + "):" + HexString.encode(lengthField));
				var data = new byte[new BigInteger(lengthField).intValue()];
				readBytes = is.read(data);
				
				if (readBytes == -1) {
					BasicLogger.log(getClass(), "Stream is EOF while reading payload");
					return false;
				}
				
				BasicLogger.log(getClass(), "Received data (" + readBytes + "):" + HexString.encode(data));

				if (data.length > 1) {
					BasicLogger.log(getClass(), "Got APDU");
					byte [] responseApdu = SimulatorManager.getSim().processCommand(data);
					send(responseApdu, os);
				} else if (data.length == 1) {
					switch (data[0]) {
					case 0:
						BasicLogger.log(getClass(), "Got power off");
						SimulatorManager.getSim().cardPowerDown();
						break;
					case 1:
						BasicLogger.log(getClass(), "Got power on");
						SimulatorManager.getSim().cardPowerUp();
						break;
					case 2:
						BasicLogger.log(getClass(), "Got reset");
						SimulatorManager.getSim().cardReset();
						break;
					}
				}
		}
		return true;
	}
	
	public static void send(byte [] payload, OutputStream os) throws IOException {
		sendLength(payload.length, os);
		os.write(payload);
		os.flush();
	}
	
	public static void sendLength(int length, OutputStream os) throws IOException {
		byte [] lengthBytes = new byte [2];
		lengthBytes[0] = (byte) (length >> 8);
		lengthBytes[1] = (byte) (length & 0xff);
        os.write(lengthBytes);		
	}

	public static byte [] receive(InputStream is) throws IOException {
		int length = getLengthFromStream(is);
		if (length > 0) {
			byte [] data = new byte [length];
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

	private static int getLengthFromStream(InputStream is) throws IOException {
		int lengthByte1 = is.read();
		int lengthByte2 = is.read();
		
		if (lengthByte1 < 0 || lengthByte2 < 0) {
			return -1;
		}
		
		return (lengthByte1 << 8) + lengthByte2;
	}

}
