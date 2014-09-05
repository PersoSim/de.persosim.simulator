package de.persosim.simulator.perso;

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
	}
	
}
