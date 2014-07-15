package com.kohls.ibm.ocf.pca.rcp.relatedtasks;

import org.eclipse.jface.action.IAction;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSChangeShipmentAddressEditor;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCRelatedTask;
import com.yantra.yfc.rcp.YRCRelatedTaskAction;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSChangeShippingAddressTaskAction extends YRCRelatedTaskAction{

	
	public static final String ACTION_ID = "com.kohls.ibm.ocf.pca.rcp.relatedtasks.KohlsChangeShippingAddressAction";
	
	@Override
	public void executeTask(IAction arg0, YRCEditorInput eEditorInput, YRCRelatedTask arg2) {
		// TODO Auto-generated method stub
		Element eInXml = eEditorInput.getXml();
		
		Object inputObj = eEditorInput.getInputObject();
		eEditorInput.setInputObject(eInXml);
		
		
		Element inputElement = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_CHANGE_SHIPMENT_ADDRESS).getDocumentElement();		
		YRCXmlUtils.importElement(inputElement, eInXml);
		YRCPlatformUI.openEditor(KOHLSChangeShipmentAddressEditor.ID_Editor,
				new YRCEditorInput(inputElement,
						new String[] { "" }, ""));
		
			
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