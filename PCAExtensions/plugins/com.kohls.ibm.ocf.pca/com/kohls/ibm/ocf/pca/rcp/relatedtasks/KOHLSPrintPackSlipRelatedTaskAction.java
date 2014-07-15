package com.kohls.ibm.ocf.pca.rcp.relatedtasks;

import org.eclipse.jface.action.IAction;

import com.kohls.ibm.ocf.pca.tasks.printpackslip.actions.KOHLSPrintPackSlipEditorOpenAction;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCRelatedTask;
import com.yantra.yfc.rcp.YRCRelatedTaskAction;

public class KOHLSPrintPackSlipRelatedTaskAction extends YRCRelatedTaskAction {

	@Override
	protected boolean checkForErrors() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkForModifications() {
		// TODO Auto-generated method stub
		return super.checkForModifications();
	}

	@Override
	public void executeTask(IAction action, YRCEditorInput input,
			YRCRelatedTask task) {

		
		YRCPlatformUI.fireAction(KOHLSPrintPackSlipEditorOpenAction.ACTION_ID);
		
		
	}


}
