package de.persosim.simulator.protocols.pace;

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu;
import de.persosim.simulator.apdumatching.ApduSpecification;
import de.persosim.simulator.apdumatching.ApduSpecificationConstants;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
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
public class PaceBypassProtocol implements Pace, Protocol, Iso7816, ApduSpecificationConstants,
		InfoSource, TlvConstants {

	private CardStateAccessor cardState;

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
		//FIXME implement
		byte cla = processingData.getCommandApdu().getCla();
		byte ins = processingData.getCommandApdu().getIns(); 
		if (cla == 0xff && ins == INS_86_GENERAL_AUTHENTICATE) {
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
		//FIXME add info to SecStatus
		//FIXME ensure that protocol stays on stack as long as pseudo SM is active
	}

	/**
	 * Handle pseudo SM APDU.
	 * <p/>
	 * After PACE was succesfully initalized through
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
		// FIXME Auto-generated method stub
		//FIXME break SM Channel and remove protocol from stack
		
	}

	@Override
	public Collection<ApduSpecification> getApduSet() {
		//currently not implemented (not required)
		return Collections.emptySet();
	}
	
	@Override
	public String getIDString() {
		return "Restricted Identification";
	}

	@Override
	public void reset() {
		//nothing to reset
	}

}
