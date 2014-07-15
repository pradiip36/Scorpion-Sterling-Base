/*
 * Licensed Materials - Property of IBM
 * IBM Sterling Business Center
 * (C) Copyright IBM Corp. 2009, 2011 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.BasicItemSearch = function(config) {
	config = config || {};
	if (config.scOwnerScr
			&& config.scOwnerScr.hideSearchServiceOnlyOption == false) {
		this.hideSearchServiceOnlyOption = false;
	}
	sc.sbc.kohls.item.ineligibility.BasicItemSearch.superclass.constructor
			.call(this, config);
	
}
Ext.extend(
				sc.sbc.kohls.item.ineligibility.BasicItemSearch,
				sc.plat.ui.ExtensibleScreen,
				{
					className : 'sc.sbc.kohls.item.ineligibility.BasicItemSearch',
					namespaces : {
						source : [ "itemSearch_output", "SavedSearch",
								"ItemTypeList_output" ],
						target : [ "itemSearch_input", "itemDetails_input" ]
					},

					namespacesDesc : {

						sourceDesc : [
								"Not used",
								"This namespace contains the saved search model used to populate saved searches.",
								"This namespace contains the model used to populate item types",
								"This namespace contains the model used to populate service types" ],

						targetDesc : [
								"This namespace contains the input to getItemList API get the search results",
								"Not used" ]

					},
					getUIConfig : sc.sbc.kohls.item.ineligibility.BasicItemSearchUIConfig,
					hideSearchServiceOnlyOption : true,
					getScreenTargetModel : function(config) {
						var searchInput = this
								.getTargetModel("itemSearch_input")[0];

						var categoryTrigger = this.find("sciId",
								"triggerItemSearchCategory")[0];
						if (categoryTrigger && !categoryTrigger.getValue()) {
							searchInput.Item.CategoryFilter = {};
						}
				
						if (searchInput.Item && this.scOwnerScr.apiInput
								&& this.scOwnerScr.apiInput.Item) {
							sc.sbc.util.JsonUtils.mergeObjects(
									searchInput.Item,
									this.scOwnerScr.apiInput.Item);
						}
					
						var catFullPath = searchInput.Item.CategoryFilter.CategoryPath;

						if (catFullPath) {
							var catFullPathNew = catFullPath.substring(1,
									catFullPath.length);

							var ind = catFullPathNew.indexOf("/");
							var catPath = catFullPathNew.substring(ind + 1,
									catFullPath.length);

							var dept = catPath.substring(0, catPath
									.indexOf("/"));
							if(dept){
							searchInput.Item.Extn.ExtnDept = dept;
							ind = catPath.indexOf("/");
							catPath = catPath
									.substring(ind + 1, catPath.length);

							var classid = catPath.substring(0, catPath
									.indexOf("/"));
							if (classid) {
								var indOfClass = classid.indexOf("-");
								var actualClassID = classid.substring(
										indOfClass + 1, classid.length);
								searchInput.Item.Extn.ExtnClass = actualClassID;
							}
							else
							{
								var indOfClass = catPath.indexOf("-");
								var actualClassID = catPath.substring(
										indOfClass + 1, catPath.length);
								searchInput.Item.Extn.ExtnClass = actualClassID;
							}

							ind = catPath.indexOf("/");
							
							if(ind > 0){
							catPath = catPath
									.substring(ind + 1, catPath.length);

							var subclass = catPath.substring(0, catPath.length);
							if (subclass) {
								var indOfSubClass = subclass.lastIndexOf("-");
								var actualSubClass = subclass.substring(
										indOfSubClass + 1, subclass.length);
								searchInput.Item.Extn.ExtnSubClass = actualSubClass;
							}
							}
							

							ind = catPath.indexOf("/");

							catPath = catPath
									.substring(ind + 1, catPath.length);

							var subclass = catPath.substring(0, catPath.length);
							if (subclass) {
								var indOfSubClass = subclass.lastIndexOf("-");
								var actualSubClass = subclass.substring(
										indOfSubClass + 1, subclass.length);
								searchInput.Item.Extn.ExtnSubClass = actualSubClass;
							}
							}
							else{
								searchInput.Item.Extn.ExtnDept = catPath;
							}

					
						}

						var style = searchInput.Item.Extn.ExtnStyle;
						if (style) {
							searchInput.Item.Extn.ExtnStyleQryType = "LIKE";
						}

						searchInput.Item.CategoryFilter.CategoryPath=null;
						searchInput.Item.CategoryFilter=null;
						
						return searchInput;
					},
					paintSavedSearch : function(searchData) {

					},
					selectCategory : function() {
						sc.sbc.util.LoadEntityUtils
								.loadSellingCatalogTreeInPopup(
										this.scope.handleCategoryResult,
										this.scope);
					},
					initialize : function() {
						var searchCriteria = {};
						searchCriteria.SearchWrapper = {};
						searchCriteria.SearchWrapper.Item = {};
						if (this.scOwnerScr && this.scOwnerScr.SearchInput) {
							sc.sbc.util.JsonUtils.mergeObjects(
									searchCriteria.SearchWrapper.Item,
									this.scOwnerScr.SearchInput.Item);
						}
						if (this.scOwnerScr && this.scOwnerScr.apiInput
								&& this.scOwnerScr.apiInput.Item) {
							// Merging the jsons to populate in the UI.
							sc.sbc.util.JsonUtils.mergeObjects(
									searchCriteria.SearchWrapper.Item,
									this.scOwnerScr.apiInput.Item);

							var itemInput = this.scOwnerScr.apiInput.Item;
							if (this.scOwnerScr.apiInput.Item.OnlyIncludeMyCatalog === "Y"
									|| this.scOwnerScr.apiInput.Item.OnlyIncludeMyCatalog === "N") {
								var chkItemSearchCatalog = this.find("sciId",
										"chkItemSearchCatalog")[0];
								chkItemSearchCatalog.disable();
							}

							if (itemInput.CategoryFilter
									&& itemInput.CategoryFilter.CategoryPath
									&& itemInput.CategoryFilter.CategoryPathQryType === "NE") {
								var categoryTrigger = this.find("sciId",
										"triggerItemSearchCategory")[0];
								categoryTrigger.disable();

								searchCriteria.SearchWrapper.Item.CategoryFilter.CategoryPath = "";
							}

						}

						this.setModel(itemManager.getItemTypeList(),
								"ItemTypeList_output", {
									clearOldVals : true
								});
						this.setModel(searchCriteria, "SavedSearch", {
							clearOldVals : true
						});
					
					},
					handleCategoryResult : function(categoryArray) {
						var categoryElem = categoryArray[0];
						var categoryTrigger = this.find("sciId",
								"triggerItemSearchCategory")[0];
						categoryTrigger.setValue(categoryElem.ShortDescription);

						var categoryPathField = this.find("sciId",
								"sbcCategoryPath")[0];
						categoryPathField.setValue(categoryElem.CategoryPath);
					},

					bulkUpdateFn : function() {
						Ext.MessageBox
								.show({
									title : 'Bulk Update?',
									msg : 'You are about to Bulk Update the searched result. <br />Would you like to continue?',
									buttons : Ext.MessageBox.YESNO,
									fn : this.showResultText,
									animEl : 'mb4',
									icon : Ext.MessageBox.QUESTION
								});
						/*
						 * var searchInput = this.getScreenTargetModel();
						 * if(searchInput) { var pnlSearchResults =
						 * this.find("sciId", "seapnlsearchresults")[0];
						 * pnlSearchResults.setVisible(true);
						 * pnlSearchResults.doLayout(); var resultsScreen =
						 * this.find("sciId", "seacstmresults")[0];
						 * resultsScreen.on('onsearch', this.collapseHeader,
						 * this); resultsScreen.doLayout();
						 * this.clearSortingInfo(resultsScreen);
						 * resultsScreen.performSearch(searchInput); }
						 */
					},

					showResultText : function(btn, text) {
						// Ext.msg('Button Click', 'You clicked the {0} button
						// and entered the text "{1}".', btn, text);
					},
					 selectVendor: function(){
					    	
							var vendorPopUp = new sc.sbc.kohls.item.ineligibility.VendorSearch();
							vendorPopUp.setParent(sc.sbc.core.context.JSContext.getCurrentScreen());
							var winVendor = sc.sbc.util.WindowUtils.getWindow({
							                              screen : vendorPopUp,
							                               buttons : sc.sbc.util.WindowUtils.CLOSE,
										                   scope : vendorPopUp,
										                   parentscrn: this,
							                              windowConfig:{maximizable : false}
							                              
					                        });
							//winVendor.setParent(this);
							sc.sbc.core.data.DataManager.setData("winVendor",winVendor);
					            winVendor.parent=this;
								 winVendor.show();
						                 				


					   // var callBack = sc.sbc.core.data.DataManager.getData("winVendor");
						}

				});
Ext.reg('seaBasicItemSearch3', sc.sbc.kohls.item.ineligibility.BasicItemSearch);
