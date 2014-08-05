package de.persosim.simulator.platform;

/**
 * @author slutters
 *
 */
public interface Iso7816 {
	/*
	 * APDU byte offsets as defined in ISO7816-4
	 */
	public static final byte OFFSET_HEADER             = (byte) 0;
	public static final byte OFFSET_CLA                = (byte) 0;
	public static final byte OFFSET_INS                = (byte) 1;
	public static final byte OFFSET_P1                 = (byte) 2;
	public static final byte OFFSET_P2                 = (byte) 3;
	public static final byte OFFSET_LC                 = (byte) 4;
	public static final byte OFFSET_LE_ISO_CASE_2      = (byte) 4;
	public static final byte OFFSET_CDATA_SHORT_LC     = (byte) 5;
	public static final byte OFFSET_CDATA_EXTENDED_LC  = (byte) 7;
	
	
	
	public static final byte APDU_MINIMUM_LENGTH_ISO_CASE1 = 4;
	public static final byte APDU_MAXIMUM_LENGTH_ISO_CASE1 = 4;
	public static final byte APDU_MINIMUM_LENGTH_ISO_CASE2 = 5;
	public static final byte APDU_MAXIMUM_LENGTH_ISO_CASE2 = 7;
	public static final byte APDU_MINIMUM_LENGTH_ISO_CASE3 = 6;
	public static final byte APDU_MINIMUM_LENGTH_EXTENDED_ISO_CASE3 = 8;
	public static final byte APDU_MINIMUM_LENGTH_ISO_CASE4 = 7;
	
	
	
	public static final byte ISO_FORMAT_INVALID = -2;
	public static final byte ISO_FORMAT_PROPRIETARY = -1;
//	public static final byte ISO_FORMAT_INTERINDUSTRY = 1;
	public static final byte ISO_FORMAT_FIRSTINTERINDUSTRY = 1;
	public static final byte ISO_FORMAT_FURTHERINTERINDUSTRY = 2;
	public static final byte ISO_FORMAT_INTERINDUSTRY_RESERVED = 3;
	
	
	
	public static final byte ISO_CASE_1 = 1;
	public static final byte ISO_CASE_2 = 2;
	public static final byte ISO_CASE_3 = 3;
	public static final byte ISO_CASE_4 = 4;
	
	
	
	public static final boolean CHAINING_ON  = true;
	public static final boolean CHAINING_OFF = false;
	
	
	
	/*
	 * APDU CLA byte information
	 */
//	public static final byte SM_UNDETERMINED = (byte) 0xFF;
//	public static final byte SM_NOT_APPLICABLE = (byte) 0xFE;
	public static final byte SM_OFF_OR_NO_INDICATION = (byte) 0x00;
	public static final byte SM_PROPRIETARY = (byte) 0x01;
	public static final byte SM_COMMAND_HEADER_NOT_PROCESSED = (byte) 0x02;
	public static final byte SM_COMMAND_HEADER_AUTHENTICATED = (byte) 0x03;
	
	public static final String SM_OFF_OR_NO_INDICATION_STRING = "No SM or no indication";
	public static final String SM_PROPRIETARY_STRING = "Proprietary SM format";
	public static final String SM_COMMAND_HEADER_NOT_PROCESSED_STRING = "SM, command header not processed";
	public static final String SM_COMMAND_HEADER_AUTHENTICATED_STRING = "SM, command header authenticated";
	
	
	
//	public static final byte CH_UNDETERMINED = (byte) 0xFF;
//	public static final byte CH_NOT_APPLICABLE = (byte) 0xFE;
	public static final byte CH_DEFAULT = (byte) 0x00;
	
	
	
