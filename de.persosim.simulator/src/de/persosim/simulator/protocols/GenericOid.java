package de.persosim.simulator.protocols;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import de.persosim.simulator.utils.HexString;

/**
 * This class implements common functionality for OIDs. The
 * {@link #equals(Object)} implementation of all {@link GenericOid}s only checks
 * for the contained bytes.
 * 
 * @author slutters
 *
 */
public class GenericOid implements Oid{
	protected byte[] oidByteArray;
	
	public GenericOid(byte[] byteArrayRepresentation) {
		if(byteArrayRepresentation == null) {throw new NullPointerException("oid byte array is null but must not be null");}
		
		oidByteArray = byteArrayRepresentation.clone();
	}
	
	/**
	 * @return the oidByteArray
	 */
	public byte[] toByteArray() {
		return oidByteArray.clone();
	}

	/**
	 * This method returns the common name associated with this OID in form id-*
	 * <p/>
	 * Most prominent user of this method is {@link #toString()}.
	 * 
	 * @return the oidString
	 */
	public String getIdString() {
		return HexString.encode(oidByteArray);
	}
	
	@Override
	final public boolean equals(Object anotherOid) {
		if (anotherOid == null) return false;
		
		if(anotherOid instanceof Oid) {
			return Arrays.equals(this.oidByteArray, ((Oid) anotherOid).toByteArray());
		}
		
		return false;
	}
	
	@Override
	final public int hashCode() {
		return Arrays.hashCode(oidByteArray);
	}
	
	@Override
	public String toString() {
		return getIdString() + " (0x" + HexString.encode(oidByteArray) + ") [" + toDotString() + "]";
	}
	
	@Override
	public String toDotString() {
		List<Integer> oids = new LinkedList<Integer>();

		oids.add(oidByteArray[0] / 40);
		oids.add(oidByteArray[0] % 40);
		
		for (int i = 1; i < oidByteArray.length; i++) {
			if ((oidByteArray [i] & 0x80) == 0) {
				oids.add((int) oidByteArray[i]);
			} else {
				int oid = 0;
				boolean done = false;
				do {
					oid <<= 7;
					byte b = oidByteArray[i];
					if ((b & 0x80) == 0) {
						done = true;
					} else {
						b = (byte)(b & ~0x80);
						i++;
					}
					oid |= b;
					
				} while (!done);
				//oids.add(Utils.getIntFromUnsignedByteArray(Utils.invertByteOrder(Utils.toShortestUnsignedByteArray(oid))));
				oids.add(oid);
			}
		}
		
		StringJoiner builder = new StringJoiner(".");
		for (Integer i : oids) {
			builder.add(i.toString());
		}
		
		return builder.toString();
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
	
	/**
	 * This method returns the length of this OID in bytes
	 * @return this OID's length in bytes
	 */
	public int getLength() {
		return oidByteArray.length;
	}

}

