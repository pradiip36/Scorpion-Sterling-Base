package com.kohls.oms.api;

import java.rmi.RemoteException;
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

public class KohlsCashEarnedActivationAPI implements YIFCustomApi {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsCashEarnedActivationAPI.class.getName());
	private YIFApi api = null;


	public KohlsCashEarnedActivationAPI() throws YIFClientCreationException{

		api = YIFClientFactory.getInstance().getLocalApi();
	}



	public void invoke(YFSEnvironment env, Document InXML) throws YFSException, RemoteException{

		if(YFCLogUtil.isDebugEnabled()){

			log.debug("<------- Begining of KohlsCashEarnedActivationAPI -------> \n" + XMLUtil.getXMLString(InXML));
		}

		Element eleRoot = InXML.getDocumentElement();
		String strOrderNo = eleRoot.getAttribute(KohlsXMLLiterals.A_ORDERNO);	
		String strEnterpriseCode = eleRoot.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);
		String strDocumentType = eleRoot.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE);

		Element eleExtn = (Element)eleRoot.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		String sKCECouponNo = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_KCE_COUPON_NO);
		String sKCECouponAlgo = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_KCE_COUPON_ALGORITHM);

		if((null== sKCECouponNo || sKCECouponNo.equalsIgnoreCase("")) 
				|| (null== sKCECouponAlgo || sKCECouponAlgo.equalsIgnoreCase(""))){

			//Raise alert
			api.executeFlow(env, KohlsConstant.SERVICE_RAISE_KOHLS_CASH_EARNED_EXCEPTION, createException(strOrderNo, sKCECouponNo, sKCECouponAlgo));

		}else{

			// getOrderDetails
			env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemp());
			Document docGetOrderDetails = api.getOrderDetails(env, 
					getOrderInputXML(strOrderNo, strEnterpriseCode, strDocumentType));
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);

			if(YFCLogUtil.isDebugEnabled()){				
				log.debug("getOrderDetails output XML  : \n" + XMLUtil.getXMLString(docGetOrderDetails));
			}

			boolean bRepricingHoldExist = false;
			boolean bCashActivationHoldExist = false;
			boolean bOOBHoldExist = false;
			
			Element eleOrder = docGetOrderDetails.getDocumentElement();
			String sMinOrderStatus = eleOrder.getAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS);
			NodeList ndlstOrderHoldTypes = eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);

			for(int i=0;i<ndlstOrderHoldTypes.getLength();i++){
				Element eleOrderDetailsHoldType = (Element)ndlstOrderHoldTypes.item(i);
				String sHoldType = eleOrderDetailsHoldType.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE);
				String sHoldStatus = eleOrderDetailsHoldType.getAttribute(KohlsXMLLiterals.A_STATUS);

				if(KohlsConstant.CASH_ACTIVATION_HOLD.equalsIgnoreCase(sHoldType) && KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)){
					bCashActivationHoldExist = true;
					continue;
				}
				if(KohlsConstant.INVOICE_HOLD_INDICATOR.equalsIgnoreCase(sHoldType) && KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)){
					bRepricingHoldExist = true ;
					break;
				}
				if(KohlsConstant.OOB_HOLD.equalsIgnoreCase(sHoldType) && KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)){
					bOOBHoldExist = true ;

				}
			}
			
			if(bCashActivationHoldExist){
				Element eleOrderHoldTypes = InXML.createElement(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
				Element eleOrderHoldType = InXML.createElement(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
				eleOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, KohlsConstant.CASH_ACTIVATION_HOLD);
				eleOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, KohlsConstant.HOLD_REMOVED_STATUS);
				eleOrderHoldTypes.appendChild(eleOrderHoldType);
				eleRoot.appendChild(eleOrderHoldTypes);

				if(YFCLogUtil.isDebugEnabled()){				
					log.debug("ChangeOrder XML  : \n" + XMLUtil.getXMLString(InXML));
				}

				api.changeOrder(env, InXML);


				if(YFCLogUtil.isDebugEnabled()){				
					log.debug("Does Repricing Hold Exist : " + bRepricingHoldExist);
				}

				if(!bRepricingHoldExist && !bOOBHoldExist  && sMinOrderStatus.equalsIgnoreCase(KohlsConstant.ORDER_SHIPPED)){
					// change Order Status to 'Awaiting Invoice Creation'
					if(YFCLogUtil.isDebugEnabled()){				
						log.debug("Move order to Awaiting Invoice Creation : \n" + getChangeOrderStatusInput(strOrderNo,strDocumentType,strEnterpriseCode));
					}
					api.changeOrderStatus(env, getChangeOrderStatusInput(strOrderNo,strDocumentType,strEnterpriseCode));
				}
			}
		}

		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("<------- End of KohlsCashEarnedActivationAPI ------->");
		}
	}


	private Document getOrderDetailsTemp() {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");	
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS, "");

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


	private Document getChangeOrderStatusInput(String strOrderNo, String strDocumentType, String strEnterpriseCode) {		
		YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
		YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_INCLUDE_INVOICE_0001_EX);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderNo);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KohlsConstant.AWAITING_INVOICE_CREATION);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("getOrderDetails Input \n" + XMLUtil.getXMLString(yfcDocChangeOrderStatus.getDocument()));
		}
		return yfcDocChangeOrderStatus.getDocument();
	}




	private Document createException(String sOrderNo, String sKCECouponNo, String sKCECouponAlgo){

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<------ Begining of createorder ----->");
		}

		YFCDocument yfcDocOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_INBOX);
		YFCElement yfcEleOrder = yfcDocOrder.getDocumentElement();
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.KOHLS_ENTERPRISE_CODE);
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDERNO, sOrderNo);

		YFCElement yfcEleExtn = yfcEleOrder.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_KCE_COUPON_NO, sKCECouponNo);
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_KCE_COUPON_ALGORITHM, sKCECouponAlgo);
		yfcEleOrder.appendChild(yfcEleExtn);


		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Create Order XML" + XMLUtil.getXMLString(yfcDocOrder.getDocument()));
		}
		return yfcDocOrder.getDocument();
	}

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
