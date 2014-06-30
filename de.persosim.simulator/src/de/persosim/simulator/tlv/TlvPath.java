package de.persosim.simulator.tlv;

import java.util.Collection;
import java.util.Vector;

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
 * TODO currently the path does not allow to differentiate between multiple
 * elements that would normally all match its description, i.e. with the same
 * tag. This insufficiency may be straightened out by wrapping the TLVTag in a
 * TLVTagIdentifier also listing the offset of occurrences of the specified tag.
 * This would allow to address one specific element out of several elements with
 * identical tags. Certain values of the occurrence index (e.g. negative values)
 * could be used to signal special matching handling like "any" or "last"
 * occurrence.
 * 
 * --> Class signature may change!
 * 
 * @author slutters
 * 
 */
public class TlvPath extends Vector<TlvTag> {
	
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
	 * @param collection a collection representation of this object
	 */
	public TlvPath(Collection<TlvTag> collection) {
		super(collection);
	}
	
	/**
	 * Constructor for constructing this object from a list of {@link TlvTag} objects.
	 * @param tags a list of {@link TlvTag} objects
	 */
	public TlvPath(TlvTag... tlvTags) {
		super();
		
		if ( tlvTags == null ) {throw new IllegalArgumentException( "path must not be empty" );}
		
		for(int i = 0; i < tlvTags.length; i++) {
			this.add(tlvTags[i]);
		}
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Adds a tag to the path.
	 * @param tag the tag to add
	 * @return true iff add successful, false otherwise
	 */
	public boolean add(byte[] tag) {
		return this.add(new TlvTag(tag));
	}
	
	/**
	 * Adds a tag to the path.
	 * @param tag the tag to add
	 * @return true iff add successful, false otherwise
	 */
	public boolean add(byte tag) {
		return this.add(new TlvTag(tag));
	}
	
	/**
	 * Adds a tag to the path.
	 * @param tag the tag to add
	 * @return true iff add successful, false otherwise
	 */
	public boolean add(short tag) {
		return this.add(new TlvTag(tag));
	}
	
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
			if(!(this.elementAt(i).equals(anotherTlvPath.elementAt(i)))) {return false;}
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		for(int i = 0; i < size(); i++) {
			hash *= elementAt(i).hashCode();
		}
		return hash;
	}
	
	/**
	 * Returns a Vector analogous to this path using byte arrays instead of TlvTags.
	 * @return a Vector analogous to this path using byte arrays instead of TlvTags
	 */
	/* XXX AMY Scheduled for removal - requires non-trivial restructuring within {@link ExtendedTagSpecification} */
	@Deprecated
	public Vector<byte[]> toVector() {
		Vector<byte[]> vector;
		
		vector = new Vector<byte[]>();
		
		for(TlvTag tlvTag : this) {
			vector.add(tlvTag.toByteArray());
		}
		
		return vector;
	}
	
}
