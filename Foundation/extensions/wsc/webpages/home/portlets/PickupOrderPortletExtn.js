scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/home/portlets/PickupOrderPortletExtnUI","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!isccs/utils/UIUtils","scbase/loader!wsc/utils/CustomerPickUpUtils","scbase/loader!sc/plat/dojo/utils/EventUtils","scbase/loader!wsc/utils/ShipmentSearchUtils","scbase/loader!isccs/utils/ContextUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnPickupOrderPortletExtnUI
			 , _scBaseUtils
			 , _scModelUtils
			 , _scScreenUtils 
			 , _isccsUIUtils 
			 , _wscCustomerPickUpUtils
			 , _scEventUtils
			 , _wscShipmentSearchUtils
			 , _isccsContextUtils
){ 
	return _dojodeclare("extn.home.portlets.PickupOrderPortletExtn", [_extnPickupOrderPortletExtnUI],{
	// custom code here
	validateSearchPickUpOrderInput : function(pickUpOrderTargetModel){
			
			//console.log("pickUpOrderTargetModel : ",pickUpOrderTargetModel);
			
			var throwError = false, isNameComboValid = false, isNameEmpty = false;
			var hasOrderNo = false, hasEmailID = false, hasPhoneNo = false;
			var errorMsg = '';			
			var errorJSON = {};
			
			if(_scBaseUtils.isVoid(pickUpOrderTargetModel.Shipment)) {
				throwError = true;
				errorMsg = "pickUpOrderSearchCriteriaMsg";	
			}

			if( !throwError && !_scBaseUtils.isVoid(pickUpOrderTargetModel.Shipment.ShipmentLines) && !_scBaseUtils.isVoid(pickUpOrderTargetModel.Shipment.ShipmentLines.ShipmentLine.OrderNo)) {
				hasOrderNo = true;
			}

			if(!throwError && !_scBaseUtils.isVoid(pickUpOrderTargetModel.Shipment.BillToAddress)) {
			
				var firstName = pickUpOrderTargetModel.Shipment.BillToAddress.FirstName;
				var lastName = pickUpOrderTargetModel.Shipment.BillToAddress.LastName;
				var emailID = pickUpOrderTargetModel.Shipment.BillToAddress.EMailID;
				var phoneNo = pickUpOrderTargetModel.Shipment.BillToAddress.DayPhone;


				if(!_scBaseUtils.isVoid(firstName)){
					if(_scBaseUtils.isVoid(lastName)) {
						throwError = true;
						errorMsg = "firstAndLastNameMsg";
					} else {
						isNameComboValid = true;
					}
				} else {
					if(!_scBaseUtils.isVoid(lastName)) {
						//throwError = true;
						//errorMsg = "firstAndLastNameMsg";
						isNameComboValid = true;
					} else{
						isNameEmpty = true;
					}
				}
				
				if(!_scBaseUtils.isVoid(emailID)) {
					hasEmailID = true;
				}
				
				if(!_scBaseUtils.isVoid(phoneNo)) {
					var rx=/^\d{3}\-?\d{3}\-?\d{4}$/;
					if (!rx.test(phoneNo)) {
						errorMsg = "invalidPhoneNumber";
						throwError = true;
					}
					hasPhoneNo = true;
				}
				
			}
			
			if(throwError) {
				errorJSON.hasError = throwError;
				errorJSON.errorMsg = errorMsg;			
			} else if(!isNameComboValid && !hasOrderNo && !hasEmailID && !hasPhoneNo) {
				errorJSON.hasError = true;
				errorJSON.errorMsg = "pickUpOrderSearchCriteriaMsg";				
			}
			
			console.log("errorJSON : ",errorJSON);
			
			return errorJSON; 
		},
	pickUpOrderSearchAction: function() {
            var pickUpOrderTargetModel = null;
            pickUpOrderTargetModel = _scBaseUtils.getTargetModel(
            this, "customerpickup_getShipmentListForPickup_input", null);
            _scModelUtils.setStringValueAtModelPath("Shipment.SearchGiftAddresses", "Y", pickUpOrderTargetModel);
            var validationResult = null;
            validationResult = this.validateSearchPickUpOrderInput(
            pickUpOrderTargetModel);
            var hasValidationError = null;
            hasValidationError = _scModelUtils.getBooleanValueFromPath("hasError", validationResult, null);
            if (
            _scBaseUtils.isBooleanTrue(
            hasValidationError)) {
                var validationMsg = null;
                validationMsg = _scModelUtils.getStringValueFromPath("errorMsg", validationResult);
                var warningString = null;
                warningString = _scScreenUtils.getString(
                this, validationMsg);
                var textObj = null;
                textObj = _scBaseUtils.getNewBeanInstance();
                var textOK = null;
                textOK = _scScreenUtils.getString(
                this, "OK");
                _scBaseUtils.addStringValueToBean("OK", textOK, textObj);
                _scScreenUtils.showErrorMessageBox(
                this, warningString, "waringCallback", textObj, null);
            } else {
                _isccsUIUtils.callApi(
                this, pickUpOrderTargetModel, "extn_searchPickupOrders", null);
            }
        },
	 extInitialize : function(
        event, bEvent, ctrl, args) {
                var input = {};
                input.SessionInfo = {};

//                _isccsUIUtils.callMashup(this,input,'extn_getSessionInfo_mashup',null);
        },
	extnHandleMashupCompletion : function(
	event, bEvent, ctrl, args) {
		// check to see if we have any search results, if none, see if order number
		// was the input, if so, search by receipt id
		var searchResults = null;
		var searchInput = null;
		searchResults = _scBaseUtils.getAttributeValue("mashupArray",false, args);
		searchInput = _scBaseUtils.getAttributeValue("inputData",false, args);
		if (_scBaseUtils.equals("extn_searchPickupOrders",searchResults[0].mashupRefId)) {
			// make sure we have results
			if (Number(searchResults[0].mashupRefOutput.Shipments.TotalNumberOfRecords)==  0) {
			  // didn't find anything, try the receipt id search and then go to advanced search screen if we don't find anything
			  var input =  {};
			  input.Shipment = {};
			  input.Shipment.Extn = {};
			  if(!_scBaseUtils.isVoid(searchInput[0].mashupInputObject.Shipment.ShipmentLines)) {
			  	input.Shipment.Extn.ExtnStorePreencReceiptID = searchInput[0].mashupInputObject.Shipment.ShipmentLines.ShipmentLine.OrderNo;
			  	_isccsUIUtils.callApi(this, input, "extn_searchReceiptId", null);
			  } else {
				  this.showHideCustomerPickupOrderWizard(searchResults[0].mashupRefOutput,searchInput[0].mashupInputObject);
			  }
			} else {
				  this.showHideCustomerPickupOrderWizard(searchResults[0].mashupRefOutput,searchInput[0].mashupInputObject);
			}
		} else if (_scBaseUtils.equals("extn_searchReceiptId",searchResults[0].mashupRefId)) {
			// we've searched the receipt id as well follow the normal flow
			if (Number(searchResults[0].mashupRefOutput.Shipments.TotalNumberOfRecords)==  0) {
				// no results, reset the search back to OrderNo and take user to advanced search
				searchInput[0].mashupInputObject.Shipment.FireSearch = "Y";
				searchInput[0].mashupInputObject.Shipment.ShipmentLines = {};
				searchInput[0].mashupInputObject.Shipment.ShipmentLines.ShipmentLine = {};
				searchInput[0].mashupInputObject.Shipment.ShipmentLines.ShipmentLine.OrderNo = searchInput[0].mashupInputObject.Shipment.Extn.ExtnStorePreencReceiptID;
				this.showHideCustomerPickupOrderWizard(searchResults[0].mashupRefOutput,searchInput[0].mashupInputObject);
			} else {
				this.showHideCustomerPickupOrderWizard(searchResults[0].mashupRefOutput,searchInput[0].mashupInputObject);
			}
		}
	},
	showHideCustomerPickupOrderWizard: function(
        modelOutput, mashupInput) {
            var totalNumOfPickupOrders = 0;
            var newCustomerPickUpOrderModel = null;
            totalNumOfPickupOrders = _scModelUtils.getNumberValueFromPath("Shipments.TotalNumberOfRecords", modelOutput);
            if (
            _scBaseUtils.or(
            _scBaseUtils.equals(
            totalNumOfPickupOrders, 0), _scBaseUtils.greaterThan(
            totalNumOfPickupOrders, 1))) {
                var sGiftRecepient = null;
                sGiftRecepient = _scModelUtils.getStringValueFromPath("Shipments.GiftRecipient", modelOutput);
                this.openAdvancedShipmentSearch(
                mashupInput, sGiftRecepient);
            } else if (
            _scBaseUtils.equals(
            totalNumOfPickupOrders, 1)) {
                var shipments = null;
                var shipment = null;
                var shipmentStatus = null;
                var shipmentStore = null;
                shipments = _scModelUtils.getModelListFromPath("Shipments.Shipment", modelOutput);
                shipment = _scBaseUtils.getArrayBeanItemByIndex(
                shipments, 0);
                shipmentStatus = _scModelUtils.getStringValueFromPath("Status", shipment);
                shipmentStore = _scModelUtils.getStringValueFromPath("ShipNode", shipment);
                if (
                _scBaseUtils.negateBoolean(
                _scBaseUtils.equals(
                shipmentStatus.Status, "1100.70.06.30"), _scBaseUtils.equals(
                shipmentStore, _isccsContextUtils.getFromContext("CurrentStore")))) {
                    shipmentModel = _scBaseUtils.getNewModelInstance();
                    _scBaseUtils.setAttributeValue("Shipment", shipment, shipmentModel);
                    _wscShipmentSearchUtils.openShipmentSummary(
                    shipmentModel, this);
                } else {
                    shipmentModel = _scBaseUtils.getNewModelInstance();
                    _scModelUtils.setStringValueAtModelPath("Shipment.ShipmentKey", _scModelUtils.getStringValueFromPath("ShipmentKey", shipment), shipmentModel);
                    _isccsUIUtils.openWizardInEditor("wsc.shipment.wizards.CustomerPickupWizard", shipmentModel, "wsc.editors.CustomerPickupEditor", this);
                }
            }
            this.resetPickOrderSearchInput();
        },
	resetPickOrderSearchInput: function() {
            var eventDefn = null;
            var args = null;
            args = _scBaseUtils.getNewBeanInstance();
            eventDefn = _scBaseUtils.getNewBeanInstance();
            _scBaseUtils.setAttributeValue("argumentList", _scBaseUtils.getNewBeanInstance(), eventDefn);
            _scEventUtils.fireEventInsideScreen(
            this, "resetSearchPickUpOrderInput", eventDefn, args);
        }
});
});

