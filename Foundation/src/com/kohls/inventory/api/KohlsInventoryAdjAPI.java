package com.kohls.inventory.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

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
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class is called on receiving the Inventory Adjustment messages for items
 * 
 * @author Rohan Bhandary
 * 
 *         Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 * */

public class KohlsInventoryAdjAPI implements YIFCustomApi {

	private YIFApi api;
	private KohlsInventoryAdjWrapperAPI inventoryAdjWrapperApi;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsInventoryAdjAPI.class.getName());
	private boolean isItemExist = true;

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsInventoryAdjAPI() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
		inventoryAdjWrapperApi = new KohlsInventoryAdjWrapperAPI();
	}

	/**
	 * Method to check the received inventory adjustment message with the sync
	 * message from custom table
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void checkUpdateInvAdj(YFSEnvironment env, Document inXML)
			throws Exception {
		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Begining of KohlsInventoryAdjAPI checkUpdateInvAdj method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		String strShipNode = null;
		String strAdjTimeStamp = null;
		String strSyncTimeStamp = null;
		String strAdjTranNum = null;
		String strSyncTranNum = null;
		Date syncDate;
		Date adjDate;
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				KohlsConstant.INV_DATE_FORMAT);
		Element eleInputList = inXML.getDocumentElement();
		NodeList nodeGetList = eleInputList
				.getElementsByTagName(KohlsXMLLiterals.E_ITEM);
		Element eleData = (Element) nodeGetList.item(0);
		
		// Start - Added for SF Case # 00389544 -- OASIS_SUPPORT 13/1/2012
		
		String strItemID = eleData.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
		String strQuantity = eleData.getAttribute(KohlsXMLLiterals.A_QUANTITY);	
		strShipNode = eleData.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
		String strRef4 = eleData.getAttribute(KohlsXMLLiterals.A_REFERENCE_4);
		
		if (this.isDiscardDSVAtEFCEnabled(env) && this.isItemTypeDS(env, strItemID)) {
			
			if (YFCLogUtil.isDebugEnabled()) {

				log.debug("Adjustment for Item " + strItemID + " for quantity " + strQuantity + " at ship node " +
					strShipNode + " has been discarded because the item is configured as a Direct Ship Item");
				
			}
		}
		// End - Added for SF Case # 00389544 -- OASIS_SUPPORT 13/1/2012
		// Start - Added for PMR 64507,379,000 (PIX 500)
		else if("500".equals(strRef4) && this.isIgnorePIX500(env)){
			if (YFCLogUtil.isDebugEnabled()) {

				log.debug("Adjustment for Item " + strItemID + " for quantity " + strQuantity + " at ship node " +
					strShipNode + " has been discarded because the common code IGNORE_PIX_500 is configured as Y");
				
			}
			// End - Added for PMR 64507,379,000 (PIX 500)
		} else if (isItemExist == true) {	
			strAdjTimeStamp = eleData.getAttribute(KohlsXMLLiterals.A_REFERENCE_3);
			adjDate = dateFormat.parse(strAdjTimeStamp);
			strAdjTranNum = eleData.getAttribute(KohlsXMLLiterals.A_REFERENCE_1);
			long intTranNumAdj = Long.parseLong(strAdjTranNum);
			eleData.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE,
					KohlsConstant.SUPPLY_TYPE);
			YFCDocument yfcDocKohlsInvSyncTime = YFCDocument
					.createDocument(KohlsXMLLiterals.E_KOHLS_INV_SYNC_TIME_STAMP);
			YFCElement yfcEleKohlsInvSyncTime = yfcDocKohlsInvSyncTime
					.getDocumentElement();
			yfcEleKohlsInvSyncTime.setAttribute(KohlsXMLLiterals.A_SHIPNODE,
					strShipNode);
	
			if (YFCLogUtil.isDebugEnabled()) {
				this.log.debug("Service InvAdjGetTimeSubService Input XML : "
						+ XMLUtil.getXMLString(yfcDocKohlsInvSyncTime.getDocument()));
			}
			Document docKohlsInvSyncTimeRec = this.api.executeFlow(env,
					KohlsConstant.SERVICE_INV_ADJ_GET_TIME,
					yfcDocKohlsInvSyncTime.getDocument());
	
			if (YFCLogUtil.isDebugEnabled()) {
				this.log.debug("Output from Service InvAdjGetTimeSubService Input XML : "
						+ XMLUtil.getXMLString(docKohlsInvSyncTimeRec));
			}
	
			Element eleKohlsInvSyncTimeRec = docKohlsInvSyncTimeRec
					.getDocumentElement();
			NodeList nodeListGetInvSyncTime = eleKohlsInvSyncTimeRec
					.getElementsByTagName(KohlsXMLLiterals.E_KOHLS_INV_SYNC_TIME_STAMP);
			// check if record for node exists
			if (0 == nodeListGetInvSyncTime.getLength()) {
				this.inventoryAdjWrapperApi.adjustInventory(env, inXML);
			} else {
				Element eleDataInvSyncTime = (Element) nodeListGetInvSyncTime
						.item(0);
				strSyncTimeStamp = eleDataInvSyncTime
						.getAttribute(KohlsXMLLiterals.A_REASON_TEXT);
				syncDate = dateFormat.parse(strSyncTimeStamp);
				strSyncTranNum = eleDataInvSyncTime
						.getAttribute(KohlsXMLLiterals.A_TRANSACTION_NUMBER);
				long intTranNumSync = Long.parseLong(strSyncTranNum);
	
				// check if adjustment time stamp is greater than sync time stamp
				if (adjDate.after(syncDate)) {
					this.inventoryAdjWrapperApi.adjustInventory(env, inXML);
				}
				// check if adjustment time stamp is equal to sync time stamp
				else if (adjDate.equals(syncDate)) {
					// if adjustment message transaction number is greater than sync
					// message transaction number consume the message
					if (intTranNumAdj > intTranNumSync) {
						this.inventoryAdjWrapperApi.adjustInventory(env, inXML);
					}
				}
			}
		}// end of else - isItemTypeDS
	}

	/**
	 * @param arg0
	 *            Properties
	 * @throws Exception
	 *             e
	 */
	public void setProperties(Properties arg0) throws Exception {

	}
	
	//Added for SF Case # 00389544 -- OASIS_SUPPORT 13/1/2012
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
	
	//Added for SF Case # 00389544 -- OASIS_SUPPORT 13/1/2012
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
	
	//Added for SF Case # 00389544 -- OASIS_SUPPORT 13/1/2012
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
	
	//Added for SF Case # 00389544 -- OASIS_SUPPORT 13/1/2012
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
	 * This method determines if the common code value for IGNORE_PIX_500 is set to Y
	 * This method is added for PMR 64507,379,000 (PIX 500).
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private boolean isIgnorePIX500(YFSEnvironment env) throws Exception {
		
		boolean ignorePIX500 = false;
		String strIgnorePIX500 = KohlsConstant.YES;
		
		try {
			
			strIgnorePIX500 = KohlsUtil.getCommonCodeValue(env, "IGNORE_PIX_500");
		
		} catch (NullPointerException npExcp) {
			
			if(YFCLogUtil.isDebugEnabled()) {
				
				log.debug("Common code value is not set for code type IGNORE_PIX_500. " + 
						"Using default value of Y");
			}
		}
		
		if (KohlsConstant.YES.equals(strIgnorePIX500)) {
			
			ignorePIX500 = true;
		}
		
		return ignorePIX500;		
		
	}// end if isIgnorePIX500

	
}
