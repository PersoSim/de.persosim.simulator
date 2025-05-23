package de.persosim.simulator.protocols.puo;

import java.util.ArrayList;
import java.util.Iterator;

import de.persosim.simulator.crypto.certificates.CertificateExtension;
import de.persosim.simulator.crypto.certificates.ExtensionOid;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.Utils;

/**
 * This class implements a {@link CertificateExtension} containing Terminal
 * sector for pseudonymous signatures according to BSI-TR03110.
 * 
 * @author slutters
 *
 */
public class TerminalSectorForPseudonymousSignaturesCertificateExtension extends CertificateExtension {
	
	protected ArrayList<Integer> domainParameterSetIds;
	protected ArrayList<byte[]> hashesOfSectorPublicKeys;
	
	public TerminalSectorForPseudonymousSignaturesCertificateExtension(Oid oid, TlvDataObjectContainer contextSpecificDataObjects) {
		super(oid);
		
		if(!oid.equals(ExtensionOid.id_Ps_Sector)) {
			throw new IllegalArgumentException("OID is expected to be " + ExtensionOid.id_Ps_Sector);
		}
		
		domainParameterSetIds = new ArrayList<>();
		hashesOfSectorPublicKeys = new ArrayList<>();
		
		for(TlvDataObject contextSpecificDataObject : contextSpecificDataObjects) {
			if(contextSpecificDataObject.getTlvTag().equals(TlvConstants.TAG_A0)) {
				ConstructedTlvDataObject discretionaryDataTemplateA0 = (ConstructedTlvDataObject) contextSpecificDataObject;
				
				int domainParameterSetId;
				byte[] hashOfSectorPublicKey;
				
				TlvDataObjectContainer params = discretionaryDataTemplateA0.getTlvDataObjectContainer();
				if(params.getNoOfElements() != 2) {
					throw new IllegalArgumentException("found illegal number of parameters");
				}
				
				Iterator<TlvDataObject> iterator = params.iterator();
				TlvDataObject nextTlvDataObject = iterator.next();
				
				if(nextTlvDataObject.getTlvTag().equals(TlvConstants.TAG_80)) {
					domainParameterSetId = Utils.getIntFromUnsignedByteArray(nextTlvDataObject.getValueField());
				} else{
					throw new IllegalArgumentException("missing tag " + TlvConstants.TAG_80);
				}
				
				nextTlvDataObject = iterator.next();
				
				if(nextTlvDataObject.getTlvTag().equals(TlvConstants.TAG_81)) {
					hashOfSectorPublicKey = nextTlvDataObject.getValueField();
				} else{
					throw new IllegalArgumentException("missing tag " + TlvConstants.TAG_81);
				}
				
				domainParameterSetIds.add(domainParameterSetId);
				hashesOfSectorPublicKeys.add(hashOfSectorPublicKey);
				
				continue;
			}
			
			throw new IllegalArgumentException("found illegal tag " + contextSpecificDataObject.getTlvTag());
		}
	}
	
	public TerminalSectorForPseudonymousSignaturesCertificateExtension(ConstructedTlvDataObject extensionData) {
		this(extractOid(extensionData), extractContextSpecificDataObjects(extensionData));
	}
	
	private static Oid extractOid(ConstructedTlvDataObject extensionData) {
		TlvDataObjectContainer tlvDataObjectContainer = extensionData.getTlvDataObjectContainer();
		
		if(tlvDataObjectContainer.getLength() == 0) {
			throw new IllegalArgumentException("no OID found");
		}
		
		TlvDataObject firstElement = tlvDataObjectContainer.iterator().next();
		
		if(!firstElement.getTlvTag().equals(TlvConstants.TAG_06)) {
			throw new IllegalArgumentException("expected OID tag " + TlvConstants.TAG_06);
		}
		
		return new GenericOid(firstElement.getValueField());
	}
	
	private static TlvDataObjectContainer extractContextSpecificDataObjects(ConstructedTlvDataObject extensionData) {
		TlvDataObjectContainer tlvDataObjectContainer = extensionData.getTlvDataObjectContainer();
		
		if(tlvDataObjectContainer.getLength() == 0) {
			throw new IllegalArgumentException("no OID found");
		}
		
		Iterator<TlvDataObject> iterator = tlvDataObjectContainer.iterator();
		TlvDataObject firstElement = iterator.next();
		
		if(!firstElement.getTlvTag().equals(TlvConstants.TAG_06)) {
			throw new IllegalArgumentException("expected OID tag " + TlvConstants.TAG_06);
		}
		
		TlvDataObjectContainer contextSpecificDataObjects = new TlvDataObjectContainer();
		
		while(iterator.hasNext()) {
			contextSpecificDataObjects.addTlvDataObject(iterator.next());
		}
		
		return contextSpecificDataObjects;
	}
	
	@Override
	public ConstructedTlvDataObject toTlv() {
		ConstructedTlvDataObject rootObject = new ConstructedTlvDataObject(TlvConstants.TAG_73);
		
		PrimitiveTlvDataObject oidTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_06, objectIdentifier.toByteArray());
		
		rootObject.addTlvDataObject(oidTlv);
		
		rootObject.addAll(getDataObjects().getTlvObjects());
		
		return rootObject;
	}

	@Override
	public TlvDataObjectContainer getDataObjects() {
		TlvDataObjectContainer container = new TlvDataObjectContainer();
		
		ConstructedTlvDataObject tlvA0;
		PrimitiveTlvDataObject tlv80, tlv81;
		for(int i = 0; i < domainParameterSetIds.size(); i++) {
			tlvA0 = new ConstructedTlvDataObject(TlvConstants.TAG_A0);
			tlv80 = new PrimitiveTlvDataObject(TlvConstants.TAG_80, Utils.toUnsignedByteArray(domainParameterSetIds.get(i)));
			tlv81 = new PrimitiveTlvDataObject(TlvConstants.TAG_81, hashesOfSectorPublicKeys.get(i));
			
			tlvA0.addTlvDataObject(tlv80);
			tlvA0.addTlvDataObject(tlv81);
			
			container.addTlvDataObject(tlvA0);
		}
		
		return container;
	}
	
	public ArrayList<Integer> getDomainParameterSetIds() {
		return domainParameterSetIds;
	}

	public ArrayList<byte[]> getHashesOfSectorPublicKeys() {
		return hashesOfSectorPublicKeys;
	}
	
	/**
	 * This method parses a {@link TerminalSectorForPseudonymousSignaturesCertificateExtension} from the provided {@link ConstructedTlvDataObject}.
	 * @param extensionData the data to parse from
	 * @return the parsed object or null
	 */
	public static TerminalSectorForPseudonymousSignaturesCertificateExtension parse(ConstructedTlvDataObject extensionData) {
		TerminalSectorForPseudonymousSignaturesCertificateExtension extension;
		
		try {
			extension = new TerminalSectorForPseudonymousSignaturesCertificateExtension(extensionData);
			return extension;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * This method parses a {@link TerminalSectorForPseudonymousSignaturesCertificateExtension} from the provided {@link CertificateExtension}.
	 * @param certificateExtension the certificate extension to parse
	 * @return the parsed object or null 
	 */
	public static TerminalSectorForPseudonymousSignaturesCertificateExtension parse(CertificateExtension certificateExtension) {
		TerminalSectorForPseudonymousSignaturesCertificateExtension extension;
		
		try {
			extension = new TerminalSectorForPseudonymousSignaturesCertificateExtension(new TaOid(certificateExtension.getObjectIdentifier().toByteArray()), certificateExtension.getDataObjects());
			return extension;
		} catch (Exception e) {
			return null;
		}
	}
	
}
