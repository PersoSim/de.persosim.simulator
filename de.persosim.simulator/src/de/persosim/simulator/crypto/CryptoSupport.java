package de.persosim.simulator.crypto;

import static org.globaltester.logging.BasicLogger.log;
import static org.globaltester.logging.BasicLogger.logException;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.utils.HexString;

public abstract class CryptoSupport {

	protected String cipherAlgorithmNameModePadding;
	protected String macName;

	protected Cipher cipher;
	protected Mac mac;

	/*--------------------------------------------------------------------------------*/

	public CryptoSupport() {

	}

	public CryptoSupport(String algorithmNameModePadding, String macName) {
		if(algorithmNameModePadding == null) {throw new NullPointerException("algorithm must not be null");}

		this.setCipherAlgorithmNameModePadding(algorithmNameModePadding);
		this.setMacName(macName);
	}

	/*--------------------------------------------------------------------------------*/

	public SecretKeySpec generateSecretKeySpec(byte[] key, String algorithm) {
		return new SecretKeySpec(key, algorithm);
	}

	protected abstract String getCipherAlgorithm();

	public String getCipherAlgorithmNameModePadding() {
		return cipherAlgorithmNameModePadding;
	}

	public void setCipherAlgorithmNameModePadding(String cipherAlgorithmNameModePadding) {
		String cipherAlgorithmExpected;
		String cipherAlgorithmReceived;

		cipherAlgorithmExpected = this.getCipherAlgorithm();
		cipherAlgorithmReceived = CryptoUtil.getCipherNameAsString(cipherAlgorithmNameModePadding);

		if(!cipherAlgorithmExpected.equals(cipherAlgorithmReceived)) {
			throw new IllegalArgumentException("algorithm must be " + cipherAlgorithmExpected);
		}

		this.cipherAlgorithmNameModePadding = cipherAlgorithmNameModePadding;

		try {
			this.cipher = Cipher.getInstance(this.cipherAlgorithmNameModePadding, Crypto.getCryptoProvider());
		} catch (GeneralSecurityException e) {
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			throw new IllegalArgumentException(e);
		}
	}

	public void setMacName(String macName) {
		this.macName = macName;

		try {
			this.mac = Mac.getInstance(this.macName, Crypto.getCryptoProvider());
		} catch (GeneralSecurityException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public SecretKeySpec generateSecretKeySpecCipher(byte[] in) {
		return this.generateSecretKeySpec(in, this.cipherAlgorithmNameModePadding);
	}

	public SecretKeySpec generateSecretKeySpecMac(byte[] in) {
		return this.generateSecretKeySpec(in, this.macName);
	}

	public SecretKeySpec generateSecretKeySpec(byte[] in, int offset, int length, String algorithm) {
		return this.generateSecretKeySpec(Arrays.copyOfRange(in, offset, offset + length), algorithm);
	}

	public SecretKeySpec generateSecretKeySpecCipher(byte[] in, int offset, int length) {
		return this.generateSecretKeySpec(in, offset, length, this.cipherAlgorithmNameModePadding);
	}

	public SecretKeySpec generateSecretKeySpecMac(byte[] in, int offset, int length) {
		return this.generateSecretKeySpec(in, offset, length, this.macName);
	}

	public byte[] encrypt(byte[] plainText, Key key, IvParameterSpec ivParams) {
		return encrypt(this.cipher, plainText, key, ivParams);
	}

	public static byte[] encrypt(Cipher cipher, byte[] plainText, Key key, IvParameterSpec ivParams) {
		byte[] encryptedNonce;

		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);

			encryptedNonce = cipher.doFinal(plainText);

			return encryptedNonce;
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException("invalid key");
		} catch (InvalidAlgorithmParameterException e) {
			throw new IllegalArgumentException("invalid ivParams");
		} catch (IllegalBlockSizeException e) {
			throw new IllegalArgumentException("illegal blocksize");
		} catch (BadPaddingException e) {
			throw new IllegalArgumentException("bad padding");
		}
	}

	public static byte[] encryptWithIvZero(Cipher cipher, byte[] plainText, Key key) {
		return encrypt(cipher, plainText, key, getIvSetToAllZeros(cipher.getBlockSize()));
	}

	public byte[] encryptWithIvZero(byte[] plainNonce, Key key) {
		return this.encrypt(plainNonce, key, this.getIvSetToAllZeros());
	}

