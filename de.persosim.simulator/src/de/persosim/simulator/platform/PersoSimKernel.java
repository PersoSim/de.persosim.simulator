package de.persosim.simulator.platform;

import static org.globaltester.logging.BasicLogger.log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.globaltester.logging.InfoSource;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.simulator.Simulator;
import org.globaltester.simulator.SimulatorEventListener;
import org.globaltester.simulator.event.CommandApduEvent;
import org.globaltester.simulator.event.ResponseApduEvent;
import org.globaltester.simulator.event.SimulatorEvent;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * Main entry point for the persoSim simulation. Handles instantiation of
 * platform layers and coordinates propagation of processing data through the
 * different layers.
 * 
 * @author amay
 * 
 */
public class PersoSimKernel implements InfoSource {

	private List<Layer> layers;
	
	private HashSet<SimulatorEventListener> simEventListeners = new HashSet<>();
	
	/**
	 * Constructor that provides the inital {@link Personalization}
	 * @throws AccessDeniedException 
	 */
	public PersoSimKernel() throws AccessDeniedException {
		super();
	}

	/**
	 * Performs initialization of object.
	 * @param perso 
	 */
	public void init(Personalization perso) {
		log(this, "init called", LogLevel.TRACE);
		
		perso.initialize();
		layers = perso.getLayerList();
		
		log(this, "init finished", LogLevel.TRACE);
	}

	public byte[] powerOff() {
		//power off all Layers from top to bottom
		for (int curLayerId = layers.size()-1; curLayerId >= 0; curLayerId--) {
			layers.get(curLayerId).powerOff();	
		}
		
		return Utils.toUnsignedByteArray(Iso7816.SW_9000_NO_ERROR);
	}

	public byte[] powerOn() {
		//power on all Layers from bottom
		for (int curLayerId = 0; curLayerId < layers.size(); curLayerId++) {
			layers.get(curLayerId).powerOn();	
		}
				
		//maybe move atr definition to Personalization
		String atr = "3BE900008131FE00" + "FF" + "506572736F53696D" + "54";
		//                                 Prop.  P e r s o S i m      XOR Checksum (required for T=1)
		return HexString.toByteArray(atr);
	}

	public byte[] reset() {
		// currently we support only cold reset as powerOff() followed by powerOn()
		// if a warm reset is needed this neds to be propagated accordingly 
		powerOff();
		return powerOn();
	}

	@Override
	public String getIDString() {
		return "PersoSimKernel";
	}
	
	/**
	 * This method represents the simulator's actual core. APDUs and
	 * accompanying ProcessingData-Objects are propagated through all available
	 * layers from bottom to the top and back down again.
	 * 
	 * @param commandApduData
	 *            the APDU that was recently received
	 */
	public byte[] process(byte[] commandApduData) {
		
		log(this, "Processing incoming APDU");
		log(this, "Processing APDU:\n" + HexString.dump(commandApduData), LogLevel.TRACE);
		notifyListeners(new CommandApduEvent(commandApduData));
		
		ProcessingData processingData = new ProcessingData();
		processingData.addAllEventListener(simEventListeners);
		processingData.addUpdatePropagation(this, "initial hardware info", new HardwareCommandApduPropagation(commandApduData));
		
		//propagate the event all layers up
		int curLayerId = 0;
		Layer currentLayer;
		for (; curLayerId < layers.size(); curLayerId++) {
			currentLayer = layers.get(curLayerId);
			currentLayer.processAscending(processingData);
		}
		
		//propagate the event all layers down
		for (curLayerId--; curLayerId >= 0; curLayerId--) {
			currentLayer = layers.get(curLayerId);
			currentLayer.processDescending(processingData);
		}
		
		//extract prepared response
		byte[] responseApduData;
		LinkedList<UpdatePropagation> hardwareResponses = processingData.getUpdatePropagations(HardwareResponseApduPropagation.class);
		UpdatePropagation lastHardwareResponseUpdate = hardwareResponses.getLast();
		
		if (lastHardwareResponseUpdate != null && lastHardwareResponseUpdate instanceof HardwareResponseApduPropagation) {
			responseApduData =  ((HardwareResponseApduPropagation)lastHardwareResponseUpdate).getResponseApdu();
		} else {
			responseApduData = Utils.toUnsignedByteArray(Iso7816.SW_6F00_UNKNOWN+0x45);
		}
		
		log(this, "APDU processing finished");
		log(this, "Response APDU:\n" + HexString.dump(responseApduData), LogLevel.TRACE);
		notifyListeners(new ResponseApduEvent(responseApduData));
		return responseApduData;
		
	}

	private void notifyListeners(SimulatorEvent simEvent) {
		for (SimulatorEventListener curListener : simEventListeners) {
			curListener.notifySimulatorEvent(simEvent);
		}
	}

	/**
	 * @see Simulator#addEventListener(SimulatorEventListener...)
	 */
	public void addEventListener(SimulatorEventListener... newListeners) {
		simEventListeners.addAll(Arrays.asList(newListeners));
	}

	/**
	 * @see Simulator#removeEventListener(SimulatorEventListener)
	 */
	public void removeEventListener(SimulatorEventListener oldListener) {
		simEventListeners.remove(oldListener);
	}
}
