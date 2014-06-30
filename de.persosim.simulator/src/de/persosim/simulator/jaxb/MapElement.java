package de.persosim.simulator.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

import de.persosim.simulator.protocols.pace.PaceOid;

class MapElement {

  @XmlElement
  PaceOid oid;

  @XmlElement
  ArrayList<Integer> domainParameterId;

  MapElement() {
  }

  MapElement(PaceOid key, ArrayList<Integer> value) {
    this.oid = key;
    this.domainParameterId = value;
  }
}