package de.persosim.simulator.protocols.pin;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.apdumatching.ApduSpecification;
import de.persosim.simulator.apdumatching.ApduSpecificationConstants;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.ChangeablePasswordAuthObject;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.PinObject;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.InfoSource;
import de.persosim.simulator.utils.Utils;

@XmlRootElement(name="PinManagement")
public class PinProtocol implements Protocol, Iso7816, Tr03110, TlvConstants, ApduSpecificationConstants, InfoSource{
	
	protected HashMap<String, ApduSpecification> apdus  = new HashMap<>();
	protected ApduSpecification apduSpecification;
	protected CardStateAccessor cardState;
	protected ProcessingData processingData; 
	
	public PinProtocol() {
		registerApdus();
		reset();
	}
	
	public String getProtocolName() {
		return "PIN";
	}


	public void setCardStateAccessor(CardStateAccessor cardState) {
		this.cardState = cardState;
	}


	public Collection<? extends TlvDataObject> getSecInfos(
			SecInfoPublicity publicity, MasterFile mf) {
		return Collections.emptySet();
	}
	
	public void process(ProcessingData processingData) {
		this.processingData = processingData;
		
		if (processingData.isReportingError()) {
			
			 /*
			 * Due to a processing error there is no guarantee that there is
			 * anything like an INS byte that could be set. Set dummy INS byte
			 * 0xFF instead;
			 */
			
			processEvent((byte) 0xFF);
		} else {
			processEvent(processingData.getCommandApdu().getIns());
		}
	}
	
	public int processEvent(int msg){
		if(isAPDU("Activate PIN")){
			
			logs("ACTIVATE_PIN_RECEIVED");
			processCommandActivatePin();

		}else if(isAPDU("Change PIN")){
			
			logs("CHANGE_PASSWORD_RECEIVED");
			processCommandChangePin();

		}else if(isAPDU("Deactivate PIN")){
			
			logs("DEACTIVATE_PIN_RECEIVED");
			processCommandDeactivatePin();

		}else if(isAPDU("Unblock PIN")){
			
			logs("UNBLOCK_PIN_RECEIVED");
			processCommandUnblockPin();
		}else if(isAPDU("Verify PIN")){
			
			logs("VERIFY_PIN_RECEIVED");
			processCommandVerifyPin();
		}
		return msg;
	}
	
	public boolean isAPDU(String apduId) {
		CommandApdu apdu;
		
		ApduSpecification apduSpec = apdus.get(apduId);
		
		if(apduSpec == null) {
			log(this, "APDU matching failed due to command \"" + apduId + "\" being unknown", DEBUG);
			return false;
		}
		
		if(processingData == null) {
			log(this, "APDU matching failed due to missing processing data", DEBUG);
			return false;
		}
		apdu = processingData.getCommandApdu();
		boolean match = apduSpec.matchesFullApdu(apdu);
		
		if(match) {
			log(this, "received APDU matches definition of command \"" + apduId + "\"", DEBUG);
		}
		return match;
	}
	
	//public Collection<ApduSpecification> getApduSet() {
	//	return apdus.values();
	//}

	public void reset() {
		processEvent((byte) 0xFF); // handle the first transition
	}

	public boolean isMoveToStackRequested() {
		return false;
	}
	
	private void registerApdus(){
		ApduSpecification apduSpecification = new ApduSpecification("Activate PIN");
		apduSpecification.setInitialAPDU(true);
		apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
		apduSpecification.setIsoCase(ISO_CASE_1);
		apduSpecification.setIns(INS_44_ACTIVATE_FILE);
		apduSpecification.setP1((byte) 0x10);
		apduSpecification.setP2(ID_PIN);
		apdus.put(apduSpecification.getId(), apduSpecification);
		
		apduSpecification = new ApduSpecification("Change PIN");
		apduSpecification.setInitialAPDU(true);
		apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
		apduSpecification.setIsoCase(ISO_CASE_3);
		apduSpecification.setP1(P1_RESET_RETRY_COUNTER_NEW_DATA);
		apduSpecification.setIns(INS_2C_RESET_RETRY_COUNTER);
		apdus.put(apduSpecification.getId(), apduSpecification);
		
		apduSpecification = new ApduSpecification("Deactivate PIN");
		apduSpecification.setInitialAPDU(true);
		apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
		apduSpecification.setIsoCase(ISO_CASE_1);
		apduSpecification.setIns(INS_04_DEACTIVATE_FILE);
		apduSpecification.setP1((byte) 0x10);
		apduSpecification.setP2(ID_PIN);
		apdus.put(apduSpecification.getId(), apduSpecification);
		
		apduSpecification = new ApduSpecification("Unblock PIN");
		apduSpecification.setInitialAPDU(true);
		apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
		apduSpecification.setIsoCase(ISO_CASE_1);
		apduSpecification.setIns(INS_2C_RESET_RETRY_COUNTER);
		apduSpecification.setP1(P1_RESET_RETRY_COUNTER_ABSENT_DATA);
		apduSpecification.setP2(ID_PIN);
		apdus.put(apduSpecification.getId(), apduSpecification);
		
		apduSpecification = new ApduSpecification("Verify PIN");
		apduSpecification.setInitialAPDU(true);
		apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
		apduSpecification.setIsoCase(ISO_CASE_1);
		apduSpecification.setIns(INS_20_VERIFY);
		apduSpecification.setP1(P1_SELECT_FILE_MF_DF_EF);
		apduSpecification.setP2(ID_PIN);
		apdus.put(apduSpecification.getId(), apduSpecification);
	}
	
