package com.kohls.ibm.ocf.pca.util;

/**
 * A collection of string utilities.
 * 
 * @author Roy Nicholls
 */
public final class KOHLSStringUtility {
    //
    /**
     * Private constructor. Utility class only
     */
    private KOHLSStringUtility() {
        //
    }// constructor:StringUtility

    /**
     * A utility method that tests for empty string. A string is empty when it
     * is either null, or all spaces.
     * 
     * @param str
     *            the input string to test
     * @return true if the string is empty or null
     */
    public final static boolean isEmpty(String str) {
        //
        if (str == null) {
            return true;
        }// end: if (str == null)

        if (str.trim().length() == 0) {
            return true;
        }// end: if (str.trim().length() == 0)

        return false;

    }// isEmpty(String):boolean

}// class:StringUtility
