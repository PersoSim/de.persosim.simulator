package de.persosim.simulator.apdumatching;

import de.persosim.simulator.apdu.CommandApduImpl;
import de.persosim.simulator.apdu.InterindustryCommandApdu;

import static org.globaltester.logging.PersoSimLogger.log;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.IsoSecureMessagingCommandApdu;
import de.persosim.simulator.exception.CommandParameterUndefinedException;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvPath;
import de.persosim.simulator.tlv.TlvTag;

/**
 * This class specifies requirements that must be met by an APDU to positively match.
 * 
 * @author slutters
 *
 */
public class ApduSpecification implements Iso7816, ApduSpecificationConstants {
	/* The id, e.g. the name of the resembled APDU */
	protected String id;
	
	/* Indicates whether the resembled APDU is able to start a protocol */
	protected boolean isInitialAPDU;
	
	/* ISO format as defined in ISO7816 interface */
	protected byte isoFormat;
	
	/* Indicates whether the resembled APDU is expected to use chaining as
	 * defined in ISO7816 interface (CHAINING_X)*/
	protected boolean chaining;
	/* Indicates which chaining mode the resembled APDU is expected to use
	 * as defined in ISO7816 interface (SM_X) */
	protected byte secureMessaging;
	/* Indicates which channel the resembled APDU is expected to use as
	 * defined in ISO7816 interface (CH_X) */
	protected byte channel;
	
	protected byte isoCase;
	protected boolean isExtendedLengthLCLE;
	
	protected byte ins;
	protected byte p1;
	protected byte p2;
	
	protected TlvSpecificationContainer tags;
	
	
	
	/*
	 * REQ_FAIL      -> must not match the indicated parameter
	 * REQ_UNDEFINED -> may or may not match the indicated parameter (parameter insignificant)
	 * REQ_MATCH     -> must match the indicated parameter
	 */
	protected byte reqIsoFormat,
		reqChaining, reqSecureMessaging, reqChannel,
		reqIsoCase, reqIsExtendedLengthLCLE,
		reqIns, reqP1, reqP2;
	
	/*--------------------------------------------------------------------------------*/
	
	public ApduSpecification(String id) {
		this.id = id;
		this.isInitialAPDU = false;
		this.tags = new TlvSpecificationContainer();
	}
	
