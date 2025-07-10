package de.persosim.simulator.log;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.Message;
import org.globaltester.logging.filter.LogFilter;
import org.globaltester.logging.filter.TagFilter;
import org.globaltester.logging.tags.LogTag;

public class PersoSimTagFilter extends TagFilter implements LogFilter
{
	public PersoSimTagFilter(String logTagId, String... data)
	{
		super(logTagId, data);
	}

	@Override
	public boolean matches(Message msg)
	{
		if (msg != null) {
			for (LogTag curTag : msg.getLogTags()) {
				if (curTag.getId().equals(logTagId)) {
					return checkTagForData(curTag);
				}
			}
		}
		// Handle NO_TAGS_AVAILABLE_INFO
		if (BasicLogger.LOG_TAG_TAG_ID.equals(logTagId)) {
			for (String current : logTagData) {
				if (PersoSimLogFormatter.NO_TAGS_AVAILABLE_INFO.equals(current))
					return true;
			}
		}
		return false;
	}

}
