/*
 * Licensed Materials - Property of IBM
 * IBM Sterling Business Center
 * (C) Copyright IBM Corp. 2009, 2011 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
 
Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.ItemSearchResults = function(config) {

	config = config || {};
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
				rowdeselect : this.tableDeselectHandler,
				beforerowselect: this.beforeRowSelect,
				rowselect : this.tableSelectHandler			          
			}
		});
	}
	
	this.pagingToolbar = new sc.sbc.common.PagingToolbar();
	this.pagingToolbar.on('change', this.processAfterPageLoad, this);
	sc.sbc.kohls.item.ineligibility.ItemSearchResults.superclass.constructor.call(this, config);
	var grid = this.find("sciId", "seaItemSearchResultsGrid")[0];
	
	if(ownerScreen && ownerScreen.isForPopup){
		
	}else{
		grid.getColumnModel().setHidden(0,true);
	}
	grid.addEvents('selectedmultiple','handlerowdblclick');
}
Ext.extend(sc.sbc.kohls.item.ineligibility.ItemSearchResults, sc.plat.ui.ExtensibleScreen, {
			className : 'sc.sbc.kohls.item.ineligibility.ItemSearchResults',
			getUIConfig : sc.sbc.kohls.item.ineligibility.ItemSearchResultsUIConfig,
			namespaces : {
				source : ["itemSearch_output"],
				target : ["itemSearch_input"]
			},
			getUrl : function(){
				return sc.sbc.helper.AppHelper.getAjaxURL("searchitemaction3", "/sbc/item"); 
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
				var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
				var bulkButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"btnBulkUpdate");
				bulkButton.setDisabled(true);
				if (searchInput.Item.PrimaryInformation) {
					var vendor = searchInput.Item.PrimaryInformation.PrimarySupplier;
					
				}
				if(vendor)
				{
				bulkButton.setDisabled(true);
				}
				else if(dept)
				{
					var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
					bulkButton.setDisabled(false);
				}
				else if(style)
				{
					var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
					bulkButton.setDisabled(false);
				}
					
				var tblSearchResults = this.find("sciId", "seaItemSearchResultsGrid")[0];
				tblSearchResults.getStore().startPagination({itemSearch_input : Ext.encode(searchInput)});
			},
			processAfterPageLoad : function(pagingToolBar, pagingData){
				var itemSearchField=this.ownerScr.find("sciId","txtItemSearchItemId")[0];
				itemSearchField.focus();
	
			},
			getUnitOfMeasureDesc : function(attrName, cell, rowData){
				return sc.sbc.common.cache.uom.UomManager.getUomDescriptionFromUom(attrName)
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
    			if(this.checkboxSelectionModel.getSelections().length >= 1)
    			{
				var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
				var bulkButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"btnBulkUpdate");
				bulkButton.setDisabled(true);
    			}
    			else if(this.model.Item.Extn){
    				if((this.model.Item.Extn.ExtnDept) || (this.model.Item.Extn.ExtnStyle)){
    			 	   var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
    					var bulkButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"btnBulkUpdate");
    					bulkButton.setDisabled(false);
    				}
    			}
    		    if (this.model.Item.PrimaryInformation){
    		    	if (this.model.Item.PrimaryInformation.PrimarySupplier){
    		    	var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
					var bulkButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"btnBulkUpdate");
					bulkButton.setDisabled(true);
    		    	}
    		    }

		    },
		    tableSelectHandler:function(selectionModel, rowIndex, record){
				var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
				var bulkButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"btnBulkUpdate");
				bulkButton.setDisabled(true);
				 if (this.model.Item.PrimaryInformation){
	    		    	if (this.model.Item.PrimaryInformation.PrimarySupplier){
	    		    	var gridPanel = this.find("sciId", "seaItemSearchResultsGrid")[0];
						var bulkButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"btnBulkUpdate");
						bulkButton.setDisabled(true);
	    		    	}
	    		    }
	    },
			setInelgHandler : function(grid, rowIndex, columnIndex, e){
				var ItemlistLineList = {};
				ItemlistLineList.ItemlistLine = [];
				for (var i = 0; i < this.checkboxSelectionModel.getSelections().length; i++) {
					var itemlistLine = this.checkboxSelectionModel.getSelections()[i];
					ItemlistLineList.ItemlistLine[i] = itemlistLine.json;
				}
				this.openModifyPopup(ItemlistLineList);
			},
			openModifyPopup:function(itemlistLineList){
				var adjustDateScr = new sc.sbc.kohls.item.ineligibility.KohlsAddDatesPopUp();
				var winAdjustDateRanges = sc.sbc.util.WindowUtils.getWindow({
		                              screen : adjustDateScr,
		                              buttons : sc.sbc.util.WindowUtils.SAVECLOSE,
		                              handlers : {"save" : adjustDateScr.savaAndCloseHandler,
		                              			  "close" :adjustDateScr.closeHandler},
		                              scope : adjustDateScr,
		                              parent : this,
		                              //inputData : adjustDatesInput,
		                              windowConfig:{maximizable : false}
		                              //setDataHandler : adjustDatesInput.setSelections
		                        });
		                 sc.sbc.core.data.DataManager.setData("winAdjustDateRanges",winAdjustDateRanges);
		                 adjustDateScr.setSelections(itemlistLineList);
						 winAdjustDateRanges.parent=this;
						 winAdjustDateRanges.show();
			},
			bukUpdateHandler : function(grid, rowIndex, columnIndex, e){

				this.openModifyPopup(this.model);
			}
			
		});
Ext.reg('seaItemSearchResults', sc.sbc.kohls.item.ineligibility.ItemSearchResults);
