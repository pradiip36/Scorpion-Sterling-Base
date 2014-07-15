Ext.namespace('sc.sbc.kohls.item.eligibility');

sc.sbc.kohls.item.eligibility.selItemElgPopup = function(config) {
    sc.sbc.kohls.item.eligibility.selItemElgPopup.superclass.constructor.call(this, config);
}
Ext.extend(sc.sbc.kohls.item.eligibility.selItemElgPopup, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sbc.kohls.item.eligibility.selItemElgPopup',
    getUIConfig: sc.sbc.kohls.item.eligibility.selItemElgPopupUIConfig,
    namespaces: {
        target: ['itemElgPopupUIOut'],
        source: ['itemStatus_input','itemStatus_output']
    },
    namespacesDesc: {
        targetDesc: ['itemElgPopupUIOut'],
        sourceDesc: ['itemStatus_input','itemStatus_output']
    },
	
	
	initialize :function (){	
	
	 /*var apioutput = {};
	 apioutput.ItemList={};
	 apioutput.ItemList.Item =[];
	 apioutput.ItemList.Item[0] = {};
	 apioutput.ItemList.Item[0].ItemId = "STORE";	 
	 apioutput.ItemList.Item[1] = {};
	 apioutput.ItemList.Item[1].ItemId = "RDC";	 
	 
	 apioutput.ItemList.Item[2] = {};
	 apioutput.ItemList.Item[2].ItemId = "eFC";	 
	 
	 apioutput.ItemList.Item[3] = {};
	 apioutput.ItemList.Item[3].ItemId = "Other";
	 
	 this.setModel(apioutput,"itemStatus_input");*/
	seaAjaxUtils.request({
						action : "kohlsSetEligibleAsCommoncode",
						actionNS : sc.sbc.App.ItemStrutsNS,
						inputNS : "itemSearch_input",
						inputObj : '',
						success : this.setDropdownValue,					
						hideMask : true,
						scope : this
				});	
	},	
	setDropdownValue:function(res,options){
		 var apioutput = {};
		 apioutput.output={};
		 apioutput.output.CommonCodeList={};
		 apioutput.output.CommonCodeList.CommonCode =[];
		 apioutput.output.CommonCodeList.CommonCode=res.json.output.CommonCodeList.CommonCode;	 
		 this.setModel(apioutput,'itemStatus_output');	
	}
});
Ext.reg('xtype_name2', sc.sbc.kohls.item.eligibility.selItemElgPopup);