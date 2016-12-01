package de.persosim.simulator.perso;

import java.io.File;

import org.globaltester.PlatformHelper;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;

public class DefaultPersoTestPki extends DefaultPersonalization {

	@Override
	protected void addTaTrustPoints(MasterFile mf) throws AccessDeniedException, CertificateNotParseableException {
		// use BSI Test-PKI CVCA root certificate
		String fileNameCvcaAt = "personalization/gtCertificates/DETESTeID00005.cvcert";
		File cvcaAt = new File(fileNameCvcaAt);
		String absolutePathCvcaAt = cvcaAt.getAbsolutePath().replace(".test", "");
		byte[] cvcaAtData = PlatformHelper.readFromFile(absolutePathCvcaAt);
		
		ConstructedTlvDataObject cvcaAtTlv = new ConstructedTlvDataObject(cvcaAtData);

		// TA trustpoints
		TrustPointCardObject trustPointAt = new TrustPointCardObject(new TrustPointIdentifier(TerminalType.AT),
				new CardVerifiableCertificate(cvcaAtTlv));
		mf.addChild(trustPointAt);
	}
}
