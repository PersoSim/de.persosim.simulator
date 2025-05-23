package de.persosim.simulator.protocols.ca3;

import static org.globaltester.logging.BasicLogger.log;
import static org.globaltester.logging.BasicLogger.logException;

import java.security.KeyPair;
import java.util.Collection;
import java.util.HashSet;

import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.apdumatching.ApduSpecification;
import de.persosim.simulator.crypto.certificates.ExtensionOid;
import de.persosim.simulator.exception.ProcessingException;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.PlatformUtil;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.protocols.RoleOid;
import de.persosim.simulator.protocols.ca.ChipAuthenticationMechanism;
import de.persosim.simulator.seccondition.AndSecCondition;
import de.persosim.simulator.seccondition.AuthorizationExtensionPresentSecCondition;
import de.persosim.simulator.seccondition.AuthorizationSecCondition;
import de.persosim.simulator.seccondition.NotSecCondition;
import de.persosim.simulator.seccondition.OrSecCondition;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;

/**
 * This class implements the pseudonymous signatures protocol as described in TR-03110
 * 
 * @author jgoeke
 * 
 */
public class PsaProtocol extends PsProtocol implements Psa {
	
	private ApduSpecification apduSpecificationSetAt;
	private ApduSpecification apduSpecificationSetGa;
	
	public PsaProtocol()
	{
		apduSpecificationSetAt =  new ApduSpecification("MSE: Set AT");
		apduSpecificationSetAt.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
		apduSpecificationSetAt.setIsoCase(ISO_CASE_3);
		apduSpecificationSetAt.setChaining(false);
		apduSpecificationSetAt.setIns(INS_22_MANAGE_SECURITY_ENVIRONMENT);
		apduSpecificationSetAt.setP1((byte) 0x41);
		apduSpecificationSetAt.setP2((byte) 0xA4);
		
		apduSpecificationSetGa =  new ApduSpecification("General Authenticate");
		apduSpecificationSetGa.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
		apduSpecificationSetGa.setIsoCase(ISO_CASE_4);
		apduSpecificationSetGa.setChaining(false);
		apduSpecificationSetGa.setIns(INS_86_GENERAL_AUTHENTICATE);
		apduSpecificationSetGa.setP1((byte) 0x00);
		apduSpecificationSetGa.setP2((byte) 0x00);
	}

	@Override
	public String getProtocolName() {
		return "Pseudonymous Signature Authentication (PSA)";
	}
	
	@Override
	public void process(ProcessingData processingData) {
		this.processingData = processingData;
				
		if (apduSpecificationSetAt.matchesFullApdu(processingData.getCommandApdu())){
			log(this, "starting processing of command " + apduSpecificationSetAt.getId() + " for protocol " + getProtocolName(), LogLevel.TRACE);
			processCommandSetAt();
			return;
		}
		if (apduSpecificationSetGa.matchesFullApdu(processingData.getCommandApdu())) {
			log(this, "starting processing of command " + apduSpecificationSetGa.getId() + " for protocol " + getProtocolName(), LogLevel.TRACE);
			processCommandGeneralAuthenticate();
			return;
		}
	}
	
	/**
	 * This method performs the processing of the PSA Set AT command.
	 */
	private void processCommandSetAt() {
		try{
			//get commandDataContainer
			TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
			
			//ensure CA was executed before (otherwise this is the wrong protocol)
			getCaExecutionStatus();
			
			byte[] oidData = extractPsOidFromTag80(commandData);
			psOid = createPsOid(oidData);
			log(this, "OID received from terminal is: " + psOid, LogLevel.DEBUG);
			
			setPrivateKeyReferencedByTag84(commandData);
			
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
			processingData.updateResponseAPDU(this, "Command " + apduSpecificationSetAt.getId() + " successfully processed", resp);
			
		} catch (ProcessingException e) {
			ResponseApdu resp = new ResponseApdu(e.getStatusWord());
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
			logException(this, e);
		}
	}
	
