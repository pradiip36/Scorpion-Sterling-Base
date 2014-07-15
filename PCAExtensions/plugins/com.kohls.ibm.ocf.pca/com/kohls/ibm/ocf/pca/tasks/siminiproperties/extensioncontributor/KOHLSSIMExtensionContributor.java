package com.kohls.ibm.ocf.pca.tasks.siminiproperties.extensioncontributor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.kohls.ibm.ocf.pca.tasks.siminiproperties.wizardpages.KOHLSSIMPropertiesPage;


import com.yantra.yfc.rcp.IYRCRelatedTasksExtensionContributor;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCRelatedTask;

public class KOHLSSIMExtensionContributor implements
IYRCRelatedTasksExtensionContributor {

	private Composite pnlroot = null;
	private String titlekey = "SIM Properties"; 
	public boolean acceptTask(YRCEditorInput arg0, YRCRelatedTask arg1) {
		boolean ret = true;
		if("EXTN_KOHLS_ADD_SIM".equals(arg0.getTaskName())){

				if("KOHLS_MODIFY_SIM".equals(arg1.getId())){
				ret = true;
			}
				
		}		
		return ret;	
	}

	public boolean canExecuteNewTask(YRCEditorInput arg0, YRCRelatedTask arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public Composite createPartControl(Composite parent, YRCEditorInput editorInput, YRCRelatedTask arg2) {
	
		
		pnlroot = new KOHLSSIMPropertiesPage(parent, SWT.NONE, editorInput);
		pnlroot.setData(YRCConstants.YRC_OWNERPART, this);
		
		//pnlroot.setPartName(YRCPlatformUI.getString(YRCPlatformUI.getString(titlekey)));
		
		return pnlroot;

	}

}
