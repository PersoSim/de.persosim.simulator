package de.persosim.simulator.cardobjects;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import de.persosim.simulator.platform.CommandProcessor;

/**
 * This class is a container for a tree of CardObjects (e.g. DFs, EF,
 * AuthObjects like PINs).
 * 
 * It stores current selection state, handles object requests and returns
 * objects that restrict access according to the request and current SecStatus.
 * 
 * The ObjectStore is owned and initialized by the {@link CommandProcessor}.
 * Modifications to the ObjectStore and the Objects located herein are performed
 * by the protocols on their own. Access rights and SecurityStatus are checked
 * within the objects themselves.
 * 
 * @author amay
 * 
 */
public class ObjectStore {

	/**
	 * handle for the root object, required for default selection/select MF etc.
	 */
	private MasterFile masterFile; 
	
	public MasterFile getMasterFile() {
		return masterFile;
	}

	/**
	 * handle for the currently selected file, this is the last file selected
	 */
	private CardFile currentFile;

	/**
	 * The cachedFile is the last {@link CardFile} that was returned by
	 * {@link #getObject(CardObjectIdentifier, Scope)}.
	 */
	private CardFile cachedFile;

	public ObjectStore(MasterFile masterFile){
		this.masterFile = masterFile;
	}
	
	/**
	 * Search for a {@link CardObject} reachable from current selection, select
	 * it if its a CardFile and return it. This file is also cached for later
	 * selection. If no fitting {@link CardObject} can be found, a
	 * {@link NullCardObject} is returned.
	 * 
	 * @param id
	 *            identifier that describes the object to return
	 * @param scope
	 *            scope in which to search using the given identifier
	 * @return the described CardObject that enforces access mode according to
	 *         mode parameter or a {@link NullCardobject}
	 */
	public CardObject getObject(CardObjectIdentifier id, Scope scope) {
		Collection<CardObject> result = searchObjects(id, scope, true);
		if(result.isEmpty()) {
			return new NullCardObject();
		}
		CardObject selected = result.iterator().next();
		if (selected instanceof CardFile) {
			cachedFile = (CardFile) selected;
		} else {
			cachedFile = null;
		}
		return selected;
	}
	
	/**
	 * Search for all {@link CardObject}s with a particular id reachable from
	 * current selection and return it. If no fitting {@link CardObject} can be
	 * found, an empty set is returned.
	 * 
	 * @param id
	 *            identifier that describes the objects to return
	 * @param scope
	 *            scope in which to search using the given identifier
	 * @return the described CardObject that enforces access mode according to
	 *         mode parameter or an empty set
	 */
	public Collection<CardObject> getObjectsWithSameId(CardObjectIdentifier id, Scope scope) {
		return searchObjects(id, scope, false);
	}

	/**
	 * Selection of {@link CardFile} objects.
	 * 
	 * @param id
	 *            {@link FileIdentifier} to match against
	 * @param scope
	 *            {@link Scope} to search in
	 * @param acc
	 *            required access mode
	 * @return the selected CardFile
	 * @throws FileNotFoundException
	 *             if no file matches the identifier in the given scope
	 */
	public CardFile selectFile(CardObjectIdentifier id, Scope scope)
			throws FileNotFoundException {
		Collection<CardObject> result = searchObjects(id, scope, true);
		CardObject newSelection = result.iterator().next();
		if (!(newSelection instanceof NullCardObject)) {
			currentFile = (CardFile) newSelection;
			return currentFile;
		}
		throw new FileNotFoundException();
	}

	/**
	 * Selects a file for personalization. This method DOES NOT check if the
	 * selected file is part of the existing object tree.
	 * 
	 * @param file
	 */
	public void selectFileForPersonalization(CardFile file) {
		// IMPL ObjectStore life cycle as defined for ISO card life cycles, check it here
		currentFile = file;
	}

	/**
	 * Selects implicitly the last used file. This selects the last
	 * {@link CardFile} that was returned as a result to
	 * {@link #getObject(CardObjectIdentifier, Scope)}.
	 * 
	 * @throws FileNotFoundException
	 */
	public void selectCachedFile() throws FileNotFoundException {
		if (cachedFile != null) {
			currentFile = (CardFile) cachedFile;
			return;
		}
		throw new FileNotFoundException();
	}

