package com.kohls.ibm.ocf.pca.tasks.siminiproperties.actions;



import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSSIMEditor;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;



public class KOHLSSIMEditorOpenAction implements IWorkbenchWindowActionDelegate{

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
						new String[] {}, "EXTN_KOHLS_ADD_SIM"));
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
private Element getOpenEditorXML() {
		
		Document docInputXML = YRCXmlUtils.createDocument("SIMINIProperties");
		Element eleInputXML = docInputXML.getDocumentElement();
		eleInputXML.setAttribute("TerminalID", "");
		eleInputXML.setAttribute("ShipmentsPerTote", "");
		eleInputXML.setAttribute("DefaultPackContainer", "");
		eleInputXML.setAttribute("DefaultPackPrinter", "");
		eleInputXML.setAttribute("DefaultPrinter", "");
		eleInputXML.setAttribute("DefaultPackStation", "");
		eleInputXML.setAttribute("SerialPort", "");
		return docInputXML.getDocumentElement();
	}
}
