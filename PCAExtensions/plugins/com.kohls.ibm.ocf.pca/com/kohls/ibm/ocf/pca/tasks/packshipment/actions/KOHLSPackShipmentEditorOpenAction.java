package com.kohls.ibm.ocf.pca.tasks.packshipment.actions;

import org.eclipse.jface.action.IAction;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSPackShipmentEditor;
import com.kohls.ibm.ocf.pca.rcp.extensions.SearchShipment.KOHLSSearchShipmentExtnBehavior;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSPackShipmentEditorOpenAction extends YRCAction {
	
	public static String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.packshipment.actions.KOHLSPackShipmentEditorOpenAction";

	@Override
	public void execute(IAction arg0) {
		
		if(KOHLSPCAUtils.getCurrentPage().getFormId().equals(KOHLSPCAConstants.SOP_GENERIC_SHIPMENT_DETAILS_SCREEN_FORM_ID)){
			
		YRCWizard currentPage = (YRCWizard) YRCDesktopUI.getCurrentPage();
		KOHLSSearchShipmentExtnBehavior shipmentDetailsExtnBehavior = (KOHLSSearchShipmentExtnBehavior) currentPage.getExtensionBehavior();
		
		Element eleShipmentDtls=shipmentDetailsExtnBehavior.getShipmentDetailsModel();
		
		YRCPlatformUI.openEditor(KOHLSPackShipmentEditor.ID_Editor,
				new YRCEditorInput(eleShipmentDtls,
						new String[] { "" }, YRCPlatformUI.getString("Pack_Shipment")));
		
		}
		
		else {
		
		YRCPlatformUI.openEditor(KOHLSPackShipmentEditor.ID_Editor,
				new YRCEditorInput(YRCXmlUtils.createFromString(
						"<PackShipment/>").getDocumentElement(),
						new String[] { "" }, YRCPlatformUI.getString("Pack_Shipment")));
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
