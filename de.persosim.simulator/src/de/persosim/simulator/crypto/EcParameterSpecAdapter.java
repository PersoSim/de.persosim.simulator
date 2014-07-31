package de.persosim.simulator.crypto;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
/**
 * @see XmlAdapter
 * @see KeyPair
 * @author amay
 *
 */
public class EcParameterSpecAdapter extends XmlAdapter<EcParameterSpecAdapter.EcParameterSpecRepresentation, ECParameterSpec> {

	@XmlRootElement
	public static class EcParameterSpecRepresentation {

		@XmlElement
		@XmlJavaTypeAdapter(HexBinaryAdapter.class)
		private byte[] p;
		
		@XmlElement
		@XmlJavaTypeAdapter(HexBinaryAdapter.class)
		private byte[] a;
		
		@XmlElement
		@XmlJavaTypeAdapter(HexBinaryAdapter.class)
		private byte[] b;
		
		@XmlElement
		@XmlJavaTypeAdapter(HexBinaryAdapter.class)
		private byte[] generator;
		
		@XmlElement
		@XmlJavaTypeAdapter(HexBinaryAdapter.class)
		private byte[] order;
		
		@XmlElement
		private int cofactor;
		
		public EcParameterSpecRepresentation() {}
		
		public EcParameterSpecRepresentation(EllipticCurve curve,
				ECPoint generator, BigInteger order, int cofactor) {
			BigInteger prime = ((ECFieldFp) curve.getField()).getP();
			p = prime.toByteArray();
			a = curve.getA().toByteArray();
			b = curve.getB().toByteArray();
			this.generator = CryptoUtil.encode(generator, DomainParameterSetEcdh.getPublicPointReferenceLengthL(prime));
			this.order = order.toByteArray();
			this.cofactor = cofactor;
		}

		public EllipticCurve getCurve() {
			return StandardizedDomainParameters.generateCurveFrom(new BigInteger(p), new BigInteger(a), new BigInteger(b));
		}

		public ECPoint getGenerator() {
			return CryptoUtil.decode(getCurve(), generator);
		}

		public BigInteger getOrder() {
			return new BigInteger(order);
		}

		public int getCofactor() {
			return cofactor;
		}

	}

	@Override
	public EcParameterSpecRepresentation marshal(ECParameterSpec spec) {
		return new EcParameterSpecRepresentation(spec.getCurve(), spec.getGenerator(), spec.getOrder(), spec.getCofactor());
	}

	@Override
	public ECParameterSpec unmarshal(EcParameterSpecRepresentation repr) {
		return new ECParameterSpec(repr.getCurve(), repr.getGenerator(), repr.getOrder(), repr.getCofactor());
	}
}