Ext.namespace('sc.sbc.kohls.item.safetyfactor');

sc.sbc.kohls.item.safetyfactor.sbcKohlsSafetyFactorDetailScreen = function(config) {
	this.pagingToolbar = new sc.sbc.common.PagingToolbar();
	this.pagingToolbar.on('change', this.processAfterPageLoad, this);
    sc.sbc.kohls.item.safetyfactor.sbcKohlsSafetyFactorDetailScreen.superclass.constructor.call(this, config);	
	var CategoryBeingAdjusted = this.find("sciId", "lblCategoryBeingAdjusted")[0];
}


	
	
Ext.extend(sc.sbc.kohls.item.safetyfactor.sbcKohlsSafetyFactorDetailScreen, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sbc.kohls.item.safetyfactor.sbcKohlsSafetyFactorDetailScreen',
    getUIConfig: sc.sbc.kohls.item.safetyfactor.sbcKohlsSafetyFactorDetailScreenUIConfig,
    namespaces: {
        target: ['getCategoryItemList_input','kohlsItemSafetyFactorUpdate_input'],
        source: ['categoryItemDetails_output','SafetyFactorOperations','categorySelected','itemStatus_input','manageSafetyFactor_input']
    },
    namespacesDesc: {
        targetDesc: [''],
        sourceDesc: ['']
    },
	
	getUrl : function(){
		return sc.sbc.helper.AppHelper.getAjaxURL("getCategoryItemList", "/sbc/item"); 
	},	
			
	currentDetailPanel : null,
    currentScreenID : "",
    treePanel:null,
    categoryTree : null,
	debug: false,
	
    onLoad: function(apiResults, opt){
		if(this.debug){
			alert("inside onLoad of SafetyFactor Screen");
		}
    	var treePanelScreen = this.find("sciId", "sbcCategoryTreeScreen")[0];
    	treePanelScreen.onLoad(apiResults, opt);   	
    	this.categoryTree = treePanelScreen.getTree();
    	this.categoryTree.addListener("beforeclick", this.treeNodebeforeclick, this);
		this.categoryTree.addListener("click", this.treeNodeSelected, this);
		
		
    },
	
	treeNodebeforeclick:function(node, evtData){
    	if(this.isChkRequiredForScreenUnload(true)){
    		this.treeNodeSelected(node, evtData);
    		node.setclick=true;
    		return false;
    	}else{
    		return true;
    	}   	
    },
	
	
	
	treeNodeSelected:function(node, evtData){
    	var nodetype = node.attributes.nodetype.Type;
    	var nodedata = node.attributes.json;
		var CategoryBeingAdjusted = this.find("sciId", "lblCategoryBeingAdjusted")[0];
		var adjustmentValue = this.find("sciId", "lblPercentage")[0];
    	if( nodetype === "CategoryDomain"){
			//Disable the SafetyFactor Button if no Dept/Class/SubClass is selected
			//var btnSafetyAdj = this.find("sciId", "btnAdjustSafetyFactor")[0];
			//btnSafetyAdj.disable();
			var category = nodedata.CategoryDomain;			
			this.updateCategorySelectedModel(category);
			
			return;
    	}else if(nodetype === "Category"){
			
			//var btnSafetyAdj = this.find("sciId", "btnAdjustSafetyFactor")[0];
			//btnSafetyAdj.enable();			
			var category = nodedata.CategoryPath;
			
			var dispSubClass = node.text;
			var dispClass = node.parentNode.text;
			var dispDept = node.parentNode.parentNode.text;
			
			//Refresh the Adjustment Panel if new class id is selected
			if(category != this.getModel("categorySelected").CategoryPath){
				this.refreshTheAdjustmentPanel();
			}
			
			this.updateCategorySelectedModel(category);	
			
			if(dispDept === "root")
			{
				var formattedCategory = dispSubClass;
				
			}
			else if (dispDept === "KohlsCatalog")
			{
				var formattedCategory = dispClass.concat(" > ",dispSubClass);
			}
			else
			{
				var formattedCategory = dispDept.concat(" > ",dispClass);
				formattedCategory = formattedCategory.concat(" > ", dispSubClass);
			}
			
			this.populateComboModel();
			CategoryBeingAdjusted.setText(formattedCategory);
			
			this.callGetCategoryItemList();	
		}
    },
	
	refreshTheAdjustmentPanel: function(){
			//Default Settings for the  Adjustement Panel
			//all the fields are enabled
			//all are set to blank
			//Percenrage adjustment radio button is selected as default
			var varSafetyFactorAdjustmentByPercentage = this.find("sciId", "txtKohlsSafetyFactorAdjustmentByPercentage")[0];
			varSafetyFactorAdjustmentByPercentage.setValue("");
			varSafetyFactorAdjustmentByPercentage.enable();	
			
			var varRadButton = this.find("sciId", "radManageSafetyFactorByPercentage")[0];
			varRadButton.setValue(true);
			
			var varcmbKohlsSafetyfactorOptions = this.find("sciId", "cmbKohlsSafetyfactorOptions")[0]; 
			varcmbKohlsSafetyfactorOptions.setValue("");
			varcmbKohlsSafetyfactorOptions.enable();
			
			var varSafetyFactorAdjustmentByQty = this.find("sciId", "txtKohlsSafetyFactorAdjustmentQty")[0];
			varSafetyFactorAdjustmentByQty.setValue("");
			varSafetyFactorAdjustmentByQty.enable();
			
			var varcmbKohlsSafetyfactorOptionsForQty = this.find("sciId", "cmbKohlsSafetyfactorOptionsForQty")[0]; 
			varcmbKohlsSafetyfactorOptionsForQty.setValue(""); 
			varcmbKohlsSafetyfactorOptionsForQty.enable();
			
			var varDefaultSftyFactor = this.find("sciId", "txtDefaultSafetyFactor")[0];
			varDefaultSftyFactor.setValue("");			
	},
	
	callGetCategoryItemList: function(){
			var obj = {};
			obj["actionName"] = "getItemList";
			
			var vOrganizationCode = sc.sbc.core.context.JSContext.getCurrentOrganizationCode();
			
			var model={};
			model.Item={};
			model.Item.Extn=[];
			model.Item.OrganizationCode=vOrganizationCode;
			model.Item.UnitOfMeasure="EACH";
			
			model.Item.Extn[0]={};			
			
			var categorySelected = this.getModel("categorySelected");
			var categoryPath = categorySelected.CategoryPath;
			var splitCategoryPath = categoryPath.split("/");
			var Dept = splitCategoryPath[2];
			model.Item.Extn[0].ExtnDept=Dept;
			if(splitCategoryPath.length>2){
				var Classid = splitCategoryPath[3];
				if(Classid){
				var indOfClass = Classid.indexOf("-");
				var actualClassID = Classid.substring(
						indOfClass + 1, Classid.length);
				if(actualClassID)
				{
				model.Item.Extn[0].ExtnClass=actualClassID;
				}
				}
			}
			if(splitCategoryPath.length>3){
				var SubClass = splitCategoryPath[4];
				if(SubClass){
				var indOfSubClass = SubClass.lastIndexOf("-");
				var actualSubClass = SubClass.substring(
						indOfSubClass + 1, SubClass.length);
				if(actualSubClass)
				{
				model.Item.Extn[0].ExtnSubClass=actualSubClass;
				}
				}
			}	
			
			this.setModel(model,"getCategoryItemList_input");
			obj["getCategoryItemList_input"] = Ext.encode(model);
			
			
			var tblSearchResults = this.find("sciId", "kohlsSafetyFactorItemsGrid")[0];
			tblSearchResults.getStore().startPagination({getCategoryItemList_input : Ext.encode(model)});
			/*seaAjaxUtils.request({
                        action : "getCategoryItemList",
                        actionNS : sc.sbc.App.ItemStrutsNS,
                        params: obj,
                        success : this.manageItemSuccess,
                        scope : this
                  });*/
			if(this.debug){
				alert("inside onLoad of SafetyFactor Screen for Category"); 
			}
			return;			
    	},
	
	updateCategorySelectedModel: function(category){
			var categorySelected = {};
			categorySelected.CategoryPath={};			
			categorySelected.CategoryPath=category;
			this.setModel(categorySelected,"categorySelected");
	},
	
	handleModel: function(apiOutput){
		//this.setModel(apiOutput,"sourcemodel_test");
	},
	
	updateSourceModel: function(modelData){
		this.setModel(modelData,"sourcemodel_test");
	},
	
	populateComboModel: function(){
		var apioutput = {};
		 apioutput.Options=[];
		 		 
		 apioutput.Options[0] = {};
		 apioutput.Options[0].Option = "Increase By";	
		 
		 apioutput.Options[1] = {};
		 apioutput.Options[1].Option = "Decrease By";	
		 
		 this.setModel(apioutput,"SafetyFactorOperations");
		 
		 //this.setTestModel();
	},
	
	manageSafetyFactor: function(){
		if(this.debug){
		alert("manageSafetyFactor Button Clicked");
		}
		if(this.validateSafetyStockRequest()){
			this.submitSafetyFactorAdjustmentRequest();
		}
	},
	
	validateSafetyStockRequest: function(){
		var categorySelected = this.getModel("categorySelected");
		
			var categoryPath = categorySelected.CategoryPath;
			var splitCategoryPath = categoryPath.split("/");
			if(splitCategoryPath.length==1){
				this.invalidInput("Please select Department/Class/SubClass combination for safety factor adjustment");
				return false;
			}
		
		var safetyModel = this.getTargetModel('kohlsItemSafetyFactorUpdate_input')[0];
			
			if(safetyModel.ExtnItemSafetyFactor.SafetyOperation == ""){
				this.invalidInput("Please select either Percentage or Quantity option for safety stock adjustments");
				return false;
			}
		
			if(safetyModel.ExtnItemSafetyFactor.SafetyOperation == "P"){
				var num = new Number(safetyModel.ExtnItemSafetyFactor.SafetyFactorPercentage)	;
					if(num=="NaN"){
						this.invalidInput("Please enter a valid value%");
					return false;
					}
				if(safetyModel.ExtnItemSafetyFactor.SafetyFactorPercentage> 100 ){
					this.invalidInput("Safety Stocks can not be adjusted by more than 100%");
					return false;
				}
				if(safetyModel.ExtnItemSafetyFactor.SafetyFactorPercentage <= 0){
					this.invalidInput("Safety Stocks can not be adjusted by 0% or less");
					return false;
				}
				if(safetyModel.ExtnItemSafetyFactor.SafetyOperationPercentage==""){
					this.invalidInput("Please select a valid adjustment type from dropdown");
					return false;
				}
			}else{
				if(safetyModel.ExtnItemSafetyFactor.SafetyFactorQty<= 0){
					this.invalidInput("Quantity input for safety stocks should be more than Zero. To lower the Safety Stocks please select correct adjustment type");
					return false;
				}
				if(safetyModel.ExtnItemSafetyFactor.SafetyOperationQuantity==""){
					this.invalidInput("Please select a valid adjustment type from dropdown");
					return false;
				}
			}
			
			
			/*if(safetyModel.ExtnItemSafetyFactor.DefaultSafetyFactor==""){
					this.invalidInput("Please enter default safety factor");
					return false;
			}
			if(safetyModel.ExtnItemSafetyFactor.DefaultSafetyFactor<= 0){
					this.invalidInput("Default Safety Factor Should be greater than zero.");
					return false;
			}*/
			return true;
	},
	
	invalidInput : function(message) {
		Ext.MessageBox.show({
           title:'Invalid Input',
           msg: message,
           buttons: Ext.MessageBox.OK,
           fn: this.showResultText,		   
           animEl: 'mb4',
           icon: Ext.MessageBox.MESSAGE
       });
	   },
	
	adjustSafetyStockMessage : function() {
		Ext.MessageBox.show({
           title:'adjustSafetyStockMessage',
           msg: 'You are about to Bulk Update the searched result. <br />Would you like to continue?',
           buttons: Ext.MessageBox.YESNO,
           fn: this.showResultText,		   
           animEl: 'mb4',
           icon: Ext.MessageBox.QUESTION
       });
	   },
	   
	 	
	submitSafetyFactorAdjustmentRequest: function(){
			var obj = {};
			
			var categorySelected = this.getModel("categorySelected");
			var categoryPath = categorySelected.CategoryPath;
			var splitCategoryPath = categoryPath.split("/");
			var Dept = splitCategoryPath[2];
			
			if(splitCategoryPath.length>2){
				var Classid = splitCategoryPath[3];
				if(Classid){
				var indOfClass = Classid.indexOf("-");
				var actualClassID = Classid.substring(
						indOfClass + 1, Classid.length);
				}
			}
			if(splitCategoryPath.length>3){
				var SubClass = splitCategoryPath[4];
				if(SubClass){
				var indOfSubClass = SubClass.lastIndexOf("-");
				var actualSubClass = SubClass.substring(
						indOfSubClass + 1, SubClass.length);
				}
			}	
			
			var safetyModel = this.getTargetModel('kohlsItemSafetyFactorUpdate_input')[0];
			
			var apiinput = {};
			apiinput.ExtnItemSafetyFactor={};
			
			apiinput.ExtnItemSafetyFactor.CatDepartment=Dept;
			if(actualClassID)
			{
			apiinput.ExtnItemSafetyFactor.CatClass=actualClassID;
			}
			if(actualSubClass)
			{
			apiinput.ExtnItemSafetyFactor.CatSubClass=actualSubClass;
			}
			
			if(safetyModel.ExtnItemSafetyFactor.SafetyOperation == "P"){
				apiinput.ExtnItemSafetyFactor.SafetyFactor=safetyModel.ExtnItemSafetyFactor.SafetyFactorPercentage;
				apiinput.ExtnItemSafetyFactor.SftFlag="P";
				if(safetyModel.ExtnItemSafetyFactor.SafetyOperationPercentage == "Increase By"){
					apiinput.ExtnItemSafetyFactor.SafetyOperation="I";
				}
				else{
					apiinput.ExtnItemSafetyFactor.SafetyOperation="D";
				}
			}
			else{
				apiinput.ExtnItemSafetyFactor.SafetyFactor=safetyModel.ExtnItemSafetyFactor.SafetyFactorQty;
				apiinput.ExtnItemSafetyFactor.SftFlag="Q";
				if(safetyModel.ExtnItemSafetyFactor.SafetyOperationQuantity == "Increase By"){
					apiinput.ExtnItemSafetyFactor.SafetyOperation="I";
				}
				else{
					apiinput.ExtnItemSafetyFactor.SafetyOperation="D";
				}
			}
			if(safetyModel.ExtnItemSafetyFactor.DefaultSafetyFactor==""){
					apiinput.ExtnItemSafetyFactor.DefaultSafetyFactor=0;
			}
			else{
				apiinput.ExtnItemSafetyFactor.DefaultSafetyFactor=safetyModel.ExtnItemSafetyFactor.DefaultSafetyFactor;
			}
			
			var vUserID = sc.plat.Userprefs.getUserId();
			apiinput.ExtnItemSafetyFactor.UserID=vUserID;
			
			obj["KohlsItemSafetyFactorUpdateService_input"] = Ext.encode(apiinput);		
			
			seaAjaxUtils.request({
                        action : "KohlsItemSafetyFactorUpdateService",
                        actionNS : sc.sbc.App.ItemStrutsNS,
                        params: obj,
                        success : this.OnRequestSubmissionSuccess,
                        scope : this
                  });
			
			if(this.debug){
			alert("inside onLoad of SafetyFactor Screen for Category"); 
			}
			
			return;	
	},
	
	
	processAfterPageLoad : function(pagingToolBar, pagingData){
				var varcmbKohlsSafetyfactorOptions = this.find("sciId", "cmbKohlsSafetyfactorOptions")[0];
				varcmbKohlsSafetyfactorOptions.focus();
				scope : this
			},

	OnRequestSubmissionSuccess: function(res, options){
		Ext.MessageBox.show({
           title:'Safety Stock Adjustments',
           msg: 'Safety Stock Adjustment request submitted successfully',
           buttons: Ext.MessageBox.OK,
           fn: this.showResultText,		   
           animEl: 'mb4',
           icon: Ext.MessageBox.MESSAGE
       });
	},
	
	manageItemSuccess: function(res, options){
		var itemList = Ext.decode(res.responseText);
		var paramsObj = options.configParams.params;
		this.updateCategoryItemListDetails(itemList);
		if(this.debug){
		alert("Safety Factor Update request has been submitted successfully");
		}
		//eval(res.responseText);
	},
	
	handleQtyRadioButtons: function(radioBtn, isSelected){
		if(this.debug){
		alert("Safety Stocks will be adjusted by Qty");
		}
		if(isSelected){
			//Clear the Percentage Adjustment values and disable too
			var varSafetyFactorAdjustmentByPercentage = this.find("sciId", "txtKohlsSafetyFactorAdjustmentByPercentage")[0];
			varSafetyFactorAdjustmentByPercentage.setValue("");
			varSafetyFactorAdjustmentByPercentage.disable();			
			var varcmbKohlsSafetyfactorOptions = this.find("sciId", "cmbKohlsSafetyfactorOptions")[0];
			varcmbKohlsSafetyfactorOptions.setValue("");			
			varcmbKohlsSafetyfactorOptions.disable();
			
			
			//enable the Qty Adjustment Controls 
			var varSafetyFactorAdjustmentByQty = this.find("sciId", "txtKohlsSafetyFactorAdjustmentQty")[0];
			varSafetyFactorAdjustmentByQty.enable();
			var varcmbKohlsSafetyfactorOptionsForQty = this.find("sciId", "cmbKohlsSafetyfactorOptionsForQty")[0]; 
			varcmbKohlsSafetyfactorOptionsForQty.enable();
		}
	},
	
	handlePercentageRadioButtons: function(radioBtn, isSelected){
		if(this.debug){
			alert("Safety Stocks will be adjusted by Percentage");
		}
		if(isSelected){
			//enable the Percentage Adjustment Controls 
			var varSafetyFactorAdjustmentByPercentage = this.find("sciId", "txtKohlsSafetyFactorAdjustmentByPercentage")[0];
			varSafetyFactorAdjustmentByPercentage.enable();
			var varcmbKohlsSafetyfactorOptions = this.find("sciId", "cmbKohlsSafetyfactorOptions")[0]; 
			varcmbKohlsSafetyfactorOptions.enable();
			
			//Clear the Qty Adjustment values and disable too
			var varSafetyFactorAdjustmentByQty = this.find("sciId", "txtKohlsSafetyFactorAdjustmentQty")[0];
			varSafetyFactorAdjustmentByQty.setValue("");
			varSafetyFactorAdjustmentByQty.disable();
			
			var varcmbKohlsSafetyfactorOptionsForQty = this.find("sciId", "cmbKohlsSafetyfactorOptionsForQty")[0]; 
			varcmbKohlsSafetyfactorOptionsForQty.setValue("");
			varcmbKohlsSafetyfactorOptionsForQty.disable();
		}
	},
	
	updateCategoryItemListDetails: function(getCategoryItemListDetails_output){
		if(this.debug){
		alert("updating the getCategoryItemListDetails_output model");
		}		
		this.setModel(getCategoryItemListDetails_output,"categoryItemDetails_output");		
	},
	
	
	
	setTestModel: function(){
		var apioutput = {};
	 apioutput.ItemList={};
	 apioutput.ItemList.Item =[];
	 apioutput.ItemList.Item[0] = {};
	 apioutput.ItemList.Item[0].ItemId = "Store";	 
	 apioutput.ItemList.Item[1] = {};
	 apioutput.ItemList.Item[1].ItemId = "RDC";	 

	 apioutput.ItemList.Item[2] = {};
	 apioutput.ItemList.Item[2].ItemId = "eFC";	 

	 apioutput.ItemList.Item[3] = {};
	 apioutput.ItemList.Item[3].ItemId = "Other";

	 this.setModel(apioutput,"SafetyFactorOperations");
	},
	
	updateModel: function(modelData, modelName){
	
	}
	
});
Ext.reg('sbcKohlsSafetyFactorDetailScreen', sc.sbc.kohls.item.safetyfactor.sbcKohlsSafetyFactorDetailScreen);