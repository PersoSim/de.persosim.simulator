package de.persosim.simulator.crypto;

import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.utils.HexString;

/**
 * This interface defines constants unique to the TR-03111 specification.
 *
 */
public interface Tr03111 {
	
//                                                              0x00 itu-t(0)
//                                                                    0x04 identified-organization(4)
//                                                                          0x00 etsi(0)
//                                                                                0x7F reserved(127)
//                                                                                      0x00 etsi-identified-organization(0)
//                                                                                            0x07 7
	public static final Oid id_BSI                              = new GenericOid(HexString.toByteArray("04 00 7F 00 07"));
	
	public static final Oid id_ecc                               = new GenericOid(id_BSI, HexString.toByteArray("0101"));

	public static final Oid id_ecdsa_plain_signatures  = new GenericOid( id_ecc,                    HexString.toByteArray("0401"));
	public static final Oid id_ecdsa_plain_SHA1        = new GenericOid( id_ecdsa_plain_signatures, HexString.toByteArray("01"));
	public static final Oid id_ecdsa_plain_SHA224      = new GenericOid( id_ecdsa_plain_signatures, HexString.toByteArray("02"));
	public static final Oid id_ecdsa_plain_SHA256      = new GenericOid( id_ecdsa_plain_signatures, HexString.toByteArray("03"));
	public static final Oid id_ecdsa_plain_SHA384      = new GenericOid( id_ecdsa_plain_signatures, HexString.toByteArray("04"));
	public static final Oid id_ecdsa_plain_SHA512      = new GenericOid( id_ecdsa_plain_signatures, HexString.toByteArray("05"));
	
//	ecdsa-plain-RIPEMD160   OBJECT IDENTIFIER ::= { ecdsa-plain-signatures 6 }
//	ecdsa-plain-SHA3-224    OBJECT IDENTIFIER ::= { ecdsa-plain-signatures 8 }
//	ecdsa-plain-SHA3-256    OBJECT IDENTIFIER ::= { ecdsa-plain-signatures 9 }
//	ecdsa-plain-SHA3-384    OBJECT IDENTIFIER ::= { ecdsa-plain-signatures 10 }
//	ecdsa-plain-SHA3-512    OBJECT IDENTIFIER ::= { ecdsa-plain-signatures 11 }
	

}
