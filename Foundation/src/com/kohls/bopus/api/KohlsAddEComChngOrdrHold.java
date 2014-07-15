package com.kohls.bopus.api;


/*****************************************************************************
 * File Name    : KohlsAddEComChngOrdrHold.java
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

public class KohlsAddEComChngOrdrHold{
	
	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsAddEComChngOrdrHold.class.getName());
	
	public Document addEComChngOrdrHold(YFSEnvironment env, Document inXML) throws Exception{
		
		log.debug("orderReprice: inDoc:"+"KohlsAddEComChngOrdrHold: inXML:"+SCXmlUtil.getString(inXML));		
		
		/****Input to change order to apply hold
		 * <Order OrderNo="1234567890" EnterpriseCode="KOHLS.COM" DocumentType="0001" Override="Y">
		 * 	    <OrderHoldTypes>
		 * 	        <OrderHoldType HoldType="EComChngOrdrHold" Status="1300"/>
		 * 	    </OrderHoldTypes>
		 * 	    </Order>*****/
		
		Document docCommonCodes = SCXmlUtil.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		Document docCommonCodesOut = null;
		String strCodeValue = null;
		Element eleCommonCodes = docCommonCodes.getDocumentElement();
		Element eleCommonCode = null;
		eleCommonCodes.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, KohlsConstant.ITEM_ORGANIZATION_CODE);
		eleCommonCodes.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.BPS_MODIFICATN_TYPE);
		
		try {

			docCommonCodesOut = KohlsCommonUtil.invokeAPI(env, KohlsConstant.API_GET_COMMON_CODE_LIST, docCommonCodes);
			log.debug("KohlsAddEComChngOrdrHold: docCommonCodesOut:"+SCXmlUtil.getString(docCommonCodesOut));
			if(docCommonCodesOut != null){
				eleCommonCode = SCXmlUtil.getChildElement(docCommonCodesOut.getDocumentElement(), KohlsXMLLiterals.E_COMMON_CODE);
				if(eleCommonCode != null)
					strCodeValue = eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"The method addEComChngOrdrHold() returned Error."+e);
		}		
		
		Element eleIn = inXML.getDocumentElement();
		Document docChangeOrderOut = null;		
		Document inpDocChangeOrder = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
		Element inpEleChangeOrder = inpDocChangeOrder.getDocumentElement();
		Element eleOrderHoldTypes = SCXmlUtil.createChild(inpEleChangeOrder, KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
		Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		
		inpEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, eleIn.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER));
		inpEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleIn.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
		inpEleChangeOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, eleIn.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
		inpEleChangeOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
		inpEleChangeOrder.setAttribute(KohlsXMLLiterals.A_MODIFICATION_REASON_CODE, strCodeValue);
		
		eleOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, KohlsConstant.ECOM_CHNG_ORDR_HOLD);
		eleOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, KohlsConstant.ADD_HOLD_STATUS);
		eleOrderHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, KohlsConstant.ECOM_CHNG_ORDR_HOLD_REASON_TXT);
		
		log.debug("KohlsAddEComChngOrdrHold: inXML:"+SCXmlUtil.getString(inpDocChangeOrder));
		
		
		try {

			docChangeOrderOut = KohlsCommonUtil.invokeAPI(env, KohlsConstant.CHANGE_ORDER_INP_TMPLT, KohlsConstants.CHANGE_ORDER_API, inpDocChangeOrder);
			log.debug("KohlsAddEComChngOrdrHold: docChangeOrderOut:"+SCXmlUtil.getString(docChangeOrderOut));
			// Order Modification - Ravi
			applyingShipmentHolds(env, inXML);

		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"The method addEComChngOrdrHold() returned Error."+e);
		}
		
		return docChangeOrderOut;
	}
	
	/* <Shipment ShipmentNo="100000460" EnterpriseCode="KOHLS.COM"  Override="Y">
		<ShipmentHoldTypes>
			<ShipmentHoldType HoldType="HOLD_CFRM_SHPMT" Status="1100"/>
		</ShipmentHoldTypes>
		</Shipment> */
	
	// Applying Shipment Holds for corresponding Order holds during modification, if shipments are already created
	private void applyingShipmentHolds(YFSEnvironment env, Document inDoc) throws Exception {
		// TODO Auto-generated method stub
		
		Document docGetShpmntListForOrder = 
			KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_SHPMNT_LIST_FOR_ORDR_TEMPLATE, KohlsConstant.API_GET_SHIPMENT_LIST_FOR_ORDER, inDoc);
		
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
					eleShpmntHldType.setAttribute(KohlsXMLLiterals.A_STATUS, KohlsConstant.HOLD_CREATED_STATUS);
					
					Document docChangeShpmntOutput = 
 						KohlsCommonUtil.invokeAPI( env,KohlsConstant.API_CHANGE_SHIPMENT, docChngShipment);
				}				
		
			}
		}
	}

}