	public static byte[] decrypt(Cipher cipher, byte[] cipherText, Key key, AlgorithmParameterSpec aps) {
		byte[] plainText;

		try {
			if(aps != null) {
				cipher.init(Cipher.DECRYPT_MODE, key, aps);
			} else{
				cipher.init(Cipher.DECRYPT_MODE, key);
			}

			plainText = cipher.doFinal(cipherText);

			return plainText;
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException("invalid key", e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new IllegalArgumentException("invalid iv", e);
		} catch (IllegalBlockSizeException e) {
			throw new IllegalArgumentException("illegal blocksize", e);
		} catch (BadPaddingException e) {
			throw new IllegalArgumentException("bad padding", e);
		}
	}

	public static byte[] decryptWithIvZero(Cipher cipher, byte[] cipherText, Key key) {
		return decrypt(cipher, cipherText, key, getIvSetToAllZeros(cipher.getBlockSize()));
	}

	public byte[] decrypt(byte[] cipherText, Key key, AlgorithmParameterSpec aps) {
		return decrypt(this.cipher, cipherText, key, aps);
	}

	public byte[] decryptWithIvZero(byte[] cipherText, Key key) {
		return this.decrypt(cipherText, key, this.getIvSetToAllZeros());
	}

	public byte[] macPlain(byte[] tokenPlain, Key key) {
		return macPlain(this.mac, tokenPlain, key);
	}

	public static byte[] macPlain(Mac mac, byte[] tokenPlain, Key key) {
		try {
			mac.init(key);
			log(CryptoSupport.class, "used mac algorithm is: " + mac.getAlgorithm(), LogLevel.DEBUG,
					new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
			return mac.doFinal(tokenPlain);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static byte[] mac(Mac mac, byte[] auxiliaryBlock, Cipher cipherEnc, byte[] macInput, Key macKey, int macLength) {
		byte[] processedMacInput = new byte[auxiliaryBlock.length + macInput.length];
		System.arraycopy(auxiliaryBlock, 0, processedMacInput, 0, auxiliaryBlock.length);
		System.arraycopy(macInput, 0, processedMacInput, auxiliaryBlock.length, macInput.length);
		log(CryptoSupport.class, "processed mac input is: " + HexString.encode(processedMacInput), LogLevel.DEBUG,
				new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		byte [] macResult = CryptoSupport.macPlain(mac, processedMacInput, macKey);
		log(CryptoSupport.class, "raw mac is: " + HexString.encode(macResult), LogLevel.DEBUG,
				new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		macResult = Arrays.copyOf(macResult, macLength);
		log(CryptoSupport.class, "expected mac is : " + HexString.encode(macResult), LogLevel.DEBUG,
				new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		return macResult;
	}

	public byte[] computeInitialBlockFromAuxiliaryData(byte[] auxiliaryBlock, byte[] macInput, Key macKey) {
		return computeInitialBlockFromAuxiliaryData(auxiliaryBlock, this.cipher, macInput, macKey);
	}

	public static byte[] computeInitialBlockFromAuxiliaryData(byte[] auxiliaryBlock, Cipher cipherEnc, byte[] macInput, Key macKey) {
		byte[] processedMacInput = Arrays.copyOf(macInput, macInput.length);

		byte[] initialBlock = CryptoSupport.encryptWithIvZero(cipherEnc, auxiliaryBlock, macKey);
		log(CryptoSupport.class, "initial block is: " + HexString.encode(initialBlock), LogLevel.DEBUG,
				new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));

		for(int i = 0; i < initialBlock.length; i++) {
			processedMacInput[i] = (byte) (macInput[i] ^ initialBlock[i]);
		}

		return processedMacInput;
	}

	public byte[] macAuthenticationToken(byte[] tokenPlain, Key key) {
		return this.macPlain(tokenPlain, key);
	}

	public int getBlockSize() {
		return this.cipher.getBlockSize();
	}

	public IvParameterSpec getIvSetToAllZeros() {
		return getIvSetToAllZeros(this.cipher.getBlockSize());
	}

	public static IvParameterSpec getIvSetToAllZeros(int blockSizeInBytes) {
		byte[] ivMaterial;

		ivMaterial = new byte[blockSizeInBytes];
		Arrays.fill(ivMaterial, (byte) 0x00);

		/* initialization vector for block cipher */
		return new IvParameterSpec(ivMaterial);
	}

	public String getCipherNameModePadding() {
		return this.cipherAlgorithmNameModePadding;
	}

	public String getCipherName() {
		return CryptoUtil.getCipherNameAsString(this.cipherAlgorithmNameModePadding);
	}

	public String getMacName() {
		return this.macName;
	}

	public byte[] adjustParity(byte[] key) {
		//nothing to do here, behavior needs to be changed in some subclasses
		return key;
	}

}
