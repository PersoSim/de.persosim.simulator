package de.persosim.simulator.perso;

import de.persosim.simulator.cardobjects.DedicatedFile;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate08 extends DefaultPersoTestPkiTemplate {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = new PersonalizationDataDefaultContainer();
		persoDataContainer.setDg4PlainData("KARL");
		persoDataContainer.setDg5PlainData("HILLEBRANDT");
		persoDataContainer.setDg6PlainData("GRAF V. LÝSKY");
		persoDataContainer.setDg7PlainData("DR.HC.");
		persoDataContainer.setDg8PlainData("19520617");
		persoDataContainer.setDg9PlainData("TRIER");
		persoDataContainer.setDg11PlainData("M");
		persoDataContainer.setDg18PlainData("");
		persoDataContainer.setDocumentNumber("000000008");
		persoDataContainer.setMrzLine3Of3("GRAF<VON<LYSKY<<KARL<<<<<<<<<<");
	}
	
	@Override
	protected void addEidDg13(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
}
