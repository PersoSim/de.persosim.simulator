package de.persosim.simulator.platform;


import java.util.List;

import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ObjectStore;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.secstatus.SecStatus;

/**
 * This class realizes a generic means to provide a {@link Personalization} that
 * fills a CommandProcessor with content and concrete behavior.
 * 
 * @author amay
 * 
 */
public class CommandProcessor extends CommandProcessorStateMachine {

	public CommandProcessor(int id, List<Protocol> protocolList, MasterFile mf) throws AccessDeniedException {
		layerId = id;
		
		//create object tree
		ObjectStore objectStore = new ObjectStore(mf);
		objectStore.selectMasterFile();
		SecStatus securityStatus = new SecStatus();
		mf.setSecStatus(securityStatus);
		
		this.objectStore = objectStore;
		this.securityStatus = securityStatus;
		
		//register protocols
		for (Protocol curProtocol : protocolList) {
			addProtocol(curProtocol);
		}
	}

	/**
	 * @see ObjectStore#selectFileForPersonalization(CardFile)
	 */
	@Override
	public void selectFileForPersonalization(CardFile file) {
		objectStore.selectFileForPersonalization(file);
	}
}
