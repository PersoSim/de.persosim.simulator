package de.persosim.simulator.preferences;

import de.persosim.simulator.utils.PreferenceAccessor;

public class PersoSimPreferenceManager {
	
	static PreferenceAccessor preferenceAccessor;

	public static void setPreferenceAccessor(PreferenceAccessor preferenceAccessor) {
		PersoSimPreferenceManager.preferenceAccessor = preferenceAccessor;
	}

	public static void storePreference(String key, String value){
		preferenceAccessor.set(key, value);
	}
	
	public static String getPreference(String key){
		return preferenceAccessor.get(key);
	}
}
