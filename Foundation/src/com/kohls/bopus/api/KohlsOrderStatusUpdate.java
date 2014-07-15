package com.kohls.bopus.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsOrderStatusUpdate implements YIFCustomApi {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsOrderStatusUpdate.class.getName());
	private YIFApi api;

	public Document sendStatusUpdateToEcomm(YFSEnvironment env, Document inXML) throws Exception{
		log.debug("Status Update: inDoc:"+"KohlsBOPUSOrderStatusUpdate: inXML:"+SCXmlUtil.getString(inXML));
		
		/****Input to sendStatusUpdateToEcomm
		 *<OrderLine OrderLineKey="" PrimeLineNo="" SubLineNo="">
				<Item ItemID="" ProductClass="" UnitOfMeasure=""/>
				<Order DocumentType="" EnterpriseCode="" OrderHeaderKey="" OrderNo=""/>
				<FromStatuses>
					<OrderStatus OrderHeaderKey="" OrderLineKey="" OrderLineScheduleKey="" OrderReleaseKey="" OrderReleaseStatusKey="" PipelineKey="" ShipNode="" Status="" StatusDate="" StatusDescription="" StatusQty="" TotalQuantity="">
						<Details ExpectedDeliveryDate="" ExpectedShipmentDate="" TagNumber=""/>
					</OrderStatus>
				</FromStatuses>
				<ToStatuses>
					<OrderStatus OrderHeaderKey="" OrderLineKey="" OrderLineScheduleKey="" OrderReleaseKey="" OrderReleaseStatusKey="" PipelineKey="" ShipNode="" Status="" StatusDate="" StatusDescription="" StatusQty="" TotalQuantity="">
						<Details ExpectedDeliveryDate="" ExpectedShipmentDate="" TagNumber=""/>
					</OrderStatus>
					<OrderStatus OrderHeaderKey="" OrderLineKey="" OrderLineScheduleKey="" OrderReleaseKey="" OrderReleaseStatusKey="" PipelineKey="" ShipNode="" Status="" StatusDate="2014-03-12T16:37:55-04:00" StatusDescription="Awaiting Store Pick" StatusQty="2.00" TotalQuantity="2.00">
						<Details ExpectedDeliveryDate="" ExpectedShipmentDate="" TagNumber=""/>
					</OrderStatus>
				</ToStatuses>
			</OrderLine>
		 *
		 ******/
		
		Element inEle = inXML.getDocumentElement();
		
		boolean booleanSendStatusUpdate = false;
		
		ArrayList<Element> statusUpdatesList = new ArrayList<Element>();
		
		//Get the list of statuses for which status updates needs to be sent
		
		Document docGetCommonCodeList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		
		//Preparing common code input
		//<CommonCode CodeType="KOHLS_ORDER_STATUS" OrganizationCode="KOHLS.COM"/>
		
		Element eleReturnCodeList = docGetCommonCodeList.getDocumentElement();
		eleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_ORDER_STATUS);
		eleReturnCodeList.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, SCXmlUtil.getXpathAttribute(inEle, KohlsXMLLiterals.E_ORDER+"/@"+KohlsXMLLiterals.A_ENTERPRISE_CODE));
		
		log.debug("KohlsBOPUSOrderStatusUpdate: getCommonCodeList input:"+SCXmlUtil.getString(docGetCommonCodeList));
		
		//Calling getCommonCodeList API
		Document docGetCommonCodeOutput = KohlsCommonUtil.invokeAPI(env, KohlsConstant.API_GET_COMMON_CODE_LIST, docGetCommonCodeList);
		
		log.debug("KohlsBOPUSOrderStatusUpdate: getCommonCodeList output:"+SCXmlUtil.getString(docGetCommonCodeOutput));
	
		NodeList nlCommonCode = docGetCommonCodeOutput.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		
		//get the list of OrderStatus (OrderLine/ToStatuses/OrderStatus) from the input
		NodeList nlOrderStatuses = SCXmlUtil.getXpathNodes(inEle, KohlsXMLLiterals.E_To_STATUSES+"/"+KohlsXMLLiterals.E_ORDER_STATUS);
		Element eleOrderStatus = null;
		
		
		String strStatusQty = "";
		String strStatus = "";
		String strCodeValue = "";
		
		Element eleCommonCode = null;
		
		ArrayList<Element> OrderStatusList = new ArrayList<Element>(); 
		
		log.debug("KohlsBOPUSOrderStatusUpdate: Checking if status update needs to be sent");
		//For each order status element(OrderLine/ToStatuses/OrderStatus) in the input  check if the StatusQty = "0.00" and status is in common code list
		for(int j=0;j<nlOrderStatuses.getLength();j++){
			eleOrderStatus = (Element) nlOrderStatuses.item(j);
			
			strStatusQty = eleOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS_QTY);
			//Status Qty check
			if(!strStatusQty.equals(KohlsConstant.DECIMAL_FORMAT))
			{
				strStatus = eleOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS);
				
				//Looking Status is in the common code list
				for(int i=0;i<nlCommonCode.getLength();i++)
				{
					eleCommonCode = (Element) nlCommonCode.item(i);
					
					strCodeValue = eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE);
					
					if(strCodeValue.equals(strStatus))
					{
						//If Status code is in common code update booleanSendStatusUpdate and add the Order Status element to array
						booleanSendStatusUpdate = true;
						
						OrderStatusList.add(eleOrderStatus);	
					}	
				}					
			}
		}
				
		log.debug("KohlsBOPUSOrderStatusUpdate: status update needs to be sent is:"+booleanSendStatusUpdate);
		if(booleanSendStatusUpdate)
		{
			//Preparing input to getOrderList API
			Document indocGetOrderList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
			Element eleGetOrderList = indocGetOrderList.getDocumentElement();
			eleGetOrderList.setAttribute(KohlsConstant.ATTR_ORD_HDR_KEY, SCXmlUtil.getXpathAttribute(inEle, KohlsConstant.ELEM_ORDER+"/@"+KohlsConstant.ATTR_ORD_HDR_KEY));
			
			log.debug("KohlsBOPUSOrderStatusUpdate: getOrderList Input:"+SCXmlUtil.getString(indocGetOrderList));
			
			//calling getOrderList API
			Document outdocGetOrderList = KohlsCommonUtil.invokeAPI(env, KohlsConstant.KOHLS_GETORDERLIST_STATUSUPDATE_TEMPLATE, KohlsConstant.API_GET_ORDER_LIST, indocGetOrderList);
			
			log.debug("KohlsBOPUSOrderStatusUpdate: getOrderList output:"+SCXmlUtil.getString(outdocGetOrderList));
			
			Element eleOutGetOrderList = outdocGetOrderList.getDocumentElement();
			
			Element eleOrNotes = SCXmlUtil.getXpathElement(eleOutGetOrderList, KohlsXMLLiterals.E_ORDER+"/"+KohlsXMLLiterals.E_NOTES);
			
			NodeList NLGetOrdrListOrdrLineList = SCXmlUtil.getXpathNodes(eleOutGetOrderList, KohlsXMLLiterals.E_ORDER+"/"+KohlsXMLLiterals.E_ORDER_LINES+"/"+KohlsXMLLiterals.E_ORDER_LINE);
			
			Element eleGetOrdrListOrderLine = null;
			//Get the order line element for which status update is being sent from get order list
			for(int l=0; l<NLGetOrdrListOrdrLineList.getLength(); l++)
			{
				eleGetOrdrListOrderLine = null;
				eleGetOrdrListOrderLine = (Element) NLGetOrdrListOrdrLineList.item(l);
				
				 if(eleGetOrdrListOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY).equals(inEle.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY)))
				 {
					 break;
				 }
			}
			
			log.debug("KohlsBOPUSOrderStatusUpdate: preparing status update message");
			Document docStatusMsg = null;
			Element eleStatusMsg = null;
			Element eleOrderLines = null;
			Element eleOrderLine = null;
			Element eleOutOrderStatus = null;
			
			Element eleGetOrdrListShipNode = null;
			Element eleOrderLineStatuses = null;
			Element eleOrderLineStatus = null;
			
			for(int k=0;k<OrderStatusList.size();k++ )
			{
				eleOutOrderStatus = null;
				eleOutOrderStatus = OrderStatusList.get(k);
				docStatusMsg = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
				eleStatusMsg = docStatusMsg.getDocumentElement();
				eleStatusMsg.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, SCXmlUtil.getXpathAttribute(eleOutGetOrderList, KohlsXMLLiterals.E_ORDER+"/@"+KohlsXMLLiterals.A_DOCUMENTTYPE));
				eleStatusMsg.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, SCXmlUtil.getXpathAttribute(eleOutGetOrderList, KohlsXMLLiterals.E_ORDER+"/@"+KohlsXMLLiterals.A_ENTERPRISE_CODE));
				eleStatusMsg.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, SCXmlUtil.getXpathAttribute(eleOutGetOrderList, KohlsXMLLiterals.E_ORDER+"/@"+KohlsXMLLiterals.A_ORDER_NUMBER));
				
				String sShipNode = eleOutOrderStatus.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
				String strPreEnReceiptId = "";
				
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
					
					strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+KohlsConstant.BPS+"_"+sShipNode);
					log.debug("strPreEnReceiptId  obtained is" +strPreEnReceiptId);
				}
				
				eleOrderLines = docStatusMsg.createElement(KohlsXMLLiterals.E_ORDER_LINES);
				eleStatusMsg.appendChild(eleOrderLines);
				
				eleOrderLine = docStatusMsg.createElement(KohlsXMLLiterals.E_ORDER_LINE);
				
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, eleGetOrdrListOrderLine.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE));
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, SCXmlUtil.getXpathAttribute(inEle, KohlsXMLLiterals.E_ITEM+"/@"+KohlsXMLLiterals.A_ITEM_ID));
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QUANTITY, eleGetOrdrListOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QUANTITY));
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, eleGetOrdrListOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, eleOutOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS_QTY));
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, eleGetOrdrListOrderLine.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_UOM, SCXmlUtil.getXpathAttribute(inEle, KohlsXMLLiterals.E_ITEM+"/@"+KohlsXMLLiterals.A_UOM));
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_SCAC, eleGetOrdrListOrderLine.getAttribute(KohlsXMLLiterals.A_SCAC));
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_RECEIPT_ID, strPreEnReceiptId);
				
				eleGetOrdrListShipNode = SCXmlUtil.getChildElement(eleGetOrdrListOrderLine, KohlsXMLLiterals.E_SHIPNODE);
				
				SCXmlUtil.importElement(eleOrderLine, eleGetOrdrListShipNode);
				
				
				eleOrderLineStatuses = docStatusMsg.createElement(KohlsXMLLiterals.E_ORDER_STATUSES);
				eleOrderLineStatus = docStatusMsg.createElement(KohlsXMLLiterals.E_ORDER_STATUS);
				
				eleOrderLineStatus.setAttribute(KohlsXMLLiterals.A_SHIPNODE, eleOutOrderStatus.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
				eleOrderLineStatus.setAttribute(KohlsXMLLiterals.A_STATUS, eleOutOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS));
				eleOrderLineStatus.setAttribute(KohlsXMLLiterals.A_STATUS_DATE, eleOutOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS_DATE));
				eleOrderLineStatus.setAttribute(KohlsXMLLiterals.A_STATUS_DESCRIPTION, eleOutOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS_DESCRIPTION));
				eleOrderLineStatus.setAttribute(KohlsXMLLiterals.A_STATUS_QTY, eleOutOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS_QTY));
				
				eleOrderLineStatuses.appendChild(eleOrderLineStatus);
				eleOrderLine.appendChild(eleOrderLineStatuses);
				
				eleOrderLines.appendChild(eleOrderLine);
				
				log.debug("KohlsBOPUSOrderStatusUpdate: Calling service KohlsSendReleaseStatusToEcommSyncService: Input"+SCXmlUtil.getString(docStatusMsg));
				
				KohlsCommonUtil.invokeService(env, KohlsConstant.SERVICE_KOHLS_SEND_RELEASE_STATUS_TO_ECOMM, docStatusMsg);
			}
			
		}
		return inXML;
	}

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
