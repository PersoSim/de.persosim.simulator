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
				41, false);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0465F192A4270BB47C0C05BB4FDE29DB38031082F0DD830D533C3D0EB52F31F1249E6773E2202C7E168A20A1F2CAD4ED61766C697C3E3533916ADE402B412DAEA5"),
				HexString.toByteArray("46A957BAE8EA7D99183CAC13345CE667EC2F76D70E0095CE15D01F2686C3BD64")),
				45, true);

		// individual RI key
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("047A22C88B35A4E54A3965D78F55E4AA5B86972374D30756003E9FB9096E0464421DF17554676EA0882970ABF9500D5F48CBD551089C26F07A3D505FEBD7C408DD"),
				HexString.toByteArray("896C2649235292B40B496347BC39B0CA74333F76FECBF84EB22D481A7B46FA2A")),
				1);

		// individual RI key (Pseudonym)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("048F781988858844CFB8857E755B5CB13FB8FFCE80C82852B30C1F07D3AEBA79A93C92AF0AEB9A52265131373A95D6905FEF22625F0C34179AB80FA7BCC7ACCDC5"),
				HexString.toByteArray("182A179497FEE3E39830B517D0207B4E622C041C05720B748157B09DDFC8ABA2")),
				2);
	}

}
