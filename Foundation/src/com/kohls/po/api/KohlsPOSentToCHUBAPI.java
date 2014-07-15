package com.kohls.po.api;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.StringUtil;
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
 * This class creates ship advices once the PO is released.
 * These ship advices are sent to CHUB.
 * This class is used by KohlsSendPOToCHUBSyncService service.  
 * @author Priyadarshini
 *
 */
public class KohlsPOSentToCHUBAPI implements YIFCustomApi{
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KohlsPOSentToCHUBAPI.class.getName());
	private  YIFApi api;
	
	// SQL for register and transaction number in gift receipt number
	public static String SQL_ORACLE_REG_TRANS_SEQ_NO = "SELECT SEQ_REG_TRANS_NO.NEXTVAL from DUAL";
	private Connection m_conn;
	/**
	 * constructor initializes api
	 * 
	 * @throws YIFClientCreationException
	 */
	public KohlsPOSentToCHUBAPI() throws YIFClientCreationException {

		 api = YIFClientFactory.getInstance().getLocalApi();
	}
	/**
	 * Creates shipadvices which are sent to CHUB after the 
	 * PO is released.
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception 
	 */
	public Document createShipAdvices(YFSEnvironment env, Document inXML)
			throws Exception {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered Send PO to CHUB Module--------------------------> ");
		}
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input XML  : "
					+ KohlsUtil.extractStringFromDocument(inXML));
		}

		this.m_conn = KohlsUtil.getDBConnection(env);
		
		Element elemShipAdvice = (Element) inXML.getElementsByTagName(
				KohlsXMLLiterals.E_SHIPMENT_ADVICE).item(0);
		String strPurchaseOrdNum = elemShipAdvice
				.getAttribute(KohlsXMLLiterals.A_SALES_ORDER_NO);
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("KohlsPOSentToCHUBAPI.createShipAdvices(PO Num)"+strPurchaseOrdNum);
		}
		
		
		// get Order Details
		/*env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemp());
		Document docOrderDetails = api.getOrderDetails(env,
				getOrderDetailsInputXml(strPurchaseOrdNum, KohlsConstant.KOHLS_ENTERPRISE_CODE, KohlsConstant.PO_DOCUMENT_TYPE));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);*/
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("KohlsPOSentToCHUBAPI.createShipAdvices(ENV )"+env);
		}
		
		
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_LIST, getOrderListTemp());
		
		Document docGetOrderList = api.getOrderList(env, getOrderInputXML(strPurchaseOrdNum, KohlsConstant.KOHLS_ENTERPRISE_CODE, KohlsConstant.PO_DOCUMENT_TYPE));
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("KohlsPOSentToCHUBAPI.createShipAdvices()"+XMLUtil.getXMLString(docGetOrderList));
		}
		
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LIST);		 
		Element eleOrderTemp = (Element) docGetOrderList.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("KohlsPOSentToCHUBAPI.createShipAdvices()"+XMLUtil.getElementXMLString(eleOrderTemp));
		}
		
		
		
		/*Document docShipAdvices = getShipAdvices(env, docOrderDetails,
				elemShipAdvice);*/
		Document docOrder = XMLUtil.getDocumentForElement(eleOrderTemp);
		Document docShipAdvices = getShipAdvices(env, docOrder,
				elemShipAdvice);
		
		
		
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Ship Advice sent to CHUB : "
					+ KohlsUtil.extractStringFromDocument(docShipAdvices));
		}
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Exit Send PO to CHUB Module--------------------------> ");
		}
		return docShipAdvices;
	}

	private Document getShipAdvices(YFSEnvironment env, Document docOrderDetails, Element elemShipAdvice) throws YFSException, RemoteException, TransformerException {
		
		boolean promotionChk = false;
		boolean paymentChk = false;
		
		String strShipAdviceNum = elemShipAdvice.getAttribute(KohlsXMLLiterals.A_SA_NO);

		Element elemOrder = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);		
		
		Element elemShipTo = (Element)elemShipAdvice.getElementsByTagName(KohlsXMLLiterals.E_SHIP_TO).item(0);
		
		Element elemBillTo = (Element)elemShipAdvice.getElementsByTagName(KohlsXMLLiterals.E_BILL_TO).item(0);	
		
		NodeList nlOrderLine =  docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		
		Element elemChainedFromOrderLine = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_CHAINED_FROM_ORDER_LINE).item(0);
		
		Element elemChainedOrder = (Element)elemChainedFromOrderLine.getFirstChild();
		String sSOOrderHeaderKey = elemChainedOrder.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		
		YFCDocument yfcDocShipAdvices = YFCDocument
		.createDocument(KohlsXMLLiterals.E_SHIPMENT_ADVICES);
		YFCElement yfcEleShipAdvices = yfcDocShipAdvices
				.getDocumentElement(); 
		 
		YFCElement yfcEleShipAdvice = yfcEleShipAdvices.createChild(KohlsXMLLiterals.E_SHIPMENT_ADVICE);
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, elemOrder.getAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE));
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_BUYER_ORGANIZATION_CODE, elemOrder.getAttribute(KohlsXMLLiterals.A_BUYER_ORGANIZATION_CODE));
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, elemOrder.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE, elemOrder.getAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE));
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE, elemOrder.getAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE));
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, elemOrder.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE));
		String sSOOrderNo = elemChainedOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_SALES_ORDER_NO, sSOOrderNo);
		String sPurchaseOrderNo = elemOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_PURCHASE_ORDER_NO,  sPurchaseOrderNo);
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_SA_NO, strShipAdviceNum);
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, elemOrder.getAttribute(KohlsXMLLiterals.A_ORDER_DATE));
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_CREATETS, elemOrder.getAttribute(KohlsXMLLiterals.A_CREATETS));
		
		// get Order Details
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getSOOrderDetailsTemp());
		Document docSOOrderDetails = api.getOrderDetails(env,
				getOrderDetailsInputXml(sSOOrderNo, KohlsConstant.KOHLS_ENTERPRISE_CODE, KohlsConstant.SO_DOCUMENT_TYPE));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);
		
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_RECEIPT_ID, getReceiptId(KohlsConstant.DSV_NODE_RECEIPT_ID, docSOOrderDetails));
		
		
		
		YFCElement yfcEleShipTo = yfcEleShipAdvice.createChild(KohlsXMLLiterals.E_SHIP_TO);
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, elemShipTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_1));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, elemShipTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_2));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_3, elemShipTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_3));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_4, elemShipTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_4));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_CITY, elemShipTo.getAttribute(KohlsXMLLiterals.A_CITY));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_COMPANY, elemShipTo.getAttribute(KohlsXMLLiterals.A_COMPANY));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, elemShipTo.getAttribute(KohlsXMLLiterals.A_DAY_PHONE));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_COUNTRY, elemShipTo.getAttribute(KohlsXMLLiterals.A_COUNTRY));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_EMAIL_ID, elemShipTo.getAttribute(KohlsXMLLiterals.A_EMAIL_ID));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_STATE, elemShipTo.getAttribute(KohlsXMLLiterals.A_STATE));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, elemShipTo.getAttribute(KohlsXMLLiterals.A_ZIP_CODE));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, elemShipTo.getAttribute(KohlsXMLLiterals.A_FIRST_NAME));
		yfcEleShipTo.setAttribute(KohlsXMLLiterals.A_LAST_NAME, elemShipTo.getAttribute(KohlsXMLLiterals.A_LAST_NAME));
		
		
		YFCElement yfcElemBillTo = yfcEleShipAdvice.createChild(KohlsXMLLiterals.E_BILL_TO);
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, elemBillTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_1));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, elemBillTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_2));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_3, elemBillTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_3));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_4, elemBillTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_4));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_CITY, elemBillTo.getAttribute(KohlsXMLLiterals.A_CITY));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_COMPANY, elemBillTo.getAttribute(KohlsXMLLiterals.A_COMPANY));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, elemBillTo.getAttribute(KohlsXMLLiterals.A_DAY_PHONE));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_COUNTRY, elemBillTo.getAttribute(KohlsXMLLiterals.A_COUNTRY));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_EMAIL_ID, elemBillTo.getAttribute(KohlsXMLLiterals.A_EMAIL_ID));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_STATE, elemBillTo.getAttribute(KohlsXMLLiterals.A_STATE));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, elemBillTo.getAttribute(KohlsXMLLiterals.A_ZIP_CODE));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, elemBillTo.getAttribute(KohlsXMLLiterals.A_FIRST_NAME));
		yfcElemBillTo.setAttribute(KohlsXMLLiterals.A_LAST_NAME, elemBillTo.getAttribute(KohlsXMLLiterals.A_LAST_NAME));
		
		YFCElement yfcElePayMethods = yfcEleShipAdvice.createChild(KohlsXMLLiterals.E_PAYMENT_METHODS);
		YFCElement yfcElePayMethod;
		yfcEleShipAdvice.appendChild(yfcElePayMethods);
		
		YFCElement yfcElePromotions = yfcEleShipAdvice.createChild(KohlsXMLLiterals.E_PROMOTIONS);
		YFCElement yfcElePromotion;
		YFCElement yfcElePromotionExtn;
		yfcEleShipAdvice.appendChild(yfcElePromotions);
		
		YFCElement yfcEleSALines = yfcEleShipAdvice.createChild(KohlsXMLLiterals.E_SA_LINES);
		int strLineCount = nlOrderLine.getLength();
		for(int i = 0; i< strLineCount; i++){
			Element elemOrderLine = (Element)nlOrderLine.item(i);
			Element elemItem = (Element)elemOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
			YFCElement yfcEleSALine = yfcEleSALines.createChild(KohlsXMLLiterals.E_SA_LINE);
			YFCElement yfcEleItem = yfcEleSALine.createChild(KohlsXMLLiterals.E_ITEM);
			
			String strItemId = elemItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, strItemId);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM, elemItem.getAttribute(KohlsXMLLiterals.A_UOM));
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, elemItem.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS));
			// get Item Details	
			env.setApiTemplate(KohlsConstant.API_GET_ITEM_DETAILS, getItemDetailsOutput());
			Document docItemDetails = api.getItemDetails(env, getItemDetailsInputXml(strItemId));
			env.clearApiTemplate(KohlsConstant.API_GET_ITEM_DETAILS);
			
			Element elemItemDetails = docItemDetails.getDocumentElement();
			Element elemItemPrimInfo = (Element) elemItemDetails.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_SIZE, elemItemPrimInfo.getAttribute(KohlsXMLLiterals.A_SIZE_CODE));
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_COLOR , elemItemPrimInfo.getAttribute(KohlsXMLLiterals.A_COLOR_CODE));
			Element elemExtn = (Element) elemItemDetails.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_DESCRIPTION, elemItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC));
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC , elemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC)); 
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC , elemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC));
					
					
			
			Element elemItemAliasList = (Element) elemItemDetails.getElementsByTagName(KohlsXMLLiterals.A_ITEM_ALIAS_LIST).item(0);
			NodeList nlItemAlias =   elemItemAliasList.getElementsByTagName(KohlsXMLLiterals.A_ITEM_ALIAS);
			
			for(int j =0 ; j< nlItemAlias.getLength(); j++){
				Element elemItemAlias = (Element)nlItemAlias.item(j);
				if(elemItemAlias.getAttribute(KohlsXMLLiterals.A_ALIAS_NAME).equals(KohlsConstant.A_UPC_01)){
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_UPC, elemItemAlias.getAttribute(KohlsXMLLiterals.A_ALIAS_VALUE));
				}
			}						
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_ORD_QUANTITY, elemOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY)); 
			String sPrimeLineNo = elemOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, sPrimeLineNo);
			String sSubLineNo = elemOrderLine.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO);
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, sSubLineNo);
			  
			//Rel C Changes for getting UnitCost from catalog rather then Ecomm
			/******************** START ************************************/
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_UNIT_COST, elemItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_UNIT_COST));
			//yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_UNIT_COST, getItemUnitCost(env,sPurchaseOrderNo, sPrimeLineNo, sSubLineNo));
			/********************* END **************************************/
			// get the order line key and call getOrderLineDetails
			
			String strOrderLineKey = elemOrderLine.getAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_LINE_KEY);
			//System.out.println("OrderLineKey::::"+strOrderLineKey);
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("OrderLineKey : " + strOrderLineKey);
			}
			
			String strNoOfInstr = "";
			int inNoOfInst = 0;
			String strInstrType = "";
			String strInstrText = "";
			String strUnitPr = "";
			String strRetailPr = "";
			String strShipSurfChrg = "";
			String chrgName = "";
			String strTaxPer = "";
			String strTaxName = "";
			String strLnGiftRcptID ="";
			
			if(!strOrderLineKey.equals("")){
				
				YFCDocument yfcDocGetOrderLineDetails = YFCDocument.createDocument("OrderLineDetail");
				YFCElement yfcEleGetOrderLineDetails = yfcDocGetOrderLineDetails.getDocumentElement();
				yfcEleGetOrderLineDetails.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, strOrderLineKey);
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Input XML to getOrderLineDetails : " + XMLUtil.getXMLString(yfcDocGetOrderLineDetails.getDocument()));
				}
				env.setApiTemplate("getOrderLineDetails", this.getOrderLineDetailsTemp());
				Document docOutputGetOrderLineDetails = this.api.getOrderLineDetails(env, yfcDocGetOrderLineDetails.getDocument());
				env.clearApiTemplate("getOrderLineDetails");
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("getOrderLineDetails Output XML : " + XMLUtil.getXMLString(docOutputGetOrderLineDetails));
				}						

				Element eleGetOrderLineDetails = docOutputGetOrderLineDetails.getDocumentElement();	
				Element eleGetOrderOut = (Element)eleGetOrderLineDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
				
				Element eleExtn = (Element) eleGetOrderLineDetails.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
				yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM));
				yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE));
				yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO));
				yfcEleSALine.setAttribute(KohlsXMLLiterals.A_GIFT_FLAG, eleGetOrderLineDetails.getAttribute(KohlsXMLLiterals.A_GIFT_FLAG));
				yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ));
				yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE));
				
				
				/******************************* START *********************************************************/
				//Changes for Rel C GiftLine Receipt ID Generation
				String strGftFg = eleGetOrderLineDetails.getAttribute(KohlsXMLLiterals.A_GIFT_FLAG);
				
				
				String sOrderDate = eleGetOrderOut.getAttribute(KohlsXMLLiterals.A_ORDER_DATE);
				String sLineNo="";
				String sReceiptIDValues="";
				String sShipNodeForRcptId="";
				String strPreEncodedLnGiftRcptID="";

				if (strGftFg.equalsIgnoreCase(KohlsConstant.YES)) 
				{
					sLineNo = StringUtil.prepadStringWithZeros(eleGetOrderLineDetails.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO), KohlsConstant.PMLINENO_LEN);
