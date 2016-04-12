package de.persosim.simulator.protocols.pin;

import static org.globaltester.logging.PersoSimLogger.DEBUG;
import static org.globaltester.logging.PersoSimLogger.log;

import java.util.Collection;
import java.util.Collections;

import org.globaltester.logging.InfoSource;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.apdumatching.ApduSpecification;
import de.persosim.simulator.apdumatching.ApduSpecificationConstants;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectUtils;
import de.persosim.simulator.cardobjects.ChangeablePasswordAuthObject;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.LifeCycleChangeException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class PinProtocol implements Protocol, Iso7816, Tr03110, TlvConstants, ApduSpecificationConstants, InfoSource{
	
	protected ApduSpecification apduSpecification;
	protected CardStateAccessor cardState;
	protected ProcessingData processingData; 
	
	public PinProtocol() {
	}
	
	@Override
	public String getProtocolName() {
		return "PIN";
	}

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState) {
		this.cardState = cardState;
	}

	@Override
	public Collection<? extends TlvDataObject> getSecInfos(
			SecInfoPublicity publicity, MasterFile mf) {
		return Collections.emptySet();
	}
	
	@Override
	public void process(ProcessingData processingData) {
		this.processingData = processingData;
		if (processingData != null) {
		byte cla = processingData.getCommandApdu().getCla();
		byte ins = processingData.getCommandApdu().getIns();
		byte p1 = processingData.getCommandApdu().getP1();
		byte p2 = processingData.getCommandApdu().getP2();
		
		if(cla == (byte) 0x00) {
			switch(ins){
			case 0x20:
				processCommandVerifyPassword();
				break;
			case 0x2C:
				/* Values for p2 must be less than 0x1F */
				if (p1 == 0x02 && p2 < 0x1F) {
					processCommandChangePassword();
					break; 
				}
				/* Because unblocking the CAN is not necessary regarding ISO 7816 p2 must be 0x03 */
				else if (p1 == 0x03 && p2 == 0x03) {
					processCommandUnblockPassword();
					break;
				}
				else {
					log(this, "APDU can not be processed, this protocol is not applicable.", DEBUG);
					break;
				}
			case 0x44:
				if (p1 == 0x10) {
					processCommandActivatePassword();
					break;
				} else {
					log(this, "APDU can not be processed, this protocol is not applicable.", DEBUG);
					break;
				}
			case 0x04:
				if (p1 == 0x10) {
					processCommandDeactivatePassword();
					break;
				} else {
					log(this, "APDU can not be processed, this protocol is not applicable.", DEBUG);
					break;
				}
			default:
				log(this, "APDU can not be processed, this protocol is not applicable.", DEBUG);
				break;
			}
		}
		}
		else {
			log(this, "APDU can not be processed, this protocol is not applicable.", DEBUG);
		}
	}
	
	@Override
	public void reset() {
	}
	
	@Override
	public boolean isMoveToStackRequested() {
		return false;
	}
	
	@Override
	public String getIDString() {
		return "Personal Identification Number";
	}
	
	private void processCommandActivatePassword() {
		CommandApdu cApdu = processingData.getCommandApdu();
		
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		
		CardObject object = CardObjectUtils.findObject(cardState.getMasterFile(), new AuthObjectIdentifier(identifier));
		if(!(object instanceof PasswordAuthObjectWithRetryCounter)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		PasswordAuthObjectWithRetryCounter passwordObject = ((PasswordAuthObjectWithRetryCounter) object);
		if(!passwordObject.getLifeCycleState().equals(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED)) {
			try {
				passwordObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
			} catch (LifeCycleChangeException e) {
				ResponseApdu resp = new ResponseApdu(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "PIN object transition from state " + e.getOldState() + " to " + e.getNewState() + " not possible", resp);
				/* there is nothing more to be done here */
				return;
			} catch (AccessDeniedException e) {
				ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "Access conditions to activate password not met", resp);
				/* there is nothing more to be done here */
				return;
			}
		}
		
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		String passwordName = ((PasswordAuthObjectWithRetryCounter) object).getPasswordName();
		this.processingData.updateResponseAPDU(this, passwordName + " successfully activated", resp);
		
		log(this, "processed COMMAND_ACTIVATE_PASSWORD", DEBUG);
	}
	
	private void processCommandChangePassword() {
		CommandApdu cApdu = processingData.getCommandApdu();
		TlvValue tlvData = cApdu.getCommandData();
		
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		CardObject object = CardObjectUtils.findObject(cardState.getMasterFile(), new AuthObjectIdentifier(identifier));
		
		if(!(object instanceof ChangeablePasswordAuthObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this, "Password is not changeable", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		ChangeablePasswordAuthObject passwordObject = (ChangeablePasswordAuthObject) object;
		String passwordName = passwordObject.getPasswordName();
		
		if(tlvData.isEmpty()) {
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
		} catch (AccessDeniedException e) {
			ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this,
					"Access conditions to change " + passwordObject.getPasswordName() + " not met",
					resp);
			/* there is nothing more to be done here */
			return;
		}
		log(this, "new " + passwordName + " is: " + HexString.dump(newPasswordPlain), DEBUG);
		
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, passwordName + " successfully changed", resp);
		
		log(this, "processed COMMAND_CHANGE_PASSWORD", DEBUG);
	}
	
	private void processCommandDeactivatePassword() {
		CommandApdu cApdu = processingData.getCommandApdu();
		
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		
		CardObject object = CardObjectUtils.findObject(cardState.getMasterFile(), new AuthObjectIdentifier(identifier));
		if(!(object instanceof PasswordAuthObjectWithRetryCounter)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		PasswordAuthObjectWithRetryCounter passwordObject = ((PasswordAuthObjectWithRetryCounter) object);
		if(!passwordObject.getLifeCycleState().equals(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED)) {
			try {
				passwordObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED);
			} catch (LifeCycleChangeException e) {
				ResponseApdu resp = new ResponseApdu(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "PIN object transition from state " + e.getOldState() + " to " + e.getNewState() + " not possible", resp);
				/* there is nothing more to be done here */
				return;
			} catch (AccessDeniedException e) {
				ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "Access conditions to deactivate password not met", resp);
				/* there is nothing more to be done here */
				return;
			}
		}
		
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		String passwordName = ((PasswordAuthObjectWithRetryCounter) object).getPasswordName();
		this.processingData.updateResponseAPDU(this, passwordName + " successfully deactivated", resp);
		
		log(this, "processed COMMAND_DEACTIVATE_PASSWORD" , DEBUG);
	}
	
	private void processCommandUnblockPassword() {
		CommandApdu cApdu = processingData.getCommandApdu();
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		
		CardObject object = CardObjectUtils.findObject(cardState.getMasterFile(), new AuthObjectIdentifier(identifier));
		
		if(!(object instanceof PasswordAuthObjectWithRetryCounter)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		PasswordAuthObjectWithRetryCounter pinObject = (PasswordAuthObjectWithRetryCounter) object;
		String passwordName =  pinObject.getPasswordName();
		
		log(this, "old " + passwordName +" retry counter is: " + pinObject.getRetryCounterCurrentValue(), DEBUG);
		
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
		} catch (AccessDeniedException e) {
			ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this,
					"Access conditions to unblock " + pinObject.getPasswordName() + " not met",
					resp);
			/* there is nothing more to be done here */
			return;
		}
		
		log(this, "new " + passwordName + " retry counter is: " + pinObject.getRetryCounterCurrentValue(), DEBUG);
		
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, passwordName +" successfully unblocked", resp);
		
		log(this, "processed COMMAND_UNBLOCK_PASSWORD", DEBUG);
	}
	
	private void processCommandVerifyPassword() {
		CommandApdu cApdu = processingData.getCommandApdu();		
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		
		CardObject object = CardObjectUtils.findObject(cardState.getMasterFile(), new AuthObjectIdentifier(identifier));
		
		if(!(object instanceof PasswordAuthObjectWithRetryCounter)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		PasswordAuthObjectWithRetryCounter pinObject = (PasswordAuthObjectWithRetryCounter) object;
		String passwordName = pinObject.getPasswordName();
		ResponseApdu resp = new ResponseApdu((short) (SW_63C0_COUNTER_IS_0 + (pinObject.getRetryCounterCurrentValue())));
		this.processingData.updateResponseAPDU(this, passwordName + " retry counter is: " + pinObject.getRetryCounterCurrentValue(), resp);
		
		log(this, "processed COMMAND_VERIFY_PASSWORD", DEBUG);
	}
}
