/**
 * 
 */
package com.volvo.greenit.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.volvo.greenit.messages.Messages;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * @author pico
 * Set of methods to minify static files
 */
public class MinificationManager {

	/**
	 * Takes a javascript file as input and minifies it
	 * in the output file. 
	 * @param pInput the file to minify.
	 * @param pOutput the minified result file.
	 */
	public static void minifyJavascript(File pInput, File pOutput) {
		if (FileUtils.notNullAndExists(pInput)
				&& FileUtils.notNullAndExists(pOutput)) {
			try {
				JavaScriptCompressor lCompressor = new JavaScriptCompressor(
						new FileReader(pInput), mErrorReporter);
				FileWriter lWriter = new FileWriter(pOutput);
				lCompressor.compress(lWriter, -1, true, false, false, false);
				lWriter.flush();
			} catch (EvaluatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				System.out.println(Messages.getString("error.empty.js.file"));
			}
		}
	}
	
	/**
	 * Takes a css file as input and minifies it
	 * in the output file. 
	 * @param pInput the file to minify.
	 * @param pOutput the minified result file.
	 */	
	public static void minifyCss(File pInput, File pOutput) {
		if (FileUtils.notNullAndExists(pInput)
				&& FileUtils.notNullAndExists(pOutput)) {
			try {
				CssCompressor lCompressor = new CssCompressor(new FileReader(
						pInput));
				FileWriter lWriter = new FileWriter(pOutput);
				lCompressor.compress(lWriter, -1);
				lWriter.flush();
			} catch (EvaluatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Inner class needed to compress javascripts with YUI compressor lib.
	 */
	private static ErrorReporter mErrorReporter = new ErrorReporter() {

		public void warning(String message, String sourceName, int line,
				String lineSource, int lineOffset) {
			if (line < 0) {
				System.err.println("\n[WARNING] " + message);
			} else {
				System.err.println("\n[WARNING] " + line + ':' + lineOffset
						+ ':' + message);
			}
		}

		public void error(String message, String sourceName, int line,
				String lineSource, int lineOffset) {
			if (line < 0) {
				System.err.println("\n[ERROR] " + message);
			} else {
				System.err.println("\n[ERROR] " + line + ':' + lineOffset + ':'
						+ message);
			}
		}

		public EvaluatorException runtimeError(String message,
				String sourceName, int line, String lineSource, int lineOffset) {
			error(message, sourceName, line, lineSource, lineOffset);
			return new EvaluatorException(message);
		}
	};

}
