Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.KohlsViewItems = function(config) {
this.checkBoxSelectionModel= new Ext.grid.CheckboxSelectionModel({
										header : "",
										listeners:{
											scope : this,
                                            rowdeselect : this.tableDeselectHandler,
                                            beforerowselect : this.beforeRowSelectHandler, 
                                            rowselect : this.tableSelectHandler

										},
										singleSelect:false
									});
	this.pagingToolbar = new sc.sbc.common.PagingToolbar();
	this.pagingToolbar.on('change', this.processAfterPageLoad, this);
    sc.sbc.kohls.item.ineligibility.KohlsViewItems.superclass.constructor.call(this, config);
}
Ext.extend(sc.sbc.kohls.item.ineligibility.KohlsViewItems, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sbc.kohls.item.ineligibility.KohlsViewItems',
    getUIConfig: sc.sbc.kohls.item.ineligibility.KohlsViewItemsUIConfig,
	rowsel : false,
	    namespaces: {
        target: ['kohlsitemList_input'],
        source: ['kohlsitemList_output']
    },
    namespacesDesc: {
        targetDesc: [''],
        sourceDesc: ['']
    },
	
	initialize:function(){

    	//set source model
    	//this.setModel(apiResults,"kohlsitemList_output");
    	var inputObj={ExtnItemInEligibility:{}};
		inputObj.ExtnItemInEligibility=[];
    	
    	//<ExtnItemInEligibility/>
    	var tblSearchResults = this.find("sciId", "ineligibilityGrid")[0];
    	tblSearchResults.bindingData.pagination.url=this.getUrl();
    	tblSearchResults.getStore().startPagination({kohlsitemList_input : Ext.encode(inputObj)});
    },
	tableDeselectHandler:function(selectionModel, rowIndex, record){
            			
			var gridPanel = this.find("sciId", "ineligibilityGrid")[0];
			var delButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"inElgDelButton");
			delButton.setDisabled(true);
	    },
	
	beforeRowSelectHandler :function(selModel, rowIndex,  keepExisting,  record){
	 	return true;
	},
	
	tableSelectHandler:function(selectionModel, rowIndex, record){
			var gridPanel = this.find("sciId", "ineligibilityGrid")[0];
			var delButton = sc.sbc.util.CoreUtils.getTopToolBarItemBySciId(gridPanel,"inElgDelButton");
			delButton.setDisabled(false);
    },
	getModifyAction:function(){
		return "Modify";
		
	},

	openModifyPopup:function(itemlistLineList){
		var adjustDateScr = new sc.sbc.kohls.item.ineligibility.KohlsModifyDatesPopUp();
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
	
	showLinksWithImageInColumns:function(value,data,record){
		return sc.sbc.util.RendererUtils.showLinksWithImageInColumns(value,data,record);
	},

	handlerOpenModifyPopup : function(grid, rowIndex, columnIndex, e) {
		if(columnIndex != 0)
		{
		var colIndex = columnIndex;
		var ItemlistLineList = {};
		ItemlistLineList.ItemlistLine = [];
		var itemlistLine = this.checkBoxSelectionModel.getSelections()[0];
		ItemlistLineList.ItemlistLine[0] = itemlistLine.json;
		this.openModifyPopup(ItemlistLineList);
		}
	},
	
	deletInelgHandler : function() {
		var inputObj={ExtnItemInEligibilityList:{}};
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility=[];

		for(var i=0;i<this.checkBoxSelectionModel.getSelections().length;i++){
			inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[i]={};
			inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[i].ExtnStoreInEligblKey=this.checkBoxSelectionModel.getSelections()[i].json.ExtnStoreInEligblKey;
		}
			seaAjaxUtils.request({
					action : "kohlsDeleterInelgData",
					actionNS : sc.sbc.App.ItemStrutsNS,
					inputNS : "deleteInelgInput",
					inputObj : inputObj,
					success :this.resHandler,
					scope : this,
					successMsg:this.b_successMsgForDelete

					});
	},
	resHandler:function(res,options){
  
    	seaAjaxUtils.request({
				actionNS : sc.sbc.App.ItemStrutsNS,
				action : "kohlsItemeligibilityList",
				params : {
					scPageSize:15,
					scPaginationStrategy:"NEXTPAGE",
				},
				scope : this,
				success : function(res, options) {
								eval(res.responseText);
						  }
			});
    },
	processAfterPageLoad : function(pagingToolBar, pagingData){
		
	},
	getUrl : function(){
		return sc.sbc.helper.AppHelper.getAjaxURL("kohlsViewItemeligibilityList", "/sbc/item"); 
	}
	  

});
Ext.reg('seaKohlsItemInelgList', sc.sbc.kohls.item.ineligibility.KohlsViewItems);