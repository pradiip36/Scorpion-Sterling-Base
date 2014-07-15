scDefine([
          "scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/customerpickup/ResolutionExtnUI",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!isccs/utils/WizardUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/WizardUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
          "scbase/loader!extn/utils/ScanUtils"
          ],
          
	function(			 
			_dojodeclare,
			_extnResolutionExtnUI,
			_isccsUIUtils,
			_isccsWizardUtils,
			_scBaseUtils,
			_scWidgetUtils,
			_scScreenUtils,
			_scWizardUtils,
			_scModelUtils,
			_bopusUtils
	){ 
	return _dojodeclare("extn.shipment.customerpickup.ResolutionExtn", [_extnResolutionExtnUI],{
		// custom code here
		/******************************************************
		 * @author: Adam Dunmars/???
		 * @subscribedTo: afterScreenInit
		 * @description: This subscriber function happens after the 
		 * OOB initializeScreen occurs. This function: 
		 * 		1) Gets the shipment number of the shipment.
		 * 		2)
		 * 		3)
		 * 
		 * TODO: Finish this description
		 * TODO: ALL OF THE MASHUPS HERE CAN BE REMOVED OR CHANGED TO
		 * INITIAL MASHUPS :/ ~Adam.
		 * TODO: NOTE SOME OF THE INITIAL MASHUPS IN THIS SCREEN
		 * CAN PROBABLY BE CONSOLIDATED.
		 *****************************************************/
		extnInitialize: function (event, bEvent, ctrl, args) {
			var model1 = this.getModel("extn_shipNode_info_suspend");
			var model2 = this.getModel("extn_SuspendCommonCodes");
			var model3 = this.getModel("extn_DiamondCommonCodes");
			var model4 = _isccsUIUtils.getWizardModel(this, "PickUpOrderDetailsModel");
			
			console.log("Model 1: ", model1);
			console.log("Model 2: ", model2);
			console.log("Model 3: ", model3);
			console.log("Model 4: ", model4);
			
		},
		
		/***************************************
		 * @author: Robert Fea(?)/Adam Dunmars
		 * @description: INSERT DESCRIPTION HERE.
		 * TODO: orderNo isn't being use.
		 * TODO: Nor totalQuantity?
		 * TODO: Possibly needs status change also.
		 ***************************************/
		suspendShipment : function(event, bEvent, ctrl, args) {
	        // save the shipment information
	        var pickUpOrderDetailsModel = _isccsUIUtils.getWizardModel(this, "PickUpOrderDetailsModel");
	        var orderNo = null;
			var totalQuantity = 0;
		    var input = {};
		    input.Shipment = {};
		    input.Shipment.Extn = {};
		    // set the suspended flag
		    input.Shipment.Extn.ExtnSuspendedPickup = 'Y';
	        input.Shipment.ShipmentKey = pickUpOrderDetailsModel.Shipment.ShipmentKey;
	        input.Shipment.ShipmentLines = {};
	        input.Shipment.ShipmentLines.ShipmentLine = [];
	        // loop through all the child screens to pull the value
	        var shipmentLineLength = pickUpOrderDetailsModel.Shipment.ShipmentLines.ShipmentLine.length;
	        for (var i = 0; i < shipmentLineLength; i++) {
	            // get the current child screen
	            var childScreen = _scScreenUtils.getChildScreen(this, "CustomerPickUp_PickupOrderLineDetails" + i);
	            if (!isNaN(_scWidgetUtils.getValue(childScreen, 'txtScannedQuantity')) && _scWidgetUtils.getValue(childScreen, 'txtScannedQuantity') > 0) {
	                var ShipmentLine = {};
	                ShipmentLine.ShipmentLineKey = _scWidgetUtils.getValue(childScreen, 'ShipmentLineKey');
	                ShipmentLine.Extn = {};
	                ShipmentLine.Extn.ExtnSuspendedPickupQty = _scWidgetUtils.getValue(childScreen, 'txtScannedQuantity');
	                input.Shipment.ShipmentLines.ShipmentLine.push(ShipmentLine);
	            }
				// set the information for the suspend receipt
				orderNo = pickUpOrderDetailsModel.Shipment.ShipmentLines.ShipmentLine[i].OrderNo;
				totalQuantity += pickUpOrderDetailsModel.Shipment.ShipmentLines.ShipmentLine[i].Quantity * 1;
	        }
	        
	        _isccsUIUtils.callMashup(this, input, 'extn_saveSuspendedPickup_mashup', null);

	        this.printSuspendReceipt();

	        this.addEJEntryOrderSuspended();

	        // close the screen
	        var wizardScreen = this.getWizardForScreen(this);
	        _scWizardUtils.closeWizard(wizardScreen);
		},
		
		/*******************************************
		 * @author: Maseeh Sabir(?) / Adam Dunmars
		 * @subscribedTo: extn_VoidButton_onClick & extn_VoidButton_onKeyUp
		 * @description: This function voids and order and prints the receipt for it.
		 *******************************************/
		voidShipmentLines : function(event, bEvent, ctrl, args) {
			var modelOutput = _isccsUIUtils.getWizardModel(this, "PickUpOrderDetailsModel");
	    	var commonModel = _scScreenUtils.getModel(this, "extn_SuspendCommonCodes");
	    	var storeInfo = _scScreenUtils.getModel(this,"extn_shipNode_info_suspend");
	    	var diamondDataModel = _scScreenUtils.getModel(this, "extn_DiamondCommonCodes");
			
			var wizard = this.getWizardForScreen(this);
			
			modelOutput.diamondModel = diamondDataModel;
			modelOutput.ReceiptType = "voidpickup";
			
			_bopusUtils.printReceipt(wizard, modelOutput, commonModel, storeInfo);
		},
		
	    /******************************************************
		 * @author: Adam Dunmars/???(Maseeh Sabir)
		 * @description: This function prints the suspend receipt.
		 *****************************************************/
		printSuspendReceipt: function() {
		    var modelOutput = _isccsUIUtils.getWizardModel(this, "PickUpOrderDetailsModel");
		    var commonModel = _scScreenUtils.getModel(this, "extn_SuspendCommonCodes");
		    var storeInfo = _scScreenUtils.getModel(this,"extn_shipNode_info_suspend");
		    
		    console.log("Model Output: ", modelOutput);
		    console.log("Common Model: ", commonModel);
		    console.log("Store Info: ", storeInfo);
		    
		    modelOutput.ReceiptType = "suspend";
		    
	        _bopusUtils.printReceipt(null, modelOutput, commonModel, storeInfo);
	        _bopusUtils.printReceipt(null, modelOutput, commonModel, storeInfo);
		},
		
		/************************************************
		 * @author: Maseeh Sabir(?)/Adam Dunmars
		 * @description: This function gets called when an
		 * order gets suspended. It adds an entry to Store
		 * event hang off table.
		 ************************************************/
		addEJEntryOrderSuspended : function() {
			var shipmentModel = _isccsUIUtils.getWizardModel(this, "PickUpOrderDetailsModel");
			
			var mashInput = {};
			mashInput.ShipmentStoreEvents = {};
			mashInput.ShipmentStoreEvents.OrderNo = shipmentModel.Shipment.OrderNo;
			mashInput.ShipmentStoreEvents.EventType = "Pickup Suspended";
			mashInput.ShipmentStoreEvents.OrderHeaderKey = shipmentModel.Shipment.OrderHeaderKey;
			mashInput.ShipmentStoreEvents.ShipmentKey = shipmentModel.Shipment.ShipmentKey;

			_isccsUIUtils.callApi(this, mashInput, "extn_addEJTransCustomerVerified", null);
		},
		
		/******************************************************
		 * @author: ???(Robert Fea)?
		 * @description: INSERT DESCRIPTION HERE.
		 * TODO: Good candidate for a utility function in BOPUS utils..
		 *****************************************************/
		getWizardForScreen: function (screen) {
	        if (screen.wizardUI) {
	            return screen;
	        } else {
	            var force = true;
	            var parentScreen = screen.getOwnerScreen(force);
	            if (parentScreen) {
	                return this.getWizardForScreen(parentScreen);
	            }
	        }
	        return null;
	    }
	});
});