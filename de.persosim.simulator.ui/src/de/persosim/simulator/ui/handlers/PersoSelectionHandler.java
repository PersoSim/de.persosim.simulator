package de.persosim.simulator.ui.handlers;

import java.io.PrintWriter;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;

import de.persosim.simulator.PersoSim;
import de.persosim.simulator.ui.parts.PersoSimGuiMain;

/**
 * @author slutters
 *
 */
public class PersoSelectionHandler {
	
	public static final String persoPath = "personalization/templates/";
	public static final String persoFilePrefix = "template_";
	public static final String persoFilePostfix = ".xml";
	
	protected PrintWriter inWriter;
	
	protected PersoSimGuiMain persoSimGuiMain;
	
	@Execute
	public void execute(@Named("de.persosim.simulator.ui.commandparameter.persoSet") String param) {
		String persoFileString = persoPath + persoFilePrefix + param + persoFilePostfix;
		String persoCmdString = PersoSim.CMD_LOAD_PERSONALIZATION + " " + persoFileString;
		
		System.out.println("Perso Selction Handler called with param: " + param);
		System.out.println("executing command: " + persoCmdString);
		
		persoSimGuiMain = PersoSimGuiMain.getInstance();
		persoSimGuiMain.write(persoCmdString);
		
		System.out.println("completed setting of personalization template");
	}

}
