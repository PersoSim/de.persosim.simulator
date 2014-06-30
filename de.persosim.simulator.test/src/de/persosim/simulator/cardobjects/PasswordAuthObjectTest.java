package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;

/**
 * @author mboonk
 *
 */
public class PasswordAuthObjectTest extends PersoSimTestCase {
	@Test
	public void testGetPassword(){
		byte [] content = new byte [] {1,2,3};
		PasswordAuthObject password = new PasswordAuthObject(new AuthObjectIdentifier(2), content);
		
		//call mut
		assertArrayEquals(content, password.getPassword());
	}
}
