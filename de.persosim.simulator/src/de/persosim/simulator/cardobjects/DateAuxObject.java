package de.persosim.simulator.cardobjects;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.protocols.ta.AuthenticatedAuxiliaryData;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.utils.Utils;

@XmlRootElement
public class DateAuxObject extends AuxDataObject {
	@XmlElement
	Date date;

	public DateAuxObject(){
		
	}
	
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
		Collection<SecMechanism> currentMechanisms = securityStatus
				.getCurrentMechanisms(SecContext.APPLICATION,
						previousMechanisms);
		TerminalAuthenticationMechanism taMechanism = null;
		if (currentMechanisms.size() > 0) {
			taMechanism = (TerminalAuthenticationMechanism) currentMechanisms
					.toArray()[0];

			if (identifier.getOid().equals(TaOid.id_DateOfBirth)) {
				if (taMechanism.getTerminalType().equals(TerminalType.ST)
						|| (taMechanism.getTerminalType().equals(
								TerminalType.AT) && !taMechanism
								.getEffectiveAuthorization()
								.getAuthorization().getBit(0))) {
					throw new AccessDeniedException(
							"Age verification not allowed");
				}
				Date dateToCheck = Utils.getDate(new String(current
						.getDiscretionaryData()));
				return !date.after(dateToCheck);
			} else if (identifier.getOid().equals(TaOid.id_DateOfExpiry)) {
				Date dateToCheck = Utils.getDate(new String (current
						.getDiscretionaryData()));
				return !date.before(dateToCheck);
			}
		}

		return false;
	}

}
