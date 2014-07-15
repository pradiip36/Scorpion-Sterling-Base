package com.kohls.oms.agent;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.StringUtil;
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

/**
 * This Task Q based agent is called in the Transfer Order Pipeline This 
 * class have logic to generate the pickticket # and It has logic where it
 * checks If the receiving node is exists in the list of nodes retrieved by the
 * common code then the first digit of the PickTicket # will be replaced with
 * "X"else the first digit of the PickTicket # will be replaced with "C" logic
 * to Call changeOrderRelease to stamp the PickTicket # in ExtnPickTicketNo it
 * also Invoke the service KohlsSendReleaseOrderToWMoSSyncService to send the
 * release information to WMoS.
 * 
 * @author OASIS
 * Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 
 * Added for Inventory transfer Management .
 * 
 */

public class KohlsSendTOReleaseToWMoSAgent extends YCPBaseTaskAgent {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsSendTOReleaseToWMoSAgent.class.getName());
	private YIFApi api;
	private Connection m_conn;


	public KohlsSendTOReleaseToWMoSAgent() throws YIFClientCreationException {

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
		String strGiftwrap = "";
		String strExtnRG = "";
		String strProdLine = "";
		int strAccQty = 0;
		String strExtnWrapCode = "";
		boolean isSingleLine = false;
		String strReceiptId = "";
		String strPreEnReceiptId = "";
		
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
				//System.out.println(("Input XML to getOrderReleaseDetails : " + XMLUtil.getXMLString(yfcDocGetOrderReleaseDetails.getDocument())));
			//}
			env.setApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_DETAILS, KohlsSendTOReleaseToWMoSAgentXmls.getOrderReleaseDetailsTemplate());
			//System.out.println("getOrderReleaseDetails Template : " + XMLUtil.getXMLString(this.getOrderReleaseDetailsTemplate()));
			Document docOutputGetOrderReleaseDetails = this.api.getOrderReleaseDetails(env, yfcDocGetOrderReleaseDetails.getDocument());
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_DETAILS);
			//System.out.println("getOrderRelease Details : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));
			//if (YFCLogUtil.isDebugEnabled()) {
				log.debug("getOrderDetails Output XML : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));
			//}

			//System.out.println("getOrderReleaseDetailsTemplate Output XML : " + XMLUtil.getXMLString(docOutputGetOrderReleaseDetails));

			Element eleGetOrderReleaseDetails = docOutputGetOrderReleaseDetails.getDocumentElement();
			String sMinOrderReleaseStatus = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_MIN_ORDER_RELEASE_STATUS);
			/*Added to Remove the Redundant Task getting created when SA is getting canceled, the cancellation would move the Line to release Status
			generating a task for SENT_TO_WMOS agent */
			log.debug("INSIDE SEND2WMOS - sMinOrderReleaseStatus = " + sMinOrderReleaseStatus);
			//System.out.println("INSIDE SEND2WMOS - sMinOrderReleaseStatus = " + sMinOrderReleaseStatus);
			if(!sMinOrderReleaseStatus.equalsIgnoreCase(KohlsConstant.STATUS_RELEASED)){
				log.debug("INSIDE SEND2WMOS - sMinOrderReleaseStatus IS NOT VALID FOR THIS TRANSACTION" + sMinOrderReleaseStatus);
				//System.out.println("INSIDE SEND2WMOS - sMinOrderReleaseStatus IS NOT VALID FOR THIS TRANSACTION" + sMinOrderReleaseStatus);
				KohlsUtil.registerProcessCompletion(env, sDataKey, sDataType, sTaskQKey);
				return null;

			}
			/* END */
			Element eleGetOrderDetail = (Element) eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
				
			String sBillToID = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_BILL_TO_ID);
			String sBuyerOrganizationCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_BUYER_ORGANIZATION_CODE);
			String sCarrierServiceCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
			String sDeliveryMethod = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
			String sDivision = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_DIVISION);
			String sDocumentType = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE);
			String sEnterpriseCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);
			String sOrdHdrKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
			String sOrderName = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_NAME);
			String sOrderReleaseKey = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY);
			String sReceivingNode =  eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_RECEIVING_NODE);
			String sReleaseNo = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_RELEASE_NO);
			String sShipAdviceNo = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO);
			String sSellerOrgCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE);
			String sSalesOrderNo = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SALES_ORDER_NO);
			String sShipNode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
			//String sOrderNo = eleGetOrderDetail.getAttribute(KohlsXMLLiterals.A_ORDERNO);
			
			//get the node type
			String sNodeType="";
			
			// change Inventory transfer maintenance changes 
			sNodeType = KohlsUtil.getNodeType(sShipNode, env);
						
			// Release Message to WMoS
			Document docWMoSOrderReleaseXML = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE).getDocument();
			Element eleWMoSOrderReleaseXML = KohlsSendTOReleaseToWMoSAgentXmls.prepareReleaseXml( sBillToID,  sBuyerOrganizationCode,
					 sCarrierServiceCode,  sDeliveryMethod,  sDivision,sDocumentType, sEnterpriseCode ,sOrdHdrKey,
					 sOrderName,sOrderReleaseKey,sReceivingNode,sReleaseNo,sSalesOrderNo,sSellerOrgCode,
					sShipAdviceNo, sShipNode, docWMoSOrderReleaseXML);

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
			
			String strHdrGftReceiptId = eleExtnOrderDtl.getAttribute(KohlsXMLLiterals.A_EXTN_HDR_GFT_RECP_ID);
			log.debug("getting the strHdrGftReceiptid from the OrderDetails Extn element ExtnHdrGiftReceiptID"+strHdrGftReceiptId);
			String strExtnHdrGiftReceiptID = strHdrGftReceiptId;
			log.debug("setting strExtnHdrGiftReceiptID to strHdrGftReceiptId"+strExtnHdrGiftReceiptID);
			
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

			log.debug("docWMoSOrderReleaseXML :(Inventory transfer maintenance changes) " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
			log.debug("Output XML  : " + XMLUtil.getXMLString(eleGetOrderReleaseDetails.getOwnerDocument()));



			//Document Creation for changeOrder XML
			YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
			YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
			yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_SEND_TO_RELEASE_TO_WMOS);
			yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrdHdrKey);

			YFCElement yfcEleOrderStatusOrderLines = yfcDocChangeOrderStatus.createElement(KohlsXMLLiterals.E_ORDER_LINES);
			yfcEleChangeOrderStatus.appendChild(yfcEleOrderStatusOrderLines);

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
			boolean isLineRcptGen = false;
			//Set sExtnGiftWrapTogetherCode = new HashSet();
			//double dOrderedQty=0.00;


			int iCount = 0;
			//Order Broker start
			if(ndlstOrderLines.getLength()==1){
				isSingleLine = true;
			}
			
			for (int i = 0; i < ndlstOrderLines.getLength(); i++) {
				//log.debug("INSIDE SEND2WMOS - LOOPING THROUGH LINES OF THE RELEASE");
				log.debug("INSIDE SEND2WMOS - LOOPING THROUGH LINES OF THE RELEASE");
				//isLineRcptLengthZero = false;
				eleOrderLine = (Element) ndlstOrderLines.item(i);
				log.debug("Orderline element is " +XMLUtil.getElementXMLString(eleOrderLine));
				//Start --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
				String sCarrierServiceCodeLine = eleOrderLine.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
				// End --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
				String sOrderLineKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				//String sOrderHeaderKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
				String sQty = eleOrderLine.getAttribute(KohlsXMLLiterals.A_STATUS_QUANTITY);
				//dOrderedQty = Double.parseDouble(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QUANTITY));

				


				//Obtain the Line Gift Receipt ID and check if the value is null.
              //  Element eleOrderLineExtn = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);

            //  if(strLneTp.equalsIgnoreCase(KohlsConstant.LINE_TYPE_PGC)){
				//	strLineType = KohlsConstant.LINE_TYPE_PGC;
				//}
				if(!sQty.equals(KohlsConstant.BLANK))
				strAccQty += (int)Float.parseFloat(sQty);

				eleItem = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
				String sItemId = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
				
				
				
				
				log.debug("Status Qty : " + sQty);
				if (0 == iCount) {
						sPickTicketNo = StringUtil.prepadStringWithZeros(KohlsUtil.getNextSeqNo(env, this.m_conn), KohlsConstant.PICKTICKET_LEN);
						
						log.debug(" Pickticket  : " + sPickTicketNo);
						if(isReceivingNodeWareHouse(env, sReceivingNode)){
							
							sPickTicketNo = "X"+sPickTicketNo.substring(1);
							
							
						}else{
							log.debug("Inside else  : ");
							sPickTicketNo = "C"+sPickTicketNo.substring(1);
						}
						log.debug("Generating Pickticket  : " + sPickTicketNo);

						if (YFCLogUtil.isDebugEnabled()) {
							log.debug("PickTicket No  : " + sPickTicketNo);
						}
					}

					
					iCount++;

					// Order Broker start
					env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, KohlsSendTOReleaseToWMoSAgentXmls.getItemListTemplate());
					Document docGetItemListEx = this.api.getItemList(env, KohlsSendTOReleaseToWMoSAgentXmls.getItemListInputXML(env, sItemId));
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

										
					Element eleOrderLineDs = (Element)docWMoSOrderReleaseXML.importNode(ndlstOrderLines.item(i), true);
					log.debug("eleOrderLineDs>>>>>"+XMLUtil.getElementXMLString(eleOrderLineDs));	
					Element eleInstr = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_INSTRUCTIONS).item(0);
					Element eleLnPriceInfo = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_LINE_PRICE_INFO).item(0);
					Element eleLineChrgs = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGES).item(0);
					Element eleLineTaxes = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAXES).item(0);
					eleOrderLineDs.removeChild(eleInstr);
					eleOrderLineDs.removeChild(eleLnPriceInfo);
					eleOrderLineDs.removeChild(eleLineChrgs);
					eleOrderLineDs.removeChild(eleLineTaxes);
					ndOrderLines.appendChild(eleOrderLineDs);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_GIFT_FLAG);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_GIFT_WRAP);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_LINE_TYPE);
					eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_ORDERED_QTY);
					
					//eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_OPEN_QTY);
					

					
					Element eleOrderLineDsOStatus = (Element) eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUSES).item(0);
					Element eleOrderLineDsNotes = (Element) eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);

					eleOrderLineDs.removeChild(eleOrderLineDsOStatus);
					eleOrderLineDs.removeChild(eleOrderLineDsNotes);

					Element eleOrderLineDsItem = (Element) eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
					eleOrderLineDsItem.removeAttribute(KohlsXMLLiterals.A_PRODUCT_LINE);
					
					eleOrderLineDsItem.setAttribute(KohlsXMLLiterals.A_ITEM_DESC, eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_DESC));
					eleOrderLineDsItem.setAttribute(KohlsXMLLiterals.A_ITEM_DESC, eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC));
					log.debug("eleOrderLineDs after change>>>>>"+XMLUtil.getElementXMLString(eleOrderLineDs));	
					
				YFCElement yfcEleOrderStatusOrderLine = yfcDocChangeOrderStatus.createElement(KohlsXMLLiterals.E_ORDER_LINE);
				yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_BASEDROPSTATUS, KohlsConstant.STATUS_SEND_RELEASE_TO_WMOS);
				yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, sOrderLineKey);
				yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, sQty);
				yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
				yfcEleOrderStatusOrderLines.appendChild(yfcEleOrderStatusOrderLine);

				}
			log.debug("INSIDE SEND2WMOS - COMPLETED LOOPING THROUGH LINES OF THE RELEASE");
			
		
			
				log.debug("INSIDE SEND2WMOS - REGULAR RELEASE - SENDING TO WMOS");
				
				eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO, sPickTicketNo);
				
				this.callChangeRelease(env, sOrderReleaseKey, sPickTicketNo);
				
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Input XML to ChangeOrderStatus : " + XMLUtil.getXMLString(yfcDocChangeOrderStatus.getDocument()));
				}

				this.api.changeOrderStatus(env, yfcDocChangeOrderStatus.getDocument());

				
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
				
				// Order Broker end
				sPackTypeList = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE);

				//Start --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012
				if (!strExtnWrapCode.equalsIgnoreCase("")) {
					eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_GW);
				} else {
					eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_ST);
				}
				//End --- Added for PMR 54615,379,000 -- OASIS_SUPPORT 20/03/2012

				log.debug("XML to WMos : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Release Message to WMoS : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
				}
				log.debug("Release Message to WMoS : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
			    this.api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_SEND_RELEASE_TO_WMOS, docWMoSOrderReleaseXML);
			   

			}

			KohlsUtil.registerProcessCompletion(env, sDataKey, sDataType, sTaskQKey);


		}catch(YFSException ex){
			ex.printStackTrace();
    		throw ex;
		}
		return null;
	}



	/**
	 * @param env
	 * @param sReceivingNode
	 */
	private Boolean isReceivingNodeWareHouse(YFSEnvironment env,
			String sReceivingNode) {
		Boolean isReceivingNodeWareHouse = false;
		try {
			//get list of EFCs
			log.debug("Inside isReceivingNodeWareHouse  : ");
			Set<String> lstReceivingNode = KohlsUtil.getCommonCodeValueList(env, KohlsConstant.WAREHOUSE_TRAN);
			if(null != lstReceivingNode){
				isReceivingNodeWareHouse=  lstReceivingNode.contains(sReceivingNode);
				
			}
		} catch (YFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return isReceivingNodeWareHouse;
	}

	private boolean isVGC(String strLineType, String sProductLine) {
		return KohlsConstant.PRODUCT_LINE_VGC.equalsIgnoreCase(sProductLine) || KohlsConstant.PRODUCT_LINE_VGC.equalsIgnoreCase(strLineType);
	}

	/**
	 * Commented for Iteration 4 changes
	 */
	private String getRegTransValues(YFSEnvironment env) {

		String sRegTransValues = "";
		try {
			sRegTransValues = StringUtil.prepadStringWithZeros(KohlsUtil
					.getNextRegTransNo(env, this.m_conn),
					KohlsConstant.REG_TRANS_LEN);
			// sTransactionNo =
			// StringUtil.prepadStringWithZeros(KohlsUtil.getNextTransactionNo(env,
			// this.m_conn), KohlsConstant.TRANSACTION_LEN);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sRegTransValues;
	}

	/**
	 * Commented for Iteration 4 changes
	 */

	

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
		//System.out.println("Input XML to changeRelease - REL A : " + XMLUtil.getXMLString(yfcDocChangeRelease.getDocument()));
		this.api.changeRelease(env, yfcDocChangeRelease.getDocument());

	}

	private String getRoutingGuide(YFSEnvironment env, Element eleGetOrderReleaseDetails, String sItemId)
	throws YFSException, RemoteException, TransformerException{

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<!--------------- Inside Routing Guide ----------->");
		}

		env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, KohlsSendTOReleaseToWMoSAgentXmls.getItemListTemplate());
		Document docGetItemList = this.api.getItemList(env, KohlsSendTOReleaseToWMoSAgentXmls.getItemListInputXML(env, sItemId));
		env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);


		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemList Output XML  : " + XMLUtil.getXMLString(docGetItemList));
		}

		//System.out.println("getItemListTemplate Output XML: "+ XMLUtil.getXMLString(docGetItemList));
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

	

}
