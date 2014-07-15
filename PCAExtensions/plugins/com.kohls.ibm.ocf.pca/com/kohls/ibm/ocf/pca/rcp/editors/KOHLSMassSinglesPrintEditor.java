package com.kohls.ibm.ocf.pca.rcp.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.kohls.ibm.ocf.pca.tasks.printSingles.wizards.KOHLSMassSinglesPrintWizard;
import com.kohls.ibm.ocf.pca.tasks.printpackslip.wizards.KOHLSPrintPackSlipWizard;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCPlatformUI;

/** 
 * @author kiran potnuru
 * @created 14-Sept-2012
 * This class provides the functionality to open the editor
 */
public class KOHLSMassSinglesPrintEditor extends YRCEditorPart {
	public static final String EDITOR_ID = "com.kohls.ibm.ocf.pca.rcp.editors.KOHLSMassSinglesPrintEditor";

	private Composite pnlroot = null;

	public static final String titlekey = YRCPlatformUI.getString("SOP_Mass_Singles_Print");

	/*
	 * This is the default constructor
	 */
	public KOHLSMassSinglesPrintEditor() {
		super();

	}

	/*
	 * This method is invoked to create an instance of the KOHLSPrintPackSlipWizard
	 * and the Wizard is opened in the editor (non-Javadoc)
	 * 
	 * @see com.yantra.yfc.rcp.YRCEditorPart#createPartControl(org.eclipse.swt.widgets.Composite,
	 *      java.lang.String)
	 */
	public Composite createPartControl(Composite parent, String task) {
		pnlroot = new KOHLSMassSinglesPrintWizard(parent,SWT.NONE,null);
		pnlroot.setData(YRCConstants.YRC_OWNERPART, this);
		setPartName(YRCPlatformUI.getString(titlekey));
		return pnlroot;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty() {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {

	}

}
