package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertArrayEquals;
import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;

public class MrzAuthObjectTest extends PersoSimTestCase {

	PasswordAuthObject authObject;
	@Mocked
	SecStatus mockedSecurityStatus;
	AuthObjectIdentifier identifier;
	String password;
	
	@Before
	public void setup() throws Exception{
		identifier = new AuthObjectIdentifier(1);
		password = "P<D<<C11T002JM4<<<<<<<<<<<<<<<9608122F2310314D<<<<<<<<<<<<<4MUSTERMANN<<ERIKA<<<<<<<<<<<<<";
		authObject = new MrzAuthObject(identifier, password); 
	}
	
	@Test
	public void testGetPassword(){
		assertArrayEquals(new byte [] {(byte) 0x89,0x4D,0x03,(byte) 0xF1,0x48,(byte) 0xC6,0x26,0x5E,(byte) 0x89,(byte) 0x84,0x5B,0x21,(byte) 0x88,0x56,(byte) 0xEA,0x34,(byte) 0xD0,0x0E,(byte) 0xF8,(byte) 0xE8}, authObject.getPassword());
	}
}
