package com.kohls.bopus.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.custom.util.xml.XMLUtil;
import com.kohls.bopus.api.KohlsResolveEComChngOrdrHold;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsBOPUSHoldCheck {

    
    private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsResolveEComChngOrdrHold.class.getName());
   
	
	public boolean orderHoldCheck(YFSEnvironment env, Document inDoc) throws Exception {
		
		boolean ordHoldChkFlag = false;
		
		Document docOrderListOutPut = 
			KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_GET_ORDER_HOLDS_TEMPLATE ,KohlsConstant.API_GET_ORDER_LIST, inDoc);
		
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("######### getOrderList API Output  ##########"+ XMLUtil.getXMLString(docOrderListOutPut));				 
		}
		
		Element eleOrderListOutPut = docOrderListOutPut.getDocumentElement();
		Element eleOrder = SCXmlUtil.getChildElement(eleOrderListOutPut, KohlsXMLLiterals.E_ORDER);
		Element eleOrderHoldTypes = SCXmlUtil.getChildElement(eleOrder, KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
		if (eleOrderHoldTypes != null ) {
			NodeList nodeHoldTypeList = eleOrderHoldTypes.getElementsByTagName(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
			String strModHoldType = KohlsConstant.ORDER_HOLD_TYPE;
		
			for (int i=0; i<nodeHoldTypeList.getLength(); i++) {
				Element eleOrderHoldType = (Element) nodeHoldTypeList.item(i);
				String strHoldStatus = eleOrderHoldType.getAttribute(KohlsXMLLiterals.A_STATUS);
				String strHoldType = eleOrderHoldType.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE);
				if (strHoldStatus.equals(KohlsConstant.HOLD_CREATED_STATUS) && strHoldType.equals(strModHoldType))  {
					ordHoldChkFlag = true;
					break;
				}
			}
		}
		return ordHoldChkFlag;
	}
	

	public boolean confirmShpmtHoldCheck(YFSEnvironment env, Document inDoc) throws Exception {
		
		boolean cnfrmShpmntFlag = false;
		
		Document docShipmentListOutPut = 
			KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_GET_SHP_HOLDS_TEMPLATE ,KohlsConstant.GET_SHIPMENT_LIST_API, inDoc);
		
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("######### getOrderList API Output  ##########"+ XMLUtil.getXMLString(docShipmentListOutPut));				 
		}
		
		Element eleShipmentListOutPut = docShipmentListOutPut.getDocumentElement();
		Element eleShipment = SCXmlUtil.getChildElement(eleShipmentListOutPut, KohlsXMLLiterals.E_SHIPMENT);
		Element eleShipmentHoldTypes = SCXmlUtil.getChildElement(eleShipment, KohlsXMLLiterals.E_SHIPMENT_HOLD_TYPES);
		
		if (eleShipmentHoldTypes !=null ) {
			NodeList nodeHoldTypeList = eleShipmentHoldTypes.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_HOLD_TYPE);
			String strModHoldType = KohlsConstant.SHPMNT_HOLD_TYPE;
		
			for (int i=0; i<nodeHoldTypeList.getLength(); i++) {
				Element eleShipmentHoldType = (Element) nodeHoldTypeList.item(i);
				String strHoldStatus = eleShipmentHoldType.getAttribute(KohlsXMLLiterals.A_STATUS);
				String strHoldType = eleShipmentHoldType.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE);
				if (strHoldStatus.equals(KohlsConstant.HOLD_CREATED_STATUS) && strHoldType.equals(strModHoldType))  {
					cnfrmShpmntFlag = true;
					break;
				}
			}
		}	
	
		return cnfrmShpmntFlag;
	}
	
}
