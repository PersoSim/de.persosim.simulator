package de.persosim.simulator.apdumatching;

import static org.globaltester.logging.BasicLogger.log;

import java.util.ArrayList;
import java.util.Iterator;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvPath;
import de.persosim.simulator.tlv.TlvTagIdentifier;

/**
 * This class provides a container for specifications of TLV elements. Its
 * primary function is to serve as root element of the specified TLV structure.
 *
 * @author slutters
 *
 */
public class TlvSpecificationContainer extends ArrayList<TlvSpecification> implements Iso7816, ApduSpecificationConstants {

	private static final long serialVersionUID = 1L;
	protected boolean allowUnspecifiedSubTags;
	protected boolean isStrictOrder;

	public TlvSpecificationContainer(boolean allowUnspecifiedSubTags, boolean isStrictOrder) {
		this.allowUnspecifiedSubTags = allowUnspecifiedSubTags;
		this.isStrictOrder = isStrictOrder;
	}

	public TlvSpecificationContainer(boolean allowUnspecifiedSubTags) {
		this(allowUnspecifiedSubTags, STRICT_ORDER);
	}

	public TlvSpecificationContainer() {
		this(DO_NOT_ALLOW_FURTHER_TAGS);
	}

	/**
	 * This method adds TLV data object specifications to an existing hierarchy
	 * according to the provided path and path offset. The path (offset) is
	 * relative to the object on which it is applied.
	 *
	 * @param path
	 *            the path at which the provided specification is to be added
	 * @param pathOffset
	 *            the offset of the current position within the path
	 * @param tlvSpec
	 *            the TLV data object specifications to be added
	 */
	public void add(TlvPath path, int pathOffset, TlvSpecification tlvSpec) {
		if(path == null) {throw new NullPointerException("path must not be null");}

		for(int i = 0; i < path.size(); i++) {
			if(path.get(i) == null) {
				throw new NullPointerException("subtag in path must not be null");
			}
		}

		if(pathOffset == path.size()) {
			add(tlvSpec);
			return;
		} else{
			TlvSpecification subTlvSpec;
			int index = getIndexOfSubTag(path.get(pathOffset));

			if(index >= 0) {
				subTlvSpec = get(index);
				subTlvSpec.add(path, pathOffset + 1, tlvSpec);
			} else{
				throw new NullPointerException("path element not found");
			}
		}
	}

	/**
	 * This method inserts a TLV data object specification within an existing
	 * hierarchy as child to the constructed TLV data object specification at
	 * the provided path. The path is relative to the object on which it is
	 * applied.
	 *
	 * @param path
	 *            the path at which the provided specification is to be added
	 * @param tlvSpec
	 *            the TLV data object specifications to be added
	 */
	public void add(TlvPath path, TlvSpecification tlvSpec) {
		add(path, 0, tlvSpec);
	}

	/**
	 * This method returns the index of an occurrence of a tag matching
	 * the provided tag within the sub tags of this object. If no occurrence can
	 * be found the returned index will be "-1".
	 *
	 * @param tlvTagIdentifier
	 *            the tag to be matched for
	 * @return the first occurrence of the provided tag
	 */
	public int getIndexOfSubTag(TlvTagIdentifier tlvTagIdentifier) {
		if(tlvTagIdentifier == null) {throw new NullPointerException("tag identifier must not be null");}

		int remainingOccurences = tlvTagIdentifier.getNoOfPreviousOccurrences();

		for(int i = 0; i < size(); i++) {
			if(get(i).matches(tlvTagIdentifier.getTag())) {
				if (remainingOccurences == 0) {
					return i;
				} else {
					remainingOccurences--;
				}
			}
		}

		return -1;
	}


	/**
	 * This method returns whether the provided TLV data object container matches the hierarchy of specifications within this object.
	 * @param tlvContainer a TLV data object container
	 * @return the matching result
	 */
	public boolean matches(TlvDataObjectContainer tlvContainer) {
		Iterator<TlvDataObject> tlvIterator;
		int counter, diffCounter, currentWorkingIndex, highestAlreadyEncounteredIndex;;
		TlvDataObject tlvDataObject;
		TlvSpecification currentTlvSpecification;

		counter = 0;
		highestAlreadyEncounteredIndex = 0;

		tlvIterator = tlvContainer.iterator();

		while(tlvIterator.hasNext()) {
			tlvDataObject = tlvIterator.next();

			currentWorkingIndex = this.getIndexOfSubTag(new TlvTagIdentifier(tlvDataObject.getTlvTag()));

			if(currentWorkingIndex < 0) {
				if(!this.allowUnspecifiedSubTags) {
					/* we encountered an unknown (sub-) tag but these are implicitly forbidden at the specified place */
					log("unexpected tag " + tlvDataObject.getTlvTag(), LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
					return false;
				}
			} else{
				currentTlvSpecification = get(currentWorkingIndex);

				if(currentTlvSpecification.getRequired() == REQ_MISMATCH) {
					log("tag " + tlvDataObject.getTlvTag() + " not allowed", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
					return false;
				}

				if(currentTlvSpecification.getRequired() == REQ_MATCH) {
					log("tag " + tlvDataObject.getTlvTag() + " not matching", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
					counter++;
				}

				if(isStrictOrder) {
					if(currentWorkingIndex < highestAlreadyEncounteredIndex) {
						/* we encountered a known (sub-) tag but out of the specified order */
						log("tag " + tlvDataObject.getTlvTag() + " is out of order", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
						return false;
					} else{
						highestAlreadyEncounteredIndex = currentWorkingIndex;
					}
				}

				if(!currentTlvSpecification.matches(tlvDataObject)) {
					log("error", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
					return false;
				}
			}
		}

		diffCounter = this.getNoOfTagsMatchingRequirement(REQ_MATCH) - counter;

		if(diffCounter > 0) {
			/* tlv object failed to satisfy all required matches */
			/* "missing tags" */
			if(diffCounter == 1) {
				log("missing " + diffCounter + " more mandatory tag", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
				return false;
			} else{
				log("missing " + diffCounter + " more mandatory tags", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
				return false;
			}
		}

		return true;
	}

	/**
	 * This methods sets the expectations for unspecified sub tags.
	 * @param allowUnspecifiedSubTags whether unspecified sub tags are to be tolerated
	 */
	public void setAllowUnspecifiedSubTags(boolean allowUnspecifiedSubTags) {
		this.allowUnspecifiedSubTags = allowUnspecifiedSubTags;
	}

	/**
	 * This method returns the number of immediate child specifications matching the provided requirement state.
	 * @param req the requirement state to match against
	 * @return number of immediate child specifications matching the provided requirement state
	 */
	public int getNoOfTagsMatchingRequirement(byte req) {
		int counter;

		counter = 0;

		for(int i = 0; i < size(); i++) {
			if(get(i).getRequired() == req) {
				counter++;
			}
		}

		return counter;
	}

	/**
	 * This method sets whether sub tags are to be expected exactly in the provided order.
	 * @param strictOrder the order in which sub tags are evaluated
	 */
	public void setStrictOrder(boolean strictOrder) {
		isStrictOrder = strictOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (allowUnspecifiedSubTags ? 1231 : 1237);
		result = prime * result + (isStrictOrder ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TlvSpecificationContainer other = (TlvSpecificationContainer) obj;
		if (allowUnspecifiedSubTags != other.allowUnspecifiedSubTags)
			return false;
		if (isStrictOrder != other.isStrictOrder)
			return false;
		return true;
	}

}
