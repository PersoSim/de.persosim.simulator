package de.persosim.simulator.protocols;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.apdumatching.ApduSpecification;
import de.persosim.simulator.apdumatching.ApduSpecificationIf;
import de.persosim.simulator.apdumatching.ExtendedTagSpecification;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.statemachine.AbstractStateMachine;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvPath;
import de.persosim.simulator.utils.InfoSource;

/**
 * Generic super class for {@link Protocol} implementations with state machine code.
 * 
 * @author amay
 * 
 */
public abstract class AbstractProtocolStateMachine extends AbstractStateMachine implements ProtocolStateMachine, Iso7816, ApduSpecificationIf, InfoSource {

	protected String protocolName;

	protected ProcessingData processingData; 
	protected boolean continueProcessing;
	
	protected HashMap<String, ApduSpecification> apdus  = new HashMap<>();

	protected ApduSpecification apduSpecification;
	protected ExtendedTagSpecification tagSpecification;
	protected TlvPath path;
	
	protected CardStateAccessor cardState;
	
	public AbstractProtocolStateMachine(String protocolName) {
		this.protocolName = protocolName;		
	}
	
	@Override
	public String getProtocolName() {
		return protocolName;
	}

	// -------------------------------------------------------
	// Methods/fields to implement {@link InfoSource} functionality
	// -------------------------------------------------------

	@Override
	public String getIDString() {
		return protocolName + " protocol";
	}
	
	// -------------------------------------------------------
	// Methods to implement {@link Protocol} functionality
	// -------------------------------------------------------

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState){
		this.cardState = cardState;
	}

	@Override
	public Collection<ApduSpecification> getApduSet() {
		return new HashSet<ApduSpecification>(apdus.values());
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity) {
		return Collections.emptySet();
	}
	
	@Override
	public void process(ProcessingData processingData) {
		this.processingData = processingData;

		if (processingData.isReportingError()) {
			/*
			 * Due to a processing error there is no guarantee that there is
			 * anything like an INS byte that could be set. Set dummy INS byte
			 * 0xFF instead;
			 */
			this.processEvent((byte) 0xFF);
		} else {
			this.processEvent(processingData.getCommandApdu().getIns());
		}
	}
	
	// -------------------------------------------------------
	// Methods to implement {@link ProtocolStateMachine} functionality
	// -------------------------------------------------------
	
	@Override
	public void registerApduSpecification(ApduSpecification apduSpecification) {
		this.apdus.put(apduSpecification.getId(), apduSpecification);
	}
	
	@Override
	public void logs(String state) {
		log(this, "State changed to " + state, DEBUG);
	}

	@Override
	public void returnResult() {
		this.continueProcessing = false;
	}
	
	@Override
	public boolean apduHasBeenProcessed() {
		return this.processingData.isProcessingFinished();
	}
	
	@Override
	public boolean isStatusWord(short sw) {
		ResponseApdu resp = this.processingData.getResponseApdu();
		if (resp != null) {
			return resp.getStatusWord() == sw;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isStatusWord_63CX_Counter() {
		ResponseApdu resp = this.processingData.getResponseApdu();
		if (resp != null) {
			short swtmp = resp.getStatusWord();
			swtmp &= (short) 0xFFF0;
			return swtmp == (short) 0x63C0;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean warningOrErrorOccurredDuringProcessing() {
		if (processingData != null) {
			ResponseApdu responseApdu = processingData.getResponseApdu();
			
			if (responseApdu != null) {
				short sw = responseApdu.getStatusWord();
				return (Iso7816Lib.isReportingError(sw) || Iso7816Lib.isReportingWarning(sw));
			}
			return false;
		}
		return false;
	}

	@Override
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
		boolean match = apduSpec.matchesFullAPDU(apdu).isMatch();
		
		if(match) {
			log(this, "received APDU matches definition of command \"" + apduId + "\"", DEBUG);
		}
		
		return match;
	}
	
	// -----------------------------------------------
	// XXX AMY remove these methods (from model)
	// -----------------------------------------------
		
	public void createNewApduSpecification(String id) {
		this.apduSpecification = new ApduSpecification(id);
	}
	
	public void createNewTagSpecification(byte tag) {
		this.tagSpecification = new ExtendedTagSpecification(tag);
	}
	
	public void createNewTagSpecification(short tag) {
		this.tagSpecification = new ExtendedTagSpecification(tag);
	}
	
	public void createNewPath() {
		this.path = new TlvPath();
	}
}
