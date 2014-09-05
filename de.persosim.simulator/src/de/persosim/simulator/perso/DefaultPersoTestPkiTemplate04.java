package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate04 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		this.persoDataContainer.setDg3PlainData("20161031");
		this.persoDataContainer.setDg4PlainData("---");
		this.persoDataContainer.setDg5PlainData("ĆOSIĆ");
		this.persoDataContainer.setDg8PlainData("199409  ");
		this.persoDataContainer.setDg9PlainData("PRIŠTINA");
		this.persoDataContainer.setDg11PlainData("M");
		this.persoDataContainer.setDg17StreetPlainData("F4 14-15");
		this.persoDataContainer.setDg17CityPlainData("MANNHEIM");
		this.persoDataContainer.setDg17CountryPlainData("D");
		this.persoDataContainer.setDg17ZipPlainData("68159");
		this.persoDataContainer.setDg18PlainData("02760802220000");
		this.persoDataContainer.setDocumentNumber("000000004");
		this.persoDataContainer.setMrzLine3Of3("COSIC<<<<<<<<<<<<<<<<<<<<<<<<<");
	}

}
