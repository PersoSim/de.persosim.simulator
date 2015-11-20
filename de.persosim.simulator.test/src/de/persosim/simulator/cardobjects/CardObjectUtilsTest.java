package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.exception.ProcessingException;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.test.PersoSimTestCase;

public class CardObjectUtilsTest extends PersoSimTestCase {
	
	MasterFile masterFile;
	ElementaryFile ef1, ef2;
	FileIdentifier fid1, fid2;
	ShortFileIdentifier commonSfi, unusedSfi;
	
	@Before
	public void setUp() throws Exception{
		fid1 = new FileIdentifier(0x0101);
		fid2 = new FileIdentifier(0x0102);
		commonSfi = new ShortFileIdentifier(7);
		unusedSfi = new ShortFileIdentifier(8);
		
		masterFile = new MasterFile();
		
		byte[] elementaryFileContent = new byte[] { 1, 2, 3, 4, 5, 6 };
		ef1 = new ElementaryFile(fid1, commonSfi, elementaryFileContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		masterFile.addChild(ef1);
		ef2 = new ElementaryFile(fid2, commonSfi, elementaryFileContent, SecCondition.ALLOWED, SecCondition.ALLOWED, SecCondition.ALLOWED);
		masterFile.addChild(ef2);
	}
	
	/**
	 * Positive test: check that the combination of OID and id-able object returns the correct object.
	 */
	@Test
	public void testGetSpecificChild_MatchingSingleElement() {
		//call mut
		CardObject result = CardObjectUtils.getSpecificChild(masterFile, fid1);
		
		//check result
		assertEquals(ef1, result);
	}
	
	/**
	 * Negative test: check that the combination of OID and id-able object is not allowed.
	 */
	@Test(expected = ProcessingException.class)
	public void testGetSpecificChild_NonMatching_SecondaryIdentifier() {
		CardObjectUtils.getSpecificChild(masterFile, fid1, unusedSfi);
	}
	
	/**
	 * Negative test: check that an {@link ProcessingException} is thrown when the selection is  ambiguous.
	 */
	@Test(expected = ProcessingException.class)
	public void testGetImplicitId_Ambiguous() {
		CardObjectUtils.getSpecificChild(masterFile, commonSfi);
	}
	
}
