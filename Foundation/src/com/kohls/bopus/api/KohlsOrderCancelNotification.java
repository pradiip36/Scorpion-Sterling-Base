package com.kohls.bopus.api;

/*****************************************************************************
 * File Name    : KohlsOrderCancelNotification.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Mar 20,2014    Ashalatha        Initial Version 
 * * ---------------------------------------------------------------------------
 *****************************************************************************/


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This extended api is invoked to send the Order 
 * Cancellation Customer Notification. When all the orderlines for the same store 
 * are cancelled because of Short pick(Kohls Driven Customer Cancellation) then it is 
 * considered as the Order Cancellation and the Cancellation customer notification will be sent.
 * 
 */
public class KohlsOrderCancelNotification {

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsOrderCancelNotification.class.getName());

	public static Document getDocumentFromElement(Element element)
			throws ParserConfigurationException, FactoryConfigurationError
			{
		Document doc = null;
		Node nodeImp = null;
		DocumentBuilder dbdr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = dbdr.newDocument();
		nodeImp = doc.importNode(element, true);
		doc.appendChild(nodeImp);
		return doc;
			}

	public Document cancelCustNotification(YFSEnvironment env,  Document inXML) throws Exception{

		log.debug("*****cancelCustNotification method **************************");
		Element eleOrderElement=  inXML.getDocumentElement();
		return sendCustNotificatioForOrdCncl(env,eleOrderElement);

	}

	private Document sendCustNotificatioForOrdCncl(YFSEnvironment env, Element eleOrderElement) {
		try{
			Document orderListDoc= null;
			Document inDocOrderAudit = null;
			Document orderAuditListDoc = null;
			Document inDoc = null;

			boolean kohlsDrivenCancellation = false;

			Document getOrderListInDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
			Element inEleOrder = getOrderListInDoc.getDocumentElement();
			inEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, eleOrderElement.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));
			orderListDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstant.API_GET_ORDER_LIST_BOPUS_CONSOL_TEMPLATE, KohlsConstant.API_GET_ORDER_LIST, getOrderListInDoc);
			log.debug("orderListDoc : "+SCXmlUtil.getString(orderListDoc));		
			
			inDocOrderAudit = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER_AUDIT);
			inDocOrderAudit.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, eleOrderElement.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));

			orderAuditListDoc = KohlsCommonUtil.invokeAPI( env, KohlsConstant.GET_ORDER_AUDIT_LIST_OUTPUT_TMPLT, KohlsConstant.API_GET_ORDER_AUDIT_LIST, inDocOrderAudit);
			log.debug("getOrderAuditList output :"+XMLUtil.getXMLString(orderAuditListDoc));

			Element eleOrderAutditLevels = null;

			Document orderAutditDoc = null;
			Element eleOrderAuditLevels = null;

			Document orderAutdit2beimpDoc = null;
			Map<String, Element> orderAuditLineMap = new HashMap<String, Element>();

			if(orderAuditListDoc != null){

				ArrayList<Element> eleOrderAuditList = SCXmlUtil.getChildren(orderAuditListDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER_AUDIT);
				Iterator<Element> itrOrderAuditList  = eleOrderAuditList.iterator();

				Element eleOrderAudit = null;
				Element eleOrderAuditLevel = null;
				Element eleOrderAuditDetails = null;

				ArrayList<Element> eleOrderAuditLevelList = null;
				Iterator<Element> itrOrderAuditLevelList = null;

				ArrayList<Element> eleOrderAuditDetailList = null;
				Iterator<Element> itrOrderAuditDetailList = null;

				ArrayList<Element> eleAtrributeList = null;
				Iterator<Element> itrAtrributeList = null;

				Element eleAttributes = null;
				Element eleAttribute = null;

				while(itrOrderAuditList.hasNext()){
					eleOrderAudit = itrOrderAuditList.next();
					String strReasonCode=eleOrderAudit.getAttribute(KohlsXMLLiterals.A_REASON_CODE);

					if(!strReasonCode.equalsIgnoreCase(KohlsConstant.BLANK) && !strReasonCode.equalsIgnoreCase(KohlsConstant.REASON_CODE_CUST_INITIATED_MODIFICATION) && !strReasonCode.equalsIgnoreCase(KohlsConstant.REASON_CODE_NO_INV)){
						log.debug("********Kohl's Driven Order Cancellation *****************");
						kohlsDrivenCancellation = true;	
						break;
					}
				}

				if(kohlsDrivenCancellation){
					Iterator<Element> itrOrderAuditListNew  = eleOrderAuditList.iterator();
					while(itrOrderAuditListNew.hasNext()){
						eleOrderAudit = itrOrderAuditListNew.next();
						eleOrderAutditLevels = SCXmlUtil.getChildElement(eleOrderAudit, KohlsXMLLiterals.E_ORDER_AUDIT_LEVELS);
						eleOrderAuditLevelList = SCXmlUtil.getChildren(eleOrderAutditLevels, KohlsXMLLiterals.E_ORDER_AUDIT_LEVEL);
						itrOrderAuditLevelList = eleOrderAuditLevelList.iterator();
						while(itrOrderAuditLevelList.hasNext()){
							eleOrderAuditLevel = itrOrderAuditLevelList.next();
							if(KohlsConstant.ORDER_LINE.equals(eleOrderAuditLevel.getAttribute(KohlsXMLLiterals.A_MODIFICATION_LEVEL))){
								orderAutditDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER_AUDIT);
								eleOrderAuditLevels = SCXmlUtil.createChild(orderAutditDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER_AUDIT_LEVELS);
								SCXmlUtil.importElement(eleOrderAuditLevels, eleOrderAuditLevel);
								orderAuditLineMap.put(eleOrderAuditLevel.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY), orderAutditDoc.getDocumentElement());
							}
							else if(KohlsConstant.ORDER.equals(eleOrderAuditLevel.getAttribute(KohlsXMLLiterals.A_MODIFICATION_LEVEL))){

								eleOrderAuditDetails = SCXmlUtil.getChildElement(eleOrderAuditLevel, KohlsXMLLiterals.A_ORDER_AUDIT_DETAILS);								

								eleOrderAuditDetailList = SCXmlUtil.getChildren(eleOrderAuditDetails, KohlsXMLLiterals.A_ORDER_AUDIT_DETAIL);
								itrOrderAuditDetailList = eleOrderAuditDetailList.iterator();
								while(itrOrderAuditDetailList.hasNext()){
									eleAttributes = SCXmlUtil.getChildElement(itrOrderAuditDetailList.next(), KohlsXMLLiterals.E_ATTRIBUTES); 
									eleAtrributeList = SCXmlUtil.getChildren(eleAttributes, KohlsXMLLiterals.E_ATTRIBUTE);
									itrAtrributeList = eleAtrributeList.iterator();
									while(itrAtrributeList.hasNext()){
										eleAttribute = itrAtrributeList.next();
										if(KohlsXMLLiterals.A_TOTAL_AMOUNT.equals(eleAttribute.getAttribute(KohlsXMLLiterals.A_NAME))){
											orderAutdit2beimpDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER_AUDIT);
											eleOrderAuditLevels = SCXmlUtil.createChild(orderAutdit2beimpDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER_AUDIT_LEVELS);
											SCXmlUtil.importElement(eleOrderAuditLevels, eleOrderAuditLevel);
										}
									}
								}
							}

						}
					}
					if(orderListDoc != null){

						Element eleOrder = SCXmlUtil.getChildElement(orderListDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER);

						if (!YFCCommon.isVoid(orderAutdit2beimpDoc)){
							SCXmlUtil.importElement(eleOrder, orderAutdit2beimpDoc.getDocumentElement());
						}

						Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, KohlsXMLLiterals.E_ORDER_LINES);
						ArrayList<Element> eleOrderLineList = SCXmlUtil.getChildren(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
						Element eleOrderLine = null;
						Element eleShipnode = null;
						String strShipnode = null;
						String strStatus = null;

						List<Element> cancelledOorderLineToBeAddedList = new ArrayList<Element>();
						List<Element> orderLineListForShipNode = new ArrayList<Element>();

						String str_ship_node = KohlsConstant.BLANK;

						String strExtnCustCancelNotification  = null;

						Iterator<Element> itrOrderLineList = eleOrderLineList.iterator();
						while(itrOrderLineList.hasNext()){
							eleOrderLine = itrOrderLineList.next();

							strStatus = eleOrderLine.getAttribute(KohlsXMLLiterals.E_STATUS);
							Element eleExtn = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_EXTN);
							strExtnCustCancelNotification = eleExtn.getAttribute(KohlsXMLLiterals.EXTN_CUST_CNCL_NOTICE);
							log.debug("strExtnCustCancelNotification : "+strExtnCustCancelNotification);	
							if(strStatus.equalsIgnoreCase(KohlsConstant.CANCELLED) && !strExtnCustCancelNotification.equalsIgnoreCase(KohlsConstant.YES) ){	
								log.debug("Cancelled eleOrderLine : "+SCXmlUtil.getString(eleOrderLine));
								cancelledOorderLineToBeAddedList.add(eleOrderLine);
							}
						}
						Iterator<Element> itrCancelledOrderLineList = cancelledOorderLineToBeAddedList.iterator();	
						List<Element> cancelledOrderLineList = new ArrayList<Element>();

						while(itrCancelledOrderLineList.hasNext()){
							
							Element eleCancelledOrderLine = null;

							Element eleCancelledOrderLines = null;
							eleCancelledOrderLine = itrCancelledOrderLineList.next();

							Element eleShipNode = SCXmlUtil.getChildElement(eleCancelledOrderLine, KohlsXMLLiterals.E_SHIPNODE);
							
							log.debug("eleShipNode is : "+SCXmlUtil.getString(eleOrderLine));

							String strShipNodeForCnclOL=eleShipNode.getAttribute(KohlsConstant.ATTR_SHIP_NODE);

							if(!str_ship_node.contains(strShipNodeForCnclOL)){
								
								itrOrderLineList = eleOrderLineList.iterator();
								int count =0;								
								while(itrOrderLineList.hasNext()){
									eleOrderLine = itrOrderLineList.next();
									eleShipnode = SCXmlUtil.getChildElement(eleOrderLine,KohlsXMLLiterals.E_SHIPNODE);
									strShipnode=eleShipnode.getAttribute(KohlsConstant.ATTR_SHIP_NODE);
									if(strShipNodeForCnclOL.equalsIgnoreCase(strShipnode)){	
										count+=1;
										if(KohlsConstant.CANCELLED.equalsIgnoreCase(eleOrderLine.getAttribute(KohlsXMLLiterals.E_STATUS))){	
											cancelledOrderLineList.add(eleOrderLine);										
										}										
										orderLineListForShipNode.add(eleOrderLine);
									}
								}
								
								if(count == cancelledOrderLineList.size()){																
									log.debug("********Order Cancellation Customer Notifiaction*************");

									if(!cancelledOrderLineList.isEmpty()){
										SCXmlUtil.removeNode(eleOrderLines);
										eleCancelledOrderLines = SCXmlUtil.createChild(eleOrder, KohlsXMLLiterals.E_ORDER_LINES);
									}

									Iterator<Element> itrCancelledLineListForShipNode = cancelledOrderLineList.iterator();
									while(itrCancelledLineListForShipNode.hasNext()){
										Element cancelledOrderLine= null;
										cancelledOrderLine = itrCancelledLineListForShipNode.next();
										if(orderAuditLineMap.containsKey(cancelledOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY)))
											SCXmlUtil.importElement(cancelledOrderLine, orderAuditLineMap.get(cancelledOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY)));

										callChangeOrder(env,eleOrder,cancelledOrderLine);
										
										// Code to fetch Item Size and Color Description
										Element elemItem = SCXmlUtil.getChildElement(cancelledOrderLine,
												KohlsXMLLiterals.E_ITEM);
										Document docItem = getDocumentFromElement(elemItem);
										 Document itemListDoc = KohlsCommonUtil
										 .invokeAPI(
										 env,
										 KohlsConstant.API_GET_ITEM_LIST_WITH_EXTN_FIELDS_TEMPLATE_PATH,
										 KohlsConstant.API_GET_ITEM_LIST, docItem);

										 Element eleItem = SCXmlUtil.getChildElement(
												itemListDoc.getDocumentElement(),
												KohlsXMLLiterals.E_ITEM);
										Element eleItemExtn = SCXmlUtil.getChildElement(eleItem,
												KohlsXMLLiterals.E_EXTN);
										String colorDesc = eleItemExtn
												.getAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC);
										String sizeDesc = eleItemExtn
												.getAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC);

										Element extnElem = SCXmlUtil.createChild(elemItem,
												KohlsXMLLiterals.E_EXTN);
										extnElem.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC,
												colorDesc);
										extnElem.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC,
												sizeDesc);
										
										SCXmlUtil.importElement(eleCancelledOrderLines, cancelledOrderLine);

									} 
									
									eleOrder.setAttribute(KohlsXMLLiterals.A_CUST_NOTIFICATION_TYPE, KohlsConstant.ORDER_CANCELLATION_CUST_NOTIFICATION);
									inDoc = getDocumentFromElement(eleOrder);

									eleCancelledOrderLines = SCXmlUtil.getChildElement(eleOrder, KohlsXMLLiterals.E_ORDER_LINES);
									ArrayList<Element> eleCancelledOrderLinesList = SCXmlUtil.getChildren(eleCancelledOrderLines, KohlsXMLLiterals.E_ORDER_LINE);		
									if(!eleCancelledOrderLinesList.isEmpty()){
										log.debug("Customer Notification Message sent :"+XMLUtil.getXMLString(inDoc));
										KohlsCommonUtil.invokeService(env,KohlsConstant.KOHLS_BPS_ORDR_CANCEL_CUST_MSG, inDoc);
									}
									if(!YFCCommon.isVoid(eleCancelledOrderLines)){
										SCXmlUtil.removeNode(eleCancelledOrderLines);
									}
								}

								cancelledOrderLineList.clear();
								str_ship_node+= str_ship_node+";"+strShipNodeForCnclOL;
							}

						}						

					}
				}

			}
			return inDoc;
		}catch(Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in sendCustNotificatioForOrdCncl method is: "+e.getMessage());
		}
	}

	private void callChangeOrder(YFSEnvironment env,Element eleOrder, Element cancelledOrderLine) {
		try{
			log.debug("*** Call change Order API to stamp the ExtnCustCancelNotice='Y' for Kohls driven cancelled Order Lines ");
			Document docOrder = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);

			docOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE , KohlsConstant.SO_DOCUMENT_TYPE);
			docOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.KOHLS_ENTERPRISE_CODE);
			docOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
			docOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORDERNO, eleOrder.getAttribute(KohlsXMLLiterals.A_ORDERNO	));

			Element eleOrderLines = SCXmlUtil.createChild(docOrder.getDocumentElement(), KohlsXMLLiterals.E_ORDER_LINES);
			Element eleOrderLine = SCXmlUtil.createChild(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
			eleOrderLine.setAttribute(KohlsXMLLiterals.A_ACTION	, KohlsConstant.ACTION_MODIFY);
			eleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, cancelledOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY	));

			Element eleExtn = SCXmlUtil.createChild(eleOrderLine, KohlsXMLLiterals.E_EXTN);
			eleExtn.setAttribute(KohlsXMLLiterals.EXTN_CUST_CNCL_NOTICE, KohlsConstant.YES);

			KohlsCommonUtil.invokeAPI(env, KohlsConstant.CHANGE_ORDER_API, docOrder);
		}catch(Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in callChangeOrder method is: "+e.getMessage());
		}

	}
}

