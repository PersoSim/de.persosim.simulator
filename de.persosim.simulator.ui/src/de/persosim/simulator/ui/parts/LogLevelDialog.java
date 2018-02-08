package de.persosim.simulator.ui.parts;

import java.util.StringJoiner;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.preferences.PersoSimPreferenceManager;
import de.persosim.simulator.ui.Activator;

public class LogLevelDialog extends Dialog {

	Composite llcomposite;
	private Button[] btnLogLevels = new Button [LogLevel.values().length];

	public LogLevelDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		llcomposite = new Composite(parent, SWT.NONE);
		
		llcomposite.setLayout((new GridLayout(1, true)));
		
		Label lblInfo = new Label(llcomposite, SWT.NONE);
		lblInfo.setText("Select the log levels to view:");
		
		String levels = PersoSimPreferenceManager.getPreference("LOG_LEVELS");
		
		for (int i = 0; i < LogLevel.values().length; i++) {
			btnLogLevels[i] = new Button(llcomposite, SWT.CHECK);
			LogLevel logLevel = LogLevel.values()[i];
			btnLogLevels[i].setText(logLevel.toString());
			if (levels != null && levels.contains(logLevel.name()) || levels == null) {
				btnLogLevels[i].setSelection(true);
			}
		}
		
		parent.layout();
		parent.redraw();
		
		return llcomposite;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Change log level");
	}

	@Override
	protected void okPressed() {
		
		StringJoiner joiner = new StringJoiner(":");
		for (int i = 0; i < LogLevel.values().length; i++) {
			LogLevel current = LogLevel.values()[i];
			if (btnLogLevels[i].getSelection()) {
				joiner.add(current.name());
			}
		}
		
		PersoSimPreferenceManager.storePreference("LOG_LEVELS", joiner.toString());
		
		Activator.getListLogListener().updateConfig();
		
		super.okPressed();
	}
}
