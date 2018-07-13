package de.persosim.simulator.protocols;

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
	public static final Oid id_BSI                              = new GenericOid(new byte[]{0x04, 0x00, 0x7F, 0x00, 0x07});
	
	public static final Oid id_PK                               = new GenericOid(id_BSI, new byte[]{0x02, 0x02, 0x01});

	
	public static final Oid id_CI                               = new GenericOid(id_BSI, new byte[]{0x02, 0x02, 0x06});
	public static final Oid id_eIDSecurity                      = new GenericOid(id_BSI, new byte[]{0x02, 0x02, 0x07});
	public static final Oid id_PT                               = new GenericOid(id_BSI, new byte[]{0x02, 0x02, 0x08});
	
	public static final byte ID_MRZ = 1;
	public static final byte ID_CAN = 2;
	public static final byte ID_PIN = 3;
	public static final byte ID_PUK = 4;
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	/* Symmetric cipher */
	public static final byte DES3_CBC_CBC                          = (byte) 0x01;
	public static final byte AES_CBC_CMAC_128                      = (byte) 0x02;
	public static final byte AES_CBC_CMAC_192                      = (byte) 0x03;
	public static final byte AES_CBC_CMAC_256                      = (byte) 0x04;
	
	public static final byte[] SYMMETRIC_CIPHER                    = new byte[]{DES3_CBC_CBC, AES_CBC_CMAC_128, AES_CBC_CMAC_192, AES_CBC_CMAC_256};
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * This constant indicates that the associated session is volatile,
	 * i.e. does neither provide an explicit nor implicit identifier and
	 * hence must not be stored on switching session contexts. 
	 */
	public static final int CONTEXT_SESSION_ID_FOR_VOLATILE_SESSIONS = -1;

}
