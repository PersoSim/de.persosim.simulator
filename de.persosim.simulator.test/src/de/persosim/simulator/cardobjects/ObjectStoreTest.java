package de.persosim.simulator.cardobjects;

import static mockit.Deencapsulation.getField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.test.PersoSimTestCase;
import mockit.Mocked;
import mockit.NonStrictExpectations;

public class ObjectStoreTest extends PersoSimTestCase {
	
	MasterFile masterFile;
	AbstractFile dedicatedFile;
	CardFile elementaryFile1UnderDF;
	CardFile elementaryFile2UnderDF;
	CardFile elementaryFile3UnderDF;
	CardFile elementaryFile4UnderMF;
	CardFile elementaryFile5UnderMF;
	CardFile elementaryFile6UnderMF;
	CardObject authenticationObjectUnderMF;

	byte [] elementaryFile1UnderDFContent;
	byte [] elementaryFile2UnderDFContent;
	byte [] elementaryFile3UnderDFContent;
	byte [] elementaryFile4UnderMFContent;
	byte [] elementaryFile5UnderMFContent;
	byte [] elementaryFile6UnderMFContent;
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
	 *      \      \
	 *       \      -----EF3(011B,4)
	 *        \
	 *         --- EF4(011C,3)
	 *          \
	 *           - AO(1)
	 *            \
	 *             -----EF5(011B,5)
	 *              \
	 *               -----EF6(011B,5)
	 * @throws ReflectiveOperationException 
	 * @throws AccessDeniedException 
	 */
	@Before
	public void setUp() throws ReflectiveOperationException, AccessDeniedException{
				
		new NonStrictExpectations(SecStatus.class) {
			{
				mockedSecurityStatus.checkAccessConditions(Iso7816LifeCycleState.CREATION, (SecCondition) any, (SecContext) any);
				result = true;
			}
		};
		
		//define file contents
		elementaryFile1UnderDFContent = new byte []{1,2,3,4,5,6};
		elementaryFile2UnderDFContent = new byte []{7,8,9,10,11,12};
		elementaryFile3UnderDFContent = new byte []{19,20,21,22,23,24};
		elementaryFile4UnderMFContent = new byte []{13,14,15,16,17,18};
		elementaryFile5UnderMFContent = new byte []{25,26,27,28,29,30};
		elementaryFile6UnderMFContent = new byte []{31,32,33,34,35,36};
		authenticationObjectUnderMFContent = new byte []{1,2,3,4};
		
		// setup fresh file tree in ObjectStore
		masterFile = new MasterFile();
		masterFile.setSecStatus(mockedSecurityStatus);
		objectStore = new ObjectStore(masterFile);
		objectStore.selectMasterFile();
		
		elementaryFile4UnderMF = new ElementaryFile(new FileIdentifier(0x011C), new ShortFileIdentifier(3), elementaryFile4UnderMFContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		masterFile.addChild(elementaryFile4UnderMF);
		authenticationObjectUnderMF = new PasswordAuthObject(new AuthObjectIdentifier(1), authenticationObjectUnderMFContent);
		masterFile.addChild(authenticationObjectUnderMF);
		elementaryFile5UnderMF = new ElementaryFile(new FileIdentifier(0x011B), new ShortFileIdentifier(5), elementaryFile5UnderMFContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		masterFile.addChild(elementaryFile5UnderMF);
		elementaryFile6UnderMF = new ElementaryFile(new FileIdentifier(0x011B), new ShortFileIdentifier(6), elementaryFile6UnderMFContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		masterFile.addChild(elementaryFile6UnderMF);
		dedicatedFile = new DedicatedFile(new FileIdentifier(0x0110), new DedicatedFileIdentifier(new byte [] {0x0A, 0x00, 0x00, 0x01}));
		masterFile.addChild(dedicatedFile);
		elementaryFile1UnderDF = new ElementaryFile(new FileIdentifier(0x011A), new ShortFileIdentifier(1), elementaryFile1UnderDFContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		dedicatedFile.addChild(elementaryFile1UnderDF);
		elementaryFile2UnderDF = new ElementaryFile(new FileIdentifier(0x011B), new ShortFileIdentifier(2), elementaryFile2UnderDFContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		dedicatedFile.addChild(elementaryFile2UnderDF);
		elementaryFile3UnderDF = new ElementaryFile(new FileIdentifier(0x011B), new ShortFileIdentifier(4), elementaryFile3UnderDFContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		dedicatedFile.addChild(elementaryFile3UnderDF);
		
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
		assertTrue(getField(objectStore, "currentFile").equals(elementaryFile4UnderMF));
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
	 * Search for the auth object using the MF scope with EF1 as the last
	 * selected file. An elementary file is expected as result.
	 */
	@Test
	public void testGetObjectFromMF() {
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
	 * Search for EF2 using the DF scope with EF1 as the last selected file. An
	 * elementary file is expected as result.
	 */
	@Test
	public void testGetObjectFromDF() {
		objectStore.selectFileForPersonalization(elementaryFile1UnderDF);
		
		FileIdentifier id = new FileIdentifier(0x011B);
		
		//run mut
		CardObject result = objectStore.getObject(id, Scope.FROM_DF);
		
		//check result
		assertTrue("Returned object does not implement correct interface", result instanceof ElementaryFile);
		assertTrue("File is not equal", elementaryFile2UnderDF.equals(result));
	}
	
	/**
	 * Search for files with id 0x011B using the DF scope with EF1 as the last
	 * selected file. Elementary files are expected as result.
	 */
	@Test
	public void testGetObjectsWithSameIdFromDF() {
		objectStore.selectFileForPersonalization(elementaryFile1UnderDF);
		
		FileIdentifier id = new FileIdentifier(0x011B);
		
		//run mut
		Collection<CardObject> result = objectStore.getObjectsWithSameId(id, Scope.FROM_DF);
		
		assertTrue("Did not find the correct number of objects", result.size() == 2);
		
		//check result
		Iterator<CardObject> iterator = result.iterator();
		CardObject firstFile = iterator.next();
		assertTrue("First element of collection does not implement expected interface", firstFile instanceof ElementaryFile);

		boolean foundEF2 = elementaryFile2UnderDF.equals(firstFile);
		boolean foundEF3 = elementaryFile3UnderDF.equals(firstFile);
		assertTrue("First element matches none of the expected values", foundEF2 || foundEF3);
		
		CardObject secondFile = iterator.next();
		assertTrue("Second element of collection does not implement expected interface", secondFile instanceof ElementaryFile);
		
		if(foundEF2) {
			assertEquals("Second element of collection does noit match expected element", elementaryFile3UnderDF, secondFile);
			
		} else {
			assertEquals("Second element of collection does noit match expected element", elementaryFile2UnderDF, secondFile);
		}

	}
	
	/**
	 * Search for files with id 0x011B using the MF scope with EF1 as the last
	 * selected file. Elementary files are expected as result.
	 */
	@Test
	public void testGetObjectsWithSameIdFromMF() {
		objectStore.selectFileForPersonalization(elementaryFile1UnderDF);
		
		FileIdentifier id = new FileIdentifier(0x011B);
		
		//run mut
		Collection<CardObject> result = objectStore.getObjectsWithSameId(id, Scope.FROM_MF);
		
		assertTrue("Did not find the correct number of objects", result.size() == 2);
		
		//check result
		Iterator<CardObject> iterator = result.iterator();
		CardObject firstFile = iterator.next();
		assertTrue("First element of collection does not implement expected interface", firstFile instanceof ElementaryFile);

		boolean foundEF5 = elementaryFile5UnderMF.equals(firstFile);
		boolean foundEF6 = elementaryFile6UnderMF.equals(firstFile);
		assertTrue("First element matches none of the expected values", foundEF5 || foundEF6);
		
		CardObject secondFile = iterator.next();
		assertTrue("Second element of collection does not implement expected interface", secondFile instanceof ElementaryFile);
		
		if(foundEF5) {
			assertEquals("Second element of collection does noit match expected element", elementaryFile6UnderMF, secondFile);
			
		} else {
			assertEquals("Second element of collection does noit match expected element", elementaryFile5UnderMF, secondFile);
		}
	}
	
	/**
	 * Search for not existing file identifier using the DF scope with EF1 as
	 * the last selected file. A NullCardObject is expected as result.
	 */
	@Test
	public void testGetObjectFromDFWrongId() {
		objectStore.selectFileForPersonalization(elementaryFile1UnderDF);
		
		FileIdentifier id = new FileIdentifier(0x111B);
		
		//run mut
		CardObject result = objectStore.getObject(id, Scope.FROM_DF);
		
		//check result
		assertTrue("Returned object does not implement correct interface", result instanceof NullCardObject);
	}
		
	/**
	 * Search for several objects with not existing file identifier using the DF
	 * scope with EF1 as the last selected file. An empty set is expected as
	 * result.
	 */
	@Test
	public void testGetObjectsWithSameIdFromDFWrongId() {
		objectStore.selectFileForPersonalization(elementaryFile1UnderDF);
		
		FileIdentifier id = new FileIdentifier(0x111B);
		
		//run mut
		Collection<CardObject> result = objectStore.getObjectsWithSameId(id, Scope.FROM_DF);
		
		//check result
		assertTrue("Found objects, but should not have found anything", result.isEmpty());
		
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
