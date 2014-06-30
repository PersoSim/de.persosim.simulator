package de.persosim.simulator.securemessaging;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.persosim.simulator.processing.UpdatePropagation;

/**
 * This class provides all data required to perform secure messaging according
 * to ISO7816-4.
 * 
 * This includes algorithms, keys, initialization data etc. The interface
 * provides is intended to be as general as possible in order to allow easy
 * sub-classing. As all required data is only provided via well defined getXxx()
 * methods overriding implementations can easily include any arbitrary
 * modification logic, such as including send sequence counters within the
 * initialization data for example.
 * 
 * Before a SmDataProvider is used/queried it shall be initialized by a call to
 * {@link #init()} with the previous data provider as parameter. This allows to
 * specify partial updates, as the new DataProvider can fetch the remaining data
 * from the previously used provider. Before processing an incoming or outgoing
 * APDU the methods {@link #nextIncoming()} and {@link #nextOutgoing()} are
 * called respectively. Beside these three methods no guarantees are made
 * regarding the calling order of the methods in this interface.
 * 
 * This interface extends {@link UpdatePropagation} so that instances can
 * directly be propagated through the processing data. {@link SecureMessaging}
 * layer checks and processes UpdatePropagations with this interface as key
 * right after processing the outgoing APDU. Therefore implementing classes
 * should provide this interface in their {@link #getKey()} method.
 * 
 * @author amay
 * 
 */
public interface SmDataProvider extends UpdatePropagation {

	/**
	 * This method shall be called upon every change of of the SmDataProvider.
	 * After this call finished the new provider is expected to be ready to use.
	 * It is intended to allow partial updates by providing an implementation
	 * that fetches missing values from the previously used provider.
	 * 
	 * As the previous provider is passed as parameter implementations may even
	 * choose to keep a reference to the predecessor and proxy calls to it. This
	 * may be needed when partial updating provider expects it's predecessor to
	 * not only provide static data but perform some calculation that the new
	 * provider is not capable or aware of. If such forwarding is implemented
	 * the prev provider does not know of this, therefore the class implementing
	 * this must provide the consistency granted by this interface to the
	 * encapsulated previous instance.
	 * 
	 * If the new provider is unable to complete initialization (e.g. because
	 * the previous provider was null or did provide incompatible values) it
	 * shall throw a RuntimeException that can be handled by the calling layer.
	 * 
	 * @param prev
	 *            the previously used provider (may be null)
	 */
	public void init(SmDataProvider prev);
	
	/**
	 * Prepare the data provider for the next incoming APDU. This method is
	 * guaranteed to be called before any other method is called related to this
	 * particular incoming APDU.
	 */
	public void nextIncoming();
	
	/**
	 * Prepare the data provider for the next outgoing APDU. This method is
	 * guaranteed to be called before any other method is called related to this
	 * particular outgoing APDU.
	 */
	public void nextOutgoing();

	/**
	 * Return the cipher to be used to encipher/decipher the cryptogram of the
	 * currently handled APDU.
	 * 
	 * @return cipher
	 */
	public Cipher getCipher();

	/**
	 * Return initialization vector (IV) used to encipher/decipher the cryptogram of the
	 * currently handled APDU. This needs to be usable with the cipher returned by {@link #getCipher()}
	 * @return initialization vector
	 */
	public IvParameterSpec getCipherIv();

	/**
	 * Return the secret key used to encipher/decipher the cryptogram of the
	 * currently handled APDU. This needs to be usable with the cipher returned by {@link #getCipher()}
	 * @return secret key
	 */
	public SecretKey getKeyEnc();

	
	/**
	 * Return the "Message Authentication Code" (MAC) to be used to authenticate the currently handled APDU.
	 * @return mac
	 */
	public Mac getMac();

	/**
	 * Return auxiliary data to be used during message authentication of the
	 * currently handled APDU. This is prepended to the mac input as initial block according to ISO7816-4.
	 * @return auxiliary data
	 */
	public byte[] getMacAuxiliaryData();

	/**
	 * Return the secret key used for message authentication of the
	 * currently handled APDU. This needs to be usable with the mac returned by {@link #getMac()}
	 * @return secret key
	 */
	public SecretKey getKeyMac();

	/**
	 * Returns number of significant bytes used from mac within the APDU.
	 * According to ISO7816-4 this may be any value in the range from 4 to block
	 * size of the mac algorithm used.
	 * 
	 * @param macLength
	 */
	public Integer getMacLength();

}
