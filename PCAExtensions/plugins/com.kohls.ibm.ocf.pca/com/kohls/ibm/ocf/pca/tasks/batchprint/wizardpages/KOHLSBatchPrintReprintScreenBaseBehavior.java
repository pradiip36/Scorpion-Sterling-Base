
/*
 * Created on Jun 09,2013
 *
 */
package com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages;

import com.yantra.yfc.rcp.YRCWizardPageBehavior;
import java.util.Iterator;
import java.util.HashMap;
import org.w3c.dom.Document;
import com.yantra.yfc.rcp.YRCApiContext;
/**
 * @author srikaheb
 * @build # 200810210800
 * Copyright © 2005, 2006 Sterling Commerce, Inc. All Rights Reserved.
 */
public abstract class KOHLSBatchPrintReprintScreenBaseBehavior extends YRCWizardPageBehavior {

	private Object input ;
	protected KOHLSBatchPrintReprintScreen parent;
	public static String WIZARD_ID = "com.kohls.ibm.ocf.pca.tasks.batchprint.wizards.KOHLSBatchPrintReprintWizard";  
	public KOHLSBatchPrintReprintScreenBaseBehavior(KOHLSBatchPrintReprintScreen parent, String FORM_ID, Object input) {
		super(parent);
		this.parent = parent;
		this.input = input;
	}
	public void initPage() {
		super.init();
	}
	
	public abstract Document getDocument(String apiName);
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
	public abstract void doBatchReprint();
}
