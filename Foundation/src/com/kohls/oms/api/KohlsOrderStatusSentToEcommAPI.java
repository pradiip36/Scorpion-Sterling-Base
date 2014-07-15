/**
 * 
 */
package com.kohls.oms.api;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Vijay Kumar 
 *
 */
public class KohlsOrderStatusSentToEcommAPI implements YIFCustomApi {
	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsOrderStatusSentToEcommAPI.class.getName());
	
	
	public KohlsOrderStatusSentToEcommAPI() throws YIFClientCreationException {
		// TODO Auto-generated constructor stub
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	public void setProperties(Properties arg0) throws Exception {
		
	}
	
	public Document createStatusSentToEcomm(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, TransformerException {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered Send OrderStatus Method to EComm Module----------------> ");
		 }
		// get the OrderLineKey from the input XML to find the Related Purchase Order No. 
		
		YFCDocument yfcDocOrder= YFCDocument.getDocumentFor(inXML);	
		YFCElement eleOrder =   yfcDocOrder.getDocumentElement();
		YFCNodeList nodeOrderLine= eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		
		
		YFCElement eleOrderLine = (YFCElement) nodeOrderLine.item(0);
		String strOrderLineKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
		Map<String, String> map = this.getPurchaseOrdNum(env, strOrderLineKey);
        String strPONo = (String) map.get("PurchaseOrderNum");
        String receiptID = (String) map.get("ReceiptID");
		if (0 != nodeOrderLine.getLength()) {
			for (int i = 0; i < nodeOrderLine.getLength(); i++) {

				YFCElement elemOrderLine = (YFCElement) nodeOrderLine.item(i);
				
				// Removing the OrderLineKey attribute from the inXML
				elemOrderLine.removeAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				elemOrderLine.setAttribute(KohlsXMLLiterals.A_PURCHASE_ORDER_NO,
						strPONo);
				elemOrderLine.setAttribute(KohlsXMLLiterals.A_RECEIPT_ID, receiptID);
			}
		}
        
        if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Modified XML to be sent to EComm " + XMLUtil.getXMLString(yfcDocOrder.getDocument()));
		 }
        
        return yfcDocOrder.getDocument();
        
        
		
	}		
	
	
	 private Map<String, String> getPurchaseOrdNum(YFSEnvironment env ,String OrderLineKey) throws RemoteException, YFSException, TransformerException {
		 
	     // Private Method to get the PO Num for chainedOrderLine
		 Document OrderListInputXML = getOrderListInputXML(OrderLineKey);
		 if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input XML to getOrderList = " + XMLUtil.getXMLString(OrderListInputXML) );
		 }
		 
		 // set the API template for getOrderList and then calling it . This will fetch the OrderList with Purchase Order No. as OrderNo=""
		 env.setApiTemplate(KohlsConstant.API_GET_ORDER_LIST , this.getOrderListTemplate());
		 Document docOutputGetOrderList = this.api.getOrderList(env, OrderListInputXML );
	     env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LIST);
	     
	     // start of Order broker to get Purchase Order No.
	     Element eleOrder = (Element) docOutputGetOrderList.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		 String strPONo = eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
				 
		 Map<String, String> mapPONoReceiptID = new HashMap<String, String>();
		 mapPONoReceiptID.put("PurchaseOrderNum", strPONo);
		 mapPONoReceiptID.put("ReceiptID", getPOReceiptId(env, strPONo));
		 if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Purchase Order Number  = " + strPONo );
			 }
	     return mapPONoReceiptID ;
		 }
	 private String  getPOReceiptId(YFSEnvironment env, String strPOOrderNo) throws YFSException, RemoteException, TransformerException {
			env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS,
					getPOOrderDetailsTemplate());
			Document docOrderDetails = api.getOrderDetails(env, getOrderInputXML(strPOOrderNo, KohlsConstant.KOHLS_ENTERPRISE_CODE, 
					KohlsConstant.PO_DOCUMENT_TYPE));
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);		
			
			// get Order Details
			Element elemChainedFromOrderLine = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_CHAINED_FROM_ORDER_LINE).item(0);
			Element elemChainedOrder = (Element)elemChainedFromOrderLine.getFirstChild();
			String sSOOrderNo = elemChainedOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
			
			env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getSOOrderDetailsTemp());
			Document docSOOrderDetails = api.getOrderDetails(env,
					getOrderInputXML(sSOOrderNo, KohlsConstant.KOHLS_ENTERPRISE_CODE, KohlsConstant.SO_DOCUMENT_TYPE));
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);			
			
			return  getReceiptId(KohlsConstant.DSV_NODE_RECEIPT_ID, docSOOrderDetails)	;	 
		}
	 private String getReceiptId(String sEFC, Document docGetOrderDetials) throws RemoteException, TransformerException {
			// getOrderReleaseDetails 		
			Element elemNotes = (Element) docGetOrderDetials.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
			NodeList nlNote = elemNotes.getElementsByTagName(KohlsXMLLiterals.E_NOTE);
			for(int i=0; i< nlNote.getLength(); i++){
				Element elemNote = (Element) nlNote.item(i);
				if(elemNote.getAttribute(KohlsXMLLiterals.A_REASON_CODE).equals(KohlsConstant.PREENC+sEFC)){				
					return elemNote.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);				
				}
			}
			return null;		
		}
	 private Document getPOOrderDetailsTemplate() {
			YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
					.createDocument(KohlsXMLLiterals.E_ORDER);
			YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
			yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, "");
			YFCElement yfcElemOrderLines = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_LINES);
			YFCElement yfcElemOrderLine = yfcElemOrderLines.createChild(KohlsXMLLiterals.E_ORDER_LINE);
			yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
			
			// chained order
			YFCElement yfcElemChainedFromOrderLine = yfcElemOrderLine.createChild(KohlsXMLLiterals.E_CHAINED_FROM_ORDER_LINE);
			yfcElemChainedFromOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
			YFCElement yfcElemChainedOrder = yfcElemChainedFromOrderLine.createChild(KohlsXMLLiterals.E_ORDER);
			yfcElemChainedOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, "");		
			return yfcDocGetOrderDetailsTemp.getDocument();
		}
		
		public Document getOrderInputXML(String strOrderHeaderKey, String strEnterpriseCode, String strDocumentType){		
			YFCDocument yfcDocOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
			yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderHeaderKey);
			yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
			yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);
			return yfcDocOrder.getDocument();
		}
	 private Document getOrderListInputXML(String strOrderLineKey ) {
		 
			YFCDocument yfcDocGetOrderList = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
			YFCElement yfcEleOrderList = yfcDocGetOrderList.getDocumentElement();
			YFCElement yfcEleOrderLine = yfcEleOrderList.createChild(KohlsXMLLiterals.E_ORDER_LINE);
			yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_LINE_KEY,strOrderLineKey);
			return yfcDocGetOrderList.getDocument();
			}
	
	 private Document getOrderListTemplate() {

			YFCDocument yfcDocGetOrderListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDERLIST);
			YFCElement yfcEleGetOrderListTemp = yfcDocGetOrderListTemp.getDocumentElement();			
			YFCElement yfcEleOrder = yfcEleGetOrderListTemp.createChild(KohlsXMLLiterals.E_ORDER);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, "");
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, "");
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");					
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("getOrderList Tempalte : " + XMLUtil.getXMLString(yfcDocGetOrderListTemp.getDocument()));			}

			return yfcDocGetOrderListTemp.getDocument();
	}
	 
	private Document getSOOrderDetailsTemp() {

			YFCDocument yfcDocGetOrderDetailTemp = YFCDocument
			.createDocument(KohlsXMLLiterals.E_ORDER);
			YFCElement yfcEleGetOrderDetail = yfcDocGetOrderDetailTemp
					.getDocumentElement();				 
			//for receipt id 
			YFCElement yfcElemNotes = yfcEleGetOrderDetail.createChild(KohlsXMLLiterals.E_NOTES);
			YFCElement yfcElemNote = yfcElemNotes.createChild(KohlsXMLLiterals.E_NOTE);
			yfcElemNote.setAttribute(KohlsXMLLiterals.A_REASON_CODE, "");
			yfcElemNote.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");		
				
			return yfcDocGetOrderDetailTemp.getDocument();		
	}
	 
}


