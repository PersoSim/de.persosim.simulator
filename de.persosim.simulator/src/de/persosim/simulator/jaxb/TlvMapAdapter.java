package de.persosim.simulator.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.persosim.simulator.tlv.TlvDataObject;

public class TlvMapAdapter extends
		XmlAdapter<TlvMap, Map<TlvDataObject, TlvDataObject>> {

	@Override
	public TlvMap marshal(Map<TlvDataObject, TlvDataObject> map) {
		List<TlvMapElement> list = new ArrayList<>();
		for (Map.Entry<TlvDataObject, TlvDataObject> entry : map.entrySet()) {
			list.add(new TlvMapElement(entry.getKey(), entry.getValue()));
		}
		return new TlvMap(list);
	}

	@Override
	public Map<TlvDataObject, TlvDataObject> unmarshal(TlvMap elements) {
		Map<TlvDataObject, TlvDataObject> map = new HashMap<>();
		for (TlvMapElement mapElement : elements.mapElements) {
			map.put(mapElement.key, mapElement.value);
		}
		return map;
	}

}

class TlvMap {

	@XmlElement(name = "entry")
	List<TlvMapElement> mapElements;

	TlvMap() {
	}

	TlvMap(List<TlvMapElement> mapElements) {
		this.mapElements = mapElements;
	}
}

class TlvMapElement {

	@XmlElement(name = "key")
	@XmlJavaTypeAdapter(TlvDataObjectAdapter.class)
	TlvDataObject key;

	@XmlElement(name = "value")
	@XmlJavaTypeAdapter(TlvDataObjectAdapter.class)
	TlvDataObject value;

	TlvMapElement() {
	}

	TlvMapElement(TlvDataObject key, TlvDataObject value) {
		this.key = key;
		this.value = value;
	}
}