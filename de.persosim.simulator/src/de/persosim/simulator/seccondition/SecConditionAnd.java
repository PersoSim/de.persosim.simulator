package de.persosim.simulator.seccondition;

import java.util.Collection;

import de.persosim.simulator.secstatus.SecMechanism;

/**
 * This class implements a {@link SecCondition} representing the boolean AND
 * operation on {@link SecCondition}
 * 
 * @author slutters
 * 
 */
public final class SecConditionAnd extends SecConditionOperator {
	
	public SecConditionAnd(SecCondition... secConditions) {
		super(secConditions);
	}
	
	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		for(SecCondition secCondition:secConditions) {
			if(!secCondition.check(mechanisms)) {
				return false;
			}
		}
		return true;
	}

}
