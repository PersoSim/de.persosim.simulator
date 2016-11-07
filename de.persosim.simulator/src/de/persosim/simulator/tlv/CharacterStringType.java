package de.persosim.simulator.tlv;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * 
 */
public interface CharacterStringType {

	public byte getTag();
	
	public Pattern getPattern();
	
	public Charset getCharset();

}
