package com.kohls.bopus.api;

/*****************************************************************************
 * File Name    : KOHLSGivInventorySync.java
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
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This extended api is called to send inventory update 
 * to GIV from OMS to provide updates to supply types HOLD_AREA, ONHAND and SALES_FLOOR,
 * when a BOPUS order is picked and placed in hold area
 * 
 */

public class KOHLSGivInventorySync{
	
	
	/**
	 * This method will form a message to update the 
	 * Supply Types HOLD_AREA, ONHAND and SALES_FLOOR
	 * 
	 */

	public Document syncInventoryType(YFSEnvironment env, Document doc){

		try{

			String[] SuppyType= new String[3];
			SuppyType[0]= KohlsConstant.ONHAND;
			SuppyType[1]= KohlsConstant.SALES_FLOOR;
			SuppyType[2]= KohlsConstant.HOLD_AREA;
			
			Element eleSupply = doc.getDocumentElement();

			String strAdjustmentType = eleSupply.getAttribute(KohlsXMLLiterals.A_ADJUSTMENT_TYPE);
			String strItemID = eleSupply.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			String strProductClass = eleSupply.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS);
			String strUnitOfMeasure = eleSupply.getAttribute(KohlsXMLLiterals.A_UOM);
			String strShipNode = eleSupply.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
			String strQuantity = eleSupply.getAttribute(KohlsXMLLiterals.A_QUANTITY);

			YFCDocument yfcDocInvAdjInput = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEMS);
			YFCElement yfcEleItemTemp = yfcDocInvAdjInput.getDocumentElement();
			for (int i=0;i<3;i++){
				YFCElement yfcEleItem = yfcDocInvAdjInput.createElement(KohlsXMLLiterals.E_ITEM);
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, strItemID);
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, strProductClass);
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM, strUnitOfMeasure );
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_SHIPNODE, strShipNode );

				yfcEleItem.setAttribute(KohlsXMLLiterals.A_ADJUSTMENT_TYPE, strAdjustmentType );
				yfcEleItem.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE,  KohlsConstant.KOHLS_OrganizationCode );


				if (SuppyType[i].equalsIgnoreCase(KohlsConstant.ONHAND)||SuppyType[i].equalsIgnoreCase(KohlsConstant.SALES_FLOOR)){

					if(strQuantity != null && ! "".equals(strQuantity.trim())){
						double dQuantity = new Double(strQuantity).doubleValue()*(-1);
						yfcEleItem.setAttribute(KohlsXMLLiterals.A_QUANTITY, ""+dQuantity );
					}
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE,	SuppyType[i]);
				}else{
					if(strQuantity != null && ! "".equals(strQuantity.trim())){
						double dQuantity = new Double(strQuantity).doubleValue()*(1);
						yfcEleItem.setAttribute(KohlsXMLLiterals.A_QUANTITY, ""+dQuantity );
					}
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE,	SuppyType[i]);

				}
				yfcEleItemTemp.appendChild(yfcEleItem);

			}
			Document syncDoc = yfcDocInvAdjInput.getDocument();
			return syncDoc;


		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException("Exception in method syncInventoryType : "+e.getStackTrace());
		}

	}
}
