package de.persosim.simulator.ui.utils;

import java.text.DateFormat;
import java.util.Date;

import org.globaltester.logging.formatservice.LogFormatService;
import org.osgi.service.log.LogEntry;

/**
 * Formats the LogEntry. It is used to format or manipulate log messages.
 * @author jkoch
 */
public class LogFormatter implements LogFormatService{
	
	DateFormat format = DateFormat.getDateTimeInstance();
	
	@Override
	public String format(LogEntry entry) {
		String strEntry = "["
				+ getLogLvlName(entry.getLevel()) + " - "
				+ entry.getBundle().getSymbolicName()+" - "
				+ format.format(new Date(entry.getTime())) + "] "
				+ entry.getMessage();
		return strEntry;
	}
	
	private String getLogLvlName(int lvl){
		
		switch(lvl){
		case 1: return "TRACE";
		case 2: return "DEBUG";
		case 3: return "INFO";
		case 4: return "WARNING";
		case 5: return "ERROR";
		case 6: return "FATAL";
		case 120: return "UI";
		default: return ""; //happens if log levels are added but were not added to this method
		}

	}
}
