package com.kohls.bopus.api;

/*****************************************************************************
 * File Name    : KohlsResolveEComChngOrdrHold.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Jan 16,2014    Juned S         Initial Version 
 * ---------------------------------------------------------------------------
 *****************************************************************************/

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsResolveEComChngOrdrHold{
    
    private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsResolveEComChngOrdrHold.class.getName());
	
	public Document resolveEComChngOrdrHold(YFSEnvironment env, Document inXML){
	    
	    log.debug("resolveEComChngOrdrHold: inXML:"+SCXmlUtil.getString(inXML));
	    
	    /****Input to change order to resolve hold
		 * <Order OrderNo="1234567890" EnterpriseCode="KOHLS.COM" DocumentType="0001" Override="Y">
		 * 	    <OrderHoldTypes>
		 * 	        <OrderHoldType HoldType="EComChngOrdrHold" Status="1100"/>
		 * 	    </OrderHoldTypes>
		 * 	    </Order>*****/
	    
	    Element eleOrder = SCXmlUtil.getChildElement(inXML.getDocumentElement(), KohlsXMLLiterals.E_ORDER);
	    
	    Document docChangeOrder = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
	    Element eleChangeOrder = docChangeOrder.getDocumentElement();
	    Element eleOrderHoldTypes = SCXmlUtil.createChild(eleChangeOrder, KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
	    Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
	    eleChangeOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER));
	    eleChangeOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleOrder.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
	    eleChangeOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, eleOrder.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
	    eleChangeOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
	    
	    eleOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, KohlsConstant.ECOM_CHNG_ORDR_HOLD);
	    eleOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, KohlsConstant.RESOLVE_HOLD_STATUS);
	    eleOrderHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, KohlsConstant.ECOM_CHNG_ORDR_HOLD_REASON_TXT);
	    
	    System.out.println("resolveEComChngOrdrHold: docOrder:"+SCXmlUtil.getString(docChangeOrder));  
	    
	    try {
	    	
			Document docChangeOrderOut = KohlsCommonUtil.invokeAPI(env, KohlsConstants.CHANGE_ORDER_API, docChangeOrder);
			resolvingShipmentHolds(env, inXML);
			log.debug("resolveEComChngOrdrHold: docChangeOrderOut:"+SCXmlUtil.getString(docChangeOrderOut));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"The method resolveEComChngOrdrHold() returned Error."+e.getStackTrace());
		}	
				
	    
		
		return inXML;
	}

	private void resolvingShipmentHolds(YFSEnvironment env, Document inXML) throws Exception {
		
	    Element eleInOrder = SCXmlUtil.getChildElement(inXML.getDocumentElement(), KohlsXMLLiterals.E_ORDER);
	    Document docOrder = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
	    Element eleOrder = docOrder.getDocumentElement();
	    eleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, eleInOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER));
	    eleOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
	    eleOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.KOHLS_ENTERPRISE_CODE);
	    
		Document docGetShpmntListForOrder = 
			KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_SHPMNT_LIST_FOR_ORDR_TEMPLATE, KohlsConstant.API_GET_SHIPMENT_LIST_FOR_ORDER, docOrder);
		
		if (!(docGetShpmntListForOrder == null)) {
		
			Element eleGetShpmntListForOrder = docGetShpmntListForOrder.getDocumentElement();
			NodeList nodeShipments = eleGetShpmntListForOrder.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);
		
			for (int i=0; i<nodeShipments.getLength(); i++) {
				Element eleShipment = (Element) nodeShipments.item(i);
				String strDeliveryMethod = eleShipment.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
				String strShipmentNo = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
				String strShipNode = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIP_NODE);
								
				if ( strDeliveryMethod.equals(KohlsConstant.V_PICK) ) {
				
					Document docChngShipment = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
					Element eleChngShipment = docChngShipment.getDocumentElement();
					eleChngShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipmentNo);
					eleChngShipment.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, KohlsConstant.KOHLS_ENTERPRISE_CODE);
					eleChngShipment.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.KOHLS_ENTERPRISE_CODE);
					eleChngShipment.setAttribute(KohlsXMLLiterals.A_OVERRIDE, "Y");
					eleChngShipment.setAttribute(KohlsXMLLiterals.A_SHIP_NODE, strShipNode);

					Element eleShpmntHldTypes = SCXmlUtil.createChild(eleChngShipment, KohlsXMLLiterals.E_SHIPMENT_HOLD_TYPES);
					Element eleShpmntHldType = SCXmlUtil.createChild(eleShpmntHldTypes, KohlsXMLLiterals.E_SHIPMENT_HOLD_TYPE);
					eleShpmntHldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, KohlsConstant.V_HOLD_CONFIRM_SHPMNT);
					eleShpmntHldType.setAttribute(KohlsXMLLiterals.A_STATUS, KohlsConstant.HOLD_REMOVED_STATUS);
					
					Document docChangeShpmntOutput = 
 						KohlsCommonUtil.invokeAPI( env,KohlsConstant.API_CHANGE_SHIPMENT, docChngShipment);
				}				
		
			}
		}
		
	}

}
