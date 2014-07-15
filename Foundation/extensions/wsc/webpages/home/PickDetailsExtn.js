scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/home/PickDetailsExtnUI",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!wsc/utils/BackroomPickUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!isccs/utils/ContextUtils"]
,
function(			 
	    _dojodeclare,
	    _extnPickDetailsExtnUI,
	    _scScreenUtils,
	    _scModelUtils,
	    _scWidgetUtils,
	    _isccsUIUtils,
	    _wscBackroomPickUtils,
	    _scBaseUtils,
	    _isccsContextUtils
){ 
	return _dojodeclare("extn.home.PickDetailsExtn", [_extnPickDetailsExtnUI],{
		
		/**********************************
		 * @author: ???
		 * @description: INSERT DESCRIPTION HERE
		 * COMMENTED OUT PER DEFECT ON DROP 12 ATM

		 **********************************/
		initializeScreen: function(event, bEvent, ctrl, args) {
			var shipmentModel = null;
			shipmentModel = _scScreenUtils.getModel(this, "Shipment");
			var status = _scModelUtils.getModelObjectFromPath("Shipment.Status", shipmentModel);
			var assignedUser = _scModelUtils.getModelObjectFromPath("Shipment.AssignedToUserId", shipmentModel);
		
			switch(status) {
				case "1100.70.06.10":
					if(_scBaseUtils.isVoid(assignedUser)) {
						_scWidgetUtils.showWidget(this, "extn_assignUser", false, null);
						_scWidgetUtils.hideWidget(this, "btn_PickAction", false);
					} else {
						_scWidgetUtils.hideWidget(this, "extn_assignUser", false);
						_scWidgetUtils.showWidget(this, "btn_PickAction", false, null);
					}
					break;
				case "1100.70.06.20":
					if(_scBaseUtils.isVoid(assignedUser)) {
						_scWidgetUtils.showWidget(this, "extn_assignUser", false, null);
						_scWidgetUtils.hideWidget(this, "extn_continue", false);
					} else {
						_scWidgetUtils.hideWidget(this, "extn_assignUser", false);
						_scWidgetUtils.showWidget(this, "extn_continue", false, null);
					}
					break;
				case "1100.70.06.30":
					_scWidgetUtils.hideWidget(this, "extn_assignUser", false);
					break;
				case "1100.70.06.50.2":
					_scWidgetUtils.hideWidget(this, "extn_assignUser", false);
					break;
				case "1100.70.06.50.4":
					_scWidgetUtils.hideWidget(this, "extn_assignUser", false);
					break;
				default:
					console.warn("WARNING! Unsupported Status Found!");
					_scWidgetUtils.hideWidget(this, "extn_assignUser", false);
					break;
			}
			/*var commonCodeModel = null;
			commonCodeModel = _scScreenUtils.getModel(this, "timeThresholdCommonCodes_output");
			
			var userId = null;
			userId = _scModelUtils.getStringValueFromPath("Shipment.AssignedToUserId", shipmentModel);
	        if (_scBaseUtils.isVoid(userId)) {
	            _scWidgetUtils.hideWidget(this, "img_User", false);
	        }
	        var urlString = null;
	        urlString = _wscBackroomPickUtils.getImageUrlForExpectedShipmentDate(shipmentModel, commonCodeModel);
	        var imageUrlModel = null;
	        imageUrlModel = _scModelUtils.createNewModelObjectWithRootKey("CommonCode");
	        _scModelUtils.setStringValueAtModelPath("CommonCode.CodeLongDescription", urlString, imageUrlModel);
	        _scBaseUtils.setModel(this, "clockImageBindingValues", imageUrlModel, null);*/
	        //console.error(imageUrlModel);
		},
		
		/******************************
		 * @author: ???
		 * @description: ???
		 * TODO: Add framework comparators.
		 *****************************/
		getFormattedTotalSkus:function(){
			var shipmentModel = null;
			shipmentModel = _scScreenUtils.getModel(this, "Shipment"); 
			var totalskus = _scModelUtils.getStringValueFromPath("Shipment.TotalNumOfPickableSKUs", shipmentModel);
			totalskus =parseInt(totalskus);
			
			if(totalskus > 1 || totalskus == 0){
				totalskus = '('+totalskus+" Units"+')' ;
			} else{
				totalskus = '('+totalskus+" Unit"+')' ;
			}
			
			return totalskus;
		} ,
		
		/*********************************
		 * @author:
		 * @description:
		 *********************************/
		updateHold:function(){
			var wizardInputModel = null;
			var shipmentModel = null;
			shipmentModel = _scScreenUtils.getModel(this, "Shipment"); 
			
			wizardInputModel = _wscBackroomPickUtils.buildInputForOpenWizard(shipmentModel, "UpdateHoldLocation");
			_isccsUIUtils.openWizardInEditor("wsc.shipment.wizards.BackroomPickupWizard", wizardInputModel, "wsc.editors.BackroomPickEditor", this);
		},
		
		/********************************
		 * @author:
		 * @description: INSERT DESCRIPTION HERE
		 ******************************/
		EnableUpdateHold :function(){
			var shipmentModel = null;
			shipmentModel = _scScreenUtils.getModel(this, "Shipment");
			var Status = _scModelUtils.getStringValueFromPath("Shipment.Status", shipmentModel);
			
			if(_scBaseUtils.equals( "1100.70.06.50.4",Status)
					||_scBaseUtils.equals("1100.70.06.50.2",Status)){
				_scWidgetUtils.showWidget(this, "extn_update_hold");
			} else if(_scBaseUtils.equals("1100.70.06.30",Status)){
				_scWidgetUtils.hideWidget(this,
				"btn_PickAction", true);
				_scWidgetUtils.showWidget(this,
				"extn_update_hold");
			} else if(_scBaseUtils.equals("1100.70.06.50")){
				/*_scWidgetUtils.showWidget(this,
				"extn_continue");*/
			}
		},
		
		/*****************************************
		 * @author: ???
		 * @description: INSERT DESCRIPTION HERE 
		 ****************************************/
		EnableResume :function(){
			var shipmentModel = null;
			shipmentModel = _scScreenUtils.getModel(this, "Shipment");
			var extnIsSuspended = _scModelUtils.getStringValueFromPath("Shipment.Extn.ExtnSuspendedPickup", shipmentModel);
			
			if(_scBaseUtils.equals( "Y",extnIsSuspended)){
				_scWidgetUtils.showWidget(this,	"extn_Resume");
				_scWidgetUtils.hideWidget(this, "extn_continue", false);
			}
		},
		
		/******************************************
		 * @author: ??? (Maseeh Sabir?)
		 * @description: INSERT DESCRIPTION HERE
		 ******************************************/
		addResumeEJRecord: function() {
			var shipmentModel = null;
			shipmentModel = _scScreenUtils.getModel(this, "Shipment");
		    
			var mashInput = {};
			mashInput.ShipmentStoreEvents = {};
			mashInput.ShipmentStoreEvents.OrderNo = shipmentModel.Shipment.OrderNo;
			mashInput.ShipmentStoreEvents.EventType = "Picking Resumed";
			mashInput.ShipmentStoreEvents.OrderHeaderKey = shipmentModel.Shipment.OrderHeaderKey;
			mashInput.ShipmentStoreEvents.ShipmentKey = shipmentModel.Shipment.ShipmentKey;
	
			_isccsUIUtils.callApi(this, mashInput, "extn_addEJTransPickupResumed", null);		
		},
		
		/****************************************
		 * @author: ??? 
		 * @description: INSERT DESCRIPTION HERE
		 ***************************************/
		showContinue : function(){
			var wizardInputModel = null;
			var shipmentModel = null;
			shipmentModel = _scScreenUtils.getModel(this, "Shipment"); 
			wizardInputModel = _wscBackroomPickUtils.buildInputForOpenWizard(shipmentModel, "ItemScanMobile");
			_isccsUIUtils.openWizardInEditor("wsc.shipment.wizards.BackroomPickupWizard", wizardInputModel, "wsc.editors.BackroomPickEditor", this);
		},
		
		/***************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function returns
		 * the associate assigned to the picking application using
		 * Shipment/Extn/@ExtnUsername. If it is unavailable, it uses
		 * Shipment/@AssignedToUserId.
		 * TODO: May not need to do warning, OOB just leaves blank. Leaving for verbosity ~Adam.
		 ***************************************/
		getUsername : function(dataValue, screen, widget, namespace, modelObj) {
			if(_scBaseUtils.isVoid(dataValue)) {
				if((_scBaseUtils.isVoid(modelObj)
					|| _scBaseUtils.isVoid(modelObj.Shipment)
					|| _scBaseUtils.isVoid(modelObj.Shipment.AssignedToUserId))) {
					console.warn("WARNING: Shipment/Extn/@ExtnUsername AND Shipment/@AssignedToUserId is N/A! Model: ", modelObj);
					return "";
				} else {
					return modelObj.Shipment.AssignedToUserId;
				}
			} else {
				return dataValue;
			}
		},
		
		/***************************************
		 * @author: Adam Dunmars
		 * @subscribedTo: extn_assignUser
		 * @description: This subscriber will display the assign user
		 * dialog box, passing the shipment model from this screen.
		 **************************************/
		showAssignedDialog : function(event, bEvent, ctrl, args) {
			var ShipmentModel = _scScreenUtils.getModel(this, "Shipment"); 
			var popupParams = _scBaseUtils.getNewBeanInstance();
			popupParams.screenInput = {};
			popupParams.screenInput.ShipmentModel = ShipmentModel;
			popupParams.outputNamespace = [];
			popupParams.outputNamespace[0] = "extn_Shipment";
			popupParams.screenConstructorParams = [];
			popupParams.screenConstructorParams["ShipmentModel"] = ShipmentModel;
			var dialogParams = _scBaseUtils.getNewBeanInstance();
			var screenTitle = "extn_Assign_User";
			var path = "extn.shipment.backroomPick.AssignUserDialog";
			_isccsUIUtils.openSimplePopup(path, screenTitle, this, popupParams, dialogParams);
		},
		
		/******************************************
		 * @author: Adam Dunmars
		 * @subscribedTo: extn_assignUser_onClick, extn_assignUser_onKeyUp
		 * @description: This subscriber function will assign the user
		 * to the order and display the pick button.
		 *****************************************/
		assignUser : function(event, bEvent, ctrl, args) {
			var mashupRefIdList = _scBaseUtils.getNewArrayInstance();
			var mashupInputModelList = _scBaseUtils.getNewArrayInstance();
			var mashupInput = _scBaseUtils.getNewModelInstance();
			
			_scBaseUtils.appendToArray(mashupRefIdList, "extn_changeShipmentToInProgress");
			_scBaseUtils.appendToArray(mashupRefIdList, "extn_assignUser");
			
			var shipmentModel = _scScreenUtils.getModel(this, "Shipment");
			var shipmentKey = shipmentModel.Shipment.ShipmentKey;
			
			mashupInput.Shipment = {};
			mashupInput.Shipment.ShipmentKey = shipmentKey;
			
			_scBaseUtils.appendToArray(mashupInputModelList, mashupInput);
			_scBaseUtils.appendToArray(mashupInputModelList, mashupInput);
			
			_isccsUIUtils.callApis(this, mashupInputModelList, mashupRefIdList);
		},
		
		/*******************************************
		 * @author: Adam Dunamrs
		 * @subscribedTo: extn_assignUser_onClick, extn_assignUser_onKeyUp
		 * @description: This function displays widgets after
		 * the assign button has been clicked.
		 *******************************************/
		displayWidgetsAfterAssign : function(username) {
			_scWidgetUtils.hideWidget(this, "extn_assignUser", false);
			_scWidgetUtils.showWidget(this, "btn_PickAction", false, null);
			console.log("Username: ", username);
			_scWidgetUtils.setValue(this, "extn_lbl_AssociateName", username);
		},
		
		/********************************************
		 * @author: Adam Dunmars
		 * @subscribedTo: Whatever the OOB default event that is used. (insert name here)
		 * @description: This function handles extension mash-up files.
		 * TODO: Probably should get username from the output versus current session? ~Adam
		 *******************************************/
		handleExtnMashupCompletion: function(event, bEvent, ctrl, args) {
			var mashupRefId = args.inputData[0].mashupRefId;
			if(!_scBaseUtils.isBooleanTrue(args.hasError)) {
				switch(mashupRefId) {
					case "extn_assignUser":
						var username = JSON.parse(sessionStorage.userInformation).CurrentUser.User.Username;
						this.displayWidgetsAfterAssign(username);
						break;
					case "extn_changeShipmentToInProgress":
						var username = JSON.parse(sessionStorage.userInformation).CurrentUser.User.Username;
						this.displayWidgetsAfterAssign(username);
					default:
						break;
				}
			}			
		}
	});
});