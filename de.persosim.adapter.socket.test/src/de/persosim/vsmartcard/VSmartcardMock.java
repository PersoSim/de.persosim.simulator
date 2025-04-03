package de.persosim.vsmartcard;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import de.persosim.simulator.adapter.socket.protocol.VSmartcardServer;
import de.persosim.simulator.adapter.socket.ui.vsmartcard.Commands;
import de.persosim.simulator.utils.HexString;

public class VSmartcardMock {
	private String host;
	private int port;
	private Socket socket;
	private InputStream is;
	private static OutputStream os;
	
	public static void main(String [] args) throws Exception {
		VSmartcardMock mock = new VSmartcardMock("localhost", VSmartcardServer.DEFAULT_PORT);
		while (true) {
			mock.connect();
			while (true) {
				try {
					mock.handle();
				} catch (Exception e) {
					System.err.println("Handler crashed");
					e.printStackTrace();
					break;
				}
			}
			mock.disconnect();
		}	
	}

	public VSmartcardMock(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void connect() throws IOException {
		socket = new Socket(host, port);
		is = socket.getInputStream();
		os = socket.getOutputStream();
	}
	
	public void handle() throws IOException {
		VSmartcardServer.send(Commands.POWER_ON.getCommand(), os);
		VSmartcardServer.send(Commands.RESET.getCommand(), os);
		VSmartcardServer.send(HexString.toByteArray("00a40000"), os);
		
		System.out.println("Received: " + HexString.encode(VSmartcardServer.receive(is)));
	};
	
	public void disconnect() throws IOException {
		socket.close();
		socket = null;
	}

}
