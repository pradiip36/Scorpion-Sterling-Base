package com.kohls.ibm.ocf.pca.tasks.reprintbatch.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

import com.kohls.ibm.ocf.pca.tasks.printSingles.wizardpages.KOHLSMassSinglesPrintScreen;
import com.kohls.ibm.ocf.pca.tasks.printSingles.wizardpages.KOHLSMassSinglesPrintScreenBehavior;
import com.kohls.ibm.ocf.pca.tasks.reprintbatch.wizardpages.KOHLSReprintBatchScreen;
import com.kohls.ibm.ocf.pca.tasks.reprintbatch.wizardpages.KOHLSReprintBatchScreenBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCWizard;


public class KOHLSReprintBatchPrintAction extends YRCAction{

	public static final String ACTION_ID="com.kohls.ibm.ocf.pca.tasks.reprintbatch.actions.KOHLSReprintBatchPrintAction";

	protected boolean checkForErrors() {
		return false;
	}

	protected boolean checkForModifications() {
		return false;
	}

	public void execute(IAction action) {
		Composite comp = YRCDesktopUI.getCurrentPage();
		IYRCComposite currentPage = null;
		if (comp instanceof YRCWizard) {
			YRCWizard wizard = (YRCWizard) YRCDesktopUI.getCurrentPage();
			currentPage = (IYRCComposite) wizard.getCurrentPage();
		}else if(comp instanceof IYRCComposite){
			currentPage = (IYRCComposite)comp;
		}else{
		
		}
		if(currentPage instanceof KOHLSReprintBatchScreen){
			((KOHLSReprintBatchScreenBehavior)((KOHLSReprintBatchScreen)currentPage).getBehavior()).doPrint();
		}		
	}
}