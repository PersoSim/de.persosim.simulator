package de.persosim.simulator.ui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.persosim.simulator.PersoSim;
import de.persosim.simulator.ui.parts.PersoSimGuiMain;

/**
 * This class implements the handler for the personalization select from file menu entry.
 * 
 * @author slutters
 *
 */
public class PersoOpenFileHandler {
	
	@Inject
	private EPartService partService;
	
	@Execute
	public void execute(Shell shell){
		FileDialog dialog = new FileDialog(shell);
		dialog.open();
		
		String fileName = dialog.getFileName();
		
		if(fileName.length() > 0) {
			String pathName = dialog.getFilterPath();
			
			String persoCmdString = PersoSim.CMD_LOAD_PERSONALIZATION + " " + pathName + "/" + fileName;
			
			System.out.println("Perso Open File Handler called with param: " + fileName);
			System.out.println("executing command: " + persoCmdString);
			
			// ID of part as defined in fragment.e4xmi application model
			MPart readerPart = partService.findPart("de.persosim.simulator.ui.parts.pinPad");
			
			if (readerPart.getObject() instanceof PersoSimGuiMain) {
				((PersoSimGuiMain) readerPart.getObject()).write(persoCmdString);
			}
			
			System.out.println("finished setting of personalization from file");
		}
		
	}
}
