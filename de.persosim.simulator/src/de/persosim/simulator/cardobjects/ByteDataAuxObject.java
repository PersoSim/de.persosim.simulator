package de.persosim.simulator.cardobjects;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.bouncycastle.util.Arrays;

import de.persosim.simulator.protocols.ta.AuthenticatedAuxiliaryData;
import de.persosim.simulator.protocols.ta.TaOid;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.utils.Utils;

@XmlRootElement
public class ByteDataAuxObject extends AuxDataObject {

	@XmlElement //XXX try XmlValue here
	@XmlJavaTypeAdapter(HexBinaryAdapter.class)
	byte [] data;
	
	public ByteDataAuxObject(){
		
	}
	
	public ByteDataAuxObject(OidIdentifier identifier, byte [] data) {
		super(identifier);
		this.data = Arrays.copyOf(data, data.length);
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	@Override
	public boolean verify(AuthenticatedAuxiliaryData current) throws AccessDeniedException {
		//get necessary information stored in TA
		//XXX access conditions should be stored separately and evaluated in a more generic way
		Collection<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(TerminalAuthenticationMechanism.class);
		Collection<SecMechanism> currentMechanisms = securityStatus.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		TerminalAuthenticationMechanism taMechanism = null;
		if (currentMechanisms.size() > 0){
			taMechanism = (TerminalAuthenticationMechanism) currentMechanisms.toArray()[0];
			if (taMechanism.getTerminalType().equals(TerminalType.ST)
					|| (taMechanism.getTerminalType().equals(
							TerminalType.AT) && !taMechanism
							.getEffectiveAuthorization()
							.getAuthorization().getBit(1))){
				throw new AccessDeniedException("Community ID verification not allowed");
			}
			if (identifier.getOid().equals(TaOid.id_CommunityID)){
				return Utils.arrayHasPrefix(data, current.getDiscretionaryData());
			}
		}
		return false;
	}
}
