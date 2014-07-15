scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/backroomPick/BPUpdateHoldLocationExtnUI",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!sc/plat/dojo/utils/EditorUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!wsc/utils/BackroomPickUtils",
          "scbase/loader!isccs/utils/BaseTemplateUtils",
          "scbase/loader!isccs/utils/WizardUtils"]
,
function(			 
			    _dojodeclare,
			    _extnBPUpdateHoldLocationExtnUI,
			    _isccsUIUtils,
			    _scEditorUtils, 
			    _scScreenUtils,
			    _scModelUtils,
			    _scBaseUtils,
			    _wscBackroomPickUtils,
			    _isccsBaseTemplateUtils,
			    _isccsWizardUtils
){ 
	return _dojodeclare("extn.shipment.backroomPick.BPUpdateHoldLocationExtn", [_extnBPUpdateHoldLocationExtnUI],{
	// custom code here
		
		
		/***********************************
		 * @author: Adam Dunmars
		 * This function hides the confirm buttons on the BackroomPickup wizard
		 * after the initScreenLoad event.
		 * The extn_complete button is used instead.
		 *****************************************/
		hideWizardButtons : function(event, bEvent, ctrl, args) {
			var editor = sc.plat.dojo.utils.EditorUtils.getCurrentEditor();
			var wizard = sc.plat.dojo.utils.EditorUtils.getScreenInstance(editor);
			if(wizard instanceof sc.plat.dojo.widgets.WizardUI) {
				wizard = wizard.getCurrentWizardInstance();
			}
			_isccsWizardUtils.hideNavigationalWidget(wizard, "confirmBttn", false);
		},
		
		/*******************************
		 * @author: Robert Fea(?)
		 * TODO: Description
		 *******************************/
		extInitialize:function(event, bEvent, ctrl, args) {		
			var shipmentLinePickedModel = null;
		        shipmentLinePickedModel = _scScreenUtils.getModel(this, "ShipmentModel");
			// set the hold location from extended attributes
			if (!_scBaseUtils.isVoid(shipmentLinePickedModel.Shipment.Extn) && !_scBaseUtils.isVoid(shipmentLinePickedModel.Shipment.Extn.ExtnHoldLocationDesc)) {
				shipmentLinePickedModel.Shipment.HoldLocation = shipmentLinePickedModel.Shipment.Extn.ExtnHoldLocationDesc;
				// set the hold location data into the custom model
				var extnHoldLocationData = {};
	                        extnHoldLocationData.LocationID = shipmentLinePickedModel.Shipment.Extn.ExtnHoldLocationID;
	                        extnHoldLocationData.LocationDesc = shipmentLinePickedModel.Shipment.Extn.ExtnHoldLocationDesc;
        	                 _scScreenUtils.setModel(this, "extn_holdLocationList",extnHoldLocationData, null);
			}
		},
		
		displayAssignLaterConfirmation: function(event, bEvent, ctrl, args) {
			var message = "Are you sure you want to finish later? The customer will " +
			"not be notified the order is ready until you select complete!";
			_scScreenUtils.showConfirmMessageBox(this, message, "handleAssignLaterConfirmation", null, null);
		},
		
		/***********************************************
		 * @author: Adam Dunmars
		 * This function displays the confirmation dialog message.
		 ************************************************/
		displayCompleteConfirmation : function(event, bEvent, ctrl, args) {
			var message = "Confirm order is complete! Customer will be notified their order is ready for pick-up!";
			_scScreenUtils.showConfirmMessageBox(this, message, "handleCompleteConfirmation", null, null);
		},
		
		/***********************************************
		 * @author: Adam Dunmars
		 * This is the callback function for the displayAssignLater confirmation box.
		 **************************************************/
		handleAssignLaterConfirmation : function(response) {
			console.log("Response", response);
			if(_scBaseUtils.equals(response, "Ok")) {
				this.save();
			}
		},
		
		/*************************************************
		 * @author: Adam Dunmars
		 * This is the callback function for the displayCompleteConfirmation confirmation box.
		 ************************************************/
		handleCompleteConfirmation : function(response) {
			console.log("Response", response);
			if(_scBaseUtils.equals(response, "Ok")) {
				this.save();
			}
		},
		/**********************************************************
		 * @author Adam Dunmars
		 * Sets location in text field and fires a call to updateHoldLocation.
		 **********************************************************/
		setBarcode: function(barcodeString) {
			console.log("in setBarcode");

			var screen = this;
			var uId = "holdLocation";
			var barcode = barcodeString.BarCodeData;
			var dirty = false;
			sc.plat.dojo.utils.WidgetUtils.setValue(screen, uId, barcode, dirty); 

			console.log("out setBarcode");

			this.addHoldLocation();
		},
		
		/************************************************
		 * @author: Robert Fea(?)
		 * TODO: Description
		 ***********************************************/
		save:function(event, bEvent, ctrl, args){
			var updateStatusInput = _scScreenUtils.getModel(this, "ShipmentModel");	
			var shipmentKey = _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", updateStatusInput);
			var shipmentNo = _scModelUtils.getStringValueFromPath("Shipment.ShipmentNo", updateStatusInput);
		
			var changeStatusInput = {};
		
			changeStatusInput.Shipment = {};
			changeStatusInput.Shipment.BaseDropStatus="1100.70.06.50.4";
			changeStatusInput.Shipment.ShipmentKey=shipmentKey;
			changeStatusInput.Shipment.ShipmentNo=shipmentNo;
			changeStatusInput.Shipment.TransactionId="Hold Location.0001.ex";
			_isccsUIUtils.callApi(this, changeStatusInput, "extn_change_status_br_picked", null);
		  
			var editorInstance = null;
            editorInstance = _isccsUIUtils.getEditorFromScreen(this);
            _scEditorUtils.closeEditor(editorInstance);
		},
		
		/************************************************
		 * @author: Robert Fea(?)
		 * TODO: Description
		 ***********************************************/
		addHoldLocation: function(event, bEvent, ctrl, args) {
			var holdLocationModel = null;
			var holdLocationData = null;
			holdLocationModel = _scScreenUtils.getTargetModel(this, "HoldLocation_Add", null);
   			holdLocationData = _scModelUtils.getStringValueFromPath("HoldLocation", holdLocationModel);
			var holdLocationInput = {};
			var shipmentLinePickedModel = null;
           		shipmentLinePickedModel = _scScreenUtils.getModel(this, "ShipmentModel");
			holdLocationInput.Shipment = {};
			holdLocationInput.Shipment.ShipmentKey = shipmentLinePickedModel.Shipment.ShipmentKey;
			holdLocationInput.Shipment.HoldLocationFlag = 'Y';
			var Locations = {};
			Locations.Location = {};
			Locations.Location.LocationID = holdLocationData;
			holdLocationInput.Shipment.Locations = Locations;
			console.log(holdLocationInput);
	      		 // call extn_getLocationDetails_mashup
			_isccsUIUtils.callApi(this, holdLocationInput, "extn_getLocationDetails_mashup", null);
   		},
   		
   		/*************************************************
   		 * @author: Robert Fea
   		 * TODO: Description
   		 **************************************************/
		deleteHoldLocation: function(event, bEvent, ctrl, args) {
	            var holdLocationTarget = null;
        	    holdLocationTarget = _scScreenUtils.getTargetModel(this, "HoldLocation_Output", null);
	            var holdLocationInput = null;
            	    holdLocationInput = _wscBackroomPickUtils.deleteHoldLocation(holdLocationTarget);
            	    var shipmentLinePickedModel = null;
            	    shipmentLinePickedModel = _scScreenUtils.getModel(this, "ShipmentModel");
            	    var holdLocationUpdateModel = null;
            	    holdLocationUpdateModel = _wscBackroomPickUtils.getHoldLocationUpdateModel(holdLocationInput, shipmentLinePickedModel);
		    // need to compare to see which hold location was deleted, will need to delete the id as well from extended attributes
		    var extnHoldLocations = _scScreenUtils.getModel(this, "extn_holdLocationList");
		    var locationIds = extnHoldLocations.LocationID.split(',');
		    var locationDescs = extnHoldLocations.LocationDesc.split(',');
		    var updateDescs = holdLocationUpdateModel.Shipment.HoldLocation.split(',');
		    holdLocationUpdateModel.Shipment.Extn = {};
		    holdLocationUpdateModel.Shipment.Extn.ExtnHoldLocationID = '';
		    holdLocationUpdateModel.Shipment.Extn.ExtnHoldLocationDesc = '';
		    for (var i=0; i < locationIds.length; i++) {
			var found = false;
			// loop through the update list to see what was removed
			for (var j=0; j < updateDescs.length; j++) {
				if (_scBaseUtils.equals(updateDescs[j],locationDescs[i])) {
					found = true;
					break;
				}
			}
			if (found) {
				// add it to the hold location extended attribute updates
				if (_scBaseUtils.isVoid(holdLocationUpdateModel.Shipment.Extn.ExtnHoldLocationID)) {
					holdLocationUpdateModel.Shipment.Extn.ExtnHoldLocationID += locationIds[i];
				} else {
					holdLocationUpdateModel.Shipment.Extn.ExtnHoldLocationID += "," + locationIds[i];
				}
				if (_scBaseUtils.isVoid(holdLocationUpdateModel.Shipment.Extn.ExtnHoldLocationDesc)) {
					holdLocationUpdateModel.Shipment.Extn.ExtnHoldLocationDesc += locationDescs[i];
				} else {
					holdLocationUpdateModel.Shipment.Extn.ExtnHoldLocationDesc += "," + locationDescs[i];
				}
			}
		    }
		    // set it back into the update model
		    extnHoldLocations.LocationID = holdLocationUpdateModel.Shipment.Extn.ExtnHoldLocationID;
		    extnHoldLocations.LocationDesc = holdLocationUpdateModel.Shipment.Extn.ExtnHoldLocationDesc;
		    _scScreenUtils.setModel(this, "extn_holdLocationList",extnHoldLocations, null);
            	    _isccsUIUtils.callApi(this, holdLocationUpdateModel, "saveHoldLocation", null);
        	},
        	
        /***********************************************
         * @author: Robert Fea(?)
         * TODO: Description
         ***********************************************/
		handleExtnMashupCompletion: function(event, bEvent, ctrl, args) {
			if (!_scBaseUtils.isVoid(args.mashupArray) && !_scBaseUtils.isVoid(args.mashupArray[0])) {
				if (_scBaseUtils.equals(args.mashupArray[0].mashupRefId,"extn_getLocationDetails_mashup")) {
					// handle the location call
					// we can use the ExtnHoldLocationDesc to plug into
					// the ootb version of HoldLocation
					if (_scBaseUtils.equals("Y",args.mashupArray[0].mashupRefOutput.Shipment.ExistingLocation)) {
						// existing location, popup warning message
						var message = "This hold area is assigned to another order! Use another hold section or clear out this location.";
						_scScreenUtils.showErrorMessageBox(this, message, null, null, null);
					} else if (_scBaseUtils.equals("N",args.mashupArray[0].mashupRefOutput.Shipment.FoundMLSLocation)) {
						// couldn't find a location, open up popup
						var message = "The scan you provided is an invalid location!";
						_scScreenUtils.showErrorMessageBox(this, message, null, null, null);
					} else {
						_scScreenUtils.setModel(this, "HoldLocation_Input", null, null);
						var holdLocationData = null;
	            				var holdLocationModel = null;
	            				var extnHoldLocationData = {};
						holdLocationData = args.mashupArray[0].mashupRefOutput.Shipment.Extn.ExtnHoldLocationDesc;
						extnHoldLocationData.LocationID = args.mashupArray[0].mashupRefOutput.Shipment.Extn.ExtnHoldLocationID;
						extnHoldLocationData.LocationDesc = args.mashupArray[0].mashupRefOutput.Shipment.Extn.ExtnHoldLocationDesc;
						_scScreenUtils.setModel(this, "extn_holdLocationList",extnHoldLocationData, null); 
	            				holdLocationModel = _wscBackroomPickUtils.getHoldLocationList(holdLocationData);
	            				_wscBackroomPickUtils.repaintChildScreen(this, "HoldLocationListRef", holdLocationModel, "HoldLocationModel");
					}
				}
			}
		}
});
});