//					sReceiptIDValues = getRegTransValues(env);
//					sShipNodeForRcptId = StringUtil.prepadStringWithZeros(KohlsConstant.DSV_NODE_RECEIPT_ID,KohlsConstant.SHIPNODE_LEN);
//					String sFinalOrdDate = sOrderDate.substring(5,7) + sOrderDate.substring(8,10) + sOrderDate.substring(2,4) + sOrderDate.substring(11,19).replace(":","");
//					strPreEncodedLnGiftRcptID =  sLineNo +  sFinalOrdDate + sShipNodeForRcptId + sReceiptIDValues;
					
					//Start --- Added for SF Case # 00382750 -- OASIS_SUPPORT 06/1/2012
					sShipNodeForRcptId = KohlsConstant.PREENC + KohlsConstant.DSV_NODE_RECEIPT_ID;
					String strPreencRcptId = this.getReceiptId(sShipNodeForRcptId, docSOOrderDetails);
					strPreEncodedLnGiftRcptID =  sLineNo + strPreencRcptId.substring(3);					
					//End --- Added for SF Case # 00382750 -- OASIS_SUPPORT 06/1/2012
					strLnGiftRcptID = getFinalReceiptIDValue(strPreEncodedLnGiftRcptID);
					
					
					if(YFCLogUtil.isDebugEnabled()){
						log.debug("Gift Line Receipt ID : " + strLnGiftRcptID);
					}
					
					
						
					YFCDocument yfcDocChangeOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
					YFCElement yfcEleChangeOrder = yfcDocChangeOrder.getDocumentElement();
					yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE , KohlsConstant.SO_DOCUMENT_TYPE);
					yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE , KohlsConstant.KOHLS_ENTERPRISE_CODE);
					yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER , sSOOrderNo);
					yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE , KohlsConstant.YES);
					
					YFCElement yfcEleOrderLines = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINES);
					YFCElement yfcEleOrderLine = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINE);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, eleGetOrderLineDetails.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, eleGetOrderLineDetails.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, eleGetOrderLineDetails.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_UOM, eleGetOrderLineDetails.getAttribute(KohlsXMLLiterals.A_UOM));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, eleGetOrderLineDetails.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS));
					
					YFCElement yfcEleOrderLineExtn = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_EXTN);
					yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, strLnGiftRcptID);
					yfcEleOrderLine.appendChild(yfcEleOrderLineExtn);
					yfcEleOrderLines.appendChild(yfcEleOrderLine);
					yfcEleChangeOrder.appendChild(yfcEleOrderLines);
					
					
					if(YFCLogUtil.isDebugEnabled()){
						log.debug("ChangeOrder input XML : " + XMLUtil.getXMLString(yfcDocChangeOrder.getDocument()));
					}
					//api.changeOrder(env, yfcDocChangeOrder.getDocument());
				
				}
				/************************************* END *************************************************************/
				
				Element elePaymeths = (Element)eleGetOrderOut.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHODS).item(0);
				Element elePromotions = (Element)eleGetOrderOut.getElementsByTagName(KohlsXMLLiterals.E_PROMOTIONS).item(0);
																			
				if(paymentChk == false){
				NodeList ndPayMeth = elePaymeths.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHOD);
				if(ndPayMeth.getLength()>0){
					paymentChk = true;
					for(int s=0;s<ndPayMeth.getLength();s++){
						Element elePayMeth = (Element) ndPayMeth.item(s);
						yfcElePayMethod = yfcEleShipAdvice.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
						yfcElePayMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, elePayMeth.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE));
						yfcElePayMethod.setAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO, elePayMeth.getAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO));
						yfcElePayMethods.appendChild(yfcElePayMethod);
					}
				}
			
				//Release C changes for Print Collate Adding Kohl's Cash
				YFCDocument yfcDocKohlsCashTable = YFCDocument.createDocument(KohlsXMLLiterals.E_KOHLS_CASH_TABLE);
				YFCElement yfcEleKohlsCashTable = yfcDocKohlsCashTable.getDocumentElement();
				yfcEleKohlsCashTable.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY_FK, sSOOrderHeaderKey);
				
				Document docKohlsCashTableList = api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_CASH_TABLE_LIST, yfcDocKohlsCashTable.getDocument());
				
				NodeList ndlstKohlsCashTableList = docKohlsCashTableList.getElementsByTagName(KohlsXMLLiterals.E_KOHLS_CASH_TABLE);
				int iCashTabelListLen = ndlstKohlsCashTableList.getLength();
				
				if(iCashTabelListLen>0){
					yfcElePayMethod = yfcEleShipAdvice.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
					yfcElePayMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, KohlsConstant.CREDIT_CARD_TYPE_KOHLS_CASH);
					yfcElePayMethods.appendChild(yfcElePayMethod);
				}
				
				}
				
				if(promotionChk == false){
				NodeList ndPromotion = elePromotions.getElementsByTagName(KohlsXMLLiterals.E_PROMOTION);
				if(ndPromotion.getLength()>0){
					promotionChk = true;
					for(int s=0;s<ndPromotion.getLength();s++){						
						Element elePromo = (Element) ndPromotion.item(s);
						Element elePromoExtn = (Element) elePromo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
						yfcElePromotion = yfcEleShipAdvice.createChild(KohlsXMLLiterals.E_PROMOTION);
						yfcElePromotionExtn = yfcElePromotion.createChild(KohlsXMLLiterals.E_EXTN);
						yfcElePromotionExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_PER, elePromoExtn.getAttribute(KohlsXMLLiterals.A_EXTN_COUPON_PER));
						yfcElePromotionExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_AMNT, elePromoExtn.getAttribute(KohlsXMLLiterals.A_EXTN_COUPON_AMNT));
						yfcElePromotion.appendChild(yfcElePromotionExtn);
						yfcElePromotions.appendChild(yfcElePromotion);
					}
				}		
				}
				
				Element eleOrderLineInsts = (Element) eleGetOrderLineDetails.getElementsByTagName(KohlsXMLLiterals.E_INSTRUCTIONS).item(0);
				strNoOfInstr = eleOrderLineInsts.getAttribute(KohlsXMLLiterals.A_NUM_OF_INSTRUCTIONS);
				if(!strNoOfInstr.equals(""))
				inNoOfInst = Integer.parseInt(strNoOfInstr);
				if(inNoOfInst>0){
					NodeList nodeOrderLineInst = eleOrderLineInsts.getElementsByTagName(KohlsXMLLiterals.E_INSTRUCTION);
					for(int p=0; p<nodeOrderLineInst.getLength(); p++){
						Element eleOrderLineInst = (Element) nodeOrderLineInst.item(p);
						strInstrType = eleOrderLineInst.getAttribute(KohlsXMLLiterals.A_INSTRUCTION_TYPE);
						if(strInstrType.equalsIgnoreCase(KohlsConstant.INS_TYPE_BOGO)){
							strInstrText = eleOrderLineInst.getAttribute(KohlsXMLLiterals.A_INSTRUCTION_TEXT);
						}
					}
				}
				
				Element eleOrderLinePrInfo = (Element) eleGetOrderLineDetails.getElementsByTagName(KohlsXMLLiterals.E_LINE_PRICE_INFO).item(0);					
				
				strUnitPr = eleOrderLinePrInfo.getAttribute(KohlsXMLLiterals.A_UNIT_PRICE);
				strRetailPr = eleOrderLinePrInfo.getAttribute(KohlsXMLLiterals.A_RETAIL_PRICE);					
				
				Element eleOrderLineChrgs = (Element) eleGetOrderLineDetails.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGES).item(0);
				NodeList ndLstLineChrg = eleOrderLineChrgs.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGE);
				if(ndLstLineChrg.getLength()>0){
					for(int k=0;k<ndLstLineChrg.getLength();k++){
						Element eleLineChrg = (Element) ndLstLineChrg.item(k);
						chrgName = eleLineChrg.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME);
						if(chrgName.equalsIgnoreCase(KohlsConstant.ChargeNameSurcharge)){
							strShipSurfChrg = eleLineChrg.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT);
						}
					}
				}					
				
				Element eleOrderLineTaxes = (Element) eleGetOrderLineDetails.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAXES).item(0);
				NodeList ndLstLineTax = eleOrderLineTaxes.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAX);
				if(ndLstLineTax.getLength()>0){
					for(int m=0;m<ndLstLineTax.getLength();m++){
						Element eleLineTax = (Element) ndLstLineTax.item(m);
						strTaxName = eleLineTax.getAttribute(KohlsXMLLiterals.A_TAX_NAME);
						if(strTaxName.equalsIgnoreCase(KohlsConstant.TAX_NAME_SALES_TAX)){
							strTaxPer = eleLineTax.getAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE);
						} 
					}
				}							
			}			
			// set the values for Rel C data
			
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_BOGO_INSTR, strInstrText);
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_UNIT_PRICE, strUnitPr);
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_RETAIL_PRICE, strRetailPr);
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_SHIPPING_SURF_CHARGE, strShipSurfChrg);		
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, strTaxPer);
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, strLnGiftRcptID);
			
			}
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_LINE_COUNT, strLineCount);
		return yfcDocShipAdvices.getDocument();
	}
	
