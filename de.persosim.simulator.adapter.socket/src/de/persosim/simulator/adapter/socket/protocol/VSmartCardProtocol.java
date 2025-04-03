package de.persosim.simulator.adapter.socket.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.SocketException;

import org.globaltester.simulator.Simulator;

import de.persosim.simulator.CommandParser;
import de.persosim.simulator.adapter.socket.SimulatorProvider;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class VSmartCardProtocol implements SocketProtocol {

	private static final byte[] ACK = Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR);
	private static final byte[] NACK = Utils.toUnsignedByteArray(Iso7816.SW_6F00_UNKNOWN);

	SimulatorProvider simProvider;
	
	public VSmartCardProtocol(SimulatorProvider simProvider) {
		this.simProvider = simProvider;
	}
	
	@Override
	public boolean handleConnectionExchange(InputStream inputStream, OutputStream outputStream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		PrintStream out = new PrintStream(outputStream);
		
		// read APDU from socket
		String apduLine = null;
		try {
			apduLine = in.readLine();
		} catch (SocketException e){
			//if the other side closed the the connection, this is expected behavior
		}

		if (apduLine == null) {
			// connection closed by peer
			return false;
		}
		
		// parse hex APDU
		byte[] apdu = null;
		byte[] response = new byte[] { 0x6F, 0x23 };
		try {
			apdu = HexString.toByteArray(apduLine);
		} catch (RuntimeException e) {
			CommandParser.showExceptionToUser(e);
			// nothing else needs to be done, will lead to an empty
			// apdu==null, thus no processing is done and the default SW
			// 6F23 is returned
		}

		// process the APDU, generate response
		
		Simulator sim = simProvider.getSimulator();
		// if there is a simulator available, get the response
		if (sim != null){
			int clains = Utils.maskUnsignedShortToInt(Utils.concatenate(apdu[0], apdu[1]));
			switch (clains) {
			case 0xFF00:
				response = sim.cardPowerDown();
				break;
			case 0xFF01:
				response = sim.cardPowerUp();
				break;
			case 0xFF6F:
				response = NACK;
				break;
			case 0xFF90:
				response = ACK;
				break;
			case 0xFFFF:
				response = sim.cardReset();
				break;
			default:
				// all other (unknown) APDUs are forwarded to the
				// simulator processingl
				response = sim.processCommand(apdu);
			}
			
		}

		// encode response and return it
		String respLine = HexString.encode(response);
		out.println(respLine);
		out.flush();
		return true;
	}

}
