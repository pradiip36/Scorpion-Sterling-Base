scDefine(["dojo/text!./templates/ManagerOverrideDialog.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/layout/ContentPane", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/binding/ImageDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/widgets/Image", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Screen", "scbase/loader!wsc/utils/CustomerPickUpUtils"], function(
templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _idxContentPane, _isccsBaseTemplateUtils, _isccsUIUtils, _scplat, _scButtonDataBinder, _scCurrencyDataBinder, _scImageDataBinder, _scBaseUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _scImage, _scLabel, _scScreen, _wscCustomerPickupUtils) {
    return _dojodeclare("extn.shipment.customerpickup.ManagerOverrideDialogUI", [_scScreen], {
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
        packageName: "extn.shipment.customerpickup",
        className: "ManagerOverrideDialog",
        extensible: true,
        title: "",
        screen_description: "This screen is used as Popop for Manager Override",
        namespaces: {
            targetBindingNamespaces: [{
                description: 'target namespace which initialize the preview.',
                value: 'popupData'
            },{			
                description: 'target namespace which initialize the preview.',
                value: 'extn_dataEntered'
            }],
            sourceBindingNamespaces: [{
                description: 'Source namespace',
                value: 'inputModel'
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
                eventId: 'Popup_Approve_onClick',
                sequence: '25',
                description: 'Start Over Button Action',
                listeningControlUId: 'Popup_Approve',
                handler: {
                    methodName: "onApprove",
                    description: ""
                }
            }, {
                eventId: 'Popup_cancelButton_onClick',
                sequence: '25',
                description: '',
                listeningControlUId: 'Popup_cancelButton',
                handler: {
                    methodName: "onPopUpCancelAction",
                    description: ""
                }
            }]
        },
        onApprove: function(
        event, bEvent, ctrl, args) {
            var popupData = null;
            popupData = _scBaseUtils.getTargetModel(
            this, "extn_dataEntered", null);
			
			var userName =  popupData.User.userName;
			var password =  popupData.User.password;
			
			this.mashupSaveMgrOverrideInput = {};
			this.mashupSaveMgrOverrideInput.Login = {};
			this.mashupSaveMgrOverrideInput.Login.UserName =userName;
			this.mashupSaveMgrOverrideInput.Login.Password = password;
			var inpModel = null;
			inpModel = _scScreenUtils.getModel(this, "inputModel");			
			this.mashupSaveMgrOverrideInput.Login.ShipmentId =inpModel.Shipment.ShipmentId;
			this.mashupSaveMgrOverrideInput.Login.TransactionInfoID =inpModel.Shipment.TransactionInfoID;
			console.log("mashupSaveMgrOverrideInput",this.mashupSaveMgrOverrideInput);			
			_isccsUIUtils.callApi(this, this.mashupShipmentInput, "extn_saveMgrOverride_mashup", null);            
        },
        onPopUpCancelAction: function(
        event, bEvent, ctrl, args) {
            var popupData = null;
            popupData = _scBaseUtils.getTargetModel(
            this, "popupData", null);
            _scModelUtils.setStringValueAtModelPath("actionPerformed", "CLOSEEDITOR", popupData);
            this.onPopupConfirm(
            popupData);
        },
        onPopupConfirm: function(
        popupData) {
            var actionPerformed = null;
            actionPerformed = _scModelUtils.getStringValueFromPath("actionPerformed", popupData);
            var parentScreen = null;
            if (
            _scBaseUtils.equals(
            actionPerformed, "CLOSEEDITOR")) {
                _scWidgetUtils.closePopup(
                this, "CLOSEEDITOR", false);
            }
        },
        handleMashupCompletion: function(
        mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {			
			if (_scBaseUtils.equals("extn_saveMgrOverride_mashup",mashupRefList[0].mashupRefId)) {
				var returnCode = mashupRefList[0].mashupRefOutput.User.returnCode;
				if(returnCode == "Y"){
				_scEventUtils.fireEventToParent(this,"disableApproverPopup",null);
				_scEventUtils.fireEventToParent(this,"saveCurrentPage",null);
				_scWidgetUtils.closePopup(
            this, "CLOSE", false);				
				}else{
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
		}
    });
});