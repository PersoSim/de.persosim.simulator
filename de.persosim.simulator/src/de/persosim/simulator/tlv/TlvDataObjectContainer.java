package de.persosim.simulator.tlv;

import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.persosim.simulator.platform.Iso7816;

/**
 * This class represents a collection of TLV data objects, themselves being
 * represented by the class {@link TlvDataObject} and its sub-classes.
 * 
 * It may be used e.g. as the value field of a TLV data object with constructed
 * encoding or as a representation of APDU command data fields.
 *  
 * Objects of this class explicitly allow for a root element, representing the
 * data field of an APDU, that itself is no {@link TlvDataObject}, i.e. has no
 * dedicated tag or explicit length field. Objects of this class may also be
 * used as a representation of APDU command data fields.
 * 
 * @author slutters
 * 
 */
public class TlvDataObjectContainer extends TlvValue implements Iso7816, TlvDataStructure {
	
	protected Vector<TlvDataObject> tlvObjects;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Standard constructor for cases in which a tag is marked as constructed but the
	 * corresponding length field indicates length 0. As constructed tags are supposed
	 * to contain a TLV structure this is to prevent NullPointerExceptions when accessing
	 * this TLV structure although the structure is empty.
	 */
	public TlvDataObjectContainer() {
		this.tlvObjects = new Vector<TlvDataObject>();
	}
	
	/**
	 * Constructor for all TLV structures with indicated length > 0.
	 * @param dataField the data field that contains the TLV structure
	 * @param minOffset the first offset to be used (inclusive)
	 * @param maxOffset the last offset to be used (exclusive)
	 */
	public TlvDataObjectContainer(byte[] dataField, int minOffset, int maxOffset) {
		if(dataField == null) {throw new NullPointerException();}
		if(minOffset < 0) {throw new IllegalArgumentException("min offset must not be less than 0");}
		if(maxOffset < minOffset) {throw new IllegalArgumentException("max offset must not be smaller than min offset");}
		if(maxOffset > dataField.length) {throw new IllegalArgumentException("selected array area must not lie outside of data array");}
		
		this.tlvObjects = new Vector<TlvDataObject>();
		
		if(minOffset == maxOffset) {
			/* The TLV data object container is empty */
			return;
		}
		
		int currentOffset = minOffset;
		TlvDataObject tlvObject;
		
		while(currentOffset < maxOffset) {
			/* 
			 * It is assumed that currentOffset always holds the first offset of the next TLV object.
			 * As the number and standard conformity of the TLV data objects contained within the data
			 * field is a priori unknown, maxOffset only indicates a maximum bound no TLV data object
			 * is allowed to exceed. The loop will continue to extract TLV data objects and move on
			 * currentOffset accordingly so it will continually close in on maxOffset. Finally maxOffset
			 * will either be occupied by the last byte of a TLV data object and everything will be fine.
			 * Otherwise there will always be an incomplete TLV data object left throwing an exception. 
			 */		
			tlvObject = TlvDataObjectFactory.createTLVDataObject(dataField, currentOffset, maxOffset);
			
			currentOffset += tlvObject.getLength();
			tlvObjects.add(tlvObject);
		}
	}
	
	/**
	 * Constructor for all TLV structures with indicated length > 0.
	 * @param dataField the data field that contains the TLV structure
	 */
	public TlvDataObjectContainer(byte[] dataField) {
		this(dataField, 0, dataField.length);
	}
	
	/**
	 * Constructs an object only containing the provided object
	 * @param tlvDataObject the object to contain
	 */
	public TlvDataObjectContainer(TlvDataObject... tlvDataObject) {
		this();
		this.addTlvDataObject(tlvDataObject);
	}
	
	/**
	 * Constructs an object from the provided one.
	 * Usually the provided object is not fully processed.
	 * @param tlvValue the object to process
	 */
	public TlvDataObjectContainer(TlvValue tlvValue) {
		this(tlvValue.toByteArray(), 0, tlvValue.getLength());
	}
	
