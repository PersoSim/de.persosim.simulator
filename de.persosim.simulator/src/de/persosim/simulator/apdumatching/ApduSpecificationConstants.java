package de.persosim.simulator.apdumatching;

/**
 * This interface defines constants to be used in the area of APDU or TLV data
 * object specification.
 * 
 * @author slutters
 * 
 */
public interface ApduSpecificationConstants {

	/*
	 * These variables are used within APDUSpecification to indicate whether a
	 * variable is expected to match/mismatch another certain variable or there
	 * is no expectation at all.
	 */
	public static final byte REQ_MISMATCH = -1;
	public static final byte REQ_OPTIONAL = 0;
	public static final byte REQ_MATCH = +1;

	/*
	 * These variables are used within APDUSpecification to indicate whether
	 * occurrences of unspecified tags are tolerated.
	 */
	public static final boolean DO_NOT_ALLOW_FURTHER_TAGS = false;
	public static final boolean ALLOW_FURTHER_TAGS = true;

	/*
	 * These variables are used within APDUSpecification to indicate whether
	 * some operation is to obey a certain order of objects during execution.
	 */
	public static final boolean STRICT_ORDER = true;
	public static final boolean ARBITRARY_ORDER = false;

}
