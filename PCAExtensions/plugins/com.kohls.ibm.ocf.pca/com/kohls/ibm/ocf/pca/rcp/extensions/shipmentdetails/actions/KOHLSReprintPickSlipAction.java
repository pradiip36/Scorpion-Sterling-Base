package com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions;

import org.eclipse.jface.action.IAction;

import com.kohls.ibm.ocf.pca.rcp.extensions.SearchShipment.KOHLSSearchShipmentExtnBehavior;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCWizard;

public class KOHLSReprintPickSlipAction extends YRCAction {

	public static final String ACTION_ID = "com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSReprintPickSlipAction";

	public void execute(IAction action) {
			
			YRCWizard currentPage = (YRCWizard) YRCDesktopUI.getCurrentPage();
			KOHLSSearchShipmentExtnBehavior extnBehavior = (KOHLSSearchShipmentExtnBehavior) currentPage.getExtensionBehavior();
			extnBehavior.callReprintPickSlip();
	}

	@Override
	protected boolean checkForErrors() {
		return false;
	}

	@Override
	protected boolean checkForModifications() {
		return false;
	}
	
	

}
