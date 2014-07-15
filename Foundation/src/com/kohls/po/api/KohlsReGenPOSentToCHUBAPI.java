package com.kohls.po.api;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
 * This class creates ship advices once the PO is released.
 * These ship advices are sent to CHUB.
 * This class is used by KohlsSendPOToCHUBSyncService service.  
 * @author Priyadarshini
 *
 */
public class KohlsReGenPOSentToCHUBAPI implements YIFCustomApi{
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KohlsReGenPOSentToCHUBAPI.class.getName());
	private  YIFApi api;
	/**
	 * constructor initializes api
	 * 
	 * @throws YIFClientCreationException
	 */
	public KohlsReGenPOSentToCHUBAPI() throws YIFClientCreationException {

		api = YIFClientFactory.getInstance().getLocalApi();
	}
	/**
	 * Creates shipadvices which are sent to CHUB after the 
	 * PO is released.
	 * @param env
	 * @param inXML
	 * @return
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws TransformerException
	 */
	public void reGenerateShipAdvices(YFSEnvironment env, Document inXML)
	throws YFSException, RemoteException, TransformerException {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered Send PO to CHUB Module--------------------------> ");
		}
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input XML  : "
					+ KohlsUtil.extractStringFromDocument(inXML));
		}

		NodeList nodeShipAdvice = inXML.getElementsByTagName(
				KohlsXMLLiterals.E_SHIPMENT_ADVICE);
		if(nodeShipAdvice.getLength()>0){

			for(int i=0;i<nodeShipAdvice.getLength();i++){
				Element elemShipAdvice = (Element)nodeShipAdvice.item(i);

				String strPurchaseOrdNum = elemShipAdvice
				.getAttribute(KohlsXMLLiterals.A_PURCHASE_ORDER_NO);

				// get Order Details
				env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemp());
				Document docOrderDetails = api.getOrderDetails(env,
						getOrderDetailsInputXml(strPurchaseOrdNum, KohlsConstant.KOHLS_ENTERPRISE_CODE, KohlsConstant.PO_DOCUMENT_TYPE));
				env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);

				Document docShipAdvices = getShipAdvices(env, docOrderDetails,
						elemShipAdvice);
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Ship Advice sent to CHUB : "
							+ KohlsUtil.extractStringFromDocument(docShipAdvices));
				}
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("<---------------Exit Send PO to CHUB Module--------------------------> ");
				}

				// put the xml to jms queue
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Release Message to WMoS : " + XMLUtil.getXMLString(docShipAdvices));
				}
				this.api.executeFlow(env, "KohlsReGenSentToCHUBService", docShipAdvices);		
			}
		}
	}

	private Document getShipAdvices(YFSEnvironment env, Document docOrderDetails, Element elemShipAdvice) 
	throws YFSException, RemoteException, TransformerException {


		boolean promotionChk = false;
		boolean paymentChk = false;

		//String strShipAdviceNum = elemShipAdvice.getAttribute(KohlsXMLLiterals.A_SA_NO);

		Element elemOrder = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);		

		Element elemShipTo = (Element)elemOrder.getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO).item(0);

		Element elemBillTo = (Element)elemOrder.getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO).item(0);	

		String strOrHeadKey = elemOrder.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);

		NodeList nlOrderLine =  docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

		Element elemChainedFromOrderLine = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_CHAINED_FROM_ORDER_LINE).item(0);
		
		YFCDocument yfcDocGetOrderReleaseDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE_DETAIL);
		YFCElement yfcEleGetOrderDetails = yfcDocGetOrderReleaseDetails.getDocumentElement();
		yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrHeadKey);
		yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_RELEASE_NO, "1");
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input XML to getOrderReleaseDetails : " + XMLUtil.getXMLString(yfcDocGetOrderReleaseDetails.getDocument()));
		}
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_DETAILS, this.getOrderReleaseDetailsTemp());
		//System.out.println("getOrderReleaseDetails Template : " + XMLUtil.getXMLString(this.getOrderReleaseDetailsTemplate()));
		Document docOutputGetOrderReleaseDetails = this.api.getOrderReleaseDetails(env, yfcDocGetOrderReleaseDetails.getDocument());
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_DETAILS);
		//System.out.println("getOrderRelease Details : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getOrderDetails Output XML : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));
		}

		Element eleGetOrRelDetails = docOutputGetOrderReleaseDetails.getDocumentElement();
		String strSANo = eleGetOrRelDetails.getAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO);

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
		yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_SA_NO, strSANo);
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
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_DESCRIPTION, elemItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC));
			Element elemExtn = (Element) elemItemDetails.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
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
			Document docOrderLineDtls = getSOOrderLineDtls(env,sPurchaseOrderNo, sPrimeLineNo, sSubLineNo);
			Element eleExtn = (Element) docOrderLineDtls.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_UNIT_COST, elemItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_UNIT_COST));
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM));
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO));
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE));
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE));
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ));
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID));
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_GIFT_FLAG, docOrderLineDtls.getDocumentElement().getAttribute(KohlsXMLLiterals.A_GIFT_FLAG));

			Element eleGetOrderLineDetails = docOrderLineDtls.getDocumentElement();	
			Element eleGetOrderOut = (Element)eleGetOrderLineDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);

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
					if(chrgName.equalsIgnoreCase(KohlsConstant.ShippingChargeCategory)){
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

			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_BOGO_INSTR, strInstrText);
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_UNIT_PRICE, strUnitPr);
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_RETAIL_PRICE, strRetailPr);
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_SHIPPING_SURF_CHARGE, strShipSurfChrg);		
			yfcEleSALine.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, strTaxPer);
		}

	yfcEleShipAdvice.setAttribute(KohlsXMLLiterals.A_LINE_COUNT, strLineCount);
	return yfcDocShipAdvices.getDocument();
}
	
	
private Document getSOOrderLineDtls(YFSEnvironment env, String sPurchaseOrderNo,
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

	/*		YFCDocument yfcDocGetOrderLineDetailTemp2 = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER_LINE);
		YFCElement yfcEleGetOrderLineDetailTemp2 = yfcDocGetOrderLineDetailTemp2
				.getDocumentElement();		
		yfcEleGetOrderLineDetailTemp2.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, ""); 
		yfcEleGetOrderLineDetailTemp2.setAttribute(KohlsXMLLiterals.A_GIFT_FLAG, "");
		YFCElement yfcEleExtn = yfcEleGetOrderLineDetailTemp2.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_DSV_COST, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, "");*/

	env.setApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS, getOrderLineDetailsTemp());
	Document docOrderLineDtails2 = api.getOrderLineDetails(env, yfcDocGetOrderLineDetailInp2.getDocument());
	env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS);	

	return docOrderLineDtails2;
}
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

