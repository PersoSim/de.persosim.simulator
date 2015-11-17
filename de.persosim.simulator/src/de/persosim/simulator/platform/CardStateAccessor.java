package de.persosim.simulator.platform;

import java.util.Collection;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;

/**
 * This interface defines methods for accessing the internal state of a card.
 * The implementations wrap parts of the {@link SecStatus} in order to restrict
 * access to read only methods. It also allows access to the object tree through
 * its root element (while access conditions are checked by the objects in the
 * tree).
 * 
 * @author mboonk
 * 
 */
public interface CardStateAccessor {
	/**
	 * Return the root of the object tree in this.
	 * 
	 * @return the master file
	 */
	public MasterFile getMasterFile();

	/**
	 *	Proxy method for the {@link SecStatus#getCurrentMechanisms(SecContext, Collection)} method.
	 * 
	 * @param context
	 *            to be searched for mechanisms
	 * @param wantedMechanisms
	 *            as classes to be matched on
	 * @return all wanted SecMechanism instances in the given context
	 */
	public Collection<SecMechanism> getCurrentMechanisms(SecContext context,
			Collection<Class<? extends SecMechanism>> wantedMechanisms);
	
}
