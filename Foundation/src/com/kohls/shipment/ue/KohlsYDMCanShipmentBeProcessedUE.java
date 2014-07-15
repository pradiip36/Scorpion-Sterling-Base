package com.kohls.shipment.ue;

/*****************************************************************************
 * File Name    : KohlsYDMCanShipmentBeProcessedUE.java
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
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.yantra.ydm.japi.ue.YDMCanShipmentBeProcessedUE;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;


/**
 * This user exit sets the value of DeliveryMethos PICK before sending the 
 * shipment confirmation - demand update to GIV
 * 
 */
public class KohlsYDMCanShipmentBeProcessedUE implements YDMCanShipmentBeProcessedUE{

	@Override
	public void canShipmentBeProcessed(YFSEnvironment env, Document doc)
			throws YFSUserExitException {

		try {

			Document docShipmentListInput = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT); 

			docShipmentListInput.getDocumentElement().setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY,doc.getDocumentElement().getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY));


			env.setApiTemplate(KohlsConstant.GET_SHIPMENT_LIST_API,KohlsConstant.API_GET_SHIPMENT_LIST_BOPUS_CONFIRM_SHIPMENT_TEMPLATE);

			Document ShipmentListtOutput = KOHLSBaseApi.invokeAPI(env, KohlsConstant.GET_SHIPMENT_LIST_API, docShipmentListInput);


			NodeList nlShipment = ShipmentListtOutput.getDocumentElement().getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

			for(int i = 0; i < nlShipment.getLength(); i++){
				Element eleShipment = (Element)nlShipment.item(i);

				String strDeliveryMethod = eleShipment.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);

				if(strDeliveryMethod.equalsIgnoreCase(KohlsConstant.PICK) && strDeliveryMethod!=null){
					env.setTxnObject(KohlsXMLLiterals.A_DELIVERY_METHOD, KohlsConstant.PICK);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException("Exception in method updateDemand : "+e.getStackTrace());
		}



	}


}
