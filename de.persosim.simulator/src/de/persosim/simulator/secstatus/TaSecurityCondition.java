package de.persosim.simulator.secstatus;

import java.util.Collection;
import java.util.HashSet;

import de.persosim.simulator.protocols.ta.Authorization;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TaOid;
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
public class TaSecurityCondition implements SecCondition {

	TerminalType terminalType;
	Authorization authorization;

	public TaSecurityCondition(){
	}
			
	public TaSecurityCondition(TerminalType terminalType,
			Authorization authorization) {
		super();
		this.terminalType = terminalType;
		this.authorization = authorization;
	}

	@Override
	public boolean check(Collection<SecMechanism> mechanisms) {
		TerminalAuthenticationMechanism terminalAuthenticationMechanism = null;
		AuthorizationMechanism authorizationMechanism = null;
		
		for (SecMechanism mechanism : mechanisms) {
			if (mechanism instanceof TerminalAuthenticationMechanism) {
				terminalAuthenticationMechanism = (TerminalAuthenticationMechanism) mechanism;
			}
			if (mechanism instanceof AuthorizationMechanism) {
				authorizationMechanism = (AuthorizationMechanism) mechanism;
			}
		}
		
		if(terminalAuthenticationMechanism != null) {
			if (terminalType == null || terminalAuthenticationMechanism.getTerminalType().equals(terminalType)) {
				if (authorization == null) {
					return true;
				} else {
					if(authorizationMechanism != null) {
						Authorization auth = authorizationMechanism.getAuthorization(TaOid.id_AT);
						BitField tempField = authorization.getAuthorization().or(auth.getAuthorization());
						if (tempField.equals(auth.getAuthorization())) {
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
		result.add(AuthorizationMechanism.class);
		return result;
	}

}
