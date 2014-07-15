
/*
 * Created on Jun 07,2013
 *
 */
package com.kohls.ibm.ocf.pca.tasks.changeShippingAddress.wizardpages;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSChangeShipmentAddressEditor;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAApiNames;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author Bhaskar
 * @build # 200810210800
 * Copyright © 2005, 2006 Sterling Commerce, Inc. All Rights Reserved.
 */
public class KOHLSChangeShippingAddressBehavior extends YRCBehavior   {

	private Object input ;
	private KOHLSChangeShippingAddressHelper helper;
	private String formId;
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
		super.init();
		
	}
	public KOHLSChangeShippingAddressBehavior(KOHLSChangeShippingAddress parent, String formId, Object input, KOHLSChangeShippingAddressHelper helper) {
		super(parent, formId, input);
		this.input = input;
		this.helper = helper;
		this.formId = formId;
		getShippingAddressDetails();
		setModel("Model",(Element)input);
	}

	
	public void handleApiCompletion(YRCApiContext ctx){
		
		
        	 if(ctx.getApiName().equals(KOHLSPCAApiNames.API_KOHLS_CHANGE_SHIPMENT) && ctx.getInvokeAPIStatus()>0){
        	 	Document outXml = ctx.getOutputXml();
        	 	YRCPlatformUI.showInformation("Success", "Shipment Address Updated Successfully....");
        	 	YRCPlatformUI.closeEditor(KOHLSChangeShipmentAddressEditor.ID_Editor, true);
        	 }
        	 
        	 if(ctx.getApiName().equals(KOHLSPCAApiNames.API_KOHLS_GET_SHIPMENT_DETAILS_ADDRESS_CHANGE) && ctx.getInvokeAPIStatus()>0){
         	 	Document outXml = ctx.getOutputXml();
         	 	setModel("CurrentShipToAddress",outXml.getDocumentElement(),true);
         	 	repopulateModel("CurrentShipToAddress");
         	 }
        
		}
	/**
	 * This Method is called by base behavior while calling API 
	 */
	public Document getDocument(String apiName){
	
		return null;
	}

	public void saveShippingAddress() {		
		
		YRCApiContext context = new YRCApiContext();
		context.setApiName(KOHLSPCAApiNames.API_KOHLS_CHANGE_SHIPMENT);
		context.setFormId(getFormId());
		context.setInputXml(changeShipmentInput());
		callApi(context);	
	}

	private Document changeShipmentInput() {
		Element ele = (Element) input;
		Element eleToAddress = YRCXmlUtils.getChildElement(ele, KOHLSPCAConstants.E_TO_ADDRESS) ;
		
		Element eleSaveSIMInput = getTargetModel("ChangeShippingAddressInput");
		Document docChangeShipment = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_SHIPMENT);
		Element eleChangeShipment = docChangeShipment.getDocumentElement();
		
		eleChangeShipment.setAttribute(KOHLSPCAConstants.A_ACTION, KOHLSPCAConstants.V_ACTION_MODIFY);
		eleChangeShipment.setAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY,ele.getAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY));
		eleChangeShipment.setAttribute(KOHLSPCAConstants.A_SHIPMENT_NO,eleSaveSIMInput.getAttribute(KOHLSPCAConstants.A_SHIPMENT_NO));
		Element eleShipTo = YRCXmlUtils.createChild(eleChangeShipment, KOHLSPCAConstants.E_TO_ADDRESS);
		Element eleTo = YRCXmlUtils.getChildElement(eleSaveSIMInput, KOHLSPCAConstants.E_TO_ADDRESS);
		
		eleShipTo.setAttribute(KOHLSPCAConstants.A_ACTION, KOHLSPCAConstants.V_ACTION_MODIFY);
		eleShipTo.setAttribute(KOHLSPCAConstants.A_CITY,eleTo.getAttribute(KOHLSPCAConstants.A_CITY));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_STATE,eleTo.getAttribute(KOHLSPCAConstants.A_STATE));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_ADDRESS_LINE1,eleTo.getAttribute(KOHLSPCAConstants.A_ADDRESS_LINE1));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_ADDRESS_LINE2,eleTo.getAttribute(KOHLSPCAConstants.A_ADDRESS_LINE2));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_ZIP_CODE,eleTo.getAttribute(KOHLSPCAConstants.A_ZIP_CODE));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_DAY_PHONE,eleTo.getAttribute(KOHLSPCAConstants.A_DAY_PHONE));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_EAMIL_ID,eleTo.getAttribute(KOHLSPCAConstants.A_EAMIL_ID));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_FIRST_NAME,eleToAddress.getAttribute(KOHLSPCAConstants.A_FIRST_NAME));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_LAST_NAME,eleToAddress.getAttribute(KOHLSPCAConstants.A_LAST_NAME));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_COUNTRY,eleToAddress.getAttribute(KOHLSPCAConstants.A_COUNTRY));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_ADDRESS_LINE3,eleToAddress.getAttribute(KOHLSPCAConstants.A_ADDRESS_LINE3));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_ADDRESS_LINE4,eleToAddress.getAttribute(KOHLSPCAConstants.A_ADDRESS_LINE4));
		eleShipTo.setAttribute(KOHLSPCAConstants.A_ADDRESS_LINE5,eleToAddress.getAttribute(KOHLSPCAConstants.A_ADDRESS_LINE5));
		
		return docChangeShipment;
		
	
		
	}
	
	public void getShippingAddressDetails() {
		
		
		
		Document docChangeShipment = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_SHIPMENT);
		Element eleChangeShipment = docChangeShipment.getDocumentElement();
		Object obj = input;
		Element ele = (Element) obj;
		
		System.out.println(YRCXmlUtils.getString(ele));
			YRCApiContext context = new YRCApiContext();
			String str =  ele.getAttribute("ShipmentKey");
			YRCXmlUtils.setAttribute(eleChangeShipment, "ShipmentKey", ele.getAttribute("ShipmentKey"));
			context.setApiName("getShipmentDetailsForChangeAddress");
			context.setFormId(getFormId());
			context.setInputXml(docChangeShipment);
			callApi(context);
			
		}
	
}
