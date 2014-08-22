package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.LinkedList;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.secstatus.NullSecurityCondition;
import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;

public class ElementaryFileTest extends PersoSimTestCase {

	@Mocked
	SecStatus mockedSecurityStatus;
	ElementaryFile file;
	
	@Before
	public void setUp() throws ReflectiveOperationException{
		// prepare the mock
		new NonStrictExpectations(SecStatus.class) {
			{
				mockedSecurityStatus.getCurrentMechanisms(null, null);
				result = new HashSet<Class<? extends SecMechanism>>();
			}
		};
		
		// create file to test
		LinkedList<SecCondition> unprotected = new LinkedList<>();
		unprotected.add(new NullSecurityCondition());
		file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 }, unprotected, unprotected, unprotected);
		file.setSecStatus(mockedSecurityStatus);
				
	}
	
	@Test
	public void testUpdateBinary() throws Exception {
		byte[] newContent = new byte[] { 5, 6, 7, 8 };

		file.update(0, newContent);

		assertArrayEquals("file content not as expected", newContent,
				file.getContent());
	}
	
	@Test
	public void testGetFileControlInformation(){
		ConstructedTlvDataObject fcp = file.getFileControlParameterDataObject();
		assertTrue(fcp.containsTlvDataObject(new TlvTag((byte)0x80)));
		assertTrue(fcp.containsTlvDataObject(new TlvTag((byte)0x88)));
	}
}
