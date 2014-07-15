/*
 * Created on Sept 12th,2012
 *
 */
package com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionEvent;

import com.yantra.yfc.rcp.IYRCViewerSorter;
import com.yantra.yfc.rcp.common.utils.YRCContainerUtils;

/**
 * @author srikanth
 * 
 */
public class KOHLSBatchPrintScreenHelper {

	private Object input;

	private KOHLSBatchPrintScreen parent;

	private String formid;

	private KOHLSBatchPrintScreenBehavior behavior;

	public KOHLSBatchPrintScreenHelper(
			KOHLSBatchPrintScreen parent, String FORM_ID, Object input) {
		this.parent = parent;
		formid = FORM_ID;
		this.input = input;
	}

	void setBehavior(KOHLSBatchPrintScreenBehavior behavior) {
		this.behavior = behavior;
	}


	private void setHeaderTitle() {
		YRCContainerUtils.setHeaderMessage("Batch_Print");

	}

	public boolean setFocus() {
	//	setHeaderTitle();
		return true;
	}

	public void widgetSelected(SelectionEvent e, String string) {
		// TODO Auto-generated method stub
		
	}

	public void widgetDefaultSelected(SelectionEvent e, String string) {
		// TODO Auto-generated method stub
		
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


