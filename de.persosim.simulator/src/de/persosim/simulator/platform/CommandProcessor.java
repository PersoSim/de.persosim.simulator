package de.persosim.simulator.platform;


import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.ObjectStore;
import de.persosim.simulator.perso.DefaultPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.protocols.Protocol;

/**
 * This class realizes a generic means to provide a {@link Personalization} that
 * fills a CommandProcessor with content and concrete behavior.
 * 
 * @author amay
 * 
 */
public class CommandProcessor extends CommandProcessorStateMachine {

	private Personalization perso;

	public CommandProcessor(int id, Personalization newPerso) {
		layerId = id;
		perso = newPerso;
	}

	public CommandProcessor(int id) {
		this(id, new DefaultPersonalization());
	}

	@Override
	public void init() {
		super.init();
		
		//create object tree
		objectStore.reset(perso.getObjectTree(), securityStatus);
		
		//register protocols
		for (Protocol curProtocol : perso.getProtocolList()) {
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
