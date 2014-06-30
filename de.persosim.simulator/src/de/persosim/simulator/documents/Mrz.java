package de.persosim.simulator.documents;

import java.security.InvalidParameterException;

/**
 * Represents the machine readable zone of a passport.
 * 
 * Note: this class is under construction and will only provide basic functionality
 * 
 * @author Alexander May
 * @version 2.1.0
 * 
 */
@SuppressWarnings("all") // IMPL add functionality for all fields of MRZ
public abstract class Mrz {

	interface DocumentFormat {

		int getLengthMRZ();

		int getOffsetDocType();

		int getOffsetDocTypeEnd();

		int getOffsetIssuingState();

		int getOffsetIssuingStateEnd();

		int getOffsetName();

		int getOffsetNameEnd();

		int getOffsetDocNo();

		int getOffsetDocNoEnd();

		int getOffsetDocNoCd();

		int getOffsetDocNoCdEnd();

		int getOffsetNationality();

		int getOffsetNationalityEnd();

		int getOffsetDoB();

		int getOffsetDoBEnd();

		int getOffsetDoBCd();

		int getOffsetDoBCdEnd();

		int getOffsetSex();

		int getOffsetSexEnd();

		int getOffsetDoE();

		int getOffsetDoEEnd();

		int getOffsetDoECd();

		int getOffsetDoECdEnd();

		int getOffsetOptionalData();

		int getOffsetOptionalDataEnd();

		int getOffsetOptionalData2();

		int getOffsetOptionalData2End();

		int getOffsetOptionalDataCd();

		int getOffsetOptionalDataCdEnd();

		int getOffsetCompositeCd();

		int getOffsetCompositeCdEnd();

		String calculateCompositeCd(String mrz);

	}
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	
	
	protected static final DocumentFormat td1 = new DocumentFormat() {
		@Override
		public int getLengthMRZ() {return 90;}

		//first line
		@Override
		public int getOffsetDocType() {return 0;}
		@Override
		public int getOffsetDocTypeEnd() {return 2;}
		@Override
		public int getOffsetIssuingState() {return 2;}
		@Override
		public int getOffsetIssuingStateEnd() {return 5;}
		@Override
		public int getOffsetDocNo() {return 5;}
		@Override
		public int getOffsetDocNoEnd() {return 14;}
		@Override
		public int getOffsetDocNoCd() {return 14;}
		@Override
		public int getOffsetDocNoCdEnd() {return 15;}
		@Override
		public int getOffsetOptionalData() {return 15;}
		@Override
		public int getOffsetOptionalDataEnd() {return 30;}

		//second line
		@Override
		public int getOffsetDoB() {return 30;}
		@Override
		public int getOffsetDoBEnd() {return 36;}
		@Override
		public int getOffsetDoBCd() {return 36;}
		@Override
		public int getOffsetDoBCdEnd() {return 37;}
		@Override
		public int getOffsetSex() {return 37;}
		@Override
		public int getOffsetSexEnd() {return 38;}
		@Override
		public int getOffsetDoE() {return 38;}
		@Override
		public int getOffsetDoEEnd() {return 44;}
		@Override
		public int getOffsetDoECd() {return 44;}
		@Override
		public int getOffsetDoECdEnd() {return 45;}
		@Override
		public int getOffsetNationality() {return 45;}
		@Override
		public int getOffsetNationalityEnd() {return 48;}
		@Override
		public int getOffsetOptionalData2() {return 48;}
		@Override
		public int getOffsetOptionalData2End() {return 59;}
		@Override
		public int getOffsetCompositeCd() {return 59;}
		@Override
		public int getOffsetCompositeCdEnd() {return 60;}		
		
		//third line
		@Override
		public int getOffsetName() {return 60;}
		@Override
		public int getOffsetNameEnd() {return 90;}

		//not present
		@Override
		public int getOffsetOptionalDataCd() {return 0;}
		@Override
		public int getOffsetOptionalDataCdEnd() {return 0;}

		@Override
		public String calculateCompositeCd(String mrz) {
			String compositeData = mrz.substring(getOffsetDocNo(), getOffsetOptionalDataEnd());
			compositeData += mrz.substring(getOffsetDoB(), getOffsetDoBCdEnd());
			compositeData += mrz.substring(getOffsetDoE(), getOffsetDoECdEnd());
			compositeData += mrz.substring(getOffsetOptionalData2(), getOffsetOptionalData2End());
			
			return new String(new byte[] {computeChecksum(compositeData.getBytes(), 0, compositeData.length())});
		}

		
	};
	
