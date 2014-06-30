package de.persosim.simulator.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class MapElements {

	  @XmlElement
	  List<MapElement> mapElements;

	  MapElements() {
	  }

	  MapElements(List<MapElement> mapElements) {
	    this.mapElements = mapElements;
	  }
	}