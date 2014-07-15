package com.kohls.oms.agent;


import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.XPathUtil;
import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseTaskAgent;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/** This is a Task Q Based agent invoked in Shipment Pipeline to Send the Ship Alone shipment details to 
 * WMoS and order Status update to E-Commerce.
 * 
 *  @author Prashanth T G
 *  
 *  Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 *  
 *  INPUT XML <TaskQueue TaskQKey="" TransactionKey="" DataKey="" DataType="" AvailableDate="" Lockid="" Createts="" Modifyts="" 
 *  			Createuserid="" Modifyuserid="" Createprogid="" Modifyprogid="" > <TransactionFilters DocumentParamsKey="" 
 *  			DocumentType="" ProcessType="" ProcessTypeKey="" TransactionId="" TransactionKey="" /> <TaskQueue/> 
 *  */

public class KohlsSAWMoSMsgGeneratorAPI extends YCPBaseTaskAgent {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsSAWMoSMsgGeneratorAPI.class.getName());
	private YIFApi api;


	public KohlsSAWMoSMsgGeneratorAPI() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}


	public void setProperties(Properties arg0) throws Exception { 

	}

	@Override
	public Document executeTask(YFSEnvironment env, Document inXML)
	throws Exception {
		// TODO Auto-generated method stub

		Element eleRoot = inXML.getDocumentElement();
		//Order Broker start
		String strShipVia="";
		String strExIsPOBox="";
		String strExIsMilitary="";
		String strIsHazardous = "N";
		String strCartonType = KohlsConstant.CARTON_TYPE_BOX; // changed to BOX for PMR :05616,379,000 
		String strLineType = "";
		//String strGiftwrap = "";
		String strExtnRG = "";
		String strProdLine = "";
		String sProductLine = "";
		Element eleItemExtn = null;
		Element eleItemPrimInfo = null;		
		Element eleItemAliasList = null;
		Element eleItemAlias = null;
		int strExtWrapCnt = 0;
		String strExtnWrapTo = "";
		String strExtnWrapCode = "";
		String strExtnWrapToVal = "";
		boolean isSingleLine = false;
		String strShipNo="";
		String strSellerOrgCode="";
		String strShipNode="";
		String strReceiptId = "";
		String strPreEnReceiptId = "";
		
		try{
		//Order Broker end
			
			
			NodeList ndShipment = eleRoot.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);
			if(ndShipment.getLength()>0){
				for(int p=0; p<ndShipment.getLength(); p++){
					Element eleRelease = (Element) ndShipment.item(p);
					strShipNo = eleRelease.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
					strSellerOrgCode = eleRelease.getAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE);
					strShipNode = eleRelease.getAttribute(KohlsXMLLiterals.A_SHIPNODE);	

			YFCDocument yfcDocGetShipmentDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			YFCElement yfcEleGetShipmentDetails = yfcDocGetShipmentDetails.getDocumentElement();
			yfcEleGetShipmentDetails.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipNo);	
			yfcEleGetShipmentDetails.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, strSellerOrgCode);
			yfcEleGetShipmentDetails.setAttribute(KohlsXMLLiterals.A_SHIPNODE, strShipNode);

			int strAccQty = 0;		

		
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Input XML to getShipmentDetails : " + XMLUtil.getXMLString(yfcDocGetShipmentDetails.getDocument()));
			}
			env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS, this.getShipmentDetailsTemp());
			Document docOutputGetShipmentDetails = this.api.getShipmentDetails(env, yfcDocGetShipmentDetails.getDocument());
			env.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS);
			
			if (YFCLogUtil.isDebugEnabled()) {				
				log.debug("GetShipment Details Output XML : " + XMLUtil.getXMLString(docOutputGetShipmentDetails));
			}
			
			
			
			Element eleGetShipmentDtlOutput = docOutputGetShipmentDetails.getDocumentElement();
			Element eleToAddress = (Element) eleGetShipmentDtlOutput.getElementsByTagName(KohlsXMLLiterals.E_TO_ADDRESS).item(0);
			Element eleShipExtn = (Element) eleGetShipmentDtlOutput.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);

			//Preparing Order Release Message for WMoS
			YFCDocument yfcDocOrderRelease = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE);
			YFCElement yfcEleOrderRelease = yfcDocOrderRelease.getDocumentElement();
			yfcEleOrderRelease.setAttribute(KohlsXMLLiterals.A_SCAC, eleGetShipmentDtlOutput.getAttribute(KohlsXMLLiterals.A_SCAC));
			yfcEleOrderRelease.setAttribute(KohlsXMLLiterals.A_SHIPNODE, eleGetShipmentDtlOutput.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
			yfcEleOrderRelease.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, 
					eleGetShipmentDtlOutput.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE));
			
			String sShipNode = eleGetShipmentDtlOutput.getAttribute(KohlsXMLLiterals.A_SHIPNODE);

			YFCElement yfcEleCreateShipment = yfcDocOrderRelease.createElement(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO);
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, eleToAddress.getAttribute(KohlsXMLLiterals.A_FIRST_NAME));
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_LAST_NAME, eleToAddress.getAttribute(KohlsXMLLiterals.A_LAST_NAME));
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, eleToAddress.getAttribute(KohlsXMLLiterals.A_ADD_LINE_1));
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, eleToAddress.getAttribute(KohlsXMLLiterals.A_ADD_LINE_2));
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_CITY, eleToAddress.getAttribute(KohlsXMLLiterals.A_CITY));
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_STATE, eleToAddress.getAttribute(KohlsXMLLiterals.A_STATE));
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_COUNTRY, eleToAddress.getAttribute(KohlsXMLLiterals.A_COUNTRY));
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, eleToAddress.getAttribute(KohlsXMLLiterals.A_ZIP_CODE));
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, eleToAddress.getAttribute(KohlsXMLLiterals.A_DAY_PHONE));
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_EMAIL_ID, eleToAddress.getAttribute(KohlsXMLLiterals.A_EMAIL_ID));
			yfcEleOrderRelease.appendChild(yfcEleCreateShipment);

			YFCElement yfcEleExtn = yfcDocOrderRelease.createElement(KohlsXMLLiterals.E_EXTN);
			yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO, eleGetShipmentDtlOutput.getAttribute(KohlsXMLLiterals.A_PICKTICKET_NO));
			yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_SA);
			yfcEleOrderRelease.appendChild(yfcEleExtn);

			YFCElement yfcEleOrder = yfcDocOrderRelease.createElement(KohlsXMLLiterals.E_ORDER);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleGetShipmentDtlOutput.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
			yfcEleOrderRelease.appendChild(yfcEleOrder);

			YFCElement yfcEleOrderLines = yfcDocOrderRelease.createElement(KohlsXMLLiterals.E_ORDER_LINES);
			//yfcEleOrder.appendChild(yfcEleOrderLines);
			yfcEleOrderRelease.appendChild(yfcEleOrderLines);
			
			
			//Element eleInfoShipTo = (Element) eleGetShipmentDtlOutput.getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO).item(0);
			
			//Order Broker start
			Element eleExtnInfoShipTo = (Element) eleToAddress.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			strExIsPOBox = eleExtnInfoShipTo.getAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX);
			strExIsMilitary = eleExtnInfoShipTo.getAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY);
			//Order Broker end	

			// Document Create for Release Status Message to Ecommerce
			YFCDocument yfcDocOrderStatusToECommXML = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
			YFCElement yfcEleOrderStatusToECommXML = yfcDocOrderStatusToECommXML.getDocumentElement();

			yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
			yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleGetShipmentDtlOutput.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));

			YFCElement yfcEleLinesOrderStatusToECommXML = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_LINES);
			yfcEleOrderStatusToECommXML.appendChild(yfcEleLinesOrderStatusToECommXML);

			NodeList ndlstShipmentLines = docOutputGetShipmentDetails.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
			String RG = "";
			//Order Broker start
			if(ndlstShipmentLines.getLength()==1){
				isSingleLine = true;
			}
			
			String strNoOfInstr = "";
			int inNoOfInst = 0;
			String strInstrType = "";
			String strInstrText = "";
			String strUnitPr = "";
			String strRetailPr = "";
			String strGWChrg = "";
			String strShipSurfChrg = "";
			String chrgName = "";
			String strTaxPer = "";
			String strTaxName = "";
			String sDeliveryMethod = "";
			
			//Order Broker end
			for (int i = 0; i < ndlstShipmentLines.getLength(); i++) {

				Element eleShipmentLine = (Element) ndlstShipmentLines.item(i);	
				//Assuming that a shipmentLine is associated with only one OrderLine
				Element eleOrderLine = (Element) eleShipmentLine.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE).item(0);
				Element eleItem = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
				Element eleExtn = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
				Element eleOrder = (Element) eleShipmentLine.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
				//eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_HAZARDOUS);
				String sIsHazadous = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_HAZARDOUS);

				yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_ORDERNO, eleOrder.getAttribute(KohlsXMLLiterals.A_ORDERNO));
				
				// get the order line key and call getOrderLineDetails
				
				String strOrderLineKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				//System.out.println("OrderLineKey::::"+strOrderLineKey);
				
				sDeliveryMethod = eleOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
				
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("OrderLineKey : " + strOrderLineKey);
				}
				if(!strOrderLineKey.equals("")){
					
					YFCDocument yfcDocGetOrderLineDetails = YFCDocument.createDocument("OrderLineDetail");
					YFCElement yfcEleGetOrderLineDetails = yfcDocGetOrderLineDetails.getDocumentElement();
					yfcEleGetOrderLineDetails.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, strOrderLineKey);
					if (YFCLogUtil.isDebugEnabled()) {
						log.debug("Input XML to getOrderLineDetails : " + XMLUtil.getXMLString(yfcDocGetOrderLineDetails.getDocument()));
					}
					env.setApiTemplate("getOrderLineDetails", this.getOrderLineDetailsTemp());
					//System.out.println("getOrderLineDetailsTemp Template : " + XMLUtil.getXMLString(this.getOrderLineDetailsTemp()));
					Document docOutputGetOrderLineDetails = this.api.getOrderLineDetails(env, yfcDocGetOrderLineDetails.getDocument());
					env.clearApiTemplate("getOrderLineDetails");
					//System.out.println("getOrderLineDetails Details : " + XMLUtil.getXMLString(docOutputGetOrderLineDetails));
					if (YFCLogUtil.isDebugEnabled()) {
						log.debug("getOrderLineDetails Output XML : " + XMLUtil.getXMLString(docOutputGetOrderLineDetails));
					}						
					
					Element eleGetOrderLineDetails = docOutputGetOrderLineDetails.getDocumentElement();			
					
					Element eleGetOrderOut = (Element)eleGetOrderLineDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
					String sOrdHdrKey = eleGetOrderOut.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
					
					Element elePaymeths = (Element)eleGetOrderOut.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHODS).item(0);
					Element elePromotions = (Element)eleGetOrderOut.getElementsByTagName(KohlsXMLLiterals.E_PROMOTIONS).item(0);
					
					
					YFCElement yfcElePayMethods = yfcEleOrder.createChild(KohlsXMLLiterals.E_PAYMENT_METHODS);
					YFCElement yfcElePayMethod;
					yfcEleOrder.appendChild(yfcElePayMethods);
					
					YFCElement yfcElePromotions = yfcEleOrder.createChild(KohlsXMLLiterals.E_PROMOTIONS);
					YFCElement yfcElePromotion;
					YFCElement yfcElePromotionExtn;
					yfcEleOrder.appendChild(yfcElePromotions);											
					
					NodeList ndPayMeth = elePaymeths.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHOD);
					if(ndPayMeth.getLength()>0){
						for(int s=0;s<ndPayMeth.getLength();s++){
							Element elePayMeth = (Element) ndPayMeth.item(s);
							yfcElePayMethod = yfcEleOrder.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
							yfcElePayMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, elePayMeth.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE));
							yfcElePayMethod.setAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO, elePayMeth.getAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO));
							yfcElePayMethods.appendChild(yfcElePayMethod);
						}
					}
					/* Commented by OASIS_SUPPORT  22/02/2012
					//Release C changes for Print Collate Adding Kohl's Cash
					YFCDocument yfcDocKohlsCashTable = YFCDocument.createDocument(KohlsXMLLiterals.E_KOHLS_CASH_TABLE);
					YFCElement yfcEleKohlsCashTable = yfcDocKohlsCashTable.getDocumentElement();
					yfcEleKohlsCashTable.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY_FK, sOrdHdrKey);
					
					Document docKohlsCashTableList = api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_CASH_TABLE_LIST, yfcDocKohlsCashTable.getDocument());
					
					NodeList ndlstKohlsCashTableList = docKohlsCashTableList.getElementsByTagName(KohlsXMLLiterals.E_KOHLS_CASH_TABLE);
					int iCashTabelListLen = ndlstKohlsCashTableList.getLength();
					
					if(iCashTabelListLen>0){
						 YFCElement yfceleWMoSPaymentMethod = yfcEleOrder.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
						 yfceleWMoSPaymentMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, KohlsConstant.CREDIT_CARD_TYPE_KOHLS_CASH);
						 yfcElePayMethods.appendChild(yfceleWMoSPaymentMethod);
						 yfcEleOrder.appendChild(yfcElePayMethods);
					}
					*/
					NodeList ndPromotion = elePromotions.getElementsByTagName(KohlsXMLLiterals.E_PROMOTION);
					if(ndPromotion.getLength()>0){
						for(int s=0;s<ndPromotion.getLength();s++){
							Element elePromo = (Element) ndPromotion.item(s);
							Element elePromoExtn = (Element) elePromo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
							yfcElePromotion = yfcEleOrder.createChild(KohlsXMLLiterals.E_PROMOTION);
							yfcElePromotionExtn = yfcElePromotion.createChild(KohlsXMLLiterals.E_EXTN);
							yfcElePromotionExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_PER, elePromoExtn.getAttribute(KohlsXMLLiterals.A_EXTN_COUPON_PER));
							yfcElePromotionExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_AMNT, elePromoExtn.getAttribute(KohlsXMLLiterals.A_EXTN_COUPON_AMNT));
							yfcElePromotion.appendChild(yfcElePromotionExtn);
							yfcElePromotions.appendChild(yfcElePromotion);
						}
					}					
					
					Element eleOrderLineInsts = (Element) eleGetOrderLineDetails.getElementsByTagName(KohlsXMLLiterals.E_INSTRUCTIONS).item(0);
					strNoOfInstr = eleOrderLineInsts.getAttribute(KohlsXMLLiterals.A_NUM_OF_INSTRUCTIONS);
					if(!strNoOfInstr.equals(""))
					inNoOfInst = Integer.parseInt(strNoOfInstr);
					if(inNoOfInst>0){
						NodeList nodeOrderLineInst = eleOrderLineInsts.getElementsByTagName(KohlsXMLLiterals.E_INSTRUCTION);
						for(int m=0; m<nodeOrderLineInst.getLength(); m++){
							Element eleOrderLineInst = (Element) nodeOrderLineInst.item(m);
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
							if(chrgName.equalsIgnoreCase(KohlsConstant.GiftChargeName)){
								strGWChrg = eleLineChrg.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT);
							} else if(chrgName.equalsIgnoreCase(KohlsConstant.ChargeNameSurcharge)){
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
				
				//Order Broker start
				YFCElement elePersonInfo = yfcDocOrderRelease.createElement(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO);
				yfcEleOrderRelease.appendChild(elePersonInfo);				
				
				Element eleExtnOrderDtl = (Element) eleOrder.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
				
				// get and stamp the receipt id
				Element eleOrNotes = (Element) eleOrder.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
				String strNumOfNotes = eleOrNotes.getAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES);
				int inNumOfNotes = Integer.parseInt(strNumOfNotes);				
				if(inNumOfNotes>0){
					Map mpNote = new HashMap<String, String>();
					NodeList ndOrNote = eleOrNotes.getElementsByTagName(KohlsXMLLiterals.E_NOTE);
					for(int j=0;j<ndOrNote.getLength();j++){
						Element eleOrNote = (Element) ndOrNote.item(j);
						String strNoteReasCode = eleOrNote.getAttribute(KohlsXMLLiterals.A_REASON_CODE);
						String strNoteReasText = eleOrNote.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);						
						mpNote.put(strNoteReasCode, strNoteReasText);
					}					
					
					if (KohlsConstant.PICK.equalsIgnoreCase(sDeliveryMethod)) //check for BOPUS order line
					{
						//Prefix "BPS_" to the shipnode for BOPUS lines
						strReceiptId = (String) mpNote.get(KohlsConstant.BPS+"_"+sShipNode);
						strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+KohlsConstant.BPS+"_"+sShipNode);
					}else{
						strReceiptId = (String) mpNote.get(sShipNode);
						strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+sShipNode);
					}
				}	
				
				Element elePersonInfoBillTo = (Element) eleOrder.getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO).item(0);
				elePersonInfo.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, elePersonInfoBillTo.getAttribute(KohlsXMLLiterals.A_FIRST_NAME));
				elePersonInfo.setAttribute(KohlsXMLLiterals.A_LAST_NAME, elePersonInfoBillTo.getAttribute(KohlsXMLLiterals.A_LAST_NAME));
				elePersonInfo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, elePersonInfoBillTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_1));
				elePersonInfo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, elePersonInfoBillTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_2));
				elePersonInfo.setAttribute(KohlsXMLLiterals.A_CITY, elePersonInfoBillTo.getAttribute(KohlsXMLLiterals.A_CITY));
				elePersonInfo.setAttribute(KohlsXMLLiterals.A_STATE, elePersonInfoBillTo.getAttribute(KohlsXMLLiterals.A_STATE));
				elePersonInfo.setAttribute(KohlsXMLLiterals.A_COUNTRY, elePersonInfoBillTo.getAttribute(KohlsXMLLiterals.A_COUNTRY));
				elePersonInfo.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, elePersonInfoBillTo.getAttribute(KohlsXMLLiterals.A_ZIP_CODE));
				if(!elePersonInfoBillTo.getAttribute(KohlsXMLLiterals.A_DAY_PHONE).equals(""))
				elePersonInfo.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, elePersonInfoBillTo.getAttribute(KohlsXMLLiterals.A_DAY_PHONE));
				//Order Broker end
				
				if (KohlsConstant.YES.equalsIgnoreCase(sIsHazadous)) {

					RG = KohlsConstant.RG_HAZARDOUS;
				}

				YFCElement yfcEleOrderLine = yfcDocOrderRelease.createElement(KohlsXMLLiterals.E_ORDER_LINE);
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_STATUS_QUANTITY, eleShipmentLine.getAttribute(KohlsXMLLiterals.A_QUANTITY));
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, eleShipmentLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_GIFT_FLAG, eleOrderLine.getAttribute(KohlsXMLLiterals.A_GIFT_FLAG));
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_GIFT_WRAP, eleOrderLine.getAttribute(KohlsXMLLiterals.A_GIFT_WRAP));
				//yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_OPEN_QTY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_OPEN_QTY));
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
				
				// set the values for Rel C data
				
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_BOGO_INSTR, strInstrText);
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_UNIT_PRICE, strUnitPr);
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_RETAIL_PRICE, strRetailPr);
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_GIFT_WRAP_CHARGE, strGWChrg);		
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SHIPPING_SURF_CHARGE, strShipSurfChrg);		
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, strTaxPer);
				
				
				// Order Broker start
				String sQty = eleOrderLine.getAttribute(KohlsXMLLiterals.A_STATUS_QUANTITY);
				
				
				//String strGftWp = eleOrderLine.getAttribute(KohlsXMLLiterals.A_GIFT_WRAP);
				String strLneTp = eleOrderLine.getAttribute(KohlsXMLLiterals.A_LINE_TYPE);
				
				/*if(strGftWp.equalsIgnoreCase(KohlsConstant.YES)){
					strGiftwrap = KohlsConstant.YES;
				}*/
				if(strLneTp.equalsIgnoreCase(KohlsConstant.LINE_TYPE_PGC)){
					strLineType = KohlsConstant.LINE_TYPE_PGC;
				}
				if(!sQty.equals(KohlsConstant.BLANK))
				strAccQty += (int)Float.parseFloat(sQty);				
				
				String sItemId = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
				//String sUOM = eleItem.getAttribute(KohlsXMLLiterals.A_UOM);
				sProductLine = eleItem.getAttribute(KohlsXMLLiterals.A_PRODUCT_LINE);
					
				
				env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, this.getItemListTemplate());
				Document docGetItemListEx = this.api.getItemList(env, this.getItemListInputXML(env, sItemId));
				env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);				
				
				Element eleGetItemDetails = docGetItemListEx.getDocumentElement();
				NodeList ndItLst = eleGetItemDetails.getElementsByTagName(KohlsXMLLiterals.E_ITEM);
				if (ndItLst.getLength() == 0) {
					YFSException ex = new YFSException();
					ex.setErrorCode("Item not found");
					ex.setErrorDescription("Catalog: "+sItemId+" does not exist in the system");
					throw ex;	
				}
								
				Element eleItemDtls = (Element) eleGetItemDetails.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
				
				eleItemExtn = (Element) eleItemDtls.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
				eleItemPrimInfo = (Element) eleItemDtls.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
				
				Element eleOrderLineExtn = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);

				YFCElement yfcEleOrderLineExtn = yfcEleOrderLine.createChild(KohlsXMLLiterals.E_EXTN);
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SHIP_ALONE, eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SHIP_ALONE));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE, eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_CAGE_ITEM, eleItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_CAGE_ITEM));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_BAGGAGE, eleItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_BAGGAGE));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC, eleItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC, eleItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SERVICE_SEQ, eleItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SERVICE_SEQ));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM, eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE, eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO, eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE, eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ, eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ));
				
				//Start --- Added by OASIS_SUPPORT 17/02/2012
				//yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID,eleShipExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SA_LINE_GIFT_RCPT_ID));
				yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID,eleShipExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SA_LINE_GIFT_RCPT_ID));
				//End --- Added by OASIS_SUPPORT 17/02/2012
				
				eleItemAliasList = (Element) eleItemDtls.getElementsByTagName(KohlsXMLLiterals.A_ITEM_ALIAS_LIST).item(0);
				NodeList ndItemAlias = eleItemAliasList.getElementsByTagName(KohlsXMLLiterals.A_ITEM_ALIAS);
				if(ndItemAlias.getLength()>0){
					eleItemAlias = (Element) eleItemAliasList.getElementsByTagName(KohlsXMLLiterals.A_ITEM_ALIAS).item(0);
					yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_UPC, eleItemAlias.getAttribute("AliasValue"));
				}else{
					yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_UPC, "");
				}
				
				
				yfcEleOrderLine.appendChild(yfcEleOrderLineExtn);
								
				String sRG = this.getRoutingGuide(env, eleGetShipmentDtlOutput, sItemId);
				//eleGetOrderReleaseDetails.setAttribute("RG", sRG);	
				if(sRG.equalsIgnoreCase(KohlsConstant.RG_HAZARDOUS)){
					strIsHazardous = KohlsConstant.YES;
				}
							
				if(sProductLine.equalsIgnoreCase(KohlsConstant.PRODUCT_LINE_BK)){
					strProdLine = KohlsConstant.PRODUCT_LINE_BK;
				}				
				
				strExtnWrapTo = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE);
				if(!strExtnWrapTo.equals(KohlsConstant.BLANK)){
					strExtnWrapCode = KohlsConstant.YES;
					strExtWrapCnt++;
				}		
				// Order Broker end

				YFCElement yfcEleItem = yfcDocOrderRelease.createElement(KohlsXMLLiterals.E_ITEM);
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_ItemID, eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ItemID));
				//yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM, eleShipmentLine.getAttribute(KohlsXMLLiterals.A_UOM));
				//yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, eleShipmentLine.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS));
				//yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_LINE, eleItem.getAttribute(KohlsXMLLiterals.A_PRODUCT_LINE));
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_UPC_CODE, eleItem.getAttribute(KohlsXMLLiterals.A_UPC_CODE));
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_DESC, eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC));
				yfcEleOrderLine.appendChild(yfcEleItem);
				yfcEleOrderLines.appendChild(yfcEleOrderLine);

				

				yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDERNO, eleOrder.getAttribute(KohlsXMLLiterals.A_ORDERNO));
				yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_DATE));
				yfcEleOrder.setAttribute(KohlsXMLLiterals.A_CREATE_TS, eleOrder.getAttribute(KohlsXMLLiterals.A_CREATE_TS));
				
				yfcEleOrderRelease.setAttribute(KohlsXMLLiterals.A_RELEASE_NO, 
						eleShipmentLine.getAttribute(KohlsXMLLiterals.A_RELEASE_NO));
				//yfcEleOrderRelease.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, 
				//		eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY));
				if(!eleOrder.getAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE).equals(""))
				yfcEleOrderRelease.setAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE, 
						eleOrder.getAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE));
				if(!eleOrder.getAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE).equals(""))
				yfcEleOrderRelease.setAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE, 
						eleOrder.getAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE));


				YFCElement yfcEleLineOrderStatusToECommXML = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_LINE);
				yfcEleLineOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, eleOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
				yfcEleLineOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
				yfcEleLineOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
				yfcEleLineOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_ITEM_ID, eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ItemID));
				yfcEleLineOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_QUANTITY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_STATUS_QTY));
				yfcEleLineOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_UOM, eleShipmentLine.getAttribute(KohlsXMLLiterals.A_UOM));
				yfcEleLineOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_SCAC, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SCAC));
				yfcEleLineOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, eleOrderLine.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE));
				/** Iteration 4 changes */
				yfcEleLineOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_RECEIPT_ID, strPreEnReceiptId);
				
				yfcEleLinesOrderStatusToECommXML.appendChild(yfcEleLineOrderStatusToECommXML);

				YFCNode yfcNdOrderStatuses = YFCDocument.getNodeFor(eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUSES).item(0));

				YFCNode yfcNdOrderStatusToEcommXML = yfcDocOrderStatusToECommXML.importNode(yfcNdOrderStatuses, true);
				yfcEleLineOrderStatusToECommXML.appendChild(yfcNdOrderStatusToEcommXML);

			}			

			//Order Broker Start
			/* Commented by OASIS Support for PMR 54606,379,000 -- OASIS_SUPPORT 14/03/2012
			if(strIsHazardous.equalsIgnoreCase(KohlsConstant.YES) && strExIsMilitary.equalsIgnoreCase(KohlsConstant.YES)){
				strShipVia = KohlsConstant.SHIP_VIA_PP;
			} else if (strExIsMilitary.equalsIgnoreCase(KohlsConstant.YES)) {
				strShipVia = KohlsConstant.SHIP_VIA_PM;
			} else if (strExIsPOBox.equalsIgnoreCase(KohlsConstant.YES)) {
				strShipVia = KohlsConstant.SHIP_VIA_PMDC;
			} */
			
			//Start --- Added for PMR 54606,379,000 -- OASIS_SUPPORT 14/03/2012
			if(strIsHazardous.equalsIgnoreCase("Y") && strExIsMilitary.equalsIgnoreCase("Y")){

				try {
					
					strShipVia = KohlsUtil.getCommonCodeList(env, KohlsConstant.SHIP_VIA_VALUES, KohlsConstant.IS_HAZ_IS_MILITARY);
				
				} catch (NullPointerException npExcp) {
					strShipVia = KohlsConstant.SHIP_VIA_PP;
					if(YFCLogUtil.isDebugEnabled()) {
						log.debug("Common code value is not set for code type SHIP_VIA_VALUES. " + 
								"Using default value of PP");
					}
				}
			}		
			//End --- Added for PMR 54606,379,000 -- OASIS_SUPPORT 14/03/2012
			yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SHIP_VIA, strShipVia);
			
			yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_TOTAL_REL_UNITS, String.valueOf(strAccQty));
			
			if(strIsHazardous.equalsIgnoreCase(KohlsConstant.NO)){
				strExtnRG = getRoutingGuideExtnRG(eleGetShipmentDtlOutput);
			}else{
				strExtnRG = KohlsConstant.RG_HAZARDOUS;
			}
			yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RG, strExtnRG);
			
			if(strLineType.equalsIgnoreCase(KohlsConstant.LINE_TYPE_PGC)){
				strCartonType = KohlsConstant.CARTON_TYPE_ENV;
			}else if(strProdLine.equalsIgnoreCase(KohlsConstant.PRODUCT_LINE_BK)
					&& strExtnWrapCode.equalsIgnoreCase(KohlsConstant.YES)){
				strCartonType = KohlsConstant.CARTON_TYPE_BRK;
			}else if(strExtnWrapCode.equalsIgnoreCase(KohlsConstant.YES)){
				strCartonType = KohlsConstant.CARTON_TYPE_WRP;
			}else if(strProdLine.equalsIgnoreCase(KohlsConstant.PRODUCT_LINE_BK)){
				strCartonType = KohlsConstant.CARTON_TYPE_BRK;
			}				
			yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_CARTON_TYPE, strCartonType);
			
			// set the Receipt Id to Extn fields
			yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_HDR_GFT_RECP_ID, strReceiptId);
			yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_PREENC_HDR_RECP_ID, strPreEnReceiptId);
							
