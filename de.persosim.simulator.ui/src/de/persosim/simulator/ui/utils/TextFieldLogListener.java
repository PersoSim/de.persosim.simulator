package de.persosim.simulator.ui.utils;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

public class TextFieldLogListener implements LogListener {

	Text text;
	UISynchronize sync;
	
	public void setText(Text text, UISynchronize sync){
		this.text = text;
		this.sync = sync;
	}
	
	@Override
	public void logged(final LogEntry entry) {
		if (text != null){
			sync.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					text.append("\n[" + entry.getBundle().getSymbolicName() + "] " + entry.getMessage());
				}
			});
				
		}
	}

}