	/*
	 * INS instruction bytes in byte order as defined in ISO7816-4
	 */
	public static final byte INS_04_DEACTIVATE_FILE                  = (byte) 0x04;
	public static final byte INS_0C_ERASE_RECORD                     = (byte) 0x0C;
	public static final byte INS_0E_ERASE_BINARY                     = (byte) 0x0E;
	public static final byte INS_0F_ERASE_BINARY                     = (byte) 0x0F;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_10_PERFORM_SCQL_OPERATION           = (byte) 0x10;
	public static final byte INS_12_PERFORM_TRANSACTION_OPERATION    = (byte) 0x12;
	public static final byte INS_14_PERFORM_USER_OPERATION           = (byte) 0x14;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_20_VERIFY                           = (byte) 0x20;
	public static final byte INS_21_VERIFY                           = (byte) 0x21;
	public static final byte INS_22_MANAGE_SECURITY_ENVIRONMENT      = (byte) 0x22;
	public static final byte INS_24_CHANGE_REFERENCE_DATA            = (byte) 0x24;
	public static final byte INS_26_DISABLE_VERIFICATION_REQUIREMENT = (byte) 0x26;
	public static final byte INS_28_ENABLE_VERIFICATION_REQUIREMENT  = (byte) 0x28;
	public static final byte INS_2A_PERFORM_SECURITY_OPERATION       = (byte) 0x2A;
	public static final byte INS_2C_RESET_RETRY_COUNTER              = (byte) 0x2C;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_44_ACTIVATE_FILE                    = (byte) 0x44;
	public static final byte INS_46_GENERATE_ASYMMETRIC_KEY_PAIR     = (byte) 0x46;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_70_MANAGE_CHANNEL                   = (byte) 0x70;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_82_EXTERNAL_AUTHENTICATE            = (byte) 0x82;
	public static final byte INS_82_MUTUAL_AUTHENTICATE              = (byte) 0x82;
	public static final byte INS_84_GET_CHALLENGE                    = (byte) 0x84;
	public static final byte INS_86_GENERAL_AUTHENTICATE             = (byte) 0x86;
	public static final byte INS_87_GENERAL_AUTHENTICATE             = (byte) 0x87;
	public static final byte INS_88_INTERNAL_AUTHENTICATE            = (byte) 0x88;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_A0_SEARCH_BINARY                    = (byte) 0xA0;
	public static final byte INS_A1_SEARCH_BINARY                    = (byte) 0xA1;
	public static final byte INS_A2_SEARCH_RECORD                    = (byte) 0xA2;
	public static final byte INS_A4_SELECT                           = (byte) 0xA4;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_B0_READ_BINARY                      = (byte) 0xB0;
	public static final byte INS_B1_READ_BINARY                      = (byte) 0xB1;
	public static final byte INS_B2_READ_RECORD                      = (byte) 0xB2;
	public static final byte INS_B3_READ_RECORD                      = (byte) 0xB3;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_C0_GET_RESPONSE                     = (byte) 0xC0;
	public static final byte INS_C2_ENVELOPE                         = (byte) 0xC2;
	public static final byte INS_C3_ENVELOPE                         = (byte) 0xC3;
	public static final byte INS_CA_GET_DATA                         = (byte) 0xCA;
	public static final byte INS_CB_GET_DATA                         = (byte) 0xCB;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_D0_WRITE_BINARY                     = (byte) 0xD0;
	public static final byte INS_D1_WRITE_BINARY                     = (byte) 0xD1;
	public static final byte INS_D2_WRITE_RECORD                     = (byte) 0xD2;
	public static final byte INS_D6_UPDATE_BINARY                    = (byte) 0xD6;
	public static final byte INS_D7_UPDATE_BINARY                    = (byte) 0xD7;
	public static final byte INS_DA_PUT_DATA                         = (byte) 0xDA;
	public static final byte INS_DB_PUT_DATA                         = (byte) 0xDB;
	public static final byte INS_DC_UPDATE_RECORD                    = (byte) 0xDC;
	public static final byte INS_DD_UPDATE_RECORD                    = (byte) 0xDD;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_E0_CREATE_FILE                      = (byte) 0xE0;
	public static final byte INS_E2_APPEND_RECORD                    = (byte) 0xE2;
	public static final byte INS_E4_DELETE_FILE                      = (byte) 0xE4;
	public static final byte INS_E6_TERMINATE_DF                     = (byte) 0xE6;
	public static final byte INS_E8_TERMINATE_EF                     = (byte) 0xE8;
	/*--------------------------------------------------------------------------------*/
	public static final byte INS_FE_TERMINATE_CARD_USAGE             = (byte) 0xFE;
	public static final byte INS_FF_INVALID                          = (byte) 0xFF;
	
