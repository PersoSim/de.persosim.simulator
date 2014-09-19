package de.persosim.simulator.cardobjects;

import java.io.FileNotFoundException;

import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.secstatus.SecStatus;

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
	
	/**
	 * handle for the currently selected file, this is the last file selected
	 */
	private CardFile currentFile;

	/**
	 * The cachedFile is the last {@link CardFile} that was returned by
	 * {@link #getObject(CardObjectIdentifier, Scope)}.
	 */
	private CardFile cachedFile;

	public ObjectStore(SecStatus securityStatus){
		reset(securityStatus);
	}
	
	/**
	 * Search for a {@link CardObject} reachable from current selection, select
	 * it if its a CardFile and return it. This file is also cached for later
	 * selection. If no fitting {@link CardObject} can be found, a
	 * {@link NullCardObject} is returned.
	 * 
	 * @param id
	 *            identifier that describes the object to return
	 * @param acc
	 *            access mode that is required for the object
	 * @param scope
	 *            scope in which to search using the given identifier
	 * @return the described CardObject that enforces access mode according to
	 *         mode parameter or a {@link NullCardobject}
	 */
	public CardObject getObject(CardObjectIdentifier id, Scope scope) {
		CardObject selected = searchObject(id, scope);
		if (selected instanceof CardFile) {
			cachedFile = (CardFile) selected;
		} else {
			cachedFile = null;
		}
		return selected;
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
		CardObject newSelection = searchObject(id, scope);
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
	 * Search a CardObject starting from the currentFile
	 * 
	 * @param scope
	 * @return the searched object or {@link NullCardObject} if no fitting
	 *         object was found
	 */
	private CardObject searchObject(CardObjectIdentifier id, Scope scope) {
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
			return findFirstChild(id, searchRoot);
		}
		return new NullCardObject();
	}

	/**
	 * Search for a child {@link CardObject} in the object stores
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
	 * @return a child that fits the given identifier or {@link NullCardObject}
	 *         if no fitting child was found
	 */
	private CardObject findFirstChild(CardObjectIdentifier id,
			CardObject currentObject) {
		
		DedicatedFile currentDf;
		
		//search starts in the current DF
		if (currentObject instanceof DedicatedFile){	
			currentDf = (DedicatedFile) currentObject;
		} else {
			currentDf = findFirstParentDedicatedFile(currentObject);
		}
		
		//check the immediate children of the current DF
		for (CardObject curChild : currentDf.getChildren()){
			if (id.matches(curChild)){
				return curChild;
			}
		}
		
		//check the parentDF
		DedicatedFile parentDf = findFirstParentDedicatedFile(currentDf);
		if (id.matches(parentDf)){
			return parentDf;
		}
		//check for parent DF immediate children
		for (CardObject curChild : parentDf.getChildren()){
			if (id.matches(curChild)){
				return curChild;
			}
		}
		
		// No fitting child found
		return new NullCardObject();
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

	public void reset(SecStatus securityStatus) {
		reset (new MasterFile(), securityStatus);
	}
	
	public void reset(MasterFile newMasterFile, SecStatus securityStatus) {
		masterFile = newMasterFile;
		selectFileForPersonalization(masterFile);
		masterFile.setSecStatus(securityStatus);
		cachedFile = null;
	}
}
