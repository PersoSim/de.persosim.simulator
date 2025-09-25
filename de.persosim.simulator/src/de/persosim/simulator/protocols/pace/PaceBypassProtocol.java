package de.persosim.simulator.protocols.pace;

import static org.globaltester.logging.BasicLogger.log;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.InfoSource;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.apdu.SmMarkerApdu;
import de.persosim.simulator.apdumatching.ApduSpecificationConstants;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectUtils;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.certificates.PublicKeyReference;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.protocols.ResponseData;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.ta.Authorization;
import de.persosim.simulator.protocols.ta.CertificateHolderAuthorizationTemplate;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.AuthorizationStore;
import de.persosim.simulator.secstatus.ConfinedAuthorizationMechanism;
import de.persosim.simulator.secstatus.PaceMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusMechanismUpdatePropagation;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * In order to simplify implementation of PACE within
 * de.persosim.driver.connector no real PACE is performed but only a bypassed
 * version. This bypassed version interfaces with this protocol which ensures
 * the pseude SM handling as well as the correct contents of the
 * {@link SecStatus}.
 * <p/>
 * Essentially a pseudo APDU initiates the new pace bypass sm session. This
 * carries all required data as provided in MSE SetAT and the selected password
 * in plain. The password is verified directly and if it matches an according
 * pseudo SM channel is setup.
 * <p/>
 * The pseudo SM are just normal APDUS with lowest two bytes of CLA set (as no
 * channels are supported this is sufficient)
 *
 * @author amay
 *
 */
public class PaceBypassProtocol implements Pace, Protocol, Iso7816, ApduSpecificationConstants, InfoSource, TlvConstants
{
	private CardStateAccessor cardState;
	private boolean pseudoSmIsActive = false;

	/*
	 * Move to stack relies on the order processingData is processed. If the
	 * protocol is already on the stack it is known that the ProcessingData will
	 * be seen at least twice before {@link #isMoveToStackRequested()} is
	 * called. This checking is implemented at the beginning of #process and
	 * results are stored in the following two variables.
	 */
	private boolean moveToStack = true;
	private ProcessingData lastSeenProcessingData = null;

	public PaceBypassProtocol()
	{
		reset();
	}

