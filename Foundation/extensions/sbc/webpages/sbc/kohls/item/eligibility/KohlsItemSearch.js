Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.KohlsItemSearch = function(config) {
    sc.sbc.kohls.item.ineligibility.KohlsItemSearch.superclass.constructor.call(this, config);
}
Ext.extend(sc.sbc.kohls.item.ineligibility.KohlsItemSearch, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sbc.kohls.item.ineligibility.KohlsItemSearch',
    getUIConfig: sc.sbc.kohls.item.ineligibility.KohlsItemSearchUIConfig,
    namespaces: {
        target: ["SavedSearch"],
        source: ["itemSearch_input"]
    },
    namespacesDesc: {
        targetDesc: [],
        sourceDesc: []
    },
	search:function(){

var searchInput = this.getTargetModel("itemSearch_input")[0];

var itemIdField = this.find("sciId", "txtSKU")[0];
var itemId = itemIdField.getValue();
var itemStyleField = this.find("sciId", "txtStyle")[0];
var itemStyle = itemStyleField.getValue();
var obj = {};
var item = {};
if(itemId){
			item.Item={};
			item.ItemID = itemId;
			item.ItemIDQryType = "LIKE";
			alert(Ext.encode(item));
}
if(itemStyle){
		item.extn={};
		item.extn.ExtnStyle=itemStyle;
		item.extn.StyleQryType = "LIKE"
		alert(Ext.encode(item));
}
			obj["itemSearch_input"] = Ext.encode(item);
						
						seaAjaxUtils.request({
							action : "kohlsitemsearch",
							actionNS : sc.sbc.App.ItemStrutsNS,
							params : obj,
							success : this.handleItemSearchResult,
							hideMask : true,
							scope : this
						});
		},
handleItemSearchResult : function(res, options){
alert("result");
    	},
	selectCategory : function() {
    	sc.sbc.util.LoadEntityUtils.loadSellingCatalogTreeInPopup(this.scope.handleCategoryResult,this.scope);
	}
});
Ext.reg('seaKohlsItemSearch', sc.sbc.kohls.item.ineligibility.KohlsItemSearch);