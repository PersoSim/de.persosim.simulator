package de.persosim.simulator.securemessaging;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.persosim.simulator.processing.UpdatePropagation;

public class TestSmDataProvider implements SmDataProvider {

	public IvParameterSpec cipherIv;
	public Cipher cipher;
	public SecretKey keyEnc;
	public Mac mac;
	public byte[] macAuxiliaryData;
	public SecretKey keyMac;
	public Integer macLength;

	@Override
	public Class<? extends UpdatePropagation> getKey() {
		return null;
	}

	@Override
	public void init(SmDataProvider prev) {
		// nothing to do for tests
	}

	@Override
	public void nextIncoming() {
		// nothing to do for tests
	}

	@Override
	public void nextOutgoing() {
		// nothing to do for tests
	}

	@Override
	public Cipher getCipher() {
		return cipher;
	}

	@Override
	public IvParameterSpec getCipherIv() {
		return cipherIv;
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
		return macAuxiliaryData;
	}

	@Override
	public SecretKey getKeyMac() {
		return keyMac;
	}

	@Override
	public Integer getMacLength() {
		return macLength;
	}

	@Override
	public SmDataProviderGenerator getSmDataProviderGenerator() {
		return null;
	}

}
