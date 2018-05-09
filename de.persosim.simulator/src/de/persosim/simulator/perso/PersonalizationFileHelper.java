package de.persosim.simulator.perso;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.globaltester.logging.BasicLogger;

/**
 * This class provides methods that simplify acces to files stored on disk and required to generate default personalization objects
 * 
 * @author amay
 *
 */
public class PersonalizationFileHelper {
	
	/**
	 * This method retrieves the file that can be identified by the given parameters on the local file system.
	 * @param repoName the name of the repository
	 * @param projectName the name of the project
	 * @param fileName the path of the file relative to the project
	 * @return the identified file
	 */
	public static File getFileFromPseudoBundle(String repoName, String projectName, String fileName) {
		String absoluteRootPath = (new File("")).getAbsolutePath();
		
		int lastLineSeparatorIndex = absoluteRootPath.lastIndexOf(File.separator);
		absoluteRootPath = absoluteRootPath.substring(0, lastLineSeparatorIndex);
		lastLineSeparatorIndex = absoluteRootPath.lastIndexOf(File.separator);
		absoluteRootPath = absoluteRootPath.substring(0, lastLineSeparatorIndex);
		
		String relativePathToFile = (new File(fileName)).getPath();
		String absolutePathToFile = absoluteRootPath + File.separator + repoName + File.separator + projectName + File.separator + relativePathToFile;
		return new File(absolutePathToFile);
	}

	/**
	 * This method returns the contents of the provided file as byte array.
	 * Null is returned iff file can not be found or is longer than Integer.MAX_VALUE. 
	 * @param fileName the file to be read
	 * @return the contents of the file to be read
	 */
	public static byte[] readFromFile(String fileName) {
		try {
			return Files.readAllBytes(Paths.get(fileName));
		} catch (IOException e) {
			BasicLogger.logException(PersonalizationFileHelper.class, e);
			return null;
		}
	}
}
