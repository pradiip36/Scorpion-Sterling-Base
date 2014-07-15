package com.kohls.bopus.api;

import java.util.Calendar;
import java.util.Properties;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsSendOrderExpStatusToEcomm implements YIFCustomApi{
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsSendOrderExpStatusToEcomm.class.getName());
	private YIFApi api;
	
	public Document sendExpStatusUpdateToEcomm(YFSEnvironment env, Document inXML) throws Exception
	{
		log.debug("Order Expired Update: inDoc:"+"KohlsSendOrderExpStatusToEcomm: inXML:"+SCXmlUtil.getString(inXML));
		
		Element inEle = inXML.getDocumentElement();
		
		Document docGetOrdrListInPut = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
		Element eleGetOrdrListInPut = docGetOrdrListInPut.getDocumentElement();
		
		eleGetOrdrListInPut.setAttribute(KohlsXMLLiterals.A_ORDERNO, inEle.getAttribute(KohlsXMLLiterals.A_ORDERNO));
		eleGetOrdrListInPut.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, inEle.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
		eleGetOrdrListInPut.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, inEle.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
		eleGetOrdrListInPut.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, inEle.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));
				
		Document docGetOrderListOut = KohlsCommonUtil.invokeAPI(env, KohlsConstant.KOHLS_GETORDERLIST_EXP_STATUSUPDATE_TEMPLATE, KohlsConstant.API_GET_ORDER_LIST , docGetOrdrListInPut);
		
		Element eleGetOrderListOut = docGetOrderListOut.getDocumentElement();
		Element eleOrder = SCXmlUtil.getChildElement(eleGetOrderListOut, KohlsXMLLiterals.E_ORDER);
		
		Document docOrderStatUpdate = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
		Element eleOrderStatUpdate = docOrderStatUpdate.getDocumentElement();
		
		eleOrderStatUpdate.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, eleOrder.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
		eleOrderStatUpdate.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleOrder.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
		eleOrderStatUpdate.setAttribute(KohlsXMLLiterals.A_ORDERNO, eleOrder.getAttribute(KohlsXMLLiterals.A_ORDERNO));
		
		Element eleOrderLines = docOrderStatUpdate.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		
		NodeList NLOrderLinesIn = SCXmlUtil.getXpathNodes(inEle, KohlsXMLLiterals.E_ORDER_LINES+"/"+KohlsXMLLiterals.E_ORDER_LINE);
		Element eleInOrderLine = null;
		Element eleOrderLine = null;
		
		Element eleOrderLineStatusUpdate = null;
		Element eleOrderStatuses = null;
		Element eleOrderStatus = null;
		
		for(int i=0; i<NLOrderLinesIn.getLength(); i++)
		{
			eleInOrderLine = (Element) NLOrderLinesIn.item(i);
			String strInPrimeLineNo = eleInOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
			eleOrderLineStatusUpdate = docOrderStatUpdate.createElement(KohlsXMLLiterals.E_ORDER_LINE);
			eleOrderLine = SCXmlUtil.getXpathElement(eleOrder, KohlsXMLLiterals.E_ORDER_LINES+"/"+KohlsXMLLiterals.E_ORDER_LINE+"[@"+KohlsXMLLiterals.A_PRIME_LINE_NO+"=\""+strInPrimeLineNo+"\"]");
			
			NamedNodeMap NNMOrderLineAtt = eleOrderLine.getAttributes();
			for (int j = 0; j < NNMOrderLineAtt.getLength(); j++) {
				Attr node = (Attr) NNMOrderLineAtt.item(j);
				eleOrderLineStatusUpdate.setAttributeNS(node.getNamespaceURI(), node.getName(), node.getValue());
			}
			eleOrderLineStatusUpdate.setAttribute(KohlsXMLLiterals.A_ITEM_ID, SCXmlUtil.getXpathAttribute(eleOrderLine, KohlsXMLLiterals.E_ITEM+"/@"+KohlsXMLLiterals.A_ITEM_ID));
			eleOrderLineStatusUpdate.setAttribute(KohlsXMLLiterals.A_UOM, SCXmlUtil.getXpathAttribute(eleOrderLine, KohlsXMLLiterals.E_ITEM+"/@"+KohlsXMLLiterals.A_UOM));
			
			SCXmlUtil.importElement(eleOrderLineStatusUpdate, SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_SHIPNODE));
			
			eleOrderStatuses = docOrderStatUpdate.createElement(KohlsXMLLiterals.E_ORDER_STATUSES);
			eleOrderStatus = docOrderStatUpdate.createElement(KohlsXMLLiterals.E_ORDER_STATUS);
			
			eleOrderStatus.setAttribute(KohlsXMLLiterals.A_SHIPNODE , SCXmlUtil.getXpathAttribute(eleOrderLine, KohlsXMLLiterals.E_SHIPNODE+"/@"+KohlsXMLLiterals.A_SHIPNODE));
			eleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS , eleInOrderLine.getAttribute(KohlsXMLLiterals.A_BASEDROPSTATUS));
			eleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_QTY , SCXmlUtil.getXpathAttribute(eleInOrderLine, KohlsXMLLiterals.E_ORDERLINE_TRAN_QTY+"/@"+KohlsXMLLiterals.A_QUANTITY));
			 
			//eleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_DATE , SCXmlUtil.getXpathAttribute(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES+"/"+KohlsXMLLiterals.E_ORDER_STATUS+"[@"+KohlsXMLLiterals.A_STATUS+"=\""+eleInOrderLine.getAttribute(KohlsXMLLiterals.A_BASEDROPSTATUS)+"\"]/@"+KohlsXMLLiterals.A_STATUS_DATE));
			//eleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_DESCRIPTION , SCXmlUtil.getXpathAttribute(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES+"/"+KohlsXMLLiterals.E_ORDER_STATUS+"[@"+KohlsXMLLiterals.A_STATUS+"=\""+eleInOrderLine.getAttribute(KohlsXMLLiterals.A_BASEDROPSTATUS)+"\"]/@"+KohlsXMLLiterals.A_STATUS_DESCRIPTION));
			
			eleOrderStatuses.appendChild(eleOrderStatus);
			eleOrderLineStatusUpdate.appendChild(eleOrderStatuses);
			eleOrderLines.appendChild(eleOrderLineStatusUpdate);
		}
				
		eleOrderStatUpdate.appendChild(eleOrderLines);
		KohlsCommonUtil.invokeService(env, KohlsConstant.SERVICE_KOHLS_SEND_RELEASE_STATUS_TO_ECOMM, docOrderStatUpdate);
		
		return inXML;
		
	}

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
