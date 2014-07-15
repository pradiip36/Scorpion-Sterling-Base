package com.kohls.shipment.api;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XPathUtil;
import com.kohls.inventory.api.KohlsInventoryAdjWrapperAPI;
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

/**
 * This class is called on receiving the Shipment Confirmation message from WMoS
 * 
 * @author Rohan Bhandary
 * 
 *         Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 * */

public class KohlsShipmentConfirmAPI implements YIFCustomApi {

	private YIFApi api;
	private KohlsInventoryAdjWrapperAPI inventoryAdjWrapperApi;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsShipmentConfirmAPI.class.getName());
	
	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsShipmentConfirmAPI() throws YIFClientCreationException {
		this.api = YIFClientFactory.getInstance().getLocalApi();
		inventoryAdjWrapperApi = new KohlsInventoryAdjWrapperAPI();
	}

	/**
	 * Method to create and confirm shipment
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void getCreateConfirmShipment(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("<!-- Begining of KohlsShipmentConfirmAPI getCreateConfirmShipment method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		String strPickTktNo = null;
		String strIsShipAlone = "";
		String strShipmentKey = null;
		String strShipmentNo = "";
		Element eleShipment;
		YFCDocument yfcDocGetShipment;
		YFCElement yfcEleGetShipment;
		NodeList nodeGetExtnList;
		Document docGetShipment;
		Element eleGetShipment;
		Element eleOrderRelExtn;
		NodeList nodeGetShipment;
		Element eleGetLstShipment;

		Element eleInputList = inXML.getDocumentElement();
		NodeList nodeGetShipmentLst = eleInputList
				.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);
		
		if (nodeGetShipmentLst.getLength() != 0) {

			nodeGetExtnList = eleInputList
					.getElementsByTagName(KohlsXMLLiterals.E_EXTN);
			eleOrderRelExtn = (Element) nodeGetExtnList.item(0);

			strPickTktNo = eleOrderRelExtn
					.getAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO);
			strIsShipAlone = eleOrderRelExtn
					.getAttribute(KohlsXMLLiterals.A_EXTN_IS_SHIP_ALONE);

			if (null == strPickTktNo
					|| strPickTktNo.equals(KohlsConstant.BLANK)) {
				throw new YFSException(KohlsConstant.EXTN_PICK_TICKET_ERROR);
			}
			// Start -- Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 //
			//Inventory transfer maintenance changes starts
			/*
			 * 
			 * If PickTicket starts with C or X call getOrderReleaseList and create a
			 * set with all pickticket numbers for that order. if pickticket #
			 * does not contain in the set, then call the current PIX 500 logic
			 * else call confirmShipment which will move the shipment status to
			 * 'Shipment Shipped' status.
			 * 
			 */
			if((strPickTktNo.substring(0,1).equalsIgnoreCase("C")||strPickTktNo.substring(0,1).equalsIgnoreCase("X"))&&!isPickTicketNumExist(env, inXML, strPickTktNo)){
				
				callInventoryAdjustment(env, inXML,false);
			
				return;
			}
			
		
			//Inventory transfer maintenance changes ends
			// End -- Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 //
			eleShipment = (Element) nodeGetShipmentLst.item(0);
			eleShipment.removeAttribute(KohlsXMLLiterals.A_ACTION);

			yfcDocGetShipment = YFCDocument
					.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			yfcEleGetShipment = yfcDocGetShipment.getDocumentElement();
			yfcEleGetShipment.setAttribute(KohlsXMLLiterals.A_PICK_TICKET_NO,
					strPickTktNo);

			if (YFCLogUtil.isDebugEnabled()) {
				this.log.debug("Input xml for getShipmentList in getCreateConfirmShipment:"
						+ XMLUtil.getXMLString(yfcDocGetShipment.getDocument()));
			}
			docGetShipment = this.api.getShipmentList(env,
					yfcDocGetShipment.getDocument());

			eleGetShipment = docGetShipment.getDocumentElement();
			nodeGetShipment = eleGetShipment
					.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

			eleShipment.setAttribute(KohlsXMLLiterals.A_PICK_TICKET_NO,
					strPickTktNo);

			if (strIsShipAlone.trim().equalsIgnoreCase(KohlsConstant.YES)
					&& nodeGetShipment.getLength() > 0) {

				eleGetLstShipment = (Element) nodeGetShipment.item(0);
				strShipmentKey = eleGetLstShipment
						.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
				strShipmentNo = eleGetLstShipment
						.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
				eleShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY,
						strShipmentKey);
				eleShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO,
						strShipmentNo);
				eleShipment.setAttribute(KohlsXMLLiterals.A_OVERRIDE_MOD_RULES,
						KohlsConstant.YES);
			}

			if (YFCLogUtil.isDebugEnabled()) {
				this.log.debug("Input xml for confirmShipment in getCreateConfirmShipment:"
						+ XMLUtil.getXMLString(XMLUtil
								.getDocumentForElement(eleShipment)));
			}

			this.api.confirmShipment(env,
					XMLUtil.getDocumentForElement(eleShipment));
		}


	}

	/**
	 * Method to get cancel, change release on receiving the cancellation
	 * message node
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void orderSourcingControl(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("<!-- Begining of KohlsShipmentConfirmAPI getCreateConfirmShipment method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		String strPickTktNo = null;
		String strShipmentKey = null;
		String strIsShipAlone = null;
		Element eleOrderRelExtn;
		YFCDocument yfcDocGetShipment;
		YFCElement yfcEleGetShipment;

		Document docGetShipment;
		Element eleGetShipment;

		NodeList nodeGetExtnList;

		NodeList nodeGetShipment;
		Element eleGetLstShipment;
		Element eleOrderRelease;

		Element eleInputList = inXML.getDocumentElement();
		// Start -- Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 //
        //start Change order call for TO order 
		
		NodeList nodeGetOrderRelLst = eleInputList
				.getElementsByTagName(KohlsXMLLiterals.E_ORDER_RELEASE);
		// if the order release element exists
		if (nodeGetOrderRelLst.getLength() != 0) {
			
			//start Change order call for TO order 
			
			// Element eleInputList = inXML.getDocumentElement();
			eleOrderRelease = (Element) eleInputList.getElementsByTagName(
					KohlsXMLLiterals.E_ORDER_RELEASE).item(0);
			
			eleOrderRelease = (Element) nodeGetOrderRelLst.item(0);
			
			nodeGetExtnList = eleOrderRelease
					.getElementsByTagName(KohlsXMLLiterals.E_EXTN);
			eleOrderRelExtn = (Element) nodeGetExtnList.item(0);

			strPickTktNo = eleOrderRelExtn
					.getAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO);

			if (null == strPickTktNo
					|| strPickTktNo.equals(KohlsConstant.BLANK)) {
				throw new YFSException(KohlsConstant.EXTN_PICK_TICKET_ERROR);
			}

		if(KohlsConstant.TO_DOCUMENT_TYPE.equalsIgnoreCase(eleOrderRelease.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE))){
						// isTOFlow = true;
						this.changeTOOrderSourceControl(env, inXML);
						// ends Change order call for TO order 
		}else{
			// End -- Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 //
		
			strIsShipAlone = eleOrderRelExtn
					.getAttribute(KohlsXMLLiterals.A_EXTN_IS_SHIP_ALONE);

			// cancel the shipment in case of ship alone
			if (strIsShipAlone.equalsIgnoreCase(KohlsConstant.YES)) {

				yfcDocGetShipment = YFCDocument
						.createDocument(KohlsXMLLiterals.E_SHIPMENT);
				yfcEleGetShipment = yfcDocGetShipment.getDocumentElement();
				yfcEleGetShipment.setAttribute(
						KohlsXMLLiterals.A_PICK_TICKET_NO, strPickTktNo);

				if (YFCLogUtil.isDebugEnabled()) {

					this.log.debug("Input xml for getShipmentList in orderSourcingControl:"
							+ XMLUtil.getXMLString(yfcDocGetShipment
									.getDocument()));
				}
				docGetShipment = this.api.getShipmentList(env,
						yfcDocGetShipment.getDocument());

				eleGetShipment = docGetShipment.getDocumentElement();
				nodeGetShipment = eleGetShipment
						.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

				eleGetLstShipment = (Element) nodeGetShipment.item(0);
				strShipmentKey = eleGetLstShipment
						.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);

				// create the input xml to cancel shipment
				yfcDocGetShipment = YFCDocument
						.createDocument(KohlsXMLLiterals.E_SHIPMENT);
				yfcEleGetShipment = yfcDocGetShipment.getDocumentElement();
				yfcEleGetShipment.setAttribute(KohlsXMLLiterals.A_ACTION,
						KohlsConstant.SO_CANCEL);
				yfcEleGetShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY,
						strShipmentKey);
				yfcEleGetShipment.setAttribute(
						KohlsXMLLiterals.A_OVERRIDE_MOD_RULES,
						KohlsConstant.YES);

				if (YFCLogUtil.isDebugEnabled()) {

					this.log.debug("Input xml for changeShipment in orderSourcingControl:"
							+ XMLUtil.getXMLString(yfcDocGetShipment
									.getDocument()));
				}
				this.api.changeShipment(env, yfcDocGetShipment.getDocument());

			}
			eleOrderRelease.setAttribute(KohlsXMLLiterals.A_OVERRIDE,
					KohlsConstant.YES);
			eleOrderRelease.removeChild(eleOrderRelExtn);			

			// Added to tackle the problem of multiple threading
			eleOrderRelease.setAttribute(KohlsXMLLiterals.A_SELECT_METHOD,
					KohlsConstant.SELECT_METHOD_WAIT);
			
			if (YFCLogUtil.isDebugEnabled()) {
				this.log.debug("Input xml for changeRelease in orderSourcingControl:"
						+ XMLUtil.getXMLString(XMLUtil
								.getDocumentForElement(eleOrderRelease)));
			}

			this.api.changeRelease(env,
					XMLUtil.getDocumentForElement(eleOrderRelease));
		
			// change order for the shipment
			this.changeOrderSourceControl(env, inXML);
			
		}
		}else {
			Element eleShipment = (Element)eleInputList
			.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT).item(0);
			
			Element eleShipmentExtn = (Element) eleShipment
			.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			strPickTktNo = eleShipmentExtn
			.getAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO);
		}
		
		if(!((strPickTktNo.substring(0,1).equalsIgnoreCase("C")||strPickTktNo.substring(0,1).equalsIgnoreCase("X"))&&!isPickTicketNumExist(env, inXML, strPickTktNo))){
			
			
			callInventoryAdjustment(env, inXML,true);
			
			}
		
		// End - Added for PMR 64507,379,000 (PIX 500)		
	}
/**
 * This method is used to call the adjust inventory to adjust the supply with the negative value of demand 
 * if IGNORE_PIX_500 is set to Y. This method is added for PMR 64507,379,000 (PIX 500)
 * @param env
 * @param inXML
 * @throws Exception
 */
	private void callInventoryAdjustment(YFSEnvironment env, Document inXML,boolean isPix500Flow) throws Exception{
		NodeList nlShipmentLines = inXML.getElementsByTagName("ShipmentLines");
		// iterating the ShipmentLines elements
		for(int i = 0; i < nlShipmentLines.getLength(); i++){
			Element eleShipmentLines = (Element)nlShipmentLines.item(i);
			NodeList nlShipmentLine = eleShipmentLines.getElementsByTagName("ShipmentLine");
			
			
			Element eleShipment = (Element)eleShipmentLines.getParentNode();
			
			String strShipNode = eleShipment.getAttribute("ShipNode");
			// iterating the ShipmentLine elements
			for(int j = 0; j < nlShipmentLine.getLength(); j++){
				// Getting the Item details from ShipmentLine element

				Element eleShipmentLine = (Element)nlShipmentLine.item(j);
				String strItemID = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
				String strProductClass = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS);
				String strUnitOfMeasure = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_UOM);
				String strQuantity = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_QUANTITY);
				String strCurrDate = KohlsUtil.getCurrSysDate();

				// Creating the Item details for the input to adjustInventory API
				YFCDocument yfcDocInvAdjInput = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEMS);
				YFCElement yfcEleItemTemp = yfcDocInvAdjInput.getDocumentElement();
				YFCElement yfcEleItem = yfcDocInvAdjInput.createElement(KohlsXMLLiterals.E_ITEM);
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, strItemID);
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, strProductClass);
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM, strUnitOfMeasure );
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_SHIPNODE, strShipNode );
				
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_ADJUSTMENT_TYPE, "ADJUSTMENT" );
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_AVAILABILITY, "TRACK" );
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, "DEFAULT" );
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_REASON_CODE, "INV_ADJ" );
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_REFERENCE_3, strCurrDate );

				if(strQuantity != null && ! "".equals(strQuantity.trim())){
					double dQuantity = new Double(strQuantity).doubleValue()*(-1);
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_QUANTITY, ""+dQuantity );
				}
				
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE,	KohlsConstant.SUPPLY_TYPE);
				yfcEleItemTemp.appendChild(yfcEleItem);
				
				if(isPix500Flow){
				
				// Calling the adjustInventory API if the common code IGNORE_PIX_500 is set to "Y"
				if(this.isIgnorePIX500(env)){
					
					this.inventoryAdjWrapperApi.adjustInventory(env, yfcDocInvAdjInput.getDocument());
					
				} else {
					
					if (YFCLogUtil.isDebugEnabled()) {
						log.debug("Adjustment for Item " + strItemID + " for quantity " + strQuantity + " at ship node " +
							strShipNode + " during Shipmanet Confirm has been discarded because the common code IGNORE_PIX_500 is configured as N");
					}
					
				}
				}else{
					this.inventoryAdjWrapperApi.adjustInventory(env, yfcDocInvAdjInput.getDocument());
				}
				
			}
		}
	}// End of callInventoryAdjustment

	/**
	 * This method determines if the common code value for IGNORE_PIX_500 is set to Y.
	 * This method is added for PMR 64507,379,000 (PIX 500)
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private boolean isIgnorePIX500(YFSEnvironment env) throws Exception {
		
		boolean ignorePIX500 = false;
		String strIgnorePIX500 = KohlsConstant.YES;
		
		try {
			
			strIgnorePIX500 = KohlsUtil.getCommonCodeValue(env, "IGNORE_PIX_500");
		
		} catch (NullPointerException npExcp) {
			
			if(YFCLogUtil.isDebugEnabled()) {
				
				log.debug("Common code value is not set for code type IGNORE_PIX_500. " + 
						"Using default value of Y");
			}
		}
		
		if (KohlsConstant.YES.equals(strIgnorePIX500)) {
			
			ignorePIX500 = true;
		}
		
		return ignorePIX500;		
		
	}// end if isIgnorePIX500

	
	
	
	/**
	 * Method to set getOrderDetailsTemplate
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	private Document getOrderDetailsTemplate() {

		YFCDocument yfcDocGetOrderDetailTemp = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderDetailTemp = yfcDocGetOrderDetailTemp
				.getDocumentElement();
		yfcEleGetOrderDetailTemp.setAttribute(
				KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");

		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("getOrderDetails Template : "
					+ XMLUtil.getXMLString(yfcDocGetOrderDetailTemp
							.getDocument()));
		}
		return yfcDocGetOrderDetailTemp.getDocument();
	}

	/**
	 * Method to change order on receiving the cancellation message
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	private void changeOrderSourceControl(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("<!-- Begining of KohlsShipmentConfirmAPI changeOrderSourceControl method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		Element eleOrderLine;
		YFCDocument yfcDocChangeOrder;
		YFCElement yfcEleChangeOrder;
		YFCElement yfcEleOrderLines;
		YFCElement yfcEleOrderLine;
		YFCElement yfcEleOrderSouCntrl;
		YFCElement yfcEleOrderSouCntrls;
		YFCElement yfcEleItem;
		NodeList nodeOrderLines;
		Element eleOrderRelease;
		boolean isTOFlow = false;


		Element eleInputList = inXML.getDocumentElement();
		eleOrderRelease = (Element) eleInputList.getElementsByTagName(
				KohlsXMLLiterals.E_ORDER_RELEASE).item(0);

		yfcDocChangeOrder = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcEleChangeOrder = yfcDocChangeOrder.getDocumentElement();
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE,
				eleOrderRelease.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE,
				eleOrderRelease
						.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER,
				eleOrderRelease.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER));
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE,
				KohlsConstant.YES);

		yfcEleOrderLines = yfcDocChangeOrder
				.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		yfcEleChangeOrder.appendChild(yfcEleOrderLines);

		nodeOrderLines = eleInputList
				.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES);

		Element eleOrderLines = (Element) nodeOrderLines.item(0);
		List nodeOrderLine = XMLUtil.getElementsByTagName(eleOrderLines,
				KohlsXMLLiterals.E_ORDER_LINE);

		// loop through the order lines to create input for changeOrder
		for (Iterator iterator = nodeOrderLine.iterator(); iterator.hasNext();) {
			eleOrderLine = (Element) iterator.next();

			NodeList nodeGetItem = eleOrderLine
					.getElementsByTagName(KohlsXMLLiterals.E_ITEM);

			Element eleGetItem = (Element) nodeGetItem.item(0);

			yfcEleOrderLine = yfcDocChangeOrder
					.createElement(KohlsXMLLiterals.E_ORDER_LINE);
			yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ACTION,
					KohlsConstant.ACTION_MODIFY);
			yfcEleOrderLine
					.setAttribute(
							KohlsXMLLiterals.A_PRIME_LINE_NO,
							eleOrderLine
									.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
			yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO,
					eleOrderLine.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
			yfcEleOrderLines.appendChild(yfcEleOrderLine);

			yfcEleItem = yfcDocChangeOrder
					.createElement(KohlsXMLLiterals.E_ITEM);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID,
					eleGetItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS,
					eleGetItem.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS));
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM,
					eleGetItem.getAttribute(KohlsXMLLiterals.A_UOM));
			yfcEleOrderLine.appendChild(yfcEleItem);

			yfcEleOrderSouCntrls = yfcDocChangeOrder
					.createElement(KohlsXMLLiterals.E_ORDER_LINE_SOUR_CONTRLS);

			yfcEleOrderLine.appendChild(yfcEleOrderSouCntrls);
			// add the order line source control for the order lines
			yfcEleOrderSouCntrl = yfcDocChangeOrder
					.createElement(KohlsXMLLiterals.E_ORDER_LINE_SOUR_CONTRL);
			yfcEleOrderSouCntrl.setAttribute(KohlsXMLLiterals.A_NODE,
					eleOrderRelease.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
			yfcEleOrderSouCntrl.setAttribute(KohlsXMLLiterals.A_REASON_TEXT,
					KohlsConstant.ORDER_LINE_SOUR_CNTRL_REASON);
			yfcEleOrderSouCntrl.setAttribute(
					KohlsXMLLiterals.A_SUPPRESS_SOURCING, KohlsConstant.YES);
			yfcEleOrderSouCntrls.appendChild(yfcEleOrderSouCntrl);
		}
		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("Input xml for changeOrder in changeOrderSourceControl:"
					+ XMLUtil.getXMLString(yfcDocChangeOrder.getDocument()));
		}
		
		// cancelation logic
		this.api.changeOrder(env, yfcDocChangeOrder.getDocument());

	}
	// Start -- Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 //
	/**
	 * This method is used for inventory transfer order flow this have logic to allow partial cancellation for the order
	 * while fetching the cancellation pick tick number.
	 * @param env
	 * @param inXML
	 * @throws Exception
	 */
	private void changeTOOrderSourceControl(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			this.log
					.debug("<!-- Begining of KohlsShipmentConfirmAPI changeOrderSourceControl method -- >"
							+ XMLUtil.getXMLString(inXML));
		}

		Element eleOrderLine;
		YFCDocument yfcDocChangeOrder;
		YFCElement yfcEleChangeOrder;
		YFCElement yfcEleOrderLines;
		YFCElement yfcEleOrderLine;

		YFCElement yfcEleItem;
		NodeList nodeOrderLines;
		Element eleOrderRelease;


		Element eleInputList = inXML.getDocumentElement();
		eleOrderRelease = (Element) eleInputList.getElementsByTagName(
				KohlsXMLLiterals.E_ORDER_RELEASE).item(0);

		yfcDocChangeOrder = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcEleChangeOrder = yfcDocChangeOrder.getDocumentElement();
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE,
				eleOrderRelease.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
		
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE,
				eleOrderRelease
						.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER,
				eleOrderRelease.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER));
		yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE,
				KohlsConstant.YES);
		// Start -- Added for 69773,379,000 -- OASIS_SUPPORT 5/17/2013 //
		//Ignore the pickTicket cancellation if the pickTicket does not exist in OMS
		NodeList nodeGetExtnList = eleInputList
		.getElementsByTagName(KohlsXMLLiterals.E_EXTN);
		Element eleOrderRelExtn = (Element) nodeGetExtnList.item(0);

		String strPickTktNo = eleOrderRelExtn
		.getAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO);
		if((strPickTktNo.substring(0,1).equalsIgnoreCase("C")||strPickTktNo.substring(0,1).equalsIgnoreCase("X"))&&!isPickTicketNumExist(env, inXML, strPickTktNo)){
			return;
		}
		// End -- Added for 69773,379,000 -- OASIS_SUPPORT 5/17/2013 //
		yfcEleOrderLines = yfcDocChangeOrder
				.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		yfcEleChangeOrder.appendChild(yfcEleOrderLines);

		nodeOrderLines = eleInputList
				.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES);

		Element eleOrderLines = (Element) nodeOrderLines.item(0);
		List nodeOrderLine = XMLUtil.getElementsByTagName(eleOrderLines,
				KohlsXMLLiterals.E_ORDER_LINE);
		
		//getorderlist call for orderQuantity start
		YFCDocument yfcDocGetOrderList = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderList = yfcDocGetOrderList.getDocumentElement();
		yfcEleGetOrderList.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, eleOrderRelease.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER));
		yfcEleGetOrderList.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, eleOrderRelease.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
		yfcEleGetOrderList.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleOrderRelease.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));

		env.setApiTemplate(KohlsConstant.API_GET_ORDER_LIST, getOrderListTemp());

		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("getOrderList Input XML : " + XMLUtil.getXMLString(yfcDocGetOrderList.getDocument()));
		}

		Document docGetOrderListOutput = api.getOrderList(env, yfcDocGetOrderList.getDocument());
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LIST);

		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("getOrderList output XML : " + XMLUtil.getXMLString(docGetOrderListOutput));
		}

		NodeList ndlstOrderLinelist = docGetOrderListOutput.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

		

		
		
		//getorderlist call for orderQuantity ends

		// loop through the order lines to create input for changeOrder
		for (Iterator iterator = nodeOrderLine.iterator(); iterator.hasNext();) {
			eleOrderLine = (Element) iterator.next();

			NodeList nodeGetItem = eleOrderLine
					.getElementsByTagName(KohlsXMLLiterals.E_ITEM);

			Element eleGetItem = (Element) nodeGetItem.item(0);

			yfcEleOrderLine = yfcDocChangeOrder
					.createElement(KohlsXMLLiterals.E_ORDER_LINE);
			
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ACTION,
						KohlsConstant.ACTION_MODIFY);
			
			yfcEleOrderLine
					.setAttribute(
							KohlsXMLLiterals.A_PRIME_LINE_NO,
							eleOrderLine
									.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
			yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO,
					eleOrderLine.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
			// deepak 
			
			
			//end
			yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_CHANGE_IN_ORDERED_QTY,
					eleOrderLine.getAttribute(KohlsXMLLiterals.A_CHANGE_IN_QUANTITY));
			
			yfcEleOrderLines.appendChild(yfcEleOrderLine);

			yfcEleItem = yfcDocChangeOrder
					.createElement(KohlsXMLLiterals.E_ITEM);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, eleGetItem
					.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS,
					eleGetItem.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS));
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM, eleGetItem
					.getAttribute(KohlsXMLLiterals.A_UOM));
			yfcEleOrderLine.appendChild(yfcEleItem);
			String orderdQty = getOrderedQTY(ndlstOrderLinelist,eleOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO),eleGetItem
					.getAttribute(KohlsXMLLiterals.A_ITEM_ID));


					Double iNewOrderedQty = (Double.parseDouble(orderdQty) + Double.parseDouble(eleOrderLine.getAttribute(KohlsXMLLiterals.A_CHANGE_IN_QUANTITY)));
			if(YFCLogUtil.isDebugEnabled()){			
				log.debug("new Ordered Qty : " + String.valueOf(iNewOrderedQty));
			}

			
			yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, String.valueOf(iNewOrderedQty));
					
			
		}
		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("Input xml for changeOrder in changeOrderSourceControl:"
							+ XMLUtil.getXMLString(yfcDocChangeOrder
									.getDocument()));
		}
		log.debug("KohlsShipmentConfirmAPI.changeTOOrderSourceControl()"+XMLUtil.getXMLString(yfcDocChangeOrder
		.getDocument()));
		
		
		// cancelation logic
		yfcEleChangeOrder.setAttribute("SelectMethod", "WAIT");
		this.api.changeOrder(env, yfcDocChangeOrder.getDocument());

	}

	/**
	 * This method returns the orignal ordered Quantity if primelinenumber and item ID combination exist else return blank
	 * @param ndlstOrderLinelist
	 */
	private String getOrderedQTY(NodeList ndlstOrderLinelist, String primeLineNum1,String itemID1) {
		String sOrderedQTY = "";
		for (int i=0;i<ndlstOrderLinelist.getLength();i++){			
			Element eleOrderLineList = (Element)ndlstOrderLinelist.item(i);
			
			 sOrderedQTY = eleOrderLineList.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY);
			String primeLineNum = eleOrderLineList.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
			
			NodeList nodeGetItem = eleOrderLineList
			.getElementsByTagName(KohlsXMLLiterals.E_ITEM_DETAILS);

	         Element eleGetItem = (Element) nodeGetItem.item(0);
	         String itemID = eleGetItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			
	         if(primeLineNum1.equalsIgnoreCase(primeLineNum)&& itemID1.equalsIgnoreCase(itemID)){
	        	 return sOrderedQTY;
	         }
			
		}
		return sOrderedQTY;
	}
	
	// End -- Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 //

	/**
	 * Method to create and confirm shipment
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 * @return Document outXML
	 */
	public Document kohlsAddGiftWrapLine(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("<!-- Begining of KohlsShipmentConfirmAPI kohlsAddGiftWrapLine method -- >"
					+ XMLUtil.getXMLString(inXML));
		}
		Element elemContainers = (Element) inXML.getElementsByTagName(KohlsXMLLiterals.E_CONTAINERS).item(0);
		NodeList nlContainer = elemContainers.getElementsByTagName(KohlsXMLLiterals.E_CONTAINER);
		if(nlContainer != null){
			for(int i=0; i < nlContainer.getLength(); i++ ){
				Element elemContainer = (Element)nlContainer.item(i);
				NodeList nlContainerDetail =elemContainer.getElementsByTagName(KohlsXMLLiterals.A_CONTAINER_DETAIL);
				if(nlContainerDetail != null){
					for(int j=0; j < nlContainerDetail.getLength(); j++ ){
						Element elemContainerDetail = (Element)nlContainerDetail.item(j);
						Element elemShipment = (Element) elemContainerDetail.
												getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE).item(0);
						if(elemShipment  == null){
							continue;
						}
						// set ContainerDetail qty to the shipment line within ContainerDetail
						elemShipment.setAttribute(KohlsXMLLiterals.A_QUANTITY, elemContainerDetail.getAttribute(KohlsXMLLiterals.A_QUANTITY));
						
					}
					
				}
				
			}
			
		}
		// check for virtual gift card and adding of shipment line
		Document outXML = KohlsUtil.addGiftWrapLine(env, inXML);
		return outXML;

	}

	/**
	 * Method to check if the order exists in the system
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void checkOrderExists(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("<!-- Begining of KohlsShipmentConfirmAPI checkOrderExists method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		YFCDocument yfcDocOrder;
		YFCElement yfcEleOrder;
		String strOrderNo;
		String totalRecList;
		Element eleOrderList;

		Element eleInputList = inXML.getDocumentElement();
		Element eleShipLine = (Element) eleInputList.getElementsByTagName(
				KohlsXMLLiterals.E_SHIPMENT_LINE).item(0);

		strOrderNo = eleShipLine.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);

		if (null == strOrderNo || strOrderNo.equals(KohlsConstant.BLANK)) {
			throw new YFSException(KohlsConstant.ORDER_NO_ERROR);
		}
		yfcDocOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcEleOrder = yfcDocOrder.getDocumentElement();
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, strOrderNo);

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("Input xml for getOrderList:"
					+ XMLUtil.getXMLString(inXML));
		}

		Document docOrderList = this.api.getOrderList(env,
				yfcDocOrder.getDocument());
		eleOrderList = docOrderList.getDocumentElement();

		totalRecList = eleOrderList
				.getAttribute(KohlsXMLLiterals.A_TOTAL_ORDER_LIST);

		// if record exists in the system then call the ship confirm sync
		// service else return input
		if (!totalRecList.equalsIgnoreCase("0")) {
			this.api.executeFlow(env,
					KohlsConstant.SERVICE_SHIP_CONFIRM_SYNC_SERV, inXML);
		} else {
			this.api.executeFlow(env,
					KohlsConstant.SERVICE_ALT_SHIP_CONFIRM_SYNC_SERV, inXML);
		}
	}
	
/**
 * Method to form order release list tempalate
 * @return
 */
	private Document getOrderReleaseListTemplate() {

		YFCDocument yfcDocGetOrderReleaseListTemp = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE_lIST);
		YFCElement yfcEleGetOrderReleaseListTemp = yfcDocGetOrderReleaseListTemp
				.getDocumentElement();

		YFCElement yfcDocGetOrderReleaseDetailsTemp = yfcDocGetOrderReleaseListTemp
				.createElement(KohlsXMLLiterals.E_ORDER_RELEASE);
		// YFCElement yfcEleGetOrderReleaseDetailsTemp =
		// yfcDocGetOrderReleaseDetailsTemp.getDocumentElement();

		YFCElement yfcEleExtnTemp = yfcDocGetOrderReleaseDetailsTemp
				.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO, "");

		// yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcEleExtnTemp);
		yfcEleGetOrderReleaseListTemp
				.appendChild(yfcDocGetOrderReleaseDetailsTemp);
		return yfcDocGetOrderReleaseListTemp.getDocument();

	}
