package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import mockit.Mocked;
import mockit.NonStrictExpectations;

public class ElementaryFileTest extends PersoSimTestCase {

	@Mocked
	SecStatus mockedSecurityStatus;

	@Before
	public void setUp() throws ReflectiveOperationException, AccessDeniedException {
		// prepare the mock
		new NonStrictExpectations(SecStatus.class) {
			{
				mockedSecurityStatus.getCurrentMechanisms(null, null);
				result = new HashSet<Class<? extends SecMechanism>>();
			}
		};

	}

	@Test
	public void testUpdateBinary() throws Exception {
		// create file to test
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 },
				SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		file.setSecStatus(mockedSecurityStatus);

		byte[] newContent = new byte[] { 5, 6, 7, 8 };

		file.update(0, newContent);

		assertArrayEquals("file content not as expected", newContent, file.getContent());
	}

	@Test
	public void testDelete() throws AccessDeniedException{
		// create files to test
		DedicatedFile df = new DedicatedFile(new FileIdentifier(1), new DedicatedFileIdentifier(new byte [] {1,2}));
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 }, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		df.addChild(file);
		
		file.delete();
		
		assertFalse(df.getChildren().contains(file));
	}

	@Test
	public void testGetFileControlInformation() throws AccessDeniedException {
		// create file to test
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 },
				SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		file.setSecStatus(mockedSecurityStatus);
		
		ConstructedTlvDataObject fcp = file.getFileControlParameterDataObject();
		assertTrue(fcp.containsTlvDataObject(new TlvTag((byte) 0x80)));
		assertTrue(fcp.containsTlvDataObject(new TlvTag((byte) 0x88)));
	}
}
