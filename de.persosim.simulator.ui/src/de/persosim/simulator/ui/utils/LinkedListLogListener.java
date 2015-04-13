package de.persosim.simulator.ui.utils;

import java.util.LinkedList;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

/**
 * This {@link LogListener} implementation is used to write log entries by line
 * into a given {@link LinkedList} using a maximum number of cached lines.
 * 
 * @author mboonk
 *
 */
public class LinkedListLogListener implements LogListener {

	private LinkedList<String> list = new LinkedList<String>();
	private int maxLines;
	private LinkedList<String> bundleFilters = new LinkedList<String>();
	private boolean needsUpdate;
	
	public LinkedListLogListener(int maxLines) {
		this.maxLines = maxLines;
	}
		
	/**
	 * Filter the incoming strings. All added filters allow the corresponding
	 * messages to be added to the list.
	 * 
	 * @param filter
	 */
	public void addFilter(String filter){
		bundleFilters.add(filter);
	}
	
	/**
	 * Empty the list of filters.
	 */
	public void cleanBundleFilters(){
		bundleFilters = new LinkedList<String>();
	}
	
	/**
	 * @return the number of lines currently in the cache
	 */
	public int getNumberOfCachedLines(){
		synchronized (this) {
			return list.size();	
		}
	}
	
	/**
	 * @param index
	 *            of the line to return
	 * @return the content of the cached line at the given index
	 */
	public String getLine(int index){
		synchronized (this) {
			return list.get(index);			
		}
	}
	
	public void resetRefreshState(){
		needsUpdate = false;
	}
	
	public boolean getRefreshNeeded(){
		return needsUpdate;
	}
	
	@Override
	public void logged(final LogEntry entry) {
		boolean isFilteredBundle = false;
		for (String current : bundleFilters){
			if (entry.getBundle().getSymbolicName().equals(current)){
				isFilteredBundle = true;
				break;
			}
		}
		//allow only filtered entries or all if no filters are set.
		if (isFilteredBundle || bundleFilters.size() == 0){
			String logEntry = "[" + entry.getBundle().getSymbolicName() + "] "
					+ entry.getMessage();
			String[] splitResult = logEntry.split("(\\n|\\r)");

			for (int i = 0; i < splitResult.length; i++) {
				needsUpdate = true;
				if (list != null) {
					if (list.size() > maxLines) {

						// synchronized is used to avoid IndexOutOfBoundsExceptions
						synchronized (this) {
							list.removeFirst();
							list.add(splitResult[i]);
						}

					} else {
						synchronized (this) {
							list.add(splitResult[i]);
						}
					}
				}
			}
		}
		
	}

}
