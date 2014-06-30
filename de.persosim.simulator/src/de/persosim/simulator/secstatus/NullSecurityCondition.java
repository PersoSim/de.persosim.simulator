package de.persosim.simulator.secstatus;

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This {@link SecCondition} is used to implement unprotected files. It will
 * evaluate all check attempts to true.
 * 
 * @author mboonk
 * 
 */
@XmlRootElement
public class NullSecurityCondition implements SecCondition {

	@Override
	public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
		return Collections.emptyList();
	}

	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		return true;
	}

}
