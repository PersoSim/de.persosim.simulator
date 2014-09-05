package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate11 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		this.persoDataContainer.setDg4PlainData("HILDEGARD");
		this.persoDataContainer.setDg5PlainData("MÜLLER");
		this.persoDataContainer.setDg8PlainData("19390204");
		this.persoDataContainer.setDg9PlainData("SAARBRÜCKEN");
		this.persoDataContainer.setDg11PlainData("F");
		this.persoDataContainer.setDg17StreetPlainData("HARKORTSTR. 58");
		this.persoDataContainer.setDg17CityPlainData("DORTMUND");
		this.persoDataContainer.setDg17CountryPlainData("D");
		this.persoDataContainer.setDg17ZipPlainData("44225");
		this.persoDataContainer.setDg18PlainData("02760509130000");
		this.persoDataContainer.setDocumentNumber("000000011");
		this.persoDataContainer.setMrzLine3Of3("MUELLER<<HILDEGARD<<<<<<<<<<<<");
	}

}
