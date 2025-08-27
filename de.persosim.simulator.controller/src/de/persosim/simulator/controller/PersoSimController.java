package de.persosim.simulator.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import javax.xml.ws.WebServiceException;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.control.soap.service.PersoSimRemoteControlInterface;
import de.persosim.simulator.control.soap.service.PersoSimRemoteControlService;
import de.persosim.simulator.log.PersoSimLogTags;

public class PersoSimController implements IApplication
{
	private Process process;
	private String params; // full path including binary

	public PersoSimController()
	{
		// nothing to do; necessary for product
	}

	@Override
	public Object start(IApplicationContext context) throws Exception
	{
		BasicLogger.log("Start PersoSimController...", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		String[] args = (String[]) context.getArguments().get("application.args");
		if (args != null) {
			for (String arg : args) {
				BasicLogger.log("Argument: " + arg, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			}
			PersoSimController controller = new PersoSimController();
			controller.doWork(args);
		}

//		BasicLogger.log("Working ...", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
//		Thread.sleep(2000);
//		BasicLogger.log("Finished working.", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		return IApplication.EXIT_OK;
	}

	@Override
	public void stop()
	{
		BasicLogger.log("PersoSimController stopped.", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
	}

	public void setParams(String params)
	{
		this.params = params;
	}

	/**
	 * Starts the process if not already running.
	 */
	public void startProcess()
	{
		startProcess(false);
	}

	/**
	 * Starts the process if not already running.
	 */
	public boolean startProcess(boolean isRestart)
	{
		if (process != null && process.isAlive()) {
			BasicLogger.log("Process is already running.", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return true;
		}
		ProcessBuilder builder = new ProcessBuilder(params);
		try {
			process = builder.start();
		}
		catch (IOException e) {
			BasicLogger.log("Process could not be started: " + params, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			BasicLogger.logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return false;
		}
		if (!isRestart)
			BasicLogger.log("Process started: " + params, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		return true;
	}

	/**
	 * Stops the process if it was started by this program,
	 * otherwise attempts to kill it by name externally.
	 */
	public void stopProcess()
	{
		if (process == null || !process.isAlive()) {
			// Process might be externally running, try to kill by name
			killByName(getProcessNameFromPath(params));
			return;
		}
		process.destroy(); // attempt graceful termination
		try {
			if (!process.waitFor(3, java.util.concurrent.TimeUnit.SECONDS)) {
				process.destroyForcibly(); // force kill if not terminated
			}
		}
		catch (InterruptedException e) {
			BasicLogger.log("Interruption error while stopping process " + params, LogLevel.WARN, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			Thread.currentThread().interrupt();
			BasicLogger.logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		BasicLogger.log("Process stopped: " + params, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
	}

	/**
	 * Restarts the process.
	 * If process not running, only starts it.
	 */
	public void restartProcess()
	{
		if (isProcessAvailable()) {
			stopProcess();
			startProcess(true);
			BasicLogger.log("Process restarted: " + params, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		else {
			BasicLogger.log("Process not running yet. Starting it.", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			startProcess();
		}
	}

	/**
	 * Checks if the process is available.
	 * First checks if locally started process is alive.
	 * If not, checks OS process list by process name.
	 *
	 * @return true if process is running, false otherwise
	 */
	public boolean isProcessAvailable()
	{
		try {
			// Check locally started process state
			if (process != null && process.isAlive()) {
				return true;
			}

			// Check few external processes by name
			String processName = getProcessNameFromPath(params);
			String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

			Process checkProc;
			if (os.contains("win")) {
				// Windows: use tasklist and filter by image name
				String tasklistCmd = "tasklist /FI \"IMAGENAME eq " + processName + "\" /NH";
				checkProc = Runtime.getRuntime().exec(tasklistCmd);
			}
			else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
				// Linux/macOS: use pgrep with -f for full match
				String[] cmd = { "pgrep", "-f", processName };
				checkProc = Runtime.getRuntime().exec(cmd);
			}
			else {
				// Unknown OS, assume not running
				return false;
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(checkProc.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (!line.trim().isEmpty() && line.trim().contains(processName)) {
						// At least one matching process found
						return true;
					}
				}
			}
		}
		catch (IOException e) {
			BasicLogger.log("Error while checking if process is available.", LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			BasicLogger.logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return false;
		}

		return false;
	}

	/**
	 * Extracts the executable name from the full path.
	 * E.g., "C:\PersoSimDir\PersoSim.exe" --> "PersoSim.exe"
	 */
	private String getProcessNameFromPath(String path)
	{
		int lastUnixSep = path.lastIndexOf('/');
		int lastWinSep = path.lastIndexOf('\\');
		int lastIndex = Math.max(lastUnixSep, lastWinSep);

		if (lastIndex >= 0 && lastIndex < path.length() - 1) {
			return path.substring(lastIndex + 1);
		}
		return path;
	}

	/**
	 * Attempts to kill an externally running process by name, using OS-specific commands.
	 */
	private boolean killByName(String processName)
	{
		try {
			String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
			Process killProc = null;
			if (os.contains("win")) {
				String killCommand = "taskkill /F /IM " + (processName.endsWith(".exe") ? processName : processName + ".exe");
				killProc = Runtime.getRuntime().exec(killCommand);
			}
			else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
				String killCommand = "killall " + processName;
				killProc = Runtime.getRuntime().exec(killCommand);
			}
			else {
				BasicLogger.log("Kill by name is not supported on this operating system.", LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
				return false;
			}

			int exitCode = killProc.waitFor();
			if (exitCode == 0) {
				BasicLogger.log("External process " + processName + " killed.", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
				return true;
			}
			else {
				BasicLogger.log("Error while killing process " + processName + ", exit code=" + exitCode, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
				return false;
			}
		}
		catch (IOException e) {
			BasicLogger.log("Error while killing process " + processName, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			BasicLogger.logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return false;
		}
		catch (InterruptedException e) {
			BasicLogger.log("Interruption error while killing process " + processName, LogLevel.WARN, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			Thread.currentThread().interrupt();
			BasicLogger.logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return false;
		}
	}

	private PersoSimRemoteControlService getService()
	{
		PersoSimRemoteControlService service = null;
		try {
			service = new PersoSimRemoteControlService();
		}
		catch (WebServiceException e) {
			BasicLogger.logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		return service;
	}

	public String loadPerso(String fullPathToPersoFile)
	{
		BasicLogger.log("Try calling loadPerso from PersoSimRemoteControl...", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		PersoSimRemoteControlService service = getService();
		if (service == null) {
			String response = "PersoSim Service is not running. No respone.";
			BasicLogger.log(response, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return response;
		}
		PersoSimRemoteControlInterface port = service.getPersoSimRemoteControlPort();
		String response = port.loadPerso(fullPathToPersoFile);
		BasicLogger.log("Response: '" + response + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		return response;
	}

	public String sendApdu(String apduAsHexString)
	{
		BasicLogger.log("Try calling sendApdu from PersoSimRemoteControl...", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		PersoSimRemoteControlService service = getService();
		if (service == null) {
			String response = "PersoSim Service is not running. No respone.";
			BasicLogger.log(response, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return response;
		}
		PersoSimRemoteControlInterface port = service.getPersoSimRemoteControlPort();
		String response = port.sendApdu(apduAsHexString);
		BasicLogger.log("Response: '" + response + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		return response;
	}

	public String reset()
	{
		BasicLogger.log("Try calling reset from PersoSimRemoteControl...", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		PersoSimRemoteControlService service = getService();
		if (service == null) {
			String response = "PersoSim Service is not running. No respone.";
			BasicLogger.log(response, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
			return response;
		}
		PersoSimRemoteControlInterface port = service.getPersoSimRemoteControlPort();
		String response = port.reset();
		BasicLogger.log("Response: '" + response + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		return response;
	}


	public void usage()
	{
		String nameExePrefix = "PersoSimController";
		String nameExeSuffix = "";
		String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
		if (os.contains("win")) {
			nameExeSuffix = ".exe";
		}
		String nameExe = nameExePrefix + nameExeSuffix;
		BasicLogger.log("Usage:", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log(nameExe + " <start|stop|restart> <fullPathToExecutable>", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log("\tExample (Windows): " + nameExe + " start \"C:\\PersoSimDir\\PersoSim" + nameExeSuffix + "\"", LogLevel.INFO,
				new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log("\tExample (Linux): " + nameExePrefix + " stop \"/home/someuser/PersoSimDir/PersoSim\"", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log("or:", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log(nameExe + " loadperso <fullPathToPersoFile>", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log("or:", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log(nameExe + " sendapdu <apduAsHexString>", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log("or:", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log(nameExe + " reset", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log("or:", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		BasicLogger.log(nameExe + " help", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
	}

	public void doWork(String[] args)
	{
		if (args == null || args.length == 0) {
			usage();
			return;
		}

		String action = args[0].toLowerCase(Locale.ENGLISH);
		String paramsFromArgs = null;
		if (args.length > 1)
			paramsFromArgs = args[1];
		setParams(paramsFromArgs);

		try {
			switch (action) {
				case "start":
					if (args.length != 2) {
						usage();
						return;
					}
					if (!isProcessAvailable()) {
						startProcess();
					}
					else {
						BasicLogger.log("Process already running.", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
					}
					break;
				case "stop":
					if (args.length != 2) {
						usage();
						return;
					}
					if (isProcessAvailable()) {
						stopProcess();
					}
					else {
						BasicLogger.log("Process not running. Nothing to do.", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
					}
					break;
				case "restart":
					if (args.length != 2) {
						usage();
						return;
					}
					if (isProcessAvailable()) {
						restartProcess();
					}
					else {
						BasicLogger.log("Process not running yet. Starting it.", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
						startProcess();
					}
					break;
				case "loadperso":
					if (args.length != 2) {
						usage();
						return;
					}
					loadPerso(params);
					break;
				case "sendapdu":
					if (args.length != 2) {
						usage();
						return;
					}
					sendApdu(params);
					break;
				case "reset":
					if (args.length != 1) {
						usage();
						return;
					}
					reset();
					break;
				case "help":
					usage();
					break;
				default:
					BasicLogger.log("Invalid command: " + action, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
					usage();
			}
		}
		catch (Exception e) {
			BasicLogger.logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
	}

	public static void main(String[] args)
	{
		PersoSimController controller = new PersoSimController();
		controller.doWork(args);
	}
}
