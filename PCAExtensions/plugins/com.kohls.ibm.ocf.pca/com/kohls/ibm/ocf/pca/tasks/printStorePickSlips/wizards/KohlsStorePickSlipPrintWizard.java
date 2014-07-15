package com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizards;

import org.eclipse.swt.widgets.Composite;

import com.kohls.ibm.ocf.pca.tasks.printSingles.wizards.KOHLSMassSinglesPrintWizardBehavior;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCWizardBehavior;

public class KohlsStorePickSlipPrintWizard extends YRCWizard {
	
	private KohlsStorePickSlipPrintWizardBehavior myBehavior;
	public static final String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.printSingles.wizards.KohlsStorePickSlipPrintWizard";
	public KohlsStorePickSlipPrintWizard(Composite parent, int style, Object inputObject)
	{
		super(FORM_ID, parent, style);
		initializeWizard();
		start();
	}

	protected YRCWizardBehavior createBehavior()
	{
		myBehavior = new KohlsStorePickSlipPrintWizardBehavior(this, FORM_ID, wizardInput);
		return myBehavior;
	}
	
	 public String getFormId() {
	        return FORM_ID;
	 }

	 protected void addPages(Composite parent) {
	    }

	    protected String getFirstPageId() {
	        return null;
	    }
	    
	    public IYRCPanelHolder getPanelHolder() {
			return null;
		}

		public YRCWizardBehavior getWizardBehavior() {
			return myBehavior;
		}
}
