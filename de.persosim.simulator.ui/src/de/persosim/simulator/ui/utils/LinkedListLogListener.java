package de.persosim.simulator.ui.utils;

import java.util.LinkedList;

import org.globaltester.logging.AbstractLogListener;
import org.globaltester.logging.LogListenerConfig;
import org.globaltester.logging.filter.AndFilter;
import org.globaltester.logging.filter.BundleFilter;
import org.globaltester.logging.filter.LevelFilter;
import org.globaltester.logging.filter.LogFilter;
import org.globaltester.logging.format.LogFormat;
import org.osgi.service.log.LogListener;

import de.persosim.simulator.ui.Activator;

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
		LogListenerConfig lrc = new LogListenerConfig() {
			
			byte logLevels [] ={1,2,3,4,5,6,120};
			String bundleList [] = {"de.persosim"};
			
			public LogFormat format = new LogFormat();
			public BundleFilter bundleFilter = new BundleFilter(bundleList);
			public LevelFilter levelFilter = new LevelFilter(logLevels);
			public LogFilter [] filters = {bundleFilter, levelFilter};	
			public AndFilter filter = new AndFilter(filters);
			
			{
				Activator.setLogLevelFilter(levelFilter);
			}
			
			@Override
			public LogFilter getFilter() {
				return filter;
			}

			@Override
			public LogFormat getFormat() {
				return format;
			}
		};
		this.maxLines = maxLines;
		setLrc(lrc);
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
