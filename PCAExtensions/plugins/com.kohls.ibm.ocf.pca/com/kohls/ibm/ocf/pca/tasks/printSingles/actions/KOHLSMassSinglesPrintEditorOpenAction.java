package com.kohls.ibm.ocf.pca.tasks.printSingles.actions;

import org.eclipse.jface.action.IAction;

import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSMassSinglesPrintEditor;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSMassSinglesPrintEditorOpenAction extends YRCAction {
	
	public static String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.printSingles.actions.KOHLSMassSinglesPrintEditorOpenAction";

	@Override
	public void execute(IAction arg0) {
		YRCPlatformUI.openEditor(KOHLSMassSinglesPrintEditor.EDITOR_ID,
				new YRCEditorInput(YRCXmlUtils.createFromString(
						"<PrintMassSingles/>").getDocumentElement(),
						new String[] { "" }, YRCPlatformUI.getString("Print_Mass_Singles")));

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
