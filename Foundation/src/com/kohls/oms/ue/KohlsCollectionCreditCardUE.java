package com.kohls.oms.ue;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.bopus.util.KohlsPaymentUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCollectionCreditCardUE;



public class KohlsCollectionCreditCardUE implements YFSCollectionCreditCardUE{

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsCollectionCreditCardUE.class.getName());

	KohlsPaymentUtil paymentUtil = new KohlsPaymentUtil();
	public KohlsCollectionCreditCardUE() throws YIFClientCreationException{
		api = YIFClientFactory.getInstance().getApi();
	}

	
	public  YFSExtnPaymentCollectionOutputStruct collectionCreditCard 
	(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inStruct) throws YFSUserExitException {

		if(YFCLogUtil.isDebugEnabled()){
			
			log.debug("<------------ Begining of KohlsCollectionCreditCardUE -------------------->");
		}
		YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
		Document docGetOrderDetails = null;
	
		try {
				
			  	if(KohlsConstant.CHARGE_TYPE_AUTH.equalsIgnoreCase(inStruct.chargeType) && inStruct.requestAmount < 0.0){
			  		log.debug("Invoking the Auth Reversal Request");
			  		paymentUtil.authorizePayment(env, inStruct, outStruct);
				
			  	} else {
			  		
			  	
					    docGetOrderDetails =  paymentUtil.getOrderDetailsForOrder(env,inStruct,outStruct);
						ArrayList list = new ArrayList();
						NodeList ndlstChargeDetails = docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAIL);
						
						boolean flag = false;
						
				
						for(int i=0;i<ndlstChargeDetails.getLength();i++){
							
							Element eleChargeDetails = (Element)ndlstChargeDetails.item(i);
							String sChargeType = eleChargeDetails.getAttribute(KohlsXMLLiterals.A_CHARGE_TYPE);
							String sStatus = eleChargeDetails.getAttribute(KohlsXMLLiterals.A_STATUS);
							Double reqAmount = Double.valueOf(eleChargeDetails.getAttribute(KohlsXMLLiterals.A_REQUEST_AMOUNT));
							
							if(sStatus.equalsIgnoreCase("OPEN")){
								if(KohlsConstant.CHARGE_TYPE_AUTH.equalsIgnoreCase(sChargeType) && reqAmount < 0.0){
									log.debug("Found atleast one open Auth Reversal Record");
									flag = true;
									
									
								}
									
							}
				
				
					}

				    if(flag) {
				    	log.debug("Setting the retry flag because there is a pending Auth Reversal Record to be processed");
					 	outStruct.retryFlag = "Y";
						outStruct.authorizationAmount = 0.0;
						outStruct.RequiresCallForAuthorization = true;
						outStruct.OfflineStatus = false;
						YFCDocument yfcDocGetReturnCodeList = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
						YFCElement yfcEleReturnCodeList = yfcDocGetReturnCodeList.getDocumentElement();
						yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, "KOHLS_RETRY_SEQ");
						 
						Document docGetCommonCodeOutputXML = null;
						String code_val = "2";
						try {
							docGetCommonCodeOutputXML = api.getCommonCodeList(env, yfcDocGetReturnCodeList.getDocument());
							NodeList ndlstCommonCodeList = docGetCommonCodeOutputXML.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
							
							Element eleCommonCode = (Element)ndlstCommonCodeList.item(0);
							code_val = eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE);
						} catch (YFSException e) {
							log.error(e);
						} catch (RemoteException e) {
							log.error(e);
						}
					
						Date dNow = new Date( ); 
						Calendar cal = Calendar.getInstance();
						cal.setTime(dNow);
						cal.add(Calendar.MINUTE, Integer.parseInt(code_val));
						dNow = cal.getTime();
						outStruct.collectionDate = dNow; 
				    } else {
				    	if(KohlsConstant.CHARGE_TYPE_AUTH.equals(inStruct.chargeType)) {
				    		paymentUtil.authorizePayment(env, inStruct, outStruct);
				    	} else if(KohlsConstant.CHARGE_TYPE_CHARGE.equals(inStruct.chargeType)) {
				    		paymentUtil.processSettlement(env,inStruct,outStruct);
				    	}
				    }
				}
		}
		catch (Exception e) {
			log.error(e);
			YFSUserExitException ex = new YFSUserExitException();
			throw ex;
		}
		
		return outStruct;
		
	}
	
}
