package de.persosim.simulator.protocols;

import de.persosim.simulator.utils.Utils;

/**
 * This interface defines constants unique to the TR-03110 specification.
 * 
 * @author slutters
 *
 */
public interface Tr03110 {
	
//                                                              0x00 itu-t(0)
//                                                                    0x04 identified-organization(4)
//                                                                          0x00 etsi(0)
//                                                                                0x7F reserved(127)
//                                                                                      0x00 etsi-identified-organization(0)
//                                                                                            0x07 7
	public final static byte[] id_BSI                              = {0x04, 0x00, 0x7F, 0x00, 0x07};
	
	public final static byte[] id_PK                               = Utils.appendBytes(id_BSI, new byte[]{0x02, 0x02, 0x01});
	
	public final static byte ID_PIN = 3;
	public final static byte ID_CAN = 2;

}
