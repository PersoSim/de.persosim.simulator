package de.persosim.simulator.secstatus;

import java.util.Collection;

/**
 * This class implements a {@link SecCondition} representing the boolean NOT
 * operation on {@link SecCondition}
 * 
 * @author amay
 * 
 */
public final class SecConditionNot extends SecConditionOperator {
	
	public SecConditionNot(SecCondition secCondition) {
		super(secCondition);
	}
	
	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		if (secConditions.length != 1) return false;
		return !secConditions[0].check(mechanisms);
	}

}
