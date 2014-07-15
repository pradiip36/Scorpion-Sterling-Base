package com.kohls.ibm.ocf.pca.util;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.yantra.yfc.rcp.YRCPlatformUI;


public class KOHLSXMLFileWriter {
    

     //  This method writes a DOM document to a file
      public static void writeXmlFile(Document doc, String filename) {
        try {
            
        	//Create File location on local machine
        	 
       	     String sFilepath = "";
        	
       	     String sWorkingDir = System.getProperty(KOHLSPCAConstants.INI_PROPERTY_USER_DIRECTORY);
        	 
        	 sFilepath = sWorkingDir + File.separator + filename;
        	
        	// Prepare the DOM document for writing
        	
            Source source = new DOMSource(doc);

            // Prepare the output file
            File file = new File(sFilepath);
         
            
            Result result = new StreamResult(file.toURI().getPath());

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
       
            } catch (TransformerConfigurationException e) {
            	e.printStackTrace();  	
            	YRCPlatformUI.showError(YRCPlatformUI.getString("ERROR"),YRCPlatformUI.getString("ERROR_FILE_WRITE_FAILED"));
            	
            } catch (TransformerException e) {
            	e.printStackTrace();  	
            	YRCPlatformUI.showError(YRCPlatformUI.getString("ERROR"),YRCPlatformUI.getString("ERROR_FILE_WRITE_FAILED"));
            
            }
    }  
}

