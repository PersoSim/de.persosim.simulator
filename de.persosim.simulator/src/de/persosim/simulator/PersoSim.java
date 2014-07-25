package de.persosim.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.security.Security;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.persosim.simulator.jaxb.PersoSimJaxbContextProvider;
import de.persosim.simulator.perso.DefaultPersoTestPki;
import de.persosim.simulator.perso.Personalization;

public class PersoSim implements Runnable {

	private SocketSimulator simulator;
	
	//XXX adjust host/port (e.g. from command line args)
	private String simHost = "localhost";
	private int simPort = 9876;

	public PersoSim() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Default command line main method.
	 * 
	 * This starts the PersoSim simulator within its own thread and accepts user
	 * commands to control it on the existing thread on a simple command prompt.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PersoSim sim = new PersoSim();
		sim.handleArgs(args);
		sim.run();
	}

	@Override
	public void run() {
		System.out.println("Welcome to PersoSim");

		startSimulator();
		handleUserCommands();
	}
	
	public static void showExceptionToUser(Exception e) {
		System.out.println("Exception: " + e.getMessage());
		e.printStackTrace();
	}

	/**
	 * This method implements the behavior of the user command prompt. E.g.
	 * prints the prompt, reads the user commands and forwards this to the
	 * respective method for processing.
	 */
	private void handleUserCommands() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean executeUserCommands = true;

		while (executeUserCommands) {
			System.out.println("PersoSim commandline: ");
			String cmd = null;
			try {
				cmd = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (cmd != null) {
					cmd = cmd.trim();
					if (cmd.toLowerCase().startsWith("sendapdu")) {
						cmdSendApdu(cmd);
					} else if (cmd.toLowerCase().startsWith("exit")) {
						stopSimulator();
						executeUserCommands = false;
					} else if (cmd.toLowerCase().startsWith("help")) {
						System.out.println("Available commands:");
						System.out.println("sendApdu <hestring>");
						System.out.println("help");
						System.out.println("exit");
					} else {
						System.out.println("unknown command");
					}
				}
			} catch (RuntimeException e) {
				showExceptionToUser(e);
			}
		}
	}

	/**
	 * This methods handles instantiation and (re)start of the SocketSimulator.
	 */
	private void startSimulator() {
		if (simulator == null) {
			simulator = new SocketSimulator(getPersonalization(), simPort);
		}
		
		if (!simulator.isRunning()) {
			simulator.start();
		}

	}

	private Personalization getPersonalization() {
		// try to read perso from provided file
		String persoFileName = "perso.xml";
		File persoFile = new File(persoFileName);
		if (persoFile.exists()) {
			Unmarshaller um;
			try {
				um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
				System.out.println("Loading personalization from file " + persoFileName);
				return (Personalization) um
						.unmarshal(new FileReader(persoFile));
			} catch (JAXBException e) {
				System.out.println("Unable to parse personalization from file " + persoFileName);
				showExceptionToUser(e);
			} catch (FileNotFoundException e) {
				System.out.println("Perso file " + persoFileName + " not found");
			}
		}
		
		System.out.println("Loading default personalization");
		return new DefaultPersoTestPki();
	}

	/**
	 * Stops the simulator thread and returns when the thread is stopped.
	 */
	private void stopSimulator() {
		if (simulator != null) {
			simulator.stop();
		}
	}

	/**
	 * Transmit an APDU to the card
	 * 
	 * @param cmd
	 *            string containing the command
	 */
	private void cmdSendApdu(String cmd) {
		cmd = cmd.trim();

		Pattern cmdSendApduPattern = Pattern
				.compile("^send[aA]pdu ([0-9a-fA-F\\s]+)$");
		Matcher matcher = cmdSendApduPattern.matcher(cmd);
		if (!matcher.matches()) {
			throw new RuntimeException("invalid arguments to sendApdu");
		}
		String apdu = matcher.group(1);
		exchangeApdu(apdu);

	}

	/**
	 * Transmit the given APDU to the simulator, which processes it and returns
	 * the response. The response APDU is received from the simulator via its
	 * socket interface an returned to the caller as HexString.
	 * 
	 * @param cmdApdu
	 *            HexString containing the CommandAPDU
	 * @return
	 */
	private String exchangeApdu(String cmdApdu) {
		cmdApdu = cmdApdu.replaceAll("\\s", ""); // remove any whitespace

		Socket socket;
		try {
			socket = new Socket(simHost, simPort);
		} catch (IOException e) {
			socket = null;
			showExceptionToUser(e);
			return null;
		}

		PrintStream out = null;
		BufferedReader in = null;
		try {
			out = new PrintStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException e) {
			showExceptionToUser(e);
		}

		out.println(cmdApdu);
		out.flush();

		String respApdu = null;
		try {
			respApdu = in.readLine();
		} catch (IOException e) {
			showExceptionToUser(e);
		} finally {
			System.out.println("> " + cmdApdu);
			System.out.println("< " + respApdu);
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					showExceptionToUser(e);
				}
			}
		}

		return respApdu;

	}

	public void handleArgs(String[] args) {
		// TODO handle command line args as soon as those are defined
	}

}
