package com.kohls.ibm.ocf.pca.util;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.YRCXPathUtils;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSCollateUtil 
{
	static Document getReportCompatableXML(Document inputDocc, int numberOfDummyLine ) throws Exception 
	{ 
		Element eleshipmentline= inputDocc.getDocumentElement();
		Element eExistingContainerDetail =  (Element) YRCXPathUtils.evaluate(eleshipmentline,"//Container/ContainerDetails",XPathConstants.NODE);
		
		for(int i=0; i<numberOfDummyLine; i++ ) { 
		Element elmContainerDetail = inputDocc.createElement(KOHLSPCAConstants.A_CONTAINER_DETAIL);
		elmContainerDetail.setAttribute(KOHLSPCAConstants.A_CONTAINER_DETAILS_KEY,"");
		elmContainerDetail.setAttribute(KOHLSPCAConstants.A_ITEM_ID,"");
		elmContainerDetail.setAttribute(KOHLSPCAConstants.A_PRODUCT_CLASS,"");
		elmContainerDetail.setAttribute(KOHLSPCAConstants.A_QUANTITY,"");
		 
		Element elmShipmentLine=YRCXmlUtils.createChild(elmContainerDetail,KOHLSPCAConstants.E_SHIPMENT_LINE);
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_ACTUAL_QUANTITY,"");
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_BACKROOM_PICKED_QUANTITY,"");
		
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_CHAINED_FROM_ORDER_HEADER_KEY,"");
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_CHAINED_FROM_ORDER_LINE_KEY,"");
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_COUNTRY_OF_ORIGIN,"");
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_CUSTOMER_PO_LINE_NO,"");
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_CUSTOMER_PO_NO,"");
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_DOCUMENT_TYPE,"");
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_FIFO_NO,"");
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_IS_HAZMAT,"");
		
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_IS_PICKABLE,"");
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_ITEM_DESC,"");
		elmShipmentLine.setAttribute(KOHLSPCAConstants.A_ITEM_ID,"");
		/*
		
		*/
		Element elmExtn=YRCXmlUtils.createChild(elmShipmentLine,KOHLSPCAConstants.E_EXTN);
		Element elmOrder=YRCXmlUtils.createChild(elmShipmentLine,KOHLSPCAConstants.E_ORDER);
		/*
		 
		*/
		Element elmPaymentMethods=YRCXmlUtils.createChild(elmOrder,KOHLSPCAConstants.E_PAYMENT_METHODS);
		  for(int j=0; j<1; j++ ) { 
		  Element elmPaymentMethod=YRCXmlUtils.createChild(elmPaymentMethods,KOHLSPCAConstants.E_PAYMENT_METHOD);
		elmPaymentMethod.setAttribute(KOHLSPCAConstants.A_AWAITING_AUTH_INTERFACE_AMOUNT,"");
		elmPaymentMethod.setAttribute(KOHLSPCAConstants.A_AWAITING_CHARGE_INTERFACE_AMOUNT,"");
		/*
		 
		*/
		} //-PaymentMethod
		Element elmPromotions=YRCXmlUtils.createChild(elmOrder,KOHLSPCAConstants.E_PROMOTIONS);
		Element elmHeaderCharges=YRCXmlUtils.createChild(elmOrder,KOHLSPCAConstants.E_HEADER_CHARGES);
		  for(int k=0; k<1; k++ ) { 
		  Element elmHeaderCharge=YRCXmlUtils.createChild(elmHeaderCharges,KOHLSPCAConstants.E_HEADER_CHARGE);
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_CHARGE_AMOUNT,"");
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_CHARGE_CATEGORY,"");
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_CHARGE_NAME,"");
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_CHARGE_NAME_KEY,"");
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_INVOICED_CHARGE_AMOUNT,"");
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_IS_BILLABLE,"");
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_IS_DISCOUNT,"");
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_IS_MANUAL,"");
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_IS_SHIPPING_CHARGE,"");
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_REFERENCE,"");
		elmHeaderCharge.setAttribute(KOHLSPCAConstants.A_REMAINING_CHARGE_AMOUNT,"");
		} //-HeaderCharge
		Element elmHeaderTaxes=YRCXmlUtils.createChild(elmOrder,KOHLSPCAConstants.E_HEADER_TAXES);
		  for(int l=0; l<1; l++ ) { 
		  Element elmHeaderTax=YRCXmlUtils.createChild(elmHeaderTaxes,KOHLSPCAConstants.E_HEADER_TAX);
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_CHARGE_CATEGORY,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_CHARGE_NAME,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_CHARGE_NAME_KEY,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_INVOICED_TAX,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_REFERENCE___1,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_REFERENCE___2,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_REFERENCE___3,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_REMAINING_TAX,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_TAX,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_TAX_NAME,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_TAX_PERCENTAGE,"");
		elmHeaderTax.setAttribute(KOHLSPCAConstants.A_TAXABLE_FLAG,"");
		} //-HeaderTax
		Element elmTaxSummary=YRCXmlUtils.createChild(elmHeaderTaxes,KOHLSPCAConstants.E_TAX_SUMMARY);
		Element elmTaxSummaryDetail=YRCXmlUtils.createChild(elmTaxSummary,KOHLSPCAConstants.E_TAX_SUMMARY_DETAIL);
		elmTaxSummaryDetail.setAttribute(KOHLSPCAConstants.A_INVOICED_TAX,"");
		elmTaxSummaryDetail.setAttribute(KOHLSPCAConstants.A_OVERALL_TAX,"");
		elmTaxSummaryDetail.setAttribute(KOHLSPCAConstants.A_REMAINING_TAX,"");
		elmTaxSummaryDetail.setAttribute(KOHLSPCAConstants.A_TAX_NAME,"");
		Element elmOrderLine=YRCXmlUtils.createChild(elmShipmentLine,KOHLSPCAConstants.E_ORDER_LINE);
		elmOrderLine.setAttribute(KOHLSPCAConstants.A_ALLOCATION_DATE,"");
		elmOrderLine.setAttribute(KOHLSPCAConstants.A_ALLOCATION_LEAD_TIME,"");
		elmOrderLine.setAttribute(KOHLSPCAConstants.A_APPT_STATUS,"");
		/*
		
		*/
		Element elmItem=YRCXmlUtils.createChild(elmOrderLine,KOHLSPCAConstants.V_ITEM);
		elmItem.setAttribute(KOHLSPCAConstants.A_COST_CURRENCY,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_COUNTRY_OF_ORIGIN,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_CUSTOMER_ITEM,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_CUSTOMER_ITEM_DESC,"");
		
		elmItem.setAttribute(KOHLSPCAConstants.A_E_C_C_N_NO,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_HARMONIZED_CODE,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_I_S_B_N,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_ITEM_DESC,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_ITEM_ID,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_ITEM_SHORT_DESC,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_ITEM_WEIGHT,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_ITEM_WEIGHT_U_O_M,"");
		
		elmItem.setAttribute(KOHLSPCAConstants.A_MANUFACTURER_ITEM,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_MANUFACTURER_ITEM_DESC,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_MANUFACTURER_NAME,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_N_M_F_C_CLASS,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_N_M_F_C_CODE,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_N_M_F_C_DESCRIPTION,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_PRODUCT_CLASS,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_PRODUCT_LINE,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_SCHEDULE_B_CODE,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_SUPPLIER_ITEM,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_SUPPLIER_ITEM_DESC,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_TAX_PRODUCT_CODE,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_U_P_C_CODE,"");
		elmItem.setAttribute(KOHLSPCAConstants.A_UNIT_COST,"");
		
		elmItem.setAttribute(KOHLSPCAConstants.A_UOM,"");
		Element elmLinePriceInfo=YRCXmlUtils.createChild(elmOrderLine,KOHLSPCAConstants.E_LINE_PRICE_INFO);
		elmLinePriceInfo.setAttribute(KOHLSPCAConstants.A_ACTUAL_PRICING_QTY,"");
		elmLinePriceInfo.setAttribute(KOHLSPCAConstants.A_BUNDLE_TOTAL,"");
		/*
		 
		*/
		elmLinePriceInfo.setAttribute(KOHLSPCAConstants.A_UNIT_PRICE,"");
		Element elmLineCharges=YRCXmlUtils.createChild(elmOrderLine,KOHLSPCAConstants.E_LINE_CHARGES);
		Element elmLineTaxes=YRCXmlUtils.createChild(elmOrderLine,KOHLSPCAConstants.E_LINE_TAXES);
		  for(int m=0; m<1; m++ ) { 
		  Element elmLineTax=YRCXmlUtils.createChild(elmLineTaxes,KOHLSPCAConstants.E_LINE_TAX);
		elmLineTax.setAttribute(KOHLSPCAConstants.A_CHARGE_CATEGORY,"");
		elmLineTax.setAttribute(KOHLSPCAConstants.A_CHARGE_NAME,"");
		elmLineTax.setAttribute(KOHLSPCAConstants.A_CHARGE_NAME_KEY,"");
		elmLineTax.setAttribute(KOHLSPCAConstants.A_INVOICED_TAX,"");
		elmLineTax.setAttribute(KOHLSPCAConstants.A_REFERENCE___1,"");
		elmLineTax.setAttribute(KOHLSPCAConstants.A_REFERENCE___2,"");
		elmLineTax.setAttribute(KOHLSPCAConstants.A_REFERENCE___3,"");
		elmLineTax.setAttribute(KOHLSPCAConstants.A_REMAINING_TAX,"");
		elmLineTax.setAttribute(KOHLSPCAConstants.A_TAX,"");
		elmLineTax.setAttribute(KOHLSPCAConstants.A_TAX_NAME,"");
		elmLineTax.setAttribute(KOHLSPCAConstants.A_TAX_PERCENTAGE,"");
		} //-LineTax
		Element elmTaxSummaryOfLT1=YRCXmlUtils.createChild(elmLineTaxes,KOHLSPCAConstants.E_TAX_SUMMARY);
		Element elmTaxSummaryDetailOfTS2=YRCXmlUtils.createChild(elmTaxSummaryOfLT1,KOHLSPCAConstants.E_TAX_SUMMARY_DETAIL);
		elmTaxSummaryDetailOfTS2.setAttribute(KOHLSPCAConstants.A_INVOICED_TAX,"");
		elmTaxSummaryDetailOfTS2.setAttribute(KOHLSPCAConstants.A_OVERALL_TAX,"");
		elmTaxSummaryDetailOfTS2.setAttribute(KOHLSPCAConstants.A_REMAINING_TAX,"");
		elmTaxSummaryDetailOfTS2.setAttribute(KOHLSPCAConstants.A_TAX_NAME,"");
		Element elmItemOfSL3=YRCXmlUtils.createChild(elmShipmentLine,KOHLSPCAConstants.V_ITEM);
		elmItemOfSL3.setAttribute(KOHLSPCAConstants.A_ITEM_ID,"");
		elmItemOfSL3.setAttribute(KOHLSPCAConstants.A_UOM,"");
		elmItemOfSL3.setAttribute(KOHLSPCAConstants.A_UPC_0_1,"");
		Element elmPrimaryInformation=YRCXmlUtils.createChild(elmItemOfSL3,KOHLSPCAConstants.E_PRIMARY_INFORMATION);
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_ALLOW_GIFT_WRAP,"");
		
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_ASSUME_INFINITE_INVENTORY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_BUNDLE_FULFILLMENT_MODE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_BUNDLE_PRICING_STRATEGY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_CAPACITY_PER_ORDERED_QTY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_CAPACITY_QUANTITY_STRATEGY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_COLOR_CODE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_COMPUTED_UNIT_COST,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_COST_CURRENCY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_COUNTRY_OF_ORIGIN,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_CREDIT_W_O_RECEIPT,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_DEFAULT_PRODUCT_CLASS,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_DESCRIPTION,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_DISPLAY_ITEM_DESCRIPTION,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_EXTENDED_DESCRIPTION,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_FIXED_CAPACITY_QTY_PER_LINE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_FIXED_PRICING_QTY_PER_LINE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_INVOICE_BASED_ON_ACTUALS,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_INVOLVES_SEGMENT_CHANGE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_AIR_SHIPPING_ALLOWED,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_DELIVERY_ALLOWED,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_ELIGIBLE_FOR_SHIPPING_DISCOUNT,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_FREEZER_REQUIRED,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_HAZMAT,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_PARCEL_SHIPPING_ALLOWED,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_PICKUP_ALLOWED,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_RETURN_SERVICE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_RETURNABLE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_SHIPPING_ALLOWED,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_STANDALONE_SERVICE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_SUB_ON_ORDER_ALLOWED,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_IS_VALID,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_ITEM_TYPE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_KIT_CODE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_MANUFACTURER_ITEM,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_MANUFACTURER_ITEM_DESC,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_MANUFACTURER_NAME,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_MASTER_CATALOG_I_D,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_MAX_ORDER_QUANTITY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_MIN_ORDER_QUANTITY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_MINIMUM_CAPACITY_QUANTITY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_NUM_SECONDARY_SERIALS,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_ORDERING_QUANTITY_STRATEGY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_PRICING_QUANTITY_CONV_FACTOR,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_PRICING_QUANTITY_STRATEGY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_PRICING_U_O_M,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_PRICING_U_O_M_STRATEGY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_PRIMARY_ENTERPRISE_CODE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_PRIMARY_SUPPLIER,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_PRODUCT_LINE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_REQUIRES_PROD_ASSOCIATION,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_RETURN_WINDOW,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_RUN_QUANTITY,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_SERIALIZED_FLAG,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_SERVICE_TYPE_I_D,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_SHORT_DESCRIPTION,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_SIZE_CODE,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_BATCH_STATUS,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_TAXABLE_FLAG,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_COST,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_HEIGHT,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_HEIGHT_U_O_M,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_LENGTH,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_LENGTH_U_O_M,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_VOLUME,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_VOLUME_U_O_M,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_WEIGHT,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_WEIGHT_UOM,"");
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_WIDTH,"");
		 
		elmPrimaryInformation.setAttribute(KOHLSPCAConstants.A_UNIT_WIDTH_U_O_M,"");
		Element elmExtnOfIT4=YRCXmlUtils.createChild(elmItemOfSL3,KOHLSPCAConstants.E_EXTN);
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_BAGGABLE,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_BREAKABLE,"");
		
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_CAGE_ITEM,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_CLASS,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_COLOR_DESC,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_DEFAULT_CONTAINER_TYPE,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_DEPT,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_DEPT_CLASS_SUB_CLASS,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_DIRECT_SHIP_ITEM,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_IS_PLASTIC_GIFT_CARD,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_IS_VIRTUAL_GIFT_CARD,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_NOMADIC,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_RED_PACK_LIST_TYPE,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_RESTRICTED_SHIP_METHOD,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_SHIP_ALONE,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_SHIP_NODE_SOURCE,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_SHIPPING_SERVICE_LEVEL_SEQ,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_SIZE_DESC,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_STYLE,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_SUB_CLASS,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_VENDOR_NUMBER,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_VENDOR_STYLE_DESC,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_VENDOR_STYLE_NO,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_EXTN_WEB_I_D,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_KOHLS_ELIGIBLE_BULK_MARK_REQ,"");
		elmExtnOfIT4.setAttribute(KOHLSPCAConstants.A_KOHLS_SAFETY_FACTOR_REQ,"");
		
		Element elmItemAliasList=YRCXmlUtils.createChild(elmItemOfSL3,KOHLSPCAConstants.E_ITEM_ALIAS_LIST);
		  for(int n=0; n<1; n++ ) { 
		  Element elmItemAlias=YRCXmlUtils.createChild(elmItemAliasList,KOHLSPCAConstants.E_ITEM_ALIAS);
		elmItemAlias.setAttribute(KOHLSPCAConstants.A_ALIAS_NAME,"");
		elmItemAlias.setAttribute(KOHLSPCAConstants.A_ALIAS_VALUE,"");
		} //-ItemAlias
		  eExistingContainerDetail.appendChild(elmContainerDetail);
		} //-ContainerDetail
//		YRCPlatformUI.trace("Document passed to the method getApiInput is " , YRCXmlUtils.getString(inputDoc));
		
		return inputDocc; 
		} 
}
