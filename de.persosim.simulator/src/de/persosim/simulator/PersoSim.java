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
import java.util.Arrays;
import java.util.Iterator;
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
	
	/*
	 * This variable holds the currently used personalization.
	 * It may explicitly be null and must not be read directly from here.
	 * As there exist several ways of providing a personalization of which none at all may be used the variable may remain null/unset.
	 * Due to this possibility access to this variable must be performed by calling the getPersonalization() method. 
	 */
	private Personalization currentPersonalization;
	
	public static final String CMD_START                      = "start";
	public static final String CMD_RESTART                    = "restart";
	public static final String CMD_STOP                       = "stop";
	public static final String CMD_EXIT                       = "exit";
	public static final String CMD_SET_HOST                   = "sethost";
	public static final String CMD_SET_HOST_SHORT             = "-host";
	public static final String CMD_SET_PORT                   = "setport";
	public static final String CMD_SET_PORT_SHORT             = "-port";
	public static final String CMD_LOAD_PERSONALIZATION       = "loadperso";
	public static final String CMD_LOAD_PERSONALIZATION_SHORT = "-perso";
	public static final String CMD_SEND_APDU                  = "sendapdu";
	public static final String CMD_HELP                       = "help";
	
	//XXX adjust host/port (e.g. from command line args)
	private String simHost = "localhost"; // default
	private int simPort = 9876; // default

	public PersoSim(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		
		try {
			handleArgs(args);
		} catch (IllegalArgumentException e) {
			System.out.println("simulation aborted, reason is: " + e.getMessage());
		}
		
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
		(new PersoSim(args)).run();
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
					String[] args = parseArgs(cmd);
					
					if(args.length > 0) {
						switch (args[0]) {
				            case CMD_LOAD_PERSONALIZATION:
				            	if(args.length == 2) {
				            		try{
				            			setPersonalization(args[1]);
				            			stopSimulator();
				            			startSimulator();
				            		} catch(FileNotFoundException | IllegalArgumentException e) {
				            			if(e instanceof FileNotFoundException) {
				            				System.out.println("unable to set personalization, reason is: perso file not found");
				            			} else{
				            				System.out.println("unable to set personalization, reason is: " + e.getMessage());
				            			}
				            			
				            			System.out.println("simulation is stopped");
				            			stopSimulator();
				            		}
				            	} else{
				            		System.out.println("set personalization command requires one single file name");
				            	}
				            	break;
				            case CMD_SEND_APDU:
				            	cmdSendApdu(cmd);
				            	break;
				            case CMD_START:
				            	startSimulator();
				            	break;
				            case CMD_RESTART:
				            	stopSimulator();
				            	startSimulator();
				            case CMD_STOP:
				            	stopSimulator();
				            	break;
				            case CMD_EXIT:
				            	stopSimulator();
								executeUserCommands = false;
								break;
				            case CMD_HELP:
				            	System.out.println("Available commands:");
								System.out.println(CMD_SEND_APDU + " <hexstring>");
								System.out.println(CMD_LOAD_PERSONALIZATION + " <file name>");
								System.out.println(CMD_START);
								System.out.println(CMD_RESTART);
								System.out.println(CMD_STOP);
								System.out.println(CMD_EXIT);
								System.out.println(CMD_HELP);
								break;
				            default: System.out.println("unrecognized command \"" + args[0] + "\" and parameters will be ignored");
				                     break;
						}
					}
				}
			} catch (RuntimeException e) {
				showExceptionToUser(e);
			}
		}
	}
	
	/**
	 * This method parses the provided String object for commands and possible
	 * arguments. First the provided String is trimmed. If the String is empty,
	 * the returned array will be of length 0. If the String does not contain at
	 * least one space character ' ', the whole String will be returned as first
	 * and only element of an array of length 1. If the String does contain at
	 * least one space character ' ', the substring up to but not including the
	 * position of the first occurrence will be the first element of the
	 * returned array. The rest of the String will be trimmed and, if not of
	 * length 0, form the second array element.
	 * 
	 * @param args
	 *            the argument String to be parsed
	 * @return the parsed arguments
	 * @throws NullPointerException
	 *             if provided arguments are null
	 */
	public static String[] parseArgs(String args) {
		if(args == null) {throw new NullPointerException("arguments must not be null");}
		
		String argsInput = args.trim().toLowerCase();
		
		int index = argsInput.indexOf(" ");
		
		if(index >= 0) {
			String cmd = argsInput.substring(0, index);
			String params = argsInput.substring(index).trim();
			return new String[]{cmd, params};
		} else{
			if(argsInput.length() > 0) {
				return new String[]{argsInput};
			} else{
				return new String[0];
			}
			
		}
	}

	/**
	 * This method handles instantiation and (re)start of the SocketSimulator.
	 */
	private void startSimulator() {
		if (simulator == null) {
			simulator = new SocketSimulator(getPersonalization(), simPort);
		}
		
		if (!simulator.isRunning()) {
			simulator.start();
		}

	}

	/**
	 * This method returns the content of {@link #currentPersonalization}, the
	 * currently used personalization. If no personalization is set, i.e. the
	 * variable is null, it will be set to the default personalization which
	 * will be returned thereafter. This mode of accessing personalization
	 * opportunistic assumes that a personalization will always be set and
	 * generating a default personalization is an overhead only to be spent as a
	 * measure of last resort.
	 * 
	 * @return the currently used personalization
	 */
	public Personalization getPersonalization() {
//		// try to read perso from provided file
//		String persoFileName = "perso.xml";
//		
//		currentPersonalization = parsePersonalization(persoFileName);
		
		if(currentPersonalization == null) {
			System.out.println("Loading default personalization");
			currentPersonalization = new DefaultPersoTestPki();
		}
		
		return currentPersonalization;
	}
	
	/**
	 * This method parses a {@link Personalization} object from a file identified by its name.
	 * @param persoFileName the name of the file to contain the personalization
	 * @return the parsed personalization
	 * @throws FileNotFoundException 
	 * @throws IllegalArgumentException if parsing of personalization not successful
	 */
	public static Personalization parsePersonalization(String persoFileName) throws FileNotFoundException {
		File persoFile = new File(persoFileName);
		
		Unmarshaller um;
		try {
			um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
			System.out.println("Loading personalization from file " + persoFileName);
			return (Personalization) um
					.unmarshal(new FileReader(persoFile));
		} catch (JAXBException e) {
			throw new IllegalArgumentException("Unable to parse personalization from file " + persoFileName);
		}
	}
	
	public void setPersonalization(String persoFileName) throws FileNotFoundException {
		currentPersonalization = parsePersonalization(persoFileName);
		System.out.println("personalization successfully read from file " + persoFileName);
	}
	
	public void setPort(String newPortString) {
		if(newPortString == null) {throw new NullPointerException("port parameter must not be null");}
		int newPort = Integer.parseInt(newPortString);
		if(newPort < 0) {throw new IllegalArgumentException("port number must be positive");}
		
		simPort = newPort;
		//IMPL check for port being unused
	}
	
	public void setHost(String newHost) {
		if(newHost == null) {throw new NullPointerException("host name must not be null");}
		if(newHost.length() <= 0) {throw new IllegalArgumentException("host name must not be empty");}
		
		simHost = newHost;
		//IMPL check for host response
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
	 * socket interface and returned to the caller as HexString.
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
		if(args == null) {throw new NullPointerException("arguments must not be null");}
		
		Iterator<String> argsIterator = Arrays.asList(args).iterator();
		
		String currentArgument;
		while(argsIterator.hasNext()) {
			currentArgument = argsIterator.next();
			
			switch (currentArgument) {
            case CMD_LOAD_PERSONALIZATION_SHORT:
            	if(argsIterator.hasNext()) {
            		String fileName = argsIterator.next();
            		try{
            			setPersonalization(fileName);
            		} catch(IllegalArgumentException e) {
            			throw new IllegalArgumentException("unable to set personalization, reason is: " + e.getMessage());
            		} catch(FileNotFoundException e) {
            			throw new IllegalArgumentException("unable to set personalization, reason is: perso file not found");
            		}
            	} else{
            		System.out.println("set personalization command requires file name");
            	}
            	break;
            case CMD_SET_HOST_SHORT:
            	if(argsIterator.hasNext()) {
            		String hostName = argsIterator.next();
            		try{
            			setHost(hostName);
            		} catch(IllegalArgumentException | NullPointerException e) {
            			throw new IllegalArgumentException("unable to set host name, reason is: " + e.getMessage());
            		}
            	} else{
            		System.out.println("set host command requires host name");
            	}
            	break;
            case CMD_SET_PORT_SHORT:
            	if(argsIterator.hasNext()) {
            		String portNoString = argsIterator.next();
            		try{
            			setPort(portNoString);
            		} catch(IllegalArgumentException | NullPointerException e) {
            			throw new IllegalArgumentException("unable to set port, reason is: " + e.getMessage());
            		}
            	} else{
            		System.out.println("set port command requires port number");
            	}
            	break;
            default: System.out.println("unrecognized command or parameter \"" + currentArgument + "\" will be ignored");
                     break;
			}
		}
		
	}

}
