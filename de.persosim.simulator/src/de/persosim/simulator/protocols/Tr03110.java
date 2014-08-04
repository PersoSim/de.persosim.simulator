package de.persosim.simulator.protocols;

import de.persosim.simulator.utils.Utils;

/**
 * This interface defines constants unique to the TR-03110 specification.
 * 
 * @author slutters
 *
 */
public interface Tr03110 {
	
	public final static byte[] id_BSI                              = {0x04, 0x00, 0x7F, 0x00, 0x07};
	public final static byte[] id_PK                               = Utils.appendBytes(id_BSI, new byte[]{0x02, 0x02, 0x01});
	
	public final static int ID_PIN = 3;
	public final static int ID_CAN = 2;

}
