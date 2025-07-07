package de.persosim.simulator.ui.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.Message;
import org.globaltester.logging.format.GtFileLogFormatter;
import org.globaltester.logging.format.LogFormat;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.PersoSimLogTags;

/**
 * This formats log messages for PersoSim UI output.
 */
public class PersoSimUILogFormatter extends GtFileLogFormatter
{
	public static final String NO_TAGS_AVAILABLE_INFO = "<no_tags>";
	private static final DateTimeFormatter PERSOSIM_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_GT_STRING).withZone(ZoneId.systemDefault());


	protected static String getTimestamp(Message msg)
	{
		String tagValue = LogFormat.extractTag(msg, BasicLogger.TIMESTAMP_TAG_ID);
		if (tagValue != null) {
			try {
				long millis = Long.parseLong(tagValue);
				return PERSOSIM_DATE_FORMATTER.format(Instant.ofEpochMilli(millis));
			}
			catch (NumberFormatException e) {
				return "";
			}
		}
		return "";
	}

	public PersoSimUILogEntry getLogEntry(Message msg)
	{
		return new PersoSimUILogEntry(msg);
	}

	public static boolean isLogTagIdSupported(String tagId)
	{
		return PersoSimLogTags.isKnownTag(tagId) || NO_TAGS_AVAILABLE_INFO.equals(tagId);
	}

	public static String format(List<LogTag> logTags, String noTagsInfo)
	{
		if (logTags == null || logTags.isEmpty()) {
			return noTagsInfo != null ? noTagsInfo : "";
		}
		StringBuilder sb = new StringBuilder(20);
		boolean foundSupportedTagId = false;
		for (LogTag logTag : logTags) {
			if (!BasicLogger.LOG_TAG_TAG_ID.equals(logTag.getId()))
				continue;
			String[] additionalData = logTag.getAdditionalData();
			if (additionalData != null && additionalData.length > 0) {
				for (String logTagEntry : additionalData) {
					if (isLogTagIdSupported(logTagEntry)) {
						foundSupportedTagId = true;
						sb.append(logTagEntry).append(", ");
					}
				}
			}
		}
		if (!foundSupportedTagId && noTagsInfo != null) {
			sb.append(noTagsInfo);
		}
		int length = sb.length();
		if (length >= 2 && sb.substring(length - 2).equals(", ")) {
			sb.setLength(length - 2); // Remove trailing comma and space
		}
		return sb.toString();
	}

	public static String format(PersoSimUILogEntry logEntry)
	{
		StringBuilder sb = new StringBuilder(200);
		sb.append(logEntry.getTimeStamp()).append(" - ").append(padRight(logEntry.getLogLevel().name(), 5)).append(" - ").append(format(logEntry.getLogTags(), NO_TAGS_AVAILABLE_INFO)).append(" - ")
				.append(logEntry.getLogContent());
		return sb.toString();
	}
}
