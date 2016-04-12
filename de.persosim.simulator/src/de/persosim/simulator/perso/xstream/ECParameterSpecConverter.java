package de.persosim.simulator.perso.xstream;

import static org.globaltester.logging.PersoSimLogger.ERROR;
import static org.globaltester.logging.PersoSimLogger.log;

import java.math.BigInteger;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class is a converter which is responsible for serializing/deserializing
 * ECParameterSpec objects.
 * 
 * @author jgoeke
 *
 */
public class ECParameterSpecConverter implements Converter {
	
	int h = 0;
	BigInteger n = null;
	EllipticCurve curve = null;
	ECPoint point = null;
	
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		
		return clazz.equals(ECParameterSpec.class);
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter writer,
			MarshallingContext context) {

		ECParameterSpec parameterSpec = (ECParameterSpec) object;

		EllipticCurve curve = parameterSpec.getCurve();
		ECPoint point = parameterSpec.getGenerator();

		context.convertAnother(curve, new CurveConverter());
		context.convertAnother(point, new PointConverter());

		BigInteger n = parameterSpec.getOrder();
		int h = parameterSpec.getCofactor();

		// n element
		writer.startNode("n");
		writer.setValue(n.toString());
		writer.endNode();

		// h element
		writer.startNode("h");
		writer.setValue(String.valueOf(h));
		writer.endNode();
	}

	public void getValuesFromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String nodeName = reader.getNodeName();
			switch(nodeName) {
			case "curve":
				curve = (EllipticCurve) context.convertAnother(reader, EllipticCurve.class, new CurveConverter());
				break;
			case "point":
				point = (ECPoint) context.convertAnother(reader, ECPoint.class, new PointConverter());
				break;
			case "n":
				n = new BigInteger(reader.getValue());
				break;
			case "h":
				h = Integer.parseInt(reader.getValue());
				break;
			}
			
			if(reader.hasMoreChildren()) {
				getValuesFromXML(reader, context);
			}
			reader.moveUp();
		}
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		
		if (reader.getNodeName().toLowerCase().endsWith("ecparameterspec")) {
			getValuesFromXML(reader, context);
		}

		if(point == null || curve == null || n == null) {
			String message = "can not create ParameterSpec object, unmarshal failed!";
			log(getClass(), message, ERROR);
			throw new XStreamException (message);
		}
		return new ECParameterSpec(curve, point, n, h);
	}
}
