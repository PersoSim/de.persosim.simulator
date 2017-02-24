package de.persosim.simulator.protocols.ca;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.Utils;

public class CaSecInfoHelper implements Ca, TlvConstants {
	
	/**
	 * This method constructs a ChipAuthenticationInfo object
	 * _with_ optional key reference
	 * 
	 * ChipAuthenticationInfo ::= SEQUENCE {
     *   protocol OBJECT IDENTIFIER(
     *            id-CA-DH-3DES-CBC-CBC | 
     *            id-CA-DH-AES-CBC-CMAC-128 | 
     *            id-CA-DH-AES-CBC-CMAC-192 | 
     *            id-CA-DH-AES-CBC-CMAC-256 | 
     *            id-CA-ECDH-3DES-CBC-CBC |           
     *            id-CA-ECDH-AES-CBC-CMAC-128 | 
     *            id-CA-ECDH-AES-CBC-CMAC-192 | 
     *            id-CA-ECDH-AES-CBC-CMAC-256),
     *   version  INTEGER, -- MUST be 1 for CAv1 or 2 for CAv2 or 3 for CAv3
     *   keyId    INTEGER OPTIONAL (present)
     * }
     *
	 * @param oidBytes the OID to use
	 * @param version the protocol version to use
	 * @param keyId the key ID to use
	 * @return the constructed ChipAuthenticationInfo object
	 */
	public static ConstructedTlvDataObject constructChipAuthenticationInfoObject(byte[] oidBytes, byte version, int keyId) {
		ConstructedTlvDataObject caInfo = constructChipAuthenticationInfoObject(oidBytes, version);
		caInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, Utils.toShortestUnsignedByteArray(keyId)));
		return caInfo;
	}
	
	
	/**
	 * This method constructs a ChipAuthenticationInfo object
	 * _without_ optional key reference
	 * 
	 * ChipAuthenticationInfo ::= SEQUENCE {
     *   protocol OBJECT IDENTIFIER(
     *            id-CA-DH-3DES-CBC-CBC | 
     *            id-CA-DH-AES-CBC-CMAC-128 | 
     *            id-CA-DH-AES-CBC-CMAC-192 | 
     *            id-CA-DH-AES-CBC-CMAC-256 | 
     *            id-CA-ECDH-3DES-CBC-CBC |           
     *            id-CA-ECDH-AES-CBC-CMAC-128 | 
     *            id-CA-ECDH-AES-CBC-CMAC-192 | 
     *            id-CA-ECDH-AES-CBC-CMAC-256),
     *   version  INTEGER, -- MUST be 1 for CAv1 or 2 for CAv2 or 3 for CAv3
     *   keyId    INTEGER OPTIONAL (absent)
     * }
     *
	 * @param oidBytes the OID to use
	 * @return the constructed ChipAuthenticationInfo object
	 */
	public static ConstructedTlvDataObject constructChipAuthenticationInfoObject(byte[] oidBytes, byte version) {
		ConstructedTlvDataObject caInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
		caInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, oidBytes));
		caInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{version}));
		return caInfo;
	}
	
}
