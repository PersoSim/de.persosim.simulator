package de.persosim.simulator.perso;

import java.util.Arrays;
import java.util.Collections;

import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.secstatus.PaceSecurityCondition;
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
	protected void addEidDg9(DedicatedFile eIdAppl) {
		CardFile eidDg9 = new ElementaryFile(
				new FileIdentifier(0x0109),
				new ShortFileIdentifier(0x09),
				HexString
						.toByteArray("690AA1080C064245524C494E"),
				getAccessRightReadEidDg(9), Collections
						.<SecCondition> emptySet(), Collections
						.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg9);
	}
	
	@Override
	protected void addEidDg17(DedicatedFile eIdAppl) {
		CardFile eidDg17 = new ElementaryFile(
				new FileIdentifier(0x0111),
				new ShortFileIdentifier(0x11),
				HexString
						.toByteArray("712C302AAA110C0F484549444553545241535345203137AB070C054BC3964C4EAD03130144AE0713053531313437"),
				getAccessRightReadEidDg(17), getAccessRightUpdateEidDg(17),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg17);
	}
	
	@Override
	protected void addEpassDatagroup1(DedicatedFile ePassAppl) {
		// ePass DG1
		CardFile epassDg1 = new ElementaryFile(
				new FileIdentifier(0x0101),
				new ShortFileIdentifier(0x01),
				HexString.toByteArray("615D5F1F5A4944443C3C303132333435363738343C3C3C3C3C3C3C3C3C3C3C3C3C3C3C363430383132354632303130333135443C3C3C3C3C3C3C3C3C3C3C3C3C324D55535445524D414E4E3C3C4552494B413C3C3C3C3C3C3C3C3C3C3C3C3C"),
				Arrays.asList((SecCondition) new PaceSecurityCondition()),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		ePassAppl.addChild(epassDg1);
	}

	@Override
	public String getEidDg1PlainData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEidDg2PlainData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEidDg3PlainData() {
		return "20201031";
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
	public String getEidDg6PlainData() {
		return "";
	}

	@Override
	public String getEidDg7PlainData() {
		return "";
	}

	@Override
	public String getEidDg8PlainData() {
		return "19640812";
	}

	@Override
	public String getEidDg10PlainData() {
		// TODO Auto-generated method stub
		return null;
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
