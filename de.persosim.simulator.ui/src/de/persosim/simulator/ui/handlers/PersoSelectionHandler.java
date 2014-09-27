package de.persosim.simulator.ui.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;

/**
 * @author slutters
 *
 */
public class PersoSelectionHandler {
	
	@Execute
	public void execute(@Named("de.persosim.simulator.ui.commandparameter.persoSet") String param) {
		
		System.out.println("Perso Selction Handler called with param: " + param);
		
	}

}
