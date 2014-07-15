package com.kohls.bopus.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This extended api is invoked to add store events for each shipment line 
 * with event type as 'StorePick'.
 */
public class KohlsGetItemDetailsForUI {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsGetItemDetailsForUI.class.getName());


	public Document addStorePickEventsToShipmetLine(YFSEnvironment env, Document inDoc) throws Exception
	{
		log.debug("Input to addStorePickEventsToShipmetLine method: "+SCXmlUtil.getString(inDoc));
		
		Element eleShipmentList = inDoc.getDocumentElement();
		
		//Creating input to getKohlsShipmentStoreEventsList service
		Document docInShipmentStoreEventsList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT_STORE_EVENTS);
		Element eleInShipmentStoreEventsList = docInShipmentStoreEventsList.getDocumentElement();
		
		eleInShipmentStoreEventsList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, SCXmlUtil.getXpathAttribute(eleShipmentList, KohlsXMLLiterals.E_SHIPMENT+"/@"+KohlsXMLLiterals.A_SHIPMENT_KEY));
		eleInShipmentStoreEventsList.setAttribute(KohlsXMLLiterals.A_EVENT_TYPE, KohlsXMLLiterals.STORE_PICK);
		
		log.debug("Input to getKohlsShipmentStoreEventsList service: "+SCXmlUtil.getString(docInShipmentStoreEventsList));
		Document docShipmentStoreEventsList = KohlsCommonUtil.invokeService(env, KohlsConstant.KOHLS_GET_SHIPMENT_STORE_EVENTS, docInShipmentStoreEventsList);
		
		log.debug("output of getKohlsShipmentStoreEventsList service: "+SCXmlUtil.getString(docShipmentStoreEventsList));
		
		Element eleShipmentStoreEventsList = docShipmentStoreEventsList.getDocumentElement();
		
		NodeList NLShipmentStoreEvents = SCXmlUtil.getXpathNodes(eleShipmentStoreEventsList, KohlsXMLLiterals.SHIPMENT_STORE_EVENTS);
		
		Element eleShipmentStoreEvents = null;
		Element eleLineStoreEventsList = null;
		
		NodeList NLShipmentLines = SCXmlUtil.getXpathNodes(eleShipmentList, KohlsXMLLiterals.E_SHIPMENT+"/"+KohlsXMLLiterals.E_SHIPMENT_LINES+"/"+KohlsXMLLiterals.E_SHIPMENT_LINE);
		Element eleShipmentLine = null;
		
		//For each shipment line adding the store events
		for(int i= 0;i<NLShipmentLines.getLength(); i++)
		{
			eleShipmentLine = (Element) NLShipmentLines.item(i);
			String strShipmentLineNo = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO);
			for(int j=0; j<NLShipmentStoreEvents.getLength(); j++)
			{
				if(eleLineStoreEventsList == null)
				{
					eleLineStoreEventsList = SCXmlUtil.createChild(eleShipmentLine, KohlsXMLLiterals.SHIPMENT_STORE_EVENTS_LIST);
				}
				
				eleShipmentStoreEvents = (Element) NLShipmentStoreEvents.item(j);
				if(strShipmentLineNo.equals(eleShipmentStoreEvents.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO)))
				{
					SCXmlUtil.importElement(eleLineStoreEventsList, eleShipmentStoreEvents);
				}
				
			}
			eleLineStoreEventsList = null;
			
		}
		return inDoc;
		
	}


}
