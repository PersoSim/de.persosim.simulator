package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate03 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("JOHANNA EDELTRAUT LISBETH");
		persoDataContainer.setDg5PlainData("MUSTERMANN");
		persoDataContainer.setDg6PlainData("ORDENSSCHWESTER JOHANNA");
		persoDataContainer.setDg7PlainData("DR.");
		persoDataContainer.setDg8PlainData("19280421");
		persoDataContainer.setDg9PlainData("MÜNCHEN");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg13PlainData("VON MÜLLER-SCHWARZENBERG");
		persoDataContainer.setDg17StreetPlainData("BOUCHÉSTR. 68 A");
		persoDataContainer.setDg17CityPlainData("BERLIN");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("12059");
		persoDataContainer.setDg18PlainData("02761100000000");
		persoDataContainer.setDocumentNumber("000000003");
		persoDataContainer.setMrzLine3Of3("MUSTERMANN<<JOHANNA EDELTRAUT<");
	}

}
