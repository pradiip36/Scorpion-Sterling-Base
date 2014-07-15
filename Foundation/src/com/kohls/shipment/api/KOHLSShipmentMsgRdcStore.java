package com.kohls.shipment.api;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KOHLSShipmentMsgRdcStore implements YIFCustomApi {

	private YIFApi api;

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KOHLSShipmentMsgRdcStore.class.getName());

	/**
	 * constructor to initialize api
	 * @throws YIFClientCreationException 
	 * @throws RemoteException 
	 * @throws YFSException 
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	//input to this class is the create shipment on success xml. the event xml has been extended.
	
	public KOHLSShipmentMsgRdcStore() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	public Document massageMsg(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, YIFClientCreationException {
		if (YFCLogUtil.isDebugEnabled()) {

			this.log
					.debug("<!-- Begining of KOHLSShipmentMsgRdcStore massageMsg method -- >"
							+ XMLUtil.getXMLString(inXML));
		}

		Element eShipment = inXML.getDocumentElement();
		String sShipNode = "";
		String strPreEnReceiptId = "";
		String sNodeType="";
		
		sShipNode = eShipment.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
		boolean isnodeRDCOrStore = false;
		Element eShipNode = (Element) eShipment.getElementsByTagName(KohlsXMLLiterals.E_SHIP_NODE).item(0);
		if(!YFCObject.isNull(eShipNode)){
			sNodeType = eShipNode.getAttribute(KohlsXMLLiterals.A_NODE_TYPE);
			isnodeRDCOrStore = KohlsConstant.ATTR_RDC.equals(sNodeType) || 
			KohlsConstant.ATTR_STORE.equals(sNodeType) || 
				KohlsConstant.ATTR_RDC_STORE.equals(sNodeType);
		}
		if(isnodeRDCOrStore){
			String sMapShipNode = KohlsUtil.getEFCForStore(sShipNode, env);
			sShipNode=sMapShipNode;
			}
		YFCDocument yfcDocOrderStatusToECommXML = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleOrderStatusToECommXML = yfcDocOrderStatusToECommXML
				.getDocumentElement();

		yfcEleOrderStatusToECommXML
				.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE,
						KohlsConstant.SO_DOCUMENT_TYPE);
		yfcEleOrderStatusToECommXML.setAttribute(
				KohlsXMLLiterals.A_ENTERPRISE_CODE, eShipment
						.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));

		YFCElement yfcEleLinesOrderStatusToECommXML = yfcDocOrderStatusToECommXML
				.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		yfcEleOrderStatusToECommXML
				.appendChild(yfcEleLinesOrderStatusToECommXML);
		NodeList ndlstShipmentLines = eShipment
				.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
		for (int i = 0; i < ndlstShipmentLines.getLength(); i++) {
			Element eleShipmentLine = (Element) ndlstShipmentLines.item(i);
			// Assuming that a shipmentLine is associated with only one
			// OrderLine
			Element eleOrderLine = (Element) eleShipmentLine
					.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE)
					.item(0);
			//Get delivery method 
			String sDeliveryMethod = eleOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD); 
			
			Element eleOrder = (Element) eleShipmentLine.getElementsByTagName(
					KohlsXMLLiterals.E_ORDER).item(0);
			yfcEleOrderStatusToECommXML.setAttribute(
					KohlsXMLLiterals.A_ORDERNO, eleOrder
							.getAttribute(KohlsXMLLiterals.A_ORDERNO));
			// get and stamp the receipt id
			Element eleOrNotes = (Element) eleOrder.getElementsByTagName(
					KohlsXMLLiterals.E_NOTES).item(0);
			String strNumOfNotes = eleOrNotes
					.getAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES);
			int inNumOfNotes = Integer.parseInt(strNumOfNotes);

			if (inNumOfNotes > 0) {
				Map mpNote = new HashMap<String, String>();
				NodeList ndOrNote = eleOrNotes
						.getElementsByTagName(KohlsXMLLiterals.E_NOTE);
				for (int j = 0; j < ndOrNote.getLength(); j++) {
					Element eleOrNote = (Element) ndOrNote.item(j);
					String strNoteReasCode = eleOrNote
							.getAttribute(KohlsXMLLiterals.A_REASON_CODE);
					String strNoteReasText = eleOrNote
							.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);
					mpNote.put(strNoteReasCode, strNoteReasText);
				}
				
				if((KohlsConstant.PICK).equalsIgnoreCase(sDeliveryMethod)){ //BOPUS line
					strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC
							+KohlsConstant.BPS+"_"+ sShipNode);
					
				}else{ //Non-BOPUS line
					strPreEnReceiptId = (String) mpNote.get(KohlsConstant.PREENC
							+ sShipNode);
				}
				
			}
			YFCElement yfcEleLineOrderStatusToECommXML = yfcDocOrderStatusToECommXML
					.createElement(KohlsXMLLiterals.E_ORDER_LINE);
			yfcEleLineOrderStatusToECommXML.setAttribute(
					KohlsXMLLiterals.A_PRIME_LINE_NO, eleOrderLine
							.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
			yfcEleLineOrderStatusToECommXML.setAttribute(
					KohlsXMLLiterals.A_SUB_LINE_NO, eleOrderLine
							.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
			yfcEleLineOrderStatusToECommXML.setAttribute(
					KohlsXMLLiterals.A_ORDERED_QTY, eleOrderLine
							.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
			yfcEleLineOrderStatusToECommXML.setAttribute(
					KohlsXMLLiterals.A_ITEM_ID, eleShipmentLine
							.getAttribute(KohlsXMLLiterals.A_ItemID));
			yfcEleLineOrderStatusToECommXML.setAttribute(
					KohlsXMLLiterals.A_QUANTITY, eleOrderLine
							.getAttribute(KohlsXMLLiterals.A_STATUS_QTY));
			yfcEleLineOrderStatusToECommXML.setAttribute(
					KohlsXMLLiterals.A_UOM, eleShipmentLine
							.getAttribute(KohlsXMLLiterals.A_UOM));
			yfcEleLineOrderStatusToECommXML.setAttribute(
					KohlsXMLLiterals.A_SCAC, eleOrderLine
							.getAttribute(KohlsXMLLiterals.A_SCAC));
			yfcEleLineOrderStatusToECommXML
					.setAttribute(
							KohlsXMLLiterals.A_CARRIER_SERVICE_CODE,
							eleOrderLine
									.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE));
			/** Iteration 4 changes */
			yfcEleLineOrderStatusToECommXML.setAttribute(
					KohlsXMLLiterals.A_RECEIPT_ID, strPreEnReceiptId);

			yfcEleLinesOrderStatusToECommXML
					.appendChild(yfcEleLineOrderStatusToECommXML);

			YFCNode yfcNdOrderStatuses = YFCDocument.getNodeFor(eleOrderLine
					.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUSES)
					.item(0));

			YFCNode yfcNdOrderStatusToEcommXML = yfcDocOrderStatusToECommXML
					.importNode(yfcNdOrderStatuses, true);
			yfcEleLineOrderStatusToECommXML
					.appendChild(yfcNdOrderStatusToEcommXML);
		}
		log.debug("Final Output" + XMLUtil.getXMLString(yfcDocOrderStatusToECommXML.getDocument()));
		return yfcDocOrderStatusToECommXML.getDocument();

	}

	public void setProperties(Properties arg0) throws Exception {

	}
}
