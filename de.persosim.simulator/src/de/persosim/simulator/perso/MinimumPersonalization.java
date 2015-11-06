package de.persosim.simulator.perso;

import java.util.ArrayList;
import java.util.List;

import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.file.FileProtocol;
import de.persosim.simulator.seccondition.SecCondition;

/**
 * This class represents a minimum of personalization e.g. for testing purposes.
 * The personalization contains an MF, an EF.CardAccess and the file management protocol.
 * Please note that the content of EF.CardAccess can be chosen arbitrarily and hence may neither be valid nor any TLV structure at all.
 * The content of EF.CardAccess primarily is intended for identification of the personalization.
 * 
 * @author slutters
 *
 */
public class MinimumPersonalization extends PersonalizationImpl {
	
	protected byte[] efCardAccessValue;
	public static final byte [] DEFAULT_EF_CA_VALUE = "DUMMY".getBytes();
	
	public MinimumPersonalization(byte[] efCardAccessValue) {
		super();
		this.efCardAccessValue = efCardAccessValue;
	}
	
	public MinimumPersonalization() throws AccessDeniedException {
		this(DEFAULT_EF_CA_VALUE);
	}
	
	protected List<Protocol> buildProtocolList() {
		List<Protocol> protocols = new ArrayList<>();

		/* load FM protocol */
		FileProtocol fileManagementProtocol = new FileProtocol();
		fileManagementProtocol.init();
		protocols.add(fileManagementProtocol);
		
		return protocols;
	}
	
	public MasterFile buildObjectTree() {
		MasterFile mf = new MasterFile(new FileIdentifier(0x3F00),
				new DedicatedFileIdentifier(new byte[] { (byte) 0xA0, 0x0,
						0x0, 0x2, 0x47, 0x10, 0x03 }));
		
		// add file to object tree
		ElementaryFile efCardAccess = new ElementaryFile(new FileIdentifier(
				0x011C), new ShortFileIdentifier(0x1C),
				efCardAccessValue,
				SecCondition.ALLOWED,
				SecCondition.DENIED,
				SecCondition.DENIED);
		try {
			mf.addChild(efCardAccess);
		} catch (AccessDeniedException e) {
			//ignore as empty EF.CardAccess is not strictly needed
		}
		
		return mf;
	}
	
}
