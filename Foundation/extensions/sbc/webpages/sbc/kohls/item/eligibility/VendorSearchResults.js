Ext.namespace('sc.sbc.kohls.item.eligibility');

sc.sbc.kohls.item.eligibility.VendorSearchResults = function(config) {
	var ownerScreen = config.scOwnerScr;
	//this.parentscrn = config.parentscrn;
	
	//this.pagingToolbar = new sc.sbc.common.PagingToolbar({listeners : {'change' : this.handleCellClick, scope : this}}),
    sc.sbc.kohls.item.eligibility.VendorSearchResults.superclass.constructor.call(this, config);
}
Ext.extend(sc.sbc.kohls.item.eligibility.VendorSearchResults, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sbc.kohls.item.eligibility.VendorSearchResults',
    getUIConfig: sc.sbc.kohls.item.eligibility.VendorSearchResultsUIConfig,
    namespaces: {
        target: ["getVendorListInput"],
        source: ["getVendorListOutput"]
    },
    namespacesDesc: {
        targetDesc: [''],
        sourceDesc: ['']
    },
	getUrl : function() {
		return sc.sbc.helper.AppHelper.getAjaxURL("getVendorList", "/sbc/item");
	},
	performSearch : function(searchInput) {
		//this.pagingToolbar.on('beforerefresh', this.beforePageRefresh, this);

		var input4srch = Ext.decode(Ext.encode(searchInput));
		var tblSearchResults = this.find("sciId", "tblSearchResults")[0];
	
		seaAjaxUtils.request({
				action : "getVendorList",
				actionNS : sc.sbc.App.ItemStrutsNS,
				inputNS : "getVendorListInput",
				inputObj : searchInput,
				success : this.handleVendorSearchResult,
				hideMask : true,
				scope : this
		});
		
		//tblSearchResults.getStore().startPagination({getVendorListInput : Ext.encode(input4srch)});

	},
	handleVendorSearchResult : function(res,options)
	{
		this.setModel(res.json,"getVendorListOutput");
	},
	getResultGrid : function() {
		return this.find("sciId", "tblSearchResults")[0];
	},

	beforePageRefresh : function(store, records) {
	},

	openApprovalRuleDetails : function(approvalRuleObj) {
		seaAjaxUtils.request({
				actionNS : sc.sbc.App.PricingStrutsNS,
				action : "openApprovalRuleDetails",
				params	: {"getApprovalRuleDetails" : Ext.encode(approvalRuleObj)},
				success : function(res, options) {
								eval(res.responseText);
						  },
				scope : this
			});
	},

	handleCellClick : function(grid, rowIndex, columnIndex, e) {
		grid = this.find("sciId", "tblSearchResults")[0];
				
		var vendorID = grid.getStore().data.itemAt(rowIndex).json.OrganizationCode;
		var myVendorID = vendorID;
		var winObj = this.ownerScr.getParent();
		//winObj.find("sciId", "triggerItemSearchVendor")[0].value = vendorID;
		var winVendorObj = sc.sbc.core.data.DataManager.getData("winVendor");
		//winVendorObj.setVendorID = vendorID;
		//sc.sbc.core.data.DataManager.setData("winVendor",winVendorObj);
		//winVendorObj.find("sciId","triggerItemSearchVendor")[0].value = vendorID;
		//var searchScr = winVendorObj.parent.ownerCt;
		var basicSearchPanel = winObj.find("sciId", "pnlBasicItemSearchMain")[0];
		var searchPanel = basicSearchPanel.find("sciId", "pnlBasicSearch")[0];
		var triggerobj = searchPanel.find("sciId", "triggerItemSearchVendor")[0];
		triggerobj.setValue(vendorID);
		this.closeHandler(winVendorObj);
		//this.openApprovalRuleDetails({OrderApprovalRule : {OrderApprovalRuleKey : approvalRuleKey}});

	},
	showLinksWithImageInColumns:function(value,data,record){
		return sc.sbc.util.RendererUtils.showLinksWithImageInColumns(value,data,record);
	},
	closeHandler:function(obj){
		obj.close();
	}


	
});
Ext.reg('seaVendorSearchResults', sc.sbc.kohls.item.eligibility.VendorSearchResults);