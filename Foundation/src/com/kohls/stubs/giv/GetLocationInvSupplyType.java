package com.kohls.stubs.giv;

import java.util.Properties;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;

public class GetLocationInvSupplyType implements YIFCustomApi{

	private static final String SUPPLY_TYPE="SKU_SUPPLY_TYPE";
	private static final int NO_SUPPLY_QUANTITY=0;
	private static final int SUPPLY_QUANTITY=1;
	private YIFApi api;
	private YFCLogCategory log = YFCLogCategory
			.instance(GetLocationInvSupplyType.class);

	public GetLocationInvSupplyType() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	public int getRandomQty(int quantity){
		Random rand = new Random(); 
		int qty = rand.nextInt(quantity) + 1;
		return qty;
	}

	public Document getSupplyTypeForMultipleSku(YFSEnvironment env, Document inDoc) throws Exception {
		log.debug("##################getSupplyTypeForMultipleSku has started######################");
		Document outDoc = null;
		Element inEle = inDoc.getDocumentElement();
		outDoc = callGetTemplate(env, inEle);
		return outDoc;
	}

	private Document callGetTemplate(YFSEnvironment env, Element inEle) throws Exception {
		YFCDocument yfcInvSuppliesEle = YFCDocument.createDocument("InventorySupplies");
		NodeList invSupplyEle = inEle.getElementsByTagName("InventorySupply");
		for (int i = 0; i < invSupplyEle.getLength(); i++) {
			Element eleItem = (Element) invSupplyEle.item(i);
			log.verbose("::ItemID:"+eleItem.getAttribute("ItemID"));
			log.verbose("::Organization Code:"+eleItem.getAttribute("OrganizationCode"));
			log.verbose("::Ship Node:"+eleItem.getAttribute("ShipNode"));
			if(eleItem.getAttribute("ItemID")!=null && eleItem.getAttribute("ShipNode")!=null){
				String supplyType = getItemIDSupplyType(env, eleItem.getAttribute("ItemID"));
				if (supplyType.contains("SR")) {
					log.debug("ItemID is associated with Stock room::"+eleItem.getAttribute("ItemID")+supplyType);
					final String supplyTypeSR ="STOCK_ROOM.ex";
					if (eleItem.getAttribute("ItemID").equalsIgnoreCase("93352062")){
						appendGetSupplyTypeInput(inEle, eleItem, yfcInvSuppliesEle, supplyTypeSR, SUPPLY_QUANTITY);
					} else{
						appendGetSupplyTypeInput(inEle, eleItem, yfcInvSuppliesEle, supplyTypeSR, getRandomQty(2000));	
					}	
				} else if (supplyType.contains("SF")) {
					log.debug("ItemID is associated with SalesFloor::"+eleItem.getAttribute("ItemID")+supplyType);
					final String supplyTypeSF = "SALES_FLOOR.ex";
					appendGetSupplyTypeInput(inEle, eleItem, yfcInvSuppliesEle, supplyTypeSF, getRandomQty(1000));		 
				} else if (supplyType.contains("MIXED")){
					log.debug("ItemID is associated with Stock room and salesFloor ::"+eleItem.getAttribute("ItemID")+supplyType);
					appendGetSupplyTypeInput(inEle, eleItem, yfcInvSuppliesEle, getRandomQty(500));	 
				} else if (supplyType.contains("OUT")){
					log.debug("ItemID is associated with No Supplytype ::"+eleItem.getAttribute("ItemID")+supplyType);
					appendGetSupplyTypeInput(inEle, eleItem, yfcInvSuppliesEle, NO_SUPPLY_QUANTITY);
				}
			} else{
				log.debug("Invalid Input XML");
			}
		}
		return  yfcInvSuppliesEle.getDocument();
	}

