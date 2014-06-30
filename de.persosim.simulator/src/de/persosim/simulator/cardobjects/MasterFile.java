package de.persosim.simulator.cardobjects;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * This represents the automatically created master file
 * @author mboonk
 *
 */
@XmlRootElement
public class MasterFile extends DedicatedFile {

	public MasterFile() {
		this(null, null);
	}
	
	public MasterFile(FileIdentifier fileIdentifier, DedicatedFileIdentifier dedicatedFileName) {
		super(fileIdentifier, dedicatedFileName);
	}

	public void setIdentity(FileIdentifier identifier, DedicatedFileIdentifier name) {
		fileIdentifier = identifier;
		dedicatedFileName = name;
	}
}
