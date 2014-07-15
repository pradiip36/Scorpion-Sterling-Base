package com.kohls.bopus.api;

/*****************************************************************************
 * File Name    : KOHLSChangeSupplyTypes.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Jan 27,2014    Ashalatha        Initial Version 
 * 0.00b Mar 13,2014    Ashalatha 		 Added the changes related to GIV integration 
 * 										using Interop HTTP Servlet
 * ---------------------------------------------------------------------------
 *****************************************************************************/

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import com.kohls.bopus.util.KohlsHTTPPostUtil;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.kohls.inventory.api.KohlsInventoryAdjWrapperAPI;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


/**
 * This extended api is called to update the Supply Types ONHAND and HOLD_AREA
 * in OMS when the shipment is placed in Hold Area 
 * by calling the adjustInventory API
 * 
 */

public class KOHLSChangeSupplyTypes implements YIFCustomApi {
	
	private Properties propertiesMap;

	private YIFApi api;
	private KohlsInventoryAdjWrapperAPI inventoryAdjWrapperApi;

	private static final YFCLogCategory log = YFCLogCategory.instance(KOHLSChangeSupplyTypes.class.getName());

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KOHLSChangeSupplyTypes() throws YIFClientCreationException {
		this.api = YIFClientFactory.getInstance().getLocalApi();
		inventoryAdjWrapperApi = new KohlsInventoryAdjWrapperAPI();
	}


	public Document changeSupplyType(YFSEnvironment env, Document doc){

		try {
			String[] SuppyType= new String[2];
			SuppyType[0]=  KohlsConstant.ONHAND;
			SuppyType[1]= KohlsConstant.HOLD_AREA;

			Document syncInventory=null;


			Element eleShipment = doc.getDocumentElement();


			String strShipNode = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPNODE);

			Element eleShipLines = (Element) eleShipment.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINES).item(0);


