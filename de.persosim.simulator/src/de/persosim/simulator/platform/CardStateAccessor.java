package de.persosim.simulator.platform;

import java.io.FileNotFoundException;
import java.util.Collection;

import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ObjectStore;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;

/**
 * This interface defines methods for accessing the internal state of a card.
 * The implementations wrap parts of the {@link ObjectStore} and
 * {@link SecStatus} classes to provide reading access to existing
 * {@link CardObject}s and permit the creation of new objects in a generic way.
 * 
 * @author mboonk
 * 
 */
public interface CardStateAccessor {
	/**
	 * Proxy method for the
	 * {@link ObjectStore#selectFile(CardObjectIdentifier, Scope)} method.
	 * 
	 * @param id
	 * @param scope
	 * @return
	 * @throws FileNotFoundException
	 */
	public CardFile selectFile(CardObjectIdentifier id, Scope scope)
			throws FileNotFoundException;

	/**
	 * Proxy method for the {@link ObjectStore#selectMasterFile()} method.
	 * 
	 * @return the selected file object
	 */
	public MasterFile selectMasterFile();

	/**
	 * Proxy method for the {@link ObjectStore#getCurrentFile()} method.
	 * 
	 * @return the last successfully selected file
	 */
	public CardObject getCurrentFile();

	/**
	 * Proxy method for the {@link ObjectStore#selectCachedFile()} method.
	 * 
	 * @throws FileNotFoundException
	 */
	public void selectFile() throws FileNotFoundException;

	/**
	 * Proxy method for the
	 * {@link ObjectStore#getObject(CardObjectIdentifier, Scope)}
	 * 
	 * @param shortFileIdentifierImpl
	 * @param fromDf
	 * @return
	 */
	//XXX this method is used quite often to get the MasterFile using a MasterFileIdentifier, maybe provide a dedicated method for this usecase.
	public CardObject getObject(CardObjectIdentifier id, Scope scope);

	/**
	 * Proxy method for the
	 * {@link ObjectStore#getObjectsWithSameId(CardObjectIdentifier, Scope)}
	 * 
	 * @param shortFileIdentifierImpl
	 * @param fromDf
	 * @return
	 */
	//XXX this method is used quite often to get the MasterFile using a MasterFileIdentifier, maybe provide a dedicated method for this usecase.
	public Collection<CardObject> getObjectsWithSameId(CardObjectIdentifier id, Scope scope);
	
	/**
	 * Proxy method for the {@link ObjectStore#selectFileForPersonalization(CardFile)} method.
	 * 
	 * @param file to select
	 */
	public abstract void selectFileForPersonalization(CardFile file);
	
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
