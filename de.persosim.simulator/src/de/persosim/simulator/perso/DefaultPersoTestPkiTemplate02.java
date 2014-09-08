package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate02 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("ANDRÉ");
		persoDataContainer.setDg5PlainData("MUSTERMANN");
		persoDataContainer.setDg8PlainData("19810617");
		persoDataContainer.setDg9PlainData("FRANKFURT (ODER)");
		persoDataContainer.setDg11PlainData("M");
		persoDataContainer.setDg17StreetPlainData("EHM-WELK-STRAßE 33");
		persoDataContainer.setDg17CityPlainData("LÜBBENAU/SPREEWALD");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("03222");
		persoDataContainer.setDg18PlainData("02761200660196");
		persoDataContainer.setDocumentNumber("000000002");
		persoDataContainer.setMrzLine3Of3("MUSTERMANN<<ANDRE<<<<<<<<<<<<<");
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8"),
				HexString.toByteArray("A07EB62E891DAA84643E0AFCC1AF006891B669B8F51E379477DBEAB8C987A610")),
				41);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("04A7A6034366A5AC38533FEF2D8D70E958E50D7152389054DBDF8922D5D7003C81A03EB8E32FF5BD640F20771056FE042AE4446B7B2B4CCD92BC0A9FBE168DBCA5"),
				HexString.toByteArray("95875264427F7B72AFA1547F1714F0F5EE808AF83B40B8B02DD237843991F2B6")),
				45);
	}

}
