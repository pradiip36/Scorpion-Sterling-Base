package com.kohls.bopus.util;


import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsStatusUpdateUtil {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsStatusUpdateUtil.class.getName());
	private static YIFApi api;
	public KohlsStatusUpdateUtil() throws YIFClientCreationException {
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	public void sentOrderStatusToEcomm(YFSEnvironment env,String orderHeaderKey) throws Exception {
		log.debug("Inside sentOrderStatusToEcomm.....");
		Document indocGetOrderList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
		Element eleGetOrderList = indocGetOrderList.getDocumentElement();
		
		eleGetOrderList.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
		eleGetOrderList.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.SO_ENTERPRISE_CODE);
		eleGetOrderList.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderHeaderKey);
		
		
		Document outdocGetOrderList = KohlsCommonUtil.invokeAPI(env, KohlsConstant.KOHLS_GETORDERLIST_TEMPLATE, KohlsConstant.API_GET_ORDER_LIST, indocGetOrderList);
		
		if(outdocGetOrderList == null){
			throw new YFSException("Response from getOrderList is null");
		}
		
		log.debug("the doc is:"+SCXmlUtil.getString(outdocGetOrderList));
		
		Document processGetOrderListdoc = processGetOrderList(outdocGetOrderList);
		
		Element eleOutGetOrderList = processGetOrderListdoc.getDocumentElement();
		
		Element eleOrder = SCXmlUtil.getChildElement(eleOutGetOrderList, KohlsXMLLiterals.E_ORDER);
		
		String orderEle = SCXmlUtil.getString(eleOrder);
		log.debug("String is:"+orderEle);
		Document outDoc = SCXmlUtil.createFromString(orderEle);;
		
		
		
		log.debug("out doc is:"+SCXmlUtil.getString(outDoc));
		
		this.api.executeFlow(env, "KohlsBopusOMSSendStatusMessageSyncService", outDoc);
		
		log.debug("Exiting sentOrderStatusToEcomm.....");

	}
	
	public Document processGetOrderList(Document inDoc) {
		log.debug("Inside processGetOrderList.....");
		log.debug("The input doc is:"+ SCXmlUtil.getString(inDoc));
		Element eleinDoc = inDoc.getDocumentElement();
		
		NodeList eleOrderLineList = inDoc.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		Element eleNotes = null;
		Element eleItemDetails = null;
		
		for (int k= 0; k < eleOrderLineList.getLength() ; k++) {
			
			Element elemOrderLine = (Element)eleOrderLineList.item(k);
			
			if(!elemOrderLine.getAttribute(KohlsXMLLiterals.A_MAX_ORDER_LINE_STATUS).equalsIgnoreCase("3350.035")) {
			
				SCXmlUtil.removeNode(elemOrderLine);
			
			}
			
			else if(elemOrderLine.getAttribute(KohlsXMLLiterals.A_MAX_ORDER_LINE_STATUS).equalsIgnoreCase("3350.035")) {
				elemOrderLine.removeAttribute(KohlsXMLLiterals.A_MAX_ORDER_LINE_STATUS);
				elemOrderLine.removeAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				//For defect 153 and 154 fix
				elemOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, elemOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QUANTITY));
				eleNotes = SCXmlUtil.getChildElement(elemOrderLine, KohlsXMLLiterals.E_NOTES);
				if(null != eleNotes)
				SCXmlUtil.removeNode(eleNotes);
				eleItemDetails = SCXmlUtil.getChildElement(elemOrderLine, KohlsXMLLiterals.E_ITEM_DETAILS);
				if(null != eleItemDetails)
				{
					elemOrderLine.setAttribute(KohlsXMLLiterals.ITEM_ID, eleItemDetails.getAttribute(KohlsXMLLiterals.ITEM_ID));
					SCXmlUtil.removeNode(eleItemDetails);
				}
			}
			
			
		}
		log.debug("Current doc is:"+ SCXmlUtil.getString(inDoc));
		
		log.debug("Exiting processGetOrderList.....");
		return inDoc;
		
		
	}
	
	public void ExceptionsentOrderStatusToEcommForPickExcp(YFSEnvironment env,String orerLineKey,String orderHeaderKey,String strItemId) throws Exception {
		log.debug("Inside ExceptionsentOrderStatusToEcommForPickExcp.....");
		Document indocGetOrderList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
		Element eleGetOrderList = indocGetOrderList.getDocumentElement();
		
		eleGetOrderList.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
		eleGetOrderList.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.SO_ENTERPRISE_CODE);
		eleGetOrderList.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderHeaderKey);
		
		Document outdocGetOrderList = KohlsCommonUtil.invokeAPI(env, KohlsConstant.KOHLS_GETORDERLIST_TEMPLATE, KohlsConstant.API_GET_ORDER_LIST, indocGetOrderList);
		if(outdocGetOrderList == null) {
			throw new YFSException("Response from getOrderList is null");
		}
		log.debug("getOrderList output is:"+SCXmlUtil.getString(outdocGetOrderList));
		Element eleOutGetOrderList = outdocGetOrderList.getDocumentElement();
		Element eleOrder = SCXmlUtil.getChildElement(eleOutGetOrderList, KohlsXMLLiterals.E_ORDER);
		String strOrdNo = eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, KohlsXMLLiterals.E_ORDER_LINES);
		
		ArrayList<Element> orderLineList = SCXmlUtil.getChildren(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
		Iterator<Element> itrOrderLine = orderLineList.iterator();
		Element eleOrderLine = null;
		Element eleOrderStatuses = null;
		ArrayList<Element> orderStatusList = null;
		Iterator<Element> itrOrderStatus = null;
		Element eleOrderStatus = null;
		String strMaxLineStatus = null;
		
		Document outDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
		Element eleDoc = outDoc.getDocumentElement();
		eleDoc.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
		eleDoc.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.SO_ENTERPRISE_CODE);
		eleDoc.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, strOrdNo);
		eleOrderLines = SCXmlUtil.createChild(outDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER_LINES);
		
		while(itrOrderLine.hasNext()){
			eleOrderLine = itrOrderLine.next();
			eleOrderStatuses = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES);
			orderStatusList = SCXmlUtil.getChildren(eleOrderStatuses, KohlsXMLLiterals.E_ORDER_STATUS);

			itrOrderStatus = orderStatusList.iterator();

			while(itrOrderStatus.hasNext()){
				eleOrderStatus = itrOrderStatus.next();
				if(strMaxLineStatus.equals(eleOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS)))
					break;
			}

			if(orerLineKey.equals(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY))){
				eleOrderLine.removeAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				eleOrderLine.removeAttribute(KohlsXMLLiterals.A_MAX_ORDER_LINE_STATUS);
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, strItemId);
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_UOM, KohlsConstant.EACH);
				eleOrderStatuses = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES);
				SCXmlUtil.removeNode(eleOrderStatuses);
				eleOrderStatuses = SCXmlUtil.createChild(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES);
				SCXmlUtil.importElement(eleOrderStatuses, eleOrderStatus);
				SCXmlUtil.importElement(eleOrderLines, eleOrderLine);
			}

		
	}

	eleOrderLines = SCXmlUtil.getChildElement(eleDoc, KohlsXMLLiterals.E_ORDER_LINES);
	orderLineList = SCXmlUtil.getChildren(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
	log.debug("XML documnet to queue for pickedUp:"+  SCXmlUtil.getString(outDoc));
	
	if(!orderLineList.isEmpty()){
	    KohlsCommonUtil.invokeService(env, "KohlsBopusOMSSendStatusMessageSyncService", outDoc);	
		
	}
	
	log.debug("Exiting ExceptionsentOrderStatusToEcommForPickExcp.....");

  }
}