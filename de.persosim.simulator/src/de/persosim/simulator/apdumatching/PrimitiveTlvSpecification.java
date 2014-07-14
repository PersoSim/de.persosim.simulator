package de.persosim.simulator.apdumatching;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvTag;

/**
 * This class represents a specification of requirements concerning primitive
 * TLV data objects {@link PrimitiveTlvDataObject}. Any specification must
 * contain a {@link TlvTag} and additionally may explicitly specify whether it
 * is expected to (not) match a primitive TLV data object or matching is
 * optional (default is match). The constants for matching requirements
 * {@link ApduSpecificationConstants#REQ_MATCH}, {@link ApduSpecificationConstants#REQ_MISMATCH}
 * and {@link ApduSpecificationConstants#REQ_OPTIONAL} are specified in the interface
 * {@link ApduSpecificationConstants}.
 * 
 * @author slutters
 * 
 */
public class PrimitiveTlvSpecification extends TlvTag implements Iso7816, ApduSpecificationConstants {
	
	// whether matching is (not) expected or optional
	protected byte required;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * This constructor constructs a {@link PrimitiveTlvSpecification} based on a tag and matching requirements.
	 * @param tlvTag the TLV tag
	 * @param required matching requirements (see class documentation)
	 */
	public PrimitiveTlvSpecification(TlvTag tlvTag, byte required) {
		super(tlvTag);
		this.required = required;
	}
	
	/**
	 * This constructor constructs a {@link PrimitiveTlvSpecification} based on a tag, matching is implicitly reuired.
	 * @param tlvTag the TLV tag
	 */
	public PrimitiveTlvSpecification(TlvTag tlvTag) {
		this(tlvTag, REQ_MATCH);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * This methods returns whether the provided {@link TlvDataObject} matches the specifications made by this object.
	 * @param tlvDataObject the provided TLV data object
	 * @return whether the provided {@link TlvDataObject} matches the specifications made by this object
	 */
	public boolean matches(TlvDataObject tlvDataObject) {
		if(!(tlvDataObject.getClass().equals(PrimitiveTlvDataObject.class))) {return false;}
		if(!this.matches(tlvDataObject.getTlvTag())) {return false;}
		if(this.required == REQ_MISMATCH) {return false;}
		
		return true;
	}
	
	/**
	 * @return the required
	 */
	public byte getRequired() {
		return required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(byte required) {
		this.required = required;
	}
	
}
