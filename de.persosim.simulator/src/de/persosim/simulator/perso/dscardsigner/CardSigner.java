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


public class CardSigner {

	private String propertiesFile = "/home/tsenger/DScardSigner.properties";
	private Properties props = null;
	
	public CardSigner() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		props = loadProperties(propertiesFile);
	}

	public byte[] getSignature(String digestAlg, byte[] input) throws CardException, NoSuchAlgorithmException, NoSuchProviderException {
		byte[] digest = getDigest(digestAlg, input);
		return getCardSignature(props.getProperty("sigPIN"), digest);
		
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