	public void logs(String state) {
		log(this, "State changed to " + state, DEBUG);
	}
	
	public String getIDString() {
		return "Personal Identification Number";
	}
	
	private void processCommandActivatePin() {
			
		Object object = cardState.getObject(new AuthObjectIdentifier(Tr03110.ID_PIN), Scope.FROM_MF);
		if(!(object instanceof PinObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		((PinObject) object).updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "PIN successfully activated", resp);
		
		log(this, "processed COMMAND_ACTIVATE_PIN", DEBUG);
	}
	
	private void processCommandChangePin() {
		CommandApdu cApdu = processingData.getCommandApdu();
		TlvValue tlvData = cApdu.getCommandData();
		
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		Object object = cardState.getObject(new AuthObjectIdentifier(identifier), Scope.FROM_MF);
		
		if(!(object instanceof ChangeablePasswordAuthObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		ChangeablePasswordAuthObject passwordObject = (ChangeablePasswordAuthObject) object;
		
		String passwordName = passwordObject.getPasswordName();
		
		if(tlvData == null) {
			ResponseApdu resp = new ResponseApdu(SW_6A80_WRONG_DATA);
			this.processingData.updateResponseAPDU(this, "no new " + passwordName + " data received", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		byte[] newPasswordPlain = tlvData.toByteArray();
		
		log(this, "received data of " + newPasswordPlain.length + " bytes length for new " + passwordName + " is: " + HexString.dump(newPasswordPlain), DEBUG);
		
		log(this, "old " + passwordName + " is: " + HexString.dump(passwordObject.getPassword()), DEBUG);
		
		try {
			passwordObject.setPassword(newPasswordPlain);
		} catch (IllegalArgumentException e) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		} catch (IllegalStateException e) {
			ResponseApdu resp = new ResponseApdu(SW_6283_SELECTED_FILE_DEACTIVATED);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		}
		
		log(this, "new " + passwordName + " is: " + HexString.dump(newPasswordPlain), DEBUG);
		
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, passwordName + " successfully changed", resp);
		
		log(this, "processed COMMAND_CHANGE_PASSWORD", DEBUG);
	}
	
	private void processCommandDeactivatePin() {
		Object object = cardState.getObject(new AuthObjectIdentifier(Tr03110.ID_PIN), Scope.FROM_MF);
		
		if(!(object instanceof PinObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		//XXX this check should be done by the objects themself
		Collection<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(TerminalAuthenticationMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState
				.getCurrentMechanisms(SecContext.APPLICATION,
						previousMechanisms);
		TerminalAuthenticationMechanism taMechanism = null;
		if (currentMechanisms.size() > 0) {
			taMechanism = (TerminalAuthenticationMechanism) currentMechanisms
					.toArray()[0];

			if (!(taMechanism.getTerminalType().equals(TerminalType.AT) && taMechanism
					.getEffectiveAuthorization().getAuthorization().getBit(5))) {
				ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "PIN management rights from TA required to perform Deactivate", resp);
				return;
			}
		}
		((PinObject) object).updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED);
		
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "PIN successfully deactivated", resp);
		
		log(this, "processed COMMAND_DEACTIVATE_PIN", DEBUG);
	}
	
	private void processCommandUnblockPin() {
		Object object = cardState.getObject(new AuthObjectIdentifier(Tr03110.ID_PIN), Scope.FROM_MF);
		
		if(!(object instanceof PinObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		PinObject pinObject = (PinObject) object;
		
		log(this, "old PIN retry counter is: " + pinObject.getRetryCounterCurrentValue(), DEBUG);
		
		try {
			pinObject.resetRetryCounterToDefault();
		} catch (IllegalArgumentException e) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		} catch (IllegalStateException e) {
			ResponseApdu resp = new ResponseApdu(SW_6283_SELECTED_FILE_DEACTIVATED);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		}
		
		log(this, "new PIN retry counter is: " + pinObject.getRetryCounterCurrentValue(), DEBUG);
		
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "PIN successfully unblocked", resp);
		
		log(this, "processed COMMAND_UNBLOCK_PIN", DEBUG);
	}
	
	private void processCommandVerifyPin() {
		Object object = cardState.getObject(new AuthObjectIdentifier(Tr03110.ID_PIN), Scope.FROM_MF);
		
		if(!(object instanceof PinObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		PinObject pinObject = (PinObject) object;
		
		ResponseApdu resp = new ResponseApdu((short) (SW_63C0_COUNTER_IS_0 + (pinObject.getRetryCounterCurrentValue())));
		
		this.processingData.updateResponseAPDU(this, "new PIN retry counter is: " + pinObject.getRetryCounterCurrentValue(), resp);
		
		log(this, "processed COMMAND_VERIFY_PIN", DEBUG);
	}
	
}
