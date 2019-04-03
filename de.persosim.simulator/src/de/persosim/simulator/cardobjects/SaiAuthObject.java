package de.persosim.simulator.cardobjects;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.globaltester.cryptoprovider.Crypto;

import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.utils.HexString;

public class SaiAuthObject extends PasswordAuthObject {

	private String sai;

	public SaiAuthObject(AuthObjectIdentifier identifier, String sai) throws NoSuchAlgorithmException, IOException {
			super(identifier, constructSaiPassword(sai), "SAI");
			this.sai = sai;
		}

	private static byte[]  constructSaiPassword(String sai) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] retVal = sai.substring(1, 29).getBytes("UTF-8");
		
//		MessageDigest md = MessageDigest.getInstance("SHA-1", Crypto.getCryptoProvider());
//		retVal = md.digest(retVal);

//		retVal = HexString.toByteArray("348D2F25C266CC8068F99391BF0F5CCB876B5F5DDB004D0E5C8BCD1D3ACF2FDADA");
		MessageDigest md = MessageDigest.getInstance("SHA-256", Crypto.getCryptoProvider());
		retVal = md.digest(retVal);
		
//		retVal = HexString.toByteArray("8D2F25C266CC8068F99391BF0F5CCB876B5F5DDB004D0E5C8BCD1D3ACF2FDADA");
		retVal = Arrays.copyOf(retVal, 16);
		return retVal;
	}
	
	public String getSai() {
		return sai;
	}

}