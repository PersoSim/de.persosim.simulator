package de.persosim.simulator.perso;

import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

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
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2"),
				HexString.toByteArray("8910074CF4749A916E5864654C768D57F57B6361F70A226DD1AEBED390BB066D")),
				41);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0482ED7BDBBC67FF81507630E88819F3B001E47592D8B686D4C057FB8B75110D2E753F56C2F188337D1BCCA74CD12D7186E1AAD6D8A560DC90D56590BC373E5587"),
				HexString.toByteArray("40055E46C67A76B7BF1A3026400D8C2D9BB243B883E0D150B517120A7651480C")),
				45);
	}
	
	@Override
	protected void addEidDg13(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
}
