package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate02 extends DefaultPersoTestPkiTemplate {
	
	public DefaultPersoTestPkiTemplate02() {
		super();
		this.persoDataContainer.setDg4PlainData("ANDRÉ");
		this.persoDataContainer.setDg5PlainData("MUSTERMANN");
		this.persoDataContainer.setDg8PlainData("19810617");
		this.persoDataContainer.setDg9PlainData("FRANKFURT (ODER)");
		this.persoDataContainer.setDg11PlainData("M");
		this.persoDataContainer.setDg17StreetPlainData("EHM-WELK-STRAßE 33");
		this.persoDataContainer.setDg17CityPlainData("LÜBBENAU/SPREEWALD");
		this.persoDataContainer.setDg17CountryPlainData("D");
		this.persoDataContainer.setDg17ZipPlainData("03222");
		this.persoDataContainer.setDg18PlainData("02761200660196");
		this.persoDataContainer.setDocumentNumber("000000002");
		this.persoDataContainer.setMrzLine3Of3("MUSTERMANN<<ANDRE<<<<<<<<<<<<<");
	}

}
