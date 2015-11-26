package de.persosim.simulator.protocols.ta;

import java.util.Arrays;

import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;

/**
 * This object stores the information used in the verify command and transmitted
 * while executing terminal authentication. Instances of this class are immutable.
 * 
 * @author mboonk
 * 
 */
public class AuthenticatedAuxiliaryData {
	private Oid objectIdentifier;
	private byte[] discretionaryData;

	public AuthenticatedAuxiliaryData(Oid objectIdentifier,
			byte[] discretionaryData) {
		super();
		this.objectIdentifier = objectIdentifier;
		this.discretionaryData = Arrays.copyOf(discretionaryData, discretionaryData.length);
	}

	public Oid getObjectIdentifier() {
		return objectIdentifier;
	}

	public byte[] getDiscretionaryData() {
		return Arrays.copyOf(discretionaryData, discretionaryData.length);
	}

	public ConstructedTlvDataObject getEncoded() {
		ConstructedTlvDataObject result = new ConstructedTlvDataObject(TlvConstants.TAG_73);
		PrimitiveTlvDataObject oid = new PrimitiveTlvDataObject(TlvConstants.TAG_06, objectIdentifier.toByteArray());
		PrimitiveTlvDataObject dd = new PrimitiveTlvDataObject(TlvConstants.TAG_53, Arrays.copyOf(discretionaryData, discretionaryData.length));
		result.addTlvDataObject(oid);
		result.addTlvDataObject(dd);
		return result;
	}

}
