package de.persosim.simulator.adapter.socket;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import de.persosim.simulator.CommandParser;
import de.persosim.simulator.Simulator;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class provides the socket interface to the PersoSim simulator.
 * 
 * It instantiates and manages the communication socket as well as the PersoSim
 * kernel and mediates commands/responses between those two. It is also in
 * charge of simulating behavior "outside" the card, like power on/off or reset
 * of the card. Therefore it provides it's own APDU handler that handles some
 * special control APDUs
 * 
 * @author amay
 * 
 */
public class SocketAdapter implements Runnable {

	private static final byte[] ACK = Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR);
	private static final byte[] NACK = Utils.toUnsignedByteArray(Iso7816.SW_6F00_UNKNOWN);

	private int port;
	private Thread simThread = null;
	private boolean isRunning;
	private ServerSocket server;
	private Socket clientSocket;
	private SimulatorProvider simProvider;

	/**
	 * Create new instance.
	 * 
	 * @param simPort
	 *            port the server socket should listen on
	 */
	public SocketAdapter(SimulatorProvider simProvider, int simPort) {
		this.simProvider = simProvider;
		this.port = simPort;
	}

	/**
	 * Start execution of the simulation (within its own thread).
	 * 
	 * If this simulation already owns a (running) Thread this method does
	 * nothing and returns false.
	 * 
	 * If the newly created Thread does not start execution within a small
	 * timeout this method also returns false;
	 * 
	 * @return true iff a new simulation Thread was created and successfully
	 *         started
	 */
	public synchronized boolean start() {
		// check for existing thread
		if (simThread != null) {
			// a previous Thread exists, this needs to be stopped before a new
			// one can be created
			return isRunning();
		}

		// start the simulator within a thread
		simThread = new Thread(this);
		simThread.start();

		// wait until the just started Thread begins execution
		int counter = 0;
		while (!isRunning()) {
			try {
				counter++;
				Thread.sleep(500);
				if (counter > 4) {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		} ;

		return isRunning();
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean stop() {
		isRunning = false;
		
		//stop listening for new connections
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				CommandParser.showExceptionToUser(e);
			}
		}
		
		// terminate existing client connection
		if (clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				CommandParser.showExceptionToUser(e);
			}
		}

		//wait for second thread
		if (simThread != null) {
			try {
				simThread.join();
				simThread = null;
			} catch (InterruptedException e) {
				CommandParser.showExceptionToUser(e);
			}
		}
		
		return !isRunning();
	}

	@Override
	public void run() {
		// open ServerSocket
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			CommandParser.showExceptionToUser(e);
			return; // without an open ServerSocket this method is done
		}

		// handle connections
		isRunning = true;
		while (isRunning) {
			handleConnection(server);
		}

		// close ServerSocket
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				CommandParser.showExceptionToUser(e);
			}
		}

	}

	/**
	 * Handles a single connection from ServerSocket.
	 * 
	 * @param server
	 */
	private void handleConnection(ServerSocket server) {
		if (server == null) {
			// nothing to do without ServerSocket
			return;
		}

		clientSocket = null;
		try {
			clientSocket = server.accept();

			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintStream out = new PrintStream(clientSocket.getOutputStream());

			do {
				// read APDU from socket
				String apduLine = null;
				try {
					apduLine = in.readLine();
				} catch (SocketException e){
					//if the other side closed the the connection, this is expected behavior
				}

				if (apduLine == null) {
					// connection closed by peer
					break;
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

			} while (isRunning);

		} catch (IOException e) {
			//show the exception only if the server is still running, otherwise it is expected behavior
			if (isRunning) {
				CommandParser.showExceptionToUser(e);
			}
		} finally {
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					CommandParser.showExceptionToUser(e);
				}
			}
		}

	}

}
