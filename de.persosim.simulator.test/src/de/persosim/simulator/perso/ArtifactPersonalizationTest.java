package de.persosim.simulator.perso;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

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

		// compare the result with the previous personalization file
		File currentlyMarshalledFile = new File(xmlFile);
		File previousFile = new File(getArtifactXmlFilename());

		InputStreamReader currentlyMarshalledFileInputStreamReader = new InputStreamReader(
				new FileInputStream(currentlyMarshalledFile));
		InputStreamReader previousFileInputStreamReader = new InputStreamReader(new FileInputStream(previousFile));

		int currentReadByte = 0;
		int previousReadByte = 0;
		do {
			currentReadByte = currentlyMarshalledFileInputStreamReader.read();
			previousReadByte = previousFileInputStreamReader.read();

			assertEquals(currentReadByte, previousReadByte);
		} while (currentReadByte != -1);

		currentlyMarshalledFileInputStreamReader.close();
		previousFileInputStreamReader.close();

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
		return getPerso().getClass().getSimpleName() + ".xml";
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
