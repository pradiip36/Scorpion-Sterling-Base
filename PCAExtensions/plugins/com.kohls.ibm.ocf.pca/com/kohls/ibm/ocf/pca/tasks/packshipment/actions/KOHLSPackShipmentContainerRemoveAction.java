package com.kohls.ibm.ocf.pca.tasks.packshipment.actions;

/**
 *  © Copyright  2009 Sterling Commerce, Inc.
 */

import org.eclipse.jface.action.IAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.tasks.packshipment.wizardpages.KOHLSPackShipmentBehavior;
import com.kohls.ibm.ocf.pca.tasks.packshipment.wizardpages.KOHLSPackShipmentPage;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSPackShipmentContainerRemoveAction extends YRCAction {

	public static final String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.packshipment.actions.KOHLSPackShipmentContainerRemoveAction";

	public void execute(IAction action) {

		if (KOHLSPCAUtils.getCurrentPage() instanceof KOHLSPackShipmentPage) {

			KOHLSPackShipmentPage kohlsPackShipmentPage = (KOHLSPackShipmentPage) KOHLSPCAUtils
			.getCurrentPage();
			KOHLSPackShipmentBehavior myPageBehv = kohlsPackShipmentPage.getWizardPageBehavior();

			//			<!-- Start - Drop2 changes for "functionality"  -->

			Element eleCurrentContainer = myPageBehv.returnModel("CurrentContainerSource");
			String strTrackingNo = eleCurrentContainer.getAttribute(KOHLSPCAConstants.A_TRACKING_NO);
			
			if(!"".equals(strTrackingNo)){
				
				//	 Code for displaying void shipping label popup... if yes, then continue
				
				boolean boolWantToVoidShipment = YRCPlatformUI.getConfirmation(YRCPlatformUI.getString("TITLE_VOID_LABEL"), YRCPlatformUI.getString("MSG_VOID_LABEL_CONFIRMATION"));
				if(boolWantToVoidShipment){
					
					String strShipmentContainerKey = eleCurrentContainer.getAttribute(KOHLSPCAConstants.A_SHIPMENT_CONTAINER_KEY);
					Document inDoc = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_CONTAINER);
					Element eleInput = inDoc.getDocumentElement();
					eleInput.setAttribute(KOHLSPCAConstants.A_SHIPMENT_CONTAINER_KEY, strShipmentContainerKey);
					kohlsPackShipmentPage.getWizardPageBehavior().callgetShipmentContainerDetailsForVoid(inDoc);

				}
			}else{
				kohlsPackShipmentPage.getWizardPageBehavior().removeContainer();
			}
		
		}
	}

	public boolean checkForModifications() {
		return false;
	}

	public boolean checkForErrors() {
		return false;
	}
}
