package de.persosim.simulator.platform;

import de.persosim.simulator.exception.ISO7816Exception;
import de.persosim.simulator.tlv.TlvValue;
import de.persosim.simulator.tlv.TlvValuePlain;
import de.persosim.simulator.utils.Utils;

/**
 * @author slutters
 *
 */
// IMPL consistently  throw Exceptions for unsupported inputs
public abstract class Iso7816Lib implements Iso7816 {
	
	public static final short MASK_BYTE_TO_SHORT = (short) 0xFF;
	public static final int MASK_SHORT_TO_INT = 0xFFFF;
	
	public static final byte ISO_CASE_1 = 1;
	public static final byte ISO_CASE_2_SHORT_LE = 2;
	public static final byte ISO_CASE_3_SHORT_LC = 3;
	public static final byte ISO_CASE_4_SHORT_LC_SHORT_LE = 4;
	public static final byte ISO_CASE_2_EXTENDED_L_E = 10;
	public static final byte ISO_CASE_3_EXTENDED_L_C = 15;
	public static final byte ISO_CASE_4_EXTENDED_L_C_EXTENDED_L_E = 20;
	
	/*----------------------------------------------------------------*/
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns the ISO format according to ISO 7816-4, 5.1.1 Class byte 
	 * @param apdu the APDU
	 * @return the ISO format according to ISO 7816-4, 5.1.1 Class byte 
	 */
	public static byte getISOFormat(byte[] apdu) {
		return getISOFormat(getClassByte(apdu));
	}

