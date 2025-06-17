package de.persosim.simulator.perso;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;

/**
 * Standard personalization with same defaults used within the test PKI but with
 * trustpoints based on keys provided.
 * <p/>
 * This personalization is intended to be as close to the currently available
 * nPA as possible. During development the closest already supported
 * configuration is used.
 *
 * @author amay
 *
 */
public class DefaultPersoGt extends Profile01 {

	@Override
	protected void addTaTrustPoints(MasterFile mf) throws CertificateNotParseableException, AccessDeniedException {
		if (trustPointAtStatic == null) {
			TrustPointCardObject trustPointIs = new TrustPointCardObject(new TrustPointIdentifier(TerminalType.IS),
					createRootCertIs());
			mf.addChild(trustPointIs);

			TrustPointCardObject trustPointAt = new TrustPointCardObject(new TrustPointIdentifier(TerminalType.AT),
					createRootCertAt());
			mf.addChild(trustPointAt);

			TrustPointCardObject trustPointSt = new TrustPointCardObject(new TrustPointIdentifier(TerminalType.ST),
					createRootCertSt());
			mf.addChild(trustPointSt);
		}
		else {
			mf.addChild(trustPointAtStatic);
		}
	}

	protected CardVerifiableCertificate createRootCertIs() throws CertificateNotParseableException {
			return createCertificate("personalization/gtCertificates/CFG.DFLT.EAC.IS/CVCA_Cert_01.cvcert");
	}

	protected CardVerifiableCertificate createRootCertAt() throws CertificateNotParseableException {
			return createCertificate("personalization/gtCertificates/CFG.DFLT.EAC.AT/CVCA_Cert_01.cvcert");
	}

	protected CardVerifiableCertificate createRootCertSt() throws CertificateNotParseableException {
			return createCertificate("personalization/gtCertificates/CFG.DFLT.EAC.ST/CVCA_Cert_01.cvcert");
	}

	protected static CardVerifiableCertificate createCertificate(String certPath)
			throws CertificateNotParseableException {
				String id = "de.persosim.simulator";
		String absolutePath = PersonalizationFileHelper.getFileFromPseudoBundle(id, id, certPath).getAbsolutePath();

		byte[] certData = PersonalizationFileHelper.readFromFile(absolutePath);

		ConstructedTlvDataObject certTlv = new ConstructedTlvDataObject(certData);

		return new CardVerifiableCertificate(certTlv);
	}
}
