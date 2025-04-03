package de.persosim.simulator.adapter.socket.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import de.persosim.driver.connector.SimulatorManager;
import de.persosim.simulator.utils.HexString;

public class VSmartcardServer {

	public static int DEFAULT_PORT = 35963;
	
	private Thread serverThread;
	private ServerSocket serverSocket;
	
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
	
	public VSmartcardServer() throws IOException {
		this(DEFAULT_PORT);
	}

	public VSmartcardServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	void start() {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						var socket = serverSocket.accept();
						var is = socket.getInputStream();
						var os = socket.getOutputStream();

						while (true) {
							var lengthField = new byte[2];
							var readBytes = is.read(lengthField);
							System.out.println("Received length field (" + readBytes + "):" + HexString.encode(lengthField));
							var data = new byte[new BigInteger(lengthField).intValue()];
							readBytes = is.read(data);
							System.out.println("Received data (" + readBytes + "):" + HexString.encode(data));

							if (data.length > 1) {
								System.err.println("    Got APDU");
								byte [] responseApdu = SimulatorManager.getSim().processCommand(data);
								send(responseApdu, os);
							} else if (data.length == 1) {
								switch (data[0]) {
								case 0:
									System.err.println("    Got power off");
									SimulatorManager.getSim().cardPowerDown();
									break;
								case 1:
									System.err.println("    Got power on");
									SimulatorManager.getSim().cardPowerUp();
									break;
								case 2:
									System.err.println("    Got reset");
									SimulatorManager.getSim().cardReset();
									break;
								}
							}
						}
					} catch (Exception e) {
						BasicLogger.logException(getClass(), e);
					}
				}
			}
		};
		serverThread = new Thread(r);
		serverThread.start();
	}
	
	void stop() {
		serverThread.interrupt();
		try {
			serverThread.join();
		} catch (InterruptedException e) {
			BasicLogger.logException("Waiting for server thread join failed", e, LogLevel.ERROR);
		}
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
