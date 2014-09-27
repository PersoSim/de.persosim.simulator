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
	
	public final static byte[] id_PT                               = Utils.appendBytes(id_BSI, new byte[]{0x02, 0x02, 0x08});
	
	public final static byte ID_MRZ = 1;
	public final static byte ID_CAN = 2;
	public final static byte ID_PIN = 3;
	public final static byte ID_PUK = 4;
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	/* Symmetric cipher */
	public final static byte DES3_CBC_CBC                          = (byte) 0x01;
	public final static byte AES_CBC_CMAC_128                      = (byte) 0x02;
	public final static byte AES_CBC_CMAC_192                      = (byte) 0x03;
	public final static byte AES_CBC_CMAC_256                      = (byte) 0x04;
	
	public final static byte[] SYMMETRIC_CIPHER                    = new byte[]{DES3_CBC_CBC, AES_CBC_CMAC_128, AES_CBC_CMAC_192, AES_CBC_CMAC_256};
	
	/*--------------------------------------------------------------------------------*/

}
