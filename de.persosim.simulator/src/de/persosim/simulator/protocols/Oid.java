package de.persosim.simulator.protocols;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.persosim.simulator.utils.HexString;

/**
 * 
 * This abstract class implements common functionality for OIDs.
 * 
 * @author slutters
 *
 */
@XmlTransient
public abstract class Oid {
	@XmlValue
	@XmlJavaTypeAdapter(HexBinaryAdapter.class)
	protected byte[] oidByteArray;
	
	public Oid() {
	}
	
	public Oid(byte[] byteArrayRepresentation) {
		if(byteArrayRepresentation == null) {throw new NullPointerException("oid byte array is null but must not be null");}
		
		oidByteArray = byteArrayRepresentation;
	}
	
	/**
	 * @return the oidByteArray
	 */
	public byte[] toByteArray() {
		return oidByteArray;
	}

	/**
	 * This method returns the common name associated with this OID in form id-*
	 * <p/>
	 * Most prominent user of this method is {@link #toString()}.
	 * 
	 * @return the oidString
	 */
	public abstract String getIdString();
	
	@Override
	public boolean equals(Object anotherOid) {
		if (anotherOid == null) return false;
		
		if(anotherOid.getClass() == getClass()) {
			return Arrays.equals(this.oidByteArray, ((Oid) anotherOid).oidByteArray);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(oidByteArray);
	}
	
	@Override
	public String toString() {
		return getIdString() + " (0x" + HexString.encode(oidByteArray) + ")";
	}
	
	/**
	 * This method checks whether the byte array representation of this object starts with the the provided OID prefix. 
	 * @param oidPrefix the provided OID prefix
	 * @return whether the byte array representation of this object starts with the the provided OID prefix
	 */
	public boolean startsWithPrefix(byte[] oidPrefix) {
		if(oidPrefix == null) {throw new NullPointerException("OID must not be null");}
		
		if(oidPrefix.length > oidByteArray.length) {return false;}
		if(oidPrefix.length == 0) {return true;}
		
		for (int i = 0; i < oidPrefix.length; i++)  {
			if(oidPrefix[i] != oidByteArray[i]) {
				return false;
			}
		}
		return true;
	}

}

