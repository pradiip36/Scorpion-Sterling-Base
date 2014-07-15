package com.kohls.bopus.util;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
//import com.kohls.common.util.XPathUtil;
import com.kohls.oms.ue.KohlsCollectionCreditCardUE;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.util.YFCUtils;
import com.yantra.yfc.date.YDate;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;

public class KohlsPaymentUtil {
	
	
	public static final String  SQL_ORACLE_AUTH_REG_TRANS_SEQ_NO_STORE = "SELECT SEQ_AUTH_REG_TRANS_NO_STORE.NEXTVAL from DUAL";
	public static final YFCLogCategory log = YFCLogCategory.instance(KohlsCollectionCreditCardUE.class.getName());
	private YIFApi api;
	
	public KohlsPaymentUtil() throws YIFClientCreationException{
		api = YIFClientFactory.getInstance().getApi();
	}
	
	
	public void authorizePayment(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inputStruct,YFSExtnPaymentCollectionOutputStruct outputStruct) throws ParserConfigurationException, YFSException, RemoteException, ParseException 
			{
		log.debug("order no is:"+inputStruct.orderNo);
		double dRequestAmount = inputStruct.requestAmount;
		
		if(!isValidAuth(inputStruct) || dRequestAmount < 0.0) {	
		  
		  log.debug("Inside getAuthorizationFromKOHLSPaymentSystem........Request amount is "+dRequestAmount);
		  String setWebSericeCallError = "";

			    Document inDocForBankCardAuthService = prepareInputForCardAuthService(env,inputStruct);
				log.debug("Input XML for Auth Service" + SCXmlUtil.getString(inDocForBankCardAuthService));
				Document responseDocForBankCardAuthService = null;
				try {
					responseDocForBankCardAuthService = api.executeFlow(env,"KohlsBopusPaymentWebservice", inDocForBankCardAuthService);
				} catch (Exception e) {
					log.error("Error while invoking payment service");
					log.error(e);
					setWebSericeCallError = "ERROR_CALL";
				}
					log.debug("Output XML for Auth Service" + SCXmlUtil.getString(responseDocForBankCardAuthService));
				
				setAuthDetailsToBankCard(env, responseDocForBankCardAuthService, inputStruct,outputStruct,setWebSericeCallError);
				log.debug("Out Strut for Auth Service"+outputStruct);
				

		} 
	
	}
	
	private Document formResponse(YFSExtnPaymentCollectionInputStruct inputStruct){
		
		Document responseDocForCardAuthService = SCXmlUtil.createDocument(KohlsConstant.PPROCESS_TRANS_RESP);
		Element eleformAuthResp = responseDocForCardAuthService
				.getDocumentElement();
		Element childResp = SCXmlUtil.createChild(eleformAuthResp, KohlsXMLLiterals.E_RESPONSE);
		childResp.setAttribute(KohlsXMLLiterals.A_ACTION_CODE, "");
		Element childTrans = SCXmlUtil.createChild(eleformAuthResp, KohlsXMLLiterals.E_TRANSACTION);
		childTrans.setAttribute(KohlsXMLLiterals.A_APPROVAL_NUMBER, "");
		return responseDocForCardAuthService;
	}
	
	private Document prepareInputForCardAuthService(YFSEnvironment env,YFSExtnPaymentCollectionInputStruct inputStruct) throws ParserConfigurationException {
		
		Document reqDoc = YFCDocument.createDocument(KohlsConstant.PPROCESS_TRANS_REQ).getDocument();;		
		Element eleReqDoc = reqDoc.getDocumentElement();
		Element eleReq = reqDoc.createElement(KohlsXMLLiterals.E_REQUEST);
		eleReq.setAttribute(KohlsXMLLiterals.A_CHANNEL, KohlsConstant.BPS);
		XMLUtil.appendChild(eleReqDoc, eleReq);
		Element eleToken = reqDoc.createElement(KohlsXMLLiterals.E_TOKEN);
		eleToken.setAttribute(KohlsXMLLiterals.A_CARD_NUMBER, inputStruct.creditCardNo);
		
		eleToken.setAttribute(KohlsXMLLiterals.A_TYPE, KohlsConstant.INTERNAL);
		eleToken.setAttribute(KohlsXMLLiterals.A_PROVIDER, KohlsConstant.PROTEGRITY);
		XMLUtil.appendChild(eleReqDoc, eleToken);
		Element eleTran = reqDoc.createElement(KohlsXMLLiterals.E_TRANSACTION);
		Double dAmount = new Double(inputStruct.requestAmount);
		if (dAmount.doubleValue() > 0) {
			eleTran.setAttribute(KohlsXMLLiterals.A_TRANS_REQ_TYPE, KohlsConstant.SALE);
		} else {
			eleTran.setAttribute(KohlsXMLLiterals.A_TRANS_REQ_TYPE, KohlsConstant.VOID_SALE);
		}
		
		eleTran.setAttribute(KohlsXMLLiterals.A_AMOUNT, KohlsPaymentUtil.convertAmtToAJBFormat(Math.abs(dAmount.doubleValue())));
		Date dbDate = new Date();
		DateFormat sdfDateFormat = new SimpleDateFormat(YDate.ISO_DATETIME_FORMAT);
		eleTran.setAttribute(KohlsXMLLiterals.A_DATE_TIME ,sdfDateFormat.format(dbDate));
		eleTran.setAttribute(KohlsXMLLiterals.A_TRANSACTION_NUMBER,inputStruct.orderNo);
		eleTran.setAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE, KohlsConstant.PAYMENT_TYPE_CREDIT_AUTH);
		eleTran.setAttribute(KohlsXMLLiterals.A_TRANSACTION_TYPE, KohlsConstant.PAYMENT_TYPE_AUTH_TRAN_TYPE);
		