	protected static final DocumentFormat td2 = new DocumentFormat() {
		@Override
		public int getLengthMRZ() {return 74;}

		//first line
		@Override
		public int getOffsetDocType() {return 0;}
		@Override
		public int getOffsetDocTypeEnd() {return 2;}
		@Override
		public int getOffsetIssuingState() {return 2;}
		@Override
		public int getOffsetIssuingStateEnd() {return 5;}
		@Override
		public int getOffsetName() {return 5;}
		@Override
		public int getOffsetNameEnd() {return 36;}

		//second line
		@Override
		public int getOffsetDocNo() {return 36;}
		@Override
		public int getOffsetDocNoEnd() {return 45;}
		@Override
		public int getOffsetDocNoCd() {return 45;}
		@Override
		public int getOffsetDocNoCdEnd() {return 46;}
		@Override
		public int getOffsetNationality() {return 46;}
		@Override
		public int getOffsetNationalityEnd() {return 49;}
		@Override
		public int getOffsetDoB() {return 49;}
		@Override
		public int getOffsetDoBEnd() {return 55;}
		@Override
		public int getOffsetDoBCd() {return 55;}
		@Override
		public int getOffsetDoBCdEnd() {return 56;}
		@Override
		public int getOffsetSex() {return 56;}
		@Override
		public int getOffsetSexEnd() {return 57;}
		@Override
		public int getOffsetDoE() {return 57;}
		@Override
		public int getOffsetDoEEnd() {return 63;}
		@Override
		public int getOffsetDoECd() {return 63;}
		@Override
		public int getOffsetDoECdEnd() {return 64;}
		@Override
		public int getOffsetOptionalData() {return 64;}
		@Override
		public int getOffsetOptionalDataEnd() {return 73;}
		@Override
		public int getOffsetCompositeCd() {return 73;}
		@Override
		public int getOffsetCompositeCdEnd() {return 74;}
		
		//not present
		@Override
		public int getOffsetOptionalDataCd() {return 0;}
		@Override
		public int getOffsetOptionalDataCdEnd() {return 0;}
		@Override
		public int getOffsetOptionalData2() {return 0;}
		@Override
		public int getOffsetOptionalData2End() {return 0;}

		@Override
		public String calculateCompositeCd(String mrz) {
			String compositeData = mrz.substring(getOffsetDocNo(), getOffsetDocNoCdEnd());
			compositeData += mrz.substring(getOffsetDoB(), getOffsetDoBCdEnd());
			compositeData += mrz.substring(getOffsetDoE(), getOffsetDoECdEnd());
			compositeData += mrz.substring(getOffsetOptionalData(), getOffsetOptionalDataEnd());

			return new String(new byte[] {computeChecksum(compositeData.getBytes(), 0, compositeData.length())});
		}
	};
	
