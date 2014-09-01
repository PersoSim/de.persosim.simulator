package de.persosim.simulator.perso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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
import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public abstract class DefaultPersoTestPkiTemplate extends DefaultPersoTestPki {
	
	public abstract String getEidDg1PlainData();
	public abstract String getEidDg2PlainData();
	public abstract String getEidDg3PlainData();
	public abstract String getEidDg4PlainData();
	public abstract String getEidDg5PlainData();
	public abstract String getEidDg6PlainData();
	public abstract String getEidDg7PlainData();
	public abstract String getEidDg8PlainData();
	
	public abstract String getEidDg10PlainData();
	public abstract String getEidDg11PlainData();
	
	public abstract String getEidDg13PlainData();
	
	
	
	
	public abstract String getEidDg18PlainData();
	
	
	
	
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
	
}
