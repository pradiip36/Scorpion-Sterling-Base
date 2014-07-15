package com.kohls.ibm.ocf.pca.tasks.siminiproperties.actions;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSSIMEditor;

public class KOHLSCloseEditorAction implements IWorkbenchWindowActionDelegate{

	public static String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.siminiproperties.actions.KOHLSCloseEditorAction";
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		// TODO Auto-generated method stub
		YRCPlatformUI.closeEditor(KOHLSSIMEditor.ID_Editor,
				true);
		}
	

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
