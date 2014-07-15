/*
 * Created on Sept 12th,2012
 *
 */
package com.kohls.ibm.ocf.pca.tasks.printSingles.wizardpages;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.IYRCTableImageProvider;
import com.yantra.yfc.rcp.IYRCViewerSorter;
import com.yantra.yfc.rcp.YRCCellModifier2;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTableBindingData;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.common.utils.YRCContainerUtils;

/**
 * @author kiran
 * 
 */
public class KOHLSMassSinglesPrintScreenHelper {

	private Object input;

	private KOHLSMassSinglesPrintScreen parent;

	private String formid;

	private KOHLSMassSinglesPrintScreenBehavior behavior;

	public KOHLSMassSinglesPrintScreenHelper(
			KOHLSMassSinglesPrintScreen parent, String FORM_ID, Object input) {
		this.parent = parent;
		formid = FORM_ID;
		this.input = input;
	}

	void setBehavior(KOHLSMassSinglesPrintScreenBehavior behavior) {
		this.behavior = behavior;
	}


	private void setHeaderTitle() {
		YRCContainerUtils.setHeaderMessage("Print_Pick_Ticket");

	}

	public boolean setFocus() {
	//	setHeaderTitle();
		return true;
	}



	

}

class KOHLSMassSinglesPrintScreenSorter extends IYRCViewerSorter {

	public KOHLSMassSinglesPrintScreenSorter() {
		super();
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		// TODO Auto-generated method stub
		return super.compare(viewer, e1, e2);
	}

	public void setSortingField(String sortingField) {
		// TODO Auto-generated method stub
	}

	public String getSortingField() {
		// TODO Auto-generated method stub
		return null;
	}

}


