package de.persosim.simulator.tlv;

import static de.persosim.simulator.utils.PersoSimLogger.DEBUG;
import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This class implements TLV data objects allowing errors.
 * 
 * @author cstroh
 * 
 */
public class BogusPrimitiveTlvDataObject extends PrimitiveTlvDataObject {
	private boolean omitTlvValue;
	
	public BogusPrimitiveTlvDataObject(TlvTag tlvTagInput, byte[] tlvValuePlainInput) {
		this(tlvTagInput, tlvValuePlainInput, false);
	}

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
			logException(this.getClass(), e, DEBUG);
		}

		return outputStream.toByteArray();
	}
	
}
