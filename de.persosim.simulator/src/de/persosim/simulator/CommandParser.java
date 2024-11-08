package de.persosim.simulator;

import static org.globaltester.logging.BasicLogger.log;
import static org.globaltester.logging.BasicLogger.logException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.globaltester.logging.tags.LogLevel;
import org.globaltester.simulator.Simulator;
import org.osgi.framework.Bundle;

import com.thoughtworks.xstream.XStreamException;

import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;
import de.persosim.simulator.perso.export.ProfileHelper;
import de.persosim.simulator.utils.HexString;

/**
 * This class provides methods that parse console commands for the control of
 * the Simulator and calls the corresponding methods of the {@link Simulator}
 * interface.
 * 
 * @author mboonk
 *
 */
public class CommandParser {

	public static final int DEFAULT_SIM_PORT = 9876;
	public static final String DEFAULT_SIM_HOST = "localhost";

	public static final String CMD_START = "start";
	public static final String CMD_RESTART = "restart";
	public static final String CMD_STOP = "stop";
	public static final String CMD_EXIT = "exit";
	public static final String ARG_SET_PORT = "-port";
	public static final String CMD_LOAD_PERSONALIZATION = "loadperso";
	public static final String ARG_LOAD_PERSONALIZATION = "-perso";
	public static final String CMD_SEND_APDU = "sendapdu";
	public static final String CMD_HELP = "help";
	public static final String ARG_HELP = "-h";
	public static final String CMD_CONSOLE_ONLY = "--consoleOnly";

	public static final String LOG_UNKNOWN_ARG  = "unknown argument";
	public static final String LOG_NO_OPERATION = "nothing to process";
	
	private static boolean processingCommandLineArguments = false;
	
	public static final String PERSO_PATH = "personalization/profiles/";
	public static final String PERSO_FILE_PREFIX = "Profile";
	public static final String PERSO_FILE_POSTFIX = ".perso";
	
