scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/search/ShipmentDetailsExtnUI",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!isccs/utils/ContextUtils",
          "scbase/loader!dojo/string",
          "scbase/loader!sc/plat/dojo/utils/EditorUtils"]
,
function(			 
	    _dojodeclare,
	    _extnShipmentDetailsExtnUI,
	    _scScreenUtils,
	    _scWidgetUtils,
	    _scBaseUtils,
	    _isccsUIUtils,
	    _isccsContextUtils,
	    _dString,
	    _scEditorUtils
){ 
	return _dojodeclare("extn.shipment.search.ShipmentDetailsExtn", [_extnShipmentDetailsExtnUI],{
	// custom code here
		
	/**************************************
	 * @author: Rob Fea / Randy Washington)
	 * @description: The function extends the initialization screen and
	 *		 shows/hides widgets depending on the status of the order. 
	 *************************************/
		extInitialize: function() {
			// decide what to show or hide
			var targetModel = _scScreenUtils.getModel(this, "getShipmentList_output");
			// check for status
	        //console.log("targetModel",targetModel);
	   		var currentStore = _isccsContextUtils.getFromContext("CurrentStore");
	   		var currentStoreFlag = _scBaseUtils.equals(targetModel.ShipNode,currentStore);
	   		//console.log("currentStore",currentStore);
	   		//console.log("search - currentStoreFlag",currentStoreFlag);

	   		if(_scBaseUtils.isBooleanTrue(this.isJewelryOrder(targetModel)))
	   			this.showJewelryWidget();
			
			if(!_scBaseUtils.isVoid(targetModel) &&
			   !_scBaseUtils.isVoid(targetModel.Status) &&
			   !_scBaseUtils.isVoid(targetModel.Status.Status)){
					var status  = targetModel.Status.Status;
			
					switch(status){
						case "1100.70.06.10":	
								this.widgetsForAwaitingStorePick();
								break;
						case "1100.70.06.20":	
								this.widgetsForPickingInProgress();
								break;
						case "1100.70.06.30":	
								if (_scBaseUtils.equals(targetModel.Extn.ExtnSuspendedPickup,"Y"))   
									this.widgetsForReadyForCustomerPickedUpSuspend(currentStoreFlag);
								else 	
									this.widgetsForReadyForCustomerPickedUp(currentStoreFlag);
								break;
						case "1400":	
								this.widgetsForCustomerPickedUp();
								break;
						case "1400.1":	
								this.widgetsForCustomerPickedUp();
								break;
						case "1400.2":	
								this.widgetsForExpiredPickup();
								break;
						case "1400.4":	
								this.widgetsForPendingExpiredReturn(currentStoreFlag);
								break;
						case "9000":	
								this.widgetsForCancelled(currentStoreFlag);
								break;
						default:	
								this.widgetsForMiscellaneous();
								break;
					
					}
			}else
				console.log("Status.Status - N/A");	
		},
	
	/********************************************************
	 * @author: Randy Washington
	 * @description: Thefunction shows the jewelry image 
	 * 		 		 if the order has jewelry items. 
	 ********************************************************/
	showJewelryWidget:function(){
		_scWidgetUtils.showWidget(this,'extn_imageSearchJewelryImg');	
	},
	
	/********************************************************
	 * @author: Randy Washington
	 * @description: This function returns true is the order
	 * 				 is a jewelry order, false otherwise.
	 ********************************************************/
	isJewelryOrder:function(model){
		if (!_scBaseUtils.isVoid(model) &&
		    !_scBaseUtils.isVoid(model.Extn) &&
		    !_scBaseUtils.isVoid(model.Extn.ExtnShipmentIndicator) &&
		    _scBaseUtils.equals(model.Extn.ExtnShipmentIndicator,"JEW")){	
				return true;
		}else{
				return false;
		}

	},	

	/*********************************************
	 * @author: Robert Fea
	 * @description: To be added.
	 ********************************************/
	resumePickUp : function(event, bEvent, ctrl, args) {
			var targetModel = null;
            targetModel = _scScreenUtils.getTargetModel(
            this, "getShipmentDetails_input", null);
            _isccsUIUtils.openWizardInEditor("wsc.shipment.wizards.CustomerPickupWizard", targetModel, "wsc.editors.CustomerPickupEditor", this);	
            this.addResumeEJEntry();	
	},

	/*********************************************
	 * @author: Maseeh Sabir
	 * @description: To be added.
	 ********************************************/
	addResumeEJEntry: function() {
	    var shipmentModel = null;
        shipmentModel = _scScreenUtils.getModel(this, "getShipmentList_output");
	    
		var mashInput = {};
		mashInput.ShipmentStoreEvents = {};
		mashInput.ShipmentStoreEvents.OrderNo = shipmentModel.OrderNo;
		mashInput.ShipmentStoreEvents.EventType = "Pickup Resumed";
		mashInput.ShipmentStoreEvents.OrderHeaderKey = shipmentModel.OrderHeaderKey;
		mashInput.ShipmentStoreEvents.ShipmentKey = shipmentModel.ShipmentKey;

		_isccsUIUtils.callApi(this, mashInput, "extn_addEJTransPickupResumed", null);		
	},

	/*********************************************
	 * @author: Randy Washington
	 * @description: The function returns the picked up time.
	 ********************************************/
	getShipmentTime: function(dataValue, screen, widget, namespace, modelObj, options){
		if (!_scBaseUtils.isVoid(modelObj) &&
			!_scBaseUtils.isVoid(modelObj.StatusDate) )  {
				var date = modelObj.StatusDate;
				return new Date(date).toLocaleString("en-US");
		}else{
				return new "N/A";
				console.log("StatusDate - N/A");
		}
	},

	/*********************************************
	 * @author: Randy Washington
	 * @description: The function displays the order status alert message.
	 ********************************************/
	getAlertMessage: function(dataValue, screen, widget, namespace, modelObj, options){
		//console.log("----- modelObj.Status ----- ",modelObj.Status);
		if (!_scBaseUtils.isVoid(modelObj) &&
		    !_scBaseUtils.isVoid(modelObj.Status) )  {

			var alertMess = null;
			if(_scBaseUtils.equals(modelObj.Status.Status,'1100.70.06.10')) 
				alertMess = "This order is awaiting store pick!";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'1100.70.06.20')) 	
				alertMess = "This order is being picked!";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'1100.70.06.50.2')) 	
				alertMess = "This order has been picked!";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'1100.70.06.50.4')) 	
				alertMess = "This order has been placed in a hold location!";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'1100.70.06.30')) 	
				alertMess = "This order is ready for customer pickup!";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'1400')) 	
				alertMess = "This order has been picked up by the customer!";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'1400.1')) 	
				alertMess = "This order has been picked up by the customer!";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'1400.2')) 	
				alertMess = "This order has expired!";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'1400.3')) 	
				alertMess = "This order is a pick up exception!";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'1400.4')) 	
				alertMess = "This order is a pending expired return!";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'9000')) 	
				alertMess = "This order has been cancelled!";
			
			return alertMess;
		}else{	
			console.log("Status.Status - N/A");
			return "N/A";
		}
		
	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function returns the order date.
	 ********************************************/
	getOrderDate: function(dataValue, screen, widget, namespace, modelObj, options){
		if (!_scBaseUtils.isVoid(modelObj) &&
		    !_scBaseUtils.isVoid(modelObj.ShipDate) )  {
			var date = modelObj.ShipDate;
			return new Date(date).toLocaleString("en-US");
		}
		else{	
			console.log("Order Date - N/A");
			return "N/A";
		}	
	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function returns the picked up time.
	 ********************************************/
	getPickUpDate: function(dataValue, screen, widget, namespace, modelObj, options){
		if (!_scBaseUtils.isVoid(modelObj) &&
		    !_scBaseUtils.isVoid(modelObj.ExpectedShipmentDate) )  {
			var date = modelObj.ExpectedShipmentDate;
			return new Date(date).toLocaleString("en-US");
		}
		else{	
			console.log("modelObj.ExpectedShipmentDate - N/A");
			return "N/A";
		}	
	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function returns the shipment status description.
	 ********************************************/
	getShipmentDescription: function(dataValue, screen, widget, namespace, modelObj, options){
		if (!_scBaseUtils.isVoid(modelObj) &&
		    !_scBaseUtils.isVoid(modelObj.Status) &&
		    !_scBaseUtils.isVoid(modelObj.Status.Status) )  {

			if(_scBaseUtils.equals(modelObj.Status.Status,'1400'))
				return "Customer Picked Up";
			else if(_scBaseUtils.equals(modelObj.Status.Status,'1100.70.06.10')) 
				return "Waiting to be Picked";
			else
				return modelObj.Status.Description;
		}
		else{
			return "N/A";
			console.log("modelObj.Status.Status/modelObj.Status.Description - N/A");

		}
	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function returns the cancel date.
	 ********************************************/
	getCancelDate: function(dataValue, screen, widget, namespace, modelObj, options){
		if (!_scBaseUtils.isVoid(modelObj) &&
		    !_scBaseUtils.isVoid(modelObj.StatusDate) )  {
			var date = modelObj.StatusDate;
			return new Date(date).toLocaleString("en-US");
		}
		else{	
			console.log("Cancelled - StatusDate - N/A");
			return "N/A";
		}	
	},
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function returns the expiration date.
	 ********************************************/
	getExpirationDate: function(dataValue, screen, widget, namespace, modelObj, options){
		//console.log("modelObj",modelObj);
		if (!_scBaseUtils.isVoid(modelObj) &&
		    !_scBaseUtils.isVoid(modelObj.Extn) &&
		    !_scBaseUtils.isVoid(modelObj.Extn.ExtnExpirationDate))  {
			var date = modelObj.Extn.ExtnExpirationDate;
			return new Date(date).toLocaleString("en-US");
		}
		else{	
			console.log("Extn.ExtnExpirationDate - N/A");
			return "N/A";
		}
	},	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function displays the widgets for
	 * 		 an order in suspended, ready for customer pickup status.
	 ********************************************/
	widgetsForReadyForCustomerPickedUpSuspend:function(storeFlag){
		_scWidgetUtils.hideWidget(this,'extn_datalabelPickedUpOnDate');
		if(storeFlag)
			_scWidgetUtils.showWidget(this,'extn_button_resumePick');

	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function displays the widgets for
	 * 		 an order in ready for customer pickup status.
	 ********************************************/
	widgetsForReadyForCustomerPickedUp:function(storeFlag){
		_scWidgetUtils.hideWidget(this,'extn_datalabelPickedUpOnDate');
		if(storeFlag)
			_scWidgetUtils.showWidget(this,'extn_button_startPick');
		
	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function displays the widgets for
	 * 		 an order in awaiting store pick status.
	 ********************************************/
	widgetsForAwaitingStorePick:function(){
		_scWidgetUtils.hideWidget(this,'extn_datalabelPickedUpOnDate');
	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function displays the widgets for
	 * 		 an order in picking in progress status.
	 ********************************************/
	widgetsForPickingInProgress:function(){
		_scWidgetUtils.hideWidget(this,'extn_datalabelPickedUpOnDate');

	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function displays the widgets for
	 * 		 an order in customer picked up status.
	 ********************************************/
	widgetsForCustomerPickedUp:function(){
	 	_scWidgetUtils.hideWidget(this,'extn_datalabelPickUpDate');
	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function displays the widgets for
	 * 		 an order in expired pickup status.
	 ********************************************/
	widgetsForExpiredPickup:function(){
	 	_scWidgetUtils.hideWidget(this,'extn_datalabelPickedUpOnDate');
	 	_scWidgetUtils.hideWidget(this,'extn_datalabelPickUpDate');
	 	_scWidgetUtils.showWidget(this,'extn_datalabelExpirationDate');


	},
	/***************************************************************************************************
	 * @author: Randy Washington
	 * @description: The function displays widget for shipments in pending expired return status. 
	 * 				 storeFlag = TRUE : The store will show mark as returned action button.
	 * 				 storeFlag = FALSE:	The store will NOT show the mark as returned action button.
	 ***************************************************************************************************/
	widgetsForPendingExpiredReturn:function(storeFlag){
		_scWidgetUtils.hideWidget(this,'extn_datalabelPickedUpOnDate');
		if(storeFlag)
			_scWidgetUtils.showWidget(this,'extn_buttonMarkAsReturned');
		
	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function displays the widgets for
	 * 		 an order in cancel status.
	 ********************************************/
	widgetsForCancelled:function(){
		_scWidgetUtils.showWidget(this,'extn_datalabelCancelDate');
	 	_scWidgetUtils.hideWidget(this,'extn_datalabelPickedUpOnDate');
	 	_scWidgetUtils.hideWidget(this,'extn_datalabelPickUpDate');
	},
	
	/*********************************************
	 * @author: Randy Washington
	 * @description: The function displays the widgets for
	 * 		 an order in any of the remaining statuses.
	 ********************************************/
	widgetsForMiscellaneous:function(){
		_scWidgetUtils.hideWidget(this,'extn_datalabelPickedUpOnDate');
	},
	
	/*********************************************
	 * @author: Robert Fea
	 * @description: The function changes the status of an order in 
	 * 				 pending expired return to expired.
	 ********************************************/
	markAsReturned: function(event, bEvent, ctrl, args){
		var shipmentModel = null;
		shipmentModel = _scScreenUtils.getModel(this, "getShipmentList_output");
		var input={};
		input.Shipment={};	
	    input.Shipment.ShipmentKey = shipmentModel.ShipmentKey;
	    _isccsUIUtils.callMashup(this, input, "extn_MarkAsReturned", null);  				
			 
	},	

	/*********************************************
	 * @author: ???
	 * @description: To be added.
	 ********************************************/
	 changeShipmentVisibility: function(event, bEvent, ctrl, args) {
			if(args == null || args.hasError){
        	}  
        	else{		
			var searchResults = null;
			var searchInput = null;
			searchResults = _scBaseUtils.getAttributeValue("mashupArray",false, args);
			if (_scBaseUtils.equals("extn_saveMgrOverride_mashup",searchResults[0].mashupRefId)) {	
				_scWidgetUtils.hideWidget(this, "extn_buttonMarkAsReturned", false);
				_scWidgetUtils.enableReadOnlyWidget(this,"lblOrderNo");				
				var targetModel = _scScreenUtils.getModel(this, "getShipmentList_output");
				_scBaseUtils.setAttributeValue("Status.Description", "Returned Order", targetModel);
				screenInput = _scScreenUtils.setModel(this, "getShipmentList_output",targetModel,null);				
				 _scWidgetUtils.addClass(this, "basicContentPane", "infoPanel");
				
			}
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
			&& !_scBaseUtils.isvoid(modelObj.Shipment.Extn.ExtnUsername)) {
			return modelObj.Shipment.Extn.ExtnUsername;
		} else if(!_scBaseUtils.isVoid(dataValue)) {
			return dataValue;
		} else {
			return "N/A";
		}
	},
	
	/****************************************
	 * @author: Adam Dunmars
	 * @description: This dynamic binding function displays
	 * the item's backroom picked quantity based on the 
	 * ShipmentLine/@BackroomPickedQuantity attribute.
	 * If the field is empty it returns 0.
	 ****************************************/
	getBackroomPickQty : function(dataValue, screen, widget, namespace, modelObj, options){
		return (!_scBaseUtils.isVoid(dataValue)) ? dataValue :"0";
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
});
});
