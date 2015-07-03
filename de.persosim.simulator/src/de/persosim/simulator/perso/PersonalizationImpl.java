package de.persosim.simulator.perso;

import java.util.ArrayList;
import java.util.List;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.protocols.Protocol;

public class PersonalizationImpl implements Personalization {

	protected List<Protocol> protocols = null;
	
	protected MasterFile mf = null;
	
	public PersonalizationImpl() {
		buildProtocolList();
		buildObjectTree();
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
	 */
	protected void buildObjectTree() {
		mf = new MasterFile();
	}
	
}