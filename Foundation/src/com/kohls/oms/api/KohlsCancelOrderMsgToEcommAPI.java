package com.kohls.oms.api;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

public class KohlsCancelOrderMsgToEcommAPI implements YIFCustomApi {
	
	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsOrderStatusSentToEcommAPI.class.getName());
	
	public KohlsCancelOrderMsgToEcommAPI() throws YIFClientCreationException {
		
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	public void setProperties(Properties arg0) throws Exception {
		
	}
	
	public void POLineCancelMsgToEcomm(YFSEnvironment env, Document inXML) throws YFSException, Exception ,RemoteException, TransformerException {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered POLineCancelMsgToEcomm ----------------> ");
		 }
		// get the OrderLineKey from the input XML to find the Related Purchase Order No. 
		
		YFCDocument yfcDocOrder= YFCDocument.getDocumentFor(inXML);	
		YFCElement eleOrder =   yfcDocOrder.getDocumentElement();
		YFCNodeList nodeOrderLine= eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		
		if (0 != nodeOrderLine.getLength()) {
			for (int i = 0; i < nodeOrderLine.getLength(); i++) {
				
				YFCElement eleOrderLine = eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE).item(i);
				String strOrderLineKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("OrderLineKey = " + strOrderLineKey );
				 }
				
				// calling a Private Method to Find the Related Chained Purhase Order No else Null
		        String strPONo = getPurchaseOrdNum(env , strOrderLineKey);
		        if (!strPONo.equals("")) {
		          // Appending the Purchase Order Attribute 
		          eleOrderLine.setAttribute(KohlsXMLLiterals.A_PURCHASE_ORDER_NO , strPONo);
					}
		        
		          }
			if (YFCLogUtil.isDebugEnabled()) {
    			log.debug("Output XML stamping PO Number : " + XMLUtil.getXMLString(yfcDocOrder.getDocument()));
    				}
			// Pushing the modified XML into the Cancel Msg Flow 
			try { 
				this.api.executeFlow(env , KohlsConstant.SERVICE_KOHLS_CANCEL_MSG_TO_ECOMM , yfcDocOrder.getDocument());
					}   catch (Exception e) {
				   		     e.printStackTrace();
				   		     throw e;
			               }
			  
			   
			   }
               
	}	
	
	private String getPurchaseOrdNum(YFSEnvironment env ,String OrderLineKey) throws RemoteException {
		 
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
		 String strOrderNo = eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
		 
		 if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Purchase Order Number  = " + strOrderNo );
			 }
	     return strOrderNo ;
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
				
		//System.out.println("getOrderList Template XML : " + XMLUtil.getXMLString(yfcDocGetOrderListTemp.getDocument()));
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getOrderList Tempalte : " + XMLUtil.getXMLString(yfcDocGetOrderListTemp.getDocument()));
		}

		return yfcDocGetOrderListTemp.getDocument();
	}

}
