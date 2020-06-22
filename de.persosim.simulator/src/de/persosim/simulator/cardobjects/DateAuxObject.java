package de.persosim.simulator.cardobjects;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.protocols.RoleOid;
import de.persosim.simulator.protocols.auxVerification.AuxOid;
import de.persosim.simulator.protocols.ta.AuthenticatedAuxiliaryData;
import de.persosim.simulator.protocols.ta.Authorization;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.EffectiveAuthorizationMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.HexString;
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
					Authorization auth = authMechanism.getAuthorization(RoleOid.id_AT);
					
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

	public void setDate(TlvDataObject tlv) throws AccessDeniedException {
		if (!getLifeCycleState().isCreation()) {
			throw new AccessDeniedException("Changing DateAuxObject after creation phase is forbidden");
		}
		//only update if complete date is specified
		if (tlv.getLengthValue() != 10) return;
		
		String numString = new String(tlv.getValueField());
		String dateString = numString.substring(2);
		String yearString = dateString.substring(0,4);
		String monthString = dateString.substring(4, 6);
		String dayString = dateString.substring(6, 8);
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		if (yearString.contains(" ")) {
			cal.setTime(new Date());
		} else {
			cal.set(Calendar.YEAR, Integer.parseInt(yearString));
			if (monthString.contains(" ")) {
				cal.set(Calendar.MONTH, Calendar.DECEMBER);
				cal.set(Calendar.DAY_OF_MONTH, 31);
			} else {
				cal.set(Calendar.MONTH, Integer.parseInt(monthString)-1);
				if (dayString.contains(" ")) {
					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				} else {
					cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayString));
				}
			}
		}
		
		
		Date newDate = cal.getTime();
		
		this.date = newDate;
		
		HexString.encode(tlv.getValueField());
		tlv.getValueField();
	}

}
