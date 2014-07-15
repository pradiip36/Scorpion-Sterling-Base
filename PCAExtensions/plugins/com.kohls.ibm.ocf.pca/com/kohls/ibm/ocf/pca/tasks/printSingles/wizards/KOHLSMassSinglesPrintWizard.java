/**
 *  © Copyright  2009 Sterling Commerce, Inc.
 */
/*
 * Created on Mar 09,2006
 *
 */
package com.kohls.ibm.ocf.pca.tasks.printSingles.wizards;

import org.eclipse.swt.widgets.Composite;

import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCWizardBehavior;

/**
 * @author Kiran
 *
 */

public class KOHLSMassSinglesPrintWizard extends YRCWizard
{

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */

	private KOHLSMassSinglesPrintWizardBehavior myBehavior;
	public static final String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.printSingles.wizards.KOHLSMassSinglesPrintWizard";
	public KOHLSMassSinglesPrintWizard(Composite parent, int style, Object inputObject)
	{
		super(FORM_ID, parent, inputObject, style);
		initializeWizard();
		start();
	}

	protected YRCWizardBehavior createBehavior()
	{
		myBehavior = new KOHLSMassSinglesPrintWizardBehavior(this, FORM_ID, wizardInput);
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