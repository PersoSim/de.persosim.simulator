package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * Container class carrying the information of the response APDU. This class
 * provides simplified access to all the relevant information that can be
 * extracted from the response APDU.
 * 
 * Response Apdus are immutable.
 */
public class ResponseApdu {

	/* Declares data to be sent with the response APDU */
	protected final TlvValue data;
	protected final short statusWord;

	/**
	 * Instantiate a new ResponseAPDU without data field and the given SW
	 * @param sw status word of the new response APDU
	 */
	public ResponseApdu(short sw) {
		this.statusWord = sw;
		this.data = null;
	}
	
	/**
	 * Instantiate a new ResponseAPDU with given data field and SW
	 * @param data data field of the new response APDU
	 * @param sw status word of the new response APDU
	 */
	public ResponseApdu(TlvValue data, short sw) {
		this.data = data;
		this.statusWord = sw;
	}
	
	public boolean isReportingError() {
		return Iso7816Lib.isReportingError(statusWord);
	}

	public short getStatusWord() {
		return statusWord;
	}

	public TlvValue getData() {
		TlvValue ret = this.data;
		if(ret != null){
			return ret.copy();
		}
		return null;
	}

	public byte[] toByteArray() {
		byte[] swArray = Utils.toUnsignedByteArray(statusWord);
		
		if (data != null) {
			return Utils.concatByteArrays(data.toByteArray(), swArray);
		} else {
			return swArray;
		}
	}
	
	@Override
	public String toString() {
		if(data == null) {
			return HexString.hexifyShort(statusWord);
		} else{
			return data + "|" + HexString.hexifyShort(statusWord);
		}
	}

}

