package com.kohls.ibm.ocf.pca.tasks.printpackslip.actions;
/**
 *  © Copyright  2009 Sterling Commerce, Inc.
 */


import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

import com.kohls.ibm.ocf.pca.tasks.printpackslip.wizardpages.KOHLSPrintPackSlip;
import com.kohls.ibm.ocf.pca.tasks.printpackslip.wizards.KOHLSPrintPackSlipWizard;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCDesktopUI;

public class KOHLSPrintPackSlipPrintSinglesAction extends YRCAction{

	public static final String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.printpackslip.actions.KOHLSPrintPackSlipPrintSinglesAction";

	public void execute(IAction action) {
		
		Composite comp = YRCDesktopUI.getCurrentPage();
		if(comp instanceof KOHLSPrintPackSlipWizard){
			Composite page = ((KOHLSPrintPackSlipWizard)comp).getCurrentPage();
			if(page instanceof KOHLSPrintPackSlip){
				KOHLSPrintPackSlip kohlsPrintPackSlipPage = (KOHLSPrintPackSlip)page;
				kohlsPrintPackSlipPage.getWizardPageBehavior().singleprint();
			}
		}
	}
	
	public boolean checkForModifications(){
		return false;
	}

	public boolean checkForErrors(){
		return false;
	}
}
