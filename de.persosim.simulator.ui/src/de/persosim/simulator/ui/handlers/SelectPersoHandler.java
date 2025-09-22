package de.persosim.simulator.ui.handlers;

import static org.globaltester.logging.BasicLogger.log;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.driver.connector.SimulatorManager;
import de.persosim.simulator.CommandParser;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.ui.Activator;

/**
 * This class implements an abstract super class for any handler handling the
 * loading of personalizations providing shared code for the common goal.
 *
 * @author slutters
 *
 */
public abstract class SelectPersoHandler
{
	/**
	 * This method sends the actual load command for a provided personalization
	 * path or Id to the simulator GUI.
	 *
	 * @param personalization
	 *            the path (including file name) of an arbitrary personalization or
	 *            the Id of a default personalization.
	 * @param withOverlayProfile
	 *            Handle overlay profile files or not
	 */
	protected void loadPersonalization(String personalization, boolean withOverlayProfile)
	{
		log("Select Perso Handler called with param: " + personalization, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		String persoCmdString = CommandParser.CMD_LOAD_PERSONALIZATION + " " + personalization;

		log("Executing command: " + persoCmdString, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		if (!SimulatorManager.getSim().isRunning()) {
			SimulatorManager.getSim().startSimulator();
		}

		Activator.executeUserCommands(persoCmdString, withOverlayProfile);

		log("Finished setting of personalization", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
	}

}
