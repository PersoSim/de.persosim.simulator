package de.persosim.simulator.ui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import de.persosim.simulator.PersoSim;
import de.persosim.simulator.ui.parts.PersoSimGuiMain;

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
	 */
	protected void loadPersonalization(String personalization) {
		System.out.println("Select Perso Handler called with param: " + personalization);
		
		String persoCmdString = PersoSim.CMD_LOAD_PERSONALIZATION + " " + personalization;
		
		System.out.println("executing command: " + persoCmdString);
		
		// ID of part as defined in fragment.e4xmi application model
		MPart mainPart = partService.findPart("de.persosim.simulator.ui.parts.mainPart");
		
		if (mainPart.getObject() instanceof PersoSimGuiMain) {
			((PersoSimGuiMain) mainPart.getObject()).write(persoCmdString);
		}
		
		System.out.println("finished setting of personalization");
	}
	
}
