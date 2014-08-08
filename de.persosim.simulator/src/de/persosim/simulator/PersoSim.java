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

/**
 * This class provides access to and control of the actual simulator. It can be
 * used to start, stop and configure it. The simulator may be configured by
 * providing either command line arguments during start-up or user initiated
 * commands at runtime. Currently both options only allow for a single command
 * to be executed parameterized by at most one command argument. As all
 * parameters vital for the operation of the simulator are implicitly set to
 * default values by fall-through, no explicit configuration is required.
 * 
 * @author slutters
 * 
 */
public class PersoSim implements Runnable {
	
	private SocketSimulator simulator;
	
	/*
	 * This variable holds the currently used personalization.
	 * It may explicitly be null and should not be read directly from here.
	 * As there exist several ways of providing a personalization of which none at all may be used the variable may remain null/unset.
	 * Due to this possibility access to this variable should be performed by calling the getPersonalization() method. 
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
	private boolean executeUserCommands = true;

	public PersoSim(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		
		try {
			executeStartupCommands(args);
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
	 * IMPL extend to parse for multiple arguments add recognition of "
	 * characters as indication of file names allowing white spaces in between.
	 * 
	 * @param args
	 *            the argument String to be parsed
	 * @return the parsed arguments
	 * @throws NullPointerException
	 *             if provided arguments are null
	 */
	public static String[] parseArgs(String args) throws NullPointerException {
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
		simulator = new SocketSimulator(getPersonalization(), simPort);
		
		if (!simulator.isRunning()) {
			simulator.start();
		}

	}
	
