package de.persosim.simulator.perso;

import java.util.Arrays;
import java.util.Collections;

import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.Protocol.SecInfoPublicity;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.NullSecurityCondition;
import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.secstatus.TaSecurityCondition;
import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.BitField;

/**
 * This can be used as callback on an {@link XmlPersonalization} in order to
 * automatically complete SecInfo files. The SecInfo files created by this
 * Unmarshaller comply with DefectList ObjectIdentifier
 * "id­EAC2Privile­gedTerminalIn­foMissing" and "id­eIDSecurity­InfoMissing"
 * according to TR-03127 attachment D.
 * 
 * Documents created by this Unmarshaller differ in the following points from
 * documents created according to latest specifications:
 * 
 * EF.CardAccess and EF.ChipSecurity do not contain a structure
 * PrivilegedTerminalInfo. The key available to non-privileged terminals for
 * chip authentication is the one addressed during first ChipAuthenticationInfo.
 * 
 * @author slutters
 * 
 */
public class DefectListNpaUnmarshallerCallback extends DefaultNpaUnmarshallerCallback {
	
	@Override
	protected void createEfCardAccess(Personalization perso) {
		// collect SecInfos from protocols
		ConstructedTlvDataObject secInfos = new ConstructedTlvDataObject(new TlvTag(Asn1.SET));
		for (Protocol curProtocol : perso.getProtocolList()) {
			secInfos.addAll(curProtocol.getSecInfos(SecInfoPublicity.PUBLIC, perso.getObjectTree()));
		}

		// add file to object tree
		ElementaryFile efCardAccess = new ElementaryFile(new FileIdentifier(
				0x011C), new ShortFileIdentifier(0x1C),
				secInfos.toByteArray(),
				Arrays.asList((SecCondition) new NullSecurityCondition()),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		perso.getObjectTree().addChild(efCardAccess);
	}
	
	@Override
	protected void createEfChipSecurity(Personalization perso) {
		// collect SecInfos from protocols
		ConstructedTlvDataObject secInfos = new ConstructedTlvDataObject(new TlvTag(Asn1.SET));
		for (Protocol curProtocol : perso.getProtocolList()) {
			secInfos.addAll(curProtocol.getSecInfos(SecInfoPublicity.PRIVILEGED, perso.getObjectTree()));
		}
		
		SecCondition taWithIs = new TaSecurityCondition(TerminalType.IS, null);
		SecCondition taWithAtPrivileged = new TaSecurityCondition(
				TerminalType.AT, new RelativeAuthorization(
						CertificateRole.TERMINAL, new BitField(6).flipBit(3)));

		TlvDataObject cmsSignedData = buildSignedDataFile(secInfos);

		ElementaryFile efChipSecurity = new ElementaryFile(new FileIdentifier(
				0x011B), new ShortFileIdentifier(0x1B),
				cmsSignedData.toByteArray(), 
				Arrays.asList(taWithIs, taWithAtPrivileged),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		perso.getObjectTree().addChild(efChipSecurity);
	}
	
}
