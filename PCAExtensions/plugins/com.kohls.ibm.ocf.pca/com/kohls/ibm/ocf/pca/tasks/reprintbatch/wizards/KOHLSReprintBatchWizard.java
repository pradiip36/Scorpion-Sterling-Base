/**
 *  © Copyright  2009 Sterling Commerce, Inc.
 */
/*
 * Created on Mar 09,2006
 *
 */
package com.kohls.ibm.ocf.pca.tasks.reprintbatch.wizards;

import org.eclipse.swt.widgets.Composite;

import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCWizardBehavior;

/**
 * @author Kiran
 *
 */

public class KOHLSReprintBatchWizard extends YRCWizard
{

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */

	private KOHLSReprintBatchWizardBehavior myBehavior;
	Object wizardInput;
	
	public static final String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.reprintbatch.wizards.KOHLSReprintBatchWizard";
	public KOHLSReprintBatchWizard(Composite parent, int style, Object wizardInput)
	{
		super(FORM_ID, parent, wizardInput, style);
		this.wizardInput=wizardInput;
		initializeWizard();
		start();
	}

	protected YRCWizardBehavior createBehavior()
	{
		myBehavior = new KOHLSReprintBatchWizardBehavior(this, FORM_ID, wizardInput);
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