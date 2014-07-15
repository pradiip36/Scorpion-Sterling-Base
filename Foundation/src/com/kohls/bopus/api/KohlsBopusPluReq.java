package com.kohls.bopus.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.bopus.util.KohlsBopusPluUtil;
import com.kohls.bopus.util.KohlsOrderPickProcessUtil;
import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXPathUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsBopusPluReq {
	
	private YIFApi api;
	public KohlsBopusPluReq() throws YIFClientCreationException {
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsBopusPluReq.class.getName());
	
	/**
	 * 
	 *	<ItemValidation ShipmentNo="" ShipNode="" OrderNo="" OrderHeaderKey="" OrderLineNo="" ItemId="" LookupType="" />
	 * 
	 */
	public Document formPLUReq(YFSEnvironment env,Document inDoc) throws Exception {

		Element eleDoc = inDoc.getDocumentElement();
		//Read the attributes from the input document
		String shipmentNo = eleDoc.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
		String orderNo = eleDoc.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
		String orderHeaderKey = eleDoc.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		String orderLineNo = eleDoc.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_NO);
		String shipNode = eleDoc.getAttribute(KohlsXMLLiterals.A_SHIP_NODE);
		String itemId = eleDoc.getAttribute(KohlsXMLLiterals.A_ITEMID);
		String lookupType = eleDoc.getAttribute(KohlsXMLLiterals.A_LOOKUPTYPE);
		
		Document pluRespDoc = null;
		
		KohlsBopusPluUtil pluUtil = new KohlsBopusPluUtil();
		//Form PLU request
		Document pluReqDoc = pluUtil.constructPLURequestDocument(shipNode,itemId,lookupType);
		log.debug("PLU request doc before processing"+ SCXmlUtil.getString(pluReqDoc));
		
		//call PLU webservice
		try {
		
			pluRespDoc = api.executeFlow(env, "KohlsBOPUSPLULookupWS", pluReqDoc);
		} catch (Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in addHold method is: "+e.getMessage());
		
		}
		
		
		if(pluRespDoc == null){
			throw new YFSException("PLU returned null");
		}else {
		
			log.debug("PLU response doc "+ SCXmlUtil.getString(pluRespDoc));
			Element elepluRespDoc = pluRespDoc.getDocumentElement();
			Element eleException = (Element) elepluRespDoc.getElementsByTagName("EXCEPTION").item(0);
			if(!YFCCommon.isVoid(eleException)) {
				throw new YFSException("plu returned exception:"+ eleException.getTextContent());
			}else {
				Element eleRecords = (Element) elepluRespDoc.getElementsByTagName(KohlsXMLLiterals.E_RECORD).item(0);
				String callBack ="";
			
				if(!YFCCommon.isVoid(eleRecords)) {
		
					callBack = eleRecords.getAttribute(KohlsXMLLiterals.A_CALLBACK);
			
					//If callback indicator is Y
					if(callBack.equalsIgnoreCase("Y")) {
						//Form document to call getShipmentList API
						Document inputShipmentDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();				
						Element ele =  inputShipmentDoc.getDocumentElement();
						ele.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, shipmentNo);
						ele.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.SO_ENTERPRISE_CODE);
						
						log.debug("Input to getshipmentList API "+SCXmlUtil.getString(inputShipmentDoc));
						
						Document outputShipmentListDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.TEMPLATE_SHIPMENTS_PICK_PROCESS, 
								"getShipmentList", inputShipmentDoc);
						
						
						if(outputShipmentListDoc == null) {
							throw new YFSException("getshipmentList returned null");
						}
						
						log.debug("Output from getshipmentList API "+SCXmlUtil.getString(outputShipmentListDoc));
						
						Element eleShipment = KohlsXPathUtil.getElementByXpath(outputShipmentListDoc,"Shipments/Shipment");
						String sShipmentKey = eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
								    
					    // Add entry to hangoff table
						KohlsOrderPickProcessUtil.updatingItemLegalCallbackEventsHFTable(env,KohlsConstant.V_ITEM_LEGAL_CALLBACK,sShipmentKey,orderNo,orderHeaderKey,shipNode,orderLineNo,itemId,"Legal Callback");
					
					}
			
			    }
				eleDoc.setAttribute(KohlsXMLLiterals.A_CALLBACK_INDICATOR, callBack);
		
				return inDoc;
		
			}
	
		}

	}
	
}
