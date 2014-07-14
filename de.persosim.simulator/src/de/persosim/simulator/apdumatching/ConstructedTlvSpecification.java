package de.persosim.simulator.apdumatching;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvPath;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValue;

/**
 * This class represents a specification of requirements concerning constructed
 * TLV data objects. It extends the requirements specified within
 * {@link PrimitiveTlvSpecification} by adding requirements concerning contained
 * sub tags. Sub tags may either be of type {@link PrimitiveTlvSpecification} or
 * {@link ConstructedTlvSpecification}. Additionally it can be specified whether
 * sub tag specifications that are not explicitly stated this way are allowed.
 * Sub tags internally are stored in an object of type
 * {@link TlvSpecificationContainer} so many methods, e.g.
 * {@link #add(PrimitiveTlvSpecification)} will be delegated to this object.
 * 
 * @author slutters
 * 
 */
public class ConstructedTlvSpecification extends PrimitiveTlvSpecification implements ApduSpecificationConstants { // FIXME SLS Constructed extends Primitive looks suspicious, either one is not named correctly or this hierarchy is not a good idea
	protected TlvSpecificationContainer subTags;
	
	/**
	 * This constructor constructs a {@link ConstructedTlvSpecification} based on a tag, permission for unknown sub tags and matching requirements.
	 * @param tlvTag the TLV tag
	 * @param allowUnspecifiedSubTags whether unspecified sub tags are accepted
	 * @param isStrictOrder specifies whether the order of sub tags is to be evaluated as expected in strict given order
	 * @param required matching requirements (see class documentation)
	 */
	public ConstructedTlvSpecification(TlvTag tlvTag, boolean allowUnspecifiedSubTags, boolean isStrictOrder, byte req) {
		super(tlvTag, req);
		this.subTags = new TlvSpecificationContainer(allowUnspecifiedSubTags, isStrictOrder);
	}
	
	/**
	 * This constructor constructs a {@link ConstructedTlvSpecification} based on a tag.
	 * Default settings are:
	 * unknown sub tags allowed: false
	 * matching requirements : required
	 * @param tlvTag the TLV tag
	 */
	public ConstructedTlvSpecification(TlvTag tlvTag) {
		this(tlvTag, DO_NOT_ALLOW_FURTHER_TAGS, STRICT_ORDER, REQ_MATCH);
	}
	
	/**
	 * @see {@link TlvSpecificationContainer#add(PrimitiveTlvSpecification)}
	 */
	public void add(PrimitiveTlvSpecification tlvSpec) {
		subTags.add(tlvSpec);
	}
	
	/**
	 * @see {@link TlvSpecificationContainer#add(TlvPath, int, PrimitiveTlvSpecification)}
	 */
	public void add(TlvPath path, int pathOffset, PrimitiveTlvSpecification tlvSpec) {
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
	
	@Override
	public boolean matches(TlvDataObject tlvDataObject) {
		//FIXME SLS add testcases for this method
		if(!(tlvDataObject instanceof ConstructedTlvDataObject)) {return false;}
		if(!this.matches(tlvDataObject.getTlvTag())) {return false;}
		if(this.required == REQ_MISMATCH) {return false;}
		//FIXME SLS MATCH subTags are not covered here at all
		
		TlvValue value = tlvDataObject.getTlvValue();
		if(value instanceof TlvDataObjectContainer) {
			return subTags.matches((TlvDataObjectContainer) value).isMatch();
		} else{
			return false;
		}
	}
	
	/**
	 * @return the subTags
	 */
	public TlvSpecificationContainer getSubTags() {
		return subTags;
	}
	
}
