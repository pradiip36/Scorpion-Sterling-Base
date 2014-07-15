package com.kohls.ibm.ocf.pca.rcp.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.tasks.changeShippingAddress.wizardpages.KOHLSChangeShippingAddress;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;


public class KOHLSChangeShipmentAddressEditor extends YRCEditorPart {
	public static final String ID_Editor = "com.kohls.ibm.ocf.pca.rcp.editors.KOHLSChangeShipmentAddressEditor";

	private Composite pnlroot = null;

	//public static final String titlekey = YRCPlatformUI.getString("Reprint_Batch");

	/*
	 * This is the default constructor
	 */
	public KOHLSChangeShipmentAddressEditor() {
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
		System.out.println(task);
		YRCEditorInput input = (YRCEditorInput) getEditorInput();
		System.out.println(YRCXmlUtils.getString(input.getXml()));
		pnlroot = new KOHLSChangeShippingAddress(parent,SWT.NONE,input);
		pnlroot.setData(YRCConstants.YRC_OWNERPART, this);
		setPartName(YRCPlatformUI.getString("Change Shipping Address"));
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

	@Override
	public IEditorInput getEditorInput() {
		// TODO Auto-generated method stub
		return super.getEditorInput();
	}
	
	

}
