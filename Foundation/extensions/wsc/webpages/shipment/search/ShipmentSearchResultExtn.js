scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/shipment/search/ShipmentSearchResultExtnUI","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!sc/plat/dojo/utils/EventUtils","scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnShipmentSearchResultExtnUI
			 ,  _scBaseUtils
			 ,  _scModelUtils
			 ,  _scScreenUtils
			 ,  _scEventUtils
			 ,  _scWidgetUtils
){ 
	return _dojodeclare("extn.shipment.search.ShipmentSearchResultExtn", [_extnShipmentSearchResultExtnUI],{
	// custom code here
	applyCustomFilters: function(
	event, bEvent, ctrl, args) {
		var currentScreen = _scEventUtils.getScreenFromEventArguments(args);
                var parentScreen = currentScreen.getOwnerScreen(true);
		
		// grab all the custom filters
		
			var inputModel = null;
	                inputModel =  _scBaseUtils.getAttributeValue("inputData", false, args); 
			var statusList = _scModelUtils.getModelObjectFromPath("Shipment.StatusList.Status", inputModel);
		// expired checkbox
		var expiredOrdersModel = _scScreenUtils.getTargetModel(
			parentScreen, "extn_expiredOrders_input", null);
		if ( _scBaseUtils.equals(
		            _scModelUtils.getStringValueFromPath("isChecked", 
			expiredOrdersModel), "Y")) {
			// apply expired order status
			// add it to the status list
			if (!_scBaseUtils.isVoid(statusList)) {
	                	statusList[statusList.length] = '1400.2';
			} else {
				// only selection
				inputModel.Shipment.StatusList = {};
				inputModel.Shipment.StatusList.Status = [];
				inputModel.Shipment.StatusList.Status[0] = '1400.2';	
			}
		}
		if (!_scBaseUtils.isVoid(statusList)) {
			for (var i=0; i < statusList.length; i++) {
				if (_scBaseUtils.equals(statusList[i],'1400')) {
					// change it to the custom status of 1400.1
					statusList[i] = '1400.1';
					break;
				}
				
			}
		}
	},
	clearEnterpriseCode: function( event, bEvent, ctrl, args) {
		 // update the enterprise code to blank to allow search
        	var targetModel = null;
        	targetModel = _scBaseUtils.getAttributeValue("inputData",false,args);
        	targetModel.Shipment.EnterpriseCode = '';
	}
});
});

