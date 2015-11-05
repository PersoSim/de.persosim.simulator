package de.persosim.simulator.perso;

import java.util.ArrayList;
import java.util.List;

import de.persosim.simulator.platform.Layer;

public class PersonalizationImpl implements Personalization {
	
	protected List<Layer> layers = null;
	
	public PersonalizationImpl() {
		buildLayerList();
	}

	@Override
	public List<Layer> getLayerList() {
		return layers;
	}
	
	/**
	 * (Re)Build the layer list (in {@link #protocols})
	 * <p/>
	 * This method is called from {@link #reset()} and should be implemented at
	 * least in all Subclasses that are used within tests that need to reset the
	 * personalization.
	 */
	protected void buildLayerList() {
		layers = new ArrayList<>();
	}

	@Override
	public void initialize() {
		// nothing to do here
	}
	
}