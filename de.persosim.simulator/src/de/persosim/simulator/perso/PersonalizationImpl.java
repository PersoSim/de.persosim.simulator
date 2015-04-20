package de.persosim.simulator.perso;

import java.util.ArrayList;
import java.util.List;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.protocols.Protocol;

public class PersonalizationImpl implements Personalization {

	protected List<Protocol> protocols = null;
	
	protected MasterFile mf = null;
		
	public List<Protocol> getProtocols() {
		if (protocols == null) reset();
		return protocols;
	}

	public MasterFile getMf() {
		if (mf == null) reset();
		return mf;
	}

	@Override
	public MasterFile getObjectTree() {
		return getMf();
	}

	@Override
	public List<Protocol> getProtocolList() {
		return getProtocols();
	}

	@Override
	public void reset() {
		buildProtocolList();
		buildObjectTree();
	}

	/**
	 * (Re)Build the protocol list (in {@link #protocols})
	 * <p/>
	 * This method is called from {@link #reset()} and should be implemented at
	 * least in all Subclasses that are used within tests that need to reset the
	 * personalization.
	 */
	protected void buildProtocolList() {
		// initialize empty protocol list but do not overwrite a deserialized perso
		if (protocols == null) {
			protocols = new ArrayList<>();	
		}
	}
	
	/**
	 * (Re)Build the Object tree (in {@link #mf})
	 * <p/>
	 * This method is called from {@link #reset()} and should be implemented at
	 * least in all Subclasses that are used within tests that need to reset the
	 * personalization.
	 */
	protected void buildObjectTree() {
		// initialize empty protocol list but do not overwrite a deserialized perso
		if (mf == null) {
			mf = new MasterFile();
		}
	}
	
}