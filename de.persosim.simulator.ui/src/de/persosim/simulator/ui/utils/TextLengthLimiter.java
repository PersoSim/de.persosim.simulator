package de.persosim.simulator.ui.utils;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public class TextLengthLimiter implements ModifyListener {
	
	public static final int TEXT_LIMIT_DEFAULT = 2000;
	public static int counter = 0;
	
	protected int textLimit;
	public boolean lock;
	
	public TextLengthLimiter() {
		textLimit = TEXT_LIMIT_DEFAULT;
		lock = false;
		counter++;
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
		if((!lock) && (e.getSource() instanceof Text)) {
			Text text = (Text) e.getSource();
			String lineDelimiter = text.getLineDelimiter();
			
			String content = text.getText();
			
			int contentLength = content.length();
			if(contentLength > textLimit) {
				lock = true;
				
				do {
					int index = content.indexOf(lineDelimiter);
					if(index >= 0) {
						content = content.substring(index + 1);
					} else {
						content = content.substring(content.length() - textLimit);
					}
					
					contentLength = content.length();
				} while(contentLength > textLimit);
				
				text.setText(content);
				System.out.println("!!! text shortened !!!");
				
				lock = false;
			}
		}
		
	}

	public int getTextLimit() {
		return textLimit;
	}

	public void setTextLimit(int textLimit) {
		this.textLimit = textLimit;
	}

}
