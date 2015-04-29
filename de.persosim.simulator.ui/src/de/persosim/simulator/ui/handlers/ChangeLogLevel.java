 
package de.persosim.simulator.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

import de.persosim.simulator.ui.parts.LogLevelDialog;

public class ChangeLogLevel {
	@Execute
	public void execute() {

		LogLevelDialog ld = new LogLevelDialog(null);
		ld.open();
	
	}
		
}