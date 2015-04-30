package de.persosim.simulator.ui.utils;

import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.globaltester.logging.filterservice.LogFilterService;
import org.osgi.service.log.LogEntry;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * The LogFilter class is used to manage filters that consist of acceptable log
 * levels (integers) and bundle names. The Most important method is the
 * logfilter method which checks if provided log messages fit the 'rules'.
 * 
 * @author jkoch
 *
 */
public class LogFilter implements LogFilterService {

	/*
	 * The log levels exist TRACE = 1 DEBUG = 2 INFO = 3 WARN = 4 ERROR = 5
	 * FATAL = 6 UI = 120
	 * 
	 * Not existing levels have no effect
	 */

	private byte[] logLevels;

	// place for persistent storage of filter data
	Preferences preferences = InstanceScope.INSTANCE
			.getNode("de.persosim.simulator.ui.utils.logging");

	// List of bundles that should be shown
	private LinkedList<String> bundleFilters = new LinkedList<String>();
	
	// Constructor
	public LogFilter() {
		this.bundleFilters.add("de.persosim");
		loadFilter();
	}

	@Override
	public void setLogLevels(byte[] logLevels) {
		this.logLevels = Arrays.copyOf(logLevels, logLevels.length);
	}
	
	@Override
	public byte[] getLogLevels() {
		return logLevels;
	}

	@Override
	public void setBundleFilters(LinkedList<String> bundleFilters) {
		this.bundleFilters = bundleFilters;
	}

	@Override
	public void saveFilter() {
		preferences.putByteArray("loglevels", logLevels);

		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void loadFilter() {
		setLogLevels(preferences.getByteArray("loglevels", logLevels));
	}

	public void addFilter(String filter) {
		bundleFilters.add(filter);
	}

	/**
	 * Filters a given LogEntry object. It checks if the entry should be shown
	 * in the log or not. It checks the which bundle throws the log message and
	 * which log level it has.
	 * 
	 * @param entry
	 *            the LogEntry object which contains the message to log
	 * 
	 * @return shouldBeLogged a boolean value that can be used to control
	 *         printing the log messages
	 */
	@Override
	public boolean logFilter(LogEntry entry) {

		// 1. check for bundles
		for (String currentBundle : bundleFilters) {

			if (entry.getBundle().getSymbolicName().startsWith(currentBundle)) {

				// 2. check if log level is ok
				for (int currentLevel : logLevels) {
					if (entry.getLevel() == currentLevel) {
						// the entry is ok and should be logged
						return true;
					}
				}
				// stop: bundle correct but wrong log level
				break;
			}
		}
		return false;
	}

}
