package com.kohls.bopus.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsComplexQueryExpression;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.common.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsBOPUSGetLocationDesc {


	private static final YFCLogCategory log = YFCLogCategory
	.instance(KohlsOrderPickProcessUtil.class.getName());

	public Document parseUpdateSRLocDesToShipmentHeader(YFSEnvironment env, Document docMLSStockRmLocs, String strShipmentNo, String shipmentKey ) throws Exception{
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("input to parseUpdateSRLocDesToShipmentHeader:" + SCXmlUtil.getString(docMLSStockRmLocs));
		}
		
		Element eleMLSStockRmLocs = docMLSStockRmLocs.getDocumentElement();
		NodeList nodeListMLSStockRmLoc = eleMLSStockRmLocs.getElementsByTagName(KohlsXMLLiterals.E_STOCK_ROOM_LOCATION);
		String strLocID ="";
		String strLocDesc="";
		String strConcatLocId = "";
		String strConcatLocDesc = "";

		for (int i=0; i< nodeListMLSStockRmLoc.getLength(); i++) {

			Element eleMLSStockRmLoc = (Element) nodeListMLSStockRmLoc.item(i);
			strLocID = eleMLSStockRmLoc.getElementsByTagName(KohlsXMLLiterals.E_BAR_CODE_NUM).item(0).getTextContent();
			strLocDesc = eleMLSStockRmLoc.getElementsByTagName(KohlsXMLLiterals.E_FULL_DESC_CODE).item(0).getTextContent();
			strConcatLocId += strLocID;
			strConcatLocDesc += strLocDesc;
		}

//		System.out.println(strConcatLocId);
//		System.out.println(strConcatLocDesc);

		return updtShipHeaderSRLocDes(env, strConcatLocId, strConcatLocDesc, strShipmentNo, shipmentKey);
	}

	public Document updtShipHeaderSRLocDes(YFSEnvironment env, String strConcatLocId,
			String strConcatLocDesc, String strShipmentNo, String shipmentKey) throws Exception {
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("input to updtShipHeaderSRLocDes:" + strConcatLocId + "," + strConcatLocDesc);
		}
		
		String foundLocation = KohlsConstant.YES;
		if ("".equalsIgnoreCase(strConcatLocDesc) && "".equalsIgnoreCase(strConcatLocId)) {
			foundLocation = KohlsConstant.NO;
		}

		Document docInShipment = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element eleInShipment = docInShipment.getDocumentElement();
		eleInShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, shipmentKey);
		eleInShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipmentNo);

		String strTemplate = "<Shipment ShipmentKey=\"\" EnterpriseCode=\"\" ShipmentNo=\"\" ><Extn ExtnHoldLocationID=\"\" ExtnHoldLocationDesc=\"\" /></Shipment>";
		Document docTemplate =   XMLUtil.getDocument(strTemplate);

		Document docShipmentListOutput =
			KohlsCommonUtil.invokeAPI( env, docTemplate, KohlsConstant.API_actual_GET_SHIPMENT_LIST, docInShipment);

		Element eleShipments = docShipmentListOutput.getDocumentElement();
		Element eleShipment = SCXmlUtil.getChildElement(eleShipments, KohlsXMLLiterals.E_SHIPMENT);
		String strShipmentKey = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
		Element eleShipmentExtn =  SCXmlUtil.getChildElement(eleShipment, KohlsXMLLiterals.E_EXTN);
		String strExtnHoldLocationID = eleShipmentExtn.getAttribute(KohlsXMLLiterals.A_EXTN_HOLD_LOCATION_ID);
		String strExtnHoldLocationDesc= eleShipmentExtn.getAttribute(KohlsXMLLiterals.A_EXTN_HOLD_LOCATION_DESC);
		// don't update if we already have the barcode / location id
		StringTokenizer tokenizer = new StringTokenizer (strExtnHoldLocationID, ",");
		boolean existing = false;
		while (tokenizer.hasMoreElements()) {
			String currentLocation = tokenizer.nextToken();
			if (strConcatLocId.indexOf(currentLocation) >= 0) {
				existing = true;
				break;
			}
		}
		Document docChangeShipmentIn = null;
		if (!existing) {
			// need to update
			if (!YFCObject.isVoid(strExtnHoldLocationID)) {
				if (YFCObject.isVoid(strConcatLocId)) {
					strConcatLocId += strExtnHoldLocationID;
				} else {
					strConcatLocId += "," + strExtnHoldLocationID;
				}
			}
			if (!YFCObject.isVoid(strExtnHoldLocationDesc)) {
				if (YFCObject.isVoid(strConcatLocDesc)) {
					strConcatLocDesc += strExtnHoldLocationDesc;
				} else {
					strConcatLocDesc += "," + strExtnHoldLocationDesc;
				}
			}
	
			docChangeShipmentIn = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			Element eleChangeShipmentIn = docChangeShipmentIn.getDocumentElement();
			if (!YFCObject.isVoid(strShipmentNo)) {
				eleChangeShipmentIn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipmentNo);
			}
			eleChangeShipmentIn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, strShipmentKey);
			eleChangeShipmentIn.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
			eleChangeShipmentIn.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
			eleChangeShipmentIn.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleShipment.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
	
	
			Element eleChangeShipmentExtn = SCXmlUtil.createChild(eleChangeShipmentIn, KohlsXMLLiterals.E_EXTN);
			eleChangeShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_HOLD_LOCATION_ID, strConcatLocId);
			eleChangeShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_HOLD_LOCATION_DESC, strConcatLocDesc);
	
	//		System.out.println(SCXmlUtil.getString(docChangeShipmentIn));
			
			KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_CHANGE_SHIPMENT, docChangeShipmentIn);
		} else {
			docChangeShipmentIn = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			Element eleChangeShipmentIn = docChangeShipmentIn.getDocumentElement();
			eleChangeShipmentIn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, strShipmentKey);
			eleChangeShipmentIn.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleShipment.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
			Element eleChangeShipmentExtn = SCXmlUtil.createChild(eleChangeShipmentIn, KohlsXMLLiterals.E_EXTN);
			eleChangeShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_HOLD_LOCATION_ID, strExtnHoldLocationID);
			eleChangeShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_HOLD_LOCATION_DESC, strExtnHoldLocationDesc);
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("output from  updtShipHeaderSRLocDes:" + SCXmlUtil.getString(docChangeShipmentIn));
		}
		
		if (docChangeShipmentIn != null) {
			// didn't find a location, tack on an attribute to indicate that its missing
			docChangeShipmentIn.getDocumentElement().setAttribute(KohlsXMLLiterals.A_FOUND_MLS_LOCATION, foundLocation);
		}
		
		return docChangeShipmentIn;
	}

	public Document callMLSUpdtSRLocDescShipmentHeader(YFSEnvironment env, Document docShipmentStockRmLocs) throws Exception {
		boolean updateHoldLocation = true;
		
		
		// Document docMLSStockRmLocDesc = stubMLSLocDesc(env);
		// get the shipnode to build the dynamic url
		Element inputElement = docShipmentStockRmLocs.getDocumentElement();
		updateHoldLocation = YFCCommon.isVoid(inputElement.getAttribute("HoldLocationFlag")) || YFCCommon.equalsIgnoreCase(inputElement.getAttribute("HoldLocationFlag"), "Y");
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("input to callMLSUpdtSRLocDescShipmentHeader" + SCXmlUtil.getString(docShipmentStockRmLocs));
		}
		String shipNode = inputElement.getAttribute(KohlsXMLLiterals.A_SHIP_NODE);
		// see if we passed a shipnode, if not, look it up from shipment information
		
		if (YFCObject.isVoid(shipNode)) {
			Document shipmentLineListDoc = getShipmentLineList(env, inputElement.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY), "", KohlsConstant.API_GET_SHIPMENT_LINE_LIST_BARCODE_TEMPLATE);
				// loop through and use shipnode the first shipment line
			if(shipmentLineListDoc != null){
				Element eleShipmentLines = shipmentLineListDoc.getDocumentElement();
				Iterator<Element> itrShipmentLine = SCXmlUtil.getChildren(eleShipmentLines);
				Element eleShipmenLine = null;
				while(itrShipmentLine.hasNext()){
					eleShipmenLine = itrShipmentLine.next();
					shipNode = ((Element) eleShipmenLine.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT).item(0)).getAttribute(KohlsXMLLiterals.A_SHIP_NODE);
					break;
				}
			} else {
				// default to store 9911
				shipNode = YFSSystem.getProperty(KohlsConstant.MLS_DEFAULT_STORE_LOOKUP);
			}
		}
		
		// fix for Defect 202, check to see if we have a location with this id already
		boolean usedLocation = validateHoldLocation(env, docShipmentStockRmLocs, shipNode);
		
		//set the environment userID and password fields in the input to XSL to call the webservices
		// get the prefix information for the namespaces and login information if present
		String yfsEndPointUser = "";
		String yfsEndPointPassword = "";
		String environmentElement = "";
		Element environmentNode = null;
		yfsEndPointUser = YFSSystem.getProperty(KohlsConstant.END_POINT_USER_MLS);
		yfsEndPointPassword = YFSSystem.getProperty(KohlsConstant.END_POINT_PWD_MLS);
		environmentElement = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_ENV_ELEMENT_PROPERTY);
		if (!YFCObject.isVoid(yfsEndPointUser) && !YFSObject.isVoid(yfsEndPointPassword) && !YFSObject.isVoid(environmentElement)) {
			// we have an user id and password, need to construct the environment element
			environmentNode = docShipmentStockRmLocs.createElement(environmentElement);
			// add the user id and password elements
			Element userId = docShipmentStockRmLocs.createElement(KohlsConstant.A_USER_ID);
			// set the userid value from the properties file
			userId.setTextContent(yfsEndPointUser);
			// add the userId
			environmentNode.appendChild(userId);
			Element password = docShipmentStockRmLocs.createElement(KohlsConstant.A_PASSWORD);
			// set the password value from the properties file
			password.setTextContent(yfsEndPointPassword);
			// add the password
			environmentNode.appendChild(password);
			// add the environment
			inputElement.appendChild(environmentNode);
		}
		
		// set the MLS endpoint based on the shipnode
		StringBuffer endpointURL = new StringBuffer("");
		// grab the end point properties minus the shipnode
		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_PREFIX))) {
			endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_PREFIX));
		} else {
			// set to default
			endpointURL.append("http://isp");
		}
		// see if we have any translation shipnode values
		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + shipNode))) {
			// use the translated value
			endpointURL.append(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + shipNode));
		} else if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_USE_DEFAULT_STORE)) 
				&& "Y".equalsIgnoreCase(YFSSystem.getProperty(KohlsConstant.MLS_USE_DEFAULT_STORE))) {
			// use the default store
			endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_DEFAULT_STORE_LOOKUP));
		} else {
			// no translation
			String formattedShipNode = shipNode;
			while (formattedShipNode.toString().length() < 4) {
				formattedShipNode = "0" + formattedShipNode;
			}
			endpointURL.append(formattedShipNode);
		}
		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_SUFFIX))) {
			endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_SUFFIX));
		} else {
			// set to default
			endpointURL.append("e.st.ad.kohls.com/KohlsInventoryDemandService/InventoryDemandService.svc");
		}
		env.setTxnObject(KohlsConstant.V_ENDPOINTURL, endpointURL.toString());
		
		// set the soap action information
		StringBuffer soapActionOperation = new StringBuffer("");
		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTIONURL_PROPERTY))) {
			soapActionOperation.append(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTIONURL_PROPERTY));
		} else {
			// set to default
			soapActionOperation.append("http://tempuri.org/IInventoryDemandService/");
		}
		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTION_GETLOCATIONDETAILS))) {
			soapActionOperation.append(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTION_GETLOCATIONDETAILS));
		} else {
			// set to default
			soapActionOperation.append("GetLocationDetails");
		}
		env.setTxnObject(KohlsConstant.V_SOAPACTION, soapActionOperation.toString());
	
		//Service that makes MLS WS call with Shipment No and Location ID to obtain Location Description
		Document docMLSStockRmLocDesc=KohlsCommonUtil.invokeService( env, KohlsConstant.SERVICE_WS_MLS_LOC_DESC, docShipmentStockRmLocs);

		if (docMLSStockRmLocDesc == null) {
			throw new YFSException("MLS Location Details Description WS returned null");
		}
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug(SCXmlUtil.getString(docMLSStockRmLocDesc));
		}
		
