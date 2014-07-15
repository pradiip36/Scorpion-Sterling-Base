package com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.kohls.ibm.ocf.pca.tasks.printSingles.wizardpages.KOHLSMassSinglesPrintScreen;
import com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizardpages.KOHLSStorePickSlipPrintPage;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCWizardBehavior;

public class KohlsStorePickSlipPrintWizardBehavior extends YRCWizardBehavior {
	private Object inputObject;
	public KohlsStorePickSlipPrintWizardBehavior(Composite ownerComposite,
			String formId, Object inputObject){
		super(ownerComposite,formId,inputObject);
		this.inputObject = inputObject;
		init();
		
	}
	@Override
	public IYRCComposite createPage(String pageIdToBeShown, Composite ownerComposite) {
		if (pageIdToBeShown.equals(KOHLSStorePickSlipPrintPage.FORM_ID))
		{
			KOHLSStorePickSlipPrintPage page = new KOHLSStorePickSlipPrintPage(
					ownerComposite, SWT.None, inputObject);
			page.setWizBehavior(this);
			return page;
		}
		return null;
	}

	@Override
	public void pageBeingDisposed(String arg0) {
		// TODO Auto-generated method stub

	}
	
}
