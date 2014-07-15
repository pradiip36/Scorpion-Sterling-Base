package com.kohls.ibm.ocf.pca.rcp.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.kohls.ibm.ocf.pca.tasks.packshipment.wizards.KOHLSPackShipmentWizard;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCPlatformUI;

/** 
 * @author kiran potnuru
 * @created 22-May-2012
 * This class provides the functionality to open the editor
 */
public class KOHLSPackShipmentEditor extends YRCEditorPart {
	public static final String ID_Editor = "com.kohls.ibm.ocf.pca.rcp.editors.KOHLSPackShipmentEditor";

	private Composite pnlroot = null;

	public static final String titlekey = YRCPlatformUI.getString("Pack_Shipment");

	/*
	 * This is the default constructor
	 */
	public KOHLSPackShipmentEditor() {
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
		

		pnlroot = new KOHLSPackShipmentWizard(parent,getEditorInput(),SWT.NONE);
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
