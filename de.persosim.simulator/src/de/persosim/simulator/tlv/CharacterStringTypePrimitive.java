package de.persosim.simulator.tlv;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */
public class CharacterStringTypePrimitive {
	
	protected TlvTag tlvTag;
	protected Pattern pattern;
	protected Charset charset;
	
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
	
//	public static boolean stringMatchesPattern(String input, CharacterStringTypePrimitive characterStringTypePrimitive) {
//		Matcher matcher = characterStringTypePrimitive.getPattern().matcher(input);
//		return matcher.matches();
//	}
//	
//	public static byte[] encode(String input, CharacterStringTypePrimitive characterStringTypePrimitive) {
//		if(!stringMatchesPattern(input, characterStringTypePrimitive)) {
//			throw new IllegalArgumentException("mismatching character string type");
//		}
//		
//		return input.getBytes(characterStringTypePrimitive.getCharset());
//	}
	
	public PrimitiveTlvDataObject encode(String input) {
		Matcher matcher = pattern.matcher(input);
		
		if(!matcher.matches()) {
			throw new IllegalArgumentException("mismatching character string type");
		}
		
		byte[] value = input.getBytes(charset);
		
		return new PrimitiveTlvDataObject(tlvTag, value);
	}
	
}
