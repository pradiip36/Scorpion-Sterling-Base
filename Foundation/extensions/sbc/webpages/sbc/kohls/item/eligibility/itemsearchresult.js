/*
 * Licensed Materials - Property of IBM
 * IBM Sterling Business Center
 * (C) Copyright IBM Corp. 2009, 2011 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
 
Ext.namespace('sc.sbc.kohls.item.eligibility');

sc.sbc.kohls.item.eligibility.ItemSearchResults = function(config) {

	config = config || {};
	var tempSearchInput=null;
	var ownerScreen = config.scOwnerScr;
	var singleSelect = false;
	if(ownerScreen && ownerScreen.hasOwnProperty("singleSelect")){
		singleSelect = config.scOwnerScr.singleSelect;
		Ext.apply(config, {singleSelect : singleSelect});
	}
	this.checkboxSelectionModel = null;
	if (singleSelect) {
		this.checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
					singleSelect : true,
					header : '',
					listeners:{
						scope : this,
						rowdeselect : this.tableDeselectHandler,
						beforerowselect: this.beforeRowSelect,
						rowselect : this.tableSelectHandler						
					}
				});
	} else {
		this.checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
			listeners:{
				scope : this,
				//rowdeselect : this.tableDeselectHandler,
				beforerowselect: this.beforeRowSelect,
				//rowselect : this.tableSelectHandler	,
				'selectionchange': this.selectionChangeHandler				
			}
		});
	}
	
	this.pagingToolbar = new sc.sbc.common.PagingToolbar();
	this.pagingToolbar.on('change', this.processAfterPageLoad, this);
	sc.sbc.kohls.item.eligibility.ItemSearchResults.superclass.constructor.call(this, config);
	var grid = this.find("sciId", "seaItemSearchResultsGrid")[0];
	
	if(ownerScreen && ownerScreen.isForPopup){
		
	}else{
		grid.getColumnModel().setHidden(0,true);
	}
	grid.addEvents('selectedmultiple','handlerowdblclick');
}
Ext.extend(sc.sbc.kohls.item.eligibility.ItemSearchResults, sc.plat.ui.ExtensibleScreen, {
			className : 'sc.sbc.kohls.item.eligibility.ItemSearchResults',
			getUIConfig : sc.sbc.kohls.item.eligibility.ItemSearchResultsUIConfig,
			namespaces : {
				source : ["itemSearch_output"],
				target : ["itemSearch_input"]
			},
			getUrl : function(){
				return sc.sbc.helper.AppHelper.getAjaxURL("searchitemaction4", "/sbc/item"); 
			},			
			performSearch : function(searchInput){
				
				searchInput.Item.ItemGroupCode = "PROD";
				searchInput.Item.CallingOrganizationCode = sc.sbc.core.context.JSContext.getCurrentOrganizationCode();
				this.model = searchInput;
				var extnVal=searchInput.Item.Extn;
				if (extnVal) {
					var dept = searchInput.Item.Extn.ExtnDept;
					var style = searchInput.Item.Extn.ExtnStyle;	
				}					
				if (searchInput.Item.PrimaryInformation) {
					var vendor = searchInput.Item.PrimaryInformation.PrimarySupplier;
				}
				var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
					var bulkButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"btnBulkUpdate");				
				if (dept || style || vendor) {					
					bulkButton.setDisabled(false);
				} else {
					bulkButton.setDisabled(true);
				}			
				
				var tblSearchResults = this.find("sciId", "seaItemSearchResultsGrid")[0];
				this.storesearchInput(searchInput);
				tblSearchResults.getStore().startPagination({itemSearch_input : Ext.encode(searchInput)});
			},
			handleItemSearchResult : function(res,options) {
				this.setModel(res.json,"itemSearch_output");
			},
			storesearchInput:function(searchInput){
				this.tempSearchInput=searchInput;
			},
			processAfterPageLoad : function(pagingToolBar, pagingData){
				if(this.scOwnerScr && this.scOwnerScr.isForPopup) {
					
				}else{
					if(pagingData.isFirstPage && pagingData.isFirstPage && pagingData.total === 1){
						this.find("sciId", "seaItemSearchResultsGrid")[0].getSelectionModel().selectFirstRow();
						this.openItemDetails();
					}else{
						var itemSearchField=this.ownerScr.find("sciId","txtItemSearchItemId")[0];
						itemSearchField.focus();
					}
				}
			},
			getUnitOfMeasureDesc : function(attrName, cell, rowData){
				return sc.sbc.common.cache.uom.UomManager.getUomDescriptionFromUom(attrName)
			},
			itemIDLink : function(attrName, cell, rowData) {
				return ("<a href= '#'>" + attrName +"</a>");
			},
			getFormattedDate: function(attributeValue){
				if (attributeValue && !(attributeValue instanceof Date)) {
					attributeValue = Date.parseDate(attributeValue, "Y-m-d");
				}
				if(attributeValue){
					return attributeValue.dateFormat(sc.plat.Userprefs.getDateFormat());
				}else{
					return "";
				}
			},
			itemdetails: function(res,options){
				eval(res.responseText);
			},
			getResultGrid: function(){
				return this.find("sciId", "seaItemSearchResultsGrid")[0];
			},
			beforeRowSelect: function(selectionModel, rowIndex, keepExisting, record){
				return this.isRowValid(record, rowIndex);
			},
			getRowClass: function(record, index){
					var data = record.json;
					if(!this.scope.isRowValid(record, index)){
						return 'row-invalid';
					}
			    	return '';
			},
			isRowValid : function(record, index){
				var apiInput = this.scOwnerScr.apiInput;
				if(apiInput && apiInput){
					var data = {};
					data.Item = {};
					data.Item = record.json;
					var ignoreForRowValidation = this.scOwnerScr.IgnoreForRowValidation;
					return itemUtils.isDataValid(data, apiInput, ignoreForRowValidation);
				}else {
					return true;
				}
			},
			tableDeselectHandler:function(selectionModel, rowIndex, record){
    			if(this.checkboxSelectionModel.getSelections().length > 1)
    			{
    				if(!this.model.searchInput.Item.Extn.ExtnDept){
				var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
				var bulkButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"btnBulkUpdate");
				bulkButton.setDisabled(false);
    			}
    			}
		    },
		    tableSelectHandler:function(selectionModel, rowIndex, record){
				var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
				var bulkButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"btnBulkUpdate");
				bulkButton.setDisabled(true);
	    },
		selectionChangeHandler : function(sel){
			var gridResults=this.find("sciId","seaItemSearchResultsGrid")[0];
			var tBar=gridResults.getTopToolbar();
			bttn=tBar.items.itemAt(1);
			var len=this.checkboxSelectionModel.getSelections().length;			
			//if(len >= 1 || len===0){
			if(len >= 1){
			//bttn.setDisabled(true);
			bttn.setDisabled(false);
			}else{
			bttn.setDisabled(true);
				//bttn.setDisabled(false);
			}
		},
			
			bukUpdateHandler : function(grid, rowIndex, columnIndex, e){
				
				this.confirmationPopup = new sc.sbc.kohls.item.eligibility.itemElgPopup();
                this.confirmationPopup.initialize();				
				var spopupTitle = "Select Eligibility";
				
				this.addAssociationWindow1 = sc.sbc.util.WindowUtils.getWindow({
					screen : this.confirmationPopup,					
					windowConfig : {
						closeAction : "close",
						maximizable : false,
						title: spopupTitle
					},					
					buttons : sc.sbc.util.WindowUtils.SAVECLOSE,
					handlers : {"save" : this.selectButtonAction},
					scope : this,
					parent: this
					});
				this.addAssociationWindow1.show();
				//Set default value to STORE				
				this.confirmationPopup.items.itemAt(0).items.items[1].setValue('STORE');
			},
			selectButtonAction:function() {		
				
				var mywin=this.addAssociationWindow1;
				
				var modelFromScreen=sc.plat.DataManager.getTargetModel("itemElgPopupUIOut");				
				var storeValueName=modelFromScreen[0].ItemList.Item;
				var chkVal=modelFromScreen[0].checkbox6;
				
				var searchInput = {};
				searchInput.ItemEligibility = {};				
							
				/*searchInput.ItemEligibility.Department="MyOwnDpet";
				searchInput.ItemEligibility.ClassID="MyOwnClass";
				searchInput.ItemEligibility.SubClass="MySubClass";*/
				
				if (this.tempSearchInput.Item.Extn) {
					searchInput.ItemEligibility.CatSubClass=this.tempSearchInput.Item.Extn.ExtnSubClass;
					searchInput.ItemEligibility.CatClass=this.tempSearchInput.Item.Extn.ExtnClass;
					searchInput.ItemEligibility.CatDepartment=this.tempSearchInput.Item.Extn.ExtnDept;
					searchInput.ItemEligibility.Style=this.tempSearchInput.Item.Extn.ExtnStyle;
				}
				if (this.tempSearchInput.Item.PrimaryInformation) {
					searchInput.ItemEligibility.VendorId=this.tempSearchInput.Item.PrimaryInformation.PrimarySupplier;
				}
				searchInput.ItemEligibility.SetEligibleAs=storeValueName;
				searchInput.ItemEligibility.ApplyNewItem=chkVal;
				
				var obj={};					
				obj["itemStatus_input"] = Ext.encode(searchInput);					
				
				seaAjaxUtils.request({						
					action : "bulkItemEligibility",
					actionNS : sc.sbc.App.ItemStrutsNS,										
					params : obj,						
					success : this.itemdetails,
					scope : this
				});
								
				mywin.destroy();
    	
		},
		selUpdBttnHandler:function(){
				
				this.confirmationPopup = new sc.sbc.kohls.item.eligibility.selItemElgPopup();
                this.confirmationPopup.initialize();				
				var spopupTitle = "Select Eligibility";
				
				this.addAssociationWindow2 = sc.sbc.util.WindowUtils.getWindow({
					screen : this.confirmationPopup,					
					windowConfig : {
						closeAction : "close",
						maximizable : false,
						title: spopupTitle
					},					
					buttons : sc.sbc.util.WindowUtils.SAVECLOSE,
					handlers : {"save" : this.selectButtonAction2},
					scope : this,
					parent: this
					});
				this.addAssociationWindow2.show();	
				//Set default value to STORE				
				this.confirmationPopup.items.itemAt(0).items.items[1].setValue('STORE');				
		},
		selectButtonAction2:function(){
				
				var mywin=this.addAssociationWindow2;
				
				var modelFromScreen=sc.plat.DataManager.getTargetModel("itemElgPopupUIOut");				
				var storeValueName=modelFromScreen[0].ItemList.Item;
				var chkVal=modelFromScreen[0].checkbox6;
				
				var selectedItems = this.find("sciId", "seaItemSearchResultsGrid")[0].getSelectionModel().getSelections();
		    	var selectedItemList = {};
		    	selectedItemList.ItemList = {};
		    	selectedItemList.ItemList.Item = [];
		    	for(var i=0; i<selectedItems.length; i++){
		    		selectedItemList.ItemList.Item[i] = selectedItems[i].json;
					if (selectedItemList.ItemList.Item[i].Extn) {					
						selectedItemList.ItemList.Item[i].Extn.ExtnShipNodeSource=storeValueName;
					} else {
						selectedItemList.ItemList.Item[i].Extn={};
						selectedItemList.ItemList.Item[i].Extn.ExtnShipNodeSource=storeValueName;
					}
		    	}
			    	var obj = {};
			    	//obj["itemdetailsNS"] = Ext.encode(itemDetails);
					obj["itemStatus_input"] = Ext.encode(selectedItemList);
					
				seaAjaxUtils.request({
						action : "selectedItemEligibility",
						actionNS : sc.sbc.App.ItemStrutsNS,
						params : obj,						
						success : this.itemdetails,
						scope : this
					});
				mywin.destroy();
		}
			
		});
Ext.reg('seaItemSearchResults', sc.sbc.kohls.item.eligibility.ItemSearchResults);
