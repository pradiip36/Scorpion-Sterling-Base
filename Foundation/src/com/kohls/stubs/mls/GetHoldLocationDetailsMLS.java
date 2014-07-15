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

public class GetHoldLocationDetailsMLS implements YIFCustomApi {
	
	
	private static final String SKU_LOC_BARCODE="SKU_LOC_BARCODE";
	private static final String SKU_LOC_DET_FDC="SKU_LOC_DET_FDC";
	private YIFApi api;

	public GetHoldLocationDetailsMLS() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	private YFCLogCategory log = YFCLogCategory
			.instance(GetInventoryLocationsMLS.class);
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
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
		YFCDocument getItemLocationsResultdoc = YFCDocument.createDocument("GetItemLocationsResponse");
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
			//////System.out.println("Current Element::"+locationsIDsList);
			NodeList locationIDs =  locationsIDsList.getChildNodes();
			for (int j = 0; j < locationIDs.getLength(); j++){
				if (locationIDs.item(j).getNodeType() == Node.ELEMENT_NODE && 
						locationIDs.item(j).getNodeName().indexOf("string") > -1){
					String locID = locationIDs.item(j).getTextContent().trim();
					if(locID!=null && !locID.isEmpty()){
						log.verbose("ItemID::"+locID);
						//System.out.println("ItemID::"+locID);
						String supplyRootEleSR ="StockroomLocations";
						String locDesc = getItemIDDetails(env, locID, SKU_LOC_DET_FDC);
						String locbarCode = getItemIDDetails(env, locDesc, SKU_LOC_BARCODE);
						appendLocDescriptionTemplate(env, datarespEle, itemEle , locDesc, locbarCode, supplyRootEleSR);
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
	
	private String getItemIDDetails(YFSEnvironment env, String itemID, String CodeType) throws Exception {
		// TODO Auto-generated method stub
		String codeDesc = null;
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
			String codeValue = commoncodeEle.getAttribute("CodeValue");
			if(itemID.equals(codeValue)){
				codeDesc = commoncodeEle.getAttribute("CodeShortDescription");
				break;
			}		
		}
		return codeDesc;
	}
}
