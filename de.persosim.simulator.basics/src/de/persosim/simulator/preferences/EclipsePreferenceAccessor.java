package de.persosim.simulator.preferences;

import org.globaltester.base.PreferenceHelper;

import de.persosim.simulator.utils.PreferenceAccessor;

public class EclipsePreferenceAccessor implements PreferenceAccessor {

	@Override
	public void set(String key, String value) {
		PreferenceHelper.setPreferenceValue("de.persosim.simulator", key, value);
		PreferenceHelper.flush("de.persosim.simulator");
	}

	@Override
	public String get(String key) {
		return PreferenceHelper.getPreferenceValue("de.persosim.simulator", key);
	}

}
