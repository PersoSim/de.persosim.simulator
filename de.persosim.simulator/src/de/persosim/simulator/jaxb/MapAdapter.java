package de.persosim.simulator.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.persosim.simulator.protocols.pace.PaceOid;

//XXX AMY remove along with DocumentEpa
public class MapAdapter extends XmlAdapter<MapElements, Map<PaceOid, ArrayList<Integer>>> {

  @Override
  public MapElements marshal(Map<PaceOid, ArrayList<Integer>> map) {
    List<MapElement> list = new ArrayList<>();
    for (Map.Entry<PaceOid, ArrayList<Integer>> entry : map.entrySet()) {
      list.add(new MapElement(entry.getKey(), entry.getValue()));
    }
    return new MapElements(list);
  }

  @Override
  public Map<PaceOid, ArrayList<Integer>> unmarshal(MapElements elements) {
    Map<PaceOid, ArrayList<Integer>> map = new HashMap<>();
    for (MapElement mapElement : elements.mapElements) {
      map.put(mapElement.oid, mapElement.domainParameterId);
    }
    return map;
  }
}