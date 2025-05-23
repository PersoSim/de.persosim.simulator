package de.persosim.simulator.protocols.ca3;

import java.math.BigInteger;
import java.util.Arrays;

import de.persosim.simulator.crypto.DomainParameterSetEcdh;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

/**
 * This class represents a pseudonymous signature as described in TR-03110
 * v2.20.
 * 
 * @author mboonk
 */
public class PsSignature {

	private BigInteger c, s1, s2;

	public PsSignature(BigInteger c, BigInteger s1, BigInteger s2) {
		this.c = c;
		this.s1 = s1;
		this.s2 = s2;
	}

	/**
	 * @return the witness as {@link BigInteger}
	 */
	public BigInteger getC() {
		return c;
	}

	/**
	 * @return the first signature component
	 */
	public BigInteger getS1() {
		return s1;
	}

	/**
	 * @return the second signature component
	 */
	public BigInteger getS2() {
		return s2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		result = prime * result + ((s1 == null) ? 0 : s1.hashCode());
		return prime * result + ((s2 == null) ? 0 : s2.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PsSignature other = (PsSignature) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c)) {
			return false;
		}
		
		if (s1 == null) {
			if (other.s1 != null)
				return false;
		} else if (!s1.equals(other.s1)) {
			return false;
		} else {
			//s1 fields are equal
		}
		
		if (s2 == null) {
			if (other.s2 != null)
				return false;
		} else if (!s2.equals(other.s2)) {
			return false;
		} else {
			//s2 fields are equal
		}
		
		return true;
	}
	
	/**
	 * This method reconstructs a {@link PsSignature} object from its byte array encoding
	 * @param domainParametersEcdh the used domain parameters
	 * @param rawSignature the raw signature byte array to be parsed
	 * @return a {@link PsSignature} object from its byte array encoding
	 */
	public static PsSignature parsePsSignature(DomainParameterSetEcdh domainParametersEcdh, byte[] rawSignature) {
		return parsePsSignature(domainParametersEcdh.getPublicPointReferenceLengthL(), rawSignature);
	}
	
	/**
	 * This method reconstructs a {@link PsSignature} object from its byte array encoding
	 * @param publicPointReferenceLength the reference length for key encoding according to the used domain parameters
	 * @param rawSignature the raw signature byte array to be parsed
	 * @return a {@link PsSignature} object from its byte array encoding
	 */
	public static PsSignature parsePsSignature(int publicPointReferenceLength, byte[] rawSignature) {
		if(rawSignature.length < (2*publicPointReferenceLength)) {
			throw new IllegalArgumentException("signature too short for provided parameters");
		}
		
		byte[] cRaw, s1Raw, s2Raw;
		int indexEnd = rawSignature.length;
		int indexStart = indexEnd - publicPointReferenceLength;
		s2Raw = Arrays.copyOfRange(rawSignature, indexStart, indexEnd);
		indexEnd = indexStart;
		indexStart = indexEnd - publicPointReferenceLength;
		s1Raw = Arrays.copyOfRange(rawSignature, indexStart, indexEnd);
		indexEnd = indexStart;
		indexStart = 0;
		cRaw = Arrays.copyOfRange(rawSignature, indexStart, indexEnd);
		
		BigInteger c, s1, s2;
		c = new BigInteger(1, cRaw);
		s1 = new BigInteger(1, s1Raw);
		s2 = new BigInteger(1, s2Raw);
		
		return new PsSignature(c, s1, s2);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("PS Signature\n");
		sb.append("c : " + HexString.encode(Utils.toUnsignedByteArray(c)) + "\n");
		sb.append("s1: " + HexString.encode(Utils.toUnsignedByteArray(s1)) + "\n");
		sb.append("s2: " + HexString.encode(Utils.toUnsignedByteArray(s2)) + "\n");
		
		return sb.toString();
	}
	
	/**
	 * This method checks the length of the signature values and pads them if necessary.
	 * @param signature
	 * @return padded signature values assembled as byte array
	 */
	public static byte[] encode(PsSignature signature, int messageDigestSize, int publicPointReferenceLength) {
		byte [] c = signature.getC().toByteArray();
		c = Utils.removeLeadingZeroBytes(c);
		if (c.length > messageDigestSize){
			throw new IllegalArgumentException("The signature is too long for the provided message digest length");
		}
		byte [] s1 = signature.getS1().toByteArray();
		s1 = Utils.removeLeadingZeroBytes(s1);

		if (s1.length > publicPointReferenceLength){
			throw new IllegalArgumentException("The signature component 1 is longer than the public point reference length");
		}
		
		s1 = Utils.padWithLeadingZeroes(s1, publicPointReferenceLength);
		
		byte [] s2 = signature.getS2().toByteArray();
		s2 = Utils.removeLeadingZeroBytes(s2);

		if (s2.length > publicPointReferenceLength){
			throw new IllegalArgumentException("The signature component 2 is longer than the public point reference length");
		}
		
		s2 = Utils.padWithLeadingZeroes(s2, publicPointReferenceLength);
		return Utils.concatByteArrays(c, s1, s2);
	}

}
