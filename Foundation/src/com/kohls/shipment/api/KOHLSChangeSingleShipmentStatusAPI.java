package com.kohls.shipment.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSChangeSingleShipmentStatusAPI extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KOHLSChangeSingleShipmentStatusAPI.class.getName());
	Properties properties = null;
	
	private Properties props;

	/**
	* @param properties
	*            argument from configuration.
	*/
	public void setProperties(Properties props) {
	    this.props = props;
	}

	public void processChangeSingleShipment(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Start of KOHLSChangeSingleShipmentStatusAPI processChangeSingleShipment method -- >");
		}

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("input  for KOHLSChangeSingleShipmentStatusAPI : \n"
					+ XMLUtil.getXMLString(inXML));
		}
		// get the shipment key from input xml
		Element eleShipment = inXML.getDocumentElement();
		String strprinterid=eleShipment.getAttribute(KohlsConstant.A_PRINTER_ID);
		String shipmentkeyfrominXML = eleShipment
				.getAttribute(KohlsConstant.A_SHIPMENT_KEY);

		// create a input which has only shipmentkey
		Document indocforgetshipmentDetails = XMLUtil
				.createDocument(KohlsConstant.E_SHIPMENT);
		Element eleindocforgetshipmentDetails = indocforgetshipmentDetails
				.getDocumentElement();
		eleindocforgetshipmentDetails.setAttribute(
				KohlsConstant.A_SHIPMENT_KEY, shipmentkeyfrominXML);
		// create the input for getshipmentlist
		Document inDocforgetshipmentlist = XMLUtil
				.createDocument(KohlsConstant.E_SHIPMENT);
		Element eleinDocforgetshipmentlist = inDocforgetshipmentlist
				.getDocumentElement();
		eleinDocforgetshipmentlist.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
				shipmentkeyfrominXML);
		eleinDocforgetshipmentlist.setAttribute(KohlsConstant.A_IGNORE_ORDERING, KohlsConstant.V_N);
		eleinDocforgetshipmentlist.setAttribute(KohlsConstant.A_MAXIMUM_RECORDS, KohlsConstant.V_MAXIMUM_RECORDS);
		Element eleorderby=inDocforgetshipmentlist.createElement(KohlsConstant.E_ORDER_BY);
		eleinDocforgetshipmentlist.appendChild(eleorderby);
		Element eleattribute=inDocforgetshipmentlist.createElement(KohlsConstant.A_ATTRIBUTE);
		eleorderby.appendChild(eleattribute);
		eleattribute.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_NO);

		// creating the template for getshipmentlist
		Document docGetShipListTemp = XMLUtil
				.createDocument(KohlsConstant.E_SHIPMENTS);
		Element eleGetShpmntsListTemp = docGetShipListTemp.getDocumentElement();
		Element eleGetShipListTemp = docGetShipListTemp
				.createElement(KohlsConstant.E_SHIPMENT);
		eleGetShipListTemp.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
				KohlsConstant.A_BLANK);
		eleGetShipListTemp.setAttribute(KohlsConstant.A_STATUS,
				KohlsConstant.A_BLANK);
		eleGetShipListTemp
				.setAttribute(KohlsConstant.A_SELLER_ORGANIZATION_CODE,
						KohlsConstant.A_BLANK);
		eleGetShipListTemp.setAttribute(KohlsConstant.A_SHIP_NODE,
				KohlsConstant.A_BLANK);
		eleGetShpmntsListTemp.appendChild(eleGetShipListTemp);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("input  for getshipmentlist : \n"
					+ XMLUtil.getXMLString(inDocforgetshipmentlist));
		}

		// invoking getshipmentlist
		Document docShipmentListOutput = KOHLSBaseApi.invokeAPI(env,
				docGetShipListTemp, KohlsConstant.API_actual_GET_SHIPMENT_LIST,
				inDocforgetshipmentlist);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("output  for getshipmentlist : \n"
					+ XMLUtil.getXMLString(docShipmentListOutput));
		}

		// getting the attributes from getSHipmentlist
		Element eleShipmentListOutput = docShipmentListOutput
				.getDocumentElement();
		Element eleShip = (Element) eleShipmentListOutput
				.getElementsByTagName(KohlsConstant.E_SHIPMENT).item(0);
		String strStatus = eleShip.getAttribute(KohlsConstant.A_STATUS);
		String shipmentkey = eleShip.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
		String shipnode = eleShip.getAttribute(KohlsConstant.A_SHIP_NODE);
		String SellerOrgCode = eleShip
				.getAttribute(KohlsConstant.A_SELLER_ORGANIZATION_CODE);

		if (strStatus.equalsIgnoreCase(KohlsConstant.V_AWAITING_PICKLIST_PRINT)) {
			// creating the input to changeshipmentstatus
			Document inDocOfChangeShipmentStatus = XMLUtil
					.createDocument(KohlsConstant.E_SHIPMENT);
			Element eleinDocOfChangeShipmentStatus = inDocOfChangeShipmentStatus
					.getDocumentElement();
			eleinDocOfChangeShipmentStatus.setAttribute(
					KohlsConstant.E_BASE_DROP_STATUS,
					KohlsConstant.V_BASE_DROP_STATUS);// put this in constant
														// file n check if it is
														// correct
			eleinDocOfChangeShipmentStatus.setAttribute(
					KohlsConstant.A_SELLER_ORGANIZATION_CODE, SellerOrgCode);
			eleinDocOfChangeShipmentStatus.setAttribute(
					KohlsConstant.A_SHIP_NODE, shipnode);
			eleinDocOfChangeShipmentStatus.setAttribute(
					KohlsConstant.A_SHIPMENT_KEY, shipmentkey);
			eleinDocOfChangeShipmentStatus.setAttribute(
					KohlsConstant.A_TRANSACTION_ID,
					KohlsConstant.V_TRANSACTION_ID);

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("input  for changeShipmentStatus : \n"
						+ XMLUtil.getXMLString(inDocOfChangeShipmentStatus));
			}
			// invoke changeshipmentstatus to change the status
			Document docChangeShipStatusOutput = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_CHANGE_SHIPMENT_STATUS,
					inDocOfChangeShipmentStatus);

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("output  for changeShipmentStatus : \n"
						+ XMLUtil.getXMLString(docChangeShipStatusOutput));
			}
			/**Commenting out the code for changeShipment to avoid deadlocks **/
			// creating the input for changestatus
			Document docChangeShipment = XMLUtil
					.createDocument(KohlsConstant.E_SHIPMENT);
			Element eledocChangeShipment = docChangeShipment
					.getDocumentElement();
			eledocChangeShipment.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
					shipmentkey);
			eledocChangeShipment.setAttribute(
					KohlsConstant.A_IS_PICK_LIST_PRINTED, KohlsConstant.V_Y);

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("input  for changeShipment : \n"
						+ XMLUtil.getXMLString(docChangeShipment));
			}
			// invoke the changeShipment
			Document docchangestatus = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_CHANGE_SHIPMENT, docChangeShipment);

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("output  for changeShipment : \n"
						+ XMLUtil.getXMLString(docchangestatus));

			}
			env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS,
				     KohlsConstant.API_GET_SHIPMENT_DETAILS_PACK_SHIPMENT_TEMPLATE_PATH_1);
			
			Document outDocgetShipmentDetails = KOHLSBaseApi.invokeAPI(env,
				     KohlsConstant.API_GET_SHIPMENT_DETAILS, indocforgetshipmentDetails);
			
			env.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS);
			// invoking the get shipment deatils
			/*Document outDocgetShipmentDetails = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_GET_SHIPMENT_DETAILS,
					indocforgetshipmentDetails);*/
			Element eleoutDocgetShipmentDetailsElement = outDocgetShipmentDetails
					.getDocumentElement();
			String strshipnode=eleoutDocgetShipmentDetailsElement.getAttribute(KohlsConstant.A_SHIP_NODE);
			String shipnodenew = eleoutDocgetShipmentDetailsElement
					.getAttribute(KohlsConstant.A_SHIP_NODE);
			String sellorg = eleoutDocgetShipmentDetailsElement
					.getAttribute(KohlsConstant.A_SELLER_ORGANIZATION_CODE);
			String itemnodedefnkey = eleoutDocgetShipmentDetailsElement
					.getAttribute(KohlsConstant.A_ENTERPRISE_CODE);
			String shipmentno = eleoutDocgetShipmentDetailsElement
					.getAttribute(KohlsConstant.A_SHIPMENT_NO);
			NodeList listshipmentlines = eleoutDocgetShipmentDetailsElement
					.getElementsByTagName(KohlsConstant.A_SHIPMENT_LINE);
			int iLength = listshipmentlines.getLength();
			String itemid = null;
			String uom = null;
			String desc = null;
			String qty = null;
			String strShipnode=null;
			Element eleshipments=null;
			Element eleItemAlias = null;
			String strAliasName = "";
			String strAliasValue = "";
			String strLineNo = "";
			String strLocationID = "";
			
			if (iLength > 0) {
				 eleshipments = (Element) listshipmentlines.item(0);
				itemid = eleshipments.getAttribute(KohlsConstant.A_ITEM_ID);
				uom = eleshipments
						.getAttribute(KohlsConstant.A_UNIT_OF_MEASURE);
				desc = eleshipments.getAttribute(KohlsConstant.A_ITEM_DESC);
				qty = eleshipments.getAttribute(KohlsConstant.A_QUANTITY);
				strShipnode=eleshipments.getAttribute(KohlsConstant.A_SHIPMENT_NO);
			}

			// create input for getItemNodeDefnDetails
			Document inDocForgetItemNodeDefnDetails = XMLUtil
					.createDocument(KohlsConstant.E_ITEM_NODE_DEFN);
			Element eleinDocForgetItemNodeDefnDetails = inDocForgetItemNodeDefnDetails
					.getDocumentElement();
			eleinDocForgetItemNodeDefnDetails.setAttribute(
					KohlsConstant.A_ITEM_ID, itemid);
			eleinDocForgetItemNodeDefnDetails.setAttribute(
					KohlsConstant.A_NODE, shipnodenew);
			String strCatalogOrgCode = props.getProperty(KohlsConstant.A_CATALOG_ORG_CODE);
            if(!YFCObject.isVoid(strCatalogOrgCode))
            {
            	eleinDocForgetItemNodeDefnDetails.setAttribute(
    					KohlsConstant.A_ORGANIZATION_CODE, strCatalogOrgCode);
            }else
            {
            	eleinDocForgetItemNodeDefnDetails.setAttribute(
    					KohlsConstant.A_ORGANIZATION_CODE, KohlsConstant.A_CATALOG_ORG);
            }
			
			eleinDocForgetItemNodeDefnDetails.setAttribute(
					KohlsConstant.A_UNIT_OF_MEASURE, uom);

			// create template for getItemNodeDefnDetails
			Document tempdocgetitemnodedefndetails = XMLUtil
					.createDocument(KohlsConstant.E_ITEM_NODE_DEFN);
			Element eletempdocgetitemnodedefndetails = tempdocgetitemnodedefndetails
					.getDocumentElement();
			Element eletempdocgetitemnodedefndetailsextended = tempdocgetitemnodedefndetails
					.createElement(KohlsConstant.A_EXTN);
			eletempdocgetitemnodedefndetails
					.appendChild(eletempdocgetitemnodedefndetailsextended);
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("input  for getitemnodedefndetails : \n"
						+ XMLUtil.getXMLString(inDocForgetItemNodeDefnDetails));
			}
			// invoke getitemnodedefndetails
			Document OutDocofGetitemNodeDefnDetails = KOHLSBaseApi.invokeAPI(
					env, tempdocgetitemnodedefndetails,
					KohlsConstant.API_GET_ITEM_NODE_DEFN_DETAILS,
					inDocForgetItemNodeDefnDetails);
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("output  for getitemnodedefndetails : \n"
						+ XMLUtil.getXMLString(OutDocofGetitemNodeDefnDetails));
			}
			// preparing the jasperprint input

			// getting the line no and location no from
			// OutDocofGetitemNodeDefnDetails
			
			NodeList nlItemAliasList = eleshipments
			.getElementsByTagName(KohlsConstant.A_ITEM_ALIAS);
			for (int intItemAliasCount = 0; intItemAliasCount < nlItemAliasList.getLength(); intItemAliasCount++) {
				Element eleItemAliasAll = (Element) nlItemAliasList.item(intItemAliasCount);


				String sAliasName = eleItemAliasAll.getAttribute(KohlsConstant.A_ALIAS_NAME);
				if(!YFCObject.isVoid(sAliasName) && sAliasName.equalsIgnoreCase(KohlsConstant.V_UPC01))
				{
					eleItemAlias = eleItemAliasAll;
				}

			}
			if(!YFCObject.isVoid(eleItemAlias))
			{
				strAliasName = eleItemAlias.getAttribute(KohlsConstant.A_ALIAS_NAME);
				strAliasValue = eleItemAlias.getAttribute(KohlsConstant.A_ALIAS_VALUE);
			}
			
			Element extn = (Element) OutDocofGetitemNodeDefnDetails
					.getElementsByTagName(KohlsConstant.A_EXTN).item(0);
			String extnlocationid = extn.getAttribute(KohlsConstant.A_EXTN_LOCATION_ID); 
			String extnlineno = extn.getAttribute(KohlsConstant.A_EXTN_LINE_NO);
			// getting the other info from getshipmentdetails

			Document indocjasper = XMLUtil
					.createDocument(KohlsConstant.E_SHIPMENTS);
			Element eleindocjasper = indocjasper.getDocumentElement();
			eleindocjasper.setAttribute(KohlsConstant.A_PRINTER_ID, strprinterid);
			eleindocjasper.setAttribute(KohlsConstant.A_SHIP_NODE, strshipnode);
			Element shipmentindocjasper = indocjasper
					.createElement(KohlsConstant.E_SHIPMENT);
			
			shipmentindocjasper.setAttribute(KohlsConstant.A_TOTEID,
					KohlsConstant.V_TOTE_ID);
			shipmentindocjasper.setAttribute(KohlsConstant.A_SHIPMENT_NO,
					shipmentno);
			eleindocjasper.appendChild(shipmentindocjasper);
			Element shipmentlinesindocjasper = indocjasper
					.createElement(KohlsConstant.E_SHIPMENT_LINES);
			shipmentindocjasper.appendChild(shipmentlinesindocjasper);
			Element shipmentlineindocjasper = indocjasper
					.createElement(KohlsConstant.E_SHIPMENT_LINE);
			if(strAliasName.equalsIgnoreCase(KohlsConstant.V_UPC01))
			{
				shipmentlineindocjasper.setAttribute(KohlsConstant.V_UPC01, strAliasValue);
			}
			shipmentlineindocjasper.setAttribute(KohlsConstant.A_SHIPMENT_NO,
					shipmentno);
			shipmentlineindocjasper.setAttribute(KohlsConstant.A_ITEM_ID,
					itemid);
			shipmentlineindocjasper.setAttribute(KohlsConstant.A_ITEM_DESC,
					desc);
			shipmentlineindocjasper.setAttribute(KohlsConstant.A_QUANTITY, qty);
			shipmentlineindocjasper.setAttribute(KohlsConstant.A_TOTEID,
					KohlsConstant.V_TOTE_ID);
			shipmentlineindocjasper.setAttribute(KohlsConstant.A_EXTN_LOCATION_ID,
					extnlocationid);
			shipmentlineindocjasper.setAttribute(KohlsConstant.A_EXTN_LINE_NO,
					extnlineno);
			shipmentlineindocjasper.setAttribute(KohlsConstant.A_SKU_QTY, qty);
			shipmentlinesindocjasper.appendChild(shipmentlineindocjasper);
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("input  for changestatussingleprintservice : \n"
						+ XMLUtil.getXMLString(indocjasper));
			}

			// invoke the jasper service
			Document outdocjasperservice = KOHLSBaseApi.invokeService(env,
					KohlsConstant.SERVICE_JASPER_SINGLE_PRINT_SERVICE,
					indocjasper);

			

		}
		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- end of KOHLSChangeSingleShipmentStatusAPI processChangeSingleShipment method -- >");
		}
	}
}
