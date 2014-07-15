package com.kohls.bopus.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSException;


/**
 * Util class for Interop servlet
 * 
 */

public class KohlsHTTPPostUtil {

	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 */

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsHTTPPostUtil.class.getName());

	private StringBuffer XmlIn;
	private static StringBuffer XmlOut;
	private static Map propertiesMap = new HashMap();

	private static String readURLConnection(URLConnection uc) throws Exception {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			int letter = 0;
			while ((letter = reader.read()) != -1)
				buffer.append((char) letter);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException io) {
				throw io;
			}
		}
		return buffer.toString();
	}

	private static Document tryConnection(java.net.HttpURLConnection connection, OutputStream stream,String  m )   throws IOException {
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);

		OutputStream out = connection.getOutputStream();
		out.write(m.getBytes());
		out.close();

		String data = "";
		Document doc = null;
		try {
			data = readURLConnection(connection);

			doc = KohlsXMLUtil.getDocument(data);

			//doc = getXMLDocument(data);            
			boolean b=connection.usingProxy();
			if(b)
			{
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("******** USING PROXY *************");
				}
			}

		}catch (NullPointerException ne) {
			ne.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		XmlOut = new StringBuffer(data);

		return doc;
	}// end of method

	public static Document contactService(String strurl,String proxyHost,String req) throws Exception{
		Document retDoc =null;
		OutputStream out = System.out;
		String protocol=KohlsConstant.HTTP;

		String prefix   = "";
		if(proxyHost==null||"".equals(proxyHost)||" ".equals(proxyHost))
			proxyHost = "";
		else {
			System.setProperty(KohlsXMLLiterals.PROXY_SET,KohlsConstant.TRUE);
			System.setProperty(KohlsXMLLiterals.PROXY_HOST, proxyHost);
		}

		if(strurl==null||"".equals(strurl)) {
			throw new YFSException("Host name null");
		}

		StringBuffer XmlIn;
		StringBuffer XmlOut;

		String query = "";

		StringBuffer  buffer = new StringBuffer(req);

		String handlers = System.getProperty("java.protocol.handler.pkgs");
		System.setProperty("java.protocol.handler.pkgs",
				"com.sun.net.ssl.internal.www.protocol|" + handlers);        

		URL url = null;
		try {

			url = new URL(strurl);
			java.net.HttpURLConnection connection =
					(java.net.HttpURLConnection) url.openConnection();

			retDoc = tryConnection(connection, out, buffer.toString());

			int statusCode=connection.getResponseCode();
			if(connection.HTTP_CLIENT_TIMEOUT==statusCode){
				connection.disconnect();				
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("******** CLIENT TIME OUT *************");
				}
				Exception timeout=new Exception("CLIENT TIME OUT");
				throw timeout;
			}
			else if (-1==statusCode) {
				connection.disconnect();
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("******** NOT a valid HTTP Req *************");
				}
				Exception notvalidhttp=new Exception("NOT a valid HTTP Req");
				throw notvalidhttp;
			}

		} catch(UnknownHostException unk){
			unk.printStackTrace();
			throw new YFSException("Host Unknown, Check for server url");

		}catch (Exception e) {

			e.printStackTrace();
			throw new YFSException(e.getMessage());
		}

		return retDoc;
	}
	public static Document transmit(String strurl,String proxyHost,String request) {

		Document retDoc =null;
		try {
			retDoc =  contactService(strurl,proxyHost,request)  ;

		}catch(Exception e) {
			throw new RuntimeException("Cannot contact the service: "+ e.toString());

		}

		return retDoc;
	}


	private static Document createHTTPquery(Properties propertiesMap, Document template, String flow){
		try{

			String proxyHost="";
			String interopApiName = propertiesMap.getProperty(KohlsXMLLiterals.INTEROP_API_NAME);
			String userId=propertiesMap.getProperty(KohlsXMLLiterals.YFSENV_USERID);
			String password=propertiesMap.getProperty(KohlsXMLLiterals.YFSENV_PWD);
			String inXmlString =propertiesMap.getProperty(KohlsXMLLiterals.INTEROP_API_DATA);
			String progId = propertiesMap.getProperty(KohlsXMLLiterals.YFSENV_PROGID);
			String strurl =propertiesMap.getProperty(KohlsXMLLiterals.URL);

			if(progId==null){
				progId=KohlsConstant.YANTRA_HTTP_TESTER;
			}

			String content=null;
			if(template!=null) {
				String strtemplate=XMLUtil.getXMLString(template);

				if(YFCLogUtil.isDebugEnabled()){
					log.debug("YANTRA\\CUSTOM API:"+interopApiName +":Template XMLDocument:[ "+ strtemplate+" ]");
				}
				content = "?" + KohlsConstant.YFSENV_PROG_ID + "=" + URLEncoder.encode(progId)+
						"&"+KohlsConstant.INTEROP_API_NAME+"="+	URLEncoder.encode(interopApiName)+
						"&"+ KohlsConstant.IS_FLOW +"="+URLEncoder.encode(flow)+
						"&"+ KohlsConstant.INTEROP_API_DATA +"="+URLEncoder.encode(inXmlString)+
						"&"+KohlsConstant.TEMPLATE_DATA+"="+URLEncoder.encode(strtemplate)+
						"&"+ KohlsConstant.YFSENV_USERID+"="+URLEncoder.encode(userId)+
						"&" + KohlsConstant.YFSENV_PWD+"="+URLEncoder.encode(password);

			}
			else{
				content = "?" + KohlsConstant.YFSENV_PROG_ID + "=" + URLEncoder.encode(progId)+
						"&"+KohlsConstant.INTEROP_API_NAME+"="+	URLEncoder.encode(interopApiName)+
						"&"+ KohlsConstant.IS_FLOW +"="+URLEncoder.encode(flow)+
						"&"+ KohlsConstant.INTEROP_API_DATA +"="+URLEncoder.encode(inXmlString)+
						"&"+ KohlsConstant.YFSENV_USERID+"="+URLEncoder.encode(userId)+
						"&" + KohlsConstant.YFSENV_PWD+"="+URLEncoder.encode(password);				
			}
			
			log.debug("*#strurl*# : "+strurl);
			log.debug("*#proxyHost*# : "+proxyHost);
			log.debug("*#content*# : "+content);

			Document clOut  = transmit(strurl,proxyHost,content);

			if(clOut==null){
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("**Output Empty Document**");
				}
			}
			else{

				NodeList nodeExists= clOut.getElementsByTagName(KohlsXMLLiterals.API_SUCCESS);
				if(nodeExists.getLength()>0){
					if(YFCLogUtil.isDebugEnabled()){
						log.debug("In If Conditon:"+ interopApiName +":Output XMLDocument contains base elment ApiSuccess, return document might be null"+ XMLUtil.getXMLString(clOut));
					}
				}
				else{
					if(YFCLogUtil.isDebugEnabled()){
						log.debug("In Else Condition:"+ interopApiName +":Output XMLDocument:"+ XMLUtil.getXMLString(clOut));
					}
				}
			}
			return clOut;

		}catch(Exception e){
			e.printStackTrace();
			throw new YFSException(e.getMessage());
		}
	}

	public static Document invokeAPI(Properties propertiesMap) {

		String flow = propertiesMap.getProperty(KohlsXMLLiterals. IS_FLOW);
		Document outdoc=createHTTPquery(propertiesMap,null, flow);
		return outdoc;
	}

}