	/*--------------------------------------------------------------------------------*/
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (chaining ? 1231 : 1237);
		result = prime * result + channel;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ins;
		result = prime * result + (isExtendedLengthLCLE ? 1231 : 1237);
		result = prime * result + (isInitialAPDU ? 1231 : 1237);
		result = prime * result + isoCase;
		result = prime * result + isoFormat;
		result = prime * result + p1;
		result = prime * result + p2;
		result = prime * result + reqChaining;
		result = prime * result + reqChannel;
		result = prime * result + reqIns;
		result = prime * result + reqIsExtendedLengthLCLE;
		result = prime * result + reqIsoCase;
		result = prime * result + reqIsoFormat;
		result = prime * result + reqP1;
		result = prime * result + reqP2;
		result = prime * result + reqSecureMessaging;
		result = prime * result + secureMessaging;
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApduSpecification other = (ApduSpecification) obj;
		if (chaining != other.chaining)
			return false;
		if (channel != other.channel)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ins != other.ins)
			return false;
		if (isExtendedLengthLCLE != other.isExtendedLengthLCLE)
			return false;
		if (isInitialAPDU != other.isInitialAPDU)
			return false;
		if (isoCase != other.isoCase)
			return false;
		if (isoFormat != other.isoFormat)
			return false;
		if (p1 != other.p1)
			return false;
		if (p2 != other.p2)
			return false;
		if (reqChaining != other.reqChaining)
			return false;
		if (reqChannel != other.reqChannel)
			return false;
		if (reqIns != other.reqIns)
			return false;
		if (reqIsExtendedLengthLCLE != other.reqIsExtendedLengthLCLE)
			return false;
		if (reqIsoCase != other.reqIsoCase)
			return false;
		if (reqIsoFormat != other.reqIsoFormat)
			return false;
		if (reqP1 != other.reqP1)
			return false;
		if (reqP2 != other.reqP2)
			return false;
		if (reqSecureMessaging != other.reqSecureMessaging)
			return false;
		if (secureMessaging != other.secureMessaging)
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}
	
	/**
	 * This method performs a matching for a single provided parameter
	 * @param name the name of the matching parameter
	 * @param expected the expected value
	 * @param received the received value
	 * @param required whether this parameter is required
	 * @return whether the provided parameter matches
	 */
	private boolean matchByteParameter(String name, byte expected, byte received, byte required) {
		if(expected == received) {
			if(required == REQ_MISMATCH) {
				log(ApduSpecification.class, name + " must not be " + String.format("%02X", received));
				return false;
			}
		} else{
			if(required == REQ_MATCH) {
				log(ApduSpecification.class, name + " expected to be " + String.format("%02X", expected));
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This method performs a matching of the specification defined within this object against the provided {@link CommandApduImpl}.
	 * The matching is positive only iff all parameters match.
	 * Parameters match iff the received parameters are optional, equal the expected ones or do not equal parameters expected to mismatch.
	 * @param apdu the {@link CommandApdu} to match
	 * @return whether the specification defined within this object matches against the provided {@link CommandApduImpl}
	 */
	public boolean matchesFullApdu(CommandApdu apdu) {
		byte isoCaseReceived;
		boolean elementMatch;
		
		elementMatch = matchByteParameter("ISO format", isoFormat, apdu.getIsoFormat(), reqIsoFormat);
		if(!elementMatch) {return false;}
		
		if (reqChaining != REQ_OPTIONAL) {
			if (!(apdu instanceof InterindustryCommandApdu)) {
				log(ApduSpecification.class, "apdu class does not support channels");
				return false;
			}
			if(this.chaining == ((InterindustryCommandApdu) apdu).isChaining()) {
				if(this.reqChaining == REQ_MISMATCH) {
					if(this.chaining) {
						log(ApduSpecification.class, "chaining is not supported");
						return false;
					} else{
						log(ApduSpecification.class, "chaining expected");
						return false;
					}
				}
			} else{
				if(this.reqChaining == REQ_MATCH) {
					if(this.chaining) {
						log(ApduSpecification.class, "chaining expected");
						return false;
					} else{
						log(ApduSpecification.class, "chaining is not supported");
						return false;
					}
				}
			}
		}
		
		if(reqSecureMessaging != REQ_OPTIONAL) {
			CommandApdu curApdu = apdu;
			while (curApdu != null) {
				if ((curApdu instanceof IsoSecureMessagingCommandApdu) &&
						(secureMessaging == ((IsoSecureMessagingCommandApdu) curApdu).getSecureMessaging())) {
					if(reqSecureMessaging == REQ_MATCH) {
						break;
					} else {
						log(ApduSpecification.class, "SM mismatch not fulfilled");
						return false;
					}
				}
				
				//check predecessor
				curApdu = curApdu.getPredecessor();
			}
		}
		
		if (reqChannel != REQ_OPTIONAL) {
			if (!(apdu instanceof InterindustryCommandApdu)) { //XXX use a marker interface to check for channel abilities to provide more generic way
				log(ApduSpecification.class, "apdu class does not support channels");
				return false;
			}
			
			elementMatch = matchByteParameter("channel", channel, ((InterindustryCommandApdu) apdu).getChannel(), reqChannel);
			if(!elementMatch) {return false;}
			
		}
		
		elementMatch = matchByteParameter("INS byte", ins, apdu.getIns(), reqIns);
		if(!elementMatch) {return false;}
		
		elementMatch = matchByteParameter("P1 byte", p1, apdu.getP1(), reqP1);
		if(!elementMatch) {return false;}
		
		elementMatch = matchByteParameter("P2 byte", p2, apdu.getP2(), reqP2);
		if(!elementMatch) {return false;}
		
		isoCaseReceived = apdu.getIsoCase();
		
		elementMatch = matchByteParameter("ISO case", isoCase, isoCaseReceived, reqIsoCase);
		if(!elementMatch) {return false;}
		
		if (reqIsExtendedLengthLCLE != REQ_OPTIONAL) {
			if (isoCaseReceived == 1) {
				log(ApduSpecification.class, "unable to determine extended length for iso case 1 apdu");
				return false;
			}
			
			if(this.isExtendedLengthLCLE == apdu.isExtendedLength()) {
				if(this.reqIsExtendedLengthLCLE == REQ_MISMATCH) {
					if(this.isExtendedLengthLCLE) {
						log(ApduSpecification.class, "extended length L_C/L_E fields must not be used");
						return false;
					} else{
						log(ApduSpecification.class, "extended length L_C/L_E fields expected");
						return false;
					}
				}
			} else{
				if(this.reqIsExtendedLengthLCLE == REQ_MATCH) {
					if(this.isExtendedLengthLCLE) {
						log(ApduSpecification.class, "extended length L_C/L_E fields expected");
						return false;
					} else{
						log(ApduSpecification.class, "extended length L_C/L_E fields must not be used");
						return false;
					}
				}
			}
			
		}
		
		if (!tags.isEmpty()) {
			
			TlvDataObjectContainer constructedDataField;
			
			byte[] commandDataBytes = apdu.getCommandData().toByteArray();
			
			try {
				constructedDataField = new TlvDataObjectContainer(commandDataBytes, 0, commandDataBytes.length);
				
				return tags.matches(constructedDataField);
			} catch (IllegalArgumentException e) {
				log(ApduSpecification.class, "command data field does not contain TLV constructed data");
				return false;
			}
					
		}
		
		return true;
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * @return the isInitialAPDU
	 */
	public boolean isInitialAPDU() {
		return isInitialAPDU;
	}
	
	public void setInitialApdu() {
		this.isInitialAPDU = true;
	}

	/**
	 * @param isInitialAPDU the isInitialAPDU to set
	 */
	public void setInitialAPDU(boolean isInitialAPDU) {
		this.isInitialAPDU = isInitialAPDU;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/*--------------------------------------------------------------------------------*/

	/**
	 * @return the isoFormat
	 */
	public byte getIsoFormat() {
		if(this.reqIsoFormat == REQ_OPTIONAL) {
			CommandParameterUndefinedException.throwIt("ISO format undefined");
		}
		return isoFormat;
	}

	/**
	 * @param isoFormat the isoFormat to set
	 */
	public void setIsoFormat(byte isoFormat) {
		this.isoFormat = isoFormat;
		this.reqIsoFormat = REQ_MATCH;
	}

	/**
	 * @return the chaining
	 */
	public boolean isChaining() {
		if(this.reqChaining == REQ_OPTIONAL) {
			CommandParameterUndefinedException.throwIt("chaining undefined");
		}
		return chaining;
	}

	/**
	 * @param chaining the chaining to set
	 */
	public void setChaining(boolean chaining) {
		this.chaining = chaining;
		this.reqChaining = REQ_MATCH;
	}

	/**
	 * @return the secureMessaging
	 */
	public byte getSecureMessaging() {
		if(this.reqSecureMessaging == REQ_OPTIONAL) {
			CommandParameterUndefinedException.throwIt("secure messaging undefined");
		}
		return secureMessaging;
	}

	/**
	 * @param secureMessaging the secureMessaging to set
	 */
	public void setSecureMessaging(byte secureMessaging) {
		this.secureMessaging = secureMessaging;
		this.reqSecureMessaging = REQ_MATCH;
	}

	/**
	 * @return the channel
	 */
	public byte getChannel() {
		if(this.reqChannel == REQ_OPTIONAL) {
			CommandParameterUndefinedException.throwIt("channel undefined");
		}
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(byte channel) {
		this.channel = channel;
		this.reqChannel = REQ_MATCH;
	}

	/**
	 * @return the isoCase
	 */
	public byte getIsoCase() {
		if(this.reqIsoCase == REQ_OPTIONAL) {
			CommandParameterUndefinedException.throwIt("ISO case undefined");
		}
		return isoCase;
	}

	/**
	 * @param isoCase the isoCase to set
	 */
	public void setIsoCase(byte isoCase) {
		this.isoCase = isoCase;
		this.reqIsoCase = REQ_MATCH;
	}

	/**
	 * @return the isExtendedLengthLCLE
	 */
	public boolean isExtendedLengthLCLE() {
		if(this.reqIsExtendedLengthLCLE == REQ_OPTIONAL) {
			CommandParameterUndefinedException.throwIt("extended length L_C/L_E undefined");
		}
		return isExtendedLengthLCLE;
	}

	/**
	 * @param isExtendedLengthLCLE the isExtendedLengthLCLE to set
	 */
	public void setExtendedLengthLCLE(boolean isExtendedLengthLCLE) {
		this.isExtendedLengthLCLE = isExtendedLengthLCLE;
		this.reqIsExtendedLengthLCLE = REQ_MATCH;
	}

	/**
	 * @return the ins
	 */
	public byte getIns() {
		if(this.reqIns == REQ_OPTIONAL) {
			CommandParameterUndefinedException.throwIt("instruction byte undefined");
		}
		return ins;
	}

	/**
	 * @param ins the ins to set
	 */
	public void setIns(byte ins) {
		this.ins = ins;
		this.reqIns = REQ_MATCH;
	}

	/**
	 * @return the p1
	 */
	public byte getP1() {
		if(this.reqP1 == REQ_OPTIONAL) {
			CommandParameterUndefinedException.throwIt("P1 undefined");
		}
		return p1;
	}

	/**
	 * @param p1 the p1 to set
	 */
	public void setP1(byte p1) {
		this.p1 = p1;
		this.reqP1 = REQ_MATCH;
	}

	/**
	 * @return the p2
	 */
	public byte getP2() {
		if(this.reqP2 == REQ_OPTIONAL) {
			CommandParameterUndefinedException.throwIt("P2 undefined");
		}
		return p2;
	}

	/**
	 * @param p2 the p2 to set
	 */
	public void setP2(byte p2) {
		this.p2 = p2;
		this.reqP2 = REQ_MATCH;
	}
	
	/*--------------------------------------------------------------------------------*/
	
	/**
	 * @return the tags
	 */
	public TlvSpecificationContainer getTags() {
		return tags;
	}
	
	/*--------------------------------------------------------------------------------*/
	
	public void addTag(TlvPath path, TlvSpecification eTagSpec) {
		this.tags.add(path.clone(), eTagSpec);
	}
	
	public void addTag(TlvSpecification eTagSpec) {
		this.tags.add(eTagSpec);
	}
	
	public void addTag(TlvTag tag) {
		addTag(new TlvSpecification(tag));
	}

	/*--------------------------------------------------------------------------------*/
	
	/**
	 * @return the reqIsoFormat
	 */
	public byte getReqIsoFormat() {
		return reqIsoFormat;
	}

	/**
	 * @param reqIsoFormat the reqIsoFormat to set
	 */
	public void setReqIsoFormat(byte reqIsoFormat) {
		this.reqIsoFormat = reqIsoFormat;
	}

	/**
	 * @return the reqChaining
	 */
	public byte getReqChaining() {
		return reqChaining;
	}

	/**
	 * @param reqChaining the reqChaining to set
	 */
	public void setReqChaining(byte reqChaining) {
		this.reqChaining = reqChaining;
	}

	/**
	 * @return the reqSecureMessaging
	 */
	public byte getReqSecureMessaging() {
		return reqSecureMessaging;
	}

	/**
	 * @param reqSecureMessaging the reqSecureMessaging to set
	 */
	public void setReqSecureMessaging(byte reqSecureMessaging) {
		this.reqSecureMessaging = reqSecureMessaging;
	}

	/**
	 * @return the reqChannel
	 */
	public byte getReqChannel() {
		return reqChannel;
	}

	/**
	 * @param reqChannel the reqChannel to set
	 */
	public void setReqChannel(byte reqChannel) {
		this.reqChannel = reqChannel;
	}

	/**
	 * @return the reqIsoCase
	 */
	public byte getReqIsoCase() {
		return reqIsoCase;
	}

	/**
	 * @param reqIsoCase the reqIsoCase to set
	 */
	public void setReqIsoCase(byte reqIsoCase) {
		this.reqIsoCase = reqIsoCase;
	}

	/**
	 * @return the reqIsExtendedLengthLCLE
	 */
	public byte getReqIsExtendedLengthLCLE() {
		return reqIsExtendedLengthLCLE;
	}

	/**
	 * @param reqIsExtendedLengthLCLE the reqIsExtendedLengthLCLE to set
	 */
	public void setReqIsExtendedLengthLCLE(byte reqIsExtendedLengthLCLE) {
		this.reqIsExtendedLengthLCLE = reqIsExtendedLengthLCLE;
	}

	/**
	 * @return the reqIns
	 */
	public byte getReqIns() {
		return reqIns;
	}

	/**
	 * @param reqIns the reqIns to set
	 */
	public void setReqIns(byte reqIns) {
		this.reqIns = reqIns;
	}

	/**
	 * @return the reqP1
	 */
	public byte getReqP1() {
		return reqP1;
	}

	/**
	 * @param reqP1 the reqP1 to set
	 */
	public void setReqP1(byte reqP1) {
		this.reqP1 = reqP1;
	}

	/**
	 * @return the reqP2
	 */
	public byte getReqP2() {
		return reqP2;
	}

	/**
	 * @param reqP2 the reqP2 to set
	 */
	public void setReqP2(byte reqP2) {
		this.reqP2 = reqP2;
	}
}
