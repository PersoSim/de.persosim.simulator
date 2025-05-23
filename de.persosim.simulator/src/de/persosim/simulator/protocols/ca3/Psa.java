package de.persosim.simulator.protocols.ca3;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;

/**
 * This interface provides constants used in the context of the PSA protocol according to TR-03110.
 */
public interface Psa extends Ps {
	
	public final static Oid id_PSA                                                 = new GenericOid(id_PS, new byte[] {0x01});
	public final static Oid id_PSA_ECDH_ECSchnorr                                  = new GenericOid(id_PSA, new byte[] {0x02});
	
	public final static String id_PSA_ECDH_ECSchnorr_SHA_256_STRING                = "id-PSA-ECDH-SHA-256";
	
	/* id-PSA-ECDH-ECSchnorr */
	public final static PsaOid id_PSA_ECDH_ECSchnorr_SHA_256                   = new PsaOid(id_PSA_ECDH_ECSchnorr, SHA_256);
	
	/* OidIdentifier PsaOid */
	public final static OidIdentifier OID_IDENTIFIER_id_PSA_ECDH_ECSchnorr_SHA_256 = new OidIdentifier(id_PSA_ECDH_ECSchnorr_SHA_256);
	
}
