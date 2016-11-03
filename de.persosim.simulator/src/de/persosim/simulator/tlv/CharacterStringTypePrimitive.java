package de.persosim.simulator.tlv;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides support for encoding primitive ASN.1 data structures, i.e. ASN.1 types that are not nested.
 */
public class CharacterStringTypePrimitive {
	
	protected TlvTag tlvTag;
	protected Pattern pattern;
	protected Charset charset;
	
	/**
	 * Constructor for a primitive character string type object
	 * @param tlvTag the tag to use (must be primitive)
	 * @param pattern the regex pattern to match for (optional)
	 * @param charset the character set to use for byte[] encoding
	 */
	public CharacterStringTypePrimitive(TlvTag tlvTag, Pattern pattern, Charset charset) {
		if(!tlvTag.indicatesEncodingPrimitive()) {
			throw new IllegalArgumentException("provided TLV tag must be primitive");
		}
		
		this.tlvTag = tlvTag;
		this.pattern = pattern;
		this.charset = charset;
	}
	
	public TlvTag getTlvTag() {
		return tlvTag;
	}
	public Pattern getPattern() {
		return pattern;
	}
	public Charset getCharset() {
		return charset;
	}
	
	/**
	 * This method returns an ASN.1 byte[] encoding of the object 
	 * @param input the value to be encoded
	 * @return the encoded byte[] representation
	 */
	public PrimitiveTlvDataObject encode(String input) {
		Matcher matcher = pattern.matcher(input);
		
		if(!matcher.matches()) {
			throw new IllegalArgumentException("mismatching character string type");
		}
		
		byte[] value = input.getBytes(charset);
		
		return new PrimitiveTlvDataObject(tlvTag, value);
	}
	
}
