package de.persosim.simulator.control.soap.service;

import static org.globaltester.logging.BasicLogger.log;
import static org.globaltester.logging.BasicLogger.logException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Action;

import org.globaltester.control.AbstractRemoteControlHandler;
import org.globaltester.control.soap.JaxWsSoapAdapter;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.CommandParser;
import de.persosim.simulator.PersoSim;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.perso.export.ProfileHelper;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;
import de.persosim.simulator.utils.HexString;


@WebService(serviceName = "PersoSimRemoteControlService", portName = "PersoSimRemoteControlPort", name = "PersoSimRemoteControl", targetNamespace = "http://service.soap.control.simulator.persosim.de/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class PersoSimRemoteControlImpl extends AbstractRemoteControlHandler implements JaxWsSoapAdapter
{
	@Override
	public String getIdentifier()
	{
		return "PersoSimRemoteControl";
	}

	@WebMethod
	@WebResult(partName = "return")
	@Action(input = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/loadPersoRequest", output = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/loadPersoResponse")
	public PersoSimRemoteControlResult loadPerso(String filePath)
	{
		String command = CommandParser.CMD_LOAD_PERSONALIZATION + " " + filePath;
		log("Executing command: '" + command + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		String[] commands = CommandParser.parseCommand(command);

		PersoSimPreferenceManager.storePreference("PREF_NON_INTERACTIVE", Boolean.TRUE.toString());

		Path rootPathPersoFiles = ProfileHelper.getRootPathPersoFiles();
		if (rootPathPersoFiles != null)
			log("Root path Perso files: '" + rootPathPersoFiles.toAbsolutePath().toString() + "'", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		int lastUnixSep = filePath.lastIndexOf('/');
		int lastWinSep = filePath.lastIndexOf('\\');
		int lastIndex = Math.max(lastUnixSep, lastWinSep);
		if (lastIndex == -1 && filePath.startsWith("Profile") && filePath.endsWith(".perso")) {
			filePath = rootPathPersoFiles + File.separator + filePath;
			log("Perso template file path: '" + filePath + "'", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			commands[1] = filePath;
		}

		Path pathPerso = null;

		try {
			pathPerso = Path.of(filePath);
		}
		catch (IllegalArgumentException e) {
			logException("Invalid file path: '" + filePath + "'", e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			String resultMessage = "NOT OK. Perso '" + filePath + "' could not be loaded. Invalid file path: '" + filePath + "'.";
			return createResult(1, resultMessage);
		}

		if (!Files.exists(pathPerso)) {
			String resultMessage = "NOT OK. Perso '" + filePath + "' could not be loaded. File does not exist.";
			return createResult(1, resultMessage);
		}
		if (Files.isDirectory(pathPerso)) {
			String resultMessage = "NOT OK. Perso '" + filePath + "' could not be loaded. Path is a directory.";
			log(resultMessage, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return createResult(1, resultMessage);
		}

		boolean withOverlay = false;

		log("Path Perso file: '" + pathPerso.toAbsolutePath().toString() + "'", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		if ((rootPathPersoFiles != null) && (pathPerso.startsWith(rootPathPersoFiles) || pathPerso.startsWith(rootPathPersoFiles.toAbsolutePath().toString().replace('\\', '/')))) {
			log("Default profiles root path", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			Path pathPersoOverlay = Path.of(ProfileHelper.getOverlayFilePath(pathPerso));
			if (Files.exists(pathPersoOverlay) && !Files.isDirectory(pathPersoOverlay)) {
				log("Overlay file '" + pathPersoOverlay.toAbsolutePath().toString() + "' exists.", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
				withOverlay = true;
			}
			else {
				log("Overlay file '" + pathPersoOverlay.toAbsolutePath().toString() + "' does not exist.", LogLevel.WARN, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			}
		}

		PersoSim persoSim = de.persosim.simulator.Activator.getDefault().getSim();
		if (persoSim != null && !persoSim.isRunning()) {
			persoSim.startSimulator();
		}

		boolean result = CommandParser.cmdLoadPersonalization(new ArrayList<>(Arrays.asList(commands)), withOverlay).isOk();
		int resultCode = 0;
		String resultMessage;
		if (result) {
			resultMessage = "OK. Perso '" + filePath + "' loaded " + (withOverlay ? "with" : "without") + " overlay.";
			log(resultMessage, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		else {
			resultCode = 1;
			resultMessage = "NOT OK. Perso '" + filePath + "' could not be loaded.";
			log(resultMessage, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		return createResult(resultCode, resultMessage);
	}

	@WebMethod
	@WebResult(partName = "return")
	@Action(input = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/sendApduRequest", output = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/sendApduResponse")
	public PersoSimRemoteControlResult sendApdu(String apduAsHexString)
	{
		String command = CommandParser.CMD_SEND_APDU + " " + apduAsHexString;
		log("Executing command: '" + command + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		PersoSimPreferenceManager.storePreference("PREF_NON_INTERACTIVE", Boolean.TRUE.toString());

		PersoSim persoSim = de.persosim.simulator.Activator.getDefault().getSim();
		if (persoSim != null && !persoSim.isRunning()) {
			persoSim.startSimulator();
		}

		String[] commands = CommandParser.parseCommand(command);
		String resultPrettyPrint = CommandParser.cmdSendApdu(new ArrayList<>(Arrays.asList(commands))).getMessage().trim();
		String resultHex = null;
		int resultCode = 0;
		String resultMessage;
		if (resultPrettyPrint.contains("APDU")) {
			resultCode = 1;
			resultMessage = "NOT OK. SendApdu Result: '" + resultPrettyPrint + "'";
			log(resultMessage, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		else {
			resultMessage = "OK. SendApdu Result: '" + resultPrettyPrint + "'";
			log(resultMessage, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		return createResult(resultCode, resultMessage, resultHex, resultPrettyPrint); // TODO: Hex; add to CommandParserResult in MS3
	}

	@WebMethod
	@WebResult(partName = "return")
	@Action(input = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/resetRequest", output = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/resetResponse")
	public PersoSimRemoteControlResult reset()
	{
		log("Executing reset card", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		PersoSimPreferenceManager.storePreference("PREF_NON_INTERACTIVE", Boolean.TRUE.toString());

		PersoSim persoSim = de.persosim.simulator.Activator.getDefault().getSim();
		int resultCode = 0;
		String resultMessage = null;
		String resultPrettyPrint = null;
		String resultHex = null;
		if (persoSim != null && persoSim.isRunning()) {
			byte[] atr = persoSim.cardReset();
			resultHex = HexString.encode(atr);
			resultPrettyPrint = HexString.dump(atr).trim();
			resultMessage = "OK. Card reset done (ATR: '" + resultPrettyPrint + "').";
		}
		else {
			// resultCode = 1;
			resultMessage = "OK. PersoSim is not running. Nothing to reset.";
		}
		log(resultMessage, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		return createResult(resultCode, resultMessage, resultHex, resultPrettyPrint);
	}

	@Override
	public <T> T getAdapter(Class<T> c)
	{
		if (c == JaxWsSoapAdapter.class) {
			return c.cast(this);
		}
		return null;
	}

	/**
	 * Helper method, cause standard wsimport does not create parameterized constructors.
	 */
	public static PersoSimRemoteControlResult createResult(int resultCode, String resultMessage, String resultAsHex, String resultPrettyPrint)
	{
		PersoSimRemoteControlResult result = new PersoSimRemoteControlResult();
		result.setResultCode(resultCode);
		result.setResultMessage(resultMessage);
		result.setResultAsHex(resultAsHex);
		result.setResultPrettyPrint(resultPrettyPrint);
		return result;
	}

	/**
	 * Helper method, cause standard wsimport does not create parameterized constructors.
	 */
	public static PersoSimRemoteControlResult createResult(int resultCode, String resultMessage)
	{
		PersoSimRemoteControlResult result = new PersoSimRemoteControlResult();
		result.setResultCode(resultCode);
		result.setResultMessage(resultMessage);
		return result;
	}

}
