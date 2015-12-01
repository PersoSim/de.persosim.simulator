package de.persosim.simulator.cardobjects;


/**
 * {@link CardObjectIdentifier} that can be used to search for
 * {@link MasterFile}s
 * 
 * @author amay
 * 
 */
public class MasterFileIdentifier implements CardObjectIdentifier {

	@Override
	public boolean matches(CardObject currentObject) {
		return (currentObject instanceof MasterFile);
	}

}
