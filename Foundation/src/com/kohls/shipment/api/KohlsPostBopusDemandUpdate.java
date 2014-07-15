package com.kohls.shipment.api;

/*****************************************************************************
 * File Name    : KohlsPostBopusDemandUpdate.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Jan 27,2014    Ashalatha        Initial Version 
 * ---------------------------------------------------------------------------
 *****************************************************************************/


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.inventory.api.KohlsInventoryAdjWrapperAPI;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


/**
 * This extended api is called to send demand update to GIV and 
 * change the supply type in OMS on Shipment Confirmation of BOPUS lines
 * 
 */

public class KohlsPostBopusDemandUpdate {

	private YIFApi api;
	private KohlsInventoryAdjWrapperAPI inventoryAdjWrapperApi;
	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsPostBopusDemandUpdate() throws YIFClientCreationException {
		this.api = YIFClientFactory.getInstance().getLocalApi();
		inventoryAdjWrapperApi = new KohlsInventoryAdjWrapperAPI();
	}


	/**
	 * This method is called to send the demand update message to GIV
	 * with StorePick="Y" and DemandType="HOLD_AREA.ex"
	 *  
	 */

	public Document updateDemand(YFSEnvironment env, Document doc){
		try{
			String sDeliveryMethod = (String)env.getTxnObject(KohlsXMLLiterals.A_DELIVERY_METHOD);

			if ("PICK".equals(sDeliveryMethod)){
				updateSupplyType(env, doc);
				Element eleDemand = doc.getDocumentElement();

				eleDemand.setAttribute(KohlsXMLLiterals.STORE_PICK, KohlsConstant.YES);

				eleDemand.setAttribute(KohlsXMLLiterals.A_DEMAND_TYPE, KohlsConstant.HOLD_AREA);
			}

			return doc;
		}catch (Exception e) {
			e.printStackTrace();
			throw new YFSException("Exception in method updateDemand : "+e.getStackTrace());
		}
	}


	/**
	 * This method is called to decrease Supply Type HOLD_AREA in OMS 
	 * when the BOPUS Order has been picked up by the customer
	 * 
	 */

	private Document updateSupplyType(YFSEnvironment env, Document doc){
		try{

			Element eleShipment = doc.getDocumentElement();
			String strItemID = eleShipment.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			String strProductClass = eleShipment.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS);
			String strUnitOfMeasure = eleShipment.getAttribute(KohlsXMLLiterals.A_UOM);

			String strShipNode = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPNODE);

			String strQuantity = eleShipment.getAttribute(KohlsXMLLiterals.A_QUANTITY);

			// Creating the Item details for the input to adjustInventory API
			YFCDocument yfcDocInvAdjInput = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEMS);
			YFCElement yfcEleItemTemp = yfcDocInvAdjInput.getDocumentElement();

			YFCElement yfcEleItem = yfcDocInvAdjInput.createElement(KohlsXMLLiterals.E_ITEM);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, strItemID);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, strProductClass);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM, strUnitOfMeasure );
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_SHIPNODE, strShipNode );
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ADJUSTMENT_TYPE, KohlsConstant.ADJUSTMENT);
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, KohlsConstant.KOHLS_OrganizationCode );

			if(strQuantity != null && ! "".equals(strQuantity.trim())){
				double dQuantity = new Double(strQuantity).doubleValue()*(1);
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_QUANTITY, ""+dQuantity );
			}
			yfcEleItem.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE, KohlsConstant.HOLD_AREA);


			yfcEleItemTemp.appendChild(yfcEleItem);


			this.inventoryAdjWrapperApi.adjustInventory(env, yfcDocInvAdjInput.getDocument());
			return doc;

		}catch (Exception e) {
			e.printStackTrace();
			throw new YFSException("Exception in method updateSupplyType : "+e.getStackTrace());
		}
	}

}
