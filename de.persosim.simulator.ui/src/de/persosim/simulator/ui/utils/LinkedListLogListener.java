package de.persosim.simulator.ui.utils;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import org.globaltester.logging.AbstractLogListener;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.LogListenerConfig;
import org.globaltester.logging.filter.AndFilter;
import org.globaltester.logging.filter.BundleFilter;
import org.globaltester.logging.filter.LogFilter;
import org.globaltester.logging.filter.TagFilter;
import org.globaltester.logging.format.GtFileLogFormatter;
import org.globaltester.logging.format.LogFormatService;
import org.osgi.service.log.LogListener;

import de.persosim.simulator.preferences.PersoSimPreferenceManager;

/**
 * This {@link LogListener} implementation is used to write log entries by line
 * into a given {@link LinkedList} using a maximum number of cached lines.
 * 
 * @author mboonk
 *
 */
public class LinkedListLogListener extends AbstractLogListener {

	private LinkedList<String> list = new LinkedList<String>();
	private int maxLines;
	private boolean needsUpdate;
	
	public LinkedListLogListener(int maxLines) {
		updateConfig();
		this.maxLines = maxLines;
	}
	
	public void updateConfig() {
		LogListenerConfig lrc = new LogListenerConfig() {
			String bundleList [] = {"org.globaltester", "de.persosim"};
			
			private LogFormatService format = new GtFileLogFormatter(new SimpleDateFormat(GtFileLogFormatter.DATE_FORMAT_GT_STRING));
			private LogFilter filter;
						
			@Override
			public LogFilter getFilter() {
				if (filter == null) {
					String levelsPreference = PersoSimPreferenceManager.getPreference("LOG_LEVELS");
					
					String [] levels = null;
					TagFilter tagFilter = null;
					if (levelsPreference != null) {
						levels = levelsPreference.split(":");
						tagFilter = new TagFilter(BasicLogger.LOG_LEVEL_TAG_ID, levels);
					}
					
					BundleFilter bundleFilter = new BundleFilter(bundleList);
					if (tagFilter != null) {
						LogFilter [] filters = {bundleFilter, tagFilter};	
						filter = new AndFilter(filters);	
					} else {
						filter = bundleFilter;
					}
				}
				
				return filter;
			}

			@Override
			public LogFormatService getFormat() {
				return format;
			}
		};
		setConfig(lrc);
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
	public void displayLogMessage(String msg) {
		// cut at line breaks and print
		String[] splitResult = msg.split("(\\n|\\r)");
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
