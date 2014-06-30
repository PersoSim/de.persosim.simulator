package de.persosim.simulator.cardobjects;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents a PIN which may be used e.g. by the PACE protocol.
 * 
 * @author slutters
 *
 */
//XXX SLS why define an additional object here? I think PasswordAuthObject should be sufficient
@XmlRootElement
public class PinObject extends PasswordAuthObjectWithRetryCounter {
	
	public PinObject() {
		
	}
	
	public PinObject(AuthObjectIdentifier identifier,
			byte [] password, int minLengthOfPasswordInBytes, int maxLengthOfPasswordInBytes,
			int defaultValueRetryCounter){
		
		super(identifier, password, "PIN", minLengthOfPasswordInBytes, maxLengthOfPasswordInBytes, defaultValueRetryCounter);
	}
	
	@Override
	public void setPassword(byte[] newPassword) {
		boolean accessGranted = true;
		//XXX fix this access condition check
		if(accessGranted) {
			super.setPassword(newPassword);
			resetRetryCounterToDefault();
		} else{
			throw new IllegalStateException("access to PIN denied");
		}
		
	}
	
}
