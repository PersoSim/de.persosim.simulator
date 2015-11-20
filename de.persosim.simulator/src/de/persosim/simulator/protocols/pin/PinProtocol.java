package de.persosim.simulator.protocols.pin;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

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
import de.persosim.simulator.exception.LifeCycleChangeException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.protocols.ta.Authorization;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.EffectiveAuthorizationMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.InfoSource;
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
		
		Object object = cardState.getObject(new AuthObjectIdentifier(identifier), Scope.FROM_MF);
		if(!(object instanceof PinObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		try {
			((PinObject) object).updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		} catch (LifeCycleChangeException e) {
			ResponseApdu resp = new ResponseApdu(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this, "PIN object transition from state " + e.getOldState() + " to " + e.getNewState() + " not possible", resp);
			/* there is nothing more to be done here */
			return;
		}
		String passwordName = ((PinObject) object).getPasswordName();
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, passwordName + " successfully activated", resp);
		
		log(this, "processed COMMAND_ACTIVATE_PASSWORD", DEBUG);
	}
	
	private void processCommandChangePassword() {
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
	
	private void processCommandDeactivatePassword() {
		CommandApdu cApdu = processingData.getCommandApdu();
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		
		Object object = cardState.getObject(new AuthObjectIdentifier(identifier), Scope.FROM_MF);
		
		if(!(object instanceof PinObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		String passwordName = ((PinObject) object).getPasswordName();
		//XXX this check should be done by the objects themself
		Collection<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(TerminalAuthenticationMechanism.class);
		previousMechanisms.add(EffectiveAuthorizationMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		TerminalAuthenticationMechanism taMechanism = null;
		EffectiveAuthorizationMechanism authMechanism = null;
		
		if (currentMechanisms.size() >= 2) {
			for(SecMechanism secmechanism:currentMechanisms) {
				if(secmechanism instanceof TerminalAuthenticationMechanism) {
					taMechanism = (TerminalAuthenticationMechanism) secmechanism;
				}
				
				if(secmechanism instanceof EffectiveAuthorizationMechanism) {
					authMechanism = (EffectiveAuthorizationMechanism) secmechanism;
				}
			}
			
			if((taMechanism == null) || (authMechanism == null)) {
				ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, passwordName + " management rights from TA required to perform Deactivate", resp);
				return;
			}
			
			Authorization auth = authMechanism.getAuthorization(TaOid.id_AT);
			
			if (!(taMechanism.getTerminalType().equals(TerminalType.AT) && (auth != null) && auth.getAuthorization().getBit(5))) {
				ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, passwordName + " management rights from TA required to perform Deactivate", resp);
				return;
			}
		}
		try {
			((PinObject) object).updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED);
		} catch (LifeCycleChangeException e) {
			ResponseApdu resp = new ResponseApdu(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this, "PIN object transition from state " + e.getOldState() + " to " + e.getNewState() + " not possible", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, passwordName + " successfully deactivated", resp);
		
		log(this, "processed COMMAND_DEACTIVATE_PASSWORD" , DEBUG);
	}
	
	private void processCommandUnblockPassword() {
		CommandApdu cApdu = processingData.getCommandApdu();
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		
		Object object = cardState.getObject(new AuthObjectIdentifier(identifier), Scope.FROM_MF);
		
		if(!(object instanceof PinObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		PinObject pinObject = (PinObject) object;
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
		}
		
		log(this, "new " + passwordName + " retry counter is: " + pinObject.getRetryCounterCurrentValue(), DEBUG);
		
		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, passwordName +" successfully unblocked", resp);
		
		log(this, "processed COMMAND_UNBLOCK_PASSWORD", DEBUG);
	}
	
	private void processCommandVerifyPassword() {
		CommandApdu cApdu = processingData.getCommandApdu();		
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		
		Object object = cardState.getObject(new AuthObjectIdentifier(identifier), Scope.FROM_MF);
		
		if(!(object instanceof PinObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		PinObject pinObject = (PinObject) object;
		String passwordName = pinObject.getPasswordName();
		ResponseApdu resp = new ResponseApdu((short) (SW_63C0_COUNTER_IS_0 + (pinObject.getRetryCounterCurrentValue())));
		this.processingData.updateResponseAPDU(this, passwordName + " retry counter is: " + pinObject.getRetryCounterCurrentValue(), resp);
		
		log(this, "processed COMMAND_VERIFY_PASSWORD", DEBUG);
	}
}
