/*****************************************************************************
 * File Name    : KohlsBeforeCreateOrderUE.java
 *
 * Description  : This class is called BeforeCreateOrderUE
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Oct 4,2010    Sudeepth Bollu         Initial Version
 * 0.00b Oct 5,2010    Sudeepth Bollu		  Dependency among gift wrap lines is replaced with stamping of PackListType value
 * 0.00c Oct 23,2010   Sudeepth Bollu		  PackListType value for Gift Lines and Ship Alone lines is stamped by Ecommerce. So, its logic is removed from the code.
 * 											  GiftWrap line's PrimeLineNo, ItemID are stamped as ExtnGiftPrimeLineNo, ExtnGiftItemID fields on the corresponding order lines to use them during line status updates to Ecommerce.
 * 0.00d Jan 16,2014   Juned S		          1. Update FulfillmentType with "STORE_PICKUP" for BOPUS lines.
											  2. Update ExtnOCF with "BPS" for BOPUS lines.
											  3. Update /OrderLine/OrderLineSourcingCntrl/@Node with OrderLine/@ShipNode and /OrderLine/OrderLineSourcingCntrl/@SuppressNodeCapacity to "Y".
										      4.Update PackListType with the ItemType to distinguish order line with Jewelry Items

 * ---------------------------------------------------------------------------
 *****************************************************************************/

package com.kohls.oms.ue;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.XPathUtil;
import com.kohls.common.util.KohlsCommonAPIUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsIdentifyPromiseType;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfc.util.YFCDateUtils;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSBeforeCreateOrderUE;
import com.yantra.shared.ycp.YFSContext;

/**
 * This class will a. add the gift wrap information at header charge, if there
 * are more than two lines with the same gift wrap code. b. add the gift wrap
 * information as a line charge, if there are is a gift line and a gift wrap
 * line. c. remove the gift wrap item line.
 */
public class KohlsBeforeCreateOrderUE implements YFSBeforeCreateOrderUE {

	String propFileName;

