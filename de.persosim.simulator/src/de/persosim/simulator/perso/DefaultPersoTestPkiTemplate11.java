package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate11 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("HILDEGARD");
		persoDataContainer.setDg5PlainData("MÜLLER");
		persoDataContainer.setDg8PlainData("19390204");
		persoDataContainer.setDg9PlainData("SAARBRÜCKEN");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg17StreetPlainData("HARKORTSTR. 58");
		persoDataContainer.setDg17CityPlainData("DORTMUND");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("44225");
		persoDataContainer.setDg18PlainData("02760509130000");
		persoDataContainer.setDocumentNumber("000000011");
		persoDataContainer.setMrzLine3Of3("MUELLER<<HILDEGARD<<<<<<<<<<<<");
	}

}
