package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertArrayEquals;
import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

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
		assertArrayEquals(HexString.toByteArray("894D03F148C6265E89845B218856EA34D00EF8E8"), authObject.getPassword());
	}
}
