package de.persosim.simulator.ui;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public final class LogHelper {

	private LogHelper() {
		// hide implicit public constructor
	}

	public static void logEnvironmentInfo() {
		BasicLogger.log("Starting PersoSim " + getProductVersion() + " at: "
				+ DateFormat.getDateTimeInstance().format(new Date()), LogLevel.INFO);
		BasicLogger.log(
				LogHelper.class, "Operating System: " + System.getProperty("os.name") + " (version: "
						+ System.getProperty("os.version") + "; arch: " + System.getProperty("os.arch") + ")",
				LogLevel.INFO);
		BasicLogger.log(
				LogHelper.class, "Java Version: " + System.getProperty("java.version") + " (vendor: "
						+ System.getProperty("java.vendor") + ", " + System.getProperty("java.vendor.url") + ")",
				LogLevel.INFO);
		BasicLogger.log(LogHelper.class, "Java Home: \"" + System.getProperty("java.home") + "\"", LogLevel.INFO);
		BasicLogger.log(
				LogHelper.class, "User: \"" + System.getProperty("user.name") + "\" (home: \""
						+ System.getProperty("user.home") + "\"; dir: \"" + System.getProperty("user.dir") + "\")",
				LogLevel.INFO);
	}

	/**
	 * This method returns the bundle version as a String
	 */
	public static String getProductVersion() {
		final IProduct product = Platform.getProduct();
		final Bundle bundle = product.getDefiningBundle();
		final Version version = bundle.getVersion();
		return version.toString();
	}
}
