package de.persosim.simulator.utils;

public class Base64 {
	
	public interface Encoder {
		public String encode(byte[] input);	
	}
	
	private static Encoder encoder = null;
	
	public static Encoder getEncoder() {
		return encoder;
	}

	public static void setEncoder(Encoder encoder) {
		Base64.encoder = encoder;
	}

	public static String encode(byte[] input) {
		
		if (encoder != null) {
			return encoder.encode(input);
		}
		
		return java.util.Base64.getEncoder().encodeToString(input);
	}
	
	
	

}
