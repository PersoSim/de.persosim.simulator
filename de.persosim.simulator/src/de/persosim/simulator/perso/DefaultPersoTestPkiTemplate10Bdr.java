package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate10Bdr extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("GHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGHGH");
		persoDataContainer.setDg5PlainData("CDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCD");
		persoDataContainer.setDg6PlainData("STSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTSTST");
		persoDataContainer.setDg7PlainData("ABABABABABABABABABABABABABABABABABABABAB");
		persoDataContainer.setDg8PlainData("18990502");
		persoDataContainer.setDg9PlainData("IJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJIJ");
		persoDataContainer.setDg11PlainData("M");
		persoDataContainer.setDg13PlainData("EFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFE");
		persoDataContainer.setDg17StreetPlainData("OPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOPOP");
		persoDataContainer.setDg17CityPlainData("MNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMNMN");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("12345");
		persoDataContainer.setDg18PlainData("02761100000000");
		persoDataContainer.setDocumentNumber("000000010");
		persoDataContainer.setMrzLine3Of3("CDCDCDCDCDCDCD<<GHGHGHGHGHGHGH");
	}

}
