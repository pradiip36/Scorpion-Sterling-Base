package com.kohls.oms.agent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This is a helper class for KohlsSendTOReleaseToWMoSAgent , it has utility methods which are getting used in agent class.
 * @author OASIS
 * Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 
 * Added for Inventory transfer Management .
 *
 */
public class KohlsSendTOReleaseToWMoSAgentXmls {
	
	public static Document getOrderReleaseDetailsTemplate() {

		YFCDocument yfcDocGetOrderReleaseDetailsTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE);
		YFCElement yfcEleGetOrderReleaseDetailsTemp = yfcDocGetOrderReleaseDetailsTemp.getDocumentElement();

		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_BILL_TO_ID, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_BUYER_ORGANIZATION_CODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_DIVISION, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_RECEIVING_NODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SALES_ORDER_NO, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_NAME, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_RELEASE_NO, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_REQ_DELIVERY_DATE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_REQ_SHIP_DATE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SCAC, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_SHIP_TO_KEY, "");
		yfcEleGetOrderReleaseDetailsTemp.setAttribute(KohlsXMLLiterals.A_MIN_ORDER_RELEASE_STATUS, "");
		
		/* <Extn ExtnCartonType="BOX"
        ExtnPickTicketNo="X000188823"
        ExtnRG="" ExtnShipVia="" ExtnTotalReleaseUnits="1"
        ExtnWMSInstType="" />*/
		
		YFCElement yfcEleExtnTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_CARTON_TYPE, "");
		yfcEleExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO, "");
		yfcEleExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_RG, "");
		yfcEleExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_SHIP_VIA, "");
		yfcEleExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, "");
		yfcEleExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_TO_TOTAL_RELEASE_UNITS, "");
		yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcEleExtnTemp);
		
		/*<PersonInfoShipTo AddressLine1="" AddressLine2="" AddressLine3="" 
		  AddressLine4="" AddressLine5="" AddressLine6="" AlternateEmailID="" Beeper="" 
		  City="San Bernardino" Company="" Country="US" DayFaxNo="" DayPhone="" Department="" EMailID="" EveningFaxNo="" EveningPhone="" 
		   FirstName="" JobTitle="" LastName="" MiddleName="" 
			 MobilePhone="" OtherPhone="" PersonID="" PersonInfoKey="201010201706088212" 
				 State="CA" Suffix="" Title="" ZipCode="" />*/

		YFCElement yfcElePersonInfoShipToTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO);
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_3, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_4, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_5, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_6, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ALTERNATE_EMAIL_ID, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_BEEPER, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_COMPANY, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_DAY_FAX_NO, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_DEPARTMENT, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EVENING_FAX_NO, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EVENING_PH_NO, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_JOB_TITLE, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_SUFFIX, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_MIDDLE_NAME, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_PERSON_ID, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_MOBILE_PHONE, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_OTHER_PHONE, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_CITY, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_STATE, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_COUNTRY, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EMAIL_ID, "");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_PERSONINFOKEY, "");
		
		yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcElePersonInfoShipToTemp);


		YFCElement yfcEleExtnInfoShipToTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX, "");
		yfcEleExtnInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY, "");
		yfcElePersonInfoShipToTemp.appendChild(yfcEleExtnInfoShipToTemp);

		YFCElement yfcEleOrderTemp = yfcEleGetOrderReleaseDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER);
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, "");
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "");
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_CREATE_TS, "");
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, "");
		
		yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcEleOrderTemp);

		YFCElement yfcElePaymentMethodsTemp = yfcEleOrderTemp.createChild(KohlsXMLLiterals.E_PAYMENT_METHODS);
		YFCElement yfcElePaymentMethodTemp = yfcElePaymentMethodsTemp.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, "");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO, "");
		yfcElePaymentMethodsTemp.appendChild(yfcElePaymentMethodTemp);
		yfcEleOrderTemp.appendChild(yfcElePaymentMethodsTemp);

		YFCElement yfcElePromotionsTemp = yfcEleOrderTemp.createChild(KohlsXMLLiterals.E_PROMOTIONS);
		YFCElement yfcElePromotionTemp = yfcElePromotionsTemp.createChild(KohlsXMLLiterals.E_PROMOTION);
		yfcElePromotionTemp.setAttribute(KohlsXMLLiterals.A_PROMOTION_ID, "");
		YFCElement yfcElePromotionExtnTemp = yfcElePromotionTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcElePromotionExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_PER, "");
		yfcElePromotionExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COUPON_AMNT, "");
		yfcElePromotionTemp.appendChild(yfcElePromotionExtnTemp);
		yfcElePromotionsTemp.appendChild(yfcElePromotionTemp);
		yfcEleOrderTemp.appendChild(yfcElePromotionsTemp);

		YFCElement yfcEleOrNotesTemp = yfcEleOrderTemp.createChild(KohlsXMLLiterals.E_NOTES);
		yfcEleOrNotesTemp.setAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES, "");
		YFCElement yfcEleOrNoteTemp = yfcEleOrNotesTemp.createChild(KohlsXMLLiterals.E_NOTE);
		yfcEleOrNoteTemp.setAttribute(KohlsXMLLiterals.A_REASON_CODE, "");
		yfcEleOrNoteTemp.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");
		yfcEleOrNotesTemp.appendChild(yfcEleOrNoteTemp);
		yfcEleOrderTemp.appendChild(yfcEleOrNotesTemp);

		YFCElement yfcElePersonInfoBillToTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO);
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_CITY, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_STATE, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_COUNTRY, "");
		yfcElePersonInfoBillToTemp.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");
		yfcEleOrderTemp.appendChild(yfcElePersonInfoBillToTemp);

		YFCElement yfcEleExtnOrderTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnOrderTemp.setAttribute(KohlsXMLLiterals.A_EXTN_HDR_GFT_RECP_ID, "");
		yfcEleOrderTemp.appendChild(yfcEleExtnOrderTemp);


		YFCElement yfcEleOrderLinesTemp = yfcDocGetOrderReleaseDetailsTemp.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcEleOrderLineTemp = yfcEleOrderLinesTemp.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_RECEIVING_NODE, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_SHIP_TO_ID, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_GIFT_FLAG, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_GIFT_WRAP, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDERED_QUANTITY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_OPEN_QTY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_STATUS_QUANTITY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_LINE_TYPE, "");
		yfcEleOrderLinesTemp.appendChild(yfcEleOrderLineTemp);
		yfcEleGetOrderReleaseDetailsTemp.appendChild(yfcEleOrderLinesTemp);

		YFCElement yfcEleLinePriceInfoTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_LINE_PRICE_INFO);
		yfcEleLinePriceInfoTemp.setAttribute(KohlsXMLLiterals.A_RETAIL_PRICE, "");
		yfcEleLinePriceInfoTemp.setAttribute(KohlsXMLLiterals.A_UNIT_PRICE, "");
		yfcEleOrderLineTemp.appendChild(yfcEleLinePriceInfoTemp);

		YFCElement yfcEleLineChargesTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_LINE_CHARGES);
		YFCElement yfcEleLineChargeTemp = yfcEleLineChargesTemp.createChild(KohlsXMLLiterals.E_LINE_CHARGE);
		yfcEleLineChargeTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, "");
		yfcEleLineChargeTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, "");
		yfcEleLineChargesTemp.appendChild(yfcEleLineChargeTemp);
		yfcEleOrderLineTemp.appendChild(yfcEleLineChargesTemp);

		YFCElement yfcEleLineTaxesTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_LINE_TAXES);
		YFCElement yfcEleLineTaxTemp = yfcEleLineTaxesTemp.createChild(KohlsXMLLiterals.E_LINE_TAX);
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX_NAME, "");
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, "");
		yfcEleLineTaxesTemp.appendChild(yfcEleLineTaxTemp);
		yfcEleOrderLineTemp.appendChild(yfcEleLineTaxesTemp);

		YFCElement yfcEleInstrsTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_INSTRUCTIONS);
		YFCElement yfcEleInstrTemp = yfcEleInstrsTemp.createChild(KohlsXMLLiterals.E_INSTRUCTION);
		yfcEleInstrTemp.setAttribute(KohlsXMLLiterals.A_INSTRUCTION_TYPE, "");
		yfcEleInstrTemp.setAttribute(KohlsXMLLiterals.A_INSTRUCTION_TEXT, "");
		yfcEleInstrsTemp.appendChild(yfcEleInstrTemp);
		yfcEleOrderLineTemp.appendChild(yfcEleInstrsTemp);

		YFCElement yfcEleNotesTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_NOTES);
		YFCElement yfcEleNoteTemp = yfcEleNotesTemp.createChild(KohlsXMLLiterals.E_NOTE);
		yfcEleNoteTemp.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");
		yfcEleNotesTemp.appendChild(yfcEleNoteTemp);
		yfcEleOrderLineTemp.appendChild(yfcEleNotesTemp);

		YFCElement yfcEleOrderStatuses = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_ORDER_STATUSES);
		YFCElement yfcEleOrderStatus = yfcEleOrderStatuses.createChild(KohlsXMLLiterals.E_ORDER_STATUS);
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_DATE, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_DESCRIPTION, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_QTY, "");
		yfcEleOrderStatus.setAttribute(KohlsXMLLiterals.A_STATUS_REASON, "");
		yfcEleOrderStatuses.appendChild(yfcEleOrderStatus);
		yfcEleOrderLineTemp.appendChild(yfcEleOrderStatuses);

		/*YFCElement yfcEleOrderLineExtn = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SHIP_ALONE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE, "");
		*//** Iteration 4 changes *//*
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_FROM, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_TO, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GIFT_MESSAGE, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ, "");
		yfcEleOrderLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE, "");
		yfcEleOrderLineTemp.appendChild(yfcEleOrderLineExtn);*/

		YFCElement yfcEleItemTemp = yfcEleOrderLineTemp.createChild(KohlsXMLLiterals.E_ITEM);
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_LINE, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UPC_CODE, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_DESC, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_WEIGHT, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_WEIGHT_UOM, "");
		
		yfcEleOrderLineTemp.appendChild(yfcEleItemTemp);

		return yfcDocGetOrderReleaseDetailsTemp.getDocument();

	}
	/**
	 * provides item list template
	 * @return
	 */
	public static Document getItemListTemplate() {

		YFCDocument yfcDocGetItemListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM_LIST);
		YFCElement yfcEleGetItemListTemp = yfcDocGetItemListTemp.getDocumentElement();

		YFCElement yfcEleItemTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_ITEM);
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");

		YFCElement yfcEleItemAliasListTemp = yfcEleItemTemp.createChild(KohlsXMLLiterals.A_ITEM_ALIAS_LIST);
		YFCElement yfcEleItemAliasTemp = yfcEleItemAliasListTemp.createChild(KohlsXMLLiterals.A_ITEM_ALIAS);
		yfcEleItemAliasTemp.setAttribute(KohlsXMLLiterals.A_ALIAS_NAME, "");
		yfcEleItemAliasTemp.setAttribute("AliasValue", "");
		yfcEleItemAliasListTemp.appendChild(yfcEleItemAliasTemp);
		yfcEleItemTemp.appendChild(yfcEleItemAliasListTemp);

		YFCElement yfcEleItemPrimaryInformationTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_PRIMARY_INFORMATION);
		yfcEleItemPrimaryInformationTemp.setAttribute(KohlsXMLLiterals.A_IS_HAZMAT, "");
		yfcEleItemPrimaryInformationTemp.setAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC, "");
		yfcEleItemTemp.appendChild(yfcEleItemPrimaryInformationTemp);

		YFCElement yfcEleItemExtnTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_CAGE_ITEM, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_SERVICE_SEQ, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_BAGGAGE, "");
		yfcEleItemTemp.appendChild(yfcEleItemExtnTemp);

		yfcEleGetItemListTemp.appendChild(yfcEleItemTemp);

		if (YFCLogUtil.isDebugEnabled()) {
			//log.debug("getItemList Template : " + XMLUtil.getXMLString(yfcDocGetItemListTemp.getDocument()));
		}

		return yfcDocGetItemListTemp.getDocument();
	}
	/**
	 * form a input templete for getitemList call
	 * @param env
	 * @param sItemID
	 * @return
	 */
	public static Document getItemListInputXML(YFSEnvironment env, String sItemID) {

		YFCDocument yfcDocGetItemList = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcEleGetItemList = yfcDocGetItemList.getDocumentElement();
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_ITEM_ID, sItemID);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, KohlsConstant.PRODUCT_CLASS_GOOD);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_UOM, KohlsConstant.UNIT_OF_MEASURE);

		if (YFCLogUtil.isDebugEnabled()) {
			//log.debug("getItemList Input XML : " + XMLUtil.getXMLString(yfcDocGetItemList.getDocument()));
		}
		return yfcDocGetItemList.getDocument();
	}
	/**
	 * 
	 * @param elePersonInfo
	 * @param eleOrderDtl
	 */
	public static void preparePersonInfoBillTo(Element elePersonInfo,
			Element eleOrderDtl) {
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
	}
	
	/**
	 * form confirm shipmet xml
	 * @param sOrderReleaseKey
	 * @param sOrderLineKey
	 * @param sOrderHeaderKey
	 * @param sQty
	 * @param yfcEleConfirmShipment
	 * @param yfcEleShipmentLines
	 * @param yfcEleShipmentLine
	 */
	public static void prepareConfirmShipmentXml(String sOrderReleaseKey,
			String sOrderLineKey, String sOrderHeaderKey, String sQty,
			YFCElement yfcEleConfirmShipment, YFCElement yfcEleShipmentLines,
			YFCElement yfcEleShipmentLine) {
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, sOrderLineKey);
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, sQty);
		yfcEleShipmentLines.appendChild(yfcEleShipmentLine);
		yfcEleConfirmShipment.appendChild(yfcEleShipmentLines);
	}

	
	/**
	 * Method to prepare Release xml.
	 * @param sBillToID
	 * @param sBuyerOrganizationCode
	 * @param sCarrierServiceCode
	 * @param sDeliveryMethod
	 * @param sDivision
	 * @param sDocumentType
	 * @param sEnterpriseCode
	 * @param sOrderHeaderKey
	 * @param sOrderName
	 * @param sOrderReleaseKey
	 * @param sReceivingNode
	 * @param sReleaseNo
	 * @param sSalesOrderNo
	 * @param sSellerOrganizationCode
	 * @param sShipAdviceNo
	 * @param sShipNode
	 * @param docWMoSOrderReleaseXML
	 * @return
	 */
	public static Element prepareReleaseXml(String sBillToID,
			String sBuyerOrganizationCode, String sCarrierServiceCode,
			String sDeliveryMethod, String sDivision, String sDocumentType,
			String sEnterpriseCode, String sOrderHeaderKey, String sOrderName,
			String sOrderReleaseKey, String sReceivingNode, String sReleaseNo,
			String sSalesOrderNo, String sSellerOrganizationCode,
			String sShipAdviceNo, String sShipNode,
			Document docWMoSOrderReleaseXML) {
		Element eleWMoSOrderReleaseXML = docWMoSOrderReleaseXML
				.getDocumentElement();
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_BILL_TO_ID,
				sBillToID);
		eleWMoSOrderReleaseXML.setAttribute(
				KohlsXMLLiterals.A_BUYER_ORGANIZATION_CODE,
				sBuyerOrganizationCode);
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD,
				sDeliveryMethod);
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_DIVISION,
				sDivision);
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE,
				sDocumentType);
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_RECEIVING_NODE,
				sReceivingNode);
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_SALES_ORDER_NO,
				sSalesOrderNo);
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO,
				sShipAdviceNo);
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_ORDER_NAME,
				sOrderName);
		eleWMoSOrderReleaseXML.setAttribute(
				KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, sCarrierServiceCode);
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE,
				sEnterpriseCode);
		eleWMoSOrderReleaseXML.setAttribute(
				KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
		// eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER,
		// "");
		eleWMoSOrderReleaseXML.setAttribute(
				KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_RELEASE_NO,
				sReleaseNo);
		eleWMoSOrderReleaseXML.setAttribute(
				KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE,
				sSellerOrganizationCode);
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_SHIPNODE,
				sShipNode);

		return eleWMoSOrderReleaseXML;
	}



	






}
