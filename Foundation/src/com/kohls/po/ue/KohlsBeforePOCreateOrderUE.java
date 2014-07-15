package com.kohls.po.ue;

import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSBeforeCreateOrderUE;

/**
 * This user exit sets the value of Purchase Order number as SO_1, SO_2,..
 * before invoking the "createOrder" API.
 * 
 * @author Priyadarshini
 * 
 * Sets the ReceiptID value for PO as an extended attribute at order header level.
 * @author Sudeepth - Jan 16th 2011
 */
public class KohlsBeforePOCreateOrderUE implements YFSBeforeCreateOrderUE {
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KohlsBeforePOCreateOrderUE.class.getName());
	private YIFApi api;
	

	/**
	 * constructor initializes api
	 * 
	 * @throws YIFClientCreationException
	 */
	public KohlsBeforePOCreateOrderUE() throws YIFClientCreationException {

		api = YIFClientFactory.getInstance().getLocalApi();
	}

	
	/**
	 * This methods does the following 
	 * 1) Gets order no by calling getOrderDetails API
	 * 2) Gets total order list count by calling getOrderList API
	 * 3) Modify the inXML order no as orderno_totalorderlistcount
	 */
	public Document beforeCreateOrder(YFSEnvironment env, Document inXML) throws YFSUserExitException {
		log.verbose("<---------------Entered beforeCreateOrder Module--------------------------> ");
		
		
		NodeList nlOrderLine = inXML
				.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		Element elemOrderLine = (Element) nlOrderLine.item(0);
		// chained order key retrieved
		String strChainedFromOrderHeaderKey = elemOrderLine
				.getAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_HEADER_KEY);

		Document docGetOrderDetail = getOrderDetailsInputXml(strChainedFromOrderHeaderKey);

		Document docOrderDetails = getOrderDetails(env, docGetOrderDetail);
		// get order no
		String strOrderNumber = docOrderDetails.getDocumentElement()
				.getAttribute(KohlsXMLLiterals.A_ORDERNO);

		Document docGetOrderList = getOrderListInputXml(strChainedFromOrderHeaderKey);

		Document docOrderList = getOrderList(env, docGetOrderList);
		String strTotalOrderList = docOrderList.getDocumentElement()
				.getAttribute(KohlsXMLLiterals.A_TOTAL_ORDER_LIST);
		// get total order list
		Integer intTotalOrderList = Integer.valueOf(strTotalOrderList);
		
		// Increment the total order list attribute by 1 and append to order
		// number
		Element elemOrder = inXML.getDocumentElement();
		elemOrder.setAttribute(KohlsXMLLiterals.A_ORDERNO,	strOrderNumber + "_" + (++intTotalOrderList));
		 
		Element elemInputBillTo = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO).item(0).cloneNode(true);
		Element elemBillto = inXML.createElement(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO);
		elemBillto.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, elemInputBillTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_1));
		elemBillto.setAttribute(KohlsXMLLiterals.A_CITY, elemInputBillTo.getAttribute(KohlsXMLLiterals.A_CITY));
		elemBillto.setAttribute(KohlsXMLLiterals.A_STATE, elemInputBillTo.getAttribute(KohlsXMLLiterals.A_STATE));
		elemBillto.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, elemInputBillTo.getAttribute(KohlsXMLLiterals.A_ZIP_CODE));
		elemBillto.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, elemInputBillTo.getAttribute(KohlsXMLLiterals.A_FIRST_NAME));
		elemBillto.setAttribute(KohlsXMLLiterals.A_LAST_NAME, elemInputBillTo.getAttribute(KohlsXMLLiterals.A_LAST_NAME));
		elemBillto.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, elemInputBillTo.getAttribute(KohlsXMLLiterals.A_DAY_PHONE));		 
		elemOrder.appendChild(elemBillto);
		
		log.verbose("Create Order XML for PO : " + XMLUtil.getXMLString(inXML));
		log.verbose("<---------------Exit beforeCreateOrder Module--------------------------> ");
		return inXML;
	}


	private Document getOrderList(YFSEnvironment env, Document docGetOrderList) {
		Document docOrderList = null;
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_LIST,
				getOrderListTemplate());
		try {
			docOrderList = api.getOrderList(env, docGetOrderList);
		} catch (YFSException e) {
			log.verbose("<=====================Exception in PO Create Order UE===========================>"+ e.getMessage());
		} catch (RemoteException e) {
			log.verbose("<=====================Exception in PO Create Order UE===========================>"+ e.getMessage());
		}
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LIST);
		return docOrderList;
	}

	private Document getOrderDetails(YFSEnvironment env,
			Document docGetOrderDetail) {
		Document docOrderDetails = null;
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS,
				getOrderDetailsTemplate());
		try {
			docOrderDetails = api.getOrderDetails(env, docGetOrderDetail);
		} catch (YFSException e) {
			log.verbose("<=====================Exception in PO Create Order UE===========================>"+ e.getMessage());
		} catch (RemoteException e) {
			log.verbose("<=====================Exception in PO Create Order UE===========================>"+ e.getMessage());
		}
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);
		return docOrderDetails;
	}

	private Document getOrderListInputXml(String strChainedFromOrderHeaderKey) {
		YFCDocument yfcDocGetOrderList = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderList = yfcDocGetOrderList.getDocumentElement();
		YFCElement elemOrderLineOrderList = yfcEleGetOrderList.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		 
		elemOrderLineOrderList.setAttribute(
				KohlsXMLLiterals.A_CHAINED_FROM_ORDER_HEADER_KEY,
				strChainedFromOrderHeaderKey);
		return yfcDocGetOrderList.getDocument();
	}

	private Document getOrderDetailsInputXml(String strChainedFromOrderHeaderKey) {
		YFCDocument yfcDocGetOrderDetail = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderDetail = yfcDocGetOrderDetail
				.getDocumentElement();
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY,
				strChainedFromOrderHeaderKey);
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE,
				KohlsConstant.SO_DOCUMENT_TYPE);
		return yfcDocGetOrderDetail.getDocument();
	}

	public String beforeCreateOrder(YFSEnvironment arg0, String arg1) throws YFSUserExitException {

		return null;
	}

	private Document getOrderDetailsTemplate() {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, "");
		
		YFCElement yfcElemOrdereLines = yfcDocGetOrderDetailsTemp.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcElemOrdereLine = yfcDocGetOrderDetailsTemp.createElement(KohlsXMLLiterals.E_ORDER_LINE);
		yfcElemOrdereLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcElemOrdereLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcElemOrdereLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcElemOrdereLines.appendChild(yfcElemOrdereLine);
		
		YFCElement yfcElemBillto = yfcDocGetOrderDetailsTemp.createElement(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO);
		yfcElemBillto.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
		yfcElemBillto.setAttribute(KohlsXMLLiterals.A_CITY, "");
		yfcElemBillto.setAttribute(KohlsXMLLiterals.A_STATE, "");
		yfcElemBillto.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");
		yfcElemBillto.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "");
		yfcElemBillto.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "");
		yfcElemBillto.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, "");
		yfcElemOrderDetailsTemp.appendChild(yfcElemBillto);
		return yfcDocGetOrderDetailsTemp.getDocument();
	}

	private Document getOrderListTemplate() {
		YFCDocument yfcDocGetOrderListTemp = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDERLIST);
		YFCElement yfcElemGetorderListTemp = yfcDocGetOrderListTemp
				.getDocumentElement();
		yfcElemGetorderListTemp.setAttribute(
				KohlsXMLLiterals.A_TOTAL_ORDER_LIST, "");

		YFCElement yfElemOrderTemp = yfcDocGetOrderListTemp
				.createElement(KohlsXMLLiterals.E_ORDER);
		yfElemOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfElemOrderTemp.setAttribute(KohlsXMLLiterals.A_STATUS, "");

		yfcElemGetorderListTemp.appendChild(yfElemOrderTemp);

		return yfcDocGetOrderListTemp.getDocument();

	}

}
