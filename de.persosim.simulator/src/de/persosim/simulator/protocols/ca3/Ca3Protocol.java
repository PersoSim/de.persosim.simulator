package de.persosim.simulator.protocols.ca3;

import static org.globaltester.logging.BasicLogger.log;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.exception.ProcessingException;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.PlatformUtil;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.Tr03110Utils;
import de.persosim.simulator.protocols.ca.CaOid;
import de.persosim.simulator.protocols.ca.ChipAuthenticationMechanism;
import de.persosim.simulator.protocols.ca.DefaultCaProtocol;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusMechanismUpdatePropagation;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;

/**
 * This class is part of the implementation of the Chip Authentication (CA)
 * protocol version 3 and implements basic methods to be used by
 * {@link DefaultCaProtocol}.
 * 
 */
public class Ca3Protocol extends de.persosim.simulator.protocols.ca.CaProtocol {
	
	@Override
	public String getIDString() {
		return "Chip Authentication Version 3";
	}

	@Override
	public String getProtocolName() {
		return "CA3";
	}
	
	/**
	 * This method prepares the response data to be sent within the response APDU
	 * @param ephemeralPublicKeyPicc the PICC's ephemeral public key
	 * @return the response data to be sent within the response APDU
	 */
	public TlvValue prepareResponseData(PublicKey ephemeralPublicKeyPicc) {
		//create and prepare response APDU
		byte[] ephemeralPublicKeyPiccEncoding = caDomainParameters.encodePublicKey(ephemeralPublicKeyPicc);
		PrimitiveTlvDataObject primitive81 = new PrimitiveTlvDataObject(TAG_81, ephemeralPublicKeyPiccEncoding);
		log(this, "primitive tag 81 is: " + primitive81, LogLevel.TRACE);
		ConstructedTlvDataObject constructed7C = new ConstructedTlvDataObject(TAG_7C);
		constructed7C.addTlvDataObject(primitive81);
		
		log(this, "response data to be sent is: " + constructed7C, LogLevel.DEBUG);
		
		//create and propagate response APDU
		return new TlvDataObjectContainer(constructed7C);
	}
	
	/**
	 * This method generates a new key pair based on the CA domain parameters
	 * @return the new key pair
	 */
	protected KeyPair generateEphemeralKeyPairPicc() {
		KeyPair ephemeralKeyPairPicc;
		try {
			ephemeralKeyPairPicc = CryptoUtil.generateKeyPair(caDomainParameters, Crypto.getSecureRandom());
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			throw new ProcessingException(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR, e.getMessage());
		}
		return ephemeralKeyPairPicc;
	}
	
	/**
	 * This method checks that no preceding CA3 has been performed
	 */
	protected void checkForPrecedingCa3() {
		HashSet<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(ChipAuthenticationMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		
		for (SecMechanism curMechanismToCheck : currentMechanisms) {
			if (curMechanismToCheck instanceof ChipAuthentication3Mechanism){
				throw new ProcessingException(PlatformUtil.SW_4982_SECURITY_STATUS_NOT_SATISFIED, "The chip authentication 3 can not be executed twice in a row");
			}
		}
		
	}
	
	@Override
	public void processCommandSetAT() {
		try {
			checkForPrecedingCa3();
			
			//get commandDataContainer
			TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
			
			//extract Session Contect ID from APDU
			sessionContextIdentifier = extractSessionContextId(commandData);
			
			caOid = extractCaOidFromCommandData(commandData);
			
			KeyIdentifier keyIdentifier = extractKeyIdentifierFromCommandData(commandData);
			
			// perform fuzzy matching - key needs to be some ECDH_ECSchnorr-* OID
			OidIdentifier psaOidIdentifier = new OidIdentifier(Psa.id_PSA_ECDH_ECSchnorr);
			KeyObject keyObject = getkeyObjectForKeyIdentifier(keyIdentifier, psaOidIdentifier);

			if (keyObject instanceof KeyObjectIcc){
				KeyObjectIcc keyObjectIcc = (KeyObjectIcc) keyObject;
				/* CA domain parameters */
				caDomainParameters = Tr03110Utils.getDomainParameterSetFromKey(keyObjectIcc.getPublicKeyICC());
			} else {
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
				processingData.updateResponseAPDU(this, "The domain parameters could not be extracted from the referenced key", resp);
				return;
			}
			
			keyReference = keyObject.getPrimaryIdentifier().getInteger();
			
			
			this.cryptoSupport = caOid.getCryptoSupport();
			
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
			processingData.updateResponseAPDU(this, "Command Set AT successfully processed", resp);
		} catch (ProcessingException e) {
			ResponseApdu resp = new ResponseApdu(e.getStatusWord());
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
		}
	}
	
	@Override
	public void processCommandGeneralAuthenticate() {
		try {
			checkForPrecedingCa3();
			
			byte[] pcdPublicKeyMaterial = getPcdPublicKeyMaterialFromApdu();
			PublicKey ephemeralPublicKeyPcd = reconstructEphemeralPublicKeyPcd(pcdPublicKeyMaterial);
			assertEphemeralPublicKeyPcdMatchesCompressedKeyReceivedDuringTa(ephemeralPublicKeyPcd);
			KeyPair ephemeralKeyPairPicc = generateEphemeralKeyPairPicc();
			
			PublicKey ephemeralPublicKeyPicc = ephemeralKeyPairPicc.getPublic();
			PrivateKey ephemeralPrivateKeyPicc = ephemeralKeyPairPicc.getPrivate();
			log(this, "CAv3 Picc's ephemeral public key : " + HexString.encode(caDomainParameters.encodePublicKey(ephemeralPublicKeyPicc)), LogLevel.DEBUG);
			log(this, "CAv3 Picc's ephemeral private key: " + HexString.encode(caDomainParameters.encodePrivateKey(ephemeralPrivateKeyPicc)), LogLevel.DEBUG);
			
			byte[] sharedSecret = performKeyAgreement(ephemeralKeyPairPicc.getPrivate(), ephemeralPublicKeyPcd);
			computeSessionKeys(sharedSecret, null);
			propagateSessionKeys();
			
			storeCurrentSessionContext();
			
			ChipAuthenticationMechanism mechanism = new ChipAuthentication3Mechanism(caOid, keyReference, ephemeralPublicKeyPcd, ephemeralKeyPairPicc);
			processingData.addUpdatePropagation(this, "Updated security status with chip authentication information", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, mechanism));
			
			TlvValue responseData = prepareResponseData(ephemeralKeyPairPicc.getPublic());
			
			publishSessionContextId();
			
			ResponseApdu resp = new ResponseApdu(responseData, Iso7816.SW_9000_NO_ERROR);
			processingData.updateResponseAPDU(this, "Command General Authenticate successfully processed", resp);
			
			/* 
			 * Request removal of this instance from the stack.
			 * Protocol either successfully completed or failed.
			 * In either case protocol is completed.
			 */
			processingData.addUpdatePropagation(this, "Command General Authenticate successfully processed - Protocol CA completed", new ProtocolUpdate(true));
		} catch (ProcessingException e) {
			ResponseApdu resp = new ResponseApdu(e.getStatusWord());
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
		}
	}
	
