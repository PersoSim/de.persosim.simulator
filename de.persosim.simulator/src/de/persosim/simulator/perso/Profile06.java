package de.persosim.simulator.perso;

import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.protocols.TR03110Utils;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class Profile06 extends AbstractProfile {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("Hans-Günther");
		persoDataContainer.setDg5PlainData("von Drebenbusch-Dalgoßen");
		persoDataContainer.setDg6PlainData("Freiherr zu Möckern-Windensberg");
		persoDataContainer.setDg7PlainData("Dr.eh.Dr.");
		persoDataContainer.setDg8PlainData("19460125");
		persoDataContainer.setDg9PlainData("BREMERHAVEN");
		persoDataContainer.setDg11PlainData("M"); //XXX this is needed in order to make MRZ generation work but should not be included in data groups
		persoDataContainer.setDg13PlainData("Weiß");
		persoDataContainer.setDg17StreetPlainData("WEG NR. 12 8E");
		persoDataContainer.setDg17CityPlainData("HAMBURG");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("22043");
		persoDataContainer.setDg18PlainData("02760200000000");
		persoDataContainer.setDocumentNumber("000000006");
		persoDataContainer.setMrzLine3Of3("VONDREBENBUSCHDALGOSSEN<<HANS<");
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2"),
				HexString.toByteArray("8910074CF4749A916E5864654C768D57F57B6361F70A226DD1AEBED390BB066D")),
				41, false);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("043DA77A3738157092849CD540172494F28F76C83EE9B866838A7A8424139858D5496550AC5E4BC7C3932E0DFC7B6CB93CC3C10A07EB73F5AC97FBE9C9BDA50D1B"),
				HexString.toByteArray("935E8C1BA669471F87BEC93CF9671AD1A0504B8BFE5E3FB91A72074F4F6ECF45")),
				45, true);

		// individual RI key
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("041DF62F5438AD5132BF8799295F4B4C4887F21151FC98330193FCBE501D2560F0181BA7E9508C82C27EFD5BDD5499D84E86C442FAF37383FBBF4C104C8E9ED9DF"),
				HexString.toByteArray("0826A30BD682ABF632911F6380C9CF6A65C191DB1C62DCB4A25C1EA023FB6E97")),
				1);

		// individual RI key (Pseudonym)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0439036762A5736E37A55D4F4E875CAB87744DB74ADD255BA42C6533729AF1D95AA6AF1B264E53C08CD5FAE58684F462BD2AC6E6CFDFD8ADCB6BA65894439AA6FA"),
				HexString.toByteArray("0640C5FB16B6083DF60DE4A00B231B22ED012FC672E5EE2849CAB6FB93CA947C")),
				2);
	}
	
	@Override
	protected void addTaTrustPoints() throws CertificateNotParseableException {
		// certificates
		byte[] cvcaAtData = HexString
				.toByteArray("7F218201BA7F4E8201725F2901004210444543564341654944435430303030317F4982011D060A04007F000702020202038120A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E537782207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9832026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B68441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F0469978520A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A78641047EC402F29B04079C9D89A8F732AD09BABC6538849128C6539B6C0F17EA4B72F56DD632376FA8CFD0E08E0DCA0F54802344F3137599121D20F9CADD358E5C3C7E8701015F2010444543564341654944435430303030317F4C12060904007F0007030102025305FE1FFFFFFF5F25060103000400075F24060106000400065F37406CB5D646D3F9AB8663946B7855CEAA589FD14BCC6917CE2FCFDD35AB3218DF16812EF181809FBFE53042424559D780618082765013F359991EE8216DF6C40E32");

		TlvDataObject cvcaAtTlv = ((ConstructedTlvDataObject) new TlvDataObjectContainer(
				cvcaAtData).getTlvDataObject(TR03110Utils.TAG_7F21))
				.getTlvDataObject(TR03110Utils.TAG_7F4E);

		// TA trustpoints
		TrustPointCardObject trustPointAt = new TrustPointCardObject(
				new TrustPointIdentifier(TerminalType.AT),
				new CardVerifiableCertificate(
						(ConstructedTlvDataObject) cvcaAtTlv));
		mf.addChild(trustPointAt);
	}

}
