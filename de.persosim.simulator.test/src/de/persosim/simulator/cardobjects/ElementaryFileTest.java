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
		
		file.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);

		file.update(0, newContent);

		assertArrayEquals("file content not as expected", newContent, file.getContent());
	}
	
	/**
	 * Negative test: Try to read when no SecConditions are set.
	 */
	@Test (expected = AccessDeniedException.class)
	public void testReadWithoutSecConditions() throws AccessDeniedException {
		// create file to test
		byte[] content = new byte[] { 1, 2, 3, 4 };
		
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), content);
		file.setSecStatus(mockedSecurityStatus);		
		file.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		file.getContent();
	}

	/**
	 * Positive test:  Create Elementary Files without SecConditions and add them for reading and writing later
	 */
	@Test
	public void testConstructorWithoutSecConditions() throws AccessDeniedException {
		// create file to test
		byte[] content = new byte[] { 1, 2, 3, 4 };
		byte[] newContent = new byte[] { 5, 6, 7, 8 };
		
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), content);
		file.setSecStatus(mockedSecurityStatus);
		file.setReadingConditions(SecCondition.ALLOWED);
		file.setWritingConditions(SecCondition.ALLOWED);
		
		file.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		assertArrayEquals("file content not as expected", content, file.getContent());
		
		file.update(0, newContent);
		
		assertArrayEquals("file content not as expected", newContent, file.getContent());
	}
	
	@Test
	public void testSetReadingConditions() throws Exception {
		// create file to test
		byte[] content = new byte[] { 1, 2, 3, 4 };
		
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), content);
		file.setSecStatus(mockedSecurityStatus);
		
		file.setReadingConditions(SecCondition.ALLOWED);
		
		file.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		file.getContent();
	}
	
	@Test
	public void testSetWritingConditions() throws Exception {
		// create file to test
		byte[] content = new byte[] { 1, 2, 3, 4 };
		
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), content);
		file.setSecStatus(mockedSecurityStatus);
		
		file.setWritingConditions(SecCondition.ALLOWED);
		
		file.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		file.update(0, content);
	}
	
	@Test
	public void testSetErasingConditions() throws Exception {
		// create file to test
		byte[] content = new byte[] { 1, 2, 3, 4 };
		
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), content);
		file.setSecStatus(mockedSecurityStatus);
		
		file.setErasingConditions(SecCondition.ALLOWED);
		
		file.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		file.erase();
	}
	
	@Test
	public void testSetDeletionConditions() throws Exception {
		// create file to test
		byte[] content = new byte[] { 1, 2, 3, 4 };
		
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), content);
		file.setSecStatus(mockedSecurityStatus);
		
		file.setDeletionConditions(SecCondition.ALLOWED);
		
		MasterFile mf = new MasterFile();
		mf.addChild(file);
		
		file.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		
		file.delete();
	}


	/**
	 * Positive test: delete a file from the object tree.
	 */
	@Test
	public void testDelete() throws AccessDeniedException{
		// create files to test
		DedicatedFile df = new DedicatedFile(new FileIdentifier(1), new DedicatedFileIdentifier(new byte [] {1,2}));
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 }, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		df.addChild(file);
		
		file.delete();
		
		assertFalse(df.getChildren().contains(file));
	}

	/**
	 * Positive test: erase a files contents.
	 */
	@Test
	public void testErase() throws AccessDeniedException{
		// create files to test
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 }, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		file.erase();
		
		assertArrayEquals(new byte [] {0,0,0,0}, file.getContent());
	}

	/**
	 * Negative test: try erasing with a negative starting offset.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEraseWithNegativeOffset() throws AccessDeniedException{
		// create files to test
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 }, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		file.erase(-2);
	}

	/**
	 * Positive test: erase a file using a starting offset.
	 */
	@Test
	public void testEraseWithOffset() throws AccessDeniedException{
		// create files to test
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 }, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		file.erase(2);
		
		assertArrayEquals(new byte [] {1,2,0,0}, file.getContent());
	}

	/**
	 * Positive test: erase file contents selectively using starting and ending offsets.
	 */
	@Test
	public void testEraseWithBothOffsets() throws AccessDeniedException{
		// create files to test
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 }, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		file.erase(1,3);
		
		assertArrayEquals(new byte [] {1,0,0,4}, file.getContent());
	}

	/**
	 * Negative test: Try erasing a file with an starting offset higher than the the ending offset.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEraseWithFirstOffsetHigher() throws AccessDeniedException{
		// create files to test
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 }, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		file.erase(2,1);
	}

	/**
	 * Negative test: Try erasing a file with an ending offset that is to high for the file. 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEraseWithBadSecondOffset() throws AccessDeniedException{
		// create files to test
		ElementaryFile file = new ElementaryFile(new FileIdentifier(0), new ShortFileIdentifier(1), new byte[] { 1, 2, 3, 4 }, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		file.erase(2,5);
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
