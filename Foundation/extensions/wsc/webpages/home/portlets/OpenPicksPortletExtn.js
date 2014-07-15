scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/home/portlets/OpenPicksPortletExtnUI","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/PlatformUIFmkImplUtils","scbase/loader!wsc/utils/BackroomPickUtils","scbase/loader!isccs/utils/UIUtils","scbase/loader!isccs/utils/BaseTemplateUtils","scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnOpenPicksPortletExtnUI
			 ,_scModelUtils   
			 ,_scBaseUtils
			 ,_scPlatformUIFmkImplUtils
			 ,_wscBackroomPickUtils
			 ,_isccsUIUtils
			 ,_isccsBaseTemplateUtils
			 ,_scWidgetUtils
){ 
	return _dojodeclare("extn.home.portlets.OpenPicksPortletExtn", [_extnOpenPicksPortletExtnUI],{
	// custom code here

	 extInitialize: function() {
		if (!(
            _scBaseUtils.isVoid("openPicksPortletContainer"))) {
                if (
                _scBaseUtils.equals(
                this.firstClickEvent, "true")) {
                    _scWidgetUtils.showWidget(
                    this, "openPicksPortletContainer", false, null);
                    var completeInputModel = null;
                    completeInputModel = _scModelUtils.createNewModelObjectWithRootKey("Shipment");
                    _isccsUIUtils.callApi(
                    this, completeInputModel, "getShipmentListInit", null);
                    this.firstClickEvent = "false";
                } else {
                    if (
                    _scWidgetUtils.isWidgetVisible(
                    this, "openPicksPortletContainer")) {
                        _scWidgetUtils.hideWidget(
                        this, "openPicksPortletContainer", false);
                    } else {
                        _scWidgetUtils.showWidget(
                        this, "openPicksPortletContainer", false, null);
                    }
                }
            }

	 },
		
		setDefaultFilterAndOrder: function() {
            var filterParamsModel = null;
            filterParamsModel = _scModelUtils.createNewModelObjectWithRootKey("Filter");
            _scModelUtils.setStringValueAtModelPath("Filter.BackroomPickup", "Y", filterParamsModel);
            _scModelUtils.setStringValueAtModelPath("Filter.PicksInProgress", "Y", filterParamsModel);
            _scModelUtils.setStringValueAtModelPath("Filter.MaximumRecords", "100", filterParamsModel);
            _scModelUtils.setStringValueAtModelPath("Filter.OrderBy", "ExpectedShipmentDate-N", filterParamsModel);
            _scModelUtils.setStringValueAtModelPath("Filter.MaximumRecords", "100", filterParamsModel);
            _scModelUtils.setStringValueAtModelPath("Filter.Unassigned", "Y", filterParamsModel);
            _scBaseUtils.setModel(
            this, "filterParams_output", filterParamsModel, null);
        },
        
        callGetShipmentList: function() {
            var getShipmentListTargetModel = null;
            getShipmentListTargetModel = _scBaseUtils.getTargetModel(
            this, "getShipmentList_input", null);
            var filterParamsTargetModel = null;
            filterParamsTargetModel = _scBaseUtils.getTargetModel(
            this, "filterParams_input", null);
            
            var jewelry = null;
            jewelry = _scModelUtils.getStringValueFromPath("Filter.Jewelry", filterParamsTargetModel)
            if (
            _scBaseUtils.equals("Y", jewelry)) {
                _scModelUtils.setStringValueAtModelPath("Shipment.Extn.ExtnShipmentIndicator", "JEW", getShipmentListTargetModel);
            }
           
            var alertFlag = null;
            alertFlag = _scModelUtils.getStringValueFromPath("Filter.Alert", filterParamsTargetModel)
            if (
            _scBaseUtils.equals("Y", alertFlag)) {
                _scModelUtils.setStringValueAtModelPath("Shipment.Extn.ExtnTiersAlertFlag", "Y", getShipmentListTargetModel);
            }
                        var complexQryModel = null;
            complexQryModel = this.buildComplexQueryforFilter();
            if (
            _scBaseUtils.isVoid(
            complexQryModel)) {
                _isccsBaseTemplateUtils.showMessage(
                this, "Error_SelectStatus", "error", null);
                return;
            }
            var orderByModel = null;
            orderByModel = this.buildOrderByModel(
            filterParamsTargetModel);
            var completeInputModel = null;
            completeInputModel = _wscBackroomPickUtils.buildCompleteInputGetShipmentList(
            getShipmentListTargetModel, complexQryModel, orderByModel);
            
            // shift OrderNo to shipment level
            if (!_scBaseUtils.isVoid(completeInputModel.Shipment.ShipmentLines) 
            		&& !_scBaseUtils.isVoid(completeInputModel.Shipment.ShipmentLines.ShipmentLine)
            		&& !_scBaseUtils.isVoid(completeInputModel.Shipment.ShipmentLines.ShipmentLine.OrderNo)) {
            	completeInputModel.Shipment.OrderNo = completeInputModel.Shipment.ShipmentLines.ShipmentLine.OrderNo;
            	completeInputModel.Shipment.ShipmentLines = undefined;
            }
            _isccsUIUtils.callApi(
            this, completeInputModel, "getShipmentList", null);
        },
        
         buildComplexQueryforFilter: function(){
        	
        	 var loggedInUser = null;
            loggedInUser = _scPlatformUIFmkImplUtils.getUserId(); 
        	var filterOptionsModel = null;
        	filterOptionsModel = _scBaseUtils.getTargetModel(
            this, "filterParams_input", null);
	
			var complexQryModel = {};
			complexQryModel.ComplexQuery={};
			complexQryModel.ComplexQuery.Operator="AND";
			complexQryModel.ComplexQuery.And={};
			complexQryModel.ComplexQuery.And.Or=[];
			complexQryModel.ComplexQuery.And.Or[0] = {};
			complexQryModel.ComplexQuery.And.Or[1] = {};
			complexQryModel.ComplexQuery.And.Or[2] = {};
			complexQryModel.ComplexQuery.And.Or[0].Exp=[];
			complexQryModel.ComplexQuery.And.Or[1].Exp=[];
			complexQryModel.ComplexQuery.And.Or[2].Exp=[];
		
			var pos = 0;
			
			if(filterOptionsModel.Filter.BackroomPickup == "Y"){
				complexQryModel.ComplexQuery.And.Or[0].Exp[pos]=_wscBackroomPickUtils.buildQueryElement("Status", "1100.70.06.10","FLIKE");
				pos++;
			}
			if(filterOptionsModel.Filter.PicksInProgress == "Y"){
				complexQryModel.ComplexQuery.And.Or[0].Exp[pos]=_wscBackroomPickUtils.buildQueryElement("Status", "1100.70.06.20","FLIKE");
				pos++;
				
				complexQryModel.ComplexQuery.And.Or[0].Exp[pos]=_wscBackroomPickUtils.buildQueryElement("Status", "1100.70.06.50.2","FLIKE");
				pos++;
				complexQryModel.ComplexQuery.And.Or[0].Exp[pos]=_wscBackroomPickUtils.buildQueryElement("Status", "1100.70.06.50.4","FLIKE");
				pos++;
			}
			if(filterOptionsModel.Filter.CustomerPickup == "Y"){
				complexQryModel.ComplexQuery.And.Or[0].Exp[pos]=_wscBackroomPickUtils.buildQueryElement("Status", "1100.70.06.30","FLIKE");
				pos++;
			}
			if(filterOptionsModel.Filter.ShipmentComplete == "Y"){
				complexQryModel.ComplexQuery.And.Or[0].Exp[pos]=_wscBackroomPickUtils.buildQueryElement("Status", "1400.1","FLIKE");
				pos++;
			}
			var stock = 0;
			if(filterOptionsModel.Filter.Sales == "Y"){
				complexQryModel.ComplexQuery.And.Or[1].Exp[stock]=_wscBackroomPickUtils.buildQueryElement("ExtnFilterVal", "Sales","FLIKE");
				stock++;
			}
			if(filterOptionsModel.Filter.Stock == "Y"){
				complexQryModel.ComplexQuery.And.Or[1].Exp[stock]=_wscBackroomPickUtils.buildQueryElement("ExtnFilterVal", "Stock","FLIKE");
				stock++;	
			}
			if(filterOptionsModel.Filter.Mixed == "Y"){
				complexQryModel.ComplexQuery.And.Or[1].Exp[stock]=_wscBackroomPickUtils.buildQueryElement("ExtnFilterVal", "Mixed","FLIKE");
				stock++;
			}
			
			var assignedcount = 0;
			var includeMyPicks = null;
            includeMyPicks = _scModelUtils.getStringValueFromPath("Filter.OwnedByMe", filterOptionsModel);
            var Assigned = null;
            Assigned = _scModelUtils.getStringValueFromPath("Filter.Assigned", filterOptionsModel);
            var UnAssigned = null;
            UnAssigned = _scModelUtils.getStringValueFromPath("Filter.Unassigned", filterOptionsModel);
			if(_scBaseUtils.equals("Y", includeMyPicks) && _scBaseUtils.equals("Y", Assigned) && _scBaseUtils.equals("Y", UnAssigned)) {
				
			}
			else if(_scBaseUtils.equals("Y", includeMyPicks) && _scBaseUtils.equals("Y", Assigned)){
				var qryElement = {};
				qryElement.Name = "AssignedToUserId";
				qryElement.QryType = "NOTNULL";
				complexQryModel.ComplexQuery.And.Or[2].Exp[assignedcount]=qryElement;
				assignedcount++;
			}
			else if(_scBaseUtils.equals("Y", includeMyPicks)&& _scBaseUtils.equals("Y", UnAssigned)){
				var qryElement = {};
				qryElement.Name = "AssignedToUserId";
				qryElement.QryType = "ISNULL";
				complexQryModel.ComplexQuery.And.Or[2].Exp[assignedcount]=qryElement;
				assignedcount++;
				complexQryModel.ComplexQuery.And.Or[2].Exp[assignedcount]=_wscBackroomPickUtils.buildQueryElement("AssignedToUserId", loggedInUser,"FLIKE");
				assignedcount++;
			}
			else if(_scBaseUtils.equals("Y", Assigned)&& _scBaseUtils.equals("Y", UnAssigned)){
				
			}
			else if(_scBaseUtils.equals("Y", Assigned)){
				var qryElement = {};
				qryElement.Name = "AssignedToUserId";
				qryElement.QryType = "NOTNULL";
				complexQryModel.ComplexQuery.And.Or[2].Exp[assignedcount]=qryElement;
				assignedcount++;
			}
			else if(_scBaseUtils.equals("Y", UnAssigned)){
				var qryElement = {};
				qryElement.Name = "AssignedToUserId";
				qryElement.QryType = "ISNULL";
				complexQryModel.ComplexQuery.And.Or[2].Exp[assignedcount]=qryElement;
				assignedcount++;
			}
			else if(_scBaseUtils.equals("Y", includeMyPicks)){
				complexQryModel.ComplexQuery.And.Or[2].Exp[assignedcount]=_wscBackroomPickUtils.buildQueryElement("AssignedToUserId", loggedInUser,"FLIKE");
				assignedcount++;
			}
			
			//If no elements were added, blank out the return model
			if(pos == 0){
				complexQryModel={};
			}
			
			return complexQryModel;
		
        
        }
		
		
});
});