	protected static final DocumentFormat mrp = new DocumentFormat() {
		@Override
		public int getLengthMRZ() {return 88;}

		//first line
		@Override
		public int getOffsetDocType() {return 0;}
		@Override
		public int getOffsetDocTypeEnd() {return 2;}
		@Override
		public int getOffsetIssuingState() {return 2;}
		@Override
		public int getOffsetIssuingStateEnd() {return 5;}
		@Override
		public int getOffsetName() {return 5;}
		@Override
		public int getOffsetNameEnd() {return 44;}

		//second line
		@Override
		public int getOffsetDocNo() {return 44;}
		@Override
		public int getOffsetDocNoEnd() {return 53;}
		@Override
		public int getOffsetDocNoCd() {return 53;}
		@Override
		public int getOffsetDocNoCdEnd() {return 54;}
		@Override
		public int getOffsetNationality() {return 54;}
		@Override
		public int getOffsetNationalityEnd() {return 57;}
		@Override
		public int getOffsetDoB() {return 57;}
		@Override
		public int getOffsetDoBEnd() {return 63;}
		@Override
		public int getOffsetDoBCd() {return 63;}
		@Override
		public int getOffsetDoBCdEnd() {return 64;}
		@Override
		public int getOffsetSex() {return 64;}
		@Override
		public int getOffsetSexEnd() {return 65;}
		@Override
		public int getOffsetDoE() {return 65;}
		@Override
		public int getOffsetDoEEnd() {return 71;}
		@Override
		public int getOffsetDoECd() {return 71;}
		@Override
		public int getOffsetDoECdEnd() {return 72;}
		@Override
		public int getOffsetOptionalData() {return 72;}
		@Override
		public int getOffsetOptionalDataEnd() {return 86;}
		@Override
		public int getOffsetOptionalDataCd() {return 86;}
		@Override
		public int getOffsetOptionalDataCdEnd() {return 87;}
		@Override
		public int getOffsetCompositeCd() {return 87;}
		@Override
		public int getOffsetCompositeCdEnd() {return 88;}

		//not present
		@Override
		public int getOffsetOptionalData2() {return 0;}
		@Override
		public int getOffsetOptionalData2End() {return 0;}
		
		@Override
		public String calculateCompositeCd(String mrz) {
			String compositeData = mrz.substring(getOffsetDocNo(), getOffsetDocNoCdEnd());
			compositeData += mrz.substring(getOffsetDoB(), getOffsetDoBCdEnd());
			compositeData += mrz.substring(getOffsetDoE(), getOffsetDoECdEnd());
			compositeData += mrz.substring(getOffsetOptionalData(), getOffsetOptionalDataCdEnd());

			return new String(new byte[] {computeChecksum(compositeData.getBytes(), 0, compositeData.length())});
		}
	};
	
	
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	
	
	public static final String Blank = " "; //$NON-NLS-1$
	public static final String Filler = "<"; //$NON-NLS-1$ character used to fill spaces
	
	private static final int LENGTH_COUNTRYCODE = 3; // length of the country code
	private static final int LENGTH_DOCTYPE = 2; // length of the type field
	// static constants
	private static final int LENGTH_MRZ = 88; // length of the MRZ record
	// of issuing state field
	private static final int LENGTH_NAME = 39; // length of the name field
	private static final int LENGTH_NO = 9; // length of the document number
	// field
	private static final int LENGTH_OPTIONALDATA = 14; // length of the optional
	// mrz
	private static final int OFFSET_CHECKSUM = 87; // offset of checksum in mrz
	// data field
	private static final int OFFSET_CODE = 2; // offset of the country code in
	// in mrz
	private static final int OFFSET_DOB = 57; // offset of date of birth in mrz
	private static final int OFFSET_DOE = 65; // offset of date of expiry in mrz
	private static final int OFFSET_NATIONALITY = 54; // offset of nationality
	private static final int OFFSET_NO = 44; // offset of document number in mrz
	private static final int OFFSET_ODATA = 72; // offset of optional data in
	private static final int OFFSET_SEX = 64; // offset of sex in mrz
	// mrz
	private static final int OFFSET_SURNAME = 5; // offset of name in mrz

	/**
	 * calculates the checksum as defined in ICAO Doc9303
	 * 
	 * This method calculates the checksum of the pbuffer as defined in ICAO
	 * Doc9303. For the calculation the elements pbufer[0] to pbuffer[length]
	 * are used. The offset has no influence on the elements that are used for
	 * calculating the checksum. It is used as an offset in the weight function.
	 * 
	 * @param pbuffer
	 *            buffer to calculate the checksum of
	 * 
	 * @param offset
	 *            offset used in the weight function
	 * 
	 * @param length
	 *            length of the buffer that should be used for the checksum
	 * @return
	 */
	private static byte computeChecksum(byte[] pbuffer, int offset, int length) {
		int[] weight = { 7, 3, 1 };
		int csum = 0;
		byte ctemp = (byte) 0x00;

		for (int i = 0; i < length; i++) {
			ctemp = pbuffer[i];
			/*
			 * '<' will be neglected '0' has value 0 (dec) ... '9' has value 9
			 * (dec) 'A' has value 10 (dec) ... 'Z' has value 35 (dec) the rest
			 * is 0 (dec)
			 */
			if ((ctemp >= 'A') && (ctemp <= 'Z')) {
				ctemp -= (byte) 0x37;
			} else if ((ctemp >= '0') && (ctemp <= '9')) {
				ctemp -= (byte) 0x30;
			} else {
				ctemp = 0;
			}
			csum += ctemp * weight[(i + offset) % 3];
		}

		ctemp = (byte) (csum % 10);
		ctemp += (byte) 0x30;
		return ctemp;
	}
	
