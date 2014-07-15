package com.kohls.ibm.ocf.pca.tasks.siminiproperties.actions;

import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSSIMEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSModifySIMAction implements IWorkbenchWindowActionDelegate{

	public static String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.siminiproperties.actions.KOHLSModifySIMAction";
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		Element inputElement =getOpenEditorXML();
		YRCPlatformUI.openEditor(KOHLSSIMEditor.ID_Editor,
				new YRCEditorInput(inputElement,
						new String[] { "" }, ""));

	}
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
	private Element getOpenEditorXML() {
		
			Element eleSaveSIMInput = KohlsApplicationInitializer.eleVerifyTerminalProperties;
			Document docSIMINIPropertiesInput = YRCXmlUtils.createDocument("SIMINIPropertiesInput");
			Element SIMINIPropertiesInput = docSIMINIPropertiesInput.getDocumentElement();
			SIMINIPropertiesInput.setAttribute("TerminalID",eleSaveSIMInput.getAttribute("TerminalID"));
			SIMINIPropertiesInput.setAttribute("Action", "Modify");
			return SIMINIPropertiesInput;
		
	}

}
