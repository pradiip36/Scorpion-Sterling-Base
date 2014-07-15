scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!dojo/_base/kernel",
          "scbase/loader!dojo/_base/lang",
          "dojo/text!./templates/ManagerOverrideDialog.html",
          "scbase/loader!extn/shipment/customerpickup/ManagerOverrideDialogUI",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!sc/plat/dojo/utils/EventUtils",
          "scbase/loader!isccs/utils/UIUtils"],
          
          function(
        		  _dojodeclare, 
        		  _dojokernel,
        		  _dojolang,
        		  templateText,
        		  _wscManagerOverrideDialogUI,
        		  _scBaseUtils,
        		  _scScreenUtils,
        		  _scModelUtils,
        		  _scWidgetUtils,
        		  _scEventUtils,
        		  _isccsUIUtils) {
    return _dojodeclare("extn.shipment.customerpickup.ManagerOverrideDialog", [_wscManagerOverrideDialogUI], {
        // custom code here
    	templateString: templateText,
        postMixInProperties: function() {
            if (this.getScreenMode() != "default") {
                var origArgs = arguments;
                var htmlName = "templates/ManagerOverrideDialog_" + this.getScreenMode() + ".html";
                this.templateString = dojo.cache("extn.shipment.customerpickup", htmlName);
                var modeUIJSClassString = "extn.shipment.customerpickup.ManagerOverrideDialog_" + this.getScreenMode() + "UI";
                var that = this;
                var _scUtil = _dojolang.getObject("dojo.utils.Util", true, _scplat);
                _scUtil.getInstance(modeUIJSClassString, null, null, function(instance) {
                    _scBaseUtils.screenModeMixin(that, instance);
                    that.inherited(origArgs);
                });
            }
        },
        baseTemplate: {
            url: _dojokernel.moduleUrl("extn.shipment.customerpickup.templates", "ManagerOverrideDialog.html"),
            shared: true
        },
        uId: "managerOverrideDialog",
        Shipment: null,
        Exception: null,
        inputData: null,
        packageName: "extn.shipment.customerpickup",
        className: "ManagerOverrideDialog",
        extensible: true,
        title: "",
        screen_description: "This screen is used as Popop for Manager Override",
        namespaces: {
            targetBindingNamespaces: [{
                description: 'target namespace which initialize the preview.',
                value: 'popupData'
            }, {
            	description: "Admin credential information",
            	value: "Admin"
            }],
            sourceBindingNamespaces: [{
                description: 'Source namespace',
                value: 'inputData'
            }, {
            	description: "Shipment Detail information used for this dialog.",
            	value : "Shipment"
            }, {
            	description: "Exception information captured from previous screen.",
            	value : "Exception"
            }]
        },
        showRelatedTask: false,
        isDirtyCheckRequired: false,
        hotKeys: [{
            id: "Popup_cancelButton",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "Popup_cancelButton",
            invocationContext: "",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }],
        subscribers: {
            local: [{
                eventId: 'approveButton_onClick',
                sequence: '25',
                description: 'Start Over Button Action',
                listeningControlUId: 'Popup_Approve', 
                handler: {
                    methodName: "onApprove",
                    description: ""
                }
            }, {
                eventId: 'cancelButton_onClick',
                sequence: '25',
                description: '',
                listeningControlUId: 'Popup_cancelButton',
                handler: {
                    methodName: "onPopUpCancelAction",
                    description: ""
                }
            }, {
            	eventId: "afterScreenInit",
            	sequence: '25',
            	description: "",
            	handler: {
            		methodName: "extnInitialize",
            		description: ""
            	}
            }]
        },
        
        /*************************************************
         * @author: Adam Dunmars
         * @subscribedTo: afterScreenInit
         * @description: This subscriber function is used
         * to test the screen.
         * TODO: Delete when done if not used.
         *************************************************/
        extnInitialize : function(event, bEvent, ctrl, args) {
        	_scWidgetUtils.showWidget(this, "warningIcon", false, null);
        	this.setModel("Shipment", this.Shipment);
        	this.setModel("Exception", this.Exception);
        	this.setModel("inputData", this.inputData);
        	console.log("INITIAL INPUT: ", this._initialInputData);
        	var shipmentModel = this.getModel("Shipment");
        	var exceptionModel = this.getModel("Exception");
        	var inputData = this.getModel("inputData");
        	console.log("Shipment Model: ", shipmentModel);
        	console.log("Exception Model: ", exceptionModel);
        	console.log("Input Model: ", inputData);
        },
        
        /****************************************
         * @author: ??? (OMS Team?)/Adam Dunmars
         * @subscribedTo:approveButton_onClick
         * @description: ENTER DESCRIPTION HERE
         * TODO: Work on this ~Adam
         ****************************************/
        onApprove: function(event, bEvent, ctrl, args) {
        	var adminModel = _scBaseUtils.getTargetModel(this, "Admin", null);
            console.log("Admin Model: ", adminModel);
        			
			var username =  adminModel.Admin.Username;
			var password =  adminModel.Admin.Password;
        			
			var mashupSaveMgrOverrideInput = {};
			mashupSaveMgrOverrideInput.Login = {};
			mashupSaveMgrOverrideInput.Login.UserName =username;
			mashupSaveMgrOverrideInput.Login.Password = password;
			
			var inpModel = null;
			inpModel = _scScreenUtils.getModel(this, "inputData");	
			
			mashupSaveMgrOverrideInput.Login.ShipmentId = inpModel.Shipment.ShipmentId;
			mashupSaveMgrOverrideInput.Login.TransactionInfoID = inpModel.Shipment.TransactionInfoID;
			
			console.log("mashupSaveMgrOverrideInput", mashupSaveMgrOverrideInput);			
			
			_isccsUIUtils.callApi(this, mashupSaveMgrOverrideInput, "extn_saveMgrOverride_mashup", null);            
        },

        /****************************************
         * @author: ??? (OMS Team?)/Adam Dunmars
         * @subscribedTo: cancelButton_onClick
         * @description: This subscriber function 
         * will close the pop-up window. 
         * TODO: Work on this ~Adam
         ****************************************/
        onPopUpCancelAction: function(event, bEvent, ctrl, args) {
        	_scWidgetUtils.closePopup(this, "CLOSEEDITOR", false);
        },
       
        /****************************************
         * @author: ??? (OMS Team?)
         * @subscribedTo: ???
         * @description: ENTER DESCRIPTION HERE
         * TODO: Work on this ~Adam
         * TODO 2: Change this to use the preferred method.
         ****************************************/
        handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {			
			if (_scBaseUtils.equals("extn_saveMgrOverride_mashup",mashupRefList[0].mashupRefId)) {
				var returnCode = mashupRefList[0].mashupRefOutput.User.returnCode;
				if(returnCode == "Y"){
					_scEventUtils.fireEventToParent(this,"disableApproverPopup",null);
					_scEventUtils.fireEventToParent(this,"saveCurrentPage",null);
					_scWidgetUtils.closePopup(this, "CLOSE", false);				
				} else {
					//_scScreenUtils.showErrorMessageBox(this, "Please enter a value into the scan field.", null, null, null);
					_scWidgetUtils.displaySingleMessage(this, "messagePanelHolder", "errorMessagePanel", {
						"title": "Manager Override error",
						"description": "Incorrect credentials",
						"type": "error",
						"showAction": false,
						"showTimestamp": false
					});
				}
			}
		},
                
    	/******************************************
    	 * @author: Adam Dunmars
    	 * @description: This dynamic binding function returns the 
    	 * shipment's customer's first name and last name
    	 * using Shipment/BillToAddress/@FirstName and
    	 * Shipment/BillToAddress/@LastName.
    	 ******************************************/
    	getCustomerName : function(dataValue, screen, widget, namespace, modelObj, options) {
    		if(_scBaseUtils.isVoid(dataValue)) {
    			console.warn("WARNING: Shipment/BillToAddress is N/A! Model: ", modelObj);
    			return "N/A";
    		} else {
    			var firstName = dataValue.FirstName;
    			var lastName = dataValue.LastName;
    			return firstName + " " + lastName;
    		}
    	},
    	
    	/******************************************
    	 * @author: Adam Dunmars
    	 * @description: This dynamic binding function returns the 
    	 * shipment's alternate customer's first name and last name
    	 * using Shipment/BillToAddress/@FirstName and
    	 * Shipment/BillToAddress/@LastName.
    	 * TODO: Determine where the alternate name is supposed to be...~Adam
    	 ******************************************/
    	getAlternateName: function(dataValue, screen, widget, namespace, modelObj, options) {
    		if(_scBaseUtils.isVoid(dataValue)) {
    			console.warn("WARNING: Shipment/BillToAddress is N/A! Model: ", modelObj);
    			return "N/A";
    		} else {
    			var firstName = dataValue.FirstName;
    			var lastName = dataValue.LastName;
    			return firstName + " " + lastName;
    		}
    	},
    	
    	/******************************************
    	 * @author: Adam Dunmars
    	 * description: This dynamic binding function returns the
    	 * Billing Address of the shipment using XXXXX.
    	 * TODO: Implement this function correctly. ~Adam
    	 ******************************************/
    	getBillingAddress: function(dataValue, screen, widget, namespace, modelObj, options) {
    		if(_scBaseUtils.isVoid(dataValue)) {
    			console.warn("WARNING: Shipment/BillToAddress is N/A! Model: ", modelObj);
    			return "N/A";
    		} else {
    			var firstName = dataValue.FirstName;
    			var lastName = dataValue.LastName;
    			var addressLineOne = firstName + " " + lastName;
    			var addressLineTwo = dataValue.AddressLine1;
    			var addressLineThree = dataValue.City + ", " + dataValue.State + " " + dataValue.ZipCode;
    			return addressLineOne + "\n" + addressLineTwo + "\n" + addressLineThree;
    		}
    	},
    	
    	/******************************************
    	 * @author: Adam Dunmars
    	 * @description: This dynamic binding function returns the
    	 * exception first name using the value entered in the text field
    	 * using Exception/@FirstName.
    	 ******************************************/
    	getExceptionFirstName : function(dataValue, screen, widget, namespace, modelObj, options) {
    		if(_scBaseUtils.isVoid(dataValue)) {
    			console.warn("WARNING: Exception/@FirstName is N/A! Model: ", modelObj);
    			return "N/A";
    		} else {
    			return dataValue;
    		}
    	},
    	
    	/******************************************
    	 * @author: Adam Dunmars
    	 * @description: This dynamic binding function returns the
    	 * exception last name using the value entered in the text field 
    	 * using Exception/@LastName.
    	 ******************************************/
    	geExceptionLastName : function(dataValue, screen, widget, namespace, modelObj, options) {
    		if(_scBaseUtils.isVoid(dataValue)) {
    			console.warn("WARNING: Exception/@LastName is N/A! Model: ", modelObj);
    			return "N/A";
    		} else {
    			return dataValue;
    		}
    	},
    	
    	/******************************************
    	 * @author: Adam Dunmars
    	 * @description: This dynamic binding function returns the
    	 * exception reason using the value selected from the drop down
    	 * using Exception/@Reason.
    	 ******************************************/
    	getExceptionReasonCode : function(dataValue, screen, widget, namespace, modelObj, options) {
    		if(_scBaseUtils.isVoid(dataValue)) {
    			console.warn("WARNING: Exception/@Reason is N/A! Model: ", modelObj);
    			return "N/A";
    		} else {
    			return dataValue;
    		}
    	}
    });
});