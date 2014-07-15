package com.kohls.stubs.oms.agent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.comergent.api.xml.XMLUtils;
import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.ui.backend.util.Util;
import com.yantra.yfs.japi.YFSEnvironment;

public class CreateOrderAgent extends YCPBaseAgent {
	
	private static final String API_GET_SHIP_NODE_LIST = "getShipNodeList";
	private static final String KOHLS_GETSHIPNODELIST_TEMPLATE = "global/template/api/getShipNodeList.xml";
	private static final String NODETYPE = "STORE";
	private static final int TOTAL_SCENARIO_COUNT = 15;
	private static int COUNT = 1;
	private static final String TRAINING_SCENARIO = "TRAINING_SCENARIO";
	private static final String EMAIL_ID = "EMAIL_ID";
	private static final String SCENARIO_TYPE = "SCENARIO_TYPE";
	private static final String SKU_QUANTITY = "SKU_QUANTITY";
	private static final int SUB_LINE_NO = 1;
	private static final int PRIME_LINE_NO = 1;
	private static final String API_GET_COMMON_CODE_LIST = "getCommonCodeList";
	private static final String API_GET_COMMON_CODE_TEMPLATE = "global/template/api/getCommonCodeList.xml";
	private static YFCLogCategory log = YFCLogCategory
			.instance(CreateOrderAgent.class);

