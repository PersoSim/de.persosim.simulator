package de.persosim.simulator.cardobjects;

/**
 * Identifier for {@link DateTimeCardObject}s.
 * 
 * @author mboonk
 *
 */
public class DateTimeObjectIdentifier extends AbstractCardObjectIdentifier {

	@Override
	public boolean matches(CardObjectIdentifier obj) {
		if (obj instanceof DateTimeObjectIdentifier){
			return true;
		}
		return false;
	}

}
