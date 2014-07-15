package com.kohls.ibm.ocf.pca.rcp.extensions.SearchShipment;

import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.util.KohlsCommonUtil;
import com.yantra.yfc.rcp.IYRCRelatedTasksExtensionContributor;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCRelatedTask;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSSearchShipmentExtensionContributor implements
		IYRCRelatedTasksExtensionContributor {

	public boolean acceptTask(YRCEditorInput editorInput,
			YRCRelatedTask relatedTask) {
		boolean ret = true;

		Element eleinput = editorInput.getXml();
		Element eleShipment = YRCXmlUtils.getChildElement(eleinput, "Shipment");
		if(!YRCPlatformUI.isVoid(eleShipment)){
		
			Element elementShipment = YRCXmlUtils.getChildElement(eleShipment, "Shipment");
			boolean flag=KohlsCommonUtil.getUserList();
			if(flag==true)
			{
				if ("Print_Shipment_Pick_Slip".equals(relatedTask.getName())) {
					ret = false;
				}
				if ("Print Mass Multis".equals(relatedTask.getName())) {
					ret = false;
				}
				if ("Print_Shipment_Pack_Slip".equals(relatedTask.getName())) {
					ret = false;
				}
				if ("Pack_Shipment".equals(relatedTask.getName())) {
					ret = false;
				}
				if ("Reprint_Batch".equals(relatedTask.getName())) {
					ret = false;
				}
				if ("Batch_Print".equals(relatedTask.getName())) {
					ret = false;
				}
				if ("Change_Shipping_Address".equals(relatedTask.getName())) {
					ret = false;
				}
				if ("Print_Store_Pick_Slip".equals(relatedTask.getName())) {
					ret = false;
				}
				
			}
		Element eleShipNode = YRCXmlUtils
				.getChildElement(elementShipment, "ShipNode");
		if (!YRCPlatformUI.isVoid(eleShipNode.getAttribute("NodeType"))) {
			if (eleShipNode.getAttribute("NodeType").equals("STORE")) {

				if ("Print_Shipment_Pick_Slip".equals(relatedTask.getName())) {
					ret = false;
				}
				if ("Print Mass Multis".equals(relatedTask.getName())) {
					ret = false;
				}
			}
			else if(eleShipNode.getAttribute("NodeType").equals("RDC")){
				if ("Print_Store_Pick_Slip".equals(relatedTask.getName())) {
					ret = false;
				}
			}
		
		}
		}
		return ret;
	}

	public boolean canExecuteNewTask(YRCEditorInput arg0, YRCRelatedTask arg1) {

		return false;
	}

	public Composite createPartControl(Composite arg0, YRCEditorInput arg1,
			YRCRelatedTask arg2) {

		return null;
	}

}
