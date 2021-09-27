package de.persosim.simulator.perso;

import java.util.List;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.SecInfoObject;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.SecInfoProtocol;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValuePlain;

/**
 * @author mboonk
 *
 */
public abstract class ProfileOA extends AbstractProfile {

	@Override
	protected List<Protocol> buildProtocolList() {
		List<Protocol> protocolList = super.buildProtocolList();
		protocolList.add(new SecInfoProtocol());
		return protocolList;
	}

	protected void addMobileOid(Oid mobileIdOid) {
		SecInfoObject mobileIdObject = new SecInfoObject(new OidIdentifier(mobileIdOid));

		ConstructedTlvDataObject mobileEidTypeInfo = new ConstructedTlvDataObject(new TlvTag(TlvTag.SEQUENCE));
		mobileEidTypeInfo.addTlvDataObject(new PrimitiveTlvDataObject(new TlvTag(TlvTag.OBJECT_IDENTIFIER),
				new TlvValuePlain(mobileIdOid.toByteArray())));
		mobileIdObject.setSecInfoContent(mobileEidTypeInfo);
		mobileIdObject.addPublicity(SecInfoPublicity.PUBLIC);
		mobileIdObject.addPublicity(SecInfoPublicity.AUTHENTICATED);

		persoDataContainer.addAdditionalObject(mobileIdObject);
	}

}
