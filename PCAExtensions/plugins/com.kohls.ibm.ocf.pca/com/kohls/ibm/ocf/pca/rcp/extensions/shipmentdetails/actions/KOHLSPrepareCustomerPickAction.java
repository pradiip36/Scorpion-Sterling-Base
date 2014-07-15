package com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions;

import org.eclipse.jface.action.IAction;

import com.kohls.ibm.ocf.pca.rcp.extensions.SearchShipment.KOHLSSearchShipmentExtnBehavior;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCWizard;

public class KOHLSPrepareCustomerPickAction extends YRCAction {

	public static final String ACTION_ID = "com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSPrepareCustomerPickAction";

	public void execute(IAction action) {
			//System.out.println("adasdsa");
			YRCWizard currentPage = (YRCWizard) YRCDesktopUI.getCurrentPage();
			KOHLSSearchShipmentExtnBehavior extnBehavior = (KOHLSSearchShipmentExtnBehavior) currentPage.getExtensionBehavior();
			extnBehavior.callReadyForCustomerPick();
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
