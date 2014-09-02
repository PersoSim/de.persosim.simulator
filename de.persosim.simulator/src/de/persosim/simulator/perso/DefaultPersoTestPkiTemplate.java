package de.persosim.simulator.perso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
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
import de.persosim.simulator.documents.Mrz;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.secstatus.PaceSecurityCondition;
import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public abstract class DefaultPersoTestPkiTemplate extends DefaultPersoTestPki {
	
	public String getEidDg1PlainData() {
		return "ID";
	}
	
	public String getEidDg2PlainData() {
		return "D";
	}
	
	public String getEidDg3PlainData() {
		return "20201031";
	}
	
	public abstract String getEidDg4PlainData();
	public abstract String getEidDg5PlainData();
	
	public String getEidDg6PlainData() {
		return "";
	}
	
	public String getEidDg7PlainData() {
		return "";
	}
	
	public abstract String getEidDg8PlainData();
	public abstract String getEidDg9PlainData();
	
	public String getEidDg10PlainData() {
		return "D";
	}
	
	public abstract String getEidDg11PlainData();
	
	public String getEidDg13PlainData() {
		return "";
	}
	
	public abstract byte[] getEidDg17Data();
	
	public abstract String getEidDg18PlainData();
	
	public String getEidDg19PlainData() {
		return "ResPermit1";
	}
	
	public String getEidDg20PlainData() {
		return "ResPermit2";
	}
	
	public abstract String getDocumentNumber();
	public abstract String getMrzLine3of3();
	
	public String getPin() {
		return "123456";
	}
	
	public String getCan() {
		return "500540";
	}
	
	public String getPuk() {
		return "9876543210";
	}
	
	private static String getMrzLine1of3(String documentType, String issuingCountry, String documentNumber) {
		String line1;
		
		if(documentType == null) {throw new NullPointerException("document type must not be null");}
		if((documentType.length() <= 0) || (documentType.length() > 2)) {throw new IllegalArgumentException("document type must be 1 or 2 characters long");}
		
		line1 = documentType;
		
		if(documentType.length() == 1) {
			line1 += Mrz.Filler;
		}
		
		if(issuingCountry == null) {throw new NullPointerException("issuing country must not be null");}
		if((issuingCountry.length() <= 0) || (documentType.length() > 3)) {throw new IllegalArgumentException("issuing country must be between 1 or 3 characters long");}
		
		line1 += issuingCountry;
		
		for(int i = issuingCountry.length(); i < 3; i++) {
			line1 += Mrz.Filler;
		}
		
		if(documentNumber == null) {throw new NullPointerException("document number must not be null");}
		if(issuingCountry.length() != 9) {throw new IllegalArgumentException("document number must be exactly 9 characters long");}
		
		line1 += documentNumber;
		line1 += String.valueOf((char) Mrz.computeChecksum(documentNumber.getBytes(), 0, documentNumber.length()));
		
		for(int i = 0; i < 15; i++) {
			line1 += Mrz.Filler;
		}
		
		return line1;
	}
	
	private static String getMrzLine2of3(String mrzLine1, String dob, String sex, String doe, String nation) {
		String line2;
		
		String dobNew = dob.substring(2).replace(" ", Mrz.Filler);
		dobNew += String.valueOf((char) Mrz.computeChecksum(dobNew.getBytes(), 0, dobNew.length()));
		
		String doeNew = doe.substring(2);
		doeNew += String.valueOf((char) Mrz.computeChecksum(doeNew.getBytes(), 0, doeNew.length()));
		
		line2 = dobNew + sex + doeNew + nation;
		
		for(int i = 0; i < 13; i++) {
			line2 += Mrz.Filler;
		}
		
		String lines12 = mrzLine1.substring(5) + dobNew + doeNew;
		line2 += String.valueOf((char) Mrz.computeChecksum(lines12.getBytes(), 0, lines12.length()));
		
		return line2;
	}
	
	public String getMrz() {
		String line1 = getMrzLine1of3(getEidDg1PlainData(), getEidDg2PlainData(), getDocumentNumber());
		String line2 = getMrzLine2of3(line1, getEidDg8PlainData(), getEidDg11PlainData(), getEidDg3PlainData(), getEidDg10PlainData());
		String line3 = getMrzLine3of3();
		
		return line1 + line2 + line3;
	}
	
	@Override
	protected void addEpassDatagroup1(DedicatedFile ePassAppl) {
		String mrz = getMrz();
		byte[] mrzPlainBytes;
		
		try {
			mrzPlainBytes = mrz.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// US-ASCII is a valid encoding so this is never going to happen
			e.printStackTrace();
			mrzPlainBytes = new byte[0];
		}
		
		ConstructedTlvDataObject ePassDg1 = new ConstructedTlvDataObject(new TlvTag((byte) 0x61));
		PrimitiveTlvDataObject ePassDg1Sub = new PrimitiveTlvDataObject(new TlvTag(new byte[]{(byte) 0x5F, (byte) 0x1F}), mrzPlainBytes);
		ePassDg1.addTlvDataObject(ePassDg1Sub);
		
		// ePass DG1
		CardFile epassDg1 = new ElementaryFile(
				new FileIdentifier(0x0101),
				new ShortFileIdentifier(0x01),
				ePassDg1.toByteArray(),
				Arrays.asList((SecCondition) new PaceSecurityCondition()),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		ePassAppl.addChild(epassDg1);
	}
	
	@Override
	protected void addAuthObjects() throws NoSuchAlgorithmException,
			NoSuchProviderException, IOException, UnsupportedEncodingException {
		MrzAuthObject mrz = new MrzAuthObject(
				new AuthObjectIdentifier(1),
				getMrz());
		mf.addChild(mrz);

		ChangeablePasswordAuthObject can = new ChangeablePasswordAuthObject(
				new AuthObjectIdentifier(2), getCan().getBytes("UTF-8"), "CAN",
				6, 6);
		can.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		mf.addChild(can);

		PasswordAuthObjectWithRetryCounter pin = new PinObject(
				new AuthObjectIdentifier(3), getPin().getBytes("UTF-8"), 6, 6,
				3);
		pin.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		mf.addChild(pin);

		PasswordAuthObject puk = new PasswordAuthObject(
				new AuthObjectIdentifier(4), getPuk().getBytes("UTF-8"),
				"PUK");
		mf.addChild(puk);
	}
	
	@Override
	protected void addAuxData() {
		// Aux data
		byte[] communityId;
		
		try {
			communityId = getEidDg18PlainData().getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// US-ASCII is a valid encoding so this is never going to happen
			e.printStackTrace();
			communityId = new byte[0];
		}
		
		Date dateOfBirth = Utils.getDate(getEidDg8PlainData(), Utils.DATE_SET_MAX_VALUE);
		
		Date validityDate = Utils.getDate(getEidDg3PlainData());

		mf.addChild(new ByteDataAuxObject(new OidIdentifier(
				TaOid.id_CommunityID), communityId));
		mf.addChild(new DateAuxObject(new OidIdentifier(TaOid.id_DateOfBirth),
				dateOfBirth));
		mf.addChild(new DateAuxObject(new OidIdentifier(TaOid.id_DateOfExpiry),
				validityDate));
	}
	
	@Override
	protected void addEidDg1(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg1Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x61));
		PrimitiveTlvDataObject documentType = new PrimitiveTlvDataObject(new TlvTag((byte) 0x13), HexString.toByteArray(getEidDg1PlainData()));
		dg1Tlv.addTlvDataObject(documentType);
		
		CardFile eidDg1 = new ElementaryFile(new FileIdentifier(0x0101),
				new ShortFileIdentifier(0x01),
				dg1Tlv.toByteArray(),
				getAccessRightReadEidDg(1),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg1);
	}
	
	@Override
	protected void addEidDg2(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg2Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x62));
		PrimitiveTlvDataObject documentType = new PrimitiveTlvDataObject(new TlvTag((byte) 0x13), HexString.toByteArray(getEidDg2PlainData()));
		dg2Tlv.addTlvDataObject(documentType);
		
		CardFile eidDg1 = new ElementaryFile(new FileIdentifier(0x0102),
				new ShortFileIdentifier(0x02),
				dg2Tlv.toByteArray(),
				getAccessRightReadEidDg(2),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg1);
	}
	
	@Override
	protected void addEidDg3(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg3Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x63));
		PrimitiveTlvDataObject dateOfExpiry = new PrimitiveTlvDataObject(new TlvTag((byte) 0x12), HexString.toByteArray(getEidDg3PlainData()));
		dg3Tlv.addTlvDataObject(dateOfExpiry);
		
		CardFile eidDg3 = new ElementaryFile(new FileIdentifier(0x0103),
				new ShortFileIdentifier(0x03),
				dg3Tlv.toByteArray(),
				getAccessRightReadEidDg(3),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg3);
	}
	
	@Override
	protected void addEidDg4(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg4Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x64));
		byte[] givenNamesPlainBytes;
		
		try {
			givenNamesPlainBytes = getEidDg4PlainData().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 is a valid encoding so this is never going to happen
			e.printStackTrace();
			givenNamesPlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject givenNames = new PrimitiveTlvDataObject(new TlvTag((byte) 0x12), givenNamesPlainBytes);
		dg4Tlv.addTlvDataObject(givenNames);
		
		CardFile eidDg4 = new ElementaryFile(new FileIdentifier(0x0104),
				new ShortFileIdentifier(0x04),
				dg4Tlv.toByteArray(),
				getAccessRightReadEidDg(4),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg4);
	}
	
	@Override
	protected void addEidDg5(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg5Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x65));
		byte[] familyNamesPlainBytes;
		
		try {
			familyNamesPlainBytes = getEidDg5PlainData().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 is a valid encoding so this is never going to happen
			e.printStackTrace();
			familyNamesPlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject fn = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), familyNamesPlainBytes);
		dg5Tlv.addTlvDataObject(fn);
		
		CardFile eidDg5 = new ElementaryFile(
				new FileIdentifier(0x0105),
				new ShortFileIdentifier(0x05),
				dg5Tlv.toByteArray(),
				getAccessRightReadEidDg(5),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg5);
	}
	
	@Override
	protected void addEidDg6(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg6Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x66));
		byte[] religiousArtisticNamePlainBytes;
		
		try {
			religiousArtisticNamePlainBytes = getEidDg6PlainData().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 is a valid encoding so this is never going to happen
			e.printStackTrace();
			religiousArtisticNamePlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject religiousArtisticName = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), religiousArtisticNamePlainBytes);
		dg6Tlv.addTlvDataObject(religiousArtisticName);
		
		CardFile eidDg6 = new ElementaryFile(
				new FileIdentifier(0x0106),
				new ShortFileIdentifier(0x06),
				dg6Tlv.toByteArray(),
				getAccessRightReadEidDg(6),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg6);
	}
	
	@Override
	protected void addEidDg7(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg7Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x67));
		byte[] academicTitlePlainBytes;
		
		try {
			academicTitlePlainBytes = getEidDg7PlainData().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 is a valid encoding so this is never going to happen
			e.printStackTrace();
			academicTitlePlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject academicTitle = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), academicTitlePlainBytes);
		dg7Tlv.addTlvDataObject(academicTitle);
		
		CardFile eidDg7 = new ElementaryFile(new FileIdentifier(0x0107),
				new ShortFileIdentifier(0x07),
				dg7Tlv.toByteArray(),
				getAccessRightReadEidDg(7),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg7);
	}
	
	@Override
	protected void addEidDg8(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg8Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x68));
		PrimitiveTlvDataObject dateOfBirth = new PrimitiveTlvDataObject(new TlvTag((byte) 0x12), HexString.toByteArray(getEidDg8PlainData()));
		dg8Tlv.addTlvDataObject(dateOfBirth);
		
		CardFile eidDg8 = new ElementaryFile(new FileIdentifier(0x0108),
				new ShortFileIdentifier(0x08),
				dg8Tlv.toByteArray(),
				getAccessRightReadEidDg(8),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg8);
	}
	
	@Override
	protected void addEidDg9(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg9Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x69));
		ConstructedTlvDataObject free = new ConstructedTlvDataObject(new TlvTag((byte) 0xA1));
		
		byte[] placeOfBirthPlainBytes;
		
		try {
			placeOfBirthPlainBytes = getEidDg9PlainData().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 is a valid encoding so this is never going to happen
			e.printStackTrace();
			placeOfBirthPlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject pob = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), placeOfBirthPlainBytes);
		dg9Tlv.addTlvDataObject(free);
		free.addTlvDataObject(pob);
		
		CardFile eidDg9 = new ElementaryFile(
				new FileIdentifier(0x0109),
				new ShortFileIdentifier(0x09),
				dg9Tlv.toByteArray(),
				getAccessRightReadEidDg(9),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg9);
	}
	
	@Override
	protected void addEidDg10(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg10Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x6A));
		PrimitiveTlvDataObject nationality = new PrimitiveTlvDataObject(new TlvTag((byte) 0x13), HexString.toByteArray(getEidDg10PlainData()));
		dg10Tlv.addTlvDataObject(nationality);
		
		CardFile eidDg10 = new ElementaryFile(new FileIdentifier(0x010A),
				new ShortFileIdentifier(0x0A),
				dg10Tlv.toByteArray(),
				getAccessRightReadEidDg(10),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg10);
	}
	
	@Override
	protected void addEidDg11(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg11Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x6B));
		byte[] sexPlainBytes;
		
		try {
			sexPlainBytes = getEidDg11PlainData().getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// US-ASCII is a valid encoding so this is never going to happen
			e.printStackTrace();
			sexPlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject sex = new PrimitiveTlvDataObject(new TlvTag((byte) 0x13), sexPlainBytes);
		dg11Tlv.addTlvDataObject(sex);
		
		CardFile eidDg11 = new ElementaryFile(new FileIdentifier(0x010B),
				new ShortFileIdentifier(0x0B),
				sex.toByteArray(),
				getAccessRightReadEidDg(11),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg11);
	}
	
	@Override
	protected void addEidDg13(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg13Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x6D));
		byte[] birthNamePlainBytes;
		
		try {
			birthNamePlainBytes = getEidDg13PlainData().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 is a valid encoding so this is never going to happen
			e.printStackTrace();
			birthNamePlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject birthName = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), birthNamePlainBytes);
		dg13Tlv.addTlvDataObject(birthName);
		
		CardFile eidDg13 = new ElementaryFile(new FileIdentifier(0x010D),
				new ShortFileIdentifier(0x0D),
				dg13Tlv.toByteArray(),
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
				getEidDg17Data(),
				getAccessRightReadEidDg(17), getAccessRightUpdateEidDg(17),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg17);
	}
	
	@Override
	protected void addEidDg18(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg18Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x72));
		PrimitiveTlvDataObject communityId = new PrimitiveTlvDataObject(new TlvTag((byte) 0x04), HexString.toByteArray(getEidDg18PlainData()));
		dg18Tlv.addTlvDataObject(communityId);
		
		CardFile eidDg18 = new ElementaryFile(new FileIdentifier(0x0112),
				new ShortFileIdentifier(0x12),
				dg18Tlv.toByteArray(),
				getAccessRightReadEidDg(18), getAccessRightUpdateEidDg(18),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg18);
	}
	
	@Override
	protected void addEidDg19(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg19Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x73));
		PrimitiveTlvDataObject residencePermit1 = new PrimitiveTlvDataObject(new TlvTag((byte) 0x04), HexString.toByteArray(getEidDg19PlainData()));
		dg19Tlv.addTlvDataObject(residencePermit1);
		
		CardFile eidDg19 = new ElementaryFile(new FileIdentifier(0x0113),
				new ShortFileIdentifier(0x13),
				dg19Tlv.toByteArray(),
				getAccessRightReadEidDg(19),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg19);
	}
	
	@Override
	protected void addEidDg20(DedicatedFile eIdAppl) {
		ConstructedTlvDataObject dg20Tlv = new ConstructedTlvDataObject(new TlvTag((byte) 0x74));
		PrimitiveTlvDataObject residencePermit2 = new PrimitiveTlvDataObject(new TlvTag((byte) 0x04), HexString.toByteArray(getEidDg20PlainData()));
		dg20Tlv.addTlvDataObject(residencePermit2);
		
		CardFile eidDg19 = new ElementaryFile(new FileIdentifier(0x0114),
				new ShortFileIdentifier(0x14),
				dg20Tlv.toByteArray(),
				getAccessRightReadEidDg(20),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		eIdAppl.addChild(eidDg19);
	}
	
	public static ConstructedTlvDataObject createEidDg17Tlv(String streetString, String cityString, String countryString, String zipString) throws UnsupportedEncodingException {
		ConstructedTlvDataObject dg17 = new ConstructedTlvDataObject(new TlvTag((byte) 0x71));
		ConstructedTlvDataObject npor = new ConstructedTlvDataObject(new TlvTag((byte) 0x30));
		ConstructedTlvDataObject seq1 = new ConstructedTlvDataObject(new TlvTag((byte) 0xAA));
		ConstructedTlvDataObject seq2 = new ConstructedTlvDataObject(new TlvTag((byte) 0xAB));
		ConstructedTlvDataObject seq3 = new ConstructedTlvDataObject(new TlvTag((byte) 0xAD));
		ConstructedTlvDataObject seq4 = new ConstructedTlvDataObject(new TlvTag((byte) 0xAE));
		PrimitiveTlvDataObject street = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), streetString.getBytes("UTF-8"));
		PrimitiveTlvDataObject city = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), cityString.getBytes("UTF-8"));
		PrimitiveTlvDataObject country = new PrimitiveTlvDataObject(new TlvTag((byte) 0x13), countryString.getBytes("US-ASCII"));
		PrimitiveTlvDataObject zip = new PrimitiveTlvDataObject(new TlvTag((byte) 0x13), zipString.getBytes("US-ASCII"));
		dg17.addTlvDataObject(npor);
		npor.addTlvDataObject(seq1);
		npor.addTlvDataObject(seq2);
		npor.addTlvDataObject(seq3);
		npor.addTlvDataObject(seq4);
		seq1.addTlvDataObject(street);
		seq2.addTlvDataObject(city);
		seq3.addTlvDataObject(country);
		seq4.addTlvDataObject(zip);
		
		return dg17;
	}
	
}
