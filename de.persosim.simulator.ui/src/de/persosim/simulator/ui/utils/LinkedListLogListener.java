package de.persosim.simulator.ui.utils;

import java.util.LinkedList;

import org.globaltester.logging.filterservice.LogFilterService;
import org.globaltester.logging.formatservice.LogFormatService;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

import de.persosim.simulator.ui.Activator;

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
	
	/**
	 * This method is used to show logEntries even if format/filter services are
	 * unavailable.
	 * 
	 * @param entry
	 *            the log message to show
	 */
	public void standardOutput(LogEntry entry){
		String[] splitResult = entry.getMessage().split("(\\n|\\r)");
		for (int i = 0; i < splitResult.length; i++) {
			System.out.println(splitResult[i]);
		}
	}
	
	@Override
	public void logged(final LogEntry entry) {
		
		LogFilterService filter = Activator.getLogFilterService();
		LogFormatService format = Activator.getLogFormatService();
		
		if (filter != null && format != null) {
			if (entry.getMessage() != null) {
				// use filter on the entry. Checks Bundle and log level
				if (filter.logFilter(entry)) {

					// format the entry
					String logEntry = format.format(entry);

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
		} else {
			// format or filter service not available...show logs anyway
			standardOutput(entry);
		}
	}
}
