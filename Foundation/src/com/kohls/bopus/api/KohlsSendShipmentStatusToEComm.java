package com.kohls.bopus.api;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsSendShipmentStatusToEComm implements YIFCustomApi {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsSendShipmentStatusToEComm.class.getName());
	private YIFApi api;
	
	public Document sendStatusUpdateToEcomm(YFSEnvironment env, Document inXML) throws Exception
	{
		log.debug("Shipment Status Update: inDoc:"+"KohlsSendShipmentStatusToEComm: inXML:"+SCXmlUtil.getString(inXML));
		
		Element inEle = inXML.getDocumentElement();
		
		//Preparing getShipmentList input 
		Document docGetShipmentList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element elseGetShipmentList = docGetShipmentList.getDocumentElement();
		
		elseGetShipmentList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, inEle.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY));
		elseGetShipmentList.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, inEle.getAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE));
		elseGetShipmentList.setAttribute(KohlsXMLLiterals.A_SHIPNODE, inEle.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
		elseGetShipmentList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, inEle.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO));
		
		log.debug("Shipment Status Update: calling getShipmentList:"+"getShipmentList: inXML:"+SCXmlUtil.getString(docGetShipmentList));
		
		//Calling getShipmentList api 
		Document docGetShipmentListOut = KohlsCommonUtil.invokeAPI(env, KohlsConstant.KOHLS_GETSHIPLIST_STATUSUPDATE_TEMPLATE, KohlsConstant.GET_SHIPMENT_LIST_API, docGetShipmentList);
		
		log.debug("Shipment Status Update: getShipmentList: outXML:"+SCXmlUtil.getString(docGetShipmentListOut));
		Element eleGetShipmentListOut = docGetShipmentListOut.getDocumentElement();
		
		Element eleShipmentFromList = SCXmlUtil.getXpathElement(eleGetShipmentListOut, KohlsXMLLiterals.E_SHIPMENT);
		
		//Added By Asha: Fix for Defect 365
		eleShipmentFromList.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_Create);
		
		//End: Fix for Defect 365
		
		//Preparing Shipment Status update message 
		Document docShipmentStatusUpdate = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element eleShipmentStatusUpdate = docShipmentStatusUpdate.getDocumentElement();
		
		//Coping all attributes from Shipment (getShipmentList output) 
		NamedNodeMap NNMShipmentAtt = eleShipmentFromList.getAttributes();
		
		for (int i = 0; i < NNMShipmentAtt.getLength(); i++) {
			Attr node = (Attr) NNMShipmentAtt.item(i);
			if(!node.getName().equals(KohlsXMLLiterals.A_SHIPMENT_KEY))
			{
				eleShipmentStatusUpdate.setAttributeNS(node.getNamespaceURI(), node.getName(), node.getValue());
			}
	      }
		
		//Coping all elements from Shipment (getShipmentList output)
		Iterator<Element> itrShipmentChildEle = SCXmlUtil.getChildren(eleShipmentFromList);
		Element eleShipmentChild = null;
		
		while(itrShipmentChildEle.hasNext()){
			eleShipmentChild = itrShipmentChildEle.next();
			SCXmlUtil.importElement(eleShipmentStatusUpdate, eleShipmentChild);
		}
		
		//Preparing input to getKohlsShipmentStoreEventsList Service
		Document docGetStoreEventsList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT_STORE_EVENTS);
		Element eleGetStoreEventsList = docGetStoreEventsList.getDocumentElement();
		
		eleGetStoreEventsList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, eleShipmentFromList.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY));
		
		log.debug("Shipment Status Update: getKohlsShipmentStoreEventsList: inXML:"+SCXmlUtil.getString(docGetStoreEventsList));
		
		Document docShipmentStoreEventsListOut = KohlsCommonUtil.invokeService(env, KohlsConstant.KOHLS_GET_SHIPMENT_STORE_EVENTS, docGetStoreEventsList);
		
		log.debug("Shipment Status Update: getKohlsShipmentStoreEventsList: outXML:"+SCXmlUtil.getString(docShipmentStoreEventsListOut));
		
		Element eleShipmentStoreEventsListOut = docShipmentStoreEventsListOut.getDocumentElement();
		
		//Adding store event to the shipment status update
		SCXmlUtil.importElement(eleShipmentStatusUpdate, eleShipmentStoreEventsListOut);
		
		KohlsCommonUtil.invokeService(env, KohlsConstant.KOHLS_SEND_SHIPMENT_STATUS_TO_ECOMM, docShipmentStatusUpdate);
		
		return inXML;
		
	}

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
