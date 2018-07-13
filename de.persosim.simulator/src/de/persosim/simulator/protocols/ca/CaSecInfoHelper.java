package de.persosim.simulator.protocols.ca;

import java.security.PublicKey;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.Utils;

public final class CaSecInfoHelper implements Ca, TlvConstants {
	
	private CaSecInfoHelper() {
		// this static class is not to be instantiated
	}
	
	/**
	 * This method constructs a ChipAuthenticationInfo element
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
	 * @param oid the OID to use
	 * @param version the protocol version to use
	 * @param keyId the key ID to use
	 * @return the constructed ChipAuthenticationInfo element
	 */
	public static ConstructedTlvDataObject constructChipAuthenticationInfoObject(Oid oid, byte version, int keyId) {
		ConstructedTlvDataObject caInfo = constructChipAuthenticationInfoObject(oid, version);
		caInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, Utils.toShortestUnsignedByteArray(keyId)));
		return caInfo;
	}
	
	
	/**
	 * This method constructs a ChipAuthenticationInfo element
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
	 * @param oid the OID to use
	 * @return the constructed ChipAuthenticationInfo element
	 */
	public static ConstructedTlvDataObject constructChipAuthenticationInfoObject(Oid oid, byte version) {
		ConstructedTlvDataObject caInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
		caInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, oid.toByteArray()));
		caInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{version}));
		return caInfo;
	}
	
	/**
	 * This method constructs an AlgorithmIdentifier element
	 * 
	 * AlgorithmIdentifier ::= SEQUENCE {
	 *   algorithm  OBJECT IDENTIFIER,
	 *   parameters ANY DEFINED BY algorithm OPTIONAL
	 * }
	 * 
	 * @param publicKey the public key as data source
	 * @return the constructed AlgorithmIdentifier
	 */
	public static ConstructedTlvDataObject constructAlgorithmIdentifier(PublicKey publicKey) {
		ConstructedTlvDataObject encKey = new ConstructedTlvDataObject(publicKey.getEncoded());
		return (ConstructedTlvDataObject) encKey.getTlvDataObject(TAG_SEQUENCE);
	}
	
	/**
	 * This method constructs a subjectPublicKey element (BIT STRING)
	 * 
	 * BIT STRING
	 * 
	 * @param publicKey the public key as data source
	 * @return the constructed subjectPublicKey element (BIT STRING)
	 */
	public static PrimitiveTlvDataObject constructSubjectPublicKey(PublicKey publicKey) {
		ConstructedTlvDataObject encKey = new ConstructedTlvDataObject(publicKey.getEncoded());
		return (PrimitiveTlvDataObject) encKey.getTlvDataObject(TAG_BIT_STRING);
	}
	
	/**
	 * This method constructs a ChipAuthenticationDomainParameterInfo element
	 * _with_ optional key ID
	 * 
	 * ChipAuthenticationDomainParameterInfo ::= SEQUENCE {
	 *   protocol        OBJECT IDENTIFIER(id-CA-DH | id-CA-ECDH),
	 *   domainParameter AlgorithmIdentifier,
	 *   keyId           INTEGER OPTIONAL (present)
	 * 
	 * @param genericCaOidBytes the OBJECT IDENTIFIER to use
	 * @param algorithmIdentifier the AlgorithmIdentifier to use
	 * @param keyId the key ID to use
	 * @return the constructed ChipAuthenticationDomainParameterInfo element
	 */
	public static ConstructedTlvDataObject constructChipAuthenticationDomainParameterInfo(byte[] genericCaOidBytes, TlvDataObject algorithmIdentifier, int keyId) {
		ConstructedTlvDataObject chipAuthenticationDomainParameterInfo = constructChipAuthenticationDomainParameterInfo(genericCaOidBytes, algorithmIdentifier);
		chipAuthenticationDomainParameterInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, Utils.toShortestUnsignedByteArray(keyId)));
		
		return chipAuthenticationDomainParameterInfo;
	}
	
	/**
	 * This method constructs a ChipAuthenticationDomainParameterInfo element
	 * _without_ optional key ID
	 * 
	 * ChipAuthenticationDomainParameterInfo ::= SEQUENCE {
	 *   protocol        OBJECT IDENTIFIER(id-CA-DH | id-CA-ECDH),
	 *   domainParameter AlgorithmIdentifier,
	 *   keyId           INTEGER OPTIONAL (absent)
	 * 
	 * @param genericCaOidBytes the OBJECT IDENTIFIER to use
	 * @param algorithmIdentifier the AlgorithmIdentifier to use
	 * @return the constructed ChipAuthenticationDomainParameterInfo element
	 */
	public static ConstructedTlvDataObject constructChipAuthenticationDomainParameterInfo(byte[] genericCaOidBytes, TlvDataObject algorithmIdentifier) {
		ConstructedTlvDataObject chipAuthenticationDomainParameterInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
		chipAuthenticationDomainParameterInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, genericCaOidBytes));
		chipAuthenticationDomainParameterInfo.addTlvDataObject(algorithmIdentifier);
		
		return chipAuthenticationDomainParameterInfo;
	}
	
	/**
	 * This method constructs a ChipAuthenticationPublicKeyInfo element
	 * _with_ optional key ID
	 * 
	 * id-PK OBJECT IDENTIFIER ::= {
	 *   bsi-de protocols(2) smartcard(2) 1
	 * }
	 * 
	 * id-PK-DH                 OBJECT IDENTIFIER ::= {id-PK 1}
	 * id-PK-ECDH               OBJECT IDENTIFIER ::= {id-PK 2}
	 * 
	 * ChipAuthenticationPublicKeyInfo ::= SEQUENCE {
	 *   protocol                    OBJECT IDENTIFIER(id-PK-DH | id-PK-ECDH),
	 *   chipAuthenticationPublicKey SubjectPublicKeyInfo,
	 *   keyId                       INTEGER OPTIONAL (present)
	  * }
	 * 
	 * @param subjectPublicKeyInfo the subjectPublicKeyInfo element to use
	 * @param objectIdentifierBytes the object identifier bytes to use
	 * @param keyId the key ID to use
	 * @return the constructed ChipAuthenticationPublicKeyInfo element
	 */
	public static ConstructedTlvDataObject constructChipAuthenticationPublicKeyInfo(ConstructedTlvDataObject subjectPublicKeyInfo, byte[] objectIdentifierBytes, int keyId) {
		ConstructedTlvDataObject chipAuthenticationPublicKeyInfo = constructChipAuthenticationPublicKeyInfo(subjectPublicKeyInfo, objectIdentifierBytes);
		
		chipAuthenticationPublicKeyInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{(byte) keyId}));
		
		return chipAuthenticationPublicKeyInfo;
	}
	
	/**
	 * This method constructs a ChipAuthenticationPublicKeyInfo element
	 * _without_ optional key ID
	 * 
	 * id-PK OBJECT IDENTIFIER ::= {
	 *   bsi-de protocols(2) smartcard(2) 1
	 * }
	 * 
	 * id-PK-DH                 OBJECT IDENTIFIER ::= {id-PK 1}
	 * id-PK-ECDH               OBJECT IDENTIFIER ::= {id-PK 2}
	 * 
	 * ChipAuthenticationPublicKeyInfo ::= SEQUENCE {
	 *   protocol                    OBJECT IDENTIFIER(id-PK-DH | id-PK-ECDH),
	 *   chipAuthenticationPublicKey SubjectPublicKeyInfo,
	 *   keyId                       INTEGER OPTIONAL (absent)
	  * }
	 * 
	 * @param subjectPublicKeyInfo the subjectPublicKeyInfo element to use
	 * @param objectIdentifierBytes the object identifier bytes to use
	 * @param keyId the key ID to use
	 * @return the constructed ChipAuthenticationPublicKeyInfo element
	 */
	public static ConstructedTlvDataObject constructChipAuthenticationPublicKeyInfo(ConstructedTlvDataObject subjectPublicKeyInfo, byte[] objectIdentifierBytes) {
		ConstructedTlvDataObject chipAuthenticationPublicKeyInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
		chipAuthenticationPublicKeyInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, objectIdentifierBytes));
		chipAuthenticationPublicKeyInfo.addTlvDataObject(subjectPublicKeyInfo);
		
		return chipAuthenticationPublicKeyInfo;
	}
	
	/**
	 * This method constructs a SubjectPublicKeyInfo element
	 * 
	 * SubjectPublicKeyInfo ::= SEQUENCE {
	 *   algorithm        AlgorithmIdentifier,
	 *   subjectPublicKey BIT STRING
	 * }
	 * 
	 * @param algorithmIdentifier the AlgorithmIdentifier to use
	 * @param subjectPublicKey the subjectPublicKey (BIT STRING) to use
	 * @return the constructed SubjectPublicKeyInfo element
	 */
	public static ConstructedTlvDataObject constructSubjectPublicKeyInfo(ConstructedTlvDataObject algorithmIdentifier, PrimitiveTlvDataObject subjectPublicKey) {
		ConstructedTlvDataObject subjectPublicKeyInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
		subjectPublicKeyInfo.addTlvDataObject(algorithmIdentifier);
		subjectPublicKeyInfo.addTlvDataObject(subjectPublicKey);
		
		return subjectPublicKeyInfo;
	}
	
	/**
	 * This method constructs a SubjectPublicKeyInfo element by calling
	 * {@link #constructAlgorithmIdentifier(PublicKey)} and
	 * {@link #constructSubjectPublicKeyInfo(ConstructedTlvDataObject, PrimitiveTlvDataObject)}.
	 * 
	 * SubjectPublicKeyInfo ::= SEQUENCE { algorithm AlgorithmIdentifier,
	 * subjectPublicKey BIT STRING }
	 * 
	 * @param publicKey
	 *            the public key to use
	 * @return the constructed SubjectPublicKeyInfo element
	 */
	public static ConstructedTlvDataObject constructSubjectPublicKeyInfo(PublicKey publicKey) {
		ConstructedTlvDataObject algorithmIdentifier = constructAlgorithmIdentifier(publicKey);
		PrimitiveTlvDataObject subjectPublicKey = constructSubjectPublicKey(publicKey);
		return constructSubjectPublicKeyInfo(algorithmIdentifier, subjectPublicKey);
	}
	
}