/**
 * Method to form Orderreleaselist input tempalate
 * @param orderNumber
 * @return
 */
	private Document getOrderReleaseListInTemplate(String orderNumber) {

		YFCDocument yfcDocGetOrderReleaseListInTemp = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE);
		YFCElement yfcEleGetOrderReleaseListTemp = yfcDocGetOrderReleaseListInTemp
				.getDocumentElement();

		yfcEleGetOrderReleaseListTemp.setAttribute(
				KohlsXMLLiterals.A_DOCUMENTTYPE, "0006");

		yfcEleGetOrderReleaseListTemp.setAttribute(
				KohlsXMLLiterals.A_ENTERPRISE_CODE, "KOHLS.COM");

		YFCElement yfcDocOrder = yfcDocGetOrderReleaseListInTemp
				.createElement(KohlsXMLLiterals.E_ORDER);
		yfcDocOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, orderNumber);

		// YFCElement yfcEleGetOrderReleaseDetailsTemp =
		// yfcDocGetOrderReleaseDetailsTemp.getDocumentElement();

		// yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcEleExtnTemp);
		yfcEleGetOrderReleaseListTemp.appendChild(yfcDocOrder);
		return yfcDocGetOrderReleaseListInTemp.getDocument();

	}
	/**
	 * This method have logic to check If PickTicket starts with C call
	 * getOrderReleaseList and create a set with all pickticket numbers for that
	 * order. and check if pickticket # contain in the set
	 * 
	 * @param env
	 * @param inXML
	 * @param pickTicketNumber
	 * @return
	 * @throws Exception
	 */
	private boolean isPickTicketNumExist(YFSEnvironment env, Document inXML, String pickTicketNumber) throws Exception{
		String orderNum=null;
		//this.log.debug("KohlsShipmentConfirmAPI.isPickTicketNumExist(deepak start)"+XMLUtil.getXMLString(inXML));
		Set<String> pickTicketSet = new HashSet<String>();
		Element eleInputList = inXML.getDocumentElement();
		NodeList nodeGetOrderRelLst = eleInputList
		.getElementsByTagName(KohlsXMLLiterals.E_ORDER_RELEASE);
		if (nodeGetOrderRelLst.getLength()>0){
			Element eleOrderRelease = (Element) eleInputList.getElementsByTagName(
					KohlsXMLLiterals.E_ORDER_RELEASE).item(0);
			orderNum=	eleOrderRelease.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);	
		}else{
		try {
			Element eleShipmentLine = (Element) XPathUtil.getNode(inXML,	"/MergeDoc/Shipment/ShipmentLines/ShipmentLine");
			 orderNum= eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
			this.log.debug("inventory transfer maintaince(isPickTicketNumExist)"+orderNum);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		if((KohlsConstant.BLANK).equals(orderNum) || null == orderNum||orderNum==" "||orderNum.equalsIgnoreCase(" ")){
			return false;
		}
       Document yfcDocGetOrderReleaseDetails = getOrderReleaseListInTemplate(orderNum);
       
       //this.log.debug("KohlsShipmentConfirmAPI.isPickTicketNumExist(in template)"+XMLUtil.getXMLString(yfcDocGetOrderReleaseDetails));
      // this.log.debug("KohlsShipmentConfirmAPI.isPickTicketNumExist(in template)"+XMLUtil.getXMLString(yfcDocGetOrderReleaseDetails));
       
		
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_RELEASE_LIST, getOrderReleaseListTemplate());
		//this.log.debug("getOrderReleaseDetails Template : " + XMLUtil.getXMLString(this.getOrderReleaseDetailsTemplate()));
		Document docOutputGetOrderReleaseList = this.api.getOrderReleaseList(env, yfcDocGetOrderReleaseDetails);
		
		
		
		
		NodeList nlShipmentLines = docOutputGetOrderReleaseList.getElementsByTagName("OrderRelease");
		if(nlShipmentLines.getLength()>0){
		// iterating the ShipmentLines elements
		for(int i = 0; i < nlShipmentLines.getLength(); i++){
			Element eleShipmentLines = (Element)nlShipmentLines.item(i);
			NodeList nlShipmentLine = eleShipmentLines.getElementsByTagName("Extn");
			
				// iterating the ShipmentLine elements
			for(int j = 0; j < nlShipmentLine.getLength(); j++){
				// Getting the Item details from ShipmentLine element

				Element eleShipmentLine = (Element)nlShipmentLine.item(j);
				String strPickTicketNumber = eleShipmentLine.getAttribute("ExtnPickTicketNo");
				

				pickTicketSet.add(strPickTicketNumber);
				
		}
		}
		log.debug("KohlsShipmentConfirmAPI.isPickTicketNumExist(inventory transfer maintaince(isPickTicketNumExist) ends)");
		return pickTicketSet.contains(pickTicketNumber);
		}else{
			return false;
		}
	}// End of callInventoryAdjustment
	
	/**
	 * Form getOrderList Template
	 * @return
	 */
	public static Document getOrderListTemp(){

		YFCDocument yfcDocGetOrderListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDERLIST);
		YFCElement yfcEleDocGetOrderListTemp = yfcDocGetOrderListTemp.getDocumentElement();

		YFCElement yfcEleOrderTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleOrderLinesTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcEleOrderLineTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_ORDER_LINE);
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");

		YFCElement yfcEleItemDetailsTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_ITEM_DETAILS);
		yfcEleItemDetailsTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleOrderLineTemp.appendChild(yfcEleItemDetailsTemp);
		yfcEleOrderLinesTemp.appendChild(yfcEleOrderLineTemp);
		yfcEleOrderTemp.appendChild(yfcEleOrderLinesTemp);
		yfcEleDocGetOrderListTemp.appendChild(yfcEleOrderTemp);

		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("getOrderList Template : " + XMLUtil.getXMLString(yfcDocGetOrderListTemp.getDocument()));
		}
		
		return yfcDocGetOrderListTemp.getDocument();


	}


	/**
	 * @param arg0
	 *            Properties
	 * @throws Exception
	 *             e
	 */
	public void setProperties(Properties arg0) throws Exception {

	}
}