	/*--------------------------------------------------------------------------------*/

	private String countryCode;    /* issuing state or organization */
	private String dateOfBirth;    /* date of birth */
	private String dobCd;          /* date of birth check digit */
	private String dateOfExpiry;   /* date of expiry */
	private String dateOfExpiryCd; /* date of expiry check digit */
	private String docNo;          // Document number
	private String docNoCd;        // Fields needed for the MRZ
	private String docType;        // Document type
	private String givenName;      // Given Name of Holder
	private String nationality;    /* nationality */
	private String optionalData;   /* optional data */
	private String sex;            /* sex */
	private String surname;        // Surname of Holder
	
	/*--------------------------------------------------------------------------------*/
	
	private boolean safetyOn;
	private String originalMRZ;
	
	/*--------------------------------------------------------------------------------*/
	
	
	
	/**
	 * Constructs a new MRZ from the input material defined in mrzString.
	 * 
	 * @param mrzString
	 *            String representation of a MRZ
	 */
	public Mrz(String mrz, boolean safety) {
		DocumentFormat docFormat;
		
		this.safetyOn = safety;
		
		this.originalMRZ = mrz;
		
		docFormat = this.getDocumentFormat();
		
		this.setDocNumber(Mrz.extractDocNo(mrz, docFormat));
		this.setDocNumberCd(Mrz.extractDocNoCd(mrz, docFormat));
		this.setDateOfBirth(Mrz.extractDoB(mrz, docFormat));
		this.setDateOfBirthCd(Mrz.extractDoBCd(mrz, docFormat));
		this.setDateOfExpiry(Mrz.extractDoE(mrz, docFormat));
		this.setDateOfExpiryCd(Mrz.extractDoECd(mrz, docFormat));
	}
	
	public Mrz(String mrz) {
		this(mrz, false);
	}
	
	/**
	 * Constructs a new MRZ from the input material byte array.
	 * 
	 * @param values
	 *            byte array containing byte representation of a MRZ
	 */
	public Mrz(byte[] values) {
		this(new String(values));
	}
	
	
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	protected abstract DocumentFormat getDocumentFormat();
	
	/*--------------------------------------------------------------------------------*/
	
	
	
	@Override
	public String toString() {
		return this.getOriginalMRZ();
	}
	
	/**
	 * Returns the country code of issuing state.
	 * 
	 * @return country code
	 */
	public String getCountryCode() {
		return this.countryCode;
	}

	/**
	 * Returns date of birth of holder.
	 * 
	 * @return date of birth of holder
	 */
	public String getDateOfBirth() {
		return this.dateOfBirth;
	}
	
	public String getDateOfBirthCd() {
		return this.dobCd;
	}

	/**
	 * Returns date of expiry.
	 * 
	 * @return date of expiry
	 */
	public String getDateOfExpiry() {
		return this.dateOfExpiry;
	}
	
	public String getDateOfExpiryCd() {
		return this.dateOfExpiryCd;
	}

	/**
	 * Returns document number.
	 * 
	 * @return document number
	 */
	public String getDocumentNumber() {
		return this.docNo;
	}
	
	public String getDocumentNumberCd() {
		return this.docNoCd;
	}

