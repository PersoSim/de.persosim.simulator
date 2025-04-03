package de.persosim.simulator.adapter.socket.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SocketProtocol {
	
	boolean handleConnectionExchange(InputStream inputStream, OutputStream outputStream) throws IOException;
	
}
