package de.persosim.simulator.ui.handlers;

import static org.globaltester.logging.BasicLogger.log;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.driver.connector.SimulatorManager;

public class RemoveCardHandler {
	
	@Inject
	protected EPartService partService;
	
	/**
	 * This method stops the simulator.
	 */
	@Execute
	public void execute(Shell shell){
		SimulatorManager.getSim().stopSimulator();
		
		log(this.getClass(), "finished stopping of simulator", LogLevel.INFO);
	}
	
}
