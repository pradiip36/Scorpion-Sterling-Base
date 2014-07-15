package com.kohls.po.api;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
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
 * This class processes  DSV Inventory  Sync Messages
 * @author Priyadarshini
 *
 */
public class KohlsDSVProcessInvAPI implements YIFCustomApi{
	
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KohlsDSVProcessInvAPI.class.getName());
	private  YIFApi api;
	private KohlsInventoryAdjWrapperAPI inventoryAdjWrapperApi;
	
	private static final YFCLogCategory dsvLog = YFCLogCategory.instance("DSVInvLogger."
			+ KohlsDSVProcessInvAPI.class);

	public KohlsDSVProcessInvAPI() throws YIFClientCreationException{	
			api = YIFClientFactory.getInstance().getLocalApi();
			inventoryAdjWrapperApi = new KohlsInventoryAdjWrapperAPI();
	}
	
	
	@Override
	public void setProperties(Properties arg0) throws Exception {		 
		// do nothing
	}
	
	/**
	 * This method process DSV Inventory Sync messages
	 * @param env
	 * @param inXML
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws YIFClientCreationException
	 * @throws TransformerException
	 */
	public void processDSVInventorySync(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, YIFClientCreationException, TransformerException, Exception{
		if(log.isVerboseEnabled()){
			log.verbose(KohlsUtil.extractStringFromDocument(inXML));
		}
		
		boolean chkError = false;
		
		// check to validate the xml
		chkError = validateInputXML(env, inXML);
		if(chkError){
			return;
		}
		
		String sIsProcessAvailabilityActive =  KohlsUtil.getCommonCodeValue(env, KohlsConstant.IS_PROCESS_AVAILABILITY_ACTIVE);
		
		if (sIsProcessAvailabilityActive.trim().equals(KohlsConstant.YES)){
			//Added -- OASIS_SUPPORT 13/2/2012	
			boolean isZeroDSVEnabled = this.isZeroDSVEnabled(env);
			NodeList nlItem = inXML.getElementsByTagName(KohlsXMLLiterals.E_ITEM);
			Document docAdjInvInput = XMLUtil.createDocument("Items");
			for (int i=0; i < nlItem.getLength(); i++) {
				if (isZeroDSVEnabled == true) {
					double dItemQuantity = 0;
					boolean isItemTypeDSV = false;
					boolean isItemDiscontinued = false;
	
					Element eleItem = (Element) nlItem.item(i);
					Document docGetItemDetails = this.getItemDetails(env, eleItem);
					
					if (docGetItemDetails != null) {
						Element eleItemList = docGetItemDetails.getDocumentElement();
						Element eleItemPrimInfo = (Element) eleItemList.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
					
						String strItemType = eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_TYPE);
						if (eleItem.getAttribute(KohlsXMLLiterals.A_QUANTITY) != null && eleItem.getAttribute(KohlsXMLLiterals.A_QUANTITY).trim() != ""){
							dItemQuantity = new Double(eleItem.getAttribute(KohlsXMLLiterals.A_QUANTITY)).doubleValue();
						}
						if ("DS".equals(strItemType)) {
							isItemTypeDSV = true;
						}
						String strItemAvailibility = eleItem.getAttribute("Available"); 
						if (strItemAvailibility.equalsIgnoreCase("NO") || strItemAvailibility.equalsIgnoreCase("DISCONTINUED")) {
							isItemDiscontinued = true;
						}
					}
					if ((isItemTypeDSV == true && dItemQuantity == 0) || ((isItemTypeDSV == true && dItemQuantity > 0 && isItemDiscontinued))) {
						
						createAdjInvInputXML(env, eleItem, docAdjInvInput);
					
						//End -- OASIS_SUPPORT 13/2/2012
					} else {
						if(log.isDebugEnabled()){
							log.debug("Calling processAvailabilitySnapShot API ");
						}
						api.processAvailabilitySnapShot(env, getProcessAvailabilityXml((Element) nlItem.item(i)));
					}

				} else {
					if(log.isDebugEnabled()){
						log.debug("Calling processAvailabilitySnapShot API ");
					}
					api.processAvailabilitySnapShot(env, getProcessAvailabilityXml((Element) nlItem.item(i)));
				}
				
			}
			//Added -- OASIS_SUPPORT 13/2/2012
				Element eleAdjInvInputXML = docAdjInvInput.getDocumentElement();
				NodeList nlItemAdjInv =  docAdjInvInput.getElementsByTagName(KohlsXMLLiterals.E_ITEM);

				if (nlItemAdjInv != null && nlItemAdjInv.getLength() > 0){ 
					if(log.isDebugEnabled()){
						log.debug("Calling adjustInventory API with Input" + KohlsUtil.getXMLString(docAdjInvInput));
					}
					this.inventoryAdjWrapperApi.adjustInventory(env, docAdjInvInput);
				}
			//End -- OASIS_SUPPORT 13/2/2012
				
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Calling DSVInvAdjProcessSubService Service ");
			}
			api.executeFlow(env, KohlsConstant.DSV_INV_ADJ_PROCESS_SUB_SERVICE, inXML);
		}
		
	}
	
	/**
	 * This method checks the data in the input xml for possible errors
	 * 
	 * @param env
	 * @param inXML
	 * @throws YFSException
	 * @throws TransformerException
	 * @throws RemoteException
	 */
	public boolean validateInputXML(YFSEnvironment env, Document inXML)
			throws YFSException, TransformerException, RemoteException {

		if (log.isVerboseEnabled()) {
			log.verbose(KohlsUtil.extractStringFromDocument(inXML));
		}
		Element eleRoot = inXML.getDocumentElement();

		Element eleLstPrimInfo;
		String strItemType = "";
		String strPrimSupply = "";
		String expMsg = "";
		Element eleLstItem;

		NodeList ndItem = eleRoot.getElementsByTagName(KohlsXMLLiterals.E_ITEM);
		if (ndItem.getLength() > 0) {
			Element eleItem = (Element) ndItem.item(0);

			String strItemID = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			String strShipNode = eleItem
			.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
			if(null==strItemID || strItemID.equals("") || strItemID.equals(" ")){

				expMsg = "Item ID Doesn't Exist";
				writeDSVLog(strItemID, strShipNode, expMsg);
				return true;
			}

			String strUnAvailInv = eleItem.getAttribute("Available");

			env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST,
					this.getItemListTemplate());
			Document docGetItemListEx = this.api.getItemList(env,
					this.getItemListInputXML(env, strItemID));
			env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);

			Element eleGetItemDetails = docGetItemListEx.getDocumentElement();
			int inCnt = Integer.parseInt(eleGetItemDetails
					.getAttribute("TotalItemList"));

			if (inCnt > 0) {
				eleLstItem = (Element) eleGetItemDetails.getElementsByTagName(
						KohlsXMLLiterals.E_ITEM).item(0);

				eleLstPrimInfo = (Element) eleLstItem.getElementsByTagName(
						KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
				strItemType = eleLstPrimInfo
						.getAttribute(KohlsXMLLiterals.A_ITEM_TYPE);
				strPrimSupply = eleLstPrimInfo.getAttribute("PrimarySupplier");
			}

			if (inCnt == 0) {
				updateInvData(env, inXML);
				expMsg = "Invalid Item";
				writeDSVLog(strItemID, strShipNode, expMsg);
				return true;
				// Start -- Added for 21085,379,000 -- OASIS_SUPPORT 08/22/2012 //
				//Added DELETED in the else if condition
			} else if (strUnAvailInv.equalsIgnoreCase("NO") || strUnAvailInv.equalsIgnoreCase("DISCONTINUED")|| strUnAvailInv.equalsIgnoreCase("DELETED")) {
				// End -- Added for 21085,379,000 -- OASIS_SUPPORT 08/22/2012 //
				//Commented -- OASIS_SUPPORT 13/2/2012
				//updateInvData(env, inXML);
				expMsg = "Inventory for Item is Discontinued";
				writeDSVLog(strItemID, strShipNode, expMsg);
				return false;
			} else if (!strItemType.equalsIgnoreCase("DS")) {
				updateInvData(env, inXML);
				expMsg = "Item Not Attributed as Direct Ship";
				writeDSVLog(strItemID, strShipNode, expMsg);
				return true;
			} else if (strPrimSupply.equals("")
					|| !strPrimSupply.equalsIgnoreCase(strShipNode)) {
				updateInvData(env, inXML);
				expMsg = "Inventory Doesn't Match the Primary Vendor for the SKU";
				writeDSVLog(strItemID, strShipNode, expMsg);
				return true;
			}
		}
		return false;
	}


	private Document getProcessAvailabilityXml(Element eleItem) {
		
		YFCDocument yfcDocAvailabilitySnapShot = YFCDocument.createDocument(KohlsXMLLiterals.E_AVAILABILITY_SNAPSHOT);
		YFCElement  yfcEleAvailabilitySnapShot = yfcDocAvailabilitySnapShot.getDocumentElement();		
		
		YFCElement  yfcEleShipNode = yfcEleAvailabilitySnapShot.createChild(KohlsXMLLiterals.E_SHIP_NODE);
		YFCElement  yfcEleItem = yfcEleShipNode.createChild(KohlsXMLLiterals.E_ITEM);
		YFCElement  yfcEleAvailabilityDetails = yfcEleItem.createChild(KohlsXMLLiterals.E_AVAILABILITY_DETAILS);
		
		yfcEleShipNode.setAttribute(KohlsXMLLiterals.A_SHIPNODE, eleItem.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
		yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
		yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, eleItem.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS));
		yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM, eleItem.getAttribute(KohlsXMLLiterals.A_UOM));
		yfcEleItem.setAttribute(KohlsXMLLiterals.A_INV_ORG_CODE, eleItem.getAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE));
		yfcEleItem.setAttribute(KohlsXMLLiterals.A_DESCRIPTION , eleItem.getAttribute(KohlsXMLLiterals.A_REASON_CODE));
		
		yfcEleAvailabilityDetails.setAttribute(KohlsXMLLiterals.A_QUANTITY, eleItem.getAttribute(KohlsXMLLiterals.A_QUANTITY));
		yfcEleAvailabilityDetails.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE, eleItem.getAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE));
		
		return yfcDocAvailabilitySnapShot.getDocument();
		 
	}
	
	private void updateInvData(YFSEnvironment env, Document inXML) throws YFSException, RemoteException {
		
		
		Element eleRoot = inXML.getDocumentElement();		
		Element eleItem = (Element) eleRoot.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
		String strItemID = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
		String strShipNode = eleItem.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
		
		env.setApiTemplate("getInventorySupply",
				this.getInvSupplyTemplate());
		Document docGetInvSupplyOut = this.api.getInventorySupply(env,
				this.getInvSupplyInputXML(env, strItemID, strShipNode));
		env.clearApiTemplate("getInventorySupply");
		
		Element eleInvSupOut = docGetInvSupplyOut.getDocumentElement();
		Element eleSupplies = (Element)eleInvSupOut.getElementsByTagName("Supplies").item(0);
		NodeList ndInvSup = eleSupplies.getElementsByTagName("InventorySupply");
		if(ndInvSup.getLength()>0){
			api.processAvailabilitySnapShot(env, getProcessAvailabilityXml(eleItem));
		}		
	}
	
	private void writeDSVLog(String itemId, String shipNode, String msg) {
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("writeDSVLog Input : " + itemId+":"+shipNode+":"+msg);
		}
		dsvLog.error("Exception:"+msg+", "+"ItemID:"+itemId+", "+"ShipNode:"+shipNode);
	}
	
	private Document getItemListInputXML(YFSEnvironment env, String sItemID) {

		YFCDocument yfcDocGetItemList = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcEleGetItemList = yfcDocGetItemList.getDocumentElement();
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_ITEM_ID, sItemID);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, KohlsConstant.PRODUCT_CLASS_GOOD);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_UOM, KohlsConstant.UNIT_OF_MEASURE);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemList Input XML : " + XMLUtil.getXMLString(yfcDocGetItemList.getDocument()));
		}
		return yfcDocGetItemList.getDocument();
	}
	
	private Document getInvSupplyInputXML(YFSEnvironment env, String sItemID, String strShipNode) {

		YFCDocument yfcDocInvSupply = YFCDocument.createDocument("InventorySupply");
		YFCElement yfcEleInvSupply = yfcDocInvSupply.getDocumentElement();
		yfcEleInvSupply.setAttribute(KohlsXMLLiterals.A_ITEM_ID, sItemID);
		yfcEleInvSupply.setAttribute(KohlsXMLLiterals.A_SHIPNODE, strShipNode);
		yfcEleInvSupply.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, KohlsConstant.ITEM_ORGANIZATION_CODE);
		yfcEleInvSupply.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE, KohlsConstant.SUPPLY_TYPE);
		yfcEleInvSupply.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, KohlsConstant.PRODUCT_CLASS_GOOD);
		yfcEleInvSupply.setAttribute(KohlsXMLLiterals.A_UOM, KohlsConstant.UNIT_OF_MEASURE);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemList Input XML : " + XMLUtil.getXMLString(yfcDocInvSupply.getDocument()));
		}
		return yfcDocInvSupply.getDocument();
	}
	
	private Document getItemListTemplate() {

		YFCDocument yfcDocGetItemListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM_LIST);
		YFCElement yfcEleGetItemListTemp = yfcDocGetItemListTemp.getDocumentElement();
		yfcEleGetItemListTemp.setAttribute("TotalItemList", "");		

		YFCElement yfcEleItemTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_ITEM);
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		 	
		YFCElement yfcEleItemPrimaryInformationTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_PRIMARY_INFORMATION);
		yfcEleItemPrimaryInformationTemp.setAttribute(KohlsXMLLiterals.A_ITEM_TYPE, "");
		yfcEleItemPrimaryInformationTemp.setAttribute("PrimarySupplier", "");
		yfcEleItemTemp.appendChild(yfcEleItemPrimaryInformationTemp);
		
		yfcEleGetItemListTemp.appendChild(yfcEleItemTemp);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemList Template : " + XMLUtil.getXMLString(yfcDocGetItemListTemp.getDocument()));
		}

		return yfcDocGetItemListTemp.getDocument();
	}
	
	private Document getInvSupplyTemplate() {

		YFCDocument yfcDocGetItemTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcEleGetItemTemp = yfcDocGetItemTemp.getDocumentElement();
		yfcEleGetItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");		

		YFCElement yfcEleSuupliesTemp = yfcDocGetItemTemp.createElement("Supplies");
		yfcEleGetItemTemp.appendChild(yfcEleSuupliesTemp);
		YFCElement yfcEleInvSupplyTemp = yfcDocGetItemTemp.createElement("InventorySupply");
		yfcEleInvSupplyTemp.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");		
		yfcEleSuupliesTemp.appendChild(yfcEleInvSupplyTemp);
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getInvSupplyTemplate Template : " + XMLUtil.getXMLString(yfcDocGetItemTemp.getDocument()));
		}
		return yfcDocGetItemTemp.getDocument();
	}
	//Added -- OASIS_SUPPORT 08/2/2012
	/**
	 * This method returns the Supply Quantity
	 * @param env
	 * @param strItemID
	 * @return
	 * @throws Exception
	 */
	private String getItemQuantity(YFSEnvironment env, String strItemID, String strShipNode) throws Exception {
		
		String strQuantity = "0";
		env.setApiTemplate("getInventorySupply",this.getInvSupplyTemplate());
		Document docGetInvSupply = this.api.getInventorySupply(env, this.getInvSupplyInputXML(env, strItemID, strShipNode)); 
		env.clearApiTemplate("getInventorySupply");	
		Element eleItemOutput = docGetInvSupply.getDocumentElement();
		Element eleInvSupply = (Element) eleItemOutput.getElementsByTagName("InventorySupply").item(0);
		if (eleInvSupply != null) {
			strQuantity = eleInvSupply.getAttribute(KohlsXMLLiterals.A_QUANTITY);
		} 
		return strQuantity;
	}// end of getItemQuantity

	//Added -- OASIS_SUPPORT 08/2/2012
	
	/**
	 * This method determines if the common code value for ENBL_0_DSV is set to Y
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private boolean isZeroDSVEnabled(YFSEnvironment env) throws Exception {
		
		boolean enableZeroDSV = false;
		String strEnableZeroDSV = KohlsConstant.YES;
		
		try {
			
			strEnableZeroDSV = KohlsUtil.getCommonCodeValue(env, "ENBL_0_DSV");
		
		} catch (NullPointerException npExcp) {
			
			if(YFCLogUtil.isDebugEnabled()) {
				
				log.debug("Common code value is not set for code type ENBL_0_DSV. " + 
						"Using default value of Y");
			}
		}
		
		if (KohlsConstant.YES.equals(strEnableZeroDSV)) {
			
			enableZeroDSV = true;
		}
		
		return enableZeroDSV;		
		
	}// end if isZeroDSVEnabled

	//Added -- OASIS_SUPPORT 08/2/2012
	/**
	 * This method determines if the item type is DS
	 * @param env
	 * @param strItemID
	 * @return
	 * @throws Exception
	 */
	private Document getItemDetails(YFSEnvironment env, Element nItem) throws Exception {

		env.setApiTemplate(KohlsConstant.API_GET_ITEM_DETAILS, this.getItemDetailsTemplate());
		Document docGetItemDetails = this.api.getItemDetails(env, this.getItemDetailsInputXML(nItem));
		env.clearApiTemplate(KohlsConstant.API_GET_ITEM_DETAILS);	
		return docGetItemDetails;
	}// end of getItemDetails
	
	//Added -- OASIS_SUPPORT 08/2/2012
	/**
	 * This method builds the template for getItemDetails
	 * @return
	 */
	private Document getItemDetailsTemplate() {

		YFCDocument yfcDocGetItemDetailsTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcEleItemTemp = yfcDocGetItemDetailsTemp.getDocumentElement();
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");	
		yfcEleItemTemp.setAttribute("Available", "");
		YFCElement yfcEleItemPrimaryInformationTemp = yfcDocGetItemDetailsTemp.createElement(KohlsXMLLiterals.E_PRIMARY_INFORMATION);
		yfcEleItemPrimaryInformationTemp.setAttribute(KohlsXMLLiterals.A_ITEM_TYPE, "");
		yfcEleItemTemp.appendChild(yfcEleItemPrimaryInformationTemp);				

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemDetails Template : " + XMLUtil.getXMLString(yfcDocGetItemDetailsTemp.getDocument()));
		}

		return yfcDocGetItemDetailsTemp.getDocument();
	}// end of getItemDetailsTemplate
	
	//Added -- OASIS_SUPPORT 08/2/2012
	/**
	 * This method builds the input xml for getItemDetails
	 * @param strItemID
	 * @return
	 */
	private Document getItemDetailsInputXML(Element nItem) {
		String strItemID = nItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
		YFCDocument yfcDocGetItemDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcEleGetItemDetails = yfcDocGetItemDetails.getDocumentElement();
		yfcEleGetItemDetails.setAttribute(KohlsXMLLiterals.A_ITEM_ID, nItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
		yfcEleGetItemDetails.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, nItem.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS));
		yfcEleGetItemDetails.setAttribute(KohlsXMLLiterals.A_UOM, nItem.getAttribute(KohlsXMLLiterals.A_UOM));
		yfcEleGetItemDetails.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, nItem.getAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE));

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemDetails Input XML : " + XMLUtil.getXMLString(yfcDocGetItemDetails.getDocument()));
		}
		return yfcDocGetItemDetails.getDocument();
	}//  end of getItemDetailsInputXML
	
	//Added -- OASIS_SUPPORT 10/2/2012
	/**
	 * This method builds the input xml to set inventory to zero
	 * @param env
	 * @param inXML
	 * @return
	 */
	private void createAdjInvInputXML(YFSEnvironment env, Element nItem , Document itemsDoc)throws Exception {
		
		String strItemID = nItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
		String strShipNode = nItem.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
		Document supplyXmlDoc=this.getInvSupplyInputXML(env, strItemID, strShipNode);
		
		Element eleItemSupply = supplyXmlDoc.getDocumentElement();
		String strSupplyType=eleItemSupply.getAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE);	
		nItem.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE, strSupplyType);
		String strItemSupplyQuantity=getItemQuantity(env, strItemID, strShipNode);
		if(strItemSupplyQuantity != "0")
		{
			nItem.setAttribute(KohlsXMLLiterals.A_QUANTITY, ""+new Double(strItemSupplyQuantity).doubleValue()*(-1));
			Node nElement = itemsDoc.importNode(nItem,true);
			itemsDoc.getDocumentElement().appendChild(nElement);

		}
	}//  end of createAdjInvInputXML

	
}
