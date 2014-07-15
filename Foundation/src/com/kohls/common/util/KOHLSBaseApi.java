package com.kohls.common.util;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfs.japi.YFSEnvironment;


/** 
 * Base class which provides methods for API/Service invocation. All custom APIs are required to extend this class.
 */
public class KOHLSBaseApi implements YIFCustomApi {
	
	/** Getter for properties set during custom API's configuration
	 * @return Properties object containing key/value pairs as set in SDF 
	 */
	public Properties getProperties() {
		return properties;
	}

	private Properties properties;

	/** Setter called by the framework for the properties set during custom API configuration
	 * @param prop for setting key/value pairs
	 * @throws Exception if there is problem setting the properties
	 */
	public void setProperties(Properties prop) throws Exception {
		this.properties = prop;
	}

	private YFCLogCategory log;
	
	/** Returns handler to YFCLogCategory for logging
	 * @return
	 */
	protected YFCLogCategory getLog() {
		return this.log;
	}

	private static YIFApi api = null;
	
	
	static {
		try {
			api = YIFClientFactory.getInstance().getApi();
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 */
	public KOHLSBaseApi() {
		this.log = YFCLogCategory.instance(this.getClass());
	}

	/**
     * Invokes a Sterling API.
     * @param env Sterling Environment Context.
     * @param templateName Name of API Output Template that needs to be set
     * @param apiName Name of API to invoke.
     * @param inDoc Input Document to be passed to the API.
     * @throws java.lang.Exception Exception thrown by the API.
     * @return Output of the API.
     */
    public static Document invokeAPI(YFSEnvironment env, String templateName,
            String apiName, Document inDoc) throws Exception {
        env.setApiTemplate(apiName, templateName);
        Document returnDoc = api.invoke(env, apiName, inDoc);
        env.clearApiTemplate(apiName);
        return returnDoc;
    }

    /**
     * Invokes a Sterling API.
     * @param env Sterling Environment Context.
     * @param template Output Template that needs to be set
     * @param apiName Name of API to invoke.
     * @param inDoc Input Document to be passed to the API.
     * @throws java.lang.Exception Exception thrown by the API.
     * @return Output of the API.
     */

    public static Document invokeAPI(YFSEnvironment env, Document template,
            String apiName, Document inDoc) throws Exception {
        env.setApiTemplate(apiName, template);
        Document returnDoc = api.invoke(env, apiName, inDoc);
        env.clearApiTemplate(apiName);
        return returnDoc;
    }
    /**
     * Invokes a Sterling API.
     * @param env Sterling Environment Context.
     * @param apiName Name of API to invoke.
     * @param inDoc Input Document to be passed to the API.
     * @throws java.lang.Exception Exception thrown by the API.
     * @return Output of the API.
     */
    public static Document invokeAPI(YFSEnvironment env, String apiName, Document inDoc) throws Exception {
        return api.invoke(env, apiName, inDoc);
    }
    
    /**
     * Invokes a Sterling API.
     * @param env Sterling Environment Context.
     * @param apiName Name of API to invoke.
     * @param str Input to be passed to the API. Should be a valid XML string.
     * @throws java.lang.Exception Exception thrown by the API.
     * @return Output of the API.
     */
    public static Document invokeAPI(YFSEnvironment env, String apiName, String str) throws Exception {
        return api.invoke(env, apiName, YFCDocument.parse(str).getDocument());
    }
    
    /**
     * Invokes a Sterling Service.
     * @param env Sterling Environment Context.
     * @param serviceName Name of Service to invoke.
     * @param inDoc Input Document to be passed to the Service.
     * @throws java.lang.Exception Exception thrown by the Service.
     * @return Output of the Service.
     */
    public static Document invokeService(YFSEnvironment env, String serviceName, Document inDoc) throws Exception {
        return api.executeFlow(env, serviceName, inDoc);
    }
    
    /**
     * Invokes a Sterling Service.
     * @param env Sterling Environment Context.
     * @param serviceName Name of Service to invoke.
     * @param str Input to be passed to the Service. Should be a valid XML String.
     * @throws java.lang.Exception Exception thrown by the Service.
     * @return Output of the Service.
     */
    public static Document invokeService(YFSEnvironment env, String serviceName, String str) throws Exception {
        return api.executeFlow(env, serviceName, YFCDocument.parse(str).getDocument());
    }
    
    /**
     * Stores the object in the environment under a certain key.
     * @param env Yantra Environment Context.
     * @param key Key to identify object in environment.
     * @param value Object to be stored in the environment under the given key.
     * @return Previous object stored in the environment with the same key (if present).
     */
    public static Object setContextObject(YFSEnvironment env, String key, Object value) {
        Object oldValue = null;
        Map map = env.getTxnObjectMap();
        if (map != null) {
            oldValue = map.get(key);
        }
        env.setTxnObject(key, value);
        return oldValue;
    }
    
    /**
     * Retrieves the object stored in the environment under a certain key.
     * @param env Yantra Environment Context.
     * @param key Key to identify object in environment.
     * @return Object retrieved from the environment under the given key.
     */
    public static Object getContextObject(YFSEnvironment env, String key) {
        return env.getTxnObject(key);
    }
    
    /**
     *
     * Returns the clone of an XML Document.
     * @param doc Input document to be cloned.
     * @throws java.lang.Exception If uable to clone document.
     * @return Clone of the document.
     */
    public static Document cloneDocument(Document doc) throws Exception {
        return YFCDocument.parse(XMLUtil.getXMLString(doc)).getDocument();
    }
    
    /**
     * Returns the clone of an XML Document.
     * @param doc Input document to be cloned.
     * @throws java.lang.Exception If uable to clone document.
     * @return Clone of the document.
     */
    public static YFCDocument cloneDocument(YFCDocument doc) throws Exception {
        return YFCDocument.parse(doc.getString());
    }
    
    /**
     * Method to get resource as InputStream
     * @param resource Resource path relative to classpath
     * @return Resource as InputStream
     */
    public static InputStream getResourceStream(String resource) {
        return KOHLSBaseApi.class.getResourceAsStream(resource);
    }
    
    public static YFSEnvironment createEnvironment(String userID, String progID)
    throws Exception {
            Document doc = XMLUtil.createDocument("YFSEnvironment");
            Element elem = doc.getDocumentElement();
            elem.setAttribute("userId", userID);
            elem.setAttribute("progId", progID);
            YFSEnvironment env = api.createEnvironment(doc);
    		
    		Document doc1 = XMLUtil.createDocument("Login");
    		Element elem1= doc1.getDocumentElement();
    		elem1.setAttribute("LoginID", userID);			
    		elem1.setAttribute("Password", "password");
    		
    		Document doclogin = api.login(env, doc1);
    		env.setTokenID(doclogin.getDocumentElement().getAttribute("UserToken"));
    		return env;
    }
    
    	/**
	 * Removes the passed Node name from the input document.
	 * If no name is passed, it removes all the nodes.
	 * @param node Node from which we have to remove the child nodes
	 * @param nodeType nodeType e.g. Element Node, Comment Node or Text Node
	 * @param name Name of the Child node to be removed
	 */
	public static void removeAll(Node node, short nodeType, String name) 
	{
        if (node.getNodeType() == nodeType &&
                (name == null || node.getNodeName().equals(name))) 
        {
            node.getParentNode().removeChild(node);
        } 
        else 
        {
            // Visit the children
            NodeList list = node.getChildNodes();
            for (int i=0; i<list.getLength(); i++) {
                removeAll(list.item(i), nodeType, name);
            }
        }
        
	}
	
	public static String convertToYantraDate(Date dt) {
		YFCDate yDate = new YFCDate(dt);
		return yDate.getString();
	}
		
	/** helper method to get child element of a parent
	 * @param parent Name of parent element
	 * @param childName Name of child element
	 * @return Element which represents the child element
	 */
	public static Element getChildElement(Element parent, String childName) {
		return KOHLSBaseApi.getChildElement(parent, childName, false);
	}

	/** overloaded to accept a flag for creating the child element under the parent
	 * @param parent Name of parent element
	 * @param childName Name of child element
	 * @param createChild flag to indicate whether the child element should be created under the passed parent element
	 * @return Element which represents the child element
	 */
	public static Element getChildElement(Element parent, String childName, boolean createChild) {
		Element retVal = null;
		
		if (parent != null) {
			for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
				if (Node.ELEMENT_NODE == node.getNodeType() && childName.equals(node.getNodeName())) {
					retVal = (Element) node;
					return retVal;
				}
			}
		}
		
		if (createChild) {
			retVal = parent.getOwnerDocument().createElement(childName);
			parent.appendChild(retVal);
		}
		
		return retVal;
	}
}
