package de.persosim.simulator.protocols.pin;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.ChangeablePasswordAuthObject;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.protocols.AbstractProtocolStateMachine;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;

/**
 * This class implements the PIN management functionality specified in TR-03110,
 * i.e. all parts that have not already been covered by the implementation of
 * the PACE protocol.
 * 
 * @author slutters
 * 
 */
public abstract class AbstractPinProtocol extends AbstractProtocolStateMachine implements Pin, TlvConstants {
	
	public AbstractPinProtocol() {
		super("PIN");
	}
	
	public void processCommandActivatePin() {
		PasswordAuthObjectWithRetryCounter pinObject = getPinObject();
		
		pinObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "PIN successfully activated", resp);
		
		log(this, "processed COMMAND_ACTIVATE_PIN", DEBUG);
	}
	
	public void processCommandChangePin() {
		changePassword(getPinObject());
		log(this, "processed COMMAND_CHANGE_PIN", DEBUG);
	}
	
	public void processCommandChangeCan() {
		changePassword(getCanObject());
		log(this, "processed COMMAND_CHANGE_CAN", DEBUG);
	}
	
	//XXX this could be defined as a generic command instead of the both methods above
	private void changePassword(ChangeablePasswordAuthObject passwordObject) {
		CommandApdu cApdu = processingData.getCommandApdu();
		TlvValue tlvData = cApdu.getCommandData();
		
		String passwordName = passwordObject.getPasswordName();
		
		//XXX SLS null check on passwordObject
		
		if(tlvData == null) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND); // is this the correct SW here? suggest 6A80
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
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		} catch (IllegalStateException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6283_SELECTED_FILE_DEACTIVATED);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		}
		
		log(this, "new " + passwordName + " is: " + HexString.dump(newPasswordPlain), DEBUG);
		
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, passwordName + " successfully changed", resp);
	}
	
	public void processCommandDeactivatePin() {
		PasswordAuthObjectWithRetryCounter pinObject = getPinObject();
		
		
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
				ResponseApdu resp = new ResponseApdu(Iso7816.SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "PIN management rights from TA required to perform Deactivate", resp);
				return;
			}
		}
		
		pinObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED);
		
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "PIN successfully deactivated", resp);
		
		log(this, "processed COMMAND_DEACTIVATE_PIN", DEBUG);
	}
	
	public void processCommandUnblockPin() {
		PasswordAuthObjectWithRetryCounter pinObject = getPinObject();
		log(this, "old PIN retry counter is: " + pinObject.getRetryCounterCurrentValue(), DEBUG);
		
		try {
			pinObject.resetRetryCounterToDefault();
		} catch (IllegalArgumentException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		} catch (IllegalStateException e) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6283_SELECTED_FILE_DEACTIVATED);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		}
		
		log(this, "new PIN retry counter is: " + pinObject.getRetryCounterCurrentValue(), DEBUG);
		
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "PIN successfully unblocked", resp);
		
		log(this, "processed COMMAND_UNBLOCK_PIN", DEBUG);
	}
	
	public void processCommandResumePin() {
		log(this, "processed COMMAND_RESUME_PIN", DEBUG);
	}
	
	private PasswordAuthObjectWithRetryCounter getPinObject() {
		return (PasswordAuthObjectWithRetryCounter) cardState.getObject(new AuthObjectIdentifier(3), Scope.FROM_MF);
	}
	
	private ChangeablePasswordAuthObject getCanObject() {
		return (ChangeablePasswordAuthObject) cardState.getObject(new AuthObjectIdentifier(2), Scope.FROM_MF);
	}

}
