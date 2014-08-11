package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.securemessaging.SecureMessaging;

/**
 * This interface should be implemented by all {@link CommandApduImpl}s that
 * support SecureMessaging according to ISO7816-4.
 * <p/>
 * {@link SecureMessaging} operates directly on these APDUs to wrap/unwrap the
 * sm encoded data. This allows to apply ISO-SecureMessaging on proprietary
 * APDUs if their content is encoded accordingly.
 * 
 * @author amay
 * 
 */
public interface IsoSecureMessagingCommandApdu extends CommandApdu {

	/**
	 * see {@link Iso7816Lib#getSecureMessagingStatus(byte[])}
	 * 
	 * @return SM indicator
	 */
	public abstract byte getSecureMessaging();

	/**
	 * This methods implementations should wrap a given data (including L_c,
	 * command data and L_e fields) into a new CommandApdu using the same
	 * header.
	 * 
	 * @param newSmStatus
	 *            the new ISO7816 secure messaging status to store
	 * @param data
	 *            the L_c|command data|L_e fields as concatenated byte array
	 * @return
	 */
	public CommandApdu rewrapApdu(byte newSmStatus, byte[] data);


	/**
	 * Returns true iff this APDU is (or any predecessor was) sm secured
	 * <p/>
	 * 
	 * @return
	 */
	public abstract boolean wasSecureMessaging();

}
