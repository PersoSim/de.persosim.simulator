package de.persosim.simulator.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
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
	
	protected PersoSimGuiMain persoSimGuiMain;
	
	@Execute
	public void execute(Shell shell){
		FileDialog dialog = new FileDialog(shell);
		dialog.open();
		
		String fileName = dialog.getFileName();
		String pathName = dialog.getFilterPath();
		
		String persoCmdString = PersoSim.CMD_LOAD_PERSONALIZATION + " " + pathName + "/" + fileName;
		
		System.out.println("Perso Open File Handler called with param: " + fileName);
		System.out.println("executing command: " + persoCmdString);
		
		persoSimGuiMain = PersoSimGuiMain.getInstance();
		persoSimGuiMain.write(persoCmdString);
		
		System.out.println("finished setting of personalization from file");
	}
}