	/**
	 * Returns document type.
	 * 
	 * @return document type
	 */
	public String getDocumentType() {
		return this.docType;
	}

//	/**
//	 * Returns given name of holder.
//	 * 
//	 * @return given name of holder
//	 */
//	public String getGivenName() {
//		return givenName;
//	}

//	/**
//	 * Return length of value field
//	 * 
//	 * @return Length in bytes
//	 */
//	public int getLength() {
//		return MRZ.LENGTH_MRZ;
//	}

//	/**
//	 * calculates the string representation of the MRZ form the variables
//	 * provided
//	 * 
//	 * @return string representation of MRZ
//	 */
//	public String getMRZString() {
//		String mrz1stLine = "";
//		String mrz2ndLine = "";
//		String tmpString = "";
//
//		// add document type
//		tmpString = this.docType;
//		while (tmpString.length() < MRZ.LENGTH_DOCTYPE) {
//			tmpString += LdsConstants.Filler;
//		}
//		mrz1stLine += tmpString;
//
//		// add issuing state
//		tmpString = this.countryCode;
//		while (tmpString.length() < MRZ.LENGTH_COUNTRYCODE) {
//			tmpString += LdsConstants.Filler;
//		}
//		mrz1stLine += tmpString;
//
//		// add name of holder
//		tmpString = this.surname + LdsConstants.Filler + LdsConstants.Filler
//				+ this.givenName;
//		tmpString = tmpString.replaceAll("[\\s-,]+", LdsConstants.Filler); // replace spaces with filler characters
//
//		if (tmpString.length() > MRZ.LENGTH_NAME)
//			tmpString = tmpString.substring(0, MRZ.LENGTH_NAME);
//		while (tmpString.length() < MRZ.LENGTH_NAME) {
//			tmpString += LdsConstants.Filler;
//		}
//		mrz1stLine += tmpString;
//
//		// add document number and check digit
//		tmpString = this.docNo;
//		while (tmpString.length() < MRZ.LENGTH_NO) {
//			tmpString += LdsConstants.Filler;
//		}
//		tmpString += String.valueOf((char) MRZ.computeChecksum(tmpString
//				.getBytes(), 0, tmpString.length()));
//		mrz2ndLine += tmpString;
//
//		// add nationality
//		tmpString = this.nationality;
//		mrz2ndLine += tmpString;
//
//		// add date of birth and check digit
//		tmpString = this.dateOfBirth;
//		tmpString += String.valueOf((char) MRZ.computeChecksum(tmpString
//				.getBytes(), 0, tmpString.length()));
//		mrz2ndLine += tmpString;
//
//		// add sex
//		tmpString = this.sex;
//		mrz2ndLine += tmpString;
//
//		// add date of expiry and check digit
//		tmpString = this.dateOfExpiry;
//		tmpString += String.valueOf((char) MRZ.computeChecksum(tmpString
//				.getBytes(), 0, tmpString.length()));
//		mrz2ndLine += tmpString;
//
//		// add optional data and check digit
//		tmpString = this.optionalData;
//		while (tmpString.length() < MRZ.LENGTH_OPTIONALDATA) {
//			tmpString += LdsConstants.Filler;
//		}
//		tmpString += String.valueOf((char) MRZ.computeChecksum(tmpString
//				.getBytes(), 0, tmpString.length()));
//		mrz2ndLine += tmpString;
//
//		// add checkdigit for 2nd line
//		mrz2ndLine += String.valueOf((char) MRZ.computeChecksum(mrz2ndLine
//				.getBytes(), 0, mrz2ndLine.length()));
//
//		return mrz1stLine + mrz2ndLine;
//	}

	/**
	 * Returns nationality of holder.
	 * 
	 * @return nationality of holder
	 */
	public String getNationality() {
		return this.nationality;
	}

//	/**
//	 * Returns optional data.
//	 * 
//	 * @return optional data
//	 */
//	public String getOptionalData() {
//		return optionalData;
//	}

	/**
	 * Returns sex of holder.
	 * 
	 * @return sex of holder
	 */
	public String getSex() {
		return this.sex;
	}

//	/**
//	 * Returns surname of holder.
//	 * 
//	 * @return surname of holder
//	 */
//	public String getSurname() {
//		return surname;
//	}

