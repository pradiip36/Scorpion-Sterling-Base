package com.kohls.inventory.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
/**
 * This class is called on receiving the Inventory Sync messages for items
 * 
 * 
 * 
 *         Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 * */

public class KohlsLoadInventoryMismatchAPI {

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsLoadInventoryMismatchAPI.class.getName());
	private boolean isItemExist = true;
	
	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsLoadInventoryMismatchAPI() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}	
	/**
	 * 
	 * @param env
	 * @param inXML
	 */
	public void filterInventorySync(YFSEnvironment env, Document inXML)throws Exception{
		String strShipNode = null;
		Element eleInputList = inXML.getDocumentElement();
		NodeList nodeGetList = eleInputList
				.getElementsByTagName(KohlsXMLLiterals.E_ITEM);
		Element eleData = (Element) nodeGetList.item(0);
		String strItemID = eleData.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
		String strQuantity = eleData.getAttribute(KohlsXMLLiterals.A_QUANTITY);	
		strShipNode = eleData.getAttribute(KohlsXMLLiterals.A_SHIPNODE);

		if (this.isDiscardDSVAtEFCEnabled(env) && this.isItemTypeDS(env, strItemID)) {
			
			if (YFCLogUtil.isDebugEnabled()) {

				log.debug("Sync for Item " + strItemID + " for quantity " + strQuantity + " at ship node " +
					strShipNode + " has been discarded because the item is configured as a Direct Ship Item");
						
			}
			
		} else {
			if(isItemExist == true){
				this.api.loadInventoryMismatch(env, inXML);
			}
		}
		
	}

	/**
	 * This method determines if the common code value for DISCARD_DSV_EFC is set to Y
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private boolean isDiscardDSVAtEFCEnabled(YFSEnvironment env) throws Exception {
		
		boolean discardDSVAtEFC = false;
		String strDiscardDSVAtEFC = KohlsConstant.YES;
		
		try {
			
			strDiscardDSVAtEFC = KohlsUtil.getCommonCodeValue(env, "DISCARD_DSV_EFC");
		
		} catch (NullPointerException npExcp) {
			
			if(YFCLogUtil.isDebugEnabled()) {
				
				log.debug("Common code value is not set for code type DISCARD_DSV_EFC. " + 
						"Using default value of Y");
			}
		}
		
		if (KohlsConstant.YES.equals(strDiscardDSVAtEFC)) {
			
			discardDSVAtEFC = true;
		}
		
		return discardDSVAtEFC;		
		
	}// end if isDiscardDSVAtEFCEnabled

	
	
	/**
	 * This method determines if the item type is DS
	 * @param env
	 * @param strItemID
	 * @return
	 * @throws Exception
	 */
	private boolean isItemTypeDS(YFSEnvironment env, String strItemID) throws Exception {
		
		boolean isItemTypeDS = false;
		
		env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, this.getItemListTemplate());
		Document docGetItemList = this.api.getItemList(env, this.getItemListInputXML(strItemID));
		env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);	
		
		Element eleItemList = docGetItemList.getDocumentElement();
		NodeList eleItems = eleItemList.getElementsByTagName(KohlsXMLLiterals.E_ITEM);
		
		if (null != eleItems && eleItems.getLength() > 0) {
			
			Element eleItem = (Element) eleItems.item(0);
			Element eleItemPrimInfo = (Element) eleItem.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
			String strItemType = eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_TYPE);
			
			if ("DS".equals(strItemType)) {
				
				isItemTypeDS = true;
			}
		} else {
			
			isItemExist = false;
			if(YFCLogUtil.isDebugEnabled()) {
				log.debug("Item Id " +  strItemID + " does not exist.");
			}
		}
		
		return isItemTypeDS;
	}// end of isItemTypeDS
	/**
	 * This method builds the template for getItemList
	 * @return
	 */
	private Document getItemListTemplate() {

		YFCDocument yfcDocGetItemListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM_LIST);
		YFCElement yfcEleListTemp = yfcDocGetItemListTemp.getDocumentElement();
		
		YFCElement yfcEleItemTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_ITEM);
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");		

		YFCElement yfcEleItemPrimaryInformationTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_PRIMARY_INFORMATION);
		yfcEleItemPrimaryInformationTemp.setAttribute(KohlsXMLLiterals.A_ITEM_TYPE, "");
		yfcEleItemTemp.appendChild(yfcEleItemPrimaryInformationTemp);	
		
		yfcEleListTemp.appendChild(yfcEleItemTemp);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemList Template : " + XMLUtil.getXMLString(yfcDocGetItemListTemp.getDocument()));
		}

		return yfcDocGetItemListTemp.getDocument();
	}// end of getItemListTemplate
	/**
	 * This method builds the input xml for getItemList
	 * @param strItemID
	 * @return
	 */
	private Document getItemListInputXML(String strItemID) {

		YFCDocument yfcDocGetItemList = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcEleGetItemList = yfcDocGetItemList.getDocumentElement();
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_ITEM_ID, strItemID);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, KohlsConstant.PRODUCT_CLASS_GOOD);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_UOM, KohlsConstant.UNIT_OF_MEASURE);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, KohlsConstant.ITEM_ORGANIZATION_CODE);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getItemList Input XML : " + XMLUtil.getXMLString(yfcDocGetItemList.getDocument()));
		}
		return yfcDocGetItemList.getDocument();
	}//  end of getItemListInputXML

}
