package de.persosim.simulator.perso;

/**
 * Instances of this interface can be added to any {@link XmlPersonalisation}.
 * They are serialized and after deserialization they are called, which allows
 * recreation of dynamic structures.
 * 
 * @author amay
 * 
 */
public interface PersoUnmarshallerCalback {

	/**
	 * JAXB callback
	 * <p/>
	 * This method is called immediately after unmarshalling to allow any
	 * required modifications to the unmarshalled perso object.
	 * 
	 * @param perso
	 */
	void afterUnmarshall(Personalization perso);

}
