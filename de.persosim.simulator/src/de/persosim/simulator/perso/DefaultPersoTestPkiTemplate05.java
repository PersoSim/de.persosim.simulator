package de.persosim.simulator.perso;

import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate05 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg3PlainData("20161031");
		persoDataContainer.setDg4PlainData("AĞÇA");
		persoDataContainer.setDg5PlainData("ÖZM̂EN");
		persoDataContainer.setDg8PlainData("1989    ");
		persoDataContainer.setDg9PlainData("DESSAU-ROßLAU");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg17StreetPlainData("GROßENHAINER STR. 133/135");
		persoDataContainer.setDg17CityPlainData("DRESDEN");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("01129");
		persoDataContainer.setDg18PlainData("02761406120000");
		persoDataContainer.setDocumentNumber("000000005");
		persoDataContainer.setMrzLine3Of3("OEZMEN<<AGCA<<<<<<<<<<<<<<<<<<");
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8"),
				HexString.toByteArray("A07EB62E891DAA84643E0AFCC1AF006891B669B8F51E379477DBEAB8C987A610")),
				41);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0458597F17D0CBA7D87367C74A2CEC20BDE2C3A48691CA46E9251F32714FF6C797837E409E64E32DB4F300103E2213BB7DFC272BF145BF3288179F853DF58B5EFF"),
				HexString.toByteArray("999BEFF963FB29349046083727F196AB6BF86ACBDE08401851C150DE7C61948C")),
				45);
	}
	
	@Override
	protected void addEidDg13(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
}
