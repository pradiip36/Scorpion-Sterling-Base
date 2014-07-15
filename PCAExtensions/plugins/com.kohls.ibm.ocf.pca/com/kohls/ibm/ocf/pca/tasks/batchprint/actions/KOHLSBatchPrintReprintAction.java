package com.kohls.ibm.ocf.pca.tasks.batchprint.actions;


import java.util.HashMap;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages.KOHLSBatchPrintReprintScreen;
import com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages.KOHLSBatchPrintScreen;
import com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages.KOHLSBatchPrintScreenBehavior;
import com.kohls.ibm.ocf.pca.tasks.reprintbatch.wizardpages.KOHLSReprintBatchScreen;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCDialog;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizard;


public class KOHLSBatchPrintReprintAction extends YRCAction{

	public static final String ACTION_ID="com.kohls.ibm.ocf.pca.tasks.batchprint.actions.KOHLSBatchPrintReprintAction";
	Element eleBatchPrint = null;
	YRCDialog oDialog=null;

	protected boolean checkForErrors() {
		return false;
	}

	protected boolean checkForModifications() {
		return false;
	}
	
	public KOHLSBatchPrintReprintAction(){
		super();
	}
	
	public void execute(IAction action) {
		Composite comp = YRCDesktopUI.getCurrentPage();
		IYRCComposite currentPage = null;
		if (comp instanceof YRCWizard) {
			YRCWizard wizard = (YRCWizard) YRCDesktopUI.getCurrentPage();
			currentPage = (IYRCComposite) wizard.getCurrentPage();
		}else if(comp instanceof IYRCComposite){
			currentPage = (IYRCComposite)comp;
		}else{
		
		}
		
		if(currentPage instanceof KOHLSBatchPrintScreen){
			Element input = ((KOHLSBatchPrintScreenBehavior)((KOHLSBatchPrintScreen)currentPage).getBehavior()).getReprintBatchModel();
			Element input1 = ((KOHLSBatchPrintScreenBehavior)((KOHLSBatchPrintScreen)currentPage).getBehavior()).getdeviceListModel();
			HashMap elehm = new HashMap();
			elehm.put("input", input);
			elehm.put("input1", input1);
			String strNodeType=KOHLSPCAConstants.A_NODE_TYPE;
			 if(!YRCPlatformUI.isVoid(input.getAttribute(strNodeType))){
					if(KOHLSPCAConstants.V_STORE.equals(input.getAttribute(strNodeType))) {
							
						KOHLSBatchPrintReprintScreen screen = new KOHLSBatchPrintReprintScreen(new
						 Shell(Display.getDefault()), SWT.NONE, elehm);
						 YRCDialog oDialog = new YRCDialog(screen,700,400,"Store Batch Print",null);
						 screen.setData("screen", oDialog);
						 oDialog.open();
					}
					else if(KOHLSPCAConstants.A_SINGLES_PRINT.equals(input.getAttribute(strNodeType))){
						KOHLSBatchPrintReprintScreen screen = new KOHLSBatchPrintReprintScreen(new
						 Shell(Display.getDefault()), SWT.NONE, elehm);
						 YRCDialog oDialog = new YRCDialog(screen,700,400,"RDC Singles Batch Print",null);
						 screen.setData("screen", oDialog);
						 oDialog.open();
					}
					else {
						KOHLSReprintBatchScreen kohlsReprintBatch = new KOHLSReprintBatchScreen(new
						 Shell(Display.getDefault()), SWT.NONE, elehm,input1);
						YRCDialog oDialog = new YRCDialog(kohlsReprintBatch,700,300,"Multi Batch Print",null);
						kohlsReprintBatch.setData("screen", oDialog);
						 oDialog.open();
						
				}
					
				}
		}
	}
	
}
