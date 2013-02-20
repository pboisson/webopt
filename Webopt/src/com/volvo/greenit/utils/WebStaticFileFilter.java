/**
 * 
 */
package com.volvo.greenit.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

/**
 * @author pico Filter to get static files in a directory
 */
public class WebStaticFileFilter implements FileFilter {

	private static List<String> mListOfExtension;

	/**
	 * Constructor of the filter from a string.
	 * Takes ";" as a separator and builds a list of extension to check
	 * @param pListOfExtensionAsString the list of extension as a string (ex: css;jpg;txt)
	 */
	public WebStaticFileFilter(String pListOfExtensionAsString) {
		super();
		if (pListOfExtensionAsString != null) {
			List<String> lListOfExtension = Arrays
					.asList(pListOfExtensionAsString.split(";"));
			mListOfExtension = lListOfExtension;
		}
	}

	/**
	 * Constructor of the filter from a list of string.
	 * Each string is an extension to look for.
	 * @param pListOfExtensionAsString the list of extension
	 */
	public WebStaticFileFilter(List<String> pListOfExtension) {
		super();
		mListOfExtension = pListOfExtension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File pFile) {
		boolean isStaticFile = false;
		if (mListOfExtension != null && pFile != null
				&& pFile.getName() != null
				&& pFile.getName().lastIndexOf(".") > 0
				&& pFile.getName().lastIndexOf(".") != pFile.getName().length()) {
			isStaticFile = mListOfExtension.contains(pFile.getName().toLowerCase().substring(
					pFile.getName().lastIndexOf(".") + 1));
		}
		return isStaticFile;
	}

}
