package de.persosim.simulator;

import static de.persosim.simulator.utils.PersoSimLogger.log;
import static de.persosim.simulator.utils.PersoSimLogger.logException;
import static de.persosim.simulator.utils.PersoSimLogger.UI;
import static de.persosim.simulator.utils.PersoSimLogger.INFO;
import static de.persosim.simulator.utils.PersoSimLogger.WARN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.persosim.simulator.jaxb.PersoSimJaxbContextProvider;
import de.persosim.simulator.perso.DefaultPersoTestPki;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.utils.PersoSimLogger;

/**
 * This class provides access to and control of the actual simulator. It can be
 * used to start, stop and configure it. The simulator may be configured by
 * providing either command line arguments during start-up or user initiated
 * commands at runtime. As all parameters vital for the operation of the
 * simulator are implicitly set to default values by fall-through, no explicit
 * configuration is required.
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
	private Personalization currentPersonalization = new DefaultPersoTestPki();
	
	public static final String CMD_START                      = "start";
	public static final String CMD_RESTART                    = "restart";
	public static final String CMD_STOP                       = "stop";
	public static final String CMD_EXIT                       = "exit";
	public static final String CMD_SET_PORT                   = "setport";
	public static final String ARG_SET_PORT                   = "-port";
	public static final String CMD_LOAD_PERSONALIZATION       = "loadperso";
	public static final String ARG_LOAD_PERSONALIZATION       = "-perso";
	public static final String CMD_SEND_APDU                  = "sendapdu";
	public static final String CMD_HELP                       = "help";
	public static final String ARG_HELP                       = "-h";
	public static final String CMD_CONSOLE_ONLY               = "--consoleOnly";
	
	public static final String LOG_NO_OPERATION = "nothing to process";
	public static final String LOG_SIM_EXIT     = "simulator exit";
	public static final String LOG_UNKNOWN_ARG  = "unknown argument";
	
	public static final int DEFAULT_SIM_PORT = 9876;
	public static final String DEFAULT_SIM_HOST = "localhost";
	
	public static final String persoPlugin = "platform:/plugin/de.persosim.rcp/";
	public static final String persoPath = "personalization/profiles/";
	public static final String persoFilePrefix = "Profile";
	public static final String persoFilePostfix = ".xml";
	
	private int simPort = DEFAULT_SIM_PORT; // default
	private boolean executeUserCommands = false;
	private boolean processingCommandLineArguments = false;
	
	static {
		//register BouncyCastle provider
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}
	
	public PersoSim(String... args) {
		try {
			handleArgs(args);
		} catch (IllegalArgumentException e) {
			log(this.getClass(), "simulation aborted, reason is: " + e.getMessage());
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
		log(this.getClass(), "Welcome to PersoSim", UI);
		startSimulator();
		handleUserCommands();
	}
	
	public static void showExceptionToUser(Exception e) {
		log(PersoSim.class, "Exception: " + e.getMessage(), UI);
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
	 */
	public static String[] parseCommand(String args) {
		String argsInput = args.trim();
		
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
	 * @return whether instantiation and starting was successful
	 */
	public boolean startSimulator() {
		if (simulator != null && simulator.isRunning()) {
			log(this.getClass(), "Simulator already running", UI);
			return true;
		}
		
		if (getPersonalization() == null) {
			log(this.getClass(), "No personalization available, please load a valid personalization before starting the simulator", PersoSimLogger.UI);
			return false;
		}
		
		SocketSimulator newSimulator = new SocketSimulator(getPersonalization(), simPort);
		
		if(newSimulator.start()) {
			simulator = newSimulator;
			log(this.getClass(), "The simulator has been started", UI);
			return true;
		} else{
			return false;
		}

	}
	
	/**
	 * This method processes the command for starting the simulator.
	 * @param args arguments that may contain a start command
	 * @return whether instantiation and starting was successful
	 */
	public boolean cmdStartSimulator(List<String> args) {
		if((args != null) && (args.size() >= 1)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_START)) {
				args.remove(0);
				return startSimulator();
			}
		}
		
		return false;
	}
	
	/**
	 * Stops the simulator thread and returns when the thread is stopped.
	 * @return whether stopping was successful
	 */
	public boolean stopSimulator() {
		boolean simStopped = false;
		
		if (simulator != null) {
			simStopped = simulator.stop();
			simulator = null;
			
			if(simStopped) {
				log(this.getClass(), "The simulator has been stopped and will no longer respond to incoming APDUs until it is (re-) started", UI);
			}
		}
		
		return simStopped;
	}
	
	/**
	 * This method processes the command for stopping the simulator.
	 * @param args arguments that may contain a stop command
	 * @return whether stopping was successful
	 */
	public boolean cmdStopSimulator(List<String> args) {
		if((args != null) && (args.size() >= 1)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_STOP)) {
				args.remove(0);
				return stopSimulator();
			}
		}
		
		return false;
	}
	
	/**
	 * This method restarts the simulator.
	 * @return whether restarting has been successful
	 */
	public boolean restartSimulator() {
		stopSimulator();
		return startSimulator();
	}
	
	/**
	 * This method processes the command for restarting the simulator.
	 * @param args arguments that may contain a restart command
	 * @return whether restarting was successful
	 */
	public boolean cmdRestartSimulator(List<String> args) {
		if((args != null) && (args.size() >= 1)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_RESTART)) {
				args.remove(0);
				return restartSimulator();
			}
		}
		
		return false;
	}
	
	/**
	 * This method processes the command for exiting the simulator.
	 * @param args arguments that may contain an exit command
	 * @return whether exiting was successful
	 */
	public boolean cmdExitSimulator(List<String> args) {
		if((args != null) && (args.size() >= 1)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_EXIT)) {
				args.remove(0);
				return exitSimulator();
			}
		}
		
		return false;
	}
	
	/**
	 * This method stops the simulator and no longer accepts user input.
	 * @return whether the simulator has been stopped
	 */
	public boolean exitSimulator() {
		executeUserCommands = false;
		log(this.getClass(), LOG_SIM_EXIT, UI);
		
		boolean stopped = stopSimulator();
		
		if(stopped) {
			log(this.getClass(), "The simulator has been terminated and will no longer respond to incoming APDUs or commands", UI);
		}
				
		return stopped;
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
		log(PersoSim.class, "Parsing personalization from file " + persoFileName, INFO);
		return (Personalization) um.unmarshal(new FileReader(persoFile));
	}
	
	/**
	 * This method sets a new port for the simulator to be used at the next start.
	 * In order for the changes to take effect, the simulator needs to be restarted.
	 * @param newPortString the new port to be used
	 */
	public void setPort(String newPortString) {
		if(newPortString == null) {throw new NullPointerException("port parameter must not be null");}
		int newPort = Integer.parseInt(newPortString);
		if(newPort < 0) {throw new IllegalArgumentException("port number must be positive");}
		
		simPort = newPort;
		
		log(this.getClass(), "new port set to " + newPort + " after restart of simulation.", UI);
		
		//IMPL check for port being unused
	}

	/**
	 * Transmit an APDU to the card
	 * 
	 * @param cmd
	 *            string containing the command
	 * @return the response
	 */
	private String sendCmdApdu(String cmd) {
		cmd = cmd.trim();

		Pattern cmdSendApduPattern = Pattern
				.compile("^send[aA]pdu ([0-9a-fA-F\\s]+)$");
		Matcher matcher = cmdSendApduPattern.matcher(cmd);
		if (!matcher.matches()) {
			throw new RuntimeException("invalid arguments to sendApdu");
		}
		String apdu = matcher.group(1);
		return exchangeApdu(apdu);

	}
	
	/**
	 * This method processes the send APDU command according to the provided arguments.
	 * @param args the arguments provided for processing
	 * @return whether processing has been successful
	 */
	public String cmdSendApdu(List<String> args) {
		if((args != null) && (args.size() >= 2)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_SEND_APDU)) {
				String result;
				
				try{
	    			result = sendCmdApdu("sendApdu " + args.get(1));
	    			args.remove(0);
	    			args.remove(0);
	    			return result;
	    		} catch(RuntimeException e) {
	    			result = "unable to send APDU, reason is: " + e.getMessage();
	    			args.remove(0);
	    			return result;
	    		}
			} else{
				return "no send APDU command";
			}
		} else{
			return "missing parameter for APDU content";
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
		return exchangeApdu(cmdApdu, DEFAULT_SIM_HOST, simPort);
	}

	/**
	 * Transmit the given APDU to the simulator identified by host name and port
	 * number, where it will be processed and answered by a response. The
	 * response APDU is received from the simulator via its socket interface and
	 * returned to the caller as HexString.
	 * 
	 * @param cmdApdu
	 *            HexString containing the CommandAPDU
	 * @param host
	 *            the host to contact
	 * @param port
	 *            the port to query
	 * @return the response
	 */
	private String exchangeApdu(String cmdApdu, String host, int port) {
		cmdApdu = cmdApdu.replaceAll("\\s", ""); // remove any whitespace

		Socket socket;
		try {
			socket = new Socket(host, port);
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
			log(this.getClass(), "> " + cmdApdu, UI);
			log(this.getClass(), "< " + respApdu, UI);
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
	private void printHelpArgs() {
		log(this.getClass(), "Available commands:", UI);
		log(this.getClass(), ARG_LOAD_PERSONALIZATION + " <file name>", UI);
		log(this.getClass(), ARG_SET_PORT + " <port number>", UI);
		log(this.getClass(), ARG_HELP, UI);
	}
	
	/**
	 * This method prints the help menu to the user command line.
	 */
	private void printHelpCmd() {
		log(this.getClass(), "Available commands:", UI);
		log(this.getClass(), CMD_SEND_APDU + " <hexstring>", UI);
		log(this.getClass(), CMD_LOAD_PERSONALIZATION + " <file name>", UI);
		log(this.getClass(), CMD_SET_PORT + " <port number>", UI);
		log(this.getClass(), CMD_START, UI);
		log(this.getClass(), CMD_RESTART, UI);
		log(this.getClass(), CMD_STOP, UI);
		log(this.getClass(), CMD_EXIT, UI);
		log(this.getClass(), CMD_HELP, UI);
	}
	
	/**
	 * This method processes the load personalization command according to the provided arguments.
	 * @param args the arguments provided for processing the load personalization command
	 * @return whether processing of the load personalization command has been successful
	 */
	public boolean cmdLoadPersonalization(List<String> args) {
		
		if((args != null) && (args.size() >= 2)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_LOAD_PERSONALIZATION) || cmd.equals(ARG_LOAD_PERSONALIZATION)) {
				String arg = args.get(1);
				
				args.remove(0);
    			args.remove(0);
    			
    			return loadPersonalization(arg);
				
			}
		}
		
		return false;
	}
	
	public boolean loadPersonalization(String identifier) {
		currentPersonalization = null;

		//try to parse the given identifier as profile number
		try {
			int personalizationNumber = Integer.parseInt(identifier);
			log(this.getClass(), "trying to load personalization profile no: " + personalizationNumber, INFO);
			Bundle plugin = Platform.getBundle("de.persosim.simulator");
			
			if(plugin == null) {
				// TODO how to handle this case? Add OSGI requirement?
				log(this.getClass(), "unable to resolve bundle \"de.persosim.simulator\" - personalization unchanged", INFO);
				return false;
			} else {
				URL url = plugin.getEntry(persoPath + persoFilePrefix + String.format("%02d", personalizationNumber) + persoFilePostfix);
				URL resolvedURL = FileLocator.resolve(url);
				log(this.getClass(), "resolved absolute URL for selected profile is: " + resolvedURL, INFO);
				identifier = resolvedURL.getPath();
			}
		} catch (Exception e) {
			//seems to be a call to load a personalization by path
		}
		
		//actually load perso from the identified file
		try{
			currentPersonalization = parsePersonalization(identifier);
			if(processingCommandLineArguments) {
				return true;
			} else{
				return restartSimulator();
			}
		} catch(FileNotFoundException | JAXBException e) {
			logException(this.getClass(), e);
			stopSimulator();
			log(this.getClass(), "simulation is stopped", INFO);
			return false;
		}
	}
	
	/**
	 * This method processes the set port command according to the provided arguments.
	 * @param args the arguments provided for processing the set port command
	 * @return whether processing of the set port command has been successful
	 */
	public boolean cmdSetPortNo(List<String> args) {
		if((args != null) && (args.size() >= 2)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_SET_PORT) || cmd.equals(ARG_SET_PORT)) {
				String arg = args.get(1);
				args.remove(0);
    			args.remove(0);
				
				try{
	    			setPort(arg);
	    			
	    			if(processingCommandLineArguments) {
	    				return true;
	    			} else{
	    				return restartSimulator();
	    			}
	    		} catch(IllegalArgumentException | NullPointerException e) {
	    			logException(this.getClass(), e);
	    			return false;
	    		}
			}
		}
		
		return false;
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

		executeUserCommands = true;
		while (executeUserCommands) {
			log(this.getClass(), "PersoSim commandline: ", UI);
			String cmd = null;
			try {
				cmd = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (cmd != null) {
					cmd = cmd.trim();
					String[] args = parseCommand(cmd);
					executeUserCommands(args);
				}
			} catch (RuntimeException e) {
				showExceptionToUser(e);
			}
		}
	}
	
	public boolean cmdHelp(List<String> args) {
		if((args != null) && (args.size() >= 1)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_HELP) || cmd.equals(ARG_HELP)) {
				args.remove(0);
				
				if(processingCommandLineArguments) {
					printHelpArgs();
				} else{
					printHelpCmd();
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This method implements the execution of commands initiated by user interaction.
	 * It processes user commands based on a single String containing the whole command and all of its parameters.
	 * @param cmd the single String command
	 */
	public void executeUserCommands(String cmd) {
		String trimmedCmd = cmd.trim();
		String[] args = parseCommand(trimmedCmd);
		
		executeUserCommands(args);
	}
	
	/**
	 * This method implements the execution of commands initiated by user interaction.
	 * It processes user commands based on single String representing the command and all of its parameters.
	 * @param args the parsed commands and arguments
	 */
	public void executeUserCommands(String... args) {
		if((args == null) || (args.length == 0)) {log(this.getClass(), LOG_NO_OPERATION, INFO); return;}
		
		ArrayList<String> currentArgs = new ArrayList<String>(Arrays.asList(args)); // plain return value of Arrays.asList() does not support required remove operation
		
		for(int i = currentArgs.size() - 1; i >= 0; i--) {
			if(currentArgs.get(i) == null) {
				currentArgs.remove(i);
			}
		}
		
		if(currentArgs.size() == 0) {log(this.getClass(), LOG_NO_OPERATION, INFO); return;}
		
		int noOfArgsWhenCheckedLast;
		while(currentArgs.size() > 0) {
			noOfArgsWhenCheckedLast = currentArgs.size();
			
			cmdLoadPersonalization(currentArgs);
			cmdSetPortNo(currentArgs);
			cmdSendApdu(currentArgs);
			cmdStartSimulator(currentArgs);
			cmdRestartSimulator(currentArgs);
			cmdStopSimulator(currentArgs);
			cmdExitSimulator(currentArgs);
			cmdHelp(currentArgs);
			
			if(noOfArgsWhenCheckedLast == currentArgs.size()) {
				//first command in queue has not been processed
				String currentArgument = currentArgs.get(0);
				log(this.getClass(), LOG_UNKNOWN_ARG + " \"" + currentArgument + "\" will be ignored, processing of arguments stopped", WARN);
				currentArgs.remove(0);
				printHelpCmd();
				break;
			}
		}
		
	}
	
	/**
	 * This method implements the execution of commands initiated by command line arguments.
	 * @param args the parsed commands and arguments
	 */
	public void handleArgs(String... args) {
		if((args == null) || (args.length == 0)) {log(this.getClass(), LOG_NO_OPERATION, INFO); return;}
		
		processingCommandLineArguments = true;
		
		List<String> currentArgs = Arrays.asList(args);
		// the list returned by Arrays.asList() does not support optional but required remove operation
		currentArgs = new ArrayList<String>(currentArgs);
		
		for(int i = currentArgs.size() - 1; i >= 0; i--) {
			if(currentArgs.get(i) == null) {
				currentArgs.remove(i);
			}
		}
		
		if(currentArgs.size() == 0) {log(this.getClass(), LOG_NO_OPERATION, INFO); return;}
		
		int noOfArgsWhenCheckedLast;
		while(currentArgs.size() > 0) {
			noOfArgsWhenCheckedLast = currentArgs.size();
			
			cmdLoadPersonalization(currentArgs);
			cmdSetPortNo(currentArgs);
			cmdHelp(currentArgs);
			
			if(currentArgs.size() > 0) {
				if(currentArgs.get(0).equals(CMD_CONSOLE_ONLY)) {
					// do no actual processing, i.e. prevent simulator from logging unknown command error as command has already been processed
		        	// command is passed on as part of unprocessed original command line arguments
		        	currentArgs.remove(0);
				}
			}
			
			if(noOfArgsWhenCheckedLast == currentArgs.size()) {
				//first command in queue has not been processed
				String currentArgument = currentArgs.get(0);
				log(this.getClass(), LOG_UNKNOWN_ARG + " \"" + currentArgument + "\" will be ignored, processing of arguments stopped", WARN);
				currentArgs.remove(0);
				printHelpCmd();
				break;
			}
		}
		
		processingCommandLineArguments = false;
		
	}

}
