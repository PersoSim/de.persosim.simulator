package de.persosim.simulator.perso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.ByteDataAuxObject;
import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.ChangeablePasswordAuthObject;
import de.persosim.simulator.cardobjects.DateAuxObject;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.MrzAuthObject;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.PinObject;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class DefaultPersoTestPkiTemplate02 extends DefaultPersoTestPki {
	
	@Override
	protected void addAuxData() {
		// Aux data
		byte[] communityId = HexString.toByteArray("02761200660196"); // updated
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(1981, Calendar.JUNE, 17, 0, 0, 0); // updated
		calendar.set(Calendar.MILLISECOND, 0);
		Date dateOfBirth = calendar.getTime();
		
		calendar.set(2020, Calendar.OCTOBER, 31, 0, 0, 0); // updated
		calendar.set(Calendar.MILLISECOND, 0);
		Date validityDate = calendar.getTime();

		mf.addChild(new ByteDataAuxObject(new OidIdentifier(
				TaOid.id_CommunityID), communityId));
		mf.addChild(new DateAuxObject(new OidIdentifier(TaOid.id_DateOfBirth),
				dateOfBirth));
		mf.addChild(new DateAuxObject(new OidIdentifier(TaOid.id_DateOfExpiry),
				validityDate));
	}
	
	@Override
	protected void addEidDg3(DedicatedFile eIdAppl) {
		CardFile eidDg3 = new ElementaryFile(new FileIdentifier(0x0103),
				new ShortFileIdentifier(0x03),
				HexString.toByteArray("6306120420201031"),
				getAccessRightReadEidDg(3),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg3);
	}
	
	@Override
	protected void addEidDg4(DedicatedFile eIdAppl) {
		CardFile eidDg4 = new ElementaryFile(new FileIdentifier(0x0104),
				new ShortFileIdentifier(0x04),
				HexString.toByteArray("64081206414E4452C389"),
				getAccessRightReadEidDg(4),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg4);
	}
	
	@Override
	protected void addEidDg5(DedicatedFile eIdAppl) {
		CardFile eidDg5 = new ElementaryFile(
				new FileIdentifier(0x0105),
				new ShortFileIdentifier(0x05),
				HexString
						.toByteArray("650C0C0A4D55535445524D414E4E"),
				getAccessRightReadEidDg(5), Collections
						.<SecCondition> emptySet(), Collections
						.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg5);
	}
	
	@Override
	protected void addEidDg6(DedicatedFile eIdAppl) {
		CardFile eidDg6 = new ElementaryFile(
				new FileIdentifier(0x0106),
				new ShortFileIdentifier(0x06),
				HexString
						.toByteArray("66020C00"),
				getAccessRightReadEidDg(6), Collections
						.<SecCondition> emptySet(), Collections
						.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg6);
	}
	
	@Override
	protected void addEidDg7(DedicatedFile eIdAppl) {
		CardFile eidDg7 = new ElementaryFile(new FileIdentifier(0x0107),
				new ShortFileIdentifier(0x07),
				HexString.toByteArray("67020C00"),
				getAccessRightReadEidDg(7),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg7);
	}
	
	@Override
	protected void addEidDg8(DedicatedFile eIdAppl) {
		CardFile eidDg8 = new ElementaryFile(new FileIdentifier(0x0108),
				new ShortFileIdentifier(0x08),
				HexString.toByteArray("6806120419810617"),
				getAccessRightReadEidDg(8),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg8);
	}
	
	@Override
	protected void addEidDg9(DedicatedFile eIdAppl) {
		CardFile eidDg9 = new ElementaryFile(
				new FileIdentifier(0x0109),
				new ShortFileIdentifier(0x09),
				HexString
						.toByteArray("6914A1120C104652414E4B4655525420284F44455229"),
				getAccessRightReadEidDg(9), Collections
						.<SecCondition> emptySet(), Collections
						.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg9);
	}
	
	@Override
	protected void addEidDg11(DedicatedFile eIdAppl) {
		CardFile eidDg11 = new ElementaryFile(new FileIdentifier(0x010B),
				new ShortFileIdentifier(0x0B),
				HexString.toByteArray("6B0313014D"),
				getAccessRightReadEidDg(11),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg11);
	}
	
	@Override
	protected void addEidDg13(DedicatedFile eIdAppl) {
		CardFile eidDg13 = new ElementaryFile(new FileIdentifier(0x010D),
				new ShortFileIdentifier(0x0D),
				HexString.toByteArray("6D020C00"),
				getAccessRightReadEidDg(13),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg13);
	}
	
	@Override
	protected void addEidDg17(DedicatedFile eIdAppl) {
		CardFile eidDg17 = new ElementaryFile(
				new FileIdentifier(0x0111),
				new ShortFileIdentifier(0x11),
				HexString
						.toByteArray("713E303CAA150C1345484D2D57454C4B2D53545241C39F45203333AB150C134CC39C4242454E41552F535052454557414C44AD03130144AE0713053033323232"),
				getAccessRightReadEidDg(17), getAccessRightUpdateEidDg(17),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg17);
	}
	
	@Override
	protected void addEidDg18(DedicatedFile eIdAppl) {
		CardFile eidDg18 = new ElementaryFile(new FileIdentifier(0x0112),
				new ShortFileIdentifier(0x12),
				HexString.toByteArray("7209040702761200660196"),
				getAccessRightReadEidDg(18), getAccessRightUpdateEidDg(18),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg18);
	}

	@Override
	protected void addAuthObjects() throws NoSuchAlgorithmException,
			NoSuchProviderException, IOException, UnsupportedEncodingException {
		MrzAuthObject mrz = new MrzAuthObject(
				new AuthObjectIdentifier(1),
				"IDD<<0123456784<<<<<<<<<<<<<<<8106171M2010315D<<<<<<<<<<<<<2MUSTERMANN<<ANDRE<<<<<<<<<<<<<");
		mf.addChild(mrz);

		ChangeablePasswordAuthObject can = new ChangeablePasswordAuthObject(
				new AuthObjectIdentifier(2), "500540".getBytes("UTF-8"), "CAN",
				6, 6);
		can.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		mf.addChild(can);

		PasswordAuthObjectWithRetryCounter pin = new PinObject(
				new AuthObjectIdentifier(3), "123456".getBytes("UTF-8"), 6, 6,
				3);
		pin.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		mf.addChild(pin);

		PasswordAuthObject puk = new PasswordAuthObject(
				new AuthObjectIdentifier(4), "9876543210".getBytes("UTF-8"),
				"PUK");
		mf.addChild(puk);
	}

}
