package de.persosim.simulator.ui.handlers;

import static org.globaltester.logging.BasicLogger.log;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.driver.connector.SimulatorManager;
import de.persosim.simulator.log.PersoSimLogTags;

public class RemoveCardHandler
{
	/**
	 * This method stops the simulator.
	 */
	@Execute
	public void execute(Shell shell)
	{
		SimulatorManager.getSim().stopSimulator();

		log("Finished stopping of simulator", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
	}

}
