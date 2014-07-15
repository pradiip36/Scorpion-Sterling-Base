scDefine([
	"dojo/text!./templates/ShortageReasonDialog.html",
	"scbase/loader!dijit/form/Button",
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!idx/form/TextBox",
	"scbase/loader!idx/layout/ContentPane",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/widgets/Label",
	"scbase/loader!sc/plat/dojo/widgets/Link",
	"scbase/loader!sc/plat/dojo/widgets/Screen",
	"scbase/loader!wsc/utils/BackroomPickUtils",
	"scbase/loader!isccs/utils/UIUtils"
],
function(
	templateText,
	_dijitButton,
	_dojodeclare,
	_idxTextBox,
	_idxContentPane,
	_scBaseUtils,
	_scEventUtils,
	_scWidgetUtils,
	_scScreenUtils,
	_scLabel,
	_scLink,
	_scScreen,
	wscBackroomPickUtils,
	_isccsUIUtils
) {
    return _dojodeclare("extn.shipment.backroomPick.ShortageReasonDialog", [_scScreen], {
		templateString: templateText,
		uId: "shortageReasonDialog",
		packageName: "extn.shipment.backroomPick",
		className: "ShortageReasonDialog",
		showRelatedTask: false,
		namespaces: {
			sourceBindingNamespaces: [{
				description: 'Initial input to screen',
				value: 'extn_getShortageResolutions_input'
			}],
			targetBindingNamespaces: [{
                description: 'Output used in changeShipment call to set the shortage reason.',
                value: 'extn_getShortageResolutions_output'
            }],
		},
		hotKeys: [{
            id: "Popup_cancelButton",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "Popup_cancelButton",
            invocationContext: "",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }],
		staticBindings: [],
		events: [],
		subscribers: {
			local: [{
	        	eventId: 'Popup_btnCancel_onClick',
	            sequence: '30',
	            handler: {
	                methodName: "onCancelShortageReason"
	            }
        	}, {
	        	eventId: 'Popup_btnNext_onClick',
	            sequence: '30',
	            handler: {
	                methodName: "onSaveShortageReason"
	            }
        	}, {
        		eventId: 'afterBehaviorMashupCall',
        		sequence: '51',
        		handler : {
        			methodName : "handleMashupCompletion"
        		}
        	}],
		},
    	
    	/*****************************************
    	 * @author: Adam Dunmars
    	 * @description: This subscriber function: 
    	 * 	1) Saves the reason code for the shortage reason and closes the pop-up,
    	 * 	2) Displays the selected shortage reason in the parent screen's widget, 
    	 * 	3) Proceeds to handle the shortage as the OOB flow would.
    	 ******************************************/
    	onSaveShortageReason : function(event, bEvent, ctrl, args) {
    		var parentScreen = this.getOwnerScreen();
    		var shipmentLineModel = parentScreen.getModel("ShipmentLine");
    		var isLastShortageLineForCancellingShipment = wscBackroomPickUtils.isLastShortageLineForCancellingShipment(_isccsUIUtils.getParentScreen(parentScreen, true), shipmentLineModel);
    		var argBean = null;
            argBean = _scBaseUtils.getNewBeanInstance();
            argBean = _scBaseUtils.addStringValueToBean("isLastShortageLineForCancellingShipment", isLastShortageLineForCancellingShipment, argBean);

            var select = this.getWidgetByUId("cmbCustVerfMethod");
            if(select.isValid()){
                var mashInput = _scScreenUtils.getTargetModel(this, "extn_getShortageResolutions_output");
                _scWidgetUtils.setLabel(parentScreen, "extn_shortageReasonDataLbl", mashInput.Shipment.ShipmentLines.ShipmentLine.Extn.ExtnShortageReason);
                _scWidgetUtils.showWidget(parentScreen, "extn_shortageReasonDataLbl");
                parentScreen.updateShortageReason(mashInput);
        		parentScreen.updateShortageResolution("Ok", argBean);
        		
        		_scWidgetUtils.closePopup(this, "CLOSE", false);
            } else {
            	var message = "A cancel reason code must be selected.";
            	textObj = {};
            	textObj.OK = "Ok";
            	_scScreenUtils.showErrorMessageBox(this, message, "onErrorMessageReasonOK", textObj, null);
            	_scEventUtils.stopEvent(bEvent);
            }
    	},
    	
    	/*****************************************
    	 * @author: Adam Dunmars
    	 * @description: This subscriber function 
    	 * cancels selecting a shortage reason and 
    	 * closes the pop-up.
    	 ******************************************/
    	onCancelShortageReason : function(event, bEvent, ctrl, args) {
    		_scWidgetUtils.closePopup(this, "CLOSE", false);
    	},
    	
    	/*****************************************
    	 * @author: Adam Dunmars
    	 * @description: This subscriber method is the
    	 * handler for the showErrorMessageBox called in 
    	 * onSaveShortageReason to stop an undefined
    	 * error from being thrown.
    	 * TODO: Determine if there's an OOB
    	 * default handler that can be passed
    	 * rather than have to create a new function just for this...
    	 */
    	onErrorMessageReasonOK: function(event, bEvent, ctrl, args) {
    		
    	}
	});
});