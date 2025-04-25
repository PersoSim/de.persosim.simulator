package de.persosim.simulator.adapter.socket.ui.vsmartcard;

import java.util.List;

import de.persosim.driver.connector.IfdComm;
import de.persosim.driver.connector.pcsc.PcscListener;
import de.persosim.simulator.adapter.socket.ui.Activator;
import de.persosim.simulator.adapter.socket.ui.handlers.VSmartcardHandler;

public class VSmartcardComm implements IfdComm {
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
		return VSmartcardHandler.NAME;
	}
}