	/*
	 * Set of _all_ INS byte values defined in ISO7816-4 and represented by the table above.
	 */
	public static final byte[] INSTRUCTIONSET = {INS_04_DEACTIVATE_FILE, INS_0C_ERASE_RECORD, INS_0E_ERASE_BINARY, INS_0F_ERASE_BINARY,
		INS_10_PERFORM_SCQL_OPERATION, INS_12_PERFORM_TRANSACTION_OPERATION, INS_14_PERFORM_USER_OPERATION,
		INS_20_VERIFY, INS_21_VERIFY, INS_22_MANAGE_SECURITY_ENVIRONMENT, INS_24_CHANGE_REFERENCE_DATA, INS_26_DISABLE_VERIFICATION_REQUIREMENT,
			INS_28_ENABLE_VERIFICATION_REQUIREMENT, INS_2A_PERFORM_SECURITY_OPERATION, INS_2C_RESET_RETRY_COUNTER,
		INS_44_ACTIVATE_FILE, INS_46_GENERATE_ASYMMETRIC_KEY_PAIR,
		INS_70_MANAGE_CHANNEL,
		INS_82_EXTERNAL_AUTHENTICATE, INS_84_GET_CHALLENGE, INS_86_GENERAL_AUTHENTICATE, INS_87_GENERAL_AUTHENTICATE, INS_88_INTERNAL_AUTHENTICATE,
		INS_A0_SEARCH_BINARY, INS_A1_SEARCH_BINARY, INS_A2_SEARCH_RECORD, INS_A4_SELECT,
		INS_B0_READ_BINARY, INS_B1_READ_BINARY, INS_B2_READ_RECORD, INS_B3_READ_RECORD,
		INS_C0_GET_RESPONSE, INS_C2_ENVELOPE, INS_C3_ENVELOPE, INS_CA_GET_DATA, INS_CB_GET_DATA,
		INS_D0_WRITE_BINARY, INS_D1_WRITE_BINARY, INS_D2_WRITE_RECORD, INS_D6_UPDATE_BINARY, INS_D7_UPDATE_BINARY, INS_DA_PUT_DATA, INS_DB_PUT_DATA,
			INS_DC_UPDATE_RECORD, INS_DD_UPDATE_RECORD,
		INS_E0_CREATE_FILE, INS_E2_APPEND_RECORD, INS_E4_DELETE_FILE, INS_E6_TERMINATE_DF, INS_E8_TERMINATE_EF,
		INS_FE_TERMINATE_CARD_USAGE, INS_FF_INVALID};
	
	/*
	 * P1 according to ISO7816-4
	 */

	public static final byte P1_SELECT_FILE_MF_DF_EF 				   = (byte) 0x00;
	public static final byte P1_SELECT_FILE_CHILD_DF 				   = (byte) 0x01;
	public static final byte P1_SELECT_FILE_EF_UNDER_CURRENT_DF 	   = (byte) 0x02;
	public static final byte P1_SELECT_FILE_PARENT_DF 				   = (byte) 0x03;
	public static final byte P1_SELECT_FILE_DF_BY_NAME 				   = (byte) 0x04;
	public static final byte P1_SELECT_FILE_BY_PATH_FROM_MF 		   = (byte) 0x08;
	public static final byte P1_SELECT_FILE_BY_PATH_FROM_DF 		   = (byte) 0x09;
	
	// P1 for {@link #INS_2C_RESET_RETRY_COUNTER}
	public static final byte P1_RESET_RETRY_COUNTER_UNBLOCK_AND_CHANGE = (byte) 0x02;
	public static final byte P1_RESET_RETRY_COUNTER_CHANGE             = (byte) 0x02;
	public static final byte P1_RESET_RETRY_COUNTER_UNBLOCK            = (byte) 0x03;
	

	/*
	 * P2 according to ISO7816-4
	 */

	public static final byte P2_SELECT_OCCURRENCE_MASK                 = 0b00000011;
	public static final byte P2_SELECT_OCCURRENCE_FIRST                = (byte) 0b0000;
	public static final byte P2_SELECT_OCCURRENCE_LAST                 = (byte) 0b0001;
	public static final byte P2_SELECT_OCCURRENCE_NEXT                 = (byte) 0b0010;
	public static final byte P2_SELECT_OCCURRENCE_PREVIOUS             = (byte) 0b0011;

	public static final byte P2_SELECT_FCI_MASK                        = 0b00001100;
	public static final byte P2_SELECT_FCI_TEMPLATE                    = (byte) 0b0000;
	public static final byte P2_SELECT_FCP_TEMPLATE                    = (byte) 0b0100;
	public static final byte P2_SELECT_FMD_TEMPLATE                    = (byte) 0b1000;
	public static final byte P2_SELECT_NO_OR_PROPRIETARY               = (byte) 0b1100;
	
	
	/*
	 * SW1-SW2 according to ISO7816-4
	 */
	
