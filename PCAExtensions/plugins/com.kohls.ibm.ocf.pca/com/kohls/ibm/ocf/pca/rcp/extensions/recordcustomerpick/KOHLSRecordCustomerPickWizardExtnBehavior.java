	
package com.kohls.ibm.ocf.pca.rcp.extensions.recordcustomerpick;

/**
 * Created on Jun 16,2012
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.util.KOHLSPCAApiNames;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
/**
 * @author admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class KOHLSRecordCustomerPickWizardExtnBehavior extends YRCWizardExtensionBehavior {
	 
	 
	 private static final String WIZARD_ID = "com.yantra.pca.sop.rcp.tasks.outboundexecution.pickup.recordcustomerpick.wizards.SOPRecordCustomerPickWizard";

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
	}
	
	
	@Override
	public void handleApiCompletion(YRCApiContext ctx) {
		if (ctx.getInvokeAPIStatus() < 1) {
			YRCPlatformUI.trace("API exception in " + getFormId()
					+ " page, ApiName " + ctx.getApiName() + ",Exception : ",
					ctx.getException());
		} else {

			if (ctx.getApiName().equals(
					KOHLSPCAApiNames.API_CONFIRM_CUSTOMER_PICK_SERVICE)) {
				YRCPlatformUI.trace("The command name is "
						+ KOHLSPCAApiNames.API_CONFIRM_CUSTOMER_PICK_SERVICE, null);

				if (!YRCPlatformUI.isVoid(KOHLSPCAUtils.getDefaultPrinterID())) {

					YRCPlatformUI.setMessage(YRCPlatformUI
							.getFormattedString(YRCPlatformUI
									.getString("REPRINT_PACK_SLIP_PRINT_INFO"),
									new String[] { KOHLSPCAUtils
											.getDefaultPrinterID() }));

				}

			}
		}

		super.handleApiCompletion(ctx);
	}
	@Override
	public void postSetModel(String model) {
		
		if(YRCPlatformUI.equals(model, "ShipmentDetails")){
			
			Element eleShipmentDetails=getModel("ShipmentDetails");
			
			String strPrinterID = System
			.getProperty(KOHLSPCAConstants.INI_PROPERTY_PRINTER_ID);
			eleShipmentDetails.setAttribute(KOHLSPCAConstants.A_LABEL_PICK_TICKET, KOHLSPCAConstants.V_N);

	if (!YRCPlatformUI.isVoid(eleShipmentDetails)) {

		if(!YRCPlatformUI.isVoid(strPrinterID)){
			eleShipmentDetails.setAttribute(KOHLSPCAConstants.A_PRINTER_ID, strPrinterID);
		}else{
			
			YRCPlatformUI.showError(YRCPlatformUI.getString("ERROR"), YRCPlatformUI.getString("ERROR_DEFAULTING_PRINTER_ID"));
		}

	}
				
		}	
		super.postSetModel(model);
	}
	

  
  
 	
    public String getExtnNextPage(String currentPageId) {
		//TODO
		return null;
    }
    
    public IYRCComposite createPage(String pageIdToBeShown) {
		//TODO
		return null;
	}
    
    public void pageBeingDisposed(String pageToBeDisposed) {
		//TODO
    }

    /**
     * Called when a wizard page is about to be shown for the first time.
     *
     */
    public void initPage(String pageBeingShown) {
		//TODO
    }
 	
 	
	/**
	 * Method for validating the text box.
     */
    public YRCValidationResponse validateTextField(String fieldName, String fieldValue) {
    	// TODO Validation required for the following controls.
		
		// TODO Create and return a response.
		return super.validateTextField(fieldName, fieldValue);
	}
    
    /**
     * Method for validating the combo box entry.
     */
    public void validateComboField(String fieldName, String fieldValue) {
    	// TODO Validation required for the following controls.
		
		// TODO Create and return a response.
		super.validateComboField(fieldName, fieldValue);
    }
    
    /**
     * Method called when a button is clicked.
     */
    public YRCValidationResponse validateButtonClick(String fieldName) {
    	// TODO Validation required for the following controls.
		
			// Control name: btnConfirm
		
		// TODO Create and return a response.
		return super.validateButtonClick(fieldName);
    }
    
    /**
     * Method called when a link is clicked.
     */
	public YRCValidationResponse validateLinkClick(String fieldName) {
    	// TODO Validation required for the following controls.
		
		// TODO Create and return a response.
		return super.validateLinkClick(fieldName);
	}
	
	/**
	 * Create and return the binding data for advanced table columns added to the tables.
	 */
	 public YRCExtendedTableBindingData getExtendedTableBindingData(String tableName, ArrayList tableColumnNames) {
	 	// Create and return the binding data definition for the table.
		
	 	// The defualt super implementation does nothing.
	 	return super.getExtendedTableBindingData(tableName, tableColumnNames);
	 }
	 
	 void callApi(String name, Document inputXml) {
			YRCApiContext context = new YRCApiContext();		
			context.setApiName(name);
			context.setFormId(WIZARD_ID);
		    context.setInputXml(inputXml);
		    callApi(context);
		}
}
