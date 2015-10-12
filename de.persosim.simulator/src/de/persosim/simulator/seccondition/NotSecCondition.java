package de.persosim.simulator.seccondition;

import java.util.Collection;

import de.persosim.simulator.secstatus.SecMechanism;

/**
 * This class implements a {@link SecCondition} representing the boolean NOT
 * operation on {@link SecCondition}
 * 
 * @author amay
 * 
 */
public final class NotSecCondition extends OperatorSecCondition {
	
	public NotSecCondition(SecCondition secCondition) {
		super(secCondition);
	}
	
	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		if (secConditions.length != 1) return false;
		return !secConditions[0].check(mechanisms);
	}

}
