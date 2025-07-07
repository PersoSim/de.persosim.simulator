package de.persosim.simulator.ui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.Message;
import org.globaltester.logging.format.LogFormat;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.PersoSimLogTags;

public class PersoSimUILogEntry
{
	public static final int DEFAULT_COLOR_ID = SWT.COLOR_BLACK;
	public static final int DEFAULT_FONT_STYLE = SWT.NORMAL;

	public enum PersoSimUILogLevel
	{
		FATAL(LogLevel.FATAL, SWT.COLOR_RED, SWT.BOLD), //
		ERROR(LogLevel.ERROR, SWT.COLOR_RED, SWT.BOLD), //
		WARN(LogLevel.WARN, SWT.COLOR_DARK_YELLOW, SWT.BOLD), //
		INFO(LogLevel.INFO, SWT.COLOR_DARK_GREEN, DEFAULT_FONT_STYLE), //
		DEBUG(LogLevel.DEBUG, DEFAULT_COLOR_ID, DEFAULT_FONT_STYLE), //
		TRACE(LogLevel.TRACE, DEFAULT_COLOR_ID, DEFAULT_FONT_STYLE);

		private LogLevel logLevel = LogLevel.DEBUG;
		private int colorId = SWT.COLOR_BLACK;
		private int fontStyle = SWT.NORMAL;

		PersoSimUILogLevel(LogLevel logLevel, int colorId, int fontStyle)
		{
			this.logLevel = logLevel;
			this.colorId = colorId;
			this.fontStyle = fontStyle;
		}

		public LogLevel getLogLevel()
		{
			return logLevel;
		}

		public int getColorId()
		{
			return colorId;
		}

		public int getFontStyle()
		{
			return fontStyle;
		}

		public static Optional<PersoSimUILogLevel> getByLogLevel(LogLevel level)
		{
			return Arrays.stream(PersoSimUILogLevel.values()).filter(value -> value.getLogLevel() == level).findFirst();
		}
	} // PersoSimUILogLevel

	private String logContent;
	private final String timeStamp;
	private final LogLevel logLevel;
	private List<LogTag> logTags = new ArrayList<>();
	private String logTagsFormatted;


	public PersoSimUILogEntry(String logContent, String timeStamp, LogLevel logLevel, List<LogTag> logTags)
	{
		this.logContent = logContent;
		this.timeStamp = timeStamp;
		this.logLevel = logLevel;
		if (logTags != null)
			this.logTags = logTags;
	}

	public PersoSimUILogEntry(String logContent, String timeStamp, LogLevel logLevel)
	{
		this.logContent = logContent;
		this.timeStamp = timeStamp;
		this.logLevel = logLevel;
	}

	public PersoSimUILogEntry(Message msg)
	{
		logContent = msg.getMessageContent();
		timeStamp = PersoSimUILogFormatter.getTimestamp(msg);
		String logLevelMsg = LogFormat.getLogLevel(msg);
		if (!LogFormat.LOG_LEVEL_UNKNOWN.equals(logLevelMsg))
			logLevel = LogLevel.valueOf(logLevelMsg);
		else
			logLevel = LogLevel.TRACE;
		logTags = msg.getLogTags();
		LogTag logTagEx = getLogTag(BasicLogger.EXCEPTION_STACK_TAG_ID);
		if (logTagEx != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(logContent).append('\n').append(logTagEx.getAdditionalData()[0]);
			logContent = sb.toString();
		}
	}

	public String getLogContent()
	{
		return logContent;
	}

	public String getTimeStamp()
	{
		return timeStamp;
	}

	public LogLevel getLogLevel()
	{
		return logLevel;
	}

	public List<LogTag> getLogTags()
	{
		return logTags;
	}

	private LogTag getLogTag(String id, String... additionalData)
	{
		for (LogTag current : logTags) {
			// Check only 1st additional data at the moment!
			if (current.getId().equals(id) && (additionalData == null || additionalData.length == 0 || current.getAdditionalData()[0].equals(additionalData[0])))
				return current;
		}
		return null;
	}

	public int getColorId()
	{
		int colorId = DEFAULT_COLOR_ID;
		Optional<PersoSimUILogLevel> levelOptional = PersoSimUILogLevel.getByLogLevel(logLevel);
		if (levelOptional.isPresent())
			colorId = levelOptional.get().getColorId();
		return colorId;
	}

	public int getFontStyle()
	{
		int fontStyle = DEFAULT_FONT_STYLE;
		Optional<PersoSimUILogLevel> levelOptional = PersoSimUILogLevel.getByLogLevel(logLevel);
		if (levelOptional.isPresent())
			fontStyle = levelOptional.get().getFontStyle();
		if (getLogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.APDU_TAG_ID) != null)
			fontStyle = SWT.BOLD; // SWT.ITALIC does not work properly
		return fontStyle;
	}

	public String getLogTagsFormatted()
	{
		return logTagsFormatted;
	}

	public void setLogTagsFormatted(String logTagsFormatted)
	{
		this.logTagsFormatted = logTagsFormatted;
	}

}

