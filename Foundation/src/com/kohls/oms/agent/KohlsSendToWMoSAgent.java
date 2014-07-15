package com.kohls.oms.agent;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.StringUtil;
import com.custom.util.XPathUtil;
import com.kohls.bopus.util.KohlsOrderPickProcessUtil;
import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseTaskAgent;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.kohls.bopus.util.KohlsBOPUSHoldCheck;

/** This Task Q based agent in called in the Order Pipeline to split the release for Ship Alone items to every quantity before creating the shipment.
 * The agent also send the release update to WMoS for Non-Ship Alone item and order status update to E-Commerce system.
 *
 *  @author Prashanth T G
 *  @author Baijayanta Bhattacharjee
 *  Added code for creating shipment for BOPUS scenario
 *
 *  Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 *
 *  INPUT XML <TaskQueue TaskQKey="" TransactionKey="" DataKey="" DataType="" AvailableDate="" Lockid="" Createts="" Modifyts=""
 *  			Createuserid="" Modifyuserid="" Createprogid="" Modifyprogid="" > <TransactionFilters DocumentParamsKey=""
 *  			DocumentType="" ProcessType="" ProcessTypeKey="" TransactionId="" TransactionKey="" /> <TaskQueue/>
 **/

public class KohlsSendToWMoSAgent extends YCPBaseTaskAgent {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsSendToWMoSAgent.class.getName());
	private YIFApi api;
	private Connection m_conn;
	private Properties props;
	ArrayList dependentreleaseList = new ArrayList();
	ArrayList releaseDetailsList = new ArrayList();

	public KohlsSendToWMoSAgent() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}



	public void setProperties(Properties props) throws Exception {
		
		this.props=props;

	}

	@Override
	public Document executeTask(YFSEnvironment env, Document inXML)
	throws Exception {
		// TODO Auto-generated method stub
		//log.debug("Inside executeTask");
		//log.debug("Input XML executeTask start: " + XMLUtil.getXMLString(inXML));
		this.m_conn = KohlsUtil.getDBConnection(env);
		Element eleRoot = inXML.getDocumentElement();
		//added by Baijayanta
		String sDeliveryMethod = "";
		//ended by Baijayanta
		//Order Broker start
		String strShipVia="";
		String strExIsPOBox="";
		String strExIsMilitary="";
		String strIsHazardous = "N";
		String strCartonType = KohlsConstant.CARTON_TYPE_BOX;// changed to BOX for PMR :05616,379,000
		String strLineType = "";
		String strGiftwrap = "";
		String strExtnRG = "";
		String strProdLine = "";
		int strAccQty = 0;
		int strExtWrapCnt = 0;
		String strExtnWrapTo = "";
		String strExtnWrapToVal = "";
		String strExtnWrapCode = "";
		boolean isSingleLine = false;
		String strReceiptId = "";
		String strPreEnReceiptId = "";
		//Start --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
		boolean wrapMultTogether = false;
		Map <String , Integer> mapGiftWrapTogetherCode = new HashMap <String , Integer>();
		//End --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
		boolean isShipmentCreated = false;
		String iniPackListVal = "";
		String inifulfillmentType = "";

		try{
		//Order Broker end
		String sDataType = eleRoot.getAttribute(KohlsXMLLiterals.A_DATA_TYPE);
		String sDataKey = eleRoot.getAttribute(KohlsXMLLiterals.A_DATA_KEY);
		String sTaskQKey = eleRoot.getAttribute(KohlsXMLLiterals.A_TASK_Q_KEY);
		if (KohlsConstant.ORDER_RELEASE_KEY.equalsIgnoreCase(sDataType)) {
			log.debug("Obtained Order Release Key from the transaction table");
			YFCDocument yfcDocGetOrderReleaseDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE_DETAIL);
			YFCElement yfcEleGetOrderDetails = yfcDocGetOrderReleaseDetails.getDocumentElement();
			yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sDataKey);
			//if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Input XML to getOrderReleaseDetails : " + XMLUtil.getXMLString(yfcDocGetOrderReleaseDetails.getDocument()));
				//log.debug(("Input XML to getOrderReleaseDetails : " + XMLUtil.getXMLString(yfcDocGetOrderReleaseDetails.getDocument())));
			//}
			
			env.setApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_DETAILS, this.getOrderReleaseDetailsTemplate());
			//log.debug("getOrderReleaseDetails Template : " + XMLUtil.getXMLString(this.getOrderReleaseDetailsTemplate()));
			Document docOutputGetOrderReleaseDetails = this.api.getOrderReleaseDetails(env, yfcDocGetOrderReleaseDetails.getDocument());
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_DETAILS);
			
			//log.debug("getOrderRelease Details : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));
			//if (YFCLogUtil.isDebugEnabled()) {
				log.debug("getOrderDetails Output XML : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));
			//}

			//log.debug("getOrderReleaseDetailsTemplate Output XML : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));

			Element eleGetOrderReleaseDetails = docOutputGetOrderReleaseDetails.getDocumentElement();
			String sMinOrderReleaseStatus = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_MIN_ORDER_RELEASE_STATUS);
			/*Added to Remove the Redundant Task getting created when SA is getting canceled, the cancellation would move the Line to release Status
			generating a task for SENT_TO_WMOS agent */
			log.debug("INSIDE SEND2WMOS - sMinOrderReleaseStatus = " + sMinOrderReleaseStatus);
			//log.debug("INSIDE SEND2WMOS - sMinOrderReleaseStatus = " + sMinOrderReleaseStatus);
			if(!sMinOrderReleaseStatus.equalsIgnoreCase(KohlsConstant.STATUS_RELEASED)){
				log.debug("INSIDE SEND2WMOS - sMinOrderReleaseStatus IS NOT VALID FOR THIS TRANSACTION" + sMinOrderReleaseStatus);
				//log.debug("INSIDE SEND2WMOS - sMinOrderReleaseStatus IS NOT VALID FOR THIS TRANSACTION" + sMinOrderReleaseStatus);
				KohlsUtil.registerProcessCompletion(env, sDataKey, sDataType, sTaskQKey);
				return null;

			}
			/* END */
			Element eleGetOrderDetail = (Element) eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);

			String sOrderReleaseKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY);
			String sOrdHdrKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
			String sOrderNo = eleGetOrderDetail.getAttribute(KohlsXMLLiterals.A_ORDERNO);
			String sShipToKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SHIP_TO_KEY);
			String sEnterpriseCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);
			String sShipNode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
			String sSellerOrgCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE);
			String sReleaseNo = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_RELEASE_NO);
			String sCarrierServiceCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
			String sSCAC = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SCAC);
			String sReqShipDate = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE);
			String sReqDeliveryDate = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE);
			String sExpectedShipmentDate = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_EXPECTED_SHIPMENT_DATE);


			String sOrderDate = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_DATE);
			String sFinalOrdDate = sOrderDate.substring(5,7) + sOrderDate.substring(8,10) + sOrderDate.substring(2,4) + sOrderDate.substring(11,19).replace(":","");
			//get the node type
			String sNodeType="";
			sNodeType = KohlsUtil.getNodeType(sShipNode, env);
			boolean isnodeRDCOrStore = KohlsConstant.ATTR_RDC.equals(sNodeType) ||
					KohlsConstant.ATTR_STORE.equals(sNodeType) ||
						KohlsConstant.ATTR_RDC_STORE.equals(sNodeType);
			boolean isnodeRDC = KohlsConstant.ATTR_RDC.equals(sNodeType) ||
						KohlsConstant.ATTR_RDC_STORE.equals(sNodeType);
			
			//Check for BOPUS lines
			sDeliveryMethod = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
			String sPickShipNode = "";
			
			//Added by Baijayanta for defect 325
			Document shpmntDocForDpndntRls = null;
			if("PICK".equalsIgnoreCase(sDeliveryMethod)){
				releaseDetailsList.add(docOutputGetOrderReleaseDetails);
				shpmntDocForDpndntRls = getDependentReleases(env,sShipNode,sOrdHdrKey,sSellerOrgCode,sOrderNo);
				log.debug("the document is :"+ SCXmlUtil.getString(shpmntDocForDpndntRls));
				if(dependentreleaseList.size() > 1){
					this.api.createShipment(env, shpmntDocForDpndntRls);
					log.debug("after shipment creation");
					KohlsUtil.registerProcessCompletion(env, sDataKey, sDataType, sTaskQKey);
					dependentreleaseList.clear();
					releaseDetailsList.clear();
					return null;
				}
			}
			//Ended by Baijayant for Defect 325
			// get and stamp the receipt id
			Element eleOrNotes = (Element) eleGetOrderDetail.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
			String strNumOfNotes = eleOrNotes.getAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES);
			int inNumOfNotes = Integer.parseInt(strNumOfNotes);
			if(inNumOfNotes>0){
				log.debug("Notes have been obtained hence the logic to loop through notes and find the receipt id begins");
				Map mpNote = new HashMap<String, String>();
				NodeList ndOrNote = eleOrNotes.getElementsByTagName(KohlsXMLLiterals.E_NOTE);
				for(int j=0;j<ndOrNote.getLength();j++){
					Element eleOrNote = (Element) ndOrNote.item(j);
					String strNoteReasCode = eleOrNote.getAttribute(KohlsXMLLiterals.A_REASON_CODE);
					String strNoteReasText = eleOrNote.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);
					mpNote.put(strNoteReasCode, strNoteReasText);
				}
				//Added  by Zubair Bug 4458 - Begin
				if(isnodeRDC){
					String sMapShipNode = KohlsUtil.getEFCForStore(sShipNode, env);
					
					if(KohlsConstant.PICK.equalsIgnoreCase(sDeliveryMethod)){  //For BOPUS Order
						strReceiptId = (String) mpNote.get(KohlsConstant.BPS+"_"+sMapShipNode);
						log.debug("Receipt id obtained is"+strReceiptId + " Using shipnode: " + KohlsConstant.BPS+"_"+sMapShipNode);
					}else{  //Non BOPUS Order
						strReceiptId = (String) mpNote.get(sMapShipNode);
						log.debug("Receipt id obtained is"+strReceiptId + " Using shipnode: " + sMapShipNode);
					}
					
					if(KohlsConstant.PICK.equalsIgnoreCase(sDeliveryMethod)){  //For BOPUS Order
						strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+KohlsConstant.BPS+"_"+sMapShipNode);
						log.debug("strPreEnReceiptId  obtained is" +KohlsConstant.PREENC+KohlsConstant.BPS+"_"+strPreEnReceiptId);
					}else{//Non BOPUS Order
						strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+sMapShipNode);
						log.debug("strPreEnReceiptId  obtained is" +strPreEnReceiptId);
					}
					
				} else {
					if(KohlsConstant.PICK.equalsIgnoreCase(sDeliveryMethod)){  //For BOPUS Order
						strReceiptId = (String) mpNote.get(KohlsConstant.BPS+"_"+sShipNode);
						log.debug("Receipt id obtained is"+strReceiptId + " Using shipnode: " + KohlsConstant.BPS+"_"+sShipNode);
						strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+KohlsConstant.BPS+"_"+sShipNode);
						log.debug("strPreEnReceiptId  obtained is" +strPreEnReceiptId);
					}else{ //Non BOPUS Order
						strReceiptId = (String) mpNote.get(sShipNode);
						log.debug("Receipt id obtained is"+strReceiptId + " Using shipnode: " + sShipNode);
						strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+sShipNode);
						log.debug("strPreEnReceiptId  obtained is" +strPreEnReceiptId);
					}
					//Added  by Zubair Bug 4458 - Begin
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
			//log.debug("release message doc is:"+XMLUtil.getXMLString(docWMoSOrderReleaseXML));

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
			/**
			 * Commented for Iteration 4 changes
			 */
			 String strHdrGftReceiptId = eleExtnOrderDtl.getAttribute(KohlsXMLLiterals.A_EXTN_HDR_GFT_RECP_ID);
			log.debug("getting the strHdrGftReceiptid from the OrderDetails Extn element ExtnHdrGiftReceiptID"+strHdrGftReceiptId);
			String strExtnHdrGiftReceiptID = strHdrGftReceiptId;
			log.debug("setting strExtnHdrGiftReceiptID to strHdrGftReceiptId"+strExtnHdrGiftReceiptID);
			/*
			if (strExtnHdrGiftReceiptID != null)
			{
			eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_HDR_GFT_RECP_ID, strExtnHdrGiftReceiptID);
			} */

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

			//log.debug("docWMoSOrderReleaseXML : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
			//log.debug("Output XML  : " + XMLUtil.getXMLString(eleGetOrderReleaseDetails.getOwnerDocument()));



			//Document Creation for changeOrder XML
			YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
			YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
			yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_SEND_RELEASE_TO_WMOS);
			yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrdHdrKey);

			YFCElement yfcEleOrderStatusOrderLines = yfcDocChangeOrderStatus.createElement(KohlsXMLLiterals.E_ORDER_LINES);
			yfcEleChangeOrderStatus.appendChild(yfcEleOrderStatusOrderLines);

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
			String sPickTicketNo = "";
			String sPackTypeList = "";
			String sProductLine = "";
			String strLneTp = "";
			/** Iteration 4 changes */
			String sShipNodeForRcptId="";
			String sLineNo="";
			//String strExtnSAGiftReceiptID="";
			String strSALnGiftRcptID="";
			String sReceiptIDValues="";
			//String sExtnPreEncodedHdrReceiptID="";
			String strPreEncodedLnGiftRcptID="";
			//String sExtnPreEncodedSAGiftReceiptID="";
			String sExtnPreEncodedSALnReceiptID ="";
			boolean isLineRcptGen = false;
			Set sExtnGiftWrapTogetherCode = new HashSet();
			double dOrderedQty=0.00;


			int iCount = 0;
			//Order Broker start
			if(ndlstOrderLines.getLength()==1){
				isSingleLine = true;
			}

			//Order Broker end

			
			Document docOrderStatusToECommXML = null;
			Element eleOrderStatusToECommXML = null;
			Element eleOrderStatusOrderLines = null;
			Element eleOrderStatusOrderLine = null;
			Element eleOrderStatuses = null;
			Element eleOrderStatus = null;

			String prevFilterType = null;
			
			Document yfcBopusDocCreateShipment = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			Element eleCreateShipment = yfcBopusDocCreateShipment.getDocumentElement();
			Element eleShipmentExtn = (Element) SCXmlUtil.createChild(eleCreateShipment, KohlsXMLLiterals.E_EXTN);
			Element eleShipmentLines = (Element) SCXmlUtil.createChild(eleCreateShipment, KohlsXMLLiterals.E_SHIPMENT_LINES);
			// Added for ShipmentExpired CR - RAVI
			Element eleAdditionalDates = (Element) SCXmlUtil.createChild(eleCreateShipment, KohlsXMLLiterals.E_ADDITIONAL_DATES);
			Element eleAdditionalDate = (Element) SCXmlUtil.createChild(eleAdditionalDates, KohlsXMLLiterals.E_ADDITIONAL_DATE);

			for (int i = 0; i < ndlstOrderLines.getLength(); i++) {
				log.debug("INSIDE SEND2WMOS - LOOPING THROUGH LINES OF THE RELEASE");
				//log.debug("INSIDE SEND2WMOS - LOOPING THROUGH LINES OF THE RELEASE");
				//isLineRcptLengthZero = false;
				eleOrderLine = (Element) ndlstOrderLines.item(i);
				log.debug("Orderline element is " +XMLUtil.getElementXMLString(eleOrderLine));
				log.debug(SCXmlUtil.getString(eleOrderLine));
				//added by Baijayanta
				sDeliveryMethod = eleOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
				//ended by Baijayanta
				//Start --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
				String sCarrierServiceCodeLine = eleOrderLine.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
				// End --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
				String sOrderLineKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				String sOrderHeaderKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
				String sQty = eleOrderLine.getAttribute(KohlsXMLLiterals.A_STATUS_QUANTITY);
				dOrderedQty = Double.parseDouble(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QUANTITY));
				//added by vinay
                String packListType = eleOrderLine.getAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE);
                String fulfillmentType =  eleOrderLine.getAttribute("FullfilmentType");
                
				//Start --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
				if (sCarrierServiceCodeLine == null || sCarrierServiceCodeLine.trim().equals("")) {
					if(sCarrierServiceCode != null && !sCarrierServiceCode.trim().equals("")) {
						sCarrierServiceCodeLine = sCarrierServiceCode;
					}
				}
				// End --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012

					// Order Broker start
				String strGftWp = eleOrderLine.getAttribute(KohlsXMLLiterals.A_GIFT_WRAP);
				strLneTp = eleOrderLine.getAttribute(KohlsXMLLiterals.A_LINE_TYPE);
				String strGftFg = eleOrderLine.getAttribute(KohlsXMLLiterals.A_GIFT_FLAG);

				//Obtain the Line Gift Receipt ID and check if the value is null.
                Element eleOrderLineExtn = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
                
              //added by Baijayanta
    			String sExpirationDate = eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXPIRATION_DATE);
    			String sExtnExpectedShipmentDate = eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_EXPECTED_SHIPMENT_DATE);
    			
    			//ended by Baijayanta

                //Added by Zubair for ExtnWrapTogetherGroupCode - Begin
                String sExtnWrapTogetherGroupCode = eleOrderLineExtn.getAttribute("ExtnWrapTogetherGroupCode");
				if(sExtnWrapTogetherGroupCode == null){
					sExtnWrapTogetherGroupCode="";
				}
				boolean bExtnWrapTogetherGroupCode = !"".equals(sExtnWrapTogetherGroupCode.trim());
				//Added by Zubair for ExtnWrapTogetherGroupCode - End
                /**
    			 * Commented for Iteration 4 changes
    			 */
				 String strLnGiftRcptID = eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID);
				 log.debug("Check if strLnGiftRcptID has a value" +strLnGiftRcptID);

					if (strLnGiftRcptID != null
							&& !strLnGiftRcptID.trim().equals("")) {
					if (strLnGiftRcptID.length() == 1) {
						//log.debug("strLnGiftRcptID.length() == 1");
						isLineRcptGen = true;
					}
				}
				if(bExtnWrapTogetherGroupCode){
					strGiftwrap = KohlsConstant.YES;
				}
				if(strLneTp.equalsIgnoreCase(KohlsConstant.LINE_TYPE_PGC)){
					strLineType = KohlsConstant.LINE_TYPE_PGC;
				}
				if(!sQty.equals(KohlsConstant.BLANK))
				strAccQty += (int)Float.parseFloat(sQty);

				eleItem = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
				String sItemId = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
				String sUOM = eleItem.getAttribute(KohlsXMLLiterals.A_UOM);
				sProductLine = eleItem.getAttribute(KohlsXMLLiterals.A_PRODUCT_LINE);
							//	log.debug("value of product line is" +sProductLine);
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
					for(int p=0; p<nodeOrderLineInst.getLength(); p++){
						Element eleOrderLineInst = (Element) nodeOrderLineInst.item(p);
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

				//log.debug("Product line1 : " + sProductLine);
				//log.debug("Status Qty : " + sQty);
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Product Line : " + sProductLine);
				}
				//env.clearApiTemplate(KohlsConstant.API_GET_ORGANIZATION_LIST);
				if (KohlsConstant.PRODUCT_LINE_SA.equalsIgnoreCase(sProductLine) && (!sDeliveryMethod.equalsIgnoreCase(KohlsConstant.PICK))) {
					log.debug("if productline value is SA");
					YFCDocument yfcDocCreateShipment = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
					YFCElement yfcEleCreateShipment = yfcDocCreateShipment.getDocumentElement();
					// Start --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
					yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, sCarrierServiceCodeLine);
					// End --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
					yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_TO_ADDRESS_KEY, sShipToKey);
					yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, sEnterpriseCode);
					//Start --- Added for Store Issues with ShipAlones 3/13/2013
					//Adding Order No to the CreateShipment XML
					if(isnodeRDCOrStore){
						yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_ORDERNO, sOrderNo);
					}
					//End --- Added for Store Issues with ShipAlones 3/13/2013
					yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_SHIPNODE, sShipNode);
					yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_EXPECTED_DELIVERY_DATE, "");
					yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_EXPECTED_SHIPMENT_DATE, "");
					yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, sSellerOrgCode);
					yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);


					YFCElement yfcEleShipmentLines = yfcDocCreateShipment.createElement(KohlsXMLLiterals.E_SHIPMENT_LINES);
					YFCElement yfcEleShipmentLine = yfcDocCreateShipment.createElement(KohlsXMLLiterals.E_SHIPMENT_LINE);
					YFCElement yfcEleShipmentExtn = yfcDocCreateShipment.createElement(KohlsXMLLiterals.E_EXTN);
					yfcEleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_IS_SHIP_ALONE, "Y");
					//Added by Abhijit Kumar Roy - Stamping Shipment_Type for SA items sourced in RDC/Store
					boolean isSA = KohlsConstant.PRODUCT_LINE_SA.equalsIgnoreCase(sProductLine);
					//Added by OASIS- adding argument sNodeType to the method
					setShipmentType(yfcEleCreateShipment, sCarrierServiceCode, strGiftwrap, eleGetOrderReleaseDetails,sNodeType, isSA, env);
					// End
					//Added  by Zubair Bug 4458 - Begin
					yfcEleShipmentExtn.setAttribute("ExtnStoreRdcReceiptID", strReceiptId);
					//Added by Zubair Bug 4458 - End

					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, sOrderLineKey);
					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, sItemId);
					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_UOM, sUOM);
					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, KohlsConstant.PRODUCT_CLASS_GOOD);
					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, KohlsConstant.SHIP_ALONE_QTY);
					yfcEleShipmentLines.appendChild(yfcEleShipmentLine);
					yfcEleCreateShipment.appendChild(yfcEleShipmentLines);
					yfcEleCreateShipment.appendChild(yfcEleShipmentExtn);

					Double iQty = Double.parseDouble(sQty);

					for (int j = 0; j < iQty; j++) {

						/**
		    			 * Commented for Iteration 4 changes
		    			 */

						sPickTicketNo = StringUtil.prepadStringWithZeros(KohlsUtil.getNextSeqNo(env, this.m_conn), KohlsConstant.PICKTICKET_LEN);
						yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_PICKTICKET_NO, sPickTicketNo);


						// Code for SA receipt header id
						if (strExtnHdrGiftReceiptID == null ||  strExtnHdrGiftReceiptID.equals(""))
						{
						log.debug("if receiptid is null or ");
						if (strGftFg.equalsIgnoreCase(KohlsConstant.YES))
						{
						if (isLineRcptGen)
						{
							sLineNo = StringUtil.prepadStringWithZeros(eleOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO), KohlsConstant.PMLINENO_LEN);
							//sReceiptIDValues = getRegTransValues(env);
							sShipNodeForRcptId = StringUtil.prepadStringWithZeros(sShipNode,KohlsConstant.SHIPNODE_LEN);
							//Start --- Modified for SF Case # 00382750 -- OASIS_SUPPORT 04/1/2012
							strSALnGiftRcptID =  sLineNo +  strPreEnReceiptId.substring(3);

							sExtnPreEncodedSALnReceiptID =  sLineNo +  strPreEnReceiptId.substring(3);
							//End --- Modified for SF Case # 00382750 -- OASIS_SUPPORT 04/1/2012
							if(isnodeRDCOrStore){
								log.debug("This is a RDC/STORE receipt id hence not appending the gift character");
								strSALnGiftRcptID = getFinalReceiptIDValue(sExtnPreEncodedSALnReceiptID);
							}else{
								log.debug("This is a ( NOT )RDC/STORE receipt id hence  appending the gift character");
								strSALnGiftRcptID = getFinalReceiptIDValue(sExtnPreEncodedSALnReceiptID).concat(strLnGiftRcptID);
							}

							yfcEleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_PREENC_SA_LINE_GIFT_RCPT_ID, sExtnPreEncodedSALnReceiptID);
							yfcEleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SA_LINE_GIFT_RCPT_ID, strSALnGiftRcptID);
							}
						 }
						}

						if (YFCLogUtil.isDebugEnabled()) {
							log.debug("Input XML createShipment : " + XMLUtil.getXMLString(yfcDocCreateShipment.getDocument()));
						}
						//log.debug("Input XML createShipment : " + XMLUtil.getXMLString(yfcDocCreateShipment.getDocument()));
						this.api.createShipment(env, yfcDocCreateShipment.getDocument());
					}

					//	registerProcessCompletion(env, sDataKey, sDataType, sTaskQKey);
					//	return null;

				}
					else if (!isnodeRDCOrStore && isVGC(strLneTp, sProductLine)) {
						log.debug("if isVGC is true");
					//log.debug("Inside Virtual Gift Card ");
					YFCDocument yfcDocConfirmShipment = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
					YFCElement yfcEleConfirmShipment = yfcDocConfirmShipment.getDocumentElement();				
					
					YFCElement yfcEleShipmentLines = yfcDocConfirmShipment.createElement(KohlsXMLLiterals.E_SHIPMENT_LINES);
					YFCElement yfcEleShipmentLine = yfcDocConfirmShipment.createElement(KohlsXMLLiterals.E_SHIPMENT_LINE);
					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, sOrderLineKey);
					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
					yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, sQty);
					yfcEleShipmentLines.appendChild(yfcEleShipmentLine);
					yfcEleConfirmShipment.appendChild(yfcEleShipmentLines);

					//log.debug("Input XML to confirmShipment : " + XMLUtil.getXMLString(yfcDocConfirmShipment.getDocument()));
					if (YFCLogUtil.isDebugEnabled()) {
						log.debug("Input XML to confirmShipment : " + XMLUtil.getXMLString(yfcDocConfirmShipment.getDocument()));
					}
					this.api.confirmShipment(env, yfcDocConfirmShipment.getDocument());

				} 
					//added by Baijayanta 
			        //Forming the shipment line for BOPUS line
				    else if (sDeliveryMethod.equalsIgnoreCase(KohlsConstant.PICK)) {
				    	log.debug("delivery method is PICK");
				    	//yfcBopusDocCreateShipment = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);

						eleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXPIRATION_DATE, sExpirationDate);
						eleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_EXPECTED_SHIPMENT_DATE, sExtnExpectedShipmentDate);
						eleShipmentExtn.setAttribute("ExtnStorePreencReceiptID", strReceiptId);
						
						// Added for ShipmentExpired CR - RAVI
						eleAdditionalDate.setAttribute(KohlsXMLLiterals.A_DATE_TYPE_ID, KohlsXMLLiterals.A_EXPIRATION_DATE);
						eleAdditionalDate.setAttribute(KohlsXMLLiterals.A_ACTUAL_DATE, sExpirationDate);
						
						//added by vinay
						if(null != packListType && packListType.contains(KohlsConstant._JEW)) {
							eleShipmentExtn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_INDICATOR, KohlsConstant.JEW);
							iniPackListVal = packListType;
						}
						
						if(null != fulfillmentType && fulfillmentType.equalsIgnoreCase("STORE_PICKUP")){
							inifulfillmentType=fulfillmentType;
						}
						// Start --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
						eleCreateShipment.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, sCarrierServiceCodeLine);
						// End --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
						eleCreateShipment.setAttribute(KohlsXMLLiterals.A_TO_ADDRESS_KEY, sShipToKey);
						eleCreateShipment.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, sEnterpriseCode);
						//Start --- Added for Store Issues with ShipAlones 3/13/2013
						//Adding Order No to the CreateShipment XML
						//Defect 105 - Commented the condition check before setting order no
						//if(isnodeRDCOrStore){
							eleCreateShipment.setAttribute(KohlsXMLLiterals.A_ORDERNO, sOrderNo);
						//}
						eleCreateShipment.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY,sOrderHeaderKey);
						//End --- Added for Store Issues with ShipAlones 3/13/2013
						eleCreateShipment.setAttribute(KohlsXMLLiterals.A_SHIPNODE, sShipNode);
						eleCreateShipment.setAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD, sDeliveryMethod);
						log.debug("delivery method:"+sDeliveryMethod);
						
						eleCreateShipment.setAttribute(KohlsXMLLiterals.A_EXPECTED_DELIVERY_DATE, "");
						
						//Defect - 172 Changing Expected Ship Date to Extended Expected Shipment Date - Bala
						eleCreateShipment.setAttribute(KohlsXMLLiterals.A_EXPECTED_SHIPMENT_DATE, sExtnExpectedShipmentDate);
						
						eleCreateShipment.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, sSellerOrgCode);
						eleCreateShipment.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
						//yfcEleCreateShipment.setAttribute("Status","1100.70.06.10");
						
						eleCreateShipment.appendChild(eleShipmentExtn);


						
						eleCreateShipment.appendChild(eleShipmentExtn);

						Double iQty = Double.parseDouble(sQty);
						
						Element eleShipmentLine = null;

						eleShipmentLine = SCXmlUtil.createChild(eleShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);
						eleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
						eleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, sOrderLineKey);
						eleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
						eleShipmentLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, sItemId);
						eleShipmentLine.setAttribute(KohlsXMLLiterals.A_UOM, sUOM);
						eleShipmentLine.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, KohlsConstant.PRODUCT_CLASS_GOOD);
						eleShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, String.valueOf(iQty));
													
						
						
						KohlsOrderPickProcessUtil pickProcessUtil = new KohlsOrderPickProcessUtil();
						
						//String filterVal = "SalesFloor";
						String filterVal = pickProcessUtil.givItemFilterObtain(env,sItemId,sShipNode);
						
						if (YFCCommon.isVoid(prevFilterType)){
							prevFilterType = filterVal;
						}
						else{
							if (!filterVal.equals(prevFilterType)){
								prevFilterType=KohlsConstant.MIXED;
							}
						}
						
						eleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_FILTER_VAL, prevFilterType);
						//eleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_FILTER_VAL, "");
						log.debug("the input doc for createShipment:"+XMLUtil.getXMLString(yfcBopusDocCreateShipment));
						
				} //ended by Baijayanta
				    else {
					log.debug("inside else loop ");

					/**
	    			 * Commented for Iteration 4 changes
	    			 */

					if (0 == iCount) {
						sPickTicketNo = StringUtil.prepadStringWithZeros(KohlsUtil.getNextSeqNo(env, this.m_conn), KohlsConstant.PICKTICKET_LEN);
						//log.debug("Generating Pickticket  : " + sPickTicketNo);

						if (YFCLogUtil.isDebugEnabled()) {
							log.debug("PickTicket No  : " + sPickTicketNo);
						}
					}

					//log.debug("ROuting Guide : " + sRG);
					iCount++;

					// Order Broker start
					env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, this.getItemListTemplate());
					Document docGetItemListEx = this.api.getItemList(env, this.getItemListInputXML(env, sItemId));
					env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);

					log.debug("GetItemList OUTPUT::::"+XMLUtil.getXMLString(docGetItemListEx));

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

					/**
	    			 * Commented for Iteration 4 changes
	    			 */
					//Line Gift Receipt ID generation start

						if (strGftFg.equalsIgnoreCase(KohlsConstant.YES))
						{
							log.debug("if gift flag is yes");
							if (isLineRcptGen)
							{
							String strOrderLineNumber = eleOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
							sLineNo = StringUtil.prepadStringWithZeros(strOrderLineNumber, KohlsConstant.PMLINENO_LEN);
							//sReceiptIDValues = getRegTransValues(env);
							sShipNodeForRcptId = StringUtil.prepadStringWithZeros(sShipNode,KohlsConstant.SHIPNODE_LEN);
							//Start --- Modified for SF Case # 00382750 -- OASIS_SUPPORT 04/1/2012
							strPreEncodedLnGiftRcptID =  sLineNo +  strPreEnReceiptId.substring(3);
							//End --- Modified for SF Case # 00382750 -- OASIS_SUPPORT 04/1/2012
							//log.debug("PreEncoded Line Receipt ID :" + strPreEncodedLnGiftRcptID);
							if(isnodeRDCOrStore){
								log.debug("This is a RDC/STORE receipt id hence not appending the gift character");
								strLnGiftRcptID = getFinalReceiptIDValue(strPreEncodedLnGiftRcptID);
								
								// Start - Added by Saravana as a part of 60-Lines fix
								this.callChangeOrderForLnGftRcptID(env, sOrderHeaderKey, sOrderLineKey,  strLnGiftRcptID, strPreEncodedLnGiftRcptID, sShipNode,strOrderLineNumber);
								// End - Added by Saravana as a part of 60-Lines fix
							}else{
								log.debug("This is a ( NOT )RDC/STORE receipt id hence  appending the gift character");
								strLnGiftRcptID = getFinalReceiptIDValue(strPreEncodedLnGiftRcptID).concat(strLnGiftRcptID);
							}
							this.callChangeReleaseForLnGftRcptID(env, sOrderReleaseKey,  strLnGiftRcptID, strPreEncodedLnGiftRcptID, sShipNode,strOrderLineNumber);
							}
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

					if(strLnGiftRcptID.length() == 1)
						eleOrderLineDsExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, "");
					else
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

					//log.debug("ShortDesc VALUE::::"+eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC));
					eleOrderLineDsItem.setAttribute(KohlsXMLLiterals.A_ITEM_DESC, eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC));

					// Order Broker end

					//log.debug("Added Node to WMoS release XML : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));

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
					log.debug("set the receipt id on the orderline");
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_RECEIPT_ID, strPreEnReceiptId);
					yfcEleOrderLines.appendChild(yfcEleOrderLine);

					YFCNode yfcNdOrderStatuses = YFCDocument.getNodeFor(eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUSES).item(0));

					YFCNode yfcNdOrderStatusToEcommXML = yfcDocOrderStatusToECommXML.importNode(yfcNdOrderStatuses, true);
					yfcEleOrderLine.appendChild(yfcNdOrderStatusToEcommXML);

				}

				
				YFCElement yfcEleOrderStatusOrderLine = yfcDocChangeOrderStatus.createElement(KohlsXMLLiterals.E_ORDER_LINE);
				//added by Baijayanta
				//if(sDeliveryMethod.equalsIgnoreCase(KohlsConstant.PICK)) {
				 //yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_BASEDROPSTATUS, KohlsConstant.STATUS_AWAITING_STORE_PICK);
				//} else {
				 yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_BASEDROPSTATUS, KohlsConstant.STATUS_SEND_TO_WMOS);
				//}
				//ended by Baijayanta
				yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, sOrderLineKey);
				yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, sQty);
				yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
				yfcEleOrderStatusOrderLines.appendChild(yfcEleOrderStatusOrderLine);

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
			log.debug("INSIDE SEND2WMOS - COMPLETED LOOPING THROUGH LINES OF THE RELEASE");
			
		
			if (sDeliveryMethod.equalsIgnoreCase(KohlsConstant.PICK) && !YFCCommon.isVoid(yfcBopusDocCreateShipment)) {
				
				
				
				//Order modification hold check - Ravi
				
				KohlsBOPUSHoldCheck holdCheckObj = new KohlsBOPUSHoldCheck();
				Document docOrder = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
				Element eleOrder = docOrder.getDocumentElement();
				eleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrdHdrKey);
				eleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, sOrderNo);
				boolean eComHoldExists = holdCheckObj.orderHoldCheck(env, docOrder);
				
				if (!eComHoldExists) {
				
					Element yfcEleShipmentExtn = yfcBopusDocCreateShipment.getDocumentElement();
					yfcEleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_FILTER_VAL, prevFilterType);
					this.api.createShipment(env, yfcBopusDocCreateShipment);

				}
				else {
					return null;
				}
			}
			//log.debug("INSIDE SEND2WMOS - COMPLETED LOOPING THROUGH LINES OF THE RELEASE");

			//Punit: Omni: Adding logic for creating shipments for RDC/Store releases
			if(isnodeRDCOrStore && !KohlsConstant.PRODUCT_LINE_SA.equalsIgnoreCase(sProductLine) && !sDeliveryMethod.equalsIgnoreCase(KohlsConstant.PICK)){
				log.debug("INSIDE SEND2WMOS - CREATING SHIPMENT FOR RDC/STORE");
				//log.debug("INSIDE SEND2WMOS - CREATING SHIPMENT FOR RDC/STORE");
				YFCDocument yfcDocCreateShipment = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
				YFCElement yfcEleCreateShipment = yfcDocCreateShipment.getDocumentElement();
				yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
				yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_CREATE);
				yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, sOrderNo);
				yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_IGNORE_ORDERING, KohlsConstant.YES);
				YFCElement yfcEleShipmentExtn = yfcDocCreateShipment.createElement(KohlsXMLLiterals.E_EXTN);
				yfcEleCreateShipment.appendChild(yfcEleShipmentExtn);
				//Modified by Abhijit Kumar Roy - Added the boolean in the method
				boolean isNotSA = KohlsConstant.PRODUCT_LINE_SA.equalsIgnoreCase(sProductLine);
				//Added by OASIS- adding argument sNodeType to the method
				setShipmentType(yfcEleCreateShipment, sCarrierServiceCode, strGiftwrap, eleGetOrderReleaseDetails,sNodeType, isNotSA, env);
				//Added by Zubair Bug 4458 - Begin
				log.debug("receipt id is getting stamped on the shipment extn element");
				yfcEleShipmentExtn.setAttribute("ExtnStoreRdcReceiptID", strReceiptId);
				//Added by Zubair Bug 4458 - End
				YFCElement yfcEleOrderReleases = yfcEleCreateShipment.createChild("OrderReleases");
				YFCElement yfcEleOrderRelease = yfcEleOrderReleases.createChild(KohlsXMLLiterals.E_ORDER_RELEASE);
				yfcEleOrderRelease.setAttribute(KohlsConstant.ATTR_ASSOCIATION_ACTION, "Add");

				yfcEleOrderRelease.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
				YFCDocument createShipTemplate = YFCDocument.parse("<Shipment>" +
						"<Extn ExtnStoreRdcReceiptID=\"\" />"+
						"<ShipmentLines>"+
						"<ShipmentLine>"+
						"<Extn/>"+
						"</ShipmentLine>"+
						"</ShipmentLines>"+
						"</Shipment>");
				env.setApiTemplate("createShipment", createShipTemplate.getDocument());
				log.debug("INSIDE SEND2WMOS - CREATING SHIPMENT FOR RDC/STORE WITH XML: " + XMLUtil.getXMLString(yfcDocCreateShipment.getDocument()));
				//log.debug("INSIDE SEND2WMOS - CREATING SHIPMENT FOR RDC/STORE WITH XML: " + XMLUtil.getXMLString(yfcDocCreateShipment.getDocument()));
				Document createShipmentOutDoc = this.api.createShipment(env, yfcDocCreateShipment.getDocument());
				env.clearApiTemplate("createShipment");
				//Added by Abhijit Kumar Roy - Order Status message to Ecommerce for Orders sourced from RDC/Store
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Release Status Message for Store/RDC To Ecommerce : "	+ XMLUtil.getXMLString(yfcDocOrderStatusToECommXML.getDocument()));
							}
					this.api.executeFlow(env,KohlsConstant.SERVICE_KOHLS_SEND_RELEASE_STATUS_TO_ECOMM,	yfcDocOrderStatusToECommXML.getDocument());

				log.debug("INSIDE SEND2WMOS - CREATED SHIPMENT FOR RDC/STORE OUTPUT XML: " + XMLUtil.getXMLString(createShipmentOutDoc));
				//log.debug("INSIDE SEND2WMOS - CREATED SHIPMENT FOR RDC/STORE OUTPUT XML: " + XMLUtil.getXMLString(createShipmentOutDoc));

			}

			//End Punit Omni changes
			else if (!KohlsConstant.PRODUCT_LINE_SA.equalsIgnoreCase(sProductLine) && !isVGC(strLneTp, sProductLine) && !sDeliveryMethod.equalsIgnoreCase(KohlsConstant.PICK)) {
				log.debug("if not SA and if not isVGC enter this loop");
				//log.debug("Product Line : " + sProductLine);
				log.debug("INSIDE SEND2WMOS - REGULAR RELEASE - SENDING TO WMOS");
				//log.debug("INSIDE SEND2WMOS - REGULAR RELEASE - SENDING TO WMOS");
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO, sPickTicketNo);
				/**
    			 * Commented for Iteration 4 changes
    			 */
				/*if (strHdrGftReceiptId != null &&
						!strHdrGftReceiptId.trim().equals(""))
				{*/
				this.callChangeRelease(env, sOrderReleaseKey, sPickTicketNo);
				//}
				//log.debug("String HDR Receipt ID is "+ strExtnHdrGiftReceiptID);
				/**
    			 * Commented for Iteration 4 changes
    			 */
				/*if (strHdrGftReceiptId == null ||
						strHdrGftReceiptId.trim().equals(""))
				{
					this.callChangeRelease(env, sOrderReleaseKey, sPickTicketNo, strExtnHdrGiftReceiptID, sExtnPreEncodedHdrReceiptID );
				}*/
				//log.debug("input XML to changeOrderStatus  : " + XMLUtil.getXMLString(yfcDocChangeOrderStatus.getDocument()));
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Input XML to ChangeOrderStatus : " + XMLUtil.getXMLString(yfcDocChangeOrderStatus.getDocument()));
				}

				this.api.changeOrderStatus(env, yfcDocChangeOrderStatus.getDocument());

				// Order Broker start
				/* Commented by OASIS Support for PMR 54606,379,000 -- OASIS_SUPPORT 14/03/2012
				if(strIsHazardous.equalsIgnoreCase("Y") && strExIsMilitary.equalsIgnoreCase("Y")){
					strShipVia = KohlsConstant.SHIP_VIA_PP;
				} else if (strExIsMilitary.equalsIgnoreCase("Y")) {
					strShipVia = KohlsConstant.SHIP_VIA_PM;
				} else if (strExIsPOBox.equalsIgnoreCase("Y")) {
					strShipVia = KohlsConstant.SHIP_VIA_PMDC;
				}
				*/

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
				log.debug("setting the receipt id to the extn fields" +strReceiptId +strPreEnReceiptId );
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
				}*/

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
				sPackTypeList = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE);

				/* Commented for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
				if (!strExtnWrapTo.equalsIgnoreCase("")) {
					eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_GW);
				} else {
					eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_ST);
				}*/

				//Start --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
				if (!strExtnWrapCode.equalsIgnoreCase("")) {
					eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_GW);
				} else {
					eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_ST);
				}
				//End --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012

				//log.debug("XML to WMos : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Release Message to WMoS : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
				}
				//log.debug("Release Message to WMoS : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
			    this.api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_SEND_RELEASE_TO_WMOS, docWMoSOrderReleaseXML);
			    //log.debug("XML to Ecomm : " + XMLUtil.getXMLString(yfcDocOrderStatusToECommXML.getDocument()));
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Release Status Message To Ecommerce : " + XMLUtil.getXMLString(yfcDocOrderStatusToECommXML.getDocument()));
				}
				//log.debug("Release OrderStatus MSG::"+XMLUtil.getXMLString(yfcDocOrderStatusToECommXML.getDocument()));
				this.api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_SEND_RELEASE_STATUS_TO_ECOMM, yfcDocOrderStatusToECommXML.getDocument());

			}

		}
			
		
			KohlsUtil.registerProcessCompletion(env, sDataKey, sDataType, sTaskQKey);
		
		
		

		}catch(YFSException ex){
			ex.printStackTrace();
    		throw ex;
		}
		return null;
	}
	



	private boolean isVGC(String strLineType, String sProductLine) {
		return KohlsConstant.PRODUCT_LINE_VGC.equalsIgnoreCase(sProductLine) || KohlsConstant.PRODUCT_LINE_VGC.equalsIgnoreCase(strLineType);
	}

	/**
	 * Commented for Iteration 4 changes
	 */
	private String getRegTransValues(YFSEnvironment env) {

		String sRegTransValues="";
		try
		{
			sRegTransValues = StringUtil.prepadStringWithZeros(KohlsUtil.getNextRegTransNo(env, this.m_conn), KohlsConstant.REG_TRANS_LEN);
			//sTransactionNo = StringUtil.prepadStringWithZeros(KohlsUtil.getNextTransactionNo(env, this.m_conn), KohlsConstant.TRANSACTION_LEN);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return sRegTransValues;
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

	private void callChangeRelease(YFSEnvironment env, String sOrderReleaseKey, String sPickTicketNo) throws YFSException, RemoteException {

		//calling changeRelease
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<!--------------- Inside changeRelease ----------->");
		}

		YFCDocument yfcDocChangeRelease = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE);
		YFCElement yfcEleChangeRelease = yfcDocChangeRelease.getDocumentElement();
		yfcEleChangeRelease.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
		yfcEleChangeRelease.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
		yfcEleChangeRelease.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);

		YFCElement yfcEleChangeReleaseExtn = yfcDocChangeRelease.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleChangeReleaseExtn.setAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO, sPickTicketNo);
		yfcEleChangeRelease.appendChild(yfcEleChangeReleaseExtn);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input XML to changeRelease  : " + XMLUtil.getXMLString(yfcDocChangeRelease.getDocument()));
		}
		//log.debug("Input XML to changeRelease - REL A : " + XMLUtil.getXMLString(yfcDocChangeRelease.getDocument()));
		this.api.changeRelease(env, yfcDocChangeRelease.getDocument());

	}


	/**
	 * Commented for Iteration 4 changes
	 * @param shipNode 
	 * @param strPreEncodedLnGiftRcptID 
	 * @param strOrderLineNumber 
	 */

	private void callChangeReleaseForLnGftRcptID(YFSEnvironment env, String sOrderReleaseKey, String sLineRcptNo, String strPreEncodedLnGiftRcptID, String shipNode, String strOrderLineNumber) throws YFSException, RemoteException {

		//calling changeRelease
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<!--------------- Inside changeRelease for Line Receipt ID ----------->");
		}

		YFCDocument yfcDocChangeRelease = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE);
		YFCElement yfcEleChangeRelease = yfcDocChangeRelease.getDocumentElement();
		yfcEleChangeRelease.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
		yfcEleChangeRelease.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
		yfcEleChangeRelease.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);

		YFCElement yfcEleChangeReleaseNotes = yfcDocChangeRelease.createElement(KohlsXMLLiterals.E_NOTES);
		yfcEleChangeRelease.appendChild(yfcEleChangeReleaseNotes);
		YFCElement yfcEleChangeReleaseNote1 = yfcDocChangeRelease.createElement(KohlsXMLLiterals.E_NOTE);
		yfcEleChangeReleaseNote1.setAttribute(KohlsXMLLiterals.A_NOTE_KEY, sLineRcptNo);
		yfcEleChangeReleaseNote1.setAttribute(KohlsXMLLiterals.A_REASON_CODE, shipNode);
		yfcEleChangeReleaseNote1.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, sLineRcptNo);
		
		YFCElement yfcEleChangeReleaseNote1Extn = yfcDocChangeRelease.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleChangeReleaseNote1Extn.setAttribute("ExtnOrderLineNo", strOrderLineNumber);
		yfcEleChangeReleaseNote1.appendChild(yfcEleChangeReleaseNote1Extn);
		
		YFCElement yfcEleChangeReleaseNote2 = yfcDocChangeRelease.createElement(KohlsXMLLiterals.E_NOTE);
		yfcEleChangeReleaseNote2.setAttribute(KohlsXMLLiterals.A_NOTE_KEY, strPreEncodedLnGiftRcptID);
		yfcEleChangeReleaseNote2.setAttribute(KohlsXMLLiterals.A_REASON_CODE, KohlsConstant.PREENC+shipNode);
		yfcEleChangeReleaseNote2.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, strPreEncodedLnGiftRcptID);
		
		yfcEleChangeReleaseNotes.appendChild(yfcEleChangeReleaseNote1);
		yfcEleChangeReleaseNotes.appendChild(yfcEleChangeReleaseNote2);

		//log.debug("Input XML to changeRelease(Gift Receipt)  : " + XMLUtil.getXMLString(yfcDocChangeRelease.getDocument()));

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input XML to changeRelease (GiftReceipt)  : " + XMLUtil.getXMLString(yfcDocChangeRelease.getDocument()));
		}

		this.api.changeRelease(env, yfcDocChangeRelease.getDocument());

	}

	
	// Start - Added by Saravana as a part of 60-Lines fix
	
	/**
	 * Commented for Iteration 4 changes
	 * @param shipNode 
	 * @param strPreEncodedLnGiftRcptID 
	 * @param strOrderLineNumber 
	 */

	private void callChangeOrderForLnGftRcptID(YFSEnvironment env, String sOrderHeaderKey, String sOrderLineKey, String sLineRcptNo, String strPreEncodedLnGiftRcptID, String shipNode, String strOrderLineNumber) throws YFSException, RemoteException {

		//calling changeRelease
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<!--------------- Inside changeOrder for Line Receipt ID ----------->");
		}

		YFCDocument yfcDocChangeOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleChangeOrder = yfcDocChangeOrder.getDocumentElement();
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);

		YFCElement yfcEleChangeOrderLines = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		yfcEleChangeOrder.appendChild(yfcEleChangeOrderLines);
		YFCElement yfcEleChangeOrderLine = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINE);
		yfcEleChangeOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, sOrderLineKey);
		yfcEleChangeOrderLine.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
		yfcEleChangeOrderLines.appendChild(yfcEleChangeOrderLine);
		
		YFCElement yfcEleChangeOrderNotes = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_NOTES);
		yfcEleChangeOrderLine.appendChild(yfcEleChangeOrderNotes);
		YFCElement yfcEleChangeOrderNote1 = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_NOTE);
		yfcEleChangeOrderNote1.setAttribute(KohlsXMLLiterals.A_NOTE_KEY, sLineRcptNo);
		yfcEleChangeOrderNote1.setAttribute(KohlsXMLLiterals.A_REASON_CODE, shipNode);
		yfcEleChangeOrderNote1.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, sLineRcptNo);
		
		YFCElement yfcEleChangeOrderNote1Extn = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleChangeOrderNote1Extn.setAttribute("ExtnOrderLineNo", strOrderLineNumber);
		yfcEleChangeOrderNote1.appendChild(yfcEleChangeOrderNote1Extn);
		
		YFCElement yfcEleChangeOrderNote2 = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_NOTE);
		yfcEleChangeOrderNote2.setAttribute(KohlsXMLLiterals.A_NOTE_KEY, strPreEncodedLnGiftRcptID);
		yfcEleChangeOrderNote2.setAttribute(KohlsXMLLiterals.A_REASON_CODE, KohlsConstant.PREENC+shipNode);
		yfcEleChangeOrderNote2.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, strPreEncodedLnGiftRcptID);
		
		yfcEleChangeOrderNotes.appendChild(yfcEleChangeOrderNote1);
		yfcEleChangeOrderNotes.appendChild(yfcEleChangeOrderNote2);

		//log.debug("Input XML to changeOrder(Gift Receipt)  : " + XMLUtil.getXMLString(yfcDocChangeOrder.getDocument()));

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input XML to changeOrder (GiftReceipt)  : " + XMLUtil.getXMLString(yfcDocChangeOrder.getDocument()));
		}

		this.api.changeOrder(env, yfcDocChangeOrder.getDocument());

	}
	
	// End - Added by Saravana as a part of 60-Lines fix

	
	
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

		//log.debug("getItemListTemplate Output XML: "+ XMLUtil.getXMLString(docGetItemList));
		Element elePrimaryInfo = (Element) docGetItemList.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
		Element elePersonInfoShipTo = (Element) XPathUtil.getNode((Node) eleGetOrderReleaseDetails, KohlsXMLLiterals.XP_ORDERRELEASE_PERSON_INFO);
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

		Element elePersonInfoShipTo = (Element) XPathUtil.getNode((Node) eleGetOrderReleaseDetails, KohlsXMLLiterals.XP_ORDERRELEASE_PERSON_INFO);

		Element elePersonInfoShipToExtn = (Element)elePersonInfoShipTo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		String strIsMilitary = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY);
		String strIsPOBox = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX);
		String sCarrierServiceCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
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
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, "");
		//yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute("PacklistType", "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_RELEASE_NO, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SCAC, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SHIP_TO_KEY, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_MIN_ORDER_RELEASE_STATUS, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD, "");

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
		//added by Baijayanta
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD, "");
		yfcEleOrderLineTemp.setAttribute("FulfillmentType", "");
		//ended by Baijayanta
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_GIFT_FLAG, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_GIFT_WRAP, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
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
		//added by Baijayanta
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXPIRATION_DATE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_EXPECTED_SHIPMENT_DATE,"");
				//ended by Baijayanta
		/** Iteration 4 changes */
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE, "");
		yfcEleOrderLineTemp.appendChild(yfcEleOrderLineExtn);

		YFCElement yfcEleItemTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_ITEM);
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_LINE, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UPC_CODE, "");
		yfcEleOrderLineTemp.appendChild(yfcEleItemTemp);

		return yfcDocGetOrderReleaseDetailsTemp.getDocument();

	}
	//Added by OASIS- adding argument sNodeType to the method
	public void setShipmentType(YFCElement yfcEleCreateShipment, String sCarrierServiceCode, String strGiftwrap,
			Element eleGetOrderReleaseDetails,String sNodeType, boolean bProductLineSA, YFSEnvironment env)	throws Exception {
		
		
		Element eleOrderreleaseLines = (Element)XPathUtil.getNode((Node) eleGetOrderReleaseDetails, KohlsXMLLiterals.XP_ORDERRELEASE_ORDERLINES);
		Element eleOrderreleaseLine = (Element)eleOrderreleaseLines.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE).item(0);
		boolean isProductFamilyItem = false;
		int intNoOfLines = eleOrderreleaseLines.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE).getLength();
		//String giftflag = eleOrderreleaseLine.getAttribute(KohlsXMLLiterals.A_GIFT_FLAG);
		String quantity=KohlsConstant.ATTR_SINGLE;
		String type=KohlsConstant.ATTR_REGULAR;
		if(sCarrierServiceCode.contains(KohlsConstant.CARRIER_SERVICE_CODE_PRIORITY)){
			type = KohlsConstant.CARRIER_SERVICE_CODE_PRIORITY;
		}else if(KohlsConstant.YES.equals(strGiftwrap)){
			type = KohlsConstant.ATTR_GIFT;
		}else {
			type = KohlsConstant.ATTR_REGULAR;
		}
		//Start --- Added for Store Issues with ShipAlones 3/13/2013
		//SINGLE_REGULAR - For shipalone sim items that are not gifts or priority shipments
		//SINGLE_GIFT - For shipalone sim items that are gifts
		//SINGLE_PRIORITY - For shipalone sim items that are priority shipments
		boolean isnodeRDCOrStore = KohlsConstant.ATTR_RDC.equals(sNodeType) ||
		KohlsConstant.ATTR_STORE.equals(sNodeType) ||
			KohlsConstant.ATTR_RDC_STORE.equals(sNodeType);
		if(bProductLineSA && isnodeRDCOrStore ){
			log.debug("It is Ship Alone for SIM");
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_TYPE, quantity + "_" + type);
			log.debug("Create Shipment Element for non ProductFamily shipments  " +yfcEleCreateShipment.toString());
		}
		
		//End --- Added for Store Issues with ShipAlones 3/13/2013
		if(!bProductLineSA){
		if(intNoOfLines > 1){
			quantity = KohlsConstant.ATTR_MULTI;
		} else {
			String lineQty = eleOrderreleaseLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QUANTITY);
			//int intLineQty = Integer.parseInt(lineQty);
			Double intLineQty = Double.parseDouble(lineQty);
			if(intLineQty>1){
				quantity = KohlsConstant.ATTR_MULTI;
			}
		}
		
		if((quantity.equalsIgnoreCase(KohlsConstant.ATTR_MULTI)) && (KohlsConstant.ATTR_RDC.equals(sNodeType)))
		{
			log.debug("Quantity is for Multi...Checking For ProductFamily..Start");
			NodeList ItemsList = eleOrderreleaseLines.getElementsByTagName(KohlsConstant.E_ITEM);
			log.debug("No. Of Item Elements fetched from OrderReleaseLines " + ItemsList.getLength());
			
			
			
			if(ItemsList.getLength() > 0)
			{
				
					String strShipNode_v=eleGetOrderReleaseDetails.getAttribute(KohlsConstant.A_SHIP_NODE);
					
					//creating input xml for getItemNodeDefnList API
					YFCDocument docGetItemNodeDefnList = YFCDocument.createDocument(KohlsConstant.E_ITEM_NODE_DEFN);
					YFCElement eleGetItemNodeDefn = docGetItemNodeDefnList.getDocumentElement();
					eleGetItemNodeDefn.setAttribute(KohlsConstant.A_NODE, strShipNode_v);
					String strCatalogOrgCode = KohlsConstant.A_CATALOG_ORG;
					//props.getProperty(KohlsConstant.A_CATALOG_ORG_CODE);
					//TODO
					eleGetItemNodeDefn.setAttribute(KohlsConstant.A_ORGANIZATION_CODE, strCatalogOrgCode);
				
				YFCElement eleComplexQry = ((YFCElement) eleGetItemNodeDefn).createChild("ComplexQuery");
				YFCElement eleOrQry = eleComplexQry.createChild("Or");
				for(int i=0; i<ItemsList.getLength(); i++){
					Element EleItem = (Element) ItemsList.item(i);
					YFCElement expEle = eleOrQry.createChild("Exp");
					expEle.setAttribute("Name", KohlsConstant.A_ITEM_ID);
					expEle.setAttribute("Value", EleItem.getAttribute(KohlsConstant.A_ITEM_ID));

				}
				
				log.debug("Input Going to getItemNodeDefnList API is  " + XMLUtil.getXMLString(docGetItemNodeDefnList.getDocument()));
				YFCDocument yfcDocgetItemNodeDefnListTemp = YFCDocument.createDocument(KohlsConstant.E_ITEM_NODE_DEFN_LIST);
				YFCElement yfcEleGetItemNodeDefnListTemp = yfcDocgetItemNodeDefnListTemp.getDocumentElement();
				YFCElement yfcEleItemTemp = yfcDocgetItemNodeDefnListTemp.createElement(KohlsConstant.E_ITEM_NODE_DEFN);
				yfcEleItemTemp.setAttribute(KohlsConstant.A_ITEM_ID, "");
				YFCElement yfcEleItemExtnTemp = yfcDocgetItemNodeDefnListTemp.createElement(KohlsConstant.E_EXTN);
				yfcEleItemExtnTemp.setAttribute(KohlsConstant.A_EXTN_PRODUCT_FAMILY, "");
				yfcEleItemTemp.appendChild(yfcEleItemExtnTemp);
				yfcEleGetItemNodeDefnListTemp.appendChild(yfcEleItemTemp);
				log.debug("Template for  GetItemList API is  " + XMLUtil.getXMLString(yfcDocgetItemNodeDefnListTemp.getDocument()));
				env.setApiTemplate(KohlsConstant.API_GET_ITEM_NODE_DEFN_LIST, yfcDocgetItemNodeDefnListTemp.getDocument());
				
				Document docGetItemNodeDefnListResult = KOHLSBaseApi.invokeAPI(env, KohlsConstant.API_GET_ITEM_NODE_DEFN_LIST, docGetItemNodeDefnList.getDocument());
				env.clearApiTemplate(KohlsConstant.API_GET_ITEM_NODE_DEFN_LIST);
				log.debug("Output for  getItemNodeDefnList API is  " + XMLUtil.getXMLString(docGetItemNodeDefnListResult));
				
				
				
				
				if(!YFCObject.isVoid(docGetItemNodeDefnListResult)){
					
					
				Element eleGetItemNodeDefnListResult =docGetItemNodeDefnListResult.getDocumentElement();
				NodeList outItemList = eleGetItemNodeDefnListResult.getElementsByTagName(KohlsConstant.E_ITEM_NODE_DEFN);
				Element EleFirstItemElem = (Element) outItemList.item(0);
				if(!YFCObject.isNull(EleFirstItemElem)){
				Element EleFirstItemExtnElem = (Element) EleFirstItemElem.getElementsByTagName(KohlsConstant.E_EXTN).item(0);
				String strFirstProductFamily = EleFirstItemExtnElem.getAttribute(KohlsConstant.A_EXTN_PRODUCT_FAMILY);
				log.debug("Product Family  for  getItemNodeDefnList API is  " + XMLUtil.getXMLString(docGetItemNodeDefnListResult));
				if(!YFCObject.isVoid(strFirstProductFamily)){
					log.debug("Got this ProductFamily from first Item  " + strFirstProductFamily);
					boolean tempIsProductFamilyItem = true;
					String strExtnProductFamily = strFirstProductFamily;
					for (int j=0 ; j <outItemList.getLength();j++)
					{
						Element EleItem2 = (Element) outItemList.item(j);
						Element ItemExtntempElem = (Element) EleItem2.getElementsByTagName(KohlsConstant.E_EXTN).item(0);
						String strProductFamilyTemp = ItemExtntempElem.getAttribute(KohlsConstant.A_EXTN_PRODUCT_FAMILY);
					
						if(YFCObject.isVoid(strProductFamilyTemp))
						{
							log.debug("Found null as ProductFamily one of the Item.");
							tempIsProductFamilyItem= false;
							j = outItemList.getLength();
						}else if(!strProductFamilyTemp.equalsIgnoreCase(strFirstProductFamily))
						{
							log.debug("Found different  ProductFamily one of the Item from the First Item in the list");
							tempIsProductFamilyItem= false;
							j = outItemList.getLength();
						}

					}
					log.debug("Setting if is ProductFamily Item or not " + tempIsProductFamilyItem);
					isProductFamilyItem = tempIsProductFamilyItem;


					if(isProductFamilyItem)
					{				
						log.debug("It is a ProductFamily Item and ProductFamily is " + strFirstProductFamily);
						yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_TYPE,KohlsConstant.V_MULTI_PRODUCT_FAMILY);
						YFCElement ShipmentExtnElem= yfcEleCreateShipment.getChildElement("Extn", true);
						ShipmentExtnElem.setAttribute(KohlsConstant.A_EXTN_SHIPMENT_FAMILY,strFirstProductFamily);
						log.debug("Create Shipment Element after setting Multi_Product_Family shipment Type and ExtnShipmentFamily  " +yfcEleCreateShipment.toString());
					}
				}
			}
		  }
		}

	
		}
		
		//for quantity, look at the release document, if more than 1 order line, them nulti, or if only 1 order line, look at quantity
		if(!isProductFamilyItem)
		{
			log.debug("It is not a ProductFamily Item");
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_TYPE, quantity + "_" + type);
			log.debug("Create Shipment Element for non ProductFamily shipments  " +yfcEleCreateShipment.toString());
		}

		
	}
		
 }

  public Document getDependentReleases(YFSEnvironment env, String shipNode,String orderHeaderKey , String sellerOrgCode,String orderNo) throws Exception{
	  
	  String aDeliveryMethod = "";
	  String apackListType ="";
	  String afulfillmentType ="";
	  String astrLneTp = "";
	  
	  Document docdocOutputGetOrderReleaseDetails = (Document) releaseDetailsList.get(0);
	  Element edocdocOutputGetOrderReleaseDetails = docdocOutputGetOrderReleaseDetails.getDocumentElement();
	  String initialReleaseKey = edocdocOutputGetOrderReleaseDetails.getAttribute("OrderReleaseKey");
	  NodeList ndlstOrderLines = docdocOutputGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
	  
	  for (int i = 0; i < ndlstOrderLines.getLength(); i++) {
		  
		  Element eleOrderLine = (Element) ndlstOrderLines.item(i);
		  log.debug("Orderline element is " +XMLUtil.getElementXMLString(eleOrderLine));
		  aDeliveryMethod = eleOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
    	  apackListType = eleOrderLine.getAttribute("PackListType");
    	 // afulfillmentType =  eleOrderLine.getAttribute("FulfillmentType");
    	  //astrLneTp = eleOrderLine.getAttribute(KohlsXMLLiterals.A_LINE_TYPE);
	  }
	  
	  if("PICK".equalsIgnoreCase(aDeliveryMethod)) {
			
			Document docOrderRelease = SCXmlUtil.createDocument("OrderRelease");
			Element eleOrderRelease = docOrderRelease.getDocumentElement();
			eleOrderRelease.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderHeaderKey);
			eleOrderRelease.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, sellerOrgCode);
			eleOrderRelease.setAttribute("ShipNode",shipNode);
			env.setApiTemplate("getOrderReleaseList", this.getOrderReleaseListTemplate());
			Document getReleaseListDoc = this.api.getOrderReleaseList(env,docOrderRelease);
			env.clearApiTemplate("getOrderReleaseList");
			Element elegetReleaseListDoc = getReleaseListDoc.getDocumentElement();
			NodeList shipNodeList = elegetReleaseListDoc.getElementsByTagName("OrderRelease");
			for(int m=0;m < shipNodeList.getLength(); m++) {
				Element eleRelease = (Element) shipNodeList.item(m);
				String apackLitsType = eleRelease.getAttribute("PacklistType");
				String aDelivryMethod = eleRelease.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
				if(apackListType.equalsIgnoreCase(apackLitsType) && aDeliveryMethod.equalsIgnoreCase(aDelivryMethod)) {
					dependentreleaseList.add(eleRelease);
				}
				 
			}
			
			
			
	  }
	  
	  Document docShipment = getDepenedentReleaseDeatils(env,orderHeaderKey,orderNo,initialReleaseKey);
	  return docShipment;
  
  }
  
  public Document getDepenedentReleaseDeatils(YFSEnvironment env,String OrdHdrKey,String OrdNo,String initialReleaseKey) throws Exception {
	  Document ShipmentDoc = null;
	  ArrayList releaseKey = new ArrayList();
	  
	  for(int a= 0;a<dependentreleaseList.size(); a++){
		  Element eRelease = (Element) dependentreleaseList.get(a);
		  String aReleaseKey = eRelease.getAttribute("OrderReleaseKey");
		  if(!initialReleaseKey.equalsIgnoreCase(aReleaseKey))
			releaseKey.add(aReleaseKey);
		 
	  }
	  ShipmentDoc = getShipmentConsolidationForBopusReleases(env,releaseKey);
	  return ShipmentDoc;
 
  }
  
  public Document getShipmentConsolidationForBopusReleases(YFSEnvironment env,ArrayList releaseKeyList) throws Exception{
	  
	  	String sDeliveryMethod = "";
		String strProdLine = "";
		String strReceiptId = "";
		String strPreEnReceiptId = "";
		String prevFilterType = null;
		
		Document yfcBopusDocCreateShipment = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element eleCreateShipment = yfcBopusDocCreateShipment.getDocumentElement();
		Element eleShipmentExtn = (Element) SCXmlUtil.createChild(eleCreateShipment, KohlsXMLLiterals.E_EXTN);
		Element eleShipmentLines = (Element) SCXmlUtil.createChild(eleCreateShipment, KohlsXMLLiterals.E_SHIPMENT_LINES);
		// Added for ShipmentExpired CR - RAVI
		Element eleAdditionalDates = (Element) SCXmlUtil.createChild(eleCreateShipment, KohlsXMLLiterals.E_ADDITIONAL_DATES);
		Element eleAdditionalDate = (Element) SCXmlUtil.createChild(eleAdditionalDates, KohlsXMLLiterals.E_ADDITIONAL_DATE);
		
		log.debug("the list size is:"+releaseKeyList.size());
	  
		for(int d=0 ; d< releaseKeyList.size();d++) {
		
			YFCDocument yfcDocGetOrderReleaseDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE_DETAIL);
			YFCElement yfcEleGetOrderDetails = yfcDocGetOrderReleaseDetails.getDocumentElement();
			String aReleaseKey = (String)releaseKeyList.get(d);
			yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, aReleaseKey);
			log.debug("Input XML to getOrderReleaseDetails : " + XMLUtil.getXMLString(yfcDocGetOrderReleaseDetails.getDocument()));
			env.setApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_DETAILS, this.getOrderReleaseDetailsTemplate());
			//log.debug("getOrderReleaseDetails Template : " + XMLUtil.getXMLString(this.getOrderReleaseDetailsTemplate()));
			Document docOutputGetOrderReleaseDetails = this.api.getOrderReleaseDetails(env, yfcDocGetOrderReleaseDetails.getDocument());
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_DETAILS);
			log.debug("getOrderDetails Output XML : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));
			releaseDetailsList.add(docOutputGetOrderReleaseDetails);
			
		}
			
		    log.debug("the list size is:"+releaseDetailsList.size());
		   
		    for(int s=0;s< releaseDetailsList.size();s++) {
		    	
				     Document OutputGetOrderReleaseDetails = (Document)releaseDetailsList.get(s);
				     Element eleGetOrderReleaseDetails = OutputGetOrderReleaseDetails.getDocumentElement();
					
					/* END */
				     Element eleGetOrderDetail = (Element) eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
				     String sOrderReleaseKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY);
				     String sOrdHdrKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
				     String sOrderNo = eleGetOrderDetail.getAttribute(KohlsXMLLiterals.A_ORDERNO);
				     String sShipToKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SHIP_TO_KEY);
				     String sEnterpriseCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);
				     String sShipNode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
				     String sSellerOrgCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE);
				     String sCarrierServiceCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
						//get the node type
						String sNodeType="";
						sNodeType = KohlsUtil.getNodeType(sShipNode, env);
						boolean isnodeRDCOrStore = KohlsConstant.ATTR_RDC.equals(sNodeType) ||
								KohlsConstant.ATTR_STORE.equals(sNodeType) ||
									KohlsConstant.ATTR_RDC_STORE.equals(sNodeType);
						boolean isnodeRDC = KohlsConstant.ATTR_RDC.equals(sNodeType) ||
									KohlsConstant.ATTR_RDC_STORE.equals(sNodeType);
						
						//Check for BOPUS lines
						sDeliveryMethod = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
						String sPickShipNode = "";
				
						// get and stamp the receipt id
						Element eleOrNotes = (Element) eleGetOrderDetail.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
						String strNumOfNotes = eleOrNotes.getAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES);
						int inNumOfNotes = Integer.parseInt(strNumOfNotes);
						if(inNumOfNotes>0){
							
							strReceiptId = getReceiptId(env,sShipNode,isnodeRDC,eleOrNotes,sDeliveryMethod);
							log.debug("Notes have been obtained hence the logic to loop through notes and find the receipt id begins");

						}
				
						
			
					NodeList ndlstOrderLines = OutputGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
					Element eleOrderLine = null;
					Element eleItem = null;
					String sProductLine = "";
					double dOrderedQty=0.00;
			
						for (int i = 0; i < ndlstOrderLines.getLength(); i++) {
							log.debug("INSIDE SEND2WMOS - LOOPING THROUGH LINES OF THE RELEASE");
				
							eleOrderLine = (Element) ndlstOrderLines.item(i);
							log.debug("Orderline element is " +XMLUtil.getElementXMLString(eleOrderLine));
							log.debug(SCXmlUtil.getString(eleOrderLine));
							//added by Baijayanta
							sDeliveryMethod = eleOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
							//ended by Baijayanta
				
							String sCarrierServiceCodeLine = eleOrderLine.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
							// End --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
							String sOrderLineKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
							String sOrderHeaderKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
							String sQty = eleOrderLine.getAttribute(KohlsXMLLiterals.A_STATUS_QUANTITY);
							dOrderedQty = Double.parseDouble(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QUANTITY));
							//added by vinay
							String packListType = eleOrderLine.getAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE);
							//Start --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
							if (sCarrierServiceCodeLine == null || sCarrierServiceCodeLine.trim().equals("")) {
								if(sCarrierServiceCode != null && !sCarrierServiceCode.trim().equals("")) {
									sCarrierServiceCodeLine = sCarrierServiceCode;
								}
							}
				
							//Obtain the Line Gift Receipt ID and check if the value is null.
							Element eleOrderLineExtn = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
				          
							//added by Baijayanta
							String sExpirationDate = eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXPIRATION_DATE);//Required
							String sExtnExpectedShipmentDate = eleOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_EXPECTED_SHIPMENT_DATE);//Required
							
							//ended by Baijayanta
				
							eleItem = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
							String sItemId = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
							String sUOM = eleItem.getAttribute(KohlsXMLLiterals.A_UOM);
							sProductLine = eleItem.getAttribute(KohlsXMLLiterals.A_PRODUCT_LINE);
										//	log.debug("value of product line is" +sProductLine);
							if(sProductLine.equalsIgnoreCase(KohlsConstant.PRODUCT_LINE_BK)){
								strProdLine = KohlsConstant.PRODUCT_LINE_BK;
							}
				
				
									eleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXPIRATION_DATE, sExpirationDate);
									eleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_EXPECTED_SHIPMENT_DATE, sExtnExpectedShipmentDate);
									eleShipmentExtn.setAttribute("ExtnStorePreencReceiptID", strReceiptId);
									
									// Added for ShipmentExpired CR - RAVI
									eleAdditionalDate.setAttribute(KohlsXMLLiterals.A_DATE_TYPE_ID, KohlsXMLLiterals.A_EXPIRATION_DATE);
									eleAdditionalDate.setAttribute(KohlsXMLLiterals.A_ACTUAL_DATE, sExpirationDate);
									
									//added by vinay
									if(null != packListType && packListType.contains(KohlsConstant._JEW)) {
										eleShipmentExtn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_INDICATOR, KohlsConstant.JEW);
										
									}
				
									// Start --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
									eleCreateShipment.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, sCarrierServiceCodeLine);
									// End --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
									eleCreateShipment.setAttribute(KohlsXMLLiterals.A_TO_ADDRESS_KEY, sShipToKey);
									eleCreateShipment.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, sEnterpriseCode);
				
									eleCreateShipment.setAttribute(KohlsXMLLiterals.A_ORDERNO, sOrderNo);
							
									eleCreateShipment.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY,sOrderHeaderKey);
									//End --- Added for Store Issues with ShipAlones 3/13/2013
									eleCreateShipment.setAttribute(KohlsXMLLiterals.A_SHIPNODE, sShipNode);
									eleCreateShipment.setAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD, sDeliveryMethod);
									log.debug("delivery method:"+sDeliveryMethod);
									
									eleCreateShipment.setAttribute(KohlsXMLLiterals.A_EXPECTED_DELIVERY_DATE, "");
									
									//Defect - 172 Changing Expected Ship Date to Extended Expected Shipment Date - Bala
									eleCreateShipment.setAttribute(KohlsXMLLiterals.A_EXPECTED_SHIPMENT_DATE, sExtnExpectedShipmentDate);
									
									eleCreateShipment.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, sSellerOrgCode);
				
									eleCreateShipment.appendChild(eleShipmentExtn);
									
									eleCreateShipment.appendChild(eleShipmentExtn);
				
									Double iQty = Double.parseDouble(sQty);
									
									Element eleShipmentLine = null;
				
									eleShipmentLine = SCXmlUtil.createChild(eleShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);
									eleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
									eleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, sOrderLineKey);
									eleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
									eleShipmentLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, sItemId);
									eleShipmentLine.setAttribute(KohlsXMLLiterals.A_UOM, sUOM);
									eleShipmentLine.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, KohlsConstant.PRODUCT_CLASS_GOOD);
									eleShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, String.valueOf(iQty));
									
									KohlsOrderPickProcessUtil pickProcessUtil = new KohlsOrderPickProcessUtil();
									
									//String filterVal = "SalesFloor";
									String filterVal = pickProcessUtil.givItemFilterObtain(env,sItemId,sShipNode);
									
									if (YFCCommon.isVoid(prevFilterType)){
										prevFilterType = filterVal;
									}
									else{
										if (!filterVal.equals(prevFilterType)){
											prevFilterType=KohlsConstant.MIXED;
										}
									}
									
									eleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_FILTER_VAL, prevFilterType);
									//eleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_FILTER_VAL, "");
									log.debug("the input doc for createShipment:"+XMLUtil.getXMLString(yfcBopusDocCreateShipment));
							
						}
						log.debug("INSIDE SEND2WMOS - COMPLETED LOOPING THROUGH LINES OF THE RELEASE");
						
						if (sDeliveryMethod.equalsIgnoreCase(KohlsConstant.PICK) && !YFCCommon.isVoid(yfcBopusDocCreateShipment)) {
							
							//Order modification hold check - Ravi
							
							KohlsBOPUSHoldCheck holdCheckObj = new KohlsBOPUSHoldCheck();
							Document docOrder = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
							Element eleOrder = docOrder.getDocumentElement();
							eleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrdHdrKey);
							eleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, sOrderNo);
							boolean eComHoldExists = holdCheckObj.orderHoldCheck(env, docOrder);
							
							if (!eComHoldExists) {
							
								Element yfcEleShipmentExtn = yfcBopusDocCreateShipment.getDocumentElement();
								yfcEleShipmentExtn.setAttribute(KohlsXMLLiterals.A_EXTN_FILTER_VAL, prevFilterType);
								
							}
							
						}
					
					
					}
					log.debug("the shipdoc is:"+SCXmlUtil.getString(yfcBopusDocCreateShipment));
				    return yfcBopusDocCreateShipment;
		
		    }
  
  	private Document getOrderReleaseListTemplate() {

		YFCDocument yfcDocGetOrderReleaseListTemp = YFCDocument.createDocument("OrderReleaseList");
		YFCElement yfcEleGetOrderReleaseListTemp = yfcDocGetOrderReleaseListTemp.getDocumentElement();
		
		YFCElement yfcEleOrderReleaseTemp = yfcDocGetOrderReleaseListTemp.createElement("OrderRelease");

		yfcEleOrderReleaseTemp.setAttribute("OrderReleaseKey", "");
		yfcEleOrderReleaseTemp.setAttribute("OrderHeaderKey", "");
		yfcEleOrderReleaseTemp.setAttribute("ShipNode", "");
		yfcEleOrderReleaseTemp.setAttribute("PacklistType", "");
		yfcEleOrderReleaseTemp.setAttribute("DeliveryMethod", "");
		yfcEleGetOrderReleaseListTemp.appendChild(yfcEleOrderReleaseTemp);
		
		return yfcDocGetOrderReleaseListTemp.getDocument();
  	}
  	
  	public String getReceiptId(YFSEnvironment env,String sShipNode,boolean isNodeRDC,Element eleOrNotes,String sDeliveryMethod) throws YFSException, RemoteException, YIFClientCreationException{
  		
  		String strReceiptId = "";
  		log.debug("Notes have been obtained hence the logic to loop through notes and find the receipt id begins");
		Map mpNote = new HashMap<String, String>();
		NodeList ndOrNote = eleOrNotes.getElementsByTagName(KohlsXMLLiterals.E_NOTE);
		for(int j=0;j<ndOrNote.getLength();j++){
			Element eleOrNote = (Element) ndOrNote.item(j);
			String strNoteReasCode = eleOrNote.getAttribute(KohlsXMLLiterals.A_REASON_CODE);
			String strNoteReasText = eleOrNote.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);//Receipt
			mpNote.put(strNoteReasCode, strNoteReasText);
		}
		//Added  by Zubair Bug 4458 - Begin
		if(isNodeRDC){
			String sMapShipNode = KohlsUtil.getEFCForStore(sShipNode, env);
			
			if(KohlsConstant.PICK.equalsIgnoreCase(sDeliveryMethod)){  //For BOPUS Order
				strReceiptId = (String) mpNote.get(KohlsConstant.BPS+"_"+sMapShipNode);
				log.debug("Receipt id obtained is"+strReceiptId + " Using shipnode: " + KohlsConstant.BPS+"_"+sMapShipNode);
				/*strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+KohlsConstant.BPS+"_"+sMapShipNode);
				log.debug("strPreEnReceiptId  obtained is" +KohlsConstant.PREENC+KohlsConstant.BPS+"_"+strPreEnReceiptId);*/
			}
			
			
		} else {
			if(KohlsConstant.PICK.equalsIgnoreCase(sDeliveryMethod)){  //For BOPUS Order
				strReceiptId = (String) mpNote.get(KohlsConstant.BPS+"_"+sShipNode);
				log.debug("Receipt id obtained is"+strReceiptId + " Using shipnode: " + KohlsConstant.BPS+"_"+sShipNode);
				/*strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+KohlsConstant.BPS+"_"+sShipNode);
				log.debug("strPreEnReceiptId  obtained is" +strPreEnReceiptId);*/
			}
			//Added  by Zubair Bug 4458 - Begin
		}
  		
  		return strReceiptId;
  		
  	}

}
