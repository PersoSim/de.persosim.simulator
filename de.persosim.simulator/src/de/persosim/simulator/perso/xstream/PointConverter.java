package de.persosim.simulator.perso.xstream;


import static de.persosim.simulator.utils.PersoSimLogger.ERROR;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.math.BigInteger;
import java.security.spec.ECPoint;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * This class is a converter which is responsible for serializing/deserializing
 * point objects.
 * 
 * @author jgoeke
 *
 */
public class PointConverter implements Converter {
	BigInteger x = null;
	BigInteger y = null;

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz.equals(ECPoint.class);
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		
		ECPoint point = (ECPoint) object;
		BigInteger x = point.getAffineX();
		BigInteger y = point.getAffineY();
		
		// open point element
		writer.startNode("point");
			
			// x element
			writer.startNode("x");
			writer.setValue(x.toString());
			writer.endNode();
			
			// y element
			writer.startNode("y");
			writer.setValue(y.toString());
			writer.endNode();
		// close point element
		writer.endNode();

	}
	
	public void getValuesFromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String nodeName = reader.getNodeName();
			switch(nodeName) {
			case "x":
				x = new BigInteger(reader.getValue());
				break;
			case "y":
				y = new BigInteger(reader.getValue());
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
			UnmarshallingContext context) throws NullPointerException {
		
		if (reader.getNodeName().equals("point")) {
			getValuesFromXML (reader, context);
		}
		
		if (x == null || y == null) {
			log(ECParameterSpecConverter.class, "can not create point object, unmarshal failed", ERROR);
			throw new NullPointerException ("can not create point object, unmarshal failed!");
		}
		ECPoint point = new ECPoint(x, y);
		
		return point;
	}

}
