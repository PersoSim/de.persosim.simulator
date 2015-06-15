package de.persosim.simulator.perso;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.test.PersoSimTestCase;

public abstract class PersonalizationTest extends PersoSimTestCase {

	/**
	 * Return the personalization to test.
	 * @return
	 */
	public abstract Personalization getPerso();
	
	
	/**
	 * Positive test test marshalling/unmarshalling the testPerso to/from a temporary file in the filesystem.
	 * @throws Exception
	 */
	@Test
	public void test_MarshallUnmarshall_File() throws Exception {
		
		String xmlFile = getXmlFilename();
		
		// Write to File
		FileWriter file = new FileWriter(xmlFile);
		PersonalizationFactory.marshal(getPerso(), file);

		// get variables from our xml file, created before
		Personalization unmarshalledPerso = (Personalization) PersonalizationFactory.unmarshal(xmlFile);
		assertNotNull(unmarshalledPerso);
		
		//check all CardObjects, their children and all Identifiers of the card object tree
		assertObjectTypes(unmarshalledPerso.getObjectTree());

	}

	protected String getXmlFilename() {
		File personalizationFolder = new File(personalizationPath);
		if (!personalizationFolder.isDirectory())
			personalizationFolder.mkdirs();
		String retVal = personalizationPath + "/" + getPerso().getClass().getSimpleName() + ".xml"; 
		return retVal;
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
