package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;
import mockit.Mocked;

public class MrzAuthObjectTest extends PersoSimTestCase {

	PasswordAuthObject authObject;
	@Mocked
	SecStatus mockedSecurityStatus;
	AuthObjectIdentifier identifier;
	
	@Before
	public void setup() throws Exception{
		identifier = new AuthObjectIdentifier(1);
	}
	
	@Test
	public void testGetPasswordTd1DocumentNumber9CharactersMustermann() throws NoSuchAlgorithmException, IOException{
		authObject = new MrzAuthObject(identifier, "P<D<<C11T002JM4<<<<<<<<<<<<<<<9608122F2310314D<<<<<<<<<<<<<4MUSTERMANN<<ERIKA<<<<<<<<<<<<<"); 
		assertArrayEquals(HexString.toByteArray("894D03F148C6265E89845B218856EA34D00EF8E8"), authObject.getPassword());
	}
	
	@Test
	public void testGetPasswordTd1DocumentNumber9Characters() throws NoSuchAlgorithmException, IOException{
		authObject = new MrzAuthObject(identifier, "I<UTOL898902C<3<<<<<<<<<<<<<<<6908061F9406236UTO<<<<<<<<<<<1ERIKSSON<<ANNA<MARIA<<<<<<<<<<"); 
		assertArrayEquals(HexString.toByteArray("239AB9CB282DAF66231DC5A4DF6BFBAEDF477565"), authObject.getPassword());
	}
	
	@Ignore("not supported, ticket GT-88")
	@Test
	public void testGetPasswordTd1DocumentNumberMoreThan9Characters() throws NoSuchAlgorithmException, IOException{
		authObject = new MrzAuthObject(identifier, "I<UTOD23145890<7349<<<<<<<<<<<3407127M9507122UTO<<<<<<<<<<<2STEVENSON<<PETER<JOHN<<<<<<<<<"); 
		assertArrayEquals(HexString.toByteArray("b366ad857ddca2b08c0e2998117147300fa5d581"), authObject.getPassword());
	}
	
	@Ignore("not supported, ticket GT-88")
	@Test
	public void testGetPasswordTd2DocumentNumberMoreThan9Characters() throws NoSuchAlgorithmException, IOException{
		authObject = new MrzAuthObject(identifier, "I<UTOSTEVENSON<<PETER<JOHN<<<<<<<<<<D23145890<UTO3407127M95071227349<<<8"); 
		assertArrayEquals(HexString.toByteArray("b366ad857ddca2b08c0e2998117147300fa5d581"), authObject.getPassword());
	}
	
	@Ignore("not supported, ticket GT-88")
	@Test
	public void testGetPasswordTd2DocumentNumber9Characters() throws NoSuchAlgorithmException, IOException{
		authObject = new MrzAuthObject(identifier, "I<UTOERIKSSON<<ANNA<MARIA<<<<<<<<<<<L898902C<3UTO6908061F9406236<<<<<<<8"); 
		assertArrayEquals(HexString.toByteArray("239ab9cb282daf66231dc5a4df6bfbaedf477565"), authObject.getPassword());
	}
}
