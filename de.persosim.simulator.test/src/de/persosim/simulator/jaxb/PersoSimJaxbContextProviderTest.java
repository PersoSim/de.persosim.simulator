package de.persosim.simulator.jaxb;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.junit.BeforeClass;
import org.junit.Test;

import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.jaxb.PersoSimJaxbContextProvider;
import de.persosim.simulator.perso.XmlPersonalisation;
import de.persosim.simulator.protocols.ca.CaProtocol;
import de.persosim.simulator.protocols.file.FileProtocol;
import de.persosim.simulator.protocols.pace.PaceProtocol;
import de.persosim.simulator.protocols.ta.TaProtocol;

public class PersoSimJaxbContextProviderTest {

	static Collection<Class<?>> classes;

	@BeforeClass
	public static void setUp() {
		classes = PersoSimJaxbContextProvider.getJaxbClasses();
	}

	/**
	 * Check that all returned classes do contain JAXB annotations
	 */
	@Test
	public void testGetJaxbClasses_AllContainedAreAnnotated() {
		for (Class<?> curClass : classes) {
			List<Class<? extends Annotation>> annotationTypes = new ArrayList<>();
			for (Annotation curAnnotation : curClass.getAnnotations()) {
				annotationTypes.add(curAnnotation.annotationType());
			}

			if (annotationTypes.contains(XmlRootElement.class)) continue;
			if (annotationTypes.contains(XmlTransient.class)) continue;
			fail("Class "+ curClass.getCanonicalName() + " does not contain any JAXB annotation");
		}
	}

	/**
	 * Check that some known classes are actually found by the algorithm
	 */
	@Test
	public void testGetJaxbClasses_ContainesKnownClasses() {
		assertTrue(classes.contains(FileIdentifier.class));
		assertTrue(classes.contains(ElementaryFile.class));
		assertTrue(classes.contains(MasterFile.class));
		assertTrue(classes.contains(XmlPersonalisation.class));

		assertTrue(classes.contains(FileProtocol.class));
		assertTrue(classes.contains(PaceProtocol.class));
		assertTrue(classes.contains(TaProtocol.class));
		assertTrue(classes.contains(CaProtocol.class));
	}

}
