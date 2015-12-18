package de.persosim.simulator.perso;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
		List<Byte> currentContext = new LinkedList<>();
		List<Byte> previousContext = new LinkedList<>();
		int contextSize = 128;
		long positionInFile = 0;
		
		do {
			currentReadByte = currentlyMarshalledFileInputStreamReader.read();
			previousReadByte = previousFileInputStreamReader.read();

			currentContext.add((byte) currentReadByte);
			previousContext.add((byte) previousReadByte);
			if (currentContext.size() >= contextSize){
				currentContext.remove(0);
			}
			if (previousContext.size() >= contextSize){
				previousContext.remove(0);
			}
			
			if (currentReadByte != previousReadByte){
				break;
			}
			positionInFile++;
		} while (currentReadByte != -1);
		
		
		
		assertEquals("Found difference at byte " + positionInFile + ", " + contextSize + " bytes context provided.", toString(previousContext), toString(currentContext));

		currentlyMarshalledFileInputStreamReader.close();
		previousFileInputStreamReader.close();

	}

	/**
	 * Converts a list of bytes to a string using UTF-8
	 * @param list
	 * @return
	 */
	private String toString(List<Byte> list) {
		StringBuilder builder = new StringBuilder();
		for (Byte b : list){
			builder.append((char)(byte)b);
		}
		return builder.toString();
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