private Document getOrderReleaseDetailsTemp() {

	YFCDocument yfcDocGetOrderReleaseDetailsTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE);
	YFCElement yfcEleGetOrderReleaseDetailsTemp = yfcDocGetOrderReleaseDetailsTemp.getDocumentElement();
	yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO, "");	

	return yfcDocGetOrderReleaseDetailsTemp.getDocument();
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
	yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY,
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

	YFCElement yfcElemPersonInfoShipTo = yfcEleGetOrderDetail.createChild(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO);
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_3, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_4, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_CITY, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_COMPANY, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_STATE, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_COUNTRY, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, "");
	yfcElemPersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_EMAIL_ID, "");
	yfcEleGetOrderDetail.appendChild(yfcElemPersonInfoShipTo);

	YFCElement yfcElemPersonInfoBillTo = yfcEleGetOrderDetail.createChild(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO);
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_3, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_4, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_CITY, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_COMPANY, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_STATE, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_COUNTRY, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, "");
	yfcElemPersonInfoBillTo.setAttribute(KohlsXMLLiterals.A_EMAIL_ID, "");
	yfcEleGetOrderDetail.appendChild(yfcElemPersonInfoBillTo);


	YFCElement yfcElePaymentMethodsTemp = yfcEleGetOrderDetail.createChild(KohlsXMLLiterals.E_PAYMENT_METHODS);
	YFCElement yfcElePaymentMethodTemp = yfcElePaymentMethodsTemp.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
	yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, "");
	yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO, "");		
	yfcElePaymentMethodsTemp.appendChild(yfcElePaymentMethodTemp);
	yfcEleGetOrderDetail.appendChild(yfcElePaymentMethodsTemp);

	YFCElement yfcElePromotionsTemp = yfcEleGetOrderDetail.createChild(KohlsXMLLiterals.E_PROMOTIONS);
	YFCElement yfcElePromotionTemp = yfcElePromotionsTemp.createChild(KohlsXMLLiterals.E_PROMOTION);
	yfcElePromotionTemp.setAttribute(KohlsXMLLiterals.A_PROMOTION_ID, "");
	YFCElement yfcElePromotionExtnTemp = yfcElePromotionTemp.createChild(KohlsXMLLiterals.E_EXTN);
	yfcElePromotionExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_PER, "");
	yfcElePromotionExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_AMNT, "");
	yfcElePromotionTemp.appendChild(yfcElePromotionExtnTemp);
	yfcElePromotionsTemp.appendChild(yfcElePromotionTemp);
	yfcEleGetOrderDetail.appendChild(yfcElePromotionsTemp);

	// chained order
	YFCElement yfcElemChainedFromOrderLine = yfcElemOrderLine.createChild(KohlsXMLLiterals.E_CHAINED_FROM_ORDER_LINE);
	yfcElemChainedFromOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
	YFCElement yfcElemChainedOrder = yfcElemChainedFromOrderLine.createChild(KohlsXMLLiterals.E_ORDER);
	yfcElemChainedOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, "");	
	yfcElemChainedOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");

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
	
	//Start --- Added by OASIS_SUPPORT 08/05/2013 PMR 15872 379 000
	yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, "");
	//End --- Added by OASIS_SUPPORT 08/05/2013 PMR 15872 379 000
	if (YFCLogUtil.isDebugEnabled()) {
		log.debug("getOrderLineDetails Template : " + XMLUtil.getXMLString(yfcDocGetOrderLineTemp.getDocument()));
	}

	return yfcDocGetOrderLineTemp.getDocument();
}
@Override
public void setProperties(Properties arg0) throws Exception {


}

}