/*			if(isSingleLine && strExtWrapCnt==1){
				strExtnWrapToVal = KohlsConstant.WRAP_SINGLE_TO_T;
			}else if(strExtWrapCnt>1){
				strExtnWrapToVal = KohlsConstant.WRAP_SINGLE_TO_S;
			}	*/			
			yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_SINGLE_TOG, "");
			
			
			//Order Broker End
			
			
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Release Message to WMoS : " + XMLUtil.getXMLString(yfcDocOrderRelease.getDocument()));
			}
			this.api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_SEND_SHIP_ALONE_RELEASE_TO_WMOS, yfcDocOrderRelease.getDocument());

		}
			}
		//KohlsUtil.registerProcessCompletion(env, sDataKey, sDataType, sTaskQKey);
		}catch(YFSException ex){			
			ex.printStackTrace();
			throw ex;
		}
		return null;
	}


	private Document getShipmentDetailsTemp() {

		YFCDocument yfcDocGetShipmentListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		YFCElement yfcEleGetShipmentTemp = yfcDocGetShipmentListTemp.getDocumentElement();
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, "");
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, "");
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "");
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_RELEASE_NO, "");
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_PICKTICKET_NO, "");
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE, "");
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_SCAC, "");
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, "");
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, "");
		yfcEleGetShipmentTemp.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, "");
		
		YFCElement yfcEleShipmentExtn = yfcEleGetShipmentTemp.createChild(KohlsXMLLiterals.E_EXTN);
		/** Iteration 4 changes */
		//yfcEleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SA_RCPT_ID, "");
		yfcEleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SA_LINE_GIFT_RCPT_ID, "");
		yfcEleGetShipmentTemp.appendChild(yfcEleShipmentExtn);	
		

		YFCElement yfcElePersonInfoShipTo = yfcEleGetShipmentTemp.createChild(KohlsXMLLiterals.E_TO_ADDRESS);
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "");
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "");
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "");
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_ADD_LINE_3, "");
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_CITY, "");
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_STATE, "");
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_COUNTRY, "");
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, "");
		yfcElePersonInfoShipTo.setAttribute(KohlsXMLLiterals.A_EMAIL_ID, "");
		yfcEleGetShipmentTemp.appendChild(yfcElePersonInfoShipTo);		
		
		YFCElement yfcEleExtnInfoShipToTemp = yfcElePersonInfoShipTo.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX, "");
		yfcEleExtnInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY, "");
		yfcElePersonInfoShipTo.appendChild(yfcEleExtnInfoShipToTemp);

		YFCElement yfcEleGetShipmentListLinesTemp = yfcDocGetShipmentListTemp.createElement(KohlsXMLLiterals.E_SHIPMENT_LINES);
		YFCElement yfcEleGetShipmentListLineTemp = yfcEleGetShipmentListLinesTemp.createChild(KohlsXMLLiterals.E_SHIPMENT_LINE);
		yfcEleGetShipmentListLineTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleGetShipmentListLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleGetShipmentListLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcEleGetShipmentListLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, "");
		yfcEleGetShipmentListLineTemp.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcEleGetShipmentListLineTemp.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcEleGetShipmentListLineTemp.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");
		yfcEleGetShipmentListLineTemp.setAttribute(KohlsXMLLiterals.A_RELEASE_NO, "");
		yfcEleGetShipmentListLineTemp.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, "");
		yfcEleGetShipmentListLineTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcEleGetShipmentListLinesTemp.appendChild(yfcEleGetShipmentListLineTemp);
		yfcEleGetShipmentTemp.appendChild(yfcEleGetShipmentListLinesTemp);


		YFCElement yfcEleGetShipmentListOrderTemp = yfcEleGetShipmentListLineTemp.createChild(KohlsXMLLiterals.E_ORDER);
		yfcEleGetShipmentListOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcEleGetShipmentListOrderTemp.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "");
		yfcEleGetShipmentListOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, "");
		yfcEleGetShipmentListOrderTemp.setAttribute(KohlsXMLLiterals.A_CREATE_TS, "");
		yfcEleGetShipmentListOrderTemp.setAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE, "");
		yfcEleGetShipmentListOrderTemp.setAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE, "");
		yfcEleGetShipmentListLineTemp.appendChild(yfcEleGetShipmentListOrderTemp);
		
		YFCElement yfcEleOrNotesTemp = yfcEleGetShipmentListOrderTemp.createChild(KohlsXMLLiterals.E_NOTES);
		yfcEleOrNotesTemp.setAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES, "");
		YFCElement yfcEleOrNoteTemp = yfcEleOrNotesTemp.createChild(KohlsXMLLiterals.E_NOTE);
		yfcEleOrNoteTemp.setAttribute(KohlsXMLLiterals.A_REASON_CODE, "");
		yfcEleOrNoteTemp.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");
		yfcEleOrNotesTemp.appendChild(yfcEleOrNoteTemp);
		yfcEleGetShipmentListOrderTemp.appendChild(yfcEleOrNotesTemp);	
		
		YFCElement yfcElePersonInfoBillToTemp = yfcEleGetShipmentListOrderTemp.createChild(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO);
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "");		
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_CITY, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_STATE, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_COUNTRY, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");		
		yfcEleGetShipmentListOrderTemp.appendChild(yfcElePersonInfoBillToTemp);
		
		YFCElement yfcEleOrderExtnTemp = yfcEleGetShipmentListOrderTemp.createChild(KohlsXMLLiterals.E_EXTN);
		/** Iteration 4 changes */
		//yfcEleOrderExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_HDR_GFT_RECP_ID, "");	
		yfcEleGetShipmentListOrderTemp.appendChild(yfcEleOrderExtnTemp);

		YFCElement yfcEleGetShipmentListOrderLineTemp = yfcEleGetShipmentListLineTemp.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, "");
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_GIFT_WRAP, "");
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_GIFT_FLAG, "");
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_LINE_TYPE, "");
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_STATUS_QUANTITY, "");
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_OPEN_QTY, "");
		yfcEleGetShipmentListOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, "");

		YFCElement yfcEleNotesTemp = yfcEleGetShipmentListOrderLineTemp.createChild(KohlsXMLLiterals.E_NOTES);
		YFCElement yfcEleNoteTemp = yfcEleNotesTemp.createChild(KohlsXMLLiterals.E_NOTE);
		yfcEleNoteTemp.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");
		yfcEleNotesTemp.appendChild(yfcEleNoteTemp);
		yfcEleGetShipmentListOrderLineTemp.appendChild(yfcEleNotesTemp);

		YFCElement yfcEleOrderStatuses = yfcEleGetShipmentListOrderLineTemp.createChild(KohlsXMLLiterals.E_ORDER_STATUSES);
		YFCElement yfcEleOrderStatus = yfcEleOrderStatuses.createChild(KohlsXMLLiterals.E_ORDER_STATUS);
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_DATE, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_DESCRIPTION, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_QTY, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_REASON, "");
		yfcEleOrderStatuses.appendChild(yfcEleOrderStatus);
		yfcEleGetShipmentListOrderLineTemp.appendChild(yfcEleOrderStatuses);

		yfcEleGetShipmentListLineTemp.appendChild(yfcEleGetShipmentListOrderLineTemp);

		YFCElement yfcEleOrderLineExtn = yfcEleGetShipmentListOrderLineTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SHIP_ALONE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE, "");
		/** Iteration 4 changes */
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_IS_HAZARDOUS, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ, "");
		yfcEleGetShipmentListOrderLineTemp.appendChild(yfcEleOrderLineExtn);

		YFCElement yfcEleGetShipmentListOrderLineItemTemp = yfcEleGetShipmentListOrderLineTemp.createChild(KohlsXMLLiterals.E_ITEM);
		yfcEleGetShipmentListOrderLineItemTemp.setAttribute(KohlsXMLLiterals.A_ItemID, "");
		yfcEleGetShipmentListOrderLineItemTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcEleGetShipmentListOrderLineItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleGetShipmentListOrderLineItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_LINE, "");
		yfcEleGetShipmentListOrderLineTemp.appendChild(yfcEleGetShipmentListOrderLineItemTemp);
	

		//System.out.println("getShipmentList Tempalte : " + XMLUtil.getXMLString(yfcDocGetShipmentListTemp.getDocument()));
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getShipmentList Tempalte : " + XMLUtil.getXMLString(yfcDocGetShipmentListTemp.getDocument()));
		}

		return yfcDocGetShipmentListTemp.getDocument();
	}
	
	private String getRoutingGuide(YFSEnvironment env, Element eleGetOrderReleaseDetails, String sItemId) 
	throws YFSException, RemoteException, TransformerException{

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<!--------------- Inside Routing Guide ----------->");
		}

		env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, this.getItemListTemplate());
		Document docGetItemList = this.api.getItemList(env, this.getItemListInputXML(env, sItemId));
		env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);
		
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemList Output XML  : " + XMLUtil.getXMLString(docGetItemList));
		}
		
		//System.out.println("getItemListTemplate Output XML: "+ XMLUtil.getXMLString(docGetItemList));
		Element elePrimaryInfo = (Element) docGetItemList.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
		Element elePersonInfoShipTo = (Element) XPathUtil.getNode((Node) eleGetOrderReleaseDetails, KohlsXMLLiterals.XP_ORDERRELEASE_TO_ADDRESS);
		String sIsHazMat = elePrimaryInfo.getAttribute(KohlsXMLLiterals.A_IS_HAZMAT);

		String sCarrierServiceCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
		Element elePersonInfoShipToExtn = (Element)elePersonInfoShipTo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		String strIsMilitary = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY);
		String strIsPOBox = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX);
		//String sAddressLine1 = elePersonInfoShipTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_1);
		//String sAddressLine2 = elePersonInfoShipTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_2);
		//String sAddressLine3 = elePersonInfoShipTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_3);
		String sState = elePersonInfoShipTo.getAttribute(KohlsXMLLiterals.A_STATE);

		if (KohlsConstant.YES.equalsIgnoreCase(sIsHazMat)) {
			return KohlsConstant.RG_HAZARDOUS;
		} else if (strIsMilitary.equalsIgnoreCase(KohlsConstant.YES) 
				|| strIsPOBox.equalsIgnoreCase(KohlsConstant.YES)) {			
			return KohlsConstant.RG_POAPO;
		} else if (sCarrierServiceCode.toLowerCase().contains(KohlsConstant.CARRIER_SERVICE_CODE_PRIORITY.toLowerCase())) {
			return KohlsConstant.RG_PRIORITY;
		} else if (KohlsConstant.STATE_AK.equalsIgnoreCase(sState) || KohlsConstant.STATE_HI.equalsIgnoreCase(sState)) {
			return KohlsConstant.RG_AK_HI;
		} else {
			return KohlsConstant.RG_STANDARD;					
		}

	}
	
	private String getRoutingGuideExtnRG(Element eleGetOrderReleaseDetails) 
	throws YFSException, RemoteException, TransformerException{

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<!--------------- Inside getRoutingGuideExtnRG ----------->");
		}
		
		Element elePersonInfoShipTo = (Element) XPathUtil.getNode((Node) eleGetOrderReleaseDetails, KohlsXMLLiterals.XP_ORDERRELEASE_TO_ADDRESS);
		
		String sCarrierServiceCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
		Element elePersonInfoShipToExtn = (Element)elePersonInfoShipTo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		String strIsMilitary = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY);
		String strIsPOBox = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX);
		//String sAddressLine1 = elePersonInfoShipTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_1);
		//String sAddressLine2 = elePersonInfoShipTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_2);
		//String sAddressLine3 = elePersonInfoShipTo.getAttribute(KohlsXMLLiterals.A_ADD_LINE_3);
		String sState = elePersonInfoShipTo.getAttribute(KohlsXMLLiterals.A_STATE);

		if (strIsMilitary.equalsIgnoreCase(KohlsConstant.YES) 
				|| strIsPOBox.equalsIgnoreCase(KohlsConstant.YES)) {			
			return KohlsConstant.RG_POAPO;
		} else if (sCarrierServiceCode.toLowerCase().contains(KohlsConstant.CARRIER_SERVICE_CODE_PRIORITY.toLowerCase())) {
			return KohlsConstant.RG_PRIORITY;
		} else if (KohlsConstant.STATE_AK.equalsIgnoreCase(sState) || KohlsConstant.STATE_HI.equalsIgnoreCase(sState)) {
			return KohlsConstant.RG_AK_HI;
		} else {
			return KohlsConstant.RG_STANDARD;					
		}
	}
	
	private Document getItemListTemplate() {

		YFCDocument yfcDocGetItemListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM_LIST);
		YFCElement yfcEleGetItemListTemp = yfcDocGetItemListTemp.getDocumentElement();

		YFCElement yfcEleItemTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_ITEM);
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");
		
		YFCElement yfcEleItemAliasListTemp = yfcEleItemTemp.createChild(KohlsXMLLiterals.A_ITEM_ALIAS_LIST);
		YFCElement yfcEleItemAliasTemp = yfcEleItemAliasListTemp.createChild(KohlsXMLLiterals.A_ITEM_ALIAS);
		yfcEleItemAliasTemp.setAttribute(KohlsXMLLiterals.A_ALIAS_NAME, "");
		yfcEleItemAliasTemp.setAttribute("AliasValue", "");
		yfcEleItemAliasListTemp.appendChild(yfcEleItemAliasTemp);
		yfcEleItemTemp.appendChild(yfcEleItemAliasListTemp);
		
		YFCElement yfcEleItemPrimaryInformationTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_PRIMARY_INFORMATION);
		yfcEleItemPrimaryInformationTemp.setAttribute(KohlsXMLLiterals.A_IS_HAZMAT, "");
		yfcEleItemPrimaryInformationTemp.setAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC, "");
		yfcEleItemTemp.appendChild(yfcEleItemPrimaryInformationTemp);
		
		YFCElement yfcEleItemExtnTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_CAGE_ITEM, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_SERVICE_SEQ, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_BAGGAGE, "");
		yfcEleItemTemp.appendChild(yfcEleItemExtnTemp);
		
		yfcEleGetItemListTemp.appendChild(yfcEleItemTemp);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemList Template : " + XMLUtil.getXMLString(yfcDocGetItemListTemp.getDocument()));
		}

		return yfcDocGetItemListTemp.getDocument();
	}
	
	private Document getItemListInputXML(YFSEnvironment env, String sItemID) {

		YFCDocument yfcDocGetItemList = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcEleGetItemList = yfcDocGetItemList.getDocumentElement();
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_ITEM_ID, sItemID);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, KohlsConstant.PRODUCT_CLASS_GOOD);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_UOM, KohlsConstant.UNIT_OF_MEASURE);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemList Input XML : " + XMLUtil.getXMLString(yfcDocGetItemList.getDocument()));
		}
		return yfcDocGetItemList.getDocument();
	}
	
	private Document getOrderLineDetailsTemp() {

		YFCDocument yfcDocGetOrderLineTemp = YFCDocument.createDocument("OrderLine");
		YFCElement yfcEleGetOrdLnTemp = yfcDocGetOrderLineTemp.getDocumentElement();
		yfcEleGetOrdLnTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");		
		
		YFCElement yfcEleOrder = yfcEleGetOrdLnTemp.createChild(KohlsXMLLiterals.E_ORDER);	
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
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
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getOrderLineDetails Template : " + XMLUtil.getXMLString(yfcDocGetOrderLineTemp.getDocument()));
		}

		return yfcDocGetOrderLineTemp.getDocument();
	}
	
}
