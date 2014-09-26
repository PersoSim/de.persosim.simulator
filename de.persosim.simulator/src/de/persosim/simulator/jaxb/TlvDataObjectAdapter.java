package de.persosim.simulator.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectFactory;
import de.persosim.simulator.utils.HexString;

/**
 * @see XmlAdapter
 * @see TlvDataObject
 * @author amay
 *
 */
public class TlvDataObjectAdapter extends XmlAdapter<String, TlvDataObject> {

	@Override
	public String marshal(TlvDataObject v) throws Exception {
		return HexString.encode(v.toByteArray());
	}

	@Override
	public TlvDataObject unmarshal(String v) throws Exception {
		return TlvDataObjectFactory.createTLVDataObject(v);
	}

}