	/**
	 * Sets the fields of this object according to the MRZ string given.
	 * 
	 * @param mrzString
	 *            MRZ String containing the values to bee set
	 */
	public void parseString(String mrzString) {
		try {
			/*
			 * Cookbook MRZ parsing: First, check the length and final checksum
			 */
			switch (mrzString.length()) {
			case 88:
				
				break;

			default:
				throw new InvalidParameterException(
				"MRZ illegally encoded. MRZ has wrong length.");
			}

			mrzString = mrzString.toUpperCase();

			byte[] checksumData = getCompositeCheckDigitData(mrzString);
			byte checksum = computeChecksum(checksumData, 0,
					checksumData.length);

			if (checksum != (byte) mrzString.charAt(OFFSET_CHECKSUM)) {
				throw new InvalidParameterException(
						"MRZ illegally encoded. Wrong MRZ check digit.");
			}

			/*
			 * Get type and country code at predefined offset positions
			 */
			setDocType(mrzString.substring(0, OFFSET_CODE));
			setCountryCode(mrzString.substring(OFFSET_CODE, OFFSET_SURNAME));

			/*
			 * Get name and split at <<, extract given and surname
			 */
			String name = mrzString.substring(OFFSET_SURNAME, OFFSET_NO);
			int i = name.indexOf(Filler + Filler);
			setSurname(name.substring(0, i).replaceAll(Filler, Blank));
			setGivenName(name.substring(i + 2).replaceAll(Filler, Blank).trim());

			/*
			 * Get the document number and verify check digit
			 */
			String value = mrzString.substring(OFFSET_NO,
					OFFSET_NATIONALITY - 1);
			checksum = computeChecksum(value.getBytes(), 0, value.length());

			if (checksum != (byte) mrzString.charAt(OFFSET_NATIONALITY - 1)) {
				throw new InvalidParameterException(
						"MRZ illegally encoded. Wrong passport number check digit.");
			}
			setDocNumber(value.replaceAll(Filler, ""));

			/*
			 * Get the nationality
			 */
			setNationality(mrzString.substring(OFFSET_NATIONALITY, OFFSET_DOB)
					.replaceAll(Filler, ""));

			/*
			 * Get the DOB and verify check digit
			 */
			value = mrzString.substring(OFFSET_DOB, OFFSET_SEX - 1);
			checksum = computeChecksum(value.getBytes(), 0, value.length());

			if (checksum != (byte) mrzString.charAt(OFFSET_SEX - 1)) {
				throw new InvalidParameterException(
						"MRZ illegally encoded. Wrong DOB check digit.");
			}
			setDateOfBirth(value);

			/*
			 * get the sex
			 */
			setSex(mrzString.substring(OFFSET_SEX, OFFSET_DOE));

			/*
			 * Get the DOE and verify check digit
			 */
			value = mrzString.substring(OFFSET_DOE, OFFSET_ODATA - 1);
			checksum = computeChecksum(value.getBytes(), 0, value.length());

			if (checksum != (byte) mrzString.charAt(OFFSET_ODATA - 1)) {
				throw new InvalidParameterException(
						"MRZ illegally encoded. Wrong DOE check digit.");
			}
			setDateOfExpiry(value);

			/*
			 * Get the optional data and verify check digit
			 */
			value = mrzString.substring(OFFSET_ODATA, OFFSET_CHECKSUM - 1);
			checksum = computeChecksum(value.getBytes(), 0, value.length());

			if (checksum != (byte) mrzString.charAt(OFFSET_CHECKSUM - 1)) {
				throw new InvalidParameterException(
						"MRZ illegally encoded. Wrong optional data check digit.");
			}
			setOptionalData(value.replaceAll(Filler, ""));

		} catch (ArrayIndexOutOfBoundsException e) {
			throw new InvalidParameterException(
					"MRZ illegally encoded. Incorrect lengths."); //$NON-NLS-1$
		} catch (InvalidParameterException e) {
			throw new InvalidParameterException(e.getMessage());
		}

	}

