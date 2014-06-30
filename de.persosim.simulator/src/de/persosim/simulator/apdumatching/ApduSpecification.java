package de.persosim.simulator.apdumatching;

import java.util.Vector;

import de.persosim.simulator.apdu.CommandApdu;
import de.persosim.simulator.apdu.InterindustryCommandApdu;
import de.persosim.simulator.exception.CommandParameterUndefinedException;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvPath;

/**
 * @author slutters
 *
 */
public class ApduSpecification implements Iso7816, ApduSpecificationIf {
	/* The id, e.g. the name of the resembled APDU */
	protected String id;
	
	/* Indicates whether the resembled APDU is able to start a protocol */
	protected boolean isInitialAPDU;
	
	/* ISO format as defined in ISO7816 interface */
	protected byte isoFormat;
	
	/*
	 * The content of the following variables may be widely and especially
	 * dynamically determined by the card or the active protocol.
	 * 
	 * START
	 */
	/* Indicates whether the resembled APDU is expected to use chaining as
	 * defined in ISO7816 interface (CHAINING_X)*/
	protected boolean chaining;
	/* Indicates which chaining mode the resembled APDU is expected to use
	 * as defined in ISO7816 interface (SM_X) */
	protected byte secureMessaging;
	/* Indicates which channel the resembled APDU is expected to use as
	 * defined in ISO7816 interface (CH_X) */
	protected byte channel;
	/*
	 * END
	 */
	
	protected byte isoCase;
	protected boolean isExtendedLengthLCLE;
	
	protected byte ins;
	protected byte p1;
	protected byte p2;
	
