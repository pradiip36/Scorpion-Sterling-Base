package com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizardpages;

import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSXMLFileWriter;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.SetterOnlyReflection;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizardPageBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSPickSlipsBulkPrintPopupBehavior extends YRCWizardPageBehavior {
	private Object input ;
	private Element outputModel = null ;
	protected KOHLSPickSlipsBulkPrintPopup parent;
	protected KOHLSStorePickSlipPrintPageBehavior parentWizPageBehav;
	
	public KOHLSPickSlipsBulkPrintPopupBehavior(Composite arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public KOHLSPickSlipsBulkPrintPopupBehavior(KOHLSPickSlipsBulkPrintPopup parent, String FORM_ID, Object input, YRCWizardPageBehavior parentPageBehav) {
		super(parent);
		this.parent = parent;
		this.input = input;
		outputModel = YRCXmlUtils.createDocument("PickSlipBulkPrintDataModel").getDocumentElement();
		if(parentPageBehav instanceof KOHLSStorePickSlipPrintPageBehavior){
			parentWizPageBehav = (KOHLSStorePickSlipPrintPageBehavior)parentPageBehav;
		}
	}
	
	public void initPage() {
		super.init();
	}
	
	public void validateInput(){
		String strQtyToPrint = parent.getQtyToPrint();
		if(isValid(strQtyToPrint)){
			YRCXmlUtils.setAttribute(outputModel, "QtyToPrint", strQtyToPrint);
			parentWizPageBehav.performBulkPrint(outputModel);
		}
	}
	
	public boolean isValid(String strQtyToPrint){
			try{
				Integer IntQtyToPrint = Integer.parseInt(strQtyToPrint);
				Integer IntMaxQtyToPrint = Integer.parseInt(YRCPlatformUI.getString("Max_Qty_For_Pick_Slip_Print"));
				
				int intQtyToPrint = IntQtyToPrint.intValue();
				int intMaxQtyToPrint = IntMaxQtyToPrint.intValue();
				
				if(intQtyToPrint<=0 || intQtyToPrint>intMaxQtyToPrint){
					YRCPlatformUI.showInformation("Invalid Data", "Please Enter a Qty between 1 to " 
							+ YRCPlatformUI.getString("Max_Qty_For_Pick_Slip_Print"));
					return false;
				}
				return true;
			}
			catch(NumberFormatException e ){
				YRCPlatformUI.showInformation("Invalid Data", "Please Enter a valid Qty");				
				return false;
			}
		}
	
	
}