	/**
	 * This method performs the processing of the PSA General Authenticate
	 * command.
	 */
	private void processCommandGeneralAuthenticate() {
		try{
			//get commandDataContainer
			TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
			sectorPublicKey = extractPublicKeySector(commandData);
			
			log(this, "sector public key received from terminal is: " + HexString.encode(psDomainParameters.encodePublicKey(sectorPublicKey)), LogLevel.DEBUG);
			
			computeSectorIccs();
			
			log(this, "computing PSA signature", LogLevel.DEBUG);
			PsSignature signature = sign(getMessage());
			log(this, "signature is:\n" + signature, LogLevel.DEBUG);
			
			TlvValue responseData = generateResponseData(signature);
			log(this, "response data to be sent is: " + responseData, LogLevel.DEBUG);
			
			ResponseApdu resp = new ResponseApdu(responseData, Iso7816.SW_9000_NO_ERROR);
			processingData.updateResponseAPDU(this, "Command General Authenticate successfully processed", resp);
			processingData.addUpdatePropagation(this, "Command " + apduSpecificationSetGa.getId() + " successfully processed - Protocol " + getProtocolName() + " completed", new ProtocolUpdate(true));
		} catch (ProcessingException e) {
			ResponseApdu resp = new ResponseApdu(e.getStatusWord());
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
			logException(this, e);
		}
	}
	
	@Override
	protected Collection<SecMechanism> getCaExecutionStatus() {
		HashSet<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(ChipAuthenticationMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		
		if ((currentMechanisms.size() != 1) || !(currentMechanisms.iterator().next() instanceof ChipAuthentication3Mechanism)){

			throw new ProcessingException(PlatformUtil.SW_4982_SECURITY_STATUS_NOT_SATISFIED, "The " + getProtocolName() + " protocol can not be executed without preceding CA");
		}
		return currentMechanisms;
	}
	
	@Override
	protected byte[] getMessage() {
		Collection<SecMechanism> currentMechanisms = getCaExecutionStatus();
		
		KeyPair ephemeralPublicKeyIcc = getEphemeralKeyPairChip(currentMechanisms);
		
		byte[] keyData = psDomainParameters.encodePublicKey(ephemeralPublicKeyIcc.getPublic());
		
		PrimitiveTlvDataObject keyDataTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_81, keyData);
		
		byte[] message = keyDataTlv.toByteArray();
		
		log(this, "PSA message is the chip's ephemeral public key wrapped in tag 0x81: " + HexString.encode(message), LogLevel.DEBUG);
		
		return message;
	}
	
	/** This method extracts the chip's ephemeral key pair from the {@link ChipAuthentication3Mechanism}
	 * @param currentMechanisms the {@link ChipAuthentication3Mechanism} including the key pair
	 * @return KeyPair the chip's ephemeral key pair
	 */
	protected KeyPair getEphemeralKeyPairChip(Collection<SecMechanism> currentMechanisms) {
		KeyPair ephemeralPublicKeyIcc = null;
		for(SecMechanism secMechanism : currentMechanisms) {
			if(secMechanism instanceof ChipAuthentication3Mechanism) {
				ephemeralPublicKeyIcc = ((ChipAuthentication3Mechanism) secMechanism).getEphemeralKeyPairPicc();
				break; // there is at most one ChipAuthentication3Mechanism
			}
		}
		
		if (ephemeralPublicKeyIcc == null) {
			throw new ProcessingException(SW_6A88_REFERENCE_DATA_NOT_FOUND, "no epemeral public key found");
		}
		return ephemeralPublicKeyIcc;
	}
	
	@Override
	public SecCondition getSecConditionForExplicitAuthorization() {
		SecCondition ascSf = super.getSecConditionForExplicitAuthorization();
		
		AuthorizationExtensionPresentSecCondition sfPresent = new AuthorizationExtensionPresentSecCondition(ExtensionOid.id_specialFunctions);
		NotSecCondition notSfPresent = new NotSecCondition(sfPresent);
		
		AuthorizationSecCondition ascChat = new AuthorizationSecCondition(RoleOid.id_AT, 30);
		
		AndSecCondition and = new AndSecCondition(notSfPresent, ascChat);
		
		return new OrSecCondition(ascSf, and);
	}

	@Override
	protected PsaOid createPsOid(byte[] oidData) {
		return new PsaOid(oidData);
	}

	@Override
	public TlvTag getDataTag() {
		return TAG_7C;
	}

	@Override
	protected Oid getGenericPsxOid() {
		return Psa.id_PSA;
	}

	@Override
	protected int getBit() {
		return 5;
	}
	
}
