/**
 * 
 */
package com.volvo.greenit.conf;

/**
 * Configuration class for all constants
 * 
 * @author pico
 */
public interface Configuration {

	/**
	 * Constant for directory to analyse
	 */
	public String DIR = "dir";

	/**
	 * Constant for directory to analyse (short version)
	 */
	public String DIR_SHORT = "D";

	/**
	 * Constant for output directory of static resources
	 */
	public String OUT = "output";

	/**
	 * Constant for directory to analyse (short version)
	 */
	public String OUT_SHORT = "O";

	/**
	 * Constant for help file
	 */
	public String HELP = "help";

	/**
	 * Constant for help file (short version)
	 */
	public String HELP_SHORT = "h";

	/**
	 * Constant for list of extensions
	 */
	public String EXT = "extension";

	/**
	 * Constant for list of extensions (short version)
	 */
	public String EXT_SHORT = "e";

	/**
	 * Default format to parse log
	 */
	public String DEFAULT_EXT_LIST = "jpg;gif;png;jpeg;css;html;htm;js";

}
