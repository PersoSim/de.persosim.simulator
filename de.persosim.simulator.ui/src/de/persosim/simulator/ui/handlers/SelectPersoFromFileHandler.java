package de.persosim.simulator.ui.handlers;

import java.io.File;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * This class implements the handler for the select personalization from file menu entry.
 *
 * @author slutters
 *
 */
public class SelectPersoFromFileHandler extends SelectPersoHandler
{
	@Execute
	public void execute(Shell shell)
	{
		FileDialog dialog = new FileDialog(shell);
		dialog.open();

		String fileName = dialog.getFileName();

		if (fileName.length() > 0) {
			String pathName = dialog.getFilterPath() + File.separator + fileName;
			loadPersonalization(pathName, false);
		}
	}
}
