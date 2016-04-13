package de.persosim.simulator.ui.parts;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.logging.configui.LogLevelComposite;
import org.globaltester.logging.filter.LevelFilter;

public class LogLevelDialog extends Dialog {

	LevelFilter filter;
	LogLevelComposite llcomposite;

	public LogLevelDialog(Shell parentShell) {
		super(parentShell);
		filter = de.persosim.simulator.ui.Activator.getLogLevelFilter();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		llcomposite = new LogLevelComposite(parent, SWT.NONE, filter);
		return llcomposite;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Change log level");
	}

	@Override
	protected void okPressed() {
		llcomposite.applyLevelFilter();
		super.okPressed();
	}
}
