package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class Profile02 extends AbstractProfile {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = PersonalizationDataContainer.getDefaultContainer();
		persoDataContainer.setDg4PlainData("ANDRÉ");
		persoDataContainer.setDg5PlainData("MUSTERMANN");
		persoDataContainer.setDg8PlainData("19810617");
		persoDataContainer.setDg9PlainData("FRANKFURT (ODER)");
		persoDataContainer.setDg17StreetPlainData("EHM-WELK-STRAßE 33");
		persoDataContainer.setDg17CityPlainData("LÜBBENAU/SPREEWALD");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("03222");
		persoDataContainer.setDg18PlainData("02761200660196");
		
		String documentNumber = "000000002";
		String sex = "M";
		String mrzLine3 = "MUSTERMANN<<ANDRE<<<<<<<<<<<<<";
		String mrz = persoDataContainer.createMrzFromDgs(documentNumber, sex, mrzLine3);
		
		persoDataContainer.setMrz(mrz);
		persoDataContainer.setEpassDg1PlainData(mrz);
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8"),
				HexString.toByteArray("A07EB62E891DAA84643E0AFCC1AF006891B669B8F51E379477DBEAB8C987A610")),
				41, false);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("04A7A6034366A5AC38533FEF2D8D70E958E50D7152389054DBDF8922D5D7003C81A03EB8E32FF5BD640F20771056FE042AE4446B7B2B4CCD92BC0A9FBE168DBCA5"),
				HexString.toByteArray("95875264427F7B72AFA1547F1714F0F5EE808AF83B40B8B02DD237843991F2B6")),
				45, true);
		
		// individual RI key (Sperrmerkmal)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("047D1EA24146C3ADAC11143E7267B4E3EC572534828DB54904877B8D6EFDC5C13123A9E955890447643735C4F0AB9093FAA0C96DEFA1CE9079DA0B3C43BE6A0255"),
				HexString.toByteArray("1183F16814B3947D01DAED7F8D236769F5ABD8020FFF53C5E5FE86A8ABAB02D2")),
				1, false);

		// individual RI key (Pseudonym)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0438F989893E84090978AA09AD5611359D149180171E0F880467A327416B76C95650E791860C0DBE1E66A871A3C2E42514C3084322D3CB81D6187CA3FE0345B460"),
				HexString.toByteArray("86CF6C7F4D7A8A119C426A5195789DA496FFD134699E5E2D674E78A5E92091EB")),
				2, true);
	}

}
