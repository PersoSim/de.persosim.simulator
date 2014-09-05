package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate01 extends DefaultPersoTestPkiTemplate {
	
	public DefaultPersoTestPkiTemplate01() {
		super();
		this.persoDataContainer.setDg4PlainData("ERIKA");
		this.persoDataContainer.setDg5PlainData("MUSTERMANN");
		this.persoDataContainer.setDg8PlainData("19640812");
		this.persoDataContainer.setDg9PlainData("BERLIN");
		this.persoDataContainer.setDg11PlainData("F");
		this.persoDataContainer.setDg13PlainData("GABLER");
		this.persoDataContainer.setDg17StreetPlainData("HEIDESTRASSE 17");
		this.persoDataContainer.setDg17CityPlainData("KÖLN");
		this.persoDataContainer.setDg17CountryPlainData("D");
		this.persoDataContainer.setDg17ZipPlainData("51147");
		this.persoDataContainer.setDg18PlainData("02760503150000");
		this.persoDataContainer.setDocumentNumber("000000001");
		this.persoDataContainer.setMrzLine3Of3("MUSTERMANN<<ERIKA<<<<<<<<<<<<<");
	}
	
}
