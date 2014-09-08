package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate07 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("ANNEKATHRIN");
		persoDataContainer.setDg5PlainData("LERCH");
		persoDataContainer.setDg8PlainData("19760705");
		persoDataContainer.setDg9PlainData("BAD KÖNIGSHOFEN I. GRABFELD");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg13PlainData("BJØRNSON");
		persoDataContainer.setDg17CityPlainData("HALLE (SAALE)");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("06108");
		persoDataContainer.setDg18PlainData("02760200000000");
		persoDataContainer.setDocumentNumber("000000007");
		persoDataContainer.setMrzLine3Of3("LERCH<<ANNEKATHRIN<<<<<<<<<<<<");
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2"),
				HexString.toByteArray("8910074CF4749A916E5864654C768D57F57B6361F70A226DD1AEBED390BB066D")),
				42);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("043F8F473E895EF19EE2482771496C922468E15509A1A83610CC4F9D0E46E3170846B967928F18A46F7B4F8FF6D464E97E3ADD7324253BEE0521A57D2138060C63"),
				HexString.toByteArray("6777611DD39BCC83ADE6495F4F9C2379B7FEE67D662F470E32853186AE176EEF")),
				46);
	}
	
}