/***************************************************************************************************
 * @author: Adam Dunmars
 * -----------------------------------------
 * This function will be called by the scanner in the CEFSharp application whenever the 
 * physical scanner is used.
 * 
 * The function will:
 * 	1. Get the current editor instance.
 * 	2. Get the current wizard instance.
 * 	3. Check to see if the wizard is a vershion of the WizardUI widget(gotcha):
 *		a. If it is, get the current wizard instance(instead of the generic?)
 * 		b. Otherwise use the wizard.
 * 	4. If the screen contains the function setBarcode it will call its version of the function.
 ***************************************************************************************************/
function scannedBarcode(barcodeString) {
	console.log(barcodeString);

	var editor = sc.plat.dojo.utils.EditorUtils.getCurrentEditor();
	var wizard = sc.plat.dojo.utils.EditorUtils.getScreenInstance(editor); 
	if(wizard instanceof sc.plat.dojo.widgets.WizardUI) {
		wizard = wizard.getCurrentWizardInstance();
		var screen = sc.plat.dojo.utils.WizardUtils.getCurrentPage(wizard);
	} else if(wizard instanceof sc.plat.dojo.widgets.Screen) { // There was no wizard
		var screen = wizard;
	} else {
		console.log("Unsupported Type");
	}

	if(typeof screen.setBarcode == "function") {	
		screen.setBarcode(barcodeString);
	}
}