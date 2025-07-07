package de.persosim.simulator.ui.utils;

import java.util.LinkedList;

import org.globaltester.logging.AbstractLogListener;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.LogListenerConfig;
import org.globaltester.logging.Message;
import org.globaltester.logging.filter.AndFilter;
import org.globaltester.logging.filter.LogFilter;
import org.globaltester.logging.filter.NullFilter;
import org.globaltester.logging.filter.TagFilter;
import org.globaltester.logging.format.LogFormatService;
import org.osgi.service.log.LogListener;

import de.persosim.simulator.preferences.PersoSimPreferenceManager;
import de.persosim.simulator.ui.LogHelper;

/**
 * This {@link LogListener} implementation is used to write log entries
 * into a given {@link LinkedList} using a maximum number of entries.
 */
public class LinkedListLogListener extends AbstractLogListener
{
	private LinkedList<PersoSimUILogEntry> list = new LinkedList<>();
	private int maxLines;
	private boolean haveToRefresh;

	public LinkedListLogListener(int maxLines)
	{
		this.maxLines = maxLines;
		setRefreshState(true);
		updateConfig();
	}

	public void updateConfig()
	{
		LogListenerConfig lrc = new LogListenerConfig() {

			private LogFormatService formatter = new PersoSimUILogFormatter();
			private LogFilter filter = null;

			@Override
			public LogFilter getFilter()
			{
				if (filter == null && haveToRefresh) {
					String preferenceLogLevels = PersoSimPreferenceManager.getPreference(LogHelper.PREF_LOG_LEVELS);
					String preferenceLogTags = PersoSimPreferenceManager.getPreference(LogHelper.PREF_LOG_TAGS);
					LogFilter filterLogLevels = null;
					if (preferenceLogLevels != null) {
						String[] levels = preferenceLogLevels.split(LogHelper.PREF_DELIMITER);
						filterLogLevels = new TagFilter(BasicLogger.LOG_LEVEL_TAG_ID, levels);
					}
					LogFilter filterLogTags = null;
					if (preferenceLogTags != null) {
						String[] tags = preferenceLogTags.split(LogHelper.PREF_DELIMITER);
						filterLogTags = new PersoSimTagFilter(BasicLogger.LOG_TAG_TAG_ID, tags);
					}
					if (preferenceLogLevels != null && preferenceLogTags == null)
						filter = filterLogLevels;
					else if (preferenceLogLevels == null && preferenceLogTags != null)
						filter = filterLogTags;
					else if (preferenceLogLevels != null)
						filter = new AndFilter(filterLogLevels, filterLogTags);
					else
						filter = new NullFilter();
					haveToRefresh = false;
				}

				return filter;
			}

			@Override
			public LogFormatService getFormat()
			{
				return formatter;
			}
		};
		setConfig(lrc);
	}

	/**
	 * @return the number of entries currently in the cache
	 */
	public int getNumberOfCachedEntries()
	{
		synchronized (this) {
			return list.size();
		}
	}

	/**
	 * @param index
	 *            of the entry to return
	 * @return the content of the cached entry at the given index
	 * @throws IndexOutOfBoundsException
	 *             if index is invalid
	 */
	public PersoSimUILogEntry getEntry(int index)
	{
		synchronized (this) {
			return list.get(index);
		}
	}

	public void setRefreshState(boolean haveToRefresh)
	{
		this.haveToRefresh = haveToRefresh;
	}

	public boolean isRefreshNeeded()
	{
		return haveToRefresh;
	}

	@Override
	public void displayLogMessage(String msg)
	{
		// do nothing
	}

	@Override
	public void log(Message msg)
	{
		if (config.getFilter().matches(msg)) {
			PersoSimUILogEntry entry = ((PersoSimUILogFormatter) config.getFormat()).getLogEntry(msg);
			synchronized (this) {
				if (list.size() >= maxLines) {
					list.removeFirst();
				}
				list.add(entry);
			}
		}
	}

}
