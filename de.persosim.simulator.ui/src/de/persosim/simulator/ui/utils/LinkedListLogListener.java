package de.persosim.simulator.ui.utils;

import java.util.LinkedList;

import org.globaltester.logging.filterservice.LogReader;
import org.globaltester.logging.filterservice.LogReaderConfig;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

/**
 * This {@link LogListener} implementation is used to write log entries by line
 * into a given {@link LinkedList} using a maximum number of cached lines.
 * 
 * @author mboonk
 *
 */
public class LinkedListLogListener extends LogReader {

	LogReaderConfig lrc = new LogReaderConfig();
	private LinkedList<String> list = new LinkedList<String>();
	private int maxLines;
	private boolean needsUpdate;	
	
	public LinkedListLogListener(int maxLines) {
		this.maxLines = maxLines;
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
	
	public boolean isRefreshNeeded(){
		return needsUpdate;
	}
	
	@Override
	public void logged(final LogEntry entry) {

		if (lrc.checkFilter(entry)) {
			// format the entry
			String logEntry = lrc.formatter.format(entry);

			// cut at line breaks and print
			String[] splitResult = logEntry.split("(\\n|\\r)");
			for (int i = 0; i < splitResult.length; i++) {
				needsUpdate = true;
				if (list != null) {
					if (list.size() > maxLines) {

						// synchronized is used to avoid
						// IndexOutOfBoundsExceptions
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
