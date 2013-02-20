/**
 * 
 */
package com.volvo.greenit.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

/**
 * @author pico
 * 
 */
public class CompressionManager {

	/**
	 * Gzip the content of the input into the ouput
	 * 
	 * @param pInput
	 *            the file to compress
	 * @param pOutput
	 *            the compressed file
	 */
	public static void compressFile(File pInput, File pOutput) {
		if (FileUtils.notNullAndExists(pInput)
				&& FileUtils.notNullAndExists(pOutput)) {
			BufferedWriter lBufferedWriter = null;
			BufferedReader lBufferedReader = null;
			try {

				/* Construct the BufferedWriter object */
				lBufferedWriter = new BufferedWriter(new OutputStreamWriter(
						new GZIPOutputStream(new FileOutputStream(pOutput))));

				/* Construct the BufferedReader object */
				lBufferedReader = new BufferedReader(new FileReader(pInput));

				String lLine = null;

				/* from the input file to the GZIP output file */
				while ((lLine = lBufferedReader.readLine()) != null) {
					lBufferedWriter.write(lLine);
					lBufferedWriter.newLine();
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				/* Close the BufferedWrter */
				if (lBufferedWriter != null) {
					try {
						lBufferedWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				/* Close the BufferedReader */
				if (lBufferedReader != null) {
					try {
						lBufferedReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
