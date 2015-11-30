package de.persosim.simulator.apdu;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvValue;

public class SmMarkerApdu implements CommandApdu,
		IsoSecureMessagingCommandApdu {

	private final CommandApdu predecessor;

	public SmMarkerApdu(CommandApdu previousCommandApdu) {
		//store history
		predecessor = previousCommandApdu;
	}
		
	@Override
	public byte getSecureMessaging() {
		return Iso7816.SM_COMMAND_HEADER_AUTHENTICATED;
	}

	@Override
	public CommandApdu rewrapApdu(byte newSmStatus, byte[] data) {
		// XXX Auto-generated method stub
		return this;
	}

	@Override
	public boolean wasSecureMessaging() {
		return true;
	}

	@Override
	public byte getIsoFormat() {
		return predecessor.getIsoFormat();
	}

	@Override
	public byte getCla() {
		return predecessor.getCla();
	}

	@Override
	public byte getIns() {
		return predecessor.getIns();
	}

	@Override
	public byte getP1() {
		return predecessor.getP1();
	}

	@Override
	public byte getP2() {
		return predecessor.getP2();
	}

	@Override
	public byte getIsoCase() {
		return predecessor.getIsoCase();
	}

	@Override
	public boolean isExtendedLength() {
		return predecessor.isExtendedLength();
	}

	@Override
	public int getNc() {
		return predecessor.getNc();
	}

	@Override
	public TlvValue getCommandData() {
		return predecessor.getCommandData();
	}

	@Override
	public TlvDataObjectContainer getCommandDataObjectContainer() {
		return getPredecessor().getCommandDataObjectContainer();
	}

	@Override
	public int getNe() {
		return predecessor.getNe();
	}

	@Override
	public short getP1P2() {
		return predecessor.getP1P2();
	}

	@Override
	public byte[] getHeader() {
		return predecessor.getHeader();
	}

	@Override
	public byte[] toByteArray() {
		return predecessor.toByteArray();
	}

	@Override
	public CommandApdu getPredecessor() {
		SmMarkerApdu ret = new SmMarkerApdu(predecessor);
		return ret.predecessor;
	}

	@Override
	public boolean isNeZeroEncoded() {
		return predecessor.isNeZeroEncoded();
	}

}
