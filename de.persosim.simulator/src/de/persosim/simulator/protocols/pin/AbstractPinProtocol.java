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
import de.persosim.simulator.cardobjects.NullCardObject;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.protocols.AbstractProtocolStateMachine;
import de.persosim.simulator.protocols.TR03110;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

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
		PasswordAuthObject pinObject = getPasswordAuthObject(TR03110.ID_PIN);
		
		pinObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		ResponseApdu resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, "PIN successfully activated", resp);
		
		log(this, "processed COMMAND_ACTIVATE_PIN", DEBUG);
	}
	
	public void processCommandChangePassword() {
		CommandApdu cApdu = processingData.getCommandApdu();
		TlvValue tlvData = cApdu.getCommandData();
		
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		ChangeablePasswordAuthObject passwordObject = (ChangeablePasswordAuthObject) getPasswordAuthObject(identifier);
		
		if(passwordObject == null) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A86_INCORRECT_PARAMETERS_P1P2);
			this.processingData.updateResponseAPDU(this, "P2 references unknown password " + identifier, resp);
			/* there is nothing more to be done here */
			return;
		}
		
		String passwordName = passwordObject.getPasswordName();
		
		if(tlvData == null) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
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
		
		log(this, "processed COMMAND_CHANGE_PASSWORD", DEBUG);
	}
	
	public void processCommandDeactivatePin() {
		PasswordAuthObject pinObject = getPasswordAuthObject(TR03110.ID_PIN);
		
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
		PasswordAuthObjectWithRetryCounter pinObject = (PasswordAuthObjectWithRetryCounter) getPasswordAuthObject(TR03110.ID_PIN);
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
	
	private PasswordAuthObject getPasswordAuthObject(int identifier) {
		Object object = cardState.getObject(new AuthObjectIdentifier(identifier), Scope.FROM_MF);
		
		if(object instanceof NullCardObject) {
			return null;
		} else{
			return (PasswordAuthObject) object;
		}
	}

}
