package de.persosim.simulator.perso;

import de.persosim.simulator.cardobjects.DedicatedFile;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate05 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg3PlainData("20161031");
		persoDataContainer.setDg4PlainData("AĞÇA");
		persoDataContainer.setDg5PlainData("ÖZM̂EN");
		persoDataContainer.setDg8PlainData("1989    ");
		persoDataContainer.setDg9PlainData("DESSAU-ROßLAU");
		persoDataContainer.setDg11PlainData("F");
		persoDataContainer.setDg17StreetPlainData("GROßENHAINER STR. 133/135");
		persoDataContainer.setDg17CityPlainData("DRESDEN");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("01129");
		persoDataContainer.setDg18PlainData("02761406120000");
		persoDataContainer.setDocumentNumber("000000005");
		persoDataContainer.setMrzLine3Of3("OEZMEN<<AGCA<<<<<<<<<<<<<<<<<<");
	}
	
	@Override
	protected void addEidDg13(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
}
