package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate01 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("ERIKA");
		persoDataContainer.setDg5PlainData("MUSTERMANN");
		persoDataContainer.setDg8PlainData("19640812");
		persoDataContainer.setDg9PlainData("BERLIN");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg13PlainData("GABLER");
		persoDataContainer.setDg17StreetPlainData("HEIDESTRASSE 17");
		persoDataContainer.setDg17CityPlainData("KÖLN");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("51147");
		persoDataContainer.setDg18PlainData("02760503150000");
		persoDataContainer.setDocumentNumber("000000001");
		persoDataContainer.setMrzLine3Of3("MUSTERMANN<<ERIKA<<<<<<<<<<<<<");
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8"),
				HexString.toByteArray("A07EB62E891DAA84643E0AFCC1AF006891B669B8F51E379477DBEAB8C987A610")),
				41);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("041AC6CAE884A6C2B8461404150F54CD1150B21E862A4E5F21CE34290C741104BD1BF31ED91E085D7C630E8B4D10A8AE22BBB2898B44B52EA0F4CDADCF57CFBA25"),
				HexString.toByteArray("763B6BBF8A7DFC5DAB3205791BA64D211BBC4E8A5C531C77488792C508BD3D1E")),
				45);
	}
	
}
