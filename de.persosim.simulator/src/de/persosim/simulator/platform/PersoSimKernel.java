package de.persosim.simulator.platform;

import static de.persosim.simulator.utils.PersoSimLogger.APDU;
import static de.persosim.simulator.utils.PersoSimLogger.TRACE;
import static de.persosim.simulator.utils.PersoSimLogger.log;
import static de.persosim.simulator.utils.PersoSimLogger.logPlain;

import java.util.Collection;
import java.util.LinkedList;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.ObjectStore;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.LifeCycleChangeException;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.securemessaging.SecureMessaging;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.InfoSource;
import de.persosim.simulator.utils.PersoSimLogger;
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
	private SecStatus securityStatus;
	private ObjectStore objectStore;
	
	/**
	 * Constructor that provides the inital {@link Personalization}
	 * @throws AccessDeniedException 
	 */
	public PersoSimKernel() throws AccessDeniedException {
		super();
	}

	private void setLifeCycleStates(CardObject objectTree) {
		Collection<CardObject> children = objectTree.getChildren();
		if (children.size() > 0){
			for (CardObject cardObject : children) {
				setLifeCycleStates(cardObject);
				if (cardObject.getLifeCycleState().isPersonalizationPhase()){
					try {
						cardObject.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
					} catch (LifeCycleChangeException e) {
						PersoSimLogger.logException(this, e, PersoSimLogger.WARN);
					}	
				}
			}
		}
	}

	/**
	 * Performs initialization of object.
	 * @param perso 
	 */
	public void init(Personalization perso) {
		log(this, "init called", TRACE);
		
		this.objectStore = new ObjectStore(perso.getObjectTree());
		objectStore.selectMasterFile();
		this.securityStatus = new SecStatus();
		try {
			perso.getObjectTree().setSecStatus(securityStatus);
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		}
		setLifeCycleStates(perso.getObjectTree());
		
		int layerId = 0;
		layers = new LinkedList<>();
		layers.add(new IoManager(layerId++));
		layers.add(new SecureMessaging(layerId++));
		CommandProcessor commandProcessor = new CommandProcessor(layerId++, perso, objectStore, securityStatus);
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
		String atr = "3BE800008131FE00506572736F53696D" + "AA";
		//                            P e r s o S i m      XOR Checksum (required for T=1)
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
		
		log(this, "processing incoming APDU", TRACE);
		logPlain(PersoSimLogger.PREFIX_IN + HexString.encode(commandApduData), APDU);
		log(this, "incoming APDU:\n" + HexString.dump(commandApduData), TRACE);
		
		ProcessingData processingData = new ProcessingData();
		processingData.addUpdatePropagation(this, "initial hardware info", new HardwareCommandApduPropagation(commandApduData));
		
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
		byte[] responseApduData;
		LinkedList<UpdatePropagation> hardwareResponses = processingData.getUpdatePropagations(HardwareResponseApduPropagation.class);
		UpdatePropagation lastHardwareResponseUpdate = hardwareResponses.getLast();
		
		if (lastHardwareResponseUpdate != null && lastHardwareResponseUpdate instanceof HardwareResponseApduPropagation) {
			responseApduData =  ((HardwareResponseApduPropagation)lastHardwareResponseUpdate).getResponseApdu();
		} else {
			responseApduData = Utils.toUnsignedByteArray(Iso7816.SW_6F00_UNKNOWN+0x45);
		}
		
		log(this, "finished processing APDU");
		logPlain(PersoSimLogger.PREFIX_OUT + HexString.encode(responseApduData), APDU);
		log(this, "outgoing APDU:\n" + HexString.dump(responseApduData), TRACE);
		return responseApduData;
		
	}
}
