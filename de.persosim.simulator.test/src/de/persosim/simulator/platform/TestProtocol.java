package de.persosim.simulator.platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.tlv.TlvDataObject;

public class TestProtocol implements Protocol {

	public List<MethodCall> methodCalls = new ArrayList<>();
	

	@Override
	public String getProtocolName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<? extends TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(ProcessingData processingData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		methodCalls.add(new MethodCall("reset"));
	}

	@Override
	public boolean isMoveToStackRequested() {
		// TODO Auto-generated method stub
		return false;
	}

}
