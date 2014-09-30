package de.persosim.simulator.protocols;

import java.util.Collection;

import de.persosim.simulator.apdumatching.ApduSpecification;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ObjectStore;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.tlv.TlvDataObject;

/**
 * Classes implementing this interface can be used to extend the
 * {@link CommandProcessor} with the support of a given protocol.
 * <p/>
 * {@link CommandProcessor} decides which protocol to use basically by
 * performing a matching on the list of supported APDUs returned by
 * {@link #getApduSet()}. If a {@link Protocol} is capable of handling a given
 * APDU the APDU is passed to {@link #process(ProcessingData)} and if this call
 * returns with a successful SW set in the {@link ProcessingData} the protocol
 * is loaded as active protocol on the stack.
 * 
 * @author amay
 * 
 */
public interface Protocol {

	/**
	 * @return the protocols name in human readable form.
	 * 
	 */
	public abstract String getProtocolName();

	/**
	 * As some may protocols require access to the cards internal state (in
	 * terms of the {@link SecStatus} and {@link ObjectStore}) users of
	 * Protocols are required to call this method before usage of the protocol
	 * in order to provide an instance of {@link CardStateAccessor} that may be
	 * cached by the protocol if known to be required later.
	 * 
	 * @param cardState accessor object for {@link SecStatus} and {@link ObjectStore}
	 */
	public abstract void setCardStateAccessor(CardStateAccessor cardState);

	/**
	 * Return all Collection of SecInfos according to the current configuration
	 * of the Protocol. These can be used by the caller to create default
	 * implementations of EF.CardAccess, EF.CardSecurity or DG14.
	 * 
	 * @param publicity defines which SecInfos should be included in the returned Collection
	 * @param mf the masterfile that contains the objecttree
	 * @return set of SecurityInfos. May be an immutable collection.
	 */
	public abstract Collection<? extends TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf);
	
	public enum SecInfoPublicity {
	    PUBLIC,
	    AUTHENTICATED,
	    PRIVILEGED
	  }


	/**
	 * Implements handling of APDUs.
	 * <p/>
	 * If the {@link CommandProcessor} decides to forward an APDU to the
	 * protocol this is done by calling this method. The protocol is expected to
	 * handle the APDU and provide a corresponding response in the
	 * processingData. If the protocol is unable to handle the APDU no
	 * interaction with the processing data is required at all.
	 * 
	 * @param processingData
	 */
	public abstract void process(ProcessingData processingData);

	/**
	 * Returns collection of supported APDUs. The protocol is expected to be
	 * able to handle each APDU that matches one of these specifications.
	 * 
	 * 
	 * @return collection of supported {@link ApduSpecification}s. The returned
	 *         {@link Collection} shall not be altered by the calling entity as
	 *         it may be immutable.
	 */
	public abstract Collection<ApduSpecification> getApduSet();

	
	/**
	 * Reset the {@link Protocol} to it's initial configuration.
	 * <p/>
	 * After this method is called the object shall behave exactly like a newly
	 * created and initialized object (e.g. object created via constructor).
	 */
	public abstract void reset();

	/**
	 * This allows a protocol to be moved to the protocol stack even without
	 * processing an APDU.
	 * <p/>
	 * This method is called every time after {@link #process(ProcessingData)}
	 * is called when the protocol is not referenced from the protocol stack.
	 * <p/>
	 * When using this option keep in mind to ensure that this either returns
	 * only true if no instance is already on the stack or the protocol
	 * implementation is robust enough to be added to the stack multiple times.
	 * 
	 * @return
	 */
	public abstract boolean isMoveToStackRequested();
	
}