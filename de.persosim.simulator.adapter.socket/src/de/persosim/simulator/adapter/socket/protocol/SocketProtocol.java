package de.persosim.simulator.adapter.socket.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SocketProtocol {
	
	/**
	 * Handle an established connection.
	 * 
	 * @param inputStream
	 * @param outputStream
	 * @return true, iff the connection should continue
	 * @throws IOException
	 */
	boolean handleConnectionExchange(InputStream inputStream, OutputStream outputStream) throws IOException;
	
}
