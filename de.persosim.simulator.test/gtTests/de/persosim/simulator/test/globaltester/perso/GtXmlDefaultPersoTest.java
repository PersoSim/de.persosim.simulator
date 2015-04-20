package de.persosim.simulator.test.globaltester.perso;

import java.io.StringReader;
import java.io.StringWriter;

import de.persosim.simulator.perso.DefaultPersoGt;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;

/**
 * Marshal/unmarshal the {@link DefaultPersonalization} and check it afterwards
 * against the GlobalTester. This ensures that the serialization process does
 * not loose data.
 * 
 * @author amay
 * 
 */
public class GtXmlDefaultPersoTest extends GtDefaultPersoTest {

	@Override
	public Personalization getPersonalization() {

		// marshal the perso to StringWriter
		StringWriter strWriter = new StringWriter();
		PersonalizationFactory.marshal(new DefaultPersoGt(), strWriter);

		// unmarshal the perso from StringReader
		StringReader strReader = new StringReader(strWriter.toString());
		Personalization unmarshalledPerso = PersonalizationFactory.unmarshal(strReader);

		return unmarshalledPerso;
	}
}
