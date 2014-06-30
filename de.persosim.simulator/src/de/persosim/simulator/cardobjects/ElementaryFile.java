package de.persosim.simulator.cardobjects;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.persosim.simulator.secstatus.SecCondition;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.Utils;

/**
 * This class represents an ISO7816-4 compliant elementary file in the object hierarchy on the card
 * @author mboonk
 *
 */
@XmlRootElement
public class ElementaryFile extends AbstractFile {

	@XmlElement
	@XmlJavaTypeAdapter(HexBinaryAdapter.class)
	private byte[] content;
	
	@XmlElement
	private ShortFileIdentifier shortFileIdentifier;

	@XmlElementWrapper
	@XmlAnyElement(lax=true)
	private Collection<SecCondition> readingConditions;
	
	@XmlElementWrapper
	@XmlAnyElement(lax=true)
	private Collection<SecCondition> writingConditions;
	
	@XmlElementWrapper
	@XmlAnyElement(lax=true)
	private Collection<SecCondition> erasingConditions; //IMPL MBK implement the ISO7816 erase functionality for files
	
	/**
	 * Default constructor fur JAXB usage.
	 */
	public ElementaryFile(){
		
	}
			
	public ElementaryFile(FileIdentifier fileIdentifier,
			ShortFileIdentifier shortFileIdentifier, byte[] content, Collection<SecCondition> readingConditions, Collection<SecCondition> writingConditions, Collection<SecCondition> erasingConditions) {
		super(fileIdentifier);
		this.shortFileIdentifier = shortFileIdentifier;
		this.content = content;
		this.readingConditions = readingConditions;
		this.writingConditions = writingConditions;
		this.erasingConditions = erasingConditions;
	}

	@Override
	public Collection<CardObject> getChildren() {
		return Collections.emptySet();
	}

	/**
	 * Reads the files internal data.
	 * @return stored data as byte array
	 */
	public byte[] getContent() throws AccessDeniedException {
		for (SecCondition condition : readingConditions){
			if (condition.check(securityStatus.getCurrentMechanisms(SecContext.APPLICATION, condition.getNeededMechanisms()))){
				return Arrays.copyOf(content, content.length);
			}
		}
		throw new AccessDeniedException("Reading forbidden");
	}

	/**
	 * Replaces the files internal data.
	 * @param data to be used as a replacement
	 */
	public void update(int offset, byte[] data) throws AccessDeniedException {
		for (SecCondition condition : writingConditions){
			if (condition.check(securityStatus.getCurrentMechanisms(SecContext.APPLICATION, condition.getNeededMechanisms()))){
				for(int i = 0; i < data.length; i++){
					content[i + offset] = data[i];
				}
				return;
			}
		}
		throw new AccessDeniedException("Updating forbidden");
	}

	@Override
	public void addChild(CardObject newChild) {
	}

	@Override
	public ConstructedTlvDataObject getFileControlParameterDataObject() {
		ConstructedTlvDataObject result = super
				.getFileControlParameterDataObject();

		result.addTlvDataObject(new PrimitiveTlvDataObject(new TlvTag(
				(byte) 0x80), Utils.removeLeadingZeroBytes(Utils
				.toUnsignedByteArray(content.length))));

		result.addTlvDataObject(new PrimitiveTlvDataObject(new TlvTag(
				(byte) 0x88), Utils
				.toUnsignedByteArray((byte) shortFileIdentifier
						.getShortFileIdentifier())));

		return result;
	}

	@Override
	public Collection<CardObjectIdentifier> getAllIdentifiers() {
		Collection<CardObjectIdentifier> result = super.getAllIdentifiers();
		result.add(shortFileIdentifier);
		return result;
	}
	
	/**
	 * JAXB callback
	 * <p/>
	 * Used to erase wrapper elements for empty collections
	 * @param m
	 */
	protected void beforeMarshal(Marshaller m){
		super.beforeMarshal(m);
		if ((readingConditions != null) && (readingConditions.isEmpty())) {
			readingConditions = null;
		}
		if ((writingConditions != null) && (writingConditions.isEmpty())) {
			writingConditions = null;
		}
		if ((erasingConditions != null) && (erasingConditions.isEmpty())) {
			erasingConditions = null;
		}
	}
	
	/**
	 * JAXB callback
	 * <p/>
	 * Used to initialize undefined access conditions with empty sets => forbidden
	 * @param u
	 * @param parent
	 */
	protected void afterUnmarshal(Unmarshaller u, Object parent) {
		super.afterUnmarshal(u, parent);
		if (readingConditions == null) readingConditions = Collections.emptySet();
		if (writingConditions == null) writingConditions = Collections.emptySet();
		if (erasingConditions == null) erasingConditions = Collections.emptySet();
	}

}
