package de.persosim.simulator.cardobjects;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.globaltester.cryptoprovider.Crypto;

import de.persosim.simulator.documents.Mrz;
import de.persosim.simulator.documents.MrzFactory;
import de.persosim.simulator.documents.MrzTD1;

/**
 * This authentication object constructs its returned password from a given MRZ.
 * 
 * @author mboonk
 * 
 */
//IMPL MrzAuthObject, relies on TD1 format
public class MrzAuthObject extends PasswordAuthObject {
	
	protected String mrz;
	protected byte[] idPicc;
		
	public MrzAuthObject(AuthObjectIdentifier identifier, String mrz, byte[] idPicc)
			throws NoSuchAlgorithmException,
			IOException {
		super(identifier, constructMrzPassword(mrz), "MRZ");
		this.mrz = mrz;
		this.idPicc = idPicc;
	}
	
	public MrzAuthObject(AuthObjectIdentifier identifier, String mrz)
			throws NoSuchAlgorithmException,
			IOException {
		this(identifier, mrz, extractIdPicc(mrz));
	}

	static byte[] extractIdPicc(String mrzString) {
		Mrz mrz = MrzFactory.parseMrz(mrzString);
		String documentNumber = mrz.getDocumentNumber();
		String documentNumberCheckDigit = mrz.getDocumentNumberCd();
		String documentNumberWithCheckDigit = documentNumber + documentNumberCheckDigit;

		return documentNumberWithCheckDigit.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * This method returns the input String used to compute the common secret
	 * from the MRZ
	 * 
	 * @return the input String used to compute the common secret from the MRZ
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	private static byte[] constructMrzPassword(String machineReadableZone)
			throws NoSuchAlgorithmException, IOException {
		
		
		byte[] digestInput = null;
		
		if (machineReadableZone.length() == 90) {
			digestInput = getDigestInputTd1(machineReadableZone);
		} else {
			digestInput = getDigestInputIdl(machineReadableZone);
		}
		
		MessageDigest md = MessageDigest.getInstance("SHA-1", Crypto.getCryptoProvider());
		return md.digest(digestInput);
	}

	static byte[] getDigestInputTd1(String machineReadableZone) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		Mrz mrz = new MrzTD1(machineReadableZone);

		/* document number */
		sb.append(mrz.getDocumentNumber());
		/* document number check digit */
		sb.append(mrz.getDocumentNumberCd());

		/* date of birth */
		sb.append(mrz.getDateOfBirth());
		/* date of birth check digit */
		sb.append(mrz.getDateOfBirthCd());

		/* Date of expiry */
		sb.append(mrz.getDateOfExpiry());
		/* Date of expiry check digit */
		sb.append(mrz.getDateOfExpiryCd());
		
		return sb.toString().getBytes("UTF-8");
	}
	
	static byte[] getDigestInputIdl(String machineReadableZone) throws UnsupportedEncodingException {
		return machineReadableZone.substring(1, 29).getBytes("UTF-8");
	}

	public String getMrz() {
		return mrz;
	}

	public byte[] getIdPicc() {
		if (idPicc == null ){
			idPicc = extractIdPicc(mrz);
		}
		return Arrays.copyOf(idPicc, idPicc.length);
	}
	
}
