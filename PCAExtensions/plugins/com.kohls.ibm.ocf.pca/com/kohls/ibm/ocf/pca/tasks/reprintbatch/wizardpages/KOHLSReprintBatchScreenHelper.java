/*
 * Created on Sept 12th,2012
 *
 */
package com.kohls.ibm.ocf.pca.tasks.reprintbatch.wizardpages;

import org.eclipse.jface.viewers.Viewer;

import com.yantra.yfc.rcp.IYRCViewerSorter;
import com.yantra.yfc.rcp.common.utils.YRCContainerUtils;

/**
 * @author kiran
 * 
 */
public class KOHLSReprintBatchScreenHelper {

	private Object input;

	private KOHLSReprintBatchScreen parent;

	private String formid;

	private KOHLSReprintBatchScreenBehavior behavior;

	public KOHLSReprintBatchScreenHelper(
			KOHLSReprintBatchScreen parent, String FORM_ID, Object input) {
		this.parent = parent;
		formid = FORM_ID;
		this.input = input;
	}

	void setBehavior(KOHLSReprintBatchScreenBehavior behavior) {
		this.behavior = behavior;
	}


	private void setHeaderTitle() {
		YRCContainerUtils.setHeaderMessage("Reprint_Batch");

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


