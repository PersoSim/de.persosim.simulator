package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate03 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("JOHANNA EDELTRAUT LISBETH");
		persoDataContainer.setDg5PlainData("MUSTERMANN");
		persoDataContainer.setDg6PlainData("ORDENSSCHWESTER JOHANNA");
		persoDataContainer.setDg7PlainData("DR.");
		persoDataContainer.setDg8PlainData("19280421");
		persoDataContainer.setDg9PlainData("MÜNCHEN");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg13PlainData("VON MÜLLER-SCHWARZENBERG");
		persoDataContainer.setDg17StreetPlainData("BOUCHÉSTR. 68 A");
		persoDataContainer.setDg17CityPlainData("BERLIN");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("12059");
		persoDataContainer.setDg18PlainData("02761100000000");
		persoDataContainer.setDocumentNumber("000000003");
		persoDataContainer.setMrzLine3Of3("MUSTERMANN<<JOHANNA EDELTRAUT<");
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8"),
				HexString.toByteArray("A07EB62E891DAA84643E0AFCC1AF006891B669B8F51E379477DBEAB8C987A610")),
				41);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("04992622DD06173C593D84A07B9EFBCAD889483223063E3613A982503EE34285E60A10C5F0DACE211A769947C6986A415DB5FFE624F9C2961570E475A1201F35C1"),
				HexString.toByteArray("861BA9BF16AF946D48AD045C5B55044F188E4A4633CDF5A4E438EC016C5A8719")),
				45);

		// individual RI key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("04578955167E9C206D60A6AE4BCE69122DD9A0CEC36CA62F30E1E225674E09E6333DBFAA4DF3608D523BF7066BDC4E5645BD3E3C7F0D9075927EF208887C2801A3"),
				HexString.toByteArray("6A54A3417BE51977A8A38D8FF8113982B893E92EFD6AA28849EA160F545A071E")),
				1);

		// individual RI key (Pseudonym)
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("047F3885D4050E253C6C8686B0471A0EF4F0FADAAAC7507C4BB3A9144656EB5CD03D6E4D5D9CF02F57FF9411B155C96E8C9922F2E26E1F46768F0A9DFD9773B4C4"),
				HexString.toByteArray("348D3FDBB0164239D25EB6E3F34CC514D6EA8549AE20E8A0FADDF2E610478059")),
				2);
	}

}
