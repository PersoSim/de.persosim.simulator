package de.persosim.simulator.protocols.pin;

import static org.globaltester.logging.BasicLogger.log;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.InfoSource;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

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
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.secstatus.CAPAMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusEventUpdatePropagation;
import de.persosim.simulator.secstatus.SecurityEvent;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class PinProtocol implements Protocol, Iso7816, Tr03110, TlvConstants, ApduSpecificationConstants, InfoSource
{
	protected ApduSpecification apduSpecification;
	protected CardStateAccessor cardState;
	protected ProcessingData processingData;

	@Override
	public String getProtocolName()
	{
		return "PIN";
	}

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState)
	{
		this.cardState = cardState;
	}

	@Override
	public Collection<? extends TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf)
	{
		return Collections.emptySet();
	}

	@Override
	public void process(ProcessingData processingData)
	{
		this.processingData = processingData;
		if (processingData != null) {
			byte cla = processingData.getCommandApdu().getCla();
			byte ins = processingData.getCommandApdu().getIns();
			byte p1 = processingData.getCommandApdu().getP1();
			byte p2 = processingData.getCommandApdu().getP2();
			if (cla == (byte) 0x00) {
				switch (ins) {
					case INS_20_VERIFY:
						if (p1 == 0x00 && p2 == 0x03) {
							HashSet<Class<? extends SecMechanism>> wantedMechanisms = new HashSet<>();
							wantedMechanisms.add(CAPAMechanism.class);
							Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, wantedMechanisms);
							for (SecMechanism currentMechanism : currentMechanisms) {
								if (currentMechanism instanceof CAPAMechanism) {
									// see CAPAProtocolPINVerify
									log("APDU can not be processed, this protocol is not applicable while performing CAPA.", LogLevel.DEBUG,
											new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
									return;
								}
							}
							// Hack for special CAPA PIN Verify test case: Secure messaging is already
							// ended; no CAPAMechanism available
							LinkedList<UpdatePropagation> updatePropagations = processingData.getUpdatePropagations(de.persosim.simulator.secstatus.SecStatusEventUpdatePropagation.class);
							for (UpdatePropagation updatePropagation : updatePropagations) {
								if (updatePropagation instanceof SecStatusEventUpdatePropagation secStatusEventUpdatePropagation) {
									SecurityEvent securityEvent = secStatusEventUpdatePropagation.getEvent();
									if (SecurityEvent.SECURE_MESSAGING_SESSION_ENDED.equals(securityEvent)) {
										// see CAPAProtocolPINVerify
										log("APDU can not be processed, this protocol is not applicable while performing CAPA (SECURE_MESSAGING_SESSION_ENDED).", LogLevel.DEBUG,
												new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
										return;
									}
								}
							}
						}
						log("Perform " + getProtocolName() + " Verify", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
						processCommandVerifyPassword();
						break;
					case INS_2C_RESET_RETRY_COUNTER:
						/* Values for p2 must be less than 0x1F */
						if (p1 == 0x02 && p2 < 0x1F) {
							processCommandChangePassword();
							break;
						}
						/*
						 * Because unblocking the CAN is not necessary regarding ISO 7816 p2 must be
						 * 0x03
						 */
						else if (p1 == 0x03 && p2 == 0x03) {
							processCommandUnblockPassword();
							break;
						}
						else {
							log("APDU can not be processed, this protocol is not applicable.", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
							break;
						}
					case INS_44_ACTIVATE_FILE:
						if (p1 == 0x10) {
							processCommandActivatePassword();
							break;
						}
						else {
							log("APDU can not be processed, this protocol is not applicable.", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
							break;
						}
					case INS_04_DEACTIVATE_FILE:
						if (p1 == 0x10) {
							processCommandDeactivatePassword();
							break;
						}
						else {
							log("APDU can not be processed, this protocol is not applicable.", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
							break;
						}
					default:
						log("APDU can not be processed, this protocol is not applicable.", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
						break;
				}
			}
		}
		else

		{
			log("APDU can not be processed, this protocol is not applicable.", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		}
	}

	@Override
	public void reset()
	{
		// nothing to do
	}

	@Override
	public boolean isMoveToStackRequested()
	{
		return false;
	}

	@Override
	public String getIDString()
	{
		return "Personal Identification Number";
	}

	private void processCommandActivatePassword()
	{
		CommandApdu cApdu = processingData.getCommandApdu();

		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());

		CardObject object = CardObjectUtils.findObject(cardState.getMasterFile(), new AuthObjectIdentifier(identifier));
		if (!(object instanceof PasswordAuthObjectWithRetryCounter)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}

		PasswordAuthObjectWithRetryCounter passwordObject = ((PasswordAuthObjectWithRetryCounter) object);
		if (!passwordObject.getLifeCycleState().equals(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED)) {
			try {
				passwordObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
			}
			catch (LifeCycleChangeException e) {
				ResponseApdu resp = new ResponseApdu(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "PIN object transition from state " + e.getOldState() + " to " + e.getNewState() + " not possible", resp);
				/* there is nothing more to be done here */
				return;
			}
			catch (AccessDeniedException e) {
				ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "Access conditions to activate password not met", resp);
				/* there is nothing more to be done here */
				return;
			}
		}

		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		String passwordName = ((PasswordAuthObjectWithRetryCounter) object).getPasswordName();
		this.processingData.updateResponseAPDU(this, passwordName + " successfully activated", resp);

		log("processed COMMAND_ACTIVATE_PASSWORD", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
	}

	private void processCommandChangePassword()
	{
		CommandApdu cApdu = processingData.getCommandApdu();
		TlvValue tlvData = cApdu.getCommandData();

		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());
		CardObject object = CardObjectUtils.findObject(cardState.getMasterFile(), new AuthObjectIdentifier(identifier));

		if (!(object instanceof ChangeablePasswordAuthObject)) {
			ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this, "Password is not changeable", resp);
			/* there is nothing more to be done here */
			return;
		}

		ChangeablePasswordAuthObject passwordObject = (ChangeablePasswordAuthObject) object;
		String passwordName = passwordObject.getPasswordName();

		if (passwordObject.isResetRetryCounterAvailable()) {
			try {
				passwordObject.decrementResetRetryCounter();
			}
			catch (IllegalStateException e) {
				ResponseApdu resp = new ResponseApdu(SW_6900_COMMAND_NOT_ALLOWED);
				this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
				/* there is nothing more to be done here */
				return;
			}
		}

		if (tlvData.isEmpty()) {
			ResponseApdu resp = new ResponseApdu(SW_6A80_WRONG_DATA);
			this.processingData.updateResponseAPDU(this, "no new " + passwordName + " data received", resp);
			/* there is nothing more to be done here */
			return;
		}

		byte[] newPasswordPlain = tlvData.toByteArray();

		log("received data of " + newPasswordPlain.length + " bytes length for new " + passwordName + " is: " + HexString.dump(newPasswordPlain), LogLevel.DEBUG,
				new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		log("old " + passwordName + " is: " + HexString.dump(passwordObject.getPassword()), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		try {
			passwordObject.setPassword(newPasswordPlain);
		}
		catch (IllegalArgumentException e) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		}
		catch (IllegalStateException e) {
			ResponseApdu resp = new ResponseApdu(SW_6283_SELECTED_FILE_DEACTIVATED);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		}
		catch (AccessDeniedException e) {
			ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this, "Access conditions to change " + passwordObject.getPasswordName() + " not met", resp);
			/* there is nothing more to be done here */
			return;
		}
		log("new " + passwordName + " is: " + HexString.dump(newPasswordPlain), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, passwordName + " successfully changed", resp);

		log("processed COMMAND_CHANGE_PASSWORD", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
	}

	private void processCommandDeactivatePassword()
	{
		CommandApdu cApdu = processingData.getCommandApdu();

		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());

		CardObject object = CardObjectUtils.findObject(cardState.getMasterFile(), new AuthObjectIdentifier(identifier));
		if (!(object instanceof PasswordAuthObjectWithRetryCounter)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}

		PasswordAuthObjectWithRetryCounter passwordObject = ((PasswordAuthObjectWithRetryCounter) object);
		if (!passwordObject.getLifeCycleState().equals(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED)) {
			try {
				passwordObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED);
			}
			catch (LifeCycleChangeException e) {
				ResponseApdu resp = new ResponseApdu(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "PIN object transition from state " + e.getOldState() + " to " + e.getNewState() + " not possible", resp);
				/* there is nothing more to be done here */
				return;
			}
			catch (AccessDeniedException e) {
				ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
				this.processingData.updateResponseAPDU(this, "Access conditions to deactivate password not met", resp);
				/* there is nothing more to be done here */
				return;
			}
		}

		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		String passwordName = ((PasswordAuthObjectWithRetryCounter) object).getPasswordName();
		this.processingData.updateResponseAPDU(this, passwordName + " successfully deactivated", resp);

		log("processed COMMAND_DEACTIVATE_PASSWORD", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
	}

	private void processCommandUnblockPassword()
	{
		CommandApdu cApdu = processingData.getCommandApdu();
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());

		CardObject object = CardObjectUtils.findObject(cardState.getMasterFile(), new AuthObjectIdentifier(identifier));

		if (!(object instanceof PasswordAuthObjectWithRetryCounter)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}

		PasswordAuthObjectWithRetryCounter pinObject = (PasswordAuthObjectWithRetryCounter) object;
		String passwordName = pinObject.getPasswordName();

		if (pinObject.isResetRetryCounterAvailable()) {
			try {
				pinObject.decrementResetRetryCounter();
			}
			catch (IllegalStateException e) {
				ResponseApdu resp = new ResponseApdu(SW_6900_COMMAND_NOT_ALLOWED);
				this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
				/* there is nothing more to be done here */
				return;
			}
		}

		log("old " + passwordName + " retry counter is: " + pinObject.getRetryCounterCurrentValue(), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		try {
			pinObject.resetRetryCounterToDefault();
		}
		catch (IllegalArgumentException e) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		}
		catch (IllegalStateException e) {
			ResponseApdu resp = new ResponseApdu(SW_6283_SELECTED_FILE_DEACTIVATED);
			this.processingData.updateResponseAPDU(this, e.getMessage(), resp);
			/* there is nothing more to be done here */
			return;
		}
		catch (AccessDeniedException e) {
			ResponseApdu resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
			this.processingData.updateResponseAPDU(this, "Access conditions to unblock " + pinObject.getPasswordName() + " not met", resp);
			/* there is nothing more to be done here */
			return;
		}

		log("new " + passwordName + " retry counter is: " + pinObject.getRetryCounterCurrentValue(), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		ResponseApdu resp = new ResponseApdu(SW_9000_NO_ERROR);
		this.processingData.updateResponseAPDU(this, passwordName + " successfully unblocked", resp);

		log("processed COMMAND_UNBLOCK_PASSWORD", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
	}

	private void processCommandVerifyPassword()
	{
		CommandApdu cApdu = processingData.getCommandApdu();
		int identifier = Utils.maskUnsignedByteToInt(cApdu.getP2());

		CardObject object = CardObjectUtils.findObject(cardState.getMasterFile(), new AuthObjectIdentifier(identifier));

		if (!(object instanceof PasswordAuthObjectWithRetryCounter)) {
			ResponseApdu resp = new ResponseApdu(SW_6984_REFERENCE_DATA_NOT_USABLE);
			this.processingData.updateResponseAPDU(this, "PIN object not found", resp);
			/* there is nothing more to be done here */
			return;
		}
		PasswordAuthObjectWithRetryCounter pinObject = (PasswordAuthObjectWithRetryCounter) object;
		String passwordName = pinObject.getPasswordName();
		ResponseApdu resp = new ResponseApdu((short) (SW_63C0_COUNTER_IS_0 + (pinObject.getRetryCounterCurrentValue())));
		this.processingData.updateResponseAPDU(this, passwordName + " retry counter is: " + pinObject.getRetryCounterCurrentValue(), resp);

		log("processed COMMAND_VERIFY_PASSWORD", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
	}
}
