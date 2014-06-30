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
	public TlvDataObject getTagField(TlvPath path, int index);
	
	/**
	 * Returns the TLV data object at the specified path.
	 * The TLV data object is identified using its tag field.
	 * @param path the path of the TLV data object, including the objects tag itself, not including the starting object
	 * @return the TLV data object at the specified path, null otherwise
	 */
	public TlvDataObject getTagField(TlvPath path);
	
	/**
	 * Returns the TLV data object identified by the provided tag.
	 * @param tlvTag the tag used for identification
	 * @return the TLV data object identified by the provided tag, null otherwise
	 */
	public TlvDataObject getTagField(TlvTag tlvTag);
	
	/**
	 * Returns whether this data structure contains a TLV data object identified by the provided tag.
	 * @param tlvTag the tag used for identification
	 * @return whether this data structure contains a TLV data object identified by the provided tag
	 */
	public boolean containsTagField(TlvTag tagField);
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Adds the provided object at the specified path in this data structure if possible
	 * @param path the path at which the object is to be added
	 * @param tlvDataObject the object to be added
	 */
	public void addTlvDataObject(TlvPath path, TlvDataObject tlvDataObject);
	
	/**
	 * Adds the specified object to this data structure if possible
	 * 
	 * XXX DEV Check for/prohibit circular object references?
	 * Example:
	 * 1) Create constructed empty object A
	 * 2) Create constructed empty object B
	 * 3) add B to A
	 * 4) add A to B
	 * 5) A.toByteArray ...
	 *
	 * @param tlvDataObject the object to be added
	 */
	public void addTlvDataObject(TlvDataObject tlvDataObject);
	
	/**
	 * Removes the object at the specified path if possible.
	 * @param path the path of the object to be removed including the object itself
	 */
	public void removeTlvDataObject(TlvPath path);
	
	/**
	 * Removes the specified object from this data structure if possible.
	 * @param tlvTag the object to be removed
	 */
	public void removeTlvDataObject(TlvTag tlvTag);
	
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