	@Override
	public String getProtocolName()
	{
		return "PaceBypass";
	}

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState)
	{
		this.cardState = cardState;
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf)
	{
		// no own SecInfos needed, simply support those configured by the actual PaceProtocol
		return Collections.emptySet();
	}

	@Override
	public void process(ProcessingData processingData)
	{
		// check whether this processingData has been seen before
		if (processingData == lastSeenProcessingData) {
			moveToStack = false;
		}
		else {
			moveToStack = true;
			lastSeenProcessingData = processingData;
		}

		byte cla = processingData.getCommandApdu().getCla();
		byte ins = processingData.getCommandApdu().getIns();
		if (cla == (byte) 0xff && ins == INS_86_GENERAL_AUTHENTICATE) {
			processInitPaceBypass(processingData);
		}
		else {
			processSm(processingData);
		}

	}


	/**
	 * Try to initiate a Pace Bypass
	 * <p>
	 *
	 */
	private void processInitPaceBypass(ProcessingData processingData)
	{
		// prepare the response data
		TlvDataObjectContainer responseObjects = new TlvDataObjectContainer();
		short sw = Iso7816.SW_9000_NO_ERROR;
		String note = "";

		// get commandDataContainer
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();

		// PACE password id
		PasswordAuthObject passwordObject = null;
		TlvDataObject tlvObject = commandData.getTlvDataObject(TAG_83);

		CardObject pwdCandidate = CardObjectUtils.getSpecificChild(cardState.getMasterFile(), new AuthObjectIdentifier(tlvObject.getValueField()));
		if (pwdCandidate instanceof PasswordAuthObject) {
			passwordObject = (PasswordAuthObject) pwdCandidate;
			log("selected password is: " + AbstractPaceProtocol.getPasswordName(passwordObject.getPasswordIdentifier()), LogLevel.DEBUG,
					new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		}
		else {
			sw = Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND;
			note = "no fitting authentication object found";
		}

		// provided password
		byte[] providedPassword = null;
		tlvObject = commandData.getTlvDataObject(TAG_92);
		if (tlvObject != null) {
			providedPassword = tlvObject.getValueField();

		}
		else {
			if (sw == Iso7816.SW_9000_NO_ERROR) {
				sw = Iso7816.SW_6A80_WRONG_DATA;
				note = "no password provided";
			}
		}

		// extract CHAT
		CertificateHolderAuthorizationTemplate usedChat = null;
		TrustPointCardObject trustPoint = null;
		tlvObject = commandData.getTlvDataObject(TAG_7F4C);
		if (tlvObject != null) {
			ConstructedTlvDataObject chatData = (ConstructedTlvDataObject) tlvObject;
			TlvDataObject oidData = chatData.getTlvDataObject(TAG_06);
			byte[] roleData = chatData.getTlvDataObject(TAG_53).getValueField();
			Oid chatOid = new GenericOid(oidData.getValueField());
			RelativeAuthorization authorization = new RelativeAuthorization(CertificateRole.getFromMostSignificantBits(roleData[0]), BitField.buildFromBigEndian((roleData.length * 8) - 2, roleData));
			usedChat = new CertificateHolderAuthorizationTemplate(TerminalType.getFromOid(chatOid), authorization);

			TerminalType terminalType = usedChat.getTerminalType();

			trustPoint = (TrustPointCardObject) CardObjectUtils.getSpecificChild(cardState.getMasterFile(), new TrustPointIdentifier(terminalType));
			if (!AbstractPaceProtocol.checkPasswordAndAccessRights(usedChat, passwordObject)) {
				if (sw == Iso7816.SW_9000_NO_ERROR) {
					sw = Iso7816.SW_6A80_WRONG_DATA;
					note = "The given terminal type and password does not match the access rights";
				}
			}
		}


		// check passwords
		boolean paceSuccessful = false;

		ResponseData responseData;
		if (sw == Iso7816.SW_9000_NO_ERROR) {
			responseData = AbstractPaceProtocol.isPasswordUsable(passwordObject, cardState, getProtocolName());
			if (responseData == null) {
				responseObjects.addTlvDataObject(new PrimitiveTlvDataObject(TAG_80, Utils.toUnsignedByteArray(SW_9000_NO_ERROR)));
			}
			else {
				// add MseSetAT SW to response data
				responseObjects.addTlvDataObject(new PrimitiveTlvDataObject(TAG_80, Utils.toUnsignedByteArray(responseData.getStatusWord())));
			}
		}

		if (sw == Iso7816.SW_9000_NO_ERROR) {
			if ((passwordObject != null) && (providedPassword != null) && Arrays.equals(providedPassword, passwordObject.getPassword())) {
				log("Provided password matches expected one", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

				if (passwordObject instanceof PasswordAuthObjectWithRetryCounter) {
					ResponseData pinResponse = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(passwordObject, cardState, getProtocolName());

					sw = pinResponse.getStatusWord();
					note = pinResponse.getResponse();

					paceSuccessful = !Iso7816Lib.isReportingError(sw);
				}
				else {
					sw = Iso7816.SW_9000_NO_ERROR;
					note = "MutualAuthenticate processed successfully";
					paceSuccessful = true;
				}


			}
			else {
				// PACE failed
				log("Provided password does NOT match expected one", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
				paceSuccessful = false;

				if (passwordObject instanceof PasswordAuthObjectWithRetryCounter) {
					ResponseData pinResponse = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceFailed((PasswordAuthObjectWithRetryCounter) passwordObject, getProtocolName());
					sw = pinResponse.getStatusWord();
					note = pinResponse.getResponse();
				}
				else {
					sw = Iso7816.SW_6300_AUTHENTICATION_FAILED;
					note = "Provided password does NOT match expected one";
				}
			}
		}

		if (paceSuccessful) {
			byte[] compEphermeralPublicKey = HexString.toByteArray("0102030405060708900A0B0C0D0E0F1011121314"); // arbitrary selected value
			TlvDataObject primitive86 = new PrimitiveTlvDataObject(TAG_86, compEphermeralPublicKey);
			responseObjects.addTlvDataObject(primitive86);

			// add CARs to response data if available
			if (trustPoint != null) {
				if (trustPoint.getCurrentCertificate() != null && trustPoint.getCurrentCertificate().getCertificateHolderReference() instanceof PublicKeyReference) {
					responseObjects.addTlvDataObject(new PrimitiveTlvDataObject(TAG_87, trustPoint.getCurrentCertificate().getCertificateHolderReference().getBytes()));
					if (trustPoint.getPreviousCertificate() != null && trustPoint.getPreviousCertificate().getCertificateHolderReference() instanceof PublicKeyReference) {
						responseObjects.addTlvDataObject(new PrimitiveTlvDataObject(TAG_88, trustPoint.getPreviousCertificate().getCertificateHolderReference().getBytes()));
					}
				}
			}

			// enable pseudo SM
			pseudoSmIsActive = true;

			// propagate data about successfully performed SecMechanism in SecStatus
			if (sw == Iso7816.SW_9000_NO_ERROR) {
				Oid terminalTypeOid = usedChat != null ? usedChat.getTerminalType().getAsOid() : null;
				byte[] compressedEphemeralPublicKeyTerminal = HexString.toByteArray("0102030405060708090A0B0C0D0E0F1011121314"); // arbitrary selected value
				PaceMechanism paceMechanism = new PaceMechanism(Pace.id_PACE_ECDH_GM_AES_CBC_CMAC_128, passwordObject, null, compEphermeralPublicKey, compressedEphemeralPublicKeyTerminal,
						terminalTypeOid);

				if (usedChat != null) {
					HashMap<Oid, Authorization> authorizations = new HashMap<>();
					authorizations.put(usedChat.getTerminalType().getAsOid(), usedChat.getRelativeAuthorization());
					AuthorizationStore authorizationStore = new AuthorizationStore(authorizations);
					ConfinedAuthorizationMechanism authMechanism = new ConfinedAuthorizationMechanism(authorizationStore);
					processingData.addUpdatePropagation(this, "Security status updated with authorization mechanism", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, authMechanism));
				}


				processingData.addUpdatePropagation(this, "Security status updated with PACE mechanism", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, paceMechanism));
			}

			note = "Established PACE Bypass";

		}

		// build and propagate response Apdu
		TlvValue responseTlvData = new TlvDataObjectContainer(responseObjects);
		ResponseApdu responseApdu = new ResponseApdu(responseTlvData, sw);
		processingData.updateResponseAPDU(this, note, responseApdu);
	}

	/**
	 * Handle pseudo SM APDU.
	 * <p/>
	 * After PACE was successfully initialized through
	 * {@link #processInitPaceBypass(ProcessingData)} pseudo SM is initiated,
	 * that does not provide any kind of security. This is indicated by usage of
	 * the otherwise unused logical Channel 3 e.g. the lowest two bits of CLA
	 * are set.
	 * <p/>
	 * This method removes these flagging bits and ensures that the "decoded"
	 * commandApdu correctly returns on
	 * {@link IsoSecureMessagingCommandApdu#wasSecureMessaging()}
	 * <p/>
	 * Responses are simply returned in plain. Pseudo SM is aborted when an SM
	 * 6987 or 6988 is returned or whenever a plain (unflagged) APDU is
	 * transmitted.
	 */
	private void processSm(ProcessingData processingData)
	{
		CommandApdu commandApdu = processingData.getCommandApdu();
		byte cla = commandApdu.getCla();
		if ((cla & 0x03) != 0x03) {
			if (pseudoSmIsActive) {
				if (!(commandApdu instanceof IsoSecureMessagingCommandApdu) || !((IsoSecureMessagingCommandApdu) commandApdu).wasSecureMessaging()) {

					log("Plain APDU received, breaking pseudo SM", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
					pseudoSmIsActive = false;

					// remove from protocol stack
					processingData.addUpdatePropagation(this, "Pseudo SM deactivated, no need to stay on stack", new ProtocolUpdate(true));
				}
			}
			return;
		}
		// ignore everything when pseudo SM is not active
		if (!pseudoSmIsActive) {
			// do nothing with this APDU
			return;
		}

		// add a dummy APDU in the chain that indicates wasSecureMessaging()
		SmMarkerApdu smMarkerApdu = new SmMarkerApdu(commandApdu);
		processingData.updateCommandApdu(this, "SM marker APDU added", smMarkerApdu);

		// unmask the pseudo SM CLA and create new CommandApdu
		byte[] apduBytes = commandApdu.toByteArray();
		apduBytes[0] &= (byte) 0xFC;
		processingData.updateCommandApdu(this, "Unmasked plain APDU", CommandApduFactory.createCommandApdu(apduBytes, smMarkerApdu));
	}

	@Override
	public String getIDString()
	{
		return "PaceBypass";
	}

	@Override
	public void reset()
	{
		// do NOT reset anything here (as this might be called when the protocol is still active on stack)
	}

	@Override
	public boolean isMoveToStackRequested()
	{
		return moveToStack;
	}

}
