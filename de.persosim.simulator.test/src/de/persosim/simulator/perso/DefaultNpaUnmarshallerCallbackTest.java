package de.persosim.simulator.perso;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.persosim.simulator.cardobjects.FileIdentifier;

public class DefaultNpaUnmarshallerCallbackTest {

	/**
	 * Positive test: check that EF.CardAccess is created if missing
	 */
	@Test
	public void testAfterUnmarshall_addCardAccess() {
		Personalization perso = new XmlPersonalization();
		
		new DefaultNpaUnmarshallerCallback().afterUnmarshall(perso);
		
		assertEquals(1, perso.getObjectTree().findChildren(new FileIdentifier(0x011C)).size());
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
