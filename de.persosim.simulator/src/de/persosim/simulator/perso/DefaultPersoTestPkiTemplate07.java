package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate07 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("ANNEKATHRIN");
		persoDataContainer.setDg5PlainData("LERCH");
		persoDataContainer.setDg8PlainData("19760705");
		persoDataContainer.setDg9PlainData("BAD KÖNIGSHOFEN I. GRABFELD");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg13PlainData("BJØRNSON");
		persoDataContainer.setDg17CityPlainData("HALLE (SAALE)");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("06108");
		persoDataContainer.setDg18PlainData("02760200000000");
		persoDataContainer.setDocumentNumber("000000007");
		persoDataContainer.setMrzLine3Of3("LERCH<<ANNEKATHRIN<<<<<<<<<<<<");
	}
	
}
