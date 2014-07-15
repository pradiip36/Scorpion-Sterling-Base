package com.kohls.oms.api;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.log.LogUtil;
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
 * This is a custom API for handling OMS out of balance scenario.
 * This is called on PaymentCollection 'On payment status change event'
 * 
 * @author Priyadarshini
 *
 */
public class KohlsOOBHoldAPI implements YIFCustomApi{

	private static final YFCLogCategory log = YFCLogCategory.instance(
			KohlsOOBHoldAPI.class.getName());
	private  YIFApi api;

	public KohlsOOBHoldAPI() throws YIFClientCreationException{

		api = YIFClientFactory.getInstance().getLocalApi();

	}


	public void processOOBHold(YFSEnvironment env, Document inXML) throws YFSException, RemoteException{

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Payment Collection : " + XMLUtil.getXMLString(inXML));
		}
		Element elemOrder = inXML.getDocumentElement();
		String sOrderHeaderKey = elemOrder.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		String sPaymentStatus = elemOrder.getAttribute(KohlsXMLLiterals.A_PAYMENT_STATUS);
		String sMinOrderStatus = elemOrder.getAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Minimum Order Status : " + sMinOrderStatus);
		}

		if(Double.parseDouble(KohlsConstant.AWAITING_INVOICE_CREATION)> Double.parseDouble(sMinOrderStatus)){
			// getOrderDetails

			env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemp());

			Document docGetOrderDetails = api.getOrderDetails(env, getOrderInputXML(sOrderHeaderKey));
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("getOrderDetails Output XML : " + XMLUtil.getXMLString(docGetOrderDetails));
			}
			boolean bOOBHoldExist = doesOOBHoldExist(docGetOrderDetails);
			//Added to overcome to Hold happening after cancellation and Invoicing due the Await payment Info status
			
			/************* START *******************/
			Element eleGetOrderDetails = docGetOrderDetails.getDocumentElement();
			Element eleGetChargeTransactionDetails = (Element)eleGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAILS).item(0);
			log.debug("eleGetChargeTransactionDetails xml is :"+XMLUtil.getElementXMLString(eleGetChargeTransactionDetails));
			String sTotalCredits = eleGetChargeTransactionDetails.getAttribute(KohlsXMLLiterals.A_TOTAL_CREDITS);
			String sTotalAuthAmount = eleGetChargeTransactionDetails.getAttribute(KohlsXMLLiterals.A_TOTAL_OPEN_AUTHORIZATION);

			Element elePriceInfo = (Element)eleGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_PRICE_INFO).item(0);
			String sTotalAmount = elePriceInfo.getAttribute(KohlsXMLLiterals.A_TOTAL_AMOUNT);
			BigDecimal bgTotalCredits = new BigDecimal(sTotalCredits);
			BigDecimal bgTotalAuthAmount = new BigDecimal(sTotalAuthAmount);
			BigDecimal bgTotal = bgTotalAuthAmount.add(bgTotalCredits);
			
			//added by Baijayanta
			NodeList nchargeTransactionDetails = docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAIL);
			double dReqAmount = 0.00;
			for(int k= 0; k < nchargeTransactionDetails.getLength() ; k++) {
				
				Element eleChargeTransDetail = (Element)nchargeTransactionDetails.item(k);
				String aChargeType = eleChargeTransDetail.getAttribute("ChargeType");
				if("CHARGE".equalsIgnoreCase(aChargeType)) {
					String aRequestAmount = eleChargeTransDetail.getAttribute("RequestAmount");
					BigDecimal bReqAmount = new BigDecimal(aRequestAmount);
					dReqAmount = dReqAmount + Double.parseDouble(bReqAmount.toString());	
					
				}
				
			}
			
			//ended by Baijayanta

			double dTotalAmountAuthorized = Double.parseDouble(bgTotal.toString());			
			double dTotalOrderAmount = Double.parseDouble(sTotalAmount);
			
			double totToCompare = dTotalAmountAuthorized + dReqAmount;
			
			/*BigDecimal bReqAmt = new BigDecimal(dReqAmount);
			BigDecimal bTotalToCompare = bgTotal.add(bReqAmt);
			
			BigDecimal bTotAmt = new BigDecimal(dTotalOrderAmount);*/
			
			BigDecimal bTotAmt = new BigDecimal(dTotalOrderAmount);
			
			BigDecimal bOne = new BigDecimal(totToCompare);
			
			
			/************* END *****************/
			
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("dTotalAmountAuthorized : " + dTotalAmountAuthorized);
				log.debug("dTotalOrderAmount : " + dTotalOrderAmount);
				log.debug("dReqAmount :" + dReqAmount);
			}
			//if((dTotalOrderAmount > dTotalAmountAuthorized) && !bOOBHoldExist){
			//added by Baijayanta
			//if((dTotalOrderAmount > (dTotalAmountAuthorized + dReqAmount) ) && !bOOBHoldExist){
			if((bTotAmt.compareTo(bOne) > 0)  && !bOOBHoldExist){
			//Ended by Baijayanta
				//	if(sPaymentStatus.equals(KohlsConstant.AWAIT_PAY_INFO) && !bOOBHoldExist){
				// put OOB Hold when order total > tender total
				log.debug("inside if condition:");
				Document docChangeOrder = createOOBHoldDoc(sOrderHeaderKey, KohlsConstant.HOLD_CREATED_STATUS);
				log.debug("the hold doc is:"+XMLUtil.getXMLString(docChangeOrder));
				api.changeOrder(env, docChangeOrder);
			}
			if(sPaymentStatus.equals(KohlsConstant.PAYMENT_STATUS_AUTH) && bOOBHoldExist){
				// remove OOB Hold when order total is <= tender total
				log.debug("inside payment status auth check");
				Document docChangeOrder = createOOBHoldDoc(sOrderHeaderKey, KohlsConstant.HOLD_REMOVED_STATUS);
				api.changeOrder(env, docChangeOrder);
				String strMinOrderStatus = elemOrder.getAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS);	
				if(!strMinOrderStatus.equals(KohlsConstant.ORDER_SHIPPED)){
					return;
				}			
				// if order is Shipped completely and no hold exist 
				// move to Awaiting Invoice Creation status
				if(!doesHoldExist(docGetOrderDetails)){
					// change Order Status to 'Awaiting Invoice Creation'
					api.changeOrderStatus(env, getChangeOrderStatusInpt(sOrderHeaderKey));
				}
			}
		}

	}

	private Document getChangeOrderStatusInpt(String sOrderHeaderKey) {		
		YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
		YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_INCLUDE_INVOICE_0001_EX);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KohlsConstant.AWAITING_INVOICE_CREATION);
		return yfcDocChangeOrderStatus.getDocument();
	}

	private boolean doesHoldExist(Document docGetOrderDetails) {
		boolean bHoldExist = false;
		NodeList ndlstOrderHoldTypes = docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		for(int i=0;i<ndlstOrderHoldTypes.getLength();i++){
			Element eleOrderHoldType = (Element)ndlstOrderHoldTypes.item(i);
			String sHoldType = eleOrderHoldType.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE);
			String sHoldStatus = eleOrderHoldType.getAttribute(KohlsXMLLiterals.A_STATUS);

			if((KohlsConstant.CASH_ACTIVATION_HOLD.equalsIgnoreCase(sHoldType) 
					&& KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)) ||
					(KohlsConstant.INVOICE_HOLD_INDICATOR.equalsIgnoreCase(sHoldType) 
							&& KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)) ||
							(KohlsConstant.ECOMM_HOLD.equalsIgnoreCase(sHoldType) 
									&& KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus))){
				bHoldExist = true ;
				break;
			}					
		}
		return bHoldExist;
	}

	private  Document  createOOBHoldDoc(String sOrderHeaderKey, String holdStatus) {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);		 
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);	
		YFCElement yfcEleOrderHoldTypes = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
		YFCElement yfcEleOrderHoldType = yfcEleOrderHoldTypes.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, KohlsConstant.OOB_HOLD);
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, holdStatus);
		if(holdStatus.equals(KohlsConstant.HOLD_CREATED_STATUS)){
			yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, "Placing OOB Hold");
		}else{
			yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, "Resolving OOB Hold");
		}
		return yfcDocGetOrderDetailsTemp.getDocument();		
	}


	private boolean doesOOBHoldExist(Document docGetOrderDetails) {
		NodeList nlHoldType = docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		for(int i =0; i< nlHoldType.getLength(); i++){
			Element elemHoldType = (Element) nlHoldType.item(i);
			String sHoldType = elemHoldType.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE);
			String sHoldStatus = elemHoldType.getAttribute(KohlsXMLLiterals.A_STATUS);
			if(KohlsConstant.OOB_HOLD.equalsIgnoreCase(sHoldType) 
					&& KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)){
				return true ;				
			}				
		}
		return false;			
	}


	private Document getOrderDetailsTemp() {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");		 

		YFCElement yfcElePriceInfo = yfcDocGetOrderDetailsTemp.createElement(KohlsXMLLiterals.E_PRICE_INFO);
		yfcElePriceInfo.setAttribute(KohlsXMLLiterals.A_TOTAL_AMOUNT, "");
		yfcElemOrderDetailsTemp.appendChild(yfcElePriceInfo);

		YFCElement yfcEleChargeTranDetails = yfcDocGetOrderDetailsTemp.createElement(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAILS);
		yfcEleChargeTranDetails.setAttribute(KohlsXMLLiterals.A_TOTAL_CREDITS, "");
		yfcEleChargeTranDetails.setAttribute(KohlsXMLLiterals.A_TOTAL_OPEN_AUTHORIZATION, "");
		yfcElemOrderDetailsTemp.appendChild(yfcEleChargeTranDetails);

		YFCElement yfcEleChargeTranDetail = yfcEleChargeTranDetails.createChild(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAIL);
		yfcEleChargeTranDetail.setAttribute(KohlsXMLLiterals.A_AUTHORIZATION_ID, "");
		yfcEleChargeTranDetail.setAttribute("ChargeType", "");
		//yfcEleChargeTranDetail.setAttribute("ChargeType", "");
		yfcEleChargeTranDetail.setAttribute("RequestAmount", "");
		

		YFCElement yfcEleOrderHoldTypes = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
		YFCElement yfcEleOrderHoldType = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, "");
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleOrderHoldTypes.appendChild(yfcEleOrderHoldType);
		yfcElemOrderDetailsTemp.appendChild(yfcEleOrderHoldTypes);

		return yfcDocGetOrderDetailsTemp.getDocument();
	}
	private Document getOrderInputXML(String sOrderHeaderKey){		
		YFCDocument yfcDocOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("getOrderDetails Input \n" + XMLUtil.getXMLString(yfcDocOrder.getDocument()));
		}

		return yfcDocOrder.getDocument();
	}

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
