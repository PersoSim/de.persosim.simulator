package de.persosim.simulator.ui.utils;

import java.util.LinkedList;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

public class LinkedListLogListener implements LogListener {

	LinkedList<String> list;
	private int maxLines;

	public void setMaxLines(int lines) {
		maxLines = lines;
	}

	public void setLinkedList(LinkedList<String> list) {
		this.list = list;
	}

	@Override
	public void logged(final LogEntry entry) {
		String logEntry = "[" + entry.getBundle().getSymbolicName() + "] "
				+ entry.getMessage();
		String[] splitResult = logEntry.split("(\\n|\\r)");

		for (int i = 0; i < splitResult.length; i++) {
			if (list != null) {
				if (list.size() > maxLines) {

					// synchronized is used to avoid IndexOutOfBoundsExceptions
					synchronized (list) {
						list.removeFirst();
						list.add(splitResult[i]);
					}

				} else {
					synchronized (list) {
						list.add(splitResult[i]);
					}
				}
			}
		}
	}

}