	/* Normal processing */
	public static final short SW_9000_NO_ERROR                           = (short) 0x9000;
	/*--------------------------------------------------------------------------------*/
	public static final short SW_6100_BYTES_REMAINING                    = (short) 0x6100;
	/*--------------------------------------------------------------------------------*/
	/* Warning processing */
	public static final short SW_6200_WARNING_STATE_UNCHANGED            = (short) 0x6200;
	public static final short SW_6282_END_OF_FILE_REACHED_BEFORE_READING_NE_BYTES = (short) 0x6282;
	public static final short SW_6283_SELECTED_FILE_DEACTIVATED          = (short) 0x6283;
	/*--------------------------------------------------------------------------------*/
	public static final short SW_6300_AUTHENTICATION_FAILED              = (short) 0x6300;
	public static final short SW_63C0_COUNTER_IS_0                       = (short) 0x63C0;
	public static final short SW_63C1_COUNTER_IS_1                       = (short) 0x63C1;
	/*--------------------------------------------------------------------------------*/
	/* Execution error */
	public static final short SW_6400_EXECUTION_ERROR                    = (short) 0x6400;
	/*--------------------------------------------------------------------------------*/
	/* Checking error */
	public static final short SW_6700_WRONG_LENGTH                       = (short) 0x6700;
	/*--------------------------------------------------------------------------------*/
	public static final short SW_6800_FUNCTION_IN_CLA_NOT_SUPPORTED      = (short) 0x6800;
	public static final short SW_6881_LOGICAL_CHANNEL_NOT_SUPPORTED      = (short) 0x6881;
	public static final short SW_6882_SECURE_MESSAGING_NOT_SUPPORTED     = (short) 0x6882;
	public static final short SW_6883_LAST_COMMAND_EXPECTED              = (short) 0x6883;
	public static final short SW_6884_COMMAND_CHAINING_NOT_SUPPORTED     = (short) 0x6884;
	/*--------------------------------------------------------------------------------*/
	public static final short SW_6900_COMMAND_NOT_ALLOWED                = (short) 0x6900;
	public static final short SW_6982_SECURITY_STATUS_NOT_SATISFIED      = (short) 0x6982;
	public static final short SW_6983_FILE_INVALID                       = (short) 0x6983;
	public static final short SW_6984_REFERENCE_DATA_NOT_USABLE          = (short) 0x6984;
	public static final short SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED    = (short) 0x6985;
	public static final short SW_6986_COMMAND_NOT_ALLOWED_NO_EF          = (short) 0x6986;
	public static final short SW_6987_EXPECTED_SM_DATA_OBJECTS_MISSING   = (short) 0x6987;
	public static final short SW_6988_INCORRECT_SM_DATA_OBJECTS          = (short) 0x6988;
	public static final short SW_6999_APPLET_SELECT_FAILED               = (short) 0x6999;
	/*--------------------------------------------------------------------------------*/
	public static final short SW_6A00_WRONG_PARAMETERS_P1P2              = (short) 0x6A00;
	public static final short SW_6A80_WRONG_DATA                         = (short) 0x6A80;
	public static final short SW_6A81_FUNC_NOT_SUPPORTED                 = (short) 0x6A81;
	public static final short SW_6A82_FILE_NOT_FOUND                     = (short) 0x6A82;
	public static final short SW_6A83_RECORD_NOT_FOUND                   = (short) 0x6A83;
	public static final short SW_6A84_FILE_FULL                          = (short) 0x6A84;
	public static final short SW_6A85_NC_INCONSISTENT_WITH_TLV_STRUCTURE = (short) 0x6A85;
	public static final short SW_6A86_INCORRECT_PARAMETERS_P1P2          = (short) 0x6A86;
	public static final short SW_6A88_REFERENCE_DATA_NOT_FOUND           = (short) 0x6A88;
	/*--------------------------------------------------------------------------------*/
	public static final short SW_6B00_WRONG_P1P2                         = (short) 0x6B00;
	/*--------------------------------------------------------------------------------*/
	public static final short SW_6C00_CORRECT_LENGTH                     = (short) 0x6C00;
	/*--------------------------------------------------------------------------------*/
	public static final short SW_6D00_INS_NOT_SUPPORTED                  = (short) 0x6D00;
	/*--------------------------------------------------------------------------------*/
	public static final short SW_6E00_CLA_NOT_SUPPORTED                  = (short) 0x6E00;
	/*--------------------------------------------------------------------------------*/
	public static final short SW_6F00_UNKNOWN                            = (short) 0x6F00;
	public static final short SW_6FFF_IMPLEMENTATION_ERROR               = (short) 0x6FFF; // proprietary
	

	public static final int MINIMUM_SHORT_FILE_IDENTIFIER = 1;
	public static final int MAXIMUM_SHORT_FILE_IDENTIFIER = 30;
	
	public static final byte TAG_FILE_CONTROL_INFORMATION_TEMPLATE = 0x6F;
	public static final byte TAG_FILE_CONTROL_PARAMETERS_TEMPLATE = 0x62;
	public static final byte TAG_FILE_MANAGEMENT_DATA_TEMPLATE = 0x64;
}
