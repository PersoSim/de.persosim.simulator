package de.persosim.simulator.platform;

import java.util.Arrays;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu;
import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.tlv.TlvValuePlain;
import de.persosim.simulator.utils.Utils;

/**
 * Implementation of TR-03110 Part 3 Annex E Envelope/Get Response.
 */
public class Enveloping extends Layer{
	
	enum State {
		IDLE, RECEIVING_COMMANDS, LAST_COMMAND, FIRST_RESPONSE, RESPONDING
	}

	private byte [] currentCommandApduData = null;
	private byte [] currentResponseApduData = null;
	
	int neFromLastChainElement = 0;
	
	State currentState = State.IDLE;
		
	@Override
	public void initializeForUse() {
		currentCommandApduData = new byte [0];
		currentResponseApduData = new byte [0];
	}

	@Override
	public String getLayerName() {
		return "ENVELOPING";
	}
	
	@Override
	public boolean processAscending() {
		CommandApdu command = processingData.getCommandApdu();
		if (command instanceof IsoSecureMessagingCommandApdu && ((IsoSecureMessagingCommandApdu)command).wasSecureMessaging()) {
			reset();
			return true;
		}
		if (command.getIns() == Iso7816.INS_C2_ENVELOPE && command.getP1P2() == 0) {
			return handleEnvelope();
		}
		if (command.getIns() == Iso7816.INS_C0_GET_RESPONSE && command.getP1P2() == 0) {
			return handleGetResponse();
		}
		reset();
		return true;
	}
	
	private boolean handleGetResponse() {
		if (currentState == State.FIRST_RESPONSE || currentState == State.RESPONDING) {
			int neToUse = processingData.getCommandApdu().getNe();
			
			int bytesToSend = neToUse;
			if (processingData.getCommandApdu().isNeZeroEncoded() && currentResponseApduData.length <= neToUse) {
				bytesToSend = currentResponseApduData.length;
			}
			
			byte [] portionToSend = Arrays.copyOfRange(currentResponseApduData, 0, bytesToSend);
			currentResponseApduData = Arrays.copyOfRange(currentResponseApduData, bytesToSend, currentResponseApduData.length);
			
			short sw;
			if (currentResponseApduData.length > 0) {
				currentState = State.RESPONDING;
				sw = Utils.concatenate((byte)0x61, (byte)(currentResponseApduData.length > 255 ? 0 : currentResponseApduData.length));
			} else {
				sw = Iso7816.SW_9000_NO_ERROR;
				reset();
			}
			
			if (portionToSend.length > 0) {
				processingData.updateResponseAPDU(this, "Replying with data for GetResponse", new ResponseApdu(new TlvValuePlain(portionToSend), sw));
			}
			
		} else {
			processingData.updateResponseAPDU(this, "Replaced response part of answer for GetResponse APDUs", new ResponseApdu(Iso7816.SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED));
		}
		return false;
	}

	private boolean handleEnvelope() {
		if ((currentState.equals(State.IDLE) || currentState.equals(State.RECEIVING_COMMANDS)) && (processingData.getCommandApdu().getCla() & 0x10) == 0x10) {
			currentState = State.RECEIVING_COMMANDS;
			neFromLastChainElement = processingData.getCommandApdu().getNe();
		}
		if ((currentState.equals(State.IDLE) || currentState.equals(State.RECEIVING_COMMANDS)) && (processingData.getCommandApdu().getCla() & 0x10) == 0x00){
			// last command in chain
			currentState = State.LAST_COMMAND;
			if (processingData.getCommandApdu().getLe().length != 0) {
				reset();
				processingData.updateResponseAPDU(this, "Commands that are not last in a chain are not allowed to have Le fields", new ResponseApdu(SW_6883_LAST_COMMAND_EXPECTED));
			}
		}
		
		if (currentState == State.RECEIVING_COMMANDS) {
			currentCommandApduData = Utils.concatByteArrays(currentCommandApduData, processingData.getCommandApdu().getCommandData().toByteArray());
			processingData.updateResponseAPDU(this, "C-PR successfully processed so far", new ResponseApdu(SW_9000_NO_ERROR));
			return false;
		}
		
		if (currentState == State.LAST_COMMAND) {
			currentCommandApduData = Utils.concatByteArrays(currentCommandApduData, processingData.getCommandApdu().getCommandData().toByteArray());
			currentResponseApduData = new byte [0];
			processingData.updateCommandApdu(this, "Completed chained command apdu", CommandApduFactory.createCommandApdu(currentCommandApduData, processingData.getCommandApdu()));
			return true;
		}
		
		return true;
	}

	private void reset() {
		currentCommandApduData = null;
		currentResponseApduData = null;
		currentState = State.IDLE;
	}

	@Override
	public void processDescending() {
		CommandApdu command = processingData.getCommandApdu();
		
		CommandApdu firstPredecessor = getFirstPredecessor(command);
		
		if (firstPredecessor instanceof IsoSecureMessagingCommandApdu && ((IsoSecureMessagingCommandApdu)firstPredecessor).wasSecureMessaging()) {
			return;
		}
		
		if (currentState == State.LAST_COMMAND) {
			currentResponseApduData = processingData.getResponseApdu().getData() != null ? processingData.getResponseApdu().getData().toByteArray() : new byte [0];
			currentResponseApduData = Utils.concatByteArrays(currentResponseApduData, Utils.toUnsignedByteArray(processingData.getResponseApdu().getStatusWord()));
			processingData.updateResponseAPDU(this, "Replaced response apdu, " + currentResponseApduData.length + " bytes response data", new ResponseApdu(Utils.concatenate((byte) 0x61, (byte) (currentResponseApduData.length > 255 ? 0 : (byte) currentResponseApduData.length))));
			currentState = State.FIRST_RESPONSE;
			return;
		}

		if (currentState == State.FIRST_RESPONSE) {
			currentCommandApduData = new byte [0];
			currentResponseApduData = processingData.getResponseApdu().getData().toByteArray();
		}

	}

	private CommandApdu getFirstPredecessor(CommandApdu command) {
		while(command.getPredecessor() != null) {
			command = command.getPredecessor();
		}
		return command;
	}

	
}
