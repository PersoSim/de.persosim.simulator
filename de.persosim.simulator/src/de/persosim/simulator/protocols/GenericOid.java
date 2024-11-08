package de.persosim.simulator.protocols;

import java.util.Arrays;

import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

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
		if(byteArrayRepresentation == null) {throw new NullPointerException("oid byte array is null, but must not be null");}
		
		oidByteArray = byteArrayRepresentation.clone();
	}
	
	public GenericOid(String dotString)	{
		if (dotString == null) {
			throw new NullPointerException("oid dot string is null, but must not be null");
		}

		String[] split = dotString.split("\\.");
		StringBuilder hexBuilder = new StringBuilder();
		for (int i = 0; i < split.length; i++) {
			// skip first if 0
			int asInt = Integer.parseInt(split[i]);
			if (i == 0 && asInt == 0)
				continue;
			hexBuilder.append(HexString.hexifyByte(asInt));
		}
		oidByteArray = HexString.toByteArray(hexBuilder.toString()).clone();
	}
	
	public GenericOid(Oid prefix, byte... suffix) {
		this(Utils.concatByteArrays(prefix.toByteArray(), suffix));
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
	public final int hashCode() {
		return Arrays.hashCode(oidByteArray);
	}
	
	@Override
	public String toString() {
		return getIdString() + " (0x" + HexString.encode(oidByteArray) + ") [" + toDotString() + "]";
	}
	
	@Override
	public String toDotString() {
		
//		try {
//			return new org.ietf.jgss.Oid(oidByteArray).toString();
//		}
//		catch (GSSException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(Integer.toString(oidByteArray[0] / 40));
		builder.append(".");
		builder.append(Integer.toString(oidByteArray[0] % 40));
		
		for (int i = 1; i < oidByteArray.length; i++) {
			int current; 
			if ((oidByteArray [i] & 0x80) == 0) {
				current = (int) oidByteArray[i];
			} else {
				current = 0;
				boolean done = false;
				do {
					current <<= 7;
					byte b = oidByteArray[i];
					if ((b & 0x80) == 0) {
						done = true;
					} else {
						b = (byte)(b & ~0x80);
						i++;
					}
					current |= b;
					
				} while (!done);
			}
			
			builder.append(".");
			builder.append(Integer.toString(current));
		}
		
		return builder.toString();
	}

	@Override
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
	
	@Override
	public boolean startsWithPrefix(Oid oidPrefix) {
		return startsWithPrefix(oidPrefix.toByteArray());
	}
	
	@Override
	public int getLength() {
		return oidByteArray.length;
	}

}

