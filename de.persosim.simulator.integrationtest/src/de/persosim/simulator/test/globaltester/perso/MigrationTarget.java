package de.persosim.simulator.test.globaltester.perso;

/**
 * Define a MigrationTarget, i.e. contains all relevant configuration values to
 * perform a migration test.
 * 
 * @author amay
 *
 */
public enum MigrationTarget {
	
	
	ECDSA_SHA1_P256r1(MigrationType.ECDSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.ecdsaWithSHA1", "Packages.org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers.brainpoolP256r1"),
	ECDSA_SHA224_P256r1(MigrationType.ECDSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.ecdsaWithSHA224", "Packages.org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers.brainpoolP256r1"),
	ECDSA_SHA256_P256r1(MigrationType.ECDSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.ecdsaWithSHA256", "Packages.org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers.brainpoolP256r1"),
	ECDSA_SHA384_P256r1(MigrationType.ECDSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.ecdsaWithSHA384", "Packages.org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers.brainpoolP256r1"),
	ECDSA_SHA384_P384r1(MigrationType.ECDSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.ecdsaWithSHA384", "Packages.org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers.brainpoolP384r1"),
	ECDSA_SHA512_P256r1(MigrationType.ECDSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.ecdsaWithSHA512", "Packages.org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers.brainpoolP256r1"),
	
	RSA_SHA1_1024(MigrationType.RSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.rsa1_5WithSHA1", "1024"),
	RSA_SHA256_1024(MigrationType.RSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.rsa1_5WithSHA256", "1024"),
	RSA_SHA512_1024(MigrationType.RSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.rsa1_5WithSHA512", "1024"),

	RSAPSS_SHA1_1024(MigrationType.RSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.rsa1_5WithSHA1", "1024"),
	RSAPSS_SHA256_1024(MigrationType.RSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.rsa1_5WithSHA256", "1024"),
	RSAPSS_SHA512_1024(MigrationType.RSA, "Packages.com.secunet.globaltester.epassport.eac.pki.CVObjectIdentifier.rsa1_5WithSHA512", "1024"),
	;

	public final MigrationType type;
	public final String sigAlg;
	public final String param;

	private MigrationTarget(MigrationType type, String sigAlg, String param) {
		this.type = type;
		this.sigAlg = sigAlg;
		this.param = param;
	}
	
}
