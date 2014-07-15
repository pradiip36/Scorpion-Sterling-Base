package com.kohls.bopus.util;

/**
 * 
 * This JAVA program have Utilities to perform MLS & GIV Integration with OMS BOPUS
 * @author Ravi, Juned, Asha, Sudhakar 
 * 
 **/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.common.util.KohlsXPathUtil;
import com.kohls.bopus.util.KohlsBOPUSHoldCheck;
import com.kohls.oms.api.KohlsResolveShipAlerts;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


public class KohlsOrderPickProcessUtil {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsOrderPickProcessUtil.class.getName());
	
	private Properties props;
	private String KOHLS_GET_SHIPMENT_DETAILS_MLS_GIV = "global/template/api/getShipmentDetailsWithMLSGIV.xml";
	private String KOHLS_GET_SHIPMENT_DETAILS_TEMPLATE = "global/template/api/extn/KohlsGetShipmentListForUI.xml";
	
	/********************************************
	 * @author: Adam Dunmars
	 * This function takes the document returned from the MLS getItemDetails and returns
	 * the brand and floorpad information for the shipment  line.
	 * @param getLocationDetailsDoc
	 * @param shipmentLinesDoc
	 * @return
	 * @throws TransformerException 
	 ********************************************/
	public Document getMLSInformation(Document getLocationDetailsDoc, Document shipmentLinesDoc) throws TransformerException{
		
		String brand = "";
		String floorpad = "";
		String skuStatus = "";
		String kohlsUPC = "";
		String boxID = "";
		//YFCDocument mlsDoc = YFCDocument.getDocumentFor(getLocationDetailsDoc);
		//Element gilr = SCXmlUtil.getChildElement(getLocationResponse, "GetItemLocationsResult");
		Element gilr = KohlsXMLUtil.getElementByXpath(getLocationDetailsDoc, "//" + KohlsXMLLiterals.E_GET_ITEM_LOCATIONS_RESULT);
		Element response = SCXmlUtil.getChildElement(gilr, KohlsXMLLiterals.E_RESPONSE);
		Element data = SCXmlUtil.getChildElement(response, KohlsXMLLiterals.E_DATA);
		Element itemLocations = SCXmlUtil.getChildElement(data, KohlsXMLLiterals.E_ITEMLOCATIONS);
		Element responseItemLocations = SCXmlUtil.getChildElement(itemLocations, KohlsXMLLiterals.E_INVENTORY_LOCATIONS);
		Element item = SCXmlUtil.getChildElement(responseItemLocations, KohlsXMLLiterals.E_ITEM);
		Element brandLabel = SCXmlUtil.getChildElement(item, KohlsXMLLiterals.E_BRAND_LABEL);
		Element skuStatusElement = SCXmlUtil.getChildElement(item, KohlsXMLLiterals.E_SKU_STATUS_DESCRIPTION);
		Element boxNumberElement = SCXmlUtil.getChildElement(item, KohlsXMLLiterals.E_BOX_NUMBER);
		Element upcs = SCXmlUtil.getChildElement(item, KohlsXMLLiterals.E_UPCS);
		brand = !YFCObject.isVoid(brandLabel) ? brandLabel.getTextContent() : "N/A";
		skuStatus = !YFCObject.isVoid(skuStatusElement) ? skuStatusElement.getTextContent() : "N/A";
		boxID = !YFCObject.isVoid(boxNumberElement) ? boxNumberElement.getTextContent() : "N/A";
		
		NodeList salesfloorLocationList = null;
		NodeList stockRoomLocationList = null;
		log.debug("Gilr is " + gilr);
		log.debug("Response is " + response);
		log.debug("Data is " + data);
		log.debug("ItemLocations is " + itemLocations);
		log.debug("ResponseItemLocations is " + responseItemLocations);
		log.debug("Item is " + item);
		log.debug("BrandLabel is " + brandLabel);
		log.debug("SKUStatusElement is " + skuStatusElement);
		log.debug("BoxId is " + boxID);
		
		if (!YFCObject.isVoid(responseItemLocations) && !YFCObject.isVoid(responseItemLocations.getElementsByTagName(KohlsXMLLiterals.E_SALES_FLOOR_LOCATIONS)) && 
				!YFCObject.isVoid(responseItemLocations.getElementsByTagName(KohlsXMLLiterals.E_SALES_FLOOR_LOCATIONS).item(0))) {
			Element salesfloorLocations = (Element)responseItemLocations.getElementsByTagName(KohlsXMLLiterals.E_SALES_FLOOR_LOCATIONS).item(0);		
			salesfloorLocationList = salesfloorLocations.getElementsByTagName(KohlsXMLLiterals.E_SALES_FLOOR_LOCATION);
			for(int i = 0; i <salesfloorLocationList.getLength(); i++){
				Element currentSalesFloorLocation = (Element)salesfloorLocationList.item(i);
				if (!YFCObject.isVoid(currentSalesFloorLocation.getElementsByTagName(KohlsXMLLiterals.E_FULL_DESC_CODE)) &&
						!YFCObject.isVoid(currentSalesFloorLocation.getElementsByTagName(KohlsXMLLiterals.E_FULL_DESC_CODE).item(0))) {
					Element fullDescriptionCode = (Element)currentSalesFloorLocation.getElementsByTagName(KohlsXMLLiterals.E_FULL_DESC_CODE).item(0);
					floorpad += fullDescriptionCode.getTextContent() + "\n";
				}
			}
		}
		
		if(!YFCObject.isVoid(responseItemLocations) && !YFCObject.isVoid(responseItemLocations.getElementsByTagName(KohlsXMLLiterals.E_STOCK_ROOM_LOCATIONS)) &&
				!YFCObject.isVoid(responseItemLocations.getElementsByTagName(KohlsXMLLiterals.E_STOCK_ROOM_LOCATIONS).item(0))) {
			Element stockRoomLocations = (Element)responseItemLocations.getElementsByTagName(KohlsXMLLiterals.E_STOCK_ROOM_LOCATIONS).item(0);
			stockRoomLocationList = stockRoomLocations.getElementsByTagName(KohlsXMLLiterals.E_STOCK_ROOM_LOCATION);
		}
		
		if(!YFCObject.isVoid(upcs) && !YFCObject.isVoid(upcs.getElementsByTagName(KohlsXMLLiterals.E_UPC))) {
			NodeList upcElements = upcs.getElementsByTagName(KohlsXMLLiterals.E_UPC);
			for(int i = 0; i < upcElements.getLength(); i++) {
				Element upcElement = (Element) upcElements.item(i);
				Element idTypeElement = SCXmlUtil.getChildElement(upcElement, KohlsXMLLiterals.ID_TYPE);
				String idType = idTypeElement.getTextContent();
				if(YFCObject.equals(idType, "Kohls")) {
					Element idElement = SCXmlUtil.getChildElement(upcElement, KohlsXMLLiterals.ID);
					kohlsUPC = idElement.getTextContent();
					break;
				}
			}
			
		}
		
		YFCDocument yfcShipmentLinesDoc = YFCDocument.getDocumentFor(shipmentLinesDoc);
		YFCElement shipmentElement = yfcShipmentLinesDoc.getDocumentElement();
		YFCElement shipmentLines = shipmentElement.getChildElement(KohlsConstant.E_SHIPMENT_LINES);
		YFCElement shipmentLine = shipmentLines.getChildElement(KohlsConstant.E_SHIPMENT_LINE);
		
		shipmentLine.setAttribute(KohlsXMLLiterals.E_BRAND, brand);
		shipmentLine.setAttribute(KohlsXMLLiterals.E_FLOORPAD, floorpad);
		shipmentLine.setAttribute(KohlsXMLLiterals.E_SKU_STATUS, skuStatus);
		shipmentLine.setAttribute(KohlsXMLLiterals.A_KOHLS_UPC, kohlsUPC);
		shipmentLine.setAttribute(KohlsXMLLiterals.A_BOX_ID, boxID);
		
		if(!YFCCommon.isVoid(salesfloorLocationList)) {
			YFCElement yfcSalesFloorLocations = shipmentLine.createChild("SalesFloorLocations");
			for(int i = 0; i < salesfloorLocationList.getLength(); i++) {
				Element currentSalesFloorLocation = (Element) salesfloorLocationList.item(i);
				YFCElement salesFloorLocation = yfcSalesFloorLocations.createChild("SalesFloorLocation");
				
				Element eSignsElement = SCXmlUtil.getChildElement(currentSalesFloorLocation, KohlsXMLLiterals.E_ESIGNS);
				Element eSignElement = SCXmlUtil.getChildElement(eSignsElement, KohlsXMLLiterals.E_ESIGN_LCASE);
				Element eSignIDElement = SCXmlUtil.getChildElement(eSignElement, KohlsXMLLiterals.E_ESIGN_ID);
				
				Element fullDescriptionCodeElement = SCXmlUtil.getChildElement(currentSalesFloorLocation, KohlsXMLLiterals.E_FULL_DESC_CODE);
				Element sectionMaxElement = SCXmlUtil.getChildElement(currentSalesFloorLocation, KohlsXMLLiterals.E_SECTION_MAX);
				
				String eSignID = eSignIDElement.getTextContent();
				String fullDescriptionCode = fullDescriptionCodeElement.getTextContent();
				String sectionMax = sectionMaxElement.getTextContent();
				
				String eSignLocation = fullDescriptionCode + " of " + sectionMax;
				
				salesFloorLocation.setAttribute(KohlsXMLLiterals.A_ESIGN_ID, eSignID);
				salesFloorLocation.setAttribute(KohlsXMLLiterals.A_ESIGN_LOCATION, eSignLocation);
			}
		}
		
		
		if(!YFCCommon.isVoid(stockRoomLocationList)) {
			YFCElement yfcStockRoomLocations = shipmentLine.createChild("StockRoomLocations");
			for(int i = 0; i < stockRoomLocationList.getLength(); i++) {
				Element currentStockRoomLocation = (Element) stockRoomLocationList.item(i);
				YFCElement stockRoomLocation = yfcStockRoomLocations.createChild("StockRoomLocation");
				
				Element quantityElement = SCXmlUtil.getChildElement(currentStockRoomLocation, KohlsXMLLiterals.E_QUANTITY);
				Element locationElement = SCXmlUtil.getChildElement(currentStockRoomLocation, KohlsXMLLiterals.E_FULL_DESC_CODE);
				Element barcodeNumberElement = SCXmlUtil.getChildElement(currentStockRoomLocation, KohlsXMLLiterals.E_BAR_CODE_NUM);
				
				String quantity = quantityElement.getTextContent();
				String location = locationElement.getTextContent();
				String barcode = barcodeNumberElement.getTextContent();
				
				stockRoomLocation.setAttribute(KohlsXMLLiterals.A_LOCATION, location);
				stockRoomLocation.setAttribute(KohlsXMLLiterals.A_QUANTITY, quantity);
				stockRoomLocation.setAttribute(KohlsXMLLiterals.A_BARCODE, barcode);
			}
		}
		
		return yfcShipmentLinesDoc.getDocument();
	}
	
	/*******************************
	 * @author: Adam Dunmars
	 * This function gets information from external systems to display on the 
	 * Item Details Screen.
	 * @param env
	 * @param inputXML
	 * @return
	 * @throws Exception
	 ******************************/
	public Document getItemDetails(YFSEnvironment env, Document inputXML) throws Exception {
		String shipNode = null;
		Document returnDoc = null;
		Document getShipmentListOutputDoc = null;
		Document givGetInventoryOutputDoc = null;
		Document mlsGetItemLocationInputDoc = null;
		Document mlsGetItemLocationOutputDoc = null;
		
		log.debug("Input to getShipmentList: "+XmlUtils.getString(inputXML));
		
		env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS, KOHLS_GET_SHIPMENT_DETAILS_TEMPLATE);
		
		//env.setApiTemplate(KohlsConstant.GET_SHIPMENT_LIST_API, KOHLS_GET_SHIPMENT_LIST_TEMPLATE);
		getShipmentListOutputDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.API_GET_SHIPMENT_DETAILS, inputXML);
		env.clearApiTemplates();
		
		log.debug("Output to getShipmentList: "+XmlUtils.getString(getShipmentListOutputDoc));
		
		Document newReturnDoc = XmlUtils.getDocumentBuilder().newDocument();
		Element shipmentElement= getShipmentListOutputDoc.getDocumentElement();
		shipNode = shipmentElement.getAttribute(KohlsConstant.E_SHIP_NODE);
		Element newRoot = (Element)newReturnDoc.importNode(shipmentElement, true);
		newReturnDoc.appendChild(newRoot);
		
		log.debug("New transformed input for GIV: "+XmlUtils.getString(newReturnDoc));
		
		givGetInventoryOutputDoc = KOHLSBaseApi.invokeService(env, KohlsConstant.SERVICE_GET_INV_STOREPICK_WS, newReturnDoc);
		returnDoc = setGIVQuantities(givGetInventoryOutputDoc, newReturnDoc);
		
		log.debug("Output after GIV transformation: "+XmlUtils.getString(returnDoc));
		
		Map<String,Double> mapOrderLineQty = shipmentItemLvlConsolidation(env, returnDoc);
		mlsGetItemLocationInputDoc = mergeInvShipmentLines(env, givGetInventoryOutputDoc, mapOrderLineQty);
		
		if (log.isDebugEnabled()) {
			log.debug(SCXmlUtil.getString(mlsGetItemLocationInputDoc));
		}

		//Appending ShipNode to MLS Input for making Dynamic URL
		Element eleInputMLSGetItemLocDet = mlsGetItemLocationInputDoc.getDocumentElement();
		eleInputMLSGetItemLocDet.setAttribute(KohlsXMLLiterals.A_SHIP_NODE, shipNode);

		//MLS-STUB Can be added as needed - BRK
		//getMlsResponseDoc= stubMLSRespDoc(env , docInputMLSGetItemLocDet);
		// set the MLS endpoint based on the shipnode
		StringBuffer endpointURL = new StringBuffer("");
		// grab the end point properties minus the shipnode
		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_PREFIX))) {
			endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_PREFIX));
		} else {
			// set to default
			endpointURL.append("http://isp");
		}
		// see if we have any translation shipnode values
		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + shipNode))) {
			// use the translated value
			endpointURL.append(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + shipNode));
		}  else if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_USE_DEFAULT_STORE)) 
				&& "Y".equalsIgnoreCase(YFSSystem.getProperty(KohlsConstant.MLS_USE_DEFAULT_STORE))) {
			// use the default store
			endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_DEFAULT_STORE_LOOKUP));
		} else {
			// no translation
			String formattedShipNode = shipNode;
			while (formattedShipNode.toString().length() < 4) {
				formattedShipNode = "0" + formattedShipNode;
			}
			endpointURL.append(formattedShipNode);
		}
		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_SUFFIX))) {
			endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_SUFFIX));
		} else {
			// set to default
			endpointURL.append("e.st.ad.kohls.com/KohlsInventoryDemandService/InventoryDemandService.svc");
		}
		env.setTxnObject(KohlsConstant.V_ENDPOINTURL, endpointURL.toString());
		
		// set the soap action information
		StringBuffer soapActionOperation = new StringBuffer("");
		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTIONURL_PROPERTY))) {
			soapActionOperation.append(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTIONURL_PROPERTY));
		} else {
			// set to default
			soapActionOperation.append("http://tempuri.org/IInventoryDemandService/");
		}
		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTION_GETITEMLOCATIONS))) {
			soapActionOperation.append(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTION_GETITEMLOCATIONS));
		} else {
			// set to default
			soapActionOperation.append("GetItemLocations");
		}
		env.setTxnObject(KohlsConstant.V_SOAPACTION, soapActionOperation.toString());
	
		
		//Making an MLS Web service call for fetching Item Location details
		mlsGetItemLocationOutputDoc=KOHLSBaseApi.invokeService(env, KohlsConstant.SERVICE_GET_ITEM_LOC_STOREPICK_WS ,mlsGetItemLocationInputDoc);
		returnDoc = getMLSInformation(mlsGetItemLocationOutputDoc, returnDoc);
		
		log.debug("After MLS informatoin added "+XmlUtils.getString(returnDoc));
		
		returnDoc = KOHLSBaseApi.invokeService(env, "KohlsOpenAPIItemImage", returnDoc);
		log.debug("After OpenAPI call"+XmlUtils.getString(returnDoc));
	
		return returnDoc;
	}
	
	/*********************************
	 * @author: Adam Dunmars
	 * This function will take the document returned from GIV and set the attributes for Stockroom and or Salesfloor Quantity
	 * if they are available.
	 * @param givOutputDocument
	 * @param shipmentLinesDocument
	 * @return
	 *********************************/
	public Document setGIVQuantities(Document givOutputDocument, Document shipmentLinesDocument) {
		YFCDocument yfcShipmentLinesDocument = YFCDocument.getDocumentFor(shipmentLinesDocument);
		YFCElement yfcShipmentElement = yfcShipmentLinesDocument.getDocumentElement();
		//YFCElement yfcShipmentElement = yfcShipmentsElement.getChildElement(KohlsConstant.E_SHIPMENT);
		YFCElement yfcShipmentLines = yfcShipmentElement.getChildElement(KohlsConstant.E_SHIPMENT_LINES);
		YFCNodeList<YFCElement> yfcShipmentLineElements = yfcShipmentLines.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
		
		YFCDocument yfcGIVOutputDocument = YFCDocument.getDocumentFor(givOutputDocument);
		YFCElement givRootElement = yfcGIVOutputDocument.getDocumentElement();
		YFCElement givReturnElement = givRootElement.getChildElement("return");
		YFCNodeList<YFCElement> yfcItemElements = givReturnElement.getElementsByTagName("ns4:Item");
		
		for(int i = 0; i < yfcShipmentLineElements.getLength(); i++){
			YFCElement currentShipmentLine = yfcShipmentLineElements.item(i);
			YFCElement selectedShipmentLine = null;
			for(int j = 0; j < yfcItemElements.getLength(); j++) {
				if(currentShipmentLine.getAttribute("ItemID").equals(yfcItemElements.item(j).getAttribute("ItemID"))) {
					selectedShipmentLine = yfcItemElements.item(j);
				}
			} 
			if(!YFCObject.isVoid(selectedShipmentLine) && !YFCObject.isVoid(selectedShipmentLine.getChildElement("ns4:Supplies"))) {
				YFCNodeList<YFCElement> supplyTypes = selectedShipmentLine.getChildElement("ns4:Supplies").getElementsByTagName("ns4:InventorySupply");
				for(int k = 0; k < supplyTypes.getLength(); k++) {
					YFCElement currentSupplyType = supplyTypes.item(k);
					String supplyType = currentSupplyType.getAttribute("SupplyType");
					String quantity = currentSupplyType.getAttribute("Quantity");
					
					String supplyTypeAttribute = (supplyType.equals("STOCK_ROOM.ex")) ? "StockRoomQuantity" : "SalesFloorQuantity";
					currentShipmentLine.setAttribute(supplyTypeAttribute, quantity);
				}
			}
			
		}
		return shipmentLinesDocument;
		//return yfcShipmentLinesDocument.getDocument();
	}
	
	/********************************************************
	 * Parsing MLS Output and obtaining Item and its corresponding details
	 * @param env
	 * @param inDoc
	 * @return
	 ********************************************************/
	public  Document getItemLocationDetails( YFSEnvironment env, Document inDoc) throws Exception {

		if (inDoc != null) {

			Element eleGetItemLocationsResult = KohlsXMLUtil.getElementByXpath(inDoc, "//" + KohlsXMLLiterals.E_GET_ITEM_LOCATIONS_RESULT);
			Element eleResponse = SCXmlUtil.getChildElement(eleGetItemLocationsResult, KohlsXMLLiterals.E_RESPONSE);
			Element eleData = SCXmlUtil.getChildElement(eleResponse, KohlsXMLLiterals.E_DATA);
			Element eleItemLocations = SCXmlUtil.getChildElement(eleData, KohlsXMLLiterals.E_ITEMLOCATIONS);
			Element eleRespItemLocations = null;
			Element eleSalesFloorLocations = null;
			Element eleSalesFloorLocation = null;
			Element eleStockroomLocations = null;
			Element eleStockroomLocation = null;
			Iterator<Element> itrResponseItemLocations = SCXmlUtil.getChildren(eleItemLocations);
			ArrayList<Element> salesFloorLocationsList = null;
			ArrayList<Element> salesroomLocationsList = null;
			String strSrIdDesc = null;
			String strSrIdBarcode = null;
			String strSfIdDesc = null;
			Element eleEsigns = null;
			Element eleEsign = null;
			ArrayList<Element> esignList = null;
			String strEsignIds = null;
			Element eleItem = null;
			Element eleLocs = null;
			String strItemID = null;
			String strSfId = null;
			Document itemsDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ITEMS);
			Element eleItems = itemsDoc.getDocumentElement();

			while(itrResponseItemLocations.hasNext()){
				eleRespItemLocations = itrResponseItemLocations.next();
				eleItem = SCXmlUtil.getChildElement(eleRespItemLocations, KohlsXMLLiterals.E_ITEM);

				if(eleItem != null){
					strItemID = eleItem.getElementsByTagName(KohlsXMLLiterals.E_SKU).item(0).getTextContent();
					eleItem = SCXmlUtil.createChild(eleItems, KohlsXMLLiterals.E_ITEM);
					eleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, strItemID);
					eleLocs = SCXmlUtil.createChild(eleItem, "Locations");
				}

				eleSalesFloorLocations = SCXmlUtil.getChildElement(eleRespItemLocations, KohlsXMLLiterals.E_SALES_FLOOR_LOCATIONS);
				eleStockroomLocations = SCXmlUtil.getChildElement(eleRespItemLocations, KohlsXMLLiterals.E_STOCK_ROOM_LOCATIONS);

				if(eleSalesFloorLocations != null){
					salesFloorLocationsList = SCXmlUtil.getChildren(eleSalesFloorLocations, KohlsXMLLiterals.E_SALES_FLOOR_LOCATION);
					for(int i=0; i< salesFloorLocationsList.size(); i++){
						eleSalesFloorLocation = salesFloorLocationsList.get(i);

						eleEsigns = SCXmlUtil.getChildElement(eleSalesFloorLocation, KohlsXMLLiterals.E_ESIGNS);
						if(eleEsigns != null){
							esignList = SCXmlUtil.getChildren(eleEsigns, KohlsXMLLiterals.E_ESIGN);
							for(int j=0; j< esignList.size(); j++){
								eleEsign = esignList.get(j);
								if(j==0)
									strEsignIds = eleEsign.getElementsByTagName(KohlsXMLLiterals.A_ESIGN_ID).item(0).getTextContent();
								else
									strEsignIds += ","+eleEsign.getElementsByTagName(KohlsXMLLiterals.A_ESIGN_ID).item(0).getTextContent();
							}

						}

						strSfId = eleSalesFloorLocation.getElementsByTagName(KohlsXMLLiterals.E_LOCATION_ID).item(0).getTextContent();
						strSfIdDesc = eleSalesFloorLocation.getElementsByTagName("FullDescriptionCode").item(0).getTextContent();

						Element eleLoc =  SCXmlUtil.createChild(eleLocs, "Location");
						eleLoc.setAttribute("LocationType", "SalesFloor");
						if(strSfId != null && strSfIdDesc != null) {
							eleLoc.setAttribute("LocationID", strSfId);
							eleLoc.setAttribute("LocationDesc", strSfIdDesc);
						}
						else if(strSfId == null && strSfIdDesc == null) {
							eleLoc.setAttribute("LocationID", "");
							eleLoc.setAttribute("LocationDesc", "");
						}
					}
				}
				if(eleStockroomLocations != null){
					salesroomLocationsList = SCXmlUtil.getChildren(eleStockroomLocations, KohlsXMLLiterals.E_STOCK_ROOM_LOCATION);
					for(int i=0; i< salesroomLocationsList.size(); i++){
						eleStockroomLocation = salesroomLocationsList.get(i);

						strSrIdBarcode = eleStockroomLocation.getElementsByTagName(KohlsXMLLiterals.E_BAR_CODE_NUM).item(0).getTextContent();
						strSrIdDesc = eleStockroomLocation.getElementsByTagName(KohlsXMLLiterals.E_FULL_DESC_CODE).item(0).getTextContent();

						Element eleLoc =  SCXmlUtil.createChild(eleLocs, "Location");
						eleLoc.setAttribute("LocationType", "StockRoom");
						if(strSrIdBarcode != null && strSrIdDesc != null) {
							eleLoc.setAttribute("LocationID", strSrIdBarcode);
							eleLoc.setAttribute("LocationDesc", strSrIdDesc);
						}
						else if(strSfId == null && strSfIdDesc == null) {
							eleLoc.setAttribute("LocationID", "");
							eleLoc.setAttribute("LocationDesc", "");
						}
					}
				}
			}
			log.debug("Items:"+SCXmlUtil.getString(eleItems));

			if(eleItem != null)
				return itemsDoc;
		}
		return null;
	}
	
	/**
	 * 
	 * @param env
	 * @param shipmentKey
	 * @param shipmentLineNo
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	public String getLocationDesc(YFSEnvironment env, String shipmentKey, String shipmentLineNo, String locationId) throws Exception {
		Document shipmentLineListDoc = getShipmentLineList(env, shipmentKey, shipmentLineNo);
		if(shipmentLineListDoc != null){
			Element eleShipmentLines = shipmentLineListDoc.getDocumentElement();
			Iterator<Element> itrShipmentLine = SCXmlUtil.getChildren(eleShipmentLines);
			Element eleShipmenLine = null;
			Element eleExtn = null;
			String strExtnMlsLocationIdDesc = null;
			String[] mlsLocationIdDescAry = null;
			while(itrShipmentLine.hasNext()){
				eleShipmenLine = itrShipmentLine.next();
				eleExtn = SCXmlUtil.getChildElement(eleShipmenLine, KohlsXMLLiterals.E_EXTN);
				if(eleExtn != null){
					strExtnMlsLocationIdDesc = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_MLS_LOC_ID_DESC);
					if(strExtnMlsLocationIdDesc != null){
						if(locationId != null){
							if(strExtnMlsLocationIdDesc.indexOf(";") != -1){
								mlsLocationIdDescAry = strExtnMlsLocationIdDesc.split(";");
								for(String value: mlsLocationIdDescAry){
									if(value.contains(locationId))
										return value.substring(value.indexOf(":")+1, value.length());
									else
										return null;
								}
							}
							else{
								if(strExtnMlsLocationIdDesc.contains(locationId))							   
								   return strExtnMlsLocationIdDesc.substring(strExtnMlsLocationIdDesc.indexOf(":")+1, strExtnMlsLocationIdDesc.length());
								else
									return null;
							}

						}
						else{
							return null;
						}
					}
					else
						return null;
				}
			}
		}

		return null;
	}
	
	/**
	 * 
	 * @param env
	 * @param shipmentKey
	 * @param shipmentLineNo
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	public String getLocationBarcode(YFSEnvironment env, String shipmentKey, String shipmentLineNo, String locationId) throws Exception {
				
		Document shipmentLineListDoc = getShipmentLineList(env, shipmentKey, shipmentLineNo, KohlsConstant.API_GET_SHIPMENT_LINE_LIST_BARCODE_TEMPLATE);
		
		if(shipmentLineListDoc != null){
			Element eleShipmentLines = shipmentLineListDoc.getDocumentElement();
			Iterator<Element> itrShipmentLine = SCXmlUtil.getChildren(eleShipmentLines);
			Element eleShipmenLine = null;
			Element eleExtn = null;
			String strExtnMlsLocationIdBarcode = null;
			String[] mlsLocationIdDescAry = null;
			while(itrShipmentLine.hasNext()){
				eleShipmenLine = itrShipmentLine.next();
				eleExtn = SCXmlUtil.getChildElement(eleShipmenLine, KohlsXMLLiterals.E_EXTN);
				if(eleExtn != null){
					strExtnMlsLocationIdBarcode = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_MLS_LOC_ID_BARCODE);
					if(strExtnMlsLocationIdBarcode != null){
						if(locationId != null){
							if(strExtnMlsLocationIdBarcode.indexOf(";") != -1){
								mlsLocationIdDescAry = strExtnMlsLocationIdBarcode.split(";");
								for(String value: mlsLocationIdDescAry){
									if(value.contains(locationId))
										return value.substring(value.indexOf(":")+1, value.length());
									else
										return null;
								}
							}
							else{
								if(strExtnMlsLocationIdBarcode.contains(locationId))							   
								   return strExtnMlsLocationIdBarcode.substring(strExtnMlsLocationIdBarcode.indexOf(":")+1, strExtnMlsLocationIdBarcode.length());
								else
									return null;
							}

						}
						else{
							return null;
						}
					}
					else
						return null;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param env
	 * @param shipmentKey
	 * @param shipmentLineNo
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	public String getLocationEsignIDs(YFSEnvironment env, String shipmentKey, String shipmentLineNo, String locationId) throws Exception{
		
		Document shipmentLineListDoc = getShipmentLineList(env, shipmentKey, shipmentLineNo);	
		if(shipmentLineListDoc != null){
			Element eleShipmentLines = shipmentLineListDoc.getDocumentElement();
			Iterator<Element> itrShipmentLine = SCXmlUtil.getChildren(eleShipmentLines);
			Element eleShipmenLine = null;
			Element eleExtn = null;
			String strExtnMlsLocationIdEsignID = null;
			String[] mlsLocationIdDescAry = null;
			while(itrShipmentLine.hasNext()){
				eleShipmenLine = itrShipmentLine.next();
				eleExtn = SCXmlUtil.getChildElement(eleShipmenLine, KohlsXMLLiterals.E_EXTN);
				if(eleExtn != null){
					strExtnMlsLocationIdEsignID = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_MLS_LOC_ID_ESIGNID);
					if(strExtnMlsLocationIdEsignID != null){
						if(locationId != null){
							if(strExtnMlsLocationIdEsignID.indexOf(";") != -1){
								mlsLocationIdDescAry = strExtnMlsLocationIdEsignID.split(";");
								for(String value: mlsLocationIdDescAry){
									if(value.contains(locationId))
										return value.substring(value.indexOf(":")+1, value.length());
									else
										return null;
								}
							}
							else{
								if(strExtnMlsLocationIdEsignID.contains(locationId))							   
								   return strExtnMlsLocationIdEsignID.substring(strExtnMlsLocationIdEsignID.indexOf(":")+1, strExtnMlsLocationIdEsignID.length());
								else
									return null;
							}

						}
						else{
							return null;
						}
					}
					else
						return null;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param env
	 * @param shipmentKey
	 * @param shipmentLineNo
	 * @param templateName
	 * @return
	 * @throws Exception
	 */
	public Document getShipmentLineList(YFSEnvironment env, String shipmentKey, String shipmentLineNo, String templateName) throws Exception{

		Document inDocGetShpmntLineList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT_LINE);
		Element eleGetShpmntLineList = inDocGetShpmntLineList.getDocumentElement();

		eleGetShpmntLineList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, shipmentKey);
		eleGetShpmntLineList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, shipmentLineNo);

		Document outDocGetShpmntLineList = null;
		outDocGetShpmntLineList = KohlsCommonUtil.invokeAPI(env, templateName, KohlsConstant.GET_SHIPMENT_LINE_LIST_API, inDocGetShpmntLineList);
		return outDocGetShpmntLineList;
	}
	
	/**
	 * 
	 * @param env
	 * @param shipmentKey
	 * @param shipmentLineNo
	 * @return
	 * @throws Exception
	 */
	public Document getShipmentLineList(YFSEnvironment env, String shipmentKey, String shipmentLineNo) throws Exception{

		Document inDocGetShpmntLineList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT_LINE);
		Element eleGetShpmntLineList = inDocGetShpmntLineList.getDocumentElement();

		eleGetShpmntLineList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, shipmentKey);
		eleGetShpmntLineList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, shipmentLineNo);

		Document outDocGetShpmntLineList = null;
		outDocGetShpmntLineList = KohlsCommonUtil.invokeAPI(env, KohlsConstant.GET_SHIPMENT_LINE_LIST_API, inDocGetShpmntLineList);
		return outDocGetShpmntLineList;
	}


	/**
	 * Forming input to MLS by consolidating Shipment Map and GIV Output and adding appropriate Filter
	 * @param env
	 * @param docInventoryXmlOutput
	 * @param mapOrderLineQty
	 * @return
	 */
	public Document mergeInvShipmentLines(YFSEnvironment env, Document docInventoryXmlOutput, Map<String, Double> mapOrderLineQty) {

		Element eleInventoryXmlOutput = (Element) docInventoryXmlOutput.getDocumentElement().getFirstChild();
		// default GIV response namespaces
		// ns2="http://webservices.inventory.giv.kohls.com/documentation/GetLocationInventorySupply/getLocationInventorySupply/input" 
		// ns3="http://webservices.inventory.giv.kohls.com/" 
		// ns4="http://webservices.inventory.giv.kohls.com/documentation/GetLocationInventorySupply/getLocationInventorySupply/output">
	    String outputNameSpace = "http://webservices.inventory.giv.kohls.com/documentation/GetLocationInventorySupply/getLocationInventorySupply/output";
	    if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_RESPONSE_NAMESPACE_PROPERTY))) {
	    	// use the namespace from the property file
	    	outputNameSpace = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_RESPONSE_NAMESPACE_PROPERTY);
	    }
	    NodeList nodeItemList = eleInventoryXmlOutput.getElementsByTagNameNS(outputNameSpace,KohlsXMLLiterals.E_ITEM);

		
		Map <String, String> mapItemSupply = new HashMap<String, String>();

		for (int i=0; i<nodeItemList.getLength(); i++) {
			Node eleItem = (Node) nodeItemList.item(i);
			String ItemId = eleItem.getAttributes().getNamedItem(KohlsXMLLiterals.A_ITEM_ID).getNodeValue();
			// loop through the child nodes to get the SUPPLY element
			NodeList itemChildNodes = eleItem.getChildNodes();
			for (int j=0; j < itemChildNodes.getLength(); j++) {
				if (itemChildNodes.item(j).getNodeType() == Node.ELEMENT_NODE && 
						itemChildNodes.item(j).getNodeName().indexOf(KohlsXMLLiterals.E_SUPPLIES) > -1) {
					// we've found the supplies node
					NodeList inventorySupplies = itemChildNodes.item(j).getChildNodes();
					String LocationFilterTypeEnum=KohlsConstant.V_N;
					for (int k=0; k< inventorySupplies.getLength(); k++) {
						if (inventorySupplies.item(k).getNodeType() == Node.ELEMENT_NODE && 
								inventorySupplies.item(k).getNodeName().indexOf(KohlsXMLLiterals.E_INVENTORY_SUPPLY) > -1) {
							Element eleSupplyType = (Element)inventorySupplies.item(j);
							double Qty = Double.parseDouble(eleSupplyType.getAttribute(KohlsXMLLiterals.A_QUANTITY));
							if (Qty > 0) {
								LocationFilterTypeEnum=KohlsConstant.V_Y;
								break;
							}
						}
					}
					mapItemSupply.put(ItemId, LocationFilterTypeEnum);
				}
			}
		}

		Document docConsolShipmentFilter = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element eleConsolShipmentFilter = docConsolShipmentFilter.getDocumentElement();
		
		// get the prefix information for the namespaces and login information if present
		String yfsEndPointUser = "";
		String yfsEndPointPassword = "";
		String environmentElement = "";
		Element environmentNode = null;
		yfsEndPointUser = YFSSystem.getProperty(KohlsConstant.END_POINT_USER_MLS);
		yfsEndPointPassword = YFSSystem.getProperty(KohlsConstant.END_POINT_PWD_MLS);
		environmentElement = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_ENV_ELEMENT_PROPERTY);
		if (!YFCObject.isVoid(yfsEndPointUser) && !YFSObject.isVoid(yfsEndPointPassword) && !YFSObject.isVoid(environmentElement)) {
			// we have an user id and password, need to construct the environment element
			environmentNode = docConsolShipmentFilter.createElement(environmentElement);
			// add the user id and password elements
			Element userId = docConsolShipmentFilter.createElement(KohlsConstant.A_USER_ID);
			// set the userid value from the properties file
			userId.setTextContent(yfsEndPointUser);
			// add the userId
			environmentNode.appendChild(userId);
			Element password = docConsolShipmentFilter.createElement(KohlsConstant.A_PASSWORD);
			// set the password value from the properties file
			password.setTextContent(yfsEndPointPassword);
			// add the password
			environmentNode.appendChild(password);
			// add the environment
			eleConsolShipmentFilter.appendChild(environmentNode);
		}

		Document docShipmentBackOrder = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element eleShipmentBackOrder = docShipmentBackOrder.getDocumentElement();

		Set<String> reqLineKeys = mapOrderLineQty.keySet();
		for (String reqLineKey:reqLineKeys) {
			String delim = "-";
			String  tokenValues [] = reqLineKey.split(delim);
			String orderLineKey = tokenValues[0];
			String reqItemId = tokenValues[1];
			String quantity = mapOrderLineQty.get(reqLineKey).toString();
			String backOrderFlag = "Y";
			String userId ="";

			Set<String> itemSupplyKeys = mapItemSupply.keySet();
			for (String itemSupplyKey : itemSupplyKeys) {
				if ( (itemSupplyKey.equals(reqItemId) || itemSupplyKey.equalsIgnoreCase(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + reqItemId)))&& mapItemSupply.get(itemSupplyKey).equals("Y"))
				{
					Element eleOrderLines = SCXmlUtil.createChild(eleConsolShipmentFilter, KohlsXMLLiterals.E_ORDER_LINES);
					eleOrderLines.setAttribute(KohlsXMLLiterals.A_LOCATION_FILTER_ENUM, KohlsConstant.V_Y);
					userId = env.getUserId();
					if (userId == null || userId.equals(""))
						 userId = "KohlsBOPUSPickProcessUtil";
					eleOrderLines.setAttribute(KohlsXMLLiterals.A_USER_ID, env.getUserId());
					Element eleOrderLine = SCXmlUtil.createChild(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
					eleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, orderLineKey);
					// this is for testing purposes, normally should be directly item id and not translation
					if (!YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + reqItemId))) {
						eleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID,  YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + reqItemId));
					} else {
						eleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID,  reqItemId);
					}
					eleOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, String.valueOf(Double.valueOf(quantity).intValue()) );
					backOrderFlag="N";
				}
			}
			if (backOrderFlag.equals("Y"))
			{
				Element eleOrderLines = SCXmlUtil.createChild(eleShipmentBackOrder, KohlsXMLLiterals.E_ORDER_LINES);
				eleOrderLines.setAttribute(KohlsXMLLiterals.A_LOCATION_FILTER_ENUM, KohlsConstant.V_N);
				eleOrderLines.setAttribute(KohlsXMLLiterals.A_USER_ID, userId);
				Element eleOrderLine = SCXmlUtil.createChild(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, orderLineKey);
				// this is for testing purposes, normally should be directly item id and not translation
				if (!YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + reqItemId))) {
					eleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID,  YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + reqItemId));
				} else {
					eleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID,  reqItemId);
				}
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, String.valueOf(Double.valueOf(quantity).intValue()) );
			}
		}
		System.out.println("MLS input:" + SCXmlUtil.getString(docConsolShipmentFilter));
		log.debug(SCXmlUtil.getString(docConsolShipmentFilter));
		log.debug(SCXmlUtil.getString(docShipmentBackOrder));
		return docConsolShipmentFilter;
	}
	

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws IllegalArgumentException
	 */
	public  Document getInvDetailsGIV(YFSEnvironment env, Document inXML)
			throws IllegalArgumentException {
				Document getGIVResponseDoc = null;
				try{
					//Element eleShipments =inXML.getDocumentElement();

					Element eleShipment = inXML.getDocumentElement();//SCXmlUtil.getChildElement(eleShipments, KohlsXMLLiterals.E_SHIPMENT);

					String strEnterpriseCode =eleShipment.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);
					String strShipNode =eleShipment.getAttribute(KohlsXMLLiterals.E_SHIP_NODE);

					String strItemID = null;
					String strUnitOfMeasure = null;
					String strProductClass = null;

					Element eleShipLines = (Element) eleShipment.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINES).item(0);

					NodeList nlShipmentLines = eleShipLines.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
					
					// get the prefix information for the namespaces and login information if present
					String yfsEndPointUser = "";
					String yfsEndPointPassword = "";
					String inputNamespacePrefix = "";
					String messageNamespacePrefix = "";
					String environmentElement = "";
					String messageElement = "";
					String inputElement = "";
					yfsEndPointUser = YFSSystem.getProperty(KohlsConstant.END_POINT_USER_GIV_LOC_INV_SUPPLY);
					yfsEndPointPassword = YFSSystem.getProperty(KohlsConstant.END_POINT_PWD_GIV_LOC_INV_SUPPLY);
					environmentElement = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_ENV_ELEMENT_PROPERTY);
					if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_INPUT_PREFIX_PROPERTY))) {
						inputNamespacePrefix = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_INPUT_PREFIX_PROPERTY);
					}
					if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_MESSAGE_PREFIX_PROPERTY))) {
						messageNamespacePrefix = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_MESSAGE_PREFIX_PROPERTY);
					}
					messageElement = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_MESSAGE_ELEMENT_PROPERTY);
					inputElement = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_INPUT_ELEMENT_PROPERTY);
					Document docInvSupplies = SCXmlUtil.createDocument(messageNamespacePrefix + ":" + messageElement);
					Element environmentNode = null;
					
					if (!YFCObject.isVoid(nlShipmentLines) && nlShipmentLines.getLength() > 0) {
						// only need to do this if we have shipment lines
						if (!YFCObject.isVoid(yfsEndPointUser) && !YFSObject.isVoid(yfsEndPointPassword) && !YFSObject.isVoid(environmentElement)) {
							// we have an user id and password, need to construct the environment element
							environmentNode = docInvSupplies.createElement(environmentElement);
							// add the user id and password elements
							Element userId = docInvSupplies.createElement(KohlsConstant.A_USER_ID);
							// set the userid value from the properties file
							userId.setTextContent(yfsEndPointUser);
							// add the userId
							environmentNode.appendChild(userId);
							Element password = docInvSupplies.createElement(KohlsConstant.A_PASSWORD);
							// set the password value from the properties file
							password.setTextContent(yfsEndPointPassword);
							// add the password
							environmentNode.appendChild(password);
							// add the environment
							docInvSupplies.getDocumentElement().appendChild(environmentNode);
						}
					}
					
					Element eleInvSupplies = docInvSupplies.getDocumentElement();
					// append the input element
					Element inventorySuppliesInput = docInvSupplies.createElement(inputElement);
					for(int i = 0; i < nlShipmentLines.getLength(); i++){
						Element eleShipmentLine = (Element)nlShipmentLines.item(i);
						if(!YFCCommon.isVoid(eleShipmentLine)){
							strItemID=eleShipmentLine.getAttribute(KohlsXMLLiterals.ITEM_ID);
							strUnitOfMeasure=eleShipmentLine.getAttribute(KohlsXMLLiterals.A_UOM);
							strProductClass=eleShipmentLine.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS);

							Element eleInventorySupply=docInvSupplies.createElement(inputNamespacePrefix + ":" + KohlsXMLLiterals.E_INV_SUPPLY );
							
							// code put here for translating organizations for testing purposes
							if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + strEnterpriseCode))) {
								eleInventorySupply.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + strEnterpriseCode));
							} else {
								// use the default value
								eleInventorySupply.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, strEnterpriseCode);
							}
							if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + strShipNode))) {
								eleInventorySupply.setAttribute(KohlsXMLLiterals.A_SHIPNODE, YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + strShipNode));
							} else {
								// use the default value
								eleInventorySupply.setAttribute(KohlsXMLLiterals.A_SHIPNODE, strShipNode);
							}
							if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + strItemID))) {
								eleInventorySupply.setAttribute(KohlsXMLLiterals.ITEM_ID, YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + strItemID));
							} else {
								//use the default value
								eleInventorySupply.setAttribute(KohlsXMLLiterals.ITEM_ID, strItemID);
							}
							eleInventorySupply.setAttribute(KohlsXMLLiterals.A_UOM, strUnitOfMeasure);
							if (YFCObject.isVoid(strProductClass)) {
								// use the default value from properties file
								eleInventorySupply.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, YFSSystem.getProperty(KohlsConstant.BOPUS_DEFAULT_PRODUCT_CLASS_PROPERTY));
							} else {
								eleInventorySupply.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, strProductClass);
							}
							inventorySuppliesInput.appendChild(eleInventorySupply);
						}
					}
					// add the input to the overall message
					eleInvSupplies.appendChild(inventorySuppliesInput);

					getGIVResponseDoc=KOHLSBaseApi.invokeService(env, "KohlsGetLocationInventorySupply" , docInvSupplies);


				}catch(Exception e){
					e.getStackTrace();
					throw new YFSException("Exception in getLocationInventorySupply method is: "+e.getMessage());
				}
				return getGIVResponseDoc;
			}
	
	/********************************
	 * 
	 * @param env
	 * @param docInventoryXmlOutput
	 * @param mapOrderLineQty
	 * @param docShipmentListOutput
	 ********************************/
	private void checkGIVQuantityAndOrderQuantity(YFSEnvironment env, Document docInventoryXmlOutput, Map mapOrderLineQty, Document docShipmentListOutput){		
		try{			
			
			//Add GIV output into a map(Item, qty)
			Element eleShipmentListOutput = docShipmentListOutput.getDocumentElement();
			Element eleShipment = SCXmlUtil.getChildElement(eleShipmentListOutput, KohlsXMLLiterals.E_SHIPMENT);
			String ShipmentKey = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
			
			NodeList nodeShipmentLineList = eleShipment.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
			Element eleShipmentLine = (Element)  nodeShipmentLineList.item(0);		
			String shipmentLineKey = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY);
			
			Element eleInventoryXmlOutput = docInventoryXmlOutput.getDocumentElement();
			NodeList nodeItemList = eleInventoryXmlOutput.getElementsByTagName(KohlsXMLLiterals.E_ITEM);
			
			Map <String, String> mapItemSupply = new HashMap<String, String>();
			String strQty  = null;
			
			for (int i=0; i<nodeItemList.getLength(); i++) {
				Element eleItem = (Element) nodeItemList.item(i);
				String ItemId = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
				NodeList nodeItemSupply = eleItem.getElementsByTagName(KohlsXMLLiterals.E_INVENTORY_SUPPLY);
				for (int j=0; j<nodeItemSupply.getLength(); j++) {
					Element eleSupplyType = (Element)nodeItemSupply.item(j);					
					double Qty = Double.parseDouble(eleSupplyType.getAttribute(KohlsXMLLiterals.A_QUANTITY));
					strQty = Double.toString(Qty);
				}
				mapItemSupply.put(ItemId, strQty);
			}			
			
			
			Set<String> reqLineKeys = mapOrderLineQty.keySet();
			for (String reqLineKey:reqLineKeys) {
				String delim = "-";
				String tokenValues [] = reqLineKey.split(delim);
				String orderLineKey = tokenValues[0];
				String orderedItemId = tokenValues[1];
				String orderNo = tokenValues[2];
				String orderedHeaderKey = tokenValues[3];
				String strEnterpriseCode = tokenValues[4];
				String orderedQuantity = mapOrderLineQty.get(reqLineKey).toString();	
				
				Set<String> itemSupplyKeys = mapItemSupply.keySet();
				for (String itemSupplyKey : itemSupplyKeys) {
					
					if ( itemSupplyKey.equals(orderedItemId))
					{							
						if(Integer.parseInt(orderedQuantity) > Integer.parseInt(mapItemSupply.get(itemSupplyKey))){	
							
							//Cancel the excess quantity on the shipment
							/**
							 * Since the quantity on the Shipment is not fully available, call
							 * changeShipment with Action = 'Modify' *
							 */										
							Document inChangeShipmentLineDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();
							Element eChangeShipmentLineInput = inChangeShipmentLineDoc.getDocumentElement();	
							eChangeShipmentLineInput.setAttribute(KohlsConstant.A_ACTION,KohlsConstant.ACTION_MODIFY);
							eChangeShipmentLineInput.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, ShipmentKey);
							
							Element elschangeShipmentLines = SCXmlUtil.createChild(eChangeShipmentLineInput, KohlsXMLLiterals.E_SHIPMENT_LINES);
							Element elschangeShipmentLine = SCXmlUtil.createChild(elschangeShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);						
							elschangeShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY, shipmentLineKey);
							elschangeShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, mapItemSupply.get(itemSupplyKey));
							log.debug("input for changeShipment API "+SCXmlUtil.getString(inChangeShipmentLineDoc));
							KOHLSBaseApi.invokeAPI(env, KohlsConstant.CHANGE_SHIPMENT_API, inChangeShipmentLineDoc);
							
							/**
							 * Since the quantity on the Order is not fully available, call
							 * changeOrder with Action = 'Modify' *
							 */
							Document inChangeOrderLineDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER).getDocument();
							Element eChangeOrderLineInput = inChangeOrderLineDoc.getDocumentElement();	
							eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
							eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_ORDERNO, orderNo);
							eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderedHeaderKey);
							eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_MODIFICATION_REASON_CODE, KohlsConstant.REASON_CODE_NO_INV);
							eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_MODIFICATION_REASON_TEXT, KohlsConstant.REASON_TEXT_NO_INV);
							
							Element elschangeOrderLines = SCXmlUtil.createChild(eChangeOrderLineInput, KohlsXMLLiterals.E_ORDER_LINES);
							Element elschangeOrderLine = SCXmlUtil.createChild(elschangeOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
							elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, orderLineKey);
							elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, mapItemSupply.get(itemSupplyKey));							
							elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
							log.debug("input for changeOrder API "+SCXmlUtil.getString(inChangeOrderLineDoc));
							KOHLSBaseApi.invokeAPI(env, KohlsConstant.CHANGE_ORDER_API, inChangeOrderLineDoc);
						}
					}
				}
			}				
						
		}catch(Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in method checkGIVQuantityAndOrderQuantity : "+e.getStackTrace());
		}
	}
	
		/**
		 * Method that consolidates shipment lines for same Order Line at Shipment Level
		 * Map Containing ITEMID-ORDERLINE as Key and Qty as Value
		 * @param env
		 * @param docShipmentListOutput
		 * @return
		 */
		public  Map<String, Double> shipmentItemLvlConsolidation(YFSEnvironment env, Document docShipmentListOutput) {

			if (log.isDebugEnabled()) {
				log.debug("Method input doc shipmentItemLvlConsolidation: " + SCXmlUtil.getString(docShipmentListOutput));
			}

			Element eleShipment = docShipmentListOutput.getDocumentElement();
			//Element eleShipment = SCXmlUtil.getChildElement(eleShipmentListOutput, KohlsXMLLiterals.E_SHIPMENT);
			String ShipmentNo = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);

			Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment, KohlsXMLLiterals.E_SHIPMENT_LINES);
			NodeList nodeShipmentLineList =  eleShipmentLines.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);

			Document docConsolShipment = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			Element eleConsolShipment = docConsolShipment.getDocumentElement();
			eleConsolShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, ShipmentNo);
			Element eleOrderLines = SCXmlUtil.createChild(eleConsolShipment, KohlsXMLLiterals.E_ORDER_LINES);

			Map <String, Double> mapOrderLineQty = new HashMap<String, Double>();

			for(int i=0; i<nodeShipmentLineList.getLength(); i++) {
				Element eleShipmentLine = (Element) nodeShipmentLineList.item(i);
				String OrderLineKey = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				String ItemId = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
				String mapLineItemQtyKey = OrderLineKey + "-" + ItemId;

				if (log.isDebugEnabled()) {
					log.debug("OrderLineKey Value: " + OrderLineKey);
				}

				Double Quantity = Double.parseDouble(eleShipmentLine.getAttribute(KohlsXMLLiterals.A_QUANTITY));
				if (mapOrderLineQty.containsKey(mapLineItemQtyKey)) {
					Quantity += mapOrderLineQty.get(mapLineItemQtyKey);
					mapOrderLineQty.remove(mapLineItemQtyKey);
					mapOrderLineQty.put(mapLineItemQtyKey, Quantity);
				}
					mapOrderLineQty.put(mapLineItemQtyKey, Quantity);
			}

			Set<String> keys = mapOrderLineQty.keySet();
			for (String key : keys) {
				String Delim = "-";
				String  TokenValues [] = key.split(Delim);
				Element eleOrderLine = SCXmlUtil.createChild(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, TokenValues[0]);
				eleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID,  TokenValues[1]);

				if (log.isDebugEnabled()) {
					log.debug("OrderLineKey Value: " + TokenValues[0] + "ItemId Value" + TokenValues[1]);
				}

				eleOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, mapOrderLineQty.get(key).toString() );
			}
			return mapOrderLineQty;
		}
	
	public void changeShipment(YFSEnvironment env, Document docShipmentListOutput, Document docItem) throws Exception {
	
		Element eleShipmentListOutput = docShipmentListOutput.getDocumentElement();
		Element eleShipment = SCXmlUtil.getChildElement(eleShipmentListOutput, KohlsXMLLiterals.E_SHIPMENT);
		String ShipmentKey = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
		NodeList nodeShipmentLineList = eleShipment.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
		
		Element eleItems = docItem.getDocumentElement();
		NodeList nodeItemList = eleItems.getElementsByTagName(KohlsXMLLiterals.E_ITEM);
		
		for(int i=0; i<nodeShipmentLineList.getLength(); i++) {
			Element eleShipmentLine = (Element)  nodeShipmentLineList.item(i);
			String sItemID =  eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			String shipmentLineKey = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY);
			String changeFlag="N";
			String givLocItem="";
			String signID ="";
			String barcode = "";
			String desc ="";
			for(int j=0; j<nodeItemList.getLength(); j++) {
				Element eleItem = (Element) nodeItemList.item(j);
				givLocItem = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
				signID = eleItem.getAttribute(KohlsXMLLiterals.A_SF_ID_ESIGN_ID);
				barcode = eleItem.getAttribute(KohlsXMLLiterals.A_SR_ID_BAR_CODE);
				desc = eleItem.getAttribute(KohlsXMLLiterals.A_SR_SF_ID_DESC);
				if (sItemID.equals(givLocItem)) {
					changeFlag = "Y"; break;
				}
			}
			if (changeFlag.equals(KohlsConstant.V_Y)) {
				Document docChangeShipment = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
				Element eleChangeShipment = docChangeShipment.getDocumentElement();
				eleChangeShipment.setAttribute(KohlsXMLLiterals.A_ACTION, "modify");
				eleChangeShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, ShipmentKey);
				Element eleShipmentLines = SCXmlUtil.createChild(eleChangeShipment, KohlsXMLLiterals.E_SHIPMENT_LINES);
				Element eleShipLine = SCXmlUtil.createChild(eleShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);
				eleShipLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY, shipmentLineKey);
				eleShipLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, sItemID);
				Element eleShipLineExtn = SCXmlUtil.createChild(eleShipLine, KohlsXMLLiterals.E_EXTN);
				eleShipLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_MLS_LOC_ID_DESC, desc);
				eleShipLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_MLS_LOC_ID_BARCODE, barcode);
				eleShipLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_MLS_LOC_ID_ESIGNID, signID);

				log.debug(SCXmlUtil.getString(docChangeShipment));

				Document changeShipmentLineStatus = 
					KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_CHANGE_SHIPMENT, docChangeShipment);
			}
		}
		Document docChangeShipmentInput = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element eleChangeShipmentInput = docChangeShipmentInput.getDocumentElement();
		eleChangeShipmentInput.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, KohlsConstant.V_KOHLS_COM);
		eleChangeShipmentInput.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, ShipmentKey);
		eleChangeShipmentInput.setAttribute(KohlsXMLLiterals.A_TRANSACTION_ID, KohlsConstant.V_PROCESS_BACK_ROOM_PICK);
		
		Document changeShipmentStatus = 
			KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_CHANGE_SHIPMENT_STATUS, docChangeShipmentInput);
	}
	
	/**
	 * After each successful item scan on the UI, there will be an update sent from OMS to MLS and the details will be input to OMS
	 * KohlsBOPUSUpdatePicking service. The sample input xml format is as given below:
	 * 
	 * <Shipment ShipmentNo="" SellerOrganizationCode="">
	 *	<ShipmentLines>
	 *		<ShipmentLine ShipmentLineNo="" ItemID="" PickedQty="" LocationId="" StoreNumber="" ShortPickIndicator="" AssignedToUser="" 
	 *		ReasonCode="" ReasonText=""/>
	 *	</ShipmentLines>
	 * </Shipment>
	 * 
	 * @param env
	 * @param inXML
	 */
	public void KohlsBOPUSUpdatingPickingDetailsToMLS(YFSEnvironment env, Document inXML){		
		try{
			log.debug("Inside the method KohlsBOPUSUpdatingPickingDetailsToMLS ");
			
			log.debug("Input Xml "+SCXmlUtil.getString(inXML));
			
			String strLocationContext = null;
			String strShipmentLineNo = null;
			String strItemId =null;
			String strQtyPicked= null;
			String strLocationId = null;
			String strStoreID = null;
			String strShortPickIndicator = null;
			String strShipmentKey = null;
			String strShipmentLineKey = null;
			String strExtnPickedQty = null;
			String strOrderedQty = null;
			String strOrderLineKey = null;
			String strEnterpriseCode = null;
			String strOrderNo = null;
			String strOrderHeaderKey = null;
			String strAssignedToUser = null;
			Document outChangeShipmentDoc = null;
			String strLocationBarcode = null;
			String strReasonCode = null;
			String strReasonText = null;
			String strprimeLineNo = null;
			String strshipmentLineNo = null;
			String strShipmentLineQty = null;
			String strPickedQtyTillNow = "0";
			double dPickedQtyTillNow = 0.0;
			double strUpdatedPickedQty = 0.0;
			String locationContext = KohlsConstant.SRO;
			
			//System.out.println(SCXmlUtil.getString(inXML));
			Element elePicking =inXML.getDocumentElement();
			
			// grab the location context, default to Stockroom
			if (!YFCObject.isVoid(elePicking.getAttribute(KohlsXMLLiterals.A_LOCATION_CONTEXT))) {
				locationContext = elePicking.getAttribute(KohlsXMLLiterals.A_LOCATION_CONTEXT);
			}
			
			//String strShipmentNo = elePicking.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
			Element eleShipmentLine = KohlsXPathUtil.getElementByXpath(inXML,"ShipmentLines/ShipmentLine");  
			Element shipmentElement = KohlsXPathUtil.getElementByXpath(inXML, "ShipmentLines/ShipmentLine/Shipment");
			String strShipmentNo = shipmentElement.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
			strShipmentKey = shipmentElement.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
			
			Document inputCheckShipmentHoldDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();				
			Element eleCheckShipment =  inputCheckShipmentHoldDoc.getDocumentElement();			
			eleCheckShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipmentNo);
			
			KohlsBOPUSHoldCheck holdCheck = new KohlsBOPUSHoldCheck();
			boolean shipmentHoldApplied = holdCheck.confirmShpmtHoldCheck(env,inputCheckShipmentHoldDoc);
			if(!shipmentHoldApplied){
			if (!YFCCommon.isVoid(eleShipmentLine)) {
				strShipmentLineNo = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO);
				strItemId = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
				//strQtyPicked = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_PICKED_QTY);
				strQtyPicked = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_BACKROOM_PICKED_QUANTITY);
				strLocationId = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_PICKING_LOCATION);
				strLocationContext = eleShipmentLine.getAttribute(KohlsConstant.A_LOCATION_CONTEXT);
				//strLocationId = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_LOCATIONID);
				strStoreID = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_STORE_NUMBER);
				strShortPickIndicator = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHORT_PICK_INDICATOR);
				strAssignedToUser = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ASSIGNED_TO_USER);
				strReasonCode = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_REASON_CODE);
				strReasonText = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_REASON_TEXT);
				strShipmentLineKey = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY);
			}
			String strSellerOrganizationCode = elePicking.getAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE);
			

			if(!"Y".equalsIgnoreCase(strShortPickIndicator)){ //not a short pick scenario
				log.debug("The strShortPickIndicator: "+strShortPickIndicator);
						
				if((!YFCCommon.isStringVoid(strShipmentNo) && !YFCCommon.isStringVoid(strShipmentLineNo) && !YFCCommon.isStringVoid(strQtyPicked)) 
					|| !YFCObject.isVoid(strShipmentLineKey)){
					if(log.isDebugEnabled()){
						log.debug("Calling changeShipmentAPI to stamp the picked Qty on the Shipment");
					}
					
					Document inputShipmentDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();				
					Element ele =  inputShipmentDoc.getDocumentElement();
					if (!YFCObject.isVoid(strShipmentKey)) {
						// use shipment key if present
						ele.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, strShipmentKey);
					} else {
						// use shipment no
						ele.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipmentNo);
					}
					Element elsShipmentLines = SCXmlUtil.createChild(ele, KohlsXMLLiterals.E_SHIPMENT_LINES);
					Element elsShipmentLine = SCXmlUtil.createChild(elsShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);					
					elsShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, strShipmentLineNo);
					if (!YFCObject.isVoid(strShipmentLineKey)) {
						elsShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY, strShipmentLineKey);
					}
					
					log.debug("Input to getshipmentList API "+SCXmlUtil.getString(inputShipmentDoc));
					
					Document outputShipmentListDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.TEMPLATE_SHIPMENTS_PICK_PROCESS, 
							KohlsConstants.API_GET_SHIPMENT_LIST, inputShipmentDoc);
					log.debug("Output from  getshipmentList API "+SCXmlUtil.getString(outputShipmentListDoc));
					
					Element eleShipment = KohlsXPathUtil.getElementByXpath(outputShipmentListDoc,"Shipments/Shipment");
					if (!YFCCommon.isVoid(eleShipment)) {
						strShipmentKey = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);	
						if (YFCCommon.isVoid(strStoreID)) {
							// set the store id from the shipment
							strStoreID = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIP_NODE);
						}					
					}
					log.debug("Store id is "+strStoreID);
					
					Element eleReadShipLines = KohlsXPathUtil.getElementByXpath(outputShipmentListDoc,"Shipments/Shipment/ShipmentLines");	
					NodeList nlReadShipmentLine = eleReadShipLines.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);	
					
					for(int k=0; k<nlReadShipmentLine.getLength(); k++){
						Element eleReadShipmentLine = (Element)nlReadShipmentLine.item(k);
						String strReadShipmentLineNo = eleReadShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO);
						if(strShipmentLineNo.equals(strReadShipmentLineNo)){
							strShipmentLineKey = eleReadShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY);
							strOrderNo = eleReadShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDERNO);
							strOrderHeaderKey = eleReadShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
							strprimeLineNo = eleReadShipmentLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
							strshipmentLineNo = eleReadShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO);
							strShipmentLineQty = eleReadShipmentLine.getAttribute(KohlsXMLLiterals.A_QUANTITY);
							double dblShipmentLineQty = Double.parseDouble(strShipmentLineQty);
							int intShipmentLineQty = (int) dblShipmentLineQty;
							strShipmentLineQty = String.valueOf(intShipmentLineQty);
							Element eleExtn = SCXmlUtil.getChildElement(eleReadShipmentLine, KohlsXMLLiterals.E_EXTN);
							if(!YFCCommon.isVoid(eleExtn)){
								if(!"".equals(eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_PICKED_QTY)))
									strPickedQtyTillNow = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_PICKED_QTY);	
								else {
									// initialize at 0
									strPickedQtyTillNow = "0";
								}
							}
							dPickedQtyTillNow = Double.parseDouble(strPickedQtyTillNow);
							strUpdatedPickedQty = dPickedQtyTillNow + Double.parseDouble(strQtyPicked);
						}
					}
					
					Document inChangeShipmentLineDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();
					Element eChangeShipmentLineInput = inChangeShipmentLineDoc.getDocumentElement();
					eChangeShipmentLineInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);
					Element elschangeShipmentLines = SCXmlUtil.createChild(eChangeShipmentLineInput, KohlsXMLLiterals.E_SHIPMENT_LINES);
					Element elsChangeShipmentLine = SCXmlUtil.createChild(elschangeShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);
					elsChangeShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY, strShipmentLineKey);
