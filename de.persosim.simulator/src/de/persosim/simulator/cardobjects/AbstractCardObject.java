package de.persosim.simulator.cardobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import de.persosim.simulator.secstatus.SecStatus;

/**
 * Abstract superclass for most/all CardObjects. This implements handling of
 * bidirectional parent/child relation.
 * 
 * @author amay
 * 
 */
@XmlTransient
public abstract class AbstractCardObject implements CardObject, Iso7816LifeCycle {

	@XmlTransient
	protected CardObject parent;
	
	@XmlElementWrapper
	@XmlAnyElement(lax=true)
	protected List<CardObject> children = new ArrayList<>();
	SecStatus securityStatus;
	
	@XmlElement
	protected Iso7816LifeCycleState lifeCycleState = Iso7816LifeCycleState.CREATION;

	@Override
	public void setSecStatus(SecStatus securityStatus){
		this.securityStatus = securityStatus;
		
		//forward the SecStatus to all children
		for (CardObject curChild : getChildren()) {
			curChild.setSecStatus(securityStatus);
		}
	}
	
	@Override
	public CardObject getParent() {
		return parent;
	}

	@Override
	public Collection<CardObject> getChildren() {
		return children;
	}

	/**
	 * Add new child to the collection.
	 * <p/>
	 * This method also sets the SecStatus of the new child. If the new child is of
	 * type AbstractCardObject also the parent is set.
	 * 
	 * @param newChild
	 *            child to add to the collection
	 */
	public void addChild(CardObject newChild) {
		children.add(newChild);
		if (newChild instanceof AbstractCardObject) {
			((AbstractCardObject) newChild).parent = this;
		}
		newChild.setSecStatus(securityStatus);
	}

	/**
	 * Remove child from the collection.
	 * 
	 * If the given child is of type AbstractCardObject its parent field will be
	 * reset to null after successful removal.
	 * 
	 * If the given element is not a child nothing will be done at all.
	 * 
	 * @param child
	 *            element to remove from the collection
	 */
	public void removeChild(CardObject child) {
		if (children.contains(child)) {
			children.remove(child);
			if (child instanceof AbstractCardObject) {
				((AbstractCardObject) child).parent = null;
			}
		}
	}

	@Override
	public Iso7816LifeCycleState getLifeCycleState() {
		return lifeCycleState;
	}

	@Override
	public void updateLifeCycleState(Iso7816LifeCycleState state) {
		//XXX MBK check for life cycle change access rights
		//XXX who is allowed to set life cycle state during object initialization?
		//XXX what is the default life cycle state directly after object initialization (if no state was explicitly provided)?
		lifeCycleState = state;
	}
	
	@Override
	public Collection<CardObject> findChildren(CardObjectIdentifier... cardObjectIdentifiers) {
		if(cardObjectIdentifiers.length == 0) {throw new IllegalArgumentException("must provide at least 1 identifier");}
		
		Collection<CardObject> matchingChildren = new ArrayList<>();
		
		//check the immediate children of the current DF
		boolean fullMatch;
		for (CardObject curChild : getChildren()){
			fullMatch = true;
			for(CardObjectIdentifier cardObjectIdentifier : cardObjectIdentifiers) {
				if (!cardObjectIdentifier.matches(curChild)){
					fullMatch = false;
					break;		
				}
			}
			
			if(fullMatch) {
				matchingChildren.add(curChild);
			}
		}
		
		// if no fitting child has been found, collection is empty
		return matchingChildren;
	}
	
	/**
	 * JAXB callback
	 * <p/>
	 * Used to erase wrapper element for children if children is empty.
	 * @param m
	 */
	protected void beforeMarshal(Marshaller m){
		if ((children != null) && (children.isEmpty())) {
			children = null;
		}
	}
	
	/**
	 * JAXB callback
	 * <p/>
	 * Used to fix the parent relation
	 * @param u
	 * @param parent
	 */
	protected void afterUnmarshal(Unmarshaller u, Object parent) {
		if (parent instanceof CardObject) { 
			this.parent = (CardObject)parent;
		}
	}
	
}
