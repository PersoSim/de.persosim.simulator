package de.persosim.simulator.apdu;

import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvValue;

/**
 * This interface defines an container object carrying the information of the
 * command APDU. Implementations provide simplified access to all the relevant
 * information that can be extracted from the command APDU.
 * 
 * Additionally access to the history of this CommandApdu is to be provided. For
 * example if the APDU was SM secured and unwrapped by the SecureMessaging layer
 * the original CommandApdu is preserved in the predecessor field.
 * 
 * @author mboonk
 * 
 */
public interface CommandApdu {

	public abstract byte getIsoFormat();

	public abstract byte getCla();

	public abstract byte getIns();

	public abstract byte getP1();

	public abstract byte getP2();

	public abstract byte getIsoCase();

	public abstract boolean isExtendedLength();

	public abstract int getNc();

	public abstract TlvValue getCommandData();

	/**
	 * Tries to create a TlvDataObjectContainer from the commandDataField. This
	 * may result in a RuntimeException when the contained data cannot be
	 * parsed. Thus the caller is expected to handle this gracefully.
	 * 
	 * @return TlvDataObjectContainer created from command data field
	 */
	public abstract TlvDataObjectContainer getCommandDataObjectContainer();

	public abstract int getNe();

	public abstract short getP1P2();

	public abstract byte[] getHeader();

	/**
	 * Returns a byte representation of this object.
	 * 
	 * @return
	 */
	public abstract byte[] toByteArray();

	public abstract CommandApdu getPredecessor();

	public abstract boolean isNeZeroEncoded();

}