package de.persosim.simulator.protocols.ca3;

import java.util.Collection;

import de.persosim.simulator.cardobjects.AbstractCardObject;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.protocols.Oid;

/**
 * This {@link CardObject} is used to store the Auth-Info access rights for a
 * given object identifier.
 * 
 * @author mboonk
 *
 */
public class PsAuthInfo extends AbstractCardObject {
	/**
	 * This encodes the possible values for PS type protocols auth infos.
	 * @author mboonk
	 *
	 */
	public enum PsAuthInfoValue {
		NO_EXPLICIT_AUTHORISATION((byte) 0), EXPLICIT_AUTHORISATION((byte) 1), NO_TERMINAL_AUTHORISATION((byte) 2);

		private byte value;

		private PsAuthInfoValue(byte value) {
			this.value = value;
		}

		public byte toValue() {
			return value;
		}
	}

	private PsAuthInfoValue ps1AuthInfo;
	private PsAuthInfoValue ps2AuthInfo;

	private OidIdentifier identifier;

	/**
	 * Creates a new instance identified by the used {@link Oid} and containing the given AuthInfo values
	 * @param protocolOid
	 * @param ps1AuthInfo
	 * @param ps2AuthInfo
	 */
	public PsAuthInfo(OidIdentifier protocolOid, PsAuthInfoValue ps1AuthInfo, PsAuthInfoValue ps2AuthInfo) {
		identifier = protocolOid;
		this.ps1AuthInfo = ps1AuthInfo;
		this.ps2AuthInfo = ps2AuthInfo;
	}

	public PsAuthInfoValue getPs1AuthInfo() {
		return ps1AuthInfo;
	}

	public PsAuthInfoValue getPs2AuthInfo() {
		return ps2AuthInfo;
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = super.getAllIdentifiers();
		result.add(identifier);
		return result;
	}
}
