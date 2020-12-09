package de.persosim.simulator.cardobjects;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * This authentication object constructs its returned password from a given MRZ.
 * 
 * @author mboonk
 * 
 */
//IMPL MrzAuthObject, relies on TD1 format
public class NonHashedMrzAuthObject extends PasswordAuthObject {
	
	protected String mrz;
	protected byte[] idPicc;
		
	public NonHashedMrzAuthObject(AuthObjectIdentifier identifier, String mrz, byte[] idPicc)
			throws NoSuchAlgorithmException,
			IOException {
		super(identifier, constructNonHashedMrzPassword(mrz), "MRZ");
		this.mrz = mrz;
		this.idPicc = idPicc;
	}
	
	public NonHashedMrzAuthObject(AuthObjectIdentifier identifier, String mrz)
			throws NoSuchAlgorithmException,
			IOException {
		this(identifier, mrz, MrzAuthObject.extractIdPicc(mrz));
	}

	/**
	 * This method returns the input String used to compute the common secret
	 * from the MRZ
	 * 
	 * @return the input String used to compute the common secret from the MRZ
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	private static byte[] constructNonHashedMrzPassword(String machineReadableZone)
			throws NoSuchAlgorithmException, IOException {
		if (machineReadableZone.length() == 90) {
			return MrzAuthObject.getDigestInputTd1(machineReadableZone);
		} else {
			return MrzAuthObject.getDigestInputIdl(machineReadableZone);
		}
	}

	public String getMrz() {
		return mrz;
	}

	public byte[] getIdPicc() {
		if (idPicc == null ){
			idPicc = MrzAuthObject.extractIdPicc(mrz);
		}
		return Arrays.copyOf(idPicc, idPicc.length);
	}
	
}