//					elsChangeShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
					Element elsChangeShipmentExtne = SCXmlUtil.createChild(elsChangeShipmentLine, KohlsXMLLiterals.E_EXTN);
					elsChangeShipmentExtne.setAttribute(KohlsXMLLiterals.A_EXTN_PICKED_QTY, String.valueOf(strUpdatedPickedQty));						
					
					log.debug("Input to changeShipment to updat qty API "+SCXmlUtil.getString(inChangeShipmentLineDoc));
					
					outChangeShipmentDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.TEMPLATE_CHANGE_SHIPMENTS_PICK_PROCESS, KohlsConstant.API_CHANGE_SHIPMENT, inChangeShipmentLineDoc);
					
					log.debug("Output from changeShipment API "+SCXmlUtil.getString(outChangeShipmentDoc));
										
					//Store Event table update 
					updatingStoreEventsHFTable(env, strShipmentKey, strOrderNo, strOrderHeaderKey, strprimeLineNo, strshipmentLineNo, strShipmentLineQty, strLocationId, env.getUserId(), 
							KohlsConstant.V_ITEM_BEING_PICKED );
							
					
					//To get location bar code
					strLocationBarcode = getLocationBarcode(env, strShipmentKey, strShipmentLineNo, strLocationId);
					if(strLocationBarcode != null){					
						Element eleShipmentLines = KohlsXPathUtil.getElementByXpath(outChangeShipmentDoc, "Shipment/ShipmentLines");
						NodeList nlShipmentLine = eleShipmentLines.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);			
								
						for(int i=0; i<nlShipmentLine.getLength(); i++){
							Element eleVerifyShipmentLine = (Element)nlShipmentLine.item(i);
							String strVerifyShipmentLineKey = eleVerifyShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY);
							if(!strShipmentLineKey.equals(strVerifyShipmentLineKey)){
								XMLUtil.removeChild(eleShipmentLines, eleVerifyShipmentLine);
							}
						}
						
						Element eWebServiceInput = outChangeShipmentDoc.getDocumentElement();
						Element elsWebServiceLocation = SCXmlUtil.createChild(eWebServiceInput, KohlsConstant.A_LOCATION);
						elsWebServiceLocation.setAttribute(KohlsConstant.A_LOCATION_ID, strLocationId);		
						elsWebServiceLocation.setAttribute(KohlsConstant.A_LOCATION_CONTEXT, strLocationContext);
						Element elsWebServiceUser = SCXmlUtil.createChild(eWebServiceInput, KohlsConstant.A_USER);
						elsWebServiceUser.setAttribute(KohlsConstant.A_USER_ID, strAssignedToUser);
					}
				}	
				
				log.debug("about to call MLS");
				log.debug("Location id:" + strLocationId);
				log.debug("strQtyPicked is:" + strQtyPicked);
				
				
				//If location bar code is not null, call MLS service for update
				//if(strLocationBarcode != null && Double.parseDouble(strQtyPicked) > 0){
				if(strLocationId != null && Double.parseDouble(strQtyPicked) > 0) {
					YFCDocument yfcOutChangeShipmentDoc = YFCDocument.getDocumentFor(outChangeShipmentDoc);
					YFCElement shipmentEle = yfcOutChangeShipmentDoc.getDocumentElement();
					
					// get the prefix information for the namespaces and login information if present
					String yfsEndPointUser = "";
					String yfsEndPointPassword = "";
					String environmentElement = "";
					Element environmentNode = null;
					yfsEndPointUser = YFSSystem.getProperty(KohlsConstant.END_POINT_USER_MLS);
					yfsEndPointPassword = YFSSystem.getProperty(KohlsConstant.END_POINT_PWD_MLS);
					environmentElement = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_ENV_ELEMENT_PROPERTY);
					if (!YFCObject.isVoid(yfsEndPointUser) && !YFSObject.isVoid(yfsEndPointPassword) && !YFSObject.isVoid(environmentElement)) {
						// we have an user id and password, need to construct the environment element
						environmentNode = outChangeShipmentDoc.createElement(environmentElement);
						// add the user id and password elements
						Element userId = outChangeShipmentDoc.createElement(KohlsConstant.A_USER_ID);
						// set the userid value from the properties file
						userId.setTextContent(yfsEndPointUser);
						// add the userId
						environmentNode.appendChild(userId);
						Element password = outChangeShipmentDoc.createElement(KohlsConstant.A_PASSWORD);
						// set the password value from the properties file
						password.setTextContent(yfsEndPointPassword);
						// add the password
						environmentNode.appendChild(password);
						// add the environment
						outChangeShipmentDoc.getDocumentElement().appendChild(environmentNode);
					}
					
					YFCElement shipmentLinesEle = shipmentEle.getChildElement(KohlsXMLLiterals.E_SHIPMENT_LINES);
					YFCElement shipmentLineEle = shipmentLinesEle.getChildElement(KohlsXMLLiterals.E_SHIPMENT_LINE);
					// see if we need to translate the item id
					if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + shipmentLineEle.getAttribute(KohlsXMLLiterals.A_ITEM_ID)))) {
						shipmentLineEle.setAttribute(KohlsXMLLiterals.A_ITEM_ID, YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + shipmentLineEle.getAttribute(KohlsXMLLiterals.A_ITEM_ID)));
					}
					YFCElement locationEle = shipmentLineEle.createChild("Location");
					locationEle.setAttribute(KohlsXMLLiterals.A_LOCATIONID, strLocationId);
					if (KohlsConstant.SRO.equalsIgnoreCase(strLocationContext)) {
						strLocationContext = KohlsConstant.SRO;
					}
					locationEle.setAttribute(KohlsConstant.A_LOCATION_CONTEXT, strLocationContext);
					log.debug("Input to update locatin times service "+SCXmlUtil.getString(yfcOutChangeShipmentDoc.getDocument()));
					// set the MLS endpoint based on the shipnode
					StringBuffer endpointURL = new StringBuffer("");
					// grab the end point properties minus the shipnode
					if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_PREFIX))) {
						endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_PREFIX));
					} else {
						// set to default
						endpointURL.append("http://isp");
					}
					// see if we have any translation shipnode values
					if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + strStoreID))) {
						// use the translated value
						endpointURL.append(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + strStoreID));
					} else if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_USE_DEFAULT_STORE)) 
							&& "Y".equalsIgnoreCase(YFSSystem.getProperty(KohlsConstant.MLS_USE_DEFAULT_STORE))) {
						// use the default store
						endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_DEFAULT_STORE_LOOKUP));
					} else {
						// no translation
						String formattedShipNode = strStoreID;
						while (formattedShipNode.toString().length() < 4) {
							formattedShipNode = "0" + formattedShipNode;
						}
						endpointURL.append(formattedShipNode);
					}
					if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_SUFFIX))) {
						endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_SUFFIX));
					} else {
						// set to default
						endpointURL.append("e.st.ad.kohls.com/KohlsInventoryDemandService/InventoryDemandService.svc");
					}
					env.setTxnObject(KohlsConstant.V_ENDPOINTURL, endpointURL.toString());
					
					// set the soap action information
					StringBuffer soapActionOperation = new StringBuffer("");
					if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTIONURL_PROPERTY))) {
						soapActionOperation.append(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTIONURL_PROPERTY));
					} else {
						// set to default
						soapActionOperation.append("http://tempuri.org/IInventoryDemandService/");
					}
					if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTION_UPDATELOCATIONITEMS))) {
						soapActionOperation.append(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTION_UPDATELOCATIONITEMS));
					} else {
						// set to default
						soapActionOperation.append("FulfillBopusOrderItems");
					}
					env.setTxnObject(KohlsConstant.V_SOAPACTION, soapActionOperation.toString());
					/**
					 * <ShipmentLines SellerOrganizationCode="Matrix-R">
					 *	<ShipmentLine ShipmentLineNo="1" ItemID="100001" BackroomPickedQuantity="1" PickLocation="1373" StoreNumber="9911" ShortPickIndicator="N" AssignedToUser="abrooks" ReasonCode="" ReasonText="" ShipmentLineKey="2014032100064545037" >
					 *	<Shipment ShipmentNo="1" ShipmentKey="CPICK_ORDER_01_SHKEY" >
					 * </Shipment>
					 * </ShipmentLine>
					 * </ShipmentLines>
					 */
					// don't do this for sales floor
					if (!KohlsConstant.SFO.equalsIgnoreCase(strLocationContext)) {
						log.debug("Input to update location times service "+SCXmlUtil.getString(yfcOutChangeShipmentDoc.getDocument()));
						Document outputMLSDoc = KohlsCommonUtil.invokeService(env, KohlsConstant.A_UPDATE_LOCATION_ITEMS, yfcOutChangeShipmentDoc.getDocument());
					
					
						if(outputMLSDoc == null)
							throw new YFSException("Unavailable MLS Service");
					}
				}
				
				if(Double.parseDouble(strQtyPicked) < 0 ) {
					updatingStoreEventsHFTable(env,strShipmentKey,strOrderNo,strOrderHeaderKey,"","",strShipmentLineQty,strLocationId,env.getUserId(),KohlsConstant.V_UNDO_PICKING);
				}
				
			}else{// Short pick scenario
				
				
				/* <ShipmentLine ShipmentNo="100000011" ShipmentLineKey="2014012213204892012">  
				   </ShipmentLine>  */
				
				Document inputShipmentDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();				
				Element ele =  inputShipmentDoc.getDocumentElement();
				if (!YFCObject.isVoid(strShipmentKey)) {
					// use the shipment key for lookup
					ele.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, strShipmentKey);
				} else {
					// use the shipment line no for lookup
					ele.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipmentNo);
				}
				Element elsShipmentLines = SCXmlUtil.createChild(ele, KohlsXMLLiterals.E_SHIPMENT_LINES);
				Element elsShipmentLine = SCXmlUtil.createChild(elsShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);					
				elsShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, strShipmentLineNo);
				
				log.debug("Input to getshipmentList API "+SCXmlUtil.getString(inputShipmentDoc));
				
				Document outputShipmentListDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.TEMPLATE_SHIPMENTS_PICK_PROCESS, 
						KohlsConstants.API_GET_SHIPMENT_LIST, inputShipmentDoc);
				
				Element eleShipment = KohlsXPathUtil.getElementByXpath(outputShipmentListDoc,"Shipments/Shipment");
				if (!YFCCommon.isVoid(eleShipment)) {
					strShipmentKey = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
					strEnterpriseCode = eleShipment.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);
				}
				
				Element eleReadShipLines = KohlsXPathUtil.getElementByXpath(outputShipmentListDoc,"Shipments/Shipment/ShipmentLines");	
				NodeList nlReadShipmentLine = eleReadShipLines.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);	
				
				for(int l=0; l<nlReadShipmentLine.getLength(); l++){
					Element eleShipLine = (Element)nlReadShipmentLine.item(l);
					String strReadShipmentLineNo = eleShipLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO);
					if(strShipmentLineNo.equals(strReadShipmentLineNo)){
						strShipmentLineKey = eleShipLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY);	
						strOrderNo = eleShipLine.getAttribute(KohlsXMLLiterals.A_ORDERNO);
						strOrderHeaderKey = eleShipLine.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
						strprimeLineNo = eleShipLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
						strshipmentLineNo = eleShipLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO);
						
						Element eleExtn = SCXmlUtil.getChildElement(eleShipLine, KohlsXMLLiterals.E_EXTN);						
						strExtnPickedQty = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_PICKED_QTY);
												
						Element eleOrder = SCXmlUtil.getChildElement(eleShipLine, KohlsXMLLiterals.E_ORDER_LINE);	
						strOrderedQty = eleOrder.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY);				
						strOrderLineKey = eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);	
					}
				}							
							
				double intOrderQty =0.0;
				double intPickedQty =0.0;
				double intShortPickedQty=0.0;
				
				intOrderQty = Double.parseDouble(strOrderedQty);
				if(!YFCCommon.isStringVoid(strExtnPickedQty))
					intPickedQty = Double.parseDouble(strExtnPickedQty);
				else
					strExtnPickedQty="0";
				
				intShortPickedQty = intOrderQty-intPickedQty;				
				String strShortPickedQty = Double.toString(intShortPickedQty);
				
				//Invoking changeShipment API and update the ShortPickedQty and ShortPickIndicator fields on the shipment lines.				
				if(!YFCCommon.isStringVoid(strShipmentNo) && !YFCCommon.isStringVoid(strShipmentLineNo)	&& !YFCCommon.isStringVoid(strShortPickIndicator)){
					if(log.isDebugEnabled()){
						log.debug("Calling changeShipmentAPI to stamp the short picked Qty on the Shipment");
					}
					Document inChangeShipmentLineDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();
					Element eChangeShipmentLineInput = inChangeShipmentLineDoc.getDocumentElement();
					eChangeShipmentLineInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);
					Element elschangeShipmentLines = SCXmlUtil.createChild(eChangeShipmentLineInput, KohlsXMLLiterals.E_SHIPMENT_LINES);
					Element elsChangeShipmentLine = SCXmlUtil.createChild(elschangeShipmentLines, KohlsXMLLiterals.E_SHIPMENT_LINE);
					elsChangeShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
					elsChangeShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY, strShipmentLineKey);
					Element elsChangeShipmentExtne = SCXmlUtil.createChild(elsChangeShipmentLine, KohlsXMLLiterals.E_EXTN);
					elsChangeShipmentExtne.setAttribute(KohlsXMLLiterals.A_EXTN_SHORT_PICK_INDICATOR, strShortPickIndicator);
					elsChangeShipmentExtne.setAttribute(KohlsXMLLiterals.A_EXTN_SHORT_PICKED_QTY, strShortPickedQty);
					elsChangeShipmentExtne.setAttribute(KohlsXMLLiterals.A_QUANTITY, strExtnPickedQty);	
					log.debug("Input to changeShipment API "+SCXmlUtil.getString(inChangeShipmentLineDoc));
					Document outShipmentDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.API_CHANGE_SHIPMENT, inChangeShipmentLineDoc);
					
					
					Document inChangeOrderLineDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER).getDocument();
					Element eChangeOrderLineInput = inChangeOrderLineDoc.getDocumentElement();	
					eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
					eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderNo);
					eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
					eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_MODIFICATION_REASON_CODE, strReasonCode);
					eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_MODIFICATION_REASON_TEXT, strReasonText);
					
					Element elschangeOrderLines = SCXmlUtil.createChild(eChangeOrderLineInput, KohlsXMLLiterals.E_ORDER_LINES);
					Element elschangeOrderLine = SCXmlUtil.createChild(elschangeOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
					elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, strOrderLineKey);
					elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, strExtnPickedQty);
					log.debug("Input to changeOrder API "+SCXmlUtil.getString(inChangeOrderLineDoc));
					
					Document outChangeOrderDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.CHANGE_ORDER_API, inChangeOrderLineDoc);
	 				//ADDED BY Baijayanta
					KohlsStatusUpdateUtil updtUtil = new KohlsStatusUpdateUtil();
					updtUtil.ExceptionsentOrderStatusToEcommForPickExcp(env,strOrderLineKey,strOrderHeaderKey,strItemId);
					
					//Store Event Table
					 updatingShortPickEventsHFTable(env,	strShipmentKey, strOrderNo, strOrderHeaderKey, strprimeLineNo, strshipmentLineNo, strShortPickedQty, strLocationId, 
								env.getUserId(), KohlsConstant.V_SHORT_PICKED, strReasonCode, strReasonText); 
					} 
					
					
				}
				
				updateShipmentStatus(env,strShipmentKey,strShipmentLineKey,strSellerOrganizationCode);			
			}else{
				throw new YFSException("This Order is on Hold, so pickup cannot be processed.");
			}
			
		} catch(Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in KohlsBOPUSUpdatePicking method is: "+e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param env
	 * @param strShipmentKey
	 * @param strShipmentLineKey
	 * @param strSellerOrganizationCode
	 */
	private void updateShipmentStatus(YFSEnvironment env, String strShipmentKey, String strShipmentLineKey, String strSellerOrganizationCode)
	{
		
		try{
			boolean isPickingCompleted = true;			
		
			Document inDocShipmentLine = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			Element inEleShipment = inDocShipmentLine.getDocumentElement();
	
			inEleShipment.setAttribute("ShipmentKey", strShipmentKey);			
			inEleShipment.setAttribute("SellerOrganizationCode", strSellerOrganizationCode);	
			
			//Invoking getShipmentLineList method 
			log.debug("input for getShipmentList API "+SCXmlUtil.getString(inDocShipmentLine));
			Document outShipmentDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstant.TEMPLATE_SHIPMENTS_PICK_PROCESS, KohlsConstants.API_GET_SHIPMENT_LIST ,inDocShipmentLine);
			String outStr= SCXmlUtil.getString(outShipmentDoc);	
			
			Element eleShipmentLines = KohlsXPathUtil.getElementByXpath(outShipmentDoc,"Shipments/Shipment/ShipmentLines");	
			NodeList nlShipmentLine = eleShipmentLines.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);	
			
			Double pickedqty = 0.0;
			Double orderqty = 0.0;
			String strOrderNo = " ";
			String strOrderHeaderKey =" ";
			String strprimeLineNo = " ";
			String strshipmentLineNo = " ";
			String strShipmentLineQty = " ";
			String strLocation = " ";
			
			for(int i=0; i<nlShipmentLine.getLength(); i++){
				Element eleShipmentLine = (Element)nlShipmentLine.item(i);
						
				String strReadShipmentLineNo = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO);
				if(strReadShipmentLineNo.equals(strReadShipmentLineNo)){
					strShipmentLineKey = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY);
					strOrderNo = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDERNO);
					strOrderHeaderKey = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
					strprimeLineNo = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
					strshipmentLineNo = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO);
					strShipmentLineQty = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_QUANTITY);
				}
				
				Element eleOrderLine = SCXmlUtil.getChildElement(eleShipmentLine, KohlsConstant.E_ORDER_LINE);
				if(eleOrderLine != null){
					if(!"".equals(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY)))
					    orderqty = Double.parseDouble(eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
				}
				
				Element eleExtn = SCXmlUtil.getChildElement(eleShipmentLine, KohlsXMLLiterals.E_EXTN);
				if(eleExtn != null){
					if(!"".equals(eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_PICKED_QTY)))
					    pickedqty = Double.parseDouble(eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_PICKED_QTY));
				}
				if (orderqty != pickedqty){
					isPickingCompleted=false;
				}
			}
			
			if(isPickingCompleted){
				if(!YFCCommon.isStringVoid(strShipmentKey) && !YFCCommon.isStringVoid(strShipmentLineKey)){
					if(log.isDebugEnabled()){
						log.debug("Calling changeShipmentAPI to update the shipment status to Picking Completed");
					}
					Document inChangeShipmentLineDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT_LINE).getDocument();
					Element eChangeShipmentLineInput = inChangeShipmentLineDoc.getDocumentElement();
					eChangeShipmentLineInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);
					eChangeShipmentLineInput.setAttribute(KohlsConstant.A_STATUS, "Picking Completed");
					log.debug("input for changeshipment API "+SCXmlUtil.getString(inChangeShipmentLineDoc));
					KOHLSBaseApi.invokeAPI(env,KohlsConstant.API_CHANGE_SHIPMENT, inChangeShipmentLineDoc);
					
					//Store Event table update 
					updatingStoreEventsHFTable(env, strShipmentKey, strOrderNo, strOrderHeaderKey, strprimeLineNo, strshipmentLineNo, strShipmentLineQty, strLocation, env.getUserId(), KohlsConstant.V_PICKING_COMPLETED);
				}	
			}
		}catch(Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in KohlsBOPUSPickingProcess method is: "+e.getMessage());
			}
		}
		
		/**
		 * The main method that performs Shipment Order Line consolidation for a given node.
		 * This function will make a call to GIV to retrieve Sales Floor and Stock Room quantity
		 * for Shipment Line, Stock Room and Sales Floor locations for a Shipment Line, and a call to
		 * OpenAPI to retrieve the Shipment Line's product URL. The output is added to the Item Locations details
		 * in the Shipment Line level.
		 * @param env
		 * @param docInShipment
		 * @return
		 * @throws Exception
		 */
		public Document shipmentLine_GIV_MLS_Consolidation(YFSEnvironment env, Document docInShipment)  throws Exception {

			Document docInventoryXmlOutput=null;
			Document docInputMLSGetItemLocDet=null;
			Document getMlsResponseDoc=null;
			Document getItemsForMlsDoc=null;
			Document docMlsShipmentout=null;
			String shipNode="";
			
			Document returnDoc = null;
			
			System.out.println(SCXmlUtil.getString(docInShipment));

			Element eleInShipment = docInShipment.getDocumentElement();
			String pickingFlag = eleInShipment.getAttribute(KohlsXMLLiterals.A_PICKING_FLAG);
			
			//Document docShipmentListOutput = KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_GET_SHIPMENT_LIST_MLS_TEMPLATE, KohlsConstant.API_actual_GET_SHIPMENT_LIST, docInShipment);
			Document docShipmentListOutput = KohlsCommonUtil.invokeAPI(env, KOHLS_GET_SHIPMENT_DETAILS_MLS_GIV, "getSortedShipmentDetails", docInShipment);
			if (docShipmentListOutput == null) {
				throw new YFSException("GetShipmentDetails API call returned null value");
			}
			if (log.isDebugEnabled()) {
				log.debug(SCXmlUtil.getString(docShipmentListOutput));
			}

			Element shipmentElement = docShipmentListOutput.getDocumentElement();
			//Element shipmentElement = (Element)eleShipmentListOutput.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT).item(0);
			shipNode = shipmentElement.getAttribute(KohlsXMLLiterals.A_SHIP_NODE);

			returnDoc = docShipmentListOutput;
			//GIV Call with Shipment Details Output
			docInventoryXmlOutput = KOHLSBaseApi.invokeService(env, KohlsConstant.SERVICE_GET_INV_STOREPICK_WS , docShipmentListOutput);

			//GIV-STUB Can be added as needed - BRK
			//docInventoryXmlOutput = stubGIVRespDoc( env );

			if (docInventoryXmlOutput == null) {
				throw new YFSException("GIV Webservice call returned null value");
			} else {
				if (log.isDebugEnabled()) {
					log.debug(SCXmlUtil.getString(docInventoryXmlOutput));
				}
				returnDoc = setGIVQuantities(docInventoryXmlOutput, docShipmentListOutput);
				
				Map<String,Double> mapOrderLineQty = shipmentItemLvlConsolidation(env, docShipmentListOutput);
				docInputMLSGetItemLocDet = mergeInvShipmentLines(env, docInventoryXmlOutput, mapOrderLineQty);

				if (log.isDebugEnabled()) {
					log.debug(SCXmlUtil.getString(docInputMLSGetItemLocDet));
				}

				//Appending ShipNode to MLS Input for making Dynamic URL
				Element eleInputMLSGetItemLocDet = docInputMLSGetItemLocDet.getDocumentElement();
				eleInputMLSGetItemLocDet.setAttribute(KohlsXMLLiterals.A_SHIP_NODE, shipNode);

				//MLS-STUB Can be added as needed - BRK
				//getMlsResponseDoc= stubMLSRespDoc(env , docInputMLSGetItemLocDet);
				// set the MLS endpoint based on the shipnode
				StringBuffer endpointURL = new StringBuffer("");
				// grab the end point properties minus the shipnode
				if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_PREFIX))) {
					endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_PREFIX));
				} else {
					// set to default
					endpointURL.append("http://isp");
				}
				// see if we have any translation shipnode values
				if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + shipNode))) {
					// use the translated value
					endpointURL.append(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ORG_PREFIX + shipNode));
				} else if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_USE_DEFAULT_STORE)) 
						&& "Y".equalsIgnoreCase(YFSSystem.getProperty(KohlsConstant.MLS_USE_DEFAULT_STORE))) {
					// use the default store
					endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_DEFAULT_STORE_LOOKUP));
				} else {
					// no translation
					String formattedShipNode = shipNode;
					while (formattedShipNode.toString().length() < 4) {
						formattedShipNode = "0" + formattedShipNode;
					}
					endpointURL.append(formattedShipNode);
				}
				if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_SUFFIX))) {
					endpointURL.append(YFSSystem.getProperty(KohlsConstant.MLS_ENDPOINT_URL_SUFFIX));
				} else {
					// set to default
					endpointURL.append("e.st.ad.kohls.com/KohlsInventoryDemandService/InventoryDemandService.svc");
				}
				env.setTxnObject(KohlsConstant.V_ENDPOINTURL, endpointURL.toString());
				
				// set the soap action information
				StringBuffer soapActionOperation = new StringBuffer("");
				if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTIONURL_PROPERTY))) {
					soapActionOperation.append(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTIONURL_PROPERTY));
				} else {
					// set to default
					soapActionOperation.append("http://tempuri.org/IInventoryDemandService/");
				}
				if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTION_GETITEMLOCATIONS))) {
					soapActionOperation.append(YFSSystem.getProperty(KohlsConstant.MLSSOAPACTION_GETITEMLOCATIONS));
				} else {
					// set to default
					soapActionOperation.append("GetItemLocations");
				}
				env.setTxnObject(KohlsConstant.V_SOAPACTION, soapActionOperation.toString());
			
				//Making an MLS Web service call for fetching Item Location details
				getMlsResponseDoc=KOHLSBaseApi.invokeService(env, KohlsConstant.SERVICE_GET_ITEM_LOC_STOREPICK_WS , docInputMLSGetItemLocDet);

				if (getMlsResponseDoc == null) {
					throw new YFSException("MLS Webservice call returned null value");
				} else {
					if (log.isDebugEnabled()) {
						log.debug(SCXmlUtil.getString(getMlsResponseDoc));
					}

					getItemsForMlsDoc = getItemLocationDetails(env, getMlsResponseDoc);
					
					if (log.isDebugEnabled()) {
						log.debug(SCXmlUtil.getString(getItemsForMlsDoc));
					}

					docMlsShipmentout = updateShipmentLines(env, docShipmentListOutput, getItemsForMlsDoc, pickingFlag);
					returnDoc = docMlsShipmentout;
					
					if (docMlsShipmentout == null) {
						throw new YFSException("MLS Location Output is null returned by mehod updateShipmentLines()");
					}
					if (log.isDebugEnabled()) {
						log.debug(SCXmlUtil.getString(docMlsShipmentout));
					}

					//Calling Open API Service for fetching Item Image URL's
					Document docMLSOpenAPIOut=KOHLSBaseApi.invokeService(env, "KohlsOpenAPIItemImage" , docMlsShipmentout);
//
//					//Open API - STUB Can be added as needed by commenting above Service call - BRK
//					//Document respDoc = openAPIOutput(env, docShipmentListOutput);
//
					if (docMLSOpenAPIOut == null) {
						throw new YFSException("Open API Call failed and returned null document at method callOpenAPIItemImageURL()");
					}
					if (log.isDebugEnabled()) {
						log.debug(SCXmlUtil.getString(docMLSOpenAPIOut));
					}
				}
			}
			return returnDoc;
		}
		
		/**
		 * 
		 * @param env
		 * @param itemId
		 * @param shipNode
		 * @return
		 */
		public String givItemFilterObtain ( YFSEnvironment env, String itemId, String shipNode) {

            String itemFilter=KohlsConstant.MIXED;
            try {
                //***** Following code block is added by Rob 
                String yfsEndPointUser = "";
                String yfsEndPointPassword = "";
                String inputNamespacePrefix = "";
                String messageNamespacePrefix = "";
                String environmentElement = "";
                String messageElement = "";
                String inputElement = "";
                
                yfsEndPointUser = YFSSystem.getProperty(KohlsConstant.END_POINT_USER_GIV_LOC_INV_SUPPLY);
                yfsEndPointPassword = YFSSystem.getProperty(KohlsConstant.END_POINT_PWD_GIV_LOC_INV_SUPPLY);
                environmentElement = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_ENV_ELEMENT_PROPERTY);
                
                if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_INPUT_PREFIX_PROPERTY))) {
                    inputNamespacePrefix = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_INPUT_PREFIX_PROPERTY);
                }
                
                if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_MESSAGE_PREFIX_PROPERTY))) {
                    messageNamespacePrefix = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_MESSAGE_PREFIX_PROPERTY);
                }
                
                messageElement = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_MESSAGE_ELEMENT_PROPERTY);
                inputElement = YFSSystem.getProperty(KohlsConstant.BOPUS_GIV_INPUT_ELEMENT_PROPERTY);
                Document docInvSupplies = SCXmlUtil.createDocument(messageNamespacePrefix + ":" + messageElement);
                Element environmentNode = null;
                
                if (!YFCObject.isVoid(yfsEndPointUser) && !YFSObject.isVoid(yfsEndPointPassword) && !YFSObject.isVoid(environmentElement)) 
                {
                    // we have an user id and password, need to construct the environment element
                    environmentNode = docInvSupplies.createElement(environmentElement);
                    
                    // add the user id and password elements
                    Element userId = docInvSupplies.createElement(KohlsConstant.A_USER_ID);
                    
                    // set the userid value from the properties file
                    userId.setTextContent(yfsEndPointUser);
                    
                    // add the userId
                    environmentNode.appendChild(userId);
                    
                    Element password = docInvSupplies.createElement(KohlsConstant.A_PASSWORD);
                    // set the password value from the properties file
                    password.setTextContent(yfsEndPointPassword);
                    // add the password
                    environmentNode.appendChild(password);
                    
                    // add the environment
                    docInvSupplies.getDocumentElement().appendChild(environmentNode);
                }
                
                Element eleInvSupplies = docInvSupplies.getDocumentElement();
                
                Element inventorySuppliesInput = docInvSupplies.createElement(inputElement);
                            
                Element eleInventorySupply=docInvSupplies.createElement(inputNamespacePrefix + ":" + KohlsXMLLiterals.E_INV_SUPPLY );
                                
                eleInventorySupply.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, KohlsConstant.KOHLS_OrganizationCode);            
                eleInventorySupply.setAttribute(KohlsXMLLiterals.A_SHIPNODE, shipNode);
                eleInventorySupply.setAttribute(KohlsXMLLiterals.ITEM_ID, itemId);
                eleInventorySupply.setAttribute(KohlsXMLLiterals.A_UOM, KohlsConstant.EACH);
                eleInventorySupply.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, KohlsConstant.PRODUCT_CLASS_GOOD);
                inventorySuppliesInput.appendChild(eleInventorySupply);
                eleInvSupplies.appendChild(inventorySuppliesInput);
    
                Document getGIVResponseDoc = null;
                try {
                    getGIVResponseDoc=KOHLSBaseApi.invokeService(env, "KohlsGIVLocationInventorySupplyWebservice" , docInvSupplies);            
                } catch (Exception e) {
                    e.getStackTrace();
                    throw new YFSException("Exception in givItemFilterObtain method is: "+e.getMessage());
                }
    
                if(getGIVResponseDoc == null) {
                    throw new YFSException("Response from GIV is null");
                }else {
                    Element eleGIVResp = getGIVResponseDoc.getDocumentElement();
                    NodeList supplyList = eleGIVResp.getElementsByTagName(KohlsXMLLiterals.E_INVENTORY_SUPPLY);
                    boolean SF_enable = false;
                    boolean SR_enable = false;
    
                    for (int j = 0; j < supplyList.getLength(); j++) {
    
                        Element eleList = (Element)supplyList.item(j);
                        String qty = eleList.getAttribute(KohlsXMLLiterals.A_QUANTITY);
                        String supplyTyp = eleList.getAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE);
    
                        if(supplyTyp.equalsIgnoreCase(KohlsConstant.SALES_FLOOR) && Integer.parseInt(qty) > 0){
                            SF_enable = true;
                        } else if (supplyTyp.equalsIgnoreCase(KohlsConstant.STOCK_ROOM) && Integer.parseInt(qty) > 0) {
                            SR_enable = true;
    
                        } 
    
                    }
    
                if(SF_enable && !SR_enable)
                    itemFilter = KohlsConstant.SFO;
                else if (!SF_enable && SR_enable)
                    itemFilter = KohlsConstant.SRO;
                else if (SF_enable && SR_enable)
                    itemFilter = KohlsConstant.MIXED;
                }
            } catch(Exception e1) {
                e1.getStackTrace();
                throw new YFSException("Exception in givItemFilterObtain method is: "+e1.getMessage());
                
            }
            
            log.debug("itemFilter value is:"+itemFilter);
            return itemFilter;
           

        }
	
		/**
		 * Method to perform change on Shipment Status to PickingInProgress if PikingFlag is Y and Adding MLS Output to Shipment Line
		 * Updating Item MLS Location details at shipment line level and based on picking flag change Shipment Status to Picking-In-Progress
		 * 
		 * @param env
		 * @param docShipmentListOutput
		 * @param docItem
		 * @param pickingFlag
		 * @return
		 * @throws Exception
		 */
		public Document updateShipmentLines(YFSEnvironment env, Document docShipmentListOutput, Document docItem, String pickingFlag)
		throws Exception {

			if (log.isDebugEnabled()) {
				log.debug("Method input doc shipmentItemLvlConsolidation: " + SCXmlUtil.getString(docShipmentListOutput));
			}

			Element eleShipment = docShipmentListOutput.getDocumentElement();
			//Element eleShipment = SCXmlUtil.getChildElement(eleShipmentListOutput, KohlsXMLLiterals.E_SHIPMENT);
			String shipmentKey = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
			String orderNo = eleShipment.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);

			NodeList nodeShipmentLineList = eleShipment.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);

			if (!YFCObject.isVoid(docItem)) {
				Element eleItems = docItem.getDocumentElement();
				if (!YFCObject.isVoid(eleItems)) {
					NodeList nodeItemList = eleItems.getElementsByTagName(KohlsXMLLiterals.E_ITEM);
					
					// in case item isn't found in MLS, don't process
					if (!YFCObject.isVoid(nodeShipmentLineList) && !YFCObject.isVoid(nodeItemList)) {
						for(int i=0; i<nodeShipmentLineList.getLength(); i++) {
							Element eleShipmentLine = (Element)  nodeShipmentLineList.item(i);
							String strShipmentItemID =  eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
							String shipmentLineNo = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO);
							String primeLineNo = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
							String orderHeaderKey = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
							String sQty = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_QUANTITY);
							String mlsItemFound="N";
							String strMLSItemID="";
							String strSRDESC = " ";
							Element eleMlsItem=null;
							Element eleMlsLoc=null;
							for(int j=0; j<nodeItemList.getLength(); j++) {
								eleMlsItem = (Element) nodeItemList.item(j);
								strMLSItemID = eleMlsItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
								if (strShipmentItemID.equals(strMLSItemID) 
										|| strMLSItemID.equalsIgnoreCase(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + strShipmentItemID))) {
									mlsItemFound = "Y"; break;
								}
							}
			
							if (mlsItemFound.equals(KohlsConstant.V_Y)) {
			
								eleMlsLoc = SCXmlUtil.getChildElement(eleMlsItem, KohlsXMLLiterals.E_LOCATIONS);
			
								Document docShipmentLine = eleShipmentLine.getOwnerDocument();
								Element eleMLSItemLocs = (Element) docShipmentLine.importNode(eleMlsLoc, true);
								eleShipmentLine.appendChild(eleMLSItemLocs);
			
								double dQty = Double.parseDouble(sQty);
								int iQty = (int) dQty;
								String strQty = iQty + "";
			
								updatingStoreEventsHFTable(env, shipmentKey, orderNo, orderHeaderKey, primeLineNo, shipmentLineNo, strQty, strSRDESC, env.getUserId(),
										KohlsConstant.V_PICKING_IN_PROGRESS );
							}
						}
					}
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("Picking Flag: " +  pickingFlag);
			}

			if (pickingFlag.equals(KohlsConstant.V_Y)) {
				Document docChangeShipmentInput = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);
				Element eleChangeShipmentInput = docChangeShipmentInput.getDocumentElement();
				eleChangeShipmentInput.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, KohlsConstant.V_KOHLS_COM);
				eleChangeShipmentInput.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, shipmentKey);
				eleChangeShipmentInput.setAttribute(KohlsXMLLiterals.A_TRANSACTION_ID, KohlsConstant.V_PROCESS_BACK_ROOM_PICK);
				Document changeShipmentStatus =
					KohlsCommonUtil.invokeAPI( env,  KohlsConstant.API_CHANGE_SHIPMENT_STATUS, docChangeShipmentInput);
					
				eleChangeShipmentInput.setAttribute(KohlsConstant.TMP_TIER_FLAG, KohlsConstant.V_TIER0);
				new KohlsResolveShipAlerts().updateShipmentTierFlag(env, docChangeShipmentInput);	
			}

			return docShipmentListOutput;
		}
		
		// Method to set LocationID, Description, Barcode data to env as an Object
		
		public void settingMLSOutputEnv(YFSEnvironment env, String shipmentNo,
				String shipmentLineNo, String sItemID, String desc, String barcode, String signID) {
		
			KohlsLocDetailsTransaction transacObj = new KohlsLocDetailsTransaction();
			transacObj.setShipmentNo(shipmentNo);
			transacObj.setShipmentLineNo(shipmentLineNo);
			transacObj.setsItemID(sItemID);
			transacObj.setBarcode(barcode);
			transacObj.setDesc(desc);
			transacObj.setSignID(signID);
			env.setTxnObject("LocDetailsTxnObj", transacObj);
			KohlsLocDetailsTransaction transacObjOut = (KohlsLocDetailsTransaction) env.getTxnObject("LocDetailsTxnObj");
			
			log.debug(transacObjOut.getShipmentNo());
			log.debug(transacObjOut.getShipmentLineNo());
			log.debug(transacObjOut.getsItemID());
		}

		
		//Method to Update HangOff Store Events table on Short Pick event
		private static void updatingShortPickEventsHFTable(YFSEnvironment env,
				String shipmentKey, String orderNo, String orderHeaderKey, String lineNo, String shipmentLineNo, String qty, String locId,
				String userId, String eventType, String reasonCode,
				String reasonText) throws Exception {

			Document docShipStoreEvents = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT_STORE_EVENTS);
			Element eleShipStoreEventsIn = docShipStoreEvents.getDocumentElement();
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, shipmentKey);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, shipmentLineNo);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, orderNo);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderHeaderKey);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, lineNo);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_QUANTITY, qty);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_LOCATIONID, locId);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_USER_ID, userId);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_EVENT_TYPE, eventType);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_REASON_CODE, reasonCode);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, reasonText);

			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_MODIFY_USERID, env.getUserId());
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_CREATE_PROGID, env.getUserId());
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_MODIFY_PROGID, env.getUserId());

			if (log.isDebugEnabled()) {
				log.debug(SCXmlUtil.getString(docShipStoreEvents));
			}

			KohlsCommonUtil.invokeService( env, KohlsConstant.SERVICE_CRTE_PUB_SHIP_STR_EVNTS, docShipStoreEvents);
		}
		
		//Added by Baijayanta for Item Legal Callback
		
		public static void updatingItemLegalCallbackEventsHFTable(YFSEnvironment env,String eventType,
				String shipmentKey, String orderNo, String orderHeaderKey, String Shipnode, String lineNo, 
				String itemId,String reasonCode) throws Exception {

			Document docShipStoreEvents = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT_STORE_EVENTS);
			Element eleShipStoreEventsIn = docShipStoreEvents.getDocumentElement();
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_EVENT_TYPE, eventType);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, shipmentKey);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, orderNo);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderHeaderKey);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_SHIP_NODE, Shipnode);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, lineNo);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_ITEM_ID, itemId);
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_REASON_CODE, reasonCode);
			
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_MODIFY_USERID, env.getUserId());
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_CREATE_PROGID, env.getUserId());
			eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_MODIFY_PROGID, env.getUserId());

			if (log.isDebugEnabled()) {
				log.debug(SCXmlUtil.getString(docShipStoreEvents));
			}

			KohlsCommonUtil.invokeService( env, KohlsConstant.SERVICE_CRTE_PUB_SHIP_STR_EVNTS, docShipStoreEvents);
		}
		
		//Method to Update HangOff Store Events table on various events
		public static void updatingStoreEventsHFTable(YFSEnvironment env,
				String shipmentKey, String orderNo, String orderHeaderKey, String lineNo, String shipmentLineNo, String qty, String locId,
				String userId, String eventType) throws Exception {

			if ( eventType.equals(KohlsConstant.V_PICKING_IN_PROGRESS) || eventType.equals(KohlsConstant.V_ITEM_IS_BEING_PICKED) ) {

				Document docShipStoreEvents = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT_STORE_EVENTS);
				Element eleShipStoreEventsIn = docShipStoreEvents.getDocumentElement();
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, shipmentKey);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, shipmentLineNo);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, orderNo);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderHeaderKey);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, lineNo);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_QUANTITY, qty);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_LOCATIONID, locId);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_USER_ID, userId);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_EVENT_TYPE, eventType);

				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_MODIFY_USERID, env.getUserId());
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_CREATE_PROGID, env.getUserId());
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_MODIFY_PROGID, env.getUserId());

				if (log.isDebugEnabled()) {
					log.debug(SCXmlUtil.getString(docShipStoreEvents));
				}

				KohlsCommonUtil.invokeService( env, KohlsConstant.SERVICE_CRTE_PUB_SHIP_STR_EVNTS, docShipStoreEvents);

			}
			if ( eventType.equals(KohlsConstant.V_PICKING_SUSPENDED) || eventType.equals(KohlsConstant.V_PICKING_RESUMED)
					|| eventType.equals(KohlsConstant.V_UNDO_PICKING) || eventType.equals(KohlsConstant.V_PICKING_COMPLETED) ||
						eventType.equals(KohlsConstant.V_PLACED_IN_HOLD_AREA) || eventType.equals(KohlsConstant.V_READY_FOR_CUSTOMER_PICKUP) ) {

				Document docShipStoreEvents = SCXmlUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT_STORE_EVENTS);
				Element eleShipStoreEventsIn = docShipStoreEvents.getDocumentElement();
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, shipmentKey);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, orderNo);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderHeaderKey);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_LOCATIONID, locId);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_USER_ID, userId);
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_EVENT_TYPE, eventType);

				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_MODIFY_USERID, env.getUserId());
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_CREATE_PROGID, env.getUserId());
				eleShipStoreEventsIn.setAttribute(KohlsXMLLiterals.A_MODIFY_PROGID, env.getUserId());

				if (log.isDebugEnabled()) {
					log.debug(SCXmlUtil.getString(docShipStoreEvents));
				}

				KohlsCommonUtil.invokeService( env, KohlsConstant.SERVICE_CRTE_PUB_SHIP_STR_EVNTS, docShipStoreEvents);
			}

		}
		
		//Method to Obtain Store Events related input during On Success of Hold Location transaction
		public void placedHoldAreasOnSuccess(YFSEnvironment env, Document inDoc) throws Exception
		{
			Element mainElement = inDoc.getDocumentElement();
			String shipmentKey = mainElement.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
			String orderNo = mainElement.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
			String orderHeaderKey = mainElement.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
			String locId = mainElement.getAttribute(KohlsXMLLiterals.A_LOCATIONID);
			String userId = env.getUserId();
			String eventType = KohlsConstant.V_PLACED_IN_HOLD_AREA;
			String lineNo = " ";
			String shipmentLineNo = " ";
			String qty = " ";
			if (log.isDebugEnabled()) {
				log.debug(shipmentKey);
				log.debug(orderNo);
				log.debug(lineNo);
				log.debug(qty);
				log.debug(locId);
				log.debug(eventType);
			}
			updatingStoreEventsHFTable(env, shipmentKey, orderNo, orderHeaderKey, lineNo, shipmentLineNo, qty, locId, userId, eventType);
		}
		
		//Method to Obtain Store Events related input during On Success of Customer Pick transaction
		public void readyCustomerPickupOnSuccess(YFSEnvironment env, Document inDoc) throws Exception
		{
			Element mainElement = inDoc.getDocumentElement();
			String shipmentKey = mainElement.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
			String orderNo = mainElement.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
			String orderHeaderKey = mainElement.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
			String locId = mainElement.getAttribute(KohlsXMLLiterals.A_LOCATIONID);
			String userId = env.getUserId();
			String eventType = KohlsConstant.V_READY_FOR_CUSTOMER_PICKUP;
			String lineNo = " ";
			String shipmentLineNo = " ";
			String qty = " ";
			if (log.isDebugEnabled()) {
				log.debug(shipmentKey);
				log.debug(orderNo);
				log.debug(lineNo);
				log.debug(qty);
				log.debug(locId);
				log.debug(eventType);
			}
			updatingStoreEventsHFTable(env, shipmentKey, orderNo, orderHeaderKey, lineNo, shipmentLineNo, qty, locId, userId, eventType);
		}
		
		public String getPropertyValue(String property) {

			String propValue;
			propValue = YFSSystem.getProperty(property);
			if(YFCCommon.isVoid(propValue)){
				propValue = property;
			}
			return propValue;

		}

		public void setProperties(Properties prop) throws Exception {
			this.props = prop;
		}
		
		public String openImageHttpRequest ( YFSEnvironment env,  String openAPIItemURL) throws IOException, Exception
		{
			// see if we need to bypass open api
			String openApiOutput = "";
			if (YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.V_BYPASS_OPEN_API)) || "N".equalsIgnoreCase(YFSSystem.getProperty(KohlsConstant.V_BYPASS_OPEN_API))) {
				StringBuffer outputContents = new StringBuffer();
				char[] cbuf = new char[500];
				BufferedReader reader = null;
				HttpClient httpClient = new HttpClient();
				HttpMethod openApiGet = new GetMethod(openAPIItemURL);
				
				// check to see if we need to use a proxy / port combination
				try {
					if (!YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.V_OPEN_API_PROXY_HOST)) && !YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.V_OPEN_API_PROXY_PORT))) {
						httpClient.getHostConfiguration().setProxy(
								YFSSystem.getProperty(KohlsConstant.V_OPEN_API_PROXY_HOST), Integer.parseInt(YFSSystem.getProperty(KohlsConstant.V_OPEN_API_PROXY_PORT)));
					}
				} catch (Exception ex) {
					// problem with setting proxy, default to the following settings
					httpClient.getHostConfiguration().setProxy("proxy.kohls.com", 3128);
				}
				
				openApiGet.setRequestHeader(KohlsConstant.V_OPEN_API_HEADER_KEY_PARAM, 
						YFSSystem.getProperty(KohlsConstant.V_OPEN_API_KEY));
				// set per default
				httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
				  new DefaultHttpMethodRetryHandler());
				try {
					httpClient.executeMethod(openApiGet);
					
					/*
					Setting the Connection timeout to input millisecs.
					connection.setConnectTimeout(Integer.parseInt(connectionTimeout));
					connection.setReadTimeout(Integer.parseInt(connectionReadTimeout));
					*/
					reader = new BufferedReader(new InputStreamReader(
							openApiGet.getResponseBodyAsStream()));
					int retVal = 0;
					int currentIteration = 0;
					while(true)	{
						retVal = reader.read(cbuf);
						if(retVal==-1)
							break;
						outputContents.append(String.valueOf(cbuf).substring(0, retVal));
						currentIteration++;
					}
					log.debug(outputContents.toString());
					openApiOutput = outputContents.toString();
				} catch (Exception ex) {
					// problem hitting open API, restort to default
					if (!YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.V_OPEN_API_RESPONSE))) {
						String openAPIImage =  YFSSystem.getProperty(KohlsConstant.V_OPEN_API_RESPONSE);
						String altText =  YFSSystem.getProperty(KohlsConstant.V_OPEN_API_ALT_TEXT);
						Document images = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ITEM_DETAILS);
						Element imagesElement =  images.createElement(KohlsXMLLiterals.E_IMAGES);
						Element imageElement =  images.createElement(KohlsXMLLiterals.E_IMAGE);
						Element urlElement =  images.createElement(KohlsXMLLiterals.E_URL);
						Element altTextElement =  images.createElement(KohlsXMLLiterals.E_ALT_TEXT);
						imagesElement.appendChild(imageElement);
						urlElement.setTextContent(openAPIImage);
						altTextElement.setTextContent(altText);
						imageElement.appendChild(urlElement);
						imageElement.appendChild(altTextElement);
						images.getDocumentElement().appendChild(imagesElement);
						openApiOutput = SCXmlUtil.getString(images);
					}
				}
			} else {
				// we are bypassing, return the system property
				if (!YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.V_OPEN_API_RESPONSE))) {
					String openAPIImage =  YFSSystem.getProperty(KohlsConstant.V_OPEN_API_RESPONSE);
					String altText =  YFSSystem.getProperty(KohlsConstant.V_OPEN_API_ALT_TEXT);
					Document images = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ITEM_DETAILS);
					Element imagesElement =  images.createElement(KohlsXMLLiterals.E_IMAGES);
					Element imageElement =  images.createElement(KohlsXMLLiterals.E_IMAGE);
					Element urlElement =  images.createElement(KohlsXMLLiterals.E_URL);
					Element altTextElement =  images.createElement(KohlsXMLLiterals.E_ALT_TEXT);
					imagesElement.appendChild(imageElement);
					urlElement.setTextContent(openAPIImage);
					altTextElement.setTextContent(altText);
					imageElement.appendChild(urlElement);
					imageElement.appendChild(altTextElement);
					images.getDocumentElement().appendChild(imagesElement);
					openApiOutput = SCXmlUtil.getString(images);
				}
			}
			return openApiOutput;
		}

		public Document callOpenAPIItemImageURL(YFSEnvironment env, Document docShipmentListOutput) throws Exception
		{
			//String connectionTimeOut = this.getPropertyValue(this.props.getProperty(KohlsConstant.V_CONNECTION_TIMEOUT));
			//String connectionReadTimeOut = this.getPropertyValue(this.props.getProperty(KohlsConstant.V_CONNECTION_READ_TIMEOUT));

			String openAPIBaseURL = this.getPropertyValue(this.props.getProperty(KohlsConstant.V_OPEN_API_BASEURL));
			String openAPIKey = this.getPropertyValue(this.props.getProperty(KohlsConstant.V_OPEN_API_KEY));
			String skuDetail = this.getPropertyValue(this.props.getProperty(KohlsConstant.V_SKU_DETAIL));

	    	Element shipmentEle = docShipmentListOutput.getDocumentElement();
	    	NodeList shipmentLineList = shipmentEle.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);


	    	for (int j=0; j< shipmentLineList.getLength(); j++) {

	    		Element eleShipmentLine = (Element) shipmentLineList.item(j);
	    		String itemID = "";
	    		// see if we have a translation
	    		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID)))) {
					// use the translated value
	    			itemID = YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
				} else {
					// no translation
					itemID = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
				}

	    		Element eleOrderLine = SCXmlUtil.getChildElement(eleShipmentLine, KohlsXMLLiterals.E_ORDER_LINE );
	    		Element eleItemDetails = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ITEM_DETAILS);
	    		
	    		// if item details are null, create it
	    		if (YFCObject.isVoid(eleItemDetails)) {
	    			// add it to the order line
	    			eleItemDetails = docShipmentListOutput.createElement(KohlsXMLLiterals.E_ITEM_DETAILS);
	    			eleOrderLine.appendChild(eleItemDetails);
	    		}
	    		
	    		String openAPIItemURL = openAPIBaseURL + "?skuCode=" + itemID + "&skuDetail=" + skuDetail;
	    		log.debug(openAPIItemURL);

	    		String outputContents = openImageHttpRequest(env, openAPIItemURL );
	    		log.debug("Output Contents: "+outputContents);
	        	Document respDoc = XMLUtil.getDocument(outputContents);

	        	if (respDoc != null) {
	        		Element respEle = respDoc.getDocumentElement();
	        		NodeList imagesList =  respEle.getElementsByTagName(KohlsXMLLiterals.E_IMAGES);

	        		Document docItemUrls = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ITEM_URLS);
					Element eleItemUrls = docItemUrls.getDocumentElement();
	        		for (int i=0; i<imagesList.getLength(); i++) {
	        			String sURL = "";
	        			String altText = "";
	        			Element imagesEle = (Element) imagesList.item(i);
	        			Element imageEle = SCXmlUtil.getChildElement(imagesEle, KohlsXMLLiterals.E_IMAGE);
	        			sURL = imageEle.getElementsByTagName(KohlsXMLLiterals.E_URL).item(0).getTextContent();
	        			if (!YFCObject.isVoid(imageEle.getElementsByTagName(KohlsXMLLiterals.E_ALT_TEXT)) && !YFCObject.isVoid(imageEle.getElementsByTagName(KohlsXMLLiterals.E_ALT_TEXT).item(0))) {
	        				altText = imageEle.getElementsByTagName(KohlsXMLLiterals.E_ALT_TEXT).item(0).getTextContent();
	        			}
	        			Element eleItemUrl = SCXmlUtil.createChild(eleItemUrls, KohlsXMLLiterals.E_ITEM_URL);
	        			eleItemUrl.setAttribute(KohlsXMLLiterals.A_URL, sURL);
	        			
	        			int lastSlash = sURL.lastIndexOf("/");
						if (lastSlash > 0) {
    						eleItemUrl.setAttribute(KohlsXMLLiterals.A_IMAGE_LOCATION, sURL.substring(0, lastSlash));
    						String imageId = sURL.substring(lastSlash+1, sURL.length());
    						//ootb doesn't like ? remove query parameters
    						int lastQuestion = imageId.lastIndexOf("?");
    						imageId = imageId.substring(0,lastQuestion);
    						eleItemUrl.setAttribute(KohlsXMLLiterals.A_IMAGE_ID, imageId);
    						eleItemUrl.setAttribute(KohlsXMLLiterals.A_IMAGE_LABEL, altText);
						}
	        			
	        			// use the first image as the primary image
	        			if (i==0) {
	        				// get the item details
	        				NodeList itemDetailsList = eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ITEM_DETAILS);
	        				if (!YFCObject.isVoid(itemDetailsList) && !YFCObject.isVoid(itemDetailsList.item(0))) {
	        					Element itemDetailsElement = (Element) itemDetailsList.item(0);
	        					// get the primary information
	        					NodeList primaryInformationList = itemDetailsElement.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION);
	        					if (!YFCObject.isVoid(primaryInformationList) && !YFCObject.isVoid(primaryInformationList.item(0))) {
	        						Element primaryInformationElement = (Element) primaryInformationList.item(0);
	        						// break the URL up into image location and image id
	        						lastSlash = sURL.lastIndexOf("/");
	        						if (lastSlash > 0) {
		        						primaryInformationElement.setAttribute(KohlsXMLLiterals.A_IMAGE_LOCATION, sURL.substring(0, lastSlash));
		        						String imageId = sURL.substring(lastSlash+1, sURL.length());
		        						//ootb doesn't like ? remove query parameters
		        						int lastQuestion = imageId.lastIndexOf("?");
		        						imageId = imageId.substring(0,lastQuestion);
		        						primaryInformationElement.setAttribute(KohlsXMLLiterals.A_IMAGE_ID, imageId);
		        						primaryInformationElement.setAttribute(KohlsXMLLiterals.A_IMAGE_LABEL, altText);
	        						}
	        					}
	        				}
	        			}
	        		}

	    			Document docShipmentLine = eleItemDetails.getOwnerDocument();
					Element eleItemUrlsParent = (Element) docShipmentLine.importNode(eleItemUrls, true);
					eleItemDetails.appendChild(eleItemUrlsParent);
	        	}
	    	}

	    	log.debug(SCXmlUtil.getString(docShipmentListOutput));
	    	System.out.println(SCXmlUtil.getString(docShipmentListOutput));
			return docShipmentListOutput;
		}
		
		public Document getOpenAPIImages(YFSEnvironment env, Document itemInputDocument) throws Exception
		{
			String openAPIBaseURL = YFSSystem.getProperty(KohlsConstant.V_OPEN_API_BASEURL);
			String skuDetail = YFSSystem.getProperty(KohlsConstant.V_SKU_DETAIL);

	    	Element itemElement = itemInputDocument.getDocumentElement();
	    	String itemID = "";
    		// see if we have a translation
    		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + itemElement.getAttribute(KohlsXMLLiterals.A_ITEM_ID)))) {
				// use the translated value
    			itemID = YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + itemElement.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
			} else {
				// no translation
				itemID = itemElement.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			}
    		
    		String openAPIItemURL = openAPIBaseURL + "?skuCode=" + itemID + "&skuDetail=" + skuDetail;
    		log.debug(openAPIItemURL);

    		String outputContents = openImageHttpRequest(env, openAPIItemURL );
        	Document itemImagesOutput = XMLUtil.getDocument(outputContents);

	    	log.debug(SCXmlUtil.getString(itemImagesOutput));
	    	System.out.println(SCXmlUtil.getString(itemImagesOutput));
			return itemImagesOutput;
		}
	}