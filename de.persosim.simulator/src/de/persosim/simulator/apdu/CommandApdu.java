package de.persosim.simulator.apdu;

import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * Container class carrying the information of the command APDU. This class
 * provides simplified access to all the relevant information that can be
 * extracted from the command APDU.
 * 
 * It also stores the processing history of this CommandApdu. For example if the
 * APDU was SM secured and unwrapped by the SecureMessaging layer the original
 * CommandApdu is preserved in the predecessor field.
 * 
 * @author amay
 * 
 */
public class CommandApdu {
	protected byte [] header;
	private boolean isExtendedLength;
	private byte isoCase;
	private int ne;
	private short nc;
	private TlvValue commandData;

	private CommandApdu predecessor = null;

	/**
	 * Parses the apdu from the given byte array.
	 * @param apdu
	 */
	CommandApdu(byte[] apdu) {
		this(apdu, null);
	}
	
	/**
	 * Parses the apdu from the given byte array and sets the provided instance as predecessor.
	 * @param apdu
	 * @param previousCommandApdu the predecessor of this instance, may be null
	 */
	CommandApdu(byte[] apdu, CommandApdu previousCommandApdu) {
		//store history
		predecessor = previousCommandApdu;
		
		//copy header
		header = new byte [4];
		System.arraycopy(apdu, 0, header, 0, header.length);
		
		//analyze/store Iso case and length
		isExtendedLength = Iso7816Lib.isExtendedLengthLCLE(apdu);
		isoCase = Iso7816Lib.getISOcase(apdu);
		
		//handle commandData (if present)
		if ((isoCase == Iso7816.ISO_CASE_3) || (isoCase == Iso7816.ISO_CASE_4)) {
			nc = Iso7816Lib.getNc(apdu);
			commandData = Iso7816Lib.getCommandData(apdu);
		} else {
			nc = 0;
			commandData = null;
		}
		
		//store ne (if present)
		if ((isoCase == Iso7816.ISO_CASE_2) || (isoCase == Iso7816.ISO_CASE_4)) {
			ne = Iso7816Lib.getNe(apdu);
		} else {
			ne = 0;
		}

	}

	public byte getIsoFormat() {
		return Iso7816Lib.getISOFormat(Iso7816Lib.getClassByte(header));
	}

	public byte getCla() {
		return Iso7816Lib.getClassByte(header);
	}

	public byte getIns() {
		return Iso7816Lib.getInstructionByte(header);
	}

	public byte getP1() {
		return Iso7816Lib.getP1(header);
	}

	public byte getP2() {
		return Iso7816Lib.getP2(header);
	}

	public byte getIsoCase() {
		return isoCase;
	}

	public boolean isExtendedLength() {
		return isExtendedLength;
	}
	
	public int getNc() {
		return nc;
	}

	public TlvValue getCommandData() {
		return commandData;
	}
	
	/**
	 * Tries to create a TlvDataObjectContainer from the commandDataField. This
	 * may result in a RuntimeException when the contained data cannot be
	 * parsed. Thus the caller is expected to handle this gracefully.
	 * 
	 * @return TlvDataObjectContainer created from command data field
	 */
	public TlvDataObjectContainer getCommandDataObjectContainer() {
		if (!(commandData instanceof TlvDataObjectContainer)) {
			commandData = new TlvDataObjectContainer(commandData);
		}
		return (TlvDataObjectContainer) commandData;
	}

	public int getNe() {
		return ne;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(HexString.encode(getHeader()));

		if (isoCase > 2) {
			sb.append('|');
			sb.append(HexString.encode(getLc()));
			sb.append('|');
			sb.append(getCommandData().toString());
		}

		if ((isoCase == 2) || (isoCase == 4)) {
			sb.append('|');
			sb.append(HexString.encode(getLe()));
		}
		
		return sb.toString();
	}
	
	public short getP1P2() {
		return Utils.concatenate(getP1(), getP2());
	}

	public byte[] getHeader() {
		byte[] header = new byte[this.header.length];
		System.arraycopy(this.header, 0, header, 0, this.header.length);
		return header;
	}

	/**
	 * Returns a byte representation of this object.
	 * @return
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(getHeader());
			if (isoCase > 2) {
				os.write(getLc());
				os.write(getCommandData().toByteArray());
			}
			os.write(getLe());
		} catch (IOException e) {
			logException(getClass(), e);
		}

	    return os.toByteArray();
	}

	/**
	 * Constructs valid byte encoding of Lc.
	 * <p/>
	 * If no data field is present an empty array is returned. If data field is
	 * present and extended length is used the returned byte array contains the
	 * required leading zero byte.
	 * 
	 * @return byte encoding of Lc field
	 */
	private byte[] getLc() {
		if (isoCase > 2) {
			if (isExtendedLength) {
				byte[] retVal = new byte[3];
				retVal[0] = 0;
				retVal[1] = (byte) ((nc & (short) 0xFF00) >> 8);
				retVal[2] = (byte) (nc & (short) 0x00FF);
				return retVal;
			} else {
				return new byte[]{(byte) nc};
			}
		} else {
			return new byte[]{};
		}
	}

	/**
	 * Constructs valid byte encoding of Le.
	 * <p/>
	 * If Le is absent an empty array is returned.
	 * If extended length is used Le is encoded in two bytes.
	 * If data field is absent and extended length is used the returned byte array contains the
	 * required leading zero byte.
	 * 
	 * @return byte encoding of Le field
	 */
	private byte[] getLe() {
		if (ne > 0) {  
			if(isExtendedLength) {
				if (isoCase > 2) {
					return Utils.toUnsignedByteArray((short)ne);
				} else {
					byte[] retVal = new byte[3];
					retVal[0] = 0;
					retVal[1] = (byte) ((ne & (short) 0xFF00) >> 8);
					retVal[2] = (byte) (ne & (short) 0x00FF);
					return retVal;
				}
				
			} else {
				return new byte[] {(byte) ne};
			}
		} else {
			return new byte[]{};
		}
	}

	public CommandApdu getPredecessor() {
		return predecessor;
	}

	/**
	 * Returns true iff this APDU is (or any predecessor was) sm secured
	 * <p/>
	 * This methods needs to be overridden by subclasses that support secure messaging
	 * @return
	 */
	public boolean wasSecureMessaging() {
		//this class does not support secure messaging, maybe a predecessor does
		if (predecessor != null) {
			return predecessor.wasSecureMessaging();
		} else {
			return false;
		}
	}

	public boolean isNeZeroEncoded() {
		if (isExtendedLength) {
			return ne == 65536;
		} else {
			return ne == 256;
		}
	}

}
