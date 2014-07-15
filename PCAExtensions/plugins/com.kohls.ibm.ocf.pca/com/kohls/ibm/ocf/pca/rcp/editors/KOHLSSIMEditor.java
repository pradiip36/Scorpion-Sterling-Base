package com.kohls.ibm.ocf.pca.rcp.editors;

import com.kohls.ibm.ocf.pca.tasks.siminiproperties.wizardpages.KOHLSSIMPropertiesPage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCPlatformUI;


public class KOHLSSIMEditor extends YRCEditorPart{

	private String titlekey = "SIM Properties";  //  @jve:decl-index=0:
	private Composite pnlroot = null;
	public static final String ID_Editor = "com.kohls.ibm.ocf.pca.rcp.editors.KOHLSSIMEditor";
	
	public KOHLSSIMEditor() {
		super();

	}
	public Composite createPartControl(Composite parent, String arg1) {

		YRCEditorInput input = (YRCEditorInput) getEditorInput();
		pnlroot = new KOHLSSIMPropertiesPage(parent, SWT.NONE, input);
		pnlroot.setData(YRCConstants.YRC_OWNERPART, this);
		setPartName(YRCPlatformUI.getString(titlekey));
		return pnlroot;
		
	}
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
