package de.persosim.simulator.adapter.socket.ui.handlers;

import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.driver.connector.IfdComm;
import de.persosim.driver.connector.pcsc.PcscListener;
import de.persosim.driver.connector.ui.parts.ReaderPart;
import de.persosim.driver.connector.ui.parts.ReaderPart.ReaderType;
import de.persosim.simulator.adapter.socket.ui.Activator;

public class VSmartcardHandler {
	private static final String NAME = "VSMARTCARD";

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

			readerPartObject.switchReaderType(ReaderType.EXTERNAL, new IfdComm() {
				
				@Override
				public void stop() {
					Activator.stopVsmartcard();
				}
				
				@Override
				public void start() {
					Activator.startVsmartcard();
				}
				
				@Override
				public void setListeners(List<PcscListener> listeners) {
					// Do nothing, we do not actually simulate a PCSC reader
				}
				
				@Override
				public void reset() {
					Activator.stopVsmartcard();
					Activator.startVsmartcard();
				}
				
				@Override
				public boolean isRunning() {
					return Activator.isVSmartcardRunning();
				}
				
				@Override
				public String getUserString() {
					return "VSmartcard Adapter";
				}
				
				@Override
				public String getName() {
					return NAME;
				}
			});
		}
	}
}