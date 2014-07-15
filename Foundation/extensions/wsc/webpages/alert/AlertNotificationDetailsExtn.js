scDefine(["scbase/loader!dojo/_base/declare",
			"scbase/loader!extn/alert/AlertNotificationDetailsExtnUI",
			"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
			"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
			"scbase/loader!sc/plat/dojo/widgets/DataLabel",
			"scbase/loader!sc/plat/dojo/widgets/Label",
			"scbase/loader!sc/plat/dojo/widgets/Screen",
			"scbase/loader!wsc/utils/BackroomPickUtils",
			"scbase/loader!sc/plat/dojo/utils/ModelUtils",
			"scbase/loader!sc/plat/dojo/utils/BaseUtils",
			"scbase/loader!isccs/utils/UIUtils"]
,
function(			 
		  _dojodeclare
			 ,
			    _extnAlertNotificationDetailsExtnUI, _scScreenUtils, _scWidgetUtils, _scDataLabel,
				_scLabel, _scScreen, _wscBackroomPickUtils,_scModelUtils,_scBaseUtils,_isccsUIUtils
){ 
	return _dojodeclare("extn.alert.AlertNotificationDetailsExtn", [_extnAlertNotificationDetailsExtnUI],{
	// custom code here
		hideExistingButtons: function(event, bEvent, ctrl, args){
			var inboxModel = null;
			inboxModel = _scScreenUtils.getModel(this, "Inbox"); 
			var excType = _scModelUtils.getStringValueFromPath("Inbox.ExceptionType", inboxModel); 
			//alert(excType);
			
			  
			
		if (_scBaseUtils.equals("KOHLS_SHIPMENT_READY_FOR_STORE_PICK",excType) || _scBaseUtils.equals("KOHLS_SHIPMENT_NOT_PICKED",excType) ||
				_scBaseUtils.equals("KOHLS_SHIPMENT_NOT_PICKED_TIER2",excType)) {
									_scWidgetUtils.hideWidget(this,
											"btn_PickAction");
									
									_scWidgetUtils.showWidget(this,
									"lbl_exceptionName");
									_scWidgetUtils.showWidget(this,
									"lbl_CustName");
									_scWidgetUtils.showWidget(this,
									"lbl_OrderNum");
									_scWidgetUtils.showWidget(this,
									"lbl_timeRemaining");
									 var getShipmentDetailsTargetModel = null;
									getShipmentDetailsTargetModel = _scScreenUtils.getTargetModel(
							                this, "getShipmentDetails_input", null);
							                _isccsUIUtils.callApi(
							                this, getShipmentDetailsTargetModel, "getShipmentDetails", null);					
									
									
								} 
		else {
			_scWidgetUtils.hideWidget(this,"extn_button_pick");
		}
		},
		pickReadyForPickOrders: function(event, bEvent, ctrl, args){
			//alert("hello");
			var resolveExceptionTargetModel = null;
            resolveExceptionTargetModel = _scScreenUtils.getTargetModel(
            this, "resolveException_input", null);
			var inboxModel = null;
			inboxModel = _scScreenUtils.getModel(this, "Inbox"); 
			var wizardInputModel = null;
            var shipmentModel = null;
            shipmentModel = _scScreenUtils.getModel(
            		this, "getShipmentDetails_output");
            _isccsUIUtils.callApi(
      	           this, resolveExceptionTargetModel, "resolveException", null); 
            //alert(shipmentModel);
            var parentScreen = false;
            parentScreen = _isccsUIUtils.getParentScreen(
            this, true);
			_scWidgetUtils.closePopup(
	                parentScreen, null, false);
						
            wizardInputModel = _wscBackroomPickUtils.buildInputForOpenWizard(
                    shipmentModel, "ItemScanMobile");
                    _isccsUIUtils.openWizardInEditor("wsc.shipment.wizards.BackroomPickupWizard", wizardInputModel, "wsc.editors.BackroomPickEditor", parentScreen);
                
		}
});
});

