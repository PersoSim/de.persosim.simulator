package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class Profile06 extends AbstractProfile {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = PersonalizationDataContainer.getDefaultContainer();
		persoDataContainer.setDg4PlainData("Hans-Günther");
		persoDataContainer.setDg5PlainData("von Drebenbusch-Dalgoßen");
		persoDataContainer.setDg6PlainData("Freiherr zu Möckern-Windensberg");
		persoDataContainer.setDg7PlainData("Dr.eh.Dr.");
		persoDataContainer.setDg8PlainData("19460125");
		persoDataContainer.setDg9PlainData("BREMERHAVEN");
		persoDataContainer.setDg13PlainData("Weiß");
		persoDataContainer.setDg17StreetPlainData("WEG NR. 12 8E");
		persoDataContainer.setDg17CityPlainData("HAMBURG");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("22043");
		persoDataContainer.setDg18PlainData("02760200000000");
		
		String documentNumber = "000000006";
		String sex = "M";
		String mrzLine3 = "VONDREBENBUSCHDALGOSSEN<<HANS<";
		String mrz = persoDataContainer.createMrz(documentNumber, sex, mrzLine3);
		
		persoDataContainer.setMrz(mrz);
		persoDataContainer.setEpassDg1PlainData(mrz);
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2"),
				HexString.toByteArray("8910074CF4749A916E5864654C768D57F57B6361F70A226DD1AEBED390BB066D")),
				42, false);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("043DA77A3738157092849CD540172494F28F76C83EE9B866838A7A8424139858D5496550AC5E4BC7C3932E0DFC7B6CB93CC3C10A07EB73F5AC97FBE9C9BDA50D1B"),
				HexString.toByteArray("935E8C1BA669471F87BEC93CF9671AD1A0504B8BFE5E3FB91A72074F4F6ECF45")),
				46, true);

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

}
