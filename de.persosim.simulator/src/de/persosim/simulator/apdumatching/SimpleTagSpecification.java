package de.persosim.simulator.apdumatching;

import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvTag;

/**
 * XXX missing JavaDoc
 * @author slutters
 *
 */
public class SimpleTagSpecification extends TlvTag implements Iso7816, ApduSpecificationIf {
	protected byte required;
	
	/*--------------------------------------------------------------------------------*/
	
	public SimpleTagSpecification() {
		super();
		this.required = REQ_MATCH;
	}
	
	
	public SimpleTagSpecification(byte[] dataField, int minOffset, int length, byte required) {
		super(dataField, minOffset, length);
		this.required = required;
	}
	
	public SimpleTagSpecification(byte[] dataField, int minOffset, int maxOffset) {
		this(dataField, minOffset, maxOffset, REQ_MATCH);
	}
	
	
	public SimpleTagSpecification(byte[] dataField, byte required) {
		super(dataField);
		this.required = required;
	}
	
	public SimpleTagSpecification(byte[] tag) {
		this(tag, REQ_MATCH);
	}
	
	
	public SimpleTagSpecification(short dataField, byte required) {
		super(dataField);
		this.required = required;
	}
	
	public SimpleTagSpecification(short tag) {
		this(tag, REQ_MATCH);
	}
	
	
	public SimpleTagSpecification(byte dataField, byte required) {
		super(dataField);
		this.required = required;
	}
	
	public SimpleTagSpecification(byte tag) {
		this(tag, REQ_MATCH);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	public boolean equals(SimpleTagSpecification tagSpec) {
		if(this.required != tagSpec.getRequired()) {
			return false;
		}
		
		return this.matches(tagSpec);
	}
	
	public boolean matches(TlvDataObject tlvDataObject) {
		if(!this.matches(tlvDataObject.getTlvTag())) {
			return false;
		}
		
		if(this.required == REQ_MISMATCH) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * @return the required
	 */
	public byte getRequired() {
		return required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(byte required) {
		this.required = required;
	}
}
