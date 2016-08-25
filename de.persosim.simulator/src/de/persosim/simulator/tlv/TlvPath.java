package de.persosim.simulator.tlv;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a path that can be used to navigate within any
 * {@link de.persosim.simulator.tlv.TlvDataStructure TLV data structure}.
 * Navigation is performed on the basis of a sequence of valid tags that are
 * compared to the tags of {@link de.persosim.simulator.tlv.TlvDataObject TLV
 * data objects} using the {@link #equals(Object) equals} method. The last tag
 * element of a path usually, i.e. if not explicitly stated otherwise, is
 * considered the one to identify the TLV data object to be the target of a
 * certain operation. A path always is interpreted relative to where it is used.
 * The object on which a method is called that is provided with a path must not
 * be explicitly identified by the first tag of the path. Due to the relativity
 * of a path, navigation results will always be different for the same path to
 * be used both on a child and its parent. If, when navigating through a TLV
 * data structure, a tag is not able to identify a TLV data object, i.e. the
 * path is wrong or the respective object is missing, the resulting behavior
 * solely is up to the processing method.
 * 
 * @author slutters
 * 
 */
public class TlvPath extends ArrayList<TlvTagIdentifier> {
	
	/* Default serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Basic constructor for constructing an empty path.
	 */
	public TlvPath() {
		super();
	}
	
	/**
	 * Basic constructor for constructing this object from a collection representation.
	 * @param tagList a collection representation of this object
	 */
	public TlvPath(List<TlvTagIdentifier> tagList) {
		super(tagList);
	}
	
	/**
	 * Constructor for constructing this object from a list of {@link TlvTagIdentifier} objects.
	 * @param tagIdentifiers a list of {@link TlvTagIdentifier} objects
	 */
	public TlvPath(TlvTagIdentifier... tagIdentifiers) {
		for(TlvTagIdentifier curTagIdentifier : tagIdentifiers) {
			this.add(curTagIdentifier);
		}
	}
	
	/**
	 * Constructor for constructing this object from a list of {@link TlvTag} objects.
	 * <p/>
	 * The according {@link TlvTagIdentifier} are generated using their TlvTag-only constructor
	 * @param tags a list of {@link TlvTag} objects
	 */
	public TlvPath(TlvTag... tlvTags) {
		for(int i = 0; i < tlvTags.length; i++) {
			this.add(new TlvTagIdentifier(tlvTags[i]));
		}
	}
	
	/*--------------------------------------------------------------------------------*/

	@Override
	public TlvPath clone() {
		return new TlvPath(this);
	}
	
	@Override
	public boolean equals(Object anotherProbableTlvPath) {
		if(anotherProbableTlvPath == null) {return false;}
		
		if (!(anotherProbableTlvPath instanceof TlvPath)) {
			return false;
		}
		
		TlvPath anotherTlvPath = (TlvPath) anotherProbableTlvPath;
		
		if(size() != anotherTlvPath.size()) {return false;};
		
		for(int i = 0; i < size(); i++) {
			if(!(this.get(i).equals(anotherTlvPath.get(i)))) {return false;}
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		for(int i = 0; i < size(); i++) {
			hash *= get(i).hashCode();
		}
		return hash;
	}
	
	/**
	 * This method returns the last object of the path.
	 * If the path is empty, null will be returned.
	 * @return the last object of the path
	 */
	public TlvTagIdentifier getLastElement() {
		int size = size();
		if(size == 0) {
			return null;
		} else{
			return get(size - 1);
		}
	}

	/**
	 * Convenience method to add {@link TlvTagIdentifier} based on a {@link TlvTag}
	 * @param tag TlvTag to be added (will be wrapped in a {@link TlvTagIdentifier})
	 */
	public void add(TlvTag tag) {
		add(new TlvTagIdentifier(tag));
	}
	
}
