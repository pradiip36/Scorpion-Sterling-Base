package com.kohls.stubs.mls;

import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;


public class GetInventoryLocationsMLS implements YIFCustomApi{

	private static final String SUPPLY_TYPE="SKU_SUPPLY_TYPE";
	private static final String SKU_LOCATION="SKU_LOCATION";
	private static final String SKU_LOC_BARCODE="SKU_LOC_BARCODE";
	private static final String SKU_LOC_ESIGNID="SKU_LOC_ESIGNID";
	private static final String SKU_LOC_DET_FDC="SKU_LOC_DET_FDC";
	private YIFApi api;

	public GetInventoryLocationsMLS() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	private YFCLogCategory log = YFCLogCategory
			.instance(GetInventoryLocationsMLS.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	public Document GetItemLocations(YFSEnvironment env, Document inXml)
			throws Exception {
		Document outTemp = null;
		if(inXml!=null){
			outTemp = getMLSInventoryLocationsTemplate(env, inXml);
		}
		else {
			log.debug("Input Xml is Null::"+SCXmlUtil.getString(inXml));
			////System.out.println("Input Xml is Null::"+SCXmlUtil.getString(inXml));
		}
		return outTemp;
	}

	private Document getMLSInventoryLocationsTemplate(YFSEnvironment env, Document inXml) throws Exception {
		log.verbose("Input XML received"+SCXmlUtil.getString(inXml));
		////System.out.println("Input XML received::"+SCXmlUtil.getString(inXml));
		//Get the Document Element from input XML
		Element itemEle = inXml.getDocumentElement();
		YFCDocument getItemLocationsResultdoc = YFCDocument.createDocument("GetItemLocationsResponse");
		//getRequestElement(getItemLocationsResultdoc, inXml);
		YFCElement getItemLocationsRootElement = getItemLocationsResultdoc.getDocumentElement();
		YFCElement getItemLocationsResultEle = getItemLocationsRootElement.createChild("GetItemLocationsResult");
		getItemLocationsRootElement.appendChild(getItemLocationsResultEle);
		YFCElement responseElement = getItemLocationsResultEle.createChild("Response");
		getItemLocationsResultEle.appendChild(responseElement);
		YFCElement datarespEle = responseElement.createChild("Data");
		responseElement.appendChild(datarespEle);
		YFCElement itemLocationsEle = getItemLocationsResultEle.createChild("ItemLocations");
		datarespEle.appendChild(itemLocationsEle);
		//Fetching all the Nodes from the input XML received.
		////System.out.println("Root element::" + inXml.getDocumentElement().getNodeName());
		NodeList requestItemList = itemEle.getElementsByTagName("RequestItem");
		for (int i = 0; i < requestItemList.getLength(); i++) {
			Node reqItem = requestItemList.item(i);
			log.verbose("Current Element::"+reqItem.getNodeName());
			////System.out.println("Current Element::"+reqItem.getNodeName());
			NodeList reqItemChildNodes =  reqItem.getChildNodes();
			for (int j = 0; j < reqItemChildNodes.getLength(); j++){
				if (reqItemChildNodes.item(j).getNodeType() == Node.ELEMENT_NODE && 
						reqItemChildNodes.item(j).getNodeName().indexOf("ItemId") > -1){
					String itemID = reqItemChildNodes.item(j).getTextContent().trim();
					if(itemID!=null && !itemID.isEmpty()){
						log.verbose("ItemID::"+itemID);
						////System.out.println("ItemID::"+itemID);
						YFCElement InventoryLocationsEle = itemLocationsEle.createChild("InventoryLocations");	
						YFCElement itemElement = getItemLocationsResultEle.createChild("Item");
						YFCElement skuEle = itemElement.createChild("Sku");
						itemElement.appendChild(skuEle);
						skuEle.setNodeValue(itemID);
						YFCElement brandLabelEle = itemElement.createChild("BrandLabel");
						brandLabelEle.setNodeValue("TRAINING");
						itemElement.appendChild(brandLabelEle);					
						YFCElement skuStatusDesc = itemElement.createChild("SkuStatusDescription");
						skuStatusDesc.setNodeValue("Active");
						itemElement.appendChild(skuStatusDesc);
						InventoryLocationsEle.appendChild(itemElement);
						String supplyType = getItemIDDetails(env, itemID, SUPPLY_TYPE);
						log.verbose("SupplyType Received from common codes::"+supplyType);
						////System.out.println("SupplyType Received from common codes::"+supplyType);
						if (supplyType.contains("SF")){
							String supplyRootEleSF ="SalesFloorLocations";
							appendSalesFloorLocationTemplate(env, InventoryLocationsEle, getItemLocationsResultdoc, itemEle , itemID, supplyRootEleSF);	
						} else if (supplyType.contains("SR")){
							String supplyRootEleSR ="StockroomLocations";
							appendStockRoomLocationTemplate(env, InventoryLocationsEle, getItemLocationsResultdoc, itemEle , itemID, supplyRootEleSR);
						} else if (supplyType.contains("MIXED")){
							String supplyRootEle="SalesFloorLocations";
							appendMixedItemLocationTemplate(env, InventoryLocationsEle, getItemLocationsResultdoc, itemEle , itemID, supplyRootEle);
						}
						else {
							String supplyRootEle="StockroomLocations";
							appendStockRoomLocationTemplate(env, InventoryLocationsEle, getItemLocationsResultdoc, itemEle , itemID, supplyRootEle);
						}
					}
					else {
						log.debug("ItemID is NULL::"+itemID);
						////System.out.println("itemID is NULL::"+itemID);
					}
				} else{
					continue;
				}
			}  
		}
		////System.out.println("Return response::"+SCXmlUtil.getString(getItemLocationsResultdoc.getDocument()));
		return getItemLocationsResultdoc.getDocument();

	} 

	private void appendSalesFloorLocationTemplate(YFSEnvironment env, YFCElement InventoryLocationsEle, YFCDocument getItemLocationsResultdoc, Element itemEle , String itemID, String 
			SupplyTypeRootElement) throws Exception{
		//Constructing the "itemLocationsEle" root element.

		YFCElement supplyRootEle = InventoryLocationsEle.createChild(SupplyTypeRootElement);
		String skuLocation = getItemIDDetails(env, itemID, SKU_LOCATION);
		log.verbose("skuLocation in appendSalesFloorLocation method::"+skuLocation);
		//System.out.println("skuLocation in appendSalesFloorLocation method::"+skuLocation);
		if(skuLocation.contains(";")){
			String locationsList[] = skuLocation.split("\\;");
			for (String Skulocation:locationsList){
				String esignId = getItemIDDetails(env, Skulocation, SKU_LOC_ESIGNID);
				log.verbose("esignID in salesfloor IF::"+esignId);
				//System.out.println("esignID in salesfloor IF::"+esignId);
				YFCElement SalesFloorLocationEle = supplyRootEle.createChild("SalesFloorLocation");
				YFCElement ESignsEle = SalesFloorLocationEle.createChild("ESigns");
				YFCElement EsignChildEle = ESignsEle.createChild("Esign");
				SalesFloorLocationEle.appendChild(ESignsEle);
				ESignsEle.appendChild(EsignChildEle);
				YFCElement EsignID = EsignChildEle.createChild("ESignId");
				EsignID.setNodeValue(esignId);
				EsignChildEle.appendChild(EsignID);
				YFCElement locationID = SalesFloorLocationEle.createChild("LocationId");
				SalesFloorLocationEle.appendChild(locationID);
				locationID.setNodeValue(Skulocation);
				YFCElement FloorPadDescriptionEle = SalesFloorLocationEle.createChild("FloorPadDescription");
				YFCElement FloorPadIdEle = SalesFloorLocationEle.createChild("FloorPadId");
				YFCElement FullDescriptionCode = SalesFloorLocationEle.createChild("FullDescriptionCode");				
				FullDescriptionCode.setNodeValue(Skulocation);
				SalesFloorLocationEle.appendChild(FloorPadDescriptionEle);
				SalesFloorLocationEle.appendChild(FloorPadIdEle);
				SalesFloorLocationEle.appendChild(FullDescriptionCode);
			}
		} else {			
			String esignId = getItemIDDetails(env, skuLocation, SKU_LOC_ESIGNID);
			log.verbose("esignID in SF else::"+esignId);
			//System.out.println("esignID in salesfloor else::"+esignId);
			YFCElement SalesFloorLocationEle = supplyRootEle.createChild("SalesFloorLocation");
			YFCElement ESignsEle = SalesFloorLocationEle.createChild("ESigns");
			YFCElement EsignChildEle = ESignsEle.createChild("Esign");
			SalesFloorLocationEle.appendChild(ESignsEle);
			ESignsEle.appendChild(EsignChildEle);
			YFCElement EsignID = EsignChildEle.createChild("ESignId");
			EsignID.setNodeValue(esignId);
			ESignsEle.appendChild(EsignID);
			YFCElement locationID = SalesFloorLocationEle.createChild("LocationId");
			SalesFloorLocationEle.appendChild(locationID);
			locationID.setNodeValue(skuLocation);
			YFCElement FloorPadDescriptionEle = SalesFloorLocationEle.createChild("FloorPadDescription");
			YFCElement FloorPadIdEle = SalesFloorLocationEle.createChild("FloorPadId");
			YFCElement FullDescriptionCode = SalesFloorLocationEle.createChild("FullDescriptionCode");
			SalesFloorLocationEle.appendChild(FloorPadDescriptionEle);
			SalesFloorLocationEle.appendChild(FloorPadIdEle);
			SalesFloorLocationEle.appendChild(FullDescriptionCode);
			FullDescriptionCode.setNodeValue(skuLocation);
		}

	}

	private void appendStockRoomLocationTemplate(YFSEnvironment env, YFCElement InventoryLocationsEle, YFCDocument getItemLocationsResultdoc, Element itemEle , String itemID, String 
			SupplyTypeRootElement) throws Exception{
		YFCElement supplyRootEle = InventoryLocationsEle.createChild(SupplyTypeRootElement);
		String skuLocation = getItemIDDetails(env, itemID, SKU_LOCATION);
		log.verbose("SkuLocation in stockroom before if::"+skuLocation);
		if(skuLocation.contains(";")){
			String locationsList[] = skuLocation.split("\\;");
			for (String Skulocation:locationsList){
				log.verbose("SkuLocation in stockroom if::"+Skulocation);
				//System.out.println("SkuLocation in stockroom if::"+Skulocation);
				YFCElement StockroomLocationEle = supplyRootEle.createChild("StockroomLocation");
				YFCElement barCodeNumber = StockroomLocationEle.createChild("BarcodeNumber");	
				String locBarcode = getItemIDDetails(env, Skulocation, SKU_LOC_BARCODE);
				StockroomLocationEle.appendChild(barCodeNumber);	
				barCodeNumber.setNodeValue(locBarcode);
				YFCElement FloorDescriptionEle = StockroomLocationEle.createChild("FullDescriptionCode");
				YFCElement locSortCode = StockroomLocationEle.createChild("LocationSortCode");
				FloorDescriptionEle.setNodeValue(Skulocation);
				StockroomLocationEle.appendChild(FloorDescriptionEle);
				StockroomLocationEle.appendChild(locSortCode);
			}
		} else {			
			log.verbose("SkuLocation in stockroom else::"+skuLocation);	 
			YFCElement StockroomLocationEle = supplyRootEle.createChild("StockroomLocation");
			YFCElement barCodeNumber = StockroomLocationEle.createChild("BarcodeNumber");	
			String locBarcode = getItemIDDetails(env, skuLocation, SKU_LOC_BARCODE);
			StockroomLocationEle.appendChild(barCodeNumber);	
			barCodeNumber.setNodeValue(locBarcode);
			YFCElement FloorDescriptionEle = StockroomLocationEle.createChild("FullDescriptionCode");
			YFCElement locSortCode = StockroomLocationEle.createChild("LocationSortCode");
			FloorDescriptionEle.setNodeValue(skuLocation);
			StockroomLocationEle.appendChild(FloorDescriptionEle);
			StockroomLocationEle.appendChild(locSortCode);
		}

	}

	private void appendMixedItemLocationTemplate(YFSEnvironment env, YFCElement InventoryLocationsEle, YFCDocument getItemLocationsResultdoc, Element itemEle , String itemID, String 
			SupplyTypeRootElement) throws Exception{

		String SupplyTypeRootElementSF="SalesFloorLocations";
		YFCElement supplyRootEleSF = InventoryLocationsEle.createChild(SupplyTypeRootElementSF);
		String SupplyTypeRootElementSR="StockroomLocations";
		YFCElement supplyRootEleSR = InventoryLocationsEle.createChild(SupplyTypeRootElementSR);
		String skuLocation = getItemIDDetails(env, itemID, SKU_LOCATION);
		log.verbose("Skulocation in Mixed::"+skuLocation);
		if(skuLocation.contains(";")){
			String locationsList[] = skuLocation.split("\\;");
			for (String location:locationsList){
				if(location.contains(",")){
					String MixedlocationsList[] = location.split("\\,");	
					for (String SkuSplitlocation:MixedlocationsList){
						if(SkuSplitlocation.contains("SF")){
							log.verbose("Skulocation in Mixed SF before substring::"+location);
							//System.out.println("Skulocation in Mixed SF before substring::"+location);
							//String Skulocation = location.substring(3,location.length()).toString();
							log.verbose("Skulocation in Mixed SF before substring::"+SkuSplitlocation);
							String esignId = getItemIDDetails(env, SkuSplitlocation, SKU_LOC_ESIGNID);
							YFCElement SalesFloorLocationEle = supplyRootEleSF.createChild("SalesFloorLocation");
							YFCElement ESignsEle = SalesFloorLocationEle.createChild("ESigns");
							YFCElement EsignChildEle = ESignsEle.createChild("Esign");
							SalesFloorLocationEle.appendChild(ESignsEle);
							ESignsEle.appendChild(EsignChildEle);
							YFCElement EsignID = EsignChildEle.createChild("ESignId");
							EsignID.setNodeValue(esignId);
							EsignChildEle.appendChild(EsignID);
							YFCElement locationID = SalesFloorLocationEle.createChild("LocationId");
							SalesFloorLocationEle.appendChild(locationID);
							locationID.setNodeValue(SkuSplitlocation);
							YFCElement FloorPadDescriptionEle = SalesFloorLocationEle.createChild("FloorPadDescription");
							YFCElement FloorPadIdEle = SalesFloorLocationEle.createChild("FloorPadId");
							YFCElement FullDescriptionCode = SalesFloorLocationEle.createChild("FullDescriptionCode");
							SalesFloorLocationEle.appendChild(FloorPadDescriptionEle);
							SalesFloorLocationEle.appendChild(FloorPadIdEle);
							SalesFloorLocationEle.appendChild(FullDescriptionCode);
							FullDescriptionCode.setNodeValue(SkuSplitlocation);
						} else if(SkuSplitlocation.contains("SR")){
							log.verbose("Skulocation in Mixed SR before substring::"+SkuSplitlocation);
							YFCElement SalesFloorLocationEle = supplyRootEleSR.createChild("StockroomLocation");
							YFCElement barCodeNumber = SalesFloorLocationEle.createChild("BarcodeNumber");	
							String locBarcode = getItemIDDetails(env, SkuSplitlocation, SKU_LOC_BARCODE);
							log.verbose("location Barcode::"+locBarcode);
							//System.out.println("location Barcode in Mixed else::"+locBarcode);
							SalesFloorLocationEle.appendChild(barCodeNumber);	
							barCodeNumber.setNodeValue(locBarcode);
							YFCElement FloorDescriptionEle = SalesFloorLocationEle.createChild("FullDescriptionCode");
							YFCElement locSortCode = SalesFloorLocationEle.createChild("LocationSortCode");
							FloorDescriptionEle.setNodeValue(SkuSplitlocation);
							SalesFloorLocationEle.appendChild(FloorDescriptionEle);
							SalesFloorLocationEle.appendChild(locSortCode);
						}

					}
				} 
				else {
					log.verbose("Location List does not contain MIXED location list::Please verify::"+location);
				}
			}
		}
	}

	private String getItemIDDetails(YFSEnvironment env, String itemID, String CodeType) throws Exception {
		String codeValue = null;
		StringBuffer codeTypeCheck = new StringBuffer();
		codeTypeCheck.append("<CommonCode CodeType='"+CodeType+"'> </CommonCode>");	
		log.verbose("codeTypeCheck element ::"+codeTypeCheck);
		//System.out.println("codeTypeCheck element ::"+codeTypeCheck);
		Document commonCodeInXML = XmlUtils.createFromString(codeTypeCheck.toString());
		log.verbose("CommonCode inputXML::"+SCXmlUtil.getString(commonCodeInXML));
		//System.out.println("CommonCode inputXML::"+SCXmlUtil.getString(commonCodeInXML));
		Document itemIDsupplyType = this.api.getCommonCodeList(env, commonCodeInXML);
		log.verbose("Commoncodelist output::"+SCXmlUtil.getString(itemIDsupplyType));
		//System.out.println("Commoncodelist output::"+SCXmlUtil.getString(itemIDsupplyType));
		Element commonCodeListEle = itemIDsupplyType.getDocumentElement();
		NodeList codeList = commonCodeListEle.getElementsByTagName("CommonCode");		
		for (int i = 0; i < codeList.getLength(); i++) {
			Element commoncodeEle = (Element) codeList.item(i); 
			String codeDesc = commoncodeEle.getAttribute("CodeShortDescription");
			if(itemID.equals(codeDesc)){
				codeValue = commoncodeEle.getAttribute("CodeValue");
				break;
			}		
		}
		return codeValue;
	}

	public Document updateLocationItems(YFSEnvironment env, Document inXml){

		try {
			Thread.sleep(720);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		log.debug("Input Xml::"+SCXmlUtil.getString(inXml));
		//System.out.println("Input Xml is Null::"+SCXmlUtil.getString(inXml));
		return inXml;
	}

	public Document GetHoldDescription(YFSEnvironment env, Document inXml)
			throws Exception {
		Document outTemp = null;
		if(inXml!=null){
			outTemp = getLocationDetails(env, inXml);
		}
		else {
			log.debug("Input Xml is Null::"+SCXmlUtil.getString(inXml));
			//System.out.println("Input Xml is Null::"+SCXmlUtil.getString(inXml));
		}
		return outTemp;
	}

	private Document getLocationDetails(YFSEnvironment env, Document inXml) throws Exception{
		log.verbose("Input XML received"+SCXmlUtil.getString(inXml));
		//System.out.println("Input XML received::"+SCXmlUtil.getString(inXml));
		//Get the Document Element from input XML
		Element itemEle = inXml.getDocumentElement();
		YFCDocument getItemLocationsResultdoc = YFCDocument.createDocument("GetLocationDetailsResponse");
		//getRequestElement(getItemLocationsResultdoc, inXml);
		YFCElement getLocationsRootElement = getItemLocationsResultdoc.getDocumentElement();
		YFCElement getLocationsResultEle = getLocationsRootElement.createChild("GetLocationDetailsResult");
		getLocationsRootElement.appendChild(getLocationsResultEle);
		YFCElement responseElement = getLocationsResultEle.createChild("Response");
		getLocationsResultEle.appendChild(responseElement);
		YFCElement datarespEle = responseElement.createChild("Data");
		responseElement.appendChild(datarespEle);
		//Fetching all the Nodes from the input XML received.
		//System.out.println("Root element::" + inXml.getDocumentElement().getNodeName());
		NodeList requestlocationsIDsList = itemEle.getElementsByTagName("LocationIds");	
		for (int i = 0; i < requestlocationsIDsList.getLength(); i++) {
			Node locationsIDsList = requestlocationsIDsList.item(i);
			log.verbose("Current Element::"+locationsIDsList);
			//System.out.println("Current Element::"+locationsIDsList);
			NodeList locationIDs =  locationsIDsList.getChildNodes();
			for (int j = 0; j < locationIDs.getLength(); j++){
				if (locationIDs.item(j).getNodeType() == Node.ELEMENT_NODE && 
						locationIDs.item(j).getNodeName().indexOf("string") > -1){
					String locID = locationIDs.item(j).getTextContent().trim();
					if(locID!=null && !locID.isEmpty()){
						log.verbose("ItemID::"+locID);
						//System.out.println("ItemID::"+locID);
						String locbarCode = locID;
						String supplyRootEleSR ="StockroomLocations";
						String locDesc = getItemIDDetails(env, locID, SKU_LOC_DET_FDC);
						if(locDesc.equalsIgnoreCase(null)){
							locbarCode = null;	
							appendLocDescriptionTemplate(env, datarespEle, itemEle , locDesc, locbarCode, supplyRootEleSR);
						} else {							
							appendLocDescriptionTemplate(env, datarespEle, itemEle , locDesc, locbarCode, supplyRootEleSR);
						}
					}
					else {
						log.debug("ItemID is NULL::"+locID);
						//System.out.println("itemID is NULL::"+locID);
					}
				}
				//System.out.println("Return response::"+SCXmlUtil.getString(getItemLocationsResultdoc.getDocument()));
			}
		}
		return getItemLocationsResultdoc.getDocument();
	}

	private void appendLocDescriptionTemplate(YFSEnvironment env,
			YFCElement datarespEle, Element itemEle, String locDesc, String locbarCode, String supplyRootEleSR) throws Exception {
		String SupplyTypeRootElementSR="StockroomLocations";
		log.verbose("SkuLocation::"+locDesc);
		//System.out.println("Skulocation in SR::"+locDesc);
		YFCElement supplyRootEle = datarespEle.createChild(SupplyTypeRootElementSR);
		YFCElement StockRoomLocationEle = supplyRootEle.createChild("StockroomLocation");
		YFCElement barCodeNumber = StockRoomLocationEle.createChild("BarcodeNumber");
		log.verbose("location Barcode::"+locbarCode);
		//System.out.println("location Barcode in Mixed else::"+locbarCode);
		StockRoomLocationEle.appendChild(barCodeNumber);	
		barCodeNumber.setNodeValue(locbarCode);
		YFCElement FloorDescriptionEle = StockRoomLocationEle.createChild("FullDescriptionCode");
		YFCElement locSortCode = StockRoomLocationEle.createChild("LocationSortCode");
		FloorDescriptionEle.setNodeValue(locDesc);
		StockRoomLocationEle.appendChild(FloorDescriptionEle);
		StockRoomLocationEle.appendChild(locSortCode);
	}

	/*private void getRequestElement(YFCDocument getItemLocationsResultdoc, Document inXml) {
	//Constructing Request Element
	Element itemEle = inXml.getDocumentElement();
	YFCElement getItemLocationsResultEle = getItemLocationsResultdoc.getDocumentElement();
	YFCElement requestEle = getItemLocationsResultEle.createChild("Request");
	getItemLocationsResultEle.appendChild(requestEle);
	YFCElement dataEle = requestEle.createChild("Data");
	requestEle.appendChild(dataEle);
	YFCElement itemsEle = requestEle.createChild("ItemId");
	itemsEle.setNodeValue(itemEle.getNodeValue());
	YFCElement ItemType = requestEle.createChild("ItemType");
	ItemType.setNodeValue(itemEle.getNodeValue());
	YFCElement Quantity = requestEle.createChild("Quantity");
	itemsEle.setNodeValue(itemEle.getNodeValue());
	YFCElement locationFilterEle = dataEle.createChild("LocationFilters");
	YFCElement locationFilterTypeEnumEleSF = locationFilterEle.createChild("LocationFilterTypeEnum");
	locationFilterTypeEnumEleSF.setNodeValue("SalesFloor");
	YFCElement locationFilterTypeEnumEleSR = locationFilterEle.createChild("LocationFilterTypeEnum");
	locationFilterTypeEnumEleSR.setNodeValue("SalesFloor");
	locationFilterEle.appendChild(locationFilterTypeEnumEleSF);
	locationFilterEle.appendChild(locationFilterTypeEnumEleSR);
	YFCElement headerEle = requestEle.createChild("Header");
	YFCElement userIDEle = headerEle.createChild("UserId");
	requestEle.appendChild(headerEle);
	headerEle.appendChild(userIDEle);
}*/
}
