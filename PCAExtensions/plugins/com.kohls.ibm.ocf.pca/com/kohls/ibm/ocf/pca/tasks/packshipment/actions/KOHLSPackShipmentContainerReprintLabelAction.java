package com.kohls.ibm.ocf.pca.tasks.packshipment.actions;
/**
 *  © Copyright  2009 Sterling Commerce, Inc.
 *  Drop2 - Reprint functionality
 */

import org.eclipse.jface.action.IAction;

import com.kohls.ibm.ocf.pca.tasks.packshipment.wizardpages.KOHLSPackShipmentPage;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.YRCAction;

public class KOHLSPackShipmentContainerReprintLabelAction extends YRCAction {

	public static final String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.packshipment.actions.KOHLSPackShipmentContainerReprintLabelAction";

	public void execute(IAction action) {
		
		if (KOHLSPCAUtils.getCurrentPage() instanceof KOHLSPackShipmentPage) {

			KOHLSPackShipmentPage kohlsPackShipmentPage = (KOHLSPackShipmentPage) KOHLSPCAUtils
					.getCurrentPage();
			kohlsPackShipmentPage.getWizardPageBehavior().callGetShipmentContainerDetails();

		}
	}

	public boolean checkForModifications() {
		return false;
	}

	public boolean checkForErrors() {
		return false;
	}
}