	private String getItemIDSupplyType(YFSEnvironment env, String itemID) throws Exception {
		// TODO Auto-generated method stub
		String codeValue = null;
		StringBuffer itemIDSupplyTypeCheck = new StringBuffer();
		itemIDSupplyTypeCheck.append("<CommonCode CodeType='"+SUPPLY_TYPE+"'> </CommonCode>");	
		log.debug("itemIDSupplyTypeCheck element ::"+itemIDSupplyTypeCheck);
		Document commonCodeInXML = XmlUtils.createFromString(itemIDSupplyTypeCheck.toString());
		log.debug("CommonCode inputXML::"+commonCodeInXML);
		Document itemIDsupplyType = this.api.getCommonCodeList(env, commonCodeInXML);
		log.debug("Commoncodelist output::"+itemIDsupplyType);
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

	private void appendGetSupplyTypeInput(Element inEle, Element eleItem, YFCDocument yfcInvSuppliesEle, String supplyType, int qty) {
		// TODO Auto-generated method stub
		YFCElement yfcElemItemTemp = yfcInvSuppliesEle.getDocumentElement();
		YFCElement yfcElemExtnTemp = yfcElemItemTemp.createChild("Item");
		yfcElemItemTemp.appendChild(yfcElemExtnTemp);
		yfcElemExtnTemp.setAttribute("ItemID", eleItem.getAttribute("ItemID"));
		yfcElemExtnTemp.setAttribute("OrganizationCode", eleItem.getAttribute("OrganizationCode"));
		yfcElemExtnTemp.setAttribute("ProductClass", eleItem.getAttribute("ProductClass"));
		yfcElemExtnTemp.setAttribute("UnitOfMeasure", eleItem.getAttribute("UnitOfMeasure"));
		YFCElement yfcElemPrimaryInfo = yfcElemExtnTemp.createChild("Supplies");
		YFCElement yfsElemSupplyTemp = yfcElemPrimaryInfo.createChild("InventorySupply");
		yfsElemSupplyTemp.setAttribute("Quantity", qty);
		yfsElemSupplyTemp.setAttribute("SupplyType", supplyType);
		yfcElemPrimaryInfo.appendChild(yfsElemSupplyTemp);
		yfcElemExtnTemp.appendChild(yfcElemPrimaryInfo);

	}

	private void appendGetSupplyTypeInput(Element inEle, Element eleItem, YFCDocument yfcInvSuppliesEle, int qty) {
		// TODO Auto-generated method stub
		YFCElement yfcElemItemTemp = yfcInvSuppliesEle.getDocumentElement();
		YFCElement yfcElemExtnTemp = yfcElemItemTemp.createChild("Item");
		yfcElemItemTemp.appendChild(yfcElemExtnTemp);
		yfcElemExtnTemp.setAttribute("ItemID", eleItem.getAttribute("ItemID"));
		yfcElemExtnTemp.setAttribute("OrganizationCode", eleItem.getAttribute("OrganizationCode"));
		yfcElemExtnTemp.setAttribute("ProductClass", eleItem.getAttribute("ProductClass"));
		yfcElemExtnTemp.setAttribute("UnitOfMeasure", eleItem.getAttribute("UnitOfMeasure"));
		YFCElement yfcElemPrimaryInfo = yfcElemExtnTemp.createChild("Supplies");
		YFCElement yfsElemSupplyTemp = yfcElemPrimaryInfo.createChild("InventorySupply");
		yfsElemSupplyTemp.setAttribute("Quantity", qty*3);
		yfsElemSupplyTemp.setAttribute("SupplyType", "STOCK_ROOM.ex");	
		yfcElemPrimaryInfo.appendChild(yfsElemSupplyTemp);
		yfcElemExtnTemp.appendChild(yfcElemPrimaryInfo);
		YFCElement yfsElemSupplyTempStock = yfcElemPrimaryInfo.createChild("InventorySupply");
		yfsElemSupplyTempStock.setAttribute("Quantity", qty*2);
		yfsElemSupplyTempStock.setAttribute("SupplyType", "SALES_FLOOR.ex");
		yfcElemPrimaryInfo.appendChild(yfsElemSupplyTempStock);

	}
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
	}
}
