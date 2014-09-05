package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate06 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		this.persoDataContainer.setDg4PlainData("Hans-Günther");
		this.persoDataContainer.setDg5PlainData("von Drebenbusch-Dalgoßen");
		this.persoDataContainer.setDg6PlainData("Freiherr zu Möckern-Windensberg");
		this.persoDataContainer.setDg7PlainData("Dr.eh.Dr.");
		this.persoDataContainer.setDg8PlainData("19460125");
		this.persoDataContainer.setDg9PlainData("BREMERHAVEN");
		this.persoDataContainer.setDg11PlainData("M");
		this.persoDataContainer.setDg13PlainData("Weiß");
		this.persoDataContainer.setDg17StreetPlainData("WEG NR. 12 8E");
		this.persoDataContainer.setDg17CityPlainData("HAMBURG");
		this.persoDataContainer.setDg17CountryPlainData("D");
		this.persoDataContainer.setDg17ZipPlainData("22043");
		this.persoDataContainer.setDg18PlainData("02760200000000");
		this.persoDataContainer.setDocumentNumber("000000006");
		this.persoDataContainer.setMrzLine3Of3("VONDREBENBUSCHDALGOSSEN<<HANS<");
	}

}
