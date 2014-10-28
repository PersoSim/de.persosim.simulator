package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class Profile01 extends AbstractProfile {
	//FIXME make this Profile the new DefaultPersonalisation and fix the inheritance hierarchy
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("ERIKA");
		persoDataContainer.setDg5PlainData("MUSTERMANN");
		persoDataContainer.setDg8PlainData("19640812");
		persoDataContainer.setDg9PlainData("BERLIN");
		persoDataContainer.setDg11PlainData("F"); //FIXME this is needed in order to make MRZ generation work but should not be included in data groups
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
				41, false);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("041AC6CAE884A6C2B8461404150F54CD1150B21E862A4E5F21CE34290C741104BD1BF31ED91E085D7C630E8B4D10A8AE22BBB2898B44B52EA0F4CDADCF57CFBA25"),
				HexString.toByteArray("763B6BBF8A7DFC5DAB3205791BA64D211BBC4E8A5C531C77488792C508BD3D1E")),
				45, true);
		
		// individual RI key
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("041BB377A683CE97DF9FDD5D121FFB235DDFF4F489BF645D75AF87A5B7D4B74EA22DFE7200EA90D3820CA9EBBC7ACE272B0919AA2703C591D78960854F7E498D20"),
				HexString.toByteArray("9E14FD0D1F5828CF828BA71EC13440DD44E0D95A7F903F9F50C05E0402503871")),
				1);

		// individual RI key (Pseudonym)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("049862543716B0F5C580D62653C19DF2F0E117E085E1C210E7ED8050F678EB79A91D2EA91B022A0BA3852CE03AB5A1FE39B98D2F3111CD20E8E7B5447A50DB6E64"),
				HexString.toByteArray("A96900652CD324770078AEBF8C52EF462E5DEA406B9B977138DF891B44DCE7D8")),
				2);
	}
	
}
