package de.persosim.simulator.platform;

import static org.globaltester.logging.BasicLogger.TRACE;
import static org.globaltester.logging.BasicLogger.log;
import static org.globaltester.logging.BasicLogger.logException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.ProcessingException;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.ProtocolStateMachine;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusMechanismUpdatePropagation;
import de.persosim.simulator.statemachine.AbstractStateMachine;
import de.persosim.simulator.statemachine.StateMachine;

/**
 * This class implements the processing of CommandApdus. It orchestrates
 * registered protocols and mediates between those protocols and the card
 * internal state, such as {@link SecStatus} and the object tree.
 * 
 * @author amay
 * 
 */
public abstract class AbstractCommandProcessor extends Layer implements
		CardStateAccessor, StateMachine {
	
	public static final String COMMANDPROCESSOR = "CommandProcessor";
	
	// -------------------------------------------------------
	// Methods/fields to implement {@link Layer} functionality
	// -------------------------------------------------------

	@Override
	public String getLayerName() {
		return COMMANDPROCESSOR;
	}

	@Override
	public void processAscending() {
		log(this, "will now begin processing of ascending APDU", TRACE);

		try {
			securityStatus.updateSecStatus(processingData);
			
			//process the event
			int event = 0xFF;
			if (processingData.getCommandApdu() != null) {
				event = processingData.getCommandApdu().getIns();
			}
			this.processEvent(event);
			
			//convert internal SW if required
			if (processingData.getResponseApdu() != null && PlatformUtil.is4xxxStatusWord(processingData.getResponseApdu().getStatusWord())){
				log(this, "APDU contents could not be processed by any protocol");

				ResponseApdu rApdu = new ResponseApdu(PlatformUtil.convert4xxxTo6xxxStatusWord(processingData.getResponseApdu().getStatusWord()));
				this.processingData.updateResponseAPDU(this,
						"No protocol was able to process the APDU contents", rApdu);
			}

			securityStatus.updateSecStatus(processingData);

			log(this, "successfully processed ascending APDU", TRACE);
		} catch(ProcessingException e) {
			ResponseApdu resp = new ResponseApdu(e.getStatusWord());
			processingData.updateResponseAPDU(this, e.getMessage(), resp);
			return;
		} catch (Exception e) {
			logException(this, e, TRACE);
			ResponseApdu resp = new ResponseApdu(Iso7816.SW_6FFF_IMPLEMENTATION_ERROR);
			this.processingData.updateResponseAPDU(this, "Generic error handling", resp);
			return;
		}
	}

	@Override
	public void powerOn() {
		super.powerOn();

		log(this, "powerOn, remove all protocols from stack", TRACE);
		setStackPointerToBottom();
		removeCurrentProtocolAndAboveFromStack();
		reset();
		
		log(this, "powerOn, reset SecStatus", TRACE);
		securityStatus.reset();

	}

	// ---------------------------------------------------
	// methods/fields handling/representing the card state
	// ---------------------------------------------------

	protected transient SecStatus securityStatus;
	protected MasterFile masterFile;

	/**
	 * Adds a new protocol to the list of available protocols. The new protocol
	 * is added at the end of the list.
	 * <p/>
	 * This method does not provide any consistency checking. It is in the
	 * responsibility of the caller to make sure that only valid protocols are
	 * added (e.g. that duplicate protocols are only added if the given protocol
	 * supports this behavior).
	 * 
	 * @param newProtocol
	 *            protocol to add, this is expected to be initialized and ready
	 *            to use
	 */
	public void addProtocol(Protocol newProtocol) {
		protocols.add(newProtocol);
	}

	// --------------------------------------------------------
	// methods implementing {@link CardStateAccessor} interface
	// --------------------------------------------------------
	@Override
	public Collection<SecMechanism> getCurrentMechanisms(SecContext context,
			Collection<Class<? extends SecMechanism>> wantedMechanisms) {
		return securityStatus.getCurrentMechanisms(context, wantedMechanisms);
	}

	@Override
	public MasterFile getMasterFile() {
		return masterFile;
	}

	// ---------------------------------------------------
	// Methods/fields used from within state machine code.
	// ---------------------------------------------------
	/**
	 * List of available protocols
	 */
	protected List<Protocol> protocols = new ArrayList<>();
	
	private transient Protocol currentlyActiveProtocol;
	
	
	
	/**
	 * stackPointer is a pointer pointing at an element of protocolStack, i.e.
	 * the currently active/unfinished/interrupted protocols
	 */
	protected transient int stackPointer;

	/**
	 * the stack containing all active/unfinished/interrupted protocols
	 */
	protected transient ArrayList<Protocol> protocolStack;

	/**
	 * protocolPointer is a pointer pointing at an element of protocols, i.e.
	 * the list of known/supported protocols
	 */
	protected transient int protocolPointer;

	public void setStackPointerToBottom() {
		this.stackPointer = 0;
	}

	public boolean stackPointerIsNull() {
		if ((this.protocolStack.size() == 0)
				|| (this.protocolStack.size() <= this.stackPointer)) {
			return true;
		}

		return false;
	}

	public void makeStackPointerCurrentlyActiveProtocol() {
		setCurrentlyActiveProtocol(this.protocolStack.get(this.stackPointer));
		
		log(this, "currently active protocol is now: "
				+ getCurrentlyActiveProtocol().getProtocolName());
	}
	
	protected Protocol getCurrentlyActiveProtocol() {
		return currentlyActiveProtocol;
	}
	
	protected void setCurrentlyActiveProtocol(Protocol nextActiveProtocol) {
		currentlyActiveProtocol = nextActiveProtocol;
		
		ProtocolMechanism protocolMechanism;
		
		if(nextActiveProtocol == null) {
			// this effectively deletes the security mechanism
			protocolMechanism = null;
		} else{
			protocolMechanism = new ProtocolMechanism(currentlyActiveProtocol.getClass());
		}
		
		SecStatusMechanismUpdatePropagation updatePropagation = new SecStatusMechanismUpdatePropagation(SecContext.APPLICATION, protocolMechanism);
		
		securityStatus.updateMechanisms(updatePropagation);
	}

	public void incrementStackPointer() {
		this.stackPointer++;
	}

	public void currentProtocolProcess() {
		log(this, "protocol chosen for processing is: " + getCurrentlyActiveProtocol().getProtocolName()); 
		getCurrentlyActiveProtocol().process(processingData);
	}

	/**
	 * Check whether the currently active protocol has requested its removal
	 * from the stack. This request should be found in
	 * {@link Layer#processingData} as ProtocolUpdate
	 * 
	 * @return true iff a ProtocolUpdate is found in processingData that
	 *         requests removal of the current protocol.
	 */
	public boolean isProtocolFinished() {
		LinkedList<UpdatePropagation> protocolUpdates = processingData
				.getUpdatePropagations(ProtocolUpdate.class);

		try {
			UpdatePropagation lastProtocolUpdate = protocolUpdates.getLast();

			if (lastProtocolUpdate != null
					&& lastProtocolUpdate instanceof ProtocolUpdate) {
				return ((ProtocolUpdate) lastProtocolUpdate).isFinished();
			} else {
				return false;
			}
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public void removeCurrentProtocolAndAboveFromStack() {
		log(this, "started cleaning up stack - will commence top down", TRACE);
		for (int i = this.protocolStack.size() - 1; i >= this.stackPointer; i--) {
			log(this, "removing protocol "
					+ this.protocolStack.get(i).getProtocolName() + " from stack",
					TRACE);
			this.protocolStack.remove(i);
		}
		log(this, "finished cleaning up stack", TRACE);
	}

	/**
	 * Method used from within state machine code.
	 * <p/>
	 * Add the currently active protocol (as defined by {@link #protocolPointer}
	 * ) to the {@link #protocolStack}
	 */
	public void addProtocolAtProtocolPointerToStack() {
		Protocol protocol = protocols.get(protocolPointer);
		log(this,
				"protocol put to top of stack is "
						+ protocol.getProtocolName());
		this.protocolStack.add(protocol);
	}
	
	/**
	 * 
	 */
	public boolean protocolAtPointerWantsToGetOnStack() {
		Protocol protocol = protocols.get(protocolPointer);
		return protocol == null ? false : protocol.isMoveToStackRequested();
	}

	/**
	 * Method used from within state machine code.
	 * <p/>
	 * Calls {@link AbstractStateMachine#reset() reset} method for the protocol
	 * in the list of known {@link #protocols} as specified by
	 * {@link #protocolPointer}.
	 */
	public void resetProtocolAtProtocolPointer() {
		protocols.get(protocolPointer).reset();
	}

	/**
	 * Method used from within state machine code.
	 * <p/>
	 * Resets {@link #protocolPointer} to point at first protocol in the
	 * {@link #protocols protocol list}.
	 */
	public void setProtocolPointerToFirstElementOfProtocolList() {
		protocolPointer = 0;
	}

	/**
	 * Method used from within state machine code.
	 * <p/>
	 * Increments {@link #protocolPointer} to point at the next protocol in the
	 * {@link #protocols protocol list}.
	 */
	public void setProtocolPointerToNextElementOfProtocolList() {
		protocolPointer++;
	}

	/**
	 * Method used from within state machine code.
	 * <p/>
	 * Returns whether all protocols contained in the {@link #protocols protocol
	 * list} have been processed, i.e. there is nor further protocol available
	 * for processing.
	 */
	public boolean allProtocolsOfProtocolListProcessed() {
		return protocolPointer >= protocols.size();
	}

	/**
	 * Method used from within state machine code.
	 * <p/>
	 * Promotes the protocol specified by the {@link #protocolPointer} within
	 * the {@link #protocols protocol list} to be the currently active
	 * protocol.
	 */
	public void makeProtocolAtProtocolPointerCurrentlyActiveProtocol() {
		setCurrentlyActiveProtocol(protocols.get(this.protocolPointer));
	}

	/**
	 * Method used from within state machine code.
	 * <p/>
	 * Set the status word for unsupported/unprocessed commands. Method is
	 * called if no protocol is able or willing to process a command.
	 */
	public void setStatusWordForUnsupportedCommand() {
		if (processingData.getResponseApdu() != null && PlatformUtil.is4xxxStatusWord(processingData.getResponseApdu().getStatusWord())){
			log(this, "APDU contents could not be processed by any protocol");

			ResponseApdu rApdu = new ResponseApdu(PlatformUtil.convert4xxxTo6xxxStatusWord(processingData.getResponseApdu().getStatusWord()));
			this.processingData.updateResponseAPDU(this,
					"No protocol was able to process the APDU contents", rApdu);
		} else {
			log(this, "APDU not supported");
			ResponseApdu rApdu = new ResponseApdu(Iso7816.SW_6D00_INS_NOT_SUPPORTED);
			this.processingData.updateResponseAPDU(this,
					"No protocol was able to process the APDU", rApdu);

			/*
			 * TODO Test cases for which the current implementation yields a sw which is
			 * suitable/acceptable for passing the test case but does not describe actual circumstances
			 * appropriately. This list does not claim to be exhaustive and may be extended.
			 * 
			 * ISO7816_P_04: invalid class byte, accepts: checking error, appropriate: 6E00
			 * ISO7816_P_05: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_07: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_10: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_11: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_12: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_13: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_15: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_16: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_17: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_18: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_19: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_20: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_21: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_22: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_31: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_32: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_33: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_34: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_37: invalid class byte, accepts: checking error, appropriate: 6E00
			 * ISO7816_P_38: invalid class byte, accepts: checking error, appropriate: 6E00
			 * ISO7816_P_39: invalid class byte, accepts: checking error, appropriate: 6E00
			 * ISO7816_P_40: invalid class byte, accepts: checking error, appropriate: 6E00
			 * ISO7816_P_62: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_64: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_69: invalid tag,        accepts: checking error, appropriate: 6A80
			 * ISO7816_P_77: invalid tag,        accepts: checking error, appropriate: 6A80
			 * 
			 * Sketched _possible_ solution:
			 * Have protocols report _all_ APDU specifications and perform fuzzy matching to find
			 * most appropriate sw.
			 */
		}
	}

	// -----------------------
	// Control of StateMachine
	// -----------------------
	private transient boolean initialized = false;
	protected transient boolean continueProcessing;

	@Override
	public void init() {
		protocolStack = new ArrayList<>();
		stackPointer = 0;
		reset();
		initialized = true;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}
	
	@Override
	public void initializeForUse() {
		securityStatus = new SecStatus();
		
		try {
			getObjectTree().setSecStatus(securityStatus);
		} catch (AccessDeniedException e) {
			throw new ProcessingException(SW_6FFF_IMPLEMENTATION_ERROR, "something went wrong reinitializing the command processor");
		}
		
		for(Protocol protocol:protocols) {
			protocol.setCardStateAccessor(this);
		}
		
		PersonalizationHelper.setLifeCycleStates(masterFile);
		
		init();
	}

	@Override
	public void reset() {
		reInitialize();
		processEvent((byte) 0xFF); // handle the first transition
	}

	/**
	 * @see ProtocolStateMachine#returnResult()
	 */
	public void returnResult() {
		this.continueProcessing = false;
	}

	/**
	 * @see ProtocolStateMachine#apduHasBeenProcessed()
	 */
	public boolean apduHasBeenProcessed() {
		return this.processingData.isProcessingFinished();
	}

	/**
	 * Returns the root element of the object tree.
	 */
	public MasterFile getObjectTree(){
		return masterFile;
	}

	/**
	 * Returns the list of activated protocols.
	 * <p/>
	 * The protocols contained in this List are required to be already
	 * initialized and ready to be added to a {@link CardStateAccessor} and
	 * used afterwards
	 * 
	 * @return
	 */
	public List<Protocol> getProtocolList(){
		return protocols;
	}

}
