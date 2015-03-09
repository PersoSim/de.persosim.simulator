package de.persosim.simulator.ui.utils;

import java.util.LinkedList;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

public class LinkedListLogListener implements LogListener {

	private LinkedList<String> list;
	private int maxLines;
	private LinkedList<String> bundleFilters = new LinkedList<String>();
	
	public void setMaxLines(int lines) {
		maxLines = lines;
	}

	public void setLinkedList(LinkedList<String> list) {
		this.list = list;
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
				if (list != null) {
					if (list.size() > maxLines) {

						// synchronized is used to avoid IndexOutOfBoundsExceptions
						synchronized (list) {
							list.removeFirst();
							list.add(splitResult[i]);
						}

					} else {
						synchronized (list) {
							list.add(splitResult[i]);
						}
					}
				}
			}
		}
		
	}

}