/*	private String getItemUnitCost(YFSEnvironment env, String sPurchaseOrderNo,
			String sPrimeLineNo, String sSubLineNo) throws YFSException, RemoteException {
		
		YFCDocument yfcDocGetOrderLineDetailInp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER_LINE_DETAIL);
		YFCElement yfcEleGetOrderLineDetailInp = yfcDocGetOrderLineDetailInp
				.getDocumentElement();
		
		yfcEleGetOrderLineDetailInp.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.PO_DOCUMENT_TYPE);
		yfcEleGetOrderLineDetailInp.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.KOHLS_ENTERPRISE_CODE);
		yfcEleGetOrderLineDetailInp.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, sSubLineNo);
		yfcEleGetOrderLineDetailInp.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, sPrimeLineNo);
		yfcEleGetOrderLineDetailInp.setAttribute(KohlsXMLLiterals.A_ORDERNO, sPurchaseOrderNo);
		
		YFCDocument yfcDocGetOrderLineDetailTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER_LINE);
		YFCElement yfcEleGetOrderLineDetailTemp = yfcDocGetOrderLineDetailTemp
				.getDocumentElement();
		
		yfcEleGetOrderLineDetailTemp.setAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_LINE_KEY, ""); 
		
		
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS, yfcDocGetOrderLineDetailTemp.getDocument());
		Document docOrderLineDtails1 = api.getOrderLineDetails(env, yfcDocGetOrderLineDetailInp.getDocument());
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS);		
		
		// get SO Order Line key
		String sSOOrderLineKey = docOrderLineDtails1.getDocumentElement().getAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_LINE_KEY);
		
		// get SO unit Price Info
		YFCDocument yfcDocGetOrderLineDetailInp2 = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER_LINE_DETAIL);
		YFCElement yfcEleGetOrderLineDetailInp2 = yfcDocGetOrderLineDetailInp2
				.getDocumentElement();		
		yfcEleGetOrderLineDetailInp2.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, sSOOrderLineKey);
		
		YFCDocument yfcDocGetOrderLineDetailTemp2 = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER_LINE);
		YFCElement yfcEleGetOrderLineDetailTemp2 = yfcDocGetOrderLineDetailTemp2
				.getDocumentElement();		
		yfcEleGetOrderLineDetailTemp2.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, ""); 
		YFCElement yfcEleExtn = yfcEleGetOrderLineDetailTemp2.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_DSV_COST, "");
		
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS, yfcDocGetOrderLineDetailTemp2.getDocument());
		Document docOrderLineDtails2 = api.getOrderLineDetails(env, yfcDocGetOrderLineDetailInp2.getDocument());
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS);	
		Element eleExtn = (Element) docOrderLineDtails2.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		
		return eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_DSV_COST);
	}*/
	private Document getSOOrderDetailsTemp() {

		YFCDocument yfcDocGetOrderDetailTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderDetail = yfcDocGetOrderDetailTemp
				.getDocumentElement();
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");	 
		//for receipt id 
		YFCElement yfcElemNotes = yfcEleGetOrderDetail.createChild(KohlsXMLLiterals.E_NOTES);
		YFCElement yfcElemNote = yfcElemNotes.createChild(KohlsXMLLiterals.E_NOTE);
		yfcElemNote.setAttribute(KohlsXMLLiterals.A_REASON_CODE, "");
		yfcElemNote.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");		
			
		return yfcDocGetOrderDetailTemp.getDocument();
	
	}
	private String getReceiptId(String sEFC, Document docGetOrderDetials) throws RemoteException, TransformerException {
		// getOrderReleaseDetails 		
		Element elemNotes = (Element) docGetOrderDetials.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
		NodeList nlNote = elemNotes.getElementsByTagName(KohlsXMLLiterals.E_NOTE);
		for(int i=0; i< nlNote.getLength(); i++){
			Element elemNote = (Element) nlNote.item(i);
			if(elemNote.getAttribute(KohlsXMLLiterals.A_REASON_CODE).equals(sEFC)){				
				return elemNote.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);				
			}
		}
		return null;		
	}
	private Document getItemDetailsInputXml(String strItemId) {
		YFCDocument yfcItem = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ITEM);
		yfcItem.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ITEM_ID,
				strItemId);
		yfcItem.getDocumentElement().setAttribute(
				KohlsXMLLiterals.A_ORGANIZATION_CODE,
				KohlsConstant.ITEM_ORGANIZATION_CODE);
		yfcItem.getDocumentElement().setAttribute(KohlsXMLLiterals.A_UOM,
				KohlsConstant.UNIT_OF_MEASURE);
		return yfcItem.getDocument();
	}
	private Document getItemDetailsOutput() {
		YFCDocument yfcDocItemTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcElemItemTemp = yfcDocItemTemp.getDocumentElement();
		yfcElemItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		
		YFCElement yfcElemPrimInfo = yfcElemItemTemp.createChild(KohlsXMLLiterals.E_PRIMARY_INFORMATION);
		yfcElemPrimInfo.setAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC, "");
		yfcElemPrimInfo.setAttribute(KohlsXMLLiterals.A_SIZE_CODE, "");
		yfcElemPrimInfo.setAttribute(KohlsXMLLiterals.A_COLOR_CODE, "");
		yfcElemPrimInfo.setAttribute(KohlsXMLLiterals.A_ITEM_UNIT_COST, "");
		
		YFCElement yfcElemItemAliasList = yfcElemItemTemp.createChild(KohlsXMLLiterals.A_ITEM_ALIAS_LIST);
		YFCElement yfcElemItemAlias = yfcElemItemAliasList.createChild(KohlsXMLLiterals.A_ITEM_ALIAS);
		yfcElemItemAlias.setAttribute(KohlsXMLLiterals.A_ALIAS_NAME, "");
		yfcElemItemAlias.setAttribute(KohlsXMLLiterals.A_ALIAS_VALUE, "");

		YFCElement yfcElemExtn = yfcElemItemTemp.createChild(KohlsXMLLiterals.E_EXTN);
		//yfcElemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_VENDOR_STYLE_NO, "");
		yfcElemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC, "");
		yfcElemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC, "");
		
		
		return yfcDocItemTemp.getDocument();
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
	
	private Document getOrderDetailsInputXml(String strPurchaseOrdNum,
			String strEnterpriseCode, String soDocumentType) {
		YFCDocument yfcDocGetOrderDetail = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderDetail = yfcDocGetOrderDetail
				.getDocumentElement();
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE,
				soDocumentType);
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE,
				strEnterpriseCode);
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER,
				strPurchaseOrdNum);
		return yfcDocGetOrderDetail.getDocument();
		
	}
	
	
	private Document getOrderListTemp(){
		YFCDocument yfcDocGetOrderList = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDERLIST);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderList.getDocumentElement();
		YFCElement yfcEleGetOrderDetail= yfcDocGetOrderList.createElement(KohlsXMLLiterals.E_ORDER);
		/*YFCDocument yfcDocGetOrderDetailTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);*/
		//YFCElement yfcEleGetOrderDetail = yfcDocGetOrderDetailTemp.getDocumentElement();
		
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE,
				"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER,
				"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE,
				"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_BUYER_ORGANIZATION_CODE,
		"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE,
		"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE,
		"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ORDER_DATE,
		"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_CREATETS,
		"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE,
		"");
				
		// OrderLines
		YFCElement yfcElemOrderLines = yfcEleGetOrderDetail.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcElemOrderLine = yfcElemOrderLines.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, ""); 
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_LINE_KEY, "");
		// chained order
		YFCElement yfcElemChainedFromOrderLine = yfcElemOrderLine.createChild(KohlsXMLLiterals.E_CHAINED_FROM_ORDER_LINE);
		yfcElemChainedFromOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		YFCElement yfcElemChainedOrder = yfcElemChainedFromOrderLine.createChild(KohlsXMLLiterals.E_ORDER);
		yfcElemChainedOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcElemChainedOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, "");		
		
		// item
		YFCElement yfcElemItem = yfcElemOrderLine.createChild(KohlsXMLLiterals.E_ITEM);
		yfcElemItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcElemItem.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcElemItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		
		
		/*yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS, "");
		YFCElement yfsElePriceInfo =yfcEleOrder.createChild(KohlsXMLLiterals.E_PRICE_INFO);
		yfcEleOrder.appendChild(yfsElePriceInfo);
		YFCElement yfcEleOrderLines = yfcEleOrder.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcEleOrderLine = yfcEleOrderLines.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		YFCElement yfcEleLineCharges = yfcEleOrderLine.createChild(KohlsXMLLiterals.E_LINE_CHARGES);
		YFCElement yfcEleLineCharge = yfcEleLineCharges.createChild(KohlsXMLLiterals.E_LINE_CHARGE);
		yfcEleLineCharges.appendChild(yfcEleLineCharge);
		YFCElement yfcEleLineTaxes = yfcEleOrderLine.createChild(KohlsXMLLiterals.E_LINE_TAXES);
		YFCElement yfcEleLineTax = yfcEleLineTaxes.createChild(KohlsXMLLiterals.E_LINE_TAX);
		yfcEleLineTaxes.appendChild(yfcEleLineTax);
		yfcEleOrderLines.appendChild(yfcEleOrderLine);
		yfcEleOrder.appendChild(yfcEleOrderLines);
		YFCElement yfcElePaymentMethods = yfcEleOrder.createChild(KohlsXMLLiterals.E_PAYMENT_METHODS);
		YFCElement yfcElePaymentMethod = yfcEleOrderLines.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
		yfcElePaymentMethod.setAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE, "") ;
		yfcElePaymentMethod.setAttribute(KohlsXMLLiterals.A_SVC_NO, "") ;
		yfcElePaymentMethod.setAttribute(KohlsXMLLiterals.A_DISPLAY_SVC_NO, "") ;	
		yfcElePaymentMethod.setAttribute(KohlsXMLLiterals.A_TOTAL_CHARGED, "") ;	
		yfcElePaymentMethods.appendChild(yfcElePaymentMethod);
		YFCElement yfcEleOrderHoldTypes = yfcEleOrder.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
		YFCElement yfcEleOrderHoldType = yfcEleOrderLines.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, "");
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleOrderHoldTypes.appendChild(yfcEleOrderHoldType);
		YFCElement yfcEleHeaderCharges = yfcEleOrder.createChild(KohlsXMLLiterals.A_HEADER_CHARGES);
		YFCElement yfcEleHeaderCharge = yfcEleOrderLines.createChild(KohlsXMLLiterals.A_HEADER_CHARGE);
		yfcEleHeaderCharges.appendChild(yfcEleHeaderCharge);
		YFCElement yfcEleHeaderTaxes = yfcEleOrder.createChild(KohlsXMLLiterals.E_HEADER_TAXES);
		YFCElement yfcEleHeaderTax = yfcEleOrderLines.createChild(KohlsXMLLiterals.E_HEADER_TAX);
		yfcEleHeaderTaxes.appendChild(yfcEleHeaderTax);
		yfcEleOrder.appendChild(yfcElePaymentMethods);
		yfcEleOrder.appendChild(yfcEleOrderHoldTypes);
		yfcEleOrder.appendChild(yfcEleHeaderCharges);
		yfcEleOrder.appendChild(yfcEleHeaderTaxes);
		yfcElemOrderDetailsTemp.appendChild(yfcEleOrder);*/
		yfcElemOrderDetailsTemp.appendChild(yfcEleGetOrderDetail);
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("KohlsPOSentToCHUBAPI.getOrderListTemp()"+XMLUtil.getXMLString(yfcDocGetOrderList.getDocument()));
		}

		
			return yfcDocGetOrderList.getDocument();
	}
	
	private Document getOrderDetailsTemp() {
		YFCDocument yfcDocGetOrderDetailTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderDetail = yfcDocGetOrderDetailTemp
				.getDocumentElement();
		
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE,
				"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER,
				"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE,
				"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_BUYER_ORGANIZATION_CODE,
		"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE,
		"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE,
		"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ORDER_DATE,
		"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_CREATETS,
		"");
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE,
		"");
				
		// OrderLines
		YFCElement yfcElemOrderLines = yfcEleGetOrderDetail.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcElemOrderLine = yfcElemOrderLines.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, ""); 
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_LINE_KEY, "");
		// chained order
		YFCElement yfcElemChainedFromOrderLine = yfcElemOrderLine.createChild(KohlsXMLLiterals.E_CHAINED_FROM_ORDER_LINE);
		yfcElemChainedFromOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		YFCElement yfcElemChainedOrder = yfcElemChainedFromOrderLine.createChild(KohlsXMLLiterals.E_ORDER);
		yfcElemChainedOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcElemChainedOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, "");		
		
		// item
		YFCElement yfcElemItem = yfcElemOrderLine.createChild(KohlsXMLLiterals.E_ITEM);
		yfcElemItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcElemItem.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcElemItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		
		
		return yfcDocGetOrderDetailTemp.getDocument();
	}
	
	private Document getOrderLineDetailsTemp() {

		YFCDocument yfcDocGetOrderLineTemp = YFCDocument.createDocument("OrderLine");
		YFCElement yfcEleGetOrdLnTemp = yfcDocGetOrderLineTemp.getDocumentElement();
		yfcEleGetOrdLnTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");		
		yfcEleGetOrdLnTemp.setAttribute(KohlsXMLLiterals.A_GIFT_FLAG, "");	
		yfcEleGetOrdLnTemp.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcEleGetOrdLnTemp.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcEleGetOrdLnTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcEleGetOrdLnTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleGetOrdLnTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		
		YFCElement yfcEleOrder = yfcEleGetOrdLnTemp.createChild(KohlsXMLLiterals.E_ORDER);	
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, "");
		yfcEleGetOrdLnTemp.appendChild(yfcEleOrder);	
				
		YFCElement yfcElePaymentMethodsTemp = yfcEleOrder.createChild(KohlsXMLLiterals.E_PAYMENT_METHODS);
		YFCElement yfcElePaymentMethodTemp = yfcElePaymentMethodsTemp.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, "");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO, "");		
		yfcElePaymentMethodsTemp.appendChild(yfcElePaymentMethodTemp);
		yfcEleOrder.appendChild(yfcElePaymentMethodsTemp);
		
		YFCElement yfcElePromotionsTemp = yfcEleOrder.createChild(KohlsXMLLiterals.E_PROMOTIONS);
		YFCElement yfcElePromotionTemp = yfcElePromotionsTemp.createChild(KohlsXMLLiterals.E_PROMOTION);
		yfcElePromotionTemp.setAttribute(KohlsXMLLiterals.A_PROMOTION_ID, "");
		YFCElement yfcElePromotionExtnTemp = yfcElePromotionTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcElePromotionExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_PER, "");
		yfcElePromotionExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_AMNT, "");
		yfcElePromotionTemp.appendChild(yfcElePromotionExtnTemp);
		yfcElePromotionsTemp.appendChild(yfcElePromotionTemp);
		yfcEleOrder.appendChild(yfcElePromotionsTemp);
		
		YFCElement yfcEleLinePriceInfoTemp = yfcEleGetOrdLnTemp.createChild(KohlsXMLLiterals.E_LINE_PRICE_INFO);
		yfcEleLinePriceInfoTemp.setAttribute(KohlsXMLLiterals.A_RETAIL_PRICE, "");
		yfcEleLinePriceInfoTemp.setAttribute(KohlsXMLLiterals.A_UNIT_PRICE, "");		
		yfcEleGetOrdLnTemp.appendChild(yfcEleLinePriceInfoTemp);
		
		YFCElement yfcEleLineChargesTemp = yfcEleGetOrdLnTemp.createChild(KohlsXMLLiterals.E_LINE_CHARGES);
		YFCElement yfcEleLineChargeTemp = yfcEleLineChargesTemp.createChild(KohlsXMLLiterals.E_LINE_CHARGE);
		yfcEleLineChargeTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, "");
		yfcEleLineChargeTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, "");		
		yfcEleLineChargesTemp.appendChild(yfcEleLineChargeTemp);
		yfcEleGetOrdLnTemp.appendChild(yfcEleLineChargesTemp);
		
		YFCElement yfcEleLineTaxesTemp = yfcEleGetOrdLnTemp.createChild(KohlsXMLLiterals.E_LINE_TAXES);
		YFCElement yfcEleLineTaxTemp = yfcEleLineTaxesTemp.createChild(KohlsXMLLiterals.E_LINE_TAX);
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX_NAME, "");
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, "");		
		yfcEleLineTaxesTemp.appendChild(yfcEleLineTaxTemp);
		yfcEleGetOrdLnTemp.appendChild(yfcEleLineTaxesTemp);
		
		YFCElement yfcEleInstrsTemp = yfcEleGetOrdLnTemp.createChild(KohlsXMLLiterals.E_INSTRUCTIONS);
		YFCElement yfcEleInstrTemp = yfcEleInstrsTemp.createChild(KohlsXMLLiterals.E_INSTRUCTION);
		yfcEleInstrTemp.setAttribute(KohlsXMLLiterals.A_INSTRUCTION_TYPE, "");
		yfcEleInstrTemp.setAttribute(KohlsXMLLiterals.A_INSTRUCTION_TEXT, "");		
		yfcEleInstrsTemp.appendChild(yfcEleInstrTemp);
		yfcEleGetOrdLnTemp.appendChild(yfcEleInstrsTemp);
		
		YFCElement yfcEleExtn = yfcEleGetOrdLnTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_DSV_COST, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ, "");
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getOrderLineDetails Template : " + XMLUtil.getXMLString(yfcDocGetOrderLineTemp.getDocument()));
		}

		return yfcDocGetOrderLineTemp.getDocument();
	}
	
	/** 
	 * Commented for Iteration 4 changes
	 */
	
	public String getFinalReceiptIDValue(String PreEncodedRcptId)
	{
		String RcptId = "";
		try
		{
			RcptId = PreEncodedRcptId;
			//Calculate the Mod 10 Check Digit Value
			int CheckDigit = KohlsUtil.computeMod10CheckDigit(RcptId);
			
			
			//Append Check Digit to the Receipt ID
			String RcpId = RcptId + CheckDigit;
			
			//Compute 10's Compliment 
			RcptId = KohlsUtil.computeTensComplement(RcpId);
			
			
			//Obtain the Check Digit for the transformed Receipt ID
			CheckDigit = KohlsUtil.computeMod10CheckDigit(RcptId);
			
			
			//Append Check Digit to the Receipt ID
			RcptId = RcptId + CheckDigit;
			
			//Format Receipt ID Value with -
			RcptId = KohlsUtil.formatReceiptID(RcptId);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
		return  RcptId;
	}

	
	/** 
	 * Commented for Iteration 4 changes
	 */
	private String getRegTransValues(YFSEnvironment env) {
	
		String sRegTransValues="";
		try
		{
			String sRegTransNo = this.getNextRegTransNo(env, this.m_conn);
			
			/*******Added to avoid sending TranID ending with 0000 to COSA**********/
			int iReceiptIDValues = Integer.parseInt(sRegTransNo);
			int modRcptID = (iReceiptIDValues % 10000);
			if(0==modRcptID){							
				sRegTransNo = getRegTransValues(env);
			}
			/******** END ***********/
			sRegTransValues = StringUtil.prepadStringWithZeros(sRegTransNo, KohlsConstant.REG_TRANS_LEN);
			//sRegTransValues = StringUtil.prepadStringWithZeros(this.getNextRegTransNo(env, this.m_conn), KohlsConstant.REG_TRANS_LEN);
	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return sRegTransValues;
	}
	
	/**
	 * This method is used in KohlsSendToWMoSAgent to generate the Register & Transaction No for Gift Receipt ID */

	public String getNextRegTransNo(YFSEnvironment env, Connection m_conn) throws SQLException {

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("############## Getting Next Register,Transaction No ##################");
		}

		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String sRegisterTransSeqNo = null;
		try{
			String sql = SQL_ORACLE_REG_TRANS_SEQ_NO;

			stmt = m_conn.prepareStatement(sql);
			rSet = stmt.executeQuery();

			if (rSet.next()) {
				sRegisterTransSeqNo = rSet.getString(1);
			}

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("The sequential Register Transaction Number : " + sRegisterTransSeqNo);
			}
		}catch(SQLException SqlEx){
			
			SqlEx.printStackTrace();
			
		}finally{
			if(rSet!=null){
				rSet.close();
			}
			if(stmt!=null){
				stmt.close();
			}
		}

		return sRegisterTransSeqNo;

	}
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		 
		
	}
	
	

	
}