		if (!YFCCommon.isVoid(inputStruct.authorizationId)) {
			String[] tempTran = inputStruct.authorizationId.split("=");
			if (tempTran.length == 2)
				eleTran.setAttribute("Reversal", tempTran[1]);
			else
				eleTran.setAttribute("Reversal", "");
		}
		
		//added by Baijayanta on 13/2/2014
		if (!YFCUtils.isVoid(inputStruct.creditCardExpirationDate)) {
			try {
				//We need to pass Exp Date in MMYY format
				String expDate = getCardExpDateInMMYYFormat(inputStruct.creditCardExpirationDate);
				eleTran.setAttribute(KohlsXMLLiterals.A_CARD_EXP_DATE, expDate);
			} catch (Exception e) {
				log.error("Error while converting the credit card expiration date in YYMM format");
				eleTran.setAttribute(KohlsXMLLiterals.A_CARD_EXP_DATE, "");
			}
		}
		eleTran.setAttribute(KohlsXMLLiterals.A_CVVCODE,
				inputStruct.secureAuthenticationCode);
		eleTran.setAttribute(KohlsXMLLiterals.A_IS_SWIPED, KohlsConstant.FALSE);
		
		eleTran.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, inputStruct.billToZipCode);
	
		//closed by Baijayanta 13/2/2014
		eleTran.setAttribute(KohlsXMLLiterals.A_REQUEST_TYPE, KohlsConstant.HUNDRED_INT);
		Element eleStore = reqDoc.createElement(KohlsXMLLiterals.E_STORE);
		eleStore.setAttribute(KohlsXMLLiterals.A_STORE_NUMBER,prepadStoreNoWithZeros(KohlsConstant.STORE_873));
		//eleStore.setAttribute(KohlsXMLLiterals.A_TERMINAL_NUMBER,"0000");
		try {
			String regTransValues = getRegTransValues(env);
			eleStore.setAttribute(KohlsXMLLiterals.A_TERMINAL_NUMBER, "00" + regTransValues.substring(0, 2));
			eleTran.setAttribute(KohlsXMLLiterals.A_TRANSACTION_NUMBER,regTransValues.substring(2));
		} catch (SQLException e) {
			log.error("Error while reading the register and transaction number for an Auth request");
			log.error(e);
		}
		XMLUtil.appendChild(eleReqDoc, eleStore);
		XMLUtil.appendChild(eleReqDoc, eleTran);
		log.debug("the req doc is:"+ SCXmlUtil.getString(reqDoc));
		return reqDoc;
	
	}
	
	/**
	 * 
	 * @param cardExpDate - 
	 * @return
	 * @throws ParseException
	 */
	private String getCardExpDateInMMYYFormat(String cardExpDate) throws ParseException {

	    String expDate = null;
	    if(cardExpDate.indexOf("/") > 0) {
		    String[] split = cardExpDate.split("/");
		    expDate =  split[1] + split[0].substring(2);
		    log.debug("the expDate for / is:"+expDate);
	 
	    } else if(cardExpDate.indexOf("-") > 0){
		    String[] split = cardExpDate.split("-");
		    expDate =  split[1] + split[0].substring(2);
		    log.debug("the expDate for - is:"+expDate);
	    } else {
		    log.debug("Card Exp Date is in unknown format, setting to blank :" + cardExpDate);
		    expDate = "";
	    }
	    return expDate;
	}


	private String getRegTransValues(YFSEnvironment env) throws SQLException {
		
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String sRegisterTransSeqNo = null;
		Connection m_conn = null;
		YFSConnectionHolder connHolder = null;
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("############## Getting Next Register,Transaction No for Auth ##################");
		}
		
		try{
			connHolder = (YFSConnectionHolder) env;
			m_conn= connHolder.getDBConnection();
			stmt = m_conn.prepareStatement(SQL_ORACLE_AUTH_REG_TRANS_SEQ_NO_STORE);
			
			rSet = stmt.executeQuery();

			if (rSet.next()) {
				sRegisterTransSeqNo = rSet.getString(1);
			}

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("The sequential Register Transaction Number : " + sRegisterTransSeqNo);
			}
		}catch (Exception e) {
				log.debug("Error while closing the resouces");
				log.error(e);
			}
		
		return sRegisterTransSeqNo;
    
       }


	
	private void setAuthDetailsToBankCard (YFSEnvironment env,Document responseDocForBankCardAuthService,YFSExtnPaymentCollectionInputStruct inputStruct,YFSExtnPaymentCollectionOutputStruct outputStruct,String setWebSericeCallError) throws YFSException, RemoteException, ParseException {
		
		if (setWebSericeCallError.equalsIgnoreCase("ERROR_CALL")) {
			outputStruct.retryFlag = "Y";
			outputStruct.RequiresCallForAuthorization = true;
			
			YFCDocument yfcDocGetReturnCodeList = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
			YFCElement yfcEleReturnCodeList = yfcDocGetReturnCodeList.getDocumentElement();
			yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, "KOHLS_RETRY_INTERVAL");
			 
			Document docGetCommonCodeOutputXML = api.getCommonCodeList(env, yfcDocGetReturnCodeList.getDocument());
		
			NodeList ndlstCommonCodeList = docGetCommonCodeOutputXML.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
			
			Element eleCommonCode = (Element)ndlstCommonCodeList.item(0);
			String code_val = eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE);
			
			Date dNow = new Date( ); 
			Calendar cal = Calendar.getInstance();
			cal.setTime(dNow);
			cal.add(Calendar.MINUTE, Integer.parseInt(code_val));
			dNow = cal.getTime();
			
			outputStruct.collectionDate = dNow;
			 
		} else {
				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
				DateFormat df2 = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
				Element transactionResponse = responseDocForBankCardAuthService.getDocumentElement();
				Element responseElement = XMLUtil.getFirstElementByName(transactionResponse, KohlsXMLLiterals.E_RESPONSE);
				String actionCode = responseElement.getAttribute(KohlsXMLLiterals.A_ACTION_CODE);
				Element eleTransaction =  SCXmlUtil.getChildElement(transactionResponse, KohlsXMLLiterals.E_TRANSACTION);
				String amount = eleTransaction.getAttribute(KohlsXMLLiterals.A_AMOUNT);
				
				String expDateforCard = setExpirationDateForCards(env,inputStruct);
				Calendar cl = Calendar.getInstance();
				cl.add(Calendar.DATE, Integer.parseInt(expDateforCard));
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				String sExpDate = df.format(cl.getTime());
				
				String sAuthID = "";
				String sReversal = "";
				if (!YFCCommon.isVoid(eleTransaction.getAttribute(KohlsXMLLiterals.A_APPROVAL_NUMBER))) {
					sAuthID = eleTransaction.getAttribute(KohlsXMLLiterals.A_APPROVAL_NUMBER);
				}
				if (!YFCCommon.isVoid(eleTransaction.getAttribute("Reversal"))) {
					sReversal = eleTransaction.getAttribute("Reversal");
					if (!YFCCommon.isVoid(sAuthID))
						outputStruct.authorizationId = sAuthID + "=" + sReversal;
				} else {
		
					outputStruct.authorizationId = sAuthID;
				}
				
				String tranRetCode = "";
				
				if(!(KohlsConstant.ZERO_INT.equalsIgnoreCase(actionCode) || KohlsConstant.ONE_INT.equalsIgnoreCase(actionCode))) {
						//actionCode = KohlsConstant.ZERO_INT;
						sAuthID = KohlsConstant.NINE_INT;
						sExpDate = setExpirationDate(env);
						
				}
				outputStruct.internalReturnCode = actionCode;
				/*if(!YFCCommon.isVoid(amount)){
				    outputStruct.authorizationAmount = convertAmtToOMSFormat(amount);
				} else {
					outputStruct.authorizationAmount = inputStruct.requestAmount;
				}*/
				outputStruct.authorizationAmount = inputStruct.requestAmount;
				if(!YFCCommon.isVoid(eleTransaction.getAttribute(KohlsXMLLiterals.A_AMOUNT))) {
					double dbAmount = convertAmtToOMSFormat(eleTransaction.getAttribute(KohlsXMLLiterals.A_AMOUNT));
					if (dbAmount < Math.abs(inputStruct.requestAmount) && inputStruct.requestAmount > 0) {
						outputStruct.authorizationAmount = dbAmount;
					} else if (dbAmount < Math.abs(inputStruct.requestAmount) && inputStruct.requestAmount < 0){
						outputStruct.authorizationAmount = 0 - dbAmount;
					} 
				}
				if(!YFCCommon.isVoid(eleTransaction.getAttribute(KohlsXMLLiterals.A_CARD_LEVEL))) {
					tranRetCode = eleTransaction.getAttribute(KohlsXMLLiterals.A_CARD_LEVEL);
				}
				outputStruct.tranReturnCode = tranRetCode;
					
				outputStruct.authorizationExpirationDate = sExpDate;
				//Element transactionElement = XMLUtil.getFirstElementByName(transactionResponse, KohlsXMLLiterals.E_TRANSACTION);
				
				String strPS2000data = eleTransaction.getAttribute(KohlsXMLLiterals.A_PS2000Data);
				String strSequenceNumber = eleTransaction
						.getAttribute(KohlsXMLLiterals.A_SEQUENCE_NUMBER);
				String strBankResponseCode = eleTransaction
						.getAttribute(KohlsXMLLiterals.A_BANK_RESPONSE_CODE);
				String strPostingDate = eleTransaction.getAttribute(KohlsXMLLiterals.A_POSTING_DATE);
				// Added attribute Options on 09/30 for sales hub
				String strOption = eleTransaction.getAttribute(KohlsXMLLiterals.A_OPTIONS);
		
				outputStruct.authReturnMessage = "ps2000Data='" + strPS2000data
						+ "' sequenceNumber='" + strSequenceNumber
						+ "' bankResponseCode='" + strBankResponseCode
						+ "' postingDate='" + strPostingDate + "'" + "' options='"
						+ strOption + "'";
				
				outputStruct.retryFlag ="";
				
				outputStruct.tranReturnMessage = eleTransaction
						.getAttribute(KohlsXMLLiterals.A_DEPOSIT_DATA);
				
				//outputStruct.authorizationId = sAuthID;
				String sAuthTime = eleTransaction.getAttribute(KohlsXMLLiterals.A_DATE_TIME);
				Date dtAuthTimeRaw = df1.parse(sAuthTime);
				String sAuthTimeFormatted = df2.format(dtAuthTimeRaw);
				outputStruct.authTime = sAuthTimeFormatted;
				
				outputStruct.sCVVAuthCode = eleTransaction.getAttribute(KohlsXMLLiterals.A_CVVRESP);
				outputStruct.authAVS = eleTransaction.getAttribute(KohlsXMLLiterals.A_AVSRESP);
				
				outputStruct.authCode = sAuthID;
				log.debug("OutStruct value is:"+outputStruct);
		  }
				
			
      }
	
	public static Double convertAmtToOMSFormat(String sReqAmount) {
		BigDecimal omsAmt = new BigDecimal(Integer.parseInt(sReqAmount));
		return (omsAmt.divide(new BigDecimal(100)).doubleValue());
	}
	
    private String setExpirationDate(YFSEnvironment env) throws YFSException, RemoteException {
		
		log.debug("Start setExpirationDate method.......");
		
		YFCDocument yfcDocGetReturnCodeList = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		YFCElement yfcEleReturnCodeList = yfcDocGetReturnCodeList.getDocumentElement();
		yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_EXP_INTERVAL);
		
		Document docGetCommonCodeOutputXML = api.getCommonCodeList(env, yfcDocGetReturnCodeList.getDocument());
	
		NodeList ndlstCommonCodeList = docGetCommonCodeOutputXML.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		
		Element eleCommonCode = (Element)ndlstCommonCodeList.item(0);
		String code_val = eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE);		
		
		Calendar cal = Calendar.getInstance();
	    DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");//"yyyy-MM-dd'T'HH:mm:ss"
		String currDate = df1.format(cal.getTime());
		log.debug("Current Date:"+ currDate);
		
		cal.add(Calendar.HOUR, Integer.parseInt(code_val));
		String updtExpDate = df1.format(cal.getTime());
		
		log.debug("updated exp date:"+updtExpDate);
		
		return updtExpDate;
		
		
	}
    
    private String setExpirationDateForCards(YFSEnvironment env,YFSExtnPaymentCollectionInputStruct inputStruct) throws YFSException, RemoteException {
    	
    	YFCDocument yfcDocGetReturnCodeList = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		YFCElement yfcEleReturnCodeList = yfcDocGetReturnCodeList.getDocumentElement();
		
		if(inputStruct.paymentType.equalsIgnoreCase("Credit Card")) {
			String creditCardType = inputStruct.creditCardType;
			if("VISA".equalsIgnoreCase(creditCardType)) {
		   yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_CRD_VISA_EXP);
			} else if("MSTR_CARD".equalsIgnoreCase(creditCardType)){
			yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_CRD_DISCVR_EXP);
			} else if("DISC".equalsIgnoreCase(creditCardType)) {
			yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_CRD_MSTCRD_EXP);
			} else if("AMEX".equalsIgnoreCase(creditCardType)) {
			yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_CRD_AMX_EXP);
			} else if("KL_CHRG_CARD".equalsIgnoreCase(creditCardType)) {
			yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_CHARGE_EXP);
			} else {
				yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_DEFAULT_EXP);
			}
		} else if(inputStruct.paymentType.equalsIgnoreCase("Gift Card")){
			yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_STORE_VAL_EXP);
		} 
		log.debug("the document is:"+ SCXmlUtil.getString(yfcDocGetReturnCodeList.getDocument()));
		Document docGetCommonCodeOutputXML = api.getCommonCodeList(env, yfcDocGetReturnCodeList.getDocument());
	
		NodeList ndlstCommonCodeList = docGetCommonCodeOutputXML.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		
		Element eleCommonCode = (Element)ndlstCommonCodeList.item(0);
		String code_val = eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE);
		
		return code_val;
    	
    }
    
       
    
    
    public void processSettlement(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inputStruct, YFSExtnPaymentCollectionOutputStruct outStruct) throws Exception {
     	
		YFCDocument yfcDocOrderDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleOrderDetails = yfcDocOrderDetails.getDocumentElement();
		yfcEleOrderDetails.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, inputStruct.orderHeaderKey);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Input XML for getOrderDetails : \n" + XMLUtil.getXMLString(yfcDocOrderDetails.getDocument()));
		}
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemp());
		
		Document docGetOrderDetails;
		
		docGetOrderDetails = api.getOrderDetails(env, yfcDocOrderDetails.getDocument());
		
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);
		
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Output XML for getOrderDetails : \n" + XMLUtil.getXMLString(docGetOrderDetails));
		}
		
                	
        	NodeList ndlstChargeDetails = docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAIL);
			String sAuthAvs = "";
			String sAuthMessage = "";
			String sAuthCode = "";
			String sAuthReturnCode = "";
			ArrayList list = new ArrayList();
				
		    for(int i=0;i<ndlstChargeDetails.getLength();i++){
		
					Element eleChargeDetails = (Element)ndlstChargeDetails.item(i);
					String sChargeTransactionKey = eleChargeDetails.getAttribute(KohlsXMLLiterals.A_CHARGE_TRANSACTION_KEY);
					String sAuthorizationID = eleChargeDetails.getAttribute(KohlsXMLLiterals.A_AUTHORIZATION_ID);
					String sChargeType = eleChargeDetails.getAttribute(KohlsXMLLiterals.A_CHARGE_TYPE);
					String sStatus = eleChargeDetails.getAttribute(KohlsXMLLiterals.A_STATUS);

					if(KohlsConstant.CHARGE_TYPE_AUTH.equalsIgnoreCase(sChargeType)
							&& inputStruct.authorizationId.equalsIgnoreCase(sAuthorizationID)){
						//Assuming there will be only one Credit Card Authorization information
						Element eleCreditCardDtl = (Element)eleChargeDetails.getElementsByTagName(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTION).item(0);
						if(eleChargeDetails.getElementsByTagName(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTION).getLength() > 0){
							sAuthAvs = eleCreditCardDtl.getAttribute(KohlsXMLLiterals.A_AUTH_AVS);
							sAuthMessage = eleCreditCardDtl.getAttribute(KohlsXMLLiterals.A_AUTH_RETURN_MESSAGE);
							sAuthCode = eleCreditCardDtl.getAttribute(KohlsXMLLiterals.A_AUTH_CODE);
							sAuthReturnCode = eleCreditCardDtl.getAttribute(KohlsXMLLiterals.A_AUTH_RETURN_CODE);
						}
		
					}
					if(YFCLogUtil.isDebugEnabled()){
						log.debug("Charge Transaction Key : " + sChargeTransactionKey);
					}
				}
				outStruct.authorizationId = inputStruct.authorizationId;
				outStruct.authorizationAmount = inputStruct.requestAmount;
				outStruct.authAVS = sAuthAvs;
				outStruct.authReturnMessage = sAuthMessage;
				outStruct.authCode = sAuthCode;
				outStruct.authReturnCode = sAuthReturnCode;
				outStruct.tranAmount = inputStruct.requestAmount;
				outStruct.tranReturnCode = sAuthReturnCode;			
				outStruct.tranReturnMessage = KohlsConstant.PAYMENT_RETURN_MSG_SUCCESSFUL;
				outStruct.tranType = inputStruct.chargeType;
				
       }
        

    
    private boolean isValidAuth(YFSExtnPaymentCollectionInputStruct inputStruct) {
    	
		if(YFCCommon.isVoid(inputStruct.authorizationId)) {
			return false;
		}
		return true;
    }
    
    public void authorizePaymentForStoreCard(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inputStruct,YFSExtnPaymentCollectionOutputStruct outputStruct) 
			throws Exception{
		log.debug("order no is:"+inputStruct.orderNo);
		
		if(!isValidAuth(inputStruct)) {	
		  double dRequestAmount = inputStruct.requestAmount;
		  log.debug("Inside authorizePaymentForStoreCard: "+dRequestAmount);
		  //check for main auth
		  if(dRequestAmount > 0.0){

			 
				Document inDocForStoreCardAuthService = prepareInputForCardAuthServiceForStoreCard(inputStruct);
				log.debug("Input XML for Auth Service" + SCXmlUtil.getString(inDocForStoreCardAuthService));
				Document responseDocForStoreCardAuthService = api.executeFlow(env, "KohlsBopusSVCService", inDocForStoreCardAuthService);
				log.debug("Output XML for Auth Service" + SCXmlUtil.getString(responseDocForStoreCardAuthService));
				setAuthDetailsToStoreSVCCard(env, responseDocForStoreCardAuthService, inputStruct,outputStruct);
				log.debug("Out Strut for Auth Service"+outputStruct);				
		  }
	   }
    } 
    
    private Document prepareInputForCardAuthServiceForStoreCard(YFSExtnPaymentCollectionInputStruct inputStruct) throws ParserConfigurationException {
		
    	DecimalFormat dcf = new DecimalFormat(KohlsConstant.DECIMAL_FORMAT);//KohlsPOCConstant.DECIMAL_FORMAT
    	Document reqDoc = YFCDocument.createDocument(KohlsConstant.PAYMENT_REQUEST).getDocument();;		
		Element eleReqDoc = reqDoc.getDocumentElement();
		Element eleTran = reqDoc.createElement(KohlsXMLLiterals.E_TRANSACTION);
		Double dAmount = new Double(inputStruct.requestAmount);
		eleTran.setAttribute(KohlsXMLLiterals.A_REQUEST_TYPE, KohlsConstant.TENDER);
		
		eleTran.setAttribute(KohlsXMLLiterals.A_TENDER_AMOUNT,dcf.format(Math.abs(dAmount.doubleValue())));
		
		if (!YFCCommon.isVoid(inputStruct.creditCardType)
				&& inputStruct.creditCardType.startsWith(KohlsConstant.THIRTEEN_INT)) {
			eleTran.setAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE, KohlsConstant.GIFT_CARD);
			eleTran.setAttribute(KohlsXMLLiterals.A_ENTRY_METHOD, KohlsConstant.KEYED);
		} else if (!YFCCommon.isVoid(inputStruct.creditCardType)
				&& inputStruct.creditCardType.startsWith(KohlsConstant.ZERO_TWO_INT)) {
			eleTran.setAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE, KohlsConstant.MERCHANDISE_RETURN_CREDIT);
			eleTran.setAttribute(KohlsXMLLiterals.A_ENTRY_METHOD, KohlsConstant.KEYED);
		}
		eleTran.setAttribute(KohlsXMLLiterals.A_STORE_NUMBER,prepadStoreNoWithZeros(KohlsConstant.STORE_873));
		eleTran.setAttribute(KohlsXMLLiterals.A_REGISTER_NUMBER, "");
		if (!YFCCommon.isVoid(inputStruct.secureAuthenticationCode))
			eleTran.setAttribute(KohlsXMLLiterals.A_SVP_INFO,
					inputStruct.secureAuthenticationCode);

		eleTran.setAttribute(KohlsXMLLiterals.A_SVC_NO, inputStruct.svcNo);//"SVCno"
		//eleTran.setAttribute("OperatorID","");
		eleTran.setAttribute(KohlsXMLLiterals.A_TRANSACTION_NUMBER,inputStruct.orderNo);
		XMLUtil.appendChild(eleReqDoc, eleTran);
		
		return reqDoc;
	
	}
    
      private void setAuthDetailsToStoreSVCCard (YFSEnvironment env,Document respDoc,YFSExtnPaymentCollectionInputStruct inputStruct,YFSExtnPaymentCollectionOutputStruct outputStruct) throws YFSException, RemoteException, ParseException {
		
    	Element paymentResponse = respDoc.getDocumentElement();
  		String authCode = paymentResponse.getAttribute(KohlsXMLLiterals.A_AUTH_CODE);
  		
  		String sSetExpDate = setExpirationDateForCards(env,inputStruct);
		Calendar cl = Calendar.getInstance();
		cl.add(Calendar.DATE, Integer.parseInt(sSetExpDate));
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String sExpDate = df.format(cl.getTime());
		
		String sAuthId = "";
		
		if (!YFCCommon.isVoid(paymentResponse.getAttribute(KohlsXMLLiterals.A_APPROVED_AMOUNT))) {
			sAuthId = paymentResponse.getAttribute(KohlsXMLLiterals.A_AUTHRESPONSE);
		}
		
		if(!(KohlsConstant.ZERO_INT.equalsIgnoreCase(authCode) || KohlsConstant.ONE_INT.equalsIgnoreCase(authCode))) {
			   // authCode = KohlsConstant.NINE_INT;
			    sAuthId = KohlsConstant.NINE_INT;
				sExpDate = setExpirationDate(env);
		}
		outputStruct.authorizationId = sAuthId;
		outputStruct.authorizationExpirationDate = sExpDate;
		outputStruct.internalReturnCode = paymentResponse.getAttribute(KohlsXMLLiterals.A_AUTHRESPONSE);
		// String appAmount=paymentResponse.getAttribute("ApprovedAmount");
		if (!YFCCommon.isVoid(paymentResponse.getAttribute(KohlsXMLLiterals.A_APPROVED_AMOUNT))) {
			double dAppAmount = Double.parseDouble(paymentResponse
					.getAttribute(KohlsXMLLiterals.A_APPROVED_AMOUNT));
			outputStruct.authorizationAmount = dAppAmount;
		} else {
			outputStruct.authorizationAmount = inputStruct.requestAmount;
		}

		outputStruct.tranReturnMessage = paymentResponse
				.getAttribute(KohlsXMLLiterals.A_REMAINING_BALANCE);

		outputStruct.internalReturnMessage = paymentResponse
				.getAttribute(KohlsXMLLiterals.A_AUTH_SOURCE);

		outputStruct.authCode = authCode;

		
		
		outputStruct.authTime = df.format(Calendar.getInstance().getTime());
			
      }
      
      public void processSettlementSVCCard(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inputStruct, YFSExtnPaymentCollectionOutputStruct outStruct) throws Exception {

    	  Document docGetOrderDetails = null;
			if(0 > inputStruct.requestAmount){

				if(YFCLogUtil.isDebugEnabled()){
					log.debug("Request Amount : " + String.valueOf(inputStruct.requestAmount));
				}

				YFCDocument yfcDocOrderDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
				YFCElement yfcEleOrderDetails = yfcDocOrderDetails.getDocumentElement();
				yfcEleOrderDetails.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, inputStruct.orderHeaderKey);

				if(YFCLogUtil.isDebugEnabled()){
					log.debug("Input to getOrderDetails : " + XMLUtil.getXMLString(yfcDocOrderDetails.getDocument()));
				}

				env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemp());
				docGetOrderDetails = api.getOrderDetails(env, yfcDocOrderDetails.getDocument());
				env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);

				if(YFCLogUtil.isDebugEnabled()){
					log.debug("Output to getOrderDetails : " + XMLUtil.getXMLString(docGetOrderDetails));
				}
				
				Element eleChargeDetails = (Element)docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAILS).item(0);
				eleChargeDetails.setAttribute(KohlsXMLLiterals.A_TOTAL_CREDITS, "");
				NodeList ndlstChargeDetails = docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAIL);

				for(int i=0;i<ndlstChargeDetails.getLength();i++){

					Element eleChargeDetail = (Element)ndlstChargeDetails.item(i);
					String sChargeTransactionKey = eleChargeDetail.getAttribute(KohlsXMLLiterals.A_CHARGE_TRANSACTION_KEY);
					if(!inputStruct.chargeTransactionKey.equals(sChargeTransactionKey)){

						eleChargeDetails.removeChild(ndlstChargeDetails.item(i));
					}


				}
				
				

				if(YFCLogUtil.isDebugEnabled()){
					log.debug("Kohls Stored Value Card Refund Message : " + XMLUtil.getXMLString(docGetOrderDetails));
				}
				

			}	


      	outStruct.authorizationAmount = inputStruct.requestAmount;
      	outStruct.SvcNo = inputStruct.svcNo;
      	outStruct.tranAmount = inputStruct.requestAmount;
		
      }
	
     
     
     public static String prepadStoreNoWithZeros(String storeNo) {

 		StringBuilder storeNoStringBuilder = new StringBuilder();
 		
 		for (int j = storeNo.length(); j < KohlsConstant.SHIPNODE_LEN; j++){
 			storeNoStringBuilder.append(KohlsConstant.ZERO_INT);
 		}

 			return storeNoStringBuilder.append(storeNo).toString();
 	}
     
     public static String convertAmtToAJBFormat(Double dReqAmount) {

 		DecimalFormat dcf = new DecimalFormat("0.00");
 		BigDecimal ajbAmt = new BigDecimal(dcf.format(dReqAmount));
 		int iNewAJBAmt = ajbAmt.multiply(new BigDecimal(100)).intValue();
 		String sAJBAmt = Integer.toString(iNewAJBAmt);
 		if (iNewAJBAmt < 10) {
 			sAJBAmt = String.format("%02d", iNewAJBAmt);
 		}
 		return sAJBAmt;
 	}
    
    private Document getOrderDetailsTemp(){

		YFCDocument yfcDocGetOrderDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderDetails = yfcDocGetOrderDetails.getDocumentElement();
		yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, "");
		yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "");
		yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_PAYMENT_STATUS, "");

		YFCElement yfcEleChargeTranDtls = yfcDocGetOrderDetails.createElement(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAILS);
		yfcEleChargeTranDtls.setAttribute(KohlsXMLLiterals.A_REMAINING_AMOUNT_TO_AUTH,"");
		YFCElement yfcEleChargeTranDtl = yfcDocGetOrderDetails.createElement(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAIL);
		yfcEleChargeTranDtl.setAttribute(KohlsXMLLiterals.A_AUTH_EXPIRY_DATE, "");
		yfcEleChargeTranDtl.setAttribute(KohlsXMLLiterals.A_AUTHORIZATION_ID, "");
		yfcEleChargeTranDtl.setAttribute(KohlsXMLLiterals.A_CHARGE_TRANSACTION_KEY, "");
		yfcEleChargeTranDtl.setAttribute(KohlsXMLLiterals.A_CHARGE_TYPE, "");
		yfcEleChargeTranDtl.setAttribute(KohlsXMLLiterals.A_OPEN_AUTHORIZED_AMOUNT, "");
		yfcEleChargeTranDtl.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleChargeTranDtl.setAttribute(KohlsXMLLiterals.A_REQUEST_AMOUNT, "");
		yfcEleChargeTranDtl.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleChargeTranDtls.appendChild(yfcEleChargeTranDtl);
		yfcEleGetOrderDetails.appendChild(yfcEleChargeTranDtls);
		
		YFCElement yfcEleCreditCardTrans = yfcDocGetOrderDetails.createElement(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTIONS);
		YFCElement yfcEleCreditCardTran = yfcDocGetOrderDetails.createElement(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTION);
		yfcEleCreditCardTran.setAttribute(KohlsXMLLiterals.A_AUTH_AVS, "");
		yfcEleCreditCardTran.setAttribute(KohlsXMLLiterals.A_AUTH_CODE, "");
		yfcEleCreditCardTran.setAttribute(KohlsXMLLiterals.A_AUTH_RETURN_CODE, "");
		yfcEleCreditCardTran.setAttribute(KohlsXMLLiterals.A_AUTH_RETURN_MESSAGE, "");
		yfcEleCreditCardTran.setAttribute(KohlsXMLLiterals.A_AUTH_TIME, "");
		yfcEleCreditCardTran.setAttribute(KohlsXMLLiterals.A_CHARGE_TRANSACTION_KEY, "");
		yfcEleCreditCardTrans.appendChild(yfcEleCreditCardTran);
		yfcEleChargeTranDtl.appendChild(yfcEleCreditCardTrans);

		if(YFCLogUtil.isDebugEnabled()){

			log.debug("getOrderDetails output Template \n" + XMLUtil.getXMLString(yfcDocGetOrderDetails.getDocument()));
		}

		return yfcDocGetOrderDetails.getDocument();
	}
    
    public Document getOrderDetailsForOrder(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inputStruct, YFSExtnPaymentCollectionOutputStruct outStruct) throws YFSException, RemoteException{
    	
    	YFCDocument yfcDocOrderDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleOrderDetails = yfcDocOrderDetails.getDocumentElement();
		yfcEleOrderDetails.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, inputStruct.orderHeaderKey);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Input XML for getOrderDetails : \n" + XMLUtil.getXMLString(yfcDocOrderDetails.getDocument()));
		}
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemp());
		
		Document docGetOrderDetails = api.getOrderDetails(env, yfcDocOrderDetails.getDocument());
		
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);
		
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Output XML for getOrderDetails : \n" + XMLUtil.getXMLString(docGetOrderDetails));
		}
    	
    	return docGetOrderDetails;
    	
    }


}  


