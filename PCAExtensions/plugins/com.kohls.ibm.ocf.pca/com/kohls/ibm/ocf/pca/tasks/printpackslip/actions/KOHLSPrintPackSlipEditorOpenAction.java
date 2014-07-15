package com.kohls.ibm.ocf.pca.tasks.printpackslip.actions;

import org.eclipse.jface.action.IAction;

import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSPrintPackSlipEditor;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSPrintPackSlipEditorOpenAction extends YRCAction {
	
	public static String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.printpackslip.actions.KOHLSPrintPackSlipEditorOpenAction";

	@Override
	public void execute(IAction arg0) {
		YRCPlatformUI.openEditor(KOHLSPrintPackSlipEditor.ID_Editor,
				new YRCEditorInput(YRCXmlUtils.createFromString(
						"<PrintPackSlip/>").getDocumentElement(),
						new String[] { "" }, YRCPlatformUI.getString("Print_Pack_Slips")));

	}

	@Override
	protected boolean checkForErrors() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkForModifications() {
		// TODO Auto-generated method stub
		return false;
	}

}
