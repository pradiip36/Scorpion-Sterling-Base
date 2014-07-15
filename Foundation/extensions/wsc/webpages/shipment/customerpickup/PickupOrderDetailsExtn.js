scDefine([	"scbase/loader!dojo/_base/declare",
		"scbase/loader!extn/shipment/customerpickup/PickupOrderDetailsExtnUI",
		"scbase/loader!isccs/utils/UIUtils",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		"scbase/loader!sc/plat/dojo/utils/BaseUtils",
		"scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(_dojodeclare,
	 _extnPickupOrderDetailsExtnUI,
	_isccsUIUtils,
	_scScreenUtils,
	_scBaseUtils,
	_scWidgetUtils
){ 
	return _dojodeclare("extn.shipment.customerpickup.PickupOrderDetailsExtn", [_extnPickupOrderDetailsExtnUI],{
	
		//TODO: The image for the alert message is distorted...this should be fixed either with CSS
		//		or with manually editing image. ~Adam
		/*********************************
		 * @author: Robert Fea/Randy Washington
		 * @description: This function hides additional information
		 *      and shows/hides jewelry widget.
		 ********************************/
		extnInitializeScreen: function(event, bEvent, ctrl, args) {
	        // default to show additional info
			this.hideShowAdditionalInformation();
			this.checkOrderForJewelry();
	    },
	    
	    /*********************************
		 * @author: Randy Washington
		 * @description: This function displays the jewelry 
		 * image widget if this is a jewelry order.
		 * TODO: Cleanup this function. ~Adam
		 ********************************/	
		checkOrderForJewelry:function(){
			var parentScreen = this.getOwnerScreen(true);
			var model = _scScreenUtils.getModel(parentScreen, "getCustomerPickupOrder_Source");
			var model2 = _scScreenUtils.getModel(parentScreen, "getCustomerPickupOrderSummaryList_output");
			//console.log("model",model);
			//console.log("model2",model2);
			if(!_scBaseUtils.isVoid(model)){
				if (!_scBaseUtils.isVoid(model.Shipment) &&
				    !_scBaseUtils.isVoid(model.Shipment.Extn) &&
				    !_scBaseUtils.isVoid(model.Shipment.Extn.ExtnShipmentIndicator) &&
				    _scBaseUtils.equals(model.Shipment.Extn.ExtnShipmentIndicator,"JEW"))
						_scWidgetUtils.showWidget(this,'extn_imageJewelryImg');		
			} else if (!_scBaseUtils.isVoid(model2.Shipment) &&
				    !_scBaseUtils.isVoid(model2.Shipment.Extn) &&
				    !_scBaseUtils.isVoid(model2.Shipment.Extn.ExtnShipmentIndicator) &&
				    _scBaseUtils.equals(model2.Shipment.Extn.ExtnShipmentIndicator,"JEW")) {
						_scWidgetUtils.showWidget(this,'extn_imageJewelryImg');		
			}
		},
		
		/***************************************************************************************************
		 * @author: Randy Washington/Adam Dunmars
		 * @description This dynamic binding function returns the alert message
		 * based on the shipment's status using Shipment/Status/@Status.
		 ***************************************************************************************************/
		getAlertMessage: function(dataValue, screen, widget, namespace, modelObj, options){
			var alertMess = null;
			if(_scBaseUtils.isVoid(dataValue)) {
				console.warn("WARNING: Shipment/Status/@Status is N/A! Model: ", modelObj);
				alertMess = "N/A";
			} else {
				var status = dataValue; //For readability sakes...for now. ~Adam
				switch(status) {
				case "1100.70.06.10":
					alertMess = "This order is awaiting store pick!";
					break;
				case "1100.70.06.20":
					alertMess = "This order is being picked!";
					break;
				case "1100.70.06.30":
					alertMess = "This order is ready for customer pickup!";
					break;
				case "1100.70.06.50.2":
					alertMess = "This order has been picked!";
					break;
				case "1100.70.06.50.4":
					alertMess = "This order has been placed in a hold location!";
					break;
				case "1400":
				case "1400.1":
					alertMess = "This order has been picked up by the customer!";
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
				default:
					console.warn("UNSUPPORTED STATUS: ", status);
					break;
				}
			}
			return alertMess;
		},
		
		/*********************************
		 * @author: Randy Washington/Adam Dunmars
		 * @description: This dynamic binding function 
		 * returns the shipments order number using
		 * Shipment/@OrderNo.
		 ********************************/
		getOrderNo: function(dataValue, screen, widget, namespace, modelObj, options){
			if(_scBaseUtils.isVoid(dataValue)) {
				console.warn("WARNING: Shipemnt/@OrderNo is N/A! Model: ", modelObj);
				return "N/A"; 
			} else {
				return dataValue;
			}
		},
	    
	    /******************************************************
	     * @author: Adam Dunmars
	     * @description: This dynamic binding function returns
	     * the user that was assigned to backroom pick the shipment
	     * using Shipment/Extn/@ExtnUsername. If it's not available it defaults to
	     * using Shipment/@AssignedToUserId. Otherwise it returns N/A.
	     *******************************************************/
	    getUsername: function(dataValue, screen, widget, namespace, modelObj, options) {
	    	if(_scBaseUtils.isVoid(dataValue)){
	    		if(_scBaseUtils.isVoid(modelObj)
	    				|| _scBaseUtils.isVoid(modelObj.Shipment)
	    				|| _scBaseUtils.isVoid(modelObj.Shipment.AssignedToUserId)) {
	    			console.warn ("WARNING: Shipment/Extn/@ExtnUsername AND Shipment/@AssignedToUserId are N/A! Model: ", modelObj);
	    			return "N/A";
	    		} else {
	    			console.warn("WARNING: Shipment/Extn/@ExtnUsername is N/A! Model: ", modelObj);
	    			return modelObj.Shipment.AssignedToUserId;
	    		}
	    	} else {
	    		return dataValue;
	    	}
	    },
	    
	    /*********************************
		 * @author: Randy Washington/Adam Dunmars
		 * @description: This dynamic binding function returns 
		 * the list of hold locations. 
		 * TODO: Delimit and massaged this based on multiple
		 * locations in the list.
		 ********************************/
		getHoldLocations: function(dataValue, screen, widget, namespace, modelObj, options){
			if(_scBaseUtils.isVoid(dataValue)) {
				console.warn("WARNING: Shipment/Extn/@ExtnHoldLocationDesc is N/A! Model: ", modelObj);
				return "N/A";
			} else {
				//Do delimiting here and massaging here. ~Adam
				return dataValue;
			}
		},
		
		/*********************************
		 * @author: Randy Washington/Adam Dunmars
		 * @description: This dynamic binding function returns the 
		 * order date of the shipment using Shipment/ShipmentLines/ShipmentLine[0]/Order/@OrderDate
		 * TODO: Ask product to put OrderDate in Shipment level...~Adam
		 * TODO2: Tell product team about defect when your attribute ends with word Date and passed into binding function... Adam
		 ********************************/
		getOrderDate: function(dataValue, screen, widget, namespace, modelObj, options){
			if(_scBaseUtils.isVoid(modelObj)
					|| _scBaseUtils.isVoid(modelObj.Shipment)
					|| _scBaseUtils.isVoid(modelObj.Shipment.ShipmentLines)
					|| _scBaseUtils.isVoid(modelObj.Shipment.ShipmentLines.ShipmentLine.length)
					|| _scBaseUtils.isVoid(modelObj.Shipment.ShipmentLines.ShipmentLine[0].Order)
					|| _scBaseUtils.isVoid(modelObj.Shipment.ShipmentLines.ShipmentLine[0].Order.OrderDate)) {
				console.log("WARNING: Shipment/ShipmentLines/ShipmentLine[0]/Order/OrderDate is N/A! Model: ", modelObj);
				return "N/A";
			} else {
				var orderDate = new Date(modelObj.Shipment.ShipmentLines.ShipmentLine[0].Order.OrderDate);
				return orderDate.toLocaleString("en-US");
			}
		},
		
		/*********************************
		 * @author: Randy Washington/Adam Dunmars
		 * @description: This dynamic binding function 
		 * returns the expected pickup date of the order using
		 * Shipment/@ExpectedShipmentDate.
		 * TODO: Tell product team about defect when your attribute ends with word Date and passed into binding function... Adam
		 ********************************/
		getExpectedPickUpDate: function(dataValue, screen, widget, namespace, modelObj, options){
			if(_scBaseUtils.isVoid(modelObj)
					|| _scBaseUtils.isVoid(modelObj.Shipment)
					|| _scBaseUtils.isVoid(modelObj.Shipment.ExpectedShipmentDate)) {
				console.warn("WARNING: Shipment/@ExpectedShipmentDate is N/A!");
				return "N/A";
			} else {
				var expPickupDate = new Date(modelObj.Shipment.ExpectedShipmentDate);
				return expPickupDate.toLocaleString("en-US");
			}
		}
	});
});