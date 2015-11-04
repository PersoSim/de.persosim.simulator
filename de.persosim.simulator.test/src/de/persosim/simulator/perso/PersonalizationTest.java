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
	 * @return
	 * @throws AccessDeniedException 
	 */
	public abstract Personalization getPerso() throws AccessDeniedException;
	
	
	/**
	 * Positive test test marshalling/unmarshalling the testPerso to/from a temporary file in the filesystem.
	 * @throws Exception
	 */
	@Test
	public void test_MarshallUnmarshall_File() throws Exception {
		
		String xmlFile = getXmlFilename();
		
		// Write to File
		FileWriter file = new FileWriter(xmlFile);
		Personalization perso = getPerso();
		PersonalizationFactory.marshal(perso, file);

		// get variables from our xml file, created before
		Personalization unmarshalledPerso = (Personalization) PersonalizationFactory.unmarshal(xmlFile);
		assertTrue(perso.getClass().equals(unmarshalledPerso.getClass()));
		
		Collection<CommandProcessor> compatibleLayers = PersonalizationHelper.getCompatibleLayers(perso.getLayerList(), CommandProcessor.class);
		for(CommandProcessor commandProcessor: compatibleLayers) {
			//check all CardObjects, their children and all Identifiers of the card object tree
			assertObjectTypes(commandProcessor.getObjectTree());
		}

	}


	protected String getXmlFilename() throws AccessDeniedException {
		return getTmpFolder().getAbsolutePath() + File.separator + getPerso().getClass().getSimpleName() + ".xml";
	}
	/**
	 * Checks whether a given CardObject contains only children of type
	 * {@link CardObject} and identifiers of type {@link CardObjectIdentifier}
	 * <p/>
	 * This check is executed recursively and throws an AssertionError when any check fails.
	 * 
	 * @param objectToCheck
	 */
	private void assertObjectTypes(CardObject objectToCheck) {
		// check identifiers
		for (Object curIdentifier : objectToCheck.getAllIdentifiers()) {
			if (curIdentifier == null) continue; 
			assertTrue("Wrong identifier type (identifier <" + curIdentifier + ">on object <"+ objectToCheck + ">)", curIdentifier instanceof CardObjectIdentifier);
		}
		
		// check children (recursive)
		for (Object curChild : objectToCheck.getChildren()) {
			assertTrue("Wrong child type (child <" + curChild
					+ ">on object <" + objectToCheck + ">)",
					curChild instanceof CardObject);
			assertObjectTypes((CardObject) curChild);
		}
		
	}

}
