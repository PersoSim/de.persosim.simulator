package de.persosim.simulator.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.Message;
import org.globaltester.logging.format.LogFormat;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

public class PersoSimLogEntry
{
	public static final int DEFAULT_COLOR_ID = 2; // SWT.COLOR_BLACK
	public static final int INFO_COLOR_ID = 6; // SWT.COLOR_DARK_GREEN
	public static final int WARN_COLOR_ID = 8; // SWT.COLOR_DARK_YELLOW
	public static final int ERROR_COLOR_ID = 3; // SWT.COLOR_RED

	public static final int DEFAULT_FONT_STYLE = 0; // SWT.NORMAL
	public static final int BOLD_FONT_STYLE = 1; // SWT.BOLD
	public static final int ITALIC_FONT_STYLE = 2; // SWT.ITALIC // Does not work properly

	public enum PersoSimLogLevel
	{
		FATAL(LogLevel.FATAL, ERROR_COLOR_ID, BOLD_FONT_STYLE), //
		ERROR(LogLevel.ERROR, ERROR_COLOR_ID, BOLD_FONT_STYLE), //
		WARN(LogLevel.WARN, WARN_COLOR_ID, BOLD_FONT_STYLE), //
		INFO(LogLevel.INFO, INFO_COLOR_ID, DEFAULT_FONT_STYLE), //
		DEBUG(LogLevel.DEBUG, DEFAULT_COLOR_ID, DEFAULT_FONT_STYLE), //
		TRACE(LogLevel.TRACE, DEFAULT_COLOR_ID, DEFAULT_FONT_STYLE);

		private LogLevel logLevel = LogLevel.DEBUG;
		private int colorId = DEFAULT_COLOR_ID;
		private int fontStyle = DEFAULT_FONT_STYLE;

		PersoSimLogLevel(LogLevel logLevel, int colorId, int fontStyle)
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

		public static Optional<PersoSimLogLevel> getByLogLevel(LogLevel level)
		{
			return Arrays.stream(PersoSimLogLevel.values()).filter(value -> value.getLogLevel() == level).findFirst();
		}
	} // PersoSimLogLevel

	private String logContent;
	private final String timeStamp;
	private final LogLevel logLevel;
	private List<LogTag> logTags = new ArrayList<>();
	private String logTagsFormatted;


	public PersoSimLogEntry(String logContent, String timeStamp, LogLevel logLevel, List<LogTag> logTags)
	{
		this.logContent = logContent;
		this.timeStamp = timeStamp;
		this.logLevel = logLevel;
		if (logTags != null)
			this.logTags = logTags;
	}

	public PersoSimLogEntry(String logContent, String timeStamp, LogLevel logLevel)
	{
		this.logContent = logContent;
		this.timeStamp = timeStamp;
		this.logLevel = logLevel;
	}

	public PersoSimLogEntry(Message msg)
	{
		logContent = msg.getMessageContent();
		timeStamp = PersoSimLogFormatter.getTimestamp(msg);
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
		Optional<PersoSimLogLevel> levelOptional = PersoSimLogLevel.getByLogLevel(logLevel);
		if (levelOptional.isPresent())
			colorId = levelOptional.get().getColorId();
		return colorId;
	}

	public int getFontStyle()
	{
		int fontStyle = DEFAULT_FONT_STYLE;
		Optional<PersoSimLogLevel> levelOptional = PersoSimLogLevel.getByLogLevel(logLevel);
		if (levelOptional.isPresent())
			fontStyle = levelOptional.get().getFontStyle();
		if (getLogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.APDU_TAG_ID) != null)
			fontStyle = BOLD_FONT_STYLE; // ITALIC does not work properly
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

