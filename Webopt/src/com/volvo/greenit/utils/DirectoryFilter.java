/**
 * 
 */
package com.volvo.greenit.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter to retrieve sub-directories of a directory
 * 
 * @author pico
 */
public class DirectoryFilter implements FileFilter {

	/** default instance to use in File.listFiles(DirectoryFilter.FILTER) */
	public static final DirectoryFilter FILTER = new DirectoryFilter();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File pFile) {
		return pFile != null && pFile.isDirectory();
	}

}
