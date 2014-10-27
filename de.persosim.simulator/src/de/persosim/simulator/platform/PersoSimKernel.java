package de.persosim.simulator.platform;

import static de.persosim.simulator.utils.PersoSimLogger.TRACE;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.util.LinkedList;

import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.securemessaging.SecureMessaging;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.InfoSource;
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

	private LinkedList<Layer> layers;
	private Personalization perso;
	
	/**
	 * Constructor that provides the inital {@link Personalization}
	 * @param perso
	 */
	public PersoSimKernel(Personalization perso) {
		super();
		this.perso = perso;
	}
	
	/**
	 * Performs initialization of object.
	 */
	public void init() {
		log(this, "init called", TRACE);
		
		int layerId = 0;
		layers = new LinkedList<>();
		layers.add(new IoManager(layerId++));
		layers.add(new SecureMessaging(layerId++));
		CommandProcessor commandProcessor = new CommandProcessor(layerId++, perso);
		commandProcessor.init();
		layers.add(commandProcessor);
		
		log(this, "init finished", TRACE);
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
				
		//TODO AMY move atr definition to Personalization
		String atr = "3BE800008131FE00506572736F53696D";
		//                            P e r s o S i m 
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
	 * @param apdu
	 *            the APDU that was recently received
	 */
	public byte[] process(byte[] apdu) {
		
		log(this, "processing incoming APDU", TRACE);
		log(this, "incoming APDU:\n" + HexString.dump(apdu), TRACE);
		
		ProcessingData processingData = new ProcessingData();
		processingData.addUpdatePropagation(this, "initial hardware info", new HardwareCommandApduPropagation(apdu));
		
		//propagate the event all layers up
		int curLayerId = 0;
		for (; curLayerId < layers.size(); curLayerId++) {
			layers.get(curLayerId).processAscending(processingData);	
		}
		
		//propagate the event all layers down
		for (curLayerId--; curLayerId >= 0; curLayerId--) {
			layers.get(curLayerId).processDescending(processingData);
		}
		
		//extract prepared response
		byte[] retVal;
		LinkedList<UpdatePropagation> hardwareResponses = processingData.getUpdatePropagations(HardwareResponseApduPropagation.class);
		UpdatePropagation lastHardwareResponseUpdate = hardwareResponses.getLast();
		
		if (lastHardwareResponseUpdate != null && lastHardwareResponseUpdate instanceof HardwareResponseApduPropagation) {
			retVal =  ((HardwareResponseApduPropagation)lastHardwareResponseUpdate).getResponseApdu();
		} else {
			retVal = Utils.toUnsignedByteArray(Iso7816.SW_6F00_UNKNOWN);
		}
		
		log(this, "finished processing APDU");
		log(this, "outgoing APDU:\n" + HexString.dump(retVal), TRACE);
		return retVal;
		
	}
}
