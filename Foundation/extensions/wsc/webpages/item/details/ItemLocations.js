scDefine([
	"dojo/text!./templates/ItemLocations.html",
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
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!wsc/utils/CustomerPickUpUtils"
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
	_isccsUIUtils,
	_wscCustomerPickUpUtils
) {
    return _dojodeclare("extn.item.details.ItemLocations", [_scScreen], {
		templateString: templateText,
		uId: "itemLocations",
		packageName: "extn.item.details",
		className: "ItemLocations",
		showRelatedTask: false,
		namespaces: {
			targetBindingNamespaces: [{
                description: "Item Details used for repeating data",
                value: 'itemDetails'
            },{
            	description : "Stuff", 
            	value : "ShipmentLine"
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
                eventId: 'afterScreenInit',
                sequence: '30',
                handler: {
                    methodName: "initializeScreen"
                }
            }, {
            	eventId: 'afterParentScreenStartup',
            	sequence: '30',
            	handler: {
            		methodName :"getInitModel"
            	}
            }],
        },
		
        /******************************************
         * @author: Adam Dunmars
         * @description: The sole purpose of this function is to verify screen is being initialized. 
         * Insert debug text here as needed.
         * 
         * TODO: Remove when everything is fully finished.
         *********************************************/
		initializeScreen : function(event, bEvent, ctrl, args) {
		}, 
		
		/******************************************
         * @author: Adam Dunmars
         * @description: This function determines if any sales floor locations
         * or stock room locations were returned and, if not, creates
         * empty elements so that the child repeating screens will display
         * N/A values at least once.
         *********************************************/
		getInitModel : function(event, bEvent, ctrl, args) {
			var parentScreen = this.getOwnerScreen();
			var shipmentLineModel = _scScreenUtils.getModel(parentScreen, "extn_ShipmentLine");
			
			if(_scBaseUtils.isVoid(shipmentLineModel.SalesFloorLocations)) {
				shipmentLineModel.SalesFloorLocations = {};
				shipmentLineModel.SalesFloorLocations.SalesFloorLocation = [];
				shipmentLineModel.SalesFloorLocations.SalesFloorLocation[0]  = {};
			}
			
			if(_scBaseUtils.isVoid(shipmentLineModel.StockRoomLocations)) {
				shipmentLineModel.StockRoomLocations = {};
				shipmentLineModel.StockRoomLocations.StockRoomLocation = [];
				shipmentLineModel.StockRoomLocations.StockRoomLocation[0]  = {};
			}
			
			_scScreenUtils.setModel(this, "ShipmentLine", shipmentLineModel);
		},
				
		/********************************************
		 * @author: Adam Dunmars
		 * @description: This repeating screen generator function
		 * fetches repeating data for Stock Room locations.
		 ********************************************/
        getStockRoomLocationRepeatingScreenData: function(value, screen, widget, namespace, modelObject) {
            var repeatingScreenId = "extn.item.details.ItemStockroomLocations";
            var returnValue = null;
            var screenMode = "default";
            var additionalParamsBean = null;
            additionalParamsBean = _scBaseUtils.getNewBeanInstance();
            _scBaseUtils.addStringValueToBean("screenMode", screenMode, additionalParamsBean);
            var namespaceMapBean = null;
            namespaceMapBean = _scBaseUtils.getNewBeanInstance();
            
            _scBaseUtils.addStringValueToBean("parentnamespace", "ShipmentLine", namespaceMapBean);
            _scBaseUtils.addStringValueToBean("childnamespace", "ShipmentLine", namespaceMapBean);
            _scBaseUtils.addStringValueToBean("parentpath", "StockRoomLocations.StockRoomLocation", namespaceMapBean);
            _scBaseUtils.addStringValueToBean("childpath", "StockRoomLocation", namespaceMapBean);
            
            returnValue = _wscCustomerPickUpUtils.getRepeatingScreenData(repeatingScreenId, namespaceMapBean, additionalParamsBean);
            return returnValue;
        },
        
        /********************************************
		 * @author: Adam Dunmars
		 * @description: This repeating screen generator function
		 * fetches repeating data for Sales Floor locations.
		 ********************************************/
        getSalesfloorLocationRepeatingScreenData: function(value, screen, widget, namespace, modelObject) {
        	var repeatingScreenId = "extn.item.details.ItemSalesFloorLocations";
            var returnValue = null;
            var screenMode = "default";
            var additionalParamsBean = null;
            additionalParamsBean = _scBaseUtils.getNewBeanInstance();
            _scBaseUtils.addStringValueToBean("screenMode", screenMode, additionalParamsBean);
            var namespaceMapBean = null;
            namespaceMapBean = _scBaseUtils.getNewBeanInstance();
            
            _scBaseUtils.addStringValueToBean("parentnamespace", "ShipmentLine", namespaceMapBean);
            _scBaseUtils.addStringValueToBean("childnamespace", "SalesFloorLocation", namespaceMapBean);
            _scBaseUtils.addStringValueToBean("parentpath", "SalesFloorLocations.SalesFloorLocation", namespaceMapBean);
            _scBaseUtils.addStringValueToBean("childpath", "SalesFloorLocation", namespaceMapBean);

            returnValue = _wscCustomerPickUpUtils.getRepeatingScreenData(repeatingScreenId, namespaceMapBean, additionalParamsBean);
            return returnValue;
        },
        
    	/*****************************************
    	 * @author: Adam Dunmars
    	 * @description: This function saves the reason code for the shortage reason and closes the popup 
    	 * then proceeds to handle the shortage as the OOB flow does.
    	 * 
    	 * TODO: Finish implementing this function
    	 ******************************************/
    	onSaveShortageReason : function(event, bEvent, ctrl, args) {
    		var model = _scScreenUtils.getModel(this, "extn_getShortageResolutions_input");
    		var parentScreen = this.getOwnerScreen();
    		
    		var shipmentLineModel = parentScreen.getModel("ShipmentLine");
    		var isLastShortageLineForCancellingShipment = wscBackroomPickUtils.isLastShortageLineForCancellingShipment(_isccsUIUtils.getParentScreen(parentScreen, true), shipmentLineModel);
    		var argBean = null;
            argBean = _scBaseUtils.getNewBeanInstance();
            argBean = _scBaseUtils.addStringValueToBean("isLastShortageLineForCancellingShipment", isLastShortageLineForCancellingShipment, argBean);
            var select = this.getWidgetByUId("cmbCustVerfMethod");
            if(select.isValid()){
                var mashInput = _scScreenUtils.getTargetModel(this, "extn_getShortageResolutions_output");
                parentScreen.updateShortageReason(mashInput);
        		parentScreen.updateShortageResolution("Ok", argBean);
        		_scWidgetUtils.closePopup(this, "CLOSE", false);
            } 
    	},
    	
    	/*****************************************
    	 * @author: Adam Dunmars
    	 * @description: This function saves the reason code for the shortage reason and closes the popup 
    	 * then proceeds to handle the shortage as the OOB flow does.
    	 * 
    	 * TODO: Finish implementing this function
    	 ******************************************/
    	onCancelShortageReason : function(event, bEvent, ctrl, args) {
    		_scWidgetUtils.closePopup(this, "CLOSE", false);
    	}
	});
});