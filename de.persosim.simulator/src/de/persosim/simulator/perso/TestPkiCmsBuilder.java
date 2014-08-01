package de.persosim.simulator.perso;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.smartcardio.CardException;

import de.persosim.simulator.perso.dscardsigner.CardSigner;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;

import static de.persosim.simulator.utils.PersoSimLogger.WARN;
import static de.persosim.simulator.utils.PersoSimLogger.log;

/**
 * SecInfoCmsBuilder that allows creation of valid Signatures within the German
 * nPA TestPKI
 * <p/>
 * This implementation returns a certificate and an according signature based on
 * a provided smart card.
 * <p/>
 * The superclass may cache these results in order to provide valid signatures
 * without the need to provide a smartcard, as long as the provided value
 * already was signed.
 * 
 * @author amay, tsenger
 * 
 */
public class TestPkiCmsBuilder extends DefaultSecInfoCmsBuilder {
	
	private CardSigner cardSigner;
		
	public TestPkiCmsBuilder() {
		cardSigner = new CardSigner();
	}

	@Override
	protected TlvDataObject getCertificate() {
		
		byte[] dsCertBytes = null;
		
		try {
			dsCertBytes = cardSigner.getDSCertificate();
			if (dsCertBytes==null) return super.getCertificate();
			else return new ConstructedTlvDataObject(dsCertBytes);
		} catch (CardException | IOException e) {
			log(TestPkiCmsBuilder.class, e.getMessage(), WARN);
			return super.getCertificate();
		}
	}

	@Override
	protected byte[] getSignature(byte[] sigInput) {	
		
		String digestAlgorithm = "SHA224"; //TODO Get digest algorithm by parsing getDigestAlgorithm()
		byte[] signature = null;
		
		try {
			signature = cardSigner.getSignature(digestAlgorithm, sigInput);
			if (signature==null) signature = super.getSignature(sigInput);
		} catch (CardException | NoSuchAlgorithmException | NoSuchProviderException e) {
			log(TestPkiCmsBuilder.class, e.getMessage(), WARN);
			return super.getSignature(sigInput);
		}
		return signature;
	}
	
	

}
