package de.persosim.simulator.tlv;

import java.util.Comparator;

/**
 * This interface defines basic functionalities any TLV data object with
 * constructed encoding or any sequence of TLV data objects must provide.
 * 
 * @author slutters
 * 
 */
public interface TlvDataStructure extends Iterable<TlvDataObject> {
	
	/**
	 * Returns the TLV data object at the specified path.
	 * The TLV data object is identified using its tag field.
	 * @param path the path of the TLV data object, including the objects tag itself, not including the starting object
	 * @param index the current position within the path
	 * @return the TLV data object at the specified path, null otherwise
	 */
	public TlvDataObject getTlvDataObject(TlvPath path, int index);
	
	/**
	 * Returns the TLV data object at the specified path.
	 * The TLV data object is identified using its tag field.
	 * @param path the path of the TLV data object, including the objects tag itself, not including the starting object
	 * @return the TLV data object at the specified path, null otherwise
	 */
	public TlvDataObject getTlvDataObject(TlvPath path);
	
	/**
	 * Returns the TLV data object identified by the provided tag identifier.
	 * @param tagIdentifier 
	 * @return the TLV data object identified by the provided tag, null otherwise
	 */
	public TlvDataObject getTlvDataObject(TlvTagIdentifier tagIdentifier);
	
	/**
	 * Convenience method to allow retrieving objects with a TlvTag directly.
	 * @see #getTlvDataObject(TlvTagIdentifier)
	 * @param tlvTag the tag used for identification
	 * @return the TLV data object identified by the provided tag, null otherwise
	 */
	public TlvDataObject getTlvDataObject(TlvTag tlvTag);
	
	/**
	 * Returns whether this data structure contains a TLV data object identified by the provided tag.
	 * @param tlvTag the tag used for identification
	 * @return whether this data structure contains a TLV data object identified by the provided tag
	 */
	public boolean containsTlvDataObject(TlvTag tagField);
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Adds the provided object at the specified path in this data structure if possible
	 * @param path the path at which the object is to be added
	 * @param tlvDataObject the object to be added
	 */
	public void addTlvDataObject(TlvPath path, TlvDataObject tlvDataObject);
	
	/**
	 * Adds the specified object to this data structure if possible
	 * <p>
	 * NOTE: callers MUST ensure that they do not construct circular references
	 * within the object structure. Also adding of the SAME object at different
	 * places in the object structure is not advisable (though not forbidden)
	 * but may lead to unexpected behavior when adding/removing elements.
	 * 
	 * @param tlvDataObject
	 *            the object to be added
	 */
	public void addTlvDataObject(TlvDataObject... tlvDataObject);
	
	/**
	 * Removes the object at the specified path if possible.
	 * @param path the path of the object to be removed including the object itself
	 */
	public void removeTlvDataObject(TlvPath path);
	
	/**
	 * Removes the specified object from this data structure if present.
	 * @param tagIdentifier the object to be removed
	 */
	public void removeTlvDataObject(TlvTagIdentifier tagIdentifier);
	
	/**
	 * Convenience method to allow removing objects with a matching TlvTag directly.
	 * @see #removeTlvDataObject(TlvTagIdentifier)
	 * @param tlvTag the tag used for identification
	 */
	public void removeTlvDataObject(TlvTag tlvTag);

	/**
	 * Removes all objects from this data structure if present.
	 */
	public void removeAllTlvDataObjects();
	
	/**
	 * Removes the specified object from this data structure if present.
	 * @param tlvObject the object to be removed
	 */
	public void remove(TlvDataObject tlvObject);
	
	/**
	 * Sort the elements within this data structure according to the provided comparator
	 * @param comparator the comparator to be used for sorting
	 */
	public void sort(Comparator<TlvDataObject> comparator);
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Returns the number of elements contained in this data structure.
	 * @param recursive whether sub-elements are to be included
	 * @return the number of elements contained in this data structure
	 */
	public int getNoOfElements(boolean recursive);
	
	/**
	 * Returns the number of elements contained in this data structure.
	 * @return the number of elements contained in this data structure
	 */
	public int getNoOfElements();
	
}
