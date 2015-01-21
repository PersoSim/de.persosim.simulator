package de.persosim.simulator.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.osgi.service.log.LogService;

import de.persosim.simulator.Activator;
import de.persosim.simulator.PersoSim;

/**
 * This class is used as primary Logger throughout all PersoSim classes.
 * 
 * @author amay
 * 
 */
public class PersoSimLogger {
	public static final byte TRACE = 1;
	public static final byte DEBUG = 2;
	public static final byte INFO = 3;
	public static final byte WARN = 4;
	public static final byte ERROR = 5;
	public static final byte FATAL = 6;
	private static final byte LOGLEVEL_DFLT = DEBUG;
	
	private static Logger logger;

	/**
	 * Ensure that this type can not be instantiated
	 */
	private PersoSimLogger() {
	}
	
	public static void init() {
		logger = Logger.getLogger("GTSimulatorLogger");
		
		logger.removeAllAppenders();

		//common log layout
		Layout layout = new PatternLayout("%d %-5p - %m%n");

		// log to stdOut
		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		logger.addAppender(consoleAppender);

		// log to file
		try {
			String logFileName = "logs" + File.separator + "PersoSim_" + new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()) + ".log";
			FileAppender fileAppender = new FileAppender(layout, logFileName, false);
			logger.addAppender(fileAppender);
		} catch (IOException e) {
			PersoSim.showExceptionToUser(e);
		}

		// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		logger.setLevel(Level.ALL);
	}

	/**
	 * Write message to the log, including origin of that message.
	 * 
	 * This method uses LOGLEVEL_DLFT as LogLevel.
	 * 
	 * @param source
	 *            origin of this log message
	 * @param message
	 *            the message to be logged
	 */
	public static void log(InfoSource source, String message) {
		log(source, message, LOGLEVEL_DFLT);
	}

	/**
	 * Write message to the log, including origin of that message.
	 * 
	 * @param source
	 *            origin of this log message
	 * @param message
	 *            the message to be logged
	 * @param logLevel
	 *            log level on which the message is shown
	 */
	public static void log(InfoSource source, String message, byte logLevel) {
		log(source.getIDString(), message, logLevel);
	}
	
	/**
	 * Write message to the log, including originating class of that message.
	 * 
	 * @param className
	 *            originating class of this log message
	 * @param message
	 *            the message to be logged
	 * @param logLevel
	 *            log level on which the message is shown
	 */
	public static void log(Class<?> className, String message, byte logLevel) {
		log(className.getCanonicalName(), message, logLevel);
	}
	
	/**
	 * Write message to the log, including originating class of that message.
	 * 
	 * This method uses LOGLEVEL_DLFT as LogLevel.
	 * 
	 * @param className
	 *            originating class of this log message
	 * @param message
	 *            the message to be logged
	 */
	public static void log(Class<?> className, String message) {
		log(className, message, LOGLEVEL_DFLT);
	}

	/*--------------------------------------------------------------------------------*/

	/**
	 * Write exception to the log, including origin of that message.
	 * 
	 * This method uses LOGLEVEL_DLFT as LogLevel.
	 * 
	 * @param source
	 *            origin of this log message
	 * @param e
	 *            the Exception to be logged
	 */
	public static void logException(InfoSource source, Exception e) {
		logException(source, e, LOGLEVEL_DFLT);
	}

	/**
	 * Write exception to the log, including origin of that message.
	 * 
	 * @param source
	 *            origin of this log message
	 * @param e
	 *            the Exception to be logged
	 * @param logLevel
	 *            log level on which the exception is shown
	 */
	public static void logException(InfoSource source, Exception e, byte logLevel) {
		logException(source.getIDString(), e, logLevel);
	}
	
	/**
	 * Write exception to the log, including origin of that message.
	 * 
	 * @param className
	 *            originating class of this log message
	 * @param e
	 *            the Exception to be logged
	 * @param logLevel
	 *            log level on which the exception is shown
	 */
	public static void logException(Class<?> className, Exception e, byte logLevel) {
		logException(className.getCanonicalName(), e, logLevel);
	}
	
	/**
	 * Write message to the log, formatted including origin of that message.
	 * 
	 * @param source
	 *            originating origin of this log message
	 * @param message
	 *            the message to be logged
	 * @param logLevel
	 *            log level on which the message is shown
	 */
	private static void log(String source, String message, byte logLevel) {
		logPlain(String.format("%s: %s", source, message), logLevel);
	}
	
	/**
	 * Transform an Exception into user readable form and write it to the log,
	 * including origin of that message.
	 * 
	 * @param source
	 *            origin of this log message
	 * @param message
	 *            the message to be logged
	 * @param logLevel
	 *            log level on which the message is shown
	 */
	private static void logException(String source, Exception e, byte logLevel) {
		StringBuilder sb;

		sb = new StringBuilder();

		sb.append("encountered the following exception: ");
		sb.append(e.getClass().getCanonicalName());
		sb.append(" at");
		
		StackTraceElement[] stackTrace = e.getStackTrace();
		
		for(StackTraceElement elem : stackTrace) {
			sb.append("\n" + elem.toString());
		}

		log(source, sb.toString(), logLevel);

		String message = e.getMessage();
		if ((message != null) && (message.length() > 0)) {
			log(source, "Additional info provided is:" + message, logLevel);
		}
		
		/* Same output but in "red" */
//		System.err.println(source.getIDString() + " " + sb.toString());
	}
	
	/**
	 * Write exception to the log, including origin of that message.
	 * 
	 * This method uses LOGLEVEL_DLFT as LogLevel.
	 * 
	 * @param className
	 *            originating class of this log message
	 * @param e
	 *            the Exception to be logged
	 */
	public static void logException(Class<?> className, Exception e) {
		logException(className, e, LOGLEVEL_DFLT);
	}

	private static void logPlain(String message, byte logLevel) {
		LogService logService = Activator.getLogservice();
		if (logService != null){
			logService.log(logLevel, message);
		}
	}
}
