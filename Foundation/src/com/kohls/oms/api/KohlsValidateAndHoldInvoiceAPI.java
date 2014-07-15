package com.kohls.oms.api;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
/**
 * This class is called on order line cancellation and confirm shipment.
 * It checks if the order is in shipped status and if any of the lines are canceled, if yes
 * then puts hold on order and non-invoiced shipments if any.
 * @author Priyadarshini
 *
 */
public class KohlsValidateAndHoldInvoiceAPI implements YIFCustomApi {
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsValidateAndHoldInvoiceAPI.class.getName());
	private  YIFApi api;

	public KohlsValidateAndHoldInvoiceAPI() throws YIFClientCreationException {
		api = YIFClientFactory.getInstance().getApi();

	}



	public void validateInfo(YFSEnvironment env, Document inXML) throws YFSException, RemoteException{
		String strOrderNo = null;

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<---------- Begining of validateInfo---------->");
		}

		String strDocumentType = KohlsConstant.SO_DOCUMENT_TYPE;
		String strEnterpriseCode = "" ;

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Input XML : " + XMLUtil.getXMLString(inXML));
		}

		if(inXML.getDocumentElement().getTagName().equals(KohlsXMLLiterals.E_ORDER)){
			// line canceled
			//get values from order XML
			Element elemOrder =(Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
			strOrderNo = elemOrder.getAttribute(KohlsXMLLiterals.A_ORDERNO);

			strEnterpriseCode = elemOrder.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);

		}else{
			//line shipped
			//get values from shipment XML
			Element elemShipment =(Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT).item(0);

			strEnterpriseCode = elemShipment.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);

			Element elemShipmentLines =(Element)elemShipment.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINES).item(0);
			Element elemShipmentLine =(Element)elemShipmentLines.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE).item(0);
			strOrderNo = elemShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDERNO);

		}
		changeOrder(env, strOrderNo, strDocumentType, strEnterpriseCode, inXML);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<---------- End of validateInfo---------->");
		}
	}

	//Added below method as part of Defect-118 by Ravi
	private boolean shippedStatChkNonBOPUSLines(YFSEnvironment env, Document docOrderDetails) {

		boolean shippedStatFlagNonBOPUSLines = true;
		log.debug("***** Order Details XML is *****  " + XMLUtil.getXMLString(docOrderDetails));
		Element elemOrderLines = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES).item(0);
		NodeList nlOrderLines = elemOrderLines.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		for(int j=0; j < nlOrderLines.getLength(); j++){
			Element elemOrderLine = (Element)nlOrderLines.item(j);
			String strDeliveryMethod = elemOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
			if ( !(strDeliveryMethod.equalsIgnoreCase(KohlsConstant.PICK)) ) {
				String strNonBOPUSLineStat = elemOrderLine.getAttribute(KohlsXMLLiterals.A_STATUS);
				if (!(KohlsConstant.V_SHIPPED_STATUS.equalsIgnoreCase(strNonBOPUSLineStat) || KohlsConstant.CANCELLED.equalsIgnoreCase(strNonBOPUSLineStat)) ) {
					shippedStatFlagNonBOPUSLines= false;
					break;
				}
			}
		}
		return shippedStatFlagNonBOPUSLines;
	}

	private void changeOrder(YFSEnvironment env, String strOrderNo,
			String strDocumentType, String strEnterpriseCode, Document inXML)
	throws RemoteException {
		Document docOrderDetails;
		boolean isRepricingReq = false;

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<---------- Begining of changeOrderShipment---------->");
		}

		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS,
				getOrderDetailsTemplate());

		docOrderDetails = api.getOrderDetails(env, getOrderInputXML(strOrderNo, strEnterpriseCode, strDocumentType));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("getOrderDetails Output XML \n" + XMLUtil.getXMLString(docOrderDetails));
		}

		Element elemOrder = docOrderDetails.getDocumentElement();

		//Changes done as part of Defect-118 by Ravi
		boolean shippedStatFlagNonBOPUSLines = shippedStatChkNonBOPUSLines(env, docOrderDetails );
		//String strMinOrderStatus = elemOrder.getAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS);

		String sPaymentRuleId = elemOrder.getAttribute(KohlsXMLLiterals.A_PAYMENT_RULE_ID);
		Map<String, String> orderLineKeyQtyMap = getOrderLineKeyQtyMap(docOrderDetails);
		// check if order is in shipped state

		/*
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Minimum Order Stauts : " + strMinOrderStatus);
		}
		if(!strMinOrderStatus.equals(KohlsConstant.ORDER_SHIPPED)){
			return;
		}*/

		//Changes done as part of Defect-118 by Ravi
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Non BOPUS Order lines shipped status check flag  : " + shippedStatFlagNonBOPUSLines);
		}
		if(!shippedStatFlagNonBOPUSLines){
			return;
		}


		if(KohlsConstant.PAYMENT_RULE_NO_AUTH.equalsIgnoreCase(sPaymentRuleId) || sPaymentRuleId.equals("")){

			YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
			YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
			yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_KOHLS_CLOSE_ORDER);
			yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderNo);
			yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
			yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);
			yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KohlsConstant.AWAITING_ORDER_CLOSURE);

			api.changeOrderStatus(env, yfcDocChangeOrderStatus.getDocument());

			return;
		}

		Element yfcElemOrdereStatuses = (Element)elemOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUSES).item(0);
		NodeList nlOrdereStatus = yfcElemOrdereStatuses.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUS);
		for(int i = 0; i<nlOrdereStatus.getLength(); i++){
			Element elemStatus = (Element)nlOrdereStatus.item(i);

			// check for canceled order lines and original qty is not 0
			if(elemStatus.getAttribute(KohlsXMLLiterals.A_STATUS).equals("9000") &&
					!orderLineKeyQtyMap.get(elemStatus.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY)).equals("0.00")){
				isRepricingReq = true;
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("Order has a cancellation, putting the order on Re-Pricing Hold");
				}
				api.changeOrder(env, getChangeOrderinput(strOrderNo,strDocumentType,strEnterpriseCode));
				break;
			}

		}

		if (!isRepricingReq) {

			boolean bHoldExist = false;
			NodeList ndlstOrderHoldTypes = elemOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);

			for(int i=0;i<ndlstOrderHoldTypes.getLength();i++){
				Element eleOrderHoldType = (Element)ndlstOrderHoldTypes.item(i);
				String sHoldType = eleOrderHoldType.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE);
				String sHoldStatus = eleOrderHoldType.getAttribute(KohlsXMLLiterals.A_STATUS);

				if((KohlsConstant.CASH_ACTIVATION_HOLD.equalsIgnoreCase(sHoldType)
						&& KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)) ||
						(KohlsConstant.OOB_HOLD.equalsIgnoreCase(sHoldType)
								&& KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)) ||
								(KohlsConstant.ECOMM_HOLD.equalsIgnoreCase(sHoldType)
										&& KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus))){
					bHoldExist = true ;
					break;
				}
			}

			if(!bHoldExist){
				// change Order Status to 'Awaiting Invoice Creation'
				api.changeOrderStatus(env, getChangeOrderStatusInpt(strOrderNo,strDocumentType,strEnterpriseCode));
			}
		}

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<---------- End of changeOrderShipment---------->");
		}
	}

	private Document getChangeOrderStatusInpt(String strOrderNo, String strDocumentType, String strEnterpriseCode) {
		YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
		YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_INCLUDE_INVOICE_0001_EX);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderNo);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KohlsConstant.AWAITING_INVOICE_CREATION);

		return yfcDocChangeOrderStatus.getDocument();
	}

	private Map<String, String> getOrderLineKeyQtyMap(Document docOrderDetails) {
		Map<String, String> orderLineKeyQtyMap = new HashMap<String, String>();
		Element elemOrderLines = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES).item(0);
		NodeList nlOrderLine =  elemOrderLines.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		for(int i =0; i<nlOrderLine.getLength(); i++){
			Element elemOrderLine = (Element)nlOrderLine.item(i);
			orderLineKeyQtyMap.put(elemOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY),
					elemOrderLine.getAttribute(KohlsXMLLiterals.A_ORIGINAL_ORDERED_QUANTITY));

		}
		return orderLineKeyQtyMap;
	}

	private Document getChangeOrderinput(String strOrderNo, String strDocumentType, String strEnterpriseCode) {
		YFCDocument yfcDocOrder = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrder = yfcDocOrder.getDocumentElement();
		yfcElemOrder.setAttribute(KohlsXMLLiterals.A_IGNORE_ORDERING, KohlsConstant.YES);
		yfcElemOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
		yfcElemOrder.setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderNo);
		yfcElemOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
		yfcElemOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);

		YFCElement yfcElemHoldTypes = yfcElemOrder.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
		YFCElement yfcElemHoldType = yfcElemHoldTypes.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		yfcElemHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, KohlsConstant.INVOICE_HOLD_INDICATOR);
		yfcElemHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, KohlsConstant.HOLD_INVOICE);
		yfcElemHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, KohlsConstant.HOLD_CREATED_STATUS);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("changeOrder Input \n" + XMLUtil.getXMLString(yfcDocOrder.getDocument()));
		}

		return yfcDocOrder.getDocument();
	}

	private Document getOrderDetailsTemplate() {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS, "");
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_PAYMENT_RULE_ID, "");

		YFCElement yfcElemOrdereStatuses = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_STATUSES);
		YFCElement yfcElemOrdereStatus = yfcElemOrdereStatuses.createChild(KohlsXMLLiterals.E_ORDER_STATUS);
		yfcElemOrdereStatus.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcElemOrdereStatus.setAttribute(KohlsXMLLiterals.A_STATUS, "");

		YFCElement yfcElemOrdereLines = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcElemOrdereLine = yfcElemOrdereLines.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcElemOrdereLine.setAttribute(KohlsXMLLiterals.A_ORIGINAL_ORDERED_QUANTITY, "");
		yfcElemOrdereLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");

		//Changes Defect-118 by Ravi
		yfcElemOrdereLine.setAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD, "");
		yfcElemOrdereLine.setAttribute(KohlsXMLLiterals.A_STATUS, "");

		YFCElement yfcEleOrderHoldTypes = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
		YFCElement yfcEleOrderHoldType = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, "");
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleOrderHoldTypes.appendChild(yfcEleOrderHoldType);
		yfcElemOrderDetailsTemp.appendChild(yfcEleOrderHoldTypes);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("getOrderDetails Template \n" + XMLUtil.getXMLString(yfcDocGetOrderDetailsTemp.getDocument()));
		}

		return yfcDocGetOrderDetailsTemp.getDocument();
	}

	public Document getOrderInputXML(String strOrderNo, String strEnterpriseCode, String strDocumentType){
		YFCDocument yfcDocOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderNo);
		yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
		yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("getOrderDetails Input \n" + XMLUtil.getXMLString(yfcDocOrder.getDocument()));
		}

		return yfcDocOrder.getDocument();
	}


	@Override
	public void setProperties(Properties arg0) throws Exception {


	}

}
