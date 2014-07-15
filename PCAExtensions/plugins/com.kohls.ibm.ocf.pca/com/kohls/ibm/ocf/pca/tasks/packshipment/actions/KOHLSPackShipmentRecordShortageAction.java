package com.kohls.ibm.ocf.pca.tasks.packshipment.actions;

/**
 *  © Copyright  2009 Sterling Commerce, Inc.
 */

import org.eclipse.jface.action.IAction;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.tasks.packshipment.wizardpages.KOHLSPackShipmentBehavior;
import com.kohls.ibm.ocf.pca.tasks.packshipment.wizardpages.KOHLSPackShipmentPage;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.YRCAction;

public class KOHLSPackShipmentRecordShortageAction extends YRCAction {

	public static final String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.packshipment.actions.KOHLSPackShipmentRecordShortageAction";

	public void execute(IAction action) {

		if (KOHLSPCAUtils.getCurrentPage() instanceof KOHLSPackShipmentPage) {
			
			
			KOHLSPackShipmentPage kohlsPackShipmentPage = (KOHLSPackShipmentPage) KOHLSPCAUtils
			.getCurrentPage();
			KOHLSPackShipmentBehavior packShipmentBehv = kohlsPackShipmentPage.getWizardPageBehavior();
			
			Element eleShipmentDetails = packShipmentBehv.returnModel("getShipmentDetailsForPackShipment");
			
			//boolean dorecordShortage=YRCPlatformUI.getConfirmation(YRCPlatformUI.getString("RECORD_SHORTAGE"), YRCPlatformUI.getString("CONFIRM_RECORD_SHORTAGE"));
			
			//if(dorecordShortage){
			packShipmentBehv.launchReasonCodePopup();
			//}
			
		}

	}

	public boolean checkForModifications() {
		return false;
	}

	public boolean checkForErrors() {
		return false;
	}
}
