package com.kohls.ibm.ocf.pca.tasks.changeShippingAddress.actions;





import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.kohls.ibm.ocf.pca.tasks.changeShippingAddress.wizardpages.KOHLSChangeShippingAddress;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCWizard;

public class KOHLSSaveChangeShippingAddressAction implements IWorkbenchWindowActionDelegate{

	public static String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.changeShippingAddress.actions.KOHLSSaveChangeShippingAddressAction";
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	public static String getACTION_ID() {
		return ACTION_ID;
	}
	
	public void run(IAction action) {
		YRCWizard currentWizard;
		
				
		Composite comp = YRCDesktopUI.getCurrentPage();
				
		//if (comp instanceof KohlsSIMPropertiesPage) {

		KOHLSChangeShippingAddress page = (KOHLSChangeShippingAddress) comp;
			try {
				// To get the WorkOrderList
				page.getBehavior().saveShippingAddress();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
		
	
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
