package com.kohls.ibm.ocf.pca.tasks.siminiproperties.actions;



import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


import com.kohls.ibm.ocf.pca.tasks.siminiproperties.wizardpages.KOHLSSIMPropertiesPage;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCWizard;

public class KOHLSSavePropertiesAction implements IWorkbenchWindowActionDelegate{

	public static String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.siminiproperties.actions.KOHLSSavePropertiesAction";
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		YRCWizard currentWizard;
		
				
		Composite comp = YRCDesktopUI.getCurrentPage();
				
		//if (comp instanceof KohlsSIMPropertiesPage) {

			KOHLSSIMPropertiesPage page = (KOHLSSIMPropertiesPage) comp;
			try {
				// To get the WorkOrderList
				page.getBehavior().saveProperties();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
		
	
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
