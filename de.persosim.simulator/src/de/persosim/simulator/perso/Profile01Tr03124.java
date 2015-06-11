package de.persosim.simulator.perso;

import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class Profile01Tr03124 extends Profile01 {
	
	@Override
	protected void addTaTrustPoints() throws CertificateNotParseableException {
		// certificates
		byte[] cvcaAtData = HexString
				.toByteArray("7F218201BA7F4E8201725F2901004210444543564341654944435430303030317F4982011D060A04007F000702020202038120A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E537782207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9832026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B68441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F0469978520A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A78641047EC402F29B04079C9D89A8F732AD09BABC6538849128C6539B6C0F17EA4B72F56DD632376FA8CFD0E08E0DCA0F54802344F3137599121D20F9CADD358E5C3C7E8701015F2010444543564341654944435430303030317F4C12060904007F0007030102025305FE1FFFFFFF5F25060103000400075F24060106000400065F37406CB5D646D3F9AB8663946B7855CEAA589FD14BCC6917CE2FCFDD35AB3218DF16812EF181809FBFE53042424559D780618082765013F359991EE8216DF6C40E32");

		TlvDataObject cvcaAtTlv = ((ConstructedTlvDataObject) new TlvDataObjectContainer(
				cvcaAtData).getTlvDataObject(TlvConstants.TAG_7F21))
				.getTlvDataObject(TlvConstants.TAG_7F4E);

		// TA trustpoints
		TrustPointCardObject trustPointAt = new TrustPointCardObject(
				new TrustPointIdentifier(TerminalType.AT),
				new CardVerifiableCertificate(
						(ConstructedTlvDataObject) cvcaAtTlv));
		mf.addChild(trustPointAt);
	}
	
}
