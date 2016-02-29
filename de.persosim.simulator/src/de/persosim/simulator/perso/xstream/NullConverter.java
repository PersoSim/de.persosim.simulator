package de.persosim.simulator.perso.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.persosim.simulator.perso.PersonalizationFactory;

/**
 * Intentionally empty converter. This class can convert nothing but providing
 * it as Converter via OSGi service will make its classloader available to
 * the CompositeClassloader used in PersoSim {@link PersonalizationFactory}
 * 
 * @author amay
 *
 */
public abstract class NullConverter implements Converter {
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) {
		return false; //we convert nothing
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {
		// intentionally do nothing
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
		// intentionally do nothing
		return null;
	}
	
}
