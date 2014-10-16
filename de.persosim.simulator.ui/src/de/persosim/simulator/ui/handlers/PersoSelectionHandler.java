package de.persosim.simulator.ui.handlers;

import java.io.PrintWriter;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;

import de.persosim.simulator.PersoSim;
import de.persosim.simulator.ui.parts.PersoSimGuiMain;

/**
 * This class implements the handler for the personalization select from template menu entries.
 * 
 * @author slutters
 *
 */
public class PersoSelectionHandler {
	
	public static final String persoPath = "personalization/profiles/";
	public static final String persoFilePrefix = "Profile";
	public static final String persoFilePostfix = ".xml";
	
	protected PrintWriter inWriter;
	
	protected PersoSimGuiMain persoSimGuiMain;
	
	@Execute
	public void execute(@Named("de.persosim.simulator.ui.commandparameter.persoSet") String param) {
		String persoCmdString = PersoSim.CMD_LOAD_PERSONALIZATION + " " + param;
		
		System.out.println("Perso Selection Handler called with param: " + param);
		System.out.println("executing command: " + persoCmdString);
		
		persoSimGuiMain = PersoSimGuiMain.getInstance();
		persoSimGuiMain.write(persoCmdString);
		
		System.out.println("finished setting of personalization from template");
	}

}
