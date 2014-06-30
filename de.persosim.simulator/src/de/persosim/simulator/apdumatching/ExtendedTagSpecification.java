package de.persosim.simulator.apdumatching;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvPath;
import de.persosim.simulator.tlv.TlvTag;

/**
 * @author slutters
 *
 */
//TODO SLS proposed name ConstructedTlvSpecification
//TODO SLS document this Class
/* TODO SLS use {@link TlvTag} instead of byte[] and {@link TlvPath} instead of List */
public class ExtendedTagSpecification extends SimpleTagSpecification implements ApduSpecificationIf {
	protected boolean isRootNode;
	protected boolean allowUnspecifiedSubTags;
	protected ExtendedTagSpecification parentTag;
	protected Vector<ExtendedTagSpecification> subTags;
	
	public ExtendedTagSpecification() {
		super();
		this.subTags = new Vector<ExtendedTagSpecification>();
		this.isRootNode = true;
		this.allowUnspecifiedSubTags = false;
	}
	
	public ExtendedTagSpecification(byte[] tagArray, int tagOffset, byte tagLength, boolean isRootNode, boolean allowance, byte req) {
		super(tagArray, tagOffset, tagLength, req);
		this.subTags = new Vector<ExtendedTagSpecification>();
		this.isRootNode = isRootNode;
		this.allowUnspecifiedSubTags = allowance;
	}
	
	public ExtendedTagSpecification(byte[] tagArray, int tagOffset, byte tagLength, boolean allowance, byte req) {
		this(tagArray, tagOffset, tagLength, false, allowance, req);
	}
	
	public ExtendedTagSpecification(byte[] tag, boolean allowance, byte req) {
		this(tag, (byte) 0, (byte) tag.length, allowance, req);
	}
	
	public ExtendedTagSpecification(byte[] tag) {
		this(tag, false, REQ_MATCH);
	}
	
	public ExtendedTagSpecification(byte tag, boolean allowance, byte req) {
		this(new byte[]{tag}, allowance, req);
	}
	
	public ExtendedTagSpecification(byte tag) {
		this(tag, false, REQ_MATCH);
	}
	
	public ExtendedTagSpecification(short tag, boolean allowance, byte req) {
		this(new byte[]{(byte) ((tag & (short) 0xFF00) >> 8), (byte) (tag & (short) 0x00FF)} , allowance, req);
	}
	
