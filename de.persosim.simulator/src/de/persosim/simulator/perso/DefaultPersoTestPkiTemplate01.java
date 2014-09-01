package de.persosim.simulator.perso;

import java.util.Collections;

import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate01 extends DefaultPersoTestPkiTemplate {
	
	public static final String DG17_PLAIN_DATA_STREET  = "HEIDESTRASSE 17";
	public static final String DG17_PLAIN_DATA_CITY    = "KÖLN";
	public static final String DG17_PLAIN_DATA_COUNTRY = "D";
	public static final String DG17_PLAIN_DATA_ZIP     = "51147";
	public static final String DG18_PLAIN_DATA         = "02760503150000";
	
	
	
	@Override
	protected void addEidDg17(DedicatedFile eIdAppl) {
		CardFile eidDg17 = new ElementaryFile(
				new FileIdentifier(0x0111),
				new ShortFileIdentifier(0x11),
				HexString.toByteArray("712C302AAA110C0F484549444553545241535345203137AB070C054BC3964C4EAD03130144AE0713053531313437"),
				getAccessRightReadEidDg(17), getAccessRightUpdateEidDg(17),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg17);
	}

	@Override
	public String getEidDg4PlainData() {
		return "ERIKA";
	}

	@Override
	public String getEidDg5PlainData() {
		return "MUSTERMANN";
	}

	@Override
	public String getEidDg8PlainData() {
		return "19640812";
	}
	
	@Override
	public String getEidDg9PlainData() {
		return "BERLIN";
	}

	@Override
	public String getEidDg11PlainData() {
		return "F";
	}
	
	@Override
	public String getEidDg13PlainData() {
		return "GABLER";
	}

	@Override
	public String getEidDg18PlainData() {
		return "02760503150000";
	}

	@Override
	public String getDocumentNumber() {
		return "00000001";
	}

	@Override
	public String getMrzLine3of3() {
		return "MUSTERMANN<<ERIKA<<<<<<<<<<<<<";
	}
	
}
