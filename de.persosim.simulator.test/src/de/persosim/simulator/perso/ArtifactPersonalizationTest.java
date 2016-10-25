package de.persosim.simulator.perso;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

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
		
		long positionInFile = checkFilesForDifferences(previousFile, currentlyMarshalledFile, contextSize, previousContext, currentContext);

		assertEquals("Found difference at byte " + positionInFile + ", " + contextSize + " bytes context provided.", toString(previousContext), toString(currentContext));
	}
	
	/**
	 * Method for checking files for differences at byte level.
	 * @param expected the {@link File} which contains the expected content
	 * @param fileToCheck the {@link File} which should be checked for differences
	 * @param contextSize the number of bytes to store as context before differences
	 * @param expectedContext context buffer for the expected file
	 * @param fileToCheckContext context buffer for the file to be checked
	 * @return the position of the first differing byte or -1 if no differences found
	 * @throws IOException
	 */
	public static long checkFilesForDifferences(File expected, File fileToCheck, int contextSize, LinkedList<Byte> expectedContext,
			LinkedList<Byte> fileToCheckContext) throws IOException {
		try (InputStreamReader currentlyMarshalledFileInputStreamReader = new InputStreamReader(
				new FileInputStream(fileToCheck));
				InputStreamReader previousFileInputStreamReader = new InputStreamReader(
						new FileInputStream(expected));) {

			int fileToCheckReadByte = 0;
			int expectedReadByte = 0;
			long positionInFile = 0;

			do {
				fileToCheckReadByte = currentlyMarshalledFileInputStreamReader.read();
				expectedReadByte = previousFileInputStreamReader.read();

				fileToCheckContext.add((byte) fileToCheckReadByte);
				expectedContext.add((byte) expectedReadByte);
				if (fileToCheckContext.size() >= contextSize) {
					fileToCheckContext.remove(0);
				}
				if (expectedContext.size() >= contextSize) {
					expectedContext.remove(0);
				}

				if (fileToCheckReadByte != expectedReadByte) {
					return positionInFile;
				}
				positionInFile++;
			} while (fileToCheckReadByte != -1);

		}

		return -1;
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
