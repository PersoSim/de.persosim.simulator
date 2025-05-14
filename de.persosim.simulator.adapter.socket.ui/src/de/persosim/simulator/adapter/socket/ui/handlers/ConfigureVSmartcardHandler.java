 
package de.persosim.simulator.adapter.socket.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import de.persosim.simulator.adapter.socket.ui.parts.ConfigVSmartcardDialog;
import jakarta.inject.Inject;

public class ConfigureVSmartcardHandler {

	@Inject
	private EPartService partService;
	
	@Execute
	public void execute(Shell shell) {
		// ID of part as defined in fragment.e4xmi application model
		MPart readerPart = partService.findPart("de.persosim.driver.connector.ui.parts.reader");

		//configuration dialog
		ConfigVSmartcardDialog dialog = new ConfigVSmartcardDialog(shell, readerPart);
		dialog.open();
	}
		
}