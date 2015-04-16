package de.persosim.simulator.perso;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.Protocol.SecInfoPublicity;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.HexString;

public class DefaultNpaUnmarshallerCallbackTest extends PersoSimTestCase {
	
	@Mocked
	Personalization mockedPerso;
	MasterFile masterFile = new MasterFile();
	@Mocked
	Protocol mockedProtocol1, mockedProtocol2;

	@Before
	public void setUp() {
		masterFile.setSecStatus(new SecStatus());
		
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedPerso.getObjectTree();
				result = masterFile;
				
				mockedPerso.getProtocolList();
				result = Arrays.asList(mockedProtocol1, mockedProtocol2);
				
				mockedProtocol1.getSecInfos(withInstanceOf(SecInfoPublicity.class), masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("310301011F")));
				
				mockedProtocol2.getSecInfos(withInstanceOf(SecInfoPublicity.class), masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("310301012F")));
				
			}
		};
		
		//XXX AMY is this necessary in the context of the bouncy castle dependency removal
		//ensure that matching of IssuerNames works as expected
		X500Name.setDefaultStyle(RFC4519Style.INSTANCE);
	}
	
	/**
	 * Positive test: check that EF.CardAccess is created if missing
	 * @throws Exception 
	 */
	@Test
	public void testAfterUnmarshall_addCardAccess() throws Exception {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedProtocol1.getSecInfos(SecInfoPublicity.PUBLIC, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010101")));
				
				mockedProtocol2.getSecInfos(SecInfoPublicity.PUBLIC, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010102")));
				
			}
		};
				
//		new DefaultNpaUnmarshallerCallback().afterUnmarshall(mockedPerso);
		
		//check content of created EF.CardAccess
		Collection<CardObject> files = masterFile.findChildren(new FileIdentifier(0x011C));
		assertEquals(1, files.size());
		
		ElementaryFile efCardAccess = (ElementaryFile) files.iterator().next();
		
		assertEquals("310A31030101013103010102", HexString.encode(efCardAccess.getContent()));
	}
	
	/**
	 * Positive test: check that EF.CardSecurity is created if missing
	 * @throws Exception 
	 */
	@Test
	public void testAfterUnmarshall_addCardSecurity() throws Exception {
		
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedProtocol1.getSecInfos(SecInfoPublicity.AUTHENTICATED, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010101")));
				
				mockedProtocol2.getSecInfos(SecInfoPublicity.AUTHENTICATED, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010102")), new ConstructedTlvDataObject(HexString.toByteArray("3103010103")));
				
			}
		};
						
//		new DefaultNpaUnmarshallerCallback().afterUnmarshall(mockedPerso);
		
		//check content of created EF.CardSecurity
		Collection<CardObject> files = masterFile.findChildren(new FileIdentifier(0x011D));
		assertEquals(1, files.size());
		
		ElementaryFile efCardSecurity = (ElementaryFile) files.iterator().next();
		byte[] expecedEContent = HexString.toByteArray("310F310301010131030101023103010103");
		
		byte[] fileContent = Deencapsulation.getField(efCardSecurity, "content");
		
		ConstructedTlvDataObject fileContentTlv = new ConstructedTlvDataObject(fileContent);
		
		assertEquals(new TlvTag(Asn1.SEQUENCE),  fileContentTlv.getTlvTag());
		TlvDataObject cmsTlv = new ConstructedTlvDataObject(fileContentTlv.getTlvDataObject(new TlvTag((byte)0xA0)).getValueField());
		SecInfoCmsBuilderTest.checkSignedData(cmsTlv.toByteArray(), expecedEContent);
	}

	/**
	 * Positive test: check that EF.ChipSecurity is created if missing
	 * @throws Exception 
	 */
	@Test
	public void testAfterUnmarshall_addChipSecurity() throws Exception {
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedProtocol1.getSecInfos(SecInfoPublicity.AUTHENTICATED, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010101")));
				
				mockedProtocol1.getSecInfos(SecInfoPublicity.PRIVILEGED, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010101")));
				
				mockedProtocol2.getSecInfos(SecInfoPublicity.AUTHENTICATED, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010102")));
				
				mockedProtocol2.getSecInfos(SecInfoPublicity.PRIVILEGED, masterFile);
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010102")), new ConstructedTlvDataObject(HexString.toByteArray("3103010103")));
				
			}
		};
								
//		new DefaultNpaUnmarshallerCallback().afterUnmarshall(mockedPerso);
		
		//check content of created EF.ChipSecurity
		Collection<CardObject> files = masterFile.findChildren(new FileIdentifier(0x011B));
		assertEquals(1, files.size());
		
		ElementaryFile efChipSecurity = (ElementaryFile) files.iterator().next();
		byte[] expecedEContent = HexString.toByteArray("310F310301010131030101023103010103");
		
		byte[] fileContent = Deencapsulation.getField(efChipSecurity, "content");
		
		ConstructedTlvDataObject fileContentTlv = new ConstructedTlvDataObject(fileContent);
		
		assertEquals(new TlvTag(Asn1.SEQUENCE),  fileContentTlv.getTlvTag());
		TlvDataObject cmsTlv = new ConstructedTlvDataObject(fileContentTlv.getTlvDataObject(new TlvTag((byte)0xA0)).getValueField());
		SecInfoCmsBuilderTest.checkSignedData(cmsTlv.toByteArray(), expecedEContent);
	}

}
