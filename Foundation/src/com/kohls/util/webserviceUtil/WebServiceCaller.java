package com.kohls.util.webserviceUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.kohls.messaging.v1_0.header.MessageHeaderException;
import com.kohls.messaging.v1_0.header.MessageSenderNodeInfo;
import com.kohls.messaging.v1_0.header.jaxb.MessageHeader;
import com.kohls.messaging.v1_0.header.soap.MessageHeaderProcessorImpl;
import com.kohls.messaging.v1_0.header.soap.SoapMessageUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

/**************************************************************************
 * File : KohlsPoc WebServiceCaller.java Author : IBM Created : June 6
 * 2013 Modified : July 27 2013 Version : 0.2
 ***************************************************************************** 
 * HISTORY
 ***************************************************************************** 
 * V0.1 12/07/2013 IBM First Cut.
 * ***************************************************************************
 * Copyright @ 2013. This document has been prepared and written by IBM Global
 * Services on behalf of Kohls, and is copyright of Kohls
 * 
 ***************************************************************************** 
 ***************************************************************************** 
 * This file is a webservice client that invokes the webservice end point and
 * processes the response
 * 
 * @author 
 * @version 0.2
 *****************************************************************************/
public class WebServiceCaller implements YIFCustomApi {

	private static YFCLogCategory LOG_CAT = YFCLogCategory.instance(WebServiceCaller.class);

	private Properties props;


	/**
	 * Default Constructor class
	 * @throws Exception if anything goes wrong
	 */
	public WebServiceCaller() throws Exception {
		super();
	}
	

	// MLS Code for dynamically generating End point URL - RAVI
	public SOAPMessage callEndpointMLS(SOAPMessage request, String storeID) throws Exception {
		SOAPConnection connection = null;
		SOAPMessage response = null;        
		try {
	
			connection = this.createSOAPConnection();

			QName serviceName = new QName("SterlingServices");
			QName portName = new QName("SterlingServicesSOAP");

			String soapAction = "";
			if (!YFCCommon.isVoid(this.getPropertyValue(this.props
					.getProperty("soapAction")))) {
				soapAction = this.getPropertyValue(this.props
						.getProperty("soapAction"));
			}

			LOG_CAT.debug("the input is :"+soapMessageToString(request));
			Service service = Service.create(serviceName);
			
			//This method is created to dynamically generate MLS end point URL based on Store ID - RAVI
			
			String storeEndpointURL = "http:\\isp" + storeID + this.getPropertyValue(this.props.getProperty("endPoint"));
			LOG_CAT.debug("The portName"+portName+"The storeEndpointURL:"+storeEndpointURL);
			service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, storeEndpointURL);

			
			Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
			Map<String, Object>  map = dispatch.getRequestContext();

			map.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
			map.put(BindingProvider.SOAPACTION_URI_PROPERTY,  soapAction);
			if (!YFCCommon.isVoid(this.getPropertyValue(props.getProperty("timeOut")))){

				map.put("com.sun.xml.ws.connect.timeout",Integer.parseInt(this.getPropertyValue(props.getProperty("timeOut")))); 
				map.put("com.sun.xml.ws.request.timeout",Integer.parseInt(this.getPropertyValue(props.getProperty("timeOut"))));

			}

			LOG_CAT.debug("###### End point URL is ##### : "+this.getPropertyValue(this.props.getProperty("endPoint")));
			if(LOG_CAT.isDebugEnabled()){
				ByteArrayOutputStream req = new ByteArrayOutputStream();
				request.writeTo(req);
				LOG_CAT.debug("###### Request is ##### : \n"+XMLUtil.getXMLString(XMLUtil.getDocument(req.toString())));
				req.flush();
			}
			LOG_CAT.beginTimer("Webservice Call");  

			response=dispatch.invoke(request);


			LOG_CAT.endTimer("Webservice Call");
			if(LOG_CAT.isDebugEnabled()){
				ByteArrayOutputStream resp = new ByteArrayOutputStream();
				response.writeTo(resp);
				LOG_CAT.debug("###### response is ##### : \n"+XMLUtil.getXMLString(XMLUtil.getDocument(resp.toString())));
				resp.flush();
			}

		} catch (Exception e) { 
			if (e.getCause() instanceof java.net.ConnectException) { 
				throw new YFCException("EXTN_CONNECT");
			} else if (e.getCause() instanceof java.io.IOException) {
				throw new YFCException("EXTN_IO");
			} else if (e.getCause() instanceof javax.xml.soap.SOAPException ||
					e.getCause() instanceof javax.xml.ws.soap.SOAPFaultException) {
				throw new YFCException("SOAP_EXCEPTION");
			} else {
				throw new YFCException("EXTN_OTHER");
			}
		} finally {
			if(!YFCCommon.isVoid(System.getProperty("sun.net.client.defaultReadTimeout"))){
				System.clearProperty("sun.net.client.defaultReadTimeout");
			}
		}

