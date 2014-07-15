package com.kohls.bopus.api;

/**************************************************************************
 * File : KohlsSendOrderStatusToEcomm.java Author : IBM 
 * Created : January 27 2014
 * Modified :
 * Version : 0.1
 ***************************************************************************** 
 * HISTORY 
 ***************************************************************************** 
 * V0.1 27/01/2014 IBM First Cut.

 ***************************************************************************** 
 * TO DO : 
 * ***************************************************************************
 * Copyright @ 2014. This document has been prepared and written by IBM Global
 * Services on behalf of Kohls, and is copyright of Kohls
 * 
 ***************************************************************************** 
 ***************************************************************************** 
 * This file forms the document to be sent to Ecomm on status update
 * 
 * @author Baijayanta
 * @version 0.1
 * @author Juned S
 * @version 0.2
 *****************************************************************************/

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
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

public class KohlsSendOrderStatusToEcomm implements YIFCustomApi {

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsSendOrderStatusToEcomm.class.getName());
	private String strDeliveryMethod = null;
	private Element eleNotes = null;
	private Element eleNote = null;
	private ArrayList<Element> eleNoteList = null;
	private Iterator<Element> itrEleNote = null;
	private boolean isStatusSent = false;
	private ArrayList<String> codeList = new ArrayList<String>();
	private String strReasonCode = null;
	private String strNoteText = null;
	private List<String> noteTextList = new ArrayList<String>();
	
	
	public KohlsSendOrderStatusToEcomm() throws YIFClientCreationException {
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	public void setProperties(Properties arg0) throws Exception {
		
	}
	
	public void statusSentToEcomm(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, TransformerException {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("inside statusSentToEcomm.... ");
		 }

		String orderLineStat = "";
		log.debug("the input doc is:"+KohlsXMLUtil.getXMLString(inXML));
		Element eleOrder =   inXML.getDocumentElement();
		
		String tagName = eleOrder.getTagName();
		
		log.debug("tagname is:"+tagName);
		
	
		if(tagName.equalsIgnoreCase(KohlsConstant.SHIPMENT)) {
			try {
				statusCustPickSentToEcomm(env,inXML) ;
			} catch (Exception e){
				e.printStackTrace();
				throw new YFSException("Exception in statusCustPickSentToEcomm method"+e.getStackTrace());
			}
		} else {
			
		//Generate the document for calling getCommonCodeList API
		YFCDocument yfcDocGetReturnCodeList = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		YFCElement yfcEleReturnCodeList = yfcDocGetReturnCodeList.getDocumentElement();
		yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_ORDER_STATUS);
		
		Document docGetCommonCodeOutputXML = api.getCommonCodeList(env, yfcDocGetReturnCodeList.getDocument());
	
		NodeList ndlstCommonCodeList = docGetCommonCodeOutputXML.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		
		//loop through the commoncodeList and add it to an arrayList
		for(int j=0;j<ndlstCommonCodeList.getLength();j++){

			Element eleCommonCode = (Element)ndlstCommonCodeList.item(j);
			codeList.add(eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE));		
		}
		
		try {
		
		NodeList nodeOrderLine= eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
	
		for(int i=0;i<nodeOrderLine.getLength();i++){

			Element eleOrderLine = (Element) nodeOrderLine.item(i);
			
			strDeliveryMethod = eleOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
				orderLineStat = eleOrderLine.getAttribute(KohlsXMLLiterals.A_MAX_ORDER_LINE_STATUS);
				eleNotes = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_NOTES); 
				eleNoteList = SCXmlUtil.getChildren(eleNotes, KohlsXMLLiterals.E_NOTE); 
				itrEleNote = eleNoteList.iterator();
				
				while(itrEleNote.hasNext()){						
					eleNote = itrEleNote.next();
					strReasonCode = eleNote.getAttribute(KohlsXMLLiterals.A_REASON_CODE);
					strNoteText = eleNote.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);
					if(strNoteText != null)
					    noteTextList = Arrays.asList(strNoteText.split(","));
					if(KohlsConstant.ORDER_STATUS_SENT_TO_ECOMM.equals(strReasonCode) && noteTextList.contains("Status "+ orderLineStat +" sent to Ecommerce")){
						isStatusSent = true;
						break;
					}
				}

				if(codeList.contains(orderLineStat) && !isStatusSent) {


					YFCDocument yfcDocOrderStatusToECommXML = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
					YFCElement yfcEleOrderStatusToECommXML = yfcDocOrderStatusToECommXML.getDocumentElement();

					yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_ORDERNO, eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER) );
					yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, eleOrder.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
					yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleOrder.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));

					YFCElement yfcEleOrderLines = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_LINES);
					yfcEleOrderStatusToECommXML.appendChild(yfcEleOrderLines);

					YFCElement yfcEleOrderLine = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_LINE);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, eleOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SCAC, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SCAC));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, eleOrderLine.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));

					Element eleItem = KohlsXMLUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ITEM);

					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID,eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_UOM, eleItem.getAttribute(KohlsXMLLiterals.A_UOM));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, eleItem.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));

					YFCElement yfcEleOrderstatuses = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_STATUSES);
					YFCElement yfcEleOrderstatus = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_STATUS);

					Element eleOrderStatusesfrominput = KohlsXMLUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES);
					Element eleOrderStatusfrominput = KohlsXMLUtil.getChildElement(eleOrderStatusesfrominput, KohlsXMLLiterals.E_ORDER_STATUS);
					yfcEleOrderstatus.setAttribute(KohlsXMLLiterals.A_SHIPNODE, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
					yfcEleOrderstatus.setAttribute(KohlsXMLLiterals.A_STATUS, eleOrderStatusfrominput.getAttribute(KohlsXMLLiterals.A_STATUS));
					yfcEleOrderstatus.setAttribute(KohlsXMLLiterals.A_STATUS_DATE,eleOrderStatusfrominput.getAttribute(KohlsXMLLiterals.A_STATUS_DATE));
					yfcEleOrderstatus.setAttribute(KohlsXMLLiterals.A_STATUS_DESCRIPTION, eleOrderStatusfrominput.getAttribute(KohlsXMLLiterals.A_STATUS_DESCRIPTION));
					yfcEleOrderstatus.setAttribute(KohlsXMLLiterals.A_STATUS_QTY,eleOrderStatusfrominput.getAttribute(KohlsXMLLiterals.A_STATUS_QTY));


					yfcEleOrderLines.appendChild(yfcEleOrderLine);
					yfcEleOrderLine.appendChild(yfcEleOrderstatuses);
					yfcEleOrderstatuses.appendChild(yfcEleOrderstatus);
					
					log.debug("XML documnet for queue:"+  SCXmlUtil.getString(yfcDocOrderStatusToECommXML.getDocument()));
					
					if (orderLineStat.equals(KohlsConstant.BACK_ORDERED) || orderLineStat.equals(KohlsConstant.V_CANCEL_STATUS)) {
						this.api.executeFlow(env, "KohlsBopusOMSSendCancelStatusMessageSyncService", yfcDocOrderStatusToECommXML.getDocument());
					} else {
						this.api.executeFlow(env, "KohlsBopusOMSSendStatusMessageSyncService", yfcDocOrderStatusToECommXML.getDocument());
					}
					
					if(strNoteText != null)
						strNoteText.concat(",").concat("Status "+ orderLineStat +" sent to Ecommerce");
					else
						strNoteText = "Status "+ orderLineStat +" sent to Ecommerce";
					
					if(eleNote == null)
					   eleNote = SCXmlUtil.createChild(eleNotes, KohlsXMLLiterals.E_NOTE);
					
					eleNote.setAttribute(KohlsXMLLiterals.A_REASON_CODE, KohlsConstant.ORDER_STATUS_SENT_TO_ECOMM);
					eleNote.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, strNoteText);
					
					eleNotes.setAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES, String.valueOf(Integer.parseInt(eleNotes.getAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES))+1));

				}

		  }
	
	}
		catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException("Exception in statusSentToEcomm method"+e.getStackTrace());
		}
		log.debug("Exiting statusSentToEcomm.... ");
	  }

	}
	
	public void statusCustPickSentToEcomm(YFSEnvironment env, Document inXML) throws Exception {
		log.debug("inside statusCustPickSentToEcomm.... ");
		//Generate the document for calling getCommonCodeList API
		YFCDocument yfcDocGetReturnCodeList = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		YFCElement yfcEleReturnCodeList = yfcDocGetReturnCodeList.getDocumentElement();
		yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_ORDER_STATUS);
		
		Document docGetCommonCodeOutputXML = api.getCommonCodeList(env, yfcDocGetReturnCodeList.getDocument());
	
		NodeList ndlstCommonCodeList = docGetCommonCodeOutputXML.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		
		
		try {
			
			//loop through the commoncodeList and add it to an arrayList
			for(int j=0;j<ndlstCommonCodeList.getLength();j++){

				Element eleCommonCode = (Element)ndlstCommonCodeList.item(j);
				codeList.add(eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE));		
			}
			
			Element eleShimpment = inXML.getDocumentElement();
			
			String strEnterpriseCode = eleShimpment.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);
			
			Element eleShipmentLines = SCXmlUtil.getChildElement(eleShimpment, KohlsXMLLiterals.E_SHIPMENT_LINES);
			
			ArrayList<Element> shipmentLinesList = SCXmlUtil.getChildren(eleShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);
			
			String strOrderLineKey = shipmentLinesList.get(0).getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
			String strOrderNo = shipmentLinesList.get(0).getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
			String itemId = shipmentLinesList.get(0).getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			
			Document indocGetOrderList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
			Element eleGetOrderList = indocGetOrderList.getDocumentElement();
			
			eleGetOrderList.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, eleShimpment.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
			eleGetOrderList.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleShimpment.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
			eleGetOrderList.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, strOrderNo);
			
			
			Document outdocGetOrderList = KohlsCommonUtil.invokeAPI(env, KohlsConstant.KOHLS_GETORDERLIST_TEMPLATE, KohlsConstant.API_GET_ORDER_LIST, indocGetOrderList);
			Element eleOutGetOrderList = outdocGetOrderList.getDocumentElement();
			
			Element eleOrder = SCXmlUtil.getChildElement(eleOutGetOrderList, KohlsXMLLiterals.E_ORDER);
			
			Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, KohlsXMLLiterals.E_ORDER_LINES);
			
			ArrayList<Element> orderLineList = SCXmlUtil.getChildren(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
			Iterator<Element> itrOrderLine = orderLineList.iterator();
			Element eleOrderLine = null;
			Element eleOrderStatuses = null;
			ArrayList<Element> orderStatusList = null;
			Iterator<Element> itrOrderStatus = null;
			Element eleOrderStatus = null;
			String strMaxLineStatus = null;
			
			Document outDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
			Element eleDoc = outDoc.getDocumentElement();
			eleDoc.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, eleShimpment.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
			eleDoc.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleShimpment.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
			eleDoc.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, strOrderNo);
			eleOrderLines = SCXmlUtil.createChild(outDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER_LINES);
			
			while(itrOrderLine.hasNext()){
					eleOrderLine = itrOrderLine.next();
					strDeliveryMethod = eleOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
	
					strMaxLineStatus =  eleOrderLine.getAttribute(KohlsXMLLiterals.A_MAX_ORDER_LINE_STATUS);
					
					eleNotes = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_NOTES); 
					eleNoteList = SCXmlUtil.getChildren(eleNotes, KohlsXMLLiterals.E_NOTE); 
					itrEleNote = eleNoteList.iterator();
					
					
					while(itrEleNote.hasNext()){						
						eleNote = itrEleNote.next();
						strReasonCode = eleNote.getAttribute(KohlsXMLLiterals.A_REASON_CODE);
						strNoteText = eleNote.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);
						if(strNoteText != null)
						    noteTextList = Arrays.asList(strNoteText.split(","));
						if(KohlsConstant.ORDER_STATUS_SENT_TO_ECOMM.equals(strReasonCode) && noteTextList.contains("Status "+ strMaxLineStatus +" sent to Ecommerce")){
							isStatusSent = true;
							break;
						}
					}

					eleOrderStatuses = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES);
					orderStatusList = SCXmlUtil.getChildren(eleOrderStatuses, KohlsXMLLiterals.E_ORDER_STATUS);

					itrOrderStatus = orderStatusList.iterator();

					while(itrOrderStatus.hasNext()){
						eleOrderStatus = itrOrderStatus.next();
						if(strMaxLineStatus.equals(eleOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS)))
							break;
					}

					if(strOrderLineKey.equals(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY)) && codeList.contains(strMaxLineStatus) && !isStatusSent){
						eleOrderLine.removeAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
						eleOrderLine.removeAttribute(KohlsXMLLiterals.A_MAX_ORDER_LINE_STATUS);
						eleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, itemId);
						eleOrderLine.setAttribute(KohlsXMLLiterals.A_UOM, KohlsConstant.EACH);
						eleOrderStatuses = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES);
						SCXmlUtil.removeNode(eleOrderStatuses);
						eleOrderStatuses = SCXmlUtil.createChild(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES);
						SCXmlUtil.importElement(eleOrderStatuses, eleOrderStatus);
						SCXmlUtil.importElement(eleOrderLines, eleOrderLine);
					}

				
			}

			eleOrderLines = SCXmlUtil.getChildElement(eleDoc, KohlsXMLLiterals.E_ORDER_LINES);
			orderLineList = SCXmlUtil.getChildren(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
			log.debug("XML documnet to queue for pickedUp:"+  SCXmlUtil.getString(outDoc));
			
			if(!orderLineList.isEmpty()){
			    KohlsCommonUtil.invokeService(env, "KohlsBopusOMSSendStatusMessageSyncService", outDoc);		
			    eleNote = SCXmlUtil.createChild(eleNotes, KohlsXMLLiterals.E_NOTE);
				
				if(strNoteText != null)
					strNoteText.concat(",").concat("Status "+ strMaxLineStatus +" sent to Ecommerce");
				else
					strNoteText = "Status "+ strMaxLineStatus +" sent to Ecommerce";
				
				if(eleNote == null)
				   eleNote = SCXmlUtil.createChild(eleNotes, KohlsXMLLiterals.E_NOTE);
				
				eleNote.setAttribute(KohlsXMLLiterals.A_REASON_CODE, KohlsConstant.ORDER_STATUS_SENT_TO_ECOMM);
				eleNote.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, strNoteText);
				
				eleNotes.setAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES, String.valueOf(Integer.parseInt(eleNotes.getAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES))+1));
			}
			
		}
		catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException("Exception in statusCustPickSentToEcomm method"+e.getStackTrace());
		}
		
		log.debug("Exiting statusCustPickSentToEcomm.... ");
	}
	
}
	

