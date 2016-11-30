package de.persosim.simulator.perso;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.utils.HexString;

public class DefaultPersoTestPki extends DefaultPersonalization {

	@Override
	protected void addTaTrustPoints(MasterFile mf) throws AccessDeniedException, CertificateNotParseableException {
		// BSI Test-PKI CVCA root certificate
		// CAR/CHR:    DETESTeID00005
		// valid from: 04.05.2015
		// valid to  : 04.05.2018
		byte[] cvcaAtData = HexString
				.toByteArray("7F218201B67F4E82016E5F290100420E44455445535465494430303030357F4982011D060A04007F000702020202038120A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E537782207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9832026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B68441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F0469978520A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A78641049BFEBA8DC7FAAB6E3BDEB3FF794DBB800848FE4F6940A4CC7EECB5159C87DA5395505892026D420A22596CD014ED1FD872DADA597DB0F8D64441041198F62D448701015F200E44455445535465494430303030357F4C12060904007F0007030102025305FC0F13FFFF5F25060105000500045F24060108000500045F374058B4E65598EFB9CA2CAFC05C80F5A907E8B69C3897C704739320896DC53492E47766841A9C3D4EAC85CE653D166B53DB06A70E735AB93C88858811EF69D6B543");

		ConstructedTlvDataObject cvcaAtTlv = new ConstructedTlvDataObject(cvcaAtData);

		// TA trustpoints
		TrustPointCardObject trustPointAt = new TrustPointCardObject(new TrustPointIdentifier(TerminalType.AT),
				new CardVerifiableCertificate(cvcaAtTlv));
		mf.addChild(trustPointAt);
	}
}
