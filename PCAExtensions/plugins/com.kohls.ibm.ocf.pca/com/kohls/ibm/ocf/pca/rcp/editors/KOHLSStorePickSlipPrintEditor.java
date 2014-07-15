package com.kohls.ibm.ocf.pca.rcp.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizards.KohlsStorePickSlipPrintWizard;
import com.kohls.ibm.ocf.pca.tasks.reprintbatch.wizards.KOHLSReprintBatchWizard;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCPlatformUI;

public class KOHLSStorePickSlipPrintEditor extends YRCEditorPart {
	public static final String EDITOR_ID = "com.kohls.ibm.ocf.pca.rcp.editors.KOHLSStorePickSlipPrintEditor";

	private Composite pnlroot = null;

	public static final String titlekey = YRCPlatformUI.getString("KOHLS_STORE_PRINT_PICK_SLIP");
	
	public KOHLSStorePickSlipPrintEditor() {
		super();
	}

	@Override
	public Composite createPartControl(Composite parent, String task) {
		// TODO Auto-generated method stub
		pnlroot = new KohlsStorePickSlipPrintWizard(parent,SWT.NONE,null);		
		pnlroot.setData(YRCConstants.YRC_OWNERPART, this);
		setPartName(YRCPlatformUI.getString(titlekey));
		return pnlroot;
	}
	
	public boolean isDirty() {

		return false;
	}
	
	public boolean isSaveAsAllowed() {

		return false;
	}
}
