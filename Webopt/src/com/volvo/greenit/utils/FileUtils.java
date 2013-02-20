/**
 * 
 */
package com.volvo.greenit.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author pico Set of methods to handle files.
 */
public class FileUtils {

	/**
	 * Copy one file to another location
	 * 
	 * @param pInput
	 *            the file to copy
	 * @param pOutput
	 *            the result of the copy
	 * @throws IOException
	 *             if the copy cannot be written
	 */
	public static void copyStaticFile(File pInput, File pOutput)
			throws IOException {
		if (notNullAndExists(pInput) && notNullAndExists(pOutput)) {
			FileChannel source = null;
			FileChannel destination = null;
			FileInputStream fileInputStream = new FileInputStream(pInput);
			FileOutputStream fileOutputStream = new FileOutputStream(pOutput);

			try {

				source = fileInputStream.getChannel();
				destination = fileOutputStream.getChannel();

				long count = 0;
				long size = source.size();
				while ((count += destination.transferFrom(source, count, size
						- count)) < size)
					;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (source != null) {
					source.close();
				}
				if (destination != null) {
					destination.close();
				}
				fileOutputStream.close();
				fileOutputStream.close();
			}
		}
	}

	/**
	 * Removes the path separator (if any) at the end of a directory
	 * 
	 * @param pPath
	 *            the path to trim
	 * @return the trimmed path
	 */
	public static String trimEndOfPath(String pPath) {
		if (pPath != null && pPath.endsWith(File.separator)) {
			pPath = pPath.substring(0, pPath.length() - 1);
		}
		return pPath;
	}

	/**
	 * Removes the path separator (if any) at the start of a directory.
	 * 
	 * @param pPath
	 *            the path to trim
	 * @return the trimmed path
	 */
	public static String trimStartOfPath(String pPath) {
		if (pPath.startsWith(File.separator)) {
			pPath = pPath.substring(1);
		}
		return pPath;
	}

	/**
	 * Verifies that a file is not null and exists in the file system.
	 * 
	 * @param pFile
	 *            the file to check
	 * @return true if the file can be read.
	 */
	public static boolean notNullAndExists(File pFile) {
		return pFile != null && pFile.exists();
	}

	/**
	 * Checks whether a file is an image
	 * 
	 * @param pFile
	 *            the file to check
	 * @return true if the file is an image
	 */
	public static boolean isImage(File pFile) {
		boolean lIsImage = false;
		if (notNullAndExists(pFile)) {
			if (pFile.getName().toLowerCase().endsWith(".png")
					|| pFile.getName().toLowerCase().endsWith(".jpg")
					|| pFile.getName().toLowerCase().endsWith(".jpeg")
					|| pFile.getName().toLowerCase().endsWith(".gif")) {
				lIsImage = true;
			}
		}
		return lIsImage;
	}

}
