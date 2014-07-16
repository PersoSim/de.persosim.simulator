package de.persosim.simulator.apdumatching;

import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvPath;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValue;

/**
 * This class represents a specification of requirements concerning TLV data
 * objects {@link TlvDataObject}. There is no explicit distinction between
 * primitive or constructed TLV data objects. Distinction is implicit by (not)
 * providing specifications for sub elements. Any specification must contain a
 * {@link TlvTag} and additionally may explicitly specify whether it is expected
 * to (not) match a TLV data object or matching is optional (default is match).
 * The constants for matching requirements
 * {@link ApduSpecificationConstants#REQ_MATCH},
 * {@link ApduSpecificationConstants#REQ_MISMATCH} and
 * {@link ApduSpecificationConstants#REQ_OPTIONAL} are specified in the
 * interface {@link ApduSpecificationConstants}.
 * 
 * Constructed TLV data objects may be specified by adding requirements
 * concerning contained sub elements. Sub elements may be specified the same way
 * recursively. Additionally it can be specified whether sub element
 * specifications that are not explicitly stated this way are allowed. Sub
 * elements internally are stored in an object of type
 * {@link TlvSpecificationContainer} so many methods, e.g.
 * {@link #add(TlvSpecification)} will be delegated to this object.
 * 
 * @author slutters
 * 
 */
public class TlvSpecification implements ApduSpecificationConstants {
	protected TlvTag tlvTag;
	protected byte required;
	protected TlvSpecificationContainer subTags;
	
	/**
	 * This constructor constructs a {@link TlvSpecification} based on a tag, permission for unknown sub tags and matching requirements.
	 * @param tlvTag the TLV tag
	 * @param allowUnspecifiedSubTags whether unspecified sub tags are accepted
	 * @param isStrictOrder specifies whether the order of sub tags is to be evaluated as expected in strict given order
	 * @param required matching requirements (see class documentation)
	 */
	public TlvSpecification(TlvTag tlvTag, boolean allowUnspecifiedSubTags, boolean isStrictOrder, byte required) {
		this.tlvTag = tlvTag;
		this.required = required;
		this.subTags = new TlvSpecificationContainer(allowUnspecifiedSubTags, isStrictOrder);
	}
	
	/**
	 * This constructor constructs a {@link TlvSpecification} based on a tag.
	 * Default settings are:
	 * unknown sub tags allowed: false
	 * matching requirements : required
	 * @param tlvTag the TLV tag
	 * @param required matching requirements (see class documentation)
	 */
	public TlvSpecification(TlvTag tlvTag, byte required) {
		this(tlvTag, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, required);
	}
	
	/**
	 * This constructor constructs a {@link TlvSpecification} based on a tag.
	 * Default settings are:
	 * unknown sub tags allowed: false
	 * matching requirements : required
	 * @param tlvTag the TLV tag
	 */
	public TlvSpecification(TlvTag tlvTag) {
		this(tlvTag, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
	}
	
	/**
	 * @see {@link TlvSpecificationContainer#add(TlvSpecification)}
	 */
	public void add(TlvSpecification tlvSpec) {
		subTags.add(tlvSpec);
	}
	
	/**
	 * @see {@link TlvSpecificationContainer#add(TlvPath, int, TlvSpecification)}
	 */
	public void add(TlvPath path, int pathOffset, TlvSpecification tlvSpec) {
		subTags.add(path, pathOffset, tlvSpec);
	}
	
	/**
	 * @see {@link TlvSpecificationContainer#setAllowUnspecifiedSubTags(boolean)}
	 */
	public void setAllowUnspecifiedSubTags(boolean allowUnspecifiedSubTags) {
		subTags.setAllowUnspecifiedSubTags(allowUnspecifiedSubTags);
	}
	
	/**
	 * @see {@link TlvSpecificationContainer#setStrictOrder(boolean)}
	 */
	public void setStrictOrder(boolean strictOrder) {
		subTags.setStrictOrder(strictOrder);
	}
	
	/**
	 * @see {@link TlvSpecificationContainer#isEmpty()}
	 */
	public boolean isEmpty() {
		return subTags.isEmpty();
	}
	
	/**
	 * This method performs a matching against the provided {@link TlvDataObject}.
	 * Matching is performed based on the {@link TlvTag} and the sub elements recursively.
	 * @param tlvDataObject the {@link TlvDataObject} to match against
	 * @return whether this object matches the provided {@link TlvDataObject}
	 */
	public boolean matches(TlvDataObject tlvDataObject) {
		//FIXME SLS add testcases for this method
		if(!matches(tlvDataObject.getTlvTag())) {return false;}
		if(this.required == REQ_MISMATCH) {return false;}
		
		TlvValue value = tlvDataObject.getTlvValue();
		
		// IMPL support for primitive TLV data objects containing further TLV data objects disguised as Octet Strings
		if(value instanceof TlvDataObjectContainer) {
			return subTags.matches((TlvDataObjectContainer) value);
		} else{
			if(subTags.size() > 0) {
				return false;
			} else{
				return true;
			}
		}
	}
	
	/**
	 * This method returns whether the provided {@link TlvTag} matches the tag specified within this object.
	 * @param anotherTlvTag the {@link TlvTag} to match against
	 * @return whether the provided {@link TlvTag} matches the tag specified within this object
	 */
	public boolean matches(TlvTag anotherTlvTag) {
		return tlvTag.matches(anotherTlvTag);
	}
	
	/**
	 * @return the subTags
	 */
	public TlvSpecificationContainer getSubTags() {
		return subTags;
	}

	public byte getRequired() {
		return required;
	}

	public void setRequired(byte required) {
		this.required = required;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + required;
		result = prime * result + ((subTags == null) ? 0 : subTags.hashCode());
		result = prime * result + ((tlvTag == null) ? 0 : tlvTag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TlvSpecification other = (TlvSpecification) obj;
		if (required != other.required)
			return false;
		if (subTags == null) {
			if (other.subTags != null)
				return false;
		} else if (!subTags.equals(other.subTags))
			return false;
		if (tlvTag == null) {
			if (other.tlvTag != null)
				return false;
		} else if (!tlvTag.equals(other.tlvTag))
			return false;
		return true;
	}
	
}
