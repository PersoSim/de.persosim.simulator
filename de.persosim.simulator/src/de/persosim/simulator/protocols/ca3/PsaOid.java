package de.persosim.simulator.protocols.ca3;

import java.util.Arrays;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class implements functionalities for OIDs used in the {@link PsaProtocol}.
 * 
 * @author jgoeke
 * 
 */
public class PsaOid extends PsOid implements Psa {
		
	/**
	 * This constructor constructs a {@link PsaOid} based on a byte array representation of a PSA OID.
	 * @param oidByteArray the byte array representation of a PSA OID
	 */
	public PsaOid(byte[] byteArrayRepresentation) {
		super(byteArrayRepresentation);
		
		//check if provided OID is indeed PSA OID
		if (!startsWithPrefix(id_PSA)) {
			throw new IllegalArgumentException("PSA OID " + HexString.encode(oidByteArray) + " is invalid or unknown (not supported)");
		}
	}
	
	public PsaOid(Oid prefix, byte... suffix) {
		this(Utils.concatByteArrays(prefix.toByteArray(), suffix));
	}

	/**
	 * @see Oid#getIdString()
	 * @return common name of OID or null if parameter does not encode a PsaOid
	 */
	public static String getStringRepresentation(byte[] oidByteArray) {
		if (Arrays.equals(oidByteArray, id_PSA_ECDH_ECSchnorr_SHA_256.toByteArray())) return id_PSA_ECDH_ECSchnorr_SHA_256_STRING;
		
		return null;
	}
	
}
