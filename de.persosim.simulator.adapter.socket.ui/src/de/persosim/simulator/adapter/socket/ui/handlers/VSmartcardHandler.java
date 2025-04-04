package de.persosim.simulator.adapter.socket.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.driver.connector.ui.parts.ReaderPart;
import de.persosim.driver.connector.ui.parts.ReaderPart.ReaderType;
import de.persosim.simulator.adapter.socket.ui.Activator;
import jakarta.inject.Inject;

public class VSmartcardHandler {

	@Inject
	private EPartService partService;
	
	@Execute
	public void execute() {
		BasicLogger.log(this.getClass(), "Switch to use VSmartcard driver", LogLevel.INFO);

		// ID of part as defined in fragment.e4xmi application model
		MPart readerPart = partService.findPart("de.persosim.driver.connector.ui.parts.reader");

		
		if (readerPart.getObject() instanceof ReaderPart) {
			ReaderPart readerPartObject = (ReaderPart) readerPart.getObject();

			readerPartObject.switchReaderType(ReaderType.NONE);
		}
		
		Activator.startVsmartcard();
	}
}