package com.kohls.ibm.ocf.pca.tasks.packshipment.popups;

import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.YRCBehavior;

public class KOHLSWeightRequiredPopupBehavior extends YRCBehavior{

	private Element eleInput = null;
	private String formID = "";
	
	public KOHLSWeightRequiredPopupBehavior(Composite parent, String formID) {
		super(parent, formID);
		this.formID = formID;
		// TODO Auto-generated constructor stub
	} 

}