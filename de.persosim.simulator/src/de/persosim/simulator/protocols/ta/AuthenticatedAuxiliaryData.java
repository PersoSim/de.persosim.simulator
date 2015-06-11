package de.persosim.simulator.protocols.ta;

import java.util.Arrays;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;

/**
 * This object stores the information used in the verify command and transmitted
 * while executing terminal authentication.
 * 
 * @author mboonk
 * 
 */
public class AuthenticatedAuxiliaryData {
	private TaOid objectIdentifier;
	private byte[] discretionaryData;

	public AuthenticatedAuxiliaryData(TaOid objectIdentifier,
			byte[] discretionaryData) {
		super();
		this.objectIdentifier = objectIdentifier;
		this.discretionaryData = discretionaryData;
	}

	public TaOid getObjectIdentifier() {
		return objectIdentifier;
	}

	public byte[] getDiscretionaryData() {
		return Arrays.copyOf(discretionaryData, discretionaryData.length);
	}

	public ConstructedTlvDataObject getEncoded() {
		ConstructedTlvDataObject result = new ConstructedTlvDataObject(TlvConstants.TAG_73);
		PrimitiveTlvDataObject oid = new PrimitiveTlvDataObject(TlvConstants.TAG_06, objectIdentifier.toByteArray());
		PrimitiveTlvDataObject dd = new PrimitiveTlvDataObject(TlvConstants.TAG_53, discretionaryData);
		result.addTlvDataObject(oid);
		result.addTlvDataObject(dd);
		return result;
	}

}
