package de.persosim.simulator.securemessaging;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.persosim.simulator.crypto.Crypto;
import de.persosim.simulator.crypto.CryptoSupport;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.SendSequenceCounter;
import de.persosim.simulator.processing.UpdatePropagation;

public class SmDataProviderTr03110 implements SmDataProvider {
	
	@Override
	public Class<? extends UpdatePropagation> getKey() {
		return SmDataProvider.class;
	}

	private Cipher cipher;
	private SecretKey keyEnc;

	private Mac mac;
	private SecretKey keyMac;
	
	/**
	 * SendSequenceCounter according to BSI TR-03110
	 */
	private SendSequenceCounter ssc;
	
	public SmDataProviderTr03110(SecretKeySpec cipherKey, SecretKeySpec macKey) throws GeneralSecurityException {
		keyEnc = cipherKey;
		keyMac = macKey;
		
		cipher = Cipher.getInstance(keyEnc.getAlgorithm(), Crypto.getCryptoProvider());
		
		// XXX AMY use new Crypto wrappers here
		// According to developer consens we want to create wrapper objects that
		// encapsulate Cipher or Mac resp. together with the relevant key. These
		// objects can be passed as method parameters and encapsulate all
		// different behavior by overloading. Thus protocols dont need to switch
		// on objecttype while the OID classes work as factory for these
		// wrappers.
		// The differentiation between Cipher and Mac wrappers is intended to be
		// according to the JavaCryptoApi. 
		// {@link CryptoSupport}
		if (CryptoUtil.getCipherNameAsString(cipher.getAlgorithm()).equals(
				"DESede")) {
			// 3DES
			mac = Mac.getInstance("ISO9797ALG3", Crypto.getCryptoProvider());
		} else {
			//AES
			mac = Mac.getInstance(keyMac.getAlgorithm(), Crypto.getCryptoProvider());
		}
		
		
		ssc = new SendSequenceCounter(this.cipher.getBlockSize());
	}

	@Override
	public void init(SmDataProvider prev) {
		// nothing to be done here
		// complete initialization done within constructor, no need to know the predecessor
	}

	@Override
	public void nextIncoming() {
		ssc.increment();
	}

	@Override
	public void nextOutgoing() {
		ssc.increment();
	}

	@Override
	public Cipher getCipher() {
		return cipher;
	}

	@Override
	public IvParameterSpec getCipherIv() {
		byte[] cipherIvPlain;
		//XXX AMY use new Crypto wrappers here (details see above)
		if (CryptoUtil.getCipherNameAsString(cipher.getAlgorithm()).equals("DESede")) {
			//3DES
			cipherIvPlain = new byte[8];
		} else {
			//AES
			cipherIvPlain = CryptoSupport.encryptWithIvZero(cipher, ssc.toByteArray(), keyEnc);
		}
		return new IvParameterSpec(cipherIvPlain);
	}

	@Override
	public SecretKey getKeyEnc() {
		return keyEnc;
	}

	@Override
	public Mac getMac() {
		return mac;
	}

	@Override
	public byte[] getMacAuxiliaryData() {
		// incrementing is already done in #nextIncoming() and #nextOutgoing()
		return ssc.toByteArray();
	}

	@Override
	public SecretKey getKeyMac() {
		return keyMac;
	}

	@Override
	public Integer getMacLength() {
		return 8;
	}

}
