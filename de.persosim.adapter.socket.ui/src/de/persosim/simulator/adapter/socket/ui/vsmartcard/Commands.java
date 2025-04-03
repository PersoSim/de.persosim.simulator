package de.persosim.simulator.adapter.socket.ui.vsmartcard;

public enum Commands {
	POWER_OFF((byte) 0), POWER_ON((byte) 1), RESET((byte) 2);

	private byte value;
	
	private Commands(byte i) {
		value = i;
	}
	
	public byte [] getCommand() {
		return new byte [] { value };
	}
}
