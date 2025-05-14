package de.persosim.simulator.adapter.socket.ui.vsmartcard;

import java.util.List;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.driver.connector.IfdComm;
import de.persosim.driver.connector.pcsc.PcscListener;
import de.persosim.simulator.adapter.socket.ui.Activator;
import de.persosim.simulator.adapter.socket.ui.handlers.VSmartcardHandler;

public class VSmartcardComm implements IfdComm {
	@Override
	public void stop() {
		BasicLogger.log(getClass(), "VSmartcard Comm stop", LogLevel.TRACE);
		Activator.stopVsmartcard();
	}

	@Override
	public void start() {
		BasicLogger.log(getClass(), "VSmartcard Comm start", LogLevel.TRACE);
		Activator.startVsmartcard();
	}

	@Override
	public void setListeners(List<PcscListener> listeners) {
		// Do nothing, we do not actually simulate a PCSC reader
	}

	@Override
	public void reset() {
		BasicLogger.log(getClass(), "VSmartcard Comm reset", LogLevel.TRACE);
		Activator.stopVsmartcard();
		Activator.startVsmartcard();
	}

	@Override
	public boolean isRunning() {
		boolean isRunning = Activator.isVSmartcardRunning();
		BasicLogger.log(getClass(), "VSmartcard Comm is running: " + isRunning, LogLevel.TRACE);
		return isRunning;
	}

	@Override
	public String getUserString() {
		return "VSmartcard Adapter";
	}

	@Override
	public String getName() {
		return VSmartcardHandler.NAME;
	}
}
