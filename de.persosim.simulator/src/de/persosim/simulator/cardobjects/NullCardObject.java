package de.persosim.simulator.cardobjects;

import java.util.Collection;
import java.util.Collections;

/**
 * CardObject that does neither represent an existing CardObject nor does
 * anything specifically useful but intelligently returning "nothing". Mainly
 * intended to be returned instead of null, when no object is available or
 * accessible.
 * 
 * @author amay
 * 
 */
public class NullCardObject extends AbstractCardObject {

	@Override
	public CardObject getParent() {
		return null;
	}

	@Override
	public Collection<CardObject> getChildren() {
		return Collections.emptySet();
	}

	@Override
	public Iso7816LifeCycleState getLifeCycleState() {
		return Iso7816LifeCycleState.UNDEFINED;
	}

	@Override
	public void updateLifeCycleState(Iso7816LifeCycleState state) {
		
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		return Collections.emptySet();
	}

}
