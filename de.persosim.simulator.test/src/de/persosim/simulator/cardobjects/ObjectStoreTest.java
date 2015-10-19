package de.persosim.simulator.cardobjects;

import static mockit.Deencapsulation.getField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.LinkedList;

import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.seccondition.NullSecurityCondition;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;

public class ObjectStoreTest extends PersoSimTestCase {
	
	MasterFile masterFile;
	AbstractFile dedicatedFile;
	CardFile elementaryFile1UnderDF;
	CardFile elementaryFile2UnderDF;
	CardFile elementaryFile3UnderMF;
	CardObject authenticationObjectUnderMF;

	byte [] elementaryFile1UnderDFContent;
	byte [] elementaryFile2UnderDFContent;
	byte [] elementaryFile3UnderMFContent;
	byte [] authenticationObjectUnderMFContent;
	
	@Mocked
	SecStatus mockedSecurityStatus;
	ObjectStore objectStore;
	

	/**
	 * Set up a fresh file tree for each test. 
	 * 
	 * MF ------DF(0110) - EF1(011A,1)
	 *    \      \
	 *     \      ----- EF2(011B,2)
	 *      \
	 *       --- EF3(011C,3)
	 *        \
	 *         - AO(1)
	 * @throws ReflectiveOperationException 
	 * @throws AccessDeniedException 
	 */
	@Before
	public void setUp() throws ReflectiveOperationException, AccessDeniedException{
		
		// define access conditions
		LinkedList<SecCondition> unprotected = new LinkedList<>();
		unprotected.add(new NullSecurityCondition());
		
		//define file contents
		elementaryFile1UnderDFContent = new byte []{1,2,3,4,5,6};
		elementaryFile2UnderDFContent = new byte []{7,8,9,10,11,12};
		elementaryFile3UnderMFContent = new byte []{13,14,15,16,17,18};
		authenticationObjectUnderMFContent = new byte []{1,2,3,4};
		
		// setup fresh file tree in ObjectStore
		masterFile = new MasterFile();
		masterFile.setSecStatus(mockedSecurityStatus);
		objectStore = new ObjectStore(masterFile);
		objectStore.selectMasterFile();
		
		elementaryFile3UnderMF = new ElementaryFile(new FileIdentifier(0x011C), new ShortFileIdentifier(3), elementaryFile3UnderMFContent, unprotected, unprotected, unprotected);
		masterFile.addChild(elementaryFile3UnderMF);
		authenticationObjectUnderMF = new PasswordAuthObject(new AuthObjectIdentifier(1), authenticationObjectUnderMFContent);
		masterFile.addChild(authenticationObjectUnderMF);
		dedicatedFile = new DedicatedFile(new FileIdentifier(0x0110), new DedicatedFileIdentifier(new byte [] {0x0A, 0x00, 0x00, 0x01}));
		masterFile.addChild(dedicatedFile);
		elementaryFile1UnderDF = new ElementaryFile(new FileIdentifier(0x011A), new ShortFileIdentifier(1), elementaryFile1UnderDFContent, unprotected, unprotected, unprotected);
		dedicatedFile.addChild(elementaryFile1UnderDF);
		elementaryFile2UnderDF = new ElementaryFile(new FileIdentifier(0x011B), new ShortFileIdentifier(2), elementaryFile2UnderDFContent, unprotected, unprotected, unprotected);
		dedicatedFile.addChild(elementaryFile2UnderDF);
		
	}
	
	/**
	 * Select a file from MF.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testSelectFileFromMF() throws FileNotFoundException{
		//construct test data
		FileIdentifier id = new FileIdentifier(0x011C);
		
		//run mut
		objectStore.selectFile(id, Scope.FROM_MF);
		
		// make sure currentFile has been set
		assertTrue(getField(objectStore, "currentFile").equals(elementaryFile3UnderMF));
	}
	
	/**
	 * Select a file from DF.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testSelectFileFromDF() throws FileNotFoundException{
		objectStore.selectFileForPersonalization(dedicatedFile);

		//construct test data
		FileIdentifier id = new FileIdentifier(0x011A);
		//run mut
		objectStore.selectFile(id, Scope.FROM_DF);
		
		// make sure currentFile has been set
		assertTrue(getField(objectStore, "currentFile").equals(elementaryFile1UnderDF));
	}
	
	/**
	 * Search for the auth object using the MF scope with EF1 as the last selected file. An elementary file is expected as result.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testGetObjectFromMF() throws FileNotFoundException{
		objectStore.selectFileForPersonalization(elementaryFile1UnderDF);
		
		//select file in different subtree
		AuthObjectIdentifier objectId = new AuthObjectIdentifier(1);
		
		//run mut
		CardObject result = objectStore.getObject(objectId, Scope.FROM_MF);
		
		//check result
		assertTrue("Returned object does not implement correct interface", result instanceof PasswordAuthObject);
		assertTrue("File is not equal", authenticationObjectUnderMF.equals(result));
	}
	
	/**
	 * Search for EF2 using the DF scope with EF1 as the last selected file. An elementary file is expected as result.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testGetObjectFromDF() throws FileNotFoundException{		//construct test data
		objectStore.selectFileForPersonalization(elementaryFile1UnderDF);
		
		FileIdentifier id = new FileIdentifier(0x011B);
		
		//run mut
		CardObject result = objectStore.getObject(id, Scope.FROM_DF);
		
		//check result
		assertTrue("Returned object does not implement correct interface", result instanceof ElementaryFile);
		assertTrue("File is not equal", elementaryFile2UnderDF.equals(result));
	}
	
	@Test
	public void testSelectCachedFile() throws FileNotFoundException{
		FileIdentifier id = new FileIdentifier(0x011C);
		Object result = objectStore.getObject(id, Scope.FROM_MF);
		
		//run mut
		objectStore.selectCachedFile();
		
		assertEquals(result, getField(objectStore, "currentFile"));
	}
}