	@Override
	protected byte getVersion() {
		return 3;
	}
	
	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {

		OidIdentifier psaOidIdentifier = new OidIdentifier(Psa.id_PSA_ECDH_ECSchnorr);
		
		Collection<CardObject> psaKeyCardObjects = mf.findChildren(
				new KeyIdentifier(), psaOidIdentifier);
		
		ArrayList<TlvDataObject> secInfos = new ArrayList<>();
		
		for (CardObject curObject : psaKeyCardObjects) {
			if (! (curObject instanceof KeyObjectIcc)) {
				continue;
			}
			KeyObjectIcc curKey = (KeyObjectIcc) curObject;
			Collection<CardObjectIdentifier> identifiers = curKey.getAllIdentifiers();
			
			//extract keyId
			int keyId = -1;
			for (CardObjectIdentifier curIdentifier : identifiers) {
				if (curIdentifier instanceof KeyIdentifier) {
					keyId = ((KeyIdentifier) curIdentifier).getKeyReference();
					break;
				}
			}
			if (keyId == -1) continue; // skip keys that dont't provide a keyId
			
			//cached values
			Oid genericCaOidBytes = null;
			
			//construct and add ChipAuthenticationInfo object(s)
			for (CardObjectIdentifier curIdentifier : identifiers) {
				if (curIdentifier instanceof OidIdentifier) {
					Oid curOid = ((OidIdentifier) curIdentifier).getOid();
					if (curOid.startsWithPrefix(id_CA)) {
						genericCaOidBytes = CaOid.getKeyAgreementOid(curOid);
						
						ConstructedTlvDataObject caInfo = constructChipAuthenticationInfoObject(curOid, (byte) keyId);
						
						secInfos.add(caInfo);
					}
				}
			}
			
			if (genericCaOidBytes == null) {
				continue;
			}
			
			//extract required data from curKey
			ConstructedTlvDataObject encKey = new ConstructedTlvDataObject(curKey.getPublicKeyICC().getEncoded());
			ConstructedTlvDataObject algIdentifier = (ConstructedTlvDataObject) encKey.getTlvDataObject(TAG_SEQUENCE);
			
			//using standardized domain parameters if possible
			algIdentifier = StandardizedDomainParameters.simplifyAlgorithmIdentifier(algIdentifier);
			
			// add ChipAuthenticationDomainParameterInfo object(s)
			ConstructedTlvDataObject caDomainInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
			caDomainInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, genericCaOidBytes.toByteArray()));
			caDomainInfo.addTlvDataObject(algIdentifier);
			//always set keyId even if truly optional/not mandatory
			//another version of CA may be present so keys are no longer unique and the keyId field becomes mandatory
			caDomainInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, new byte[]{(byte) keyId}));
			
			secInfos.add(caDomainInfo);
			
		}
		
		return secInfos;
	}

}
