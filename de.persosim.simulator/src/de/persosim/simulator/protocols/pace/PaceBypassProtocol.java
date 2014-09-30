package de.persosim.simulator.protocols.pace;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.apdumatching.ApduSpecification;
import de.persosim.simulator.apdumatching.ApduSpecificationConstants;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.certificates.PublicKeyReference;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.ResponseData;
import de.persosim.simulator.protocols.ta.CertificateHolderAuthorizationTemplate;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.protocols.ta.TerminalType;
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
import de.persosim.simulator.utils.InfoSource;

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
@XmlRootElement
//XXX reduce code duplication with AbstractPaceProtocol
public class PaceBypassProtocol implements Pace, Protocol, Iso7816, ApduSpecificationConstants,
		InfoSource, TlvConstants {

	public class SmMarkerApdu implements CommandApdu {

		@Override
		public byte getIsoFormat() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public byte getCla() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public byte getIns() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public byte getP1() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public byte getP2() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public byte getIsoCase() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isExtendedLength() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getNc() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public TlvValue getCommandData() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TlvDataObjectContainer getCommandDataObjectContainer() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getNe() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public short getP1P2() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public byte[] getHeader() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public byte[] toByteArray() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CommandApdu getPredecessor() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isNeZeroEncoded() {
			// TODO Auto-generated method stub
			return false;
		}

	}

	private CardStateAccessor cardState;
	private boolean pseudoSmIsActive = false;

	public PaceBypassProtocol() {
		reset();
	}

	@Override
	public String getProtocolName() {
		return "PaceBypass";
	}

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState) {
		this.cardState = cardState;
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		//no own SecInfos needed, simply support those configured by the actual PaceProtocol
		return Collections.emptySet();
	}

	@Override
	public void process(ProcessingData processingData) {
		byte cla = processingData.getCommandApdu().getCla();
		byte ins = processingData.getCommandApdu().getIns(); 
		if (cla == (byte) 0xff && ins == INS_86_GENERAL_AUTHENTICATE) {
			processInitPaceBypass(processingData);
		} else if (cla != 0xff && ((cla&0x03) == 0x03)) {
			processSm(processingData);
		}
		
	}
	

	/**
	 * Try to initiate a Pace Bypass
	 * <p>
	 * 
	 */
	private void processInitPaceBypass(ProcessingData processingData) {
		// FIXME Auto-generated method stub
		//FIXME validate input
		
		//get commandDataContainer
		TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
		
		// PACE password id
		PasswordAuthObject pacePassword; //FIXME rename this
		TlvDataObject tlvObject = commandData.getTlvDataObject(TAG_83);
		
		CardObject pwdCandidate = cardState.getObject(new AuthObjectIdentifier(tlvObject.getValueField()), Scope.FROM_MF);
		if (pwdCandidate instanceof PasswordAuthObject){
			pacePassword = (PasswordAuthObject) pwdCandidate;
			log(this, "selected password is: " + AbstractPaceProtocol.getPasswordName(pacePassword.getPasswordIdentifier()), DEBUG);
		} else {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND);
			processingData.updateResponseAPDU(this, "no fitting authentication object found", resp);
			/* there is nothing more to be done here */
			return;
		}
		
		// provided password
		tlvObject = commandData.getTlvDataObject(TAG_92);
		if (tlvObject == null) {
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6A80_WRONG_DATA);
			processingData.updateResponseAPDU(this, "no password provided", resp);
			/* there is nothing more to be done here */
			return;
		}
		byte[] providedPassword = tlvObject.getValueField();
		
		//extract CHAT
		CertificateHolderAuthorizationTemplate usedChat = null;
		TrustPointCardObject trustPoint = null;
		tlvObject = commandData.getTlvDataObject(TAG_7F4C);
		if (tlvObject != null){
			ConstructedTlvDataObject chatData = (ConstructedTlvDataObject) tlvObject;
			TlvDataObject oidData = chatData.getTlvDataObject(TAG_06);
			byte[] roleData = chatData.getTlvDataObject(TAG_53).getValueField();
			TaOid chatOid = new TaOid(oidData.getValueField());
			RelativeAuthorization authorization = new RelativeAuthorization(
					CertificateRole.getFromMostSignificantBits(roleData[0]), BitField.buildFromBigEndian(
							(roleData.length * 8) - 2, roleData));
			usedChat = new CertificateHolderAuthorizationTemplate(chatOid,
					authorization);
			
			TerminalType terminalType = usedChat.getTerminalType();

			 trustPoint = (TrustPointCardObject) cardState.getObject(
					new TrustPointIdentifier(terminalType), Scope.FROM_MF);
			if (!AbstractPaceProtocol.checkPasswordAndAccessRights(usedChat, pacePassword)){
				ResponseApdu resp = new ResponseApdu(
						Iso7816.SW_6A80_WRONG_DATA);
				processingData.updateResponseAPDU(this, "The given terminal type and password does not match the access rights", resp);
				/* there is nothing more to be done here */
				return;
			}
		}
		
		
		//check passwords
		boolean paceSuccessful;
		short sw;
		String note;
		if(Arrays.equals(providedPassword, pacePassword.getPassword())) {
			log(this, "Provided password matches expected one", DEBUG);
			
			if(pacePassword instanceof PasswordAuthObjectWithRetryCounter) {
				ResponseData pinResponse = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceSuccessful(pacePassword, cardState);
				
				sw = pinResponse.getStatusWord();
				note = pinResponse.getResponse();
				
				paceSuccessful = !Iso7816Lib.isReportingError(sw);
			} else{
				sw = Iso7816.SW_9000_NO_ERROR;
				note = "MutualAuthenticate processed successfully";
				paceSuccessful = true;
			}
		} else{
			//PACE failed
			log(this, "Provided password does NOT match expected one", DEBUG);
			paceSuccessful = false;
			
			if(pacePassword.getPasswordIdentifier() == Pace.PWD_PIN) {
				ResponseData pinResponse = AbstractPaceProtocol.getMutualAuthenticatePinManagementResponsePaceFailed((PasswordAuthObjectWithRetryCounter) pacePassword);
				sw = pinResponse.getStatusWord();
				note = pinResponse.getResponse();
			} else{
				sw = Iso7816.SW_6A80_WRONG_DATA;
				note = "authentication token received from PCD does NOT match expected one";
			}
		}
		
		ResponseApdu responseApdu;
		
		if(paceSuccessful) {
			TlvDataObjectContainer responseObjects = new TlvDataObjectContainer();
			
			byte[] compEphermeralPublicKey = HexString.toByteArray("0102030405060708900A0B0C0D0E0F1011121314"); //arbitrary selected value
			TlvDataObject primitive86 = new PrimitiveTlvDataObject(TAG_86, compEphermeralPublicKey);
			responseObjects.addTlvDataObject(primitive86);
			
			//add CARs to response data if available
			if (trustPoint != null) {
				if (trustPoint.getCurrentCertificate() != null
						&& trustPoint.getCurrentCertificate()
								.getCertificateHolderReference() instanceof PublicKeyReference) {
					responseObjects
							.addTlvDataObject(new PrimitiveTlvDataObject(
									TAG_87, trustPoint.getCurrentCertificate()
											.getCertificateHolderReference()
											.getBytes()));
					if (trustPoint.getPreviousCertificate() != null
							&& trustPoint.getPreviousCertificate()
									.getCertificateHolderReference() instanceof PublicKeyReference) {
						responseObjects
								.addTlvDataObject(new PrimitiveTlvDataObject(
										TAG_88,
										trustPoint
												.getPreviousCertificate()
												.getCertificateHolderReference()
												.getBytes()));
					}
				}
			}
			
			TlvValue responseData = new TlvDataObjectContainer(responseObjects);
			
			
			
			
			//propagate data about successfully performed SecMechanism in SecStatus 
			PaceMechanism paceMechanism = new PaceMechanism(pacePassword, compEphermeralPublicKey, usedChat);
			processingData.addUpdatePropagation(this, "Security status updated with PACE mechanism", new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, paceMechanism));
				
			responseApdu = new ResponseApdu(responseData, sw);
			processingData.updateResponseAPDU(this, "Established PACE Bypass", responseApdu);
		
		} else{
			responseApdu = new ResponseApdu(sw);
		}
		
		processingData.updateResponseAPDU(this, note, responseApdu);
	}

	/**
	 * Handle pseudo SM APDU.
	 * <p/>
	 * After PACE was successfully initialized through
	 * {@link #processInitPaceBypass(ProcessingData)} pseudo SM is initiated,
	 * that does not provide any kind of security. This is indicated by usage of
	 * the otherwise unused logical Channel 3 e.g. the lowest two bits of CLA are
	 * set.
	 * <p/>
	 * This method removes these flagging bits and ensures that the "decoded"
	 * commandApdu correctly returns on
	 * {@link IsoSecureMessagingCommandApdu#wasSecureMessaging()}
	 * <p/>
	 * FIXME how to indicate SM responses?
	 */
	private void processSm(ProcessingData processingData) {
		CommandApdu commandApdu = processingData.getCommandApdu();
		byte cla = commandApdu.getCla();
		if ((cla&0x03) != 0x03) {
			if (pseudoSmIsActive) {
				log(this, "Plain APDU received, breaking pseudo SM");
				pseudoSmIsActive = false;
			}
		}
		//ignore everything when pseudo SM is not active
		if (!pseudoSmIsActive ) {
			//do nothing with this APDU
			return;
		}
		
		//add a dummy APDU in the chain that indicates wasSecureMessaging()
//		processingData.updateCommandApdu(this, "SM marker APDU added", new SmMarkerApdu(commandApdu));
		
	}

	@Override
	public Collection<ApduSpecification> getApduSet() {
		//currently not implemented (not required)
		return Collections.emptySet();
	}
	
	@Override
	public String getIDString() {
		return "PaceBypass";
	}

	@Override
	public void reset() {
		pseudoSmIsActive = false;
	}

}
