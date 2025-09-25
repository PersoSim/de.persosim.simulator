
package de.persosim.simulator.adapter.socket.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import de.persosim.simulator.adapter.socket.ui.parts.ConfigVSmartcardDialog;

public class ConfigureVSmartcardHandler
{
	@Execute
	public void execute(Shell shell)
	{
		// configuration dialog
		ConfigVSmartcardDialog dialog = new ConfigVSmartcardDialog(shell);
		dialog.open();
	}

}