package com.kohls.ibm.ocf.pca.tasks.batchprint.actions;

import org.eclipse.jface.action.IAction;

import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSBatchPrintScreenEditor;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;


public class KOHLSBatchPrintScreenEditorOpenAction extends YRCAction{

	public static final String ACTION_ID="com.kohls.ibm.ocf.pca.tasks.batchprint.actions.KOHLSBatchPrintScreenEditorOpenAction";

	protected boolean checkForErrors() {
		return false;
	}

	protected boolean checkForModifications() {
		return false;
	}

public void execute(IAction arg0) {
		String strCurrentForm="";
		
		if(!YRCPlatformUI.isVoid(KOHLSPCAUtils.getCurrentPage()))
			strCurrentForm=KOHLSPCAUtils.getCurrentPage().getFormId();
		
		
		YRCPlatformUI.openEditor(KOHLSBatchPrintScreenEditor.ID_Editor,
				new YRCEditorInput(YRCXmlUtils.createFromString(
						"<BatchPrint/>").getDocumentElement(),
						new String[] { "" }, YRCPlatformUI.getString("Batch_Print")));
			
	}
}