/**
 * 
 */
package com.volvo.greenit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.volvo.greenit.conf.Configuration;
import com.volvo.greenit.messages.Messages;
import com.volvo.greenit.utils.CompressionManager;
import com.volvo.greenit.utils.DirectoryFilter;
import com.volvo.greenit.utils.FileUtils;
import com.volvo.greenit.utils.MinificationManager;
import com.volvo.greenit.utils.WebStaticFileFilter;

/**
 * Main class to perform static file analysis. Copy and transform static files
 * of a web application directory into optimized version.
 * 
 * @author pico
 * 
 */
public class StaticFilePackageManager implements Configuration {

	/** Configuration of the class. */
	private static HashMap<String, String> configuration;

	/**
	 * Filter to be used to find static files. It is built dynamically from the
	 * configuration
	 */
	private static WebStaticFileFilter mExtensionFilter;

	/**
	 * Takes a directory in argument and finds static resources based on
	 * extension filter (optional argument) and output the files in a directory
	 * provided in argument in three different sub directories:
	 * <ul>
	 * <li>normal (no changes to files)</li>
	 * <li>minified (js and css minified)</li>
	 * <li>gzipped (js and css minified and all files but images compressed)</li>
	 * <ul>
	 * Ex: java StaticFilePackageManager -dir /tmp/input -output /tmp/output
	 * -extension jpg;gif;png;jpeg;css;html;htm;js;
	 * 
	 * @param args
	 * @throws Exception
	 *             error to be reported to command line
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			/* at least one argument has to be provided */
			System.out.println(Messages.getString("error.no.args"));
			return;
		} else if (args.length == 1) {
			/* if only one argument has to be provided, it may be help */
			if ("-".concat(HELP).equalsIgnoreCase(args[0])
					|| "-".concat(HELP_SHORT).equalsIgnoreCase(args[0])) {
				System.out.println(Messages.getString("message.help.text"));
				return;
			} else {
				/* wrong argument provided */
				System.out.println(Messages.getString("error.no.args"));
				return;
			}
		} else {
			System.out.println(Messages.getString("message.start.analysis"));
		}
		
		/* load configuration */
		StaticFilePackageManager.configureFromArgs(args);
		
		/* create the structure with the optimized files */
		StaticFilePackageManager.buildDirStructure(configuration.get(DIR),
				configuration.get(OUT), configuration.get(EXT));

		/* verifies that files have been correctly output*/
		verifyOutput();
	}

	/**
	 * Verifies if files exist in output directory and warns the user if not.
	 */
	private static void verifyOutput() {
		String vOutputDir = configuration.get(OUT);
		File vDir = new File(vOutputDir);
		if (vDir.listFiles().length > 0) {
			System.out.println(Messages
					.getString("message.static.files.copied"));
		} else {
			System.out.println(Messages.getString("message.no.static.files"));
		}
	}

	/**
	 * Takes a directory in argument and finds static resources based on
	 * extension filter (optional argument) and output the files in a directory
	 * provided in argument in three different sub directories:
	 * <ul>
	 * <li>output directory root (no change to files)</li>
	 * <li>minify (js and css minified)</li>
	 * <li>minify/gzipped (js and css minified and all files but images
	 * compressed)</li>
	 * <ul>
	 * 
	 * @param pInputDir
	 *            input directory to look into for static files
	 * @param pOutputDir
	 *            output directory to extract and transform static files
	 * @param pExtList
	 *            list of extension of static files separated by ";" (ex:
	 *            css;jpg;html)
	 * @throws Exception
	 *             errors, most of them related to I/O operations
	 */
	public static void buildDirStructure(String pInputDir, String pOutputDir,
			String pExtList) throws Exception {
		/* Build configuration from input and validate */
		StaticFilePackageManager.initConfiguration(pInputDir, pOutputDir,
				pExtList);
		StaticFilePackageManager.verifyConf();

		/* build destination structure in output directory from input directory */
		File lRoot = new File(StaticFilePackageManager.configuration.get(DIR));
		buildDestStructure(lRoot);
	}

	/**
	 * Recursive method that look in an input directory for static files, copy
	 * and transform them in destination folder
	 * 
	 * @param pRoot
	 *            the directory to look into for static files
	 * @throws IOException
	 *             if accessing input directory or writing destination structure
	 *             is not possible
	 */
	private static void buildDestStructure(File pRoot) throws IOException {

		System.out.println("Current directory: " + pRoot.getName());

		/* Recursively going into sub directories */
		File[] lSubDirectories = pRoot.listFiles(DirectoryFilter.FILTER);
		for (File file : lSubDirectories) {
			buildDestStructure(file);
		}

		/* Go through static files in the directory */
		extractStaticFiles(pRoot);
	}

	/**
	 * Extract static files from directory based on the filter provided in the
	 * configuration (if none, default is used)
	 * 
	 * @param pRoot
	 *            the directory to look into
	 */
	private static void extractStaticFiles(File pRoot) {
		if (FileUtils.notNullAndExists(pRoot)) {
			File[] lFilesToCopy = pRoot.listFiles(mExtensionFilter);
			if (lFilesToCopy != null && lFilesToCopy.length > 0) {
				for (File file : lFilesToCopy) {
					try {
						transformFile(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Copy and transform a file depending on the type of file:
	 * <ul>
	 * <li>All files are copied as is</li>
	 * <li>CSS and JS files are minified</li>
	 * <li>All files but images are compressed
	 * <li/>
	 * </ul>
	 * 
	 * @param pFile
	 * @throws IOException
	 */
	private static void transformFile(File pFile) throws IOException {
		/* logging operation */
		System.out.println("file: " + pFile.getName());
		/* handling javascript */
		if (pFile.getName() != null
				&& (pFile.getName().toLowerCase().endsWith(".js"))) {
			FileUtils.copyStaticFile(pFile, getOutputFile(pFile, false, false));
			MinificationManager.minifyJavascript(pFile,
					getOutputFile(pFile, true, false));
			CompressionManager.compressFile(getOutputFile(pFile, true, false),
					getOutputFile(pFile, true, true));
		}
		/* handling stylesheets */
		else if (pFile.getName() != null
				&& pFile.getName().toLowerCase().endsWith(".css")) {
			FileUtils.copyStaticFile(pFile, getOutputFile(pFile, false, false));
			MinificationManager.minifyCss(pFile,
					getOutputFile(pFile, true, false));
			CompressionManager.compressFile(getOutputFile(pFile, true, false),
					getOutputFile(pFile, true, true));
		}
		/* handling images */
		else if (FileUtils.isImage(pFile)) {
			FileUtils.copyStaticFile(pFile, getOutputFile(pFile, false, false));
		}
		/* handling other cases */
		else {
			FileUtils.copyStaticFile(pFile, getOutputFile(pFile, false, false));
			CompressionManager.compressFile(pFile,
					getOutputFile(pFile, false, true));
		}
	}

	/**
	 * Get and creates the file to be created depending on the context.
	 * 
	 * @param pFile
	 *            the file to be copied
	 * @param pMinify
	 *            adds "minify" directory to the output
	 * @param pCompress
	 *            adds "gzipped" directory to the output
	 * @return the file to be created
	 * @throws IOException
	 *             if the file cannot be created.
	 */
	private static File getOutputFile(File pFile, boolean pMinify,
			boolean pCompress) throws IOException {
		if (FileUtils.notNullAndExists(pFile)) {
			String lRoot = configuration.get(DIR);
			String lOutput = configuration.get(OUT);

			/* get relative file path from input directory perspective */
			String lRelativeFileName = pFile.getAbsolutePath().replaceFirst(
					lRoot, "");

			/* trim path to construct destination file */
			lRelativeFileName = FileUtils.trimStartOfPath(lRelativeFileName);
			lOutput = FileUtils.trimEndOfPath(lOutput);

			/* build the path from output */
			String lDestFileName = lOutput.concat(File.separator);
			if (pMinify) {
				/* adds minify directory */
				lDestFileName = lDestFileName.concat("minify").concat(
						File.separator);
			}
			if (pCompress) {
				/* adds gzipped directory */
				lDestFileName = lDestFileName.concat("gzipped").concat(
						File.separator);
			}
			/* adds the name of the file */
			lDestFileName = lDestFileName.concat(lRelativeFileName);

			/* creates the file if not already existing */
			File lFile = new File(lDestFileName);
			if (!lFile.exists()) {
				lFile.getParentFile().mkdirs();
				lFile.createNewFile();
			}
			return lFile;
		} else {
			/* input file is null or empty */
			return null;
		}
	}

	/**
	 * Create configuration from command line arguments
	 * 
	 * @param args
	 *            command line arguments
	 */
	private static void configureFromArgs(String[] args) {
		/* going through the arguments to configure script */
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg != null && arg.startsWith("-")) {
				/* remove - from argument before checking if arg is valid */
				arg = arg.replaceFirst("-", "");
				/* input dir setup */
				if (DIR.equals(arg) || DIR_SHORT.equals(arg)) {
					if (i + 1 < args.length) {
						getConfiguration().put(DIR, args[i + 1]);
						i++;
					}
				}
				/* output dir setup */
				if (OUT.equals(arg) || OUT_SHORT.equals(arg)) {
					if (i + 1 < args.length) {
						getConfiguration().put(OUT, args[i + 1]);
						i++;
					}
				}
				/* extension list of static files setup */
				if (EXT.equals(arg) || EXT_SHORT.equals(arg)) {
					if (i + 1 < args.length) {
						getConfiguration().put(EXT, args[i + 1]);
						i++;
					}
				}
				// @TODO write help file
			}
		}
	}

	/**
	 * Creates configuration
	 * 
	 * @param pInputDir
	 *            input directory to look into for static files
	 * @param pOutputDir
	 *            output directory to extract and transform static files
	 * @param pExtList
	 *            list of extension of static files separated by ";" (ex:
	 */
	private static void initConfiguration(String pInputDir, String pOutputDir,
			String pExtList) {
		/* going through the arguments to configure script */
		getConfiguration().put(DIR, pInputDir);
		getConfiguration().put(OUT, pOutputDir);
		getConfiguration().put(EXT, pExtList);
	}

	/**
	 * verifies that configuration is set properly to execute script
	 * 
	 * @throws Exception
	 *             error in conf to be reported
	 */
	private static void verifyConf() throws Exception {
		if (configuration == null) {
			throw new Exception(Messages.getString("error.conf.null"));
		}

		/* verify that input directory is set and is valid */
		if (configuration.get(DIR) == null) {
			throw new Exception(Messages.getString("error.no.dir"));
		} else {
			String vDirToAnalyse = configuration.get(DIR);
			File vDir = new File(vDirToAnalyse);
			if (!vDir.isDirectory()) {
				throw new Exception(Messages.getString("error.wrong.dir"));
			}
		}

		/* verify that output directory is set and is valid */
		if (configuration.get(OUT) == null) {
			throw new Exception(Messages.getString("error.no.output.dir"));
		} else {
			String vOutputDir = configuration.get(OUT);
			File vDir = new File(vOutputDir);
			if (!vDir.isDirectory()) {
				throw new Exception(
						Messages.getString("error.wrong.output.dir"));
			}
			if (vDir.listFiles().length > 0) {
				throw new Exception(
						Messages.getString("error.not.empty.output.dir"));
			}
		}

		/* verify if extension list is set or put default */
		if (configuration.get(EXT) == null) {
			configuration.put(EXT, DEFAULT_EXT_LIST);
		}
		mExtensionFilter = new WebStaticFileFilter(configuration.get(EXT));

	}

	/**
	 * Getter for configuration (initiate object if null)
	 * 
	 * @return the configuration
	 */
	protected static HashMap<String, String> getConfiguration() {
		if (configuration == null) {
			configuration = new HashMap<String, String>();
		}
		return configuration;
	}

}
