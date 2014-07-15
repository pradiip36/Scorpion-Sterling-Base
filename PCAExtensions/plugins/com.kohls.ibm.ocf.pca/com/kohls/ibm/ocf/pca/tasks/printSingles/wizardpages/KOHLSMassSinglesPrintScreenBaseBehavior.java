/**
 *  © Copyright  2009 Sterling Commerce, Inc.
 */
package com.kohls.ibm.ocf.pca.tasks.printSingles.wizardpages;

import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;

import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCWizardPageBehavior;
/**
 * @author Kiran Potnuru
 * Created on Sept 12,2010
 */
public abstract class KOHLSMassSinglesPrintScreenBaseBehavior  extends YRCWizardPageBehavior  {

	private Object input ;
	protected KOHLSMassSinglesPrintScreen parent;
	public static String WIZARD_ID = "com.kohls.ibm.ocf.pca.tasks.printSingles.wizards.KOHLSMassSinglesPrintWizard";  
	public KOHLSMassSinglesPrintScreenBaseBehavior(KOHLSMassSinglesPrintScreen parent, String FORM_ID, Object input) {
		super(parent);
		this.parent = parent;
		this.input = input;
	}
	public void initPage() {
		super.init();
		/*	
			Document inputXml = getDocument("changeShipment");
			callApi("changeShipment", inputXml);
		*/
		/*	
			Document inputXml = getDocument("getOrderList");
			callApi("getOrderList", inputXml);
		*/
		/*	
			Document inputXml = getDocument("getShipmentList");
			callApi("getShipmentList", inputXml);
		*/
		/*	
			Document inputXml = getDocument("getShipmentListForPrint");
			callApi("getShipmentListForPrint", inputXml);
		*/
		/*	
			Document inputXml = getDocument("getShipmentList_ForInit");
			callApi("getShipmentList_ForInit", inputXml);
		*/
		/*	
			Document inputXml = getDocument("getTotalRecordCount");
			callApi("getTotalRecordCount", inputXml);
		*/
		/*	
			Document inputXml = getDocument("raiseEvent");
			callApi("raiseEvent", inputXml);
		*/
	}
	
	public abstract void handleApiCompletion(YRCApiContext ctx);
	void callApi(String name, Document inputXml) {
		YRCApiContext context = new YRCApiContext();
		context.setApiName(name);
		context.setFormId(WIZARD_ID);
	    context.setInputXml(inputXml);
	    callApi(context);
	}

	void callApis(String[] names, Document[] inputXmls, HashMap userdata) {
		YRCApiContext context = new YRCApiContext();
		context.setApiNames(names);
		context.setFormId(WIZARD_ID);
	    context.setInputXmls(inputXmls);
	    if(userdata != null){
	    	String key, value;
	    	Iterator itr = userdata.keySet().iterator();
	    	while(itr.hasNext()){
	    		key = (String)itr.next();
	    		value = (String)userdata.get(key);
		 	   context.setUserData(key, value);
			}
	    }
	    callApi(context);
	} 
	public abstract void doPrint ();
		
	public abstract void performClose ();
		
}
