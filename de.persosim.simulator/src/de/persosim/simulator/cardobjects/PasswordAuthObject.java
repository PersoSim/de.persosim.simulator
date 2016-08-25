package de.persosim.simulator.cardobjects;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;

import de.persosim.simulator.utils.HexString;


/**
 * This authentication object is used for passwords, that are encoded in an array of bytes.
 * @author mboonk
 *
 */
public class PasswordAuthObject extends AbstractCardObject implements AuthObject {
	
	AuthObjectIdentifier identifier;
	
	byte [] password;
	
	protected String passwordName;
	
	public PasswordAuthObject(){
	}
	
	public PasswordAuthObject(AuthObjectIdentifier identifier,
			byte [] password, String passwordName){
		
		if(identifier == null) {throw new IllegalArgumentException("identifier must not be null");}
		if(password == null) {throw new IllegalArgumentException("password must not be null");}
		if(passwordName == null) {throw new IllegalArgumentException("password name must not be null");}
		
		this.identifier = identifier;
		this.password = password;
		this.passwordName = passwordName;
	}
	
	public PasswordAuthObject(AuthObjectIdentifier identifier,
			byte [] password){
		
		this(identifier, password, "password");
	}
	
	public byte [] getPassword(){
		return Arrays.copyOf(password, password.length);
	}
	
	public String getPasswordName() {
		return passwordName;
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = super.getAllIdentifiers();
		result.add(identifier);
		return result;
	}
	
	public int getPasswordIdentifier(){
		return identifier.getIdentifier();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("password " + passwordName + " is " + HexString.encode(password));
		
		try {
			sb.append(" (" + (new String(password, "UTF-8")).toString() + ")");
		} catch (UnsupportedEncodingException e) {
			sb.append(" (schei? encoding)");
		}
		
		return sb.toString();
	}
	
}
