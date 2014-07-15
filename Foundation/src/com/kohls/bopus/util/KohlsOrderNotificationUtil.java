package com.kohls.bopus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPBody;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.kohls.util.webserviceUtil.WebServiceCaller;
import com.kohls.common.util.KohlsWebServiceUtilAPI;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

import com.kohls.bopus.util.KohlsBOPUSStoreHoursUtil;

public class KohlsOrderNotificationUtil {


	private Properties props;


	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsOrderNotificationUtil.class.getName());

	public static Document getDocumentFromElement(Element element)
			throws ParserConfigurationException, FactoryConfigurationError
			{
		Document doc = null;
		Node nodeImp = null;
		DocumentBuilder dbdr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = dbdr.newDocument();
		nodeImp = doc.importNode(element, true);
		doc.appendChild(nodeImp);
		return doc;
			}

	public Document consolidationForMonitor(YFSEnvironment env,  Document inXML) throws Exception{
		Document inDoc = inXML;		
		Element monitorElement=  inDoc.getDocumentElement();	
		Element eleShipment = SCXmlUtil.getChildElement(monitorElement, KohlsXMLLiterals.E_SHIPMENT);
		Document newShipmentDoc = getDocumentFromElement(eleShipment);
		return shipmentLineConsolidation(env, newShipmentDoc);
	}

	public Document shipmentLineConsolidation(YFSEnvironment env,  Document inXML){

		Document inDoc = inXML;		
		//Document OutDoc = null;
		Document changeShipInDoc = null;
		String custNotificationType = null;
		Document shipmentListOutPut = null;
		Element eleShipment = null;
		NodeList nodeShipmentList =null;
		String ExtnNotificationFlag = KohlsConstant.NO;

		//String API_GET_SHIPMENT_LIST_BOPUS_CONSOL_TEMPLATE = "<Shipment ShipNode=\"\" ShipmentKey=\"\" ShipmentNo=\"\" Status=\"\" ><Extn ExtnCustNotificationSent=\"\" /></Shipment>";

		try {

			log.debug("Input xml to shipmentLineConsolidation : "+XMLUtil.getXMLString(inXML));
			Element eleShipmentElement=  inDoc.getDocumentElement();

			Element eleExtn = SCXmlUtil.getChildElement(eleShipmentElement, KohlsXMLLiterals.E_EXTN);
			/*if(eleExtn != null) 
				custNotificationType = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_CUST_NOTIFICATION_SENT);*/

			Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipmentElement, KohlsXMLLiterals.E_SHIPMENT_LINES);
			Element eleShipmentLine = SCXmlUtil.getChildElement(eleShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);

			Document inDocShipment = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			Element inEleShipment = inDocShipment.getDocumentElement();

			inEleShipment.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, eleShipmentLine.getAttribute( KohlsXMLLiterals.A_ORDER_HEADER_KEY));
			inEleShipment.setAttribute(KohlsXMLLiterals.A_SHIP_NODE, eleShipmentElement.getAttribute(KohlsXMLLiterals.A_SHIP_NODE));

			shipmentListOutPut = 
					KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_GET_SHIPMENT_LIST_BOPUS_CONSOL_TEMPLATE ,KohlsConstant.API_actual_GET_SHIPMENT_LIST, inDocShipment);

			eleShipment = shipmentListOutPut.getDocumentElement();
			nodeShipmentList = eleShipment.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

			for (int i=0; i< nodeShipmentList.getLength(); i++ ){

				Element  nodeShipment = (Element)nodeShipmentList.item(i);

				String status = nodeShipment.getAttribute(KohlsXMLLiterals.A_STATUS);
				if (!(status.equals(KohlsConstant.V_CANCEL_STATUS))) {


					/*if (checkExtnCustNotificationSent(nodeShipment, custNotificationType)) {
						ExtnNotificationFlag = KohlsConstant.YES;
						log.debug("in if:ExtnNotificationFlag:" + ExtnNotificationFlag);
						//eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_NOTIFICATION_FLAG, KohlsConstant.YES);
						break;
					}*/

					Document changeShipDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
					Element eleChangeShip = changeShipDoc.getDocumentElement();

					eleChangeShip.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, nodeShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY));
					Element eleExtnCustNotificationSent = SCXmlUtil.getChildElement(nodeShipment, KohlsXMLLiterals.E_EXTN);

					/*String newExtnCustNotificationSent = custNotificationType + ";" + 
							eleExtnCustNotificationSent.getAttribute(KohlsXMLLiterals.A_EXTN_CUST_NOTIFICATION_SENT);*/

					/*log.debug("--------------" + newExtnCustNotificationSent + "---------" );*/

					Element eleChangeExtnShip = SCXmlUtil.createChild(eleChangeShip, KohlsXMLLiterals.E_EXTN);
					/*eleChangeExtnShip.setAttribute(KohlsXMLLiterals.A_EXTN_CUST_NOTIFICATION_SENT, newExtnCustNotificationSent);*/

					//callChangeShipment(env,changeShipDoc);
				}
			}

			/*if (!ExtnNotificationFlag.equals(KohlsConstant.YES)) {
				//Stamping Condition attribute
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_NOTIFICATION_FLAG, KohlsConstant.NO);
			}*/

		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"The method shipmentLineConsolidation() returned Error."+e);
		}

		return inDoc;
	}

	public static boolean checkExtnCustNotificationSent(Element eleShipment, String notificationType){
		try{
			Element eleExtn = SCXmlUtil.getChildElement(eleShipment, KohlsXMLLiterals.E_EXTN);
			String extnCustNotificationSent = null;
			if(eleExtn == null)
				eleExtn = SCXmlUtil.createChild(eleShipment, KohlsXMLLiterals.E_EXTN);	
			extnCustNotificationSent = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_CUST_NOTIFICATION_SENT);
			List<String> notificationTypesList = new ArrayList<String>();
			if(extnCustNotificationSent != null){			
				if(extnCustNotificationSent.indexOf(";") !=-1){
					String[] aryExtnCustNotificationSent = extnCustNotificationSent.split(";");
					for(int i=0; i< aryExtnCustNotificationSent.length; i++)
						notificationTypesList.add(aryExtnCustNotificationSent[i].trim());

					if(!notificationTypesList.isEmpty()){
						if(notificationTypesList.contains(notificationType))					
							return true;
						else{
							notificationTypesList.add(notificationType);						
							//logic to set the attribute
							for(int i=0; i< notificationTypesList.size(); i++){
								if(i==0)
									extnCustNotificationSent = notificationTypesList.get(i);
								else
									extnCustNotificationSent += ";"+notificationTypesList.get(i);
							}
							return false;
						}
					}
				}
				else if(extnCustNotificationSent.equals(notificationType)){
					return true;
				}
				else{
					return false;
				}

			}
			else{
				return false;
			}
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new YFSException(
					"The method checkExtnCustNotificationSent() returned Error."+e);
		}

	}

	private void callChangeShipment(YFSEnvironment env, Document changeShipDoc) {
		// TODO Auto-generated method stub
		try {
			KohlsCommonUtil.invokeAPI(env, KohlsConstant.API_CHANGE_SHIPMENT, changeShipDoc);
		} catch (Exception e) {
			e.getStackTrace();
			throw new YFSException("Exception in callChangeShipment method is: "+e.getMessage());
		}
	}

	
	/**
	 * This method pulls the necessary details like ship node name, color, size description and form the Order XML which 
	 * will be feed to XSL. 
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws ParserConfigurationException
	 * @throws FactoryConfigurationError
	 */
	public static Document inputBOPUSWebServiceCall(YFSEnvironment env,
			Document inXML) throws ParserConfigurationException,
			FactoryConfigurationError {

		try {
			// String API_GET_ORDER_LIST_BOPUS_CONSOL_TEMPLATE =
			// "<OrderList><Order CustomerEMailID=\"\" OrderNo=\"\" OrderDate=\"\" Status=\"\" > <OrderLines> <OrderLine ReqCancelDate=\"\" OrderedQty=\"\" PackListType=\"\" Status=\"\"> <Shipnode ShipNode=\"\"> <ShipNodePersonInfo AddressLine1=\"\" AddressLine2=\"\" City=\"\" ZipCode=\"\" State=\"\"/> </Shipnode > <Item ItemShortDesc=\"\" ItemID=\"\"> <Extn ExtnColorDesc=\"\" ExtnSizeDesc=\"\"/> </Item> <OrderStatuses> <OrderStatus StatusDescription =\"Customer Picked Up\" StatusDate=\"\"/> </OrderStatuses> </OrderLine> </OrderLines> <PersonInfoBillTo FirstName=\"\" LastName=\"\" AddressLine1=\"\" AddressLine2=\"\" City=\"\" ZipCod=\"\" State=\"\"/> <PaymentMethods> <PaymentMethod CreditCardNo=\"\"/> </PaymentMethods></Order></OrderList>";

			Document shipmentListOutPut = null;
			String strPreEnReceiptId = "";
			Document orderListDoc = null;
			Document outOrderDoc = null;
			
			List<String> orderLineKeyList = new ArrayList<String>();
			Element eleShipmentElement = inXML.getDocumentElement();

			Element eleExtn = SCXmlUtil.getChildElement(eleShipmentElement,
					KohlsXMLLiterals.E_EXTN);
			String custNotificationType = null;
			/*
			 * if(eleExtn != null) custNotificationType =
			 * eleExtn.getAttribute(KohlsXMLLiterals
			 * .A_EXTN_CUST_NOTIFICATION_SENT);
			 */

			Element eleShipmentLines = SCXmlUtil.getChildElement(
					eleShipmentElement, KohlsXMLLiterals.E_SHIPMENT_LINES);
			Element eleShipmentLine = SCXmlUtil.getChildElement(
					eleShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);

			Document inDocShipment = SCXmlUtil
					.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			Element inEleShipment = inDocShipment.getDocumentElement();

			String strShipmentNo = eleShipmentElement
					.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);

			String strStoreHrs = KohlsBOPUSStoreHoursUtil.CalculateStoreHours(strShipmentNo,env);

			inEleShipment.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY,
					eleShipmentLine
							.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));
			inEleShipment.setAttribute(KohlsXMLLiterals.A_SHIP_NODE,
					eleShipmentElement
							.getAttribute(KohlsXMLLiterals.A_SHIP_NODE));

			log.debug("Input xml to shipmentLineConsolidation : "
					+ XMLUtil.getXMLString(inDocShipment));

			 shipmentListOutPut =
			 KohlsCommonUtil.invokeAPI( env,
			 KohlsConstant.API_GET_SHIPMENT_LIST_BOPUS_CONSOL_TEMPLATE
			 ,KohlsConstant.API_actual_GET_SHIPMENT_LIST, inDocShipment);

			
			NodeList shpListShipmentNl = shipmentListOutPut
					.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

			for (int i = 0; i < shpListShipmentNl.getLength(); i++) {
				Element shpListShipmentEle = (Element) shpListShipmentNl
						.item(i);

				Element shpListShipmentLinesEle = SCXmlUtil.getChildElement(
						shpListShipmentEle, KohlsXMLLiterals.E_SHIPMENT_LINES);

				NodeList shpListShipmentLineNl = shpListShipmentLinesEle
						.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);

				for (int j = 0; j < shpListShipmentLineNl.getLength(); j++) {
					Element shpListShipmentLineEle = (Element) shpListShipmentLineNl
							.item(j);
					Element shpListOrderLineEle = SCXmlUtil.getChildElement(
							shpListShipmentLineEle,
							KohlsXMLLiterals.E_ORDER_LINE);
					String OrderLineKey = shpListOrderLineEle
							.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
					
					orderLineKeyList.add(OrderLineKey);
				}
			}
			
			Document inDocOrder = SCXmlUtil
					.createDocument(KohlsXMLLiterals.E_ORDER);
			Element inEleOrder = inDocOrder.getDocumentElement();
			inEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY,
					eleShipmentLine
							.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));

			 orderListDoc = KohlsCommonUtil.invokeAPI( env,
			 KohlsConstant.API_GET_ORDER_LIST_BOPUS_CONSOL_TEMPLATE,
			 KohlsConstant.API_GET_ORDER_LIST, inDocOrder);

			
			Element orderEle = SCXmlUtil
					.getChildElement(orderListDoc.getDocumentElement(),
							KohlsXMLLiterals.E_ORDER);
			Element orderLinesEle = SCXmlUtil.getChildElement(orderEle,
					KohlsXMLLiterals.E_ORDER_LINES);

			NodeList orderLineNl = orderLinesEle
					.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

			for (int k = 0; k < orderLineNl.getLength(); k++) {

				Element orderLineEle = (Element) orderLineNl.item(k);
				String orderLineKeyStr = orderLineEle
						.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);

				if (!orderLineKeyList.contains(orderLineKeyStr)) {

					orderLinesEle.removeChild(orderLineEle);
				} else {

					Element eleShipnode = SCXmlUtil.getChildElement(
							orderLineEle, KohlsXMLLiterals.E_SHIPNODE);
					Document docItemDOC = getDocumentFromElement(eleShipnode);
					eleShipnode.setAttribute(KohlsXMLLiterals.A_STORE_HRS,
							strStoreHrs);
					
					//Code to stamp the Receipt ID in the Customer Notification
					String shipNode = eleShipnode.getAttribute(KohlsXMLLiterals.A_SHIP_NODE);

					Element eleOrNotes = SCXmlUtil.getChildElement(orderEle,
							KohlsXMLLiterals.E_NOTES);

					String strNumOfNotes = eleOrNotes.getAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES);
					int inNumOfNotes = Integer.parseInt(strNumOfNotes);
					if(inNumOfNotes>0){
						Map mpNote = new HashMap<String, String>();
						NodeList ndOrNote = eleOrNotes.getElementsByTagName(KohlsXMLLiterals.E_NOTE);
						for(int j=0;j<ndOrNote.getLength();j++){
							Element eleOrNote = (Element) ndOrNote.item(j);
							String strNoteReasCode = eleOrNote.getAttribute(KohlsXMLLiterals.A_REASON_CODE);
							String strNoteReasText = eleOrNote.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);
							mpNote.put(strNoteReasCode, strNoteReasText);
						}	

						strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+KohlsConstant.BPS+"_"+shipNode);
						log.debug("strPreEnReceiptId" + strPreEnReceiptId);

					}
					// Ends Here Stamping Receipt ID
					
					// Code to fetch Item Size and Color Description
					Element elemItem = SCXmlUtil.getChildElement(orderLineEle,
							KohlsXMLLiterals.E_ITEM);
					Document docItem = getDocumentFromElement(elemItem);
					 Document itemListDoc = KohlsCommonUtil
					 .invokeAPI(
					 env,
					 KohlsConstant.API_GET_ITEM_LIST_WITH_EXTN_FIELDS_TEMPLATE_PATH,
					 KohlsConstant.API_GET_ITEM_LIST, docItem);

					 Element eleItem = SCXmlUtil.getChildElement(
							itemListDoc.getDocumentElement(),
							KohlsXMLLiterals.E_ITEM);
					Element eleItemExtn = SCXmlUtil.getChildElement(eleItem,
							KohlsXMLLiterals.E_EXTN);
					String colorDesc = eleItemExtn
							.getAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC);
					String sizeDesc = eleItemExtn
							.getAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC);

					Element extnElem = SCXmlUtil.createChild(elemItem,
							KohlsXMLLiterals.E_EXTN);
					extnElem.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC,
							colorDesc);
					extnElem.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC,
							sizeDesc);

				}
			}

			
			
			Element elemOrder = SCXmlUtil
					.getChildElement(orderListDoc.getDocumentElement(),
							KohlsXMLLiterals.E_ORDER);

			elemOrder.setAttribute(KohlsXMLLiterals.A_RECEIPT_ID, strPreEnReceiptId);
			if (custNotificationType != null)
				elemOrder.setAttribute(
						KohlsXMLLiterals.A_CUST_NOTIFICATION_SENT,
						custNotificationType);
			else
				elemOrder.setAttribute(
						KohlsXMLLiterals.A_CUST_NOTIFICATION_SENT, "");

			outOrderDoc = getDocumentFromElement(elemOrder);

			log.debug("Final Output : "
					+ XMLUtil.getXMLString(outOrderDoc));

			return outOrderDoc;
			
		} catch (Exception e) {
			e.getStackTrace();
			throw new YFSException(
					"Exception in inputBOPUSWebServiceCall method is: "
							+ e.getMessage());
		}

	}

	public Document callWSNotification  (YFSEnvironment env, Document requestDoc) 
	{
		try {
			//WebServiceCaller wsCaller = new WebServiceCaller();
			//SOAPBody responseBody = wsCaller.invokeWSNotification(env, requestDoc);
			KohlsWebServiceUtilAPI wsCaller = new KohlsWebServiceUtilAPI();
			Document responseDoc = wsCaller.callMultiNameSpaceWebService(env, requestDoc);

			//if (responseBody.hasFault()) 
				//createAsyncRequest(env,requestDoc);
		}
		catch (Exception e)
		{
			createAsyncRequest(env,requestDoc);
			e.getStackTrace();
			throw new YFSException("Exception in callWSNotification method is: "+e.getMessage());
		}
		return requestDoc;
	}

	private void createAsyncRequest(YFSEnvironment env, Document docInput)
	{
		Element inElem = docInput.getDocumentElement();		
		Document inForAPI = SCXmlUtil.createDocument(KohlsXMLLiterals.E_CREATE_ASYNC_REQUEST);		
		Element inElemForAPI = inForAPI.getDocumentElement();		
		Element apiElem = SCXmlUtil.createChild(inElemForAPI, KohlsXMLLiterals.E_API); 		
		Element inputElem = SCXmlUtil.createChild(apiElem, KohlsXMLLiterals.E_INPUT);		
		apiElem.setAttribute(KohlsXMLLiterals.A_IS_SERVICE, KohlsConstant.YES);		
		apiElem.setAttribute(KohlsXMLLiterals.A_NAME, KohlsXMLLiterals.A_NOTIF_SERVICE_REQ);		
		Document inputDoc = inputElem.getOwnerDocument();		
		Element elem = (Element) inputDoc.importNode(inElem, true);		
		inputElem.appendChild(elem);
		try {
			KohlsCommonUtil.invokeAPI(env,KohlsConstant.API_CREATE_ASYNC_REQUEST,inputDoc);
		}
		catch (Exception ex)
		{
			ex.getStackTrace();
			throw new YFSException("The method createAsyncRequest() returned Error. "+ex.getMessage());

		}

	}

	
	public Document bopusGetOrderListForOrderMod(YFSEnvironment env, Document inDoc){

		log.debug("in bopusGetOrderListForOrderMod inDoc:"+SCXmlUtil.getString(inDoc));

		try {

			Document orderListDoc = null;
			Document orderAuditListDoc = null; 
			Element eleShipment=  inDoc.getDocumentElement();

			Element eleExtn = SCXmlUtil.getChildElement(eleShipment, KohlsXMLLiterals.E_EXTN);
			String CustNotifType = null;

			boolean sendCancelledNotification = false;
			/*if(eleExtn != null)
				CustNotifType = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_CUST_NOTIFICATION_SENT);*/

			Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment, KohlsXMLLiterals.E_SHIPMENT_LINES);
			Element eleShipmentLine = SCXmlUtil.getChildElement(eleShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);

			Document inDocOrder = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
			Element inEleOrder = inDocOrder.getDocumentElement();
			inEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, eleShipment.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));

			Document inDocOrderAudit = null;

			try {
				orderListDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstant.API_GET_ORDER_LIST_BOPUS_CONSOL_TEMPLATE, KohlsConstant.API_GET_ORDER_LIST, inDocOrder);

				inDocOrderAudit = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER_AUDIT);
				inDocOrderAudit.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, eleShipment.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));

				orderAuditListDoc = KohlsCommonUtil.invokeAPI( env, KohlsConstant.GET_ORDER_AUDIT_LIST_OUTPUT_TMPLT, KohlsConstant.API_GET_ORDER_AUDIT_LIST, inDocOrderAudit);

				Element eleOrderAutditLevels = null;

				Document orderAutditDoc = null;
				Element eleOrderAuditLevels = null;

				Document orderAutdit2beimpDoc = null;
				Map<String, Element> orderAuditLineMap = new HashMap<String, Element>();

				if(orderAuditListDoc != null){

					ArrayList<Element> eleOrderAuditList = SCXmlUtil.getChildren(orderAuditListDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER_AUDIT);
					Iterator<Element> itrOrderAuditList  = eleOrderAuditList.iterator();
					Element eleOrderAudit = null;	

					Element eleOrderAuditLevel = null;

					Element eleOrderAuditDetails = null;

					ArrayList<Element> eleOrderAuditLevelList = null;
					Iterator<Element> itrOrderAuditLevelList = null;

					ArrayList<Element> eleOrderAuditDetailList = null;
					Iterator<Element> itrOrderAuditDetailList = null;

					ArrayList<Element> eleAtrributeList = null;
					Iterator<Element> itrAtrributeList = null;

					Element eleAttributes = null;
					Element eleAttribute = null;

					while(itrOrderAuditList.hasNext()){
						eleOrderAudit = itrOrderAuditList.next();
						eleOrderAutditLevels = SCXmlUtil.getChildElement(eleOrderAudit, KohlsXMLLiterals.E_ORDER_AUDIT_LEVELS);
						eleOrderAuditLevelList = SCXmlUtil.getChildren(eleOrderAutditLevels, KohlsXMLLiterals.E_ORDER_AUDIT_LEVEL);
						itrOrderAuditLevelList = eleOrderAuditLevelList.iterator();
						while(itrOrderAuditLevelList.hasNext()){
							eleOrderAuditLevel = itrOrderAuditLevelList.next();
							if(KohlsConstant.ORDER_LINE.equals(eleOrderAuditLevel.getAttribute(KohlsXMLLiterals.A_MODIFICATION_LEVEL))){
								orderAutditDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER_AUDIT);
								eleOrderAuditLevels = SCXmlUtil.createChild(orderAutditDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER_AUDIT_LEVELS);
								SCXmlUtil.importElement(eleOrderAuditLevels, eleOrderAuditLevel);
								orderAuditLineMap.put(eleOrderAuditLevel.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY), orderAutditDoc.getDocumentElement());
							}
							else if(KohlsConstant.ORDER.equals(eleOrderAuditLevel.getAttribute(KohlsXMLLiterals.A_MODIFICATION_LEVEL))){

								eleOrderAuditDetails = SCXmlUtil.getChildElement(eleOrderAuditLevel, KohlsXMLLiterals.A_ORDER_AUDIT_DETAILS);								

								eleOrderAuditDetailList = SCXmlUtil.getChildren(eleOrderAuditDetails, KohlsXMLLiterals.A_ORDER_AUDIT_DETAIL);
								itrOrderAuditDetailList = eleOrderAuditDetailList.iterator();
								while(itrOrderAuditDetailList.hasNext()){
									eleAttributes = SCXmlUtil.getChildElement(itrOrderAuditDetailList.next(), KohlsXMLLiterals.E_ATTRIBUTES); 
									eleAtrributeList = SCXmlUtil.getChildren(eleAttributes, KohlsXMLLiterals.E_ATTRIBUTE);
									itrAtrributeList = eleAtrributeList.iterator();
									while(itrAtrributeList.hasNext()){
										eleAttribute = itrAtrributeList.next();
										if(KohlsXMLLiterals.A_TOTAL_AMOUNT.equals(eleAttribute.getAttribute(KohlsXMLLiterals.A_NAME))){
											orderAutdit2beimpDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER_AUDIT);
											eleOrderAuditLevels = SCXmlUtil.createChild(orderAutdit2beimpDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER_AUDIT_LEVELS);
											SCXmlUtil.importElement(eleOrderAuditLevels, eleOrderAuditLevel);
										}
									}
								}
							}
						}
					}

				}

				if(orderListDoc != null){

					Element eleOrder = SCXmlUtil.getChildElement(orderListDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER);

					if (!YFCCommon.isVoid(orderAutdit2beimpDoc)){
						SCXmlUtil.importElement(eleOrder, orderAutdit2beimpDoc.getDocumentElement());
					}
					Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, KohlsXMLLiterals.E_ORDER_LINES);
					ArrayList<Element> eleOrderLineList = SCXmlUtil.getChildren(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
					Element eleOrderLine = null;
					Element eleExtnOrderLine = null;
					Element eleLinePriceInfo = null;
					Element eleShipnode = null;
					String strShipnode = null;
					List<Element> orderLineToBeAddedList = new ArrayList<Element>();

					Iterator<Element> itrOrderLineList = eleOrderLineList.iterator();
					while(itrOrderLineList.hasNext()){
						eleOrderLine = itrOrderLineList.next();
						eleExtnOrderLine = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_EXTN);
						eleLinePriceInfo = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_LINE_PRICE_INFO);
						if(!eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY).equals(eleExtnOrderLine.getAttribute(KohlsXMLLiterals.A_EXTN_ORDERED_QTY)) || !eleLinePriceInfo.getAttribute(KohlsXMLLiterals.A_UNIT_PRICE).equals(eleExtnOrderLine.getAttribute(KohlsXMLLiterals.A_EXTN_UNIT_PRICE)) ){
							if(orderAuditLineMap.containsKey(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY)))
								SCXmlUtil.importElement(eleOrderLine, orderAuditLineMap.get(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY)));

							Element eleOrderLineExtn = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_EXTN);
							String strExtnCustCancelNotification = eleOrderLineExtn.getAttribute(KohlsXMLLiterals.EXTN_CUST_CNCL_NOTICE);
							if(!strExtnCustCancelNotification.equalsIgnoreCase(KohlsConstant.YES)){
								orderLineToBeAddedList.add(eleOrderLine);
							}
						}
					}
					String strOrderModShipNode = "";
					itrOrderLineList = orderLineToBeAddedList.iterator();
					while(itrOrderLineList.hasNext()){

						if(!orderLineToBeAddedList.isEmpty() ){
							SCXmlUtil.removeNode(eleOrderLines);
							eleOrderLines = SCXmlUtil.createChild(eleOrder, KohlsXMLLiterals.E_ORDER_LINES);
						}
						eleOrderLine = itrOrderLineList.next();

						Element eleModShipnode = null;
						String strModShipnode = null;

						eleShipnode = SCXmlUtil.getChildElement(eleOrderLine,"Shipnode");
						strShipnode=eleShipnode.getAttribute(KohlsConstant.ATTR_SHIP_NODE);	
						if(!strOrderModShipNode.contains(strShipnode) ){
							Iterator<Element> itrOrderLineListNew = orderLineToBeAddedList.iterator();
							while(itrOrderLineListNew.hasNext()){
								Element eleModOrderLine = null;
								eleModOrderLine=itrOrderLineListNew.next();

								eleModShipnode = SCXmlUtil.getChildElement(eleModOrderLine,"Shipnode");
								strModShipnode=eleModShipnode.getAttribute(KohlsConstant.ATTR_SHIP_NODE);	

								if(strShipnode.equalsIgnoreCase(strModShipnode)){
									SCXmlUtil.importElement(eleOrderLines, eleModOrderLine);
								}
							}
							if(CustNotifType != null)
								eleOrder.setAttribute(KohlsXMLLiterals.A_CUST_NOTIFICATION_TYPE, CustNotifType);
							else
								eleOrder.setAttribute(KohlsXMLLiterals.A_CUST_NOTIFICATION_TYPE, "");

							inDoc = getDocumentFromElement(eleOrder);
							
							// Call for include Extn Item Details.
							inDoc = getExtnItemDetails(env, inDoc);

							eleOrderLines = SCXmlUtil.getChildElement(eleOrder, KohlsXMLLiterals.E_ORDER_LINES);
							eleOrderLineList = SCXmlUtil.getChildren(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);


							if(!eleOrderLineList.isEmpty()){
								log.debug("Modification Customer Notification sent"+SCXmlUtil.getString(inDoc));
								KohlsCommonUtil.invokeService(env, KohlsConstant.KOHLS_BPS_ORDR_MOD_CUST_MSG, inDoc);
							}
							strOrderModShipNode+= strOrderModShipNode+";"+strShipnode;
						}
					}
				}
			} catch (Exception e) {
				e.getStackTrace();
				throw new YFSException("The method bopusGetOrderListForOrderMod() returned Error. "+e.getMessage());

			}


		} catch (Exception e) {
			e.getStackTrace();
			throw new YFSException("The method bopusGetOrderListForOrderMod() returned Error."+e.getMessage());
		}

		return inDoc;
	}

	
	private Document getExtnItemDetails(YFSEnvironment env, Document inDoc) {
		try {

			Element eleOrder = inDoc.getDocumentElement();
			Element orderLinesEle = SCXmlUtil.getChildElement(eleOrder,
					KohlsXMLLiterals.E_ORDER_LINES);

			NodeList orderLineNl = orderLinesEle
					.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

			for (int k = 0; k < orderLineNl.getLength(); k++) {

				Element orderLineEle = (Element) orderLineNl.item(k);

				// Code to fetch Item Size and Color Description
				Element elemItem = SCXmlUtil.getChildElement(orderLineEle,
						KohlsXMLLiterals.E_ITEM);
				Document docItem = getDocumentFromElement(elemItem);

				Document itemListDoc = KohlsCommonUtil
						.invokeAPI(
								env,
								KohlsConstant.API_GET_ITEM_LIST_WITH_EXTN_FIELDS_TEMPLATE_PATH,
								KohlsConstant.API_GET_ITEM_LIST, docItem);

				Element eleItem = SCXmlUtil.getChildElement(
						itemListDoc.getDocumentElement(),
						KohlsXMLLiterals.E_ITEM);
				Element eleItemExtn = SCXmlUtil.getChildElement(eleItem,
						KohlsXMLLiterals.E_EXTN);
				String colorDesc = eleItemExtn
						.getAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC);
				String sizeDesc = eleItemExtn
						.getAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC);

				Element extnElem = SCXmlUtil.createChild(elemItem,
						KohlsXMLLiterals.E_EXTN);
				extnElem.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC,
						colorDesc);
				extnElem.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC,
						sizeDesc);
			}
		} catch (Exception e) {
			e.getStackTrace();
			throw new YFSException("The method getExtnItemDetails() returned Error."+e.getMessage());
		}
		return inDoc;

	}
	
	public String getPropertyValue(String property) {

		String propValue;
		propValue = YFSSystem.getProperty(property);
		if(YFCCommon.isVoid(propValue)){
			propValue = property;
		}
		return propValue;

	}

	public void setProperties(Properties prop) throws Exception {
		this.props = prop;
	}

	private void testWebserviceInput()  
	{
		File file = new File("C:\\MyBriefcase\\Official\\Program\\ApplicationDocuments\\KOHLS\\CustomerNotification\\InputXMLBOPUSWebserviceCall.xml");
		try {
			Document doc = XMLUtil.getDocument(new FileInputStream(file));

			this.inputBOPUSWebServiceCall(null, doc);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] arg) {
		new KohlsOrderNotificationUtil().testWebserviceInput();
	}

}
