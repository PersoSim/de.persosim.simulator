package de.persosim.simulator.crypto;

import static org.globaltester.logging.BasicLogger.DEBUG;
import static org.globaltester.logging.BasicLogger.log;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import org.globaltester.cryptoprovider.Crypto;

import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * @author slutters
 *
 * Implementation of Key Derivation Function (KDF) accoring to TR-03110 A.2.3. Key Derivation Function
 */
public class KeyDerivationFunction {
	public static final String[] DIGEST_ORDER = new String[]{"SHA-1", "SHA-256"};
	
	public static final byte[] COUNTER_ENC = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01};
	public static final byte[] COUNTER_MAC = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02};
	public static final byte[] COUNTER_PI  = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03};
	
	/*--------------------------------------------------------------------------------*/
	
	protected MessageDigest messageDigest;
	protected int keyLengthInBytes;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * 
	 * @param messageDigest the message digest to be used
	 * @param keyLengthInBytes key length in byte
	 */
	public KeyDerivationFunction(MessageDigest messageDigest, int keyLengthInBytes) {
		if(messageDigest == null) {throw new NullPointerException();}
		if(keyLengthInBytes < 0) {throw new IllegalArgumentException("key length must be >= 0");}
		if(messageDigest.getDigestLength() < keyLengthInBytes) {throw new IllegalArgumentException("key length must be smaller than or equal to digest length");}
		
		this.messageDigest = messageDigest;
		this.keyLengthInBytes = keyLengthInBytes;
	}
	
	public KeyDerivationFunction(int keyLengthInBytes) {
		if(keyLengthInBytes < 0) {throw new IllegalArgumentException("key length must be >= 0");}
		if(keyLengthInBytes > 32) {throw new IllegalArgumentException("key length must be <= 32");}
		
		this.keyLengthInBytes = keyLengthInBytes;
		
		try {
			if(keyLengthInBytes <= 16) {
				this.messageDigest =  MessageDigest.getInstance(DIGEST_ORDER[0], Crypto.getCryptoProvider());
			} else{
				this.messageDigest =  MessageDigest.getInstance(DIGEST_ORDER[1], Crypto.getCryptoProvider());
			}
		} catch (NoSuchAlgorithmException e) {
			/* this is not supposed to happen */
			e.printStackTrace();
		}
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns key material derived derived from SHA1(secret||nonce||counter) stripped to key length
	 * @param secret the common secret shared by PICC and ICD
	 * @param nonce optional nonce, may be null
	 * @param counter a counter
	 * @return key material derived derived from SHA1(secret||nonce||counter) stripped to key length
	 */
	public byte[] deriveKey(byte[] secret, byte[] nonce, byte[] counter) {
		int inputLength;
		byte[] input;
		byte[] digest;
		
		if(secret == null) {throw new NullPointerException();}
		if(counter == null) {throw new NullPointerException();}
		
		inputLength = secret.length + counter.length;
		
		if(nonce != null) {
			inputLength += nonce.length;
			log(KeyDerivationFunction.class, "deriving key from secret \"" + HexString.encode(secret) + "\", nonce \"" + HexString.encode(nonce) + "\" and counter \"" + HexString.encode(counter) +  "\"", DEBUG);
		} else{
			log(KeyDerivationFunction.class, "deriving key from secret \"" + HexString.encode(secret) + "\", no nonce and counter \"" + HexString.encode(counter) +  "\"", DEBUG);
		}
		
		if(inputLength <= 0) {
			throw new IllegalArgumentException("KDF input length must be > 0");
		}
		
		if(nonce == null) {
			input = Utils.concatByteArrays(secret, counter);
		} else{
			input = Utils.concatByteArrays(secret, nonce, counter);
		}
		
		log(KeyDerivationFunction.class, "message digest input is: " + HexString.encode(input), DEBUG);
		log(KeyDerivationFunction.class, "message digest algorithm is: " + messageDigest.getAlgorithm() + " of " + keyLengthInBytes + " bytes length", DEBUG);
		digest = this.messageDigest.digest(input);
		log(KeyDerivationFunction.class, "message digest result is: " + HexString.encode(digest), DEBUG);
		
		return Arrays.copyOf(digest, this.keyLengthInBytes);
	}
	
	/**
	 * Returns key material derived derived from SHA1(secret||nonce||COUNTER_ENC) stripped to key length
	 * @param secret the common secret shared by PICC and ICD
	 * @param nonce optional nonce, may be null
	 * @return key material derived derived from SHA1(secret||nonce||COUNTER_ENC) stripped to key length
	 * @throws IOException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	public byte[] deriveENC(byte[] secret, byte[] nonce) {
		return this.deriveKey(secret, nonce, COUNTER_ENC);
	}
	
	/**
	 * Returns key material derived derived from SHA1(secret||COUNTER_ENC) stripped to key length
	 * @param secret the common secret shared by PICC and ICD
	 * @return key material derived derived from SHA1(secret||COUNTER_ENC) stripped to key length
	 * @throws IOException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	public byte[] deriveENC(byte[] secret) {
		return this.deriveENC(secret, null);
	}
	
	/**
	 * Returns key material derived derived from SHA1(secret||nonce||COUNTER_MAC) stripped to key length
	 * @param secret the common secret shared by PICC and ICD
	 * @param nonce optional nonce, may be null
	 * @return key material derived derived from SHA1(secret||nonce||COUNTER_MAC) stripped to key length
	 * @throws IOException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	public byte[] deriveMAC(byte[] secret, byte[] nonce) {
		return this.deriveKey(secret, nonce, COUNTER_MAC);
	}
	
	/**
	 * Returns key material derived derived from SHA1(secret||COUNTER_MAC) stripped to key length
	 * @param secret the common secret shared by PICC and ICD
	 * @return key material derived derived from SHA1(secret||COUNTER_MAC) stripped to key length
	 * @throws IOException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	public byte[] deriveMAC(byte[] secret) {
		return this.deriveMAC(secret, null);
	}
	
	/**
	 * Returns key material derived derived from SHA1(secret||COUNTER_PI) stripped to key length
	 * @param secret the common secret shared by PICC and ICD
	 * @return key material derived derived from SHA1(secret||COUNTER_PI) stripped to key length
	 * @throws IOException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	public byte[] derivePI(byte[] secret) {
		return this.deriveKey(secret, null, COUNTER_PI);
	}
	
	/*--------------------------------------------------------------------------------*/

	/**
	 * @return the digest
	 */
	public MessageDigest getDigest() {
		return messageDigest;
	}

	/**
	 * @param digest the digest to set
	 */
	public void setDigest(MessageDigest digest) {
		if(digest == null) {
			throw new NullPointerException();
		}
		
		if(digest.getDigestLength() < this.keyLengthInBytes) {
			throw new IllegalArgumentException("length of computable message digest too small for key length");
		}
		
		this.messageDigest = digest;
	}

	/**
	 * @return the keyLength
	 */
	public int getKeyLengthInBytes() {
		return keyLengthInBytes;
	}

	/**
	 * @param keyLengthInBytes the keyLength to set
	 */
	public void setKeyLengthInBytes(int keyLengthInBytes) {
		if(keyLengthInBytes < 0) {
			throw new IllegalArgumentException("key length must be >= 0");
		}
		
		if(this.messageDigest.getDigestLength() < keyLengthInBytes) {
			throw new IllegalArgumentException("key length too big for message digest");
		}
		
		this.keyLengthInBytes = keyLengthInBytes;
	}
}