	private byte[] getCompositeCheckDigitData(String mrzString) {
		if (mrzString.length() == LENGTH_MRZ) {
			String cdData = mrzString.substring(OFFSET_NO, OFFSET_NATIONALITY);
			cdData += mrzString.substring(OFFSET_DOB, OFFSET_SEX);
			cdData += mrzString.substring(OFFSET_DOE, OFFSET_CHECKSUM);
			return cdData.getBytes();
		}
		return null;
	}

	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Sets the country code according to param.
	 * 
	 * @param newCountryCode
	 *            new country code
	 * @throws InvalidParameterException
	 */
	public void setCountryCode(String newCountryCode) throws InvalidParameterException {
		if (newCountryCode.length() > Mrz.LENGTH_COUNTRYCODE)
			throw new InvalidParameterException(
					"Length of country code may not exceed "
							+ Mrz.LENGTH_COUNTRYCODE + " characters.");
		if (!newCountryCode.matches("^[a-zA-Z<]{0,3}$"))
			throw new InvalidParameterException(
					"Country code must only consist of characters and spaces.");
		
		countryCode = newCountryCode.toUpperCase();
	}

	/**
	 * Sets the date of birth according to param.
	 * 
	 * @param dob
	 *            new date of birth
	 * @throws InvalidParameterException
	 */
	public void setDateOfBirth(String dob) throws InvalidParameterException {
		if (this.safetyOn && !dob.matches("^\\d{6}$")) {
			throw new InvalidParameterException("Date of birth must consist of 6 digits.");
		}
		
		this.dateOfBirth = dob;
	}
	
	public void setDateOfBirthCd(String dobCd) {
		this.dobCd = dobCd;
	}

	/**
	 * Sets the date of expiry according to param.
	 * 
	 * @param doe
	 *            new date of expiry
	 * @throws InvalidParameterException
	 */
	public void setDateOfExpiry(String doe) throws InvalidParameterException {
		if (this.safetyOn && !doe.matches("^\\d{6}$")) {
			throw new InvalidParameterException("Date of expiry must consist of 6 digits.");
		}
		
		dateOfExpiry = doe;
	}
	
	public void setDateOfExpiryCd(String doeCd) {
		this.dateOfExpiryCd = doeCd;
	}

	/**
	 * Sets the document number according to param.
	 * 
	 * @param newDocNumber
	 *            new document number
	 * @throws InvalidParameterException
	 */
	public void setDocNumber(String newDocNumber) {
		if (!newDocNumber.matches("^[a-zA-Z0-9 ]+$")) {
			throw new InvalidParameterException("Document number must only consist of characters, numbers and spaces.");
		}
			
		docNo = newDocNumber.toUpperCase();
	}
	
	public void setDocNumberCd(String docNumberCd) {
		this.docNoCd = docNumberCd;
	}

	/**
	 * Sets the document type according to param.
	 * 
	 * @param newDocType
	 *            new document type
	 * @throws InvalidParameterException
	 */
	public void setDocType(String newDocType) {
		if (newDocType.length() > Mrz.LENGTH_DOCTYPE)
			throw new InvalidParameterException(
					"Length of document type may not exceed "
							+ Mrz.LENGTH_DOCTYPE + " characters.");
		if (!newDocType.matches("^[a-zA-Z< ]{0,2}$"))
			throw new InvalidParameterException(
					"Document type must only consist of characters and spaces.");
		docType = newDocType.toUpperCase();
	}

	/**
	 * Sets the given name oh holder.
	 * 
	 * @param newGivenName
	 *            new given name oh holder
	 * @throws InvalidParameterException
	 */
	public void setGivenName(String newGivenName)
			throws InvalidParameterException {
		if (!newGivenName.matches("^[a-zA-Z\\s,\']*$"))
			throw new InvalidParameterException(
					"Given Name must consist of characters a-z, A-Z, space, comma and apostroph only.");
		givenName = newGivenName.replaceAll("\\s+", " ").toUpperCase();
	}

	/**
	 * Sets the nationality of holder.
	 * 
	 * @param newNationality
	 *            new nationality of holder
	 * @throws InvalidParameterException
	 */
	public void setNationality(String newNationality) {
		if (newNationality.length() > Mrz.LENGTH_COUNTRYCODE)
			throw new InvalidParameterException(
					"Length of country code may not exceed "
							+ Mrz.LENGTH_COUNTRYCODE + " characters.");
		if (!newNationality.matches("^[a-zA-Z]{0,3}$"))
			throw new InvalidParameterException(
					"Country code must only consist of characters and spaces.");
		nationality = newNationality.toUpperCase();
	}

