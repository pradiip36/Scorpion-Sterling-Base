Ext.namespace('sc.sbc.kohls.item.eligibility');

sc.sbc.kohls.item.eligibility.BasicVendorSearch = function(config) {
    sc.sbc.kohls.item.eligibility.BasicVendorSearch.superclass.constructor.call(this, config);
}
Ext.extend(sc.sbc.kohls.item.eligibility.BasicVendorSearch, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sbc.kohls.item.eligibility.BasicVendorSearch',
    getUIConfig: sc.sbc.kohls.item.eligibility.BasicVendorSearchUIConfig,
    namespaces: {
        target: ["getVendorListInput"],
        source: ["getVendorListOutput"]
    },
    namespacesDesc: {
        targetDesc: ["Target model for the criteria screen"],
        sourceDesc: ["Contains the search criteria when a vendor saved search is launched"]
    },
    getScreenTargetModel : function(config) {
    	var vendorSearchInput = this.getTargetModel("getVendorListInput")[0];
    	vendorSearchInput.Organization.OrganizationCodeQryType = "LIKE";
    	vendorSearchInput.Organization.OrganizationNameQryType = "LIKE";
    	return vendorSearchInput;
    }


});
Ext.reg('seaBasicVendorSearch', sc.sbc.kohls.item.eligibility.BasicVendorSearch);