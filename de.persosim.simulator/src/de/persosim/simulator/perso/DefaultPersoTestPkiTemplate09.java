package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate09 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg3PlainData("20161031");
		persoDataContainer.setDg4PlainData("LILLY");
		persoDataContainer.setDg5PlainData("SCHUSTER");
		persoDataContainer.setDg8PlainData("19980330");
		persoDataContainer.setDg9PlainData("MÜHLHAUSEN/THÜRINGEN");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg13PlainData("VON MÜLLER-SCHWARZENBERG");
		persoDataContainer.setDg17StreetPlainData("MARIENSTRAßE 144");
		persoDataContainer.setDg17CityPlainData("EISENACH");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("99817");
		persoDataContainer.setDg18PlainData("02761600560000");
		persoDataContainer.setDocumentNumber("000000009");
		persoDataContainer.setMrzLine3Of3("SCHUSTER<<LILLY<<<<<<<<<<<<<<<");
	}

}
