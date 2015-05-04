package de.persosim.simulator.perso.xstream;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class is a converter which is responsible for serializing/desreializing ECParameterSpec objects.
 * 
 * @author jge
 *
 */
public class ECParameterSpecConverter implements Converter {

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
		
		BigInteger a = curve.getA();
		BigInteger b = curve.getB();

		BigInteger n = parameterSpec.getOrder();
		
		BigInteger p =((ECFieldFp) curve.getField()).getP();
		
		BigInteger x = point.getAffineX();
		BigInteger y = point.getAffineY();
		
		int h = parameterSpec.getCofactor();
		
		writer.startNode("curve");
		writer.startNode("field");
		writer.startNode("p");
		writer.setValue(p.toString());
		writer.endNode();
		writer.endNode();
		
		writer.startNode("a");
		writer.setValue(a.toString());
		writer.endNode();
		
		writer.startNode("b");
		writer.setValue(b.toString());
		writer.endNode();
		
		writer.endNode();
		
		writer.startNode("point");
		
		writer.startNode("x");
		writer.setValue(x.toString());
		writer.endNode();
		
		writer.startNode("y");
		writer.setValue(y.toString());
		writer.endNode();
		
		writer.endNode();
		
		writer.startNode("n");
		writer.setValue(n.toString());
		writer.endNode();
		
		writer.startNode("h");
		writer.setValue(String.valueOf(h));
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		BigInteger p = null;
		BigInteger a = null;
		BigInteger b = null;
		BigInteger x = null;
		BigInteger y = null;
		BigInteger n = null;
		int h = 0;
		
		while(reader.hasMoreChildren()) {
			
			reader.moveDown();
			reader.moveDown();
			reader.moveDown();
			p = new BigInteger(reader.getValue());
			
			reader.moveUp();
			reader.moveUp();
			reader.moveDown();
			a = new BigInteger(reader.getValue());
			
			reader.moveUp();
			reader.moveDown();
			b = new BigInteger(reader.getValue());
			
			reader.moveUp();
			reader.moveUp();
			reader.moveDown();
			reader.moveDown();
			x = new BigInteger(reader.getValue());
			
			reader.moveUp();
			reader.moveDown();
			y = new BigInteger(reader.getValue());
				
			reader.moveUp();
			reader.moveUp();
			reader.moveDown();
			n = new BigInteger(reader.getValue());
			
			reader.moveUp();
			reader.moveDown();
			h = Integer.parseInt(reader.getValue());
			
			reader.moveUp();
		}
		
		ECField field = new ECFieldFp(p);
		EllipticCurve curve = new EllipticCurve(field, a, b);
		
		ECPoint point = new ECPoint(x, y);
		
		ECParameterSpec parameterSpec = new ECParameterSpec(curve, point, n, h);
		
		return parameterSpec;
	}

}
