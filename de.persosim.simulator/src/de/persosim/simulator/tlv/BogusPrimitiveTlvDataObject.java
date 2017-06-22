package de.persosim.simulator.tlv;

import static org.globaltester.logging.BasicLogger.logException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.globaltester.logging.tags.LogLevel;

/**
 * This class implements TLV data objects allowing errors. The {@link TlvTag}
 * will not be checked for correctness. Furthermore, it is possible to omit the
 * TLV value when encoding as a byte array.
 * 
 * @author cstroh
 * 
 */
public class BogusPrimitiveTlvDataObject extends PrimitiveTlvDataObject {
	private boolean omitTlvValue;
	
	/**
	 * Constructs an object from pre-fabricated elements. Length field is implicitly set
	 * according to DER encoding rules by default.
	 * @param tlvTagInput the tag to be used
	 * @param tlvValuePlainInput the value to be used
	 */
	public BogusPrimitiveTlvDataObject(TlvTag tlvTagInput, byte[] tlvValuePlainInput) {
		this(tlvTagInput, tlvValuePlainInput, false);
	}

	/**
	 * Constructs an object from pre-fabricated elements. Length field is implicitly set
	 * according to DER encoding rules by default.
	 * @param tlvTagInput the tag to be used
	 * @param tlvValuePlainInput the value to be used
	 * @param omitTlvValue if true, the TLV value will not be written to byte array
	 */
	public BogusPrimitiveTlvDataObject(TlvTag tlvTagInput, byte[] tlvValuePlainInput, boolean omitTlvValue) {
		super(tlvTagInput, tlvValuePlainInput);
		this.omitTlvValue = omitTlvValue;
	}
	
	@Override
	public void setTag(TlvTag tlvTagInput, boolean performValidityChecksInput) {
		if(tlvTagInput == null) {throw new NullPointerException("tag must not be null");}
		
		performValidityChecks = false;
		
		/*
		 * TLV tag must be cloned to eliminate outside access to this object.
		 * The tag must only be set by methods offered by this class e.g. to
		 * prevent setting the primitive tag to be a constructed tag.
		 */
		tlvTag = tlvTagInput.clone();
	}
	
	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream outputStream;
		
		outputStream = new ByteArrayOutputStream();
		
		try {
			/* tag can be accessed directly */
			outputStream.write(tlvTag.toByteArray());
			/* length must be accessed by getter in case there is a valid override */
			outputStream.write(getTlvLength().toByteArray());
			/* value must be accessed by getter as values are only specified by sub classes */
			if(!omitTlvValue) {	
				outputStream.write(getTlvValue().toByteArray());
			}
		} catch (IOException e) {
			logException(this.getClass(), e, LogLevel.DEBUG);
		}

		return outputStream.toByteArray();
	}
	
}
