package de.persosim.simulator.protocols;

import java.util.Collection;
import java.util.HashSet;

import org.globaltester.logging.InfoSource;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.SecInfoObject;
import de.persosim.simulator.cardobjects.TypeIdentifier;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;

public class SecInfoProtocol implements Protocol, Iso7816, InfoSource, TlvConstants {
	@Override
	public String getProtocolName() {
		return "SecInfo";
	}

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState) {
		//card state not needed by this protocol
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		Collection<CardObject> secInfoCardObjects = mf.findChildren(new TypeIdentifier(SecInfoObject.class));
		
		HashSet<TlvDataObject> secInfos = new HashSet<TlvDataObject>();
		
		for (CardObject curSecInfoObject : secInfoCardObjects) {
			if (curSecInfoObject instanceof SecInfoObject) {
				SecInfoObject secInfoObject = (SecInfoObject) curSecInfoObject;
				
				if (secInfoObject.getPublicity().contains(publicity)) {
					TlvDataObject secInfo = secInfoObject.getSecInfoContent();
					if (secInfo != null) {
						secInfos.add(secInfo);
					}
				}
			}
		}			
		return secInfos;
	}

	@Override
	public void process(ProcessingData processingData) {
		//nothing to process
	}


	@Override
	public void reset() {
		//nothing to reset
	}
	
	@Override
	public boolean isMoveToStackRequested() {
		return false;
	}

	@Override
	public String getIDString() {
		return getProtocolName() + " protocol";
	}

}