	@Override
	public void executeJob(YFSEnvironment env, Document paramDocument)
			throws Exception {

		log.debug("CreateOrderAgent executeJob started");
		log.debug("CreateOrderAgent Input XML to executeJob(): "+SCXmlUtil.getString(paramDocument));

		StringBuffer shipNodedoc = new StringBuffer();
		shipNodedoc.append("<ShipNode NodeType='" + NODETYPE + "'> </ShipNode>");
		log.verbose("NodeType element ::" + shipNodedoc);
		Document indocGetShipNode = XmlUtils.createFromString(shipNodedoc.toString());
		log.verbose("ShipNodeList inputXML::"+ SCXmlUtil.getString(indocGetShipNode));
		Document outShipNodeList = getShipNodeList(env, indocGetShipNode);

		Element shipNodelist = outShipNodeList.getDocumentElement();

		NodeList ShipNodeEleList = shipNodelist.getElementsByTagName("ShipNode");

		for (int i = 0; i < ShipNodeEleList.getLength(); i++) {
			Element shipNodeEle = (Element) ShipNodeEleList.item(i);
			String shipNode = shipNodeEle.getAttribute("ShipNode");
			log.verbose("current ShipNode::" + shipNode);
			while (COUNT <= TOTAL_SCENARIO_COUNT) {
				log.verbose("TOTAL SCENARIO COUNT::" + TOTAL_SCENARIO_COUNT);
				log.verbose("Count::" + COUNT);
				Date date = new Date();
				DateFormat orderNumberdateFormat = new SimpleDateFormat("yyyyMMdd");
				log.verbose("orderNumberdateFormat::" + orderNumberdateFormat);
				DateFormat orderDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				log.verbose("orderDateFormat::" + orderDateFormat);
				String scenarioNumber = getScenarioNumber(COUNT);
				log.verbose("Current Scenario Number::"+scenarioNumber);
				//String emailID = getScenarioDetails(env, EMAIL_ID, scenarioNumber);
				String emailID = "pradeep.nukatoti@kohls.com";
				log.verbose("emailID::"+emailID);
				//String ScenarioType = getScenarioDetails(env, SCENARIO_TYPE, scenarioNumber);
				String ScenarioType = "P1";
				log.verbose("ScenarioType::" + ScenarioType);
				//YFCDocument createOrderDoc = getCreateOrderHeaderTemplate(emailID, ScenarioType, orderNumberdateFormat.format(date), orderDateFormat.format(date));
				YFCDocument yfcDocCreateOrderTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
				YFCElement yfcEleCreateOrderTemp = yfcDocCreateOrderTemp.getDocumentElement();
				yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_BILL_TO_ID, "");
				yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_CUSTOMER_EMAIL_ID, emailID);
				yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_ENTERED_BY, "12345678");
				yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_DOCUMENT_TYPE, "0001");
				yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "KOHLS.COM");
				yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, orderDateFormat.format(date));
				yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER,"SCENARIO"+"_"+ScenarioType+"_"+shipNode+"_"+(orderNumberdateFormat.format(date)).substring(4, (orderNumberdateFormat.format(date)).length()));
				yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_DRAFT_ORDER_FLAG, "N");

				YFCElement yfcHeaderChargesInfoTemp = yfcEleCreateOrderTemp.createChild(KohlsXMLLiterals.A_HEADER_CHARGES);
				YFCElement yfcHeaderChargeChildInfoTemp = yfcHeaderChargesInfoTemp.createChild(KohlsXMLLiterals.A_HEADER_CHARGE);
				yfcHeaderChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT,"0");
				yfcHeaderChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, "Shipping");
				yfcHeaderChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, "Shipping1");
				yfcHeaderChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_REFERENCE,"0-45208");
				yfcHeaderChargesInfoTemp.appendChild(yfcHeaderChargeChildInfoTemp);
				yfcEleCreateOrderTemp.appendChild(yfcHeaderChargesInfoTemp);

				YFCElement yfcEleHeaderTaxesTemp = yfcEleCreateOrderTemp.createChild(KohlsXMLLiterals.E_HEADER_TAXES);
				YFCElement yfcEleHeaderTaxTemp = yfcEleHeaderTaxesTemp.createChild(KohlsXMLLiterals.E_HEADER_TAX);
				yfcEleHeaderTaxTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, "ShippingTax");
				yfcEleHeaderTaxTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, "Shipping1");
				yfcEleHeaderTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX, "0.00");
				yfcEleHeaderTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX_NAME, "shipping tax");
				yfcEleHeaderTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, "0.00");
				yfcEleHeaderTaxTemp.setAttribute(KohlsXMLLiterals.A_TAXABLE_FLAG, "Y");
				yfcEleHeaderTaxesTemp.appendChild(yfcEleHeaderTaxTemp);
				yfcEleCreateOrderTemp.appendChild(yfcEleHeaderTaxesTemp);

				log.verbose("Header Element::"+SCXmlUtil.getString(yfcDocCreateOrderTemp.getDocument()));
				//String ItemId = getScenarioDetails(env, TRAINING_SCENARIO, scenarioNumber);
				String ItemId = "94717782";
				String ItemDesc = "SVVW PURPLE WEFT BATH TOW";
				log.verbose("ItemId::" + ItemId);
				if (ItemId.contains(";")) {
					String ItemsList[] = ItemId.split("\\;");
					for (String item : ItemsList) {
						String quantity = getScenarioDetails(env, SKU_QUANTITY, item);
						log.verbose("quantity::" + quantity);
						appendOrderLineEleTemplate(yfcDocCreateOrderTemp,(ArrayUtils.indexOf(ItemsList, item) + 1), (ArrayUtils.indexOf(ItemsList, item) + 1), quantity, shipNode, item, ItemDesc);
						appendPersonInfoDetails(yfcDocCreateOrderTemp);
						appendPaymentmethods(yfcDocCreateOrderTemp);
						log.verbose("CreateOrder input XML::"+ SCXmlUtil.getString(yfcDocCreateOrderTemp.getDocument()));
						KOHLSBaseApi.invokeAPI(env, KohlsConstant.API_CREATE_ORDER, yfcDocCreateOrderTemp.getDocument());
						COUNT++;
					}
				} else {
					//String quantity = getScenarioDetails(env, SKU_QUANTITY, ItemId);
					String quantity = "10";
					log.verbose("quantity::" + quantity);
					appendOrderLineEleTemplate(yfcDocCreateOrderTemp, SUB_LINE_NO, PRIME_LINE_NO, quantity, shipNode, ItemId, ItemDesc);
					appendPersonInfoDetails(yfcDocCreateOrderTemp);
					appendPaymentmethods(yfcDocCreateOrderTemp);
					log.verbose("CreateOrder input XML::"+ SCXmlUtil.getString(yfcDocCreateOrderTemp.getDocument()));
					KOHLSBaseApi.invokeAPI(env, KohlsConstant.API_CREATE_ORDER, yfcDocCreateOrderTemp.getDocument());
					COUNT++;
				}
				log.endTimer("CreateOrderAgent executeJob ended");
			}
		}
	}

	private String getScenarioNumber(int ScenarioCount) {

		StringBuffer scenario = new StringBuffer();
		scenario.append("SCENARIO"+"_"+ScenarioCount);
		log.debug("Current Scenario Number::"+scenario.toString());
		return scenario.toString();
	}

	public Document getShipNodeList(YFSEnvironment env, Document inXml)
			throws Exception {
		Document outdocGetShipNodeList = KohlsCommonUtil.invokeAPI(env, KOHLS_GETSHIPNODELIST_TEMPLATE, API_GET_SHIP_NODE_LIST, inXml);
		log.verbose("getShipNodelist output XML::"+ SCXmlUtil.getString(outdocGetShipNodeList));
		return outdocGetShipNodeList;
	}

	private String getScenarioDetails(YFSEnvironment env, String CodeType, String ScenarioNumber) throws Exception {
		// TODO Auto-generated method stub
		String CodeValue = null;
		StringBuffer itemIDdetails = new StringBuffer();
		itemIDdetails.append("<CommonCode CodeType='"+CodeType+"'> </CommonCode>");
		log.verbose("itemIDdetails element::"+itemIDdetails);
		Document commonCodeInXML = XmlUtils.createFromString(itemIDdetails.toString());
		log.verbose("CommonCode inputXML::"+SCXmlUtil.getString(commonCodeInXML));
		Document skuDetails = KohlsCommonUtil.invokeAPI(env, API_GET_COMMON_CODE_TEMPLATE, API_GET_COMMON_CODE_LIST,commonCodeInXML);
		log.debug("Commoncodelist output::" + SCXmlUtil.getString(skuDetails));
		Element commonCodeListEle = skuDetails.getDocumentElement();
		NodeList codeList = commonCodeListEle.getElementsByTagName("CommonCode");
		for (int i = 0; i < codeList.getLength(); i++) {
			Element commoncodeEle = (Element) codeList.item(i);
			String codeDesc = commoncodeEle.getAttribute("CodeShortDescription");
			if (ScenarioNumber.equals(codeDesc)) {
				CodeValue = commoncodeEle.getAttribute("CodeValue");
				break;
			}
		}
		return CodeValue;
	}
	public List<Document> getJobs(YFSEnvironment env, Document inXml) throws Exception {

		log.beginTimer("CreateOrderAgent:getJobs");
		log.debug("Started CreateOrderAgent getJobs::");

		return null;
	}

	/*private YFCDocument getCreateOrderHeaderTemplate(String emailId, String scenarioType, String orderNumber, String orderDate) {

		YFCDocument yfcDocCreateOrderTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleCreateOrderTemp = yfcDocCreateOrderTemp.getDocumentElement();
		yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_CUSTOMER_EMAIL_ID, emailId);
		yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_DOCUMENT_TYPE, "0001");
		yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "KOHLS.COM");
		yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, orderDate);
		yfcEleCreateOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER,"TRAINING_SCENARIO"+"_"+scenarioType+orderNumber);
		log.verbose("Header Element::"+yfcDocCreateOrderTemp.getDocument());
		return yfcDocCreateOrderTemp;
	}*/

	private void appendOrderLineEleTemplate(YFCDocument yfcDocCreateOrderTemp,
			int subline, int primeLine, String qty, String shipNode, String item, String itemDesc) {
		YFCElement yfcEleTemp = yfcDocCreateOrderTemp.getDocumentElement();
		YFCElement yfcOrderLinesEleTemp = yfcEleTemp.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcOrderLineEleTemp = yfcOrderLinesEleTemp.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.E_SUB_LINE_NO, subline);
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.E_PRIME_LINE_NO, primeLine);
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.E_ORDERED_QTY, qty);
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.A_GIFT_WRAP, "N");
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.A_GIFT_FLAG, "N");
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.E_CARRIER_SERVICE_CODE, "Standard Ground");
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.E_SHIP_NODE, shipNode);
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.E_DELIVERY_METHOD,"PICK");
		yfcOrderLinesEleTemp.appendChild(yfcOrderLineEleTemp);

		YFCElement yfcEleExtnItemPriceTemp = yfcOrderLineEleTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnItemPriceTemp.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_CODE,"");
		yfcEleExtnItemPriceTemp.setAttribute(KohlsXMLLiterals.A_EXTN_BOGO_SEQ,"1");
		yfcEleExtnItemPriceTemp.setAttribute(KohlsXMLLiterals.A_EXTN_TAX_AMOUNT, "0.00");
		yfcEleExtnItemPriceTemp.setAttribute(KohlsXMLLiterals.A_EXTN_RETURN_PRICE, "14.99");
		yfcEleExtnItemPriceTemp.setAttribute(KohlsXMLLiterals.A_EXTN_LINE_GFT_RECP_ID, "G");
		yfcEleExtnItemPriceTemp.setAttribute(KohlsXMLLiterals.A_EXTN_IS_HAZARDOUS, "N");
		yfcEleExtnItemPriceTemp.setAttribute(KohlsXMLLiterals.A_EXTN_CURRENT_ITEM_STATUS, "20");
		yfcOrderLineEleTemp.appendChild(yfcEleExtnItemPriceTemp);

		YFCElement yfcItemele = yfcOrderLineEleTemp.createChild(KohlsXMLLiterals.E_ITEM);
		yfcItemele.setAttribute(KohlsXMLLiterals.A_UOM, "EACH");
		yfcItemele.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "Good");
		yfcItemele.setAttribute(KohlsXMLLiterals.A_ITEM_DESC, itemDesc);
		yfcItemele.setAttribute(KohlsXMLLiterals.A_ITEMID, item);
		yfcItemele.setAttribute(KohlsXMLLiterals.A_ITEM_TYPE, "TEST");
		yfcOrderLineEleTemp.appendChild(yfcItemele);

		YFCElement yfcLinePriceInfoTemp = yfcOrderLineEleTemp.createChild(KohlsXMLLiterals.E_LINE_PRICE_INFO);
		yfcItemele.setAttribute(KohlsXMLLiterals.A_UNIT_PRICE, "29.99");
		yfcItemele.setAttribute(KohlsXMLLiterals.A_TAXABLE_FLAG, "Y");
		yfcItemele.setAttribute(KohlsXMLLiterals.A_RETAIL_PRICE, "29.99");
		yfcItemele.setAttribute(KohlsXMLLiterals.A_LIST_PRICE, "29.99");
		yfcItemele.setAttribute(KohlsXMLLiterals.A_IS_PRICE_LOCKED, "Y");
		yfcOrderLineEleTemp.appendChild(yfcLinePriceInfoTemp);

		YFCElement yfcLineChargesInfoTemp = yfcOrderLineEleTemp.createChild(KohlsXMLLiterals.E_LINE_CHARGES);
		YFCElement yfcLineChargeChildInfoTemp = yfcLineChargesInfoTemp.createChild(KohlsXMLLiterals.E_LINE_CHARGE);
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_IS_DISCOUNT,"Y");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_IS_BILLABLE,"Y");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_UNIT,"0");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_LINE,"0");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME,"Gift_NCth_Purchase");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY,"Discount");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT,"0");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_UNIT_PRICE,"");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_TAXABLE_FLAG, "");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_RETAIL_PRICE, "");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_LIST_PRICE,"");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_ITEM_TYPE,"N");
		yfcLineChargeChildInfoTemp.setAttribute(KohlsXMLLiterals.A_IS_PRICE_LOCKED, "Y");
		yfcLineChargesInfoTemp.appendChild(yfcLineChargeChildInfoTemp);
		yfcOrderLineEleTemp.appendChild(yfcLineChargesInfoTemp);

		YFCElement yfcElePromotionExtnTemp = yfcOrderLineEleTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcElePromotionExtnTemp.setAttribute(KohlsXMLLiterals.EXTN_PROMO_CODE, "772");
		yfcElePromotionExtnTemp.setAttribute(KohlsXMLLiterals.EXTN_PROMO_ID, "5digitCouponID");
		yfcOrderLineEleTemp.appendChild(yfcElePromotionExtnTemp);

		YFCElement yfcEleLineTaxesTemp = yfcOrderLineEleTemp.createChild(KohlsXMLLiterals.E_LINE_TAXES);
		YFCElement yfcEleLineTaxTemp = yfcEleLineTaxesTemp.createChild(KohlsXMLLiterals.E_LINE_TAX);
		yfcItemele.setAttribute(KohlsXMLLiterals.A_TAXABLE_FLAG, "N");
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, "0.000");
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX_NAME, "SALES_TAX");
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_TAX, "0.00");
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME,"Tax");
		yfcEleLineTaxTemp.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY,"Tax");		
		yfcEleLineTaxesTemp.appendChild(yfcEleLineTaxTemp);
		yfcOrderLineEleTemp.appendChild(yfcEleLineTaxesTemp);

		YFCElement yfcElePersonInfoShipToTemp = yfcOrderLineEleTemp.createChild(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO);
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_TAX_GEO_CODE,"00");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_FIRST_NAME,"PRADEEP");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_LAST_NAME,"NUKATOTI");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1,"132 ASSEMBLY DRIVE");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2,"UNIT 205");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_CITY,"MOORESVILLE");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_STATE, "NC");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_COUNTRY,"USA");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ZIP_CODE,"28117");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_DAY_PHONE,"7044978169");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EMAIL_ID,"pnukato@us.ibm.com");


		YFCElement yfcEleExtnInfoShipToTemp = yfcElePersonInfoShipToTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX, "N");
		yfcEleExtnInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY, "N");
		yfcElePersonInfoShipToTemp.appendChild(yfcEleExtnInfoShipToTemp);
		yfcOrderLineEleTemp.appendChild(yfcElePersonInfoShipToTemp);


		YFCElement yfcOrderLineReservationsEleTemp = yfcOrderLineEleTemp.createChild(KohlsXMLLiterals.E_ORDER_LINE_RESERVATIONS);
		YFCElement yfcOrderLineReserveEleTemp = yfcOrderLineReservationsEleTemp.createChild(KohlsXMLLiterals.E_ORDER_LINE_RESERVATION);
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.E_DEMAND_TYPE,"RESERVED");
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, item);
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.A_NODE, shipNode);
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS,"Good");
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.A_QUANTITY, qty);
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.A_UOM, "EACH");
		yfcOrderLineEleTemp.setAttribute(KohlsXMLLiterals.A_RESERVATION_ID, "R1515");
		yfcOrderLineReservationsEleTemp.appendChild(yfcOrderLineReserveEleTemp);
		yfcOrderLineEleTemp.appendChild(yfcOrderLineReservationsEleTemp);
	}

	public void appendPersonInfoDetails(YFCDocument yfcDocCreateOrderTemp) {
		YFCElement yfcEleTemp = yfcDocCreateOrderTemp.getDocumentElement();
		YFCElement yfcElePersonInfoShipToTemp = yfcEleTemp.createChild(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO);
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "PRADEEP");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "NUKATOTI");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "132 ASSEMBLY DRIVE");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "UNIT 205");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_CITY, "MOORESVILLE");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_STATE, "NC");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_COUNTRY, "USA");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "28117");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, "7044978169");
		yfcElePersonInfoShipToTemp.setAttribute(KohlsXMLLiterals.A_EMAIL_ID, "pnukato@us.ibm.com");
		yfcEleTemp.appendChild(yfcElePersonInfoShipToTemp);
	}

	public void appendCouponID (YFCDocument yfcDocCreateOrderTemp) {
		YFCElement yfcEleCouponIDtemp = yfcDocCreateOrderTemp.getDocumentElement();			
		YFCElement yfcEleCouponID =	yfcEleCouponIDtemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcEleCouponID.setAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX, "N");
		yfcEleCouponID.setAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY, "N");
		yfcEleCouponIDtemp.appendChild(yfcEleCouponID);
	}

	public void appendPaymentmethods(YFCDocument yfcDocCreateOrderTemp) {
		YFCElement yfcEleTempPayment = yfcDocCreateOrderTemp.getDocumentElement();
		YFCElement yfcElePaymentMethodsTemp = yfcEleTempPayment.createChild(KohlsXMLLiterals.E_PAYMENT_METHODS);
		YFCElement yfcElePaymentMethodTemp = yfcElePaymentMethodsTemp.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, "6DMjDGrMKPfa0007");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, "VISA");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_EXPIRY__DATE,"2014-12-31T00:00:00-05:00");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO, "0007");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_DISP_PAYMENT_REFERENCE_TYPE, "0002");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_MAX_CHARGE_LIMIT, "59.58");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_PAYMENT_REFERENCE_ONE, "trace\20130611.nt18a.fip 6114 002408");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE, "Credit Card");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_SVC_NO, "");
		yfcElePaymentMethodTemp.setAttribute(KohlsXMLLiterals.A_UNLIMITED_CHARGES, "N");
		yfcElePaymentMethodsTemp.appendChild(yfcElePaymentMethodTemp);
		YFCElement yfcElePaymentDetailsListTemp = yfcElePaymentMethodsTemp.createChild(KohlsXMLLiterals.E_PAYMENT_DETAILS_LIST);
		YFCElement yfcElePaymentDetails = yfcElePaymentDetailsListTemp.createChild(KohlsXMLLiterals.E_PAYMENT_DETAILS);
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_AUTH_AMOUNT, "59.58");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_AUTH_AVS, "Y1");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_AUTH_CODE, "014409");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_AUTH_RETURN_CODE, "AJB_SAJBOK");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_AUTH_RETURN_FLAG, "AJB_SAJBOK");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_AUTH_RETURN_MESSAGE, "AJB_SAJBOK");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_AUTH_TIME, "2014-03-25T02:33:24-04:00");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_AUTH_EXPIRY_DATE, "2014-12-31T00:00:00-05:00");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_AUTH_ID, "014409");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_CHARGE_TYPE, "AUTHORIZATION");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_HOLD_BOOK, "Y");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE, "CCD");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_PROCESSED_AMOUNT, "59.98");
		yfcElePaymentDetails.setAttribute(KohlsXMLLiterals.A_REQUEST_AMOUNT, "59.98");
		yfcEleTempPayment.appendChild(yfcElePaymentMethodsTemp);
	}
}
