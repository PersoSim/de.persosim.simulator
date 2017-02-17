package de.persosim.simulator.perso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.LinkedList;

import org.globaltester.base.utils.Utils;

import de.persosim.simulator.exception.AccessDeniedException;

/**
 * This test compares the {@link Personalization} under test with its artifact
 * included in some other place.
 * 
 * @author mboonk
 *
 */
public abstract class ArtifactPersonalizationTest extends PersonalizationTest {

	@Override
	protected void marshalFile(String xmlFile) throws Exception {
		super.marshalFile(xmlFile);

		int contextSize = 128;
		
		// compare the result with the previous personalization file
		File currentlyMarshalledFile = new File(xmlFile);
		File previousFile = new File(getArtifactXmlFilename());

		LinkedList<Byte> currentContext = new LinkedList<Byte>();
		LinkedList<Byte> previousContext = new LinkedList<Byte>();
		
		assertTrue("File for comparison does not exist: " + previousFile.getAbsolutePath(), previousFile.exists());
		long positionInFile = Utils.checkFilesForDifferences(new FileInputStream(previousFile), new FileInputStream(currentlyMarshalledFile), contextSize, previousContext, currentContext);

		assertEquals("Found difference at byte " + positionInFile + ", " + contextSize + " bytes context provided.", Utils.toString(previousContext), Utils.toString(currentContext));
	}

	/**
	 * @return the file name of the artifact to compare to
	 * @throws AccessDeniedException
	 */
	protected String getArtifactXmlFilename() throws AccessDeniedException {
		return Paths.get("../" + getArtifactFolder() + getArtifactName()).toAbsolutePath().normalize().toString();
	}

	/**
	 * @return the name of the artifact file
	 * @throws AccessDeniedException
	 */
	protected String getArtifactName() throws AccessDeniedException {
		return getPerso().getClass().getSimpleName() + ".perso";
	}

	/**
	 * @return The folder relative to the de.persosim.simulator.test folder
	 *         ending with a {@link File#separator}
	 */
	protected String getArtifactFolder() {
		return de.persosim.simulator.Activator.class.getPackage().getName() + File.separator + "personalization"
				+ File.separator + "profiles" + File.separator;
	}
}
