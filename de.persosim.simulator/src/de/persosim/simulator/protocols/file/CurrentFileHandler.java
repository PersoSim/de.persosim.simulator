package de.persosim.simulator.protocols.file;

import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectUtils;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;

/**
 * This handler class unifies storage and retrieval of current selected file
 * into single methods that only rely on a {@link CardStateAccessor} to do their
 * work.
 * <p/>
 * In order to allow storing/restoring the current file as part of session
 * contexts it is stored within the SecStatus in a
 * {@link CurrentFileSecMechanism}. If such a {@link SecMechanism} is present
 * the content of it is evaluated in if it represents a valid {@link CardObject}
 * within the object tree of the given {@link CardStateAccessor} it is returned.
 * In any other event it defaults to the
 * {@link CardStateAccessor#getMasterFile()}.
 * 
 * @author amay
 *
 */
public class CurrentFileHandler {

	private CurrentFileHandler() {
		// not to be instantiated
	}

	/**
	 * Query the {@link CardStateAccessor} for the current file.
	 * 
	 * @param cardStateAccessor
	 * @return
	 */
	public static CardFile getCurrentFile(CardStateAccessor cardStateAccessor) {
		Collection<Class<? extends SecMechanism>> wantedMechanisms = new HashSet<Class<? extends SecMechanism>>();
		wantedMechanisms.add(CurrentFileSecMechanism.class);
		
		Collection<SecMechanism> currentMechanisms = cardStateAccessor.getCurrentMechanisms(SecContext.GLOBAL, wantedMechanisms);
		if (currentMechanisms.size() == 1){
			CurrentFileSecMechanism curFileSecMec = (CurrentFileSecMechanism) currentMechanisms.iterator().next();
			CardFile currentFile = curFileSecMec.getCurrentFile();
			
			if (CardObjectUtils.isObjectPartOfTree(cardStateAccessor.getMasterFile(), currentFile)) {
				return currentFile;
			}
		}
		return cardStateAccessor.getMasterFile();
	}

	/**
	 * Query the {@link CardStateAccessor} for the current file.
	 * 
	 * @param cardStateAccessor
	 * @return
	 */
	public static DedicatedFile getCurrentDedicatedFile(CardStateAccessor cardStateAccessor) {
		CardObject currentFile = getCurrentFile(cardStateAccessor);
		
		while (!(currentFile instanceof DedicatedFile)) {
			currentFile = currentFile.getParent();
		}
		
		return (DedicatedFile) (currentFile != null ? currentFile : cardStateAccessor.getMasterFile());
	}

}
