package de.persosim.simulator.ui.utils;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public class TextLengthLimiter implements ModifyListener {

	@Override
	public void modifyText(ModifyEvent e) {
		if(e.getSource() instanceof Text) {
			Text text = (Text) e.getSource();
			int textLimit = 1000;
			String lineDelimiter = text.getLineDelimiter();
			
			String message = text.getMessage();
			
			if(message.length() > textLimit) {
				do {
					int index = message.indexOf(lineDelimiter);
					if(index >= 0) {
						message = message.substring(index + 1);
					} else {
						message = message.substring(message.length() - textLimit);
					}
				} while(message.length() > textLimit);
				
				text.setText(message);
			}
		}
		
	}

}
