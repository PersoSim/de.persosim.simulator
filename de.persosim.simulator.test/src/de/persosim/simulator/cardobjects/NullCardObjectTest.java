package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;

public class NullCardObjectTest extends PersoSimTestCase {
	NullCardObject nullCardObject;

	@Before
	public void setUp() {
		nullCardObject = new NullCardObject();
	}

	@Test
	public void testGetParent() {
		assertNull(nullCardObject.getParent());
	}

	@Test
	public void testGetChildren() {
		assertEquals(0, nullCardObject.getChildren().size());
	}

	@Test
	public void testGetLifeCycle() {
		assertEquals(Iso7816LifeCycleState.UNDEFINED,
				nullCardObject.getLifeCycleState());
	}

	@Test
	public void testSetLifeCycle() {
		nullCardObject.updateLifeCycleState(Iso7816LifeCycleState.CREATION);
		assertEquals(Iso7816LifeCycleState.UNDEFINED,
				nullCardObject.getLifeCycleState());
	}

	/**
	 * The {@link NullCardObject} is expected to deliver an empty collection.
	 */
	@Test
	public void testGetAllIdentifier() {
		assertEquals(0 , nullCardObject.getAllIdentifiers().size());
	}
}
