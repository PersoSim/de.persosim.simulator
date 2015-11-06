package de.persosim.simulator.perso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Date;

import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.ByteDataAuxObject;
import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.ChangeablePasswordAuthObject;
import de.persosim.simulator.cardobjects.DateAuxObject;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyPairObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.MrzAuthObject;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.PinObject;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.documents.Mrz;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.protocols.ca.Ca;
import de.persosim.simulator.protocols.ri.Ri;
import de.persosim.simulator.protocols.ri.RiOid;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.seccondition.OrSecCondition;
import de.persosim.simulator.seccondition.PaceSecurityCondition;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.seccondition.TaSecurityCondition;
import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectFactory;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public abstract class AbstractProfile extends DefaultPersoTestPki implements Asn1 {

	protected PersonalizationDataContainer persoDataContainer;
	
	public abstract void setPersoDataContainer();
	
	public void initPersonalizationDataContainer() {
		if (persoDataContainer == null) {
			setPersoDataContainer();
		}
	}
	
	public String getPin() {
		return "123456";
	}
	
	public String getCan() {
		return "500540";
	}
	
	public String getPuk() {
		return "9876543210";
	}
	
	public static String getMrzLine1of3(String documentType, String issuingCountry, String documentNumber) {
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
		if(documentNumber.length() != 9) {throw new IllegalArgumentException("document number must be exactly 9 characters long");}
		
		line1 += documentNumber;
		line1 += String.valueOf((char) Mrz.computeChecksum(documentNumber.getBytes(), 0, documentNumber.length()));
		
		for(int i = 0; i < 15; i++) {
			line1 += Mrz.Filler;
		}
		
		return line1;
	}
	
	public static String getMrzLine2of3(String mrzLine1, String dob, String sex, String doe, String nation) {
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
	
	public static String getMrz(String documentType, String issuingCountry, String documentNumber, String dob, String sex, String doe, String nation, String mrzLine3) {
		String mrzLine1 = getMrzLine1of3(documentType, issuingCountry, documentNumber);
		String mrzLine2 = getMrzLine2of3(mrzLine1, dob, sex, doe, nation);
		
		return mrzLine1 + mrzLine2 + mrzLine3;
	}
	
	@Override
	public MasterFile buildObjectTree() throws AccessDeniedException {
		initPersonalizationDataContainer();
		return super.buildObjectTree();
	}
	
	@Override
	protected void addEpassDatagroup1(DedicatedFile ePassAppl) throws AccessDeniedException {
		String mrz = persoDataContainer.getEpassDg1PlainData();
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
				new PaceSecurityCondition(),
				SecCondition.DENIED, 
				SecCondition.DENIED);
		ePassAppl.addChild(epassDg1);
	}
	
	@Override
	protected void addAuthObjects(MasterFile mf) throws NoSuchAlgorithmException,
			NoSuchProviderException, IOException, UnsupportedEncodingException, AccessDeniedException {
		MrzAuthObject mrz = new MrzAuthObject(
				new AuthObjectIdentifier(1),
				persoDataContainer.getMrz());
		mf.addChild(mrz);

		ChangeablePasswordAuthObject can = new ChangeablePasswordAuthObject(
				new AuthObjectIdentifier(2), getCan().getBytes("UTF-8"), "CAN",
				6, 6);
		mf.addChild(can);

		PasswordAuthObjectWithRetryCounter pin = new PinObject(
				new AuthObjectIdentifier(3), getPin().getBytes("UTF-8"), 6, 6,
				3);
		mf.addChild(pin);

		PasswordAuthObject puk = new PasswordAuthObject(
				new AuthObjectIdentifier(4), getPuk().getBytes("UTF-8"),
				"PUK");
		mf.addChild(puk);
	}
	
	@Override
	protected void addAuxData(MasterFile mf) throws AccessDeniedException {
		
		initPersonalizationDataContainer();
		
		byte[] communityId = HexString.toByteArray(persoDataContainer.getDg18PlainData());
		mf.addChild(new ByteDataAuxObject(new OidIdentifier(
				TaOid.id_CommunityID), communityId));
		
		Date dateOfBirth = Utils.getDate(persoDataContainer.getDg8PlainData(), Utils.DATE_SET_MAX_VALUE);
		mf.addChild(new DateAuxObject(new OidIdentifier(TaOid.id_DateOfBirth),
				dateOfBirth));
		
		Date validityDate = Utils.getDate(persoDataContainer.getDg3PlainData());
		mf.addChild(new DateAuxObject(new OidIdentifier(TaOid.id_DateOfExpiry),
				validityDate));
	}
	
	/**
	 * This method returns the TLV structure for a data group containing an ASN.1 type UTF8String.
	 * @param tlvTag the tag to be used for this data group
	 * @param content the content to be placed in the ASN.1 type UTF8String
	 * @return the TLV structure for a data group containing an ASN.1 type UTF8String
	 */
	public static ConstructedTlvDataObject getUtf8StringDgTlv(TlvTag tlvTag, String content) {
		ConstructedTlvDataObject utf8DgTlv = new ConstructedTlvDataObject(tlvTag);
		byte[] utf8StringPlainBytes;
		
		try {
			utf8StringPlainBytes = content.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 is a valid encoding so this is never going to happen
			e.printStackTrace();
			utf8StringPlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject utf8StringTlv = new PrimitiveTlvDataObject(new TlvTag(UNIVERSAL_UTF8String), utf8StringPlainBytes);
		utf8DgTlv.addTlvDataObject(utf8StringTlv);
		
		return utf8DgTlv;
	}
	
	/**
	 * This method returns the TLV structure for a data group containing an ASN.1 type Date.
	 * @param tlvTag the tag to be used for this data group
	 * @param content the content to be placed in the ASN.1 type Date
	 * @return the TLV structure for a data group containing an ASN.1 type Date
	 */
	public static ConstructedTlvDataObject getDateDgTlv(TlvTag tlvTag, String content) {
		ConstructedTlvDataObject dateDgTlv = new ConstructedTlvDataObject(tlvTag);
		byte[] datePlainBytes;
		
		try {
			datePlainBytes = content.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// US-ASCII is a valid encoding so this is never going to happen
			e.printStackTrace();
			datePlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject dateTlv = new PrimitiveTlvDataObject(new TlvTag(UNIVERSAL_NUMERIC_STRING), datePlainBytes);
		dateDgTlv.addTlvDataObject(dateTlv);
		
		return dateDgTlv;
	}
	
	/**
	 * This method returns the TLV structure for a data group containing an ASN.1 type ICAOString.
	 * @param tlvTag the tag to be used for this data group
	 * @param content the content to be placed in the ASN.1 type ICAOString
	 * @return the TLV structure for a data group containing an ASN.1 type ICAOString
	 */
	public static ConstructedTlvDataObject getIcaoStringDgTlv(TlvTag tlvTag, String content) {
		ConstructedTlvDataObject icaoStringDgTlv = new ConstructedTlvDataObject(tlvTag);
		byte[] icaoStringPlainBytes;
		
		try {
			icaoStringPlainBytes = content.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// US-ASCII is a valid encoding so this is never going to happen
			e.printStackTrace();
			icaoStringPlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject icaoStringTlv = new PrimitiveTlvDataObject(new TlvTag(UNIVERSAL_PRINTABLE_STRING), icaoStringPlainBytes);
		icaoStringDgTlv.addTlvDataObject(icaoStringTlv);
		
		return icaoStringDgTlv;
	}
	
	@Override
	protected void addEidDg1(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		ConstructedTlvDataObject dg1Tlv = getIcaoStringDgTlv(new TlvTag((byte) 0x61), persoDataContainer.getDg1PlainData());
		
		CardFile eidDg1 = new ElementaryFile(new FileIdentifier(0x0101),
				new ShortFileIdentifier(0x01),
				dg1Tlv.toByteArray(),
				getAccessRightReadEidDg(1),
				SecCondition.DENIED, 
				SecCondition.DENIED);
		eIdAppl.addChild(eidDg1);
	}
	
	/**
	 * This method returns the TLV structure for a data group containing an ASN.1 type IssuingState.
	 * @param tlvTag the tag to be used for this data group
	 * @param content the content to be placed in the ASN.1 type IssuingState
	 * @return the TLV structure for a data group containing an ASN.1 type IssuingState
	 */
	public static ConstructedTlvDataObject getIssuingStateDgTlv(TlvTag tlvTag, String content) {
		ConstructedTlvDataObject issuingStateDgTlv = new ConstructedTlvDataObject(tlvTag);
		byte[] issuingStatePlainBytes;
		
		try {
			issuingStatePlainBytes = content.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// US-ASCII is a valid encoding so this is never going to happen
			e.printStackTrace();
			issuingStatePlainBytes = new byte[0];
		}
		
		PrimitiveTlvDataObject issuingStateTlv = new PrimitiveTlvDataObject(new TlvTag(UNIVERSAL_PRINTABLE_STRING), issuingStatePlainBytes);
		issuingStateDgTlv.addTlvDataObject(issuingStateTlv);
		
		return issuingStateDgTlv;
	}
	
	@Override
	protected void addEidDg2(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		ConstructedTlvDataObject dg2Tlv = getIssuingStateDgTlv(new TlvTag((byte) 0x62), persoDataContainer.getDg2PlainData());
		
		CardFile eidDg1 = new ElementaryFile(new FileIdentifier(0x0102),
				new ShortFileIdentifier(0x02),
				dg2Tlv.toByteArray(),
				getAccessRightReadEidDg(2),
				SecCondition.DENIED,
				SecCondition.DENIED);
		eIdAppl.addChild(eidDg1);
	}
	
	@Override
	protected void addEidDg3(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		ConstructedTlvDataObject dg3Tlv = getDateDgTlv(new TlvTag((byte) 0x63), persoDataContainer.getDg3PlainData());
		
		CardFile eidDg3 = new ElementaryFile(new FileIdentifier(0x0103),
				new ShortFileIdentifier(0x03),
				dg3Tlv.toByteArray(),
				getAccessRightReadEidDg(3),
				SecCondition.DENIED,
				SecCondition.DENIED);
		eIdAppl.addChild(eidDg3);
	}
	
	@Override
	protected void addEidDg4(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		ConstructedTlvDataObject dg4Tlv = getUtf8StringDgTlv(new TlvTag((byte) 0x64), persoDataContainer.getDg4PlainData());

		CardFile eidDg4 = new ElementaryFile(new FileIdentifier(0x0104),
				new ShortFileIdentifier(0x04),
				dg4Tlv.toByteArray(),
				getAccessRightReadEidDg(4),
				SecCondition.DENIED,
				SecCondition.DENIED);
		eIdAppl.addChild(eidDg4);
	}
	
	@Override
	protected void addEidDg5(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		ConstructedTlvDataObject dg5Tlv = getUtf8StringDgTlv(new TlvTag((byte) 0x65), persoDataContainer.getDg5PlainData());
		
		CardFile eidDg5 = new ElementaryFile(
				new FileIdentifier(0x0105),
				new ShortFileIdentifier(0x05),
				dg5Tlv.toByteArray(),
				getAccessRightReadEidDg(5),
				SecCondition.DENIED,
				SecCondition.DENIED);
		eIdAppl.addChild(eidDg5);
	}
	
	@Override
	protected void addEidDg6(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		ConstructedTlvDataObject dg6Tlv = getUtf8StringDgTlv(new TlvTag((byte) 0x66), persoDataContainer.getDg6PlainData());
		
		CardFile eidDg6 = new ElementaryFile(
				new FileIdentifier(0x0106),
				new ShortFileIdentifier(0x06),
				dg6Tlv.toByteArray(),
				getAccessRightReadEidDg(6),
				SecCondition.DENIED,
				SecCondition.DENIED);
		eIdAppl.addChild(eidDg6);
	}
	
	@Override
	protected void addEidDg7(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		ConstructedTlvDataObject dg7Tlv = getUtf8StringDgTlv(new TlvTag((byte) 0x67), persoDataContainer.getDg7PlainData());
		
		CardFile eidDg7 = new ElementaryFile(new FileIdentifier(0x0107),
				new ShortFileIdentifier(0x07),
				dg7Tlv.toByteArray(),
				getAccessRightReadEidDg(7),
				SecCondition.DENIED,
				SecCondition.DENIED);
		eIdAppl.addChild(eidDg7);
	}
	
	@Override
	protected void addEidDg8(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		ConstructedTlvDataObject dg8Tlv = getDateDgTlv(new TlvTag((byte) 0x68), persoDataContainer.getDg8PlainData());
		
		CardFile eidDg8 = new ElementaryFile(new FileIdentifier(0x0108),
				new ShortFileIdentifier(0x08),
				dg8Tlv.toByteArray(),
				getAccessRightReadEidDg(8),
				SecCondition.DENIED,
				SecCondition.DENIED);
		eIdAppl.addChild(eidDg8);
	}
	
	@Override
	protected void addEidDg9(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		try {
			ConstructedTlvDataObject dg9Tlv = getGeneralPlaceDgTlv(new TlvTag((byte) 0x69), null, persoDataContainer.getDg9PlainData(), null, null, null);
			
			CardFile eidDg9 = new ElementaryFile(
					new FileIdentifier(0x0109),
					new ShortFileIdentifier(0x09),
					dg9Tlv.toByteArray(),
					getAccessRightReadEidDg(9),
					SecCondition.DENIED,
					SecCondition.DENIED);
			eIdAppl.addChild(eidDg9);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void addEidDg10(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
	@Override
	protected void addEidDg11(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
	@Override
	protected void addEidDg12(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
	@Override
	protected void addEidDg13(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		ConstructedTlvDataObject dg13Tlv = getUtf8StringDgTlv(new TlvTag((byte) 0x6D), persoDataContainer.getDg13PlainData());
		
		CardFile eidDg13 = new ElementaryFile(new FileIdentifier(0x010D),
				new ShortFileIdentifier(0x0D),
				dg13Tlv.toByteArray(),
				getAccessRightReadEidDg(13),
				SecCondition.DENIED,
				SecCondition.DENIED);
		eIdAppl.addChild(eidDg13);
	}
	
	protected void addEfCardAccess(MasterFile mf) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		TlvDataObject efCardAccessTlv = TlvDataObjectFactory.createTLVDataObject(persoDataContainer.getEfCardAccess());
		
		CardFile eidDgCardAccess = new ElementaryFile(new FileIdentifier(0x011C),
				new ShortFileIdentifier(0x1C),
				efCardAccessTlv.toByteArray(),
				SecCondition.ALLOWED,
				SecCondition.DENIED,
				SecCondition.DENIED);
		mf.addChild(eidDgCardAccess);
	}
	
	protected void addEfCardSecurity(MasterFile mf) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		TlvDataObject efCardSecurityTlv = TlvDataObjectFactory.createTLVDataObject(persoDataContainer.getEfCardSecurity());
		
		CardFile eidDgCardSecurity = new ElementaryFile(new FileIdentifier(0x011D),
				new ShortFileIdentifier(0x1D),
				efCardSecurityTlv.toByteArray(),
				new TaSecurityCondition(),
				SecCondition.DENIED,
				SecCondition.DENIED);
		mf.addChild(eidDgCardSecurity);
	}
	
	protected void addEfChipSecurity(MasterFile mf) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		TlvDataObject efChipSecurityTlv = TlvDataObjectFactory.createTLVDataObject(persoDataContainer.getEfChipSecurity());
		
		
		SecCondition taWithIs = new TaSecurityCondition(TerminalType.IS, null);
        SecCondition taWithAtPrivileged = new TaSecurityCondition(
                        TerminalType.AT, new RelativeAuthorization(
                                        CertificateRole.TERMINAL, new BitField(38).flipBit(3)));
        
		CardFile eidDgChipSecurity = new ElementaryFile(new FileIdentifier(0x011B),
				new ShortFileIdentifier(0x1B),
				efChipSecurityTlv.toByteArray(),
				new OrSecCondition(taWithIs, taWithAtPrivileged),
				SecCondition.DENIED,
				SecCondition.DENIED);
		mf.addChild(eidDgChipSecurity);
        		
	}
	
	/**
	 * This method returns the TLV structure for a data group containing an ASN.1 type GeneralPlace.
	 * @param tlvTag the tag to be used for this data group
	 * @param content the content to be placed in the ASN.1 type GeneralPlace
	 * @return the TLV structure for a data group containing an ASN.1 type GeneralPlace
	 */
	public static ConstructedTlvDataObject getGeneralPlaceDgTlv(TlvTag tlvTag, String streetString, String cityString, String stateString, String countryString, String zipString) throws UnsupportedEncodingException {
		ConstructedTlvDataObject generalPlaceDgTlv = new ConstructedTlvDataObject(tlvTag);
		ConstructedTlvDataObject generalPlace;
		
		int nullCounter = 0;
		String place = "";
		if (streetString == null) {nullCounter++;} else {place = streetString;};
		if(cityString == null) {nullCounter++;} else {place = cityString;};
		if(stateString == null) {nullCounter++;} else {place = stateString;};
		if(countryString == null) {nullCounter++;} else {place = countryString;};
		if(zipString == null) {nullCounter++;} else {place = zipString;};
		
		if(nullCounter == 5) {
			generalPlace = new ConstructedTlvDataObject(new TlvTag((byte) 0xA2));
			PrimitiveTlvDataObject noPlace = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), (new String("keine Hauptwohnung in Deutschland")).getBytes("UTF-8"));
			generalPlace.addTlvDataObject(noPlace);
		} else{
			if(nullCounter == 4) {
				generalPlace = new ConstructedTlvDataObject(new TlvTag((byte) 0xA1));
				PrimitiveTlvDataObject freeText = new PrimitiveTlvDataObject(new TlvTag(UNIVERSAL_UTF8String), place.getBytes("UTF-8"));
				generalPlace.addTlvDataObject(freeText);
			} else{
				generalPlace = new ConstructedTlvDataObject(new TlvTag((byte) 0x30));
				
				ConstructedTlvDataObject sequenceElement;
				PrimitiveTlvDataObject content;
				
				if(streetString != null) {
					sequenceElement = new ConstructedTlvDataObject(new TlvTag((byte) 0xAA));
					generalPlace.addTlvDataObject(sequenceElement);
					content = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), streetString.getBytes("UTF-8"));
					sequenceElement.addTlvDataObject(content);
				}
				
				if(cityString != null) {
					sequenceElement = new ConstructedTlvDataObject(new TlvTag((byte) 0xAB));
					generalPlace.addTlvDataObject(sequenceElement);
					content = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), cityString.getBytes("UTF-8"));
					sequenceElement.addTlvDataObject(content);
				}
				
				if(stateString != null) {
					sequenceElement = new ConstructedTlvDataObject(new TlvTag((byte) 0xAC));
					generalPlace.addTlvDataObject(sequenceElement);
					content = new PrimitiveTlvDataObject(new TlvTag((byte) 0x0C), stateString.getBytes("UTF-8"));
					sequenceElement.addTlvDataObject(content);
				}
				
				if(countryString != null) {
					sequenceElement = new ConstructedTlvDataObject(new TlvTag((byte) 0xAD));
					generalPlace.addTlvDataObject(sequenceElement);
					content = new PrimitiveTlvDataObject(new TlvTag((byte) 0x13), countryString.getBytes("US-ASCII"));
					sequenceElement.addTlvDataObject(content);
				}
				
				if(zipString != null) {
					sequenceElement = new ConstructedTlvDataObject(new TlvTag((byte) 0xAE));
					generalPlace.addTlvDataObject(sequenceElement);
					content = new PrimitiveTlvDataObject(new TlvTag((byte) 0x13), zipString.getBytes("US-ASCII"));
					sequenceElement.addTlvDataObject(content);
				}
			}
		}
		
		generalPlaceDgTlv.addTlvDataObject(generalPlace);
		
		return generalPlaceDgTlv;
	}
	
	@Override
	protected void addEidDg17(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		try {
			ConstructedTlvDataObject dg17Tlv = getGeneralPlaceDgTlv(
					new TlvTag((byte) 0x71),
					persoDataContainer.getDg17StreetPlainData(),
					persoDataContainer.getDg17CityPlainData(),
					persoDataContainer.getDg17StatePlainData(),
					persoDataContainer.getDg17CountryPlainData(),
					persoDataContainer.getDg17ZipPlainData());
			
			CardFile eidDg17 = new ElementaryFile(
					new FileIdentifier(0x0111),
					new ShortFileIdentifier(0x11),
					dg17Tlv.toByteArray(),
					getAccessRightReadEidDg(17),
					getAccessRightUpdateEidDg(17),
					SecCondition.DENIED);
			eIdAppl.addChild(eidDg17);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns the TLV structure for a data group containing an ASN.1 type CommunityId.
	 * @param tlvTag the tag to be used for this data group
	 * @param content the content to be placed in the ASN.1 type CommunityId
	 * @return the TLV structure for a data group containing an ASN.1 type CommunityId
	 */
	public static ConstructedTlvDataObject getCommunityIdDgTlv(TlvTag tlvTag, String content) {
		ConstructedTlvDataObject communityIdDgTlv = new ConstructedTlvDataObject(tlvTag);
		PrimitiveTlvDataObject communityIdTlv = new PrimitiveTlvDataObject(new TlvTag(UNIVERSAL_OCTET_STRING), HexString.toByteArray(content));
		communityIdDgTlv.addTlvDataObject(communityIdTlv);
		
		return communityIdDgTlv;
	}
	
	@Override
	protected void addEidDg18(DedicatedFile eIdAppl) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		ConstructedTlvDataObject dg18Tlv = getCommunityIdDgTlv(new TlvTag((byte) 0x72), persoDataContainer.getDg18PlainData());
		
		CardFile eidDg18 = new ElementaryFile(new FileIdentifier(0x0112),
				new ShortFileIdentifier(0x12),
				dg18Tlv.toByteArray(),
				getAccessRightReadEidDg(18),
				getAccessRightUpdateEidDg(18),
				SecCondition.DENIED);
		eIdAppl.addChild(eidDg18);
	}
	
	@Override
	protected void addEidDg19(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
	@Override
	protected void addEidDg20(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
	@Override
	protected void addEidDg21(DedicatedFile eIdAppl) {
		// do not create DG
	}
	
	@Override
	protected void addCaKeys(MasterFile mf) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		ArrayList<KeyPair> caKeys = persoDataContainer.getCaKeys();
		ArrayList<Integer> caKeyIds = persoDataContainer.getCaKeyIds();
		ArrayList<Boolean> caKeyPriv = persoDataContainer.getCaKeyPrivileges();
		
		// CA static key pair PICC
		KeyPairObject caKey;
		for(int i=0; i<caKeys.size(); i++) {
			caKey = new KeyPairObject(caKeys.get(i), new KeyIdentifier(caKeyIds.get(i)), caKeyPriv.get(i));
			caKey.addOidIdentifier(Ca.OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128);
			mf.addChild(caKey);
		}
	}
	
	@Override
	protected void addRiKeys(MasterFile mf) throws AccessDeniedException {
		initPersonalizationDataContainer();
		
		ArrayList<KeyPair> riKeys = persoDataContainer.getRiKeys();
		ArrayList<Integer> riKeyIds = persoDataContainer.getRiKeyIds();
		ArrayList<Boolean> riKeyAuthorizedOnly = persoDataContainer.getRiKeyAuthorizedOnly();
		
		// RI static key pair PICC
		KeyPairObject riKey;
		boolean authorizedOnly;
		for(int i=0; i<riKeys.size(); i++) {
			authorizedOnly = riKeyAuthorizedOnly.get(i);
			
			riKey = new KeyPairObject(riKeys.get(i), new KeyIdentifier(riKeyIds.get(i)), authorizedOnly);
			riKey.addOidIdentifier(new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)));
			
			mf.addChild(riKey);
		}
	}
	
}
