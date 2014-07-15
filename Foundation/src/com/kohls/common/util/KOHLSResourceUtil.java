package com.kohls.common.util;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Load and retrieve resource/property. At class loading, it will load from the
 * following property files: <i>/resources/yfs.properties,
 * /resources/yifclient.properties</i>.
 * Resources are also loaded in that order. <br>
 * If open failed for any of those files, it prints the error and continues.
 * <p>
 * This class should be used to retrieve application wide properties/resources.
 * <p>
 * Subclass of this class can load its own resource files. Only need to
 * implement a static block as:
 * 
 * <pre>
 * static {
 * 	loadResourceFile(&quot;file1&quot;);
 * 	loadResourceFile(&quot;file2&quot;);
 * }
 * </pre>
 * 
 */
public class KOHLSResourceUtil {
	private static Properties resources = new Properties();

	private static ArrayList msgResBundles = new ArrayList();

	private static ArrayList msgResBundleNames = new ArrayList();

	private static int numMsgResBundlesLoaded = 0;

	private final static String DESC_NOT_FOUND = "Error Description Not Found";

    //Currently, we see the need for only PROD and DEV to be two modes
    //in which Sterling Commerce would be run. Therefore, the flag is a boolean.
    //If mode modes develop in the future (like TEST etc), then the key would
    //have to be redefined.
    public final static String YANTRA_RUNTIME_MODE = "yantra.implementation.runtime.mode";
    public static boolean IS_PRODUCTION_MODE = true;
	
	static {
		loadDefaultResources();
	}

	public static void loadDefaultResources() {
		msgResBundleNames.clear();
		msgResBundles.clear();
		resources.clear();
		
		//server level configuration files
		loadResourceFile("/resources/extn/yantraimpl.properties");
	}

	/**
	 * Loading resources.
	 * 
	 * @param filename
	 *            the resource filename, must be available on CLASSPATH.
	 */
	public static void loadResourceFile(String filename) {
		InputStream is = null;
		try {

			/*
			 * YRCPlatformUI.trace("Loading Properties from: " + filename + ": " +
			 * ResourceUtil.class.getResource(filename));
			 */
			is = KOHLSResourceUtil.class.getResourceAsStream(filename);
			resources.load(is);
			is.close();
		} catch (Exception e) {
			
		}
	}

	/**
	 * Get resource by name
	 * 
	 * @param name
	 *            the resource name
	 */
	public static String get(String name) {

		String retVal = resources.getProperty(name);
		if (retVal != null)
			retVal = retVal.trim();

		return retVal;
	}

	/**
	 * 
	 * @param key
	 *            resource name
	 * @param def
	 *            default value
	 * @return true if the value is 'Y' or 'true' (case incensitive) false
	 *         otherwise
	 */
	public boolean getAsBoolean(String key, boolean def) {
		String val = (String) resources.get(key);
		if (null == val) {
			return def;
		}
		if (val.equalsIgnoreCase("Y") || val.equalsIgnoreCase("true")) {
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param key
	 * @param def
	 * @return double value of the resource. def in case of
	 *         NumberFormatException
	 */
	public double getAsDouble(String key, double def) {
		String val = (String) resources.get(key);
		if (null == val) {
			return def;
		}
		double ret = 0.0;
		try {
			ret = Double.parseDouble(val);
			return ret;
		} catch (NumberFormatException e) {
			// YRCPlatformUI.trace("Unable to convert value to double:" + val);
			return def;
		}
	}

	/**
	 * 
	 * @param key
	 * @param def
	 * @return int value of the key as defined in the properies file.
	 */
	public int getAsInt(String key, int def) {
		String val = (String) resources.get(key);
		if (null == val) {
			return def;
		}
		int ret = 0;
		try {
			ret = Integer.parseInt(val);
			return ret;
		} catch (NumberFormatException e) {
			// //YRCPlatformUI.trace("Unable to convert value to int:" + val);
		}
		return def;
	}

	/**
	 * 
	 * @param key
	 * @param def
	 * @return int value of the key as defined in the properies file.
	 */
	public long getAsLong(String key, long def) {
		String val = (String) resources.get(key);
		if (null == val) {
			return def;
		}
		long ret = 0L;
		try {
			ret = Long.parseLong(val);
			return ret;
		} catch (NumberFormatException e) {
			// //YRCPlatformUI.trace("Unable to convert value to long:" + val);
			return def;
		}
	}

	public static void list() {
		// //YRCPlatformUI.trace("Managing Following Properties");
		Enumeration keys = resources.keys();
		StringBuffer sb = new StringBuffer();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			sb.append(key).append("=").append(resources.getProperty(key))
					.append("\n");
		}
		// //YRCPlatformUI.trace(sb.toString());
	}

	/**
	 * 
	 * @return the list of resources defined
	 */
	public static Properties getAllResources() {
		return resources;
	}

	public static ResourceBundle loadMsgCodes(String componentName) {
		return loadMsgCodes(componentName, null);
	}

	/**
	 * Use this method to load messages. Message from
	 * /resources/extn/messagecodes.properties will be loaded by default
	 * 
	 * @param componentName
	 * @param locale
	 * @return
	 */
	public static ResourceBundle loadMsgCodes(String componentName,
			Locale locale) {
		ResourceBundle rb = null;

		String key = componentName;
		if (locale != null)
			key += "_" + locale.getDisplayName();

		if (!msgResBundleNames.contains(key)) {
			try {
				if (locale != null)
					rb = ResourceBundle.getBundle(componentName, locale);
				else
					rb = ResourceBundle.getBundle(componentName);

				msgResBundleNames.add(componentName);
				msgResBundles.add(rb);
				numMsgResBundlesLoaded++;
			} catch (MissingResourceException mre) {
				// YRCPlatformUI.trace("Unable to load error codes from Resource
				// Bundle: " + key);
			}
		}
		return rb;
	}

	/**
	 * This method returns the error description for the given errorCode as
	 * specified in the message Bundle file. If a matching entry is not found
	 * then it returns "Error Description Not Found"
	 * 
	 * @param errorCode
	 * @return
	 */

	public static String resolveMsgCode(String errorCode) {
		return resolveMsgCode(errorCode, null);
	}

	/**
	 * This method returns the error description for the given errorCode as
	 * specified in the message Bundle file. If a matching entry is not found
	 * then it returns "Error Description Not Found".
	 * 
	 * Use errorArgs to parameterize error description
	 * 
	 * @param errorCode
	 * @param errorArgs
	 * @return
	 */
	public static String resolveMsgCode(String errorCode, Object[] errorArgs) {
		String desc = null;
		int resBundleIndex = -1;

		while (desc == null && ++resBundleIndex < numMsgResBundlesLoaded) {
			ResourceBundle rb = (ResourceBundle) msgResBundles
					.get(resBundleIndex);
			try {
				desc = rb.getString(errorCode);
				desc = MessageFormat.format(desc, errorArgs);
			} catch (MissingResourceException mre) {
				// Ignore as we'd set it to erro desc not found
				desc = DESC_NOT_FOUND;
			} catch (IllegalArgumentException e) {
				// Ignore. If any error had occured, it'll be evident
				// from the raw text that is present as error description
			}
		}
		return desc;
	}

}
