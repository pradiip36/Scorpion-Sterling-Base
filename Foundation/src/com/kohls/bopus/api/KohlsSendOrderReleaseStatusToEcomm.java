package com.kohls.bopus.api;

/*****************************************************************************
 * File Name    : KohlsSendOrderReleaseStatusToEcomm.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.001 Apr 14,2014    Naveen                Initial Version 
 * ---------------------------------------------------------------------------
 *****************************************************************************/

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

public class KohlsSendOrderReleaseStatusToEcomm implements YIFCustomApi{

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsSendOrderReleaseStatusToEcomm.class.getName());
	private YIFApi api;
	
	public Document sendReleaseStatusUpdateToEcomm(YFSEnvironment env, Document orderRelease) throws Exception
	{
		log.debug("KohlsSendOrderReleaseStatusToEcomm: inXML:"+SCXmlUtil.getString(orderRelease));
		
		/****Input to change order to resolve hold
		 * <OrderRelease DocumentType="0001" OrderDate="2014-03-07T02:33:33-05:00" OrderHeaderKey="20140411171304188593" ShipNode="109">
				<Order DocumentType="0001" EnterpriseCode="KOHLS.COM" OrderNo="Y100001204">
					<Extn ExtnHdrGiftReceiptID=""/>
				</Order>
				<OrderLines>
					<OrderLine CarrierServiceCode="Standard Ground" OrderedQty="2.00" PrimeLineNo="2" SCAC="" SubLineNo="1">
						<Item ItemID="65656565" UnitOfMeasure="EACH"/>
						<Extn ExtnOCF="BPS"/>
						<Shipnode Description="Oak Creek" ShipNode="109">
							<ShipNodePersonInfo AddressLine1="9035 S Howell Ave" AddressLine2="" AddressLine3="" City="Oak Creek" Country="US" State="WI" ZipCode="53154"/>
						</Shipnode>
						<OrderStatuses>
							<OrderStatus ShipNode="109" Status="3200" StatusDate="2014-04-11T17:13:38-04:00" StatusDescription="Released" StatusQty="2.00"/>
						</OrderStatuses>
					</OrderLine>
					<OrderLine CarrierServiceCode="Standard Ground" OrderedQty="2.00" PrimeLineNo="3" SCAC="" SubLineNo="1">
						<Item ItemID="65656565" UnitOfMeasure="EACH"/>
						<Extn ExtnOCF="BPS"/>
						<Shipnode Description="Oak Creek" ShipNode="109">
							<ShipNodePersonInfo AddressLine1="9035 S Howell Ave" AddressLine2="" AddressLine3="" City="Oak Creek" Country="US" State="WI" ZipCode="53154"/>
						</Shipnode>
						<OrderStatuses>
							<OrderStatus ShipNode="109" Status="3200" StatusDate="2014-04-11T17:13:38-04:00" StatusDescription="Released" StatusQty="2.00"/>
						</OrderStatuses>
					</OrderLine>
				</OrderLines>
			</OrderRelease>
		 *  
		 ******/
		
		Element eleOrderRelease =  orderRelease.getDocumentElement();
		
		String strOrderHdKey = eleOrderRelease.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY) ;
		String strDeliveryMethod = eleOrderRelease.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD) ;
		String sShipNode = eleOrderRelease.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
		
		//Checking if the release belongs to BOPUS order line or not
		log.debug("Order Release: DeliveryMethod:"+strDeliveryMethod);
		if(strDeliveryMethod.equals(KohlsConstant.PICK))
		{
			log.debug("OrderLine belongs to BOPUS");
			log.debug("Creating Release message to Ecommerce");
			//Creating Release message to Ecommerce 
			Document docOrderStatusUpdate = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ORDER);
			Element eleOrderStatusUpdate = docOrderStatusUpdate.getDocumentElement();
			
			eleOrderStatusUpdate.setAttribute(KohlsXMLLiterals.A_ORDERNO, SCXmlUtil.getXpathAttribute(eleOrderRelease, KohlsXMLLiterals.E_ORDER+"/@"+KohlsXMLLiterals.A_ORDERNO));
			eleOrderStatusUpdate.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, SCXmlUtil.getXpathAttribute(eleOrderRelease, KohlsXMLLiterals.E_ORDER+"/@"+KohlsXMLLiterals.A_DOCUMENTTYPE));
			eleOrderStatusUpdate.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, SCXmlUtil.getXpathAttribute(eleOrderRelease, KohlsXMLLiterals.E_ORDER+"/@"+KohlsXMLLiterals.A_ENTERPRISE_CODE));
			
			Element eleOrNotes = SCXmlUtil.getXpathElement(eleOrderRelease, KohlsXMLLiterals.E_ORDER+"/"+KohlsXMLLiterals.E_NOTES);
			
			Element eleOrderLinesStatUpdt = SCXmlUtil.createChild(eleOrderStatusUpdate, KohlsXMLLiterals.E_ORDER_LINES);
			Element eleOrderLineStatUpdt = null;
			NodeList orderLineReleaseNL = SCXmlUtil.getXpathNodes(eleOrderRelease, KohlsXMLLiterals.E_ORDER_LINES+"/"+KohlsXMLLiterals.E_ORDER_LINE);
			Element eleOrderLineRelease = null;
			
			for(int i= 0; i<orderLineReleaseNL.getLength(); i++)
			{
				eleOrderLineRelease =  (Element) orderLineReleaseNL.item(i);
				eleOrderLineStatUpdt = SCXmlUtil.createChild(eleOrderLinesStatUpdt, KohlsXMLLiterals.E_ORDER_LINE);
				eleOrderLineStatUpdt.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, eleOrderLineRelease.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE));
				eleOrderLineStatUpdt.setAttribute(KohlsXMLLiterals.ITEM_ID, SCXmlUtil.getXpathAttribute(eleOrderLineRelease, KohlsXMLLiterals.E_ITEM+"/@"+KohlsXMLLiterals.ITEM_ID ));
				eleOrderLineStatUpdt.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY , eleOrderLineRelease.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
				eleOrderLineStatUpdt.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO , eleOrderLineRelease.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
				eleOrderLineStatUpdt.setAttribute(KohlsXMLLiterals.A_QUANTITY , eleOrderLineRelease.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
				eleOrderLineStatUpdt.setAttribute(KohlsXMLLiterals.A_UOM, SCXmlUtil.getXpathAttribute(eleOrderLineRelease, KohlsXMLLiterals.E_ITEM+"/@"+KohlsXMLLiterals.A_UOM ));
				eleOrderLineStatUpdt.setAttribute(KohlsXMLLiterals.A_SCAC , eleOrderLineRelease.getAttribute(KohlsXMLLiterals.A_SCAC));
				eleOrderLineStatUpdt.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO , eleOrderLineRelease.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
				
				//Commented by Naveen. Notes are not updated on Order yet. So getting strPreEnReceiptId from transaction
				/*
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
					
					String strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC+KohlsConstant.BPS+"_"+sShipNode);
				
					String strPreEnReceiptId = (String) env.getTxnObject(KohlsXMLLiterals.A_EXTN_PREENC_HDR_RECP_ID+strOrderHdKey);
					log.debug("strPreEnReceiptId  obtained is" +strPreEnReceiptId);
					eleOrderLineStatUpdt.setAttribute(KohlsXMLLiterals.A_RECEIPT_ID, strPreEnReceiptId);
				}
				*/
				
				String strPreEnReceiptId = (String) env.getTxnObject(KohlsXMLLiterals.A_EXTN_PREENC_HDR_RECP_ID+strOrderHdKey);
				log.debug("strPreEnReceiptId  obtained is" +strPreEnReceiptId);
				eleOrderLineStatUpdt.setAttribute(KohlsXMLLiterals.A_RECEIPT_ID, strPreEnReceiptId);
				
				SCXmlUtil.importElement(eleOrderLineStatUpdt, SCXmlUtil.getChildElement(eleOrderLineRelease, KohlsXMLLiterals.E_ORDER_STATUSES));
				SCXmlUtil.importElement(eleOrderLineStatUpdt, SCXmlUtil.getChildElement(eleOrderLineRelease, KohlsXMLLiterals.E_SHIPNODE));
			}
			
			log.debug("KohlsSendOrderReleaseStatusToEcomm: Release Message to Ecomm:"+SCXmlUtil.getString(docOrderStatusUpdate));
			log.debug("Calling KohlsSendReleaseStatusToEcommSyncService service. To drop the release message into EComm queue");
			KohlsCommonUtil.invokeService(env, KohlsConstant.SERVICE_KOHLS_SEND_RELEASE_STATUS_TO_ECOMM, docOrderStatusUpdate);
		}
		log.debug("KohlsSendOrderReleaseStatusToEcomm: OutXml:"+SCXmlUtil.getString(orderRelease));
		return orderRelease;
		
	}
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
