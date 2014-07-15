Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.KohlsModifyDatesPopUp = function(config) {
    sc.sbc.kohls.item.ineligibility.KohlsModifyDatesPopUp.superclass.constructor.call(this, config);
}
Ext.extend(sc.sbc.kohls.item.ineligibility.KohlsModifyDatesPopUp, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sbc.kohls.item.ineligibility.KohlsModifyDatesPopUp',
    getUIConfig: sc.sbc.kohls.item.ineligibility.KohlsModifyDatesPopUpUIConfig,
	
    namespaces: {
        target: ["adjustDatesModelTarget"],
        source: []
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
		var itemListLineList = selectionModel.ItemlistLine[0];
		var itemID = itemListLineList.ItemID;
		var classID = itemListLineList.ClassID;
		var Style = itemListLineList.Style;
		var effStartDate = itemListLineList.FromDate;
		var effEndDate = itemListLineList.ToDate;
		var key = itemListLineList.ExtnStoreInEligblKey;
				
		
		var seaLabelSKU = this.find("sciId", "lblSKU")[0];
		var seaTxtSKU = this.find("sciId", "txtSKU")[0];
		if(!itemID)
		{
		seaLabelSKU.setVisible(false);
		seaTxtSKU.setVisible(false);
		}
		else
		{
		seaLabelSKU.setVisible(true);
		seaTxtSKU.setVisible(true);
		seaTxtSKU.value = itemID;
		}
		
		var seaLabelClass = this.find("sciId", "lblClassID")[0];
		var seaTxtClass = this.find("sciId", "txtClassID")[0];
		if(!classID)
		{
		seaLabelClass.setVisible(false);
		seaTxtClass.setVisible(false);
		}
		else
		{
		seaLabelClass.setVisible(true);
		seaTxtClass.setVisible(true);
		seaTxtClass.value = classID;
		}
		
		var seaLabelStyle = this.find("sciId", "lblStyle")[0];
		var seaTxtStyle = this.find("sciId", "txtStyle")[0];
		if(!Style)
		{
		seaLabelStyle.setVisible(false);
		seaTxtStyle.setVisible(false);
		}
		else
		{
		seaLabelStyle.setVisible(true);
		seaTxtStyle.setVisible(true);
		seaTxtStyle.value = Style;
		}
		
		var fldEffStartDate = this.find("sciId", "fldStartDate")[0];
		fldEffStartDate.value = Date.parseDate(effStartDate, "c"); 
		var fldEffEndDate = this.find("sciId", "fldEndDate")[0];
		fldEffEndDate.value = Date.parseDate(effEndDate, "c"); 
		
		var seaTxtKey = this.find("sciId", "txtKey")[0];
		seaTxtKey.value = key;

	},
	savaAndCloseHandler:function(data, screen, parent, win){
	
	var effStartDate = this.find("sciId", "fldStartDate")[0];
	var effEndDate = this.find("sciId", "fldEndDate")[0];
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
	var inputXML = modelTarget.json;
	this.parentScr = parent;
	
	
		seaAjaxUtils.request({
				action : "kohlsUpdateInelgDateRange",
				actionNS : sc.sbc.App.ItemStrutsNS,
				inputNS : "updateInelgInput",
				inputObj : modelTarget,
				success : this.resHandler,
				scope : this,
				parentScreen : parent
				});
	
	
	},
	 resHandler:function(res,options){
		var parentRow = this.parentScr.checkBoxSelectionModel.getSelections()[0];
		
		var gridPanel = this.parentScr.find("sciId", "ineligibilityGrid")[0];
		
		var startDate = res.json.ExtnItemInEligibility.FromDate;
		var endDate = res.json.ExtnItemInEligibility.ToDate;
		
		parentRow.set("FromDate", Date.parseDate(startDate, "c"));
		parentRow.set("ToDate", Date.parseDate(endDate, "c"));
		
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
Ext.reg('xtype_name', sc.sbc.kohls.item.ineligibility.KohlsModifyDatesPopUp);