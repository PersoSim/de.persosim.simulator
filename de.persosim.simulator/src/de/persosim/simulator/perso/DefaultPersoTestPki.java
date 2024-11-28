package de.persosim.simulator.perso;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;

public class DefaultPersoTestPki extends DefaultPersonalization
{
	protected static TrustPointCardObject trustPointAtStatic = null;

	public static void setStaticTrustPointAt(TrustPointCardObject trustPointAt)
	{
		trustPointAtStatic = trustPointAt;
	}

	@Override
	protected void addTaTrustPoints(MasterFile mf) throws AccessDeniedException, CertificateNotParseableException
	{
		TrustPointCardObject trustPointAt = null;
		if (trustPointAtStatic == null) {
			// use BSI Test-PKI CVCA root certificate
			String fileNameCvcaAt = "personalization/gtCertificates/DETESTeID00005.cvcert";
			String id = "de.persosim.simulator";
			String absolutePathCvcaAt = PersonalizationFileHelper.getFileFromPseudoBundle(id, id, fileNameCvcaAt).getAbsolutePath();
			byte[] cvcaAtData = PersonalizationFileHelper.readFromFile(absolutePathCvcaAt);

			ConstructedTlvDataObject cvcaAtTlv = new ConstructedTlvDataObject(cvcaAtData);

			trustPointAt = new TrustPointCardObject(new TrustPointIdentifier(TerminalType.AT), new CardVerifiableCertificate(cvcaAtTlv));
		}
		else {
			trustPointAt = trustPointAtStatic;
		}
		mf.addChild(trustPointAt);
	}
}
