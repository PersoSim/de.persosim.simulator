package de.persosim.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.bouncycastle.util.encoders.Hex;

import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.PersoSimKernel;
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
public class SocketSimulator implements Runnable {

	private static final byte[] ACK = Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR);
	private static final byte[] NACK = Utils.toUnsignedByteArray(Iso7816.SW_6F00_UNKNOWN);

	private int port;
	private Thread simThread = null;
	private boolean isRunning;

	private PersoSimKernel kernel;
	private boolean isPowerOn;
	private ServerSocket server;
	private Socket clientSocket;

	/**
	 * Create new instance.
	 * 
	 * @param simPort
	 *            port the server socket should listen on
	 */
	public SocketSimulator(Personalization perso, int simPort) {
		port = simPort;

		kernel = new PersoSimKernel(perso);
		kernel.init();
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
		isPowerOn = false;
		
		//stop listening for new connections
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				PersoSim.showExceptionToUser(e);
			}
		}
		
		// terminate existing client connection
		if (clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				PersoSim.showExceptionToUser(e);
			}
		}

		//wait for second thread
		if (simThread != null) {
			try {
				simThread.join();
			} catch (InterruptedException e) {
				PersoSim.showExceptionToUser(e);
			}
		}
		
		return isRunning(); //FIXME SLS this returns false if the method successfuly stoped the simulator. Is this intended? seems strange, at least clearly state this within the JavaDoc and check the call hierarchy 
	}

	@Override
	public void run() {
		// open ServerSocket
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			PersoSim.showExceptionToUser(e);
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
				PersoSim.showExceptionToUser(e);
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
				String apduLine = in.readLine();
				if (apduLine == null) {
					// connection closed by peer
					isPowerOn = false;
					break;
				}

				// parse hex APDU
				byte[] apdu = null;
				byte[] response = new byte[] { 0x6F, 0x00 };
				try {
					apdu = Hex.decode(apduLine);
				} catch (RuntimeException e) {
					PersoSim.showExceptionToUser(e);
					// nothing else needs to be done, will lead to an empty
					// apdu==null, thus no processing is done and the default SW
					// 6F00 is returned
				}

				// process the APDU, generate response
				response = processCommand(apdu);

				// encode response and return it
				String respLine = new String(Hex.encode(response));
				out.println(respLine);
				out.flush();

			} while (isPowerOn);

		} catch (IOException e) {
			//show the exception only if the server is still running, otherwise it is expected behavior
			if (isRunning) {
				PersoSim.showExceptionToUser(e);
			}
		} finally {
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					PersoSim.showExceptionToUser(e);
				}
			}
		}

	}

	/**
	 * Handles APDUs received via command socket. Control APDUs are filtered and
	 * the respective methods of the kernel are called. All other APDUs are
	 * simply forwarded to the kernels process() method.
	 * 
	 * @param apdu
	 * @return
	 */
	private byte[] processCommand(byte[] apdu) {
		int clains = Utils.maskUnsignedShortToInt(Utils.concatenate(apdu[0], apdu[1]));
		switch (clains) {
		case 0xFF00:
			isPowerOn = false;
			return kernel.powerOff();
		case 0xFF01:
			isPowerOn = true;
			return kernel.powerOn();
		case 0xFF6F:
			return NACK;
		case 0xFF90:
			return ACK;
		case 0xFFFF:
			return kernel.reset();
		default:
			// all other (unknown) APDUs are forwarded to the
			// PersoSimKernel
			return kernel.process(apdu);
		}
	}

}
