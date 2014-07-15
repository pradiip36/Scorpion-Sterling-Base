package com.kohls.ibm.ocf.pca.rcp.relatedtasks;

import org.eclipse.jface.action.IAction;

import com.kohls.ibm.ocf.pca.tasks.batchprint.actions.KOHLSBatchPrintScreenEditorOpenAction;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCRelatedTask;
import com.yantra.yfc.rcp.YRCRelatedTaskAction;

public class KOHLSBatchPrintRelatedTaskAction extends YRCRelatedTaskAction {

	@Override
	public void executeTask(IAction action, YRCEditorInput input,
			YRCRelatedTask task) {
		
		YRCPlatformUI.fireAction(KOHLSBatchPrintScreenEditorOpenAction.ACTION_ID);

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
