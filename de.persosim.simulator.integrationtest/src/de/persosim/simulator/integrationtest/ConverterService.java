package de.persosim.simulator.integrationtest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.hjp.protocol.sample.HjpPaceProtocol;
import com.thoughtworks.xstream.converters.Converter;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.perso.DefaultPersoGt;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;

public class ConverterService {
	
	private static CountDownLatch dependencyLatch = new CountDownLatch(1);
	static Converter service = null;
	
	public void onServiceUp(Converter converter) {
		service = converter;
	    dependencyLatch.countDown();
	}
	
	@Before
	public void dependencyCheck() {
	  // Wait for OSGi dependencies
	    try {
	      dependencyLatch.await(10, TimeUnit.SECONDS); 
	      // Dependencies fulfilled
	    } catch (InterruptedException ex)  {
	      fail("OSGi dependencies unfulfilled");
	    }
	}
	
	@Test
	public void testMarshalling() throws IOException{

		File temp = new File ("asdf.xml");
		
		// Write to File
		FileWriter file = new FileWriter(temp);
		DefaultPersoGt perso = new DefaultPersoGt();
//		perso.getProtocolList().clear();
		perso.getProtocolList().add(new HjpPaceProtocol());
		PersonalizationFactory.marshal(perso, file);
		

		// get variables from our xml file, created before
		Personalization unmarshalledPerso = (Personalization) PersonalizationFactory.unmarshal(new FileReader(temp));
		assertNotNull(unmarshalledPerso);
		
		//check all CardObjects, their children and all Identifiers of the card object tree
		assertObjectTypes(unmarshalledPerso.getObjectTree());
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
