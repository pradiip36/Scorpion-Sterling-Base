/*
 * Licensed Materials - Property of IBM
 * IBM Sterling Business Center
 * (C) Copyright IBM Corp. 2009, 2011 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
 
Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.ItemSearch = function(config) {
	config = config || {};
	Ext.applyIf(config, {
                        sciId : "itemSearch"
                  });
	sc.sbc.kohls.item.ineligibility.ItemSearch.superclass.constructor.call(this, config);
}
Ext.extend(sc.sbc.kohls.item.ineligibility.ItemSearch, sc.sbc.common.search.sbcsearch, {
		getScreenContext : function() {
			return "Item";
	    },
	    getResultsScreenXtype : function() {
	    	return "seaItemSearchResults";
	    },
	    basicSearch : {
				getButtonText : function() {
					return this.b_BasicItemSearchTitle;
				},
				getTitle : function() {
					return "Set Ineligible Items for Store";
				},
				getViewID : function() {
					return "BASIC_ITEM_SEARCH";
				},
				getXType : function() {
					return "seaBasicItemSearch3";
				}
			},
			isSavedSearchRequired : function() {
				return false;
			},
			getSortingOrder : function() {
				var sortByComboLbl = sc.sbc.util.CoreUtils.getBottomToolBarItemBySciId(this.getPnlHolder(), "lblOrderBy");
				sortByComboLbl.setVisible(false);
		    	var sortByCombo = sc.sbc.util.CoreUtils.getBottomToolBarItemBySciId(this.getPnlHolder(), "cmbAttrib");
		    	sortByCombo.setVisible(false);
			}
});
Ext.reg('seaItemSearch3', sc.sbc.kohls.item.ineligibility.ItemSearch);
