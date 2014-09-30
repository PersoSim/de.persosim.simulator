package de.persosim.simulator.protocols;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.apdumatching.ApduSpecification;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.InfoSource;

@XmlRootElement
public class NpaProtocol implements Protocol, Iso7816, InfoSource, TlvConstants {
	@Override
	public String getProtocolName() {
		return "nPA";
	}

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState) {
		//card state not needed by this protocol
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		HashSet<TlvDataObject> secInfos = new HashSet<TlvDataObject>();
		
		//add CardInfoLocator
		ConstructedTlvDataObject cardInfoLocator = new ConstructedTlvDataObject(TAG_SEQUENCE);
		cardInfoLocator.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, Tr03110.id_CI));
		cardInfoLocator.addTlvDataObject(new PrimitiveTlvDataObject(TAG_IA5_STRING,
		HexString.toByteArray("68 74 74 70 3A 2F 2F 62 73 69 2E 62 75 6E 64 2E 64 65 2F 63 69 66 2F 6E 70 61 2E 78 6D 6C"))); 
		secInfos.add(cardInfoLocator);
			
		return secInfos;
	}
	
	@Override
	public void process(ProcessingData processingData) {
		//nothing to process
	}

	@Override
	public Collection<ApduSpecification> getApduSet() {
		return Collections.emptySet();
	}

	@Override
	public void reset() {
		//nothing to reset
	}

	@Override
	public String getIDString() {
		return "nPA protocol";
	}
	
	@Override
	public boolean isMoveToStackRequested() {
		return false;
	}

}
