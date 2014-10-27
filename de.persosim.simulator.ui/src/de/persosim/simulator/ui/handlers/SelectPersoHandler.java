package de.persosim.simulator.ui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import de.persosim.simulator.PersoSim;
import de.persosim.simulator.ui.parts.PersoSimGuiMain;

public abstract class SelectPersoHandler {
	
	@Inject
	protected EPartService partService;
	
	protected void loadPersonalization(String param) {
		System.out.println("Select Perso Handler called with param: " + param);
		
		String persoCmdString = PersoSim.CMD_LOAD_PERSONALIZATION + " " + param;
		
		System.out.println("executing command: " + persoCmdString);
		
		// ID of part as defined in fragment.e4xmi application model
		MPart mainPart = partService.findPart("de.persosim.simulator.ui.parts.mainPart");
		
		if (mainPart.getObject() instanceof PersoSimGuiMain) {
			((PersoSimGuiMain) mainPart.getObject()).write(persoCmdString);
		}
		
		System.out.println("finished setting of personalization");
	}
	
}
