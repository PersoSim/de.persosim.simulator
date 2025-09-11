package de.persosim.simulator.preferences;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.utils.PreferenceAccessor;

public class PersoSimPreferenceManager
{
	private PersoSimPreferenceManager()
	{
		// hide implicit public constructor
	}

	static PreferenceAccessor preferenceAccessor;

	public static void setPreferenceAccessorIfNotAvailable(PreferenceAccessor preferenceAccessor)
	{
		if (PersoSimPreferenceManager.preferenceAccessor == null)
			PersoSimPreferenceManager.preferenceAccessor = preferenceAccessor;
	}

	public static void storePreference(String key, String value)
	{
		preferenceAccessor.set(key, value);
		BasicLogger.log("Stored in preferences: '" + key + "' : '" + value + "'", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
	}

	public static String getPreference(String key)
	{
		return getPreference(key, null, false);
	}

	public static String getPreference(String key, boolean doLogging)
	{
		return getPreference(key, null, doLogging);
	}

	public static String getPreference(String key, String defaultValue)
	{
		return getPreference(key, defaultValue, false);
	}

	public static String getPreference(String key, String defaultValue, boolean doLogging)
	{
		if (preferenceAccessor.get(key) != null) {
			String value = preferenceAccessor.get(key);
			if (doLogging) {
				BasicLogger.log("Loaded from preferences: '" + key + "' : '" + value + "'", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
			}
			return value;
		}
		if (doLogging) {
			BasicLogger.log("Not found in preferences: '" + key + "'; use given default value: '" + defaultValue + "'", LogLevel.DEBUG,
					new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.SYSTEM_TAG_ID));
		}
		return defaultValue;
	}
}
