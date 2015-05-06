package de.persosim.simulator.perso.xstream;

import static de.persosim.simulator.utils.PersoSimLogger.ERROR;
import static de.persosim.simulator.utils.PersoSimLogger.log;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class CurveConverter implements Converter {
	BigInteger p = null;
	BigInteger a = null;
	BigInteger b = null;

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		// TODO Auto-generated method stub
		return clazz.equals(EllipticCurve.class);
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		
		EllipticCurve curve = (EllipticCurve) object;
		
		BigInteger a = curve.getA();
		BigInteger b = curve.getB();
		BigInteger p = ((ECFieldFp) curve.getField()).getP();
		
		// open curve element
		writer.startNode("curve");
			// field element
			writer.startNode("field");
				// p element
				writer.startNode("p");
				writer.setValue(p.toString());
				writer.endNode();
			writer.endNode();
			
			// a element
			writer.startNode("a");
			writer.setValue(a.toString());
			writer.endNode();
			
			// b element
			writer.startNode("b");
			writer.setValue(b.toString());
			writer.endNode();
		// close curve element
		writer.endNode();
	}
	
	public void getValuesFromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String nodeName = reader.getNodeName();
			switch(nodeName) {
			case "p":
				p = new BigInteger(reader.getValue());
				break;
			case "a":
				a = new BigInteger(reader.getValue());
				break;
			case "b":
				b = new BigInteger(reader.getValue());
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
		
		if (reader.getNodeName().equals("curve")) {
			getValuesFromXML (reader, context);
		}
		
		if (p == null || a == null || b == null) {
			log(CurveConverter.class, "can not create curve object, unmarshal failed", ERROR);
			throw new NullPointerException ("can not create curve object, unmarshal failed!");
		}
		ECField field = new ECFieldFp(p);
		EllipticCurve curve = new EllipticCurve(field, a, b);
		
		return curve;
	}

}
