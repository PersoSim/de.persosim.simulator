package de.persosim.simulator.apdu;

import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.Iso7816Lib;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.tlv.TlvValuePlain;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Serializer;
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
public class CommandApduImpl implements CommandApdu {
	protected final byte [] header;
	private final boolean isExtendedLength;
	private final byte isoCase;
	private final int ne;
	private final short nc;
	private final TlvValue commandData;

	private CommandApdu predecessor = null;

	/**
	 * Parses the apdu from the given byte array.
	 * @param apdu
	 */
	CommandApduImpl(byte[] apdu) {
		this(apdu, null);
	}
	
	/**
	 * Parses the apdu from the given byte array and sets the provided instance as predecessor.
	 * @param apdu
	 * @param previousCommandApdu the predecessor of this instance, may be null
	 */
	protected CommandApduImpl(byte[] apdu, CommandApdu previousCommandApdu) {
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
			commandData = new TlvValuePlain(new byte [0]);
		}
		
		//store ne (if present)
		if ((isoCase == Iso7816.ISO_CASE_2) || (isoCase == Iso7816.ISO_CASE_4)) {
			ne = Iso7816Lib.getNe(apdu);
		} else {
			ne = 0;
		}

	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getIsoFormat()
	 */
	@Override
	public byte getIsoFormat() {
		return Iso7816Lib.getISOFormat(Iso7816Lib.getClassByte(header));
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getCla()
	 */
	@Override
	public byte getCla() {
		return Iso7816Lib.getClassByte(header);
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getIns()
	 */
	@Override
	public byte getIns() {
		return Iso7816Lib.getInstructionByte(header);
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getP1()
	 */
	@Override
	public byte getP1() {
		return Iso7816Lib.getP1(header);
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getP2()
	 */
	@Override
	public byte getP2() {
		return Iso7816Lib.getP2(header);
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getIsoCase()
	 */
	@Override
	public byte getIsoCase() {
		return isoCase;
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#isExtendedLength()
	 */
	@Override
	public boolean isExtendedLength() {
		return isExtendedLength;
	}
	
	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getNc()
	 */
	@Override
	public int getNc() {
		return nc;
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getCommandData()
	 */
	@Override
	public TlvValue getCommandData() {
		TlvValue ret = this.commandData;
		if(ret != null){
			return ret.copy();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getCommandDataObjectContainer()
	 */
	@Override
	public TlvDataObjectContainer getCommandDataObjectContainer() {
		TlvDataObjectContainer commandDataRet;
		if (!(commandData instanceof TlvDataObjectContainer)) {
			commandDataRet = new TlvDataObjectContainer(commandData);
		} else {
			commandDataRet = (TlvDataObjectContainer) commandData;
		}
		return Serializer.deepCopy(commandDataRet);
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getNe()
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getP1P2()
	 */
	@Override
	public short getP1P2() {
		return Utils.concatenate(getP1(), getP2());
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getHeader()
	 */
	@Override
	public byte[] getHeader() {
		byte[] header = new byte[this.header.length];
		System.arraycopy(this.header, 0, header, 0, this.header.length);
		return header;
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#toByteArray()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#getPredecessor()
	 */
	@Override
	public CommandApdu getPredecessor() {
		return predecessor;
	}

	/* (non-Javadoc)
	 * @see de.persosim.simulator.apdu.CommandApdu#isNeZeroEncoded()
	 */
	@Override
	public boolean isNeZeroEncoded() {
		if (isExtendedLength) {
			return ne == 65536;
		} else {
			return ne == 256;
		}
	}

}
