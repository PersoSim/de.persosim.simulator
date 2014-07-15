package de.persosim.simulator.perso;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This can be used as callback on an {@link XmlPersonalisation} in order to
 * automatically complete SecInfo files.
 * FIXME AMY current implementation does not handle this 
 * @author amay
 * 
 */
@XmlRootElement
public class DefaultNpaUnmarshallerCallback implements PersoUnmarshallerCalback {

	@Override
	public void afterUnmarshall(Personalization perso) {
		// FIXME AMY Auto-generated method stub

	}

}
