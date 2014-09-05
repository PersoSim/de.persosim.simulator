package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate07 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		this.persoDataContainer.setDg4PlainData("ANNEKATHRIN");
		this.persoDataContainer.setDg5PlainData("LERCH");
		this.persoDataContainer.setDg8PlainData("19760705");
		this.persoDataContainer.setDg9PlainData("BAD KÖNIGSHOFEN I. GRABFELD");
		this.persoDataContainer.setDg11PlainData("F");
		this.persoDataContainer.setDg13PlainData("BJØRNSON");
		this.persoDataContainer.setDg17CityPlainData("HALLE (SAALE)");
		this.persoDataContainer.setDg17CountryPlainData("D");
		this.persoDataContainer.setDg17ZipPlainData("06108");
		this.persoDataContainer.setDg18PlainData("02760200000000");
		this.persoDataContainer.setDocumentNumber("000000007");
		this.persoDataContainer.setMrzLine3Of3("LERCH<<ANNEKATHRIN<<<<<<<<<<<<");
	}
	
}