	/**
	 * @return the last successfully selected file
	 */
	public CardFile getCurrentFile() {
		cachedFile = currentFile;
		return (CardFile) currentFile;
	}

	/**
	 * Selects the master file
	 * 
	 * @return the selected file
	 */
	public MasterFile selectMasterFile() {
		currentFile = masterFile;
		return (MasterFile) currentFile;
	}
	
	/**
	 * Search {@link CardObject}s starting from the currentFile
	 * 
	 * @param id
	 *            identifier that describes the object to return
	 * @param scope
	 *            scope in which to search using the given identifier
	 * @param onlyFirst
	 *            if true, only the first found object will be returned
	 * @return the searched objects or an empty set if no fitting object was
	 *         found
	 */
	private Collection<CardObject> searchObjects(CardObjectIdentifier id, Scope scope, boolean onlyFirst) {
		CardObject searchRoot = null;

		switch (scope) {
		case FROM_DF:
			if (currentFile instanceof DedicatedFile) {
				searchRoot = currentFile;
			} else {
				searchRoot = findFirstParentDedicatedFile(currentFile);
			}
			break;
		case FROM_MF:
			searchRoot = masterFile;
			break;
		default:
			break;
		}

		if (searchRoot != null) {
			return findChildren(id, searchRoot, onlyFirst);
		}
		return Collections.emptySet();
	}
	
	/**
	 * Search for children {@link CardObject}s in the object stores
	 * tree, starting in the given {@link CardObject}.
	 * </p>
	 * Search pattern (according to ISO7816-4 7.1.1):
	 * <ol>
	 * <li>
	 *	 immediate children of currently selected DF
	 * </li>
	 * <li>
	 *	 the parent DF of the currently selected DF
	 * </li>
	 * <li>
	 *	 immediate children of the parent DF
	 * </li>
	 * 
	 * @param identifier
	 *            to match the {@link CardObject}s with
	 * @param {@link CardObject} to start the search with
	 * @param onlyFirst if true, only the first found child will be returned
	 * @return children that fit the given identifier or an empty set
	 *         if no fitting child was found
	 */
	private Collection<CardObject> findChildren(CardObjectIdentifier id, CardObject currentObject, boolean onlyFirst) {
		
		DedicatedFile currentDf;
		
		//search starts in the current DF
		if (currentObject instanceof DedicatedFile){	
			currentDf = (DedicatedFile) currentObject;
		} else {
			currentDf = findFirstParentDedicatedFile(currentObject);
		}
		
		HashSet<CardObject> children = new HashSet<CardObject>();
		
		//check the immediate children of the current DF
		for (CardObject curChild : currentDf.getChildren()){
			if (id.matches(curChild)){
				children.add(curChild);
				if(onlyFirst) {
					return children;
				}
			}
		}
		
		//check the parentDF
		DedicatedFile parentDf = findFirstParentDedicatedFile(currentDf);
		if (id.matches(parentDf)){
			children.add(parentDf);
			if(onlyFirst) {
				return children;
			}
		}
		//check for parent DF immediate children
		for (CardObject curChild : parentDf.getChildren()){
			if (id.matches(curChild)){
				children.add(curChild);
				if(onlyFirst) {
					return children;
				}
			}
		}
		
		if(!children.isEmpty()){
			return children;
		}
		
		// No fitting child found
		return Collections.emptySet();
	}

	/**
	 * Search for the first dedicated file parent of a given {@link CardObject}
	 * if it is not itself a {@link DedicatedFile}.
	 * 
	 * @param currentObject
	 *            {@link CardObject} to start the search with
	 * @return the first matching parent, or the given object, if it is a
	 *         {@link DedicatedFile}
	 */
	private DedicatedFile findFirstParentDedicatedFile(CardObject currentObject) {
		while (!(currentObject instanceof DedicatedFile)) {
			currentObject = currentObject.getParent();
		}
		return (DedicatedFile)currentObject;
	}
	
	public void reset() {
		selectFileForPersonalization(masterFile);
		cachedFile = null;
	}
}
