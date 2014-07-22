package de.persosim.simulator.perso;

import de.persosim.simulator.tlv.TlvDataObject;

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

	@Override
	protected TlvDataObject getCertificate() {
		// TODO Auto-generated method stub
		return super.getCertificate();
	}

	@Override
	protected byte[] getSignature(byte[] sigInput) {
		// TODO Auto-generated method stub
		return super.getSignature(sigInput);
	}

}
