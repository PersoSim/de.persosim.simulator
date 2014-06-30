package de.persosim.simulator.secstatus;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.utils.BitField;

/**
 * This condition can be used to check for past executions of TA.
 * <p/>
 * If the {@link TerminalType} is set and differs from <code>null</code>, it
 * will be matched against the {@link TerminalType} stored in the
 * {@link TerminalAuthenticationMechanism}.
 * <p/>
 * If the {@link RelativeAuthorization} is set and differs from
 * <code>null</code>, it will be matched against the
 * {@link RelativeAuthorization} stored in the
 * {@link TerminalAuthenticationMechanism}.
 * 
 * @author mboonk
 * 
 */
@XmlRootElement
public class TaSecurityCondition implements SecCondition {

	@XmlElement
	TerminalType terminalType;
	@XmlElement
	RelativeAuthorization authorization;

	public TaSecurityCondition(){
	}
			
	public TaSecurityCondition(TerminalType terminalType,
			RelativeAuthorization authorization) {
		super();
		this.terminalType = terminalType;
		this.authorization = authorization;
	}

	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		for (SecMechanism mechanism : mechanisms) {
			if (mechanism instanceof TerminalAuthenticationMechanism) {
				TerminalAuthenticationMechanism terminalAuthenticationMechanism = (TerminalAuthenticationMechanism) mechanism;
				if (terminalType == null
						|| terminalAuthenticationMechanism.getTerminalType()
								.equals(terminalType)) {
					if (authorization == null) {
						return true;
					} else {
						BitField tempField = authorization.getRepresentation()
								.or(terminalAuthenticationMechanism
										.getEffectiveAuthorization()
										.getRepresentation());
						if (tempField.equals(terminalAuthenticationMechanism
								.getEffectiveAuthorization()
								.getRepresentation())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public Collection<Class<? extends SecMechanism>> getNeededMechanisms() {
		HashSet<Class<? extends SecMechanism>> result = new HashSet<>();
		result.add(TerminalAuthenticationMechanism.class);
		return result;
	}

}
