package de.persosim.simulator.ui.handlers;

import static org.globaltester.logging.BasicLogger.log;

import jakarta.inject.Inject;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.driver.connector.SimulatorManager;
import de.persosim.simulator.CommandParser;
import de.persosim.simulator.ui.Activator;

/**
 * This class implements an abstract super class for any handler handling the
 * loading of personalizations providing shared code for the common goal.
 *
 * @author slutters
 *
 */
public abstract class SelectPersoHandler {

	@Inject
	protected EPartService partService;

	/**
	 * This method sends the actual load command for a provided personalization
	 * path or Id to the simulator GUI.
	 *
	 * @param personalization
	 *            the path (incl. file name) of an arbitrary personalization or
	 *            the Id of a default personalization.
	 * @param withOverlayProfile Handle overlay profile files or not
	 */
	protected void loadPersonalization(String personalization, boolean withOverlayProfile) {
		log(this.getClass(), "Select Perso Handler called with param: " + personalization);

		String persoCmdString = CommandParser.CMD_LOAD_PERSONALIZATION + " " + personalization;

		log(this.getClass(), "executing command: " + persoCmdString);

		if (!SimulatorManager.getSim().isRunning()){
			SimulatorManager.getSim().startSimulator();
		}

		Activator.executeUserCommands(persoCmdString, withOverlayProfile);

		log(this.getClass(), "finished setting of personalization", LogLevel.INFO);
	}

}
