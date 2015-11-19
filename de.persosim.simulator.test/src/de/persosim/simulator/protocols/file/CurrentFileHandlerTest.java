package de.persosim.simulator.protocols.file;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.secstatus.SecStatusMechanismUpdatePropagation;
import de.persosim.simulator.test.PersoSimTestCase;

/**
 * Unit tests for the file management protocol.
 * 
 * @author mboonk
 * 
 */
public class CurrentFileHandlerTest extends PersoSimTestCase {

	CardStateAccessor cardStateAccessor;
	SecStatus secStatus;
	MasterFile masterFile;
	ElementaryFile ef1, ef2;
	DedicatedFile df;

	/**
	 * Create the test environment containing an elementary file and the mocked
	 * object store.
	 * @throws ReflectiveOperationException 
	 * @throws AccessDeniedException 
	 */
	@Before
	public void setUp() throws ReflectiveOperationException, AccessDeniedException {
		secStatus = new SecStatus();

		masterFile = new MasterFile();
		ef1 = new ElementaryFile(new FileIdentifier(0x0101), new byte[] { 0x01, 0x01 }, null, null, null, null);
		ef2 = new ElementaryFile(new FileIdentifier(0x0102), new byte[] { 0x01, 0x02 }, null, null, null, null);
		df = new DedicatedFile(new FileIdentifier(0x0200), null, null);

		cardStateAccessor = new CardStateAccessor(){
			@Override
			public MasterFile getMasterFile() {
				return masterFile;
			}

			@Override
			public Collection<SecMechanism> getCurrentMechanisms(SecContext context,
					Collection<Class<? extends SecMechanism>> wantedMechanisms) {
				return secStatus.getCurrentMechanisms(context, wantedMechanisms);
			}
		};
	}

	/**
	 * Test method getCurrentFile if the SecStatus does not contain a CurrentFileSecMechanism 
	 */
	@Test
	public void testGetCurrentFile_noCurrentFileSecMechanism() {
		// call mut
		CardObject result = CurrentFileHandler.getCurrentFile(cardStateAccessor);
		

		// check results
		assertEquals(masterFile, result);
	}

	/**
	 * Test method getCurrentFile if the SecStatus does contain a CurrentFileSecMechanism referring to the MasterFile
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCurrentFile_masterFileInSecMechanism() throws FileNotFoundException {
		//prepare test data
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(masterFile)));

		// call mut
		CardObject result = CurrentFileHandler.getCurrentFile(cardStateAccessor);

		// check results
		assertEquals(masterFile, result);
	}

	/**
	 * Test method getCurrentFile if the SecStatus does contain a CurrentFileSecMechanism referring to an ElementaryFile
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCurrentFile_elementaryFileInSecMechanism() throws Exception {
		//prepare test data
		masterFile.addChild(ef1);
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(ef1)));

		// call mut
		CardObject result = CurrentFileHandler.getCurrentFile(cardStateAccessor);

		// check results
		assertEquals(ef1, result);
	}

	/**
	 * Test method getCurrentFile if the SecStatus does contain a CurrentFileSecMechanism referring to an ElementaryFile that is not part of the object tree
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCurrentFile_elementaryFileInSecMechanismButNotInTree() throws Exception {
		//prepare test data
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(ef1)));

		// call mut
		CardObject result = CurrentFileHandler.getCurrentFile(cardStateAccessor);

		// check results
		assertEquals(masterFile, result);
	}

	/**
	 * Test method getCurrentFile if the SecStatus does contain a CurrentFileSecMechanism referring to a DedicatedFile
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCurrentDedicatedFile_dedicatedFileInSecMechanism() throws Exception {
		//prepare test data
		masterFile.addChild(df);
		df.addChild(ef1);
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(df)));

		// call mut
		CardObject result = CurrentFileHandler.getCurrentDedicatedFile(cardStateAccessor);

		// check results
		assertEquals(df, result);
	}

	/**
	 * Test method getCurrentFile if the SecStatus does contain a CurrentFileSecMechanism referring to an ElementaryFile in a DedicatedFile
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCurrentDedicatedFile_elementaryFileInSecMechanism() throws Exception {
		//prepare test data
		masterFile.addChild(df);
		df.addChild(ef1);
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(ef1)));

		// call mut
		CardObject result = CurrentFileHandler.getCurrentDedicatedFile(cardStateAccessor);

		// check results
		assertEquals(df, result);
	}

	/**
	 * Test method getCurrentFile if the SecStatus does contain a CurrentFileSecMechanism referring to an ElementaryFile within MasterFile next to a DedicatedFile
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCurrentDedicatedFile_elementaryNextToDfInSecMechanism() throws Exception {
		//prepare test data
		masterFile.addChild(df);
		masterFile.addChild(ef1);
		secStatus.updateMechanisms(
				new SecStatusMechanismUpdatePropagation(SecContext.GLOBAL, new CurrentFileSecMechanism(ef1)));

		// call mut
		CardObject result = CurrentFileHandler.getCurrentDedicatedFile(cardStateAccessor);

		// check results
		assertEquals(masterFile, result);
	}
	
}
