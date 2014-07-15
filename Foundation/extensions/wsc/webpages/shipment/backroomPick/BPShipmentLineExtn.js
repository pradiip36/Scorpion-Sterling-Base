
scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/backroomPick/BPShipmentLineExtnUI",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!sc/plat/dojo/utils/EventUtils", 
          "scbase/loader!isccs/utils/ContextUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils"
          ],
function(			 
		_dojodeclare,
		_extnBPShipmentLineExtnUI,
		_isccsUIUtils,
		_scScreenUtils,
		_scBaseUtils,
		_scWidgetUtils,
		_scEventUtils,
		_isccsContextUtils,
		_scModelUtils
){ 
	return _dojodeclare("extn.shipment.backroomPick.BPShipmentLineExtn", [_extnBPShipmentLineExtnUI],{
		// custom code here
		/*************************************
		 * @author: Robert Fea/Randy Washington/Adam Dunmars
		 * @description: This function hides widgets based on the screen's screen mode.
		 * If it is on the Shipment Details screen(ReadOnly), it will hide the
		 * Undo button, Sales floor quantity label, and Stock room quantity label.
		 * 
		 * If it is on the Item Scan screen (ItemScanMobile), it will hide the Undo
		 * button and the Sales floor quantity label or the Stock room quantity label
		 * based on the current context of the screen.
		 * TODO: This should show Jewelry Icon if its an order
		 * TODO: Change equals 
		 *************************************/
		extnInitialize: function(event, bEvent, ctrl, args){	
			var parentScreen = this.getOwnerScreen(true);
			var screenMode = parentScreen.screenMode;
			var shipmentLineModel = _scScreenUtils.getModel(this, "ShipmentLine");
			
			console.log("Shipment Line Model: ", shipmentLineModel);
			
			if (_scBaseUtils.equals(screenMode, "ReadOnlyMobile")){
				_scWidgetUtils.hideWidget(this, "extn_datalabelStockroomQuantity", false);
				_scWidgetUtils.hideWidget(this, "extn_datalabelSalesFloorQuantity", false);
				_scWidgetUtils.hideWidget(this,"extn_buttonUndoButton", false); 
			} else if(_scBaseUtils.equals(screenMode, "ItemScanMobile")) {
				_scWidgetUtils.hideWidget(this, "extn_buttonUndoButton", false);
				this.checkContext();
			} else if(_scBaseUtils.equals(screenMode, "ItemScan")) {
				_scWidgetUtils.hideWidget(this, "shortageReasonFilterSelect", false);
				if(_scBaseUtils.greaterThan(Number(shipmentLineModel.ShipmentLine.Quantity), 
						Number(shipmentLineModel.ShipmentLine.BackroomPickedQuantity))) {
					_scWidgetUtils.showWidget(this, "updateShortageResolutionImage", false, null);
				}
				
				_scWidgetUtils.hideWidget(parentScreen, "pickAll", false);
				_scWidgetUtils.hideWidget(this, "extn_linkShortDescription", false);
				_scWidgetUtils.hideWidget(this, "extn_buttonUndoButton", false);
				_scWidgetUtils.hideWidget(this, "extn_jewelryIcon", false);
				this.checkContext();
			}
						
			if(!_scBaseUtils.isVoid(shipmentLineModel.ShipmentLine)
				&& !_scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.Extn)
				&& !_scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.Extn.ExtnShortageReason)) {
				var shortageReason = shipmentLineModel.ShipmentLine.Extn.ExtnShortageReason;
				_scWidgetUtils.showWidget(this, "extn_shortageReasonDataLbl", false, null);
				_scWidgetUtils.setValue(this, "extn_shortageReasonDataLbl", shortageReason);
			}
			
			if(this.isJewelryShipment(shipmentLineModel)) {
				_scWidgetUtils.showWidget(this, "extn_jewelryIcon", false, null);
			} else {
				_scWidgetUtils.hideWidget(this, "extn_jewelryIcon", false);
			}
			var eventDefn = {};
			var args = {};
			_scEventUtils.fireEventInsideScreen(this, "afterScreenLoad", eventDefn, args);
		},
		
		/********************************
		 * @author: Adam Dunmars 
		 * @description: This function determines
		 * if a shipment has jewelry items.
		 *********************************/
		isJewelryShipment : function(shipmentLineModel) {
			var departmentCode = _scModelUtils.getStringValueFromPath("ShipmentLine.OrderLine.ItemDetails.Extn.ExtnDept", shipmentLineModel);
			if(_scBaseUtils.isVoid(departmentCode)) {
				console.warn("WARNING: ShipmentLine.OrderLine.ItemDetails.Extn.ExtnDept is N/A! Model: ", shipmentLineModel);
				return false;
			} else {
				switch(departmentCode) {
					case "527":
					case "927":
						return true;
					default:
						return false;
				}
			}	
		},
		
		/***************************************************************
		 * @author: Adam Dunmars
		 * @description: This function will be called after a raise to an event "pickingContextChanged"
		 * It checks the current contexts and updates the shipment lines based on the 
		 * current context. 
		 *****************************************************************/
		checkContext: function(event, bEvent, ctrl, args){
			var parentScreen = this.getOwnerScreen();
			var contextWidget = parentScreen.getWidgetByUId("extn_hfItemPickingLocation");
			var pickingContext = contextWidget.value;
			if(_scBaseUtils.equals(pickingContext, 1)) {
				_scWidgetUtils.hideWidget(this, "extn_datalabelStockroomQuantity", false);
				_scWidgetUtils.showWidget(this, "extn_datalabelSalesFloorQuantity", false, null);
			} else if(_scBaseUtils.equals(pickingContext, 2)) {
				_scWidgetUtils.hideWidget(this, "extn_datalabelSalesFloorQuantity", false);
				_scWidgetUtils.showWidget(this, "extn_datalabelStockroomQuantity", false, null);
			} else {
				_scWidgetUtils.hideWidget(this, "extn_datalabelSalesFloorQuantity", false);
				_scWidgetUtils.hideWidget(this, "extn_datalabelStockroomQuantity", false);
			}
			
			this.displayUndoButton(pickingContext);
			
		},
		
		/**************************************
		 * @author: Adam Dunmars
		 * @description: This function will display
		 * the undo button based on the picking context.
		 ****************************************/
		displayUndoButton : function(pickingContext) {
			var quantityValue = Number(this.getWidgetByUId("extn_txtScannedQuantity").value);
			if(_scBaseUtils.equals(pickingContext, 1) && _scBaseUtils.greaterThan(quantityValue, 0)) {
				this.showUndoButton();
			} else {
				this.hideUndoButton();
			}
		},
		
		/***************************
		 * @author: Adam Dunmars/Randy Washington
		 * @description: Dynamic binding function to display the Kohls UPC
		 ***************************/
		getKohlsUPC:function(dataValue, screen, widget, namespace, modelObj, options){
			var upc = "N/A";
			if (!_scBaseUtils.isVoid(dataValue)
					&& !_scBaseUtils.isVoid(dataValue.ItemAlias)
					&& !_scBaseUtils.isVoid(dataValue.ItemAlias.length)) {
				// default to the first element, then loop through to see if one of them starts with 4
				upc = dataValue.ItemAlias[0].AliasValue;
				for (var i=0; i < dataValue.ItemAlias.length; i++) {
					if (dataValue.ItemAlias[i].AliasValue.match("^400")) {
						// found the kohl's upc
						upc = dataValue.ItemAlias[i].AliasValue;
						break;
					} 
				}
			}
			return upc;
		},
		
		/***************************
		 * @author: Randy Washington / Adam Dunmars
		 * @description: Dynamic binding function to display, size or color, or both.
		 * TODO: This probably needs to be renamed and expanded based on the item's returned attributes.
		 * TODO: This function may need additional conditional checks. 
		 ***************************/
		getSizeColor:function(dataValue, screen, widget, namespace, modelObj, options){
			var itemDetails = modelObj.ShipmentLine.OrderLine.ItemDetails;
			
			var size = null;
			var color = null;
			if(!_scBaseUtils.isVoid(itemDetails) && !_scBaseUtils.isVoid(itemDetails.Extn)) {
				if(!_scBaseUtils.isVoid(itemDetails.Extn.ExtnSizeDesc)) {
					size = itemDetails.Extn.ExtnSizeDesc;
				}
				if(!_scBaseUtils.isVoid(itemDetails.Extn.ExtnColorDesc)) {
					color = itemDetails.Extn.ExtnColorDesc;
				}
				if(!_scBaseUtils.isVoid(size) && !_scBaseUtils.isVoid(color)) {
					return size + ", " + color;
				} else if(!_scBaseUtils.isVoid(size)) {
					return size;
				} else if(!_scBaseUtils.isVoid(color)) {
					return color;
				} else {
					return " ";
				}
			} else {
				return " ";
			}
		},
	
		/*************************
		 * @author: Adam Dunmars
		 * @description: This function is an event handler from the Undo dialog box. 
		 * TODO: Add something to store this event occurring?
		 *******************************/
		handleUndo : function(arg1, arg2, arg3, arg4) {
			if(_scBaseUtils.equals(arg1, "Ok")) {
				this.performUndo();
			}
		},
		
		/******************************************
		 * @author: Adam Dunmars /??? (Randy?+)
		 * @description: This function performs the undo and determines based on 
		 * the picking context if a call to MLS needs to be made.
		 * TODO: Need to determine if this needs to actually happen
		 * in both Stockroom/Salesfloor
		 *******************************************/
		performUndo: function() {
			var currentQuantity = null;
			
			var parentScreen = this.getOwnerScreen();
			var pickingContextWidget = parentScreen.getWidgetByUId("extn_hfItemPickingLocation");
			var pickingContextValue = pickingContextWidget.value;
			
			if(_scBaseUtils.equals(pickingContextValue, 2)) {
				//Update MLS.
			}
			
			var model = _scScreenUtils.getTargetModel(this, "ShipmentLine_Output");
			
			//Update OMS
			var shipmentKey = model.ShipmentLine.ShipmentKey;
			var shipmentLineKey = model.ShipmentLine.ShipmentLineKey;
			var backroomPickedQuantity = model.ShipmentLine.BackroomPickedQuantity;
			
			var input = {};
			input.Shipment = {};
			input.Shipment.ShipmentKey = shipmentKey;
			input.Shipment.ShipmentLines = {};
			input.Shipment.ShipmentLines.ShipmentLine = [];
			input.Shipment.ShipmentLines.ShipmentLine[0] = {};
			input.Shipment.ShipmentLines.ShipmentLine[0].ShipmentLineKey = shipmentLineKey;
			input.Shipment.ShipmentLines.ShipmentLine[0].BackroomPickedQuantity = backroomPickedQuantity - 1;
			
			_isccsUIUtils.callMashup(this, input, "extn_undoScan", null);
		},
		
		/***************************
		 * @author: Adam Dunmars
		 * @description: This function decrements the quantities from the PickAll models and
		 * updates the UI after a successful undo scan.
		 * TODO: Determine if we should show complete scan only after all items are scanned.
		 * TODO: Discover root of shortage issue. May be able to dynamically change the uID to fix problem
		 ***************************/
		updateUndoUI : function() {
			var STOCK_ROOM_CONTEXT = 2;
			var parentScreen = this.getOwnerScreen();
			var model2 = _scScreenUtils.getTargetModel(this, "PickAll_Output");
			var model3 = _scScreenUtils.getModel(this, "PickAll_Input");
			
			console.log("PickAll_Output", model2);
			console.log("PickAll_Input", model3);
			if(_scBaseUtils.equals(model2.PickedQty, Math.floor(model2.Quantity))) {
				if(_scBaseUtils.equals(this.getOwnerScreen().screenMode, "ItemScanMobile")) {
					_scWidgetUtils.hideWidget(this, "productScanImagePanel", false);
					_scWidgetUtils.showWidget(this, "updateShortageResolutionImage", false, null);
				} else if(_scBaseUtils.equals(parentScreen.screenMode, "ItemScan")) {
					_scWidgetUtils.hideWidget(this, "productScanImagePanel", false);
					_scWidgetUtils.hideWidget(this, "updateShortageResolutionImage", false); 
				}
			} if(_scBaseUtils.equals(parentScreen.screenMode, "ItemScan")) {
				_scWidgetUtils.showWidget(this, "updateShortageResolutionImage", false, null);
			}
		
			//_scWidgetUtils.hideWidget(parentScreen,"extn_buttonComplete", false);
			
			model2.PickedQty = model2.PickedQty-1;
			model3.Quantity = model3.Quantity-1;
			
			_scScreenUtils.setModel(this, "PickAll_Output", model2);
			_scScreenUtils.setModel(this, "PickAll_Input", model3);
			
			currentQuantity = parseInt(model2.PickedQty);
			var pickingContext = this.getPickingContext();
			
			if(_scBaseUtils.numberLessThanOrEqual(currentQuantity, 0)
			|| _scBaseUtils.equals(pickingContext, STOCK_ROOM_CONTEXT)) {
				this.hideUndoButton();
			}
		},
		
		/***************************************
		 * @author: Adam Dunmars
		 * @description: This function gets the 
		 * picking context from the parent screen.
		 **************************************/
		getPickingContext :function() {
			var parentScreen = this.getOwnerScreen();
			var pickingContext = parentScreen.getWidgetByUId("extn_hfItemPickingLocation").value;
			return pickingContext;
		},
		
		/*************************************************************************
		 * @author: Adam Dunmars
		 * @description: This function displays the new screen for the shortage reason selection 
		 * in a simple pop-up.
		 ************************************************************************/
		onClickShortage: function(event, bEvent, ctrl, args) {
			_scEventUtils.stopEvent(bEvent);
			var popupParams = _scBaseUtils.getNewBeanInstance();
			var dialogParams = _scBaseUtils.getNewBeanInstance();
			var screenName = "extn_Confirm_Shortage";
			var path = "extn.shipment.backroomPick.ShortageReasonDialog";
			_isccsUIUtils.openSimplePopup(path, screenName, this, popupParams, dialogParams);
		},
	 
	   /***************************
		 * @author: Adam Dunmars
		 * @description: This function displays a dialog box for undoing a scan 
		 * based on the picking context.
		 ***************************/
		undoQuantity : function(event, bEvent, ctrl, args) {
			var parentScreen = this.getOwnerScreen();
			var pickingContextWidget = parentScreen.getWidgetByUId("extn_hfItemPickingLocation");
			var pickingContextValue = pickingContextWidget.value;
			
			var message = (_scBaseUtils.equals(pickingContextValue, 2)) ?
				"Merchandise must be scanned back into MLS. Go to MLS Backstock function to complete process." :
				"Are you sure you want to undo this unit? Merchandise must be placed back on sales floor.";
			
			_scScreenUtils.showConfirmMessageBox(this, message, "handleUndo", null, null);
		},
	
		/*******************************
		 * @author: ??? (Randy?)
		 * @description: This dynamic binding function reformats the Picked Quantity to be the floor of the attribute.
		 * TODO: Verify that this is pulling from the correct attribute...
		 * TODO: Rename this function
		 ******************************/
		convertPickedQtyToInt:function(dataValue, screen, widget, namespace, modelObj){
			if(_scBaseUtils.isVoid(dataValue)){
				console.error("Quantity value wasn't added for either BackroomPickedQuantity " +
						"or Quantity with screenMode "+screen.screenMode);
				return "0";
			} else {
				return dataValue;
			}
		},
		
		/****************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function
		 * returns the maximum quantity for a shipment line.
		 ******************************************/
		getQuantity : function(dataValue, screen, widget, namespace, modelObj) {
			return (_scBaseUtils.isVoid(dataValue)) ? "0" : parseInt(dataValue);
		},
		
		/********************************
		 * @author: Adam Dunmars/ Randy Washington
		 * @description: This dynamic binding function reformats the StockroomQuantity to be the floor of the attribute.
		 * TODO: Rename this function..
		 ********************************/
		convertStockroomQtyToInt:function(dataValue, screen, widget, namespace, modelObj){
			var quantity = "N/A";
			
			var shipmentLineModel = _scScreenUtils.getModel(this, "ShipmentLine");
			if(!_scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.StockRoomQuantity)){
				quantity = parseInt(shipmentLineModel.ShipmentLine.StockRoomQuantity);
			}
			 
			return quantity;
		},
	
		/********************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function reformats the SalesfloorQuantity to be the floor of the attribute.
		 *********************************/
		convertSalesFloorQtyToInt:function(dataValue, screen, widget, namespace, modelObj){
			var quantity = "N/A";
			
			var shipmentLineModel = _scScreenUtils.getModel(this, "ShipmentLine");
			if(!_scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.SalesFloorQuantity)){
				quantity = parseInt(shipmentLineModel.ShipmentLine.SalesFloorQuantity);
			}
			
			return quantity;
		},
		
		/************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function displays the list of stockroom locations retrieved from MLS.
		 * Stockroom only.
		 ***********************************/
		getStockRoomLocations: function(dataValue, screen, widget, namespace, modelObj) {
	        var locations = "";
	        
			var shipmentLineModel = _scScreenUtils.getModel(this, "ShipmentLine");
			if(!_scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.Locations) && !_scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.Locations.Location)){
				for(var j = 0; j < shipmentLineModel.ShipmentLine.Locations.Location.length; j++) {
					locations += shipmentLineModel.ShipmentLine.Locations.Location[j].LocationDesc + "\n";
				}
			}
	        
	        return (_scBaseUtils.isVoid(locations)) ? "N/A" : locations;
		},
		
		/********************************
		 * @author: Adam Dunmars
		 * @description: ADD DESCRIPTION HERE
		 *********************************/
		checkForUndo : function() {
			var parentScreen = this.getOwnerScreen();
			var lastScannedInputModel = _scScreenUtils.getModel(parentScreen, "translateBarCode_output");
			console.log(lastScannedInputModel);
			var currentShipmentLine = _scScreenUtils.getModel(this, "ShipmentLine");
			var currentShipmentLineKey = currentShipmentLine.ShipmentLine.ShipmentLineKey;
			
			var pickingContext = this.getPickingContext();
			var SALES_FLOOR_CONTEXT = 1;
			var pickAllOutputModel = _scScreenUtils.getTargetModel(this, "PickAll_Output"); 
			var pickedQuantity = pickAllOutputModel.PickedQty;
			
			var totalNumberOfRecords = Number(_scModelUtils.getStringValueFromPath("BarCode.Translations.TotalNumberOfRecords", lastScannedInputModel));
			
			if(_scBaseUtils.equals(totalNumberOfRecords, 1)){
				var shipmentLineKey = lastScannedInputModel.BarCode.Translations.Translation[0].ShipmentContextualInfo.ShipmentLineKey;
				
				if((_scBaseUtils.equals(currentShipmentLineKey, shipmentLineKey)
					&& _scBaseUtils.greaterThan(pickedQuantity, 0)) 
					|| 
					(!_scBaseUtils.equals(currentShipmentLineKey, shipmentLineKey)
						&& _scBaseUtils.equals(pickingContext, SALES_FLOOR_CONTEXT)
						&& _scBaseUtils.greaterThan(pickedQuantity, 0)) ){
					this.showUndoButton();
			  	} else {
					this.hideUndoButton();
				}
			}
		},
		
		/*********************************
		 * @author: Adam Dunmars
		 * @description: This function handles the mashup from an undo scan.
		 * TODO: Replace this with the THE BETTER way of handling mashups. ~Adam
		 *********************************/
		handleMashupOutput: function(mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
			if (_scBaseUtils.equals(mashupRefId, "extn_undoScan")) {
				this.updateUndoUI();
			}
			if (_scBaseUtils.equals(mashupRefId, "updateLocationInventory")) {
			 var shipmentLinePickedModel = mashupContext;
			}
	        if (_scBaseUtils.equals(mashupRefId, "updateShortageLineToShipment")) {
	            this.updateShortageResolved(mashupRefId, mashupContext);
	        }
	        if (_scBaseUtils.equals(mashupRefId, "addShortageReasonNote")) {
	            this.updateShortageResolved(
	            mashupRefId, mashupContext);
	        }
		},
		
		/*********************************************
		 * @author: Adam Dunmars 
		 * @description: ADD DESCRIPTION HERE
		 *********************************************/
		updateShortageReason : function(mashInput) {
			var ShipmentModel = this.getModel("ShipmentLine");
			console.log('Shipment Model: ', ShipmentModel);
			mashInput.Shipment.ShipmentKey = ShipmentModel.ShipmentLine.ShipmentKey;
			mashInput.Shipment.ShipmentLines.ShipmentLine.ShipmentLineKey = ShipmentModel.ShipmentLine.ShipmentLineKey;
			console.log("Mashup Input: ", mashInput);
			_isccsUIUtils.callMashup(this, mashInput, "extn_setShortageReason", null);
		},
		
		/*************************
		 * @author: Adam Dunmars
		 * @description: This function overrides the out of box 
		 * openItemDetails() to change the input for the
		 * mashup to be aligned with that the service expects.
		 ************************/
		openItemDetails: function(event, bEvent, ctrl, args) {
	        var isMobile = null;
	        isMobile = _isccsContextUtils.getFromContext("isMobile");
	        if (_scBaseUtils.isBooleanTrue(isMobile)) {
	            var itemDetails_ip = null;
	            var shipmentLineModel = null;
	            var shipmentLineKey = null;
	            var shipmentKey = null;
	            
	            shipmentLineModel = _scScreenUtils.getModel(this, "ShipmentLine");
	            shipmentLineKey = _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", shipmentLineModel);
	            shipmentKey = _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentKey", shipmentLineModel);
	            
	            itemDetails_ip = _scBaseUtils.getNewModelInstance();
	            itemDetails_ip.Shipment = {};
	            itemDetails_ip.Shipment.ShipmentKey = shipmentKey;
	            itemDetails_ip.Shipment.ShipmentLines = {};
	            itemDetails_ip.Shipment.ShipmentLines.ShipmentLine = {};
	            itemDetails_ip.Shipment.ShipmentLines.ShipmentLine.ShipmentLineKey = shipmentLineKey;
	            console.log(itemDetails_ip);
	            
	            _isccsUIUtils.openWizardInEditor("isccs.item.wizards.itemDetails.ItemDetailsWizard", itemDetails_ip, "isccs.editors.ItemEditor", this);
	        }
	    },
	    
	    /******************************
	     * @author: Adam Dunmars
	     * @subscribedTo: successfulScan
	     * @description: This function shows the shortage button and hides
	     * the dropdown after a successful scan.
	     *******************************/
	    showCorrectShortageButton : function(){
	    	var shipmentLineModel = _scScreenUtils.getModel(this, "ShipmentLine");
	    	var model = _scScreenUtils.getTargetModel(this, "PickAll_Output");
			var model2 = _scScreenUtils.getModel(this, "PickAll_Input");
	    	var totalQuantity = shipmentLineModel.ShipmentLine.Quantity;
	    	console.log("Pick All Model: ", model);
	    	console.log("Pick All Model: ", model2);
	    	var pickedQuantity = model.PickedQty;
	    	if(!_scBaseUtils.greaterThanOrEqual(Number(pickedQuantity), Number(totalQuantity))) {
	    		_scWidgetUtils.hideWidget(this, "shortageReasonFilterSelect", false); 
				_scWidgetUtils.showWidget(this, "updateShortageResolutionImage", false, null);
	    	}
	    },
	    
	    /************************************
	     * @author: Adam Dunmars
	     * @description: This function shows the undo button.
	     * TODO: Use this function everywhere.
	     * TODO 2: Add constants from this to ConstUtils
	     *************************************/
	    showUndoButton : function() {
	    	_scWidgetUtils.showWidget(this, "extn_buttonUndoButton", false, null);
	    },
	    
	    /*************************************
	     * @author: Adam Dunmars
	     * @description: This function hides the undo button.
	     * TODO: Use this function everywhere.
	     * TODO 2: Add constnants from this to ConstUtils
	     **************************************/
	    hideUndoButton: function() {
	    	_scWidgetUtils.hideWidget(this, "extn_buttonUndoButton", false, null);
	    }
	});
});