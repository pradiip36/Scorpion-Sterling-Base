package com.kohls.bopus.api;

/*****************************************************************************
 * File Name    : KohlsShipmentStatusUpdateToEComm.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Mar 27,2014    Ashalatha        Initial Version 
 * * ---------------------------------------------------------------------------
 *****************************************************************************/

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kohls.bopus.util.KohlsOrderNotificationUtil;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This extended api is invoked to send the shipment status update
 * message to down stream system for the following statuses:
 * o	Store Pick in Progress
 * o	Placed in Hold Location
 * o	Customer Picked Up
 * o	Expired Pickup 
 * 
 */

public class KohlsShipmentStatusUpdateToEComm {
	

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsShipmentStatusUpdateToEComm.class.getName());

	
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

	public Document sendShipmentStatusUpdate(YFSEnvironment env, Document doc){
		String strShipmentKey = null;
		Document outShipmentDoc = null;
		
		log.debug("Input to sendShipmentStatusUpdate method: "+SCXmlUtil.getString(doc));
		try{	
			
			strShipmentKey = doc.getDocumentElement().getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
			log.debug("strShipmentKey :"+strShipmentKey);
			
			Document inDocGetShipmentList=SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT );
			
			if(strShipmentKey != null){
				inDocGetShipmentList.getDocumentElement().setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, strShipmentKey);	
			}
				
			Document outDocGetShipmentList=KohlsCommonUtil.invokeAPI(env, KohlsConstant.KOHLS_SHP_STATUS_GET_SHIPMENT_LIST_OUTPUT_TMPLT, KohlsConstant.API_actual_GET_SHIPMENT_LIST, inDocGetShipmentList);		
			if(outDocGetShipmentList == null) {
				throw new YFSException("Response from getShipmentList is null");
			}
			log.debug("outDocGetShipmentList is: "+SCXmlUtil.getString(outDocGetShipmentList));
			NodeList shipmentNl = outDocGetShipmentList.getElementsByTagName("Shipment");
			
			Element shipmentEle=(Element)shipmentNl.item(0);
			
			shipmentEle.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_Create);
			
			Document indocShipmentStoreEventsList=SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			if(strShipmentKey != null){
			indocShipmentStoreEventsList.getDocumentElement().setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, strShipmentKey);
			}
			Document outDocShipmentStoreEventsList=KohlsCommonUtil.invokeService(env,KohlsConstant.KOHLS_GET_SHIPMENT_STORE_EVENTS_LIST_SERVICE, indocShipmentStoreEventsList);
			log.debug("outDocShipmentStoreEventsList is: "+SCXmlUtil.getString(outDocShipmentStoreEventsList));

			if(outDocShipmentStoreEventsList == null) {
				throw new YFSException("Response from KohlsGetShipmentStoreEventsListForShipStatus is null");
			}
			XMLUtil.importElement(shipmentEle, outDocShipmentStoreEventsList.getDocumentElement());
			
			outShipmentDoc = getDocumentFromElement(shipmentEle);
			
			log.debug("Shipment Status update message sent: "+SCXmlUtil.getString(outShipmentDoc));
			
			return outShipmentDoc;

		}catch(Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in sendShipmentStatusUpdate method is: "+e.getMessage());
		}
	}

}
