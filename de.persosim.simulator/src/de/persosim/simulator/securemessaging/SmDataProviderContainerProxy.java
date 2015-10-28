package de.persosim.simulator.securemessaging;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.persosim.simulator.processing.UpdatePropagation;

/**
 * Static implementation of the{@link SmDataProvider} interface.
 * 
 * This class simply stores the required data statically and provides it on
 * every request. If not all fields are filled upon
 * {@link #init(SmDataProvider)} the previous provider must not be null. Every
 * request for a field which has no value is forwarded to the predecessor.
 * 
 * @author amay
 * 
 */
public class SmDataProviderContainerProxy implements SmDataProvider {
	
	@Override
	public Class<? extends UpdatePropagation> getKey() {
		return SmDataProvider.class;
	}

	private Cipher cipher;
	private IvParameterSpec encIv;
	private SecretKey keyEnc;

	private Mac mac;
	private byte[] macAuxiliaryData;
	private SecretKey keyMAC;
	private Integer macLength = -1;

	private SmDataProvider predecessor;

	@Override
	public Cipher getCipher() {
		if (cipher == null) {
			return predecessor.getCipher();
		}
		return cipher;
	}

	@Override
	public IvParameterSpec getCipherIv() {
		if (encIv == null) {
			return predecessor.getCipherIv();
		}
		return encIv;
	}

	@Override
	public SecretKey getKeyEnc() {
		if (keyEnc == null) {
			return predecessor.getKeyEnc();
		}
		return keyEnc;
	}

	@Override
	public SecretKey getKeyMac() {
		if (keyMAC == null) {
			return predecessor.getKeyMac();
		}
		return keyMAC;
	}

	@Override
	public Mac getMac() {
		if (mac == null) {
			return predecessor.getMac();
		}
		return mac;
	}

	@Override
	public byte[] getMacAuxiliaryData() {
		if (macAuxiliaryData == null) {
			return predecessor.getMacAuxiliaryData();
		}
		return macAuxiliaryData;
	}

	@Override
	public Integer getMacLength() {
		if (macLength < 0) {
			return predecessor.getMacLength();
		}
		return macLength;
	}

	public void setCipher(Cipher newCipher) {
		cipher = newCipher;
	}

	public void setEncIv(IvParameterSpec newEncIv) {
		encIv = newEncIv;
	}

	public void setKeyEnc(SecretKey keyEnc) {
		this.keyEnc = keyEnc;
	}

	public void setKeySpecMAC(SecretKey keySpecMAC) {
		this.keyMAC = keySpecMAC;
	}

	public void setMac(Mac mac) {
		this.mac = mac;
	}

	public void setMacAuxiliaryData(byte[] macAuxiliaryData) {
		this.macAuxiliaryData = macAuxiliaryData;
	}

	public void setMacLength(Integer macLength) {
		this.macLength = macLength;
	}

	@Override
	public void init(SmDataProvider prev) {
		if ((cipher == null) || (encIv == null) || (keyEnc == null)
						|| (keyMAC == null) || (mac == null)
						|| (macAuxiliaryData == null) || (macLength < 0)
				) {
			if (prev == null) {
			throw new IllegalArgumentException("predecessor must not be null, when not all fields are initialized");
			} else {
				predecessor = prev;		
			}
		} else {
			predecessor = null;
		}
		
	}

	@Override
	public void nextIncoming() {
		//forward to predecessor if present
		if (predecessor != null) {
			predecessor.nextIncoming();
		}
	}

	@Override
	public void nextOutgoing() {
		//forward to predecessor if present
		if (predecessor != null) {
			predecessor.nextOutgoing();;
		}
	}

	@Override
	public SmDataProviderGenerator getSmDataProviderGenerator() {
		// IMPL implement method when needed
		// currently objects of these class are only used for tests not relying on this function
		return null;
	}

}
