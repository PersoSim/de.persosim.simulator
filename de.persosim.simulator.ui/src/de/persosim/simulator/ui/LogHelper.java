package de.persosim.simulator.ui;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import de.persosim.simulator.PersoSimLogTags;

public final class LogHelper
{
	public static final String PREF_LOG_LEVELS = "LOG_LEVELS";
	public static final String PREF_LOG_TAGS = "LOG_TAGS";
	public static final String PREF_DELIMITER = ":";

	private LogHelper()
	{
		// hide implicit public constructor
	}

	public static void logEnvironmentInfo()
	{
		BasicLogger.log("Starting PersoSim " + getProductVersion() + " at: " + DateFormat.getDateTimeInstance().format(new Date()), LogLevel.INFO,
				new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
		BasicLogger.log("Operating System: " + System.getProperty("os.name") + " (version: " + System.getProperty("os.version") + "; arch: " + System.getProperty("os.arch") + ")", LogLevel.INFO,
				new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
		BasicLogger.log("Java Version: " + System.getProperty("java.version") + " (vendor: " + System.getProperty("java.vendor") + ", " + System.getProperty("java.vendor.url") + ")", LogLevel.INFO,
				new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
		BasicLogger.log("Java Home: \"" + System.getProperty("java.home") + "\"", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
		BasicLogger.log("User: \"" + System.getProperty("user.name") + "\" (home: \"" + System.getProperty("user.home") + "\"; dir: \"" + System.getProperty("user.dir") + "\")", LogLevel.INFO,
				new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
	}

	/**
	 * This method returns the bundle version as a String
	 */
	public static String getProductVersion()
	{
		final IProduct product = Platform.getProduct();
		final Bundle bundle = product.getDefiningBundle();
		final Version version = bundle.getVersion();
		return version.toString();
	}
}
