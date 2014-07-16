package de.persosim.simulator.perso;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.utils.HexString;

public class DefaultNpaUnmarshallerCallbackTest {
	
	@Mocked
	Personalization mockedPerso;
	MasterFile masterFile = new MasterFile();
	@Mocked
	Protocol mockedProtocol1, mockedProtocol2;

	@Before
	public void setUp() {
		masterFile.setSecStatus(new SecStatus());
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
				mockedPerso.getObjectTree();
				result = masterFile;
				
				mockedPerso.getProtocolList();
				result = Arrays.asList(mockedProtocol1, mockedProtocol2);
				
				mockedProtocol1.getSecInfos();
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010101")));
				
				mockedProtocol2.getSecInfos();
				result = Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("3103010102")));
				
			}
		};
		
		new DefaultNpaUnmarshallerCallback().afterUnmarshall(mockedPerso);
		
		//check content of created EF.CardAccess
		Collection<CardObject> files = masterFile.findChildren(new FileIdentifier(0x011C));
		assertEquals(1, files.size());
		
		ElementaryFile efCardAccess = (ElementaryFile) files.iterator().next();
		
		assertEquals("310A31030101013103010102", HexString.encode(efCardAccess.getContent()));
	}
	
	/**
	 * Positive test: check that EF.CardSecurity is created if missing
	 */
	@Test
	public void testAfterUnmarshall_addCardSecurity() {
		Personalization perso = new XmlPersonalization();
		
		new DefaultNpaUnmarshallerCallback().afterUnmarshall(perso);
		
		assertEquals(1, perso.getObjectTree().findChildren(new FileIdentifier(0x011D)).size());
	}
	
	/**
	 * Positive test: check that EF.ChipSecurity is created if missing
	 */
	@Test
	public void testAfterUnmarshall_addChipSecurity() {
		Personalization perso = new XmlPersonalization();
		
		new DefaultNpaUnmarshallerCallback().afterUnmarshall(perso);
		
		assertEquals(1, perso.getObjectTree().findChildren(new FileIdentifier(0x011B)).size());
	}

}
