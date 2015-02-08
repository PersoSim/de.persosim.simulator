package de.persosim.simulator.ui.utils;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class TextLengthLimiter implements ModifyListener {
	//FIXME JKH is this class still needed?
	
	public static final int TEXT_LIMIT_DEFAULT = 3000; //FIXME JKH please document or rename this value! What unit is used here?
	
	protected int textLimit;
	protected boolean lock;
	
	public TextLengthLimiter() {
		textLimit = TEXT_LIMIT_DEFAULT;
		lock = false;
	}
	
	@Override
	/*
	 * WARNING: do not add "System.out.print*" calls to this method. The output
	 * will cause an implicit but delayed recursive call of this method
	 * bypassing the lock originally intended to mitigate this problem.
	 * 
	 * (non-Javadoc) @see
	 * org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events
	 * .ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		if((!lock) && (e.getSource() instanceof Text)) {
			
			final Text text = (Text) e.getSource();
			String lineDelimiter = text.getLineDelimiter();
			
			String content = text.getText();
			
			int contentLength = content.length();
			if(contentLength > textLimit) {
				lock = true;
				
				do {
					int index = content.indexOf(lineDelimiter);
					if(index >= 0) {
						content = content.substring(index + 1); // if there is more than one line, delete oldest line
					} else {
						content = content.substring(content.length() - textLimit); // if there is only one line, crop beginning
					}
					
					contentLength = content.length();
				} while(contentLength > textLimit);
				
				final String toWrite = content;
				
				Display.getCurrent().asyncExec(new Runnable(){

					@Override
					public void run() {
						text.setText(toWrite);
						
					}
				});
				
			}
		}
		lock = false;
		
	}

	public int getTextLimit() {
		return textLimit;
	}

	public void setTextLimit(int textLimit) {
		this.textLimit = textLimit;
	}

}