	public ExtendedTagSpecification(short tag) {
		this(tag, false, REQ_MATCH);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	public void addSubTag(List<byte[]> path, int pathOffset, byte[] tagArray, int tagOffset, byte tagLength, boolean allowance, byte req) {
		ExtendedTagSpecification tagSpec;
		int index;
		
		if(path == null) {throw new NullPointerException("path is null");}
		
		for(byte i = 0; i < path.size(); i++) {
			if(path.get(i) == null) {
				throw new NullPointerException("subtag in path is null");
			}
		}
		
		if(tagArray == null) {throw new NullPointerException("tag is null");}
		if (tagOffset < 0) {throw new ArrayIndexOutOfBoundsException("tag offset < 0");}
		if (tagLength < 0) {throw new ArrayIndexOutOfBoundsException("tag length < 0");}
		if (tagOffset + tagLength > tagArray.length) {throw new ArrayIndexOutOfBoundsException("tag array too small");}
		
		if(pathOffset == path.size()) {
			tagSpec = new ExtendedTagSpecification(tagArray, tagOffset, tagLength, allowance, req);
			tagSpec.setParentTag(this);
			this.subTags.add(tagSpec);
			return;
		} else{
			index = this.getIndexOfSubTag(path.get(pathOffset));
			
			if(index >= 0) {
				tagSpec = this.subTags.get(index);
			} else{
				tagSpec = new ExtendedTagSpecification(path.get(pathOffset), allowance, req);
				tagSpec.setParentTag(this);
				this.subTags.add(tagSpec);
			}
			
			tagSpec.addSubTag(path, (byte) (pathOffset + 1), tagArray, tagOffset, tagLength, allowance, req);
			return;
		}
	}
	
	public void addSubTag(List<byte[]> path, byte[] tag, boolean allowance, byte req) {
		this.addSubTag(path, 0, tag, 0, (byte) tag.length, allowance, req);
	}
	
	public void addSubTag(List<byte[]> path, SimpleTagSpecification sTagSpec) {
		this.addSubTag(path, sTagSpec.toByteArray(), false, sTagSpec.getRequired());
	}
	
	@SuppressWarnings("deprecation") // task already annotated at called method
	public void addSubTag(TlvPath path, SimpleTagSpecification sTagSpec) {
		this.addSubTag(path.toVector(), sTagSpec.toByteArray(), false, sTagSpec.getRequired());
	}
	
	public void addSubTag(SimpleTagSpecification sTagSpec) {
		this.addSubTag(new Vector<byte[]>(), sTagSpec);
	}
	
	public void addSubTag(List<byte[]> path, ExtendedTagSpecification eTagSpec) {
		this.addSubTag(path, eTagSpec.toByteArray(), eTagSpec.isAllowUnspecifiedSubTags(), eTagSpec.getRequired());
	}
	
	@SuppressWarnings("deprecation") // task already annotated at called method
	public void addSubTag(TlvPath path, ExtendedTagSpecification eTagSpec) {
		this.addSubTag(path.toVector(), eTagSpec.toByteArray(), eTagSpec.isAllowUnspecifiedSubTags(), eTagSpec.getRequired());
	}
	
	public void addSubTag(ExtendedTagSpecification eTagSpec) {
		this.addSubTag(new Vector<byte[]>(), eTagSpec);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * 
	 * @param tlvContainer a BER-TLV structure
	 * @param isStrict whether tags are expected in a strict order
	 * @return
	 */
	public TagMatchResult matches(TlvDataObjectContainer tlvContainer, boolean isStrict) {
		ConstructedTlvDataObject cBERTLVobj;
		Iterator<TlvDataObject> tlvIterator;
		int counter, diffCounter, currentWorkingIndex, highestAlreadyEncounteredIndex;;
		TlvDataObject tlvDataObject;
		ExtendedTagSpecification subTag;
		TagMatchResult tagMatchResult;
		
		counter = 0;
		highestAlreadyEncounteredIndex = 0;
		
		tlvIterator = tlvContainer.iterator();
		
		while(tlvIterator.hasNext()) {
			tlvDataObject = tlvIterator.next();
			
			currentWorkingIndex = this.getIndexOfSubTag(tlvDataObject.getTlvTag().toByteArray());
			
			if(currentWorkingIndex < 0) {
				if(!this.allowUnspecifiedSubTags) {
					/* we encountered an unknown (sub-) tag but these are implicitly forbidden at the specified place */
					return new TagMatchResult(SW_6A80_WRONG_DATA, "unexpected tag " + tlvDataObject.getTlvTag());
//					return false;
				}
			} else{
				subTag = this.subTags.get(currentWorkingIndex);
				
				if(subTag.getRequired() == REQ_MISMATCH) {
					return new TagMatchResult(SW_6A80_WRONG_DATA, "tag " + tlvDataObject.getTlvTag() + " not allowed");
//					return false;
				}
				
				if(subTag.getRequired() == REQ_MATCH) {
					counter++;
				}
				
				if(isStrict) {
					if(currentWorkingIndex < highestAlreadyEncounteredIndex) {
						/* we encountered a known (sub-) tag but out of the specified order */
						return new TagMatchResult(SW_6A80_WRONG_DATA, "tag " + tlvDataObject.getTlvTag() + " is out of order");
//						return false;
					} else{
						highestAlreadyEncounteredIndex = currentWorkingIndex;
					}
				}
				
				if(tlvDataObject.isConstructedTLVObject()) {
					cBERTLVobj = (ConstructedTlvDataObject) tlvDataObject;
					
					tagMatchResult = subTag.matches(cBERTLVobj.getTlvDataObjectContainer(), isStrict);
					
					if(!tagMatchResult.isMatch()) {
						/* forward error message */
						return tagMatchResult;
//						return false;
					}	
				}
			}
		}
		
		diffCounter = this.getNoOfTagsMatchingRequirement(REQ_MATCH) - counter;
		
		if(diffCounter > 0) {
			/* tlv object failed to satisfy all required matches */
			/* "missing tags" */
			if(diffCounter == 1) {
				return new TagMatchResult(SW_6A80_WRONG_DATA, "missing " + diffCounter + " more mandatory tag");
			} else{
				return new TagMatchResult(SW_6A80_WRONG_DATA, "missing " + diffCounter + " more mandatory tags");
			}
			
//			return false;
		}
		
		return new TagMatchResult();
//		return true;
	}
	
	/*--------------------------------------------------------------------------------*/
	
	public int getIndexOfSubTag(byte[] byteArray, int offset, int length) {
		TlvTag tlvTag;
		
		tlvTag = new TlvTag(byteArray, offset, (offset + length));
		
		for(int i = 0; i < this.subTags.size(); i++) {
			if(this.subTags.get(i).matches(tlvTag)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int getIndexOfSubTag(byte[] tag) {
		return this.getIndexOfSubTag(tag, 0, tag.length);
	}
	
	public boolean containsSubtag(byte[] tag) {
		return this.getIndexOfSubTag(tag) >= 0;
	}
	
	public ExtendedTagSpecification getSubTag(byte[] byteArray, int offset, int length) {
		int index;
		
		index = this.getIndexOfSubTag(byteArray, offset, length);
		
		if(index < 0) {
			return null;
		} else{
			return this.subTags.get(index);
		}
	}
	
	public int getNoOfTagsMatchingRequirement(byte req) {
		int counter;
		
		counter = 0;
		
		for(int i = 0; i < this.subTags.size(); i++) {
			if(this.subTags.get(i).getRequired() == req) {
				counter++;
			}
		}
		
		return counter;
	}

	/*--------------------------------------------------------------------------------*/

	public boolean equals(ExtendedTagSpecification tagSpec) {
		if(!super.equals(tagSpec)) {
			return false;
		}
		
		for(int i = 0; i < this.subTags.size(); i++) {
			if(!this.subTags.get(i).equals(tagSpec)) {
				return false;
			}
		}
		
		return true;
	}
	
	public void setRequired(byte defaultValue, boolean recursive) {
		this.setRequired(defaultValue);
		
		if(recursive) {
			for(int i = 0; i < this.subTags.size(); i++) {
				this.subTags.get(i).setRequired(defaultValue, recursive);
			}
		}
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * @return the subTags
	 */
	public Vector<ExtendedTagSpecification> getSubTags() {
		return subTags;
	}
	
	/**
	 * @return the isRootNode
	 */
	public boolean isRootNode() {
		return isRootNode;
	}

	/**
	 * @param isRootNode the isRootNode to set
	 */
	public void setRootNode(boolean isRootNode) {
		this.isRootNode = isRootNode;
	}

	/**
	 * @return the parentTag
	 */
	public ExtendedTagSpecification getParentTag() {
		return parentTag;
	}

	/**
	 * @param parentTag the parentTag to set
	 */
	public void setParentTag(ExtendedTagSpecification parentTag) {
		this.parentTag = parentTag;
	}
	
	/**
	 * @return the allowUnspecifiedSubTags
	 */
	public boolean isAllowUnspecifiedSubTags() {
		return allowUnspecifiedSubTags;
	}

	/**
	 * @param allowUnspecifiedSubTags the allowUnspecifiedSubTags to set
	 */
	public void setAllowUnspecifiedSubTags(boolean allowUnspecifiedSubTags) {
		this.allowUnspecifiedSubTags = allowUnspecifiedSubTags;
	}
	
	/*--------------------------------------------------------------------------------*/

	public boolean hasSubTags() {
		return this.subTags.size() > 0;
	}

	public boolean isEmpty() {
		return subTags.isEmpty();
	}
}
