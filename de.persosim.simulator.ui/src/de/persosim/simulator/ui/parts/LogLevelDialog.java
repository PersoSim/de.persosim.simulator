package de.persosim.simulator.ui.parts;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.logging.filter.LevelFilter;

public class LogLevelDialog extends Dialog {

	LevelFilter filter;
	Button[] btnLogLevels = new Button[7];

	public LogLevelDialog(Shell parentShell) {
		super(parentShell);
		filter = de.persosim.simulator.ui.Activator.getLogLevelFilter();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout((new GridLayout(1, true)));
		
		Label lblInfo = new Label(container, SWT.NONE);
		lblInfo.setText("Select the log levels to view:");

		/*
		 * Existing Log Levels can be found in org.globaltester.logging.BasicLogger
		 * 
		 * Reminder: TRACE = 1 DEBUG = 2 INFO = 3 WARN = 4
		 * ERROR = 5 FATAL = 6 UI = 120
		 */
		
		for (int i = 0; i < btnLogLevels.length; i++) {
			btnLogLevels[i] = new Button(container, SWT.CHECK);
			
			switch (i) {
				case 0: btnLogLevels[i].setText("TRACE"); break;
				case 1: btnLogLevels[i].setText("DEBUG"); break;
				case 2: btnLogLevels[i].setText("INFO"); break;
				case 3: btnLogLevels[i].setText("WARN"); break;
				case 4: btnLogLevels[i].setText("ERROR"); break;
				case 5: btnLogLevels[i].setText("FATAL"); break;
				case 6: btnLogLevels[i].setText("UI"); break;
				default: break;
			}
			
		}
		
		setPreSelections();
		return container;
	}
	
	public void setPreSelections(){
		
		//get them. Necessary for preselect check boxes
		byte [] checker = filter.getLogLevels();
		
		//set the selection for every wished log level
		for(int i=0; i<checker.length;i++){
			
			switch(checker[i]){
				case 1: btnLogLevels[0].setSelection(true); break;
				case 2: btnLogLevels[1].setSelection(true); break;
				case 3: btnLogLevels[2].setSelection(true); break;
				case 4: btnLogLevels[3].setSelection(true); break;
				case 5: btnLogLevels[4].setSelection(true); break;
				case 6: btnLogLevels[5].setSelection(true); break;
				case 120: btnLogLevels[6].setSelection(true); break;
				default: break;
			}
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Change log level");
	}

	@Override
	protected void okPressed() {
		// apply and change the new logging settings

		int countSelectedLogLevels = 0;
		for (int i = 0; i < btnLogLevels.length; i++) {
			if (btnLogLevels[i].getSelection()) {
				countSelectedLogLevels++;
			}
		}

		byte[] logLevels = new byte[countSelectedLogLevels];

		for (int i = 0, j=0; i < btnLogLevels.length; i++) {
			if (btnLogLevels[i].getSelection()) {
				
				if(i<6){
					logLevels[j] = (byte) (i+1);
				}else if (i==6){
					logLevels[j] = 120;
				}
				j++;
			}
		}

		filter.setLogLevels(logLevels);

		super.okPressed();
	}
}
