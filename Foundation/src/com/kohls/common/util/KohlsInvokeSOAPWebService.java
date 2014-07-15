package com.kohls.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsInvokeSOAPWebService {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsInvokeSOAPWebService.class);
	
	Properties oProperties = new Properties();
	/**
	 * This method is overridden from interface.
	 * @param properties Properties set
	 */
	public void setProperties(Properties properties) {
		if (properties != null) {
			this.oProperties = properties;
		}
	}

	/**
	 * This method is overridden from interface.
	 * @return Properties
	 */
	public Properties getProperties() {
		return this.oProperties;
	}
	
	public static Document invokeMultiNameSpaceSoapWebservice(YFSEnvironment env, Document inDoc,	String yfsEndPointURL, 
			String yfsEndPointUser, String yfsEndPointPassword, String yfsSoapActionOperation, 
			String yfskohlsProxyHost,String yfskohlsProxyPort,String yfskohlsProxyUser,String yfskohlsProxyPassword, ArrayList<String> nameSpacePrefixes, ArrayList<String> nameSpaceURIs) throws ParserConfigurationException, Exception {
		log.beginTimer("invokeMultiNameSpaceSoapWebservice");
		try {
			String stringSOAPFactory = YFSSystem.getProperty("KOHLS_SOAPFcatory");
			String stringSOAPConnectionFactory = YFSSystem.getProperty("KOHLS_SOAPConnectionFactory");
			String stringSOAPMessageFactory = YFSSystem.getProperty("KOHLS_MessageFactory");
			String stringTransformerFactory = YFSSystem.getProperty("KOHLS_TransformerFactory");
			
			log.info("yfsEndPointURL =" + yfsEndPointURL);
			log.info("yfsEndPointUser =" + yfsEndPointUser);
			log.info("yfsEndPointPassword =" + yfsEndPointPassword);
			log.info("yfsSoapActionOperation =" + yfsSoapActionOperation);
			log.info("yfskohlsProxyHost =" + yfskohlsProxyHost);
			log.info("yfskohlsProxyPort =" + yfskohlsProxyPort);
			log.info("yfskohlsProxyUser =" + yfskohlsProxyUser);
			log.info("yfskohlsProxyPassword =" + yfskohlsProxyPassword);
			log.info("nameSpacePrefixes =" + nameSpacePrefixes);
			log.info("nameSpaceURIs =" + nameSpaceURIs);
			log.info("inDoc =" + SCXmlUtil.getString(inDoc));
			log.info("stringSOAPFactory =" + stringSOAPFactory);
			log.info("stringSOAPConnectionFactory =" + stringSOAPConnectionFactory);
			log.info("stringSOAPMessageFactory =" + stringSOAPMessageFactory);
			log.info("stringTransformerFactory =" + yfsEndPointURL);
			
			//YFCObject.isVoid(oProperties.getProperty("KOHLS_PROXY_HOST"))? oProperties.getProperty("KOHLS_PROXY_HOST"): YFSSystem.getProperty("KOHLS_PROXY_HOST");
			//String stringKohlsProxyPort=YFSSystem.getProperty("KOHLS_PROXY_PORT");
			//String stringKohlsProxyUser=YFSSystem.getProperty("KOHLS_PROXY_USER");
			//String stringKohlsProxyPass=YFSSystem.getProperty("KOHLS_PROXY_PASSWORD");
			
			if(YFCObject.isVoid(stringSOAPFactory)) {
				System.setProperty("javax.xml.soap.SOAPFactory", "org.apache.axis2.saaj.SOAPFactoryImpl");
			} else {
				System.setProperty("javax.xml.soap.SOAPFactory", stringSOAPFactory);
			}
			
			if(YFCObject.isVoid(stringSOAPConnectionFactory)){
				System.setProperty("javax.xml.soap.SOAPConnectionFactory","org.apache.axis2.saaj.SOAPConnectionFactoryImpl");
			} else {
				System.setProperty("javax.xml.soap.SOAPConnectionFactory",stringSOAPConnectionFactory);
			}
			
			if(YFCObject.isVoid(stringSOAPMessageFactory)) {
				System.setProperty("javax.xml.soap.MessageFactory","org.apache.axis2.saaj.MessageFactoryImpl");
			} else {
				System.setProperty("javax.xml.soap.MessageFactory", stringSOAPMessageFactory);
			}
			
			if(YFCObject.isVoid(stringTransformerFactory)) {
				System.setProperty("javax.xml.transform.TransformerFactory","org.apache.xalan.processor.TransformerFactoryImpl");
			} else {
				System.setProperty("javax.xml.transform.TransformerFactory", stringTransformerFactory);
			}
			
			if(!YFCObject.isVoid(yfskohlsProxyHost) && !YFCObject.isVoid(yfskohlsProxyPort) ) {
				System.setProperty("http.proxyHost",yfskohlsProxyHost);
				System.setProperty("http.proxyPort", yfskohlsProxyPort);
				
				if(log.isVerboseEnabled()) {
					log.verbose("http.proxyHost:" + yfskohlsProxyHost);
					log.verbose("http.proxyPort:" + yfskohlsProxyHost);
				}
				System.out.println("http.proxyHost:" + yfskohlsProxyHost);
				System.out.println("http.proxyPort:" + yfskohlsProxyPort);
			} 
			
			if(!YFCObject.isVoid(yfskohlsProxyUser) && !YFCObject.isVoid(yfskohlsProxyPassword)){
				System.setProperty("http.proxyUser", yfskohlsProxyUser);
				System.setProperty("http.proxyPassword", yfskohlsProxyPassword);
				if(log.isVerboseEnabled()) {
					log.verbose("http.proxyUser:" + yfskohlsProxyUser);				
				}
				log.verbose("http.proxyUser:" + yfskohlsProxyUser);
			}
			
			//String endPointURL = oProperties.getProperty("endPointURL");
			
			//System.out.println();
				log.info("javax.xml.soap.SOAPFactory:" + System.getProperty("javax.xml.soap.SOAPFactory"));
				log.info("javax.xml.soap.SOAPConnectionFactory:" + System.getProperty("javax.xml.soap.SOAPConnectionFactory"));
				log.info("javax.xml.soap.MessageFactory:" + System.getProperty("javax.xml.soap.MessageFactory"));
				log.info("javax.xml.transform.TransformerFactory:" + System.getProperty("javax.xml.transform.TransformerFactory"));
			//System.out.println();
			
			return invokeMultiNameSpaceSOAPWebservice(inDoc, yfsEndPointURL, yfsSoapActionOperation, nameSpacePrefixes, nameSpaceURIs);
		} catch (Exception e) {
			log.error(e);
			throw e;
		} finally {
			log.endTimer("invokeMultiNameSpaceSoapWebservice");
		}
	}
	
	private static Document invokeMultiNameSpaceSOAPWebservice(Document inDoc, String yfsEndPointURL, 
			String yfsSoapActionOperation, ArrayList<String> nameSpacePrefixes, ArrayList<String> nameSpaceURIs) throws SOAPException, IOException, TransformerException, ParserConfigurationException {
		log.info("Constructing the SOAP request");
		SOAPMessage request = constructMultiNameSpaceSOAPMessage(inDoc, yfsSoapActionOperation, nameSpacePrefixes, nameSpaceURIs);
		
		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection connection = soapConnectionFactory.createConnection();
		
		//Check the input
		if(log.isVerboseEnabled()) {
			log.verbose("\nREQUEST:\n");
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				request.writeTo(out);
				String requestString = new String (out.toByteArray());
				log.verbose(requestString);
			} catch (Exception ex) {
				// no need to do anything for eceptions during logging
			}
		}
		log.info("\nREQUEST:\n");
		System.out.println("\nREQUEST:\n");
		request.writeTo(System.out);
		//System.out.println();
		
		SOAPMessage response = null;
		try {
			
		    response = connection.call(request, yfsEndPointURL);		
			
			
		} catch (Exception e) {
			log.error(e);
			throw new SOAPException(e);
		} finally{
			connection.close();	
			soapConnectionFactory = null;
			connection=null;
		}
		
		//Check the input
		if(log.isVerboseEnabled()) {
			log.verbose("\nRESPONSE:\n");
		}
		
		//System.out.println("\nRESPONSE:\n");
		//response.writeTo(System.out);
		//System.out.println();
		
		return getSOAPXMLResponse(response);
	}

	private static SOAPMessage constructMultiNameSpaceSOAPMessage(Document inDoc, String yfsSoapActionOperation, ArrayList<String> nameSpacePrefixes, ArrayList<String> nameSpaceURIs) throws SOAPException, IOException, TransformerException {
		
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		soapMessage.getMimeHeaders().addHeader("SOAPAction", yfsSoapActionOperation);
		SOAPPart soapPart = soapMessage.getSOAPPart();
		SOAPEnvelope soapEnv = soapPart.getEnvelope();
		// add all available namespaces, assume both are same size
		for (int i=0; i < nameSpacePrefixes.size(); i++) {
			soapEnv.addNamespaceDeclaration(nameSpacePrefixes.get(i), nameSpaceURIs.get(i));
		}
        DOMSource domSource = new DOMSource(mergeContentWithSOAPMessage(soapPart, inDoc));
		soapPart.setContent(domSource);
		
		return soapMessage;
	}

	
	public static Document invokeSoapWebservice(YFSEnvironment env, Document inDoc,	String yfsEndPointURL, 
			String yfsEndPointUser, String yfsEndPointPassword, String yfsSoapActionURI, String yfsSoapActionURIPrefix, 
					String yfsSoapActionOperation, String yfsproShipDataContractURI, String yfsproShipDataContractPrefix,String yfskohlsProxyHost,String yfskohlsProxyPort,String yfskohlsProxyUser,String yfskohlsProxyPassword) throws ParserConfigurationException, Exception {
		
		String stringSOAPFactory = YFSSystem.getProperty("KOHLS_SOAPFcatory");
		String stringSOAPConnectionFactory = YFSSystem.getProperty("KOHLS_SOAPConnectionFactory");
		String stringSOAPMessageFactory = YFSSystem.getProperty("KOHLS_MessageFactory");
		String stringTransformerFactory = YFSSystem.getProperty("KOHLS_TransformerFactory");
		
		
		//YFCObject.isVoid(oProperties.getProperty("KOHLS_PROXY_HOST"))? oProperties.getProperty("KOHLS_PROXY_HOST"): YFSSystem.getProperty("KOHLS_PROXY_HOST");
		//String stringKohlsProxyPort=YFSSystem.getProperty("KOHLS_PROXY_PORT");
		//String stringKohlsProxyUser=YFSSystem.getProperty("KOHLS_PROXY_USER");
		//String stringKohlsProxyPass=YFSSystem.getProperty("KOHLS_PROXY_PASSWORD");
		
		if(YFCObject.isVoid(stringSOAPFactory)) {
			System.setProperty("javax.xml.soap.SOAPFactory", "org.apache.axis2.saaj.SOAPFactoryImpl");
		} else {
			System.setProperty("javax.xml.soap.SOAPFactory", stringSOAPFactory);
		}
		
		if(YFCObject.isVoid(stringSOAPConnectionFactory)){
			System.setProperty("javax.xml.soap.SOAPConnectionFactory","org.apache.axis2.saaj.SOAPConnectionFactoryImpl");
		} else {
			System.setProperty("javax.xml.soap.SOAPConnectionFactory",stringSOAPConnectionFactory);
		}
		
		if(YFCObject.isVoid(stringSOAPMessageFactory)) {
			System.setProperty("javax.xml.soap.MessageFactory","org.apache.axis2.saaj.MessageFactoryImpl");
		} else {
			System.setProperty("javax.xml.soap.MessageFactory", stringSOAPMessageFactory);
		}
		
		if(YFCObject.isVoid(stringTransformerFactory)) {
			System.setProperty("javax.xml.transform.TransformerFactory","org.apache.xalan.processor.TransformerFactoryImpl");
		} else {
			System.setProperty("javax.xml.transform.TransformerFactory", stringTransformerFactory);
		}

		if(!YFCObject.isVoid(yfskohlsProxyHost) && !YFCObject.isVoid(yfskohlsProxyPort) ) {
			System.setProperty("http.proxyHost",yfskohlsProxyHost);
			System.setProperty("http.proxyPort", yfskohlsProxyPort);
			
			if(log.isVerboseEnabled()) {
				log.verbose("http.proxyHost:" + yfskohlsProxyHost);
				log.verbose("http.proxyPort:" + yfskohlsProxyHost);
			}
			System.out.println("http.proxyHost:" + yfskohlsProxyHost);
			System.out.println("http.proxyPort:" + yfskohlsProxyPort);
		} 
		
		if(!YFCObject.isVoid(yfskohlsProxyUser) && !YFCObject.isVoid(yfskohlsProxyPassword)){
			System.setProperty("http.proxyUser", yfskohlsProxyUser);
			System.setProperty("http.proxyPassword", yfskohlsProxyPassword);
			if(log.isVerboseEnabled()) {
				log.verbose("http.proxyUser:" + yfskohlsProxyUser);				
			}
			log.verbose("http.proxyUser:" + yfskohlsProxyUser);
		}
		
		//String endPointURL = oProperties.getProperty("endPointURL");
		
		//System.out.println();
		if(log.isVerboseEnabled()) {
		log.verbose("javax.xml.soap.SOAPFactory:" + System.getProperty("javax.xml.soap.SOAPFactory"));
		log.verbose("javax.xml.soap.SOAPConnectionFactory:" + System.getProperty("javax.xml.soap.SOAPConnectionFactory"));
		log.verbose("javax.xml.soap.MessageFactory:" + System.getProperty("javax.xml.soap.MessageFactory"));
		log.verbose("javax.xml.transform.TransformerFactory:" + System.getProperty("javax.xml.transform.TransformerFactory"));
		}
		//System.out.println();
		
		
		return invokeSOAPWebservice(inDoc, yfsEndPointURL, yfsSoapActionURI, yfsSoapActionURIPrefix, yfsSoapActionOperation, yfsproShipDataContractURI, yfsproShipDataContractPrefix);
	}

	private static Document invokeSOAPWebservice(Document inDoc, String yfsEndPointURL, String yfsSoapActionURI, String yfsSoapActionURIPrefix, 
			String yfsSoapActionOperation, String yfsproShipDataContractURI, String yfsproShipDataContractPrefix) throws SOAPException, IOException, TransformerException, ParserConfigurationException {
		
		SOAPMessage request = constructSOAPMessage(inDoc, yfsSoapActionURI, yfsSoapActionURIPrefix, yfsSoapActionOperation, yfsproShipDataContractURI, yfsproShipDataContractPrefix);
		
		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection connection = soapConnectionFactory.createConnection();
		
		//Check the input
		if(log.isVerboseEnabled()) {
			log.verbose("\nREQUEST:\n");
		}
		
		//System.out.println("\nREQUEST:\n");
		//request.writeTo(System.out);
		//System.out.println();
		
		SOAPMessage response = null;
		try {
			
		    response = connection.call(request, yfsEndPointURL);		
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SOAPException(e);
		} finally{
			connection.close();	
			soapConnectionFactory = null;
			connection=null;
		}
		
		//Check the input
		if(log.isVerboseEnabled()) {
			log.verbose("\nRESPONSE:\n");
		}
		
		//System.out.println("\nRESPONSE:\n");
		//response.writeTo(System.out);
		//System.out.println();
		
		return getSOAPXMLResponse(response);
	}
	
	private static Document getSOAPXMLResponse(
			SOAPMessage response) throws TransformerConfigurationException, SOAPException, TransformerException, ParserConfigurationException {
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer =
		transformerFactory.newTransformer();
		Source sourceContent = response.getSOAPPart().getContent();
		DOMResult result = new DOMResult();
		transformer.transform(sourceContent, result);
		Node domSOAPMessageContent = getSOAPBody(result.getNode().getFirstChild()).getFirstChild();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document outDoc = db.newDocument();
		Node node = outDoc.importNode(domSOAPMessageContent, true);
		outDoc.appendChild(node);
		
		if(log.isVerboseEnabled()) {
			log.verbose("Webservice OutDoc:"+getXMLString(outDoc));
		}
		log.info("Webservice OutDoc:"+getXMLString(outDoc));
		//System.out.println();
		//System.out.println("Webservice OutDoc:"+getXMLString(outDoc));
		
		return outDoc;
	}

	private static SOAPMessage constructSOAPMessage(Document inDoc, String yfsSoapActionURI, String yfsSoapActionURIPrefix, 
			String yfsSoapActionOperation, String yfsproShipDataContractURI, String yfsproShipDataContractPrefix) throws SOAPException, IOException, TransformerException {
		
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		soapMessage.getMimeHeaders().addHeader("SOAPAction", yfsSoapActionOperation);
		SOAPPart soapPart = soapMessage.getSOAPPart();
		SOAPEnvelope soapEnv = soapPart.getEnvelope();
		soapEnv.addNamespaceDeclaration(yfsSoapActionURIPrefix, yfsSoapActionURI);
        if(!YFCObject.isNull(yfsproShipDataContractURI) && !YFCObject.isVoid(yfsproShipDataContractURI) && 
        		!YFCObject.isNull(yfsproShipDataContractPrefix) && !YFCObject.isVoid(yfsproShipDataContractPrefix)) {
        	soapEnv.addNamespaceDeclaration(yfsproShipDataContractPrefix, yfsproShipDataContractURI);
        }
        DOMSource domSource = new DOMSource(mergeContentWithSOAPMessage(soapPart, inDoc));
		soapPart.setContent(domSource);
		
		return soapMessage;
	}
	
	private static Document mergeContentWithSOAPMessage(
			SOAPPart soapPart, Document inDoc) throws SOAPException, TransformerException {
		
		Source src = soapPart.getContent();   
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();   
        DOMResult result = new DOMResult();   
        transformer.transform(src, result);   
        Node soapBodyEle = getSOAPBody(result.getNode().getFirstChild());
        
        return soapBodyEle.appendChild(soapBodyEle.getOwnerDocument().importNode(inDoc.getDocumentElement(), true)).getOwnerDocument();
	}
	
	public static Element getChildElement(Element target, String elementName, boolean create) {
		for(Node n = target.getFirstChild(); n != null; n = n.getNextSibling())
            if(n.getNodeType() == 1 && n.getNodeName().equals(elementName))
            	return (Element)n;
        if(create) {
            Element retVal = target.getOwnerDocument().createElement(elementName);
            target.appendChild(retVal);
            return retVal;
        } else {
            return null;
        }
    }
	
	private static String getXMLString(Document outDoc) throws TransformerConfigurationException, TransformerException {
		
		StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(new DOMSource(outDoc), new StreamResult(sw));
        
        return sw.toString();
	}
	
	private static Node getSOAPBody(Node soapEnvelope) {
		
		NodeList nodeList = soapEnvelope.getChildNodes();
        for(int i=0; i < nodeList.getLength(); i++) {
        	Node tempNode = nodeList.item(i);
        	if(tempNode.getLocalName().equalsIgnoreCase("body")) {
        		return tempNode;
        	}
        }
        return soapEnvelope;
	}
	
}