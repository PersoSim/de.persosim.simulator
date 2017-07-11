package de.persosim.simulator.tlv;

import static org.globaltester.logging.BasicLogger.logException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
	
	protected List<TlvDataObject> tlvObjects;
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * Standard constructor for cases in which a tag is marked as constructed but the
	 * corresponding length field indicates length 0. As constructed tags are supposed
	 * to contain a TLV structure this is to prevent NullPointerExceptions when accessing
	 * this TLV structure although the structure is empty.
	 */
	public TlvDataObjectContainer() {
		this.tlvObjects = new ArrayList<>();
	}
	
	/**
	 * Constructor for all TLV structures with indicated length > 0.
	 * @param dataField the data field that contains the TLV structure
	 * @param minOffset the first offset to be used (inclusive)
	 * @param maxOffset the last offset to be used (exclusive)
	 */
	public TlvDataObjectContainer(byte[] dataField, int minOffset, int maxOffset) {
		if(dataField == null) {throw new IllegalArgumentException("dataField must not be null");}
		if(minOffset < 0) {throw new IllegalArgumentException("min offset must not be less than 0");}
		if(maxOffset < minOffset) {throw new IllegalArgumentException("max offset must not be smaller than min offset");}
		if(maxOffset > dataField.length) {throw new IllegalArgumentException("selected array area must not lie outside of data array");}
		
		this.tlvObjects = new ArrayList<>();
		
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tlvObjects == null) ? 0 : tlvObjects.hashCode());
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
		TlvDataObjectContainer other = (TlvDataObjectContainer) obj;
		if (tlvObjects == null) {
			if (other.tlvObjects != null)
				return false;
		} else if (!tlvObjects.equals(other.tlvObjects))
			return false;
		return true;
	}

	/*--------------------------------------------------------------------------------*/

	
	@Override
	public TlvDataObject getTlvDataObject(TlvPath path, int index) {
		if((path == null) || path.isEmpty()) {throw new IllegalArgumentException("path must neither be null nor empty");}
		if((index < 0) || (index >= path.size())) {throw new IllegalArgumentException("index must not be outside of path");}
		
		
		//find indicated child
		TlvDataObject indicatedChild = getTlvDataObject(path.get(index));
		
		if (indicatedChild == null) {
			return null;
		}
		
		if(index == (path.size() - 1)) {
			return indicatedChild;
		} else if(indicatedChild instanceof ConstructedTlvDataObject) {
			return ((ConstructedTlvDataObject) indicatedChild).getTlvDataObject(path, index + 1);
		} else{
			return null;
		}
		
	}
	
	@Override
	public TlvDataObject getTlvDataObject(TlvPath path) {
		return this.getTlvDataObject(path, 0);
	}
	
	@Override
	public TlvDataObject getTlvDataObject(TlvTagIdentifier tagIdentifier) {
		if(tagIdentifier == null) {throw new IllegalArgumentException("tag must not be null");}
		int remainingOccurences = tagIdentifier.getNoOfPreviousOccurrences();
		
		for(TlvDataObject tlvDataObject : this.tlvObjects) {
			if(tlvDataObject.getTlvTag().equals(tagIdentifier.getTag())) {
				if (remainingOccurences == 0) {
					return tlvDataObject;
				} else {
					remainingOccurences--;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public TlvDataObject getTlvDataObject(TlvTag tlvTag) {
		return getTlvDataObject(new TlvTagIdentifier(tlvTag));
	}
	
	@Override
	public boolean containsTlvDataObject(TlvTag tagField) {
		return this.getTlvDataObject(tagField) != null;
	}
	
	@Override
	public int getNoOfElements(boolean recursive) {
		int noOfElements = this.tlvObjects.size();
		
		if(recursive) {
			for(TlvDataObject tlvDataObject : this.tlvObjects) {
				if(tlvDataObject instanceof ConstructedTlvDataObject) {
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
		TlvDataObject supposedParent = getTlvDataObject(path);
		
		if((supposedParent != null) && (supposedParent instanceof ConstructedTlvDataObject)) {
			ConstructedTlvDataObject actualParent = (ConstructedTlvDataObject) supposedParent;
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
		if((path == null) || path.isEmpty()) {throw new IllegalArgumentException("path must neither be null nor empty");}
		
		if(path.size() == 1) {
			removeTlvDataObject(path.get(0));
		} else{
			TlvPath subPath = path.clone();
			subPath.remove(0);
			
			TlvDataObject supposedParent = getTlvDataObject(path.get(0));
			if((supposedParent != null) && (supposedParent instanceof ConstructedTlvDataObject)) {
				((ConstructedTlvDataObject) supposedParent).removeTlvDataObject(subPath);
			}
		}
	}
	
	@Override
	public void removeTlvDataObject(TlvTagIdentifier tagIdentifier) {
		TlvDataObject objToRemove = getTlvDataObject(tagIdentifier);
		tlvObjects.removeIf(arg -> arg == objToRemove);		
	}
	
	@Override
	public void removeTlvDataObject(TlvTag tlvTag) {
		removeTlvDataObject(new TlvTagIdentifier(tlvTag));
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
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		
		for(TlvDataObject tlvDataObject : this.tlvObjects) {
			sb.append("[");
			sb.append(tlvDataObject.toString());
			sb.append("]");
		}
		
		sb.append(")");
		
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
	
	@Override
	public TlvDataObjectContainer copy(){
		return new TlvDataObjectContainer(this.toByteArray());
	}
	
}
