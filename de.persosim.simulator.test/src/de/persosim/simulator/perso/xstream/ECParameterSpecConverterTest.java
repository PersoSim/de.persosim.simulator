package de.persosim.simulator.perso.xstream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.converters.ConversionException;

import de.persosim.simulator.perso.PersonalizationFactory;
import de.persosim.simulator.test.PersoSimTestCase;

public class ECParameterSpecConverterTest extends PersoSimTestCase {
	ECField field;
	EllipticCurve curve;
	CurveConverter curveConverter;
	ECParameterSpec spec;
	ECPoint point;
	BigInteger n;
	int h;
	
	@Before
	public void setUp() {
		
		curveConverter = new CurveConverter();
		
		field = new ECFieldFp(new BigInteger("76884956397045344220809746629001649093037950200943055203735601445031516197751"));
		curve = new EllipticCurve(field, new BigInteger("56698187605326110043627228396178346077120614539475214109386828188763884139993"), 
				new BigInteger("17577232497321838841075697789794520262950426058923084567046852300633325438902"));
		
		point = new ECPoint(new BigInteger("63243729749562333355292243550312970334778175571054726587095381623627144114786"), 
				new BigInteger("38218615093753523893122277964030810387585405539772602581557831887485717997975"));
		
		n = new BigInteger("76884956397045344220809746629001649092737531784414529538755519063063536359079");
		
		h = 1;
		
		spec = new ECParameterSpec(curve, point, n, h);
	}
	
	/**
	 * Marshals a ECParameterSpec object unmarshals it and marshals it again.
	 * 
	 * All attributes must be equal.
	 */
	@Test
	public void testECParameterSpecConverter_MarshalUnmarshal_ECParameterSpec() {
		
		StringWriter writer = new StringWriter();
		PersonalizationFactory.marshal(spec, writer);
		
		ECParameterSpec spec_unmarshal = (ECParameterSpec) PersonalizationFactory.unmarshal(new StringReader(writer.toString()));
		
		assertEquals("unmarshalled curve", spec.getCurve(), spec_unmarshal.getCurve());
		assertEquals("unmarshalled G", spec.getGenerator(), spec_unmarshal.getGenerator());
		assertEquals("unmarshalled order", spec.getOrder(), spec_unmarshal.getOrder());
		assertEquals("unmarshalled cofactor", spec.getCofactor(), spec_unmarshal.getCofactor());
	}
	
	/**
	 * Marshals a ECParameterSpec object unmarshals it and marshals it again.
	 * 
	 * All unmarshalled object content (from both objects) must be identical.
	 */
	@Test
	public void testECParameterSpecConverter_MarshalUnmarshalMarshal_ECParameterSpec() {
		
		StringWriter writer = new StringWriter();
		PersonalizationFactory.marshal(spec, writer);
		
		ECParameterSpec specUnmarshal = (ECParameterSpec) PersonalizationFactory.unmarshal(new StringReader(writer.toString()));
		
		StringWriter writer2 = new StringWriter();
		PersonalizationFactory.marshal(specUnmarshal, writer2);
		
		assertEquals("ECParameterSpecXml", writer.toString(), writer2.toString());
	}
	
	/**
	 * Try to to unmarshal ECParameterSpec, but the point object inside the curve is null.
	 * 
	 * @throws ConversionException 
	 */
	@Test(expected = ConversionException.class)
	public void testECParameterSpecConverter_UnmarshalECParameterSpec_PointIsNull() throws ConversionException{
		
		spec = new ECParameterSpec(curve, point, n, h);
		
		StringWriter writer = new StringWriter();
		PersonalizationFactory.marshal(spec, writer);
		
		String xmlParamSpec = writer.toString();
		xmlParamSpec = xmlParamSpec.replace("<x>63243729749562333355292243550312970334778175571054726587095381623627144114786</x>", "");
		
		assertNull(PersonalizationFactory.unmarshal(new StringReader(xmlParamSpec)));
	}
	
	/**
	 * Try to to unmarshal ECParameterSpec, but the point curve is null.
	 * 
	 * @throws ConversionException 
	 */
	@Test(expected = ConversionException.class)
	public void testECParameterSpecConverter_UnmarshalECParameterSpec_CurveIsNull() throws ConversionException{
		
		spec = new ECParameterSpec(curve, point, n, h);
		
		StringWriter writer = new StringWriter();
		PersonalizationFactory.marshal(spec, writer);
		
		String xmlParamSpec = writer.toString();
		xmlParamSpec = xmlParamSpec.replace("<p>76884956397045344220809746629001649093037950200943055203735601445031516197751</p>", "");
		
		assertNull(PersonalizationFactory.unmarshal(new StringReader(xmlParamSpec)));
	}
	
	/**
	 * Try to to unmarshal ECParameterSpec, but the point curve is null.
	 * 
	 * @throws ConversionException 
	 */
	@Test(expected = ConversionException.class)
	public void testECParameterSpecConverter_UnmarshalECParameterSpec_ECSpecIsNull() throws ConversionException{
		
		spec = new ECParameterSpec(curve, point, n, h);
		
		StringWriter writer = new StringWriter();
		PersonalizationFactory.marshal(spec, writer);
		
		String xmlParamSpec = writer.toString();
		xmlParamSpec = xmlParamSpec.replace("<n>76884956397045344220809746629001649092737531784414529538755519063063536359079</n>", "");
		
		assertNull(PersonalizationFactory.unmarshal(new StringReader(xmlParamSpec)));
	}
}
