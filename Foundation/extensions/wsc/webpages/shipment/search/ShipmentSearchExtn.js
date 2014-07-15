scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/search/ShipmentSearchExtnUI",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/EventUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
		_dojodeclare,
		_extnShipmentSearchExtnUI,
		_scBaseUtils,  
		_scModelUtils,
		_scScreenUtils,  
		_scEventUtils,
		_scWidgetUtils
){ 
	return _dojodeclare("extn.shipment.search.ShipmentSearchExtn", [_extnShipmentSearchExtnUI],{
	// custom code here
		
	/******************************
	 * @author: ???
	 * -Function description here-
	 ***********************************/
	extInitialize : function(event, bEvent, ctrl, args) {
		// set the labels and values of the status checkboxes
		var currentScreen = _scEventUtils.getScreenFromEventArguments(args);
		var statusList = currentScreen.getWidgetByUId('ckListStatus');
		// loop through the options and apply the right values
		for (var i=0; i<statusList.options.length; i++) {
			// set the Kohls specific values
			if (_scBaseUtils.equals('Backroom Pick in Progress',statusList.options[i].label[0])) {
				// Backroom Pick in Progress
				// change the status
				statusList.options[i].value = '1100.70.06.20';
			} else if (_scBaseUtils.equals('Ready for Backroom Pick',statusList.options[i].label[0])) {
				// Ready for Backroom Pick
				// change the status
				statusList.options[i].value = '1100.70.06.10';
			} else if (_scBaseUtils.equals('Ready for Customer',statusList.options[i].label[0])) {
				// Ready for Customer
				// change the status
				statusList.options[i].value = '1100.70.06.30';
			} else if (_scBaseUtils.equals('Shipment Shipped',statusList.options[i].label[0])) {
				// Shipment Shipped
				// change the status
				statusList.options[i].label[0] = 'Complete';
				statusList.options[i].value = '1400.1';
			}	
		}
		// update the enterprise code to blank to allow search
        var targetModel = null;
        targetModel = _scBaseUtils.getTargetModel(this, this.SST_getSearchNamespace(),null);
        targetModel.Shipment.EnterpriseCode = '';
	}
	
	
});
});