	/**
	 * This method processes the command for starting the simulator.
	 * @param args arguments that may contain a start command
	 * @return whether instantiation and starting was successful
	 */
	public static boolean cmdStartSimulator(List<String> args) {
		if((args != null) && (args.size() >= 1)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_START)) {
				args.remove(0);
				de.persosim.simulator.Activator.getDefault().enableService();
				if (getPersoSim() == null) {
					log(CommandParser.class, "Enabling the PersoSimService failed", LogLevel.ERROR);
				}
					
				return getPersoSim().startSimulator();
			}
		}
		
		return false;
	}
	
	/**
	 * This method processes the command for stopping the simulator.
	 * @param args arguments that may contain a stop command
	 * @return whether stopping was successful
	 */
	public static boolean cmdStopSimulator(List<String> args) {
		if((args != null) && (args.size() >= 1)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_STOP)) {
				args.remove(0);
				if(getPersoSim() != null) {
					de.persosim.simulator.Activator.getDefault().disableService();
					return true;
				}
				else {
					log(CommandParser.class, "No running PersoSimService found", LogLevel.WARN);
				}
			}
		}

		return false;
	}

	
	/**
	 * This method processes the command for restarting the simulator.
	 * @param args arguments that may contain a restart command
	 * @return whether restarting was successful
	 */
	public static boolean cmdRestartSimulator(List<String> args) {
		if((args != null) && (args.size() >= 1)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_RESTART)) {
				args.remove(0);
				if(getPersoSim() != null)
					return getPersoSim().restartSimulator();
				else
					log(CommandParser.class, "No running PersoSimService found", LogLevel.WARN);
			}
		}
		
		return false;
	}

	
	/**
	 * This method processes the send APDU command according to the provided arguments.
	 * @param args the arguments provided for processing
	 * @return whether processing has been successful
	 */
	public static String cmdSendApdu(List<String> args) {
		if((args != null) && (args.size() >= 2)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_SEND_APDU)) {
				String result;
				if(getPersoSim() != null) {
					try{
						PersoSim sim = getPersoSim();
		    			result = sendCmdApdu(sim, "sendApdu " + args.get(1));
		    			args.remove(0);
		    			args.remove(0);
		    			return result;
		    		} catch(RuntimeException e) {
		    			result = "unable to send APDU, reason is: " + e.getMessage();
		    			args.remove(0);
		    			return result;
		    		}
				} else {
					log(CommandParser.class, "Please enable the PersoSimService before sending apdus", LogLevel.WARN);
					return "";
				}
			} else{
				return "no send APDU command";
			}
		} else{
			return "missing parameter for APDU content";
		}
	}
	
	/**
	 * This method prints the help menu to the command line.
	 */
	private static void printHelpArgs() {
		log(CommandParser.class, "Available commands:", LogLevel.INFO);
		log(CommandParser.class, ARG_LOAD_PERSONALIZATION + " <file name>", LogLevel.INFO);
		log(CommandParser.class, ARG_SET_PORT + " <port number>", LogLevel.INFO);
		log(CommandParser.class, ARG_HELP, LogLevel.INFO);
	}
	
	/**
	 * This method prints the help menu to the user command line.
	 */
	private static void printHelpCmd() {
		log(CommandParser.class, "Available commands:", LogLevel.INFO);
		log(CommandParser.class, CMD_SEND_APDU + " <hexstring>", LogLevel.INFO);
		log(CommandParser.class, CMD_LOAD_PERSONALIZATION + " <file name>", LogLevel.INFO);
		log(CommandParser.class, CMD_START, LogLevel.INFO);
		log(CommandParser.class, CMD_RESTART, LogLevel.INFO);
		log(CommandParser.class, CMD_STOP, LogLevel.INFO);
		log(CommandParser.class, CMD_HELP, LogLevel.INFO);
	}
	
	/**
	 * This method processes the load personalization command according to the provided arguments.
	 * @param args the arguments provided for processing the load personalization command
	 * @return whether processing of the load personalization command has been successful
	 */
	public static boolean cmdLoadPersonalization(List<String> args) {
		
		if((args != null) && (args.size() >= 2)) {
			String cmd = args.get(0);
			
			if(cmd.equals(CMD_LOAD_PERSONALIZATION) || cmd.equals(ARG_LOAD_PERSONALIZATION)) {
				
				String arg = args.get(1);
				
				args.remove(0);
				args.remove(0);
			
				Personalization perso = null;
				try {
					perso = getPerso(arg);
				} catch (IllegalArgumentException e) {
					logException(CommandParser.class, "Unable to load personalization", e, LogLevel.ERROR);
				}
				
				if (perso != null) {
					PersoSim sim = getPersoSim();
					if (sim != null) {
						if (sim.loadPersonalization(perso)){
							return true;
						}
					} else {
						log(CommandParser.class, "Please enable the PersoSimService before loading a personalization", LogLevel.WARN);
					}
    			}
			}
		}
		
		return false;
	}

	public static Personalization getPerso(String identifier) throws IllegalArgumentException {
		return getPerso(identifier, true);
	}
		
	/**
	 * This method parses the given identifier and loads the personalization
	 * @param identifier
	 * @return a personalization object
	 * @throws IllegalArgumentException iff the identifier does not reference a loadable perso 
	 */
	public static Personalization getPerso(String identifier, boolean withOverlayProfile) throws IllegalArgumentException {
		log(CommandParser.class, "Trying to load personalization for identifier '" + identifier + "'", LogLevel.INFO);

		InputStream stream = null;
		
		int personalizationNumber = 0;
		if (Files.exists(Paths.get(identifier))) {
			try {
				stream = Files.newInputStream(Paths.get(identifier));
			} catch (IOException e) {
				throw new IllegalArgumentException("Unable to load personalization from file", e);
			}
		} else {
			// try to parse the given identifier as profile number
			
			if (Activator.getContext() == null) {
				throw new IllegalArgumentException("Loading profiles by profile number is supported only when running within an OSGi environment");
			}
			
			try {
			personalizationNumber = Integer.parseInt(identifier);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("identifier is no valid path or profile number!", e);
			}

			if(personalizationNumber > 10) {
				throw new IllegalArgumentException("personalization profile no: " + personalizationNumber + " does not exist");
			}
			
			log(CommandParser.class, "trying to load personalization profile no: " + personalizationNumber, LogLevel.INFO);
			Bundle plugin = Activator.getContext().getBundle();
			
			if (personalizationNumber < 10) {
				identifier = "0" + personalizationNumber;
			}
			
			URL url = plugin.getEntry (PERSO_PATH + File.separator + PERSO_FILE_PREFIX + identifier + PERSO_FILE_POSTFIX);
			
			try {
				stream = url.openConnection().getInputStream();
			} catch (IOException e) {
				throw new IllegalArgumentException("Unable to load personalization from Bundle content", e);
			}
		}
		
		// load perso from the identified file
		Personalization perso = null;
		try {
			perso = (Personalization) PersonalizationFactory.unmarshal(stream);
			if (perso != null)
				log(CommandParser.class, "Personalization for identifier '" + identifier + "' loaded.", LogLevel.INFO);
			else {
				log(CommandParser.class, "Personalization for identifier '" + identifier + "' could not be loaded.", LogLevel.ERROR);
				throw new IllegalArgumentException( "Personalization for identifier '" + identifier + "' could not be loaded.");
			}
		} catch (XStreamException e) {
			throw new IllegalArgumentException("Unable to deserialize personalization", e);
		}
		
		if (withOverlayProfile)
			ProfileHelper.handleOverlayProfile(perso);
		return perso;
	} 
	
	public static void executeUserCommands(String... args) {
		if((args == null) || (args.length == 0)) {log(CommandParser.class, LOG_NO_OPERATION, LogLevel.INFO); return;}
		
		ArrayList<String> currentArgs = new ArrayList<String>(Arrays.asList(args)); // plain return value of Arrays.asList() does not support required remove operation
		
		for(int i = currentArgs.size() - 1; i >= 0; i--) {
			if(currentArgs.get(i) == null) {
				currentArgs.remove(i);
			}
		}
		
		if(currentArgs.size() == 0) {log(CommandParser.class, LOG_NO_OPERATION, LogLevel.INFO); return;}
		
		int noOfArgsWhenCheckedLast;
		while(currentArgs.size() > 0) {
			noOfArgsWhenCheckedLast = currentArgs.size();
			
			cmdLoadPersonalization(currentArgs);
			cmdSendApdu(currentArgs);
			cmdStartSimulator(currentArgs);
			cmdRestartSimulator(currentArgs);
			cmdStopSimulator(currentArgs);
			cmdHelp(currentArgs);
			
			
			if(noOfArgsWhenCheckedLast == currentArgs.size()) {
				//first command in queue has not been processed
				String currentArgument = currentArgs.get(0);
				log(CommandParser.class, LOG_UNKNOWN_ARG + " \"" + currentArgument + "\" will be ignored, processing of arguments stopped", LogLevel.WARN);
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
	public  static void handleArgs(Simulator sim, String... args) {
		if((args == null) || (args.length == 0)) {log(CommandParser.class, LOG_NO_OPERATION, LogLevel.INFO); return;}
		
		processingCommandLineArguments = true;
		
		List<String> currentArgs = Arrays.asList(args);
		// the list returned by Arrays.asList() does not support optional but required remove operation
		currentArgs = new ArrayList<String>(currentArgs);
		
		for(int i = currentArgs.size() - 1; i >= 0; i--) {
			if(currentArgs.get(i) == null) {
				currentArgs.remove(i);
			}
		}
		
		if(currentArgs.size() == 0) {log(CommandParser.class, LOG_NO_OPERATION, LogLevel.INFO); return;}
		
		int noOfArgsWhenCheckedLast;
		while(currentArgs.size() > 0) {
			noOfArgsWhenCheckedLast = currentArgs.size();
			
			cmdLoadPersonalization(currentArgs);
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
				log(CommandParser.class, LOG_UNKNOWN_ARG + " \"" + currentArgument + "\" will be ignored, processing of arguments stopped", LogLevel.ERROR);
				currentArgs.remove(0);
				printHelpCmd();
				break;
			}
		}
		
		processingCommandLineArguments = false;
		
	}
	
	public static boolean cmdHelp(List<String> args) {
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
	 * Transmit an APDU to the card
	 * 
	 * @param cmd
	 *            string containing the command
	 * @return the response
	 */
	private static String sendCmdApdu(Simulator sim, String cmd) {
		cmd = cmd.trim();

		Pattern cmdSendApduPattern = Pattern
				.compile("^send[aA]pdu ([0-9a-fA-F\\s]+)$");
		Matcher matcher = cmdSendApduPattern.matcher(cmd);
		if (!matcher.matches()) {
			throw new RuntimeException("invalid arguments to sendApdu");
		}
		String apdu = matcher.group(1);
		return exchangeApdu(sim, apdu);

	}
	
	/**
	 * Transmit the given APDU to the simulator where it will be processed
	 * and answered by a response. The response APDU is received from the 
	 * simulator and returned to the caller as HexString.
	 * 
	 * @param cmdApdu HexString containing the CommandAPDU
	 * @return the response
	 */
	private static String exchangeApdu(Simulator sim, String cmdApdu) {
		cmdApdu = cmdApdu.replaceAll("\\s", ""); // remove any whitespace
		String respApdu =  HexString.dump(sim.processCommand(HexString.toByteArray(cmdApdu)));
		log(CommandParser.class, "> " + cmdApdu, LogLevel.INFO);
		log(CommandParser.class, "< " + respApdu, LogLevel.INFO);
		return respApdu;
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
	
	public static void executeUserCommands(String cmd) {
		String trimmedCmd = cmd.trim();
		String[] args = parseCommand(trimmedCmd);
		
		executeUserCommands(args);
	}
	
	public static void showExceptionToUser(Exception e) {
		log(CommandParser.class, "Exception: " + e.getMessage(), LogLevel.INFO);
		e.printStackTrace();
	}
	
	private static PersoSim getPersoSim() {
		return de.persosim.simulator.Activator.getDefault().getSim();
	}
}
