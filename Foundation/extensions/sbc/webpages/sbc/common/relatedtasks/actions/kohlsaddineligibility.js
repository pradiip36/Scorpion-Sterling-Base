/*
 * Licensed Materials - Property of IBM
 * IBM Sterling Business Center
 * (C) Copyright IBM Corp. 2009, 2011 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
 
Ext.namespace("sc.sbc.common.relatedtask.actions");

sc.sbc.common.relatedtask.actions.KohlsItemSearchAction = new Ext.Action({
	handler : function(screen){
			//sc.sbc.helper.AppHelper.loadEntity("itemsearch", sc.sbc.App.ItemStrutsNS);
				seaAjaxUtils.request({
				actionNS : sc.sbc.App.ItemStrutsNS,
				action : "kohlsItemineligibilityHome",
				scope : this,
				success : function(res, options) {
								eval(res.responseText);
						  }
			});
		}
	}
);

sc.sbc.common.relatedtask.RelatedTaskActionMgr.registerAction("sbckohlsaddineligibilityaction", sc.sbc.common.relatedtask.actions.KohlsItemSearchAction);
