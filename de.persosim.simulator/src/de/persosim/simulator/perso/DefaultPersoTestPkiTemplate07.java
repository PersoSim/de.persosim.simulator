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
		persoDataContainer.setDg11PlainData("F"); //XXX this is needed in order to make MRZ generation work but should not be included in data groups
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
				42, false);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("043F8F473E895EF19EE2482771496C922468E15509A1A83610CC4F9D0E46E3170846B967928F18A46F7B4F8FF6D464E97E3ADD7324253BEE0521A57D2138060C63"),
				HexString.toByteArray("6777611DD39BCC83ADE6495F4F9C2379B7FEE67D662F470E32853186AE176EEF")),
				46, true);
		
		// individual RI key
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("043CAE3E81FE93B9404191A398C3D51CFA13938ACA6AD413A6F8AF07201277ACAA3C0F925C68ED2DCEFB9C8389A569FEA7E40D7F411758190FA729D570399AF76E"),
				HexString.toByteArray("1876476346AF80B6F097AE98CA4D97DE41F3F1953C2E458F53F30E1EC5563B97")),
				1);

		// individual RI key (Pseudonym)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0457BB80902D94AE7243F0737421C21D1C30D238F4F21A22FC14EED18D3BE5191035DC471939D887E376A925FA8AEFFFA97F3553EA84844B725EA3B1393ACB3E95"),
				HexString.toByteArray("0B485F3D9D674B2A1F25ED6FA1E54392D57389358535DA57D6C0DFFA3649C6E6")),
				2);
	}
	
}
