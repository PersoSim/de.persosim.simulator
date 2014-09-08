package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate10 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("HILDEGARD");
		persoDataContainer.setDg5PlainData("MÜLLER");
		persoDataContainer.setDg8PlainData("19390204");
		persoDataContainer.setDg9PlainData("SAARBRÜCKEN");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg17StreetPlainData("HARKORTSTR. 58");
		persoDataContainer.setDg17CityPlainData("DORTMUND");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("44225");
		persoDataContainer.setDg18PlainData("02760509130000");
		persoDataContainer.setDocumentNumber("000000011");
		persoDataContainer.setMrzLine3Of3("MUELLER<<HILDEGARD<<<<<<<<<<<<");
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2"),
				HexString.toByteArray("8910074CF4749A916E5864654C768D57F57B6361F70A226DD1AEBED390BB066D")),
				41);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0465F192A4270BB47C0C05BB4FDE29DB38031082F0DD830D533C3D0EB52F31F1249E6773E2202C7E168A20A1F2CAD4ED61766C697C3E3533916ADE402B412DAEA5"),
				HexString.toByteArray("46A957BAE8EA7D99183CAC13345CE667EC2F76D70E0095CE15D01F2686C3BD64")),
				45);
	}

}
