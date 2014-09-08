package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate09 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg3PlainData("20161031");
		persoDataContainer.setDg4PlainData("LILLY");
		persoDataContainer.setDg5PlainData("SCHUSTER");
		persoDataContainer.setDg8PlainData("19980330");
		persoDataContainer.setDg9PlainData("MÜHLHAUSEN/THÜRINGEN");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg13PlainData("VON MÜLLER-SCHWARZENBERG");
		persoDataContainer.setDg17StreetPlainData("MARIENSTRAßE 144");
		persoDataContainer.setDg17CityPlainData("EISENACH");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("99817");
		persoDataContainer.setDg18PlainData("02761600560000");
		persoDataContainer.setDocumentNumber("000000009");
		persoDataContainer.setMrzLine3Of3("SCHUSTER<<LILLY<<<<<<<<<<<<<<<");
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2"),
				HexString.toByteArray("8910074CF4749A916E5864654C768D57F57B6361F70A226DD1AEBED390BB066D")),
				41);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("04A2215455E8A7E71A5F811776616C990F9D0EC9C4E501B8D009DAF89FDCB29E047E8D4357F6321CDDE36C47BF4C29D663A44F828E32ACECAF25A3B32C67819177"),
				HexString.toByteArray("492CEBD9733FE276B57F9D029EDBF7FD68727DCA0E8E3BE695B77567570BE237")),
				45);
	}

}
