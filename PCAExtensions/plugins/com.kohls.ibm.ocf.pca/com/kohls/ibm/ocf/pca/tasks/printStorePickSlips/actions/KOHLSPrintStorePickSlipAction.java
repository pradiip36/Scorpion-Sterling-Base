package com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

import com.kohls.ibm.ocf.pca.tasks.printSingles.wizardpages.KOHLSMassSinglesPrintScreen;
import com.kohls.ibm.ocf.pca.tasks.printSingles.wizardpages.KOHLSMassSinglesPrintScreenBehavior;
import com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizardpages.KOHLSStorePickSlipPrintPage;
import com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizardpages.KOHLSStorePickSlipPrintPageBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCWizard;

public class KOHLSPrintStorePickSlipAction extends YRCAction {
	public static final String ACTION_ID="com.kohls.ibm.ocf.pca.tasks.printSingles.actions.KOHLSPrintStorePickSlipAction";

	public KOHLSPrintStorePickSlipAction() {
		// TODO Auto-generated constructor stub
	}
	
	protected boolean checkForErrors() {
		return false;
	}

	protected boolean checkForModifications() {
		return false;
	}

	@Override
	public void execute(IAction arg0) {

		Composite comp = YRCDesktopUI.getCurrentPage();
		IYRCComposite currentPage = null;
		if (comp instanceof YRCWizard) {
			YRCWizard wizard = (YRCWizard) YRCDesktopUI.getCurrentPage();
			currentPage = (IYRCComposite) wizard.getCurrentPage();
		}else if(comp instanceof IYRCComposite){
			currentPage = (IYRCComposite)comp;
		}else{
		
		}
		if(currentPage instanceof KOHLSStorePickSlipPrintPage){
			((KOHLSStorePickSlipPrintPageBehavior)((KOHLSStorePickSlipPrintPage)currentPage).getBehavior()).printPickSlips();
		}		
	

	}

}
