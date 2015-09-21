package de.persosim.simulator.crypto.certificates;

import java.security.NoSuchAlgorithmException;
import java.security.Signature;

import de.persosim.simulator.protocols.OidIf;

public interface CvOid extends OidIf {
	
	public Signature getSignature() throws NoSuchAlgorithmException;
	
}
