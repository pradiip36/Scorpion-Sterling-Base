package com.kohls.common.util;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.custom.util.xml.XMLUtil;
import com.ibm.sterling.afc.xapiclient.japi.XApi;
import com.ibm.sterling.afc.xapiclient.japi.XApiClientCreationException;
import com.ibm.sterling.afc.xapiclient.japi.XApiClientFactory;
import com.ibm.sterling.afc.xapiclient.japi.XApiEnvironment;
import com.ibm.sterling.afc.xapiclient.japi.XApiException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class is wrapper class for making HTTP calls.
 * @Author OASIS 02/04/2014 PMR 15178,999,000
 */

public class KohlsHTTPWrapperAPI {

	
    
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KohlsHTTPWrapperAPI.class.getName());
	private  XApi api;
	private Properties props;
	public void setProperties(Properties sysProp) throws Exception {
		if (sysProp != null) {
			this.props = sysProp;
		}
	}
    
    private DocumentBuilder docBuilder;


    private XApiEnvironment env ;
   
    private String sessionId;

   
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Document invoke (Document inXML) throws YFSException  {
	  
    	
	    String strURL=props.getProperty(KohlsConstant.URL);
	    String strUserId=props.getProperty(KohlsConstant.UserId);
	    String strPassword=props.getProperty(KohlsConstant.Password);
	    String strApiName=props.getProperty(KohlsConstant.ApiName);
	    String strIsAPI=props.getProperty(KohlsConstant.IsAPI);
	    
	    
	    if (!YFCObject.isVoid(strURL))
	    	strURL.trim();
		else
			throw new YFSException("API Argument URL Not Defined!");
	    
	    if (!YFCObject.isVoid(strUserId))
	    	strUserId.trim();
		else
			throw new YFSException("API Argument UserId Not Defined!");
	    
	    
	    if (!YFCObject.isVoid(strPassword))
	    	strPassword.trim();
		else
			throw new YFSException("API Argument Password Not Defined!");
	    
	    if (!YFCObject.isVoid(strApiName))
	    	strApiName.trim();
		else
			throw new YFSException("API Argument Api Name Not Defined!");
	    
	    if (!YFCObject.isVoid(strIsAPI))
	    	strIsAPI.trim();
		else
			{
			strIsAPI="N";
			
	    	log.debug("IsAPI Argumnet not defined, setting default value as N");
			}
	    if(YFCLogUtil.isDebugEnabled()){
			log.debug("######### Input XML   ###############" + XMLUtil.getXMLString(inXML));
			log.debug("######### URL   ###############" + strURL);
			log.debug("######### User Id   ###############" + strUserId);
			log.debug("######### Password   ###############" + strPassword);
			log.debug("######### API NAME   ###############" + strApiName);
			log.debug("######### IS API   ###############" + strIsAPI);
		}
	    
	    
	    Document getOutputXML=null;
	    Map prop = new HashMap();
	    prop.put("yif.httpapi.url", strURL);
			
		try {
			api = XApiClientFactory.getInstance().getApi("HTTP", prop);
		} catch (XApiClientCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		try {
			docBuilder = fac.newDocumentBuilder();
		} catch (ParserConfigurationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}			
		Document environmentDoc = docBuilder.newDocument();
		Element envElement = environmentDoc.createElement("YFSEnvironment");
		envElement.setAttribute("userId", strUserId);
		envElement.setAttribute("progId", strPassword);
		environmentDoc.appendChild(envElement);
		
		try {
			env = api.createEnvironment(environmentDoc);
		} catch (XApiException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		Document loginInput = docBuilder.newDocument();
		Element loginElement = loginInput.createElement("Login");
		loginElement.setAttribute("LoginID", strUserId);
		loginElement.setAttribute("Password", strPassword);
		loginInput.appendChild(loginElement);
		try {
		//Using api.invoke to call login api
		Document loginDoc;
		loginDoc = api.invoke((XApiEnvironment) env, "login", loginInput);
		env.setTokenID(loginDoc.getDocumentElement().getAttribute("UserToken"));
		//Start-OASIS-Problem 763- Modified below line to get value of attribute UserToken after confirmation from support team in PMR - 43032379000
		sessionId = loginDoc.getDocumentElement().getAttribute("UserToken");
		//OASIS - End
		log.debug(":Session Id is:"+sessionId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//logic to check if the invoke API or Service
			if(strIsAPI.equalsIgnoreCase("Y")){
				getOutputXML = api.invoke((XApiEnvironment) env, strApiName, inXML);
			}else{
				getOutputXML = api.executeFlow((XApiEnvironment) env, strApiName, inXML);
			}
			
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//tear down of session
		Document logoutDoc = docBuilder.newDocument();
    	Element logoutElement = logoutDoc.createElement("registerLogout");
    	logoutElement.setAttribute("UserId", env.getUserId());
    	logoutElement.setAttribute("SessionId", sessionId);
    	logoutDoc.appendChild(logoutElement);

    	//Using api.invoke to call registerLogout api
    	try {
    		//Using api.invoke to call registerLogout api
    		log.debug(":Input xml going to registerLogout is:"+XMLUtil.getElementXMLString(logoutDoc.getDocumentElement()));
	    	api.invoke((XApiEnvironment) env, "registerLogout", logoutDoc);
	    	api.releaseEnvironment((XApiEnvironment) env);
    	} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return getOutputXML;
    }

   
  
    
   

	
}
