package de.persosim.simulator.ui.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;

/**
 * This class implements the handler for the select personalization from template menu entries.
 * 
 * @author slutters
 *
 */
public class SelectPersoFromTemplateHandler extends SelectPersoHandler {
	@Execute
	public void execute(@Named("de.persosim.simulator.ui.commandparameter.persoSet") String param) {
		loadPersonalization(param);
	}

}
