package com.kohls.bopus.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsInvoiceUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsBOPUSGetOrderDetails {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsInvoiceUtil.class.getName());
	
	public static Document getDocumentFromElement(Element element)
	    throws ParserConfigurationException, FactoryConfigurationError {
	   Document doc = null;
	   Node nodeImp = null;
	   DocumentBuilder dbdr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	   doc = dbdr.newDocument();
	   nodeImp = doc.importNode(element, true);
	   doc.appendChild(nodeImp);
	   return doc;
	}
	
	/**
	 * 
	 * This methods returns Order details with its corresponding short pick store events
	 * for each shipment corresponding to each BOPUS Order line
	 * @author Ravi
	 * 
	 **/
	
	 
	public Document getOrderDetails_ShipmentStoreEvents(YFSEnvironment env, Document inDoc) {

		Document docOrderDetails = null;
			
		try {
			Document docOrderListOutPut = 
				KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_GET_ORDER_LIST_BOPUS_TEMPLATE ,KohlsConstant.API_GET_ORDER_LIST, inDoc);
			
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("######### getOrderList API Output  ##########"+ XMLUtil.getXMLString(docOrderListOutPut));				 
			}
			
			Element eleOrderList=docOrderListOutPut.getDocumentElement();
			Element eleOrder = SCXmlUtil.getChildElement(eleOrderList, KohlsXMLLiterals.E_ORDER );

			docOrderDetails = getDocumentFromElement(eleOrder); 
			Element eleOrderDetails = docOrderDetails.getDocumentElement();
			String OrderNo = eleOrderDetails.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
			Element eleOrderLines = SCXmlUtil.getChildElement(eleOrderDetails, KohlsXMLLiterals.E_ORDER_LINES);
			NodeList nodeOrderLineList = eleOrderLines.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

			for (int i=0; i< nodeOrderLineList.getLength(); i++ ){

				Element eleOrderLine = (Element) nodeOrderLineList.item(i);
				String DeliveryMethod = eleOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
				String OrderLineKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("#####OrderLineKey#####" + OrderLineKey + "######DeliveryMethod######"+ DeliveryMethod);				 
				}
				
				if (DeliveryMethod.equals(KohlsConstant.V_PICK)) {
				 
					double LineOrigOrdQty =  Double.parseDouble(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORIG_ORDERED_QUANTITY));
				    Element eleOrderStatuses = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES);
				    NodeList nodeOrderStatusList = eleOrderStatuses.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUS);
	
				    for (int j=0; j<nodeOrderStatusList.getLength(); j++ ){
						
				    	Element eleOrderStatus = (Element) nodeOrderStatusList.item(j);
			    		String Status = eleOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS);
				    		
			    		if(Status.equals(KohlsConstant.V_CANCEL_STATUS)) {				
			    			Document docShipmentLineListIn = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
			    			Element eleShipmentLineList = docShipmentLineListIn.getDocumentElement();
			    			eleShipmentLineList.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, OrderNo);
			    			eleShipmentLineList.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, OrderLineKey);
			    			eleShipmentLineList.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.V_KOHLS_COM);
						
			    			Document docShipmentLineListOutput = 
			    				KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_GET_SHIPMENT_LIST_FOR_ORDER, docShipmentLineListIn);
			    			
			    			if(YFCLogUtil.isDebugEnabled()){
			    				log.debug("######### getShipmentListForOrder API Output  ##########"+ XMLUtil.getXMLString(docShipmentLineListOutput));				 
			    			}
							
			    			Element eleShipmentList = docShipmentLineListOutput.getDocumentElement();
				    		Element eleShipment = SCXmlUtil.getChildElement(eleShipmentList, KohlsXMLLiterals.E_SHIPMENT);
					
				    		Document docShipStoreEventIn = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT_STORE_EVENTS);
				    			Element eleShiptStoreEventIn = docShipStoreEventIn.getDocumentElement();
					
				    		eleShiptStoreEventIn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY));
				    		eleShiptStoreEventIn.setAttribute(KohlsXMLLiterals.A_EVENT_TYPE, KohlsConstant.V_SHORT_PICK);
							
				    		Document docShipStoreEventList = 
				    			KohlsCommonUtil.invokeService( env, KohlsConstant.SERVICE_SHIPMENT_STORE_EVENTS, docShipStoreEventIn);

			    			if(YFCLogUtil.isDebugEnabled()){
			    				log.debug("######### KOHLS_ShipmentStoreEvents Service Output  ##########"+ XMLUtil.getXMLString(docShipStoreEventList));				 
			    			}
				    		
				    		Element eleShipStoreEventList = docShipStoreEventList.getDocumentElement();
							int Quantity = 0;
							String ReasonCode = ""; 	
							String ReasonText = "";	
							String ModifyUserId = "";
							String Modifyts = "";
							NodeList nodeShipStoreEventList = eleShipStoreEventList.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_STORE_EVENTS);
							for (int k=0; k < nodeShipStoreEventList.getLength(); k++) {
								Element eleShipStoreEvents = (Element) nodeShipStoreEventList.item(k);
								Quantity += Integer.parseInt(eleShipStoreEvents.getAttribute(KohlsXMLLiterals.A_QUANTITY));
								if (!(ReasonCode.equals(eleShipStoreEvents.getAttribute(KohlsXMLLiterals.A_REASON_CODE))))
									ReasonCode = ReasonCode + eleShipStoreEvents.getAttribute(KohlsXMLLiterals.A_REASON_CODE);
									ReasonText = eleShipStoreEvents.getAttribute(KohlsXMLLiterals.A_REASON_TEXT);
									ModifyUserId = eleShipStoreEvents.getAttribute(KohlsXMLLiterals.A_MODIFY_USERID);
									Modifyts = eleShipStoreEvents.getAttribute(KohlsXMLLiterals.A_MODIFY_TS);
								}	
								Document docOrderAuditList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER_AUDIT_LIST);
								Element eleOrderAuditList = docOrderAuditList.getDocumentElement();
								Element eleOrderAudit = SCXmlUtil.createChild(eleOrderAuditList, KohlsXMLLiterals.E_ORDER_AUDIT);
								double LineNewQty = LineOrigOrdQty - Quantity;
								eleOrderAudit.setAttribute(KohlsXMLLiterals.A_REASON_CODE, ReasonCode);
								eleOrderAudit.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, ReasonText);
								eleOrderAudit.setAttribute(KohlsXMLLiterals.A_MODIFY_USERID, ModifyUserId);
								eleOrderAudit.setAttribute(KohlsXMLLiterals.A_MODIFY_TS, Modifyts);
								eleOrderAudit.setAttribute(KohlsXMLLiterals.A_EVENT_TYPE, KohlsConstant.V_SHORT_PICK);
								eleOrderAudit.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY_OV, Double.toString(LineOrigOrdQty));
								eleOrderAudit.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY_NV, Double.toString(LineNewQty));
								eleOrderAudit.setAttribute(KohlsXMLLiterals.A_MODIFICATION_TYPE, KohlsConstant.V_CANCEL);
								Document docOrderStatusInput = eleOrderStatus.getOwnerDocument();
								Element eleOrdStatusAudit = (Element) docOrderStatusInput.importNode(eleOrderAuditList, true);		
								eleOrderStatus.appendChild(eleOrdStatusAudit);
									
							}
						}
					
				    }
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				throw new YFSException("Exception in method getOrderDetails_ShipmentStoreEvents() : "+ex.getStackTrace());
			}
			return docOrderDetails;
		}
}