	private void restartSimulator() {
		stopSimulator();
		startSimulator();
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
	 * @throws JAXBException if parsing of personalization not successful
	 */
	public static Personalization parsePersonalization(String persoFileName) throws FileNotFoundException, JAXBException {
		File persoFile = new File(persoFileName);
		
		Unmarshaller um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
		System.out.println("Parsing personalization from file " + persoFileName);
		return (Personalization) um
				.unmarshal(new FileReader(persoFile));
	}
	
	/**
	 * This method sets the personalization by providing a file that it is to be read and parsed from.
	 * @param persoFileName the file that the personalization is to be read from
	 * @throws FileNotFoundException if the provided file is not found
	 * @throws JAXBException if parsing of personalization not successful
	 */
	public void setPersonalization(String persoFileName) throws FileNotFoundException, JAXBException {
		currentPersonalization = parsePersonalization(persoFileName);
		System.out.println("personalization successfully read and set from file " + persoFileName);
	}
	
	/**
	 * This method sets a new port for the simulator.
	 * In order for the changes to take effect, the simulator needs to be restarted.
	 * @param newPortString the new port to be used
	 */
	public void setPort(String newPortString) {
		if(newPortString == null) {throw new NullPointerException("port parameter must not be null");}
		int newPort = Integer.parseInt(newPortString);
		if(newPort < 0) {throw new IllegalArgumentException("port number must be positive");}
		
		simPort = newPort;
		
		System.out.println("new port successfully set to " + newPort);
		
		//IMPL check for port being unused
	}
	
	/**
	 * This method sets a new host for the simulator.
	 * In order for the changes to take effect, the simulator needs to be restarted.
	 * @param newHost the new host to be used
	 */
	public void setHost(String newHost) {
		if(newHost == null) {throw new NullPointerException("host name must not be null");}
		if(newHost.length() <= 0) {throw new IllegalArgumentException("host name must not be empty");}
		
		simHost = newHost;
		
		System.out.println("new host successfully set to " + newHost);
		
		//IMPL check for host response
	}

	/**
	 * Stops the simulator thread and returns when the thread is stopped.
	 */
	private void stopSimulator() {
		if (simulator != null) {
			simulator.stop();
			simulator = null;
		}
	}

	/**
	 * Transmit an APDU to the card
	 * 
	 * @param cmd
	 *            string containing the command
	 */
	private void sendCmdApdu(String cmd) {
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
	 * This method processes the send APDU command according to the provided arguments.
	 * @param args the arguments provided for processing
	 * @return whether processing has been successful
	 */
	private int cmdSendApdu(String[] args) {
		if(args.length >= 2) {
    		try{
    			sendCmdApdu("sendApdu " + args[1]);
    			return 2;
    		} catch(RuntimeException e) {
    			System.out.println("unable to set personalization, reason is: " + e.getMessage());
    			return 1;
    		}
    	} else{
    		System.out.println("set personalization command requires one single APDU");
			return 1;
    	}
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
	
	/**
	 * This method prints the help menu to the command line.
	 */
	private void printHelp() {
		System.out.println("Available commands:");
		System.out.println(CMD_SEND_APDU + " <hexstring>");
		System.out.println(CMD_LOAD_PERSONALIZATION + " <file name>");
		System.out.println(CMD_START);
		System.out.println(CMD_RESTART);
		System.out.println(CMD_STOP);
		System.out.println(CMD_EXIT);
		System.out.println(CMD_HELP);
	}
	
	/**
	 * This method processes the load personalization command according to the provided arguments.
	 * @param args the arguments provided for processing
	 * @return whether processing has been successful
	 */
	private int cmdLoadPersonalization(String[] args) {
		if(args.length >= 2) {
    		try{
    			setPersonalization(args[1]);
    			return 2;
    		} catch(FileNotFoundException | JAXBException e) {
    			System.out.println("unable to set personalization, reason is: " + e.getMessage());
    			
    			System.out.println("simulation is stopped");
    			stopSimulator();
    			return 1;
    		}
    	} else{
    		System.out.println("set personalization command requires one single file name");
    		System.out.println("simulation is stopped");
			stopSimulator();
			return 1;
    	}
	}
	
	/**
	 * This method processes the set host name command according to the provided arguments.
	 * @param args the arguments provided for processing
	 * @return whether processing has been successful
	 */
	private int cmdSetHostName(String[] args) {
		if(args.length >= 2) {
			String hostName = args[1];
    		try{
    			setHost(hostName);
    			return 2;
    		} catch(IllegalArgumentException | NullPointerException e) {
    			System.out.println("unable to set host name, reason is: " + e.getMessage());
    			return 1;
    		}
    	} else{
    		System.out.println("set host command requires host name");
			return 1;
    	}
	}
	
	/**
	 * This method processes the set port command according to the provided arguments.
	 * @param args the arguments provided for processing
	 * @return whether processing has been successful
	 */
	private int cmdSetPortNo(String[] args) {
		if(args.length >= 2) {
			String portNoString = args[1];
    		try{
    			setPort(portNoString);
    			return 2;
    		} catch(IllegalArgumentException | NullPointerException e) {
    			System.out.println("unable to set port, reason is: " + e.getMessage());
    			return 1;
    		}
    	} else{
    		System.out.println("set host command requires host name");
			return 1;
    	}
	}
	
	/**
	 * This method implements the behavior of the user command prompt. E.g.
	 * prints the prompt, reads the user commands and forwards this to the the
	 * execution method for processing. Only one command per invocation of the
	 * execution method is allowed. The first argument provided must be the
	 * command, followed by an arbitrary number of parameters. If the number of
	 * provided parameters is higher than the number expected by the command,
	 * the surplus parameters will be ignored.
	 */
	private void handleUserCommands() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

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
					executeUserCommands(args);
				}
			} catch (RuntimeException e) {
				showExceptionToUser(e);
			}
		}
	}
	
	/**
	 * This method implements the execution of commands initiated by user interaction.
	 * @param args the parsed commands and arguments
	 */
	private void executeUserCommands(String[] args) {
		if(args.length == 0) {return;}
		
		String currentArgument = args[0];
		switch (currentArgument) {
            case CMD_LOAD_PERSONALIZATION:
            	cmdLoadPersonalization(args);
            	restartSimulator();
            	break;
            case CMD_SET_HOST:
            	cmdSetHostName(args);
            	restartSimulator();
            	break;
            case CMD_SET_PORT:
            	cmdSetPortNo(args);
            	restartSimulator();
            	break;
            case CMD_SEND_APDU:
            	cmdSendApdu(args);
            	break;
            case CMD_START:
            	startSimulator();
            	break;
            case CMD_RESTART:
            	restartSimulator();
            case CMD_STOP:
            	stopSimulator();
            	break;
            case CMD_EXIT:
            	stopSimulator();
				executeUserCommands = false;
            case CMD_HELP:
            	printHelp();
				break;
            default: 
            	System.out.println("unrecognized command \"" + currentArgument + "\" and parameters will be ignored");
                break;
		}
	}
	
	/**
	 * This method implements the execution of commands initiated by command line arguments.
	 * @param args the parsed commands and arguments
	 */
	public void executeStartupCommands(String[] args) {
		if(args.length == 0) {return;}
		
		String[] currentArgs = args;
		int noOfUnprocessedArgs = args.length;
		
		while(noOfUnprocessedArgs > 0) {
			String currentArgument = currentArgs[0];
			switch (currentArgument) {
		        case CMD_LOAD_PERSONALIZATION_SHORT:
		        	noOfUnprocessedArgs -= cmdLoadPersonalization(currentArgs);
		        	break;
		        case CMD_SET_HOST_SHORT:
		        	noOfUnprocessedArgs -= cmdSetHostName(currentArgs);
		        	break;
		        case CMD_SET_PORT_SHORT:
		        	noOfUnprocessedArgs -= cmdSetPortNo(currentArgs);
		        	break;
		        default:
		        	System.out.println("unrecognized command or parameter \"" + currentArgument + "\" will be ignored");
		        	noOfUnprocessedArgs--;
		            break;
			}
			
			String[] updatedArgs = new String[noOfUnprocessedArgs];
			for(int i = 0; i < noOfUnprocessedArgs; i++) {
				updatedArgs[i] = currentArgs[currentArgs.length - noOfUnprocessedArgs + i];
			}
			currentArgs = updatedArgs;
			
		}
		
	}

}
