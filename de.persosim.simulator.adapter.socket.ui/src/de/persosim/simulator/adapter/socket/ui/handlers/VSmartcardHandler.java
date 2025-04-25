package de.persosim.simulator.adapter.socket.ui.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.driver.connector.CommManager;
import de.persosim.driver.connector.ui.parts.ReaderPart;
import de.persosim.driver.connector.ui.parts.ReaderPart.ReaderType;

public class VSmartcardHandler {
	public static final String NAME = "VSMARTCARD";

	@CanExecute
	public boolean checkState(final MPart mPart, final MItem mItem) {
		if (mPart.getObject() instanceof ReaderPart) {
			ReaderPart readerPartObject = (ReaderPart) mPart.getObject();
			mItem.setSelected(readerPartObject.getCurrentCommType() == NAME);
		}
		return true;
	}
	
	@Execute
	public void execute(final MPart mpart, final MItem item) {
		BasicLogger.log(this.getClass(), "Switch to use VSmartcard driver", LogLevel.INFO);

		
		if (mpart.getObject() instanceof ReaderPart) {
			ReaderPart readerPartObject = (ReaderPart) mpart.getObject();

			readerPartObject.switchReaderType(ReaderType.EXTERNAL, CommManager.getCommForType(NAME));
		}
	}
}