	/**
	 * Returns the ISO format according to ISO 7816-4, 5.1.1 Class byte 
	 * @param cla the class byte
	 * @return the ISO format according to ISO 7816-4, 5.1.1 Class byte 
	 */
	public static byte getISOFormat(byte cla) {

		if (((byte) (cla & (byte) 0xE0) == (byte) 0x00)) {
			return ISO_FORMAT_FIRSTINTERINDUSTRY;
		}

		if (((byte) (cla & (byte) 0xC0) == (byte) 0x40)) {
			return ISO_FORMAT_FURTHERINTERINDUSTRY;
		}

		if (((byte) (cla & (byte) 0xE0) == (byte) 0x20)) {
			return ISO_FORMAT_INTERINDUSTRY_RESERVED;
		}

		if ((cla == (byte) 0xFF)) {
			return ISO_FORMAT_INVALID;
		}

		return ISO_FORMAT_PROPRIETARY;

	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided byte represents an ISO inter-industry APDU
	 * @param isoFormat byte representing ISO format
	 * @return whether the provided byte represents an ISO inter-industry APDU
	 */
	public static boolean isISOInterindustryCLA(byte isoFormat) {
		return isoFormat >= ISO_FORMAT_FIRSTINTERINDUSTRY;
	}
	
	/**
	 * Returns whether the provided APDU represents an ISO inter-industry APDU
	 * @param apdu the APDU
	 * @return whether the provided APDU represents an ISO inter-industry APDU
	 */
	public static boolean isISOInterindustry(byte[] apdu) {
		return isISOInterindustryCLA(getISOFormat(apdu));
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided byte represents a defined ISO inter-industry APDU
	 * @param isoFormat byte representing ISO format
	 * @return whether the provided byte represents a defined ISO inter-industry APDU
	 */
	public static boolean isISOInterindustryDefinedCLA(byte isoFormat) {
		return ((isoFormat == ISO_FORMAT_FIRSTINTERINDUSTRY) || (isoFormat == ISO_FORMAT_FURTHERINTERINDUSTRY));
	}
	
	/**
	 * Returns whether the provided APDU represents a defined ISO inter-industry APDU
	 * @param apdu the APDU
	 * @return whether the provided APDU represents a defined ISO inter-industry APDU
	 */
	public static boolean isISOInterindustryDefinedCLA(byte[] apdu) {
		return isISOInterindustryDefinedCLA(getISOFormat(apdu));
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided byte represents a first ISO inter-industry APDU
	 * @param isoFormat byte representing ISO format
	 * @return whether the provided byte represents a first ISO inter-industry APDU
	 */
	public static boolean isISOFirstInterindustryCLA(byte isoFormat) {
		return isoFormat == ISO_FORMAT_FIRSTINTERINDUSTRY;
	}
	
	/**
	 * Returns whether the provided APDU represents a first ISO inter-industry APDU
	 * @param apdu the APDU
	 * @return whether the provided APDU represents a first ISO inter-industry APDU
	 */
	public static boolean isISOFirstInterindustryCLA(byte[] apdu) {
		return isISOFirstInterindustryCLA(getISOFormat(apdu));
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided byte represents a further ISO inter-industry APDU
	 * @param isoFormat byte representing ISO format
	 * @return whether the provided byte represents a further ISO inter-industry APDU
	 */
	public static boolean isISOFurtherInterindustryCLA(byte isoFormat) {
		return isoFormat == ISO_FORMAT_FURTHERINTERINDUSTRY;
	}
	
	/**
	 * Returns whether the provided APDU represents a further ISO inter-industry APDU
	 * @param apdu the APDU
	 * @return whether the provided APDU represents a further ISO inter-industry APDU
	 */
	public static boolean isISOFurtherInterindustryCLA(byte[] apdu) {
		return isISOFurtherInterindustryCLA(getISOFormat(apdu));
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided byte represents a reserved ISO inter-industry APDU
	 * @param isoFOrmat byte representing ISO format
	 * @return whether the provided byte represents a reserved ISO inter-industry APDU
	 */
	public static boolean isISOInterindustryReservedCLA(byte isoFormat) {
		return isoFormat == ISO_FORMAT_INTERINDUSTRY_RESERVED;
	}
	
	/**
	 * Returns whether the provided APDU represents a reserved ISO inter-industry APDU
	 * @param apdu the APDU
	 * @return whether the provided APDU represents a reserved ISO inter-industry APDU
	 */
	public static boolean isISOInterindustryReservedCLA(byte[] apdu) {
		return isISOInterindustryReservedCLA(getISOFormat(apdu));
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided byte represents a proprietary APDU
	 * @param isoFormat byte representing ISO format
	 * @return whether the provided byte represents a proprietary APDU
	 */
	public static boolean isProprietaryCLA(byte isoFormat) {
		return isoFormat == ISO_FORMAT_PROPRIETARY;
	}
	
	/**
	 * Returns whether the provided APDU represents a proprietary APDU
	 * @param apdu the APDU
	 * @return whether the provided APDU represents a proprietary APDU
	 */
	public static boolean isProprietaryCLA(byte[] apdu) {
		return isProprietaryCLA(getISOFormat(apdu));
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided ISO format is invalid
	 * @param isoFormat the ISO format
	 * @return whether the provided ISO format is invalid
	 */
	public static boolean isInvalidCLA(byte isoFormat) {
		return isoFormat == ISO_FORMAT_INVALID;
	}
	
	/**
	 * Returns whether the provided APDU's ISO format is invalid
	 * @param apdu the APDU
	 * @return whether the provided APDU's ISO format is invalid
	 */
	public static boolean isInvalidCLA(byte[] apdu) {
		return isInvalidCLA(getISOFormat(apdu));
	}
	
	/*----------------------------------------------------------------*/
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns the extended ISO case no of the provided APDU
	 * If short L_C and/or L_E fields are found, the return value will represent the expected ISO case no.
	 * If extended L_C and/or L_E fields are found, the return value will represent the expected ISO case no multiplied by factor 5.
	 * Factor 5 was only chosen as it is the smallest factor for multiplication that will never yield a result indistinguishable from short L_C or L_E APDUs.
	 * In order to prevent confusion this method is kept private and only accessible through delegate methods.
	 * 
	 * return value   --> ISO case
	 * Byte.Min_VALUE --> Invalid ISO case --> Exception
	 *        1       --> ISO case 1
	 *        2       --> ISO case 2, short L_E
	 *        3       --> ISO case 3, short L_C
	 *        4       --> ISO case 4, short L_C, short L_E
	 *       10       --> ISO case 2, extended L_E
	 *       15       --> ISO case 3, extended L_C
	 *       20       --> ISO case 4, extended L_C, extended L_E
	 * @param apdu the APDU
	 * @return the extended ISO case no of the provided APDU
	 */
	public static byte getISOcaseExtended(byte[] apdu) {
		int indicatedLength;
		int offsetLc;
		short lc;
		
		offsetLc = -1;
		
		if(apdu.length >= APDU_MINIMUM_LENGTH_ISO_CASE1) {
			if(apdu.length == APDU_MINIMUM_LENGTH_ISO_CASE1) {
				/* ISO case 1 */
				return ISO_CASE_1;
			}
			
			if(apdu.length == APDU_MINIMUM_LENGTH_ISO_CASE2) {
				/* ISO case 2 */
				/* short Le */
				return ISO_CASE_2_SHORT_LE;
			}
			
			if((apdu.length == APDU_MAXIMUM_LENGTH_ISO_CASE2) && (apdu[OFFSET_LE_ISO_CASE_2] == (byte) 0x00)) {
				/* ISO case 2 */
				/* extended Le */
				return ISO_CASE_2_EXTENDED_L_E;
			}
			
			if(apdu.length >= APDU_MINIMUM_LENGTH_ISO_CASE3) {
				offsetLc = OFFSET_LC;
				lc = Utils.maskUnsignedByteToShort(apdu[offsetLc]);
				
				if(lc == (short) 0x0000) {
					/* extended Lc */
					/* ISO case 3 or 4 if at all */
					if(apdu.length >= APDU_MINIMUM_LENGTH_EXTENDED_ISO_CASE3) {
						lc = Utils.concatenate(apdu[OFFSET_LC + 1], apdu[OFFSET_LC + 2]);
						
						/* this is the total length of the APDU */
						indicatedLength = Utils.maskUnsignedByteToInt(APDU_MINIMUM_LENGTH_EXTENDED_ISO_CASE3) + Utils.maskUnsignedShortToInt(lc) -1;
						
						if(lc != (short) 0x0000) {
							if(apdu.length == indicatedLength) {
								// ISO case 3
								return ISO_CASE_3_EXTENDED_L_C;
							} else{
								if(apdu.length == (indicatedLength + 2)) { /* +2 for extended Le field with Lc present*/
									/* ISO case 4 */
									/* extended Le */
									return ISO_CASE_4_EXTENDED_L_C_EXTENDED_L_E;
								} else{
									/* Intentionally left blank */
									ISO7816Exception.throwIt(Iso7816.SW_6700_WRONG_LENGTH, "the APDU appears to be ISO case 4 with extended L_C but indicated length does not match actual length");
								}
							}
						}
					}
				} else{
					/* short Lc */
					indicatedLength = Utils.maskUnsignedByteToInt(APDU_MINIMUM_LENGTH_ISO_CASE3) + Utils.maskUnsignedShortToInt(lc) -1;
					
					if(apdu.length == indicatedLength) {
						/* ISO case 3 */
						return ISO_CASE_3_SHORT_LC;
					} else{
						if(apdu.length == (indicatedLength + 1)) {
							/* ISO case 4 */
							/* short Le */
							return ISO_CASE_4_SHORT_LC_SHORT_LE;
						} else{
							/* Intentionally left blank */
							ISO7816Exception.throwIt(Iso7816.SW_6700_WRONG_LENGTH, "the APDU appears to be ISO case 4 with short L_C but indicated length does not match actual length");
						}
					}
				}
			}
		}
		
		// ERROR
		ISO7816Exception.throwIt(Iso7816.SW_6700_WRONG_LENGTH, "the APDU can not be related to any ISO case");
		return Byte.MIN_VALUE;
	}
	
	/*----------------------------------------------------------------*/
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns the ISO case according to ISO 7816-4
	 * @param isoCaseExtended isoCase according to getISOcaseExtended(APDU):byte
	 * @return the ISO case according to ISO 7816-4
	 */
	public static byte getISOcase(byte isoCaseExtended) {
		if(isoCaseExtended <= ISO_CASE_4_SHORT_LC_SHORT_LE) {
			return isoCaseExtended;
		}
		
		return (byte) (isoCaseExtended / 5);
	}
	
	/**
	 * Returns the ISO case according to ISO 7816-4
	 * @param apdu the APDU
	 * @return the ISO case according to ISO 7816-4
	 */
	public static byte getISOcase(byte[] apdu) {
		return getISOcase(getISOcaseExtended(apdu));
	}
	
	public static boolean isExtendedLengthLCLE(byte isoCaseExtended) {
		return (isoCaseExtended > ISO_CASE_4);
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided byte represents an SIO case 1 APDU
	 * @param isoCaseExtended isoCase according to getISOcaseExtended(APDU):byte
	 * @return whether the provided byte represents an SIO case 1 APDU
	 */
	private static boolean isIsoCase1(byte isoCaseExtended) {
		return (isoCaseExtended == ISO_CASE_1);
	}
	
	/**
	 * Returns whether the APDU is an SIO case 1 APDU
	 * @param apdu the APDU
	 * @return whether the APDU is an SIO case 1 APDU
	 */
	public static boolean isIsoCase1(byte[] apdu) {
		return isIsoCase1(getISOcaseExtended(apdu));
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided byte represents an SIO case 2 APDU
	 * @param isoCaseExtended isoCase according to getISOcaseExtended(APDU):byte
	 * @return whether the provided byte represents an SIO case 2 APDU
	 */
	private static boolean isIsoCase2(byte isoCaseExtended) {
		return ((isoCaseExtended == ISO_CASE_2_SHORT_LE) || (isoCaseExtended == ISO_CASE_2_EXTENDED_L_E));
	}
	
	/**
	 * Returns whether the APDU is an SIO case 2 APDU
	 * @param apdu the APDU
	 * @return whether the APDU is an SIO case 2 APDU
	 */
	public static boolean isIsoCase2(byte[] apdu) {
		return isIsoCase2(getISOcaseExtended(apdu));
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided byte represents an SIO case 3 APDU
	 * @param isoCaseExtended isoCase according to getISOcaseExtended(APDU):byte
	 * @return whether the provided byte represents an SIO case 3 APDU
	 */
	private static boolean isIsoCase3(byte isoCaseExtended) {
		return ((isoCaseExtended == ISO_CASE_3_SHORT_LC) || (isoCaseExtended == ISO_CASE_3_EXTENDED_L_C));
	}
	
	/**
	 * Returns whether the APDU is an SIO case 3 APDU
	 * @param apdu the APDU
	 * @return whether the APDU is an SIO case 3 APDU
	 */
	public static boolean isIsoCase3(byte[] apdu) {
		return isIsoCase3(getISOcaseExtended(apdu));
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided byte represents an SIO case 4 APDU
	 * @param isoCaseExtended isoCase according to getISOcaseExtended(APDU):byte
	 * @return whether the provided byte represents an SIO case 4 APDU
	 */
	private static boolean isIsoCase4(byte isoCaseExtended) {
		return ((isoCaseExtended == ISO_CASE_4_SHORT_LC_SHORT_LE) || (isoCaseExtended == ISO_CASE_4_EXTENDED_L_C_EXTENDED_L_E));
	}
	
	/**
	 * Returns whether the APDU is an SIO case 4 APDU
	 * @param apdu the APDU
	 * @return whether the APDU is an SIO case 4 APDU
	 */
	public static boolean isIsoCase4(byte[] apdu) {
		return isIsoCase4(getISOcaseExtended(apdu));
	}
	
	/*----------------------------------------------------------------*/
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the APDU uses extended length L_C and/or L_E field(s).
	 * NOTE: ISO 7816-4 does not allow for mixed L_C/L_E fields
	 * @param apdu the APDU
	 * @return whether the APDU uses extended length L_C and/or L_E field(s)
	 */
	public static boolean isExtendedLengthLCLE(byte[] apdu) {
		return (getISOcaseExtended(apdu) > ISO_CASE_4);
	}
	
	/*----------------------------------------------------------------*/
	/*----------------------------------------------------------------*/
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the APDU's CLA byte indicates command chaining
	 * @param apdu the APDU
	 * @return whether the APDU's CLA byte indicates command chaining
	 */
	public  static boolean isCommandChainingCLA(byte[] apdu) {
		byte cla = getClassByte(apdu);
		return ((byte) (cla & (byte) 0x10) == (byte) 0x10);
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns the kind of secure messaging the provided APDU's CLA byte indicates
	 * according to ISO 7816-4:
	 * <ul>
	 * <li>0  0  - No SM or no indication</li> 
	 * <li>0  1  - Proprietary SM format </li>
	 * <li>1  0  - SM according to 6, command header not processed according to 6.2.3.1</li>
	 * <li>1  1  - SM according to 6, command header authenticated according to 6.2.3.1</li>
	 * </ul>
	 * <p/>
	 * @param apdu the APDU
	 * @return the kind of secure messaging the provided APDU's CLA byte indicates one of
	 * {@link Iso7816#SM_OFF_OR_NO_INDICATION},<br/>
	 * {@link Iso7816#SM_PROPRIETARY},<br/>
	 * {@link Iso7816#SM_COMMAND_HEADER_NOT_PROCESSED},<br/>
	 * {@link Iso7816#SM_COMMAND_HEADER_AUTHENTICATED},
	 */
	public static byte getSecureMessagingStatus(byte[] apdu) {

		byte cla = apdu[OFFSET_CLA];
		byte isoFormat = getISOFormat(apdu);
		
		if(isoFormat == ISO_FORMAT_FIRSTINTERINDUSTRY) {
			cla = (byte) (cla & (byte) 0x0C);
			return (byte) (cla >> 2);
		} else{
			if(isoFormat == ISO_FORMAT_FURTHERINTERINDUSTRY) {
				cla = (byte) (cla & (byte) 0x20);
				return (byte) (cla >> 4);
			} else{
				if(isoFormat >= ISO_FORMAT_PROPRIETARY) {					
					ISO7816Exception.throwIt(Iso7816.SW_6E00_CLA_NOT_SUPPORTED);
				} else{
					// exception is thrown implicitly by called function.
				}
				
				return Byte.MIN_VALUE;
			}
		}
	}
	
	/**
	 * Returns whether the provided APDU matches the provided secure messaging indication
	 * according to ISO 7816-4:
	 * 0  0  - No SM or no indication 
	 * 0  1  - Proprietary SM format 
	 * 1  0  - SM according to 6, command header not processed according to 6.2.3.1
	 * 1  1  - SM according to 6, command header authenticated according to 6.2.3.1
	 * @param apdu the APDU
	 * @param specification some secure messaging status
	 * @return whether the provided APDU matches the provided secure messaging status
	 */
	public static boolean isSecureMessagingCLA(byte[] apdu, byte specification) {
		return specification == getSecureMessagingStatus(apdu);
	}
	
	/**
	 * Change the SM encoding in class byte according to its iso format.
	 * 
	 * Only works for further interindustry class bytes.
	 * 
	 * 
	 * Otherwise throws a RuntimeException.
	 * 
	 * Supported :
	 * {@link Iso7816#SM_OFF_OR_NO_INDICATION}
	 * {@link Iso7816#SM_PROPRIETARY}
	 * {@link Iso7816#SM_COMMAND_HEADER_NOT_PROCESSED}
	 * {@link Iso7816#SM_COMMAND_HEADER_AUTHENTICATED}
	 * @param cla class byte to modify
	 * @return the modified class byte
	 */
	public static byte setSecureMessagingStatus(byte cla, byte smStatus) {
		switch (getISOFormat((cla))) {
		case ISO_FORMAT_FIRSTINTERINDUSTRY:
			switch (smStatus) {
			case SM_OFF_OR_NO_INDICATION: //fall through
			case SM_PROPRIETARY: //fall through
			case SM_COMMAND_HEADER_NOT_PROCESSED: //fall through
			case SM_COMMAND_HEADER_AUTHENTICATED:
				// keep command chaining and channels, add sm status
				return (byte) ((cla & 0x13) | (smStatus << 2));

			default:
				throw new IllegalArgumentException(
						"Unsupported status for first interindustry CLA");
			}
		case ISO_FORMAT_FURTHERINTERINDUSTRY:
			switch (smStatus) {
			case SM_OFF_OR_NO_INDICATION:
				return (byte) ((cla & 0x5F));
			case SM_COMMAND_HEADER_NOT_PROCESSED:
				return (byte) ((cla & 0x5F) | 0x20);
			default:
				throw new IllegalArgumentException(
						"Unsupported status for further interindustry CLA");
			}
		default:
			throw new IllegalArgumentException("Format of CLA not supported");
		}
	}
	
	/*----------------------------------------------------------------*/
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns the channel indicated by the CLA byte of the provided APDU
	 * @param apdu the APDU
	 * @param isoFormat the ISO format of the provided APDU
	 * @return the channel indicated by the CLA byte of the provided APDU
	 */
	public static byte getChannel(byte[] apdu, byte isoFormat) {
		byte cla, channel;
		
		cla = apdu[OFFSET_CLA];
		
		if(isoFormat == ISO_FORMAT_FIRSTINTERINDUSTRY) {
			cla = (byte) (cla & (byte) 0x03);
			channel = cla;
		} else{
			if(isoFormat == ISO_FORMAT_FURTHERINTERINDUSTRY) {
				cla = (byte) (cla & (byte) 0x0F);
				cla = (byte) (cla << 2);
				channel = (byte) (cla | (byte) 0x03);
			} else{
				// exception is thrown implicitly by called function.
				channel = Byte.MIN_VALUE;
			}
		}
		
		return channel;
	}
	
	/**
	 * Returns the channel indicated by the CLA byte of the provided APDU
	 * @param apdu the APDU
	 * @return the channel indicated by the CLA byte of the provided APDU
	 */
	public static byte getChannel(byte[] apdu) {
		return getChannel(apdu, getISOFormat(apdu));
	}
	
	/**
	 * Returns whether the provided APDU uses the default channel '0'
	 * @param apdu the APDU
	 * @param isoFormat the ISO format of the rpovided APDU
	 * @return whether the provided APDU uses the default channel '0'
	 */
	public static boolean usesDefaultChannel(byte[] apdu, byte isoFormat) {
		return getChannel(apdu, isoFormat) == CH_DEFAULT;
	}
	
	/**
	 * Returns whether the provided APDU uses the default channel '0'
	 * @param apdu the APDU
	 * @return whether the provided APDU uses the default channel '0'
	 */
	public static boolean usesDefaultChannel(byte[] apdu) {
		return getChannel(apdu) == CH_DEFAULT;
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns the class byte (CLA) of the provided APDU
	 * @param apdu the APDU
	 * @return the instruction byte of the provided APDU
	 */
	public static byte getClassByte(byte[] apdu) {
		return apdu[OFFSET_CLA];
	}
	
	/*----------------------------------------------------------------*/
	
	/**
	 * Returns the instruction byte (INS) of the provided APDU
	 * @param apdu the APDU
	 * @return the instruction byte of the provided APDU
	 */
	public static byte getInstructionByte(byte[] apdu) {
		return apdu[OFFSET_INS];
	}

	/*----------------------------------------------------------------*/
	
	/**
	 * Returns whether the instruction byte indicates an invalid ISO7816-4, 5.1.2 Instruction byte
	 * @param ins the instruction byte
	 * @return whether the instruction byte indicates an invalid ISO7816-4, 5.1.2 Instruction byte
	 */
	public static boolean isInvalidInstruction(byte ins) {
		ins &= (byte) 0xF0;
		return (ins == (byte) 0x60) || (ins == (byte) 0x90);
	}
	
	/**
	 * Returns whether the provided APDU indicates an invalid ISO7816-4, 5.1.2 Instruction byte
	 * @param apdu the APDU
	 * @return whether the provided APDU indicates an invalid ISO7816-4, 5.1.2 Instruction byte
	 */
	public static boolean isInvalidInstruction(byte[] apdu) {
		return isInvalidInstruction(getInstructionByte(apdu));
	}
	
	/**
	 * Returns whether the provided instruction byte is known according to ISO7816-4
	 * @param ins the instruction byte
	 * @return whether the provided instruction byte is known according to ISO7816-4
	 */
	public static boolean isKnowInstruction(byte ins) {
		for(int i=0; i<Iso7816.INSTRUCTIONSET.length; i++) {
			if(Iso7816.INSTRUCTIONSET[i] == ins) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns whether the provided APDU holds an instruction byte known according to ISO7816-4
	 * @param apdu the APDU
	 * @return whether the provided APDU holds an instruction byte known according to ISO7816-4
	 */
	public static boolean isKnowInstruction(byte[] apdu) {
		return isKnowInstruction(getInstructionByte(apdu));
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns the P1 byte of the provided APDU
	 * @param apdu the APDU
	 * @return the P1 byte of the provided APDU
	 */
	public static byte getP1(byte[] apdu) {
		return apdu[OFFSET_P1];
	}
	
	/**
	 * Returns the P2 byte of the provided APDU
	 * @param apdu the APDU
	 * @return the P2 byte of the provided APDU
	 */
	public static byte getP2(byte[] apdu) {
		return apdu[OFFSET_P2];
	}
	
	/**
	 * Returns the P1P2 bytes of the provided APDU
	 * @param apdu the APDU
	 * @return the P1P2 bytes of the provided APDU
	 */
	public static short getP1P2(byte[] apdu) {
		return Utils.concatenate(getP1(apdu), getP2(apdu));
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns value N_c as encoded in L_c field of the provided APDU
	 * @param apdu the APDU
	 * @param isoCase the ISO case of the provided APDU
	 * @return the N_c encoded in the provided APDU
	 */
	public static short getNc(byte[] apdu) {
		if(getISOcase(apdu) < ISO_CASE_3) {
			return 0;
		}
		
		if(isExtendedLengthLCLE(apdu)) {
			return Utils.concatenate(apdu[OFFSET_LC + 1], apdu[OFFSET_LC + 2]);
		} else{
			return Utils.maskUnsignedByteToShort(apdu[OFFSET_LC]);
		}
	}
	
	/**
	 * Returns value N_e as encoded in L_e field of the provided APDU
	 * @param apdu the APDU
	 * @param isoCase the ISO case of the provided APDU
	 * @param isExtendedLengthLCLE whether the APDU uses extended length L_C/L_E fields
	 * @return the N_e encoded in the provided APDU
	 */
	public static int getNe(byte[] apdu) {
		byte isoCase = getISOcase(apdu); 
		
		if((isoCase == ISO_CASE_2) || (isoCase == ISO_CASE_4)) {
			int apduLength = apdu.length;
			
			if(isExtendedLengthLCLE(apdu)) {
				short retVal = Utils.concatenate(apdu[apduLength - 2], apdu[apduLength - 1]);
				if (retVal <= 0) {
					return 65536 + retVal;
				} else {
					return retVal;
				}
			} else{
				if (apdu[apduLength - 1] == 0) {
					return 256;
				} else {
					return Utils.maskUnsignedByteToShort(apdu[apduLength - 1]);
				}
			}
		} else{
			//L_e absent, thus N_e is zero
			return 0;
		}
	}
	
	public static short getStatusWord(byte [] responseApdu) {
		if (responseApdu.length < 2) {
			throw new IllegalArgumentException("APDU too short for status word");
		}
		
		return Utils.concatenate(responseApdu[responseApdu.length - 2], responseApdu[responseApdu.length - 1]);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns the offset of the L_C field of any APDU
	 * @param isoCase the ISO case of the APDU
	 * @return the offset of the L_C field of any APDU
	 */
	public static short getOffsetLc(byte isoCase) {
		if((isoCase == Iso7816.ISO_CASE_3) || (isoCase == Iso7816.ISO_CASE_4)) {
			return Iso7816.OFFSET_LC;
		} else{
			ISO7816Exception.throwIt(SW_6700_WRONG_LENGTH);
			return Short.MIN_VALUE;
		}
	}
	
	/**
	 * Returns the offset of the L_C field of the provided APDU
	 * @param apdu the APDU
	 * @return the offset of the L_C field of the provided APDU
	 */
	public static short getOffsetLc(byte[] apdu) {
		return getOffsetLc(getISOcase(apdu));
	}
	
	/**
	 * Returns the offset of the data field of any APDU
	 * @param isoCase the ISO case of the APDU
	 * @param isExtendedLengthLCLE whether the APDU uses extended length L_C/L_E fields
	 * @return the offset of the data field of any APDU
	 */
	public static short getOffsetData(byte isoCase, boolean isExtendedLengthLCLE) {
		if((isoCase < ISO_CASE_1) || (isoCase > ISO_CASE_4)) {
			throw new IllegalArgumentException(isoCase + " is no valid ISO case");
		}
		
		if((isoCase == Iso7816.ISO_CASE_3) || (isoCase == Iso7816.ISO_CASE_4)) {
			if(isExtendedLengthLCLE) {
				return Iso7816.OFFSET_CDATA_EXTENDED_LC;
			} else{
				return Iso7816.OFFSET_CDATA_SHORT_LC;
			}
		} else{
			ISO7816Exception.throwIt(SW_6700_WRONG_LENGTH);
			return Short.MIN_VALUE;
		}
	}
	
	/**
	 * Returns the offset of the data field of the provided APDU
	 * @param apdu the APDU
	 * @return the offset of the data field of the provided APDU
	 */
	public static short getOffsetData(byte[] apdu) {
		return getOffsetData(getISOcase(apdu), isExtendedLengthLCLE(apdu));
	}
	
	/**
	 * Returns the offset of the L_E field of an APDU
	 * @param apduLength the length of the APDU in bytes
	 * @param isoCase the ISO case of the APDU
	 * @param isExtendedLengthLCLE whether the APDU uses extended length L_C/L_E fields
	 * @return the offset of the L_E field of an APDU
	 */
	public static short getOffsetLe(int apduLength, byte isoCase, boolean isExtendedLengthLCLE) {
		if((isoCase == Iso7816.ISO_CASE_2) || (isoCase == Iso7816.ISO_CASE_4)) {
			if(isExtendedLengthLCLE) {
				return (short) (apduLength - 2);
			} else{
				return (short) (apduLength - 1);
			}
		} else{
			ISO7816Exception.throwIt(SW_6700_WRONG_LENGTH);
			return Short.MIN_VALUE;
		}
	}
	
	/**
	 * Returns the offset of the L_E field of the provided APDU
	 * @param apdu the APDU
	 * @return the offset of the L_E field of the provided APDU
	 */
	public static int getOffsetLe(byte[] apdu) {
		return getOffsetLe(apdu.length, getISOcase(apdu), isExtendedLengthLCLE(apdu));
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns whether the provided status word is reporting an error, i.e.
	 * statusWord = 0x9000 or 0x6100 <= status word <= 0x61FF
	 * @param statusWord
	 * @return
	 */
	public static boolean isReportingNormalProcessing(short statusWord) {
		return (statusWord == SW_9000_NO_ERROR) || ((statusWord >= 24832) && (statusWord <= 25087));
	}
	
	/**
	 * Returns whether the provided status word is reporting an error, i.e.
	 * 0x6400 <= status word <= 0x6FFF
	 * @param statusWord the status word to be checked
	 * @return whether the provided status word is reporting an error
	 */
	public static boolean isReportingError(short statusWord) {
		return (statusWord >= 25600) && (statusWord <= 458751);
	}
	
	/**
	 * Returns whether the provided status word is reporting a warning, i.e.
	 * 0x6200 <= status word <= 0x63FF
	 * @param statusWord the status word to be checked
	 * @return whether the provided status word is reporting a warning
	 */
	public static boolean isReportingWarning(short statusWord) {
		return (statusWord >= 25088) && (statusWord <= 25599);
	}
	
	/**
	 * Returns whether the provided status word is invalid.
	 * Due to specifications in ISO/IEC 7816-3, any value different
	 * from '6XXX' and '9XXX' is invalid; any value '60XX' is also invalid.
	 * @param statusWord the status word to be checked
	 * @return whether the provided status word is invalid
	 */
	public static boolean isInvalidStatusWord(short statusWord) {
		short swcopy;
		
		swcopy = statusWord;
		swcopy &= (short) 0xFF00;
		
		if(swcopy == (short) 0x6000) {
			return false;
		}
		
		swcopy &= (short) 0xF000;
		
		return !((swcopy == (short) 0x6000) || (swcopy == (short) 0x9000));
	}
	
	/**
	 * Returns whether the provided status word is interindustry.
	 * The values '61XX', '62XX', '63XX', '64XX', '65XX', '66XX',
	 * '68XX', '69XX', '6AXX' and '6CXX' are interindustry.
	 * The values '6700', '6B00', '6D00', '6E00', '6F00' and '9000'
	 * are also interindustry.
	 * @param statusWord the status word to be checked
	 * @return whether the provided status word is interindustry
	 */
	public static boolean isInterIndustryStatusWord(short statusWord) {
		if((statusWord >= 24832) && (statusWord <= 26367)) {return true;}
		
		if((statusWord >= 26880) && (statusWord <= 27135)) {return true;}
		
		if((statusWord >= 271362) && (statusWord <= 27391)) {return true;}
		
		if((statusWord >= 27648) && (statusWord <= 27903)) {return true;}
		
		if((statusWord == SW_6700_WRONG_LENGTH) || (statusWord == SW_6B00_WRONG_P1P2)
				|| (statusWord == SW_6D00_INS_NOT_SUPPORTED) || (statusWord == SW_6E00_CLA_NOT_SUPPORTED)
				|| (statusWord == SW_6F00_UNKNOWN) || (statusWord == SW_9000_NO_ERROR))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns whether the provided status word is proprietary.
	 * Due  to specifications  in ISO/IEC 7816-3, the  values '67XX',
	 * '6BXX', '6DXX', '6EXX', '6FXX' and '9XXX' are proprietary,
	 * except the values '6700', '6B00', '6D00', '6E00', '6F00' and
	 * '9000' that are interindustry.
	 * @param statusWord the status word to be checked
	 * @return whether the provided status word is proprietary.
	 */
	public static boolean isProprietaryStatusWord(short statusWord) {
		if((statusWord == SW_6700_WRONG_LENGTH) || (statusWord == SW_6B00_WRONG_P1P2)
				|| (statusWord == SW_6D00_INS_NOT_SUPPORTED) || (statusWord == SW_6E00_CLA_NOT_SUPPORTED)
				|| (statusWord == SW_6F00_UNKNOWN) || (statusWord == SW_9000_NO_ERROR))
		{
			return false;
		}
		
		short swcopy;
		
		swcopy = statusWord;
		swcopy &= (short) 0xFF00;
		
		if((swcopy == SW_6700_WRONG_LENGTH) || (swcopy == SW_6B00_WRONG_P1P2)
				|| (swcopy == SW_6D00_INS_NOT_SUPPORTED) || (swcopy == SW_6E00_CLA_NOT_SUPPORTED)
				|| (swcopy == SW_6F00_UNKNOWN))
		{
			return true;
		}
		
		swcopy &= (short) 0xF000;
		
		if(swcopy == (short) 9000)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns whether the provided status word allows for return data to be sent with the response APDU.
	 * Return data is allowed if the status word is valid and does not indicate an error.
	 * @param statusWord the status word to be checked
	 * @return whether the provided status word allows for return data to be sent with the response APDU.
	 */
	public static boolean statusWordAllowsReturnData(short statusWord) {
		return isReportingNormalProcessing(statusWord) || isReportingWarning(statusWord);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns a TLV structure of the APDU's command data field.
	 * @param apdu the APDU
	 * @return a TLV structure of the APDU's command data field.
	 */
	public static TlvValue getCommandData(byte[] apdu) {
		short offsetData = getOffsetData(apdu);
		short nc = getNc(apdu);
		return new TlvValuePlain(apdu, offsetData, offsetData + nc);
	}
	
	/**
	 * Returns whether the APDU's header indicates the command data field to encode TLV.
	 * @param apdu the APDU.
	 * @param ins the instruction byte.
	 * @param p1 the P1 byte.
	 * @param p2 the P2 byte.
	 * @return whether the APDU's header indicates the command data field to encode TLV.
	 */
	public static boolean commandDataEncodesTLV(byte[] apdu, byte ins, byte p1, byte p2) {
		/* if bit 1 of INS byte is set to one (c.f. ISO7816-4 5.1.2 Instruction byte) */
		if(((byte) (ins & (byte) 0x01)) == (byte) 0x01) {
			return true;
		}
		
		/* or if INS byte != 0x04, i.e. "SELECT" */
		if((ins == (byte) 0xA4) && (p1 == (byte) 0x04)) {
			return false;
		}
		
		/* if in doubt return "true" as default value */
		return true;
	}
	
	/**
	 * Returns a textual representation of the Secure Messaging status.
	 * @param sm the SM byte.
	 * @return a textual representation of the Secure Messaging status.
	 */
	public static String getSMAsString(byte sm) {
		switch (sm) {

		case SM_OFF_OR_NO_INDICATION:
			return SM_OFF_OR_NO_INDICATION_STRING;
		case SM_PROPRIETARY:
			return SM_PROPRIETARY_STRING;
		case SM_COMMAND_HEADER_NOT_PROCESSED:
			return SM_COMMAND_HEADER_NOT_PROCESSED_STRING;
		case SM_COMMAND_HEADER_AUTHENTICATED:
			return SM_COMMAND_HEADER_AUTHENTICATED_STRING;
		default:
			/* Intentionally left blank */
			return "";
		}
	}
	
	/*----------------------------------------------------------------*/
	/*---------------------------- BER-TLV ---------------------------*/
	/*----------------------------------------------------------------*/
	
	public static byte[] getTLVLength(int dataLengthInBytes) {
		if(dataLengthInBytes < 0) {throw new IllegalArgumentException("data length in bytes must be greater or equal 0");}
		
		byte[] lengthField, length;
		
		length = Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(dataLengthInBytes));
		
		if(length.length > 4) {ISO7816Exception.throwIt(SW_6A85_NC_INCONSISTENT_WITH_TLV_STRUCTURE, "data too long");}
		
		if(dataLengthInBytes <= 127) {
			lengthField = new byte[1];
			lengthField[0] = (byte) dataLengthInBytes;
		} else{
			lengthField = new byte[length.length + 1];
			
			lengthField[0] = (byte) 0x8F;
			lengthField[0] = (byte) (lengthField[0] & (byte) (((byte) lengthField.length) & ((byte) 0x0F)));
			
			System.arraycopy(length, 0, lengthField, 1, length.length);
		}
		
		return lengthField;
	}
}
