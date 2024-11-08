package de.persosim.simulator.platform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.DefaultPersoTestPki;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class PersonalizationHelperTest extends PersoSimTestCase {

	Personalization perso;

	FileIdentifier fid1;
	ShortFileIdentifier sfid1;
	byte[] content1;
	FileIdentifier fid2;
	ShortFileIdentifier sfid2;
	byte[] content2;
	
	DedicatedFileIdentifier df;

	/**
	 * Create the test environment containing the elementary files and content.
	 * 
	 * @throws AccessDeniedException
	 */
	@Before
	public void setUp() throws AccessDeniedException {

		perso = new DefaultPersoTestPki();

		MasterFile mf = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class)
				.getObjectTree();

		fid1 = new FileIdentifier(0x0110);
		sfid1 = new ShortFileIdentifier(0x10);
		content1 = new byte[] { 1, 2, 3 };
		fid2 = new FileIdentifier(0x010F);
		sfid2 = new ShortFileIdentifier(0x0F);
		content2 = new byte[] { 4, 5, 6 };
		ElementaryFile file = new ElementaryFile(fid1, sfid1, content1);
		mf.addChild(file);
		
		ElementaryFile file2 = new ElementaryFile(fid2, sfid2, content2);
		df = new DedicatedFileIdentifier(HexString.toByteArray("A0 00 00 02 47 10 01"));
		Collection<CardObject> parentCandidates = mf.findChildren(df);
		((DedicatedFile)parentCandidates.iterator().next()).addChild(file2);
	}

	/**
	 * Test if getFileFromPerso returns the correct file content.
	 * 
	 * @throws AccessDeniedException
	 */
	@Test
	public void testGetFileFromPerso() throws AccessDeniedException {
		byte[] foundFile = PersonalizationHelper.getFileFromPerso(perso, 272, null);
		assertArrayEquals("Content of found file does not match expected value", foundFile, content1);
	}
	
	/**
	 * Test if getFileFromPerso returns the correct file content from ePass application.
	 * 
	 * @throws AccessDeniedException
	 */
	@Test
	public void testGetFileFromPersoFromEpass() throws AccessDeniedException {
		byte[] foundFile = PersonalizationHelper.getFileFromPerso(perso, 271, df);
		assertArrayEquals("Content of found file does not match expected value", foundFile, content2);
	}

	/**
	 * Use getFileFromPerso with file identifier not existing in given perso.
	 * 
	 * @throws AccessDeniedException
	 */
	@Test
	public void testGetFileFromPersoWrongFid() throws AccessDeniedException {
		byte[] foundFile = PersonalizationHelper.getFileFromPerso(perso, 273, null);
		assertEquals(null, foundFile);
	}

}