//		System.out.println(SCXmlUtil.getString(docMLSStockRmLocDesc));
		if(updateHoldLocation) {
			Element eleMLSStockRmLocs = docShipmentStockRmLocs.getDocumentElement();
			String strShipmentNo = eleMLSStockRmLocs.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
			String shipmentKey = eleMLSStockRmLocs.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);

			// don't update or make the MLS call if its an existing location
			if (!usedLocation) {
				return parseUpdateSRLocDesToShipmentHeader(env, docMLSStockRmLocDesc, strShipmentNo, shipmentKey);
			} else {
				// just return shipment stating its an existing location
				Document returnDocument = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();
				returnDocument.getDocumentElement().setAttribute(KohlsXMLLiterals.A_EXISTING_LOCATION, KohlsConstant.YES);
				return returnDocument;
			}
		} else {
			// make sure that the return response is in expected format front-end expects
			Document returnDocument = YFCDocument.createDocument(KohlsXMLLiterals.E_GET_LOCATION_DETAILS_RESPONSE).getDocument();
			Element originalLocationElement = KohlsXMLUtil.getElementByXpath(docMLSStockRmLocDesc, "//" + KohlsXMLLiterals.E_GET_LOCATION_DETAILS_RESULT);
			Element newLocationDetailsElement = returnDocument.createElement(KohlsXMLLiterals.E_GET_LOCATION_DETAILS_RESULT);
			KohlsXMLUtil.copyElement(returnDocument, originalLocationElement, newLocationDetailsElement);
			returnDocument.getDocumentElement().appendChild(newLocationDetailsElement);
			return returnDocument;
		}

	}
	
	/**
	 * 
	 * @param env
	 * @param shipmentKey
	 * @param shipmentLineNo
	 * @param templateName
	 * @return
	 * @throws Exception
	 */
	public Document getShipmentLineList(YFSEnvironment env, String shipmentKey, String shipmentLineNo, String templateName) throws Exception{

		Document inDocGetShpmntLineList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT_LINE);
		Element eleGetShpmntLineList = inDocGetShpmntLineList.getDocumentElement();

		eleGetShpmntLineList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, shipmentKey);
		eleGetShpmntLineList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, shipmentLineNo);

		Document outDocGetShpmntLineList = null;
		outDocGetShpmntLineList = KohlsCommonUtil.invokeAPI(env, templateName, KohlsConstant.GET_SHIPMENT_LINE_LIST_API, inDocGetShpmntLineList);
		return outDocGetShpmntLineList;
	}
	
	private boolean validateHoldLocation(YFSEnvironment env, Document input, String shipNode) {
		boolean existingLocation = false;
		
		Document shipmentListInput = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();
		Document shipmentListOutput = null;
		
		// get the location id
		try {
			Element location = KohlsXMLUtil.getElementByXpath(input, "//"+ KohlsXMLLiterals.E_LOCATION);
			
			// add the shipnode as well
			shipmentListInput.getDocumentElement().setAttribute(KohlsXMLLiterals.A_SHIPNODE, shipNode);
			
			// build the complex query, need to check for EXTN_HOLD_LOCATION_ID
			KohlsComplexQueryExpression kohlsLocationComplexQueryExpression = new KohlsComplexQueryExpression();
			kohlsLocationComplexQueryExpression.setElementName(KohlsXMLLiterals.A_EXTN_HOLD_LOCATION_ID);
			kohlsLocationComplexQueryExpression.setOperator(KohlsXMLLiterals.LIKE);
			kohlsLocationComplexQueryExpression.setValue(location.getAttribute(KohlsXMLLiterals.A_LOCATION_ID));
			
			// also need to filter for status
			KohlsComplexQueryExpression kohlsStatusComplexQueryExpression = new KohlsComplexQueryExpression();
			kohlsStatusComplexQueryExpression.setElementName(KohlsXMLLiterals.A_STATUS);
			kohlsStatusComplexQueryExpression.setOperator(KohlsXMLLiterals.LT);
			// only need to worry about non-picked up status
			kohlsStatusComplexQueryExpression.setValue("1400");
			
			
			// Build the list to pass in
			ArrayList<KohlsComplexQueryExpression> locationIdList = new ArrayList<KohlsComplexQueryExpression>();
			locationIdList.add(kohlsLocationComplexQueryExpression);
			ArrayList<KohlsComplexQueryExpression> statusList = new ArrayList<KohlsComplexQueryExpression>();
			statusList.add(kohlsStatusComplexQueryExpression);
			ArrayList<ArrayList<KohlsComplexQueryExpression>> complexQueryList = new ArrayList<ArrayList<KohlsComplexQueryExpression>>();
			complexQueryList.add(locationIdList);
			complexQueryList.add(statusList);
			
			// call the utility method
			shipmentListInput.getDocumentElement().appendChild(
					KohlsXMLUtil.buildComplexQueryElement(shipmentListInput, KohlsXMLLiterals.AND, KohlsXMLLiterals.E_AND, 
								KohlsXMLLiterals.E_OR, complexQueryList));
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug(SCXmlUtil.getString(shipmentListInput));
				System.out.println(SCXmlUtil.getString(shipmentListInput));
			}
			
			shipmentListOutput = KohlsCommonUtil.invokeAPI(env, KohlsConstant.GET_SHIPMENT_LIST_API, shipmentListInput);
			
			// check the total number of records
			Element outputElement = shipmentListOutput.getDocumentElement();
			String recordCount = outputElement.getAttribute(KohlsXMLLiterals.A_TOTAL_NUMBER_OF_RECORDS);
			if (!YFCObject.isVoid(recordCount) && Integer.parseInt(recordCount) > 0) {
				existingLocation = true;
			}
		} catch (Exception ex) {
			// problem checking for location
			log.trace(ex.getMessage());
		}
		
		return existingLocation;
	}

}
