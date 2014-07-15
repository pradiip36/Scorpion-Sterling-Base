package com.kohls.ibm.ocf.pca.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class defines the file utilities used by the JUnit framework.
 * 
 * @author Roy Nicholls
 */
public final class KOHLSFileUtility {
    //
    /**
     * A utility method that gets the contents of a file on the classpath,
     * returning the contents as a string.
     * 
     * @param path
     * @return the file contents as a string
     */
    public final static String getFileOnPathContents(final String path)
            throws IOException {
        //
        InputStream stream = null;
        try {
            stream = KOHLSFileUtility.getStreamOnPath(path);
            return KOHLSFileUtility.getFileContents(stream);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                }
            }// end: if (stream != null)
        }
    }// getFileOnPathContents(String):String

    /**
     * A utility method that gets a file on the class path as an input stream.
     * 
     * @param path
     *            the relative class path of the file
     * @return the input stream
     * @throws IOException
     *             if the file does exist or does not load properly
     */
    public final static InputStream getStreamOnPath(String path)
            throws IOException {
        //
        InputStream stream = null;
        try {
            stream = KOHLSFileUtility.class.getResourceAsStream(path);
            if (stream == null) {
                throw new IOException("File " + path
                        + " not found on class path.");
            }

            return stream;
        } finally {
        }

    }// getStreamOnPath(String):InputStream

    /**
     * A utility method that reads a file, given the path, returning the file
     * contents as a string.
     * 
     * @param fileName
     *            the name of the file to be read.
     * @return String containing the contents of the file
     * @throws IOException
     *             if the file cannot be found, or read errors occur
     */
    public final static String getFileContents(String fileName)
            throws IOException {
        //
        File file = null;
        StringBuffer buffer = null;
        BufferedReader in = null;
        String str = null;
        try {
            file = new File(fileName);
            int length = (int) file.length();
            buffer = new StringBuffer(length);
            in = new BufferedReader(new FileReader(file));
            //
            while ((str = in.readLine()) != null) {
                buffer.append(str);
            }// end: while ((str = in.readLine()) != null )

            return buffer.toString();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }// end: if ( in != null )
            } catch (IOException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }// end: try/catch/finally

    }// getFileContents(String):String

    /**
     * Overloaded file retrieval method that retrieves a file given an input
     * stream
     * 
     * @param stream
     *            the input stream to be read
     * @return String containing the contents of the file
     * @throws IOException
     */
    public final static String getFileContents(InputStream stream)
            throws IOException {
        //
        StringBuffer sbuf = null;
        byte[] buffer = null;
        try {
            int bytesRead;
            buffer = new byte[4192];

            sbuf = new StringBuffer();

            while ((bytesRead = stream.read(buffer)) != -1) {
                byte copyArea[] = new byte[bytesRead];
                System.arraycopy(buffer, 0, copyArea, 0, bytesRead);
                sbuf.append(new String(copyArea));
            }
            return sbuf.toString();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }

    }// getFileContents(InputStream):String

}// class:FileUtility

