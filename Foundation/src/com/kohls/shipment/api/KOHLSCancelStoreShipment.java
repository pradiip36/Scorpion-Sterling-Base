package com.kohls.shipment.api;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class is called on cancelling full/partial shipment from SIM.
 * 
 * @author Punit Kumar
 * 
 * Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 */

public class KOHLSCancelStoreShipment implements YIFCustomApi {

	private YIFApi api;

	private YFCDocument dMultiApi;

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KOHLSCancelStoreShipment.class.getName());

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KOHLSCancelStoreShipment() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	public void markInventoryDirty(YFSEnvironment env, Document inXML)
			throws YFSException, SAXException, IOException {

		YFCDocument inDoc = YFCDocument.getDocumentFor(inXML);
		YFCElement inEle = inDoc.getDocumentElement();

		/***********************************************************************
		 * loop through each shipment line and determine if all lines are
		 * cancelled. If yes call change shipment with Action='Cancel',else call
		 * changeShipment with Action='Modify ' and changeShipmentStatus to move
		 * the shipment to Shipment Packed status
		 **********************************************************************/

		String orgCode = inEle
				.getAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE);
		String shipNode = inEle.getAttribute(KohlsXMLLiterals.A_SHIPNODE);

		NodeList nShipmentLine = ((Element) inXML.getElementsByTagName(
				KohlsXMLLiterals.E_SHIPMENT_LINES).item(0))
				.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
		double iTotalQty = 0.0;
		for (int i = 0; i < nShipmentLine.getLength(); i++) {
			Element eShipmentLine = (Element) nShipmentLine.item(i);
			String sQty = eShipmentLine.getAttribute("Quantity");
			log.debug("Shipment Line Qty is" + sQty);
			double iQty = Double.parseDouble(sQty);
			iTotalQty = iTotalQty + iQty;
			log.debug("TotalQty is" + iTotalQty);
		}

		if (iTotalQty > 0) {
			/**
			 * Record shortage is performed on a shipment which has eligible
			 * quantity that is containerized, hence call change shipment with
			 * Action='Modify' *
			 */
			// Call changeShipment API to backorder the open/unpacked quantity.
			// input is prepared from the client.
			log.debug("Input to changeShipment API is "
					+ XMLUtil.getXMLString(inXML));
			api.changeShipment(env, inXML);
			String strReasonCode = XMLUtil.getAttribute(inXML.getDocumentElement(), KohlsConstant.A_REASON_CODE);
			YFCDocument dChangeShipmentStatus = YFCDocument
					.createDocument(KohlsConstant.E_SHIPMENT);
			dChangeShipmentStatus.getDocumentElement().setAttribute(
					KohlsConstant.A_SHIPMENT_KEY, inEle.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
			dChangeShipmentStatus.getDocumentElement().setAttribute(
					KohlsConstant.E_BASE_DROP_STATUS,
					KohlsConstant.A_PACK_COMPLETE_BASE_DROP_STATUS);
			dChangeShipmentStatus.getDocumentElement().setAttribute(
					KohlsConstant.A_TRANSACTION_ID,
					KohlsConstant.A_SHIPMENT_PACK_TRANSACTION_ID);
			
			Element eleShipmentStatusAudit = XMLUtil.createChild(dChangeShipmentStatus.getDocument().getDocumentElement(), KohlsConstant.A_SHIPMENT_STATUS_AUDIT);
			eleShipmentStatusAudit.setAttribute(KohlsConstant.A_REASON_CODE, strReasonCode);
			eleShipmentStatusAudit.setAttribute(KohlsConstant.A_SHIPMENT_KEY, inEle.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
			
			log.debug("Input to changeShipmentStatus API is "
							+ XMLUtil.getXMLString(dChangeShipmentStatus
									.getDocument()));
			api.changeShipmentStatus(env, dChangeShipmentStatus.getDocument());
		} else {
			/**
			 * Since the entire qty on the shipment is cancelled, call
			 * changeShipment with Action = 'Cancel' *
			 */
			YFCDocument dCancelShipment = YFCDocument
			.createDocument(KohlsConstant.E_SHIPMENT);
			dCancelShipment.getDocumentElement().setAttribute(KohlsConstant.A_ACTION,"Cancel");
			dCancelShipment.getDocumentElement().setAttribute(
					KohlsConstant.A_SHIPMENT_KEY, inEle.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
			dCancelShipment.getDocumentElement().setAttribute(
					"BackOrderRemovedQuantity", KohlsConstant.V_Y);
			
			log.debug("Input to changeShipment API is for cancellation"
					+ XMLUtil.getXMLString(dCancelShipment
							.getDocument()));
			/*
			 * Start OASIS 20-DEC-2013
			 * Reason Code Population on full shipment cancel.
			 */
			
			String strReasonCode = XMLUtil.getAttribute(inXML.getDocumentElement(), KohlsConstant.A_REASON_CODE);
			Element eleShipmentStatusAudit = XMLUtil.createChild(dCancelShipment.getDocument().getDocumentElement(), KohlsConstant.A_SHIPMENT_STATUS_AUDIT);
			eleShipmentStatusAudit.setAttribute(KohlsConstant.A_REASON_CODE, strReasonCode);
			eleShipmentStatusAudit.setAttribute(KohlsConstant.A_SHIPMENT_KEY, inEle.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
			
			// End OASIS 20-DEC-2013
			api.changeShipment(env, dCancelShipment.getDocument());
		}

		// Added by Zubair - Begin

		// Once changeShipment is successfully executed mark the lines which are
		// modified with thier item-node combination as dirty
		// Node will be considered dirty till midnight of the same day.i.e. if
		// the time of marking the node dirty is 2012-07-05T13:52:36
		// it will be marked dirty till 2012-07-06T00:00:00
		// Also, the node-item combination will be marked dirty only for Stores.
		// For RDC's it is performed through SIM adjust inventory screen.

		// Added by Zubair - End
		// Added by Zubair - Begin
		String strNodeType = determineNodeType(env, shipNode);
		log.debug("Node Type is" + strNodeType);
		if (KohlsConstant.ATTR_STORE.equals(strNodeType)) {
			manageInventoryNodeControlForStores(env, inXML, orgCode, shipNode);

		}
		// Added by Zubair - end

	}

	/**
	 * This method is called for stores in order to mark the inventory dirty for
	 * classified items *
	 */
	private void manageInventoryNodeControlForStores(YFSEnvironment env,
			Document inXML, String orgCode, String shipNode)
			throws RemoteException {
		// Loop through each shipment line and determine if qty has been
		// changed.
		// if qty on the line has been changed, then mark the item on that line
		// to dirty for node shipNode determined above.
		NodeList nlLineList = ((Element) inXML.getElementsByTagName(
				KohlsXMLLiterals.E_SHIPMENT_LINES).item(0))
				.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
		HashMap hm = new HashMap();
		String itemid = "";
		String uom = "";
		String pc = "";
		for (int i = 0; i < nlLineList.getLength(); i++) {
			Element elemShipLine = (Element) nlLineList.item(i);
			String sShipmentLineNo = elemShipLine
					.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO);
			itemid = elemShipLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			uom = elemShipLine.getAttribute(KohlsXMLLiterals.A_UOM);
			pc = elemShipLine.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS);
			String sQtyToPack = elemShipLine.getAttribute("QuantityToPack");
			Double dQtyToPack = Double.valueOf(sQtyToPack);
			if (dQtyToPack > 0) {
				log.debug("map has been updated with item to mark dirty"
						+ sShipmentLineNo);
				hm.put(sShipmentLineNo, itemid);
			}
		}
		if (hm.size() >= 1) {
			log
					.debug("Atleast one item has been retrieved to mark dirty hence building multi api input to call manageInventoryNodeControl");
			Iterator it = hm.entrySet().iterator();
			String sInvPictureIncorrectDate = getInvPictureIncorrectDate(env);
			dMultiApi = YFCDocument.createDocument("MultiApi");

			while (it.hasNext()) {
				Map.Entry value = (Map.Entry) it.next();
				YFCElement eMultiApi = dMultiApi.getDocumentElement();
				YFCElement eAPI = dMultiApi.createElement("API");
				eMultiApi.appendChild(eAPI);
				eAPI.setAttribute("Name", "manageInventoryNodeControl");
				YFCElement eInput = dMultiApi.createElement("Input");
				eAPI.appendChild(eInput);
				YFCElement eInventoryNodeControl = dMultiApi
						.createElement("InventoryNodeControl");
				eInput.appendChild(eInventoryNodeControl);
				eInventoryNodeControl
						.setAttribute("InvPictureIncorrectTillDate",
								sInvPictureIncorrectDate);
				eInventoryNodeControl.setAttribute("InventoryPictureCorrect",
						"N");
				eInventoryNodeControl.setAttribute(KohlsXMLLiterals.A_ITEM_ID,
						(String) value.getValue());
				eInventoryNodeControl.setAttribute(KohlsXMLLiterals.A_UOM, uom);
				eInventoryNodeControl.setAttribute(
						KohlsXMLLiterals.A_PRODUCT_CLASS, pc);
				log.debug("ShipNode is" + shipNode);
				eInventoryNodeControl.setAttribute(KohlsXMLLiterals.A_NODE,
						shipNode);
 				/*Catalog organization code is same as inventory organization code hence using the A_CATALOG_ORG literal*/
				eInventoryNodeControl.setAttribute(
						KohlsXMLLiterals.A_ORGANIZATION_CODE, KohlsConstant.A_CATALOG_ORG);
			}
			log.debug("Input to multiApi call is " + dMultiApi.toString());
			api.multiApi(env, dMultiApi.getDocument());
		}
	}

	private String getInvPictureIncorrectDate(YFSEnvironment env) {

		Date dt;
		String sDate = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 1);

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String sFutureDate = dateFormat.format(cal.getTime());
			dt = dateFormat.parse(sFutureDate);
			sDate = dateFormat.format(dt);
			sDate = sDate + "T00:00:00";
			log.debug("Date that is set to midnight from today's date" + sDate);
			return sDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sDate;

	}

	private String determineNodeType(YFSEnvironment env, String shipNode) {

		String nodeType = "";
		try {
			YFCDocument yfcDocGetOrgList = YFCDocument
					.createDocument(KohlsXMLLiterals.E_ORGANIZATION);
			YFCElement yfcEleOrgList = yfcDocGetOrgList.getDocumentElement();
			yfcEleOrgList.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE,
					shipNode);

			YFCDocument yfcDocOrgListTemp = YFCDocument
					.createDocument(KohlsXMLLiterals.E_ORGANIZATION_LIST);
			YFCElement yfcEleOrgListTemp = yfcDocOrgListTemp
					.getDocumentElement();

			YFCElement yfcEleOrgTemp = yfcEleOrgListTemp
					.createChild(KohlsXMLLiterals.E_ORGANIZATION);
			yfcEleOrgTemp
					.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, "");

			YFCElement yfcEleNodeTypeTemp = yfcEleOrgTemp
					.createChild(KohlsXMLLiterals.A_NODE);
			yfcEleNodeTypeTemp.setAttribute(KohlsXMLLiterals.A_NODE_TYPE, "");

			log.debug("Setting Template: "
					+ XMLUtil.serialize(yfcDocOrgListTemp.getDocument()));
			env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST,
					yfcDocOrgListTemp.getDocument());

			log.debug("Invoking getOrganizationList: "
					+ XMLUtil.serialize(yfcDocGetOrgList.getDocument()));
			Document docGetOrgListEx = api.getOrganizationList(env,
					yfcDocGetOrgList.getDocument());
			env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);

			log.debug("Output of getOrganizationList: "
					+ XMLUtil.serialize(docGetOrgListEx));

			YFCDocument orgListOutput = YFCDocument
					.getDocumentFor(docGetOrgListEx);
			YFCElement orgEle = orgListOutput.getDocumentElement()
					.getChildElement(KohlsXMLLiterals.E_ORGANIZATION);
			log.debug("Inside KOHLSCancelStoreShipment : "
					+ orgEle.getNodeName());
			YFCElement nodeEle = orgEle
					.getChildElement(KohlsXMLLiterals.A_NODE);
			log.debug("Inside KOHLSCancelStoreShipment : "
					+ orgEle.getNodeName());

			nodeType = nodeEle.getAttribute(KohlsXMLLiterals.A_NODE_TYPE);
			log
					.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition Got NodeType: "
							+ nodeType);
			return nodeType;
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return nodeType;

	}

	public void setProperties(Properties arg0) throws Exception {

	}
}