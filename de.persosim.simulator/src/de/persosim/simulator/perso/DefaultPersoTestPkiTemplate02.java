package de.persosim.simulator.perso;

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
	}

}
