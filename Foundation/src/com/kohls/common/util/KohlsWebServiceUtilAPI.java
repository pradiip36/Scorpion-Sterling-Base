package com.kohls.common.util;

import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsInvokeSOAPWebService;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsWebServiceUtilAPI {

	private static final YFCLogCategory logger = YFCLogCategory.instance(KohlsWebServiceUtilAPI.class);

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

	public Document callWebService(YFSEnvironment env, Document inDoc) throws Exception {

		Element rootEle = inDoc.getDocumentElement();
		Element eleShip  = XMLUtil.getChildElement(rootEle, "psws:shipment");

		
		// Start -- Added for 21043,379,000 -- OASIS_SUPPORT 2/14/2013 , Modified on 4/16/2014//
		String proShipCallType = oProperties.getProperty("proShipCallType");
		if("Rate".equalsIgnoreCase(proShipCallType)){
			Element elePhone = XMLUtil.getChildElement(eleShip, "pros:ConsigneePhone");		
			String strPhone = elePhone.getTextContent();
			
				if(!XMLUtil.isVoid(strPhone)){
					
					if(strPhone.startsWith("1")){
					
						strPhone=KOHLSStringUtil.padRight(strPhone, 11);

						elePhone.setTextContent(strPhone);		
					}
				}
		}
		
		// End -- Added for 21043,379,000 -- OASIS_SUPPORT 2/14/2013 , Modified on 4/16/2014 //
		// Getting end point URL
		String endPointURL = oProperties.getProperty("endPointURL");
		if (!YFCObject.isVoid(endPointURL))
			endPointURL.trim();
		else 
			throw new Exception("API Argument endPointURL Not Defined!");

		String yfsEndPointURL = YFSSystem.getProperty(endPointURL);
		if (!YFCObject.isVoid(yfsEndPointURL))
			yfsEndPointURL.trim();
		else
			yfsEndPointURL=endPointURL.trim();

		//throw new Exception("Property Entry Not Found in customer_overrides.properties["+endPointURL+"]");

		// Getting end point user
		String yfsEndPointUser = "";
		String endPointUser = oProperties.getProperty("endPointUser");
		if (!YFCObject.isVoid(endPointUser)) {
			endPointUser.trim();
			yfsEndPointUser = YFSSystem.getProperty(endPointUser);
			if (!YFCObject.isVoid(yfsEndPointUser))
				yfsEndPointUser.trim();
		}

		// Getting end point password
		String yfsEndPointPassword = "";
		String endPointPassword = oProperties.getProperty("endPointPassword");
		if (!YFCObject.isVoid(endPointPassword)) {
			endPointPassword.trim();
			yfsEndPointPassword = YFSSystem.getProperty(endPointPassword);
			if (!YFCObject.isVoid(yfsEndPointPassword))
				yfsEndPointPassword.trim();
		}

		// Getting soap action URI
		String soapActionURI = oProperties.getProperty("soapActionURI");
		if (!YFCObject.isVoid(soapActionURI))
			soapActionURI.trim();
		else
			throw new Exception("API Argument soapActionURI Not Defined!");

		String yfsSoapActionURI = YFSSystem.getProperty(soapActionURI);
		if (!YFCObject.isVoid(yfsSoapActionURI))
			yfsSoapActionURI.trim();
		else 
			throw new Exception("Property Entry Not Found in customer_overrides.properties["+soapActionURI+"]");

		// Getting soap action URL
		String yfsSoapActionURL = "";
		String soapActionURL = oProperties.getProperty("soapActionURL");
		if (!YFCObject.isVoid(soapActionURL)) {
			soapActionURL.trim();
			yfsSoapActionURL = YFSSystem.getProperty(soapActionURL);
			if (!YFCObject.isVoid(yfsSoapActionURL))
				yfsSoapActionURL.trim();
		}

		// Getting soap action operation
		String soapActionOperation = oProperties.getProperty("soapActionOperation");
		if (!YFCObject.isVoid(soapActionOperation))
			soapActionOperation.trim();
		else
			throw new Exception("API Argument soapActionOperation Not Defined!");

		String _SoapActionOperation = YFSSystem.getProperty(soapActionOperation);
		if (!YFCObject.isVoid(_SoapActionOperation))
			_SoapActionOperation.trim();
		else 
			throw new Exception("Property Entry Not Found in customer_overrides.properties["+soapActionOperation+"]");

		String yfsSoapActionOperation = yfsSoapActionURL + _SoapActionOperation;


		// Getting soap environment
		String yfsSoapActionURIPrefix = "";
		String soapActionURIPrefix = oProperties.getProperty("soapActionURIPrefix");
		if (!YFCObject.isVoid(soapActionURIPrefix)) {
			soapActionURIPrefix.trim();
			yfsSoapActionURIPrefix = YFSSystem.getProperty(soapActionURIPrefix);
			if (!YFCObject.isVoid(yfsSoapActionURIPrefix))
				yfsSoapActionURIPrefix.trim();
		}

		// ProShip Data Contract URI
		String yfsproShipDataContractURI = "";
		String proShipDataContractURI = oProperties.getProperty("proShipDataContractURI");
		if (!YFCObject.isVoid(proShipDataContractURI)) {
			proShipDataContractURI.trim();
			yfsproShipDataContractURI = YFSSystem.getProperty(proShipDataContractURI);
			if (!YFCObject.isVoid(yfsproShipDataContractURI))
				yfsproShipDataContractURI.trim();
		}

		// ProShip Data Contract Prefix
		String yfsproShipDataContractPrefix = "";
		String proShipDataContractPrefix = oProperties.getProperty("proShipDataContractPrefix");
		if (!YFCObject.isVoid(proShipDataContractPrefix)) {
			proShipDataContractPrefix.trim();
			yfsproShipDataContractPrefix = YFSSystem.getProperty(proShipDataContractPrefix);
			if (!YFCObject.isVoid(yfsproShipDataContractPrefix))
				yfsproShipDataContractPrefix.trim();
		}

		String yfskohlsProxyHost="";
		String yfskohlsProxyPort="";
		String yfskohlsProxyUser="";
		String yfskohlsProxyPassword="";
		String kohlsProxyHost=oProperties.getProperty("kohlsProxyHost");
		String kohlsProxyPort=oProperties.getProperty("kohlsProxyPort");
		String kohlsProxyUser=oProperties.getProperty("kohlsProxyUser");
		String kohlsProxyPassword=oProperties.getProperty("kohlsProxyPassword");
		if(!YFCObject.isVoid(kohlsProxyHost) && !YFCObject.isVoid(kohlsProxyPort) && !YFCObject.isVoid(kohlsProxyUser) && !YFCObject.isVoid(kohlsProxyPassword)){
			kohlsProxyHost.trim();
			kohlsProxyPort.trim();
			kohlsProxyUser.trim();
			kohlsProxyPassword.trim();
			yfskohlsProxyHost=YFSSystem.getProperty(kohlsProxyHost);
			yfskohlsProxyPort=YFSSystem.getProperty(kohlsProxyPort);
			yfskohlsProxyUser=YFSSystem.getProperty(kohlsProxyUser);
			yfskohlsProxyPassword=YFSSystem.getProperty(kohlsProxyPassword);
			if (YFCObject.isVoid(yfskohlsProxyHost)){
				yfskohlsProxyHost=kohlsProxyHost;
			}
			if(YFCObject.isVoid(yfskohlsProxyPort)){
				yfskohlsProxyPort=kohlsProxyPort;
			}
			if(YFCObject.isVoid(yfskohlsProxyUser)){
				yfskohlsProxyUser=kohlsProxyUser;
			}
			if(YFCObject.isVoid(yfskohlsProxyPassword)){
				yfskohlsProxyPassword=kohlsProxyPassword;
			}
			System.out.println("http.proxyHost:" + yfskohlsProxyHost);
			System.out.println("http.proxyPort:" + yfskohlsProxyPort);

		}
		System.out.println("logger.isVerboseEnabled()="+logger.isVerboseEnabled());
		if(logger.isVerboseEnabled()) {
			logger.verbose("endPointURL:: " + yfsEndPointURL);
			logger.verbose("endPointUser:: " + yfsEndPointUser);
			logger.verbose("endPointPassword:: " + yfsEndPointPassword);
			logger.verbose("soapActionURI:: " + yfsSoapActionURI);
			logger.verbose("soapActionURL:: " + yfsSoapActionURL);
			logger.verbose("soapActionOperation:: " + yfsSoapActionOperation);
			logger.verbose("soapActionURIPrefix:: " + yfsSoapActionURIPrefix);
			logger.verbose("proShipDataContractURI:: " + yfsproShipDataContractURI);
			logger.verbose("proShipDataContractPrefix:: " + yfsproShipDataContractPrefix);
			logger.verbose("kohlsProxyHost:: " + yfskohlsProxyHost);
			logger.verbose("kohlsProxyPort:: " + yfskohlsProxyPort);
			logger.verbose("yfskohlsProxyUser:: " + yfskohlsProxyUser);
			//logger.verbose("yfskohlsProxyUser:: " + yfskohlsProxyUser);
		}

		//System.out.println("endPointURL:: " + yfsEndPointURL);
		//System.out.println("endPointUser:: " + yfsEndPointUser);
		//System.out.println("endPointPassword:: " + yfsEndPointPassword);
		//System.out.println("soapActionURI:: " + yfsSoapActionURI);
		//System.out.println("soapActionURL:: " + yfsSoapActionURL);
		//System.out.println("soapActionOperation:: " + yfsSoapActionOperation);
		//System.out.println("soapActionURIPrefix:: " + yfsSoapActionURIPrefix);
		//System.out.println("proShipDataContractURI:: " + yfsproShipDataContractURI);
		//System.out.println("proShipDataContractPrefix:: " + yfsproShipDataContractPrefix);

		Document returnDoc = this.invokeSoapWebserviceUtil(
				env, inDoc, yfsEndPointURL, yfsEndPointUser, yfsEndPointPassword, yfsSoapActionURI, 
				yfsSoapActionURIPrefix, yfsSoapActionOperation, yfsproShipDataContractURI, yfsproShipDataContractPrefix,yfskohlsProxyHost,yfskohlsProxyPort,yfskohlsProxyUser,yfskohlsProxyPassword);
		return returnDoc;
	}

	protected Document invokeSoapWebserviceUtil(YFSEnvironment env, Document inDoc, 
			String yfsEndPointURL, String yfsEndPointUser, String yfsEndPointPassword, String yfsSoapActionURI, 
			String yfsSoapActionURIPrefix, String yfsSoapActionOperation, String yfsproShipDataContractURI, String yfsproShipDataContractPrefix,String yfskohlsProxyHost,String yfskohlsProxyPort,String yfskohlsProxyUser,String yfskohlsProxyPassword) {
		Document respDoc = null;
		try {
			respDoc = KohlsInvokeSOAPWebService.invokeSoapWebservice(
					env, inDoc, yfsEndPointURL, yfsEndPointUser, yfsEndPointPassword, yfsSoapActionURI, 
					yfsSoapActionURIPrefix, yfsSoapActionOperation, yfsproShipDataContractURI, yfsproShipDataContractPrefix,yfskohlsProxyHost,yfskohlsProxyPort,yfskohlsProxyUser,yfskohlsProxyPassword);

			logger.debug(YFCDocument.getDocumentFor(respDoc).getString());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new YFSException("Exception in webservice or peer system not avaliable" + ex.getMessage());

		}
		return respDoc;
	}



	public Document callMultiNameSpaceWebService(YFSEnvironment env, Document inDoc) throws Exception {


		// Getting end point URL
		String endPointURL = oProperties.getProperty("endPointURL");
		// if end point URL is null, see if we have it in environment properties, or if we have overridden
		// from the txn object
		if (YFCObject.isVoid(endPointURL) || !YFCObject.isVoid(env.getTxnObject(KohlsConstant.V_ENDPOINTURL))) {
			endPointURL = (String) env.getTxnObject(KohlsConstant.V_ENDPOINTURL);
		}
		if (!YFCObject.isVoid(endPointURL))
			endPointURL.trim();
		else 
			throw new Exception("API Argument endPointURL Not Defined!");
		String yfsEndPointURL = YFSSystem.getProperty(endPointURL);

		// if end point URL is null, see if we have it in environment properties
		if (YFCObject.isVoid(yfsEndPointURL)) {
			yfsEndPointURL = (String) env.getTxnObject(KohlsConstant.V_ENDPOINTURL);
		}
		if (!YFCObject.isVoid(yfsEndPointURL))
			yfsEndPointURL.trim();
		else
			yfsEndPointURL=endPointURL.trim();

		//throw new Exception("Property Entry Not Found in customer_overrides.properties["+endPointURL+"]");

		// Getting end point user
		String yfsEndPointUser = "";
		String endPointUser = oProperties.getProperty("endPointUser");
		if (!YFCObject.isVoid(endPointUser)) {
			endPointUser.trim();
			yfsEndPointUser = YFSSystem.getProperty(endPointUser);
			if (!YFCObject.isVoid(yfsEndPointUser))
				yfsEndPointUser.trim();
		}

		// Getting end point password
		String yfsEndPointPassword = "";
		String endPointPassword = oProperties.getProperty("endPointPassword");
		if (!YFCObject.isVoid(endPointPassword)) {
			endPointPassword.trim();
			yfsEndPointPassword = YFSSystem.getProperty(endPointPassword);
			if (!YFCObject.isVoid(yfsEndPointPassword))
				yfsEndPointPassword.trim();
		}

		// Getting soap action URI
		String soapActionURI = oProperties.getProperty("soapActionURI");
		String yfsSoapActionURI = "";
		if (!YFCObject.isVoid(soapActionURI)) {
			soapActionURI = soapActionURI.trim();
			yfsSoapActionURI = YFSSystem.getProperty(soapActionURI);
			if (!YFCObject.isVoid(yfsSoapActionURI))
				yfsSoapActionURI = yfsSoapActionURI.trim();
		}

		// Getting soap action URL
		String yfsSoapActionURL = "";
		String soapActionURL = oProperties.getProperty("soapActionURL");
		if (!YFCObject.isVoid(soapActionURL)) {
			soapActionURL.trim();
			yfsSoapActionURL = YFSSystem.getProperty(soapActionURL);
			if (!YFCObject.isVoid(yfsSoapActionURL))
				yfsSoapActionURL.trim();
		}

		// Getting soap action operation
		String soapActionOperation = oProperties.getProperty("soapActionOperation");
		String _SoapActionOperation = "";
		if (!YFCObject.isVoid(soapActionOperation)) {
			soapActionOperation = soapActionOperation.trim();
			_SoapActionOperation = YFSSystem.getProperty(soapActionOperation);
			if (!YFCObject.isVoid(_SoapActionOperation))
				_SoapActionOperation = _SoapActionOperation.trim();
		}

		String yfsSoapActionOperation = "";
		// if soap action operation is null, see if we have it in environment properties
		if ((YFCObject.isVoid(yfsSoapActionURL) && YFCObject.isVoid(_SoapActionOperation))
				|| !YFCObject.isVoid(env.getTxnObject(KohlsConstant.V_SOAPACTION))) {
			yfsSoapActionOperation = (String) env.getTxnObject(KohlsConstant.V_SOAPACTION);
		} else {
			yfsSoapActionOperation = yfsSoapActionURL + _SoapActionOperation;
		}

		// Getting soap environment
		String yfsSoapActionURIPrefix = "";
		String soapActionURIPrefix = oProperties.getProperty("soapActionURIPrefix");
		if (!YFCObject.isVoid(soapActionURIPrefix)) {
			soapActionURIPrefix.trim();
			yfsSoapActionURIPrefix = YFSSystem.getProperty(soapActionURIPrefix);
			if (!YFCObject.isVoid(yfsSoapActionURIPrefix))
				yfsSoapActionURIPrefix.trim();
		}

		// ProShip Data Contract URI
		String yfsproShipDataContractURI = "";
		String proShipDataContractURI = oProperties.getProperty("proShipDataContractURI");
		if (!YFCObject.isVoid(proShipDataContractURI)) {
			proShipDataContractURI.trim();
			yfsproShipDataContractURI = YFSSystem.getProperty(proShipDataContractURI);
			if (!YFCObject.isVoid(yfsproShipDataContractURI))
				yfsproShipDataContractURI.trim();
		}

		// ProShip Data Contract Prefix
		String yfsproShipDataContractPrefix = "";
		String proShipDataContractPrefix = oProperties.getProperty("proShipDataContractPrefix");
		if (!YFCObject.isVoid(proShipDataContractPrefix)) {
			proShipDataContractPrefix.trim();
			yfsproShipDataContractPrefix = YFSSystem.getProperty(proShipDataContractPrefix);
			if (!YFCObject.isVoid(yfsproShipDataContractPrefix))
				yfsproShipDataContractPrefix.trim();
		}

		String yfskohlsProxyHost="";
		String yfskohlsProxyPort="";
		String yfskohlsProxyUser="";
		String yfskohlsProxyPassword="";
		String kohlsProxyHost=oProperties.getProperty("kohlsProxyHost");
		String kohlsProxyPort=oProperties.getProperty("kohlsProxyPort");
		String kohlsProxyUser=oProperties.getProperty("kohlsProxyUser");
		String kohlsProxyPassword=oProperties.getProperty("kohlsProxyPassword");
		//if(!YFCObject.isVoid(kohlsProxyHost) && !YFCObject.isVoid(kohlsProxyPort) && !YFCObject.isVoid(kohlsProxyUser) && !YFCObject.isVoid(kohlsProxyPassword)){
		if(!YFCObject.isVoid(kohlsProxyHost) && !YFCObject.isVoid(kohlsProxyPort)){
			kohlsProxyHost.trim();
			kohlsProxyPort.trim();
			//kohlsProxyUser.trim();
			//kohlsProxyPassword.trim();
			yfskohlsProxyHost=YFSSystem.getProperty(kohlsProxyHost);
			yfskohlsProxyPort=YFSSystem.getProperty(kohlsProxyPort);
			//yfskohlsProxyUser=YFSSystem.getProperty(kohlsProxyUser);
			//yfskohlsProxyPassword=YFSSystem.getProperty(kohlsProxyPassword);
			if (YFCObject.isVoid(yfskohlsProxyHost)){
				yfskohlsProxyHost=kohlsProxyHost;
			}
			if(YFCObject.isVoid(yfskohlsProxyPort)){
				yfskohlsProxyPort=kohlsProxyPort;
			}
			/*if(YFCObject.isVoid(yfskohlsProxyUser)){
				yfskohlsProxyUser=kohlsProxyUser;
			}
			if(YFCObject.isVoid(yfskohlsProxyPassword)){
				yfskohlsProxyPassword=kohlsProxyPassword;
			}*/
			System.out.println("http.proxyHost:" + yfskohlsProxyHost);
			System.out.println("http.proxyPort:" + yfskohlsProxyPort);
		}
		System.out.println("logger.isVerboseEnabled()="+logger.isVerboseEnabled());
		if(logger.isVerboseEnabled()) {
			logger.verbose("endPointURL:: " + yfsEndPointURL);
			logger.verbose("endPointUser:: " + yfsEndPointUser);
			logger.verbose("endPointPassword:: " + yfsEndPointPassword);
			logger.verbose("soapActionURI:: " + yfsSoapActionURI);
			logger.verbose("soapActionURL:: " + yfsSoapActionURL);
			logger.verbose("soapActionOperation:: " + yfsSoapActionOperation);
			logger.verbose("soapActionURIPrefix:: " + yfsSoapActionURIPrefix);
			logger.verbose("proShipDataContractURI:: " + yfsproShipDataContractURI);
			logger.verbose("proShipDataContractPrefix:: " + yfsproShipDataContractPrefix);
			logger.verbose("kohlsProxyHost:: " + yfskohlsProxyHost);
			logger.verbose("kohlsProxyPort:: " + yfskohlsProxyPort);
			logger.verbose("yfskohlsProxyUser:: " + yfskohlsProxyUser);
			//logger.verbose("yfskohlsProxyUser:: " + yfskohlsProxyUser);
		}

		//System.out.println("endPointURL:: " + yfsEndPointURL);
		//System.out.println("endPointUser:: " + yfsEndPointUser);
		//System.out.println("endPointPassword:: " + yfsEndPointPassword);
		//System.out.println("soapActionURI:: " + yfsSoapActionURI);
		//System.out.println("soapActionURL:: " + yfsSoapActionURL);
		//System.out.println("soapActionOperation:: " + yfsSoapActionOperation);
		//System.out.println("soapActionURIPrefix:: " + yfsSoapActionURIPrefix);
		//System.out.println("proShipDataContractURI:: " + yfsproShipDataContractURI);
		//System.out.println("proShipDataContractPrefix:: " + yfsproShipDataContractPrefix);

		// populate the namespace URI information
		ArrayList<String> nameSpacePrefixes = new ArrayList<String>();
		ArrayList<String> nameSpaceURIs = new ArrayList<String>();

		String nameSpacePrefixesValues = "";
		String nameSpacePrefixesProperty = oProperties.getProperty("nameSpacePrefixProperty");
		if (!YFCObject.isVoid(nameSpacePrefixesProperty)) {
			nameSpacePrefixesValues = YFSSystem.getProperty(nameSpacePrefixesProperty);
			if (!YFCObject.isVoid(nameSpacePrefixesValues)) {
				StringTokenizer tokenizer = new StringTokenizer(nameSpacePrefixesValues,",");
				while (tokenizer.hasMoreElements()) {
					nameSpacePrefixes.add(tokenizer.nextToken());
				}
			}
		}

		String nameSpaceURIValues = "";
		String nameSpaceURIProperty = oProperties.getProperty("nameSpaceURIProperty");
		if (!YFCObject.isVoid(nameSpaceURIProperty)) {
			nameSpaceURIValues = YFSSystem.getProperty(nameSpaceURIProperty);
			if (!YFCObject.isVoid(nameSpaceURIValues)) {
				StringTokenizer tokenizer = new StringTokenizer(nameSpaceURIValues,",");
				while (tokenizer.hasMoreElements()) {
					nameSpaceURIs.add(tokenizer.nextToken());
				}
			}
		}

		Document returnDoc = this.invokeMultiNameSpaceSoapWebserviceUtil(
				env, inDoc, yfsEndPointURL, yfsEndPointUser, yfsEndPointPassword, yfsSoapActionOperation, 
				yfskohlsProxyHost,yfskohlsProxyPort,yfskohlsProxyUser,yfskohlsProxyPassword, nameSpacePrefixes, nameSpaceURIs);
		return returnDoc;
	}

	protected Document invokeMultiNameSpaceSoapWebserviceUtil(YFSEnvironment env, Document inDoc, 
			String yfsEndPointURL, String yfsEndPointUser, String yfsEndPointPassword, String yfsSoapActionOperation, String yfskohlsProxyHost,
			String yfskohlsProxyPort,String yfskohlsProxyUser,String yfskohlsProxyPassword, ArrayList<String> nameSpacePrefixes, ArrayList<String> nameSpaceURIs) {
		Document respDoc = null;
		try {
			respDoc = KohlsInvokeSOAPWebService.invokeMultiNameSpaceSoapWebservice(
					env, inDoc, yfsEndPointURL, yfsEndPointUser, yfsEndPointPassword, yfsSoapActionOperation, yfskohlsProxyHost,yfskohlsProxyPort,yfskohlsProxyUser,yfskohlsProxyPassword, nameSpacePrefixes, nameSpaceURIs );

			logger.debug(YFCDocument.getDocumentFor(respDoc).getString());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new YFSException("Exception in webservice or peer system not avaliable" + ex.getMessage());

		}
		return respDoc;
	}
}