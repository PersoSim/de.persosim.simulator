package de.persosim.simulator.perso.dscardsigner;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Properties;

import javax.smartcardio.CardException;

import de.persosim.simulator.utils.HexString;


/**
 * This class is the interface between the PersoSim personalization and the SigAnimaCardHandler.
 * It provides the signature function and returns the corresponding DS.
 * @author tsenger
 *
 */
/**
 * @author tsenger
 *
 */
public class CardSigner {

	/** 
	 * The properties file contains the slotId to which the SigAnima javacard is connected, 
	 * the AID of the SigAnima applet, the EF FID which contains the DS certificate 
	 * and the PIN the unlock the signature function
	 * example content:
	 *  
	 * <pre>
	 * slotId = 1
	 * saAID = D2760001324543534947
	 * sigPIN = 1234
	 * dsCertFID = 5302
	 * </pre>
	 * 
	 */
	private String propertiesFile = "/home/tsenger/DScardSigner.properties";
	private Properties props = null;
	
	public CardSigner() {
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());  // Already added by PersoSim
		props = loadProperties(propertiesFile);
	}

	/**
	 * This method gets the signature over the given input bytes. It first calculate the digest 
	 * send the digest to the signing function of the SigAnima card and returns the signature.
	 * 
	 * @param digestAlg Digest algorithm name to use e.g. SHA1, SHA224, SHA256, ...
	 * @param input the data to sign. Depending on the selected digest algorithm the signature algorithm is ECDSAwithSHAxxx
	 * @return The signature over the given input data. 
	 * @throws CardException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public byte[] getSignature(String digestAlg, byte[] input) throws CardException, NoSuchAlgorithmException, NoSuchProviderException {
		byte[] digest = getDigest(digestAlg, input);
		return getCardSignature(props.getProperty("sigPIN"), digest);
		
	}
	
	/**
	 * Reads the DS certificate which is stored in the SigAnima applet.
	 * 
	 * @return DS certificate
	 * @throws CardException
	 * @throws IOException
	 */
	public byte[] getDSCertificate() throws CardException, IOException  {
		byte[] dsCertBytes = null;
		short dsCertFID = Short.parseShort(props.getProperty("dsCertFID", "5302"), 16);

		synchronized (this) // select and read several times
        	{
            	SigAnimaCardHandler cardHandler = buildCardHandler();
                dsCertBytes = cardHandler.getFile(dsCertFID);
            }


        return dsCertBytes;
    }
	
	private byte[] getDigest(String algorithm, byte[] data) throws NoSuchAlgorithmException, NoSuchProviderException {
		MessageDigest mda = MessageDigest.getInstance(algorithm, "BC");
		return mda.digest(data);
	}
	
	private SigAnimaCardHandler buildCardHandler() throws CardException  {
		int slotId = Integer.parseInt(props.getProperty("slotId", "0"));
		byte[] saAID = HexString.toByteArray(props.getProperty("saAID", "D2760001324543534947"));
        return new SigAnimaCardHandler(slotId, saAID);
    }
	
	private byte[] getCardSignature(String pin, byte[] digest) throws CardException	{
		byte[] signatureBytes = null;
		
            synchronized (this) // verify, MSE, PSOSign
            {
                SigAnimaCardHandler cardHandler = buildCardHandler();
                boolean pinVerified = cardHandler.verify(pin);
                if (!pinVerified)
                    throw new CardException("PIN verification failed.");
                else
                	signatureBytes = cardHandler.sign((byte)0, digest);
            }
     
		return signatureBytes;
	}
	
	private Properties loadProperties(String filename) {
		
		Properties props = new Properties();
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);
			BufferedInputStream stream = new BufferedInputStream(fis);
			props.load(stream);
			stream.close();
		} catch (IOException e) {
			
		}
		return props;
		
	}

}