		return response;
	}
	
	// MLS Code for getting Store ID - RAVI
	public String storeIDFetch(Document docRequest) {
		
		docRequest.normalizeDocument();
		Element resultElement = (Element) docRequest.getElementsByTagName(KohlsXMLLiterals.A_SHIP_NODE).item(0);
		String storeID = resultElement.getTextContent();
		System.out.println(storeID);	
		return storeID;
	}
	
	// Generating MLS Web service Input XML  - RAVI
	public Document inputSOAPMessage(Document docRequest) {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        Document requestDoc = docRequest;
        Element element = (Element) requestDoc.getElementsByTagName(KohlsXMLLiterals.A_SHIP_NODE).item(0);
        element.getParentNode().removeChild(element);
        requestDoc.normalize();
//        System.out.println(IndUnitTestUtils.getXMLString(requestDoc));	
        return requestDoc;
	}
	
	// MLS WS call for dynamically generating End Point URL based on Store ID - RAVI
	public SOAPBody invokeWSMLS(YFSEnvironment env, Document docRequest)
	throws Exception {
		
		String storeID = storeIDFetch(docRequest);
		Document requestDoc = inputSOAPMessage(docRequest);
		
		//LOG_CAT.debug("in call method of webservice JAVA");

		//LOG_CAT.debug("call to createSoapMessage");
		SOAPMessage request = this.createSoapRequest(requestDoc);

		//LOG_CAT.debug("call to add Header to the SoapMessage");
		this.addHeader(request);

		//LOG_CAT.debug("Call the end point");
		SOAPMessage response = this.callEndpointMLS(request, storeID);

		//LOG_CAT.debug("Get the SoapBody from the response");
		SOAPBody responseBody = response.getSOAPBody();

		return responseBody;
	}

	// Customer Notification WS call and registering error in ASYNC Request table - RAVI
	public SOAPBody invokeWSNotification(YFSEnvironment env, Document requestDoc)
	throws Exception {

		LOG_CAT.debug("in call method of webservice JAVA");

		LOG_CAT.debug("call to createSoapMessage");
		SOAPMessage request = this.createSoapRequest(requestDoc);
		LOG_CAT.debug("SOAP request before adding header");
		
		//Check the input
		if(
			LOG_CAT.isVerboseEnabled()) {

			LOG_CAT.verbose("\nREQUEST:\n");
		try {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		request.writeTo(out);
		String requestString = new String (out.toByteArray());

		LOG_CAT.verbose(requestString);
		} catch (Exception ex) {
		// no need to do anything for eceptions during logging
		}
		}

		LOG_CAT.info("\nREQUEST:\n");
		System.out.println("\nREQUEST:\n");
		request.writeTo(System.out);
		//System.out.println();
		
		
		LOG_CAT.debug("call to add Header to the SoapMessage");
		this.addHeader(request);
		LOG_CAT.debug("SOAP request after adding header");
		//Check the input
		if(
			LOG_CAT.isVerboseEnabled()) {

			LOG_CAT.verbose("\nREQUEST:\n");
		try {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		request.writeTo(out);
		String requestString = new String (out.toByteArray());

		LOG_CAT.verbose(requestString);
		} catch (Exception ex) {
		// no need to do anything for eceptions during logging
		}
		}

		LOG_CAT.info("\nREQUEST:\n");
		System.out.println("\nREQUEST:\n");
		request.writeTo(System.out);
		//System.out.println();
		
		LOG_CAT.debug("Call the end point");
		SOAPMessage response = this.callEndpoint(request);

		LOG_CAT.debug("Get the SoapBody from the response");
		SOAPBody responseBody = response.getSOAPBody();

		return responseBody;
	}

	/**
	 * This function
	 * 1.Prepares the SOAP Request
	 * 2.Invokes the webservice endpoint
	 * 3.Returns the SOAP response as a Document
	 * @param env environment variable
	 * @param requestDoc request Document
	 * @return Document
	 * @throws Exception to handle any abnormal behavior
	 */
	public Document invokeWS(YFSEnvironment env, Document requestDoc)
			throws Exception {

		//LOG_CAT.debug("in call method of webservice JAVA");

		//LOG_CAT.debug("call to createSoapMessage");
		SOAPMessage request = this.createSoapRequest(requestDoc);

		//LOG_CAT.debug("call to add Header to the SoapMessage");
		this.addHeader(request);

		//LOG_CAT.debug("Call the end point");
		SOAPMessage response = this.callEndpoint(request);

		//LOG_CAT.debug("Get the SoapBody from the response");
		SOAPBody responseBody = response.getSOAPBody();

		//LOG_CAT.debug("Check whether the response is having any fault in the body");
		if (responseBody.hasFault()) {
			this.logSoapFault(responseBody);
			if(this.getPropertyValue(this.props.getProperty("appName")).compareToIgnoreCase("PAYMENTS")!=0)
				responseBody = this.getFaultformated(responseBody);

		}

		//LOG_CAT.debug("return the soapbody");
		return responseBody.extractContentAsDocument();
	}


	/**
	 * This method handles all soap fault message body
	 * @param responseBody which contain SAOPFault
	 * @return SOAPBody in well formated manner
	 * @throws Exception to handle any abnormal behavior
	 */

	public SOAPBody getFaultformated(SOAPBody responseBody) throws Exception { 

		String faultcode = responseBody.getFault().getFaultCode();
		String faultstring = responseBody.getFault().getFaultString();

		String code = null;
		String msg = null;
		Detail soapDetail = responseBody.getFault().getDetail();

		if (!YFCCommon.isVoid(soapDetail)) {

			code = XPathUtil.getString(soapDetail, "code");

			msg = XPathUtil.getString(soapDetail, "message");

		}

		if (YFCCommon.isVoid(code)) {
			code = faultstring;
		}

		if (YFCCommon.isVoid(msg)) {
			msg = "Undetermined error description";
		}

		System.setProperty("javax.xml.soap.MessageFactory",
				"weblogic.xml.saaj.MessageFactoryImpl");

		MessageFactory mf = MessageFactory.newInstance();
		SOAPMessage faultresponse = mf.createMessage();
		SOAPPart soapPart = faultresponse.getSOAPPart();

		SOAPEnvelope env = soapPart.getEnvelope();
		SOAPBody soapBody = env.getBody();

		SOAPFault fault = soapBody.addFault();

		fault.addNamespaceDeclaration("S", "http://schemas.xmlsoap.org/soap/envelope/");
		fault.addNamespaceDeclaration(soapBody.getPrefix(), "http://schemas.xmlsoap.org/soap/envelope/");

		fault.setFaultCode(faultcode);
		fault.setFaultString(faultstring);

		Detail myDetail = fault.addDetail();

		QName entryName = new QName("code");
		DetailEntry entry = myDetail.addDetailEntry(entryName);
		entry.addTextNode(code);

		QName entryName2 = new QName("message");
		DetailEntry entry2 = myDetail.addDetailEntry(entryName2);
		entry2.addTextNode(msg);

		return faultresponse.getSOAPBody();
	}

	/**
	 * This function is used for creating a SOAPRequest with requestDoc added to the SOAPBody 
	 * @param requestDoc Input Document using which we need to prepare a SOAPMessage.
	 * @return SOAPMessage
	 * @throws SOAPException when there is a SOAPException occur
	 */
	public SOAPMessage createSoapRequest(Document requestDoc)
			throws SOAPException {

		LOG_CAT.debug("In createSoapMessage method in the webservice");

		System.setProperty("javax.xml.soap.MessageFactory",
				"weblogic.xml.saaj.MessageFactoryImpl");

		MessageFactory mf = MessageFactory.newInstance();
		SOAPMessage request = mf.createMessage();
		SOAPPart soapPart = request.getSOAPPart();

		LOG_CAT.debug("In createSoapMessage method in the webservice1");
		SOAPEnvelope env = soapPart.getEnvelope();
		SOAPBody soapBody = env.getBody();
		soapBody.addDocument(requestDoc);

		return request;
	}

	/**
	 * This public method is used for logging the SOAPFault 
	 * @param responseBody of the response we got from the Soap call
	 */
	public void logSoapFault(SOAPBody responseBody) {
		//LOG_CAT.debug("In the call soap fault method in webservice");
		SOAPFault fault = responseBody.getFault();
		String msg = "Encountered SOAP Fault " + fault.getFaultCode()
				+ " while calling endpoint";
		//LOG_CAT.error(msg);
	}

	/**
	 * This function is used for calling the endpoint and fetching SOAP Response from it 
	 * @param request Using which we will call the endpoint
	 * @return SOAPMessage response from the endpoint after the call
	 * @exception Exception throws a specific exception depending upon the exception arrived
	 */
	public SOAPMessage callEndpoint(SOAPMessage request) throws Exception {
		SOAPConnection connection = null;
		SOAPMessage response = null;        
		try {
			LOG_CAT.debug("In callendpoint method in the webservice");



			connection = this.createSOAPConnection();

			//Seetha 12/13 Changes for ReadTimeout --START
			/*Map<String, Object> requestCtx = ((BindingProvider) port).getRequestContext();
             requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
             props.getProperty("END_POINT_URL"));

             String connectTimeout = props.getProperty("CONNECT_TIMEOUT");

             if (connectTimeout != null&& !connectTimeout.isEmpty())
                 requestCtx.put("com.sun.xml.ws.connect.timeout",Integer.parseInt(connectTimeout));

             String requestTimeout = props.getProperty("REQUEST_TIMEOUT");

             if (requestTimeout != null&& !requestTimeout.isEmpty())
                 requestCtx.put("com.sun.xml.ws.request.timeout",Integer.parseInt(requestTimeout));*/

			QName serviceName = new QName("SterlingServices");
			QName portName = new QName("SterlingServicesSOAP");

			String soapAction = "";
			if (!YFCCommon.isVoid(this.getPropertyValue(this.props
					.getProperty("soapAction")))) {
				soapAction = this.getPropertyValue(this.props
						.getProperty("soapAction"));
			}

			System.out.println("the input is :"+soapMessageToString(request));
			LOG_CAT.debug("the input is :"+soapMessageToString(request));
			Service service = Service.create(serviceName);
			service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, this.getPropertyValue(this.props.getProperty("endPoint")));



			Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
			Map<String, Object>  map = dispatch.getRequestContext();

			map.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
			map.put(BindingProvider.SOAPACTION_URI_PROPERTY,  soapAction);
			//added on  06/01/2014.
			
			if (!YFCCommon.isVoid(this.getPropertyValue(props.getProperty("timeOut")))){

				map.put("com.sun.xml.ws.connect.timeout",Integer.parseInt(this.getPropertyValue(props.getProperty("timeOut")))); 
				map.put("com.sun.xml.ws.request.timeout",Integer.parseInt(this.getPropertyValue(props.getProperty("timeOut"))));
				//  map.put("com.sun.xml.ws.read.timeout", Integer.parseInt(this.getPropertyValue(props.getProperty("timeOut"))));

			}

			LOG_CAT.debug("###### End point URL is ##### : "+this.getPropertyValue(this.props.getProperty("endPoint")));
			if(LOG_CAT.isDebugEnabled()){
				ByteArrayOutputStream req = new ByteArrayOutputStream();
				request.writeTo(req);
				LOG_CAT.debug("###### Request is ##### : \n"+XMLUtil.getXMLString(XMLUtil.getDocument(req.toString())));
				req.flush();
			}
			/*URL endpointURL=null;
            if(this.getPropertyValue(this.props.getProperty("appName")).compareToIgnoreCase("PAYMENTS")==0){
            String spec=this.getPropertyValue(this.props.getProperty("endPoint"));
            endpointURL = new URL(null,spec,new URLStreamHandler() {
                       @Override
                       protected URLConnection openConnection(URL url) throws IOException {
                          URL clone_url = new URL(url.toString());
                          HttpURLConnection clone_urlconnection =  (HttpURLConnection)clone_url.openConnection();
                          clone_urlconnection.setConnectTimeout(10000);
                          clone_urlconnection.setReadTimeout(10000);
                          return(clone_urlconnection); 
                       }
                });*/
			LOG_CAT.beginTimer("Webservice Call");  

			response=dispatch.invoke(request);

			//Seetha 12/13 Changes for ReadTimeout -- END


			LOG_CAT.endTimer("Webservice Call");
			if(LOG_CAT.isDebugEnabled()){
				ByteArrayOutputStream resp = new ByteArrayOutputStream();
				response.writeTo(resp);
				LOG_CAT.debug("###### response is ##### : \n"+XMLUtil.getXMLString(XMLUtil.getDocument(resp.toString())));
				resp.flush();
			}

		} catch (Exception e) { 

			// Temporary statement
			//e.printStackTrace();
			LOG_CAT.error(e);

			if (e.getCause() instanceof java.net.ConnectException) { 
				throw new YFCException("EXTN_CONNECT");
			} else if (e.getCause() instanceof java.io.IOException) {
				throw new YFCException("EXTN_IO");
			} else if (e.getCause() instanceof javax.xml.soap.SOAPException ||
					e.getCause() instanceof javax.xml.ws.soap.SOAPFaultException) {
				throw new YFCException("SOAP_EXCEPTION");
			} else {
				throw new YFCException("EXTN_OTHER");
			}
		} finally {
			if(!YFCCommon.isVoid(System.getProperty("sun.net.client.defaultReadTimeout"))){
				System.clearProperty("sun.net.client.defaultReadTimeout");
			}
			//connection.close();           
		}

		return response;
	}

	/**
	 * This function is used for creating a SOAPConnection
	 * @return SOAPConnection connection 
	 */
	public SOAPConnection createSOAPConnection() {

		//LOG_CAT.debug("In the createSoapConnection method in webservice");
		System.setProperty("javax.xml.soap.SOAPConnectionFactory",
				"weblogic.wsee.saaj.SOAPConnectionFactoryImpl");
		System.setProperty("sun.net.client.defaultConnectTimeout",
				this.getPropertyValue(this.props.getProperty("timeOut")));


		/*if(!YFCCommon.isVoid(this.props.getProperty("readTimeOut"))){
            System.setProperty("sun.net.client.defaultReadTimeout",this.getPropertyValue(this.props.getProperty("readTimeOut")));           
        }*/
		SOAPConnection connection = null;
		try {
			//LOG_CAT.debug("Create Soap Connection");
			connection =  SOAPConnectionFactory.newInstance().createConnection();
		} catch (SOAPException e) {
			//LOG_CAT.debug("SOAPException in the creation of SOAPConnection");
		} 

		return connection;
	}

	/**
	 * This function is used for adding Header based on Kohls standards to the SOAPMessage 
	 * Message ID and Node ID will be added to the header using the createRequestMessageHeader() function
	 * @param request to which MessageHeader is added
	 */
	public void addHeader(SOAPMessage request) {

		try {
			LOG_CAT.debug("In call to addHeader method in webservice");
			MessageHeaderProcessorImpl headprocessorimpl = new MessageHeaderProcessorImpl();
			MessageSenderNodeInfo msgsendernodeinfo = new MessageSenderNodeInfo();

			LOG_CAT.debug("Set message header details");
			msgsendernodeinfo.setSystemCode(this.getPropertyValue(this.props.getProperty("systemCode")));
			msgsendernodeinfo.setModule(this.getPropertyValue(this.props.getProperty("module")));
			msgsendernodeinfo.setAppName(this.getPropertyValue(this.props.getProperty("appName")));

			LOG_CAT.debug("Set message sender node info");
			headprocessorimpl.setMessageSenderNodeInfo(msgsendernodeinfo);

			LOG_CAT.debug("Add SOAPAction");
			MimeHeaders hd = request.getMimeHeaders();
			if (!YFCCommon.isVoid(this.props.getProperty("soapAction"))) {
				hd.addHeader("SOAPAction", this.getPropertyValue(this.props.getProperty("soapAction")));
			}

			LOG_CAT.debug("call to create Request Message Header");
			MessageHeader header = headprocessorimpl.createRequestMessageHeader();

			LOG_CAT.debug("Add Version");
			if (!YFCCommon.isVoid(this.props.getProperty("hdrVersion"))) {
				header.setVersion(this.getPropertyValue(this.props.getProperty("hdrVersion")));
			}

			LOG_CAT.debug("Add Action");
			if (!YFCCommon.isVoid(this.props.getProperty("action"))) {
				header.setAction(this.getPropertyValue(this.props.getProperty("action")));
			}

			LOG_CAT.debug("Put header to the soap message");
			SoapMessageUtil.putMessageHeader(request, header);
		} catch (MessageHeaderException e) {
			LOG_CAT.debug("MessageHeaderException in the calladdHeader method in the webservice");
		}

	}

	/**
	 * Sets the properties
	 * @param prop Properties that need to be set
	 * @throws Exception when unable to set the Property
	 */

	public void setProperties(Properties prop) throws Exception {
		this.props = prop;
		//LOG_CAT.debug("In the set properties method");

	}

	/**
	 * This function is used to get the value for a property
	 * @param property name in string format
	 * @return String propValue
	 */
	public String getPropertyValue(String property) {

		String propValue;
		propValue = YFSSystem.getProperty(property);
		//Manoj 10/22: updated to use configured property if 
		// customer_overrides.properties does not return any value
		if(YFCCommon.isVoid(propValue)){
			propValue = property;
		}
		return propValue;

	}
	
	public String soapMessageToString(SOAPMessage message) 
    {
        String result = null;

        if (message != null) 
        {
            ByteArrayOutputStream baos = null;
            try 
            {
                baos = new ByteArrayOutputStream();
                message.writeTo(baos); 
                result = baos.toString();
            } 
            catch (Exception e) 
            {
            } 
            finally 
            {
                if (baos != null) 
                {
                    try 
                    {
                        baos.close();
                    } 
                    catch (Exception ex) 
                    {
                    }
                }
            }
        }
        return result;
    }   


}