	/**
	 * Sets the optional data.
	 * 
	 * @param newOptionalData
	 *            new optional data
	 * @throws InvalidParameterException
	 */
	public void setOptionalData(String newOptionalData) {
		if (!newOptionalData.matches("^[a-zA-Z0-9 ]+$"))
			throw new InvalidParameterException(
					"Country code must only consist of characters, numbers and spaces.");
		optionalData = newOptionalData.toUpperCase();
	}

	/**
	 * Set sex of holder.
	 * 
	 * @param newSex
	 *            new sex of holder
	 */
	public void setSex(String newSex) {
		if (!newSex.matches("^[FM<]$"))
			throw new InvalidParameterException("Sex must be either F, M or <");
		sex = newSex;
	}

	/**
	 * Set surname of holder.
	 * 
	 * @param newSurname
	 *            new surname of holder
	 */
	public void setSurname(String newSurname) {
		if (!newSurname.matches("^[a-zA-Z\\s,\']+$"))
			throw new InvalidParameterException(
					"Given Name mus only consist of characters a-z, A-Z, space, comma and apostroph.");
		surname = newSurname.replaceAll("\\s+", " ").toUpperCase();
	}
	
	/**
	 * @return the safetyOn
	 */
	public boolean isSafetyOn() {
		return safetyOn;
	}

	/**
	 * @param safetyOn the safetyOn to set
	 */
	public void setSafetyOn(boolean safetyOn) {
		this.safetyOn = safetyOn;
	}
	
	/**
	 * @return the originalMRZ
	 */
	public String getOriginalMRZ() {
		return originalMRZ;
	}

	/**
	 * @param originalMRZ the originalMRZ to set
	 */
	public void setOriginalMRZ(String originalMRZ) {
		this.originalMRZ = originalMRZ;
	}
	
	
	
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/
	/*--------------------------------------------------------------------------------*/

	

	private static DocumentFormat getDocumentFormat(String mrz) {
		switch (mrz.length()) {
		case 90:
			return td1;
		case 76:
			return td2;
		case 88:
			return mrp;
		default:
			throw new InvalidParameterException(
			"MRZ illegally encoded. MRZ has wrong length.");
		}
	}
	
	/*--------------------------------------------------------------------------------*/
	
	public static String extractDocType(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetDocType(), docFormat.getOffsetDocTypeEnd());
	}
	
	public static String extractIssuingState(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetIssuingState(), docFormat.getOffsetIssuingStateEnd());
	}
	
	public static String extractName(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetName(), docFormat.getOffsetNameEnd());
	}
	
	public static String extractDocNo(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetDocNo(), docFormat.getOffsetDocNoEnd());
	}
	
	public static String extractDocNoCd(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetDocNoCd(), docFormat.getOffsetDocNoCdEnd());
	}
	
	public static String extractNationality(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetNationality(), docFormat.getOffsetNationalityEnd());
	}
	
	public static String extractDoB(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetDoB(), docFormat.getOffsetDoBEnd());
	}
	
	public static String extractDoBCd(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetDoBCd(), docFormat.getOffsetDoBCdEnd());
	}
	
	public static String extractSex(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetSex(), docFormat.getOffsetSexEnd());
	}
	
	public static String extractDoE(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetDoE(), docFormat.getOffsetDoEEnd());
	}
	
	public static String extractDoECd(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetDoECd(), docFormat.getOffsetDoECdEnd());
	}
	
	public static String extractOptionalData(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetOptionalData(), docFormat.getOffsetOptionalDataEnd());
	}
	
	public static String extractOptionalDataCd(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetOptionalDataCd(), docFormat.getOffsetOptionalDataCdEnd());
	}
	
	public static String extractCompositeCd(String mrz, DocumentFormat docFormat){
		return mrz.substring(docFormat.getOffsetCompositeCd(), docFormat.getOffsetCompositeCdEnd());
	}
	
	public static String calculateCompositeCd(String mrz, DocumentFormat docFormat){
		return docFormat.calculateCompositeCd(mrz);
	}

}
