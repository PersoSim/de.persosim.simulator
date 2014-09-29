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
		persoDataContainer.setDg11PlainData("M"); //XXX this is needed in order to make MRZ generation work but should not be included in data groups
		persoDataContainer.setDg18PlainData("");
		persoDataContainer.setDocumentNumber("000000008");
		persoDataContainer.setMrzLine3Of3("GRAF<VON<LYSKY<<KARL<<<<<<<<<<");
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2"),
				HexString.toByteArray("8910074CF4749A916E5864654C768D57F57B6361F70A226DD1AEBED390BB066D")),
				41, false);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0482ED7BDBBC67FF81507630E88819F3B001E47592D8B686D4C057FB8B75110D2E753F56C2F188337D1BCCA74CD12D7186E1AAD6D8A560DC90D56590BC373E5587"),
				HexString.toByteArray("40055E46C67A76B7BF1A3026400D8C2D9BB243B883E0D150B517120A7651480C")),
				45, true);

		// individual RI key
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("04904A688211708045D141244BC7EDB4C2C07622C8514D24524694C3791B7843C74F2DC4598C8240EB350C7488332430706D9B4613E163DC9790D111BD77B6A1E4"),
				HexString.toByteArray("49C7E53603DC2B1155A90DA950A6F5E4B050F11E9E139FFDBC9F8FCDA8D3F25B")),
				1);

		// individual RI key (Pseudonym)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("04283BC17F2D95B9BE6E491456ADDA466101EA3D7ACA3BB325BAF96B580746723B041D0A7AFFA6DE683CBB6F9E68CABFF2B28DA8D65B5DF9679889155B4E92BABB"),
				HexString.toByteArray("77635293F8ED87B970C46C4B1109B6066B4517035524DA5069173B2526348F0F")),
				2);
	}
	
	@Override
	protected void addUnmarshallerCallbacks() {
		unmarshallerCallbacks.add(new DefectListNpaUnmarshallerCallback());

	}
	
	@Override
	protected void addEidDg13(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
}
