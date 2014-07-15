package com.kohls.oms.agent;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

/** This Task Q based agent in called in the Order Pipeline to split the release for Ship Alone items to every quantity before creating the shipment.
 * The agent also send the release update to WMoS for Non-Ship Alone item and order status update to E-Commerce system.
 * 
 *  @author Prashanth T G
 *  
 *  Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 *  
 *  INPUT XML <Releases>
<Release OrderNo="Y100001425" ReleaseNo="1" DocumentType="0001/0006"/>
</Releases> 
 **/

public class KohlsWMoSMsgGeneratorAPI extends YCPBaseTaskAgent {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsWMoSMsgGeneratorAPI.class.getName());
	private YIFApi api;
	private Connection m_conn;


	public KohlsWMoSMsgGeneratorAPI() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}



	public void setProperties(Properties arg0) throws Exception { 

	}

	@Override
	public Document executeTask(YFSEnvironment env, Document inXML)
	throws Exception {
		// TODO Auto-generated method stub

		//System.out.println("Input XML executeTask start: " + XMLUtil.getXMLString(inXML));
		this.m_conn = KohlsUtil.getDBConnection(env);
		Element eleRoot = inXML.getDocumentElement();
		//Order Broker start
		String strShipVia="";
		String strExIsPOBox="";
		String strExIsMilitary="";
		String strIsHazardous = "N";
		String strCartonType = KohlsConstant.CARTON_TYPE_BOX;// changed to BOX for PMR :05616,379,000 
		String strLineType = "";
		//String strGiftwrap = "";
		String strExtnRG = "";
		String strProdLine = "";
		int strExtWrapCnt = 0;
		String strExtnWrapTo = "";
		String strExtnWrapToVal = "";
		String strExtnWrapCode = "";
		boolean isSingleLine = false;
		String sPickTicketNo = "";
		String strRelNo = "";
		String strOrderNo = "";
		String strReceiptId = "";
		String strPreEnReceiptId = "";
		//Start --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
		boolean wrapMultTogether = false;
		Map <String , Integer> mapGiftWrapTogetherCode = new HashMap <String , Integer>();
		//End --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
		//Start --- Added for Warehouse Transfer Impl -- OASIS_SUPPORT 13/05/2013
		String strDocumentType = "";
		//End --- Added for Warehouse Transfer Impl -- OASIS_SUPPORT 13/05/2013
		try{
			//Order Broker end
			//String sDataType = eleRoot.getAttribute(KohlsXMLLiterals.A_DATA_TYPE);
			//String sDataKey = eleRoot.getAttribute(KohlsXMLLiterals.A_DATA_KEY);
			//String sTaskQKey = eleRoot.getAttribute(KohlsXMLLiterals.A_TASK_Q_KEY);
			
			NodeList ndRel = eleRoot.getElementsByTagName(KohlsXMLLiterals.A_RELEASE);
			if(ndRel.getLength()>0){
				for(int p=0; p<ndRel.getLength(); p++){
					Element eleRelease = (Element) ndRel.item(p);
					strRelNo = eleRelease.getAttribute(KohlsXMLLiterals.A_RELEASE_NO);
					strOrderNo = eleRelease.getAttribute(KohlsXMLLiterals.A_ORDERNO);
					//Start --- Added for Warehouse Transfer Impl -- OASIS_SUPPORT 13/05/2013
					strDocumentType=eleRelease.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE);
					//End --- Added for Warehouse Transfer Impl -- OASIS_SUPPORT 13/05/2013
					
					YFCDocument yfcDocGetOrderReleaseDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE_DETAIL);
					YFCElement yfcEleGetOrderDetails = yfcDocGetOrderReleaseDetails.getDocumentElement();
					yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_RELEASE_NO, strRelNo);
					yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderNo);
					//Start --- Added for Warehouse Transfer Impl -- OASIS_SUPPORT 13/05/2013
					yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);
					//End --- Added for Warehouse Transfer Impl -- OASIS_SUPPORT 13/05/2013
					yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.SO_ENTERPRISE_CODE);
					
					int strAccQty = 0;			
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Input XML to getOrderReleaseDetails : " + XMLUtil.getXMLString(yfcDocGetOrderReleaseDetails.getDocument()));
			}
			env.setApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_DETAILS, this.getOrderReleaseDetailsTemplate());
			//System.out.println("getOrderReleaseDetails Template : " + XMLUtil.getXMLString(this.getOrderReleaseDetailsTemplate()));
			Document docOutputGetOrderReleaseDetails = this.api.getOrderReleaseDetails(env, yfcDocGetOrderReleaseDetails.getDocument());
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_DETAILS);
			//System.out.println("getOrderRelease Details : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("getOrderDetails Output XML : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));
			}

			//System.out.println("getOrderReleaseDetailsTemplate Output XML : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));
			
			Element eleGetOrderReleaseDetails = docOutputGetOrderReleaseDetails.getDocumentElement();
			Element eleGetOrderDetail = (Element) eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);

			//String sOrderReleaseKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY);
			String sOrdHdrKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
			String sOrderNo = eleGetOrderDetail.getAttribute(KohlsXMLLiterals.A_ORDERNO);
			//String sShipToKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SHIP_TO_KEY);
			String sEnterpriseCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);
			String sShipNode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
			//String sSellerOrgCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE);
			String sReleaseNo = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_RELEASE_NO);
			String sCarrierServiceCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
			String sSCAC = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SCAC);
			String sReqShipDate = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE);
			String sReqDeliveryDate = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE);
			
			Element eleGetExtn = (Element) eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			sPickTicketNo = eleGetExtn.getAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO);
			
			int numOfNote=0;
			Element eleOrlNotes = (Element) eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
			String strNumNotes = eleOrlNotes.getAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES);
			if(strNumNotes!= null && !strNumNotes.equals("")){
				numOfNote = Integer.parseInt(strNumNotes);
			}
			
			//String sOrderDate = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_DATE);		
			//String sFinalOrdDate = sOrderDate.substring(5,7) + sOrderDate.substring(8,10) + sOrderDate.substring(2,4) + sOrderDate.substring(11,19).replace(":","");
			
			String sDeliveryMethod = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
						
			// get and stamp the receipt id
			Element eleOrNotes = (Element) eleGetOrderDetail.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
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
				
				//Checking for BOPUS line
				if(KohlsConstant.PICK.equals(sDeliveryMethod))
				{
					strReceiptId = (String) mpNote.get(KohlsConstant.BPS+"_"+sShipNode);
					strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+KohlsConstant.BPS+"_"+sShipNode);
				}else{  //Non BOPUS line
					strReceiptId = (String) mpNote.get(sShipNode);
					strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+sShipNode);
				}
			}

			// Release Message to WMoS
			Document docWMoSOrderReleaseXML = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE).getDocument();
			Element eleWMoSOrderReleaseXML = docWMoSOrderReleaseXML.getDocumentElement();
			eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_RELEASE_NO, sReleaseNo);
			//eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
			eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_SHIPNODE, sShipNode);
			eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, sCarrierServiceCode);
			eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_SCAC, sSCAC);
			if(!sReqShipDate.equals(""))
			eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE, sReqShipDate);
			if(!sReqDeliveryDate.equals(""))
			eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE, sReqDeliveryDate);

			// Order Broker start
			Element elePersonInfoShipTo = (Element)docWMoSOrderReleaseXML.importNode(XPathUtil.getNode((Node) eleGetOrderReleaseDetails, 
					KohlsXMLLiterals.XP_ORDERRELEASE_PERSON_INFO), true);
			Element eleInfoShExtn = (Element)elePersonInfoShipTo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			elePersonInfoShipTo.removeChild(eleInfoShExtn);
			eleWMoSOrderReleaseXML.appendChild(elePersonInfoShipTo);

			Element eleOrderDs = (Element)docWMoSOrderReleaseXML.importNode(XPathUtil.getNode((Node) eleGetOrderReleaseDetails, 
					KohlsXMLLiterals.XP_ORDERRELEASE_ORDER), true);
			Element eleInfoBill = (Element)eleOrderDs.getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO).item(0);
			Element eleOrExtn = (Element)eleOrderDs.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			Element eleOrdNotes = (Element)eleOrderDs.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
			eleOrderDs.removeChild(eleInfoBill);
			eleOrderDs.removeChild(eleOrExtn);
			eleOrderDs.removeChild(eleOrdNotes);
			eleWMoSOrderReleaseXML.appendChild(eleOrderDs);
			
						
			Element eleProms = (Element)eleOrderDs.getElementsByTagName(KohlsXMLLiterals.E_PROMOTIONS).item(0);
			NodeList ndProm = eleProms.getElementsByTagName(KohlsXMLLiterals.E_PROMOTION);
			if(ndProm.getLength()>0){
				for(int g=0;g<ndProm.getLength();g++){
					Element eleProm = (Element)ndProm.item(g);
					eleProm.removeAttribute(KohlsXMLLiterals.A_PROMOTION_ID);
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
				 Element eleWMoSPaymentMethods = (Element)eleOrderDs.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHODS).item(0);
				 Element eleWMoSPaymentMethod = docWMoSOrderReleaseXML.createElement(KohlsXMLLiterals.E_PAYMENT_METHOD);
				 eleWMoSPaymentMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, KohlsConstant.CREDIT_CARD_TYPE_KOHLS_CASH);
				 eleWMoSPaymentMethods.appendChild(eleWMoSPaymentMethod);
				 eleOrderDs.appendChild(eleWMoSPaymentMethods);
			}
			*/
			Node ndOrderLines = docWMoSOrderReleaseXML.importNode(XPathUtil.getNode((Node) eleGetOrderReleaseDetails, 
					KohlsXMLLiterals.XP_ORDERRELEASE_ORDERLINES), false);
			eleWMoSOrderReleaseXML.appendChild(ndOrderLines);
		
			Element eleInfoShipTo = (Element) eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO).item(0);
			
			Element eleExtnInfoShipTo = (Element) eleInfoShipTo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			strExIsPOBox = eleExtnInfoShipTo.getAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX);
			strExIsMilitary = eleExtnInfoShipTo.getAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY);
			
			Element eleExtn = docWMoSOrderReleaseXML.createElement(KohlsXMLLiterals.E_EXTN);
			eleWMoSOrderReleaseXML.appendChild(eleExtn);	
			Element elePersonInfo = docWMoSOrderReleaseXML.createElement(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO);
			eleWMoSOrderReleaseXML.appendChild(elePersonInfo);
			
			Element eleOrderDtl = (Element) eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
			
			Element eleExtnOrderDtl = (Element) eleOrderDtl.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);

			
			Element elePersonInfoBillTo = (Element) eleOrderDtl.getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO).item(0);
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
			
			// Order Broker end

			// Document Create for StatusUpdate Message to Ecommerce
			YFCDocument yfcDocOrderStatusToECommXML = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
			YFCElement yfcEleOrderStatusToECommXML = yfcDocOrderStatusToECommXML.getDocumentElement();

						
			yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_ORDERNO, sOrderNo);
			yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
			yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, sEnterpriseCode);

			YFCElement yfcEleOrderLines = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_LINES);
			yfcEleOrderStatusToECommXML.appendChild(yfcEleOrderLines);

			NodeList ndlstOrderLines = docOutputGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
			Element eleOrderLine = null;
			Element eleItem = null;
			Element eleItemExtn = null;
			Element eleItemPrimInfo = null;
			Element eleItemAliasList = null;
			Element eleItemAlias = null;
			String sProductLine = "";
			/** Iteration 4 changes */
			//String sShipNodeForRcptId="";
			//String sLineNo="";
			//String strExtnSAGiftReceiptID="";
			//String strSALnGiftRcptID="";
			//String sReceiptIDValues="";
			//String sExtnPreEncodedHdrReceiptID="";
			//String strPreEncodedLnGiftRcptID="";
			//String sExtnPreEncodedSAGiftReceiptID="";
			///String sExtnPreEncodedSALnReceiptID ="";
			boolean isLineRcptGen = false;
			int lnRctIdCnt=0;
			Set sExtnGiftWrapTogetherCode = new HashSet();
			double dOrderedQty=0.00;
			
			int iCount = 0;
			//Order Broker start
			if(ndlstOrderLines.getLength()==1){
				isSingleLine = true;
			}
			//Order Broker end
			for (int i = 0; i < ndlstOrderLines.getLength(); i++) {
				
				//isLineRcptLengthZero = false;
				eleOrderLine = (Element) ndlstOrderLines.item(i);
				//String sOrderLineKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				//String sOrderHeaderKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
				String sQty = eleOrderLine.getAttribute(KohlsXMLLiterals.A_STATUS_QUANTITY);
				dOrderedQty = Double.parseDouble(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QUANTITY));
				// Order Broker start
				//String strGftWp = eleOrderLine.getAttribute(KohlsXMLLiterals.A_GIFT_WRAP);
				String strLneTp = eleOrderLine.getAttribute(KohlsXMLLiterals.A_LINE_TYPE);
				String strGftFg = eleOrderLine.getAttribute(KohlsXMLLiterals.A_GIFT_FLAG);
				
				//Obtain the Line Gift Receipt ID and check if the value is null.
                Element eleOrderLineExtn = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
                
                /** 
    			 * Get the Line Receipt Id
    			 */
				String strLnGiftRcptID = eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID);
				if(strLnGiftRcptID.length() == 1 && strGftFg.equalsIgnoreCase("Y")){
					lnRctIdCnt++;
					if(numOfNote>0){
						Element eleOrlNote;
						int orlNote;
						NodeList ndEleOrlNote = eleOrlNotes.getElementsByTagName(KohlsXMLLiterals.E_NOTE);
						for(int k=0;k<ndEleOrlNote.getLength();k++){
							eleOrlNote = (Element) ndEleOrlNote.item(k);
							orlNote = Integer.parseInt(eleOrlNote.getAttribute(KohlsXMLLiterals.A_SEQUENCE_NO));
							if(orlNote==lnRctIdCnt){
								strLnGiftRcptID = eleOrlNote.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);
							}
						}
					}
				} else if(strLnGiftRcptID.length() == 1){
					strLnGiftRcptID = "";
				}
							
				/*if(strGftWp.equalsIgnoreCase(KohlsConstant.YES)){
					strGiftwrap = KohlsConstant.YES;
				}*/
				if(strLneTp.equalsIgnoreCase(KohlsConstant.LINE_TYPE_PGC)){
					strLineType = KohlsConstant.LINE_TYPE_PGC;
				}
				if(!sQty.equals(KohlsConstant.BLANK))
				strAccQty += (int)Float.parseFloat(sQty);
				
				eleItem = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
				String sItemId = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
				String sUOM = eleItem.getAttribute(KohlsXMLLiterals.A_UOM);
				sProductLine = eleItem.getAttribute(KohlsXMLLiterals.A_PRODUCT_LINE);
								
				if(sProductLine.equalsIgnoreCase(KohlsConstant.PRODUCT_LINE_BK)){
					strProdLine = KohlsConstant.PRODUCT_LINE_BK;
				}				
				
				strExtnWrapTo = eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE);
				if(!strExtnWrapTo.equals("")){
					strExtnWrapCode = KohlsConstant.YES;
					sExtnGiftWrapTogetherCode.add(strExtnWrapTo);
					strExtWrapCnt++;
				}		
				// Order Broker end
				
				String strNoOfInstr = "";
				int inNoOfInst = 0;
				String strInstrType = "";
				String strInstrText = "";
				Element eleOrderLineInsts = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_INSTRUCTIONS).item(0);
				strNoOfInstr = eleOrderLineInsts.getAttribute(KohlsXMLLiterals.A_NUM_OF_INSTRUCTIONS);
				if(!strNoOfInstr.equals(""))
				inNoOfInst = Integer.parseInt(strNoOfInstr);
				if(inNoOfInst>0){
					NodeList nodeOrderLineInst = eleOrderLineInsts.getElementsByTagName(KohlsXMLLiterals.E_INSTRUCTION);
					for(int l=0; l<nodeOrderLineInst.getLength(); l++){
						Element eleOrderLineInst = (Element) nodeOrderLineInst.item(l);
						strInstrType = eleOrderLineInst.getAttribute(KohlsXMLLiterals.A_INSTRUCTION_TYPE);
						if(strInstrType.equalsIgnoreCase(KohlsConstant.INS_TYPE_BOGO)){
							strInstrText = eleOrderLineInst.getAttribute(KohlsXMLLiterals.A_INSTRUCTION_TEXT);
						}
					}
				}
				
				Element eleOrderLinePrInfo = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_PRICE_INFO).item(0);
				String strUnitPr = "";
				String strRetailPr = "";
				
				strUnitPr = eleOrderLinePrInfo.getAttribute(KohlsXMLLiterals.A_UNIT_PRICE);
				strRetailPr = eleOrderLinePrInfo.getAttribute(KohlsXMLLiterals.A_RETAIL_PRICE);
				
				String strGWChrg = "";
				String strShipSurfChrg = "";
				String chrgName = "";
				Element eleOrderLineChrgs = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGES).item(0);
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
				
				String strTaxPer = "";
				String strTaxName = "";
				Element eleOrderLineTaxes = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAXES).item(0);
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
				
				//System.out.println("Product line1 : " + sProductLine);
				//System.out.println("Status Qty : " + sQty);
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Product Line : " + sProductLine);
				}
					
					// Order Broker start
					env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, this.getItemListTemplate());
					Document docGetItemListEx = this.api.getItemList(env, this.getItemListInputXML(env, sItemId));
					env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);	
					
					//System.out.println("GetItemList OUTPUT::::"+XMLUtil.getXMLString(docGetItemListEx));
																				
					Element eleGetItemDetails = docGetItemListEx.getDocumentElement();
					
					NodeList ndItLst = eleGetItemDetails.getElementsByTagName(KohlsXMLLiterals.E_ITEM);
	
						if(ndItLst.getLength()==0){
							YFSException ex = new YFSException();
							ex.setErrorCode("Item not found");
							ex.setErrorDescription("Catalog: "+sItemId+" does not exist in the system");
							throw ex;	
						}
						
					Element eleItemDtls = (Element) eleGetItemDetails.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
					
					eleItemExtn = (Element) eleItemDtls.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
					eleItemPrimInfo = (Element) eleItemDtls.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
					
					String sRG = this.getRoutingGuide(env, eleGetOrderReleaseDetails, sItemId);
					//eleGetOrderReleaseDetails.setAttribute("RG", sRG);	
					if(sRG.equalsIgnoreCase(KohlsConstant.RG_HAZARDOUS)){
						strIsHazardous = KohlsConstant.YES;
					}
					

					Element eleOrderLineDs = (Element)docWMoSOrderReleaseXML.importNode(ndlstOrderLines.item(i), true);
					
					Element eleInstr = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_INSTRUCTIONS).item(0);
					Element eleLnPriceInfo = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_LINE_PRICE_INFO).item(0);
					Element eleLineChrgs = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGES).item(0);
					Element eleLineTaxes = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAXES).item(0);
					eleOrderLineDs.removeChild(eleInstr);
					eleOrderLineDs.removeChild(eleLnPriceInfo);
					eleOrderLineDs.removeChild(eleLineChrgs);
					eleOrderLineDs.removeChild(eleLineTaxes);
					eleOrderLineDs.setAttribute(KohlsXMLLiterals.A_BOGO_INSTR, strInstrText);
					eleOrderLineDs.setAttribute(KohlsXMLLiterals.A_UNIT_PRICE, strUnitPr);
					eleOrderLineDs.setAttribute(KohlsXMLLiterals.A_RETAIL_PRICE, strRetailPr);
					eleOrderLineDs.setAttribute(KohlsXMLLiterals.A_GIFT_WRAP_CHARGE, strGWChrg);		
					eleOrderLineDs.setAttribute(KohlsXMLLiterals.A_SHIPPING_SURF_CHARGE, strShipSurfChrg);		
					eleOrderLineDs.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, strTaxPer);
					
					
					ndOrderLines.appendChild(eleOrderLineDs);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_OPEN_QTY);
					
					Element eleOrderLineDsExtn = (Element) eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
					
					eleOrderLineDsExtn.setAttribute(KohlsXMLLiterals.A_EXTN_CAGE_ITEM, eleItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_CAGE_ITEM));
					eleOrderLineDsExtn.setAttribute(KohlsXMLLiterals.A_EXTN_BAGGAGE, eleItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_BAGGAGE));
					eleOrderLineDsExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC, eleItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC));
					eleOrderLineDsExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC, eleItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC));
					eleOrderLineDsExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SERVICE_SEQ, eleItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SERVICE_SEQ));
					/** 
	    			 * Commented for Iteration 4 changes
	    			 */
					eleOrderLineDsExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, strLnGiftRcptID);
					
					eleItemAliasList = (Element) eleItemDtls.getElementsByTagName(KohlsXMLLiterals.A_ITEM_ALIAS_LIST).item(0);
					NodeList ndItemAlias = eleItemAliasList.getElementsByTagName(KohlsXMLLiterals.A_ITEM_ALIAS);
					if(ndItemAlias.getLength()>0){
						eleItemAlias = (Element) eleItemAliasList.getElementsByTagName(KohlsXMLLiterals.A_ITEM_ALIAS).item(0);
						eleOrderLineDsExtn.setAttribute(KohlsXMLLiterals.A_EXTN_UPC, eleItemAlias.getAttribute("AliasValue"));
					}else{
						eleOrderLineDsExtn.setAttribute(KohlsXMLLiterals.A_EXTN_UPC, "");
					}					
					
					Element eleOrderLineDsOStatus = (Element) eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUSES).item(0);
					Element eleOrderLineDsNotes = (Element) eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
					
					eleOrderLineDs.removeChild(eleOrderLineDsOStatus);
					eleOrderLineDs.removeChild(eleOrderLineDsNotes);
					
					Element eleOrderLineDsItem = (Element) eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
					eleOrderLineDsItem.removeAttribute(KohlsXMLLiterals.A_PRODUCT_LINE);
					eleOrderLineDsItem.removeAttribute(KohlsXMLLiterals.A_UOM);
					eleOrderLineDsItem.removeAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS);
					
					//System.out.println("ShortDesc VALUE::::"+eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC));
					eleOrderLineDsItem.setAttribute(KohlsXMLLiterals.A_ITEM_DESC, eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC));
					
					// Order Broker end
					
					//System.out.println("Added Node to WMoS release XML : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));

					YFCElement yfcEleOrderLine = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_LINE);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, eleOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SCAC, sSCAC);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, sCarrierServiceCode);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, sItemId);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_UOM, sUOM);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, sQty);
					/** 
	    			 * Commented for Iteration 4 changes
	    			 */
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_RECEIPT_ID, strPreEnReceiptId);
					yfcEleOrderLines.appendChild(yfcEleOrderLine);

					YFCNode yfcNdOrderStatuses = YFCDocument.getNodeFor(eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUSES).item(0));

					YFCNode yfcNdOrderStatusToEcommXML = yfcDocOrderStatusToECommXML.importNode(yfcNdOrderStatuses, true);
					yfcEleOrderLine.appendChild(yfcNdOrderStatusToEcommXML);
					
					//Start --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
					
	    		    if(strExtnWrapTo != null && !("".equals(strExtnWrapTo.trim()))){
	    		    	if( mapGiftWrapTogetherCode.containsKey(strExtnWrapTo) == true){
		    		    	int countWrapTogether = mapGiftWrapTogetherCode.get(strExtnWrapTo).intValue();
		    		    	mapGiftWrapTogetherCode.put(strExtnWrapTo, ++countWrapTogether);
		    		    	if(!wrapMultTogether){
		    		    		wrapMultTogether = true;
		    		    	}
	    		    	} else {
	    		    		mapGiftWrapTogetherCode.put(strExtnWrapTo, new Integer(1));
	    		    	}
	    		    }
	    		    //End --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
					
			}

			if (!KohlsConstant.PRODUCT_LINE_SA.equalsIgnoreCase(sProductLine) && !KohlsConstant.PRODUCT_LINE_VGC.equalsIgnoreCase(sProductLine)) {
				//System.out.println("Product Line : " + sProductLine);
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO, sPickTicketNo);

				// Order Broker start
				/* Commented by OASIS Support for PMR 54606,379,000 -- OASIS_SUPPORT 14/03/2012
				if(strIsHazardous.equalsIgnoreCase("Y") && strExIsMilitary.equalsIgnoreCase("Y")){
					strShipVia = KohlsConstant.SHIP_VIA_PP;
				} else if (strExIsMilitary.equalsIgnoreCase("Y")) {
					strShipVia = KohlsConstant.SHIP_VIA_PM;
				} else if (strExIsPOBox.equalsIgnoreCase("Y")) {
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
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SHIP_VIA, strShipVia);
				
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_TOTAL_REL_UNITS, String.valueOf(strAccQty));
				
				if(strIsHazardous.equalsIgnoreCase(KohlsConstant.NO)){
					strExtnRG = getRoutingGuideExtnRG(eleGetOrderReleaseDetails);
				}else{
					strExtnRG = KohlsConstant.RG_HAZARDOUS;
				}
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RG, strExtnRG);
				
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
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_CARTON_TYPE, strCartonType);
				
				// set the Receipt Id to Extn fields
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_HDR_GFT_RECP_ID, strReceiptId);
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_PREENC_HDR_RECP_ID, strPreEnReceiptId);
				
				/* Commented for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
				if(isSingleLine && sExtnGiftWrapTogetherCode.size()==1){
					if(1.00==dOrderedQty)
						strExtnWrapToVal = KohlsConstant.WRAP_SINGLE_TO_S;
					else if(dOrderedQty>1.00)
						strExtnWrapToVal = KohlsConstant.WRAP_SINGLE_TO_T;					
				}else if(sExtnGiftWrapTogetherCode.size()==1) {
					strExtnWrapToVal = KohlsConstant.WRAP_SINGLE_TO_T;
				}else if(sExtnGiftWrapTogetherCode.size()>1){
					strExtnWrapToVal = KohlsConstant.WRAP_SINGLE_TO_S;
				}					
				*/
				//Start --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
				if (sExtnGiftWrapTogetherCode.size() > 1){
					  strExtnWrapToVal = KohlsConstant.WRAP_SINGLE_TO_S;
				}
				else {
				  if (wrapMultTogether == true) {
				    strExtnWrapToVal = KohlsConstant.WRAP_SINGLE_TO_T;
				  }
				  else {
				    strExtnWrapToVal = KohlsConstant.WRAP_SINGLE_TO_S;
				  }
				}
				//End --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012 
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_SINGLE_TOG, strExtnWrapToVal);
				
				// Order Broker end
				
				//sPackTypeList = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE);			
				/* Commented for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
				if (!strExtnWrapTo.equalsIgnoreCase("")) {
					eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_GW);				
				} else {
					eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_ST);
				}
				*/
				//Start --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
				if (!strExtnWrapCode.equalsIgnoreCase("")) {
					eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_GW);				
				} else {
					eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_ST);
				}
				//End --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
				//System.out.println("XML to WMos : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Release Message to WMoS : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
				}
				this.api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_SEND_RELEASE_TO_WMOS, docWMoSOrderReleaseXML);

			}

		}
			//KohlsUtil.registerProcessCompletion(env, sDataKey, sDataType, sTaskQKey);
			}
		
		}catch(YFSException ex){			
			ex.printStackTrace();
    		throw ex;
		}
		return null;
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
		

		Element elePrimaryInfo = (Element) docGetItemList.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
		Element elePersonInfoShipTo = (Element) XPathUtil.getNode((Node) eleGetOrderReleaseDetails, KohlsXMLLiterals.XP_ORDERRELEASE_PERSON_INFO);
		String sIsHazMat = elePrimaryInfo.getAttribute(KohlsXMLLiterals.A_IS_HAZMAT);

		String sCarrierServiceCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
		Element elePersonInfoShipToExtn = (Element)elePersonInfoShipTo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		String strIsMilitary = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY);
		String strIsPOBox = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX);
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
		
		Element elePersonInfoShipTo = (Element) XPathUtil.getNode((Node) eleGetOrderReleaseDetails, KohlsXMLLiterals.XP_ORDERRELEASE_PERSON_INFO);
		
		Element elePersonInfoShipToExtn = (Element)elePersonInfoShipTo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		String strIsMilitary = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY);
		String strIsPOBox = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX);
		String sCarrierServiceCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
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

	
	private Document getOrderReleaseDetailsTemplate() {

		YFCDocument yfcDocGetOrderReleaseDetailsTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE);
		YFCElement yfcEleGetOrderReleaseDetailsTemp = yfcDocGetOrderReleaseDetailsTemp.getDocumentElement();

		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, "");
		//yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_RELEASE_NO, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SCAC, "");
		//yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		//yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SHIP_TO_KEY, "");
		
		YFCElement yfcEleOrlNotes = yfcEleGetOrderReleaseDetailsTemp.createChild(KohlsXMLLiterals.E_NOTES);
		yfcEleOrlNotes.setAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES, "");
		YFCElement yfcEleOrlNoteTemp = yfcEleOrlNotes.createChild(KohlsXMLLiterals.E_NOTE);
		yfcEleOrlNoteTemp.setAttribute(KohlsXMLLiterals.A_SEQUENCE_NO, "");
		yfcEleOrlNoteTemp.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");
		yfcEleOrlNotes.appendChild(yfcEleOrlNoteTemp);
		yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcEleOrlNotes);	
		
		YFCElement yfcElePersonInfoShipToTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO);
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "");		
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_CITY, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_STATE, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_COUNTRY, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EMAIL_ID, "");
		yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcElePersonInfoShipToTemp);
		
				
		YFCElement yfcEleExtnInfoShipToTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX, "");
		yfcEleExtnInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY, "");
		yfcElePersonInfoShipToTemp.appendChild(yfcEleExtnInfoShipToTemp);

		YFCElement yfcEleOrderTemp = yfcEleGetOrderReleaseDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER);
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, "");
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "");
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_CREATE_TS, "");
		yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcEleOrderTemp);
		
		YFCElement yfcElePaymentMethodsTemp = yfcEleOrderTemp.createChild(KohlsXMLLiterals.E_PAYMENT_METHODS);
		YFCElement yfcElePaymentMethodTemp = yfcElePaymentMethodsTemp.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, "");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO, "");		
		yfcElePaymentMethodsTemp.appendChild(yfcElePaymentMethodTemp);
		yfcEleOrderTemp.appendChild(yfcElePaymentMethodsTemp);
		
		YFCElement yfcElePromotionsTemp = yfcEleOrderTemp.createChild(KohlsXMLLiterals.E_PROMOTIONS);
		YFCElement yfcElePromotionTemp = yfcElePromotionsTemp.createChild(KohlsXMLLiterals.E_PROMOTION);
		yfcElePromotionTemp.setAttribute(KohlsXMLLiterals.A_PROMOTION_ID, "");
		YFCElement yfcElePromotionExtnTemp = yfcElePromotionTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcElePromotionExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_PER, "");
		yfcElePromotionExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_AMNT, "");
		yfcElePromotionTemp.appendChild(yfcElePromotionExtnTemp);
		yfcElePromotionsTemp.appendChild(yfcElePromotionTemp);
		yfcEleOrderTemp.appendChild(yfcElePromotionsTemp);
		
		YFCElement yfcEleOrNotesTemp = yfcEleOrderTemp.createChild(KohlsXMLLiterals.E_NOTES);
		yfcEleOrNotesTemp.setAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES, "");
		YFCElement yfcEleOrNoteTemp = yfcEleOrNotesTemp.createChild(KohlsXMLLiterals.E_NOTE);
		yfcEleOrNoteTemp.setAttribute(KohlsXMLLiterals.A_REASON_CODE, "");
		yfcEleOrNoteTemp.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");
		yfcEleOrNotesTemp.appendChild(yfcEleOrNoteTemp);
		yfcEleOrderTemp.appendChild(yfcEleOrNotesTemp);	
		
		YFCElement yfcElePersonInfoBillToTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO);
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "");		
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_CITY, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_STATE, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_COUNTRY, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");		
		yfcEleOrderTemp.appendChild(yfcElePersonInfoBillToTemp);
		
		YFCElement yfcEleExtnOrderTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnOrderTemp.setAttribute(KohlsXMLLiterals.A_EXTN_HDR_GFT_RECP_ID, "");
		yfcEleOrderTemp.appendChild(yfcEleExtnOrderTemp);
		

		YFCElement yfcEleOrderLinesTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcEleOrderLineTemp = yfcEleOrderLinesTemp.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_GIFT_FLAG, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_GIFT_WRAP, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDERED_QUANTITY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_OPEN_QTY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_STATUS_QUANTITY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_LINE_TYPE, "");
		yfcEleOrderLinesTemp.appendChild(yfcEleOrderLineTemp);
		yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcEleOrderLinesTemp);
		
		YFCElement yfcEleLinePriceInfoTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_LINE_PRICE_INFO);
		yfcEleLinePriceInfoTemp.setAttribute(KohlsXMLLiterals.A_RETAIL_PRICE, "");
		yfcEleLinePriceInfoTemp.setAttribute(KohlsXMLLiterals.A_UNIT_PRICE, "");		
		yfcEleOrderLineTemp.appendChild(yfcEleLinePriceInfoTemp);
		
		YFCElement yfcEleLineChargesTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_LINE_CHARGES);
		YFCElement yfcEleLineChargeTemp = yfcEleLineChargesTemp.createChild(KohlsXMLLiterals.E_LINE_CHARGE);
		yfcEleLineChargeTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, "");
		yfcEleLineChargeTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, "");		
		yfcEleLineChargesTemp.appendChild(yfcEleLineChargeTemp);
		yfcEleOrderLineTemp.appendChild(yfcEleLineChargesTemp);
		
		YFCElement yfcEleLineTaxesTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_LINE_TAXES);
		YFCElement yfcEleLineTaxTemp = yfcEleLineTaxesTemp.createChild(KohlsXMLLiterals.E_LINE_TAX);
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX_NAME, "");
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, "");		
		yfcEleLineTaxesTemp.appendChild(yfcEleLineTaxTemp);
		yfcEleOrderLineTemp.appendChild(yfcEleLineTaxesTemp);
		
		YFCElement yfcEleInstrsTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_INSTRUCTIONS);
		YFCElement yfcEleInstrTemp = yfcEleInstrsTemp.createChild(KohlsXMLLiterals.E_INSTRUCTION);
		yfcEleInstrTemp.setAttribute(KohlsXMLLiterals.A_INSTRUCTION_TYPE, "");
		yfcEleInstrTemp.setAttribute(KohlsXMLLiterals.A_INSTRUCTION_TEXT, "");		
		yfcEleInstrsTemp.appendChild(yfcEleInstrTemp);
		yfcEleOrderLineTemp.appendChild(yfcEleInstrsTemp);
		
		YFCElement yfcEleNotesTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_NOTES);
		YFCElement yfcEleNoteTemp = yfcEleNotesTemp.createChild(KohlsXMLLiterals.E_NOTE);
		yfcEleNoteTemp.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");
		yfcEleNotesTemp.appendChild(yfcEleNoteTemp);
		yfcEleOrderLineTemp.appendChild(yfcEleNotesTemp);

		YFCElement yfcEleOrderStatuses = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_ORDER_STATUSES);
		YFCElement yfcEleOrderStatus = yfcEleOrderStatuses.createChild(KohlsXMLLiterals.E_ORDER_STATUS);
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_DATE, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_DESCRIPTION, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_QTY, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_REASON, "");
		yfcEleOrderStatuses.appendChild(yfcEleOrderStatus);
		yfcEleOrderLineTemp.appendChild(yfcEleOrderStatuses);

		YFCElement yfcEleOrderLineExtn = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SHIP_ALONE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ, "");
		yfcEleOrderLineTemp.appendChild(yfcEleOrderLineExtn);

		YFCElement yfcEleItemTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_ITEM);
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_LINE, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UPC_CODE, "");
		yfcEleOrderLineTemp.appendChild(yfcEleItemTemp);
		
		YFCElement yfcEleExtnOrderRelTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnOrderRelTemp.setAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO, "");
		yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcEleExtnOrderRelTemp);

		return yfcDocGetOrderReleaseDetailsTemp.getDocument();

	}
}
