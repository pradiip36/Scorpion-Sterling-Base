/**
 * 
 */
package com.kohls.common.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author jvishwak
 *
 */
public class KohlsSchemaValidator implements YIFCustomApi {
	private Properties props; 
	@Override
	public void setProperties(Properties arg0) throws Exception {
		props = arg0;
		
	}
	
	public Document verifyXML(YFSEnvironment env, Document inXML) throws SAXException
	{


		String strSchemaPath = props.getProperty("XSLPath");
		 // 1. Lookup a factory for the W3C XML Schema language
        SchemaFactory factory = 
            SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        
        // 2. Compile the schema. 
        // Here the schema is loaded from a java.io.File, but you could use 

        File schemaLocation = new File(strSchemaPath);
        Schema schema = null;
		try {
			schema = factory.newSchema(schemaLocation);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
        // 3. Get a validator from the schema.
        javax.xml.validation.Validator validator = schema.newValidator();
        
        // 4. Parse the document you want to check.
        Source source = new DOMSource(inXML.getDocumentElement());
        
        // 5. Check the document
        try {
            try {
				validator.validate(source);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //System.out.println(" VALID ");
        }
        catch (SAXException ex) {
            //System.out.println(" is not valid because ");
            //System.out.println(ex.getMessage());
            throw ex;
        }  

        return inXML;
		
	
	}
}
