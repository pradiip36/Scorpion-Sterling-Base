scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/details/ShipmentSummaryExtnUI",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!isccs/utils/RelatedTaskUtils",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!sc/plat/dojo/utils/EditorUtils",
          "scbase/loader!isccs/utils/WizardUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!isccs/utils/ContextUtils",
          "scbase/loader!extn/utils/ScanUtils",
          "scbase/loader!dojo/string"]
,
function(			 
		 _dojodeclare,
		 _extnShipmentSummaryExtnUI,
		 _scModelUtils,
		 _scWidgetUtils,
		 _isccsRelatedTaskUtils,
		 _isccsUIUtils,
		 _editorUtils,
		 _wizardUtils,
		 _scScreenUtils,
		 _scBaseUtils,
		 _isccsContextUtils,
		 _bopusUtils,
		 _dString
){ 
	return _dojodeclare("extn.shipment.details.ShipmentSummaryExtn", [_extnShipmentSummaryExtnUI],{
	// custom code here

		/*********************************************
		 * @author: Randy Washington / Adam Dunmars
		 * @description: This function is called on the 
		 * ShipmentSummary screen's initialization (after OOB). The function:
		 * 	1) Gets the shipment detail model.
		 * 	2) Determines if there are jewelry items on the shipment and if so displays the icon
		 * 	3) Displays the screen's widgets based on the shipment status 
		 * 		(using Shipment/Status/Status) and whether the current context
		 * 		of the CurrentStore is the same Shipment's current store 
		 * 		(using Shipment/ShipNode/@ShipNode). 
		 *******************************************/
		extnInitialize : function(event, bEvent, ctrl, args) {
			/*console.log("Event: ", event);
			console.log("Business Event: ", bEvent);
			console.log("Ctrl: ", ctrl);
			console.log("Args: ", args);
			var editor = _editorUtils.getCurrentEditor();
			var editorInitialInput = editor.getModel(this, "InitialEditorInput");
			
			if(!_scBaseUtils.isVoid(editorInitialInput)
					&& !_scBaseUtils.isVoid(editorInitialInput.PrintReceipts)
					&& _scBaseUtils.equals(editorInitialInput.PrintReceipts, "Y")){
				alert("I should print receipts now");
			} else {
				alert("I shouldn't print any receipts...");
			}*/
			
			var model = _scScreenUtils.getModel(this, "getShipmentDetails_output");
			var shipmentModel = this.getModel("Shipment");
			console.log("Shipment: ", shipmentModel);
				
			/*if(!_scBaseUtils.isVoid(window.systemInfo)){
				this.runPrintJob();
			}*/
			if(this.isJewelryShipment(model)) {
				this.showJewelryWidget();
			}
			if(!_scBaseUtils.isVoid(model)
				&& !_scBaseUtils.isVoid(model.Shipment)
				&& !_scBaseUtils.isVoid(model.Shipment.Status)
				&& !_scBaseUtils.isVoid(model.Shipment.Status.Status)) {
				var status = model.Shipment.Status.Status;
				
				var currentStore = _isccsContextUtils.getFromContext("CurrentStore");
				var currentStoreFlag = _scBaseUtils.equals(model.Shipment.ShipNode.ShipNode,currentStore);
				
				switch(status) {
					case "1100.70.06.10":
						this.widgetsForAwaitingStorePick(this, currentStoreFlag);
						break;
					case "1100.70.06.20":
						this.widgetsForPickingInProgress(this, currentStoreFlag);
						break;
					case "1100.70.06.30":
						if (_scBaseUtils.equals(model.Shipment.Extn.ExtnSuspendedPickup,"Y")) {
							this.widgetsForReadyForCustomerPickUpSuspend(this,currentStoreFlag);
						} else { 	
							this.widgetsForReadyForCustomerPickup(this,currentStoreFlag);
						}
						break;
					case "1100.70.06.50.4":
						this.widgetsForPlaceInHoldLocation(this, currentStoreFlag);
						break;
					case "1400":
					case "1400.1":
						this.widgetsForCustomerPickedUp(this, currentStoreFlag);
						break;
					case "1400.2":
						this.widgetsForExpiredPickup(this, currentStoreFlag);
						break;
					case "1400.4":
						this.widgetsForPendingExpiredReturn(this,currentStoreFlag);
						break;
					case "9000":
						this.widgetsForCancelled(this, currentStoreFlag);
						break;
					default: 
						this.widgetsForMiscellaneous(this, currentStoreFlag);
						break;
				}
			} else {
				console.warn("WARNING: Shipment/Status/@Status is N/A!");
			}
		},
		
		/************************************
		 * @author: Maseeh Sabir (?) / Adam Dunmars
		 * @description: This function will print the appropriate
		 * receipts for the order. This function will:
		 * 		1) Print the customer and store copy of the receipts.
		 * 		2) Prints the return (exception) receipt for shorted items.
		 * 		3) Prints the gift receipts. 
		 * TODO 1: Change the mashup reference IDs and mashup IDs to something appropriate.
		 * TODO 2: Possibly remove function if fix can't be used.
		 ***********************************/
		runPrintJob: function() {
			var pickUpSummaryModel = _scScreenUtils.getModel(this, "Shipment");
		    var amountOfGiftReceipts = pickUpSummaryModel.Shipment.NumberOfGiftReceipts;
		    var diamondDataModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_diamond");
			
		    var modelOutput = this.getCorrectItems(pickUpSummaryModel, false);
		    modelOutput.diamondModel = diamondDataModel;   
		    
		    var shortedItems = 0;
		    shortedItems = pickUpSummaryModel.Shipment.RemainingShipmentLines.ShipmentLines.ShipmentLine.length;  
		    
		    this.printReceipt("customerPickup", null, pickUpSummaryModel);
		    this.printReceipt("storePickup", null, pickUpSummaryModel);
		    
		    if(!_scBaseUtils.isVoid(pickUpSummaryModel) && shortedItems > 0){
				var shortedModel = this.getCorrectItems(pickUpSummaryModel, true);
			   	this.printReceipt("return", null, shortedModel);
			}
		    
		    if(!_scBaseUtils.isVoid(amountOfGiftReceipts) && amountOfGiftReceipts > 0) {
			    for(var i = 1; i <= giftReceiptstorePrint; i++) {	
			    	this.printReceipt("giftNoItem", null, modelOutput);
			    }	
		    }
		},
		
		/******************************************
		 * @author: Maseeh Sabir(?)
		 * @description: The screen wizard will be used for void purpose to close the screen. 
		 * If you pass null it will still print.
		 * TODO: Possibly remove function if fix can't be made ~Adam
		 ******************************************/
		printReceipt: function(receiptType, wizard, modelOutput) {		
		    var commonModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_output");
		    var storeInfo = _scScreenUtils.getModel(this,'extn_shipNode_info');
		    modelOutput.ReceiptType = receiptType;
	  	    _bopusUtils.printReceipt(wizard, modelOutput, commonModel, storeInfo);
		},
	
		/********************************************
		 * @author: Maseeh Sabir(?)
		 * @description: INSERT DESCRIPTION HERE
		 * TODO: Possibly remove function if fix can't be made.
		 *******************************************/
		getCorrectItems: function(modelOutput, isShort) {
			var pickedLines = null;
			pickedLines = (isShort) ? modelOutput.Shipment.RemainingShipmentLines.ShipmentLines 
					: modelOutput.Shipment.IncludedShipmentLines.ShipmentLines;
			modelOutput.Shipment.ShipmentLines = pickedLines;
			return modelOutput;
		},
		
		/*********************************
		 * @author: Randy Washington
		 * @description: A function that changes the close button label to back.-
		 * TODO: This function possibly needs to be deleted as it is being called nowhere? ~Adam.
		 *********************************/
		changeWizardCloseButtonLabelToBack : function(){
			var uId = "closeBttn";
			var editor = _editorUtils.getCurrentEditor();
			var wizard = _editorUtils.getScreenInstance(editor);
			if(wizard instanceof sc.plat.dojo.widgets.WizardUI) {
				wizard = wizard.getCurrentWizardInstance();
			}
			wizard = _bopusUtils.getCurrentWizardInstance();
			_wizardUtils.setLabelOnNavigationalWidget(wizard, uId, "Back");
		},
		/*********************************************
		 * @author: Randy Washington
		 * @description: The function closes the search screen.
		 * 		 
		 ********************************************/
		closeSearchResults: function(){
			var searchEditor = _editorUtils.getSpecificOpenEditors("wsc.editors.ShipmentSearchEditor");		
			_editorUtils.closeEditor(searchEditor.editorInstance);
			var searchPageEditor = null;
			for(var i = 0; i < searchEditor.length; i++) {
				//console.log(i," ",searchEditor[i].editorInstance.className);
				if(_scBaseUtils.equals(searchEditor[i].editorInstance.className, "ShipmentSearchEditor")){
					searchPageEditor = searchEditor[i].editorInstance;
					_editorUtils.closeEditor(searchPageEditor);
					break;
				}
			}
	},
		/*********************************
		 * @author: Randy Washington
		 * @description: This dynamic binding function returns the customer's name that
		 * picked up the shipment using Shipment/Extn/@ExtnPickedCustFirstName
		 * and Shipment/Extn/@ExtnPickedCustLastName
		 *********************************/
		conCatPickedUpByName: function(dataValue, screen, widget, namespace, modelObj, options){
			if(!_scBaseUtils.isVoid(modelObj) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.Extn) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.Extn.ExtnPickedCustFirstName) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.Extn.ExtnPickedCustLastName)	){
		    	var firstName = _scModelUtils.getStringValueFromPath("Shipment.Extn.ExtnPickedCustFirstName", modelObj);
		    	var lastName = _scModelUtils.getStringValueFromPath("Shipment.Extn.ExtnPickedCustLastName", modelObj);
				var pickUpName = firstName +" "+ lastName;
				return pickUpName;
			} else{
				console.warn("WARNING: Shipment/Extn/@ExtnPickedCustFirstName and/or Shipment/Extn/@ExtnPickedCustLastName is N/A!");
			    return "N/A";
			}
		},
	
		/*********************************
		 * @author: Randy Washington
		 * @description: A binding function that retrieves the shipment description.
		 *********************************/
		getShipmentDescription: function(dataValue, screen, widget, namespace, modelObj, options){
			
			if(!_scBaseUtils.isVoid(modelObj) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.Status) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.Status.Description) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.Status.Status) ){
				 	var shipDesc = _scModelUtils.getStringValueFromPath("Shipment.Status.Description", modelObj);
				    var shipStatus = _scModelUtils.getStringValueFromPath("Shipment.Status.Status", modelObj);
					_scWidgetUtils.hideWidget(this,'lblShipmentDesc');
					if (_scBaseUtils.equals(shipStatus, "1400.1") || _scBaseUtils.equals(shipStatus, "1400")){
						return "Customer Picked Up";
					} else if (_scBaseUtils.equals(shipStatus, "1100.70.06.10")) {
						return("Waiting to be Picked");
					} else {
						return  shipDesc;
					}
			}else{
				console.warn("WARNING: Shipment/Status/@Description and/or Shipment/Status/@Status is N/A!");
			    return "N/A";
			}
	
		},
		
		/*********************************
		 * @author: Randy Washington
		 * @description: A binding function that retreives the pickup time using the status date.-
		 *********************************/
		getPickTime: function(dataValue, screen, widget, namespace, modelObj, options){
			if(!_scBaseUtils.isVoid(modelObj) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.StatusDate)){
			    	var shipStatusDate = _scModelUtils.getStringValueFromPath("Shipment.StatusDate", modelObj);
					var statusDate = new Date(shipStatusDate);
					//console.log(statusDate.toLocaleString("en-US"));
					return statusDate.toLocaleString("en-US");
			}else{
					console.log("Shipment.StatusDate - N/A");
				    return "N/A";
			}
	
		},
		
		/*********************************
		 * @author: Randy Washington
		 * @description: A binding function that returns the order date.-
		 *********************************/
		getOrderDate: function(dataValue, screen, widget, namespace, modelObj, options){
			if(!_scBaseUtils.isVoid(modelObj) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.ShipmentLines) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.ShipmentLines.ShipmentLine[0]) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.ShipmentLines.ShipmentLine[0].Order) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.ShipmentLines.ShipmentLine[0].Order.OrderDate)	){
						var orderDate = modelObj.Shipment.ShipmentLines.ShipmentLine[0].Order.OrderDate;
						var orDate = new Date(orderDate);
						//console.log(orDate.toLocaleString("en-US"));
						return orDate.toLocaleString("en-US");
			}
			else{
						console.log("Shipment.ShipmentLines.ShipmentLine[0].Order.OrderDate - N/A");
						return "N/A";			
			}
		},
		
		/*********************************
		 * @author: Randy Washington
		 * @description: A binding function that returns the expected pickup time.-
		 *********************************/
		getExpectedPickUpDate: function(dataValue, screen, widget, namespace, modelObj, options){
	
			if(!_scBaseUtils.isVoid(modelObj) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.ExpectedShipmentDate)){
					var expPickupDate = modelObj.Shipment.ExpectedShipmentDate;
					var expPickup = new Date(expPickupDate);
					//console.log(expPickup.toLocaleString("en-US"));
					return expPickup.toLocaleString("en-US");
			}else{
					console.log("Shipment.ExpectedShipmentDate - N/A");
					return "N/A";
			}
		},
		
		/*********************************
		 * @author: Randy Washington
		 * @description: A function that starts the customer pickup process from the 
		 *  the pickup summary screen.-
		 *********************************/
		Ink_RT_CustomerPick_onClick: function(event, bEvent, ctrl, args) {
	        var taskInput = null;
	        taskInput = _isccsRelatedTaskUtils.getRelatedTaskInput(this);
	        _isccsUIUtils.openWizardInEditor("wsc.shipment.wizards.CustomerPickupWizard", taskInput, "wsc.editors.CustomerPickupEditor", this);
	    },
	    
		/****************************************************************
		 * @author: Randy Washington
		 * -----------------------------------------
		 * @description: A binding function returns the city, state, and 
		 * zip code for a different store location. 
		 ****************************************************************/
		getLocationInfo:function(dataValue, screen, widget, namespace, modelObj, options){
	
			if(!_scBaseUtils.isVoid(modelObj) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.ShipNode) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.ShipNode.ShipNodePersonInfo))
			{
					var city = modelObj.Shipment.ShipNode.ShipNodePersonInfo.City;
					var state = modelObj.Shipment.ShipNode.ShipNodePersonInfo.State;
					var zip = modelObj.Shipment.ShipNode.ShipNodePersonInfo.ZipCode;
					return city+" "+state+" "+zip;
			}else{
					console.log("Shipment.ShipNode.ShipNodePersonInfo - N/A");
					return "N/A";
			}
		},
		
		/***************************************************************************************************
		 * @author: Randy Washington
		 * -----------------------------------------
		 * @description: A binding function returns the hold location. 
		 * 
		 ***************************************************************************************************/
		getHoldLocation:function(dataValue, screen, widget, namespace, modelObj, options){
			if(!_scBaseUtils.isVoid(modelObj.Shipment) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.Extn) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.Extn.ExtnHoldLocationDesc) )
					return modelObj.Shipment.Extn.ExtnHoldLocationDesc;
			else{
					console.log("Shipment.Extn.ExtnHoldLocationDesc - N/A");
					return "N/A";
			}
		},
		
		/*********************************************
		 * @author: Randy Washington
		 * @description: The function returns the expiration date.
		 ********************************************/
		getExpirationDate: function(dataValue, screen, widget, namespace, modelObj, options){
			console.log("modelObj",modelObj);
			if (!_scBaseUtils.isVoid(modelObj) &&
				!_scBaseUtils.isVoid(modelObj.Shipment) &&
				!_scBaseUtils.isVoid(modelObj.Shipment.Extn) &&
			    !_scBaseUtils.isVoid(modelObj.Shipment.Extn.ExtnExpirationDate))  {
				var date = modelObj.Shipment.Extn.ExtnExpirationDate;
				return new Date(date).toLocaleString("en-US");
			}
			else{	
				console.log("Shipment.Extn.ExtnExpirationDate - N/A");
				return "N/A";
			}
		},
		
		/***************************************************************************************************
		 * @author: Randy Washington
		 * @description: A binding function returns the alert message. 
		 * TODO: This could probably be set in the other switch statement now for better 
		 * performance...but make sense to have here from Sterling's architecture perspective though, 
		 * won't touch for now.
		 ***************************************************************************************************/
		getAlertMessage: function(dataValue, screen, widget, namespace, modelObj, options){
			//console.log("----- modelObj  ----- ",modelObj );
			var alertMess = null;
			var status = (!_scBaseUtils.isVoid(modelObj) &&
		       !_scBaseUtils.isVoid(modelObj.Shipment) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.Status) &&
			   !_scBaseUtils.isVoid(modelObj.Shipment.Status.Status)) ? modelObj.Shipment.Status.Status : null;
			
			if(!_scBaseUtils.isVoid(status)) {
				switch(status) {
					case "1100.70.06.10" :
						alertMess = "This order is awaiting store pick!";
						break;
					case "1100.70.06.20" :
						alertMess = "This order is being picked!";
						break;
					case "1100.70.06.30" :
						alertMess = "This order is ready for customer pickup!";
						break;
					case "1100.70.06.50.2" :
						alertMess = "This order has been picked!";
						break;
					case "1100.70.06.50.4" :
						alertMess = "This order has been placed in a hold location!";
						break;
					case "1400":
					case "1400.1":
						var firstName = _scModelUtils.getStringValueFromPath("Shipment.Extn.ExtnPickedCustFirstName", modelObj);
						var lastName = _scModelUtils.getStringValueFromPath("Shipment.Extn.ExtnPickedCustLastName", modelObj);
						var custName = null;
						custName = (!_scBaseUtils.isVoid(firstName)) ? firstName : "N/A";
						custName += " ";
						custName += (!_scBaseUtils.isVoid(lastName)) ? lastName : " N/A";
						alertMess = "This order has been picked up by " + custName + "!";
						break;
					case "1400.2":
						alertMess = "This order has expired!";
						break;
					case "1400.3":
						alertMess = "This order is a pick up exception!";
						break;
					case "1400.4":
						alertMess = "This order is a pending expired return!";
						break;
					case "9000":
						alertMess = "This order has been cancelled!";
						break;
					default :
						alertMess = "ERROR: Unsupported status type.";
						break;
				}
				return alertMess;
			} else {
				console.log("ERROR: No Shipment/Status/@Status found.");
				return "N/A";
			}
		},
		
		/*************************************************
		 * @author: Randy Washington
		 * @description: A binding function returns the 
		 * cancellation date. 
		 *************************************************/
		getCancelDate:function(dataValue, screen, widget, namespace, modelObj, options){
			
			if(!_scBaseUtils.isVoid(modelObj) 
					&& !_scBaseUtils.isVoid(modelObj.Shipment)
					&& !_scBaseUtils.isVoid(modelObj.Shipment.StatusDate)){
				var date = modelObj.Shipment.StatusDate;
				return new Date(date).toLocaleString("en-US");
			}
			else{
				console.log("Shipment.StatusDate	-	N/A");
				return "N/A";
			}
		},
		
		/**************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will display the
		 * user assigned to picking a shipment by:
		 * 
		 *  1) Display Shipment/@ExtnUsername if it exists, 
		 *  2) Otherwise if the field doesn't exist it will display
		 *  	Shipment/@AssignedToUserId if it exists,
		 *  3) Otherwise it will default to 'N/A', if both fields do not exist
		 * 
		 ***************************************/
		getAssignedUser : function(dataValue, screen, widget, namespace, modelObj, options) {
			if(!_scBaseUtils.isVoid(modelObj) 
				&& !_scBaseUtils.isVoid(modelObj.Shipment)
				&& !_scBaseUtils.isVoid(modelObj.Shipment.Extn)
				&& !_scBaseUtils.isVoid(modelObj.Shipment.Extn.ExtnUsername)) {
					return modelObj.Shipment.Extn.ExtnUsername;
			} else if(!_scBaseUtils.isVoid(dataValue)) {
					return dataValue;
			} else {
					console.log("Shipment.Extn.ExtnUsername	-	N/A");
					return "N/A";
			}
		},
		
		/***********************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function returns the 
		 * hold locations for an order using Shipment/@ExtnHoldLocationDesc.
		 * Because multiple hold locations are delimited in this column,
		 * we replace the delimiter with a space and trim the final value before returning
		 * TODO: Determine final delimiter.
		 * SN: May not be cross-platform (replace function)
		 **********************************************/
		getHoldLocations: function(dataValue, screen, widget, namespace, modelObj, options) {
			var delimiter = ';';
			if(_scBaseUtils.isVoid(dataValue)) {
				console.log("Shipment/@ExtnHoldLocationDescription not populated.");
				return "N/A";
			} else {
				var holdLocations = dataValue.replace(delimiter, " ");
				return _dString.trim(holdLocations);
			}
		},
		
		/********************************
		 * @author: Adam Dunmars / Randy Washington
		 * @description: This function determines
		 * if a shipment has jewelry items.
		 *********************************/
		isJewelryShipment : function(model) {
			if(_scBaseUtils.isVoid(model.Shipment) 
				|| _scBaseUtils.isVoid(model.Shipment.Extn) 
				|| _scBaseUtils.isVoid(model.Shipment.Extn.ExtnShipmentIndicator)) {
				console.log("Missing attribute or attribute doesn't appear for following XPath:"
						+ "Shipment.Extn.ExtnShipmentIndicator");
				return false;
			} else {
				return (_scBaseUtils.equals(model.Shipment.Extn.ExtnShipmentIndicator,"JEW")) ? true : false;
			}		
		},
		
		/*************************************
		 * @author: Randy Washington / Adam Dunmars
		 * @description: The function shows the jewelry image. 
		 *************************************/
		showJewelryWidget:function(){
			_scWidgetUtils.showWidget(this,'extn_imageJewelryImg');		
		},
		
		/*********************************************
		 * @author: Robert Fea
		 * @description: The function starts the customer pickup after
		 * 				 an order has been suspended.
		 ********************************************/
		resumePickUp : function(event, bEvent, ctrl, args) {
				var targetModel = null;
	            targetModel = _isccsRelatedTaskUtils.getRelatedTaskInput(this);  
	            _isccsUIUtils.openWizardInEditor("wsc.shipment.wizards.CustomerPickupWizard", targetModel, "wsc.editors.CustomerPickupEditor", this);	
		},
		
		/*********************************************
		 * @author: Robert Fea
		 * @description: The function changes the status of an order in 
		 * 				 pending expired return to expired.
		 ********************************************/
		markAsReturned: function(event, bEvent, ctrl, args){
			var shipmentModel = null;
			shipmentModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");
			var input={};
			input.Shipment={};	
		    input.Shipment.ShipmentKey = shipmentModel.Shipment.ShipmentKey;
			input.Shipment.ShipNode = shipmentModel.Shipment.ShipNode.ShipNode;
			input.Shipment.SellerOrganizationCode ='KOHLS.COM';
			input.Shipment.TransactionId = 'Change To Expired.0001.ex';
			_isccsUIUtils.callMashup(this, input, "extn_MarkAsReturned", null);  				
				 
		},		
		
		/*****************************************
		 * @author: Randy Washington
		 * @description: The function displays widget for shipments in ready for customer pickup status. 
		 * 				 storeFlag = TRUE : The store will fulfill the order & displays start customer pick button.
		 * 				 storeFlag = FALSE:	The store will NOT fulfill the order & start customer pick button is hidden.
		 *****************************************/
		widgetsForReadyForCustomerPickup:function(parentScreen,storeFlag){
			_scWidgetUtils.hideWidget(this,'extn_label_pickedUp');
			_scWidgetUtils.hideWidget(parentScreen,'extn_datalabelPickedUpOn');
			_scWidgetUtils.hideWidget(this,'extn_labelPickedStatus');
			_scWidgetUtils.showWidget(this,'extn_contentpaneAltPickUp');
			if(_scBaseUtils.isBooleanTrue(storeFlag)) {
				_scWidgetUtils.showWidget(parentScreen,'extn_buttonStartCustPickUp');
			} else {
				this.widgetsForDifferentStore(parentScreen);
			}
		},
		/*********************************************
		 * @author: Randy Washington
		 * @description: The function displays the widgets for
		 * 		 an order in suspended, ready for customer pickup status.
		 ********************************************/
		widgetsForReadyForCustomerPickUpSuspend:function(parentScreen,storeFlag){
			_scWidgetUtils.hideWidget(this,'extn_label_pickedUp');
			_scWidgetUtils.hideWidget(parentScreen,'extn_datalabelPickedUpOn');
			_scWidgetUtils.hideWidget(this,'extn_labelPickedStatus');
			_scWidgetUtils.showWidget(this,'extn_contentpaneAltPickUp');
			if(_scBaseUtils.isBooleanTrue(storeFlag)){
				_scWidgetUtils.showWidget(this,'extn_buttonResumePicking');
			} else {
				this.widgetsForDifferentStore(parentScreen);
			}
		},
		
		/*******************************************************************************************
		 * @author: Randy Washington
		 * @description: The function displays widget for shipments in picking in progress status. 
		 *******************************************************************************************/
		widgetsForPickingInProgress:function(parentScreen, storeFlag){
			_scWidgetUtils.hideWidget(this,'extn_label_pickedUp');
			_scWidgetUtils.hideWidget(parentScreen,'extn_datalabelPickedUpOn');
			_scWidgetUtils.hideWidget(this,'extn_labelPickedStatus');
			if(!_scBaseUtils.isBooleanTrue(storeFlag)) {
				this.widgetsForDifferentStore(parentScreen);
			}
		},
		
		/***************************************************************************************************
		 * @author: Randy Washington
		 * @description: The function displays widget for shipments in awaiting store pick status. 
		 ***************************************************************************************************/
		widgetsForAwaitingStorePick:function(parentScreen, storeFlag){
			_scWidgetUtils.hideWidget(this,'extn_label_pickedUp');
			_scWidgetUtils.hideWidget(parentScreen,'extn_datalabelPickedUpOn');
			_scWidgetUtils.hideWidget(this,'extn_labelPickedStatus');
			if(!_scBaseUtils.isBooleanTrue(storeFlag)) {
				this.widgetsForDifferentStore(parentScreen);
			}
		},
		
		/***************************************************************************************************
		 * @author: Randy Washington
		 * @description: The function displays widget for shipments in pending expired return status. 
		 * 				 storeFlag = TRUE : The store will show mark as returned action button.
		 * 				 storeFlag = FALSE:	The store will NOT show the mark as returned action button.
		 ***************************************************************************************************/
		widgetsForPendingExpiredReturn:function(parentScreen,storeFlag){
			_scWidgetUtils.hideWidget(this,'extn_label_pickedUp');
			_scWidgetUtils.hideWidget(parentScreen,'extn_datalabelPickedUpOn');
			_scWidgetUtils.hideWidget(this,'extn_labelPickedStatus');
			if(storeFlag)
				_scWidgetUtils.showWidget(parentScreen,'extn_buttonMarkReturn');
			else
				this.widgetsForDifferentStore(parentScreen);
	
		},
		
		/***************************************************************************************************
		 * @author: Randy Washington 
		 * @description: The function displays widget for shipments in cancelled status. 
		 ***************************************************************************************************/
		widgetsForCancelled:function(parentScreen, storeFlag){
			_scWidgetUtils.hideWidget(this,'extn_label_pickedUp');
			_scWidgetUtils.hideWidget(this,'extn_labelPickedStatus');
			_scWidgetUtils.showWidget(parentScreen,'extn_datalabelCancellationDate');
			if(!_scBaseUtils.isBooleanTrue(storeFlag)) {
				this.widgetsForDifferentStore(parentScreen);
			}
		},
		
		/*************************************
		 * @author: Randy Washington
		 * @description: The function displays 
		 * widget for shipments in customer picked up status. 
		 * 
		 *************************************/
		widgetsForCustomerPickedUp:function(parentScreen, storeFlag){
			_scWidgetUtils.hideWidget(parentScreen,'extn_datalabelExpectedPickUpDate');
			_scWidgetUtils.showWidget(parentScreen,'extn_contentpanePickedUpBy');
			_scWidgetUtils.showWidget(parentScreen,'extn_datalabelPickedUpOn');  
			if(!_scBaseUtils.isBooleanTrue(storeFlag)) {
				this.widgetsForDifferentStore(parentScreen);
			}
		},
		
		/*****************************************************************************
		 * @author: Randy Washington
		 * @description: The function displays widget for shipments in expired status. 
		 *****************************************************************************/
		widgetsForExpiredPickup:function(parentScreen, storeFlag){
			_scWidgetUtils.hideWidget(parentScreen,'extn_datalabelExpectedPickUpDate');
			_scWidgetUtils.hideWidget(this,'extn_labelPickedStatus');  
			_scWidgetUtils.hideWidget(parentScreen,'extn_datalabelPickedUpOn');  
			_scWidgetUtils.showWidget(parentScreen,'extn_datalabelClearDate');  
			if(!_scBaseUtils.isBooleanTrue(storeFlag))
				this.widgetsForDifferentStore(parentScreen);
		},
		
		/************************************************************
		 * @author: Randy Washington
		 * @description: The function displays widget for shipments 
		 * for remaining statuses. 
		 ************************************************************/
		widgetsForMiscellaneous:function(parentScreen, storeFlag){
			_scWidgetUtils.hideWidget(parentScreen,'extn_datalabelPickedUpOn');
			_scWidgetUtils.hideWidget(this,'extn_labelPickedStatus');
			if(!_scBaseUtils.isBooleanTrue(storeFlag)) {
				this.widgetsForDifferentStore(parentScreen);
			}
		},
		
		/************************************************
		 * @author: Randy Washington
		 * @description: The function displays widget for 
		 * orders fulfilled at a different store. 
		 ************************************************/
		widgetsForDifferentStore:function(parentScreen){
			_scWidgetUtils.showWidget(parentScreen,'extn_contentpaneDifferentLocation');
		},
		
		/******************************************
		 * @author: Randy Washington
		 * @description: The function displays widget 
		 * for orders in hold location. 
		 ******************************************/
		widgetsForPlaceInHoldLocation:function(parentScreen, storeFlag){
			_scWidgetUtils.hideWidget(this,'extn_label_pickedUp');
			_scWidgetUtils.hideWidget(parentScreen,'extn_datalabelPickedUpOn');
			_scWidgetUtils.hideWidget(this,'extn_labelPickedStatus');
			if(!_scBaseUtils.isBooleanTrue(storeFlag)) {
				this.widgetsForDifferentStore(parentScreen);
			}
		},
	});
});