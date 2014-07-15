package com.kohls.bopus.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.kohls.bopus.util.KOHLSCommonCodeList;

public class KohlsEComChngOrdrOnHold {
	
	//<Order OrderNo="Y100005001" EnterpriseCode="KOHLS.COM" DocumentType="0001" Override="Y"/>

	public void ChangeOrder(YFSEnvironment env, Document inDoc) throws Exception {
		
		boolean changeOrderStatFlag = true;
		boolean cnfrmShpmntFlag = true;
		Element eleOrdInDoc = inDoc.getDocumentElement();
		
		
		Document inDocShipment = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element inEleShipment = inDocShipment.getDocumentElement();
		
		inEleShipment.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
		inEleShipment.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.KOHLS_ENTERPRISE_CODE);
		inEleShipment.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, eleOrdInDoc.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER));
		
		Document shipmentListOutPut = 
			KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_GET_SHP_HOLDS_TEMPLATE ,KohlsConstant.GET_SHIPMENT_LIST_API, inDocShipment);
		

		if (shipmentListOutPut != null) {
			
			Element eleGetShpmntListForOrder = shipmentListOutPut.getDocumentElement();
			NodeList nodeShipments = eleGetShpmntListForOrder.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);
		
			for (int i=0; i<nodeShipments.getLength(); i++) {
				Element eleShipment = (Element) nodeShipments.item(i);
				String strDeliveryMethod = eleShipment.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
				String strShipmentNo = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
				String strShipNode = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIP_NODE);
				
				if ( strDeliveryMethod.equals(KohlsConstant.V_PICK) ) {
					
					String strShipmentStatus =  eleShipment.getAttribute(KohlsXMLLiterals.A_STATUS);
					String strEligibleStatValue = KOHLSCommonCodeList.statusValue(env, KohlsConstant.CC_DESC_AWAITING_STORE_PICK , KohlsConstant.V_CODE_TYPE_ORD);
					
					//If shipment status beyond AWAITING STORE PICK we will not allow change Order 
					if (!(strShipmentStatus.equals(strEligibleStatValue))) {
						changeOrderStatFlag = false;
						break;
					}
				}
			}
			
			if (changeOrderStatFlag && cnfrmShpmntFlag ) {
				Document docChngOrderOutput = KohlsCommonUtil.invokeAPI( env,  KohlsConstant.CHANGE_ORDER_API, inDoc);
			}
		
		}
	}
}
	
