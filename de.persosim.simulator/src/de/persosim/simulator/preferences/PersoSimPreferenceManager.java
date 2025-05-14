package de.persosim.simulator.preferences;

import de.persosim.simulator.utils.PreferenceAccessor;

public class PersoSimPreferenceManager {

	private PersoSimPreferenceManager() {
		// hide implicit public constructor
	}

	static PreferenceAccessor preferenceAccessor;

	public static void setPreferenceAccessorIfNotAvailable(PreferenceAccessor preferenceAccessor) {
		if (PersoSimPreferenceManager.preferenceAccessor == null)
			PersoSimPreferenceManager.preferenceAccessor = preferenceAccessor;
	}

	public static void storePreference(String key, String value) {
		preferenceAccessor.set(key, value);
	}

	public static String getPreference(String key) {
		return getPreference(key, null);
	}

	public static String getPreference(String key, String defaultValue) {
		if (preferenceAccessor.get(key) != null)
			return preferenceAccessor.get(key);
		return defaultValue;
	}
}
