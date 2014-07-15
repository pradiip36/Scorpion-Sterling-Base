package com.kohls.ibm.ocf.pca.rcp.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.kohls.ibm.ocf.pca.tasks.batchprint.wizards.KOHLSBatchPrintWizard;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCPlatformUI;


public class KOHLSBatchPrintScreenEditor extends YRCEditorPart {
	
	public static final String ID_Editor = "com.kohls.ibm.ocf.pca.rcp.editors.KOHLSBatchPrintScreenEditor";

	private Composite pnlroot = null;

	public static final String titlekey = YRCPlatformUI.getString("Batch_Print");

	/*
	 * This is the default constructor
	 */
	public KOHLSBatchPrintScreenEditor() {
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
		
		pnlroot = new KOHLSBatchPrintWizard(parent,SWT.NONE,getEditorInput());
		
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
