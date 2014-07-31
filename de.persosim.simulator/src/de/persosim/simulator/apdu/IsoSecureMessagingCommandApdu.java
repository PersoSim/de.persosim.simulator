package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.securemessaging.SecureMessaging;

/**
 * This interface should be implemented by all {@link CommandApdu}s that support
 * SecureMessaging according to ISO7816-4.
 * <p/>
 * {@link SecureMessaging} operates directly on these APDUs to wrap/unwrap the
 * sm encoded data. This allows to apply ISO-SecureMessaging on prprietary APDUs
 * if their content is encoded accordingly.
 * 
 * @author amay
 * 
 */
public interface IsoSecureMessagingCommandApdu {

	/**
	 * see {@link Iso7816Lib#getSecureMessagingStatus(byte[])}
	 * @return SM indicator
	 */
	public abstract byte getSecureMessaging();

}