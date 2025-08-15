package de.persosim.simulator.adapter.socket.ui.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.driver.connector.CommManager;
import de.persosim.driver.connector.ui.parts.ReaderPart;
import de.persosim.driver.connector.ui.parts.ReaderPart.ReaderType;
import jakarta.inject.Inject;

public class VSmartcardHandler
{
	public static final String NAME = "VSMARTCARD";
	private EPartService partService;

	@Inject
	public VSmartcardHandler(EPartService partService)
	{
		this.partService = partService;
	}

	@CanExecute
	public boolean checkState(final MItem mItem)
	{
		// ID of part as defined in fragment.e4xmi application model
		MPart readerPart = partService.findPart("de.persosim.driver.connector.ui.parts.reader");
		if (readerPart.getObject() instanceof ReaderPart mPart) {
			ReaderPart readerPartObject = mPart;
			mItem.setSelected(NAME.equals(readerPartObject.getCurrentCommType()));
		}
		return true;
	}

	@Execute
	public void execute()
	{
		BasicLogger.log(this.getClass(), "VSmartcard menu entry toggled", LogLevel.INFO);
		// ID of part as defined in fragment.e4xmi application model
		MPart readerPart = partService.findPart("de.persosim.driver.connector.ui.parts.reader");
		if (readerPart.getObject() instanceof ReaderPart mPart) {
			ReaderPart readerPartObject = mPart;
			readerPartObject.switchReaderType(ReaderType.EXTERNAL, CommManager.getCommForType(NAME));
		}
	}

}
