
/*
 * Created on Jun 07,2013
 *
 */
package com.kohls.ibm.ocf.pca.tasks.changeShippingAddress.wizardpages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.yantra.yfc.rcp.YRCLabelBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
/**
 * @author Bhaskar
 * @build # 200810210800
 * Copyright © 2005, 2006 Sterling Commerce, Inc. All Rights Reserved.
 */
public class KOHLSChangeShippingAddressHelper {

	private Object input;
	private KOHLSChangeShippingAddress parent;
	private String formid;
	private KOHLSChangeShippingAddressBehavior behavior;
		
	public KOHLSChangeShippingAddressHelper(KOHLSChangeShippingAddress parent, String FORM_ID, Object input) {
		this.parent = parent;
		formid = FORM_ID;
		this.input = input;
		// TODO Auto-generated constructor stub
	}
	
	void setBehavior(KOHLSChangeShippingAddressBehavior behavior){
		this.behavior = behavior;
	}

	void screenInitialized(){
		// TODO Auto-generated constructor stub
	}
	
	void bindingRegistered(){
		// TODO Auto-generated constructor stub
	}
	
	public boolean setFocus(){
		return true;
	}
	void setAdditionalLabelBindingAttributes(YRCLabelBindingData bindingdata, String ctrlName){
		        // TODO Auto-generated method stub
		 if(YRCPlatformUI.equals(ctrlName, "lblShipmentNo")){

		}
		
	}
	public int getStyle(String controlName) { 
		int style = SWT.NONE;
		return style;	
	}
		
	// Note: 
	// 1)Existing custom screen has to be embedded into this form (comp)  
	// 2)Custome code has to be written below each condition (if required
	public void embedExistingScreen(Composite comp,String controlName) {
	}
	
}
