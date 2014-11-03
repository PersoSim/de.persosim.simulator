package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class Profile09 extends AbstractProfile {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = PersonalizationDataContainer.getDefaultContainer();
		persoDataContainer.setDg3PlainData("20161031");
		persoDataContainer.setDg4PlainData("LILLY");
		persoDataContainer.setDg5PlainData("SCHUSTER");
		persoDataContainer.setDg8PlainData("19980330");
		persoDataContainer.setDg9PlainData("MÜHLHAUSEN/THÜRINGEN");
		persoDataContainer.setDg17StreetPlainData("MARIENSTRAßE 144");
		persoDataContainer.setDg17CityPlainData("EISENACH");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("99817");
		persoDataContainer.setDg18PlainData("02761600560000");
		
		String documentNumber = "000000009";
		String sex = "F";
		String mrzLine3 = "SCHUSTER<<LILLY<<<<<<<<<<<<<<<";
		String mrz = persoDataContainer.createMrzFromDgs(documentNumber, sex, mrzLine3);
		
		persoDataContainer.setMrz(mrz);
		persoDataContainer.setEpassDg1PlainData(mrz);
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2"),
				HexString.toByteArray("8910074CF4749A916E5864654C768D57F57B6361F70A226DD1AEBED390BB066D")),
				41, false);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("04A2215455E8A7E71A5F811776616C990F9D0EC9C4E501B8D009DAF89FDCB29E047E8D4357F6321CDDE36C47BF4C29D663A44F828E32ACECAF25A3B32C67819177"),
				HexString.toByteArray("492CEBD9733FE276B57F9D029EDBF7FD68727DCA0E8E3BE695B77567570BE237")),
				45, true);

		// individual RI key
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("047A0F505E727B55B32EE70AD4C3FBBBFE0CCB91E7BDF2FFD4E8F75309C6CC37F18751E03636515C0BABC78DB429CC9E9713A005BE697C6F6CA475B1BBB0ABECF5"),
				HexString.toByteArray("1D12E4CBB86BA7374618EBB2758D3FD0DE5F72A2C2539F6B264227768412F7F6")),
				1);

		// individual RI key (Pseudonym)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("043465F1CCC05E0697F288EAD1C15A179C0ABEA3122B985F4A5F99FDEED98F62E132473F07FEABB8F4D00BE3D06BB1E4680A87E719A976F429365D3A5FFBE06C28"),
				HexString.toByteArray("0B4392E4D132935F7E42EA74840060B60421F950F3BC0A0789A7CD9CAE7ED625")),
				2);
	}

}
