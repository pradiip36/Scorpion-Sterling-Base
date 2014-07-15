package com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.actions;

import org.eclipse.jface.action.IAction;
import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSMassSinglesPrintEditor;
import org.w3c.dom.Element;
import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSStorePickSlipPrintEditor;
import com.kohls.ibm.ocf.pca.rcp.extensions.SearchShipment.KOHLSSearchShipmentExtnBehavior;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSStorePickSlipPrintEditorOpenAction extends YRCAction {

	public static String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.actions.KOHLSStorePickSlipPrintEditorOpenAction";

	@Override
	public void execute(IAction arg0) {
		
		if(KOHLSPCAUtils.getCurrentPage().getFormId().equals(KOHLSPCAConstants.SOP_GENERIC_SHIPMENT_DETAILS_SCREEN_FORM_ID)){
			
			YRCWizard currentPage = (YRCWizard) YRCDesktopUI.getCurrentPage();
			KOHLSSearchShipmentExtnBehavior shipmentDetailsExtnBehavior = (KOHLSSearchShipmentExtnBehavior) currentPage.getExtensionBehavior();
			if(!YRCPlatformUI.isVoid(shipmentDetailsExtnBehavior)){
				shipmentDetailsExtnBehavior.printPickSlipForStoreShipment();
			}
			
			/*Element eleShipmentDtls=shipmentDetailsExtnBehavior.getShipmentDetailsModel();
			if(!YRCPlatformUI.isVoid(eleShipmentDtls)){
				if(!YRCPlatformUI.isVoid(eleShipmentDtls.getAttribute("Status")) && eleShipmentDtls.getAttribute("Status").compareTo("1100.03") >= 0
						&& !eleShipmentDtls.getAttribute("Status").equals("9000")){
					
				YRCPlatformUI.openEditor(KOHLSStorePickSlipPrintEditor.EDITOR_ID,
						new YRCEditorInput(eleShipmentDtls,
								new String[] { "" }, YRCPlatformUI.getString("Print_Store_Pick_Slips")));
				
				}
				else
				{
					YRCPlatformUI.showError("Error", "Pick slip cannot be printed for the Shipment in this status");
				}
			}*/
		}
			
			else {
			
				YRCPlatformUI.openEditor(KOHLSStorePickSlipPrintEditor.EDITOR_ID,
						new YRCEditorInput(YRCXmlUtils.createFromString(
								"<PrintStorePickSlips/>").getDocumentElement(),
								new String[] { "" }, YRCPlatformUI.getString("Print_Store_Pick_Slips")));
			}

		
	}


	@Override
	protected boolean checkForErrors() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkForModifications() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
