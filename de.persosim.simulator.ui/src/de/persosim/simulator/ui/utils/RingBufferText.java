package de.persosim.simulator.ui.utils;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class RingBufferText extends Text{
	
	protected int textLimit;
	
	public RingBufferText(Composite parent, int style) {
		super(parent, style);
		
		textLimit = 100;
	}
	
	public void ensureTextLimit() {
		String message = getMessage();
		
		while(message.length() > textLimit) {
			int index = message.indexOf(DELIMITER);
			if(index >= 0) {
				message = message.substring(index + 1);
			} else {
				message = message.substring(message.length() - textLimit);
			}
		}
		
		super.setText(message);
	}

	public int getTextLimit() {
		return textLimit;
	}

	public void setTextLimit(int textLimit) {
		if(textLimit < 0) {throw new IllegalArgumentException("text limit must be positive or 0");}
		
		this.textLimit = textLimit;
	}
	
	public void append(String string) {
		super.append(string);
		ensureTextLimit();
	}
	
	public void insert(String string) {
		super.insert(string);
		ensureTextLimit();
	}
	
	public void setText(String string) {
		super.setText(string);
		ensureTextLimit();
	}
	
	public void setTextChars(char[] text) {
		super.setTextChars(text);
		ensureTextLimit();
	}

}
