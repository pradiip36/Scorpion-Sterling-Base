scDefine([
          "scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/customerpickup/CustomerIdentificationExtnUI",
          "scbase/loader!dojo/_base/lang",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!wsc/utils/CustomerPickUpUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/EventUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!isccs/utils/WizardUtils",
          "scbase/loader!sc/plat/dojo/utils/EditorUtils",
          "scbase/loader!extn/utils/ScanUtils"],
function(			 
		_dojodeclare,
		_extnCustomerIdentificationExtnUI,
		dLang,
		_isccsUIUtils,
		scScreenUtils,
		_wscCustomerPickUpUtils,
		_scBaseUtils,
		_scEventUtils,
		_scWidgetUtils,
		_scModelUtils,
		_scScreenUtils,
		_wizardUtils,
		_scEditorUtils,
		_bopusUtils
){ 
	return _dojodeclare("extn.shipment.customerpickup.CustomerIdentificationExtn", [_extnCustomerIdentificationExtnUI],{
		// custom code here
		//Look into the error with CPICK_ORDER_29 to see if its valid error~Adam

		/*********************************
		 * @author: ???/Adam Dunmars
		 * @subscribedTo: initializeScreen (OOB)
		 * @description: This subscriber function:
		 * 		1) Hides the Close Button of the wizard.
		 * TODO: All common code initialization needs to move to the wizard level
		 ********************************/
		extInitialize: function(event, bEvent, ctrl, args) {
			this.hideCloseButton();
			this.addStartPickupEJRecord();
	        
		},
		
		/******************************
		 * @author: Adam Dunmars
		 * @description: This function hides the wizard's close button.
		 *****************************/
		hideCloseButton: function() {
			var wizard = _bopusUtils.getWizardForScreen(this);
	        _wizardUtils.hideNavigationalWidget(wizard, 'closeBttn', true);
		},
		
		/*********************************
		 * @author: ??? / Adam Dunmars
		 * @description: This function adds an entry into the EJ
		 * transaction table showing that a pickup transaction has
		 * been initiated. 
		 ********************************/
		addStartPickupEJRecord: function(event, bEvent, ctrl, args) {	
			var mashInput = {};
			mashInput.ShipmentStoreEvents = {};
			mashInput.ShipmentStoreEvents.OrderNo = 
				this.sourceOriginalModelMap.getCustomerPickupOrder_Source.Shipment.ShipmentLines.ShipmentLine[0].OrderNo;
			mashInput.ShipmentStoreEvents.EventType = "Pickup Started";
			mashInput.ShipmentStoreEvents.OrderHeaderKey = 
				this.sourceOriginalModelMap.getCustomerPickupOrder_Source.Shipment.ShipmentLines.ShipmentLine[0].OrderHeaderKey;
			mashInput.ShipmentStoreEvents.ShipmentKey = 
				this.sourceOriginalModelMap.getCustomerPickupOrder_Source.Shipment.ShipmentKey;
	
			_isccsUIUtils.callApi(this, mashInput, "extn_addEJforBeginPickup", null);
		},
		
		/***********************************
		 * @author: Adam Dunmars
		 * @description: This function displays a dialog box
		 * prompting the user to void a shipment. It changes
		 * the OK value from "Ok" to "Yes" and the CANCEL
		 * value from "Cancel" to "No".
		 * TODO: Determine if this is an ask and if this should be the correct message ~Adam.
		 *************************************/
		displayVoidPrompt : function(event, bEvent, ctrl, args) {
			var message = "Are you sure you want to void the order?";
			var voidPromptCallback = "voidPromptCallback";
			var textObj = {};
			textObj.OK = "Yes";
			textObj.CANCEL = "No";
			_scScreenUtils.showConfirmMessageBox(this, message, voidPromptCallback, textObj);
		},
		
		/************************************
		 * @author: Adam Dunmars
		 * @description: This is a callback function that determines
		 * whether to void a shipment based on the user's selection.
		 * TODO: Determine if some EJ Transaction should happen here. ~Adam
		 ************************************/
		voidPromptCallback : function(selection) {
			if(_scBaseUtils.equals(selection, "Ok")) {
				this.processVoid();
			} 
		},
	
		/*********************************
		 * @author: ??? (Maseeh Sabir)?
		 * @description: This function prints a void receipt
		 * for the customer and closes the wizard. 
		 * TODO: Remove the wizard argument from ScanUtils.printReceipt. ~Adam 
		 ********************************/
		 processVoid: function() {
			var wizard = _bopusUtils.getWizardForScreen(this);
	
	    	var modelOutput = _scScreenUtils.getModel(this, "getCustomerPickupOrder_Source");
	    	var commonModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_void");
	    	var storeInfo = _scScreenUtils.getModel(this,"extn_shipNode_info_void");
	    	var diamondDataModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_diamond");
	    	
	    	modelOutput.ReceiptType = "voidpickup";
	    	modelOutput.diamondModel = diamondDataModel;
	
			_bopusUtils.printReceipt(wizard, modelOutput, commonModel, storeInfo);
		 },
		 
		 /*********************************
		  * @author: ??? (James Butler?)
		  * @description: This function will show widgets for use 
		  * in exception verification.
		  ********************************/
		 checkForException: function(selectedValue, var2, args, ctrl, event, bEvent) {
			 var firstNameUID = "extn_textfield_firstName";
			 var lastNameUID = "extn_textfield_lastName";
			 var exceptionReasonUID = "extn_filteringselect_verificationReason";
			 if (_scBaseUtils.equals(selectedValue,  "Exception")) {
				 //this.checkForOverride(); This may need to happen here. ~Adam 
				 _scWidgetUtils.showWidget(this, firstNameUID, false, null);
				 _scWidgetUtils.showWidget(this, lastNameUID, false, null);
				 _scWidgetUtils.showWidget(this, exceptionReasonUID, false, null);
				 _scWidgetUtils.setWidgetMandatory(this, firstNameUID);
				 _scWidgetUtils.setWidgetMandatory(this, lastNameUID);
				 _scWidgetUtils.setWidgetMandatory(this, exceptionReasonUID);
			 } else {
				 _scWidgetUtils.hideWidget(this, firstNameUID, false, null);
				 _scWidgetUtils.hideWidget(this, lastNameUID, false, null);
				 _scWidgetUtils.hideWidget(this, exceptionReasonUID, false, null);
				 _scWidgetUtils.setWidgetNonMandatory(this, firstNameUID);
				 _scWidgetUtils.setWidgetNonMandatory(this, lastNameUID);
				 _scWidgetUtils.setWidgetNonMandatory(this, exceptionReasonUID);
			 }
		 },
		 
		 /*********************************
		  * @author: ???/Adam Dunmars(Offshore/OMS?)
		  * @subscribedTo: saveCurrentPage(OOB).
		  * @description: This subscriber function will check to see if the 
		  * exception identification selection was chosen and if the approval was
		  * successful, it will make a call to the checkManagerOverride mashup.
		  * TODO: 
		  * 	1) Possibly add namespace model instead of pulling from
		  * 	the widgets directly...
		  * 	2) Uncomment the service call information once validation is added.
		  ********************************/
		 checkForOverride : function(event,bEvent,ctrl,args) {
			 var pickUpOrderDetailsModel = null;
			 pickUpOrderDetailsModel = _scScreenUtils.getModel(this, "getCustomerPickupOrder_Source");
			 console.log("PickupOrderDetailsModel: ", pickUpOrderDetailsModel);
	        
	        var verificationWidget = scScreenUtils.getWidgetByUId(this,"cmbCustVerfMethod");
	        if (_scBaseUtils.equals(verificationWidget.value, 'Exception') && !_scBaseUtils.equals(this.approvalDone, "T")) {
				var firstNameWidget = this.getWidgetByUId("extn_textfield_firstName");
				var lastNameWidget = this.getWidgetByUId("extn_textfield_lastName");
				var reasonCodeWidget = this.getWidgetByUId("extn_filteringselect_verificationReason");
				 
				var mashupShipmentInput = {};
				mashupShipmentInput.Shipment = {};
				mashupShipmentInput.Shipment.ShipmentId = pickUpOrderDetailsModel.Shipment.ShipmentKey;
				mashupShipmentInput.Shipment.givenFname = pickUpOrderDetailsModel.Shipment.BillToAddress.FirstName;
				mashupShipmentInput.Shipment.givenLname = pickUpOrderDetailsModel.Shipment.BillToAddress.LastName;
				mashupShipmentInput.Shipment.TransactionInfoID = pickUpOrderDetailsModel.Shipment.Status.Status;
				mashupShipmentInput.Shipment.dataCaptureFname = firstNameWidget.value;
				mashupShipmentInput.Shipment.dataCaptureLname = lastNameWidget.value;
				mashupShipmentInput.Shipment.dataCaptureReasonCode = reasonCodeWidget.value;
				console.log("Reason Code Value: ", reasonCodeWidget.value);
				console.log("Mashup Input Reason Code Value: ", mashupShipmentInput.Shipment.dataCaptureReasonCode);
				
				console.log("mashupShipmentInput", mashupShipmentInput);
				
				this.mashupShipmentInput = mashupShipmentInput;
				//_isccsUIUtils.callMashup(this, mashupShipmentInput, "extn_chkMgrOverride_mashup", null); commented for now until we get MgerOverride to work
				_scEventUtils.stopEvent(bEvent);
				this.gotoNextScreen(); //This needs to be commented out when ManagerOVerride works fully
				//this.openOverrideWindow(); ~Adam Now works! Just need to get the 2 service calls setup...
			} else{
				this.gotoNextScreen();
			}
		},
		
		/*********************************
		 * @author: ???
		 * @description: This function clears the screen (?)
		 * and fires saved event to parent.
		 * TODO: Determine if this function is actually necessary..~Adam
		 ********************************/
		gotoNextScreen: function() {
	        //_scScreenUtils.clearScreen(this, null); ~Adam
			var pickUpOrderDetailsModel = _scScreenUtils.getModel(this, "getCustomerPickupOrder_Source");
			_isccsUIUtils.setWizardModel(this, "PickUpOrderDetailsModel", pickUpOrderDetailsModel); 
			
	        _scEventUtils.fireEventToParent(this, "onSaveSuccess", null);
	    },
		
		/*********************************
		 * @author: ???(Robert Fea?)/Adam Dunmars
		 * @subscribedTo: saveCurrentPage(OOB).
		 * @description: This function overrides the OOB save function.
		 * This function will: 
		 * 		1) Check to see if the exception option was selected in the customer verification
		 * 			reason drop down and if it was:
		 * 			a) 	Update the shipment model ExtnPickupFirstName, ExtnPickupLastName and 
		 *				exception reason with the values entered in the text fields, otherwise:
		 * 			b) 	Update the shipment model ExtnPickupFirstName and ExtnPickupLastName using
		 * 				i)	The PersonInfoMarkFar supplied by the first OrderLine with it available,
		 * 				ii) Or the BillToAddress/@FirstName and BillToAddress/@LastName if the 
		 * 					PersonInfoMarkFor isn't available.
		 * 		2) 
		 * TODO: Try to reduce the verbosity of this function. ~Adam
		 * TODO 2: See if the mashup calls are different or if they can be consolidated.
		 * TODO 3: Eror happens when exception is selected. 
		 *         When confirming order (customerVerificationNotesModel is null)
		 ********************************/
		save: function(event, bEvent, ctrl, args) {
		    var pickUpOrderDetailsModel = null;
	        pickUpOrderDetailsModel = _scScreenUtils.getModel(this, "getCustomerPickupOrder_Source");
	        var customerVerificationNotesModel = _scScreenUtils.getTargetModel(this, "CustomerVerificationNotes_Input", null);
		    var verificationWidget = scScreenUtils.getWidgetByUId(this,"cmbCustVerfMethod");
		    var input = {};
		    input.Shipment = {};
		    var shipmentKey = dLang.getObject("ShipmentKey", false, pickUpOrderDetailsModel.Shipment);
		    input.Shipment.ShipmentKey = shipmentKey;
		    input.Shipment.Extn = {};
		    if (_scBaseUtils.equals(verificationWidget.value, "Exception")) {
	            var firstName = scScreenUtils.getWidgetByUId(this,"extn_textfield_firstName");
				var lastName = scScreenUtils.getWidgetByUId(this,"extn_textfield_lastName");
	            var reasonCode = scScreenUtils.getWidgetByUId(this,"extn_filteringselect_verificationReason");
	
		        input.Shipment.Extn.ExtnPickedCustFirstName = firstName.value;
		        input.Shipment.Extn.ExtnPickedCustLastName = lastName.value;
	            input.Shipment.Extn.ExtnPickupFirstName = firstName.value;
	            input.Shipment.Extn.ExtnPickupLastName = lastName.value;
	            input.Shipment.Extn.ExtnPickupIDReason = reasonCode.value;
	            _isccsUIUtils.callApi(this, input, "extn_changeShipmentPickUp_mashup", null);
	    	} else { //Default approach
				var shipmentModel = {};
				var isPersonInfoMarkForPresent = false;
				if(!_scBaseUtils.isVoid(pickUpOrderDetailsModel) && !_scBaseUtils.isVoid(pickUpOrderDetailsModel.Shipment)) {
					shipmentModel.Shipment = pickUpOrderDetailsModel.Shipment;
					if(!_scBaseUtils.isVoid(shipmentModel.Shipment.ShipmentLines.ShipmentLine.length)) {
						for(var i=0;i<shipmentModel.Shipment.ShipmentLines.ShipmentLine.length;i++) {
							if(!_scBaseUtils.isVoid(shipmentModel.Shipment.ShipmentLines.ShipmentLine[i].OrderLine.PersonInfoMarkFor)) {					
								isPersonInfoMarkForPresent = true;
								if(!_scBaseUtils.isVoid(shipmentModel.Shipment.ShipmentLines.ShipmentLine[i].OrderLine.PersonInfoMarkFor.FirstName)) {
									input.Shipment.Extn.ExtnPickedCustFirstName = shipmentModel.Shipment.ShipmentLines.ShipmentLine[i].OrderLine.PersonInfoMarkFor.FirstName;
	   		        				input.Shipment.Extn.ExtnPickedCustLastName = shipmentModel.Shipment.ShipmentLines.ShipmentLine[i].OrderLine.PersonInfoMarkFor.LastName;
									input.Shipment.Extn.ExtnPickupFirstName = shipmentModel.Shipment.ShipmentLines.ShipmentLine[i].OrderLine.PersonInfoMarkFor.FirstName; 
									input.Shipment.Extn.ExtnPickupLastName = shipmentModel.Shipment.ShipmentLines.ShipmentLine[i].OrderLine.PersonInfoMarkFor.LastName;	
								} else {
									input.Shipment.Extn.ExtnPickedCustLastName = shipmentModel.Shipment.ShipmentLines.ShipmentLine[i].OrderLine.PersonInfoMarkFor.LastName;
									input.Shipment.Extn.ExtnPickupLastName = shipmentModel.Shipment.ShipmentLines.ShipmentLine[i].OrderLine.PersonInfoMarkFor.LastName;	
								}
								break;
							}
						}	
					}
					if(!isPersonInfoMarkForPresent 
						&& !_scBaseUtils.isVoid(shipmentModel.Shipment.BillToAddress) 
						&& !_scBaseUtils.isVoid(shipmentModel.Shipment.BillToAddress.FirstName) 
						&& !_scBaseUtils.isVoid(shipmentModel.Shipment.BillToAddress.LastName)) {
							input.Shipment.Extn.ExtnPickupFirstName = shipmentModel.Shipment.BillToAddress.FirstName;
							input.Shipment.Extn.ExtnPickupLastName = shipmentModel.Shipment.BillToAddress.LastName;
				        	input.Shipment.Extn.ExtnPickedCustFirstName = shipmentModel.Shipment.BillToAddress.FirstName;
		   		        	input.Shipment.Extn.ExtnPickedCustLastName = shipmentModel.Shipment.BillToAddress.LastName;
					}
				}	
				_isccsUIUtils.callApi(this, input, "extn_changeShipmentPickUp", null);
			}
	        customerVerificationNotesModel = _wscCustomerPickUpUtils.massageCustVerificationNotesModel(this, customerVerificationNotesModel, pickUpOrderDetailsModel);
	        _isccsUIUtils.setWizardModel(this, "PickUpOrderDetailsModel", pickUpOrderDetailsModel, null);
	        _isccsUIUtils.setWizardModel(this, "customerVerificationNotes_input", customerVerificationNotesModel, null);
	        _scScreenUtils.clearScreen(this, "CustomerVerificationNotes_Input");
	        _scEventUtils.fireEventToParent(this, "onSaveSuccess", args);
		},
		 
		 //May not be used ~Adam
		 afterVerificationDropDownValidation: function(arg1, arg2, arg3, arg4, arg5, arg6) {
			 
		 },
		 
		/*********************************
		 * @author: Adam Dunmars/??? (Maseeh?)
		 * @description: This function overrides the OOB 
		 * handleMashupOutput.
		 * TODO: Determine usage. and if this is stopping other mashups from being called ~_~ ~Adam
		 * TODO: Hey this can probably use a switch too. ~Adam
		 ********************************/
		 handleMashupOutput: function(mashupRefId, modelOutput, modelInput, mashupContext) {
			if (_scBaseUtils.equals(mashupRefId, "extn_resumePick_mashup")) {
				if (modelOutput.Shipment.CommercialValue == "1.00" || modelOutput.Shipment.CommercialValue == "1") {
					_scWidgetUtils.showWidget(this,"extn_link_ResumeLink",false,null);
				}
			 } else if (_scBaseUtils.equals(mashupRefId, "extn_getKohlsCustomerVerificationList")) {
	            scScreenUtils.setModel(this, "extn_getKohlsVerificationReasonList_output", modelOutput, null);
			 } else if (_scBaseUtils.equals(mashupRefId, "extn_getShipNode_mashup")) {
	            // set the response in the page so we can refer to it later
	            _scScreenUtils.setModel(this, "extn_shipNode_info", modelOutput, null);
			 }
		},
			
	    /*********************************
		 * @author: ??? (OMS Team?)
		 * @subscribedTo: ???
		 * @description: Insert description here.
		 * TODO: Fix this function. ~Adam (this one could be useful to keep)
		 ********************************/
	    getValidationData:function(shipmentModel) {
	        var mashupShipmentInput = {};
	        mashupShipmentInput.Shipment = {};
	        mashupShipmentInput.Shipment.ShipmentId =  shipmentModel.Shipment.ShipmentNo;          
	        mashupShipmentInput.Shipment.givenFname =  shipmentModel.Shipment.BillToAddress.FirstName;
	        mashupShipmentInput.Shipment.givenLname =  shipmentModel.Shipment.BillToAddress.LastName;
	        
	        var custName = arguments[3].data.Shipment.BillToAddress.FirstName + " " + arguments[3].data.Shipment.BillToAddress.LastName;
	        mashupShipmentInput.Shipment.givenCustName = custName;
	        
	        this.mashupShipmentInput = mashupShipmentInput;
	        
	        return mashupShipmentInput;
	    },
	    		
	    /*********************************
		 * @author: ???
		 * @description Add description here.
		 * TODO: Fix this function. ~Adam
		 ********************************/
		openOverrideWindow : function() {		
	        var popupParams = null;
	        var popupTitle = "Manager Override";
	        var className = "extn.shipment.customerpickup.ManagerOverrideDialog";
	        var pickUpOrderDetailsModel = _scScreenUtils.getModel(this, "getCustomerPickupOrder_Source");
	        
	        var exceptionModel = _scBaseUtils.getNewBeanInstance();
	        exceptionModel.Exception = _scBaseUtils.getNewBeanInstance();
	        exceptionModel.Exception.FirstName = scScreenUtils.getWidgetByUId(this,"extn_textfield_firstName").value;
	        exceptionModel.Exception.LastName = scScreenUtils.getWidgetByUId(this,"extn_textfield_lastName").value;
	        exceptionModel.Exception.Reason = scScreenUtils.getWidgetByUId(this,"extn_filteringselect_verificationReason").value;
	        
	        popupParams = _scBaseUtils.getNewBeanInstance();
	        popupParams.screenInput = _scBaseUtils.getNewBeanInstance();
	        popupParams.outputNamespace = "Shipment";
	        popupParams.screenConstructorParams = [];
	        popupParams.screenConstructorParams["Shipment"] = pickUpOrderDetailsModel;
	        popupParams.screenConstructorParams["Exception"] = exceptionModel;
	        popupParams.screenConstructorParams["inputData"] = this.mashupShipmentInput;
	        popupParams.screenInput.Shipment = pickUpOrderDetailsModel;
	        popupParams.screenInput.Exception = exceptionModel;
	        popupParams.screenInput.inputData = this.mashupShipmentInput;
	        
	        var dialogParams = null;
	        dialogParams = _scBaseUtils.getNewBeanInstance();
	        //_scBaseUtils.addStringValueToBean("closeCallBackHandler", "onChangeCancellationReasonCode", dialogParams);
	        _isccsUIUtils.openSimplePopup(className, popupTitle, this, popupParams, dialogParams);    
		},
		
		/*********************************
		 * @author: ???
		 * @subscribedTo: S
		 * @description: ???
		 * TODO: Fix this function ~Adam
		 ********************************/
		handleOnExtnMashupCompletion: function(event, bEvent, ctrl, args) {
		    if(args == null || args.hasError){
		    	// handle error. 
		    	// args.errorData will contain error related information
		    }  
		    else{
				var searchResults = null;
				var searchInput = null;
				searchResults = _scBaseUtils.getAttributeValue("mashupArray",false, args);
				if (_scBaseUtils.equals("extn_chkMgrOverride_mashup",searchResults[0].mashupRefId)) {
					var returnCode = searchResults[0].mashupRefOutput.Validation.returnCode;
					if(_scBaseUtils.equals("Y",returnCode)){
						this.mashupShipmentInput.Shipment.TransactionInfoID = searchResults[0].mashupRefOutput.Validation.TransactionInfoID;
						console.log("managerOverride popup");
						this.openOverrideWindow();	
					}
				}
		    }
		},
		
		/*********************************
		 * @author: ???
		 * @description: This function sets the approval to be true.
		 * TODO: Determine usage.
		 ********************************/
		setApprovalFlag: function(event, bEvent, ctrl, args){
			this.approvalDone = "T";
		},
		
		/*********************************
		 * @author: ???
		 * @description: This function sets the approval to be false.
		 * TODO: Determine usage.
		 ********************************/
		resetApprovalFlag: function(event, bEvent, ctrl, args){
			this.approvalDone = "F";
		}
	});
});