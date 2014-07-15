package com.kohls.bopus.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.bopus.util.KohlsOrderPickProcessUtil;
import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXPathUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsApplySuspendHold {
	private YIFApi api;
	public KohlsApplySuspendHold() throws YIFClientCreationException {
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsApplySuspendHold.class.getName());
	public void addHold(YFSEnvironment env,Document inDoc) {
		log.debug("Inside addHold.....");
		Element eleinDoc = inDoc.getDocumentElement();
		log.debug("the input doc is:"+SCXmlUtil.getString(inDoc));
		
		String shipmnetNo = eleinDoc.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
	
		try {
			Document shipmentListDoc = callGetShipmentList(env,shipmnetNo);
			log.debug("Output doc of:"+SCXmlUtil.getString(shipmentListDoc));
			Element eleshipmentsDoc = shipmentListDoc.getDocumentElement();
			
			Element eleShipment = KohlsXPathUtil.getElementByXpath(shipmentListDoc,"Shipments/Shipment");	
			String strShipmntKey = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
			Element eleReadShipLines = KohlsXPathUtil.getElementByXpath(shipmentListDoc,"Shipments/Shipment/ShipmentLines/ShipmentLine");
		
			String strOrderNo = eleReadShipLines.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
			String strOrderHeaderKey = eleReadShipLines.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
	
			Document shipmentDoc = SCXmlUtil.createDocument(KohlsConstant.SHIPMENT);
			Element eleshipmentDoc = shipmentDoc.getDocumentElement();
			
			eleshipmentDoc.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, strShipmntKey);
			Element eleExtn = SCXmlUtil.createChild(eleshipmentDoc, KohlsXMLLiterals.E_EXTN);	
			eleExtn.setAttribute(KohlsXMLLiterals.EXTN_SUSPENDED_PICKUP, KohlsConstant.YES);
		
			Element eleShipmentHoldTypes = SCXmlUtil.createChild(eleshipmentDoc, KohlsXMLLiterals.E_SHIPMENT_HOLD_TYPES);
			Element eleShipmentHoldType = SCXmlUtil.createChild(eleShipmentHoldTypes, KohlsXMLLiterals.E_SHIPMENT_HOLD_TYPE);
			
			eleShipmentHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, KohlsConstant.PICKING_SUSPENDED);
			eleShipmentHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, KohlsConstant.SHIPMENT_SUSPEND_HOLD);
			eleShipmentHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, KohlsConstant.SUSPEND_STATUS);
			
			log.debug("input document for changeShipment is:"+SCXmlUtil.getString(shipmentDoc));
			
			
			Document docGetCommonCodeOutputXML = api.changeShipment(env, shipmentDoc);
			
			if(docGetCommonCodeOutputXML == null){
				throw new YFSException("changeShipment returned null");
			}
			
			log.debug("Output document for changeShipment is:"+SCXmlUtil.getString(docGetCommonCodeOutputXML));
						
			KohlsOrderPickProcessUtil.updatingStoreEventsHFTable(env,strShipmntKey,strOrderNo,strOrderHeaderKey,"","","","",env.getUserId(),KohlsConstant.V_PICKING_SUSPENDED);
			log.debug("Exiting addHold.....");
		}
		catch(Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in addHold method is: "+e.getMessage());
		}
		
		
	}
	
   public Document resolveHold(YFSEnvironment env,Document inDoc) {
	    log.debug("Inside resolveHold.....");
	    
	    log.debug("the input doc is:"+SCXmlUtil.getString(inDoc));
		Element eleinDoc = inDoc.getDocumentElement();
		String shipmnetNo = eleinDoc.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
		Document sendResponseDoc = null;
		try {
			Document shipmentListDoc = callGetShipmentList(env,shipmnetNo);
			log.debug("Output doc of:"+SCXmlUtil.getString(shipmentListDoc));
			Element eleshipmentsDoc = shipmentListDoc.getDocumentElement();
			
			Element eleShipment = KohlsXPathUtil.getElementByXpath(shipmentListDoc,"Shipments/Shipment");	
			String strShipmntKey = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
			Element eleReadShipLines = KohlsXPathUtil.getElementByXpath(shipmentListDoc,"Shipments/Shipment/ShipmentLines/ShipmentLine");
		
			String strOrderNo = eleReadShipLines.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
			String strOrderHeaderKey = eleReadShipLines.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		
			Document shipmentDoc = SCXmlUtil.createDocument(KohlsConstant.SHIPMENT);
			Element eleshipmentDoc = shipmentDoc.getDocumentElement();
			Element eleExtn = SCXmlUtil.createChild(eleshipmentDoc, KohlsXMLLiterals.E_EXTN);	
			eleExtn.setAttribute(KohlsXMLLiterals.EXTN_SUSPENDED_PICKUP, KohlsConstant.NO);
			eleshipmentDoc.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, strShipmntKey);
			eleshipmentDoc.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE,KohlsConstant.SO_ENTERPRISE_CODE);
			
					
			Element eleShipmentHoldTypes = SCXmlUtil.createChild(eleshipmentDoc, KohlsXMLLiterals.E_SHIPMENT_HOLD_TYPES);
			Element eleShipmentHoldType = SCXmlUtil.createChild(eleShipmentHoldTypes, KohlsXMLLiterals.E_SHIPMENT_HOLD_TYPE);
			
			eleShipmentHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, KohlsConstant.PICKING_SUSPENDED);
			eleShipmentHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, KohlsConstant.SHIPMENT_SUSPEND_HOLD);
			eleShipmentHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, KohlsConstant.RESUME_STATUS);
			
			log.debug("Input document for changeShipment API is:"+SCXmlUtil.getString(shipmentDoc));
		
		
			Document docGetCommonCodeOutputXML = api.changeShipment(env, shipmentDoc);
			
			if(docGetCommonCodeOutputXML == null){
				throw new YFSException("changeShipment returned null");
			}
		
			KohlsOrderPickProcessUtil.updatingStoreEventsHFTable(env,strShipmntKey,strOrderNo,strOrderHeaderKey,"","","","",env.getUserId(),KohlsConstant.V_PICKING_RESUMED);
			
		
			sendResponseDoc = KohlsCommonUtil.invokeService(env, "KohlsBOPUSStorePickService" , inDoc);
			if(sendResponseDoc == null) {
				throw new YFSException("Response doc is null");
			}
			
			
		}
		catch(Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in resolveHold method is: "+e.getMessage());
		}
		
		return sendResponseDoc;
	}
   
   public Document callGetShipmentList(YFSEnvironment env,String shipmentNo) throws Exception {
	   Document inputShipmentDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();
	   Element eleshipmnet =  inputShipmentDoc.getDocumentElement();
	   eleshipmnet.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, shipmentNo);
	   Document outputShipmentListDoc = null;
	   
	   outputShipmentListDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.TEMPLATE_SHIPMENTS_PICK_PROCESS, 
					KohlsConstants.API_GET_SHIPMENT_LIST, inputShipmentDoc);
	   
	   
	   return outputShipmentListDoc;
	   
   }

}
