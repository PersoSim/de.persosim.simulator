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


@WebService(name = "PersoSimRemoteControl", targetNamespace = "http://service.soap.control.simulator.persosim.de/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class PersoSimRemoteControl extends AbstractRemoteControlHandler implements JaxWsSoapAdapter
{
	@Override
	public String getIdentifier()
	{
		return "PersoSimRemoteControl";
	}

	@WebMethod
	@WebResult(partName = "return")
	@Action(input = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/loadPersoRequest", output = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/loadPersoResponse")
	public String loadPerso(String filePath)
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

		String response = null;
		Path pathPerso = null;

		try {
			pathPerso = Path.of(filePath);
		}
		catch (IllegalArgumentException e) {
			logException("Invalid file path: '" + filePath + "'", e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			response = "NOT OK. Perso '" + filePath + "' could not be loaded. Invalid file path: '" + filePath + "'.";
			log(response, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return response;
		}

		if (!Files.exists(pathPerso)) {
			response = "NOT OK. Perso '" + filePath + "' could not be loaded. File does not exist.";
			log(response, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return response;
		}
		if (Files.isDirectory(pathPerso)) {
			response = "NOT OK. Perso '" + filePath + "' could not be loaded. Path is a directory.";
			log(response, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return response;
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

		boolean result = CommandParser.cmdLoadPersonalization(new ArrayList<>(Arrays.asList(commands)), withOverlay);
		if (result) {
			response = "OK. Perso '" + filePath + "' loaded " + (withOverlay ? "with" : "without") + " overlay.";
			log(response, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		else {
			response = "NOT OK. Perso '" + filePath + "' could not be loaded.";
			log(response, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		return response;
	}

	@WebMethod
	@WebResult(partName = "return")
	@Action(input = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/sendApduRequest", output = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/sendApduResponse")
	public String sendApdu(String apduAsHexString)
	{
		String command = CommandParser.CMD_SEND_APDU + " " + apduAsHexString;
		log("Executing command: '" + command + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		PersoSimPreferenceManager.storePreference("PREF_NON_INTERACTIVE", Boolean.TRUE.toString());

		PersoSim persoSim = de.persosim.simulator.Activator.getDefault().getSim();
		if (persoSim != null && !persoSim.isRunning()) {
			persoSim.startSimulator();
		}

		String[] commands = CommandParser.parseCommand(command);
		String result = CommandParser.cmdSendApdu(new ArrayList<>(Arrays.asList(commands))).trim();
		String response = null;
		if (result.contains("APDU")) {
			response = "NOT OK. SendApdu Result: '" + result + "'";
			log(response, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		else {
			response = "OK. SendApdu Result: '" + result + "'";
			log(response, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		return response;
	}

	@WebMethod
	@WebResult(partName = "return")
	@Action(input = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/resetRequest", output = "http://service.soap.control.simulator.persosim.de/PersoSimRemoteControl/resetResponse")
	public String reset()
	{
		log("Executing reset card", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		PersoSimPreferenceManager.storePreference("PREF_NON_INTERACTIVE", Boolean.TRUE.toString());

		PersoSim persoSim = de.persosim.simulator.Activator.getDefault().getSim();
		String response = null;
		if (persoSim != null && persoSim.isRunning()) {
			byte[] atr = persoSim.cardReset();
			response = "OK. Card reset done (ATR: '" + HexString.dump(atr).trim() + "').";
		}
		else {
			response = "OK. PersoSim is not running. Nothing to reset.";
		}
		log(response, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		return response;
	}

	@Override
	public <T> T getAdapter(Class<T> c)
	{
		if (c == JaxWsSoapAdapter.class) {
			return c.cast(this);
		}
		return null;
	}

}
