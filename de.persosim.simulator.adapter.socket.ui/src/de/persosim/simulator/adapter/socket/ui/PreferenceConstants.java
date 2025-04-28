package de.persosim.simulator.adapter.socket.ui;

import de.persosim.simulator.adapter.socket.protocol.VSmartCardProtocol;

public interface PreferenceConstants {
	public static final String VSMARTCARD_PORT = "vsmartcard_port";
	public static final String VSMARTCARD_PORT_DEFAULT = VSmartCardProtocol.DEFAULT_PORT + "";
	public static final String VSMARTCARD_LAST_INTERFACE = "vsmartcard_last_interface";
}
