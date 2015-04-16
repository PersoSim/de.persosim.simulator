package de.persosim.simulator.perso;

import java.util.Arrays;
import java.util.Collections;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

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
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.HexString;

/**
 * This can be used as callback on an {@link XmlPersonalization} in order to
 * automatically complete SecInfo files.
 * 
 * @author amay
 * 
 */
@XmlRootElement
public class DefaultNpaUnmarshallerCallback implements PersoUnmarshallerCallback, TlvConstants {

	private static SignedDataFileCache signedDataFileCache = SignedDataFileCache.getInstance("signedDataFileCache.xml");
	
	@XmlAnyElement(lax=true)
	private SecInfoCmsBuilder cmsBuilder = new DefaultSecInfoCmsBuilder();
	

	/**
	 * Empty default constructor using defaults for all fields
	 */
	public DefaultNpaUnmarshallerCallback() {}
	
	/**
	 * Constructor using a specific {@link SecInfoCmsBuilder}
	 * @param cmsBuilder
	 */
	public DefaultNpaUnmarshallerCallback(SecInfoCmsBuilder cmsBuilder) {
		this.cmsBuilder = cmsBuilder;
	}

	@Override
	public void afterUnmarshall(Personalization perso) {
		if (fileIsMissing(perso, 0x011C)) {
			createEfCardAccess(perso);
		}
		if (fileIsMissing(perso, 0x011D)) {
			createEfCardSecurity(perso);
		}
		if (fileIsMissing(perso, 0x011B)) {
			createEfChipSecurity(perso);
		}

	}

	private boolean fileIsMissing(Personalization perso, int fileIdentifier) {
		return perso.getObjectTree()
				.findChildren(new FileIdentifier(fileIdentifier)).size() != 1;
	}
	
	protected ConstructedTlvDataObject getSecInfos(Personalization perso, SecInfoPublicity secInfoPublicity) {
		// collect SecInfos from protocols
		ConstructedTlvDataObject secInfos = new ConstructedTlvDataObject(new TlvTag(Asn1.SET));
		for (Protocol curProtocol : perso.getProtocolList()) {
			secInfos.addAll(curProtocol.getSecInfos(secInfoPublicity, perso.getObjectTree()));
		}
				
		return secInfos;
	}

	protected void createEfCardAccess(Personalization perso) {
		ConstructedTlvDataObject secInfos = getSecInfos(perso, SecInfoPublicity.PUBLIC);

		// add file to object tree
		ElementaryFile efCardAccess = new ElementaryFile(new FileIdentifier(
				0x011C), new ShortFileIdentifier(0x1C),
				secInfos.toByteArray(),
				Arrays.asList((SecCondition) new NullSecurityCondition()),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		perso.getObjectTree().addChild(efCardAccess);
	}

	protected void createEfCardSecurity(Personalization perso) {
		ConstructedTlvDataObject secInfos = getSecInfos(perso, SecInfoPublicity.AUTHENTICATED);
		
		TlvDataObject cmsSignedData = buildSignedDataFile(secInfos);

		ElementaryFile efCardSecurity = new ElementaryFile(new FileIdentifier(
				0x011D), new ShortFileIdentifier(0x1D),
				cmsSignedData.toByteArray(), 
				Arrays.asList((SecCondition) new TaSecurityCondition(null, null)),
				Collections.<SecCondition> emptySet(),
				Collections.<SecCondition> emptySet());
		perso.getObjectTree().addChild(efCardSecurity);
	}

	protected void createEfChipSecurity(Personalization perso) {
		ConstructedTlvDataObject secInfos = getSecInfos(perso, SecInfoPublicity.PRIVILEGED);
		
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

	protected TlvDataObject buildSignedDataFile(
			ConstructedTlvDataObject secInfos) {
		// return cached value if one is available
		if (signedDataFileCache.containsKey(secInfos)) {
			return signedDataFileCache.get(secInfos);
		}
		
		TlvDataObject oidTlv = new PrimitiveTlvDataObject(TAG_OID, 
		HexString.toByteArray("2A 86  48 86 F7 0D 01 07 02"));
		
		ConstructedTlvDataObject cmsContainer = new ConstructedTlvDataObject(TAG_A0);
		cmsContainer.addTlvDataObject(cmsBuilder.buildSignedData(secInfos));
		
		ConstructedTlvDataObject signedDataFile = new ConstructedTlvDataObject(TAG_SEQUENCE);
		signedDataFile.addTlvDataObject(oidTlv);
		signedDataFile.addTlvDataObject(cmsContainer);
		
		//put the newly generated signedDataFile into the cache
		signedDataFileCache.put(secInfos, signedDataFile);
		
		return signedDataFile;
	}

}
