package de.persosim.simulator.ui.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import de.persosim.simulator.PersoSim;
import de.persosim.simulator.ui.parts.PersoSimGuiMain;

/**
 * This class implements the handler for the select personalization from template menu entries.
 * 
 * @author slutters
 *
 */
public class SelectPersoFromTemplateHandler {
	//FIXME duplicate code with SelectPersoFromFileHandler, these two differ only in file selection and should use common code for actual loading
	
	@Inject
	private EPartService partService;
	
	@Execute
	public void execute(@Named("de.persosim.simulator.ui.commandparameter.persoSet") String param) {
		String persoCmdString = PersoSim.CMD_LOAD_PERSONALIZATION + " " + param;
		
		System.out.println("Perso Selection Handler called with param: " + param);
		System.out.println("executing command: " + persoCmdString);
		
		// ID of part as defined in fragment.e4xmi application model
		MPart mainPart = partService.findPart("de.persosim.simulator.ui.parts.mainPart");
		
		if (mainPart.getObject() instanceof PersoSimGuiMain) {
			((PersoSimGuiMain) mainPart.getObject()).write(persoCmdString);
		}
		
		System.out.println("finished setting of personalization from template");
	}

}
