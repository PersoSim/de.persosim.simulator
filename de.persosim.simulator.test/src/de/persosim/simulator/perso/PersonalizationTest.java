package de.persosim.simulator.perso;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

import org.junit.Test;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;
import de.persosim.simulator.test.PersoSimTestCase;

public abstract class PersonalizationTest extends PersoSimTestCase {

	/**
	 * Return the personalization to test.
	 * 
	 * @return
	 * @throws AccessDeniedException
	 */
	public abstract Personalization getPerso() throws AccessDeniedException;

	/**
	 * Positive test test marshalling/unmarshalling the testPerso to/from a
	 * temporary file in the filesystem.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_MarshallUnmarshall_File() throws Exception {

		String xmlFile = getXmlFilename();

		marshalFile(xmlFile);

		// get variables from our xml file, created before
		Personalization unmarshalledPerso = (Personalization) PersonalizationFactory.unmarshal(xmlFile);
		assertTrue(getPerso().getClass().equals(unmarshalledPerso.getClass()));

		Collection<CommandProcessor> compatibleLayers = PersonalizationHelper
				.getCompatibleLayers(getPerso().getLayerList(), CommandProcessor.class);
		for (CommandProcessor commandProcessor : compatibleLayers) {
			// check all CardObjects, their children and all Identifiers of the
			// card object tree
			assertObjectTypes(commandProcessor.getObjectTree());
		}

	}

	/**
	 * This method writes the {@link Personalization} under test into the file
	 * given by the xmlFile parameter.
	 * 
	 * @param xmlFile
	 *            a path to write the {@link Personalization} to
	 * @throws Exception
	 */
	protected void marshalFile(String xmlFile) throws Exception {
		FileWriter file = new FileWriter(xmlFile);
		PersonalizationFactory.marshal(getPerso(), file);
	}

	/**
	 * This returns a destination path for writing the {@link Personalization}
	 * during this test.
	 * 
	 * @return a path to write an xml file to
	 * @throws AccessDeniedException
	 */
	protected String getXmlFilename() throws AccessDeniedException {
		return getTmpFolder().getAbsolutePath() + File.separator + getPerso().getClass().getSimpleName() + ".xml";
	}

	/**
	 * Checks whether a given CardObject contains only children of type
	 * {@link CardObject} and identifiers of type {@link CardObjectIdentifier}
	 * <p/>
	 * This check is executed recursively and throws an AssertionError when any
	 * check fails.
	 * 
	 * @param objectToCheck
	 */
	private void assertObjectTypes(CardObject objectToCheck) {
		// check identifiers
		for (Object curIdentifier : objectToCheck.getAllIdentifiers()) {
			if (curIdentifier == null)
				continue;
			assertTrue("Wrong identifier type (identifier <" + curIdentifier + ">on object <" + objectToCheck + ">)",
					curIdentifier instanceof CardObjectIdentifier);
		}

		// check children (recursive)
		for (Object curChild : objectToCheck.getChildren()) {
			assertTrue("Wrong child type (child <" + curChild + ">on object <" + objectToCheck + ">)",
					curChild instanceof CardObject);
			assertObjectTypes((CardObject) curChild);
		}

	}

}
