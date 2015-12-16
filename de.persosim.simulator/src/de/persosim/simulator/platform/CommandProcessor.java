package de.persosim.simulator.platform;


import java.util.List;

import de.persosim.simulator.cardobjects.MasterFile;
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

	public CommandProcessor(List<Protocol> protocolList, MasterFile mf) throws AccessDeniedException {
		//initialize object tree with SecStatus
		this.masterFile = mf;
		this.securityStatus = new SecStatus();
		masterFile.setSecStatus(securityStatus);
		
		//register protocols
		for (Protocol curProtocol : protocolList) {
			addProtocol(curProtocol);
		}
	}
}