			NodeList nlShipmentLines = eleShipLines.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);


			for(int i = 0; i < nlShipmentLines.getLength(); i++){
				Element eleShipmentLine = (Element)nlShipmentLines.item(i);
				String strItemID = eleShipmentLine.getAttribute(KohlsXMLLiterals.ITEM_ID);
				String strProductClass = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS);
				String strQuantity = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_QUANTITY);
				String strUnitOfMeasure = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_UOM);


				// Creating the Item details for the input to adjustInventory API
				YFCDocument yfcDocInvAdjInput = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEMS);
				YFCElement yfcEleItemTemp = yfcDocInvAdjInput.getDocumentElement();
				for(int j=0;j<2;j++){
					YFCElement yfcEleItem = yfcDocInvAdjInput.createElement(KohlsXMLLiterals.E_ITEM);
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, strItemID);
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, strProductClass);
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM, strUnitOfMeasure );
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_SHIPNODE, strShipNode );

					yfcEleItem.setAttribute(KohlsXMLLiterals.A_ADJUSTMENT_TYPE, KohlsConstant.ADJUSTMENT );
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE,KohlsConstant.ITEM_ORGANIZATION_CODE );



					if (SuppyType[j].equalsIgnoreCase(KohlsConstant.ONHAND)){
						if(strQuantity != null && ! "".equals(strQuantity.trim())){
							double dQuantity = new Double(strQuantity).doubleValue()*(-1);
							yfcEleItem.setAttribute(KohlsXMLLiterals.A_QUANTITY, ""+dQuantity );
						}
						yfcEleItem.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE, SuppyType[j]);

					}else{
						if(strQuantity != null && ! "".equals(strQuantity.trim())){
							double dQuantity = new Double(strQuantity).doubleValue()*(1);
							yfcEleItem.setAttribute(KohlsXMLLiterals.A_QUANTITY, ""+dQuantity );
						}
						yfcEleItem.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE, SuppyType[j]);

					}
					yfcEleItemTemp.appendChild(yfcEleItem);
				}
				Document adjustInventory = this.inventoryAdjWrapperApi.adjustInventory(env, yfcDocInvAdjInput.getDocument());

				syncInventory=syncInventoryType(env,eleShipment);


			}
			
			//Start : Changes done for integration with GIV using Interop HTTP servelt
			//String strSyncInventory=XMLUtil.getXMLString(syncInventory);
			//propertiesMap.put(KohlsXMLLiterals.INTEROP_API_DATA, strSyncInventory);
			
			//KohlsHTTPPostUtil.invokeAPI(propertiesMap);
			//End
			log.debug("Input to KohlsGIVSupplyUpdate: "+XMLUtil.getXMLString(syncInventory));
			KohlsCommonUtil.invokeService(env,"KohlsGIVSupplyUpdate", syncInventory);

			
			return syncInventory;


		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException("Exception in method changeSupplyType : "+e.getStackTrace());
		}

	}
	
	/**
	 * This method is called to send inventory update 
	 * to GIV from OMS to provide updates to supply types HOLD_AREA, ONHAND and SALES_FLOOR,
	 * when a BOPUS order is picked and placed in hold area
	 * 
	 */
	private Document syncInventoryType(YFSEnvironment env, Element eleShipment){

		try{

			String[] SuppyType= new String[3];
			SuppyType[0]= KohlsConstant.ONHAND;
			SuppyType[1]= KohlsConstant.SALES_FLOOR;
			SuppyType[2]= KohlsConstant.HOLD_AREA;

			Document syncDoc=null;

			String strShipNode = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPNODE);

			Element eleShipLines = (Element) eleShipment.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINES).item(0);


			NodeList nlShipmentLines = eleShipLines.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);


			for(int i = 0; i < nlShipmentLines.getLength(); i++){
				Element eleShipmentLine = (Element)nlShipmentLines.item(i);
				String strItemID = eleShipmentLine.getAttribute(KohlsXMLLiterals.ITEM_ID);
				String strProductClass = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS);
				String strQuantity = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_QUANTITY);
				String strUnitOfMeasure = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_UOM);

				YFCDocument yfcDocInvAdjInput = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEMS);
				YFCElement yfcEleItemTemp = yfcDocInvAdjInput.getDocumentElement();
				for (int j=0;j<3;j++){
					YFCElement yfcEleItem = yfcDocInvAdjInput.createElement(KohlsXMLLiterals.E_ITEM);
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, strItemID);
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, strProductClass);
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM, strUnitOfMeasure );
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_SHIPNODE, strShipNode );

					yfcEleItem.setAttribute(KohlsXMLLiterals.A_ADJUSTMENT_TYPE,KohlsConstant.ADJUSTMENT);
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE,  KohlsConstant.ITEM_ORGANIZATION_CODE );
					
					// Start : Add the store pick time stamp in the GIV suppy update message
					Calendar today = Calendar.getInstance();  
					SimpleDateFormat sdf = new SimpleDateFormat(KohlsConstant.TIMESTAMP_DATEFORMAT);
					String storePickTimestamp = sdf.format(today.getTime());
					
					yfcEleItem.setAttribute(KohlsXMLLiterals.A_REFERENCE_3, storePickTimestamp );
					
					//End

					if (SuppyType[j].equalsIgnoreCase(KohlsConstant.ONHAND)||SuppyType[j].equalsIgnoreCase(KohlsConstant.SALES_FLOOR)){

						if(strQuantity != null && ! "".equals(strQuantity.trim())){
							double dQuantity = new Double(strQuantity).doubleValue()*(-1);
							yfcEleItem.setAttribute(KohlsXMLLiterals.A_QUANTITY, ""+dQuantity );
						}
						yfcEleItem.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE,	SuppyType[j]);
					}else {
						if(strQuantity != null && ! "".equals(strQuantity.trim())){
							double dQuantity = new Double(strQuantity).doubleValue()*(1);
							yfcEleItem.setAttribute(KohlsXMLLiterals.A_QUANTITY, ""+dQuantity );
						}
						yfcEleItem.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE,	SuppyType[j]);

					}
					yfcEleItemTemp.appendChild(yfcEleItem);

				}

				syncDoc = yfcDocInvAdjInput.getDocument();
			}

			return syncDoc;


		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException("Exception in method syncInventoryType : "+e.getStackTrace());
		}

	}


	public void setProperties(Properties  gpropertiesMap) throws Exception {
		this.propertiesMap =  gpropertiesMap;
		
	}
	

}
