package com.kohls.po.api;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
/**
 * This class cancels sales order line  when a PO cancel is received from the DSV.
 * This class is used by KohlsCancelSOOnPOCancel service
 * @author Priyadarshini
 *
 */
public class KohlsCancelSalesOrderAPI implements YIFCustomApi {
	
	
	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsCancelSalesOrderAPI.class.getName());

	/**
	 * constructor  initializes api
	 * 
	 * @throws YIFClientCreationException
	 *              
	 */
	public KohlsCancelSalesOrderAPI() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	/**
	 * This method is called by the  KohlsCancelSOOnPOCancel service.
	 * Cancels sales order line  when a PO cancel is received from the DSV.
	 * @param env
	 * @param inXml
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws TransformerException 
	 */
	public void cancelSalesOrder(YFSEnvironment env, Document inXml) throws YFSException, RemoteException, TransformerException{		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered cancelSalesOrder Module--------------------------> ");
		}
		
		this.log.debug("MsgToEcommAPI: " + XMLUtil.getXMLString(inXml));
		
		// get input XML for getOrderLineDetails API 
		// get all cancel lines
		
		Element eleOrder = inXml.getDocumentElement();
		String poOrderNo = eleOrder.getAttribute(KohlsXMLLiterals.A_ORDERNO);
		// set the PO order no for msg to ecomm
		env.setTxnObject("POOrderNo", poOrderNo);
		NodeList nodeListOrderlLine = inXml
				.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES).item(0).getChildNodes();
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS, getOrderLineDetailsTemplate());
		for (int i = 0; i < nodeListOrderlLine.getLength(); i++) {
			// get input for getOrderLineDetails API for each cancel line
			Element elemOrderLine = (Element)nodeListOrderlLine.item(i);
			Document inXMLGetOrderLineDetails = getInputXMLForGetOrderLineDetails(elemOrderLine);
			if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input to getOrderDetails API"+ KohlsUtil.extractStringFromDocument(inXMLGetOrderLineDetails));
			}
			// call getOrderLineDetails API
			Document docOrderLineDetails = api.getOrderLineDetails(env, inXMLGetOrderLineDetails); 
			if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Output  of getOrderDetails API"+ KohlsUtil.extractStringFromDocument(docOrderLineDetails));
			}
			// get changeOrder input
			Document docChangeOrderInput = getInputForChangeOrder(docOrderLineDetails);
			
			if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input for  changeOrder API"+ KohlsUtil.extractStringFromDocument(docChangeOrderInput));
			}
			// call change order API
			api.changeOrder(env, docChangeOrderInput);
		}
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS);		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Exit cancelSalesOrder Module--------------------------> ");
		}
	}
	
	
	
	/**
	 * This method is called by the  KohlsCancelSOOnPOCancel service.
	 * Stamps the PO OrderNo
	 * @param env
	 * @param inXml
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws TransformerException 
	 */
	public Document OrderStatusToEcomm(YFSEnvironment env, Document inXml) throws YFSException, RemoteException, TransformerException{		
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered OrderStatusToEcomm--------------------------> ");
			this.log.debug("OrderStatusToEcomm input XML: " + XMLUtil.getXMLString(inXml));
		}		
		
		String poOrderNo = null;
		Element eleOrder = inXml.getDocumentElement();
		poOrderNo = (String) env.getTxnObject("POOrderNo");
		
		if(null!=poOrderNo && !poOrderNo.equals("")){
			NodeList ndOrderLines = eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES);
			if(ndOrderLines.getLength()>0){
				Element eleOrderLines = (Element) ndOrderLines.item(0);
				NodeList ndOrderLine = eleOrderLines.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
				if(ndOrderLine.getLength()>0){
					Element eleOrderLine = (Element) ndOrderLine.item(0);
					eleOrderLine.setAttribute(KohlsXMLLiterals.A_PURCHASE_ORDER_NO, poOrderNo);			
				}
			}
		}		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Exiting OrderStatusToEcomm--------------------------> ");
			this.log.debug("OrderStatusToEcomm output XML: " + XMLUtil.getXMLString(inXml));
		}
		return inXml;
	}
	
	private Document getInputForChangeOrder(Document docOrderLineDetails) {
		Element docOrderDetails = docOrderLineDetails.getDocumentElement();
		String strChainedOrderHeaderKey = docOrderDetails.getAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_HEADER_KEY);
		String strChainedOrderLineKey = docOrderDetails.getAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_LINE_KEY);
		
		YFCDocument yfcDocChangeOrderInput = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemChangeOrderInput = yfcDocChangeOrderInput.getDocumentElement();
		yfcElemChangeOrderInput.setAttribute(KohlsXMLLiterals.A_ACTION,  KohlsConstant.ACTION_MODIFY) ;
		yfcElemChangeOrderInput.setAttribute(KohlsXMLLiterals.A_OVERRIDE,  KohlsConstant.YES) ;
		yfcElemChangeOrderInput.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE,  KohlsConstant.SO_DOCUMENT_TYPE) ;
		yfcElemChangeOrderInput.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strChainedOrderHeaderKey) ;
		
		YFCElement yfcElemOrderLines = yfcElemChangeOrderInput.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		
		YFCElement yfcElemOrderLine = yfcElemOrderLines.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, strChainedOrderLineKey);
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_ACTION,  KohlsConstant.ACTION_CANCEL);		
		return yfcDocChangeOrderInput.getDocument();
	}

	private Document getInputXMLForGetOrderLineDetails(Node inXml) {
		YFCDocument yfcDocGetOrderDetailsInput = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER_LINE_DETAIL);		
		yfcDocGetOrderDetailsInput.getDocumentElement().setAttribute(
				KohlsXMLLiterals.A_ORDER_LINE_KEY,
				inXml.getAttributes().getNamedItem(KohlsXMLLiterals.A_ORDER_LINE_KEY).getNodeValue());
		return yfcDocGetOrderDetailsInput.getDocument();
	}

	private Document getOrderLineDetailsTemplate() {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER_LINE);
		YFCElement yfcElemOrderDetails = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetails.setAttribute(
				KohlsXMLLiterals.A_CHAINED_FROM_ORDER_HEADER_KEY, "");
		yfcElemOrderDetails.setAttribute(
				KohlsXMLLiterals.A_CHAINED_FROM_ORDER_LINE_KEY, "");
		YFCElement yfcElemOrder = yfcElemOrderDetails.createChild(KohlsXMLLiterals.E_ORDER);
		yfcElemOrder.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, "");
		 
		return yfcDocGetOrderDetailsTemp.getDocument();
	}
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		 
	}
}
