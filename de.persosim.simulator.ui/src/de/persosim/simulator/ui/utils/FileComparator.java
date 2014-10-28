package de.persosim.simulator.ui.utils;

import java.io.File;
import java.util.Comparator;

/**
 * This Comparator compares objects of type {@link File}.
 * If one of the provided parameter objects is a file while the other is a folder, folders will be treated as being "smaller" than files, sorting them in front of files.
 * If both provided parameter objects are files or folders the return value is the the of o1.compareTo(o2).
 * 
 * @author slutters
 *
 */
public class FileComparator implements Comparator<File> {

	@Override
	public int compare(File o1, File o2) {
		if(o1.isDirectory()) {
			if(o2.isDirectory()) {
				return o1.compareTo(o2);
			} else {
				return -1;
			}
		} else {
			if(o2.isDirectory()) {
				return 1;
			} else {
				return o1.compareTo(o2);
			}
		}
	}

}
