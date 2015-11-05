package de.persosim.simulator.perso;

import java.util.ArrayList;
import java.util.List;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.protocols.Protocol;

public class PersonalizationImpl implements Personalization {

	protected List<Protocol> protocols = null;
	
	protected MasterFile mf = null;
	
	public PersonalizationImpl() {
		buildProtocolList();
		try {
			buildObjectTree();
		} catch (AccessDeniedException e) {
			throw new PersoCreationFailedException("Creation of personalization failed because of denied access", e);
		}
	}

	public MasterFile getMf() {
		return mf;
	}

	@Override
	public MasterFile getObjectTree() {
		return getMf();
	}

	@Override
	public List<Protocol> getProtocolList() {
		return protocols;
	}

	/**
	 * (Re)Build the protocol list (in {@link #protocols})
	 * <p/>
	 * This method is called from {@link #reset()} and should be implemented at
	 * least in all Subclasses that are used within tests that need to reset the
	 * personalization.
	 */
	protected void buildProtocolList() {
		protocols = new ArrayList<>();
	}
	
	/**
	 * (Re)Build the Object tree (in {@link #mf})
	 * <p/>
	 * This method is called from {@link #reset()} and should be implemented at
	 * least in all Subclasses that are used within tests that need to reset the
	 * personalization.
	 * @throws AccessDeniedException 
	 */
	protected void buildObjectTree() throws AccessDeniedException {
		mf = new MasterFile();
	}
	
}