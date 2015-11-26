package de.persosim.simulator.cardobjects;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.protocols.auxVerification.AuxOid;
import de.persosim.simulator.protocols.ta.AuthenticatedAuxiliaryData;
import de.persosim.simulator.protocols.ta.Authorization;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.EffectiveAuthorizationMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.utils.Utils;

public class DateAuxObject extends AuxDataObject {
	Date date;

	public DateAuxObject(OidIdentifier identifier, Date date) {
		super(identifier);
		this.date = date;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	@Override
	public boolean verify(AuthenticatedAuxiliaryData current)
			throws AccessDeniedException {
		// get necessary information stored in TA
		//XXX access conditions should be stored separately and evaluated in a more generic (identifier independent) way
		Collection<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(TerminalAuthenticationMechanism.class);
		previousMechanisms.add(EffectiveAuthorizationMechanism.class);
		Collection<SecMechanism> currentMechanisms = securityStatus.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		TerminalAuthenticationMechanism taMechanism = null;
		EffectiveAuthorizationMechanism authMechanism  = null;
		
		if (currentMechanisms.size() >= 2) {
			for(SecMechanism secmechanism:currentMechanisms) {
				if(secmechanism instanceof TerminalAuthenticationMechanism) {
					taMechanism = (TerminalAuthenticationMechanism) secmechanism;
				}
				
				if(secmechanism instanceof EffectiveAuthorizationMechanism) {
					authMechanism = (EffectiveAuthorizationMechanism) secmechanism;
				}
			}
			
			if((taMechanism == null) || (authMechanism == null)) {
				throw new AccessDeniedException("Age verification not allowed");
			}
			
			if (identifier.getOid().equals(AuxOid.id_DateOfBirth)) {
				
				if (taMechanism.getTerminalType().equals(TerminalType.ST)) {
					throw new AccessDeniedException("Age verification not allowed");
				}
				
				if (taMechanism.getTerminalType().equals(TerminalType.AT)) {
					Authorization auth = authMechanism.getAuthorization(TaOid.id_AT);
					
					if(!auth.getAuthorization().getBit(0)) {
							throw new AccessDeniedException("Age verification not allowed");
					}
				}
				
				Date dateToCheck = Utils.getDate(new String(current.getDiscretionaryData()));
				
				return !date.after(dateToCheck);
			} else if (identifier.getOid().equals(AuxOid.id_DateOfExpiry)) {
				Date dateToCheck = Utils.getDate(new String (current.getDiscretionaryData()));
				return !date.before(dateToCheck);
			}
		}

		return false;
	}

}
