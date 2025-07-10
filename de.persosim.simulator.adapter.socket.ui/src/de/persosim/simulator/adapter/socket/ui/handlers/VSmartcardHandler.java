package de.persosim.simulator.adapter.socket.ui.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.driver.connector.CommManager;
import de.persosim.driver.connector.ui.parts.ReaderPart;
import de.persosim.driver.connector.ui.parts.ReaderPart.ReaderType;
import de.persosim.simulator.log.PersoSimLogTags;

public class VSmartcardHandler
{
	public static final String NAME = "VSMARTCARD";

	@CanExecute
	public boolean checkState(final MPart mPart, final MItem mItem)
	{
		if (mPart.getObject() instanceof ReaderPart readerPartObject) {
			readerPartObject = (ReaderPart) mPart.getObject();
			mItem.setSelected(NAME.equals(readerPartObject.getCurrentCommType()));
		}
		return true;
	}

	@Execute
	public void execute(final MPart mPart, final MItem mItem)
	{
		if (mItem.isSelected())
			BasicLogger.log("VSmartcard interface selected", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));

		if (mPart.getObject() instanceof ReaderPart readerPartObject) {
			readerPartObject = (ReaderPart) mPart.getObject();
			readerPartObject.switchReaderType(ReaderType.EXTERNAL, CommManager.getCommForType(NAME));
		}
	}
}