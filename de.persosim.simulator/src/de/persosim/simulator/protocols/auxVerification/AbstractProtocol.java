package de.persosim.simulator.protocols.auxVerification;

import java.util.Collection;
import java.util.Collections;

import org.globaltester.logging.InfoSource;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.tlv.TlvDataObject;

public abstract class AbstractProtocol implements Protocol, InfoSource {

	protected String protocolName;
	protected CardStateAccessor cardState;

	public AbstractProtocol() {
		this.protocolName = getClass().getSimpleName();
	}

	public AbstractProtocol(String protocolName) {
		this.protocolName = protocolName;		
	}
	
	@Override
	public String getProtocolName() {
		return protocolName;
	}
	

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState) {
		this.cardState = cardState;
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		return Collections.emptySet();
	}

	@Override
	public void reset() {
		//nothing to do in abstract case, maybe this needs to be overriden by subclasses
	}

	@Override
	public String getIDString() {
		return getProtocolName();
	}

	@Override
	public boolean isMoveToStackRequested() {
		return false;
	}

}