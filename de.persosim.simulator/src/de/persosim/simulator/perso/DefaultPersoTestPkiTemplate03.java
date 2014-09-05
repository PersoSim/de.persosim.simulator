package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate03 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		this.persoDataContainer.setDg4PlainData("JOHANNA EDELTRAUT LISBETH");
		this.persoDataContainer.setDg5PlainData("MUSTERMANN");
		this.persoDataContainer.setDg6PlainData("ORDENSSCHWESTER JOHANNA");
		this.persoDataContainer.setDg7PlainData("DR.");
		this.persoDataContainer.setDg8PlainData("19280421");
		this.persoDataContainer.setDg9PlainData("MÜNCHEN");
		this.persoDataContainer.setDg11PlainData("F");
		this.persoDataContainer.setDg13PlainData("VON MÜLLER-SCHWARZENBERG");
		this.persoDataContainer.setDg17StreetPlainData("BOUCHÉSTR. 68 A");
		this.persoDataContainer.setDg17CityPlainData("BERLIN");
		this.persoDataContainer.setDg17CountryPlainData("D");
		this.persoDataContainer.setDg17ZipPlainData("12059");
		this.persoDataContainer.setDg18PlainData("02761100000000");
		this.persoDataContainer.setDocumentNumber("000000003");
		this.persoDataContainer.setMrzLine3Of3("MUSTERMANN<<JOHANNA EDELTRAUT<");
	}

}
