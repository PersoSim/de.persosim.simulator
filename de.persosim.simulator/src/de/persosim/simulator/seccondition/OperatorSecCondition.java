package de.persosim.simulator.seccondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.persosim.simulator.secstatus.SecMechanism;

/**
 * This class implements a {@link SecCondition} representing a boolean operation
 * on {@link SecCondition}
 * 
 * @author slutters
 * 
 */
public abstract class OperatorSecCondition implements SecCondition {
	
	protected SecCondition[] secConditions;
	
	public OperatorSecCondition(SecCondition... secConditions) {
		this.secConditions = secConditions;
	}

	@Override
	public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
		Collection<Class<? extends SecMechanism>> effectiveMechanisms = new ArrayList<>();
		Collection<Class<? extends SecMechanism>> additionalMechanisms;
		
		for(SecCondition secCondition:secConditions) {
			additionalMechanisms = secCondition.getNeededMechanisms();
			
			//don't add duplicates
			for(Class<? extends SecMechanism> secMechanism:additionalMechanisms) {
				if(!effectiveMechanisms.contains(secMechanism)) {
					effectiveMechanisms.add(secMechanism);
				}
			}
			
		}
		
		return effectiveMechanisms;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + Arrays.toString(secConditions) + "]";
	}

}