	/*--------------------------------------------------------------------------------*/

	
	@Override
	public TlvDataObject getTlvDataObject(TlvPath path, int index) {
		TlvTag currentTlvTag;
		
		if((path == null) || (path.size() == 0)) {throw new NullPointerException();}
		if((index < 0) || (index >= path.size())) {throw new IllegalArgumentException("index must not be outside of path");}
		
		currentTlvTag = path.get(index);
		if(currentTlvTag == null) {throw new NullPointerException();}
		
		for(TlvDataObject tlvDataObject : this.tlvObjects) {
			if(tlvDataObject.getTlvTag().matches(currentTlvTag)) {
				if(index == (path.size() - 1)) {
					return tlvDataObject;
				} else{
					if(tlvDataObject.isConstructedTLVObject()) {
						return ((ConstructedTlvDataObject) tlvDataObject).getTlvDataObject(path, index + 1);
					} else{
						return null;
					}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public TlvDataObject getTlvDataObject(TlvPath path) {
		return this.getTlvDataObject(path, 0);
	}
	
	@Override
	public TlvDataObject getTlvDataObject(TlvTag tlvTag) {
		if(tlvTag == null) {throw new NullPointerException("tag must not be null");}
		
		for(TlvDataObject tlvDataObject : this.tlvObjects) {
			if(tlvDataObject.getTlvTag().equals(tlvTag)) {
				return tlvDataObject;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean containsTlvDataObject(TlvTag tagField) {
		return this.getTlvDataObject(tagField) != null;
	}
	
	@Override
	public int getNoOfElements(boolean recursive) {
		int noOfElements;
		
		if(this.tlvObjects == null) {
			throw new NullPointerException();
		}
		
		noOfElements = this.tlvObjects.size();
		
		if(recursive) {
			for(TlvDataObject tlvDataObject : this.tlvObjects) {
				if(tlvDataObject.isConstructedTLVObject()) {
					noOfElements += ((ConstructedTlvDataObject) tlvDataObject).getNoOfElements(recursive);
				}
			}
		}
		
		return noOfElements;
	}
	
	@Override
	public int getNoOfElements() {
		return this.getNoOfElements(false);
	}

	/**
	 * @return the tlvObjects
	 */
	public List<TlvDataObject> getTlvObjects() {
		return tlvObjects;
	}
	
	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream outputStream;
		byte[] tlvObjectAsByteArray;
		
		outputStream = new ByteArrayOutputStream();
		
		for(TlvDataObject tlvObject : this.tlvObjects) {
			try {
				tlvObjectAsByteArray = tlvObject.toByteArray();
				outputStream.write(tlvObjectAsByteArray);
			} catch (IOException e) {
				logException(getClass(), e);
			}
		}

		return outputStream.toByteArray();
	}
	
	/*--------------------------------------------------------------------------------*/

	@Override
	public Iterator<TlvDataObject> iterator() {
		return this.tlvObjects.iterator();
	}
	
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public void sort(Comparator<TlvDataObject> comparator) {
		Collections.sort(this.tlvObjects, comparator);
	}
	
	@Override
	public void addTlvDataObject(TlvPath path, TlvDataObject tlvDataObject) {
		TlvDataObject supposedParent;
		ConstructedTlvDataObject actualParent;
		
		supposedParent = this.getTlvDataObject(path);
		
		if((supposedParent != null) && (supposedParent.isConstructedTLVObject())) {
			actualParent = (ConstructedTlvDataObject) supposedParent;
			actualParent.addTlvDataObject(tlvDataObject);
		}
	}
	
	@Override
	public void addTlvDataObject(TlvDataObject... tlvDataObject) {
		for (int i = 0; i < tlvDataObject.length; i++) {
			this.tlvObjects.add(tlvDataObject[i]);	
		}
	}
	
	@Override
	public void removeTlvDataObject(TlvPath path) {
		if(path == null) {throw new NullPointerException("path must not be null");};
		if(path.size() < 1) {throw new IllegalArgumentException("path must not be empty");};
		
		TlvTag tagToBeRemoved = path.getLastElement();
		
		if(path.size() == 1) {
			removeTlvDataObject(tagToBeRemoved);
		} else{
			TlvPath pathToParent = path.clone();
			pathToParent.remove(pathToParent.size() - 1);
			
			TlvDataObject supposedParent = this.getTlvDataObject(pathToParent);
			
			if((supposedParent != null) && (supposedParent.isConstructedTLVObject())) {
				ConstructedTlvDataObject actualParent = (ConstructedTlvDataObject) supposedParent;
				actualParent.removeTlvDataObject(tagToBeRemoved);
			}
		}
	}
	
	@Override
	public void removeTlvDataObject(TlvTag tlvTag) {
		TlvDataObject tlvDataObject;
		
		for(int i = 0; i < this.tlvObjects.size(); i++) {
			tlvDataObject = this.tlvObjects.get(i);
			if(tlvDataObject.matches(tlvTag)) {
				this.tlvObjects.remove(i);
			}
		}
	}

	@Override
	public int getLength() {
		int length;
		
		length = 0;
		
		for(TlvDataObject tlvDataObject : this.tlvObjects) {
			length += tlvDataObject.getLength();
		}
		
		return length;
	}
	
	@Override
	public String toString() {
		StringBuilder sb;
		
		sb = new StringBuilder();
		
		if(this.tlvObjects.size() > 1) {
			sb.append("(");
		}
		
		for(TlvDataObject tlvDataObject : this.tlvObjects) {
			sb.append("[");
			sb.append(tlvDataObject.toString());
			sb.append("]");
		}
		
		if(this.tlvObjects.size() > 1) {
			sb.append(")");
		}
		
		return sb.toString();
	}

	@Override
	public boolean isValidBerEncoding() {
		for(TlvDataObject tlvDataObject : tlvObjects) {
			if(!tlvDataObject.isValidBerEncoding()) {return false;}
		}
		
		return true;
	}
	
	@Override
	public boolean isValidDerEncoding() {
		/* first check elements for themselves */
		for(TlvDataObject tlvDataObject : tlvObjects) {
			if(!tlvDataObject.isValidDerEncoding()) {return false;}
		}
		
		/* then check combination of elements */
		if(tlvObjects.size() > 1) {
			Comparator<TlvDataObject> comparator = new TlvDataObjectComparatorDer();
			
			for(int i = 1; i < tlvObjects.size(); i++) {
				if(comparator.compare(tlvObjects.get(i-1), tlvObjects.get(i)) > 0) {return false;}
			}
		}
		
		return true;
	}
	
}
