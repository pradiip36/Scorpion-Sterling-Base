/*
 * Licensed Materials - Property of IBM
 * IBM Sterling Business Center
 * (C) Copyright IBM Corp. 2010, 2011 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
 
Ext.namespace('sc.sbc.kohls.item.eligibility');

sc.sbc.kohls.item.eligibility.VendorSearch = function(config) {
	config = config || {};
	Ext.applyIf(config, {
				sciId : "VendorSearch"
			});
	sc.sbc.kohls.item.eligibility.VendorSearch.superclass.constructor.call(this, config);
}
Ext.extend(sc.sbc.kohls.item.eligibility.VendorSearch, sc.sbc.common.search.sbcsearch, {
		getScreenContext : function() {
			return "Vendor";
	    },
	    getResultsScreenXtype : function() {
	    	return "seaVendorSearchResults";
	    },
	    basicSearch : {
				getTitle : function() {
					return "Find Vendor";
				},
				getViewID : function() {
					return "BASIC_VENDOR_SEARCH";
				},
				getXType : function() {
					return "seaBasicVendorSearch";
				}
	    },
	setParent : function (scr) { 
		this.parentScr = scr;
	},
	getParent : function(){
		return this.parentScr;
	},
	isSavedSearchRequired : function() {
		return false;
	},
	getSortingOrder : function() {
		var pnlHolder = this.find("sciId","seapnlholder")[0];
		var sortByComboLbl = sc.sbc.util.CoreUtils.getBottomToolBarItemBySciId(pnlHolder, "lblOrderBy");
		sortByComboLbl.setVisible(false);
    	var sortByCombo = sc.sbc.util.CoreUtils.getBottomToolBarItemBySciId(pnlHolder, "cmbAttrib");
    	sortByCombo.setVisible(false);
	}
});
Ext.reg('seaVendorSearch', sc.sbc.kohls.item.eligibility.VendorSearch);
