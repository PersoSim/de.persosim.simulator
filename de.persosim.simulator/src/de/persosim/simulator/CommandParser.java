package de.persosim.simulator;

import static de.persosim.simulator.utils.PersoSimLogger.ERROR;
import static de.persosim.simulator.utils.PersoSimLogger.INFO;
import static de.persosim.simulator.utils.PersoSimLogger.WARN;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;
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
	private static PersoSim sim = null;
	private static de.persosim.simulator.Activator persoSimPlugin = null;
	
	public static final String persoPlugin = "platform:/plugin/de.persosim.rcp/";
	public static final String persoPath = "personalization/profiles/";
	public static final String persoFilePrefix = "Profile";
	public static final String persoFilePostfix = ".xml";
	
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
				disablePersoSimService();
				return true;
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
				return getPersoSim().restartSimulator();
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
		log(CommandParser.class, "Available commands:", INFO);
		log(CommandParser.class, ARG_LOAD_PERSONALIZATION + " <file name>", INFO);
		log(CommandParser.class, ARG_SET_PORT + " <port number>", INFO);
		log(CommandParser.class, ARG_HELP, INFO);
	}
	
	/**
	 * This method prints the help menu to the user command line.
	 */
	private static void printHelpCmd() {
		log(CommandParser.class, "Available commands:", INFO);
		log(CommandParser.class, CMD_SEND_APDU + " <hexstring>", INFO);
		log(CommandParser.class, CMD_LOAD_PERSONALIZATION + " <file name>", INFO);
		log(CommandParser.class, CMD_START, INFO);
		log(CommandParser.class, CMD_RESTART, INFO);
		log(CommandParser.class, CMD_STOP, INFO);
		log(CommandParser.class, CMD_HELP, INFO);
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
				Personalization perso = getPerso(arg);
				
				if (perso != null) {
					PersoSim sim = getPersoSim();
					if (sim.loadPersonalization(perso)){
						return true;
					}
    			}

				// the personalization could not be loaded
				Activator.getDefault().disableService();
			}
		}
		
		return false;
	}
	
	/**
	 * This method parses the given identifier and loads the personalization
	 * @param identifier
	 * @return a personalization object
	 */
	private static Personalization getPerso(String identifier){

		String filePath = "";
		int personalizationNumber = 0;
		File xmlFile = new File(identifier);
		if (xmlFile.exists() && xmlFile.isFile()) {
			filePath = identifier;
		} else {
			//try to parse the given identifier as profile number
			try {
			personalizationNumber = Integer.parseInt(identifier);
			} catch (NumberFormatException e) {
				log(CommandParser.class, "identifier is no valid path or profile number!", ERROR);
				return null;
			}
			if(personalizationNumber > 10) {
				log(CommandParser.class, "personalization profile no: " + personalizationNumber + " does not exist", INFO);
				return null;
			}
			log(CommandParser.class, "trying to load personalization profile no: " + personalizationNumber, INFO);
			Bundle plugin = Activator.getContext().getBundle();
			
			URL url = plugin.getEntry (persoPath);
			URL resolvedUrl;
			File folder = null;
			
			try {
				resolvedUrl = FileLocator.resolve(url);
				folder = new File(resolvedUrl.getFile());
			} catch (IOException e) {
				log(CommandParser.class, e.getMessage(), ERROR);
			}
			if (personalizationNumber < 10) {
				identifier = "0" + personalizationNumber;
			}
			filePath = folder.getAbsolutePath() + File.separator + "Profile" + identifier + ".xml";
		}
		
		//actually load perso from the identified file
		try{
			return parsePersonalization(filePath);
		} catch(FileNotFoundException e) {
			log(CommandParser.class, "unable to set personalization, reason is: " + e.getMessage(), ERROR);
			log(CommandParser.class, "simulation is stopped", ERROR);
			return null;
		}
	} 
	
	/**
	 * This method parses a {@link Personalization} object from a file identified by its name.
	 * @param persoFileName the name of the file to contain the personalization
	 * @return the parsed personalization
	 * @throws FileNotFoundException 
	 * @throws JAXBException if parsing of personalization not successful
	 */
	public static Personalization parsePersonalization(String persoFileName) throws FileNotFoundException {
		log(CommandParser.class, "Parsing personalization from file " + persoFileName, INFO);
		return (Personalization) PersonalizationFactory.unmarshal(persoFileName);
	}
	
	public static void executeUserCommands(String... args) {
		if((args == null) || (args.length == 0)) {log(CommandParser.class, LOG_NO_OPERATION, INFO); return;}
		
		ArrayList<String> currentArgs = new ArrayList<String>(Arrays.asList(args)); // plain return value of Arrays.asList() does not support required remove operation
		
		for(int i = currentArgs.size() - 1; i >= 0; i--) {
			if(currentArgs.get(i) == null) {
				currentArgs.remove(i);
			}
		}
		
		if(currentArgs.size() == 0) {log(CommandParser.class, LOG_NO_OPERATION, INFO); return;}
		
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
				log(CommandParser.class, LOG_UNKNOWN_ARG + " \"" + currentArgument + "\" will be ignored, processing of arguments stopped", WARN);
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
		if((args == null) || (args.length == 0)) {log(CommandParser.class, LOG_NO_OPERATION, INFO); return;}
		
		processingCommandLineArguments = true;
		
		List<String> currentArgs = Arrays.asList(args);
		// the list returned by Arrays.asList() does not support optional but required remove operation
		currentArgs = new ArrayList<String>(currentArgs);
		
		for(int i = currentArgs.size() - 1; i >= 0; i--) {
			if(currentArgs.get(i) == null) {
				currentArgs.remove(i);
			}
		}
		
		if(currentArgs.size() == 0) {log(CommandParser.class, LOG_NO_OPERATION, INFO); return;}
		
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
				log(CommandParser.class, LOG_UNKNOWN_ARG + " \"" + currentArgument + "\" will be ignored, processing of arguments stopped", ERROR);
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
		log(CommandParser.class, "> " + cmdApdu, INFO);
		log(CommandParser.class, "< " + respApdu, INFO);
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
		log(CommandParser.class, "Exception: " + e.getMessage(), INFO);
		e.printStackTrace();
	}
	
	private static void enablePersoSimService() {
		persoSimPlugin = de.persosim.simulator.Activator.getDefault();
		persoSimPlugin.enableService();
	}
	
	
	private static PersoSim getPersoSim() {
		enablePersoSimService();
		if (persoSimPlugin != null) {
			sim = (PersoSim) persoSimPlugin.getSim();
		}
		return sim;
	}
	
	private static void disablePersoSimService() {
		de.persosim.simulator.Activator.getDefault().disableService();
		sim = null;
	}
	

}