	Properties propConst;

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsBeforeCreateOrderUE.class.getName());

	private YIFApi api;

	/**
	 * Constructor for the class This loads the CONSTANTS.properties file.
	 * CONSTANTS.properties contains the names for GiftWrapCategory and
	 * GiftWrapCharge values which are used in this class.
	 * 
	 * @throws YIFClientCreationException
	 */
	public KohlsBeforeCreateOrderUE() throws YIFClientCreationException {
		api = YIFClientFactory.getInstance().getApi();
	}

	/**
	 * @param env
	 *            YFS Environment
	 * @param inXML
	 *            Input XML received from ECommerce
	 * @return Document with updated Elements and field values
	 * @throws YFSUserExitException
	 *             Exception from the User Exit
	 */

	public Document beforeCreateOrder(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {

		HashMap hm = this.createMap(inXML, env);
		Set<Map.Entry> set = hm.entrySet();
		for (Map.Entry mapGrp : set) {
			ArrayList<Element> alGrpCode = (ArrayList) mapGrp.getValue();

			if (alGrpCode.size() > 2) {
				this.setHeaderGiftCharge(alGrpCode, mapGrp, inXML);
			}

			if (alGrpCode.size() == 2) {
				this.setLineGiftCharge(alGrpCode, inXML, (String) mapGrp
						.getKey());
			}

			if (alGrpCode.size() == 1) {
				Element elemOL = (Element) alGrpCode.get(0);
				if (elemOL.getAttribute("GiftWrap").equals("Y")) {
					String strGrpCode = (String) mapGrp.getKey();
					YFSUserExitException exception = new YFSUserExitException();
					exception.setErrorCode("ERR0001");
					exception
							.setErrorDescription("Gift Wrap line exists and the corresponding Gift Item line is not present. Please verify the input XML for Group Code:");
					throw exception;
				}
			}

			/*
			 * Added For Release B to fetch Authorization Expiration Period
			 * based on the Credit card type configured in the Common Codes
			 */}

		// Start - Added for SF Case # 00371318 -- OASIS_SUPPORT 30/1/2012
		Element eleOrder = inXML.getDocumentElement();
		String strOrderDate = eleOrder
				.getAttribute(KohlsXMLLiterals.A_ORDER_DATE);
		YFCDate orderDate = YFCDate.getYFCDate(strOrderDate);
		YFCDateUtils.addHours(orderDate, 24 * 90);
		YFCDate yfcAuthExpryDate = orderDate;
		// End - Added for SF Case # 00371318 -- OASIS_SUPPORT 30/1/2012
		// Start -- Added for 91163,379,000 -- OASIS_SUPPORT 01/06/2012 //
		try {
			// Check if common code is set as Y
			
			if (this.isEnableRedPackListType(env)) {

				NodeList orderLineList = eleOrder
						.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
				int numOrderLines = orderLineList.getLength();
				for (int i = 0; i < numOrderLines; i++) {
					Element eleOrderLine = (Element) orderLineList.item(i);
					Element eleItem = (Element) eleOrderLine
							.getElementsByTagName(KohlsXMLLiterals.E_ITEM)
							.item(0);
					String strItemID = eleItem
							.getAttribute(KohlsXMLLiterals.A_ItemID);
					// get the value set on item level
					String packListType = this.isREDPackList(env, strItemID);
					// set the PackListType attribute only if we get a value
					// back
					if (packListType.length() > 0) {
						eleOrderLine.setAttribute("PackListType", packListType);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// End -- Added for 91163,379,000 -- OASIS_SUPPORT 01/06/2012 //
		
		
		// Start -- Added for Kohls-OCF -- IBM 04/06/2013
		/*
		 * * This user exit will be modified for following purpose
		 * 1.	Stamp a default carrier to the sales order
		 * 2.	Identify the Mixed Order by calling the KohlsIdentifyPromiseType util and stamp the fulfillment type for the order.
		 */
		
		Element inputElement = inXML.getDocumentElement();
		
		//Commenting Out the Stamping of the SCAC / SCAC_AND_SERVICE_VALUE as its no longer needed
		/*inputElement.setAttribute(KohlsConstants.SCAC, KohlsConstants.FEDX);
		inputElement.setAttribute(KohlsConstants.SCAC_AND_SERVICE, KohlsConstants.SCAC_AND_SERVICE_VALUE);*/
		
		
		//To identify Fulfillment Type
		try {
			Document inputDocForUtil = KohlsCommonAPIUtil.callGetItemDetails(env, inXML);
			
			Document identifyPromiseType = KohlsIdentifyPromiseType.identifyPromiseType(env, inputDocForUtil);
			
			Element promiseTypeEle = identifyPromiseType.getDocumentElement();
			String promiseType = promiseTypeEle.getAttribute(KohlsConstants.PROMISE_TYPE);

			if (YFCCommon.equals(promiseType, KohlsConstants.MIXED, false)) {
				((YFSContext) env).setUEParam(KohlsConstants.PROMISE_TYPE, promiseType);
				
				Element orderLinesEle = (Element) inXML.getElementsByTagName(KohlsConstants.ORDER_LINES).item(0);

				if(!YFCCommon.isVoid(orderLinesEle)){
				NodeList orderLines = orderLinesEle.getElementsByTagName(KohlsConstants.ORDER_LINE);
				int length = orderLines.getLength();

				for (int i = 0; i < length; i++) {
					Element orderLineEle = (Element) orderLines.item(i);
					String fulfillType = orderLineEle.getAttribute(KohlsConstants.FULFILLMENT_TYPE);
					//Condition is modified to stamp the fulfillmentType only for the eligible lines
					if(YFCCommon.isVoid(fulfillType) && isEligibleForFulfillmentTypeStamp(orderLineEle,inputDocForUtil)){
					orderLineEle.setAttribute(KohlsConstants.FULFILLMENT_TYPE, KohlsConstants.MIXED_ORDER);
					}
				}
			 }
		
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// End -- Added for Kohls-OCF -- IBM 04/06/2013
		
		NodeList ndlstpaymentMethods = inXML
				.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHOD);

		for (int i = 0; i < ndlstpaymentMethods.getLength(); i++) {

			Element elePaymentMethod = (Element) ndlstpaymentMethods.item(i);

			String sPaymentType = elePaymentMethod
					.getAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE);

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Payment Type : " + sPaymentType);
			}
			if (null != sPaymentType && !sPaymentType.equalsIgnoreCase("")) {
				if (KohlsConstant.PAYMENT_TYPE_CREDIT_CARD
						.equalsIgnoreCase(sPaymentType)) {

					String sCreditCardType = elePaymentMethod
							.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE);
					// Start - Commented for SF Case # 00371318 -- OASIS_SUPPORT
					// 30/1/2012
					// YFCDate dt = new YFCDate();
					// YFCDate yfcAuthExpryDate = dt.HIGH_DATE;
					// End - Commented for SF Case # 00371318 -- OASIS_SUPPORT
					// 30/1/2012

					// String sExpiryDuration = KohlsUtil.getCommonCodeList(env,
					// KohlsConstant.AUTH_EXP_DAYS, sCreditCardType);
					/*
					 * if(YFCLogUtil.isDebugEnabled()){ log.debug(sPaymentType +
					 * "has Authorization Expiration period of " +
					 * sExpiryDuration); }
					 */

					// if(!sCreditCardType.equalsIgnoreCase(KohlsConstant.CREDIT_CARD_AMEX)){
					// There will be only one payment Detail every payment
					// method i.e there will be only one authorizationID for the
					// order
					Element elePaymentMethodDetail = (Element) elePaymentMethod
							.getElementsByTagName(
									KohlsXMLLiterals.E_PAYMENT_DETAILS).item(0);
					if (KohlsConstant.CHARGE_TYPE_AUTH
							.equalsIgnoreCase(elePaymentMethodDetail
									.getAttribute(KohlsXMLLiterals.A_CHARGE_TYPE))) {
						// String sAuthTime =
						// elePaymentMethodDetail.getAttribute(KohlsXMLLiterals.A_AUTH_TIME);
						// String sAuthExpirationDate =
						// KohlsUtil.getNewDate(sAuthTime, sExpiryDuration);

						elePaymentMethodDetail
								.setAttribute(
										KohlsXMLLiterals.A_AUTH_EXPIRY_DATE,
										yfcAuthExpryDate
												.getString(YFCDate.ISO_DATETIME_FORMAT));
						if (YFCLogUtil.isDebugEnabled()) {
							log
									.debug("The Auth Expiration Time is "
											+ yfcAuthExpryDate
													.getString(YFCDate.ISO_DATETIME_FORMAT));
						}
						// }
					}
				}
			}
		}

		/* Added for Kohls Cash Earned Iteration 4 changes */
		// Element eleOrder = inXML.getDocumentElement();
		Element eleOrderExtn = null;
		try {
			eleOrderExtn = (Element) XPathUtil.getNode(eleOrder,
					KohlsConstant.XPATH_ORDER_EXTN);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sKCECouponEventID = eleOrderExtn
				.getAttribute(KohlsXMLLiterals.A_EXTN_KCE_COUPON_EVENT_ID);

		if (null != sKCECouponEventID
				&& !sKCECouponEventID.equalsIgnoreCase("")) {
			Element eleOrderHoldTypes = inXML
					.createElement(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
			Element eleOrderHoldType = inXML
					.createElement(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
			eleOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE,
					KohlsConstant.CASH_ACTIVATION_HOLD);
			eleOrderHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT,
					KohlsConstant.CASH_ACTIVATION_REASON);

			eleOrderHoldTypes.appendChild(eleOrderHoldType);
			eleOrder.appendChild(eleOrderHoldTypes);
		}
		/* ===========END==================== */

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("KohlsBeforeCreateOrderUE return XML \n"
					+ XMLUtil.getXMLString(inXML));
		}
		
		/***********Added by Juned S Kohls drop 2 Order Capture**************************/
		
		try {

			Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, KohlsXMLLiterals.E_ORDER_LINES);
			ArrayList<Element> eleOrderLineList = SCXmlUtil.getChildren(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
			Iterator<Element> itrOrderLine = eleOrderLineList.iterator();
			Element eleOrderLine = null;
			Element extnOrderLine = null;
			Element eleOrderLineSourcingCntrls = null;
			Element eleOrderLineSourcingCntrl = null;
			ArrayList<Element> eleOrderLineSourcingCntrlList = null;
			Iterator<Element> itrOrderLineSourcingCntr= null;
			Element eleItem = null;
			String bopus_Line_str ="";
			Element eleLinePriceInfo = null;
			while(itrOrderLine.hasNext()){
				eleOrderLine = itrOrderLine.next();
				if(!XMLUtil.isVoid(eleOrderLine)){
					if(KohlsConstant.PICK.equals(eleOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD))){
						log.debug("DELIVERY_METHOD is  Pick..");
						if(XMLUtil.isVoid(eleOrderLine.getAttribute(KohlsXMLLiterals.A_FULFILLMENT_TYPE)))
						    eleOrderLine.setAttribute(KohlsXMLLiterals.A_FULFILLMENT_TYPE, KohlsConstant.STORE_PICKUP);
						extnOrderLine = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_EXTN);
						eleOrderLineSourcingCntrls = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ORDER_LINE_SOUR_CONTRLS);
						eleItem = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ITEM);
						eleLinePriceInfo = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_LINE_PRICE_INFO);
						
						if(!XMLUtil.isVoid(extnOrderLine)){
							if(!XMLUtil.isVoid(extnOrderLine.getAttribute(KohlsXMLLiterals.A_EXTN_OCF)))
							   extnOrderLine.setAttribute(KohlsXMLLiterals.A_EXTN_OCF, KohlsConstant.BPS);
						}
						else{
							extnOrderLine = SCXmlUtil.createChild(eleOrderLine, KohlsXMLLiterals.E_EXTN);
						    extnOrderLine.setAttribute(KohlsXMLLiterals.A_EXTN_OCF, KohlsConstant.BPS);
						}  
						
						extnOrderLine.setAttribute(KohlsXMLLiterals.A_EXPIRATION_DATE, eleOrderLine.getAttribute(KohlsXMLLiterals.REQ_CANCEL_DATE));
						extnOrderLine.setAttribute(KohlsXMLLiterals.A_EXTN_EXPECTED_SHIPMENT_DATE, eleOrderLine.getAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE));
						
                        //  BOPUS - Start : Suppress the Demand update to GIV for BOPUS lines on create Order
						//  Add PrimeLineNo attributes in env varibale for BOPUS lines
						String primeLineNo = eleOrderLine.getAttribute(KohlsXMLLiterals.PRIME_LINE_NO);						
						if(!YFCCommon.isVoid(env.getTxnObject(KohlsConstant.BOPUS_LINES))){
							bopus_Line_str = (String)env.getTxnObject(KohlsConstant.BOPUS_LINES)+KohlsConstant.DELIMITER;
						}
						bopus_Line_str = bopus_Line_str+primeLineNo;
						
						if (!YFCCommon.isVoid(bopus_Line_str))
							env.setTxnObject(KohlsConstant.BOPUS_LINES, bopus_Line_str);
						
						// BOPUS - end 
						
						if(!XMLUtil.isVoid(eleLinePriceInfo))
							extnOrderLine.setAttribute(KohlsXMLLiterals.A_EXTN_UNIT_PRICE, eleLinePriceInfo.getAttribute(KohlsXMLLiterals.A_UNIT_PRICE));
						
						extnOrderLine.setAttribute(KohlsXMLLiterals.A_EXTN_ORDERED_QTY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
						
						if(!XMLUtil.isVoid(eleOrderLineSourcingCntrls)){
						
							eleOrderLineSourcingCntrlList = SCXmlUtil.getChildren(eleOrderLineSourcingCntrls, KohlsXMLLiterals.E_ORDER_LINE_SOUR_CONTRL);
							itrOrderLineSourcingCntr = eleOrderLineSourcingCntrlList.iterator();

							while(itrOrderLineSourcingCntr.hasNext()){

								eleOrderLineSourcingCntrl = itrOrderLineSourcingCntr.next();

								if(!XMLUtil.isVoid(eleOrderLineSourcingCntrl)){
									if(XMLUtil.isVoid(eleOrderLineSourcingCntrl.getAttribute(KohlsXMLLiterals.A_NODE)))
										eleOrderLineSourcingCntrl.setAttribute(KohlsXMLLiterals.A_NODE, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
									if(XMLUtil.isVoid(eleOrderLineSourcingCntrl.getAttribute(KohlsXMLLiterals.A_SUPRESS_NODE_CAPACITY)))
										eleOrderLineSourcingCntrl.setAttribute(KohlsXMLLiterals.A_SUPRESS_NODE_CAPACITY, KohlsConstant.YES);							
								}
								else{
									eleOrderLineSourcingCntrl = SCXmlUtil.createChild(eleOrderLine, KohlsXMLLiterals.E_ORDER_LINE_SOUR_CONTRL);
									eleOrderLineSourcingCntrl.setAttribute(KohlsXMLLiterals.A_NODE, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
									eleOrderLineSourcingCntrl.setAttribute(KohlsXMLLiterals.A_SUPRESS_NODE_CAPACITY, KohlsConstant.YES);
								}

							}
						}
						else{
							eleOrderLineSourcingCntrls = SCXmlUtil.createChild(eleOrderLine, KohlsXMLLiterals.E_ORDER_LINE_SOUR_CONTRLS);
							eleOrderLineSourcingCntrl = SCXmlUtil.createChild(eleOrderLineSourcingCntrls, KohlsXMLLiterals.E_ORDER_LINE_SOUR_CONTRL);
							eleOrderLineSourcingCntrl.setAttribute(KohlsXMLLiterals.A_NODE, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
							eleOrderLineSourcingCntrl.setAttribute(KohlsXMLLiterals.A_SUPRESS_NODE_CAPACITY, KohlsConstant.YES);
						}
						
						/*if(!XMLUtil.isVoid(eleItem)){
							eleOrderLine.setAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE, eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_TYPE));
						}*/
						
					}
				}
			}
			
			log.debug("inXML::"+SCXmlUtil.getString(inXML));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/***********Added by Juned S Kohls drop 2 Order Capture**************************/
		
		return inXML;
	}

	/**
	 * This method is invoked to stamp FulfillmentType for a scenario when the
	 * Order line satusfies either one of these: CarrierServiceCode = Priority
	 * OR GiftFlag=Y and Item attribute ExtnShipNodeType=RDC/STORE/RDC_STORE
	 * 
	 * @param elemOL
	 *            Element with Order Line Information
	 * @param env
	 *            The environment object
	 * @return void
	 */
	private void stampFulfillmentType(Element elemOL, YFSEnvironment env) {

		Element eExtn = (Element) elemOL.getElementsByTagName(
				KohlsConstant.A_EXTN).item(0);
		String sExtnWrapTogetherGroupCode = eExtn
				.getAttribute("ExtnWrapTogetherGroupCode");
		if (sExtnWrapTogetherGroupCode == null) {
			sExtnWrapTogetherGroupCode = "";
		}
		boolean bExtnWrapTogetherGroupCode = !""
				.equals(sExtnWrapTogetherGroupCode.trim());

		String sCarrierServiceCode = elemOL
				.getAttribute(KohlsConstant.A_CARRIER_SERVICE_CODE);
		if (sCarrierServiceCode == null) {
			sCarrierServiceCode = "";
		}

		boolean bCarrierServiceCode = sCarrierServiceCode
				.contains(KohlsConstant.CARRIER_SERVICE_CODE_PRIORITY);

		if (bCarrierServiceCode || bExtnWrapTogetherGroupCode) {
			// call getItemList only if the order line satisfies first condition
			NodeList nlItem = elemOL.getElementsByTagName("Item");
			Element elemItem = (Element) nlItem.item(0);

			YFCDocument inDoc = YFCDocument.createDocument("Item");
			YFCElement inele = inDoc.getDocumentElement();
			inele.setAttribute("ItemID", elemItem.getAttribute("ItemID"));
			inele.setAttribute("UnitOfMeasure", elemItem
					.getAttribute("UnitOfMeasure"));
			Document itemOutDoc = null;

			YFCDocument getItemListTemplate = YFCDocument
					.createDocument("ItemList");
			YFCElement eleItem = getItemListTemplate.getDocumentElement()
					.createChild("Item");
			eleItem.createChild("Extn");
			env
					.setApiTemplate("getItemList", getItemListTemplate
							.getDocument());
			try {
				itemOutDoc = api
						.invoke(env, "getItemList", inDoc.getDocument());
			} catch (YFSException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			env.clearApiTemplate("getItemList");

			Element itemList = itemOutDoc.getDocumentElement();
			NodeList nlOutItem = itemList.getElementsByTagName("Item");
			if (!YFCObject.isNull(nlOutItem)) {
				Element elemOutItem = (Element) nlOutItem.item(0);
				NodeList nlOutItemExtn = elemOutItem
						.getElementsByTagName("Extn");
				Element elemOutItemExtn = (Element) nlOutItemExtn.item(0);
				String strExtnShipNodeSource = elemOutItemExtn
						.getAttribute(KohlsConstant.ATTR_SHIPNODESOURCE);
				if (KohlsConstant.ATTR_RDC.equals(strExtnShipNodeSource)
						|| KohlsConstant.ATTR_STORE
								.equals(strExtnShipNodeSource)
						|| KohlsConstant.ATTR_RDC_STORE
								.equals(strExtnShipNodeSource)) {
					elemOL.setAttribute(KohlsConstant.ATTR_FULFILLMENT_TYPE,
							KohlsConstant.ATTR_GIFT_PRIORITY);
				}
			}
		}
	}

	/**
	 * @param inXML
	 *            Input XML with Order Information
	 * @return HashMap with Group Code as Key and OrderLine Element as ArrayList
	 *         for the corresponding Group Code as Value.
	 */
	private HashMap<String, ArrayList<Element>> createMap(Document inXML,
			YFSEnvironment env) {

		String strExtnGrpCode = null;
		Element elemOrder = null;
		NodeList nlOrderLine = null;
		HashMap<String, ArrayList<Element>> hm = null;

		try {
			hm = new HashMap<String, ArrayList<Element>>();
			elemOrder = inXML.getDocumentElement();
			nlOrderLine = elemOrder.getElementsByTagName("OrderLine");

			for (int count = 0; count < nlOrderLine.getLength(); count++) {
				ArrayList<Element> alOrderLines = null;
				Element elemOL = (Element) nlOrderLine.item(count);
				// Punit: Omni Channel fulfillment Pilot : Inserting code to
				// stamp fulfillment type .This saves another iteration through
				// order lines.
				stampFulfillmentType(elemOL, env);
				// Punit: End changes for Omni
				NodeList nlExtn = elemOL.getElementsByTagName("Extn");
				// check for Extn element
				if (nlExtn.getLength() == 0) {
					YFSException ex = new YFSException();
					ex.setErrorCode("Extn element is missing");
					ex
							.setErrorDescription("Extn element is missing in the input xml");
					throw ex;
				}
				Element elemExtn = (Element) nlExtn.item(0);

				strExtnGrpCode = elemExtn
						.getAttribute("ExtnWrapTogetherGroupCode");
				strExtnGrpCode = strExtnGrpCode.trim();
				if (!strExtnGrpCode.equalsIgnoreCase("")
						&& strExtnGrpCode != null) {
					if (hm.containsKey(strExtnGrpCode)) {
						alOrderLines = (ArrayList<Element>) hm
								.get(strExtnGrpCode);
						alOrderLines.add(elemOL);
					} else {
						alOrderLines = new ArrayList<Element>();
						alOrderLines.add(elemOL);
						hm.put(strExtnGrpCode, alOrderLines);
					}
				}
			}
		} catch (YFSException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}

	/**
	 * This method is invoked if the number for gift line for a group code is
	 * greater than 1. This method a. transfers the gift wrap charge as order
	 * header charge b. set the GiftWrap's PrimeLineNo and ItemID as Extn
	 * attributes for Order lines of the same gift warp group code
	 * 
	 * @param alGrpCode
	 * @param mapGrp
	 * @param inXML
	 */
	private void setHeaderGiftCharge(ArrayList<Element> alGrpCode,
			Map.Entry mapGrp, Document inXML) {

		Integer iIndex1 = 0;
		Integer iIndex = 0;

		Element elemOL = null;
		Element elemGWLine = null;
		Element elemNGWFirstLine = null;
		Element elemOrder = null;

		try {
			elemOrder = inXML.getDocumentElement();
			for (iIndex1 = 0; iIndex1 < alGrpCode.size(); iIndex1++) {
				elemOL = (Element) alGrpCode.get(iIndex1);
				if (elemOL.getAttribute("GiftWrap").equals("Y")) {
					iIndex = iIndex1;
					elemGWLine = (Element) alGrpCode.get(iIndex1);
				}
			}
			if (iIndex == 0) {
				elemNGWFirstLine = (Element) alGrpCode.get(1);
			} else {
				elemNGWFirstLine = (Element) alGrpCode.get(0);
			}

			NodeList nlItem = elemGWLine.getElementsByTagName("Item");
			if (nlItem.getLength() > 0) {
				Element elemItem = (Element) nlItem.item(0);

				for (int i = 0; i < alGrpCode.size(); i++) {
					Element elemOL1 = (Element) alGrpCode.get(i);

					NodeList nlExtn = elemOL1.getElementsByTagName("Extn");
					Element elemExtn = (Element) nlExtn.item(0);

					elemExtn.setAttribute(
							KohlsXMLLiterals.A_EXTN_GIFT_WRAP_LINE_NO,
							elemGWLine.getAttribute("PrimeLineNo"));
					elemExtn.setAttribute("ExtnGiftItemID", elemItem
							.getAttribute("ItemID"));
				}
			}
			NodeList nlLinePrice = elemGWLine
					.getElementsByTagName("LinePriceInfo");
			Element elemLineCharge = (Element) nlLinePrice.item(0);
			String strLinePrice = elemLineCharge.getAttribute("UnitPrice");

			Element elemHCharge = inXML.createElement("HeaderCharge");
			NodeList nlHeaderCharges = elemOrder
					.getElementsByTagName("HeaderCharges");
			Element elemHC = (Element) nlHeaderCharges.item(0);
			if (elemHC == null) {
				elemHC = inXML.createElement("HeaderCharges");
			}
			elemHC.appendChild(elemHCharge);
			elemOrder.appendChild(elemHC);

			elemHCharge.setAttribute("ChargeCategory",
					KohlsConstant.GiftChargeCategory);
			elemHCharge.setAttribute("ChargeName", (String) mapGrp.getKey());
			elemHCharge.setAttribute("ChargeAmount", strLinePrice);
			elemHCharge.setAttribute("Reference", (String) mapGrp.getKey());
			// add gift wrap tax at header
			addHeaderTax(elemOrder, elemGWLine, mapGrp, inXML);

			elemGWLine.getParentNode().removeChild((Node) elemGWLine);

		} catch (Exception e) {
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Exception in Header Gift Wrap Charges:"
						+ e.getStackTrace());
			}
		}
	}

	private void addHeaderTax(Element elemOrder, Element elemGWLine,
			Entry mapGrp, Document inXML) {
		Element elemLineTaxes = (Element) elemGWLine.getElementsByTagName(
				KohlsXMLLiterals.E_LINE_TAXES).item(0);
		if (elemLineTaxes == null) {
			return;
		}
		NodeList nlLineTax = elemLineTaxes
				.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAX);
		if (nlLineTax == null) {
			return;
		}

		Element elemHeaderTaxes = (Element) elemOrder.getElementsByTagName(
				KohlsXMLLiterals.E_HEADER_TAXES).item(0);
		if (elemHeaderTaxes == null) {
			elemHeaderTaxes = inXML
					.createElement(KohlsXMLLiterals.E_HEADER_TAXES);
			elemOrder.appendChild(elemHeaderTaxes);
		}
		for (int i = 0; i < nlLineTax.getLength(); i++) {
			Element elemLineTax = (Element) nlLineTax.item(i);
			Element elemHeaderTax = inXML
					.createElement(KohlsXMLLiterals.E_HEADER_TAX);
			elemHeaderTax.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY,
					KohlsConstant.GiftTaxCategory);
			elemHeaderTax.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME,
					(String) mapGrp.getKey());
			elemHeaderTax.setAttribute(KohlsXMLLiterals.A_TAX, elemLineTax
					.getAttribute(KohlsXMLLiterals.A_TAX));
			elemHeaderTax.setAttribute(KohlsXMLLiterals.A_TAX_NAME, elemLineTax
					.getAttribute(KohlsXMLLiterals.A_TAX_NAME));
			elemHeaderTax
					.setAttribute(
							KohlsXMLLiterals.A_TAX_PERCENTAGE,
							elemLineTax
									.getAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE));
			elemHeaderTax.setAttribute(KohlsXMLLiterals.A_TAXABLE_FLAG,
					elemLineTax.getAttribute(KohlsXMLLiterals.A_TAXABLE_FLAG));
			elemHeaderTaxes.appendChild(elemHeaderTax);
		}

	}

	/**
	 * This method is invoked if the number for gift line for a group code is
	 * equal to 1. This method a. transfers the gift wrap charge as order line
	 * charge for the gift item. b. set the GiftWrap's PrimeLineNo and ItemID as
	 * Extn attributes for gift wrap line.
	 * 
	 * @param alGrpCode
	 * @param inXML
	 * @param strGrpCode
	 */

	private void setLineGiftCharge(ArrayList<Element> alGrpCode,
			Document inXML, String strGrpCode) {
		Element elemGWLine = null;
		Element elemNonGWLine = null;
		Element elemFirstOL = (Element) alGrpCode.get(0);

		if (!elemFirstOL.getAttribute("GiftWrap").equals("Y")) {
			elemNonGWLine = elemFirstOL;
			elemGWLine = (Element) alGrpCode.get(1);

		} else {
			elemGWLine = elemFirstOL;
			elemNonGWLine = (Element) alGrpCode.get(1);
		}

		NodeList nlLinePrice = elemGWLine.getElementsByTagName("LinePriceInfo");
		Element elemLineCharge = (Element) nlLinePrice.item(0);
		String strLinePrice = elemLineCharge.getAttribute("UnitPrice");

		Element elemLCharge = inXML.createElement("LineCharge");
		NodeList nlLineCharges = elemNonGWLine
				.getElementsByTagName("LineCharges");
		Element elemLC = (Element) nlLineCharges.item(0);
		if (elemLC == null) {
			elemLC = elemNonGWLine.getOwnerDocument().createElement(
					"LineCharges");
			elemNonGWLine.appendChild(elemLC);
		}
		elemLC.appendChild(elemLCharge);

		elemLCharge.setAttribute("ChargeCategory",
				KohlsConstant.GiftChargeCategory);
		elemLCharge.setAttribute("ChargeName", KohlsConstant.GiftChargeName);
		elemLCharge.setAttribute("ChargePerLine", strLinePrice);

		NodeList nlExtn = elemNonGWLine.getElementsByTagName("Extn");
		Element elemExtn = (Element) nlExtn.item(0);

		NodeList nlItem = elemGWLine.getElementsByTagName("Item");
		Element elemItem = (Element) nlItem.item(0);

		elemExtn.setAttribute("ExtnGiftWrapLineNo", elemGWLine
				.getAttribute("PrimeLineNo"));
		elemExtn
				.setAttribute("ExtnGiftItemID", elemItem.getAttribute("ItemID"));
		// add GW line to Non GW line
		addLineTax(elemGWLine, elemNonGWLine, inXML, strGrpCode);
		if (elemGWLine != null) {
			elemGWLine.getParentNode().removeChild((Node) elemGWLine);
		}
	}

	private void addLineTax(Element elemGWLine, Element elemNonGWLine,
			Document inXML, String strGrpCode) {

		Element elemGWLineTaxes = (Element) elemGWLine.getElementsByTagName(
				KohlsXMLLiterals.E_LINE_TAXES).item(0);
		if (elemGWLineTaxes == null) {
			return;
		}
		NodeList nlGWLineTax = elemGWLineTaxes
				.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAX);
		if (nlGWLineTax == null) {
			return;
		}

		Element elemNonGWLineTaxes = (Element) elemNonGWLine
				.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAXES).item(0);
		if (elemNonGWLineTaxes == null) {
			elemNonGWLineTaxes = inXML
					.createElement(KohlsXMLLiterals.E_LINE_TAXES);
		}
		for (int i = 0; i < nlGWLineTax.getLength(); i++) {
			Element elemGWLineTax = (Element) nlGWLineTax.item(i);
			Element elemNonGWLineTax = inXML
					.createElement(KohlsXMLLiterals.E_LINE_TAX);
			elemNonGWLineTax.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY,
					KohlsConstant.GiftTaxCategory);
			elemNonGWLineTax.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME,
					strGrpCode);
			elemNonGWLineTax.setAttribute(KohlsXMLLiterals.A_TAX, elemGWLineTax
					.getAttribute(KohlsXMLLiterals.A_TAX));
			elemNonGWLineTax.setAttribute(KohlsXMLLiterals.A_TAX_NAME,
					elemGWLineTax.getAttribute(KohlsXMLLiterals.A_TAX_NAME));
			elemNonGWLineTax.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE,
					elemGWLineTax
							.getAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE));
			elemNonGWLineTax
					.setAttribute(
							KohlsXMLLiterals.A_TAXABLE_FLAG,
							elemGWLineTax
									.getAttribute(KohlsXMLLiterals.A_TAXABLE_FLAG));
			elemNonGWLineTaxes.appendChild(elemNonGWLineTax);
		}
	}

	// Start -- Added for 91163,379,000 -- OASIS_SUPPORT 01/06/2012 //
	/**
	 * This method determines if extn_red_pack_list_type is set for the item
	 * 
	 * @param env
	 * @param strItemID
	 * @return
	 * @throws Exception
	 */
	private String isREDPackList(YFSEnvironment env, String strItemID)
			throws Exception {

		String isREDPackList = "";

		env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, this
				.getItemListTemplate());
		Document docGetItemList = this.api.getItemList(env, this
				.getItemListInputXML(strItemID));
		env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);

		Element eleItemList = docGetItemList.getDocumentElement();
		NodeList eleItems = eleItemList
				.getElementsByTagName(KohlsXMLLiterals.E_ITEM);

		if (null != eleItems && eleItems.getLength() > 0) {

			Element eleItem = (Element) eleItems.item(0);
			Element eleItemPrimInfo = (Element) eleItem.getElementsByTagName(
					KohlsXMLLiterals.E_EXTN).item(0);
			isREDPackList = eleItemPrimInfo.getAttribute("ExtnRedPackListType");
		} else {
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Item Id " + strItemID + " does not exist.");
			}
		}
		return isREDPackList;
	}// end of isREDPackList

	/**
	 * This method builds the input xml for getItemList
	 * 
	 * @param strItemID
	 * @return
	 */
	private Document getItemListInputXML(String strItemID) {

		YFCDocument yfcDocGetItemList = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcEleGetItemList = yfcDocGetItemList.getDocumentElement();
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_ITEM_ID, strItemID);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS,
				KohlsConstant.PRODUCT_CLASS_GOOD);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_UOM,
				KohlsConstant.UNIT_OF_MEASURE);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE,
				KohlsConstant.ITEM_ORGANIZATION_CODE);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemList Input XML : "
					+ XMLUtil.getXMLString(yfcDocGetItemList.getDocument()));
		}
		return yfcDocGetItemList.getDocument();
	}// end of getItemListInputXML

	/**
	 * This method builds the template for getItemList
	 * 
	 * @return
	 */
	private Document getItemListTemplate() {

		YFCDocument yfcDocGetItemListTemp = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ITEM_LIST);
		YFCElement yfcEleListTemp = yfcDocGetItemListTemp.getDocumentElement();

		YFCElement yfcEleItemTemp = yfcDocGetItemListTemp
				.createElement(KohlsXMLLiterals.E_ITEM);
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");

		YFCElement yfcEleItemPrimaryInformationTemp = yfcDocGetItemListTemp
				.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleItemPrimaryInformationTemp
				.setAttribute("ExtnRedPackListType", "");
		yfcEleItemTemp.appendChild(yfcEleItemPrimaryInformationTemp);

		yfcEleListTemp.appendChild(yfcEleItemTemp);

		if (YFCLogUtil.isDebugEnabled()) {
			log
					.debug("getItemList Template : "
							+ XMLUtil.getXMLString(yfcDocGetItemListTemp
									.getDocument()));
		}
		return yfcDocGetItemListTemp.getDocument();
	}// end of getItemListTemplate

	/**
	 * This method determines if the common code value for ENBL_RED_PCK is set
	 * to Y
	 * 
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private boolean isEnableRedPackListType(YFSEnvironment env)
			throws Exception {

		boolean enableRedPackListType = false;
		String strEnableRedPackListType = KohlsConstant.YES;

		try {

			strEnableRedPackListType = KohlsUtil.getCommonCodeValue(env,
					"ENBL_RED_PCK");

		} catch (NullPointerException npExcp) {

			if (YFCLogUtil.isDebugEnabled()) {

				log
						.debug("Common code value is not set for code type ENBL_RED_PCK. "
								+ "Using default value of Y");
			}
		}

		if (KohlsConstant.YES.equals(strEnableRedPackListType)) {

			enableRedPackListType = true;
		}

		return enableRedPackListType;

	}// end if isEnableRedPackListType

	// End -- Added for 91163,379,000 -- OASIS_SUPPORT 01/06/2012 //
	/**
	 * @param env
	 *            YFSEnvironment
	 * @param arg
	 *            String
	 * @return null
	 * @throws YFSUserExitException
	 *             This is method is specified to implement the interface.
	 */
	public String beforeCreateOrder(YFSEnvironment env, String arg)
			throws YFSUserExitException {
		return null;
	}
	
	/*
	 * In case of Order being a "Mixed Order", the fulfillment type "Mixed Order" should be stamped 
	 * only on the orderline which has item that is NOT
	 * 1)	non-regular items from the input.
	 * 2)	nomadic items from the input
	 * 3)	ship alone items from the input 
	 */
	
	private boolean isEligibleForFulfillmentTypeStamp(Element eOrderLine, Document docItemDetailsOnOrder){
		Element itemElement = (Element) eOrderLine.getElementsByTagName(KohlsConstants.ITEM).item(0);
		String strItemIDOnOrderLine = itemElement.getAttribute(KohlsConstants.ITEM_ID);
		
		NodeList promiseLines = docItemDetailsOnOrder.getDocumentElement().getElementsByTagName(KohlsConstants.PROMISE_LINE);		
		int length = promiseLines.getLength();		
		for(int i=0;i<length;i++){
			Element promiseElement = (Element) promiseLines.item(i);			
			String strItemIDOnPromiseLine = promiseElement.getAttribute(KohlsConstants.ITEM_ID);
			
			if(strItemIDOnOrderLine.equals(strItemIDOnPromiseLine)){
				String itemType = promiseElement.getAttribute(KohlsConstants.ITEM_TYPE);
				String isNomadic = promiseElement.getAttribute(KohlsConstants.EXTN_NOMADIC);
				String isShipAlone = promiseElement.getAttribute(KohlsConstants.EXTN_SHIP_ALONE);
				String isBreakable = promiseElement.getAttribute(KohlsConstants.EXTN_BREAKABLE);
				String isCage = promiseElement.getAttribute(KohlsConstants.EXTN_CAGE);
				
				/*
				 * Changing the else condition - now only ExtnShipAlone Attribute will be while 
				 * evaluating an promise line.
				 * Previous Condiions - isShipAlone=Y and isBreakable=Y and isCage='Y'		
				 */				
				if (!itemType.equalsIgnoreCase(KohlsConstants.REGULER) 
						|| isNomadic.equalsIgnoreCase(KohlsConstants.YES)
						|| isShipAlone.equalsIgnoreCase(KohlsConstants.YES)) {
					return false;
				}
				else{
					return true;
				}
			}			
		}
		return true;
	}
}
