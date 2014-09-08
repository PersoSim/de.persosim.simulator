package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate04 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg3PlainData("20161031");
		persoDataContainer.setDg4PlainData("---");
		persoDataContainer.setDg5PlainData("ĆOSIĆ");
		persoDataContainer.setDg8PlainData("199409  ");
		persoDataContainer.setDg9PlainData("PRIŠTINA");
		persoDataContainer.setDg11PlainData("M");
		persoDataContainer.setDg17StreetPlainData("F4 14-15");
		persoDataContainer.setDg17CityPlainData("MANNHEIM");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("68159");
		persoDataContainer.setDg18PlainData("02760802220000");
		persoDataContainer.setDocumentNumber("000000004");
		persoDataContainer.setMrzLine3Of3("COSIC<<<<<<<<<<<<<<<<<<<<<<<<<");
	}

}
