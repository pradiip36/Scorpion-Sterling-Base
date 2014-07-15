Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.KohlsAddDatesPopUp = function(config) {
    sc.sbc.kohls.item.ineligibility.KohlsAddDatesPopUp.superclass.constructor.call(this, config);
}
Ext.extend(sc.sbc.kohls.item.ineligibility.KohlsAddDatesPopUp, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sbc.kohls.item.ineligibility.KohlsAddDatesPopUp',
    getUIConfig: sc.sbc.kohls.item.ineligibility.KohlsAddDatesPopUpUIConfig,
    namespaces: {
        target: ["adjustDatesModelTarget"],
        source: ["adjustDatesModelSource"]
    },
    namespacesDesc: {
        targetDesc: [''],
        sourceDesc: ['']
    },
    setSelections : function(selectionModel) {
		var formattedStartDate = null;
		var formattedEndDate = null;
		this.model = selectionModel;
		var winObj = sc.sbc.core.data.DataManager.getData("winAdjustPrices");
		this.setModel(selectionModel,"adjustDatesModelSource");
	},
	savaAndCloseHandler:function(data, screen, parent, win){
	

	var effStartDate = this.find("sciId", "fldStartDate")[0];
	var effEndDate = this.find("sciId", "fldToDate")[0];
	var effStartValue = effStartDate.getValue();
	var effEndValue = effEndDate.getValue();
	if(Ext.isEmpty(effStartValue) || Ext.isEmpty(effEndValue)){
								sc.sbc.ui.messages.showErrorAlert("Dates cannot be empty!");
								return;
								}
	
	var diff=sc.sbc.util.CoreUtils.validateDate(effStartValue,effEndValue);
	if(diff <=0){
		sc.sbc.ui.messages.showErrorAlert("Effective Start Date cannot be on or after Effective End Date!");
		return;
	}
	
	
	var modelTarget=this.getTargetModel("adjustDatesModelTarget")[0];
	var inputXML = this.model;
	
	var inputObj={ExtnItemInEligibilityList:{}};
	inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility=[];
	
	var orgCode = sc.sbc.core.context.JSContext.getCurrentOrganizationCode();
	if(this.model.ItemlistLine){
	var itemlistLine=this.model.ItemlistLine;
	var len=itemlistLine.length;
	
	for(var i=0; i<len;i++)
		{
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[i]={};
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[i].ItemID = inputXML.ItemlistLine[i].ItemID;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[i].UnitOfMeasure = inputXML.ItemlistLine[i].UnitOfMeasure;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[i].FromDate = modelTarget.ExtnItemInEligibility.FromDate;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[i].ToDate = modelTarget.ExtnItemInEligibility.ToDate;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[i].ShipNode = orgCode;
		}
	}
	
	else if(inputXML.Item.Extn.ExtnDept)
		{
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[0]={};
		var classID = inputXML.Item.Extn.ExtnDept;
		if(inputXML.Item.Extn.ExtnClass){
			 classID = classID.concat("/",inputXML.Item.Extn.ExtnClass);
		}
		if(inputXML.Item.Extn.ExtnSubClass){
			 classID = classID.concat("/",inputXML.Item.Extn.ExtnSubClass);
		} 
	
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[0].ClassID = classID ;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[0].FromDate = modelTarget.ExtnItemInEligibility.FromDate;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[0].ToDate = modelTarget.ExtnItemInEligibility.ToDate;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[0].ShipNode = orgCode;
		}
	else
		{
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[0]={};
		var style = inputXML.Item.Extn.ExtnStyle;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[0].Style = style ;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[0].FromDate = modelTarget.ExtnItemInEligibility.FromDate;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[0].ToDate = modelTarget.ExtnItemInEligibility.ToDate;
		inputObj.ExtnItemInEligibilityList.ExtnItemInEligibility[0].ShipNode = orgCode;
		}
	
		seaAjaxUtils.request({
				action : "kohlsAddInelgDateRange",
				actionNS : sc.sbc.App.ItemStrutsNS,
				inputNS : "addInelgInput",
				inputObj : inputObj,
				success : this.resHandler,
				scope : this,
				parentScreen : parent
				});
	
	
	},
	 resHandler:function(res,options){
    	//eval(res.responseText);
    	//var parent=options.configParams.parentScreen;
    	//var res=Ext.decode(res.responseText);
		//parent.setModelForComponents(res.output);
    	//this.closeHandler(true);
  
    	seaAjaxUtils.request({
				actionNS : sc.sbc.App.ItemStrutsNS,
				action : "kohlsItemineligibilityHome",
				scope : this,
				success : function(res, options) {
								eval(res.responseText);
						  }
			});
    	this.closeHandler(true);
    },
    
	closeHandler:function(isInternal){
			if(sc.sbc.core.data.DataManager.getData("winAdjustDateRanges")){
	    		 sc.sbc.core.data.DataManager.removeData("winAdjustDateRanges");
			}
			if(isInternal == true) {
				this.ownerCt.close();
			} else {
	    		return true;
			}
    }
});
Ext.reg('xtype_name', sc.sbc.kohls.item.ineligibility.KohlsAddDatesPopUp);