package de.persosim.simulator.crypto.certificates;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import de.persosim.simulator.utils.HexString;

/**
 * This class represents a private key.
 * 
 * @author slutters
 *
 */
public abstract class CvPrivateKey extends CvKey implements PrivateKey {

	private static final long serialVersionUID = 1L;
	
	public CvPrivateKey(CvOid cvOid, PrivateKey privateKey) {
		super(cvOid, privateKey);
	}
	
	public byte[] sign(byte[] dataToBeSigned) {
		Signature signer;
		
		byte[] unprocessedSignature = null;
		
		try {
			signer = cvOid.getSignature();
			
			System.out.println("signing algorithm is: " + signer.getAlgorithm());
			
			signer.initSign((PrivateKey) this.key);
			signer.update(dataToBeSigned);
			unprocessedSignature = (signer.sign());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] value = postProcessSIgnature(unprocessedSignature);
		
		System.out.println("processed signature is: " + HexString.encode(value));
		
		return value;
		
	}
	
	/**
	 * This method performs post-processing on signatures specific to the needs of the used crypto system.
	 * @param unprocessedSignature the unprocessed signature
	 * @return the processed signature
	 */
	public abstract byte[] postProcessSIgnature(byte[] unprocessedSignature);

}
