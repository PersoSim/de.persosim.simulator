package de.persosim.simulator.crypto.certificates;

import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.util.Arrays;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvTagIdentifier;
import de.persosim.simulator.utils.Utils;

/**
 * This class represents an EC private key.
 * 
 * @author slutters
 *
 */
public class CvEcPrivateKey extends CvPrivateKey implements ECPrivateKey {
	
private static final long serialVersionUID = 1L;
	
	public CvEcPrivateKey(CvOid cvOid, ECPrivateKey ecPrivateKey) {
		super(cvOid, ecPrivateKey);
	}

	@Override
	public ECParameterSpec getParams() {
		return ((ECPrivateKey) key).getParams();
	}

	@Override
	public BigInteger getS() {
		return ((ECPrivateKey) key).getS();
	}

	@Override
	public byte[] postProcessSIgnature(byte[] unprocessedSignature) {
		ConstructedTlvDataObject signatureTlvUnprocessed = new ConstructedTlvDataObject(unprocessedSignature);
		
		PrimitiveTlvDataObject pTlv1 = (PrimitiveTlvDataObject) signatureTlvUnprocessed.getTlvDataObject(new TlvTagIdentifier(new TlvTag((byte) 0x02), 0));
		PrimitiveTlvDataObject pTlv2 = (PrimitiveTlvDataObject) signatureTlvUnprocessed.getTlvDataObject(new TlvTagIdentifier(new TlvTag((byte) 0x02), 1));
		
		byte[] c1 = pTlv1.getValueField();
		byte[] c2 = pTlv2.getValueField();
		
		if(((c1.length % 2) > 0) && (c1[0] == (byte) 0x00)) {
			c1 = Arrays.copyOfRange(c1, 1, c1.length);
		}
		
		if(((c2.length % 2) > 0) && (c2[0] == (byte) 0x00)) {
			c2 = Arrays.copyOfRange(c2, 1, c2.length);
		}
		
		return Utils.concatByteArrays(c1, c2);
		
	}

}
