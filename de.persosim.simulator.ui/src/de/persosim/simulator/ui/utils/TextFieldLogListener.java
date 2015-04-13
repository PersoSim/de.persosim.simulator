package de.persosim.simulator.ui.utils;

import org.eclipse.swt.widgets.Text;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

public class TextFieldLogListener implements LogListener {

	Text text;
	
	public void setText(Text text){
		this.text = text;
	}
	
	@Override
	public void logged(LogEntry entry) {
		if (text != null){
			text.append("\n[" + entry.getBundle().getSymbolicName() + "] " + entry.getMessage());	
		}
	}

}
