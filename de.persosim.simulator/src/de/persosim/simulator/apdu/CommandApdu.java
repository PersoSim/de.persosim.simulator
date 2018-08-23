package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvValue;

/**
 * This interface defines a container object carrying the information of the
 * command APDU. Implementations provide simplified access to all the relevant
 * information that can be extracted from the command APDU.
 * 
 * Additionally access to the history of this CommandApdu is to be provided. For
 * example if the APDU was SM secured and unwrapped by the SecureMessaging layer
 * the original CommandApdu is preserved in the predecessor field.
 * 
 * Implementations will usually use the methods in the {@link Iso7816Lib}.
 * 
 * Beware that all implementations of {@link CommandApdu} are expected to be immutable.
 * 
 * @author mboonk
 * 
 */
public interface CommandApdu {

	public abstract byte getIsoFormat();

	/**
	 * @return the class byte {@link Iso7816Lib#getClassByte(byte[])}
	 */
	public abstract byte getCla();

	/**
	 * @return the instruction byte
	 */
	public abstract byte getIns();

	/**
	 * @return the P1 parameter byte
	 */
	public abstract byte getP1();

	/**
	 * @return the P2 parameter byte
	 */
	public abstract byte getP2();

	/**
	 * @return the iso case representation
	 */
	public abstract byte getIsoCase();

	/**
	 * @return true, iff the APDU is an extendend length encoding
	 */
	public abstract boolean isExtendedLength();

	/**
	 * @return the number encoded in the L_c field of the APDU
	 */
	public abstract int getNc();
	
	/**
	 * Tries to create a TlvValue from the commandDataField. This
	 * may result in a RuntimeException when the contained data cannot be
	 * parsed. Thus the caller is expected to handle this gracefully.
	 * 
	 * @return TlvValue created from command data field
	 */
	public abstract TlvValue getCommandData();

	/**
	 * Tries to create a TlvDataObjectContainer from the commandDataField. This
	 * may result in a RuntimeException when the contained data cannot be
	 * parsed. Thus the caller is expected to handle this gracefully.
	 * 
	 * @return TlvDataObjectContainer created from command data field
	 */
	public abstract TlvDataObjectContainer getCommandDataObjectContainer();

	/**
	 * @return the number encoded in the L_e field of the APDU
	 */
	public abstract int getNe();

	/**
	 * @return the L_e field of the APDU
	 */
	public abstract byte [] getLe();

	/**
	 * @return both parameter bytes concatenated into one {@link short} value
	 */
	public abstract short getP1P2();

	/**
	 * @return the first four bytes of the APDU containing CLA,INS,P1,P2
	 */
	public abstract byte[] getHeader();

	/**
	 * Returns a byte representation of this object.
	 * 
	 * @return the APDU as byte array
	 */
	public abstract byte[] toByteArray();

	/**
	 * The history of this APDU is maintained in a chain of predecessors.
	 * 
	 * @return the predecessor to this APDU
	 */
	public abstract CommandApdu getPredecessor();

	/**
	 * @return true, iff the N_e number was zero encoded in the L_e field 
	 */
	public abstract boolean isNeZeroEncoded();

}