	protected ExtendedTagSpecification tags;
	
	
	
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
		this.tags = new ExtendedTagSpecification();
	}
	
	/*--------------------------------------------------------------------------------*/
	
	public void setDefaultRequirement(byte defaultValue, boolean recursive) {
		this.reqIsoFormat = defaultValue;
		this.reqChaining = defaultValue;
		this.reqSecureMessaging = defaultValue;
		this.reqChannel = defaultValue;
		this.reqIsoCase = defaultValue;
		this.reqIsExtendedLengthLCLE = defaultValue;
		this.reqIns = defaultValue;
		this.reqP1 = defaultValue;
		this.reqP2 = defaultValue;
		
		this.tags.setRequired(defaultValue, recursive);
	}
	
	public void setDefaultRequirement(byte defaultValue) {
		this.setDefaultRequirement(defaultValue, RECURSIVE);
	}
	
	/*--------------------------------------------------------------------------------*/
	
	public boolean equals(ApduSpecification otherAPDUSpecification) {
		//XXX class implements equals but not hashCode 
		if(!this.id.equals(otherAPDUSpecification.getId())) {return false;}
		
		if(this.isInitialAPDU != otherAPDUSpecification.isInitialAPDU) {return false;}
		
		if(this.isoFormat != otherAPDUSpecification.getIsoFormat()) {return false;};
		if(this.reqIsoFormat != otherAPDUSpecification.getReqIsoFormat()) {return false;};
		
		if(this.chaining != otherAPDUSpecification.isChaining()) {return false;};
		if(this.reqChaining != otherAPDUSpecification.getReqChaining()) {return false;};
		
		if(this.secureMessaging != otherAPDUSpecification.getSecureMessaging()) {return false;};
		if(this.reqSecureMessaging != otherAPDUSpecification.getReqSecureMessaging()) {return false;};
		
		if(this.channel != otherAPDUSpecification.getChannel()) {return false;};
		if(this.reqChannel != otherAPDUSpecification.getReqChannel()) {return false;};
		
		if(this.isoCase != otherAPDUSpecification.getIsoCase()) {return false;};
		if(this.reqIsoCase != otherAPDUSpecification.getReqIsoCase()) {return false;};
		
		if(this.isExtendedLengthLCLE != otherAPDUSpecification.isExtendedLengthLCLE()) {return false;};
		if(this.reqIsExtendedLengthLCLE != otherAPDUSpecification.getReqIsExtendedLengthLCLE()) {return false;};
		
		if(this.ins != otherAPDUSpecification.getIns()) {return false;};
		if(this.reqIns != otherAPDUSpecification.getReqIns()) {return false;};
		
		if(this.p1 != otherAPDUSpecification.getP1()) {return false;};
		if(this.reqP1 != otherAPDUSpecification.getReqP1()) {return false;};
		
		if(this.p2 != otherAPDUSpecification.getP2()) {return false;};
		if(this.reqP2 != otherAPDUSpecification.getReqP2()) {return false;};
		
		if(!this.tags.equals(otherAPDUSpecification.getTags())) {return false;}
		
		return true;
	}
	
	//XXX let this method return only boolean and simplify several common blocks extract comparison with parameter REQ_(MIS)MATCH
	public ApduMatchResult matchesFullAPDU(CommandApdu apdu) {
		byte isoCase;
		
		if(this.isoFormat == apdu.getIsoFormat()) {
			if(this.reqIsoFormat == REQ_MISMATCH) {
				return new ApduMatchResult(SW_6800_FUNCTION_IN_CLA_NOT_SUPPORTED, "ISO format must not be 0x" + String.format("%02X", this.isoFormat));
			}
		} else{
			if(this.reqIsoFormat == REQ_MATCH) {
				return new ApduMatchResult(SW_6800_FUNCTION_IN_CLA_NOT_SUPPORTED, "ISO format is not 0x" + String.format("%02X", this.isoFormat));
			}
		}
		
		if (reqChaining != REQ_OPTIONAL) {
			if (!(apdu instanceof InterindustryCommandApdu)) { //XXX AMY: use a marker interface to check for channel abilities to provide more generic way
				return new ApduMatchResult(SW_6F00_UNKNOWN, "apdu class does not support channels");
			}
			if(this.chaining == ((InterindustryCommandApdu) apdu).isChaining()) {
				if(this.reqChaining == REQ_MISMATCH) {
					if(this.chaining) {
						return new ApduMatchResult(SW_6884_COMMAND_CHAINING_NOT_SUPPORTED, "chaining is not supported");
					} else{
						return new ApduMatchResult(SW_6883_LAST_COMMAND_EXPECTED, "chaining expected");
					}
				}
			} else{
				if(this.reqChaining == REQ_MATCH) {
					if(this.chaining) {
						return new ApduMatchResult(SW_6883_LAST_COMMAND_EXPECTED, "chaining expected");
					} else{
						return new ApduMatchResult(SW_6884_COMMAND_CHAINING_NOT_SUPPORTED, "chaining is not supported");
					}
				}
			}
		}
		
		
		if(reqSecureMessaging != REQ_OPTIONAL) {
			CommandApdu curApdu = apdu;
			while (curApdu != null) {
				if ((curApdu instanceof InterindustryCommandApdu) && //XXX AMY use a marker interface to check for secure messaging abilities to provide more generic way
						(secureMessaging == ((InterindustryCommandApdu) curApdu).getSecureMessaging())) {
					if(reqSecureMessaging == REQ_MATCH) {
						break;
					} else {
						return new ApduMatchResult(SW_6F00_UNKNOWN, "SM mismatch not fulfilled");
					}
				}
				
				//check predecessor
				curApdu = curApdu.getPredecessor();
			}
		}
		
		if (reqChannel != REQ_OPTIONAL) {
			if (!(apdu instanceof InterindustryCommandApdu)) { //XXX use a marker interface to check for channel abilities to provide more generic way
				return new ApduMatchResult(SW_6F00_UNKNOWN, "apdu class does not support channels");
			}
			if(this.channel == ((InterindustryCommandApdu) apdu).getChannel()) {
				if(this.reqChannel == REQ_MISMATCH) {
					return new ApduMatchResult(SW_6881_LOGICAL_CHANNEL_NOT_SUPPORTED, "channel must not be " + this.channel);
				}
			} else{
				if(this.reqChannel == REQ_MATCH) {
					return new ApduMatchResult(SW_6881_LOGICAL_CHANNEL_NOT_SUPPORTED, "channel expected to be " + this.channel);
				}
			}
			
		}
		
		
		if(this.ins == apdu.getIns()) {
			if(this.reqIns == REQ_MISMATCH) {
				return new ApduMatchResult(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED, "INS byte must not be " + String.format("%02X", this.ins));
			}
		} else{
			if(this.reqIns == REQ_MATCH) {
				return new ApduMatchResult(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED, "INS byte expected to be " + String.format("%02X", this.ins));
			}
		}
		
		if(this.p1 == apdu.getP1()) {
			if(this.reqP1 == REQ_MISMATCH) {
				return new ApduMatchResult(SW_6A86_INCORRECT_PARAMETERS_P1P2, "P1 byte must not be " + String.format("%02X", this.p1));
			}
		} else{
			if(this.reqP1 == REQ_MATCH) {
				return new ApduMatchResult(SW_6A86_INCORRECT_PARAMETERS_P1P2, "P1 byte expected to be " + String.format("%02X", this.p1));
			}
		}
		
		if(this.p2 == apdu.getP2()) {
			if(this.reqP2 == REQ_MISMATCH) {
				return new ApduMatchResult(SW_6A86_INCORRECT_PARAMETERS_P1P2, "P2 byte must not be " + String.format("%02X", this.p2));
			}
		} else{
			if(this.reqP2 == REQ_MATCH) {
				return new ApduMatchResult(SW_6A86_INCORRECT_PARAMETERS_P1P2, "P2 byte expected to be " + String.format("%02X", this.p2));
			}
		}
		
		isoCase = apdu.getIsoCase();
		
		if(this.isoCase == isoCase) {
			if(this.reqIsoCase == REQ_MISMATCH) {
				return new ApduMatchResult(SW_6700_WRONG_LENGTH, "ISO case must not be " + String.format("%02X", this.isoCase));
			}
		} else{
			if(this.reqIsoCase == REQ_MATCH) {
				return new ApduMatchResult(SW_6700_WRONG_LENGTH, "ISO case expected to be " + String.format("%02X", this.isoCase));
			}
		}
		
		if (reqIsExtendedLengthLCLE != REQ_OPTIONAL) {
			if (isoCase == 1) {
				return new ApduMatchResult(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED, "unable to determine extended length for iso case 1 apdu");
			}
			
			if(this.isExtendedLengthLCLE == apdu.isExtendedLength()) {
				if(this.reqIsExtendedLengthLCLE == REQ_MISMATCH) {
					if(this.isExtendedLengthLCLE) {
						return new ApduMatchResult(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED, "extended length L_C/L_E fields must not be used");
					} else{
						return new ApduMatchResult(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED, "extended length L_C/L_E fields expected");
					}
				}
			} else{
				if(this.reqIsExtendedLengthLCLE == REQ_MATCH) {
					if(this.isExtendedLengthLCLE) {
						return new ApduMatchResult(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED, "extended length L_C/L_E fields expected");
					} else{
						return new ApduMatchResult(SW_6985_CONDITIONS_OF_USE_NOT_SATISFIED, "extended length L_C/L_E fields must not be used");
					}
				}
			}
			
		}
		
		if (!this.tags.isEmpty()) {
			
			TlvDataObjectContainer constructedDataField;
			
			byte[] commandDataBytes = apdu.getCommandData().toByteArray();
			
			try {
				constructedDataField = new TlvDataObjectContainer(commandDataBytes, 0, commandDataBytes.length);
				
				TagMatchResult tagMatchResult = this.tags.matches(constructedDataField, STRICT_ORDER);
				
				if(!tagMatchResult.isMatch()) {
					return new ApduMatchResult(tagMatchResult.getProposedStatusWord(), tagMatchResult.getAdditionalInfo());
				}
				
			} catch (IllegalArgumentException e) {
				return new ApduMatchResult(SW_6A80_WRONG_DATA, "command data field does not contain TLV constructed data");
			}
					
		}
		
		return new ApduMatchResult();
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
	public ExtendedTagSpecification getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(ExtendedTagSpecification tags) {
		this.tags = tags;
	}
	
	/*--------------------------------------------------------------------------------*/
	
	public void addTag(Vector<byte[]> path, ExtendedTagSpecification eTagSpec) {
		this.tags.addSubTag(path, eTagSpec);
	}
	
	public void addTag(TlvPath path, ExtendedTagSpecification eTagSpec) {
		this.tags.addSubTag(path.clone(), eTagSpec);
	}
	
	public void addTag(ExtendedTagSpecification eTagSpec) {
		this.tags.addSubTag(eTagSpec);
	}
	
	
	
	public void addTag(Vector<byte[]> path, byte[] tag, byte req) {
		this.tags.addSubTag(path, new SimpleTagSpecification(tag, req));
	}

	public void addTag(Vector<byte[]> path, byte[] tag) {
		this.tags.addSubTag(path, new SimpleTagSpecification(tag));
	}
	
	public void addTag(byte[] tag, byte req) {
		this.tags.addSubTag(new SimpleTagSpecification(tag, req));
	}
	
	public void addTag(byte[] tag) {
		this.tags.addSubTag(new SimpleTagSpecification(tag));
	}
	
	
	
	public void addTag(Vector<byte[]> path, short tag, byte req) {
		this.tags.addSubTag(path, new SimpleTagSpecification(tag, req));
	}
	
	public void addTag(Vector<byte[]> path, short tag) {
		this.tags.addSubTag(path, new SimpleTagSpecification(tag));
	}
	
	public void addTag(TlvPath path, short tag) {
		this.tags.addSubTag(path, new SimpleTagSpecification(tag));
	}
	
	public void addTag(short tag, byte req) {
		this.tags.addSubTag(new SimpleTagSpecification(tag, req));
	}
	
	public void addTag(short tag) {
		this.tags.addSubTag(new SimpleTagSpecification(tag));
	}
	
	
	
	public void addTag(Vector<byte[]> path, byte tag, byte req) {
		this.tags.addSubTag(path, new SimpleTagSpecification(tag, req));
	}
	
	public void addTag(Vector<byte[]> path, byte tag) {
		this.tags.addSubTag(path, new SimpleTagSpecification(tag));
	}
	
	public void addTag(TlvPath path, byte tag) {
		this.tags.addSubTag(path, new SimpleTagSpecification(tag));
	}
	
	public void addTag(byte tag, byte req) {
		this.tags.addSubTag(new SimpleTagSpecification(tag, req));
	}
	
	public void addTag(byte tag) {
		this.tags.addSubTag(new SimpleTagSpecification(tag));
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
