package de.persosim.simulator.protocols;


/**
 * This contains the different level of publicity for the security infos
 * returned by protocols. It is used when creating card files containing
 * security infos by
 * {@link Protocol#getSecInfos(SecInfoPublicity, de.persosim.simulator.cardobjects.MasterFile)}
 * .
 * 
 * @author jgoeke
 *
 */
public enum SecInfoPublicity {
	
    PUBLIC,
    AUTHENTICATED,
    PRIVILEGED
    
  }


