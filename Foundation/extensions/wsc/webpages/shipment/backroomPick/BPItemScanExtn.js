scDefine(["scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/shipment/backroomPick/BPItemScanExtnUI",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!wsc/utils/CustomerPickUpUtils",
	"scbase/loader!wsc/utils/BackroomPickUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!isccs/utils/WizardUtils",
	"scbase/loader!dojo/_base/lang",
	"scbase/loader!sc/plat/dojo/widgets/Screen", 
	"scbase/loader!isccs/utils/WizardUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils",
	"scbase/loader!extn/utils/ScanUtils",
	"scbase/loader!dojo/_base/array",
	"scbase/loader!isccs/utils/BaseTemplateUtils",
	"scbase/loader!dojo/dom-style",
	"scbase/loader!dojo/dom-construct",
	"scbase/loader!dojo/query"
	]
,
function(			 
	_dojodeclare,
	_extnBPItemScanExtnUI,
	_scScreenUtils,
	_scBaseUtils,
	_scModelUtils,
	_isccsUIUtils,
	_scEventUtils,
	_wscCustomerPickUpUtils,
	_wscBackroomPickUtils,
	_scWidgetUtils,  
	_wizardUtils,
	dLang,
	scScreen,
	_isccsWizardUtils,
	_editorUtils,
	_scanUtils,
	array,
	_isccsBaseTemplateUtils,
	dDomStyle,
	dDomConstruct,
	dQuery

){ 
	return _dojodeclare("extn.shipment.backroomPick.BPItemScanExtn", [_extnBPItemScanExtnUI],{
		// custom code here
		/******************************************************************************
		 * @author: Adam Dunmars
		 * @subscribedTo: afterScreenInit(OOB)
		 * @description: This subscriber function is called after the screen's 
		 * OOB initializeScreen function for Kohl's specific customizations.
		 * 
		 * The function firsts:
		 * 	1. Display's the screen's widgets.
		 *  2. Creates a model of a mapping between the shipment's items and their location in MLS.
		 *  3. Initializes the picking context of the screen.
		 *  
		 * TODO 1: The base product seems to have a bug in the BackroomPickupUtils.hasShortages() method...need to 
		 * notify the product team about this. If no product has been scanned the and the model is void it returns
		 * that there AREN'T any shortages when there are in fact shortages....(this is for Complete button showing)
		 * TODO 2: Verify if TODO 1 is still valid after Drop12 update.
		 ******************************************************************************/
		extnInitialize : function(event, bEvent, ctrl, args) {
			this.createLocationMap();
			this.initializePickingContext();
		},
		
		/*******************************************
		 * @author: Adam Dunmars
		 * @subscribedTo: afterScreenLoad
		 * @description: This function will display the screen's
		 * expected displayed widgets based on the screen's 
		 * screen mode.
		 * TODO: Use _scBaseUtils.or()
		 *******************************************/
		displayWidgets : function(event, bEvent, ctrl, args) {
			var screenMode = this.screenMode;
			switch(screenMode) {
				case "ReadOnlyMobile":
					this.displayWidgetsReadOnly();
					break;
				case "ItemScan":
					this.displayWidgetsActive();
					break;
				case "ItemScanMobile":
					this.displayWidgetsActive();
					break;
				default:
					console.warn("WARNING: Unsupported screen mode: ", screenMode);
					break;
			}
		},
		
		/******************************************
		 * @author: Adam Dunmars
		 * @description: This function displays widgets that appear only in 
		 * Read-Only screen-mode. It hides certain buttons based on their status.
		 * TODO: Add some utility methods to Scan/BOPUS Utils for checking models and status values. ~Adam
		 * TODO: Determine Kohl's expectations for Complete Button. ~Adam 
		 * TODO: Determine whether some widgets should be initially hidden. ~Adam
		 ******************************************/
		displayWidgetsReadOnly : function() {
			_scWidgetUtils.hideWidget(this, "extn_buttonSuspend", false);
			_scWidgetUtils.hideWidget(this, "extn_buttonRefresh", false);
			_scWidgetUtils.hideWidget(this, "extn_btnItemPickingLocation", false);
			_scWidgetUtils.hideWidget(this, "extn_labelItemPickingLocation", false);
			_scWidgetUtils.hideWidget(this, "extn_lastLocationScanned", false);
			_scWidgetUtils.showWidget(this, "extn_labelOrderDetails", false, null);
			
			var shipmentDetails = _scScreenUtils.getModel(this, "backroomPick_getShipmentDetails_output");
			var status = shipmentDetails.Shipment.Status;
			switch(status) {
				case "1100.70.06.30":
				case "1100.70.06.50.2":
				case "1400":
				case "1400.1":
					_scWidgetUtils.hideWidget(this, "extn_buttonStartPick", false);
					_scWidgetUtils.hideWidget(this, "extn_buttonComplete", false);
					break;
				default:
					_scWidgetUtils.showWidget(this, "extn_buttonStartPick", false, null);
					_scWidgetUtils.showWidget(this, "extn_buttonComplete", false, null);
					break;
			}		
		},
		
		/**********************************
		 * @author: Adam Dunmars
		 * @description: Displays widgets that appear only when
		 * screen is active for scanning.
		 * TODO: Determine Kohl's expectations for Complete Button.
		 * TODO: Maybe not initialize this screen as a performance optimization
		 * rather than hiding it after it creates it...
		 *********************************/
		displayWidgetsActive : function() {
			_scWidgetUtils.showWidget(this, "extn_buttonSuspend", false, null);
			_scWidgetUtils.showWidget(this, "extn_buttonComplete", false, null);
			
			_scWidgetUtils.hideWidget(this, "extn_buttonStartPick", false);
			_scWidgetUtils.hideWidget(this, "extn_labelOrderDetails", false);
			_scWidgetUtils.hideWidget(this, "lastProductScannedDetailsScreenRef", false);
		},
		
		/*******************************
		 * @author: Adam Dunmars
		 * @description: Creates a mapping of MLS locations to Items in the form 
		 * of <MLSLocationID> to a list of <ItemIDs> and sets
		 * them into a model for later use, then
		 * TODO: Remove checks for Shipments element if we have decided on the mashup...~Adam
		 *******************************/
		createLocationMap : function() {
			var shipmentDetails = _scScreenUtils.getModel(this, "backroomPick_getShipmentDetails_output");
			var locationMap = {};
			var shipmentLineCount = null;
			
			console.log("Shipment Details Model", shipmentDetails);
			
			if (!_scBaseUtils.isVoid(shipmentDetails.Shipments)) { 
				shipmentLineCount = shipmentDetails.Shipments.Shipment[0].ShipmentLines.ShipmentLine.length;
			} else {
				shipmentLineCount = shipmentDetails.Shipment.ShipmentLines.ShipmentLine.length;
			}
			
			for(var i = 0; i < shipmentLineCount; i++) {
				var currentShipmentLine = null;
				if (!_scBaseUtils.isVoid(shipmentDetails.Shipments)) {
					currentShipmentLine = shipmentDetails.Shipments.Shipment[0].ShipmentLines.ShipmentLine[i];
				} else {
					currentShipmentLine = shipmentDetails.Shipment.ShipmentLines.ShipmentLine[i];
				}
				
				var itemID = currentShipmentLine.ItemID;
				if(!_scBaseUtils.isVoid(currentShipmentLine.Locations) && !_scBaseUtils.isVoid(currentShipmentLine.Locations.Location )){
					var locations = currentShipmentLine.Locations.Location;
					for(var j = 0; j < locations.length; j++){
						var currentLocation = locations[j];
						var locationID = currentLocation.LocationID;
						if(!(locationID in locationMap)) {
							locationMap[locationID] = {};
							locationMap[locationID].LocationDesc = currentLocation.LocationDesc;
							locationMap[locationID].LocationType = currentLocation.LocationType;
							locationMap[locationID].ItemIDList = [];
							locationMap[locationID].ItemIDList[locationMap[locationID].ItemIDList.length] = itemID;
						} else {
							locationMap[locationID].ItemIDList[locationMap[locationID].ItemIDList.length] = itemID;						
						}
					}
				}
			}
			console.log("Location Map: ", locationMap);
			_scScreenUtils.setModel(this, "extn_mlsLocationsModel", locationMap, null);
		},
		
		/**********************************
		 * @author: Adam Dunmars
		 * @description: This function initializes the 
		 * screen picking context.
		 * TODO: Add an INITIAL_PICKING CONTEXT, SALESFLOOR_CONTEXT,
		 * and STOCKROOM_CONTEXT constant to BopusUtils.
		 ***********************************/
		initializePickingContext : function() {
			var STOCKROOM_CONTEXT = 2;
			var SALESFLOOR_CONTEXT = 1;
			var INITIAL_PICKING_CONTEXT = STOCKROOM_CONTEXT;
			
			var locationLabelValue = null;
			var pickingContextWidget = _scScreenUtils.getWidgetByUId(this,"extn_hfItemPickingLocation");
			pickingContextWidget.value = INITIAL_PICKING_CONTEXT;
			
			switch(INITIAL_PICKING_CONTEXT, STOCKROOM_CONTEXT) {
				case STOCKROOM_CONTEXT:
					locationLabelValue = "Location: Stockroom";
					this.initializeStockroomContext();
					
					break;
				case SALESFLOOR_CONTEXT: 
					locationLabelValue = "Location: Salesfloor";
					break;
				default:
					break;
			}
			_scWidgetUtils.setLabel(this, "extn_labelItemPickingLocation", locationLabelValue);
			_scWidgetUtils.setValue(this, "extn_lastLocationScanned_value", "");
		},
		
		/*******************************************
		 * @author: Adam Dunmars
		 * @description: This function initializes the 
		 * stockroom scanning context to location context, 
		 * so that while in the PICKING context of STOCKROOM, 
		 * the application will be awaiting the scanning of an 
		 * MLS location.
		 * 
		 * TODO: Add LOCATION_CONTEXT and ITEM_CONTEXT constants
		 *  to BOPUS Utils or a Constants Utils file.
		 *******************************************/
		initializeStockroomContext : function() {
			var LOCATION_CONTEXT = "Location";
			var ITEM_CONTEXT = "Item";
			
			var INITIAL_STOCKROOM_CONTEXT = LOCATION_CONTEXT;
			var stockroomContextWidget = _scScreenUtils.getWidgetByUId(this, "extn_stockroomContext");
			stockroomContextWidget.value = INITIAL_STOCKROOM_CONTEXT;
		},
		
		/**********************************************
		 * @author: Adam Dunmars
		 * @description: This subscriber function will check the picking context 
		 * for each shipment line, hiding each individual shipment line's widget based on
		 * the context.
		 * @subscribedTo: afterScreenLoad(OOB)
		 * TODO: This could probably be done on each child screen's initialization.
		 ************************************************/
		checkContextForShipmentLines : function(event, bEvent, ctrl, args) {
			var childScreens = this._allChildScreens;
			for(var i = 0; i < childScreens.length; i++) {
				if(typeof childScreens[i].checkContext == "function") {
					childScreens[i].checkContext();
				}
			}
		},
		
		/****************************************************
		 * @author: Maseeh Sabir
		 * @description: Adds suspend action EJ entry to the EJ entry records
		 * TODO: This probably needs to be updated to NOT pull from the map directly..~Adam
		 ****************************************************/
		addSuspendEJRecord: function() {
			var mashInput = {};
			mashInput.ShipmentStoreEvents = {};
			mashInput.ShipmentStoreEvents.OrderNo = 
				this.sourceOriginalModelMap.backroomPick_getShipmentDetails_output.Shipment.ShipmentLines.ShipmentLine[0].OrderNo;
			mashInput.ShipmentStoreEvents.EventType = "Picking Suspended";
			mashInput.ShipmentStoreEvents.OrderHeaderKey = 
				this.sourceOriginalModelMap.backroomPick_getShipmentDetails_output.Shipment.ShipmentLines.ShipmentLine[0].OrderHeaderKey;
			mashInput.ShipmentStoreEvents.ShipmentKey = 
				this.sourceOriginalModelMap.backroomPick_getShipmentDetails_output.Shipment.ShipmentLines.ShipmentLine[0].ShipmentKey;
	
			_isccsUIUtils.callApi(this, mashInput, "extn_addSuspendEJEntry", null);
		},
			
		/*****************************************************
		 * @author: ??? / Adam
		 * @description: This dynamic binding function used to return the customer's name.
		 *****************************************************/
		getCustomerName : function(dataValue, screen, widget, namespace, modelObj) {
			console.log("Shipment Model: ", modelObj);
			var firstName = _scModelUtils.getStringValueFromPath("Shipment.BillToAddress.FirstName", modelObj);
			var lastName = _scModelUtils.getStringValueFromPath("Shipment.BillToAddress.LastName", modelObj);
			var title = _scModelUtils.getStringValueFromPath("Shipment.BillToAddress.Title", modelObj);
			if(_scBaseUtils.isVoid(firstName) || _scBaseUtils.isVoid(lastName)) {
				console.warn("WARNING: Shipment.BillToAddress.FirstName OR Shipment.BillToAddress.LastName is N/A! Model: ", modelObj);
				return "N/A";
			} else if (_scBaseUtils.isVoid(title)) {
				return firstName + " " + lastName;
			} else {
				return title + " " + firstName + " " + lastName;
			}
			if(!_scBaseUtils.isVoid(modelObj) && !_scBaseUtils.isVoid(modelObj.Shipment)){
				custName = _wscBackroomPickUtils.buildNameFromShipment(this, modelObj.Shipment);
			}
			return custName;
		},
		
		/*****************************************************************
		 * @author: Adam Dunmars
		 * @description: This function checks the location model.
		 * TODO: UPDATE THE DESCRIPTION.
		 ****************************************************************/
		checkLocationModel : function(barCodeData, mlsLocationModel) {
			var eventArgs = _scBaseUtils.getNewBeanInstance();
			var eventDefn = _scBaseUtils.getNewBeanInstance();
			
			if(barCodeData in mlsLocationModel && _scBaseUtils.equals(mlsLocationModel[barCodeData].LocationType, "StockRoom")) {
				_scWidgetUtils.setValue(this, "extn_stockroomContext", "Item");
				_scWidgetUtils.setValue(this, "extn_lastLocationScanned", mlsLocationModel[barCodeData].LocationDesc);
				_scWidgetUtils.setValue(this, "extn_lastLocationScanned_value", barCodeData);
				_scEventUtils.fireEventInsideScreen(this, "resetBarCodeTextField", eventArgs, eventDefn);
			} else if(_scBaseUtils.isVoid(barCodeData) || _scBaseUtils.isVoid(dLang.trim(barCodeData))){
				var message = "Location not entered. Please enter a location into the scan field.";
				var textObj = {};
	    		textObj.OK = "Ok";
				_scScreenUtils.showErrorMessageBox(this, message, "errorMessageCallback", textObj, null);					
			} else {
				_scWidgetUtils.setValue(this, "extn_stockroomContext", "Unknown");
				_scWidgetUtils.setValue(this, "extn_lastLocationScanned", "Unknown");
				_scWidgetUtils.setValue(this, "extn_lastLocationScanned_value", barCodeData);
				_scEventUtils.fireEventInsideScreen(this, "resetBarCodeTextField", eventArgs, eventDefn);
			}
		},
		
		
	    
	    /************************************************
	     * @author: Adam Dunmars
	     * @description: Utility method to get the location description from
	     * the location model solely from a location ID.
	     ************************************************/
	    getLocationDescription : function(locationID) {
	    	var mlsLocationModel = _scScreenUtils.getModel(this, "extn_mlsLocationsModel");
			var currentLocationModel = mlsLocationModel[locationID];
			return (!_scBaseUtils.isVoid(currentLocationModel)) ? currentLocationModel.LocationDesc : null;
	    },
		
		/**************************
		 * @author Adam Dunmars
		 * @description: INSERT DESCRIPTION HERE. ~Adam
		 *************************/
		updateBarCodeModelWithLocation : function(barCodeModel, locationType, currentLocation) {
			barCodeModel.BarCode.LocationContextualInfo = {};
			barCodeModel.BarCode.LocationContextualInfo.LocationId = currentLocation;
			barCodeModel.BarCode.LocationContextualInfo.LocationType = locationType;
			return barCodeModel;
		},
		
		/*****************************************************************
		 * @author: Adam Dunmars
		 * @description: This function will determine whether an MLS call needs to be made
		 * on scan based on the context of the scanning stored in the 
		 * "extn_hfItemPickingLocation" widget and make a call to the BarcodeTranslation
		 * service mashup (which overrides the OOB translateBarCode mashup).
		 * 
		 * TODO: Create helper functions from some of this function and add to ScanUtils...
		 * TODO: And simplify this function (especially in case of the manual entry case...)
		 ****************************************************************/
		scanProduct: function(event, bEvent, ctrl, args, barcodeObject) {
			var barCodeModel = _scScreenUtils.getTargetModel(this, "translateBarCode_input", null);
			var barCodeData = _scModelUtils.getStringValueFromPath("BarCode.BarCodeData", barCodeModel);
			
			barCodeData = (_scBaseUtils.isVoid(barCodeData)) ? " " : dLang.trim(barCodeData);
			
			var mlsHiddenInput = _scScreenUtils.getWidgetByUId(this,"extn_hfItemPickingLocation");
			var mlsLocationModel = _scScreenUtils.getModel(this, "extn_mlsLocationsModel");
			var currentLocationWidget = _scScreenUtils.getWidgetByUId(this, "extn_lastLocationScanned_value");
			var mlsFlag = mlsHiddenInput.value;
			
			if(_scBaseUtils.equals(mlsFlag, 2)) { // In Stockroom Context
				var stockroomContextWidget = _scScreenUtils.getWidgetByUId(this, "extn_stockroomContext");
				var stockroomContextValue = stockroomContextWidget.value;
				var eventArgs = _scBaseUtils.getNewBeanInstance();
				var eventDefn = _scBaseUtils.getNewBeanInstance();
				
				if(_scBaseUtils.equals(stockroomContextValue, "Location")) {
					if(barCodeData in mlsLocationModel && _scBaseUtils.equals(mlsLocationModel[barCodeData].LocationType, "StockRoom")) {
						_scWidgetUtils.setValue(this, "extn_stockroomContext", "Item");
						_scWidgetUtils.setValue(this, "extn_lastLocationScanned", mlsLocationModel[barCodeData].LocationDesc);
						_scWidgetUtils.setValue(this, "extn_lastLocationScanned_value", barCodeData);
						_scEventUtils.fireEventInsideScreen(this, "resetBarCodeTextField", eventArgs, eventDefn);
					} else  {
						var shipmentLinePickedModel = _scScreenUtils.getModel(this, "backroomPick_getShipmentDetails_output");
						var inputModel = {};
						inputModel.Shipment = {};
						inputModel.Shipment.ShipmentKey = shipmentLinePickedModel.Shipment.ShipmentKey;
						inputModel.Shipment.Locations = {};
						inputModel.Shipment.Locations.Location = {};
						inputModel.Shipment.Locations.Location.LocationID = barCodeData;
						var mashupContext = {};
						mashupContext.ErrorMessage = "Please enter a valid location.";
						console.log("Input MODEL: ", inputModel);
						_isccsUIUtils.callMashup(this, inputModel, "extn_getLocationDetails", mashupContext);
					}
				} else if(_scBaseUtils.equals(stockroomContextValue, "Item")) {
					if(barCodeData in mlsLocationModel && _scBaseUtils.equals(mlsLocationModel[barCodeData].LocationType, "StockRoom")) {
						_scWidgetUtils.setValue(this, "extn_stockroomContext", "Item");
						_scWidgetUtils.setValue(this, "extn_lastLocationScanned", mlsLocationModel[barCodeData].LocationDesc);
						_scWidgetUtils.setValue(this, "extn_lastLocationScanned_value", barCodeData);
						_scEventUtils.fireEventInsideScreen(this, "resetBarCodeTextField", eventArgs, eventDefn);
					} else  {
						var shipmentLinePickedModel = _scScreenUtils.getModel(this, "backroomPick_getShipmentDetails_output");
						var inputModel = {};
						inputModel.Shipment = {};
						inputModel.Shipment.ShipmentKey = shipmentLinePickedModel.Shipment.ShipmentKey;
						inputModel.Shipment.Locations = {};
						inputModel.Shipment.Locations.Location = {};
						inputModel.Shipment.Locations.Location.LocationID = barCodeData;
						var mashupContext = {};
						mashupContext.ErrorMessage = "Please enter a valid location or item.";
						mashupContext.checkForItem = true;
						console.log("Input MODEL: ", inputModel);
						_isccsUIUtils.callMashup(this, inputModel, "extn_getLocationDetails", mashupContext);
					}
				} else { //Unknown
					if(barCodeData in mlsLocationModel && _scBaseUtils.equals(mlsLocationModel[barCodeData].LocationType, "StockRoom")) {
						_scWidgetUtils.setValue(this, "extn_stockroomContext", "Item");
						_scWidgetUtils.setValue(this, "extn_lastLocationScanned", mlsLocationModel[barCodeData].LocationDesc);
						_scWidgetUtils.setValue(this, "extn_lastLocationScanned_value", barCodeData);
						_scEventUtils.fireEventInsideScreen(this, "resetBarCodeTextField", eventArgs, eventDefn);
					} else  {
						var shipmentLinePickedModel = _scScreenUtils.getModel(this, "backroomPick_getShipmentDetails_output");
						var inputModel = {};
						inputModel.Shipment = {};
						inputModel.Shipment.ShipmentKey = shipmentLinePickedModel.Shipment.ShipmentKey;
						inputModel.Shipment.Locations = {};
						inputModel.Shipment.Locations.Location = {};
						inputModel.Shipment.Locations.Location.LocationID = barCodeData;
						var mashupContext = {};
						mashupContext.ErrorMessage = "Please enter a valid location or item.";
						mashupContext.checkForItem = true;
						console.log("Input MODEL: ", inputModel);
						_isccsUIUtils.callMashup(this, inputModel, "extn_getLocationDetails", mashupContext);
					}
				}
				
				//This case was comment out and had above case extracted in order to get scanning working in mobile
				//due to IE error. Will determine readdition after verifying bluebird BR.
				/*if(isScan) { 
					if(_scBaseUtils.equals(stockroomContextValue, "Location")) {
						if(!_scBaseUtils.equals(barcodeObject.BarCodeType, "Code128")) {
							var message = "Invalid location scanned. Please scan another location.";
							var textObj = {};
				    		textObj.OK = "Ok";
							_scScreenUtils.showErrorMessageBox(this, message, "errorMessageCallback", textObj);
						} else {
							this.checkLocationModel(barCodeData, mlsLocationModel);
						}
					} else if(_scBaseUtils.equals(stockroomContextValue, "Item")) {
						if(_scBaseUtils.equals(barcodeObject.BarCodeType, "Code128")) {
							this.checkLocationModel(barCodeData, mlsLocationModel);
						} else {
							var currentLocation = currentLocationWidget.value;
							barCodeModel = this.updateBarCodeModelWithLocation(barCodeModel, "Stockroom", currentLocation);
							this.performScan(barCodeModel);
						}
					} else { //In Unknown
						if(_scBaseUtils.equals(barcodeObject.BarCodeType, "Code128")) {
							this.checkLocationModel(barCodeData, mlsLocationModel);
						} else {
							var currentLocation = currentLocationWidget.value;
							barCodeModel = this.updateBarCodeModelWithLocation(barCodeModel, "Stockroom", currentLocation);
							this.performScan(barCodeModel);
						} 
					}
				} else { // Entered manually
					
				}*/	
			} else {
				var currentLocationID = this.getLocationIDFromMapping(barCodeData, mlsLocationModel);
				_scWidgetUtils.setValue(this, "extn_lastLocationScanned_value", currentLocationID);
				barCodeModel.BarCode.LocationContextualInfo = {};
				if(_scBaseUtils.isVoid(currentLocationID)) {
					barCodeModel.BarCode.LocationContextualInfo.LocationId = "";
				} else {
					barCodeModel.BarCode.LocationContextualInfo.LocationId = currentLocationID;
					barCodeModel.BarCode.LocationContextualInfo.LocationType = "SalesFloor";
				}
				
				this.performScan(barCodeModel);
			}
	    },
	    
	    /******************************
	     * @author: Adam Dunmars
	     * @description: Gets the location ID from the mapping using the barcode data.
	     * TODO: Describe this better.
	     ****************************/
	    getLocationIDFromMapping : function(barCodeData, mlsLocationModel) {
	    	var currentLocationID = null;
			var itemID = null;
			var shipmentDetailsModel = _scScreenUtils.getModel(this, "backroomPick_getShipmentDetails_output");
			
			for(var i = 0; i < shipmentDetailsModel.Shipment.ShipmentLines.ShipmentLine.length; i++) {
				var currentShipmentLine = shipmentDetailsModel.Shipment.ShipmentLines.ShipmentLine[i];
				if(_scBaseUtils.equals(currentShipmentLine.ItemID, barCodeData)) {
					itemID = currentShipmentLine.ItemID;
					break;
				}
				else if(!_scBaseUtils.isVoid(currentShipmentLine.OrderLine)
					&& !_scBaseUtils.isVoid(currentShipmentLine.OrderLine.ItemDetails)
					&& !_scBaseUtils.isVoid(currentShipmentLine.OrderLine.ItemDetails.ItemAliasList)
					&& !_scBaseUtils.isVoid(currentShipmentLine.OrderLine.ItemDetails.ItemAliasList.ItemAlias)
					&& !_scBaseUtils.isVoid(currentShipmentLine.OrderLine.ItemDetails.ItemAliasList.ItemAlias.length)) {
					for(var j = 0; j < currentShipmentLine.OrderLine.ItemDetails.ItemAliasList.ItemAlias.length; j++) {
						var currentAlias = currentShipmentLine.OrderLine.ItemDetails.ItemAliasList.ItemAlias[j];
						if(_scBaseUtils.equals(barCodeData, currentAlias.AliasValue)) {
							itemID = currentShipmentLine.ItemID;
							break;
						}
					}
				}
			}
			
			console.log("slModel", shipmentDetailsModel);
			if(_scBaseUtils.equals(itemID, null)) {
				console.err("Exception occurred: can't find Item or Location. Item ID: " + itemID);
			} else {
				for (var key in mlsLocationModel) {
					var currentLocationModel = mlsLocationModel[key];
					if(!_scBaseUtils.equals(array.indexOf(currentLocationModel.ItemIDList, itemID), -1)
							&& _scBaseUtils.equals(currentLocationModel.LocationType, "SalesFloor")) { 
						currentLocationID = key;
						break;
					} else if (!_scBaseUtils.equals(array.indexOf(currentLocationModel.ItemIDList, itemID), -1)
							&& _scBaseUtils.equals(currentLocationModel.LocationType, "StockRoom")) {
						currentLocationID = currentLocationModel.LocationDesc;
						break;
					} else {
						//placeholder. ~Adam
					}
				}
			}
			return currentLocationID;
	    },
	    
	    /*************************************************
	     * @author: Adam Dunmars
	     * @description: Function that performs the actual scanning.
	     * 		This function will:
	     * 
	     * 		1) Get the barcode data from the text field
	     * 		2) Determine if picking is done in ItemScan mode (Paper Picking) and if so
	     * 			a) Set a flag to tell the scanning service not to send an update to MLS 
	     * 				(for BOTH stockroom and salesfloor mode). 
	     * 		3) Determine if a valid barcode was entered into the text field.
	     * 			(in the case that they were entered manually)
	     * 			a) If so, make a call to the BarcodeTranslation service mashup. 
	     * 
	     * TODO: After successful scan, the stockroom context needs to be set to Item.
	     * TODO 2: Determine if asssuming only numerical values can be entered is appropriate.
	     * TODO 3: Add error messages to a constant or bundle file. 
	     * TODO 4: Numerical check isn't happening here.
	     ************************************************/
	    performScan: function(barCodeModel, checkLocations) {
		   	var barCodeData = _scModelUtils.getStringValueFromPath("BarCode.BarCodeData", barCodeModel);
		   	barCodeData.MLSFlag = (_scBaseUtils.equals(this.screenMode, "ItemScan"))  ? "N" : "Y";
			var isnum = /^\d+$/.test(barCodeData);
			var textObj = {};
			textObj.OK = "Ok";
			var errorMessageCallback = "errorMessageCallback";
			if (_scBaseUtils.isVoid(barCodeData)) {
			    _scScreenUtils.showErrorMessageBox(this, "Please enter a value into the scan field.", errorMessageCallback, textObj, null);
			} else if(!isnum){
			    _scScreenUtils.showErrorMessageBox(this, "Please ensure that only digits are entered for the barcode.", errorMessageCallback, textObj, null);
		    } else {
		    	var mashupContext = {};
		    	mashupContext.checkLocations = checkLocations;
		    	_isccsUIUtils.callMashup(this, barCodeModel, "translateBarCode", mashupContext);
		    	var eventArgs = _scBaseUtils.getNewBeanInstance();
	    		var eventDefn = _scBaseUtils.getNewBeanInstance(); 
	            _scBaseUtils.setAttributeValue("argumentList", _scBaseUtils.getNewBeanInstance(), eventDefn);
	            _scEventUtils.fireEventInsideScreen(this, "resetBarCodeTextField", eventDefn, eventArgs);
	    	}
	    },
	    
	    /********************************
	     * @author: Adam Dunmars
	     * @description: Empty callback for handling 
	     * error messsage dialog box to not throw error
	     * after clicking ok. 
	     ********************************/
	    errorMessageCallback : function() {
	    	
	    },
	
		/******************************
		 * @author: Adam Dunmars
		 * @description: This function is used to set the barcode from the physical
		 * scanning object. It is called by the scannedBarcode method 
		 * of the DOM window object. After the variable is set here, a call to 
		 * scanProduct is fired immediately.
		 * 
		 * SN: To test this on browser, the variable stored in barcode needs to be changed to just "barcodeString".
		 *****************************/
		setBarcode: function(barcodeString) {
			var screen = this;
			var uId = "scanProductIdTxt";
			
			var barcode = barcodeString.BarCodeData;
			
			// this is just to check if for some reason the data is coming in as string, instead of json object, lets try to convert
			if (_scBaseUtils.isVoid(barcode)) {
				// stringify and parse the input
				try {
					var parsedData = JSON.parse(JSON.stringify(eval("(" + barcodeString + ")")));
					barcode = parsedData.BarCodeData;
				} catch (err) {
					// don't do anything
				}
			}
			
			var dirty = false;
			sc.plat.dojo.utils.WidgetUtils.setValue(screen, uId, barcode, dirty); 
			this.scanProduct(null, null, null, null, barcodeString);
		},
		
		/*****************************************************
		 * @author: Adam Dunmars
		 * @description: INSERT DESCRIPTION HERE ~Adam
		 ******************************************************/
		handleExtnMashupCompletion: function(event, bEvent, ctrl, args) {
			console.warn("In handleExtnMashupCompletion");
			var mashupRefId = args.inputData.mashupRefId;
			var modelOutput = args.mashupObject.mashupRefOutput;
			switch(mashupRefId) {
				case "extn_getLocationDetails":
					var textObj = {};
		    		textObj.OK = "Ok";
					console.log("Model output: ", modelOutput);
					if((_scBaseUtils.isBooleanTrue(args.hasError) ||
							_scBaseUtils.isVoid(modelOutput.GetLocationDetailsResponse.GetLocationDetailsResult.Response.Data.StockroomLocations.StockroomLocation)
						) && !_scBaseUtils.isBooleanTrue(args.inputData.additionalData.checkForItem)) {
						_scScreenUtils.showErrorMessageBox(this, args.inputData.additionalData.ErrorMessage, "errorMessageCallback", textObj, null);
					} else if((_scBaseUtils.isBooleanTrue(args.hasError) || 
							_scBaseUtils.isVoid(modelOutput.GetLocationDetailsResponse.GetLocationDetailsResult.Response.Data.StockroomLocations.StockroomLocation))
							&& _scBaseUtils.isBooleanTrue(args.inputData.additionalData.checkForItem)) {
						var currentLocationWidget = _scScreenUtils.getWidgetByUId(this, "extn_lastLocationScanned_value");
						var currentLocation = currentLocationWidget.value;
						var barCodeModel = _scScreenUtils.getTargetModel(this, "translateBarCode_input", null);
						barCodeModel = this.updateBarCodeModelWithLocation(barCodeModel, "Stockroom", currentLocation);
						this.performScan(barCodeModel);
					} else {
						var mlsLocationModel = _scScreenUtils.getModel(this, "extn_mlsLocationsModel");
			    		this.checkLocationModel(modelOutput.GetLocationDetailsResponse.GetLocationDetailsResult.Response.Data.StockroomLocations.StockroomLocation[0].BarcodeNumber, mlsLocationModel);
					}
					break;
				case "extn_getItemLocation_mashup":
					_scScreenUtils.setModel(this, "backroomPick_getShipmentDetails_output", modelOutput, null);
			    	this.updateShipmentModel(modelOutput);
					break;
				case "extn_refreshShipmentDetails":
					if (!(_scBaseUtils.equals(false, applySetModel))) {
			    		_scScreenUtils.setModel(this, "backroomPick_getShipmentDetails_output", modelOutput, null);
			    	}
			    	if (!(_scBaseUtils.equals(this.screenMode, "ReadOnlyMobile"))) {
			    		this.isBackroomPickInProgress(modelOutput);
			    	} else {
			    		this.updateShipmentModel(modelOutput);
			    	}
					break;
				default:
					break;
			}
			console.warn("Out handleMashupCompletion");
		},
		
		/*******************************************
		 * @author: Adam Dunmars
		 * @description: INSERT DESCRIPTION HERE.
		 *********************************************/
		handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {	
    		if (hasError) {
    			// pass the error along so we can determine the right messaging
    			data.originalInput = inputData;
    		}
    		if(!_scBaseUtils.equals(inputData.mashupRefId, "translateBarCode")
    				|| (hasError && _scBaseUtils.equals(inputData.mashupRefId, "translateBarCode"))) { //Dont update twice.
    			_isccsBaseTemplateUtils.handleMashupCompletion(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
    		}
			console.warn("Out handleMashupCompletion");
	    },
		
		/******************************************************
		 * @author: Adam Dunmars
		 * @description: INSERT DESCRIPTION HERE.
		 * @subscribedTo: ???
		 *****************************************************/
		handleAfterBehavior: function(event, bEvent, ctrl, args) {
			console.warn("In handleAfterBehavior");
			console.log("Event: ", event);
			console.log("Business Event: ", bEvent);
			console.log("Ctrl: ", ctrl);
			console.log("Args: ", args);
			var mashupRefId = null;
			var mashupRefOutput = null;
			if(_scBaseUtils.equals(args.mashupArray.length, 0)) { // This was a multi API Call ~Adam
				mashupRefId = args.mashupObj.mashupRefId;
				mashupRefOutput = args.mashupObj.mashupRefOutput;
			} else { // Regular api call. ~Adam
				mashupRefId = args.mashupArray[0].mashupRefId;
				mashupRefOutput = args.mashupArray[0].mashupRefOutput;
			}
			console.warn("MashupRefId: ", mashupRefId);
			switch(mashupRefId){
				case "translateBarCode": // this case needs to be tested.
					var hasError = !_scBaseUtils.equals(_scModelUtils.getNumberValueFromPath("BarCode.Translations.TotalNumberOfRecords", mashupRefOutput), 1);
					if(hasError && args.inputData.additionalData.checkLocations)  {
					
			    		var barCodeData = args.inputData.mashupInputObject.BarCode.BarCodeData;
			    		var mlsLocationModel = _scScreenUtils.getModel(this, "extn_mlsLocationsModel");
			    		var eventArgs = null;
			    		var eventDefn = null;
			    		if(barCodeData in mlsLocationModel && _scBaseUtils.equals(mlsLocationModel[barCodeData].LocationType, "StockRoom")) {
							_scWidgetUtils.setValue(this, "extn_stockroomContext", "Item");
							_scWidgetUtils.setValue(this, "extn_lastLocationScanned", " "+mlsLocationModel[barCodeData].LocationDesc);
							_scWidgetUtils.setValue(this, "extn_lastLocationScanned_value", barCodeData);
							_scEventUtils.fireEventInsideScreen(this, "resetBarCodeTextField", eventArgs, eventDefn);
						} else  {
							var shipmentLinePickedModel = _scScreenUtils.getModel(this, "backroomPick_getShipmentDetails_output");
							var inputModel = {};
							inputModel.Shipment = {};
							inputModel.Shipment.ShipmentKey = shipmentLinePickedModel.Shipment.ShipmentKey;
							inputModel.Shipment.Locations = {};
							inputModel.Shipment.Locations.Location = {};
							inputModel.Shipment.Locations.Location.LocationID = barCodeData;
							inputModel.Shipment.CheckStockroom = "Y";
							mashupContext.ErrorMessage = "Please enter a valid location.";
							console.log("Input MODEL: ", inputModel);
							_isccsUIUtils.callMashup(this, inputModel, "extn_getLocationDetails", mashupContext);
						}
					} else {
						_scScreenUtils.setModel(this, "translateBarCode_output", args.mashupObj.mashupRefOutput, null);
						this.updateProductQuantity(args.mashupObj.mashupRefOutput);
			    		if(_scBaseUtils.equals(this.screenMode, "ItemScan")) {
			    			var lastScannedModel = _scScreenUtils.getTargetModel(this, "lastProductScanned_output", null);
			    			console.error("Last Scanned Model: ", lastScannedModel);
			    		}
				    	var shipmentLinePickedModel = _scScreenUtils.getTargetModel(this, "ItemScan_Output", null);
						var hasShortages = _wscBackroomPickUtils.hasShortages(shipmentLinePickedModel);
									
						this.showUndoButton(args.mashupObj.mashupRefOutput, shipmentLinePickedModel);
						this.determineStockroomContext();
						if(!_scBaseUtils.isVoid(shipmentLinePickedModel) && !hasShortages) {
							this.showCompleteButton();
				    	}
						_scEventUtils.stopEvent(bEvent);
					}
					break;
				case "changeShipmentToUpdateQty": //Called after a start over or continue ~Adam
					this.updateShipmentModel(mashupRefOutput);
					var mashInput = {};
					mashInput.Shipment = {};
					mashInput.Shipment.ShipmentKey = mashupRefOutput.Shipment.ShipmentKey;
					_isccsUIUtils.callMashup(this, mashInput, "extn_getItemLocation_mashup", null);
					break;
				case "changeShipmentStatusToInProgress":
					this.updateShipmentModel(mashupRefOutput);
					break;
				default:
					break;
			}
			console.warn("Out handleAfterBehavior");
		},
		
		/********************************
		 * @author: Adam Dunmars
		 * @description: This function checks to see if a successful scan happened.
		 * If so, it checks the context of the scan to determine if it is in 
		 * stockroom context.
		 * If so, it resets the stockroom context to look for location.
		 * TODO: This should be done only if scan is successful.
		 ********************************/
		determineStockroomContext : function() {
			var model = _scScreenUtils.getModel(this, "translateBarCode_output");
			if(!_scBaseUtils.isVoid(model.BarCode)
				&& _scBaseUtils.equals(model.BarCode.Translations.TotalNumberOfRecords, "1")) {
				var barCodeData = model.BarCode.BarCodeData; 
				var pickingContextWidget = _scScreenUtils.getWidgetByUId(this, "extn_hfItemPickingLocation");
				var lastScannedLocationValueWidget = _scScreenUtils.getWidgetByUId(this, "extn_lastLocationScanned_value");
				if(_scBaseUtils.equals(pickingContextWidget.value, 2)) {
					var stockroomContextWidget = _scScreenUtils.getWidgetByUId(this, "extn_stockroomContext");
					stockroomContextWidget.value = "Unknown";
					
					var message = null;
					var currentLocation = this.getLocationDescription(lastScannedLocationValueWidget.value);
					if(_scBaseUtils.isVoid(currentLocation)) {
						message = "Unknown" + ", " + barCodeData;
					} else {
						message = currentLocation + ", " + barCodeData;
					}
					
					_scWidgetUtils.setValue(this, "extn_lastLocationScanned", message);
					
				} else {
					_scWidgetUtils.setValue(this, "extn_lastLocationScanned", barCodeData);
				}
			}
		},
	
		/******************************
		 * @author: Adam Dunmars
		 * @description: Updates the shipment lines
	     * TODO: Can probably be removed since namespaces reference OOB namespaces again...believe its back to the same function call.
		 * TODO: Notify product team that the passed translateBarCodeOutputModel is not being used....
		 *****************************/
		updateProductQuantity: function(translateBarCodeOutputModel) {
            var productDetailModel = null;
            var selectedShipmentLinesModel = null;
            var isProductValid = false;
            productDetailModel = translateBarCodeOutputModel;//_scScreenUtils.getModel(this, "translateBarCode_output");
            selectedShipmentLinesModel = _isccsUIUtils.getWizardModel(this, "selectedShipmentLinesForPickUp");
            isProductValid = true;  // change for GA _wscCustomerPickUpUtils.isScannedProductValid(selectedShipmentLinesModel, productDetailModel);
            if (_scBaseUtils.isBooleanTrue(isProductValid)) {
                _wscBackroomPickUtils.updateShipmentLineContainer(this, productDetailModel);
                if(_scBaseUtils.equals(this.screenMode, "ItemScan")) {
                	var lastProductScannedModel = _isccsUIUtils.getWizardModel(this,"lastScannedProduct");
                	console.log("LastProductScannedModel: ", lastProductScannedModel);
                	if(!_scBaseUtils.isVoid(lastProductScannedModel)
                			&& !_scBaseUtils.isVoid(lastProductScannedModel.screenUId)) {
                		var childScreen = _scScreenUtils.getChildScreen(this, lastProductScannedModel.screenUId);
                		var eventDefn = {};
                		var eventArgs = {};
                		childScreen.showCorrectShortageButton();
                	}
                }
            } else {
                _scScreenUtils.showConfirmMessageBox(this, "InvalidProductScanned", null, null, null);
            }
        },
	
	    /*********************************
		 * @author: Shubadip Ray
		 * @description: This function alternates the context of the screen for scanning.
		 * "" or 2 = Picking Location Context is Stockroom
		 * 1 = Picking Location Context is Sales Floor
		 * TODO: This should probably use _scBaseUtils.equals instead of == (;D).
		 *********************************/
		filterbyPickingLocation: function() {
	        var itemPickingLocButtonWidget = _scScreenUtils.getWidgetByUId(this,"extn_hfItemPickingLocation");
			var itemPickingLocation = itemPickingLocButtonWidget.value;
			var locationLabelValue = "Location: Stockroom";
			var btnLabelValue = "Go to Sales Floor";
			if (_scBaseUtils.equals(itemPickingLocation, 1)) {
				itemPickingLocButtonWidget.value = 2;
				locationLabelValue = "Location: Stockroom";
				btnLabelValue = "Go to Sales Floor";
			} else {
				itemPickingLocButtonWidget.value = 1;
				locationLabelValue = "Location: Sales Floor";
				btnLabelValue = "Go to Stockroom";
			}
			_scWidgetUtils.setLabel(this, 'extn_labelItemPickingLocation', locationLabelValue);
			_scWidgetUtils.setLabel(this, 'extn_btnItemPickingLocation', btnLabelValue);
			
			this.checkContextForShipmentLines();
		},
	    	
		/************************************************************************ 
	 	* @author: Randy Washington
	 	* @description: This function will be called when the Start Pick button is clicked.
	 	* The user will transition to the picking screen.
	 	************************************************************************/
		startPickButtonAction: function(){
			var parentScreen = this.getWizardForScreen(this);
			var eventArgs = null;
			eventArgs = _scBaseUtils.getNewBeanInstance();
			var eventDefn = _scBaseUtils.getNewBeanInstance(); 
			_scBaseUtils.setAttributeValue("argumentList", _scBaseUtils.getNewBeanInstance(), eventDefn);
			_scEventUtils.fireEventInsideScreen(parentScreen, "nextBttn2_onClick", eventDefn, eventArgs);
		},
		
		/************************************************************************ 
	 	* @author: Randy Washington
	 	* @description: This function will be called when the complete button is clicked.
	 	* The user will transition to the hold location screen.
	 	************************************************************************/
		completeButtonAction: function(event, bEvent, ctrl, args){
			var parentScreen = this.getWizardForScreen(this);
			var eventArgs = null;
			eventArgs = _scBaseUtils.getNewBeanInstance();
			var eventDefn = _scBaseUtils.getNewBeanInstance(); 
			_scBaseUtils.setAttributeValue("argumentList", _scBaseUtils.getNewBeanInstance(), eventDefn);
			_scEventUtils.fireEventInsideScreen(parentScreen, "nextBttn2_onClick", eventDefn, eventArgs);
		},
		
		/**********************************
		 * @author: Randy Washington
		 * @description: To be added
		 ***********************************/
		showCompleteButton: function(){
			_scWidgetUtils.showWidget(this,"extn_buttonComplete");
		},
		
		/*****************************
		 * @author: Adam Dunmars
		 * @description: This function displays the Undo button for the last
		 * scanned shipment line ONLY.
		 ****************************/
		showUndoButton: function(modelOutput, itemPickedModel) {
			if(_scBaseUtils.equals(modelOutput.BarCode.Translations.TotalNumberOfRecords, "1")) {
				var childScreens = this._allChildScreens;
				for(var i = 0; i < childScreens.length; i++){
					_scEventUtils.fireEventToChild(this, _scWidgetUtils.getWidgetUId(childScreens[i]), "checkForUndo", null);
				}
			}
		},
		
		/******************************************
		 * @author: Randy Washington/Adam Dunmars
		 * @description: This subscriber function hides the 
		 * screen's wizard's next button during the OOB event afterScreenLoad.
		 * This subscriber MUST be placed AFTER the updateEditorHeader function.
		 ******************************************/
		hideNextButton: function(event, bEvent, ctrl, args){
			var editor = _editorUtils.getCurrentEditor();
			var wizard = _editorUtils.getScreenInstance(editor);
			if(wizard instanceof sc.plat.dojo.widgets.WizardUI) {
				wizard = wizard.getCurrentWizardInstance();
			}
			_wizardUtils.hideNavigationalWidget(wizard, "nextBttn",true );
		},
		
		/************************************************************************ 
	 	* @author: Randy Washington/Adam Dunmars/Rob Fea(?)
	 	* @description: This function will be called when the refresh button is clicked.
	 	* The calls MLS to retreive the latest location, quantity information,
	 	* and the pick up time.
	 	************************************************************************/
		handleReloadScreen: function(event, bEvent, ctrl, args) {
		
			var input = {};
			var mashupInput = _scScreenUtils.getModel(this,"backroomPick_getShipmentDetails_output");
			input.Shipment = {};
			input.Shipment.ShipmentKey = mashupInput.Shipment.ShipmentKey;
			input.Shipment.ShipNode = mashupInput.Shipment.ShipNode;
			input.Shipment.SellerOrganizationCode = mashupInput.Shipment.SellerOrganizationCode;
			input.Shipment.DisplayLocalizedFieldInLocale = mashupInput.Shipment.DisplayLocalizedFieldInLocale;
			_isccsUIUtils.callApi(this, input, "extn_refreshShipmentDetails",null);
	    },
	    
		/************************************************************************ 
	 	* @author: James Butler/Adam Dunmars
	 	* @description: This function will be called when the suspend button is clicked.
	 	* This function will display a dialog box to confirm the order suspension.
	 	************************************************************************/
		suspendButtonAction: function(){
			var message = "Are you sure you want to suspend this order?";
			_scScreenUtils.showConfirmMessageBox(this, message, "handleSuspend", null, null);		
		},
		
		/************************************************
		 * @author: James Butler/Adam Dunmars
		 * @description: This function will handle the option selected in the suspend.
		 * The addSuspendEJRecord() to record the suspend and the screen
		 * will be closed and returns to home screen.
		 ***********************************/
		handleSuspend: function(arg1){
			if(_scBaseUtils.equals(arg1, "Ok")) {
				var parentScreen = this.getWizardForScreen(this);
				var eventDefn = null;
				var eventArgs = null;
				eventArgs = _scBaseUtils.getNewBeanInstance();
				eventDefn = _scBaseUtils.getNewBeanInstance();
				_scBaseUtils.setAttributeValue("argumentList", _scBaseUtils.getNewBeanInstance(), eventDefn);
				_scEventUtils.fireEventInsideScreen(parentScreen, "closeBttn2_onClick", eventDefn, eventArgs);
				this.addSuspendEJRecord();
			}
		},
		
		/***************************************************
		 * @author: ??? (Rob Fea?)
		 * @description: INSERT DESCRIPTION HERE
		 * Override OOTB error handlers to display Kohls specific error messages
		 *************************************************/
		 customError: function(screen, data, code) {
	        var errorMessageBundle = null;
	        if (_scBaseUtils.equals( code, "YCD00063")) {
	            errorMessageBundle = "Extn_InvalidBarCodeData";
	        } else if (_scBaseUtils.equals(code, "YCD00064")) {
	            errorMessageBundle = "MultipleItemsFound";
	        } else if (_scBaseUtils.equals(code, "YCD00065")) {
	            errorMessageBundle = "ItemNotFound";
	        } else if (_scBaseUtils.equals(code, "YCD00066")) {
	            errorMessageBundle = "Extn_ItemNotInShipment";
	        } else if (_scBaseUtils.equals(code, "YCD00067")) {
	            errorMessageBundle = "Extn_ItemOverrage";
	            if (!_scBaseUtils.isVoid(data.originalInput)) {
	            	// set the parameters for the error message
	            	var scannedBarCode = data.originalInput.mashupInputObject.BarCode.BarCodeData;
	            	var lineQuantity = "0";
	            	var itemDescription = "";
	            	// loop through the model to check for the quantities
	            	var shipmentDetails = _scScreenUtils.getModel(this, "backroomPick_getShipmentDetails_output");
	        		console.log(shipmentDetails);
	        		var shipmentLineCount = null;
	        		if (!_scBaseUtils.isVoid(shipmentDetails.Shipments)) {
	        			shipmentLineCount = shipmentDetails.Shipments.Shipment[0].ShipmentLines.ShipmentLine.length;
	        		} else {
	        			shipmentLineCount = shipmentDetails.Shipment.ShipmentLines.ShipmentLine.length;
	        		}
	        		var locationMap = {};
	        		for(var i = 0; i < shipmentLineCount; i++) {
	        			var currentShipmentLine = null;
	        			if (!_scBaseUtils.isVoid(shipmentDetails.Shipments)) {
	        				currentShipmentLine = shipmentDetails.Shipments.Shipment[0].ShipmentLines.ShipmentLine[i];
	        			} else {
	        				currentShipmentLine = shipmentDetails.Shipment.ShipmentLines.ShipmentLine[i];
	        			}
	        			
	        			var itemID = currentShipmentLine.ItemID;
	        			if (_scBaseUtils.equals(itemID,scannedBarCode)) {
	        				// found a match, specify the quantity
	        				lineQuantity = currentShipmentLine.Quantity;
	        				itemDescription = currentShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ShortDescription;
	        				break;
	        			}
	        		}
	        		data.parameters = [lineQuantity,itemDescription];
	            }
	        } else {
	            return false;
	        }
	        if (!(_scBaseUtils.isVoid(errorMessageBundle))) {
	        	// assume that the parameters 
				if (!_scBaseUtils.isVoid(data.parameters)) {
	            	errorMessageBundle = _scScreenUtils.getFormattedString(this, errorMessageBundle,data.parameters);
				} else {
	            	errorMessageBundle = _scScreenUtils.getString(this, errorMessageBundle);
				}
	            _scScreenUtils.showErrorMessageBox(this, errorMessageBundle, "errorMessageCallback", _wscBackroomPickUtils.getTextOkObjectForMessageBox(this), null);
	            return true;
	        }
	   },
	
		/***********************************
		 * @author: Rob Fea(?)
		 * @description: Gets the wizard container that is enclosing this screen instance
		 *******************************/
	    getWizardForScreen: function(/*Screen*/screen){
			if(screen.wizardUI){
				return screen;
			} else {
				var force = true;
				var parentScreen = screen.getOwnerScreen(force);
				if(parentScreen){
					return this.getWizardForScreen(parentScreen); 
				}
			}
			return null;
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